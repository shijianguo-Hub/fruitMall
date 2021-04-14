package com.javapandeng.controller;

import com.alibaba.fastjson.JSONObject;
import com.javapandeng.base.BaseController;
import com.javapandeng.po.*;
import com.javapandeng.service.ItemCategoryService;
import com.javapandeng.service.ItemService;
import com.javapandeng.service.ManageService;
import com.javapandeng.service.UserService;
import com.javapandeng.utils.Consts;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.CollectionUtils;
import org.springframework.web.HttpMediaTypeException;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

/**
 * 登录相关的控制器
 */
@Controller
@RequestMapping("/login")
public class LoginController extends BaseController{

    @Autowired
    private ManageService manageService;

    @Autowired
    private ItemCategoryService itemCategoryService;

    @Autowired
    private ItemService itemService;

    @Autowired
    private UserService userService;

    /**
     * 管理员登录前
     */
    @RequestMapping("/login")
    public String login(){
        return "/login/mLogin";
    }

    /**
     * 登录验证
     */
    @RequestMapping("/toLogin")
    public String toLogin(Manage manage, HttpServletRequest request){

        Manage byEntity = manageService.getByEntity(manage);//从数据库查询是否有该用户且密码正确
        if(byEntity == null){ //如果没有从数据库查到用户信息，就退出
            return "redirect:/login/mtuichu";
        }
        //查到用户信息就跳转登录，并且把用户信息保存到session域中
        request.getSession().setAttribute(Consts.MANAGE, byEntity);
        return "/login/mIndex";
    }

    /**
     * 管理员退出
     */
    @RequestMapping("/mtuichu")
    public String mtuichu(HttpServletRequest request){
        //清空session,返回登录页
        request.getSession().setAttribute(Consts.MANAGE, null);
        return "/login/mLogin";
    }

    /**
     * 跳转到前台首页
     */
    @RequestMapping("/uIndex")
    public String uIndex(Model model){
        //首先从类目表查出一级类目
        String sql = "select * from item_category where isDelete=0 and pid is null order by name";
        List<ItemCategory> fatherList = itemCategoryService.listBySqlReturnEntity(sql);
        List<CategoryDto> list = new ArrayList<>(); // 专门用于一二级类目传输的对象list
        if(!CollectionUtils.isEmpty(fatherList)){
            for(ItemCategory item: fatherList){
                CategoryDto dto = new CategoryDto();
                dto.setFather(item);//设置一级类目
                //通过一级类目来查询二级类目,查二级类目用的肯定是一级类目item.getId()
                String sql_child = "select * from item_category where isDelete=0 and pid = " + item.getId();
                List<ItemCategory> childList = itemCategoryService.listBySqlReturnEntity(sql_child);
                dto.setChildrens(childList);
                list.add(dto);
            }
            model.addAttribute("lbs", list);
        }

        //查询折扣商品,按折扣力度排序
        String sql_zk = "select * from item where isDelete=0 and zk is not null order by zk limit 0,10";
        List<Item> zks = itemService.listBySqlReturnEntity(sql_zk);
        model.addAttribute("zks", zks);

        //查询热销商品，倒序排
        String sql_rx = "SELECT * FROM item WHERE isDelete=0 ORDER BY gmNum DESC LIMIT 0,10";
        List<Item> rxs = itemService.listBySqlReturnEntity(sql_rx);
        model.addAttribute("rxs", rxs);

        return "/login/uIndex";
    }

    //普通用户注册
    @RequestMapping("/res")
    public String res(){
        return "/login/res";
    }

    //执行用户注册
    @RequestMapping("/toRes")
    public String toRes(User user){
        userService.insert(user);
        return "/login/uLogin";
    }

    //普通用户登录入口
    @RequestMapping("/uLogin")
    public String uLogin(){
        return "login/uLogin";
    }

    //执行普通用户登录
    @RequestMapping("/utoLogin")
    public String utoLogin(User user, HttpServletRequest request){
        //首先要从数据库user查询是否存在这个用户信息
        User u = userService.getByEntity(user);
        if(u == null){
            return "redirect:/login/res.action";
        }
        //如果有用户注册信息，那么就把用户信息存入session域对象
        request.getSession().setAttribute("role", 2);//普通用户角色为2
        request.getSession().setAttribute(Consts.USERNAME, u.getUserName());
        request.getSession().setAttribute(Consts.USERID, u.getId());
        return "redirect:/login/uIndex.action"; //携带用户登录信息到前台首页
    }

    //前端用户退出
    @RequestMapping("/uTui")
    public String uTui(HttpServletRequest request){
        //退出前清空session中的信息
        request.getSession().invalidate();
        return "redirect:/login/uIndex.action";
    }

    /**
     * 用户修改密码
     */
    @RequestMapping("/pass")
    public String pass(HttpServletRequest request){
        Object attribute = request.getSession().getAttribute(Consts.USERID);
        if(attribute==null){
            return "redirect:/login/uLogin";
        }
        Integer userId = Integer.valueOf(attribute.toString());
        User load = userService.load(userId);
        request.setAttribute("obj",load);
        return "login/pass";
    }

    /**
     * 执行用户修改密码
     */
    @RequestMapping("/upass")
    @ResponseBody
    public String upass(String password,HttpServletRequest request){
        Object attribute = request.getSession().getAttribute(Consts.USERID);
        JSONObject js = new JSONObject();
        if(attribute==null){
            js.put(Consts.RES,0);
            return js.toString();
        }
        Integer userId = Integer.valueOf(attribute.toString());
        User load = userService.load(userId);
        load.setPassWord(password);
        userService.updateById(load);
        js.put(Consts.RES,1);
        return js.toString();
    }

}


















