package com.javapandeng.controller;


import com.alibaba.fastjson.JSONObject;
import com.javapandeng.base.BaseController;
import com.javapandeng.po.*;
import com.javapandeng.service.*;
import com.javapandeng.utils.Consts;
import com.javapandeng.utils.Pager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 订单控制层
 */
@Controller
@RequestMapping("/itemOrder")
public class ItemOrderController extends BaseController{

    @Autowired
    private ItemOrderService itemOrderService;

    @Autowired
    private UserService userService;

    @Autowired
    private CarService carService;

    @Autowired
    private OrderDetailService orderDetailService;

    @Autowired
    private ItemService itemService;

    @RequestMapping("/findBySql")
    public String findBySql(ItemOrder itemOrder, Model model){

        String sql = "select * from item_order where 1=1 ";
        if(!isEmpty(itemOrder.getCode())){
            sql += " and code like '%" + itemOrder.getCode() + "%' ";
        }
        sql += " order by id desc";

        //分页查询
        Pager<ItemOrder> pagers = itemOrderService.findBySqlRerturnEntity(sql);
        model.addAttribute("pagers", pagers);
        model.addAttribute("obj", itemOrder);

        return "/itemOrder/itemOrder";
    }

    /**
     * 用户查看个人订单信息
     */
    @RequestMapping("/my")
    public String my(Model model, HttpServletRequest request){
        Object attribute = request.getSession().getAttribute(Consts.USERID);
        if(attribute == null){
            return "redirect:/login/uLogin";
        }
        Integer userId = Integer.valueOf(attribute.toString());
        //通过用户id查询订单
        //全部订单
        String sql = "select * from item_order where user_id="+userId+" order by id desc";
        List<ItemOrder> all = itemOrderService.listBySqlReturnEntity(sql);
        //待发货
        String sql2 = "select * from item_order where user_id="+userId+" and status=0 order by id desc";
        List<ItemOrder> dfh = itemOrderService.listBySqlReturnEntity(sql2);

        //已取消
        String sql3 = "select * from item_order where user_id="+userId+" and status=1 order by id desc";
        List<ItemOrder> yqx = itemOrderService.listBySqlReturnEntity(sql3);
        //已发货
        String sql4 = "select * from item_order where user_id="+userId+" and status=2 order by id desc";
        List<ItemOrder> dsh = itemOrderService.listBySqlReturnEntity(sql4);
        //已收货
        String sql5 = "select * from item_order where user_id="+userId+" and status=3 order by id desc";
        List<ItemOrder> ysh = itemOrderService.listBySqlReturnEntity(sql5);

        model.addAttribute("all",all);
        model.addAttribute("dfh",dfh);
        model.addAttribute("yqx",yqx);
        model.addAttribute("dsh",dsh);
        model.addAttribute("ysh",ysh);

        return "/itemOrder/my";
    }

    /**
     * 购物车结算
     * 点击结算时，会把选中的多条购买商品信息组成数组放到请求体中，也就是对应形参中的RequestBody
     */
    @RequestMapping("/exAdd")
    @ResponseBody //ajax方式
    public String exAdd(@RequestBody List<CarDto> list, HttpServletRequest request){
        //用户登录检查
        Object attribute = request.getSession().getAttribute(Consts.USERID);
        JSONObject js = new JSONObject();
        if(attribute == null){
            js.put(Consts.RES, 0);
            return js.toJSONString();
        }
        Integer userId = Integer.valueOf(attribute.toString());
        //获取用户地址信息
        User user = userService.getById(userId);
        if(StringUtils.isEmpty(user.getAddress())){
            js.put(Consts.RES, 2);
            return js.toJSONString();
        }
        List<Integer> ids = new ArrayList<>();//存放要购买商品ID
        BigDecimal total = new BigDecimal(0);
        for(CarDto c : list){
            ids.add(c.getId());
            //也就是说并不是使用的前台购物车中的商品信息，而是获取的数据库中的购物车的信息
            Car load = carService.load(c.getId());
            //计算总金额
            total = total.add(new BigDecimal(load.getPrice()).multiply(new BigDecimal(c.getNum())));
        }
        //结算完的订单信息保存到订单表
        ItemOrder order = new ItemOrder();
        order.setStatus(0);
        //订单ID给出一个并发量小时不会重复的值
        order.setCode(getOrderNo());
        order.setIsDelete(0);
        order.setTotal(total.setScale(2,BigDecimal.ROUND_HALF_UP).toString());
        order.setUserId(userId);
        order.setAddTime(new Date());
        itemOrderService.insert(order);

        //订单信息存入订单详情表，删除购物车中已购买的商品信息
        if(!CollectionUtils.isEmpty(ids)){
            for(CarDto c : list){
                Car load = carService.load(c.getId());
                OrderDetail de = new OrderDetail();
                de.setItemId(load.getItemId());
                de.setOrderId(order.getId());
                de.setStatus(0);
                de.setNum(c.getNum());
                de.setTotal(String.valueOf(c.getNum()*load.getPrice()));
                orderDetailService.insert(de);
                //同时修改商品中的成交量
                Item load2 = itemService.load(load.getItemId());
                load2.setGmNum(load2.getGmNum()+c.getNum());
                itemService.updateById(load2);
                //删除购物车
                carService.deleteById(c.getId());
            }
        }
        //结算完成，返回json1
        js.put(Consts.RES, 1);
        return js.toJSONString();
    }

    private static String date;
    private static long orderNum = 0L;
    public static synchronized String getOrderNo(){
        String str = new SimpleDateFormat("yyyyMMddHHmm").format(new Date());
        if(date==null||!date.equals(str)){
            date = str;
            orderNum = 0L;
        }
        orderNum++;
        long orderNO = Long.parseLong(date)*10000;
        orderNO += orderNum;
        return orderNO+"";
    }

    /**
     * 用户取消订单
     */
    @RequestMapping("/qx")
    public String qx(Integer id, Model model){
        ItemOrder load = itemOrderService.load(id);
        //订单改为取消状态
        load.setStatus(1);
        itemOrderService.updateById(load);
        model.addAttribute("obj", load);
        return "redirect:/itemOrder/my";

    }

    /**
     * 后台管理员发货
     */
    @RequestMapping("fh")
    public String fh(Integer orderId, Model model){
        //发货就是把订单状态改为2
        ItemOrder load = itemOrderService.load(orderId);
        load.setStatus(2);
        itemOrderService.updateById(load);
        model.addAttribute("obj", load);
        return "redirect:/itemOrder/findBySql.action";
    }

    /**
     * 用户前台收货
     */
    @RequestMapping("/sh")
    public String sh(Integer id, Model model){

        ItemOrder obj =itemOrderService.load(id);
        obj.setStatus(3);
        itemOrderService.updateById(obj);
        model.addAttribute("obj",obj);
        return "redirect:/itemOrder/my";
    }

    /**
     * 用户评价商品
     * 要评价商品，只需要商品的ID，用户ID在session
     */
    @RequestMapping("/pj")
    public String pj(Integer id, Model model){
        model.addAttribute("id", id);
        return "itemOrder/pj";
    }
}



















