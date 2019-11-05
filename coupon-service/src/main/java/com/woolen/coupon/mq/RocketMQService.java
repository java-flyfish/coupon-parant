package com.woolen.coupon.mq;

import com.alibaba.fastjson.JSONObject;
import com.woolen.coupon.entry.User;
import com.woolen.coupon.service.UserService;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.client.producer.SendStatus;
import org.apache.rocketmq.common.consumer.ConsumeFromWhere;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.remoting.common.RemotingHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Map;

@Service
@ConfigurationProperties(prefix = "rocketmq")
@Data
public class RocketMQService {
    private Logger logger = LoggerFactory.getLogger(getClass());

    private String namesrvAddr;
    private String producerName;
    private String consumerName;
    private String topic;
    private String tag;

    private DefaultMQProducer defaultMQProducer;
    private DefaultMQPushConsumer defaultMQPushConsumer;

    @Autowired
    private UserService userService;

    public Boolean send(String message) throws Exception {
        Message msg = new Message(topic,tag, message.getBytes(RemotingHelper.DEFAULT_CHARSET));
        //调用producer的send()方法发送消息
        //这里调用的是同步的方式，所以会有返回结果
        SendResult sendResult = defaultMQProducer.send(msg);
        if (sendResult.getSendStatus().equals(SendStatus.SEND_OK)){
            return true;
        }
        return false;
    }

    @PostConstruct
    private void defaultMQProducer() throws MQClientException {
        if (StringUtils.isEmpty(tag)){
            tag = "*";
        }
        defaultMQProducer = new DefaultMQProducer(producerName);
        defaultMQProducer.setNamesrvAddr(namesrvAddr);
        defaultMQProducer.start();
        logger.info("MQ生产者启动成功。。。。");
        defaultMQPushConsumer = new DefaultMQPushConsumer(consumerName);
        defaultMQPushConsumer.setNamesrvAddr(namesrvAddr);
        defaultMQPushConsumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_FIRST_OFFSET);
        //设置consumer所订阅的Topic和Tag，*代表全部的Tag
        defaultMQPushConsumer.subscribe(topic, tag);
        //设置一个Listener，主要进行消息的逻辑处理
        defaultMQPushConsumer.registerMessageListener(new MessageListenerConcurrently() {
            @Override
            public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs, ConsumeConcurrentlyContext context) {
                for (MessageExt msg : msgs) {
                    String message = "";
                    try {
                        message = new String(msg.getBody(),"utf-8");
                        logger.info("订单支付完成消息，创建用户：{}", JSONObject.toJSONString(message));
                        Map<String,Object> paramMap = JSONObject.parseObject(message,Map.class);
                        //todo 插入需要去重
                        User user = userService.selectByPhone((String) paramMap.get("phone"));
                        if (user != null){
                            return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
                        }
                        userService.insertUserAndRole(paramMap);
                    } catch (Exception e) {
                        e.printStackTrace();
                        return ConsumeConcurrentlyStatus.RECONSUME_LATER;
                    }
                }
                //返回消费状态
                //CONSUME_SUCCESS 消费成功
                //RECONSUME_LATER 消费失败，需要稍后重新消费
                return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
            }
        });
        //调用start()方法启动consumer
        defaultMQPushConsumer.start();
        logger.info("MQ消费者启动成功。。。。");
    }
}
