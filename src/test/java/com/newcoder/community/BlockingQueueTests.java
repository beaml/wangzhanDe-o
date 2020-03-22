package com.newcoder.community;

import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class BlockingQueueTests {

    public static void main(String[] args) {
        //实例化阻塞队列
        BlockingQueue queue=new ArrayBlockingQueue(10);
        //实例化生产者线程
        new Thread(new Producer(queue)).start();
        //实例化消费者线程
        new Thread(new Consumer(queue)).start();
        new Thread(new Consumer(queue)).start();
        new Thread(new Consumer(queue)).start();

    }
}
//满足生产者与消费者模式，需要有生产者线程和消费者线程
//一个文件中只能有一个类是public
//创建生产者线程，实现Runnable接口
class Producer implements Runnable{
    private BlockingQueue<Integer> queue;
    public Producer(BlockingQueue<Integer> queue){
        this.queue=queue;
    }
    @Override
    public void run() {
        try {
            //循环不断的生成数据并输入到队列
            for(int i=0;i<100;i++){
                Thread.sleep(20);
                queue.put(i);
                System.out.println(Thread.currentThread().getName()+"生产"+queue.size());
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
//创建消费者线程
 class Consumer implements Runnable{
    private BlockingQueue<Integer> queue;
    public Consumer(BlockingQueue<Integer> queue){
        this.queue=queue;
    }
    @Override
    public void run() {
        try{
            //每次循环获取数据，从队列中弹出
            while (true){
                //使用的时间间隔，模仿业务场景
                Thread.sleep(new Random().nextInt(1000));
                queue.take();
                System.out.println(Thread.currentThread().getName()+"消费"+queue.size());
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}
