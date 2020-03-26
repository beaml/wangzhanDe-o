package com.newcoder.community.dao;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

import java.util.Date;

@Mapper
@Component
public interface BaobiaoMapper {
    int getDayDisscussPostCount(Date start,Date end);
}
