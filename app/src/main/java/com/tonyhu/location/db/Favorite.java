package com.tonyhu.location.db;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;

/**
 * Created by Administrator on 2017/3/27.
 */

@DatabaseTable(tableName = "favorite")
public class Favorite implements Serializable{
    private static final long serialVersionUID = 12345690l;
    @DatabaseField(generatedId=true,columnName="id")
    private Integer id;
    @DatabaseField(columnName="name")
    private String name;
    @DatabaseField(columnName="address")
    private String address;
    @DatabaseField(columnName="latitude")
    private Double latitude;
    @DatabaseField(columnName="longitude")
    private Double longitude;

    public Favorite() {

    }
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }
    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
