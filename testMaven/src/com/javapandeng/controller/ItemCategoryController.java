package com.javapandeng.controller;

import com.javapandeng.base.BaseController;
import com.javapandeng.po.ItemCategory;
import com.javapandeng.service.ItemCategoryService;
import com.javapandeng.utils.Pager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 类目的控制器
 */
@Controller
@RequestMapping("/itemCategory")
public class ItemCategoryController extends BaseController{

    //注解方式自动按类型注入，所以此时不需要像基于xml配置的方式注入时需要set方法
    @Autowired
    private ItemCategoryService itemCategoryService;

    /**
     * 分页查询类目列表
     */
    @RequestMapping("/findBySql")
    public String findBySql(Model model, ItemCategory itemCategory){
        //查询一级类目
        String sql = "select * from item_category where isDelete = 0 and pid is null order by id";
        Pager<ItemCategory> pagers = itemCategoryService.findBySqlRerturnEntity(sql);
        model.addAttribute("pagers", pagers);
        //从前端获取到的数据自动封装到itemCategory对象中，然后保存到request域中(这里暂时不用)
        model.addAttribute("obj", itemCategory);

        return "itemCategory/itemCategory";
    }

    /**
     * 转向到新增一级类目页面
     */
    @RequestMapping("/add")
    public String add(){
        return "itemCategory/add";
    }

    /**
     * 新增一级类目保存后，重定向到类目页面
     */
    @RequestMapping("/exAdd")
    public String exAdd(ItemCategory itemCategory){
        itemCategory.setIsDelete(0);//设置默认的类目为不删除
        itemCategoryService.insert(itemCategory);//保存到数据库
        return "redirect:/itemCategory/findBySql.action";//转向页面用页面名，转向处理方法用.action
    }

    /**
     * 转向修改一级类目页面
     */
    @RequestMapping("/update")
    public String update(Integer id, Model model){
        //通过id来查询到类目的其他信息并保存到model对象，来到update.jsp页面
        ItemCategory obj = itemCategoryService.load(id);
        model.addAttribute("obj", obj);

        return "itemCategory/update";
    }

    /**
     * 开始修改一级类目
     */
    @RequestMapping("/exUpdate")
    public String exUpdate(ItemCategory itemCategory){

        itemCategoryService.updateById(itemCategory);
        return "redirect:/itemCategory/findBySql.action";
    }

    /**
     * 删除以及类目
     */
    @RequestMapping("/delete")
    public String delete(Integer id){
        //根据id查询信息
        ItemCategory load = itemCategoryService.load(id);
        load.setIsDelete(1);//将删除标记置为1，表示已删除
        itemCategoryService.updateById(load);
        //将下级也要删除
        String sql = "update item_category set isDelete = 1 where pid = " + id;
        itemCategoryService.updateBysql(sql);

        return "redirect:/itemCategory/findBySql.action";
    }

    /**
     * 查看二级类目
     */
    @RequestMapping("/findBySql2")
    public String findBySql2(ItemCategory itemCategory, Model model){
        //查询二级类目
        String sql = "select * from item_category where isDelete = 0 and pid = " + itemCategory.getPid() + " order by id";
        Pager<ItemCategory> pagers = itemCategoryService.findBySqlRerturnEntity(sql);
        model.addAttribute("pagers", pagers);
        //从前端获取到的数据自动封装到itemCategory对象中，然后保存到request域中(这里暂时不用)
        model.addAttribute("obj", itemCategory);

        return "itemCategory/itemCategory2";
    }

    /**
     * 转向到新增二级类目页面
     */
    @RequestMapping("/add2")
    public String add2(Integer pid, Model model){
        model.addAttribute("pid", pid);
        return "itemCategory/add2";
    }

    /**
     * 新增二级类目保存后，重定向到类目页面
     */
    @RequestMapping("/exAdd2")
    public String exAdd2(ItemCategory itemCategory){
        itemCategory.setIsDelete(0);//设置默认的类目为不删除
        itemCategoryService.insert(itemCategory);//保存到数据库
        return "redirect:/itemCategory/findBySql2.action?pid=" + itemCategory.getPid();//转向页面用页面名，转向处理方法用.action
    }

    /**
     * 转向修改一级类目页面
     */
    @RequestMapping("/update2")
    public String update2(ItemCategory itemCategory, Model model){
        //通过id来查询到类目的其他信息并保存到model对象，来到update.jsp页面
        ItemCategory obj = itemCategoryService.load(itemCategory.getId());
        model.addAttribute("obj", obj);

        return "itemCategory/update2";
    }

    /**
     * 开始修改二级类目
     */
    @RequestMapping("/exUpdate2")
    public String exUpdate2(ItemCategory itemCategory){

        itemCategoryService.updateById(itemCategory);
        return "redirect:/itemCategory/findBySql2.action?pid=" + itemCategory.getPid();
    }

    /**
     * 删除以及类目
     */
    @RequestMapping("/delete2")
    public String delete2(Integer id, Integer pid){
        //根据id查询信息
        ItemCategory load = itemCategoryService.load(id);
        load.setIsDelete(1);//将删除标记置为1，表示已删除
        itemCategoryService.updateById(load);

        return "redirect:/itemCategory/findBySql2.action?pid=" + pid;
    }


























}
