package com.hotbitmapgg.ohmybilibili.module.home.bangumi;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import com.hotbitmapgg.ohmybilibili.R;
import com.hotbitmapgg.ohmybilibili.adapter.BangumiIndexAdapter;
import com.hotbitmapgg.ohmybilibili.base.RxAppCompatBaseActivity;
import com.hotbitmapgg.ohmybilibili.entity.bangumi.BangumiIndex;
import com.hotbitmapgg.ohmybilibili.network.RetrofitHelper;
import com.hotbitmapgg.ohmybilibili.widget.CircleProgressView;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.Bind;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by hcc on 16/8/4 14:12
 * 100332338@qq.com
 * <p/>
 * 番剧索引界面
 */
public class BangumiIndexActivity extends RxAppCompatBaseActivity
{

    @Bind(R.id.recycle)
    RecyclerView mRecyclerView;

    @Bind(R.id.toolbar)
    Toolbar mToolbar;

    @Bind(R.id.circle_progress)
    CircleProgressView mCircleProgressView;

    private String month = "4";

    private String year = "2016";

    private List<BangumiIndex> bangumiIndexList = new ArrayList<>();


    @Override
    public int getLayoutId()
    {

        return R.layout.activity_bangumi_index;
    }

    @Override
    public void initViews(Bundle savedInstanceState)
    {

        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new GridLayoutManager(BangumiIndexActivity.this, 3));

        getBangumiIndex();
    }

    @Override
    public void initToolBar()
    {

        mToolbar.setTitle("番剧索引");
        setSupportActionBar(mToolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null)
            actionBar.setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {

        if (item.getItemId() == android.R.id.home)
            onBackPressed();

        return super.onOptionsItemSelected(item);
    }

    public void getBangumiIndex()
    {

        RetrofitHelper.getBangumiIndexApi()
                .getBangumiIndex(year, month)
                .compose(this.bindToLifecycle())
                .doOnSubscribe(this::showProgressBar)
                .subscribeOn(Schedulers.io())
                .delay(2000, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(bangumiIndices -> {

                    if (bangumiIndices != null)
                    {
                        bangumiIndexList.addAll(bangumiIndices);
                        finishTask();
                    }
                }, throwable -> {

                    hideProgressBar();
                });
    }


    private void showProgressBar()
    {

        mCircleProgressView.setVisibility(View.VISIBLE);
        mCircleProgressView.spin();
    }

    private void hideProgressBar()
    {

        mCircleProgressView.setVisibility(View.GONE);
        mCircleProgressView.stopSpinning();
    }

    private void finishTask()
    {

        BangumiIndexAdapter mAdapter = new BangumiIndexAdapter(mRecyclerView, bangumiIndexList);
        mRecyclerView.setAdapter(mAdapter);
        hideProgressBar();
    }
}
