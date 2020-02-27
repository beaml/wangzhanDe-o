package com.newcoder.community.util;

import org.apache.commons.lang3.CharUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

@Component
public class SensitiveFilter {

    //实例化log
    private static final Logger logger= LoggerFactory.getLogger(SensitiveFilter.class);
    //将敏感词替换为该常量字符
    private static final String REPLACEMENT="*";
    //初始化树,首先初始化根节点
    private TrieNode root=new TrieNode();
    //根据Sensitice-words敏感词汇表构建前缀树
    //只需要在方法开始时初始化一次就可以了。
    // 这个注解是在构造器在实例化之后自动调用init方法。而这个bean是在服务启动的时候被初始化
    @PostConstruct
    public void init(){
        //读取文件
        //类加载器是在类路径加寻找资源（classes路径下）
        try(
                InputStream is=this.getClass().getClassLoader().getResourceAsStream("sensitive-words.txt");
                BufferedReader reader=new BufferedReader(new InputStreamReader(is));
                ){
            String keyword;
            while((keyword=reader.readLine())!=null){
                //添加到前缀树
                this.addKeyword(keyword);

            }
        } catch (IOException e) {
            logger.error("加载敏感词文件失败:"+e.getMessage());
        }
    }

    //将一个敏感词添加到前缀树当中去
    private void addKeyword(String keyword){
        TrieNode tempNode=root;
        for(int i=0;i<keyword.length();i++){
            char c=keyword.charAt(i);
            TrieNode subNode=tempNode.getSubNode(c);
            if(subNode==null){
                //初始化子节点
                subNode=new TrieNode();
                tempNode.addSubNode(c,subNode);
            }
            //指向子节点，进入下一循环
            tempNode=subNode;
            //当某个单词循环到最后，设置结束的标识
            if(i==keyword.length()-1){
                tempNode.setKeywordEnd(true);
            }
        }

    }
    //过滤敏感词
    public String filter(String text){
        if(StringUtils.isBlank(text)){
            return null;
        }
        //指针1，指向树
        TrieNode tempNode=root;
        //指针2
        int begin=0;
        //指针3
        int position=0;
        //结果
        StringBuilder sb=new StringBuilder();
        while(begin<text.length()){
            if(position<text.length()){
                char c=text.charAt(position);
                //跳过符号
                if(isSymbol(c)){
                    //若指针1处于根节点，将此符号计入结果，让指针2向下走一步
                    if(tempNode==root){
                        sb.append(c);
                        begin++;
                    }
                    //无论符号在开头或中间，指针三都向下走一步
                    position++;
                    continue;
                }
                //不是符号，检测是否是敏感词
                //检查下级节点
                tempNode=tempNode.getSubNode(c);
                if(tempNode==null){
                    //以begin为开头的字符串不是敏感词
                    sb.append(text.charAt(begin));
                    //进入下一个位置
                    position=++begin;
                    //将指针1 重新指向根节点
                    tempNode=root;
                }else if(tempNode.isKeywordEnd){
                    //发现了敏感词，将begin到position的字符串替换掉
                    sb.append(REPLACEMENT);
                    //position进入下一个位置，begin和他重合
                    begin=++position;
                    //将指针1 重新指向根节点
                    tempNode=root;
                }else{
                    //检查下一个字符
                    position++;
                }
            } else{
                //position越界仍未匹配到敏感词
                sb.append(text.charAt(begin));
                position=++begin;
                tempNode=root;
            }
        }
        //将最后一批字符，计入结果
        sb.append(text.substring(begin));
        return sb.toString();
    }
    //判断是否为符号
    //0x2E80到0x9FFF为东亚文字范围
    private boolean isSymbol(Character c){
        return !CharUtils.isAsciiAlphanumeric(c)&&(c<0x2E80||c>0x9FFF);
    }

    //定义一个内部类( 前缀树节点)
    private class TrieNode{
        //关键词结束的表示
        private boolean isKeywordEnd=false;
        //当前节点的子节点,可能有多个，用map封装.key是下级节点的字符，value是下级节点
        private Map<Character,TrieNode> subNodes=new HashMap<>();

        public boolean isKeywordEnd() {
            return isKeywordEnd;
        }

        public void setKeywordEnd(boolean keywordEnd) {
            isKeywordEnd = keywordEnd;
        }
        //添加子节点
        public void addSubNode(Character c,TrieNode node){
            subNodes.put(c,node);
        }
        //获取子节点
        public TrieNode getSubNode(Character c){
            return subNodes.get(c);
        }


    }
}
