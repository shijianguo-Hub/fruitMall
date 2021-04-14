package com.javapandeng.po;

import java.io.Serializable;

/**
 * 收藏实体类
 */
public class Sc implements Serializable{

    /**
     * 收藏信息编号
     */
    private Integer id;

    /**
     * 收藏商品ID
     */
    private Integer itemId;

    /**
     * 通过商品id查询商品信息
     */
    private Item item;

    /**
     * 收藏用户ID
     */
    private Integer userId;

    public Sc() {
    }

    public Sc(Integer id, Integer itemId, Integer userId) {
        this.id = id;
        this.itemId = itemId;
        this.userId = userId;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getItemId() {
        return itemId;
    }

    public void setItemId(Integer itemId) {
        this.itemId = itemId;
    }

    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    @Override
    public String toString() {
        return "Sc{" +
                "id=" + id +
                ", itemId=" + itemId +
                ", item=" + item +
                ", userId=" + userId +
                '}';
    }
}
