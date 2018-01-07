<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp" %>
<html>
<head>
    <title>业务表字段管理</title>
    <meta name="decorator" content="default"/>
    <script type="text/javascript">
        $(document).ready(function () {

            $("#checkedAll").click(function () {
                if ($(this).attr("checked") == 'checked') { // 全选
                    $("input[name*='checkbox_name']").each(function () {
                        $(this).attr("checked", true);

                    });
                } else { // 取消全选
                    $("input[name*='checkbox_name']").each(function () {
                        $(this).attr("checked", false);
                    });
                }
            });
        });


        function saveSelectData(){
           var i=0;
            $("input[name*='checkbox_name']").each(function () {
                if ($(this).attr("checked") == 'checked') {
                    $("input[name='columnList["+i+"].isList']").val(1);
                }
                i++;
            });
            $("#inputForm").submit();

        }


        function showSelectedData(){
            var s=0;
            $("input[name*='checkbox_name']").each(function () {
                $(this).attr("checked", false);
                $("input[name='columnList["+s+"].sort']").val('');
                s++;
            });
            var busTableType=$("#busTableType").val();
            $.ajax({
                type:'post',
                url:'${ctx}/gen/genTable/getSelectColumnData',
                data:{'busTableType':busTableType},
                cache:false,
                dataType:'json',
                success:function(data){
                    // [{"id":"13","isNewRecord":false,"busType":"橙色主题","columnId":"9f678abbea784853a3a26f084bd059f4","tableId":"3ffbe7a53ea54fe8a33181b0174c5359","sort":1},{"id":"14","isNewRecord":false,"busType":"橙色主题","columnId":"ea0afdee98204cfabbec2164e0cf4b45","tableId":"3ffbe7a53ea54fe8a33181b0174c5359","sort":2}]
                    if (data != null && data.length > 0) {
                        for (var i = 0; i < data.length; i++) {
                           // alert(data[i].columnId);
                            var j=0;
                            $("input[name*='checkbox_name']").each(function () {
                                if($("input[name='columnList["+j+"].id']").val()==data[i].columnId){
                                    //$(this).attr("checked", true);
                                    $("input[name='checkbox_name["+j+"]'").attr("checked", true);
                                    $("input[name='columnList["+j+"].sort'").val(data[i].sort);
                                    return true;
                                }

                                j++;
                            });

                        }
                    }
                }
            });

        }




    </script>
</head>
<body>

<form:form id="inputForm" modelAttribute="genTable" action="${ctx}/gen/genTable/busTableSave" method="post"
           class="form-horizontal">
    <form:hidden path="id"/>
    <sys:message content="${message}"/>
    <fieldset>
        <legend>基本信息</legend>
        <div class="control-group">
            <label class="control-label">表名:</label>
            <div class="controls">
                <form:input path="name" htmlEscape="false" maxlength="200" class="required" readonly="true"/>
            </div>
        </div>
        <div class="control-group">
            <label class="control-label">说明:</label>
            <div class="controls">
                <form:input path="comments" htmlEscape="false" maxlength="200" class="required"/>
            </div>
        </div>

        <div class="control-group">
            <label class="control-label">业务类型:</label>
            <div class="controls">
                <form:select path="busTableType" cssClass="input-xlarge" id="busTableType" onchange="showSelectedData()">
                    <form:option value="">无</form:option>
                    <form:options items="${dictList}" htmlEscape="false"/>
                </form:select>

            </div>
        </div>

        <legend>字段列表</legend>

        <div class="control-group">
            <table id="contentTable" class="table table-striped table-bordered table-condensed">
                <thead>
                <tr>
                    <th title="选择"><input type="checkbox" id="checkedAll"></th>
                    <th title="数据库字段名">列名</th>
                    <th title="默认读取数据库字段备注">说明</th>
                    <th title="数据库中设置的字段类型及长度">物理类型</th>
                    <th title="实体对象的属性字段类型">Java类型</th>
                    <th title="实体对象的属性字段（对象名.属性名|属性名2|属性名3，例如：用户user.id|name|loginName，属性名2和属性名3为Join时关联查询的字段）">
                        Java属性名称 <i class="icon-question-sign"></i></th>
                    <th title="是否是数据库主键">主键</th>
                    <th title="字段是否可为空值，不可为空字段自动进行空值验证">可空</th>

                    <th title="字段在表单中显示的类型">显示表单类型</th>
                    <th title="显示表单类型设置为“下拉框、复选框、点选框”时，需设置字典的类型">字典类型</th>
                    <th>排序</th>
                </tr>
                </thead>
                <tbody id="tableForm">
                <c:forEach items="${genTable.columnList}" var="column" varStatus="vs">
                    <tr <c:if test="${column.delFlag =='1'}"> style="display: none" </c:if>>
                        <td>
                            <input type="checkbox" name="checkbox_name[${vs.index}]"/>
                        </td>
                        <td nowrap>
                            <input type="hidden" name="columnList[${vs.index}].id" value="${column.id}"/>
                            <input type="hidden" name="columnList[${vs.index}].isList"
                                   value="0"/>
                            <input type="hidden" name="columnList[${vs.index}].genTable.id"
                                   value="${column.genTable.id}"/>
                                ${column.name}
                        </td>
                        <td>
                                ${column.comments}
                        </td>
                        <td nowrap>
                                ${column.jdbcType}
                        </td>
                        <td>
                                ${column.javaType}
                        </td>
                        <td>
                                ${column.javaField}
                        </td>
                        <td>
                            <input type="checkbox" name="columnList[${vs.index}].isPk"
                                   value="1" ${column.isPk eq '1' ? 'checked' : ''}/>
                        </td>
                        <td>
                            <input type="checkbox" name="columnList[${vs.index}].isNull"
                                   value="1" ${column.isNull eq '1' ? 'checked' : ''}/>
                        </td>


                        <td>
                                ${column.showType}
                        </td>
                        <td>
                                ${column.dictType}
                        </td>
                        <td>
                            <input type="text" name="columnList[${vs.index}].sort" value=""
                                   maxlength="200" class="required input-min digits"/>
                        </td>
                    </tr>

                </c:forEach>
                </tbody>
            </table>
        </div>
    </fieldset>
    <div class="form-actions">
        <shiro:hasPermission name="gen:genTable:edit"><input id="btnSubmit" class="btn btn-primary"
                                                             type="button" onclick="saveSelectData()"
                                                             value="保 存"/>&nbsp;</shiro:hasPermission>
        <input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>
    </div>
</form:form>

</body>
</html>
