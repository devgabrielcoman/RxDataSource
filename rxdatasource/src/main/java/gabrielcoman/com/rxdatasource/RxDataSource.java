package gabrielcoman.com.rxdatasource;

import android.content.Context;
import android.view.View;
import android.widget.ListView;

import com.jakewharton.rxbinding.widget.RxAdapterView;

import java.util.HashMap;
import java.util.List;

import rx.Observable;
import rx.functions.Action1;
import rx.functions.Action2;
import rx.functions.Func1;

public class RxDataSource {

    private Context context = null;
    private ListView listView = null;
    private int viewTypeIndex = 0;
    private HashMap<Class, Func1<Object, RxRow>> modelToRowMap = new HashMap<>();
    private HashMap<Class, Integer> modelToViewTypeMap = new HashMap<>();
    private HashMap<Integer, Action2<Integer, Object>> rowIdToClick = new HashMap<>();

    public static RxDataSource create (Context context) {
        RxDataSource dataSource = new RxDataSource();
        dataSource.context = context;
        return dataSource;
    }

    public RxDataSource bindTo (ListView listView) {
        this.listView = listView;
        return this;
    }

    public <T> RxDataSource customiseRow (final int rowId, final Class<T> modelClass, final Action2<View, T> func) {

        if (listView == null) return this;

        modelToRowMap.put(modelClass, new Func1<Object, RxRow>() {
            @Override
            public RxRow call(Object o) {

                RxRow row = new RxRow(context, rowId, modelClass, listView);
                View holder = row.getHolderView();
                func.call(holder, (T) o);

                return row;
            }
        });

        modelToViewTypeMap.put(modelClass, viewTypeIndex++);

        return this;
    }

    public RxDataSource onRowClick (final  int rowId, final Action1<Integer> action) {
        rowIdToClick.put(rowId, new Action2<Integer, Object>() {
            @Override
            public void call(Integer integer, Object o) {
                action.call(integer);
            }
        });
        return this;
    }

    public <T> RxDataSource onRowClick (final int rowId, final Action2<Integer, T> action) {
        rowIdToClick.put(rowId, new Action2<Integer, Object>() {
            @Override
            public void call(Integer integer, Object o) {
                action.call(integer, (T) o);
            }
        });
        return this;
    }

    public <T> RxDataSource update (final List<T> data) {

        Observable.from(data)
                .filter(new Func1<T, Boolean>() {
                    @Override
                    public Boolean call(T item) {
                        Class itemClass = item.getClass();
                        return modelToRowMap.containsKey(itemClass);
                    }
                })
                .toList()
                .subscribe(new Action1<List<T>>() {
                    @Override
                    public void call(final List<T> filteredData) {

                        Observable.from(filteredData)
                                .map(new Func1<T, RxRow>() {
                                    @Override
                                    public RxRow call(T item) {
                                        Class itemClass = item.getClass();
                                        Func1<Object, RxRow> mappingFunc = modelToRowMap.get(itemClass);
                                        return mappingFunc.call(item);
                                    }
                                })
                                .toList()
                                .subscribe(new Action1<List<RxRow>>() {
                                    @Override
                                    public void call(final List<RxRow> rows) {

                                        final RxAdapter adapter = new RxAdapter(context);
                                        adapter.setViewTypeCount(modelToRowMap.size());
                                        adapter.setItemViewType(new Func1<Integer, Integer>() {
                                            @Override
                                            public Integer call(Integer pos) {
                                                RxRow row = rows.get(pos);
                                                Class itemClass = row.getRowClass();
                                                return modelToViewTypeMap.get(itemClass);
                                            }
                                        });

                                        listView.setAdapter(adapter);
                                        adapter.updateData(rows);
                                        adapter.reloadTable();

                                        RxAdapterView.itemClicks(listView)
                                                .subscribe(new Action1<Integer>() {
                                                    @Override
                                                    public void call(Integer pos) {

                                                        T dt = filteredData.get(pos);
                                                        RxRow row = rows.get(pos);
                                                        int id = row.getRowId();
                                                        Action2<Integer, Object> action1 = rowIdToClick.get(id);
                                                        action1.call(pos, dt);

                                                    }
                                                });
                                    }
                                });

                    }
                });

        return this;
    }

}
