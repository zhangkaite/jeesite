package com.thinkgem.jeesite.modules.gen.entity;

import com.thinkgem.jeesite.common.persistence.BaseEntity;

/**
 * Created by kate on 2018/1/4.
 */
public class GenBusTable extends BaseEntity<GenBusTable> {

    private String busType;
    private String columnId;
    private String tableId;
    private Integer sort;

    public String getBusType() {
        return busType;
    }

    public void setBusType(String busType) {
        this.busType = busType;
    }

    public String getColumnId() {
        return columnId;
    }

    public void setColumnId(String columnId) {
        this.columnId = columnId;
    }

    public String getTableId() {
        return tableId;
    }

    public void setTableId(String tableId) {
        this.tableId = tableId;
    }

    public Integer getSort() {
        return sort;
    }

    public void setSort(Integer sort) {
        this.sort = sort;
    }

    @Override
    public void preInsert() {

    }

    @Override
    public void preUpdate() {

    }
}
