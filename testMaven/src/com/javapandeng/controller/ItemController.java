package com.javapandeng.controller;

import com.javapandeng.base.BaseController;
import com.javapandeng.po.Item;
import com.javapandeng.po.ItemCategory;
import com.javapandeng.service.ItemCategoryService;
import com.javapandeng.service.ItemService;
import com.javapandeng.utils.Pager;
import com.javapandeng.utils.SystemContext;
import com.javapandeng.utils.UUIDUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import javax.jws.WebParam;
import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * 商品控制层
 */
@Controller
@RequestMapping("/item")
public class ItemController extends BaseController {

    @Autowired
    private ItemService itemService;

    @Autowired
    private ItemCategoryService itemCategoryService;

    /**
     * 查询上架的商品信息
     *
     * @return
     */
    @RequestMapping("/findBySql")
    public String findBySql(Item item, Model model) {
        String sql = "select * from item where isDelete = 0 ";
        if (!isEmpty(item.getName())) {
            sql += " and name like '%" + item.getName() + "%' ";
        }
        sql += " order by id desc";
        Pager<Item> pagers = itemService.findBySqlRerturnEntity(sql);
        model.addAttribute("pagers", pagers);
        model.addAttribute("obj", item);

        return "/item/item";
    }

    /**
     * 转向添加商品页面
     */
    @RequestMapping("/add")
    public String add(Model model) {
        //这里需要把商品的二级类目获取到，在添加商品页面显示
        String sql = "select * from item_category where isDelete = 0 and pid is not null order by id";
        List<ItemCategory> itemCategories = itemCategoryService.listBySqlReturnEntity(sql);
        model.addAttribute("types", itemCategories);

        return "/item/add";
    }

    /**
     * 执行添加商品操作
     */
    @RequestMapping("/exAdd")
    public String exAdd(Item item, @RequestParam("file") CommonsMultipartFile[] files, HttpServletRequest request) throws IOException {
        System.out.println("systemContext.getRealPath():" + SystemContext.getRealPath());

        System.out.println("request.getContextPath:" + request.getContextPath());

        System.out.println("request.getSession().getServletContext().getRealPath()--->" + request.getSession().getServletContext().getRealPath("/resource/ueditor/upload"));

        itemCommon(item, files, request);
        //设置其他属性
        item.setGmNum(0);
        item.setIsDelete(0);
        item.setScNum(0);
        itemService.insert(item);

        return "redirect:/item/findBySql.action";
    }

    /**
     * 修改商品信息
     */
    @RequestMapping("/update")
    public String update(Integer id, Model model) {
        Item load = itemService.load(id);
        //查询二级目录
        String sql = "select * from item_category where isDelete = 0 and pid is not null order by id";
        List<ItemCategory> listBySqlReturnEntity = itemCategoryService.listBySqlReturnEntity(sql);
        model.addAttribute("types", listBySqlReturnEntity);
        model.addAttribute("obj", load);

        return "/item/update";
    }

    /**
     * 执行更新操作
     */
    @RequestMapping("/exUpdate")
    public String exUpdate(Item item, @RequestParam("file") CommonsMultipartFile[] files, HttpServletRequest request) throws IOException {
        itemCommon(item, files, request);
        itemService.updateById(item);

        return "redirect:/item/findBySql.action";
    }

    private void itemCommon(Item item, @RequestParam("file") CommonsMultipartFile[] files, HttpServletRequest request) throws IOException {
        if (files.length > 0) {
            for (int s = 0; s < files.length; s++) {
                String n = UUIDUtils.create();
                String path = SystemContext.getRealPath() + "\\resource\\ueditor\\upload\\" + n + files[s].getOriginalFilename();
                File newFile = new File(path);
                //通过CommonsMultipartFile的方法直接写文件
                files[s].transferTo(newFile);
                if (s == 0) {
                    item.setUrl1(request.getContextPath() + "\\resource\\ueditor\\upload\\" + n + files[s].getOriginalFilename());
                }
                if (s == 1) {
                    item.setUrl2(request.getContextPath() + "\\resource\\ueditor\\upload\\" + n + files[s].getOriginalFilename());
                }
                if (s == 2) {
                    item.setUrl3(request.getContextPath() + "\\resource\\ueditor\\upload\\" + n + files[s].getOriginalFilename());
                }
                if (s == 3) {
                    item.setUrl4(request.getContextPath() + "\\resource\\ueditor\\upload\\" + n + files[s].getOriginalFilename());
                }
                if (s == 4) {
                    item.setUrl5(request.getContextPath() + "\\resource\\ueditor\\upload\\" + n + files[s].getOriginalFilename());
                }
            }
        }
        //由item对象的二级类目从item_category中查询到对象的一级类目
        ItemCategory byId = itemCategoryService.getById(item.getCategoryIdTwo());
        item.setCategoryIdOne(byId.getPid());
    }

    /**
     * 商品下架
     */
    @RequestMapping("/delete")
    public String delete(Integer id) {
        Item load = itemService.load(id);
        load.setIsDelete(1);
        itemService.updateById(load);

        return "redirect:/item/findBySql.action";
    }

    /**
     * 商品搜索和分类查询：按关键字查询还有点击二级分类查询
     */
    @RequestMapping("/shoplist")
    public String shoplist(Item item, String condition, Model model) {
        String sql = "select * from item where isDelete=0 ";
        //通过点击二级分类来查询
        if(!isEmpty(item.getCategoryIdTwo())){
            sql += " and category_id_two = " + item.getCategoryIdTwo();
        }
        //通过搜索框输入关键字来查询
        if(!isEmpty(condition)){
            sql += " and name like '%" + condition + "%'";
        }

        //按价格进行排序  注意（price+0）把varchar变int的写法
        if(!isEmpty(item.getPrice())){
            sql += " order by (price+0) desc";
        }

        //按销量进行排序
        if(!isEmpty(item.getGmNum())){
            sql += " order by gmNum desc";
        }

        //如果没有价格和销量数据，那么就会按item中带的id来进行排序，从uIndex搜索的时候
        if(isEmpty(item.getPrice()) && isEmpty(item.getGmNum())){
            sql += " order by id desc";
        }

        Pager<Item> pagers = itemService.findBySqlRerturnEntity(sql);
        model.addAttribute("pagers", pagers);

        model.addAttribute("obj", item);

        //condition字符串回传，用于搜索框显示搜索历史
        model.addAttribute("condition", condition);

        return "/item/shoplist";
    }

    @RequestMapping("/view")
    public String view(Integer id, Model model){
        Item item = itemService.load(id);
        model.addAttribute("obj", item);
        return "/item/view";
    }
}
