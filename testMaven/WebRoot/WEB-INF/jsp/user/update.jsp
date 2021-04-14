<%@page language="java" contentType="text/html; character=UTF-8" pageEncoding="UTF-8" %>
<%@include file="/common/taglibs.jsp"%>

<html>
<head>
    <title>管理员后台</title>
    <link rel="stylesheet" href="${ctx}/resource/css/pintuer.css">
    <link rel="stylesheet" href="${ctx}/resource/css/admin.css">
    <script src="${ctx}/resource/js/jquery.js"></script>
    <script src="${ctx}/resource/js/pintuer.js"></script>
</head>
<body>
<div class="panel admin-panel">
    <div class="panel-head" id="add">
        <strong><span class="icon-pencil-square-o">修改用户信息</span> </strong>
    </div>
    <div class="body-content">
        <form action="${ctx}/user/exUpdate" method="post" class="form-x">
            <%--类目的id也要使用，所以隐藏备用--%>
            <input type="hidden" name="id" value="${obj.id}" />
            <div class="form-group">
                <div class="label"><label>用户昵称：</label></div>
                <div class="field">
                    <input type="text" class="input w50" name="userName" data-validate="required:请输入名称" value="${obj.userName}" />
                    <div class="tips"></div>
                </div>
                <div class="label"><label>联系方式：</label></div>
                <div class="field">
                    <input type="text" class="input w50" name="phone" data-validate="required:请输入手机号" value="${obj.phone}" />
                    <div class="tips"></div>
                </div>
                <div class="label"><label>真实姓名：</label></div>
                <div class="field">
                    <input type="text" class="input w50" name="realName" data-validate="required:请输入真实姓名" value="${obj.realName}" />
                    <div class="tips"></div>
                </div>
                <div class="label"><label>性别：</label></div>
                <div class="field">
                    <input type="text" class="input w50" name="sex" data-validate="required:请输入性别" value="${obj.sex}" />
                    <div class="tips"></div>
                </div>
                <div class="label"><label>地址：</label></div>
                <div class="field">
                    <input type="text" class="input w50" name="address" value="${obj.address}" />
                    <div class="tips"></div>
                </div>
                <div class="label"><label>邮箱：</label></div>
                <div class="field">
                    <input type="text" class="input w50" name="email" value="${obj.email}" />
                    <div class="tips"></div>
                </div>
            </div>
            <div class="form-group">
                <div class="label"></div>
                <div class="field">
                    <button class="button bg-main icon-check-square-o" type="submit">提交</button>
                </div>
            </div>
        </form>
    </div>
</div>
</body>
</html>