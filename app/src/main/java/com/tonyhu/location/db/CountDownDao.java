package com.tonyhu.location.db;

import com.j256.ormlite.dao.Dao;

import java.sql.SQLException;
import java.util.List;

/**
 * Created by Administrator on 2017/3/27.
 */

public class CountDownDao {

    private Dao<CountDown,Integer> dao;

    public CountDownDao() {
        try {
            dao = DataBaseHelper.getHelper().getDao(CountDown.class);
//            dao = DBHelper.getHelper().getDao(CountDown.class);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public int add(CountDown countDown) {
        try {
            return dao.create(countDown);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public CountDown get(int id) {
        try {
            return dao.queryForId(id);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public List<CountDown> listAll() {
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

    public void deleteAll(List<CountDown> list) {
        try {
            dao.delete(list);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void update(CountDown countDown) {
        try {
            dao.update(countDown);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void insert(CountDown countDown){
        try {
            dao.create(countDown);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
