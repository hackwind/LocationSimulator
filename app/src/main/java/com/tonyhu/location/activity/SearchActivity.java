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
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.poi.OnGetPoiSearchResultListener;
import com.baidu.mapapi.search.poi.PoiCitySearchOption;
import com.baidu.mapapi.search.poi.PoiDetailResult;
import com.baidu.mapapi.search.poi.PoiIndoorResult;
import com.baidu.mapapi.search.poi.PoiResult;
import com.baidu.mapapi.search.poi.PoiSearch;
import com.tonyhu.location.R;
import com.tonyhu.location.util.PreferenceUtil;

import java.util.List;


/*Created by Administrator on 2017/7/20.
 */

public class SearchActivity extends BaseActivity implements OnGetPoiSearchResultListener {
    private RecyclerView.Adapter adapter;
    private EditText etPlace;
    private EditText etCity;
    private RecyclerView recyclerView;
    private LinearLayout noResult;
    private PoiSearch poiSearch;
    private List<PoiInfo> poiInfos;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        Toolbar toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        toolbar.setTitleTextColor(getResources().getColor(android.R.color.white));
        toolbar.setVisibility(View.VISIBLE);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        initView();
        poiSearch = PoiSearch.newInstance();
        poiSearch.setOnGetPoiSearchResultListener(this);
    }

    private void initView() {
        etPlace = (EditText)findViewById(R.id.keyword);
        etPlace.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId,
                                          KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    String city = etCity.getText().toString();
                    String keyword = etPlace.getText().toString();
                    if(TextUtils.isEmpty(city)) {
                        Toast.makeText(getBaseContext(),"城市名不能为空",Toast.LENGTH_LONG).show();
                        return false;
                    }
                    if(!TextUtils.isEmpty(keyword)) {
                        PreferenceUtil.putString("city",city);
                        searchData(city,keyword);
                    }
                }

                return false;
            }
        });

        noResult = (LinearLayout)findViewById(R.id.no_result);

        etCity = (EditText)findViewById(R.id.city);
        String city = PreferenceUtil.getString("city");
        if(!TextUtils.isEmpty(city)) {
            etCity.setText(city);
        }

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
                return poiInfos == null ? 0 : poiInfos.size();
            }
        };
        recyclerView.setAdapter(adapter);
    }

    private void searchData(String city,String keyword) {
        PoiCitySearchOption citySearchOption = new PoiCitySearchOption();
        citySearchOption.city(city);// 城市
        citySearchOption.keyword(keyword);// 关键字
        citySearchOption.pageCapacity(20);// 默认每页10条
        citySearchOption.pageNum(1);// 分页编号
        // 为PoiSearch设置搜索方式.
        poiSearch.searchInCity(citySearchOption);

    }

    @Override
    protected void onDestroy() {
        poiSearch.destroy();
        super.onDestroy();
    }

    @Override
    public void onGetPoiResult(PoiResult poiResult) {
        if(poiInfos != null) {
            poiInfos.clear();
        }
        if (poiResult.error == SearchResult.ERRORNO.NO_ERROR) {
            noResult.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
            poiInfos = poiResult.getAllPoi();
            adapter.notifyDataSetChanged();
        } else {
            noResult.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        }
    }

    @Override
    public void onGetPoiDetailResult(PoiDetailResult poiDetailResult) {

    }

    @Override
    public void onGetPoiIndoorResult(PoiIndoorResult poiIndoorResult) {
    }

    class Holder extends RecyclerView.ViewHolder {
        TextView subTitle;
        TextView address;

        public Holder(View itemView) {
            super(itemView);
            subTitle = (TextView) itemView.findViewById(R.id.title);
            address = (TextView) itemView.findViewById(R.id.address);
        }

        public void bind(int position) {
            final PoiInfo info = poiInfos.get(position);
            subTitle.setText((position + 1 + ".") + info.name);
            address.setText(info.address);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                @TargetApi(Build.VERSION_CODES.KITKAT_WATCH)
                public void onClick(View v) {
                    Intent intent = new Intent();
                    intent.putExtra("lat",info.location.latitude);
                    intent.putExtra("lng",info.location.longitude);
                    intent.putExtra("name",info.name);
                    intent.putExtra("address",info.address);
                    setResult(RESULT_OK,intent);
                }
            });

        }

    }

}
