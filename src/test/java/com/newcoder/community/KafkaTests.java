package com.newcoder.community;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes=Application.class)
public class KafkaTests {
    @Autowired
    private KafkaProducer kafkaProducer;
    //用生产者生产一个消息，看消费者能不能自动消费
    @Test
    public void testKafka(){
        kafkaProducer.sendMessage("test","你好呀");
        kafkaProducer.sendMessage("test","在吗");
        try {
            Thread.sleep(1000*10);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
//生产者
@Component
class KafkaProducer{
    @Autowired
    private KafkaTemplate kafkaTemplate;
    public void sendMessage(String topic,String content){
        kafkaTemplate.send(topic,content);
    }
}
//消费者
@Component
class KafkaConsumer{
    //监听kafka的注解，一旦监听到，由下面的方法进行处理
    @KafkaListener(topics={"test"})
    public void handleMessage(ConsumerRecord record){
        System.out.println(record.value());
    }
}

