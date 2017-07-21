package com.tonyhu.location.activity;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.tonyhu.location.R;
import com.tonyhu.location.db.Favorite;
import com.tonyhu.location.db.FavoriteDao;
import com.tonyhu.location.util.ScreenUtil;

import java.util.List;



/*Created by Administrator on 2017/4/5.
 */

public class MyFavoriteActivity extends BaseActivity {
    private final static int PADDING_OUTSIDE = ScreenUtil.dip2px(6);
    private final static int PADDING_INSIDE = ScreenUtil.dip2px(3);
    private RecyclerView.Adapter adapter;
    private List<Favorite> favorites;


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
    }

    private void initView() {
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.listview);
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

    class Holder extends RecyclerView.ViewHolder {
        TextView name;
        TextView address;

        public Holder(View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.title);
            address = (TextView) itemView.findViewById(R.id.address);
        }

        public void bind(int position) {
            final Favorite favorite = favorites.get(position);

            name.setText((position + 1) + "." + favorite.getName());
            address.setText(favorite.getAddress());
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                @TargetApi(Build.VERSION_CODES.KITKAT_WATCH)
                public void onClick(View v) {
                    Intent intent = new Intent(SearchActivity.ACTION_SEARCH_COMPLETE);
                    intent.putExtra("lat",favorite.getLatitude());
                    intent.putExtra("lng",favorite.getLongitude());
                    intent.putExtra("name",favorite.getName());
                    intent.putExtra("address",favorite.getAddress());
                    sendBroadcast(intent);
                    finish();
                }
            });

        }

    }

}
