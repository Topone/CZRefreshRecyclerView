package example.recycler.com.recyclerloremore;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by crazy_z on 2017/1/3.
 * 加载更多 RecyclerView
 */

public class CZRecyclerView extends RecyclerView implements RecyclerSet {

    public enum LAYOUT_MANAGER_TYPE {
        LINEAR,
        GRID,
        STAGGERED_GRID
    }

    public interface OnLoadMoreListener {
        void onLoadMore();
    }

    private boolean mCanLoadMore = true;
    private boolean mIsLoading = false;
    private LAYOUT_MANAGER_TYPE layoutManagerType;

    private OnLoadMoreListener mLoadMoreListener;
    private View mLoadMoreView;

    public CZRecyclerView(Context context) {
        super(context);
    }

    public CZRecyclerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public CZRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void setLayoutManager(LayoutManager layout) {
        super.setLayoutManager(layout);
        if (layout != null && layoutManagerType == null) {
            if (layout instanceof GridLayoutManager) {
                layoutManagerType = LAYOUT_MANAGER_TYPE.GRID;
            } else if (layout instanceof LinearLayoutManager) {
                layoutManagerType = LAYOUT_MANAGER_TYPE.LINEAR;
            } else if (layout instanceof StaggeredGridLayoutManager) {
                layoutManagerType = LAYOUT_MANAGER_TYPE.STAGGERED_GRID;
            } else {
                throw new RuntimeException("Unsupported LayoutManager used." +
                        " Valid ones are LinearLayoutManager, " +
                        "GridLayoutManager and StaggeredGridLayoutManager");
            }
        }
    }

    @Override
    public void setAdapter(@NonNull Adapter adapter) {
        super.setAdapter(adapter);
        if (!mCanLoadMore) {
            return;
        }

        if (mLoadMoreView == null) {
            mLoadMoreView = getDefaultLoadMoreView();
        }

        if (mLoadMoreView.getParent() != null) {
            ((ViewGroup) mLoadMoreView.getParent()).removeView(mLoadMoreView);
        }

        setLoadMoreView((RecyclerViewAdapter) adapter);
        setFooterSpanSizeLookup(getLayoutManager(), adapter);
        addOnScrollListener(new OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                Log.e("====", "===scrolled");
            }
        });
    }

    private void setLoadMoreView(RecyclerViewAdapter adapter) {
        adapter.removeFooterView();
        adapter.addFooterView(mLoadMoreView);
        showLoadMore(false);
    }

    private void setFooterSpanSizeLookup(LayoutManager layout, final Adapter adapter) {
        if (layout == null || !(layout instanceof GridLayoutManager)) {
            return;
        }

        final GridLayoutManager gridLayoutManager = (GridLayoutManager) layout;
        final GridLayoutManager.SpanSizeLookup spanSizeLookup = gridLayoutManager.getSpanSizeLookup();

        //跨度
        gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                if (position == adapter.getItemCount() - 1) {
                    return gridLayoutManager.getSpanCount();
                }

                return spanSizeLookup != null ? spanSizeLookup.getSpanSize(position) : 1;
            }
        });
    }

    @Override
    public void addHeaderView(View view) {
        if (getAdapter() != null && getAdapter() instanceof RecyclerSet) {
            ((RecyclerSet) getAdapter()).addHeaderView(view);
        }
    }

    @Override
    public void addFooterView(View view) {
        if (getAdapter() != null && getAdapter() instanceof RecyclerSet) {
            ((RecyclerSet) getAdapter()).addFooterView(view);
        }
    }

    @Override
    public void removeFooterView() {
        if (getAdapter() != null && getAdapter() instanceof RecyclerSet) {
            ((RecyclerSet) getAdapter()).removeFooterView();
        }
    }

    @Override
    public void removeHeaderView() {
        if (getAdapter() != null && getAdapter() instanceof RecyclerSet) {
            ((RecyclerSet) getAdapter()).removeHeaderView();
        }
    }

    private View getDefaultLoadMoreView() {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.loadmore, null);
        return view;
    }


    private int getFirstPosition() {
        LayoutManager layoutManager = getLayoutManager();
        int firstPosition = 0;

        switch (layoutManagerType) {
            case LINEAR:
                firstPosition = ((LinearLayoutManager) layoutManager).findFirstVisibleItemPosition();
                break;
            case GRID:
                firstPosition = ((GridLayoutManager) layoutManager).findFirstVisibleItemPosition();
                break;
            case STAGGERED_GRID:
                StaggeredGridLayoutManager staggeredGridLayoutManager =
                        (StaggeredGridLayoutManager) layoutManager;
                int[] firstPositions = new int[staggeredGridLayoutManager.getSpanCount()];
                staggeredGridLayoutManager.findFirstVisibleItemPositions(firstPositions);
                firstPosition = findMin(firstPositions);
                break;
        }

        return firstPosition;
    }

    private int findMin(int[] lastPositions) {
        int min = Integer.MAX_VALUE;
        for (int value : lastPositions) {
            if (value != RecyclerView.NO_POSITION && value < min)
                min = value;
        }

        return min;
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        if (getLayoutManager() == null || !mCanLoadMore) {
            return onTouchEvent(e);
        }

        switch (e.getAction()) {
            case MotionEvent.ACTION_DOWN:
                break;
            case MotionEvent.ACTION_MOVE:

                LayoutManager layoutManager = getLayoutManager();
                int visibleItemCount = layoutManager.getChildCount();
                int totalItemCount = layoutManager.getItemCount();
                int firstPosition = getFirstPosition();
                if (!mIsLoading) {
                    if ((totalItemCount - visibleItemCount) <= firstPosition) {
                        startLoadmore();
                        mIsLoading = true;
                    } else if (!isLoadmoreShow() &&
                            (totalItemCount - visibleItemCount) <= firstPosition + 1) {
                        startLoadmore();
                        mIsLoading = true;
                    }
                }

                break;
        }

        return super.onTouchEvent(e);
    }

    private void showLoadMore(boolean show) {
        if (mLoadMoreView != null) {
            mLoadMoreView.setVisibility(show ? VISIBLE : GONE);
        }
    }

    private boolean isLoadmoreShow() {
        return mLoadMoreView.getVisibility() != GONE;
    }


    private void startLoadmore() {
        if (mIsLoading) {
            return;
        }

        Log.e("====", "====loadMore");
        mIsLoading = true;
        showLoadMore(true);
        postDelayed(new Runnable() {
            @Override
            public void run() {
                if (mLoadMoreListener != null) {
                    mLoadMoreListener.onLoadMore();
                }
            }
        }, 800);
    }

    public void stopLoadMore() {
        mIsLoading = false;
        showLoadMore(false);
    }

    public void setLoadMoreView(View loadMoreView) {
        mLoadMoreView = loadMoreView;
    }

    public void setCanLoadMore(boolean canLoadMore) {
        mCanLoadMore = canLoadMore;
    }

    public void setOnLoadMoreListener(OnLoadMoreListener listener) {
        mLoadMoreListener = listener;
    }
}
