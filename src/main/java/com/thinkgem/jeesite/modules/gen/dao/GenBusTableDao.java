/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.gen.dao;

import com.thinkgem.jeesite.common.persistence.CrudDao;
import com.thinkgem.jeesite.common.persistence.annotation.MyBatisDao;
import com.thinkgem.jeesite.modules.gen.entity.GenBusTable;
import com.thinkgem.jeesite.modules.gen.entity.GenTableColumn;

import java.util.List;
import java.util.Map;

/**
 * 业务表字段DAO接口
 * @author ThinkGem
 * @version 2013-10-15
 */
@MyBatisDao
public interface GenBusTableDao extends CrudDao<GenBusTable> {

    public void deleteBusTableData(Map map);

    public void insertBusTableData(List<GenBusTable> list);

    public List<GenBusTable> getBusTableList(GenBusTable genBusTable);

}
