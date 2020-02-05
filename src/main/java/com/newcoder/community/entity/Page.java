package com.newcoder.community.entity;

/*
封装分页相关的信息
 */
public class Page {
    //当前页码
    private int currentPage=1;
    //每一页最多显示的数据条数
    private int limit=10;
    //数据总数,用于计算总的页数
    private int rows;
    //查询路径，用于复用分页链接
    private String path;

    public int getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(int currentPage) {
        if(currentPage>=1){
            this.currentPage = currentPage;
        }
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        if(limit>=1&&limit<=100){
            this.limit = limit;
        }
    }
    public int getRows() {
        return rows;
    }

    public void setRows(int rows) {
        if(rows>=0){
            this.rows = rows;
        }
    }
    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
    //通过当前页的页码计算出当前页的起始行，offset（前端要用）
    public int getOffset(){
        return (currentPage*limit-limit);
    }
    //获取总页数
    public int getTotal(){
        if(rows%limit==0){
            return rows/limit;
        }else{
            return rows/limit+1;
        }
    }
    //页面上下标出页数显示的是从第from页到第to页。
    //即：当前页的前两页（from）和当前页的后两页（to）
    public int getFrom(){
        int from=currentPage-2;
        return from<1?1:from;
    }
    public int getTo(){
        int to=currentPage+2;
        int total=getTotal();
        return to>total?total:to;
    }
}
