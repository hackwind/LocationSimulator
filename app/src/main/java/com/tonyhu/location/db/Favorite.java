package com.tonyhu.location.db;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;

/**
 * Created by Administrator on 2017/3/27.
 */

@DatabaseTable(tableName = "category")
public class Favorite implements Serializable{
    private static final long serialVersionUID = 12345690l;
    @DatabaseField(generatedId=true,columnName="id")
    private Integer id;
    @DatabaseField(columnName="category")
    private Integer category;
    @DatabaseField(columnName="order")
    private Integer order;
    @DatabaseField(columnName="name")
    private String name;
    @DatabaseField(columnName="image")
    private String image;

    @DatabaseField(columnName="parent_category")
    private int parentCategory;

    public Favorite() {

    }
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
    public Integer getCategory() {
        return category;
    }

    public void setCategory(Integer category) {
        this.category = category;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getParentCategory() {return this.parentCategory;}

    public void setParentCategory(int parentCategory){this.parentCategory = parentCategory;}

    public String getImage() {return this.image;}

    public void setImage(String image) {this.image = image;}
}
