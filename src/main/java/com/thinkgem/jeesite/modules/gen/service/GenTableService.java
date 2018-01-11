/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.gen.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.thinkgem.jeesite.modules.gen.dao.GenBusTableDao;
import com.thinkgem.jeesite.modules.gen.entity.GenBusTable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.thinkgem.jeesite.common.persistence.Page;
import com.thinkgem.jeesite.common.service.BaseService;
import com.thinkgem.jeesite.common.utils.StringUtils;
import com.thinkgem.jeesite.modules.gen.entity.GenTable;
import com.thinkgem.jeesite.modules.gen.entity.GenTableColumn;
import com.thinkgem.jeesite.modules.gen.util.GenUtils;
import com.thinkgem.jeesite.modules.gen.dao.GenDataBaseDictDao;
import com.thinkgem.jeesite.modules.gen.dao.GenTableColumnDao;
import com.thinkgem.jeesite.modules.gen.dao.GenTableDao;

/**
 * 业务表Service
 *
 * @author ThinkGem
 * @version 2013-10-15
 */
@Service
@Transactional(readOnly = true)
public class GenTableService extends BaseService {

    @Autowired
    private GenTableDao genTableDao;
    @Autowired
    private GenTableColumnDao genTableColumnDao;
    @Autowired
    private GenDataBaseDictDao genDataBaseDictDao;
    @Autowired
    private GenBusTableDao genBusTableDao;

    public GenTable get(String id) {
        GenTable genTable = genTableDao.get(id);
        GenTableColumn genTableColumn = new GenTableColumn();
        genTableColumn.setGenTable(new GenTable(genTable.getId()));
        genTable.setColumnList(genTableColumnDao.findList(genTableColumn));
        return genTable;
    }

    public GenTable getByName(String name) {
        GenTable genTable = genTableDao.get(name);

        return genTable;
    }


    public Page<GenTable> find(Page<GenTable> page, GenTable genTable) {
        genTable.setPage(page);
        page.setList(genTableDao.findList(genTable));
        return page;
    }

    public List<GenTable> findAll() {
        return genTableDao.findAllList(new GenTable());
    }

    /**
     * 获取物理数据表列表
     *
     * @param genTable
     * @return
     */
    public List<GenTable> findTableListFormDb(GenTable genTable) {
        return genDataBaseDictDao.findTableList(genTable);
    }


    public List<GenTable> findGenTableList() {
        return  genTableDao.findDataList();
    }
    /**
     * 验证表名是否可用，如果已存在，则返回false
     *
     * @param tableName
     * @return
     */
    public boolean checkTableName(String tableName) {
        if (StringUtils.isBlank(tableName)) {
            return true;
        }
        GenTable genTable = new GenTable();
        genTable.setName(tableName);
        List<GenTable> list = genTableDao.findList(genTable);
        return list.size() == 0;
    }

    /**
     * 获取物理数据表列表
     *
     * @param genTable
     * @return
     */
    public GenTable getTableFormDb(GenTable genTable) {
        // 如果有表名，则获取物理表
        if (StringUtils.isNotBlank(genTable.getName())) {

            List<GenTable> list = genDataBaseDictDao.findTableList(genTable);
            if (list.size() > 0) {

                // 如果是新增，初始化表属性
                if (StringUtils.isBlank(genTable.getId())) {
                    genTable = list.get(0);
                    // 设置字段说明
                    if (StringUtils.isBlank(genTable.getComments())) {
                        genTable.setComments(genTable.getName());
                    }
                    genTable.setClassName(StringUtils.toCapitalizeCamelCase(genTable.getName()));
                }

                // 添加新列
                List<GenTableColumn> columnList = genDataBaseDictDao.findTableColumnList(genTable);
                for (GenTableColumn column : columnList) {
                    boolean b = false;
                    for (GenTableColumn e : genTable.getColumnList()) {
                        if (e.getName().equals(column.getName())) {
                            b = true;
                        }
                    }
                    if (!b) {
                        genTable.getColumnList().add(column);
                    }
                }

                // 删除已删除的列
                for (GenTableColumn e : genTable.getColumnList()) {
                    boolean b = false;
                    for (GenTableColumn column : columnList) {
                        if (column.getName().equals(e.getName())) {
                            b = true;
                        }
                    }
                    if (!b) {
                        e.setDelFlag(GenTableColumn.DEL_FLAG_DELETE);
                    }
                }

                // 获取主键
                genTable.setPkList(genDataBaseDictDao.findTablePK(genTable));

                // 初始化列属性字段
                GenUtils.initColumnField(genTable);

            }
        }
        return genTable;
    }

    @Transactional(readOnly = false)
    public void save(GenTable genTable) {
        if (StringUtils.isBlank(genTable.getId())) {
            genTable.preInsert();
            genTableDao.insert(genTable);
        } else {
            genTable.preUpdate();
            genTableDao.update(genTable);
        }
        // 保存列
        for (GenTableColumn column : genTable.getColumnList()) {
            column.setGenTable(genTable);
            if (StringUtils.isBlank(column.getId())) {
                column.preInsert();
                genTableColumnDao.insert(column);
                //alter table l_extend_user add `user_register_type` tinyint(4) DEFAULT '0' COMMENT '用户开放注册类型 ，
                // 目前教师空间有两种注册类型做下区分 ：0走区域码的逻辑，1非区域码逻辑';
                //组装修改表结构的sql语句
                String tableName = genTable.getName();
                String columnName = column.getName();
                String jdbcType = column.getJdbcType();
                String comments = column.getComments();
                if (StringUtils.isNotEmpty(columnName)){
                    String sql = "alter table " + tableName + " add  column " + columnName + " " + jdbcType + " COMMENT "
                            + "'" + comments + "'";
                    logger.info("inser modify table struct sql:" + sql);
                    Map<String, String> map = new HashMap<String, String>();
                    map.put("sql", sql);
                    genTableColumnDao.alterTable(map);
                }

            } else {
                column.preUpdate();
                genTableColumnDao.update(column);
                String tableName = genTable.getName();
                String columnName = column.getName();
                String jdbcType = column.getJdbcType();
                // String comments=column.getComments();
                Map<String, String> map = new HashMap<String, String>();
                String sql = "";
                if (column.getDelFlag().equals("1")) {//列的删除
                    //ALTER TABLE `entity_exercise` DROP COLUMN `type`,
                    sql = "alter table " + tableName + " DROP  COLUMN " + columnName;
                }/*else{//列的更新
                    // alter table user MODIFY new1 VARCHAR(10); 　//修改一个字段的类型
                    sql="alter table "+tableName+" MODIFY "+columnName+" "+jdbcType;
                }*/
                if (StringUtils.isNotEmpty(sql)){
                    logger.info("update modify table struct sql:" + sql);
                    map.put("sql", sql);
                    genTableColumnDao.alterTable(map);
                }

            }
        }
    }

    @Transactional(readOnly = false)
    public void delete(GenTable genTable) {
        genTableDao.delete(genTable);
        genTableColumnDao.deleteByGenTableId(genTable.getId());
    }

    @Transactional
    public void saveBusTableData(GenTable genTable) {
        //保存之前先删除之前的数据然后保存数据
        String busTableType = genTable.getBusTableType();
        Map delMap = new HashMap();
        delMap.put("busTableType", busTableType);
        genBusTableDao.deleteBusTableData(delMap);
        List<GenBusTable> busTableList = new ArrayList<GenBusTable>();
        String tableId = genTable.getId();
        for (GenTableColumn column : genTable.getColumnList()) {
            if (column.getIsList().equals("1")) {
                String columnId = column.getId();
                int sort = column.getSort();
                GenBusTable genBusTable = new GenBusTable();
                genBusTable.setBusType(busTableType);
                genBusTable.setColumnId(columnId);
                genBusTable.setTableId(tableId);
                genBusTable.setSort(sort);
                busTableList.add(genBusTable);
            }

        }
        genBusTableDao.insertBusTableData(busTableList);

    }



    public Page<GenBusTable> genBusTableList(Page<GenBusTable> page, GenBusTable genBusTable) {
        genBusTable.setPage(page);
        List<GenBusTable> resultList = genBusTableDao.getBusTableList(genBusTable);
        page.setList(resultList);
        return page;
    }

    public List<GenBusTable> getSelectColumnList(String busType){
        GenBusTable param=new GenBusTable();
        param.setBusType(busType);
        List<GenBusTable> list=genBusTableDao.findList(param);
        return list;

    }


}
