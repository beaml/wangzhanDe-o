package com.newcoder.community.util;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooDefs;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import org.apache.zookeeper.ZooKeeper;

@Component
public class ZookeeperUtil {
    private static CountDownLatch countDownLatch=new CountDownLatch(1);
    private ZooKeeper zookeeper;
    public ZookeeperUtil(){
        try {
            this.zookeeper=new ZooKeeper("139.199.126.68",5000,null );
            System.out.println("zookeeper session 建立了！！！");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    //获取分布式锁
    public Boolean acquireDistributedLock(){
        String path="/lock-lock1";
        try {
            zookeeper.create(path,"".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    //释放一个分布式锁
    public void releaseDistributedLock(){
        String path="/lock-lock1";
        try {
            zookeeper.delete(path,-1);
            System.out.println("release the lock for 11111!!!!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
