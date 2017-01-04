package example.recycler.com.recyclerloremore;

import android.view.View;

/**
 * Created by crazy_z on 2017/1/3.
 */

public interface RecyclerSet {
    void addHeaderView(View view);

    void addFooterView(View view);

    void removeFooterView();

    void removeHeaderView();
}
