package example.recycler.com.recyclerloremore;

import java.util.List;

/**
 * Created by crazy_z on 2017/1/3.
 */

public interface AdapterSet<T> {

    List<T> getDataList();

    void addList(List<T> list);

    void setList(List<T> listData);

    void clearList();

    T getItem(int position);

    void addItem(T item);

    void deleteItem(T item);

    void deleteItem(int position);
}
