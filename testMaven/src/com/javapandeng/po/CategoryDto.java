package com.javapandeng.po;

import java.util.List;

/**
 * 一级类目和对应二级类目列表的数据传输对象DTO
 */
public class CategoryDto {

    private ItemCategory father;//一级类目

    private List<ItemCategory> childrens;//对应二级类目列表

    public ItemCategory getFather() {
        return father;
    }

    public void setFather(ItemCategory father) {
        this.father = father;
    }

    public List<ItemCategory> getChildrens() {
        return childrens;
    }

    public void setChildrens(List<ItemCategory> childrens) {
        this.childrens = childrens;
    }
}
