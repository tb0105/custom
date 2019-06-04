package com.rzq.custom.base;

import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.rzq.custom.R;
import com.rzq.custom.base.utils.OnNextCall;

import java.util.List;

import io.reactivex.annotations.NonNull;


/**
 * Created by ${tb0105} on ${DATA}.
 */
public class RefreshView extends LinearLayout {
    private static final int START_STATE = 124;
    private final float LGNORE = 5;
    private final String TAG = this.getClass().getSimpleName();
    RelativeLayout frameview;
    AppCompatTextView compatTextView;
    RecyclerView recyclerView;
    private RelativeLayout relativeLayout_head;
    private RelativeLayout relativeLayout_loading;

    TextView tv_head;
    TextView tv_loading;
    ProgressBar pb_head;
    ProgressBar pb_loading;

    //刷新回调
    private LoadingListenner loadinglistenner;

    public RecyclerView.LayoutManager getLayoutManager() {
        return layoutManager;
    }

    private int mHeaderHeight;
    private int mBottomHeight;
    //底头部判断
    private int SCROLL_SDATE; // 状态
    private final static int SCROLL_BOTTOM = -10; // 到达底部
    private final static int SCROLL_TOP = -11; // 到达底部
    private final int SCROLL_ING = 88;//未到底部或头部

    //滑动状态
    private int REFRESH_STATE = 21;//状态
    private final int NOTFRESH = 22;//距离不足不进入刷新
    private final int START_DOWN_ON = -8;//开始下拉
    private final int START_TOP_ON = -9;//开始上拉
    private final int LOOSEN_REFRESH = -11;//进入松手刷新

    //信息显示
    private String top_on = "drop down";
    private String down_on = " pull";
    private String loosen = "loosen";
    private String refreshing = " refresh...";
    private String refreshed = " refresh Ok";

    public void setTop_on(String top_on) {
        this.top_on = top_on;
    }

    public void setDown_on(String down_on) {
        this.down_on = down_on;
    }

    //数据状态
    private int DATA_STATE = -111;//状态
    private final int DATA_DEFULT = -110;//初始
    private final int GET_IN = -112;//获取数据中
    private final int GET_OVER = -113;//完成初始状态

    //坐标
    private float mDownY;
    private float mMoveY;
    //
    private Handler handler = new Handler();

    //recyclerview配置
    BaseListAdapter adapter;
    RecyclerView.LayoutManager layoutManager;
    private boolean bottom;
    private boolean isshownot;
    private OnNextCall<Integer> scollListenner;

    public void setCompatTextView(String msg) {
        this.compatTextView.setText(msg);
    }

    public AppCompatTextView getCompatTextView() {
        return compatTextView;
    }

    private void init(Context context) {
        LayoutInflater.from(context).inflate(R.layout.refresh_listview, this, true);
        frameview = findViewById(R.id.fl_base_list);
        compatTextView = findViewById(R.id.list_top_msg);
        recyclerView = findViewById(R.id.rv_list);
        relativeLayout_head = findViewById(R.id.rl_head);
        relativeLayout_loading = findViewById(R.id.rl_loading);
        tv_head = findViewById(R.id.tv_head);
        tv_loading = findViewById(R.id.tv_loading);
        pb_loading = findViewById(R.id.pb_loading);
        pb_head = findViewById(R.id.pb_head);
        recyclerView.addOnScrollListener(onScrollListenner);
        mHeaderHeight = relativeLayout_head.getLayoutParams().height;
        mBottomHeight = relativeLayout_loading.getLayoutParams().height;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN://放下手指
                mDownY = ev.getY();
                Log.i("mDownY", "" + mDownY);
                break;
            case MotionEvent.ACTION_MOVE://屏幕上滑动中
                mMoveY = ev.getY();
                switch (SCROLL_SDATE) {
                    case SCROLL_TOP:
                        if (mMoveY - mDownY < LGNORE || DATA_STATE == GET_IN)
                            break;
                        refreshHead(ev);
                        break;
                    case SCROLL_BOTTOM:
                        if (-(mMoveY - mDownY) < LGNORE || DATA_STATE == GET_IN)
                            break;
                        refreshBottom(ev);
                        break;
                    case SCROLL_ING:
                        break;
                }
                break;
            case MotionEvent.ACTION_UP://抬起手指
                if (NOTFRESH == REFRESH_STATE) {
                    handler.postDelayed(headHideAnimation, 60);
                    handler.postDelayed(bottomHideAnimation, 60);
                    break;
                }
                switch (SCROLL_SDATE) {
                    case SCROLL_TOP:
                        if (mMoveY - mDownY < LGNORE)
                            break;
                        setTopUp();
                        break;
                    case SCROLL_BOTTOM:
                        if (mDownY - mMoveY < LGNORE)
                            break;
                        setBottomUp();
                        break;
                    case SCROLL_ING:
                        handler.removeCallbacks(headHideAnimation);
                        handler.removeCallbacks(bottomHideAnimation);
                        handler.postDelayed(headHideAnimation, 60);
                        handler.postDelayed(bottomHideAnimation, 60);
                        break;
                }

                break;
        }

        return super.dispatchTouchEvent(ev);
    }

    /**
     * 下拉效果
     *
     * @param event
     */
    private void refreshHead(MotionEvent event) {
        Log.i("mMoveY", "" + mMoveY);
        if (relativeLayout_head.getVisibility() != VISIBLE) {
            relativeLayout_head.setVisibility(VISIBLE);
        }
        viewSetMarginTop(recyclerView, -mHeaderHeight + (int) ((mMoveY - mDownY) / 3) + mHeaderHeight);
        viewSetMarginTop(relativeLayout_head, -mHeaderHeight + (int) ((mMoveY - mDownY) / 3));
        if (DATA_STATE == GET_IN) {
            if (SCROLL_SDATE == SCROLL_TOP) {
                handler.postDelayed(headBackAnimation, 60);
            } else if (SCROLL_SDATE == SCROLL_BOTTOM) {
                handler.postDelayed(bottomBackAnimation, 60);
            }
            return;
        }
        setScrollState(relativeLayout_head, (int) ((mMoveY - mDownY) / 3));

    }

    private void setScrollState(View view, int height) {
        int viewheight = 20;
        if (viewheight > height) {
            REFRESH_STATE = NOTFRESH;
        } else if (viewheight < height) {
            REFRESH_STATE = LOOSEN_REFRESH;
        }
        setTopText();
    }

    private void setScrollStateBottom(View view, int height) {
        int viewheight = 20;
        if (viewheight > -height) {
            REFRESH_STATE = NOTFRESH;
        } else if (viewheight < -height) {
            REFRESH_STATE = LOOSEN_REFRESH;
        }
        setDownText();

    }

    /**
     * 上拉效果
     *
     * @param event
     */
    private void refreshBottom(MotionEvent event) {
        Log.i("mMoveY", "" + mMoveY);
        if (relativeLayout_loading.getVisibility() != VISIBLE) {
            relativeLayout_loading.setVisibility(VISIBLE);
        }
        viewSetMarginBottom(recyclerView, -mHeaderHeight + (int) ((mDownY - mMoveY) / 3) + mBottomHeight);
        viewSetMarginBottom(relativeLayout_loading, -mHeaderHeight + (int) ((mDownY - mMoveY) / 3));
        if (DATA_STATE == GET_IN) {
            if (SCROLL_SDATE == SCROLL_TOP) {
                handler.postDelayed(headBackAnimation, 60);
            } else if (SCROLL_SDATE == SCROLL_BOTTOM) {
                handler.postDelayed(bottomBackAnimation, 60);
            }
            return;
        }
        setScrollStateBottom(relativeLayout_loading, (int) ((mMoveY - mDownY) / 3));

    }

    /**
     * top抬起状态提示
     */
    private void setTopUp() {
        if (DATA_STATE == GET_IN)
            return;
        switch (REFRESH_STATE) {
            case START_TOP_ON:
                handler.postDelayed(headHideAnimation, 60);
                break;
            case LOOSEN_REFRESH:
                DATA_STATE = GET_IN;
                setTopText();
                if (loadinglistenner != null) {
                    loadinglistenner.downRefresh();
                }
                handler.postDelayed(headBackAnimation, 60);
                break;
        }

    }

    /**
     * down抬起状态提示
     */
    private void setBottomUp() {
        if (DATA_STATE == GET_IN)
            return;
        switch (REFRESH_STATE) {
            case START_DOWN_ON:
                handler.postDelayed(bottomHideAnimation, 60);
                break;
            case LOOSEN_REFRESH:
                DATA_STATE = GET_IN;
                setDownText();
                if (loadinglistenner != null) {
                    loadinglistenner.upLoad();
                }
                handler.postDelayed(bottomBackAnimation, 60);
                break;
        }
    }

    private UpResultCall upResultCall = new UpResultCall() {
        @Override
        public void overCall() {
            DATA_STATE = GET_OVER;
            setTopText();
            handler.postDelayed(headHideAnimation, 60);
            REFRESH_STATE = START_STATE;
            DATA_STATE = DATA_DEFULT;
        }

        @Override
        public void upCall() {
            DATA_STATE = GET_OVER;
            setDownText();
            handler.postDelayed(bottomHideAnimation, 60);
            REFRESH_STATE = START_STATE;
            DATA_STATE = DATA_DEFULT;
        }
    };

    public UpResultCall getUpResultCall() {
        return upResultCall;
    }

    /**
     * 设置上部panding
     *
     * @param view
     * @param top
     */
    private void viewSetMarginTop(View view, int top) {
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) view.getLayoutParams();
        params.setMargins(0, top,
                0,
                0);
        view.setLayoutParams(params);
    }

    /**
     * 设置底部panding
     *
     * @param view
     * @param bottom
     */
    private void viewSetMarginBottom(View view, int bottom) {
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) view.getLayoutParams();
        params.setMargins(0, 0,
                0,
                bottom);
        view.setLayoutParams(params);
    }

    /**
     * top提示信息
     */
    private void setTopText() {
        switch (REFRESH_STATE) {
            case NOTFRESH:
                pb_head.setVisibility(INVISIBLE);
                tv_head.setText(top_on);
                break;
            case START_DOWN_ON:
                pb_head.setVisibility(INVISIBLE);
                tv_head.setText(top_on);
                break;
            case LOOSEN_REFRESH:
                pb_head.setVisibility(INVISIBLE);
                tv_head.setText(loosen);
                switch (DATA_STATE) {
                    case GET_IN:
                        pb_head.setVisibility(VISIBLE);
                        tv_head.setText(refreshing);
                        break;
                    case GET_OVER:
                        pb_head.setVisibility(INVISIBLE);
                        tv_head.setText(refreshed);
                        break;
                }
                break;
        }
    }

    /**
     * down提示信息
     */
    private void setDownText() {
        switch (REFRESH_STATE) {
            case NOTFRESH:
                pb_loading.setVisibility(INVISIBLE);
                tv_loading.setText(top_on);
                break;
            case START_TOP_ON:
                pb_loading.setVisibility(INVISIBLE);
                tv_loading.setText(down_on);
                break;
            case LOOSEN_REFRESH:
                pb_loading.setVisibility(INVISIBLE);
                tv_loading.setText(loosen);
                switch (DATA_STATE) {
                    case GET_IN:
                        pb_loading.setVisibility(VISIBLE);
                        tv_loading.setText(refreshing);
                        break;
                    case GET_OVER:
                        pb_loading.setVisibility(INVISIBLE);
                        tv_loading.setText(refreshed);
                        break;
                }
                break;
        }
    }

    /**
     *
     */
    Runnable headHideAnimation = new Runnable() {
        public void run() {
            int bottom = relativeLayout_head.getBottom();
            if (bottom > 0) {
                int paddingTop = (int) (-mHeaderHeight * 0.25f + getMarginTop(relativeLayout_head) * 0.75f) - 1;
                if (paddingTop < -mHeaderHeight) {
                    paddingTop = -mHeaderHeight;
                }
                viewSetMarginTop(relativeLayout_head, paddingTop);
                viewSetMarginTop(recyclerView, paddingTop + mHeaderHeight);
                handler.postDelayed(headHideAnimation, 5);
            } else {
                handler.removeCallbacks(headHideAnimation);
                viewSetMarginTop(relativeLayout_head, -mHeaderHeight);
                viewSetMarginTop(recyclerView, 0);
            }
        }
    };


    /**
     *
     */
    Runnable headBackAnimation = new Runnable() {
        public void run() {
            if (getMarginTop(relativeLayout_head) > 1) {
                viewSetMarginTop(relativeLayout_head, 0);
                viewSetMarginTop(recyclerView, (int) mHeaderHeight);
                handler.postDelayed(headBackAnimation, 5);
            } else {
                handler.removeCallbacks(headBackAnimation);
            }
        }
    };
    /**
     *
     */
    Runnable bottomHideAnimation = new Runnable() {
        public void run() {
            int top = getMarginBottom(relativeLayout_loading);
            if (top > -mBottomHeight) {
                int paddingbottom = (int) (-mBottomHeight * 0.25f + getMarginBottom(relativeLayout_loading) * 0.75f) - 1;
                if (paddingbottom < -mBottomHeight) {
                    paddingbottom = -mBottomHeight;
                }
                viewSetMarginBottom(relativeLayout_loading, paddingbottom);
                viewSetMarginBottom(recyclerView, paddingbottom + mBottomHeight);
                handler.postDelayed(bottomHideAnimation, 5);
            } else {
                handler.removeCallbacks(bottomHideAnimation);
                viewSetMarginBottom(relativeLayout_loading, -mBottomHeight);
                viewSetMarginBottom(recyclerView, 0);
            }
        }
    };


    /**
     *
     */
    Runnable bottomBackAnimation = new Runnable() {
        public void run() {
            if (getMarginBottom(relativeLayout_loading) >= 0) {
                viewSetMarginBottom(relativeLayout_loading, 0);
                viewSetMarginBottom(recyclerView, mBottomHeight);
                handler.postDelayed(bottomBackAnimation, 5);
            } else {
                handler.removeCallbacks(bottomBackAnimation);
            }
        }
    };


    /**
     * 滑动监听
     */
    private RecyclerView.OnScrollListener onScrollListenner = new RecyclerView.OnScrollListener() {
        @Override
        public synchronized void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
            isScrollState(recyclerView);
            if (scollListenner != null) {
                scollListenner.onNext(newState);
            }

        }

        @Override
        public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);

        }
    };

    /**
     * 必须的
     *
     * @param layoutManager
     */
    public void setLayoutManager(RecyclerView.LayoutManager layoutManager) {
        recyclerView.setLayoutManager(layoutManager);
        this.layoutManager = recyclerView.getLayoutManager();
    }

    public RecyclerView getRecyclerView() {
        return recyclerView;
    }

    /**
     * 必须的
     *
     * @param baseListAdapter
     */
    public void setAdapter(BaseListAdapter baseListAdapter) {
        recyclerView.setAdapter(baseListAdapter);
        this.adapter = (BaseListAdapter) recyclerView.getAdapter();

    }

    /**
     * 下拉，底部加载必须的
     *
     * @param loadinglistenner
     */
    public void setLoadinglistenner(LoadingListenner loadinglistenner) {
        this.loadinglistenner = loadinglistenner;
    }

    public void notifyDataSetChanged() {
        if (adapter != null) {
            adapter.notifyDataSetChanged();
            showList();
            refreshOver();
            if (getItemList().size() > 0)
                upOver();
        }
    }

    private void showList() {
        if (adapter != null) {
            if (adapter.getItemCount() > 0) {
                compatTextView.setVisibility(GONE);
                recyclerView.setVisibility(VISIBLE);
            } else {
                if (isshownot)
                    compatTextView.setVisibility(VISIBLE);
                else compatTextView.setVisibility(GONE);
                recyclerView.setVisibility(GONE);
                SCROLL_SDATE = SCROLL_TOP;
            }
        }
    }

    public void setItemList(List list) {
        adapter.setItemList(list);
    }

    public List getItemList() {
        return adapter.getItemList();
    }

    public void refreshOver() {
        upResultCall.overCall();
    }

    public void upOver() {
        upResultCall.upCall();
    }


    public void isbottom(boolean bottom) {
        this.bottom = bottom;
    }

    public void isshowNotData(boolean b) {
        this.isshownot = b;
        if (!b)
            getCompatTextView().setVisibility(View.GONE);


    }

    public void setScollListenner(OnNextCall<Integer> scollListenner) {
        this.scollListenner = scollListenner;
    }

    public interface LoadingListenner {

        /**
         * 上拉加载更多
         */
        void upLoad();

        /**
         * 下拉刷新
         */
        void downRefresh();

    }

    public interface UpResultCall {
        void overCall();

        void upCall();
    }

    /**
     * 判断滑到哪儿
     *
     * @param recyclerView
     * @return
     */
    public synchronized boolean isScrollState(RecyclerView recyclerView) {
        if (recyclerView == null) return false;
        if ((recyclerView.computeVerticalScrollExtent() + recyclerView.computeVerticalScrollOffset()) == recyclerView.computeVerticalScrollExtent()) {
            Log.i(TAG + SCROLL_TOP, recyclerView.computeVerticalScrollExtent() + "+" + recyclerView.computeVerticalScrollOffset() + ">=" + recyclerView.computeVerticalScrollRange());
            SCROLL_SDATE = SCROLL_TOP;
            return true;
        }else if (recyclerView.computeVerticalScrollExtent() + recyclerView.computeVerticalScrollOffset()
                >= recyclerView.computeVerticalScrollRange()) {
            if (!bottom)
                return true;
            Log.i(TAG + SCROLL_BOTTOM, recyclerView.computeVerticalScrollExtent() + "+" + recyclerView.computeVerticalScrollOffset() + ">=" + recyclerView.computeVerticalScrollRange());
            SCROLL_SDATE = SCROLL_BOTTOM;
            return true;
        }   else {
            Log.i(TAG + SCROLL_ING, recyclerView.computeVerticalScrollExtent() + "+" + recyclerView.computeVerticalScrollOffset() + ">=" + recyclerView.computeVerticalScrollRange());
            SCROLL_SDATE = SCROLL_ING;

        }
        return false;
    }

    private int getMarginBottom(View view) {
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) view.getLayoutParams();
        return params.bottomMargin;
    }

    private int getMarginTop(View view) {
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) view.getLayoutParams();
        return params.topMargin;

    }

    public RefreshView(Context context) {
        super(context);
        init(context);
    }

    public RefreshView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public RefreshView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public RefreshView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }
}
