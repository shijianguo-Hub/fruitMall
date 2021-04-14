package com.javapandeng.controller;

import com.alibaba.fastjson.JSONObject;
import com.javapandeng.base.BaseController;
import com.javapandeng.po.Car;
import com.javapandeng.po.Item;
import com.javapandeng.service.CarService;
import com.javapandeng.service.ItemService;
import com.javapandeng.utils.Consts;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

/**
 * 购物车控制层
 */
@Controller
@RequestMapping("/car")
public class CarController extends BaseController{

    @Autowired
    private ItemService itemService;

    @Autowired
    private CarService carService;

    /**
     * 添加商品到购物车
     * @param car
     * @param request
     * @return
     */
    @RequestMapping("/exAdd")
    @ResponseBody //由于要AJAX请求，所以接收请求后同时返回json串放到响应体中，不再是跳转页面
    public String exAdd(Car car, HttpServletRequest request){
        //既然是ajax请求，那么就要有json对象,使用阿里的fastjson
        JSONObject js = new JSONObject();//实现了Map
        //检查用户是否登录，未登录返回0json串
        Object attribute = request.getSession().getAttribute(Consts.USERID);
        if(attribute == null){
            js.put(Consts.RES, 0);
            return js.toJSONString();
        }
        //如果是已登录，执行以下操作
        //保存用户ID
        Integer userId = Integer.valueOf(attribute.toString());
        car.setUserId(userId);
        //查出商品价格，也保存到购物车
        Item item = itemService.load(car.getItemId());
        String price = item.getPrice();
        Double priceDouble = Double.valueOf(price);
        car.setPrice(priceDouble);
        //如果是折扣商品，那么购物车保存的是打折后的价格
        if(item.getZk() != null){
            priceDouble = priceDouble * item.getZk() / 10;
            //转换为decimal类型，防止精度损失,初始化bigDecimal尽量使用string
            BigDecimal bd = new BigDecimal(priceDouble);
            car.setPrice(bd.doubleValue());
            priceDouble = bd.doubleValue();
        }
        //获取购物车该商品数量， 计算总价
        Integer num = car.getNum();
        double totalPrice = priceDouble * num;
        //总价还要防止精度损失,RoundingMode为舍入模式
        BigDecimal bdTotal = new BigDecimal(totalPrice).setScale(2, RoundingMode.UP);
        double totalPriceDouble = bdTotal.doubleValue();
        car.setTotal(totalPriceDouble + ""); // 加一个空字符串转换为字符串类型
        carService.insert(car);

        js.put(Consts.RES, 1);//加入购物车成功，返回json1
        return js.toJSONString();

    }

    /**
     *  用户查看自己的购物车,来到购物车页面
     */
    @RequestMapping("/findBySql")
    public String findBySql(Model model, HttpServletRequest request){

        //检查用户是否登录，未登录返回0json串
        Object attribute = request.getSession().getAttribute(Consts.USERID);
        if(attribute == null){
            return "redirect:/login/uLogin";
        }
        Integer userId = Integer.valueOf(attribute.toString());
        //根据用户ID查询购物车信息
        String sql = "select * from car where user_id = " + userId + " order by id desc";
        List<Car> list = carService.listBySqlReturnEntity(sql);
        model.addAttribute("list", list);
        return "car/car";

    }

    /**
     *  从购物车中删除选中的商品
     */
    @RequestMapping("/delete")
    @ResponseBody
    public String delete(Integer id){
        carService.deleteById(id);
        return "success";
    }
}


























