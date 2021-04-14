package com.javapandeng.controller;

import com.javapandeng.base.BaseController;
import com.javapandeng.po.OrderDetail;
import com.javapandeng.service.OrderDetailService;
import com.javapandeng.utils.Pager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import sun.misc.Contended;

@Controller
@RequestMapping("/orderDetail")
public class OrderDetailController extends BaseController{

    @Autowired
    private OrderDetailService orderDetailService;

    @RequestMapping("/ulist")
    public String ulist(OrderDetail orderDetail, Model model){

        //根据订单id从订单详情表order_detail中查询
        String sql = "select * from order_detail where order_id = " + orderDetail.getOrderId();
        Pager<OrderDetail> pagers = orderDetailService.findBySqlRerturnEntity(sql);
        model.addAttribute("pagers", pagers);
        model.addAttribute("obj", orderDetail);
        return "orderDetail/ulist";
    }
}
