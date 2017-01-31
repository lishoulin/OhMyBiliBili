package com.hotbitmapgg.ohmybilibili.module.home.discover;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.flyco.tablayout.SlidingTabLayout;
import com.hotbitmapgg.ohmybilibili.R;
import com.hotbitmapgg.ohmybilibili.base.RxAppCompatBaseActivity;

import butterknife.Bind;

/**
 * Created by hcc on 16/9/12 20:20
 * 100332338@qq.com
 * <p/>
 * 原创排行榜
 */
public class OriginalRankActivity extends RxAppCompatBaseActivity
{

    @Bind(R.id.toolbar)
    Toolbar mToolbar;

    @Bind(R.id.sliding_tabs)
    SlidingTabLayout mSlidingTabLayout;

    @Bind(R.id.view_pager)
    ViewPager mViewPager;

    private String[] titles = new String[]{"原创", "全站", "最新"};

    private String[] orders = new String[]{"hot", "damku", "new"};

    @Override
    public int getLayoutId()
    {

        return R.layout.activity_original_rank;
    }

    @Override
    public void initViews(Bundle savedInstanceState)
    {

        mViewPager.setAdapter(new OriginalRankPagerAdapter(getSupportFragmentManager(), titles, orders));
        mSlidingTabLayout.setViewPager(mViewPager);
    }

    @Override
    public void initToolBar()
    {

        mToolbar.setTitle("原创排行榜");
        setSupportActionBar(mToolbar);
        ActionBar supportActionBar = getSupportActionBar();
        if (supportActionBar != null)
            supportActionBar.setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {

        if (item.getItemId() == android.R.id.home)
            onBackPressed();
        return super.onOptionsItemSelected(item);
    }


    private static class OriginalRankPagerAdapter extends FragmentStatePagerAdapter
    {

        private String[] titles;

        private String[] orders;

        OriginalRankPagerAdapter(FragmentManager fm, String[] titles, String[] orders)
        {

            super(fm);
            this.titles = titles;
            this.orders = orders;
        }

        @Override
        public Fragment getItem(int position)
        {

            return OriginalRankFragment.newInstance(orders[position]);
        }

        @Override
        public CharSequence getPageTitle(int position)
        {

            return titles[position];
        }

        @Override
        public int getCount()
        {

            return titles.length;
        }
    }
}
