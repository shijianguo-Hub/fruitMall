package com.javapandeng.po;

/**
 * 购物车的DTO数据传输对象，专门用于传输商品的id和购买商品数量
 */
public class CarDto {
    private Integer id;
    private Integer num;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getNum() {
        return num;
    }

    public void setNum(Integer num) {
        this.num = num;
    }
}
