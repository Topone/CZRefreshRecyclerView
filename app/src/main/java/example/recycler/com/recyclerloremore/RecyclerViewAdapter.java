package example.recycler.com.recyclerloremore;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import java.util.List;

/**
 * Created by crazy_z on 2017/1/3.
 * recyclerView adapter
 */

public abstract class RecyclerViewAdapter<VH extends RecyclerView.ViewHolder, T>
        extends RecyclerView.Adapter<VH> implements RecyclerSet, AdapterSet {

    private static int TYPE_HEADER = -10001;
    private static int TYPE_FOOTER = -10002;

    private ViewGroup mHeaderContainer;
    private ViewGroup mFooterContainer;

    private List<T> mListData;
    private OnItemclickListener mListener;

    public interface OnItemclickListener {
        void onItemClick(int position);

        void onItemLongClick(int position);
    }

    protected RecyclerViewAdapter(List<T> list) {
        mListData = list;
    }

    @Override
    public final int getItemViewType(int position) {
        if (hasHeaderView() && position == 0) {
            return TYPE_HEADER;
        } else if (hasFooterView() && position == getItemCount() - 1) {
            return TYPE_FOOTER;
        }

        if (hasHeaderView()) {
            position--;
        }

        return getNewItemViewType(position);
    }

    @Override
    public final int getItemCount() {
        int itemCount = mListData != null ? mListData.size() : 0;

        if (hasHeaderView()) {
            itemCount++;
        }

        if (hasFooterView()) {
            itemCount++;
        }

        return itemCount;
    }

    @Override
    public final VH onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_HEADER) {
            if (mHeaderContainer == null) {
                mHeaderContainer = createContainer(parent.getContext());
            }

            return (VH) new RecyclerView.ViewHolder(mHeaderContainer) {
            };
        } else if (viewType == TYPE_FOOTER) {
            if (mFooterContainer == null) {
                mFooterContainer = createContainer(parent.getContext());
            }

            return (VH) new RecyclerView.ViewHolder(mFooterContainer) {
            };
        }

        return onNewCreateViewHolder(parent, viewType);
    }

    @Override
    public final void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if ((hasHeaderView() && position == 0) ||
                (hasFooterView() && position == getItemCount() - 1)) {
            return;
        }

        if (hasHeaderView()) {
            position--;
        }

        final int finalPosition = position;
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mListener != null) {
                    mListener.onItemClick(finalPosition);
                }
            }
        });

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if (mListener != null) {
                    mListener.onItemLongClick(finalPosition);
                }

                return false;
            }
        });

        onNewBindViewHolder((VH) holder, position);
    }

    @Override
    public void addHeaderView(View view) {
        if (mHeaderContainer == null) {
            mHeaderContainer = createContainer(view.getContext());
        }

        mHeaderContainer.addView(view);
    }

    @Override
    public void addFooterView(View view) {
        if (mFooterContainer == null) {
            mFooterContainer = createContainer(view.getContext());
        }

        mFooterContainer.addView(view);
    }

    @Override
    public void removeFooterView() {
        if (mFooterContainer != null) {
            mFooterContainer.removeAllViews();
        }
    }

    @Override
    public void removeHeaderView() {
        if (mHeaderContainer != null) {
            mHeaderContainer.removeAllViews();
        }
    }

    @Override
    public List getDataList() {
        return mListData;
    }

    @Override
    public void addList(List list) {
        if (mListData != null) {
            mListData.addAll(list);
            notifyDataSetChanged();
        }
    }

    @Override
    public void setList(List listData) {
        mListData = listData;
        notifyDataSetChanged();
    }

    @Override
    public void clearList() {
        if (mListData != null) {
            mListData.clear();
            notifyDataSetChanged();
        }
    }

    @Override
    public Object getItem(int position) {
        return mListData.get(position);
    }

    @Override
    public void addItem(Object item) {
        if (mListData != null) {
            mListData.add((T) item);
            notifyDataSetChanged();
        }
    }

    @Override
    public void deleteItem(Object item) {
        if (mListData != null) {
            mListData.remove(item);
            notifyDataSetChanged();
        }
    }

    @Override
    public void deleteItem(int position) {
        if (mListData != null) {
            mListData.remove(position);
            notifyDataSetChanged();
        }
    }

    public int getNewItemViewType(int position) {
        return 0;
    }

    public abstract VH onNewCreateViewHolder(ViewGroup parent, int viewType);

    public abstract void onNewBindViewHolder(VH holder, int position);

    private ViewGroup createContainer(Context context) {
        LinearLayout container = new LinearLayout(context);
        container.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
        container.setOrientation(LinearLayout.VERTICAL);
        return container;
    }

    private boolean hasHeaderView() {
        return mHeaderContainer != null && mHeaderContainer.getChildCount() > 0;
    }

    private boolean hasFooterView() {
        return mFooterContainer != null && mFooterContainer.getChildCount() > 0;
    }

    private void setOnItemClickListener(OnItemclickListener listener) {
        mListener = listener;
    }
}
