package example.recycler.com.recyclerloremore;

import android.support.v7.util.DiffUtil;

import java.util.List;

/**
 * Created by crazy_z on 2017/1/3.
 */

public class CZDiffCallback<T> extends DiffUtil.Callback {

    private List<T> mNewList;
    private List<T> mOldList;

    public CZDiffCallback(List<T> oldList, List<T> newList) {
        this.mOldList = oldList;
        this.mNewList = newList;
    }

    @Override
    public int getOldListSize() {
        return mOldList.size();
    }

    @Override
    public int getNewListSize() {
        return mNewList.size();
    }

    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        return mOldList.get(oldItemPosition) == mNewList.get(newItemPosition);
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        return mOldList.get(oldItemPosition) == mNewList.get(newItemPosition);
    }
}
