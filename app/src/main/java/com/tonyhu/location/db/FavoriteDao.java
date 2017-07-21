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
            dao = DataBaseHelper.getHelper().getDao(Favorite.class);
//            dao = DBHelper.getHelper().getDao(Favorite.class);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public int add(Favorite favor) {
        try {
            return dao.create(favor);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
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

    public void delete(int id) {
        try {
            dao.deleteById(id);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteAll(List<Favorite> list) {
        try {
            dao.delete(list);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
