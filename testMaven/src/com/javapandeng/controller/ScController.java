package com.javapandeng.controller;

import com.javapandeng.base.BaseController;
import com.javapandeng.po.Item;
import com.javapandeng.po.Sc;
import com.javapandeng.service.ItemService;
import com.javapandeng.service.ScService;
import com.javapandeng.utils.Consts;
import com.javapandeng.utils.Pager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 收藏控制层
 */
@Controller
@RequestMapping("/sc")
public class ScController extends BaseController{

    @Autowired
    private ScService scService;

    @Autowired
    private ItemService itemService;

    /**
     * 执行收藏操作
     */
    @RequestMapping("/exAdd")
    public String exAdd(Sc sc, HttpServletRequest request){
        System.out.println("itemId:" + sc.getItemId());
        //首先从session查找是否用户已经登录
        Object attribute = request.getSession().getAttribute(Consts.USERID);
        if(attribute == null){
            //如果没有登录，直接
            return "redirect:/login/uLogin";
        }
        //保存到sc数据表中
        Integer userId = Integer.valueOf(attribute.toString());
        System.out.println("userId:" + userId);
        sc.setUserId(userId);
        scService.insert(sc);
        //商品表收藏数+1操作
        Item item = itemService.load(sc.getItemId());
        item.setScNum(item.getScNum() + 1);
        itemService.updateById(item);
        //重定向到收藏查询请求？这里逻辑不对吧。。。
        return "redirect:/sc/findBySql.action";

    }

    /**
     * 商品收藏列表
     */
    @RequestMapping("/findBySql")
    public String findBySql(Model model, HttpServletRequest request){
        Object attribute = request.getSession().getAttribute(Consts.USERID);
        if(attribute == null){
            return "redirect:/login/uLogin";
        }
        Integer userId = Integer.valueOf(attribute.toString());
        //根据用户ID查询他的收藏
        String sql = "select * from sc where user_id = " + userId + " order by id";
        Pager<Sc> pagers = scService.findBySqlRerturnEntity(sql);
        model.addAttribute("pagers", pagers);
        return "/sc/my";

    }

    /**
     * 取消收藏商品
     */
    @RequestMapping("/delete")
    public String delete(Integer id, HttpServletRequest request){

        Object attribute = request.getSession().getAttribute(Consts.USERID);
        if(attribute==null){
            return "redirect:/login/uLogin";
        }
        scService.deleteById(id);
        return "redirect:/sc/findBySql.action";
    }
}













