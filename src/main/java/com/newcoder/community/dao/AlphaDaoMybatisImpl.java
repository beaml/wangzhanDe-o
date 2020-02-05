package com.newcoder.community.dao;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

@Primary
@Repository
public class AlphaDaoMybatisImpl implements AlphaDao{

    @Override
    public String select() {
        return "mybatis";
    }
}
