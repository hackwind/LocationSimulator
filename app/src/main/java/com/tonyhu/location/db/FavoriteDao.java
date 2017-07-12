package com.tonyhu.location.db;

import com.j256.ormlite.dao.Dao;

import java.sql.SQLException;
import java.util.List;

/**
 * Created by Administrator on 2017/3/27.
 */

public class FavoriteDao {

    private Dao<Favorite,Integer> dao;

    public FavoriteDao() {
        try {
//            dao = DataBaseHelper.getHelper().getDao(Favorite.class);
            dao = DBHelper.getHelper().getDao(Favorite.class);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void addCategory(Favorite category) {
        try {
            dao.create(category);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Favorite get(int id) {
        try {
            return dao.queryForId(id);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public List<Favorite> listAll() {
        try {
            return dao.queryForAll();
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public List<Favorite> listByCategory(int category) {
        try {
            return  dao.queryBuilder().orderBy("category",true).where().eq("category",category).query();
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
    public List<Favorite> listByParentCategory(int category) {
        try {
            return  dao.queryBuilder().orderBy("category",true).where().eq("parent_category",category).query();
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void update(Favorite t){
        try {
            dao.update(t);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
