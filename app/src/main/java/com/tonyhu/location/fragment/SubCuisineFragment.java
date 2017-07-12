package com.tonyhu.location.fragment;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.GridLayoutManager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.support.v7.widget.RecyclerView;
import android.widget.ImageView;
import android.widget.TextView;

import com.tonyhu.cookbook.R;
import com.tonyhu.location.activity.CuisineDetailActivity;
import com.tonyhu.location.util.ScreenUtil;

import java.util.List;

public class SubCuisineFragment extends Fragment {
        private final static String TYPE = "type";
        private final static String NAME = "name";
        private final static int PADDING_OUTSIDE = ScreenUtil.dip2px(6);
        private final static int PADDING_INSIDE = ScreenUtil.dip2px(3);
        private View rootView;
        private RecyclerView.Adapter adapter;
        private List<Cuisine> cuisineItems;
        private int type;
        private String name;

        public SubCuisineFragment() {
        }

    public static SubCuisineFragment newInstance(int typeId,String name) {
        SubCuisineFragment fragment = new SubCuisineFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(TYPE,typeId);
        bundle.putString(NAME,name);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
        type = getArguments().getInt(TYPE);
        name = getArguments().getString(NAME);
        getData(type);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if(rootView == null) {
            rootView = inflater.inflate(R.layout.fragment_sub_cuisine, null);
            initView(rootView);
        }
        return rootView;
    }

    private void initView(View view) {
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.listview);
        GridLayoutManager layoutManager = new GridLayoutManager(getContext(), 2);
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
                int width = (screenWidth - PADDING_INSIDE * 2 - PADDING_OUTSIDE * 2) / 2;
                int height = width + (int)getResources().getDimension(R.dimen.cardview_title_height);

                CardView.LayoutParams params = new CardView.LayoutParams(width,height);
                params.gravity = Gravity.CENTER_HORIZONTAL;
                params.bottomMargin = PADDING_OUTSIDE;
                params.topMargin = PADDING_OUTSIDE;
                if(position % 2 == 0) {
                    params.leftMargin = PADDING_OUTSIDE;
                    params.rightMargin = PADDING_INSIDE;
                } else {
                    params.leftMargin = PADDING_INSIDE;
                    params.rightMargin = 0;
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
    }

    private void getData(int type) {
        CuisineDao dao = new CuisineDao();
        cuisineItems = dao.listByCuisineType(type);
        if(cuisineItems == null) {
            return;
        }

        if(adapter != null) {
            adapter.notifyDataSetChanged();
        }
    }


    class Holder extends RecyclerView.ViewHolder {
        ImageView subImage;
        TextView subTitle;

        public Holder(View itemView) {
            super(itemView);
            subImage = (ImageView)itemView.findViewById(R.id.sub_image);
            subTitle = (TextView)itemView.findViewById(R.id.sub_title);
        }

        public void bind(int position) {
            final Cuisine cuisine = cuisineItems.get(position);
            String cover = cuisine.getBannerImage();
            if(cover == null) {
                // set default image
                subImage.setImageResource(R.drawable.default_image);
            } else {
//                try {
//                    Bitmap bitmap = ImageUtil.getAssetsBitmap(cuisine.getName(), cover);
//                    if (bitmap != null) {
//                        subImage.setImageBitmap(bitmap);
//                    }
//                }catch(OutOfMemoryError o) {
//                    o.printStackTrace();
//                    System.gc();
//                }
                AsyncImageLoader.getInstance().loadBitmaps("1",subImage,cuisine.getName(),cover);
            }
            subTitle.setText(cuisine.getName());
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                @TargetApi(Build.VERSION_CODES.KITKAT_WATCH)
                public void onClick(View v) {
                    Intent intent = new Intent(getActivity(), CuisineDetailActivity.class);
                    intent.putExtra("cuisine_id",cuisine.getId());
                    intent.putExtra("cuisine_type_name",name);
                    intent.putExtra("cuisine_name",cuisine.getName());
                    startActivity(intent);
                }
            });

        }
    }
}
