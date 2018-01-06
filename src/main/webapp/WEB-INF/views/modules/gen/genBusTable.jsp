<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp" %>
<html>
<head>
    <title>业务表管理</title>
    <meta name="decorator" content="default"/>
    <script type="text/javascript">
        $(document).ready(function () {

        });

    </script>
</head>
<body>
<ul class="nav nav-tabs">
    <li ><a href="${ctx}/gen/genTable/genBusTableList">业务字段表列表</a></li>
    <li class="active"><a href="${ctx}/gen/genTable/genBusTable">业务表字段管理</a></li>

</ul>
<form:form id="inputForm" modelAttribute="genTable" action="${ctx}/gen/genTable/showBusTable" method="post"
           class="form-horizontal">
    <form:hidden path="id"/>
    <sys:message content="${message}"/>
    <br/>
    <div class="control-group">
        <label class="control-label">表名:</label>
        <div class="controls">
            <form:select path="name" class="input-xxlarge">
                <form:options items="${tableList}" itemLabel="nameAndComments" itemValue="name"
                              htmlEscape="false"/>
            </form:select>
        </div>
        <div class="form-actions">
            <input id="btnSubmit" class="btn btn-primary" type="submit" value="下一步"/>&nbsp;
        </div>
    </div>
</form:form>

</body>
</html>
