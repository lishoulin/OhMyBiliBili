package com.hotbitmapgg.ohmybilibili.module.home.discover;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.hotbitmapgg.ohmybilibili.R;
import com.hotbitmapgg.ohmybilibili.adapter.OriginalRankAdapter;
import com.hotbitmapgg.ohmybilibili.base.RxLazyFragment;
import com.hotbitmapgg.ohmybilibili.entity.video.VideoItemInfo;
import com.hotbitmapgg.ohmybilibili.module.video.VideoDetailsActivity;
import com.hotbitmapgg.ohmybilibili.network.RetrofitHelper;
import com.hotbitmapgg.ohmybilibili.utils.LogUtil;
import com.hotbitmapgg.ohmybilibili.utils.ToastUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by hcc on 2016/9/22 18:49
 * 100332338@qq.com
 * <p>
 * 原创排行Fragment详情界面
 */

public class OriginalRankFragment extends RxLazyFragment
{

    @Bind(R.id.swipe_refresh_layout)
    SwipeRefreshLayout mSwipeRefreshLayout;

    @Bind(R.id.recycle)
    RecyclerView mRecyclerView;

    private static final String EXTRA_KEY = "extra_order";

    private String order;

    private boolean mIsRefreshing = false;

    private List<VideoItemInfo> videos = new ArrayList<>();

    private OriginalRankAdapter mAdapter;

    public static OriginalRankFragment newInstance(String order)
    {

        OriginalRankFragment mFragment = new OriginalRankFragment();
        Bundle bundle = new Bundle();
        bundle.putString(EXTRA_KEY, order);
        mFragment.setArguments(bundle);
        return mFragment;
    }

    @Override
    public int getLayoutResId()
    {

        return R.layout.fragment_original_rank;
    }

    @Override
    public void finishCreateView(Bundle state)
    {

        order = getArguments().getString(EXTRA_KEY);
        initRefreshLayout();
        initRecyclerView();
    }

    private void initRefreshLayout()
    {

        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);
        mSwipeRefreshLayout.post(() -> {

            mSwipeRefreshLayout.setRefreshing(true);
            mIsRefreshing = true;
            getOriginalRank();
        });
        mSwipeRefreshLayout.setOnRefreshListener(() -> {

            mIsRefreshing = true;
            videos.clear();
            getOriginalRank();
        });
    }

    private void getOriginalRank()
    {

        RetrofitHelper.getOriginalRankApi()
                .getOriginalRank(1, 20, order)
                .compose(this.bindToLifecycle())
                .map(originalRankInfo -> originalRankInfo.videos)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(videoItemInfos -> {

                    LogUtil.all(videoItemInfos.get(0).author);
                    videos.addAll(videoItemInfos);
                    finishTask();
                }, throwable -> {

                    mSwipeRefreshLayout.setRefreshing(false);
                    ToastUtil.ShortToast("加载失败啦,请重新加载~");
                });
    }

    private void finishTask()
    {

        mIsRefreshing = false;
        mSwipeRefreshLayout.setRefreshing(false);
        mAdapter.notifyDataSetChanged();
    }

    private void initRecyclerView()
    {

        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mAdapter = new OriginalRankAdapter(mRecyclerView, videos);
        mRecyclerView.setAdapter(mAdapter);
        setRecycleNoScroll();
        mAdapter.setOnItemClickListener((position, holder) -> VideoDetailsActivity.
                launch(getActivity(), videos.get(position).aid, videos.get(position).pic));
    }

    private void setRecycleNoScroll()
    {

        mRecyclerView.setOnTouchListener((v, event) -> mIsRefreshing);
    }

    @Override
    protected void lazyLoad()
    {

    }
}
