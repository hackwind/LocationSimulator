package com.tonyhu.location.activity;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tonyhu.location.R;
import com.tonyhu.location.db.Favorite;
import com.tonyhu.location.util.ScreenUtil;

import java.util.List;


/*Created by Administrator on 2017/4/5.
 */

public class SearchActivity extends BaseActivity {
    private final static int PADDING_OUTSIDE = 20;
    private final static int PADDING_INSIDE = 10;
    private RecyclerView.Adapter adapter;
    private List<Favorite> cuisineItems;
    private EditText editText;
    private RecyclerView recyclerView;
    private LinearLayout noResult;
    private TextView cancel;

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
    }

    private void initView() {
        editText = (EditText)findViewById(R.id.keyword);
        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId,
                                          KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    String keyword = editText.getText().toString();
                    if(!TextUtils.isEmpty(keyword)) {
                        searchData(keyword);
                    }
                }

                return false;
            }
        });

        noResult = (LinearLayout)findViewById(R.id.no_result);

        cancel = (TextView)findViewById(R.id.cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editText.getText().clear();
                editText.clearFocus();
            }
        });

        recyclerView = (RecyclerView) findViewById(R.id.listview);
        GridLayoutManager layoutManager = new GridLayoutManager(this, 2);
        layoutManager.setOrientation(GridLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);

        adapter = new RecyclerView.Adapter<Holder>() {
                @Override
                public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
                    @SuppressLint("InflateParams")
                    View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_subcuisine_list, null);
                    return new Holder(view);
                }

            @Override
            public void onBindViewHolder(Holder holder, int position) {
                holder.bind(position);

                int screenWidth = ScreenUtil.getScreenWidth();
                int width = (screenWidth - PADDING_INSIDE *2 - PADDING_OUTSIDE * 2) / 2;
                int height = width + (int)getResources().getDimension(R.dimen.cardview_title_height);

                CardView.LayoutParams params = new CardView.LayoutParams(width,height);
                params.gravity = Gravity.CENTER_HORIZONTAL;
                params.bottomMargin = PADDING_OUTSIDE;
                params.topMargin = PADDING_OUTSIDE;
                if(position%2 == 0) {
                    params.leftMargin = PADDING_OUTSIDE;
                    params.rightMargin = PADDING_INSIDE;
                } else {
                    params.leftMargin = PADDING_INSIDE;
                    params.rightMargin = PADDING_OUTSIDE;
                }
                holder.itemView.setLayoutParams(params);
                ImageView img = (ImageView)holder.itemView.findViewById(R.id.sub_image);
                ViewGroup.LayoutParams imgParams = img.getLayoutParams();
                imgParams.width = imgParams.height = width;
                img.setLayoutParams(imgParams);
            }

            @Override
            public int getItemCount() {
                return cuisineItems == null ? 0 : cuisineItems.size();
            }
        };
        recyclerView.setAdapter(adapter);

        if(!this.isFinishing()) {
            editText.postDelayed(new Runnable() {
                @Override
                public void run() {
                    editText.requestFocus();
                    InputMethodManager inputMethodManager = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputMethodManager.showSoftInput(editText,0);
                }
            },1000);
        }
    }

    private void searchData(String keyword) {
//        Favorite dao = new Favorite();
//        cuisineItems = dao.search(keyword);
//
//
//        adapter.notifyDataSetChanged();
//        if(cuisineItems == null || cuisineItems.size() == 0) {
//            recyclerView.setVisibility(View.GONE);
//            noResult.setVisibility(View.VISIBLE);
//            return;
//        } else {
//            noResult.setVisibility(View.GONE);
//            recyclerView.setVisibility(View.VISIBLE);
//        }
    }

    class Holder extends RecyclerView.ViewHolder {
        ImageView subImage;
        TextView subTitle;

        public Holder(View itemView) {
            super(itemView);
            subImage = (ImageView) itemView.findViewById(R.id.sub_image);
            subTitle = (TextView) itemView.findViewById(R.id.sub_title);
        }

        public void bind(int position) {
//            final Cuisine cuisine = cuisineItems.get(position);
//            String cover = cuisine.getBannerImage();
//            if (cover == null) {
//                // set default image
//                subImage.setImageResource(R.drawable.default_image);
//            } else {
//                Bitmap bitmap = ImageUtil.getAssetsBitmap(cuisine.getName(), cover);
//                if (bitmap != null) {
//                    subImage.setImageBitmap(bitmap);
//                }
//            }
//            subTitle.setText(cuisine.getName());
//            itemView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                @TargetApi(Build.VERSION_CODES.KITKAT_WATCH)
//                public void onClick(View v) {
//                    Intent intent = new Intent(SearchActivity.this, CuisineDetailActivity.class);
//                    intent.putExtra("cuisine_id", cuisine.getId());
//                    intent.putExtra("cuisine_name", cuisine.getName());
//                    startActivity(intent);
//                }
//            });

        }

    }

}
