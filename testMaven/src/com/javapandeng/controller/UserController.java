package com.javapandeng.controller;

import com.javapandeng.base.BaseController;
import com.javapandeng.po.User;
import com.javapandeng.service.UserService;
import com.javapandeng.utils.Consts;
import com.javapandeng.utils.Pager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.jws.soap.SOAPBinding;
import javax.servlet.http.HttpServletRequest;
import java.lang.invoke.ConstantCallSite;

/**
 * 用户控制层
 */
@Controller
@RequestMapping("/user")
public class UserController extends BaseController{

    @Autowired
    private UserService userService;

    /**
     * 用户查询：查询出来的用户信息要展现在user.jsp页面，所以需要Model对象
     * 形参使用User，用于通过userName进行模糊查询封装user信息。
     */
    @RequestMapping("/findBySql")
    public String findBySql(Model model, User user){
        String sql = "select * from user where 1=1 ";
        if(!isEmpty(user.getUserName())){
            sql += " and userName like '%" + user.getUserName() + "%'";
        }
        sql += " order by id";

        Pager<User> pagers = userService.findBySqlRerturnEntity(sql);
        model.addAttribute("pagers", pagers);
        model.addAttribute("obj", user);
        return "/user/user";
    }

    /**
     * 跳转修改用户信息界面，这里的功能是要根据用户id，把用户的所有信息都查询出来展示到update.jsp，保存到model对象
     */
    @RequestMapping("/update")
    public String update(Integer id, Model model){
        User load = userService.load(id);
        model.addAttribute("obj", load);

        return "/user/update";
    }

    /**
     * 后台执行修改操作
     */
    @RequestMapping("/exUpdate")
    public String exUpdate(User user){
        userService.updateById(user);

        return "redirect:/user/findBySql.action";
    }

    /**
     * 后台删除用户
     */
    @RequestMapping("/delete")
    public String delete(Integer id){
        //直接根据id删除用户
        userService.deleteById(id);

        return "redirect:/user/findBySql.action";
    }

    /**
     * 前台用户查看个人信息
     */
    @RequestMapping("/view")
    public String view(Model model, HttpServletRequest request){
        Object attribute = request.getSession().getAttribute(Consts.USERID);
        if(attribute == null){
            return "redirect:/login/uLogin";
        }
        Integer userId = Integer.valueOf(attribute.toString());
        User load = userService.load(userId);
        model.addAttribute("obj", load);

        return "/user/view";
    }

    /**
     * 前台用户修改个人信息
     */
    @RequestMapping("/exUpdateUser")
    public String exUpdateUser(User user, HttpServletRequest request){
        Object attribute = request.getSession().getAttribute(Consts.USERID);
        if(attribute == null){
            return "redirect:/login/uLogin";
        }
        Integer userId = Integer.valueOf(attribute.toString());
        user.setId(userId);
        userService.updateById(user);
        return "redirect:/user/view.action";
    }
}
