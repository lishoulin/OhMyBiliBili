package com.hotbitmapgg.ohmybilibili.module.user;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;

import com.hotbitmapgg.ohmybilibili.R;
import com.hotbitmapgg.ohmybilibili.adapter.UpMoreCoverAdapter;
import com.hotbitmapgg.ohmybilibili.adapter.helper.EndlessRecyclerOnScrollListener;
import com.hotbitmapgg.ohmybilibili.adapter.helper.HeaderViewRecyclerAdapter;
import com.hotbitmapgg.ohmybilibili.base.RxAppCompatBaseActivity;
import com.hotbitmapgg.ohmybilibili.entity.user.UserUpVideoInfo;
import com.hotbitmapgg.ohmybilibili.network.RetrofitHelper;
import com.hotbitmapgg.ohmybilibili.widget.CircleProgressView;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by hcc on 16/8/4 21:18
 * 100332338@qq.com
 * <p/>
 * UP主上传视频更多界面
 */
public class UserUpMoreCoverActivity extends RxAppCompatBaseActivity
{

    @Bind(R.id.recycle)
    RecyclerView mRecycleView;

    @Bind(R.id.circle_progress)
    CircleProgressView mCircleProgressView;

    @Bind(R.id.toolbar)
    Toolbar mToolbar;

    private String uName;

    private static final String EXTRA_USER_NAME = "extra_user_name", EXTRA_MID = "extra_mid";

    private int mid;

    private List<UserUpVideoInfo.VlistBean> userVideoList = new ArrayList<>();

    private int pageNum = 1;

    private int pageSize = 10;

    private HeaderViewRecyclerAdapter mHeaderViewRecyclerAdapter;

    private UpMoreCoverAdapter mAdapter;

    private View loadMoreView;


    @Override
    public int getLayoutId()
    {

        return R.layout.activity_up_more_cover;
    }

    @Override
    public void initViews(Bundle savedInstanceState)
    {

        Intent intent = getIntent();
        if (intent != null)
        {
            uName = intent.getStringExtra(EXTRA_USER_NAME);
            mid = intent.getIntExtra(EXTRA_MID, -1);
        }

        showProgressBar();
        getUserVideoList();
        initRecyclerView();
    }

    private void initRecyclerView()
    {

        mRecycleView.setHasFixedSize(true);
        mAdapter = new UpMoreCoverAdapter(mRecycleView, userVideoList);
        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(this);
        mRecycleView.setLayoutManager(mLinearLayoutManager);
        mHeaderViewRecyclerAdapter = new HeaderViewRecyclerAdapter(mAdapter);
        mRecycleView.setAdapter(mHeaderViewRecyclerAdapter);
        createLoadMoreView();
        mRecycleView.addOnScrollListener(new EndlessRecyclerOnScrollListener(mLinearLayoutManager)
        {

            @Override
            public void onLoadMore(int i)
            {

                pageNum++;
                getUserVideoList();
                loadMoreView.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    public void initToolBar()
    {

        mToolbar.setTitle(uName + "的投稿");
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


    private void getUserVideoList()
    {

        RetrofitHelper.getUserUpVideoListApi()
                .getUserUpVideos(mid, pageNum, pageSize)
                .compose(this.bindToLifecycle())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(userUpVideoInfo -> {

                    List<UserUpVideoInfo.VlistBean> vlist =
                            userUpVideoInfo.getVlist();
                    if (vlist.size() < pageSize)
                        loadMoreView.setVisibility(View.GONE);

                    userVideoList.addAll(vlist);
                    finishTask();
                }, throwable -> {

                    loadMoreView.setVisibility(View.GONE);
                    hideProgressBar();
                });
    }

    private void finishTask()
    {

        hideProgressBar();
        loadMoreView.setVisibility(View.GONE);

        if (pageNum * pageSize - pageSize - 1 > 0)
            mAdapter.notifyItemRangeChanged(pageNum * pageSize - pageSize - 1, pageSize);
        else
            mAdapter.notifyDataSetChanged();
    }

    private void createLoadMoreView()
    {

        loadMoreView = LayoutInflater.from(UserUpMoreCoverActivity.this)
                .inflate(R.layout.layout_load_more, mRecycleView, false);
        mHeaderViewRecyclerAdapter.addFooterView(loadMoreView);
        loadMoreView.setVisibility(View.GONE);
    }

    public void showProgressBar()
    {

        mCircleProgressView.setVisibility(View.VISIBLE);
        mCircleProgressView.spin();
    }

    public void hideProgressBar()
    {

        mCircleProgressView.setVisibility(View.GONE);
        mCircleProgressView.stopSpinning();
    }

    public static void launch(Activity activity, String name, int mid)
    {

        Intent intent = new Intent(activity, UserUpMoreCoverActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(EXTRA_USER_NAME, name);
        intent.putExtra(EXTRA_MID, mid);
        activity.startActivity(intent);
    }
}
