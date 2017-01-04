package example.recycler.com.recyclerloremore;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrHandler;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final CZRecyclerView recyclerView = (CZRecyclerView) findViewById(R.id.recycler_view);
//        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
//        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        LinearLayoutManager manager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(manager);
        final MyAdapter adapter = new MyAdapter(this, getData());
        recyclerView.setAdapter(adapter);

        recyclerView.setOnLoadMoreListener(new CZRecyclerView.OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
//                DiffUtil.DiffResult diffResult =
//                        DiffUtil.calculateDiff(new CZDiffCallback(adapter.getDataList(), getMoreData()), true);
                adapter.addList(getMoreData());
//                adapter.setList(adapter.getDataList());
//                diffResult.dispatchUpdatesTo(adapter);
                recyclerView.stopLoadMore();
            }
        });

        PtrFrameLayout layout = (PtrFrameLayout) findViewById(R.id.store_house_ptr_frame);
        layout.setPtrHandler(new PtrHandler() {
            @Override
            public boolean checkCanDoRefresh(PtrFrameLayout frame, View content, View header) {
                return PtrDefaultHandler.checkContentCanBePulledDown(frame, content, header);
            }

            @Override
            public void onRefreshBegin(final PtrFrameLayout frame) {
                frame.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        frame.refreshComplete();
                    }
                }, 1800);
            }
        });
    }

    private List<String> getData() {
        List<String> list = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            list.add(i + "");
        }

        return list;
    }

    private List<String> getMoreData() {
        List<String> list = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            list.add(i + "");
        }

        return list;
    }

    class MyAdapter extends RecyclerViewAdapter {
        private Context mContext;
        private LayoutInflater mInflater;
        private List<String> mList;

        public MyAdapter(Context context, List<String> list) {
            super(list);
            mContext = context;
            mInflater = LayoutInflater.from(mContext);
            mList = list;
        }

        @Override
        public RecyclerView.ViewHolder onNewCreateViewHolder(ViewGroup parent, int viewType) {
            return new TextViewHolder(mInflater.inflate(R.layout.item, parent, false));
        }

        @Override
        public void onNewBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            if (holder instanceof TextViewHolder) {
                TextViewHolder viewHolder = (TextViewHolder) holder;
                viewHolder.textView.setText(position + "asdasdasdasdasdasd");
            }
        }

        class TextViewHolder extends RecyclerView.ViewHolder {
            TextView textView;

            public TextViewHolder(View itemView) {
                super(itemView);
                textView = (TextView) itemView.findViewById(R.id.textview);
            }
        }
    }
}
