package com.newcoder.community.service;

import com.newcoder.community.dao.AlphaDao;
import com.newcoder.community.dao.DiscussPostMapper;
import com.newcoder.community.dao.UserMapper;
import com.newcoder.community.entity.DiscussPost;
import com.newcoder.community.entity.User;
import com.newcoder.community.util.CommunityUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.Date;

@Service
//默认是单例模式。通常使用单例模式，也可以设定成多例，每次bean调用时实例化一个新的。
//@Scope("pretotype")
public class AlphaService {
    @Autowired
    private AlphaDao alphaDao;

    @Autowired
    private UserMapper userMapper;
    @Autowired
    private DiscussPostMapper discussPostMapper;

    @Autowired
    private TransactionTemplate transactionTemplate;
    //这个是构造器
    public AlphaService(){
        System.out.println("实例化（构造方法）alphaService");
    }
    @PostConstruct
    public void init(){
        System.out.println("初始化 alphaService");
    }


    @PreDestroy
    public void destroy(){
        System.out.println("销毁  AlphaService");
    }

    public String find(){
        return alphaDao.select();
    }

    //不指定参数时，会默认选择一个机制。propagation事务的传播机制
    //REQUIRED：支持当前事务，a调b，对于b来说，a就是当前事务，可以理解为外部事务。如果外部事物不存在，则创建新事务。
    //REQUIRES_NEW：创建一个新事务，并且暂停当前事务（外部事务）
    //NESTED：如果当前存在事务（外部事物），则嵌套在该事务中执行。事务b在执行的时候是有独立的提交和回滚的。否则就和REQUIRED一样了
    @Transactional(isolation = Isolation.READ_COMMITTED,propagation = Propagation.REQUIRED)
    //演示事务的使用
    public Object sava1(){
        //新增用户
        User user=new User();
        user.setUsername("alpha");
        user.setSalt(CommunityUtil.generateUUID().substring(0,5));
        user.setPassword(CommunityUtil.md5("123"+user.getSalt()));
        user.setEmail("alpha@qq.com");
        user.setHeaderUrl("http://image.nowcoder.com/head/99t.png");
        user.setCreateTime(new Date());
        userMapper.insertUser(user);
        //新增帖子
        DiscussPost discussPost=new DiscussPost();
        discussPost.setUserid(user.getId());
        discussPost.setTitle("alpha good");
        discussPost.setContent("alpha test shiwuguanli新人报道");
        discussPost.setCreateTime(new Date());
        discussPostMapper.insertDiscussPost(discussPost);

        //测试报错时能不能回滚
        Integer.valueOf("abc");//将abc字符串转为整数，转不了，会报错
        //此时如果报错了，用户也依旧会插入到数据库中，不会回滚，所以需要进行事务管理
        //通过注解管理事务，使其成为一个整体，任何一个地方报错，都需要回滚回去
        return "ok";
    }
    //通过编程管理事务
    public Object save2(){
        transactionTemplate.setIsolationLevel(TransactionDefinition.ISOLATION_READ_COMMITTED);
        transactionTemplate.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        return transactionTemplate.execute(new TransactionCallback<Object>() {
            @Override
            public Object doInTransaction(TransactionStatus transactionStatus) {
                //新增用户
                User user=new User();
                user.setUsername("a1");
                user.setSalt(CommunityUtil.generateUUID().substring(0,5));
                user.setPassword(CommunityUtil.md5("123"+user.getSalt()));
                user.setEmail("a1@qq.com");
                user.setHeaderUrl("http://image.nowcoder.com/head/99t.png");
                user.setCreateTime(new Date());
                userMapper.insertUser(user);
                //新增帖子
                DiscussPost discussPost=new DiscussPost();
                discussPost.setUserid(user.getId());
                discussPost.setTitle("a1 good");
                discussPost.setContent("a1 test shiwuguanli新人报道");
                discussPost.setCreateTime(new Date());
                discussPostMapper.insertDiscussPost(discussPost);

                //测试报错时能不能回滚
                Integer.valueOf("abc");
                return "ok";
            }
        });
    }
}
