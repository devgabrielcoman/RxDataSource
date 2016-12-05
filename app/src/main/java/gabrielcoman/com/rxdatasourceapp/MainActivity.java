package gabrielcoman.com.rxdatasourceapp;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;

import com.jakewharton.rxbinding.view.RxView;

import java.util.ArrayList;
import java.util.List;

import gabrielcoman.com.rxdatasource.RxDataSource;
import rx.Subscriber;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ListView listView = (ListView) findViewById(R.id.MyListView);

        getDatas().toList()
                .subscribe(viewModels -> {

                    RxDataSource.from(MainActivity.this, viewModels)
                            .bindTo(listView)
                            .customiseRow(R.layout.row_header, HeaderViewModel.class, (viewModel, holderView) -> {

                                HeaderViewModel header = (HeaderViewModel) viewModel;
                                TextView title = (TextView) holderView.findViewById(R.id.HeaderTitle);
                                title.setText(header.getTitle());

                            })
                            .customiseRow(R.layout.row_item, ItemViewModel.class, (viewModel, holderView) -> {

                                ItemViewModel item = (ItemViewModel) viewModel;
                                TextView title = (TextView) holderView.findViewById(R.id.ItemTitle);
                                TextView details = (TextView) holderView.findViewById(R.id.ItemDetails);
                                title.setText(item.getName());
                                details.setText(item.getDetails());

                            })
                            .customiseRow(R.layout.row_switch, SwitchViewModel.class, (viewModel, holderView) -> {

                                SwitchViewModel sw = (SwitchViewModel) viewModel;
                                TextView title = (TextView) holderView.findViewById(R.id.SwitchTitle);
                                Switch swbutton = (Switch) holderView.findViewById(R.id.SwitchButton);
                                title.setText(sw.getTitle());
                                swbutton.setChecked(sw.isActive());

                                RxView.clicks(swbutton).subscribe(aVoid -> {
                                    sw.setActive(swbutton.isChecked());
                                });
                            })
                            .onRowClick(R.layout.row_item, (integer, viewModel) -> Log.d("RxDataSource", "Wohoo it works!"))
                            .onRowClick(R.layout.row_header, (integer, viewModel) -> Log.d("RxDataSource", "Header click!"))
                            .fire();

                });

    }

    private rx.Observable<ViewModel> getDatas () {

        return rx.Observable.create(new rx.Observable.OnSubscribe<ViewModel>() {
            @Override
            public void call(Subscriber<? super ViewModel> subscriber) {

                List<ViewModel> data = new ArrayList<>();
                data.add(new ItemViewModel("Item #1", "Lorem ipsum something"));
                data.add(new HeaderViewModel("Header 1"));
                data.add(new ItemViewModel("Item #2", "Lorem ipsum another"));
                data.add(new SwitchViewModel("Button #1", true));
                data.add(new HeaderViewModel("Header 2"));
                data.add(new ItemViewModel("Item #3", "Lorem ipsum third"));
                data.add(new SwitchViewModel("Button #2", false));

                // send data to subscriber
                for (ViewModel vm : data) {
                    subscriber.onNext(vm);
                }
                subscriber.onCompleted();

            }
        });

    }

}
