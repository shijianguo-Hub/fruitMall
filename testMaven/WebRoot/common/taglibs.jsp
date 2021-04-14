<%@ taglib prefix="c" uri="/WEB-INF/tlds/c.tld" %>
<%@ taglib prefix="fmt" uri="/WEB-INF/tlds/fmt.tld" %>
<%@ taglib prefix="fn" uri="/WEB-INF/tlds/fn.tld" %>
<%@ taglib prefix="pg" uri="/WEB-INF/tlds/pager-taglib.tld" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<script>
    //ctx表示项目路径 比如项目名叫demo，那么就代表：/demo , /  代表 http://localhost:8080
    var ctx = "${ctx}";
</script>