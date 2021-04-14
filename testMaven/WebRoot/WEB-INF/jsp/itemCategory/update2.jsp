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
        <strong><span class="icon-pencil-square-o">修改二级类目</span> </strong>
    </div>
    <div class="body-content">
        <form action="${ctx}/itemCategory/exUpdate2" method="post" class="form-x">
            <%--类目的id也要使用，所以隐藏备用--%>
            <input type="hidden" name="id" value="${obj.id}" />
            <input type="hidden" name="pid" value="${obj.pid}" />
            <div class="form-group">
                <div class="label"><label>二级类目名称：</label></div>
                <div class="field">
                    <input type="text" class="input w50" name="name" data-validate="required:请输入名称" value="${obj.name}" />
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