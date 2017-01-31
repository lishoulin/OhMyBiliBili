package com.hotbitmapgg.ohmybilibili.module.search;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextPaint;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.flyco.tablayout.SlidingTabLayout;
import com.hotbitmapgg.ohmybilibili.R;
import com.hotbitmapgg.ohmybilibili.base.RxAppCompatBaseActivity;
import com.hotbitmapgg.ohmybilibili.entity.search.SearchResult;
import com.hotbitmapgg.ohmybilibili.network.RetrofitHelper;
import com.hotbitmapgg.ohmybilibili.utils.KeyBoardUtil;
import com.hotbitmapgg.ohmybilibili.utils.StatusBarUtils;
import com.jakewharton.rxbinding.view.RxView;
import com.jakewharton.rxbinding.widget.RxTextView;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.Bind;
import butterknife.OnClick;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by hcc on 16/8/29 19:58
 * 100332338@qq.com
 * <p/>
 * 全站搜索界面
 */
public class TotalStationSearchActivity extends RxAppCompatBaseActivity
{

    @Bind(R.id.sliding_tabs)
    SlidingTabLayout mSlidingTabLayout;

    @Bind(R.id.view_pager)
    ViewPager mViewPager;

    @Bind(R.id.iv_search_loading)
    ImageView mLoadingView;

    @Bind(R.id.search_img)
    ImageView mSearchBtn;

    @Bind(R.id.search_edit)
    EditText mSearchEdit;

    @Bind(R.id.search_text_clear)
    ImageView mSearchTextClear;

    @Bind(R.id.search_layout)
    LinearLayout mSearchLayout;


    private static final String EXTRA_CONTENT = "extra_content";

    private String content;

    private List<String> titles = new ArrayList<>();

    private List<Fragment> fragments = new ArrayList<>();

    private AnimationDrawable mAnimationDrawable;

    private SearchResult.PageinfoBean pageinfo;

    private SearchResult.ResultBean result;


    @Override
    public int getLayoutId()
    {

        return R.layout.activity_search;
    }

    @Override
    public void initToolBar()
    {
        //设置6.0以上StatusBar字体颜色
        StatusBarUtils.from(this)
                .setLightStatusBar(true)
                .process();
    }

    @Override
    public void initViews(Bundle savedInstanceState)
    {

        Intent intent = getIntent();
        if (intent != null)
            content = intent.getStringExtra(EXTRA_CONTENT);

        mLoadingView.setImageResource(R.drawable.anim_search_loading);
        mAnimationDrawable = (AnimationDrawable) mLoadingView.getDrawable();
        showSearchAnim();
        mSearchEdit.setText(content);
        getSearchData(content);
        search();
        setUpEditText();
    }

    private void setUpEditText()
    {

        RxTextView.textChanges(mSearchEdit)
                .compose(this.bindToLifecycle())
                .map(CharSequence::toString)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(s -> {

                    if (!TextUtils.isEmpty(s))
                        mSearchTextClear.setVisibility(View.VISIBLE);
                    else
                        mSearchTextClear.setVisibility(View.GONE);
                });


        RxView.clicks(mSearchTextClear)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(aVoid -> {

                    mSearchEdit.setText("");
                });


        RxTextView.editorActions(mSearchEdit)
                .filter(integer -> !TextUtils.isEmpty(mSearchEdit.getText().toString().trim()))
                .filter(integer -> integer == EditorInfo.IME_ACTION_SEARCH)
                .flatMap(new Func1<Integer,Observable<String>>()
                {

                    @Override
                    public Observable<String> call(Integer integer)
                    {

                        return Observable.just(mSearchEdit.getText().toString().trim());
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(s -> {

                    KeyBoardUtil.closeKeybord(mSearchEdit,
                            TotalStationSearchActivity.this);
                    showSearchAnim();
                    clearData();
                    getSearchData(s);
                });
    }

    private void search()
    {

        RxView.clicks(mSearchBtn)
                .throttleFirst(2, TimeUnit.SECONDS)
                .map(aVoid -> mSearchEdit.getText().toString().trim())
                .filter(s -> !TextUtils.isEmpty(s))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(s -> {

                    KeyBoardUtil.closeKeybord(mSearchEdit,
                            TotalStationSearchActivity.this);
                    showSearchAnim();
                    clearData();
                    getSearchData(s);
                });
    }

    private void clearData()
    {

        pageinfo = null;
        titles.clear();
        fragments.clear();
        if (result != null)
        {
            if (result.getVideo() != null)
                result.getVideo().clear();
            if (result.getBangumi() != null)
                result.getBangumi().clear();
            if (result.getTopic() != null)
                result.getTopic().clear();
        }
    }

    private void getSearchData(String text)
    {

        int page = 1;
        int count = 10;
        RetrofitHelper.getSearchApi()
                .search(text, page, count)
                .compose(this.bindToLifecycle())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(searchResult -> {

                    pageinfo = searchResult.getPageinfo();
                    result = searchResult.getResult();
                    finishTask();
                }, throwable -> {

                    setEmptyLayout();
                });
    }

    private void finishTask()
    {

        hideSearchAnim();
        titles.add("综合");
        titles.add("番剧" + "(" + checkNumResults(pageinfo.getBangumi().getNumResults()) + ")");
        titles.add("话题" + "(" + checkNumResults(pageinfo.getTopic().getNumResults()) + ")");


        ComprehensiveResultsFragment comprehensiveResultsFragment = ComprehensiveResultsFragment.newInstance(result);
        BangumiResultsFragment bangumiResultsFragment = BangumiResultsFragment.newInstance(result);
        TopicResultsFragment topicResultsFragment = TopicResultsFragment.newInstance(result);

        fragments.add(comprehensiveResultsFragment);
        fragments.add(bangumiResultsFragment);
        fragments.add(topicResultsFragment);

        SearchTabAdapter mAdapter = new SearchTabAdapter(getSupportFragmentManager(), titles, fragments);
        mViewPager.setAdapter(mAdapter);
        mSlidingTabLayout.setViewPager(mViewPager);
        measureTabLayoutTextWidth(0);
        mSlidingTabLayout.setCurrentTab(0);
        mAdapter.notifyDataSetChanged();
        mSlidingTabLayout.notifyDataSetChanged();
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener()
        {

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels)
            {

            }

            @Override
            public void onPageSelected(int position)
            {

                measureTabLayoutTextWidth(position);
            }

            @Override
            public void onPageScrollStateChanged(int state)
            {

            }
        });
    }

    public String checkNumResults(int numResult)
    {

        return numResult > 100 ? "99+" : String.valueOf(numResult);
    }

    private void measureTabLayoutTextWidth(int position)
    {

        String title = titles.get(position);
        TextView titleView = mSlidingTabLayout.getTitleView(position);
        TextPaint paint = titleView.getPaint();
        float textWidth = paint.measureText(title);
        mSlidingTabLayout.setIndicatorWidth(textWidth / 3);
    }


    private void showSearchAnim()
    {

        mLoadingView.setVisibility(View.VISIBLE);
        mSearchLayout.setVisibility(View.GONE);
        mAnimationDrawable.start();
    }

    private void hideSearchAnim()
    {

        mLoadingView.setVisibility(View.GONE);
        mSearchLayout.setVisibility(View.VISIBLE);
        mAnimationDrawable.stop();
    }


    public void setEmptyLayout()
    {

        mLoadingView.setVisibility(View.VISIBLE);
        mSearchLayout.setVisibility(View.GONE);
        mLoadingView.setImageResource(R.drawable.search_failed);
    }

    @OnClick(R.id.search_back)
    void OnBack()
    {

        onBackPressed();
    }


    @Override
    public void onBackPressed()
    {

        if (mAnimationDrawable != null && mAnimationDrawable.isRunning())
        {
            mAnimationDrawable.stop();
            mAnimationDrawable = null;
        }

        super.onBackPressed();
    }

    public static void launch(Activity activity, String str)
    {

        Intent mIntent = new Intent(activity, TotalStationSearchActivity.class);
        mIntent.putExtra(EXTRA_CONTENT, str);
        activity.startActivity(mIntent);
    }

    @Override
    protected void onDestroy()
    {

        super.onDestroy();
        if (mAnimationDrawable != null)
        {
            mAnimationDrawable.stop();
            mAnimationDrawable = null;
        }
    }

    private static class SearchTabAdapter extends FragmentStatePagerAdapter
    {

        private List<String> titles;

        private List<Fragment> fragments;

        SearchTabAdapter(FragmentManager fm,
                         List<String> titles,
                         List<Fragment> fragments)
        {

            super(fm);
            this.titles = titles;
            this.fragments = fragments;
        }

        @Override
        public Fragment getItem(int position)
        {

            return fragments.get(position);
        }

        @Override
        public CharSequence getPageTitle(int position)
        {

            return titles.get(position);
        }

        @Override
        public int getCount()
        {

            return fragments.size();
        }
    }
}
