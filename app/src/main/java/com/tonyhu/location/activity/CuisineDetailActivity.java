package com.tonyhu.location.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.qq.e.ads.banner.ADSize;
import com.qq.e.ads.banner.AbstractBannerADListener;
import com.qq.e.ads.banner.BannerView;
import com.tonyhu.cookbook.R;
import com.tonyhu.location.util.Constants;
import com.tonyhu.location.widget.TonyScrollView;

import java.util.List;


/*Created by Administrator on 2017/3/29.
 */

public class CuisineDetailActivity extends BaseActivity implements TonyScrollView.OnScrollListener{
    private TonyScrollView rootView;
    private ImageView bannerView;
    private TextView cuisineName;
    private RecyclerView ryMaterials;
    private RecyclerView rySteps;
    private RecyclerView.Adapter materialAdapter;
    private RecyclerView.Adapter stepsAdapter;
    private ImageView addFavorite;
    private TextView addFavoriteAnim;
    private LinearLayout toolbar;
    private TextView title;
    private LinearLayout adView;

    private String[] materialsNames;
    private String[] materialsValues;
    private String[] steps;
    private String[] step_images;
    private int cuisineId;
    private String cusineName;
    private int isFavor = 0;

    private CuisineDao cuisineDao;
    private Cuisine cuisine;

    private StepDao stepDao;
    private IngredientsDao ingredientsDao;
    private int oldColor;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTranslucentStatus();
        setContentView(R.layout.activity_suisine_detail);
        cuisineId = getIntent().getIntExtra("cuisine_id",0);
        cusineName = getIntent().getStringExtra("cuisine_name");
        initView();
        getData();
        loadAd();
    }

    private void initView() {
        toolbar = (LinearLayout) findViewById(R.id.my_toolbar);
        title = (TextView)toolbar.findViewById(R.id.title);
        bannerView = (ImageView)findViewById(R.id.detail_banner);
        cuisineName = (TextView)findViewById(R.id.detail_title);
        ryMaterials = (RecyclerView)findViewById(R.id.detail_recycleview_material);
        rySteps = (RecyclerView)findViewById(R.id.detail_recycleview_steps);
        addFavorite = (ImageView)findViewById(R.id.detail_addFavorite);
        addFavoriteAnim = (TextView)findViewById(R.id.addfav_anim);
        adView = (LinearLayout)findViewById(R.id.bannerview);

        GridLayoutManager matLayoutManager = new GridLayoutManager(this, 2){
            @Override
            public boolean canScrollVertically() {
                return false;//避免在外部嵌套ScrollView时容易卡主,胡爸真棒^_^
            }
        };
        matLayoutManager.setOrientation(GridLayoutManager.VERTICAL);
        ryMaterials.setLayoutManager(matLayoutManager);
        GridLayoutManager stepLayoutManager = new GridLayoutManager(this, 1) {
            @Override
            public boolean canScrollVertically() {
                return false;//避免在外部嵌套ScrollView时容易卡主,胡爸真棒^_^
            }
        };
        stepLayoutManager.setOrientation(GridLayoutManager.VERTICAL);
        rySteps.setLayoutManager(stepLayoutManager);

        materialAdapter = new RecyclerView.Adapter<MaterialHolder>() {
            @Override
            public MaterialHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                @SuppressLint("InflateParams")
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_material, null);
                return new MaterialHolder(view);
            }

            @Override
            public void onBindViewHolder(MaterialHolder holder, int position) {
                holder.bind(position);
            }

            @Override
            public int getItemCount() {
                return materialsNames == null ? 0 : materialsNames.length * 2;
            }
        };
        ryMaterials.setAdapter(materialAdapter);

        stepsAdapter = new RecyclerView.Adapter<StepHolder>() {
            @Override
            public StepHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                @SuppressLint("InflateParams")
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_steps, null);
                return new StepHolder(view);
            }

            @Override
            public void onBindViewHolder(StepHolder holder, int position) {
                holder.bind(position);
            }

            @Override
            public int getItemCount() {
                return steps == null ? 0 : steps.length;
            }
        };
        rySteps.setAdapter(stepsAdapter);

        addFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isFavor == 1) {
                    updateFavorStatus(0);
                } else {
                    updateFavorStatus(1);
                }
                addFavoriteAnim.setVisibility(View.VISIBLE);
                addFavoriteAnim.startAnimation(AnimationUtils.loadAnimation(CuisineDetailActivity.this,R.anim.add_favorite_anim));
                new Handler().postDelayed(new Runnable() {
                    public void run() {
                        addFavoriteAnim.setVisibility(View.GONE);
                    }
                }, 1000);

            }
        });

        rootView = (TonyScrollView)findViewById(R.id.rootview);
        rootView.postDelayed(new Runnable() {
            @Override
            public void run() {
             rootView.scrollTo(0,0);
            }
        },100);
        rootView.setOnScrollListener(this);

        bannerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CuisineDetailActivity.this,PhotoViewActivity.class);
                intent.putExtra("cuisine",cusineName);
                intent.putExtra("currentPosition",0);
                intent.putExtra("urls",step_images);
                intent.putExtra("descs",steps);
                startActivity(intent);
            }
        });
    }

    private void updateFavorStatus(int status) {
        isFavor = status;
        if(status == 0) {
            addFavoriteAnim.setText(R.string.remove_favorite);
            addFavorite.setImageResource(R.drawable.remove_favorite);
        } else {
            addFavoriteAnim.setText(R.string.add_favorite);
            addFavorite.setImageResource(R.drawable.add_favorite);
        }
        if(cuisineDao != null && cuisine != null) {
            cuisine.setIsFavorite(status);
            cuisineDao.update(cuisine);
        }
    }

    private void getData(){
        if(cuisineId <= 0) {
            return;
        }
        cuisineDao = new CuisineDao();
        cuisine = cuisineDao.get(cuisineId);
        if(cuisine == null) {
            return ;
        }
        String bannerIUrl = cuisine.getBannerImage();
        if(!TextUtils.isEmpty(bannerIUrl)) {
//            try {
//                Bitmap bitmap = ImageUtil.getAssetsBitmap(cusineName, bannerIUrl);
//                if (bitmap != null) {
//                    bannerView.setImageBitmap(bitmap);
//                    int width = ScreenUtil.getScreenWidth();
//                    int height = width * 225 / 300;
//                    ViewGroup.LayoutParams params = bannerView.getLayoutParams();
//                    params.width = width;
//                    params.height = height;
//                    bannerView.setLayoutParams(params);
//                }

//            } catch(OutOfMemoryError e) {
//                e.printStackTrace();
//                System.gc();
//            }
            AsyncImageLoader.getInstance().loadBitmaps("1",bannerView,cusineName,bannerIUrl);
        }
        cuisineName.setText(cuisine.getName());
        title.setText(cuisine.getName());

        isFavor = cuisine.getIsFavorite();
        if(isFavor == 1) {
            addFavorite.setImageResource(R.drawable.add_favorite);
        }

        ingredientsDao = new IngredientsDao();
        List<Ingredients> ingredientses = ingredientsDao.listByName(cuisine.getName());
        if(ingredientses != null && ingredientses.size() > 0) {
            materialsNames = new String[ingredientses.size()];
            materialsValues = new String[ingredientses.size()];
            int i = 0;
            for(Ingredients ingredients : ingredientses){
                materialsNames[i] = ingredients.getKey();
                materialsValues[i] = ingredients.getValue();
                i++;
            }
        }

        stepDao = new StepDao();
        List<Step> sps = stepDao.listByName(cuisine.getName());

        if(sps != null && sps.size() > 0) {
            steps = new String[sps.size()];
            step_images = new String[sps.size()];
            int i = 0;
            for(Step s : sps){
                steps[i] = s.getStep();
                step_images[i] = s.getPic();
                i++;
            }
        }
    }

    @Override
    public void onScroll(int y) {
        float height = (int) (bannerView.getHeight() * 0.6);
        float fTemp = ((float) y) / height;
        if (fTemp > 1)
            fTemp = 1;
        if (fTemp <= 0)
            fTemp = 0;
        String hex = Integer.toHexString((int) (255 * fTemp));
        if (hex.length() == 1)
            hex = "0".concat(hex);
        oldColor = Color.parseColor("#".concat(hex).concat("e64e40"));
        toolbar.setBackgroundColor(oldColor);
        oldColor = Color.parseColor("#".concat(hex).concat("ffffff"));
        title.setTextColor(oldColor);
    }

    private void loadAd() {
        BannerView bv = new BannerView(this, ADSize.BANNER,
                Constants.GDT_APPID, Constants.GDT_PAGE_DETAIL_POS_ID);
        bv.setRefresh(30);// 广告轮播时间 按钮默认关闭
        bv.setADListener(new AbstractBannerADListener() {

            @Override
            public void onNoAD(int arg0) {
                // 广告加载失败
                adView.setVisibility(View.GONE);
            }

            @Override
            public void onADReceiv() {
                // 加载广告成功时
                adView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onADClicked() {
                // 广告点击时
                super.onADClicked();
            }
        });
        adView.addView(bv);
        adView.setVisibility(View.GONE);
        bv.loadAD();

    }


    class MaterialHolder extends RecyclerView.ViewHolder {
        TextView name;

        public MaterialHolder(View itemView) {
            super(itemView);
            name = (TextView)itemView.findViewById(R.id.material);
        }

        public void bind(int position) {
            if(position % 2 == 0) {
                name.setText(materialsNames[position / 2] + ":");
            } else {
                name.setText(materialsValues[position / 2 ] );
            }

        }
    }

    class StepHolder extends RecyclerView.ViewHolder {
        TextView step;
        ImageView image;

        public StepHolder(View itemView) {
            super(itemView);
            step = (TextView)itemView.findViewById(R.id.step);
            image = (ImageView)itemView.findViewById(R.id.step_image);
        }

        public void bind(final int position) {
           step.setText((position + 1) + "." + steps[position]);
            if(step_images != null && !TextUtils.isEmpty(step_images[position]) && !"null".equals(step_images[position])) {
//                try{
//                    Bitmap bitmap = ImageUtil.getAssetsBitmap(cusineName, step_images[position]);
//                    if(bitmap != null) {
//                        image.setImageBitmap(bitmap);
//                        image.setVisibility(View.VISIBLE);
//                    } else {
//                        image.setVisibility(View.GONE);
//                    }
//                } catch(OutOfMemoryError e) {
//                    e.printStackTrace();
//                    System.gc();
//                }
                AsyncImageLoader.getInstance().loadBitmaps("1",image,cusineName,step_images[position]);
            } else {
                image.setVisibility(View.GONE);
            }
            image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(CuisineDetailActivity.this,PhotoViewActivity.class);
                    intent.putExtra("cuisine",cusineName);
                    intent.putExtra("currentPosition",position);
                    intent.putExtra("urls",step_images);
                    intent.putExtra("descs",steps);
                    startActivity(intent);
                }
            });
        }
    }

}
