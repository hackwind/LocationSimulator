package com.tonyhu.location.activity;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.qq.e.ads.banner.ADSize;
import com.qq.e.ads.banner.AbstractBannerADListener;
import com.qq.e.ads.banner.BannerView;
import com.tonyhu.location.R;
import com.tonyhu.location.db.Favorite;
import com.tonyhu.location.db.FavoriteDao;
import com.tonyhu.location.util.Constants;
import com.tonyhu.location.util.ScreenUtil;

import java.util.ArrayList;
import java.util.List;



/*Created by Administrator on 2017/4/5.
 */

public class MyFavoriteActivity extends BaseActivity implements View.OnClickListener{
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private List<Favorite> favorites;
    private LinearLayout bottomLayout;
    private TextView btnEdit;
    private TextView btnSelectAll;
    private TextView btnDelete;
    private List<Integer> selectList;
    private boolean isEditStatus = false;
    private LinearLayout adView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite);

        Toolbar toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        toolbar.setTitleTextColor(getResources().getColor(android.R.color.white));
        toolbar.setVisibility(View.VISIBLE);
        toolbar.setTitle(R.string.drawer_favorite);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        initView();
        getData();
        loadAd();
    }

    private void initView() {
        recyclerView = (RecyclerView) findViewById(R.id.listview);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(GridLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);

        adapter = new RecyclerView.Adapter<Holder>() {
            @Override
            public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
                @SuppressLint("InflateParams")
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_place_list, null);
                return new Holder(view);
            }

            @Override
            public void onBindViewHolder(Holder holder, int position) {
                holder.bind(position);

            }

            @Override
            public int getItemCount() {
                return favorites == null ? 0 : favorites.size();
            }
        };
        recyclerView.setAdapter(adapter);

        adView = (LinearLayout)findViewById(R.id.bannerview);

        bottomLayout = (LinearLayout)findViewById(R.id.bottom_layout);
        btnDelete = (TextView)findViewById(R.id.btn_delete);
        btnEdit = (TextView)findViewById(R.id.btn_edit);
        btnSelectAll = (TextView)findViewById(R.id.btn_select_all);
        btnSelectAll.setOnClickListener(this);
        btnEdit.setOnClickListener(this);
        btnDelete.setOnClickListener(this);
    }

    private void getData() {
        FavoriteDao dao = new FavoriteDao();
        favorites = dao.listAll();
        if(favorites == null) {
            return;
        }

        if(adapter != null) {
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_edit:
                edit();
                break;
            case R.id.btn_delete:
                delete();
                break;
            case R.id.btn_select_all:
                selectAll();
                break;
        }
    }
    private void edit(){
        isEditStatus = !isEditStatus;
        if(isEditStatus) {
            btnEdit.setText("完成");
            btnDelete.setEnabled(true);
            btnDelete.setTextColor(Color.parseColor("#000000"));
            btnSelectAll.setEnabled(true);
            btnSelectAll.setTextColor(Color.parseColor("#000000"));
        } else {
            btnEdit.setText("编辑");
            btnDelete.setEnabled(false);
            btnDelete.setTextColor(Color.parseColor("#636363"));
            btnSelectAll.setEnabled(false);
            btnSelectAll.setTextColor(Color.parseColor("#636363"));
        }
        adapter.notifyDataSetChanged();
        recyclerView.invalidate();
    }

    private void delete() {
        if(selectList == null || favorites == null) {
            return;
        }
        List<Favorite> tempList = new ArrayList<>();
        for(Favorite favorite : favorites) {
            if(selectList.contains(favorite.getId())) {
                tempList.add(favorite);
            }
        }
        favorites.removeAll(tempList);
        selectList.clear();
        FavoriteDao dao = new FavoriteDao();
        dao.deleteAll(tempList);
        tempList.clear();
        tempList = null;
        adapter.notifyDataSetChanged();
        recyclerView.invalidate();
    }

    private void selectAll() {
        if(selectList == null) {
            selectList = new ArrayList<>();
        }
        if(selectList.size() == favorites.size()) {//取消全选
            selectList.clear();
        } else {//全选
            selectList.clear();
            for (Favorite favorite : favorites) {
                if (!selectList.contains(favorite.getId())) {
                    selectList.add(favorite.getId());
                }
            }
        }
        adapter.notifyDataSetChanged();
        recyclerView.invalidate();
    }

    class Holder extends RecyclerView.ViewHolder {
        TextView name;
        TextView address;
        CheckBox checkBox;

        public Holder(View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.title);
            address = (TextView) itemView.findViewById(R.id.address);
            checkBox = (CheckBox) itemView.findViewById(R.id.checkbox);
        }

        public void bind(int position) {
            final Favorite favorite = favorites.get(position);
            if(selectList != null && selectList.contains(favorite.getId())){
                checkBox.setChecked(true);
            } else {
                checkBox.setChecked(false);
            }
            if(isEditStatus) {
                checkBox.setVisibility(View.VISIBLE);
            } else {
                checkBox.setVisibility(View.GONE);
            }
            name.setText((position + 1) + "." + favorite.getName());
            address.setText(favorite.getAddress());
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                @TargetApi(Build.VERSION_CODES.KITKAT_WATCH)
                public void onClick(View v) {
                    if(isEditStatus) {
                        if(checkBox.isChecked()) {
                            checkBox.setChecked(false);
                        } else {
                            checkBox.setChecked(true);
                        }
                        return;
                    }
                    Intent intent = new Intent(SearchActivity.ACTION_SEARCH_COMPLETE);
                    intent.putExtra("lat",favorite.getLatitude());
                    intent.putExtra("lng",favorite.getLongitude());
                    intent.putExtra("name",favorite.getName());
                    intent.putExtra("address",favorite.getAddress());
                    sendBroadcast(intent);
                    finish();
                }
            });

            checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                   if(selectList == null) {
                       selectList = new ArrayList<>();
                   }
                   if(b && !selectList.contains(favorite.getId())) {
                       selectList.add(favorite.getId());
                   } else if(!b){
                       selectList.remove(favorite.getId());
                   }
                }
            });

        }

    }

    private void loadAd() {
        BannerView bv = new BannerView(this, ADSize.BANNER,
                Constants.GDT_APPID, Constants.GDT_PAGE_FAVORITE);
        bv.setRefresh(30);// 广告轮播时间 按钮默认关闭
        bv.setADListener(new AbstractBannerADListener() {

            @Override
            public void onNoAD(int arg0) {
                // 广告加载失败
            }

            @Override
            public void onADReceiv() {
                // 加载广告成功时
            }

            @Override
            public void onADClicked() {
                // 广告点击时
                super.onADClicked();
            }
        });
        adView.addView(bv);
        bv.loadAD();

    }

}
