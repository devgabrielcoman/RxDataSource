package gabrielcoman.com.rxdatasourceapp;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;

import com.jakewharton.rxbinding.view.RxView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import gabrielcoman.com.rxdatasource.RxDataSource;
import rx.functions.Action1;
import rx.functions.Action2;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ListView listView = (ListView) findViewById(R.id.MyListView);
        Button update = (Button) findViewById(R.id.UpdateDataSet);

        RxView.clicks(update)
                .subscribe(this::updateDataSet);

        getDatas().toList()
                .subscribe(viewModels -> {

                    RxDataSource.create(this)
                            .bindTo(listView)
                            .customiseRow(R.layout.row_header, HeaderViewModel.class, new Action2<View, HeaderViewModel> () {
                                @Override
                                public void call(View view, HeaderViewModel header) {

                                    TextView title = (TextView) view.findViewById(R.id.HeaderTitle);
                                    title.setText(header.getTitle());

                                }
                            })
                            .customiseRow(R.layout.row_item, ItemViewModel.class, new Action2<View, ItemViewModel>() {
                                @Override
                                public void call(View view, ItemViewModel item) {

                                    TextView title = (TextView) view.findViewById(R.id.ItemTitle);
                                    TextView details = (TextView) view.findViewById(R.id.ItemDetails);
                                    title.setText(item.getName());
                                    details.setText(item.getDetails());

                                }
                            })
                            .customiseRow(R.layout.row_switch, SwitchViewModel.class, new Action2<View, SwitchViewModel>() {
                                @Override
                                public void call(View view, SwitchViewModel sw) {

                                    TextView title = (TextView) view.findViewById(R.id.SwitchTitle);
                                    Switch swbtn = (Switch) view.findViewById(R.id.SwitchButton);
                                    title.setText(sw.getTitle());
                                    swbtn.setChecked(sw.isActive());

                                    RxView.clicks(swbtn).subscribe(aVoid -> {
                                        sw.setActive(swbtn.isChecked());
                                    });

                                }
                            })
                            .onRowClick(R.layout.row_header, new Action1<Integer>() {
                                @Override
                                public void call(Integer pos) {
                                    Log.d("RX-DATA", "Clicked on Header " + pos);
                                }
                            })
                            .onRowClick(R.layout.row_item, new Action2<Integer, ItemViewModel>() {
                                @Override
                                public void call(Integer pos, ItemViewModel model) {
                                    Log.d("RX-DATA", "Clicked on Item " + pos + " | " + model.getName());
                                }
                            })
                            .update(viewModels);

                });

    }

    private rx.Observable<Object> getDatas () {

        return rx.Observable.create(subscriber -> {

            List<Object> data = new ArrayList<>();
            data.add(new ItemViewModel("Item #1", "Lorem ipsum something"));
            data.add(new HeaderViewModel("Header 1"));
            data.add(new ItemViewModel("Item #2", "Lorem ipsum another"));
            data.add(new SwitchViewModel("Button #1", true));
            data.add(new HeaderViewModel("Header 2"));
            data.add(new ItemViewModel("Item #3", "Lorem ipsum third"));
            data.add(new SwitchViewModel("Button #2", false));
            data.add(new ItemViewModel("Item #1", "Lorem ipsum something"));
            data.add(new ItemViewModel("Item #1", "Lorem ipsum something"));
            data.add(new ItemViewModel("Item #1", "Lorem ipsum something"));
            data.add(new ItemViewModel("Item #1", "Lorem ipsum something"));
            data.add(new ItemViewModel("Item #1", "Lorem ipsum something"));
            data.add(new ItemViewModel("Item #1", "Lorem ipsum something"));
            data.add(new ItemViewModel("Item #1", "Lorem ipsum something"));
            data.add(new ItemViewModel("Item #1", "Lorem ipsum something"));
            data.add(new ItemViewModel("Item #1", "Lorem ipsum something"));
            data.add(new ItemViewModel("Item #1", "Lorem ipsum something"));
            data.add(new ItemViewModel("Item #1", "Lorem ipsum something"));
            data.add(new ItemViewModel("Item #1", "Lorem ipsum something"));
            data.add(new ItemViewModel("Item #1", "Lorem ipsum something"));
            data.add(new ItemViewModel("Item #1", "Lorem ipsum something"));
            data.add(new ItemViewModel("Item #2", "Lorem ipsum another"));
            data.add(new SwitchViewModel("Button #1", true));
            data.add(new HeaderViewModel("Header 3"));
            data.add(new ItemViewModel("Item #3", "Lorem ipsum third"));
            data.add(new ItemViewModel("Item #1", "Lorem ipsum something"));
            data.add(new ItemViewModel("Item #1", "Lorem ipsum something"));
            data.add(new ItemViewModel("Item #1", "Lorem ipsum something"));
            data.add(new ItemViewModel("Item #1", "Lorem ipsum something"));
            data.add(new ItemViewModel("Item #1", "Lorem ipsum something"));
            data.add(new ItemViewModel("Item #1", "Lorem ipsum something"));
            data.add(new ItemViewModel("Item #1", "Lorem ipsum something"));
            data.add(new ItemViewModel("Item #1", "Lorem ipsum something"));
            data.add(new ItemViewModel("Item #2", "Lorem ipsum another"));
            data.add(new SwitchViewModel("Button #1", true));
            data.add(new HeaderViewModel("Header 4"));
            data.add(new ItemViewModel("Item #3", "Lorem ipsum third"));

            // send data to subscriber
            for (Object vm : data) {
                subscriber.onNext(vm);
            }
            subscriber.onCompleted();

        });
    }

    private void updateDataSet (Void v) {

        List<Object> data = new ArrayList<>();
        data.add(new ItemViewModel("Item #1", "Lorem ipsum something"));
        data.add(new HeaderViewModel("Header 1"));
        data.add(new ItemViewModel("Item #2", "Lorem ipsum another"));
        data.add(new SwitchViewModel("Button #1", true));
        data.add(new HeaderViewModel("Header 2"));

//        dataSource2.update(data);
    }

}
