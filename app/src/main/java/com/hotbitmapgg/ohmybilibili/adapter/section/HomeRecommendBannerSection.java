package com.hotbitmapgg.ohmybilibili.adapter.section;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.hotbitmapgg.ohmybilibili.R;
import com.hotbitmapgg.ohmybilibili.widget.banner.BannerEntity;
import com.hotbitmapgg.ohmybilibili.widget.banner.BannerView;
import com.hotbitmapgg.ohmybilibili.widget.sectioned.StatelessSection;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by hcc on 16/8/27 14:06
 * 100332338@qq.com
 * <p/>
 * 首页推荐界面轮播图Section
 */
public class HomeRecommendBannerSection extends StatelessSection
{

    private List<BannerEntity> banners = new ArrayList<>();

    public HomeRecommendBannerSection(List<BannerEntity> banners)
    {

        super(R.layout.layout_banner, R.layout.layout_home_recommend_empty);
        this.banners = banners;
    }

    @Override
    public int getContentItemsTotal()
    {

        return 1;
    }

    @Override
    public RecyclerView.ViewHolder getItemViewHolder(View view)
    {

        return new ItemViewHolder(view);
    }

    @Override
    public void onBindItemViewHolder(RecyclerView.ViewHolder holder, int position)
    {

    }


    @Override
    public RecyclerView.ViewHolder getHeaderViewHolder(View view)
    {

        return new BannerViewHolder(view);
    }

    @Override
    public void onBindHeaderViewHolder(RecyclerView.ViewHolder holder)
    {

        BannerViewHolder bannerViewHolder = (BannerViewHolder) holder;
        bannerViewHolder.mBannerView.delayTime(5).build(banners);
    }


    static class ItemViewHolder extends RecyclerView.ViewHolder
    {

        public ItemViewHolder(View itemView)
        {

            super(itemView);
        }
    }


    static class BannerViewHolder extends RecyclerView.ViewHolder
    {

        @Bind(R.id.home_recommended_banner)
        BannerView mBannerView;

        BannerViewHolder(View itemView)
        {

            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
