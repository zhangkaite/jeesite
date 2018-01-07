<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp" %>
<html>
<head>
    <title>业务表管理</title>
    <meta name="decorator" content="default"/>
    <script type="text/javascript">
        function page(n, s) {
            if (n) $("#pageNo").val(n);
            if (s) $("#pageSize").val(s);
            $("#searchForm").submit();
            return false;
        }
    </script>
</head>
<body>
<ul class="nav nav-tabs">
    <li class="active"><a href="${ctx}/gen/genTable/genBusTableList">业务字段表列表</a></li>
    <shiro:hasPermission name="gen:genTable:edit">
        <li><a href="${ctx}/gen/genTable/genBusTable">业务表字段管理</a></li>
    </shiro:hasPermission>
</ul>
<form:form id="searchForm" modelAttribute="genBusTable" action="${ctx}/gen/genTable/genBusTableList/" method="post"
           class="breadcrumb form-search">
    <input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
    <input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
    <sys:tableSort id="orderBy" name="orderBy" value="${page.orderBy}" callback="page();"/>
    <label>业务类型：</label>
    <form:select path="busType" cssClass="input-xlarge" id="busTableType">
        <form:option value="">无</form:option>
        <form:options items="${dictList}" htmlEscape="false"/>
    </form:select>

    &nbsp;<input id="btnSubmit" class="btn btn-primary" type="submit" value="查询"/>
</form:form>
<sys:message content="${message}"/>
<table id="contentTable" class="table table-striped table-bordered table-condensed">
    <thead>
    <tr>
        <th class="sort-column name">表名</th>
        <th class="sort-column name">表描述</th>
        <th class="sort-column class_name">业务类型</th>
        <th class="sort-column name">列名</th>
        <th class="sort-column name">列名描述</th>
        <th class="sort-column name">列字段类型</th>
        <th class="sort-column name">列排序</th>
    </thead>
    <tbody>
    <c:forEach items="${page.list}" var="genBusTable">
        <tr>
            <td>${genBusTable.tableName}</td>
            <td>${genBusTable.tableComments}</td>
            <td>${genBusTable.busType}</td>
            <td >${genBusTable.columnName}</td>
            <td >${genBusTable.columnComments}</td>
            <td >${genBusTable.columnJdbcType}</td>
            <td >${genBusTable.sort}</td>

        </tr>
    </c:forEach>
    </tbody>
</table>
<div class="pagination">${page}</div>
</body>
</html>
