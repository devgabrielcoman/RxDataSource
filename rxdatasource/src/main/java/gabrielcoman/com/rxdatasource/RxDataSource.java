package gabrielcoman.com.rxdatasource;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.ListView;

import com.jakewharton.rxbinding.widget.RxAdapterView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import rx.Observable;
import rx.functions.Action1;
import rx.functions.Action2;
import rx.functions.Func1;

/**
 * Created by gabriel.coman on 06/12/2016.
 */

public class RxDataSource<T> {

    private ListView listView = null;
    private Context context = null;
    private List<T> data = null;

    private int viewTypeIndex = 0;

    private HashMap<Class, Func1<T, RxRow>> viewModelToRxRowMap = null;
    private HashMap<Class, Integer> viewModelToViewTypeMap = null;

    private Observable<Integer> itemClicksObserver = null;

    /**
     * Private constructor for the data source
     * @param context the current context
     */
    private RxDataSource(@NonNull Context context) {
        // copy a reference to the context
        this.context = context;

        // initialise the data needed by the RxDataSource object
        this.data = new ArrayList<>();

        // initialise the three hash maps needed by the RxDataSource object in order to keep a
        // track of what and how it needs to display in the List View
        this.viewModelToRxRowMap = new HashMap<>();
        this.viewModelToViewTypeMap = new HashMap<>();
    }

    /**
     * Static method used to create a new RxDataSource object just by giving the context
     * @param context the current context
     * @param <T> the T over which to present data; in this case T is the "ViewModel" type object
     *           that will be used to represent data in each of the rows, according to MVVM
     * @return a new instance of the RxDataSource object
     */
    public static <T> RxDataSource<T> create (@NonNull Context context) {
        return new RxDataSource<>(context);
    }

    /**
     * Static method used to create a new RxDataSource object by giving both the context
     * and a new list of data elements. This will mean that the "update" method will not need to
     * be called at creation time
     * @param context the current context
     * @param data the new data set, of T type objects
     * @param <T> the T over which to present data; in this case T is the "ViewModel" type object
     *           that will be used to represent data in each of the rows, according to MVVM
     * @return a new instance of the RxDataSource object
     */
    public static <T> RxDataSource<T> from (@NonNull Context context, @NonNull List<T> data) {

        // create a new data source object over T and feed it the context
        RxDataSource<T> dataSource = new RxDataSource<>(context);

        // update it's data already
        dataSource.data = data;

        // return it to the user
        return dataSource;
    }

    /**
     * Method that binds the RxDataSource to a single ListView object
     * @param listView a non-null list view
     * @return the same instance of the RxDataSource object, to chain calls
     */
    public RxDataSource<T> bindTo (@NonNull ListView listView) {

        // update the internal list view reference
        this.listView = listView;

        // start the item clicks observer and share it
        itemClicksObserver = RxAdapterView.itemClicks(listView).share();

        // return the current instance
        return this;
    }

    /**
     * Method that customises a given row's view.
     * This should be called for as many rows you need to customise for your ListView
     * @param rowId the rowId to customise the view for
     * @param viewModelClass the associated view model class. Setting this param will ensure that
     *                       the rowId mentioned will get associated with the View Model you desire
     * @param func the customisation function (as an Rx Action2 type). This has the following params:
     *             - T:     callback parameter of the same type as the View Model associated with
     *                      the RxDataSource
     *             - View:  the holder view; basically the view that should get loaded when the
     *                      row type is rowId
     * @return the same instance of the RxDataSource object, to chain calls
     */
    public RxDataSource<T> customiseRow (final int rowId, @NonNull Class viewModelClass, @NonNull final Action2<T, View> func) {

        // if the list view is null then don't load anything and return
        if (this.listView == null) {
            return this;
        }

        // define a new Func1 object that operates on a "View Model" <T> type and
        // returns  RxRow object
        Func1<T, RxRow> newFunc = new Func1<T, RxRow>() {
            @Override
            public RxRow call(T t) {

                // create the new Row with it's constructor, using the rowId as layout indicator
                // and a ListView object as parent
                RxRow row = new RxRow(context, rowId, listView);

                // get the current holder view (that's just been created by the RxRow)
                View holder = row.getHolderView();

                // pass the "View Model" <T> parameter and the holde to the callback
                // where it should be parametrised
                func.call(t, holder);

                // and then return the RxRow object
                return row;
            }
        };

        // put the new Func1 object in the corresponding key of the "viewModelToRxRow" hash map
        viewModelToRxRowMap.put(viewModelClass, newFunc);

        // put the new index object in the corresponding key of the "viewModelToViewTypeMap" hash
        viewModelToViewTypeMap.put(viewModelClass, viewTypeIndex++);

        // return the current instance
        return this;
    }

    /**
     * Method that will determine what happens when a user clicks on a row
     * @param rowId the rowId you want to set an action for
     * @param action the action function, defined as an Action2 callback from RxJava
     * @return the same instance of the RxDataSource object, to chain calls
     */
    public RxDataSource<T> onRowClick (final int rowId, @NonNull final Action2<Integer, T> action) {

        // if this is null then don't go forward
        if (itemClicksObserver == null) {
            return this;
        }

        itemClicksObserver
                .subscribe(new Action1<Integer>() {
                    @Override
                    public void call(Integer position) {

                        // get the current data element of type "View Model" <T>
                        T t = data.get(position);

                        // get it's class
                        Class c = t.getClass();

                        if (viewModelToRxRowMap.containsKey(c)) {

                            // find out the associated RxRow Func type object
                            Func1<T, RxRow> rowFunc = viewModelToRxRowMap.get(c);

                            // and use that to get the RxRow object
                            RxRow row = rowFunc.call(t);

                            // get the desired row Id
                            int desiredRowId = row.getRowId();

                            // compare the desired Row ID with the one supplied by the user
                            if (rowId == desiredRowId) {

                                // and if all checks out, call the action
                                action.call(position, t);
                            }
                        }

                    }
                });

        // return the current instance
        return this;
    }

    /**
     * Method that performs an update of the current data set (every time)
     * @param data the new data set
     * @return the same instance of the RxDataSource object, to chain calls
     */
    public RxDataSource<T> update (@NonNull List<T> data) {

        // update the internal data
        // notice we copy a reference to the new data set, we don't add to the current data set
        // in order to respect functional programming percepts
        this.data = data;

        // check this
        if (viewModelToRxRowMap.keySet().size() == 0 || viewModelToRxRowMap.containsValue(null)) {
            return this;
        }

        // create the actual data array to be used by the adapter, which will only contain
        // elements that are of the same Class as any one of the ones from the viewModelToRxMap
        // hash map
        List<T> usableData = new ArrayList<>();

        for (T t : this.data) {
            Class c = t.getClass();
            if (viewModelToRxRowMap.containsKey(c)) {
                usableData.add(t);
            }
        }

        // initialise tha adapter
        final RxAdapter <RxRow> adapter = new RxAdapter<>(this.context);

        // now update the number of views the adapter can display
        adapter.setNumberOfViews(viewModelToRxRowMap.size());

        // and what type of view to display on each position
        adapter.setViewTypeRule(new Func1<Integer, Integer>() {
            @Override
            public Integer call(Integer position) {

                // get the ViewModel <T> for the current position
                T t = RxDataSource.this.data.get(position);

                // and find out it's class
                Class c = t.getClass();

                // if I can find the class key in the viewModelToViewType map then return
                // the value stored there (and I should always be able to find it)
                if (viewModelToViewTypeMap.containsKey(c)) {
                    return viewModelToViewTypeMap.get(c);
                }
                // else just return 0
                else {
                    return 0;
                }
            }
        });

        // then, if all is OK, start updating the table data
        Observable.from(usableData)
                .map(new Func1<T, RxRow>() {
                    @Override
                    public RxRow call(T t) {
                        Func1<T, RxRow> mappingFunc = viewModelToRxRowMap.get(t.getClass());
                        return mappingFunc.call(t);
                    }
                })
                .toList()
                .subscribe(new Action1<List<RxRow>>() {
                    @Override
                    public void call(List<RxRow> rxRows) {
                        // set the list view adapter to our current RxAdapter
                        listView.setAdapter(adapter);

                        // finally update the adapter with new RxRows objects
                        adapter.updateData(rxRows);

                        // and reload the table
                        adapter.reloadTable();
                    }
                });

        // return the current instance
        return this;
    }

    /**
     * Update / refresh method
     * @return the same instance of the RxDataSource object, to chain calls
     */
    public RxDataSource<T> update () {

        // call to the main update method with the current data as params
        this.update(this.data);

        // return the current instance
        return this;
    }
}
