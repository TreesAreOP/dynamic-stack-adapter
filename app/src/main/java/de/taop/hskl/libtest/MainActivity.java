package de.taop.hskl.libtest;

import android.content.res.Resources;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.View;
import android.widget.TextView;

import java.text.DecimalFormat;

import de.taop.hskl.dynamicStackAdapter.DynamicStackAdapter;
import de.taop.hskl.dynamicStackAdapter.DynamicStackBuilder;
import de.taop.hskl.dynamicStackAdapter.DynamicStackViewHolder;

public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RecyclerView stackView = (RecyclerView) findViewById(R.id.stack);


        final MyAdapter adapter = (MyAdapter) new DynamicStackBuilder()
                .stackRecyclerView(stackView)
                .withAdapterType(MyAdapter.class)
                .withViewHolderType(MyViewHolder.class)
                .withItemLayoutID(R.layout.item_layout)
                .allowUserResizingItems(true)
                .setAutoResizeItems(true)
                .provideResizeAreaID(R.id.resize)
                .build();

        adapter.addItem(new Data());
        adapter.addItem(new Data());
        adapter.addItem(new Data());

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.floatingActionButton);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adapter.addItem(new Data());
            }
        });

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

class MyViewHolder extends DynamicStackViewHolder {

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
    protected void updateOnResize(int i, Object o, float percentage) {
        text.setText("percentage: " + df.format(percentage * 100f) + "%");
    }

}
