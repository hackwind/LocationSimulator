package com.tonyhu.location.db;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;

/**
 * Created by Administrator on 2017/3/27.
 */

@DatabaseTable(tableName = "countdown")
public class CountDown implements Serializable{
    private static final long serialVersionUID = 12345690l;
    @DatabaseField(generatedId=true,columnName="id")
    private Integer id;
    @DatabaseField(columnName="count")
    private Integer count;
    @DatabaseField(columnName="type")
    private Integer type;// 1:穿越;2:神游;

    public CountDown() {

    }
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

}
