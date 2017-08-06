package de.taop.hskl.libtest;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import java.text.DecimalFormat;

import de.taop.hskl.dynamicStackAdapter.DynamicStackAdapter;
import de.taop.hskl.dynamicStackAdapter.DynamicStackBuilder;
import de.taop.hskl.dynamicStackAdapter.DynamicStackSaveManager;
import de.taop.hskl.dynamicStackAdapter.DynamicStackViewHolder;

public class MainActivity extends AppCompatActivity {

    RecyclerView stackView;
    MyAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        stackView = (RecyclerView) findViewById(R.id.stack);

        adapter = (MyAdapter) new DynamicStackBuilder()
                .stackRecyclerView(stackView)
                .withAdapterType(MyAdapter.class)
                .withViewHolderType(MyViewHolder.class)
                .withItemLayoutID(R.layout.item_layout)
                .allowUserResizingItems(true)
                .setAutoResizeItems(true)
                .provideResizeAreaID(R.id.resize)
                .setPixelPadding(0)
                .build();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.floatingActionButton);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adapter.addItem(new Data());
            }
        });
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (adapter != null) {
            DynamicStackSaveManager.saveDynamicStackAdapter(outState, adapter);
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState != null) {
            DynamicStackSaveManager.restoreDynamicStackAdapter(savedInstanceState, adapter);
        }
    }

}

class MyAdapter extends DynamicStackAdapter<Data, MyViewHolder> {
    public MyAdapter(RecyclerView container, Class holderClass) {
        super(container, holderClass);
    }

    @Override
    public void withCreateViewHolder(MyViewHolder myViewHolder) {

    }

    @Override
    public void withBindViewHolder(MyViewHolder myViewHolder, int i, Data data) {
        myViewHolder.text.setText(data.text);

    }
}

class MyViewHolder extends DynamicStackViewHolder<Data> {

    TextView text;
    DecimalFormat df;

    protected MyViewHolder(View itemView, DynamicStackAdapter adapter) {
        super(itemView, adapter);
        df = new DecimalFormat("#");
    }

    @Override
    protected void findCustomViews(View view) {
        text = (TextView) view.findViewById(R.id.content);
    }

    @Override
    public void updateOnResize(int position, Data object, float itemViewPercentage) {
        text.setText("percentage: " + df.format(itemViewPercentage * 100f) + "%");
    }


}
