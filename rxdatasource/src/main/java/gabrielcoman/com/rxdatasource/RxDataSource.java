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
 * A class that represents a Data Source that needs to be bound to a table.
 * The workflow is that you can define a data source of "ViewModel" generic objects (<T>) and
 * use the DataSource to describe how each type of row associated with the ViewModel looks like,
 * as well as what happens when a user clicks on a row.
 *
 * Proposed way of working:
 *
 * ListView listView = (ListView) findViewById (R.id.myListView);
 * List <T> dataSource = getDataSource ();
 *
 * Observable.from (dataSource)
 *  .toList ()
 *  .subscribe (data ->
 *
 *      RxDataSource.create (MainActivity.this)
 *          .bindTo (listView)
 *          .customiseRow (R.layout.my_row, CustomRow.class, (viewModel, view) -> {
 *
 *              TextView text = (TextView) view.findViewById (R.id.title);
 *              title.setText (viewModel.getTitle ());
 *
 *          })
 *          .update (data);
 *
 *  )
 *
 * @param <T>
 */

public class RxDataSource <T> {

    /**
     * Reference to the current context
     */
    private Context context = null;

    /**
     * Reference to the list view that the adapter and data source should be linked to
     */
    private ListView listView = null;

    /**
     * A list of data objects of type <T>. This should be the View Models that represent each row,
     * as described in the MVVM pattern (https://en.wikipedia.org/wiki/Model-view-viewmodel)
     */
    private List<T> data = null;

    /**
     * An index of all the view types being registered. This should be incremented each time the
     * "customiseRow" method is called.
     */
    private int viewTypeIndex = 0;

    /**
     * A hash map containing keys of type Class and values of type Func1 <T, RxRow>.
     * This will get filled each time the "customiseRow" method gets called.
     * For each type of Class registered in the hash map a function will be defined to tell how
     * the ViewModel is to be transformed into an RxRow type object (that will in turn tell
     * the adapter how to draw the cell)
     */
    private HashMap<Class, Func1<T, RxRow>> viewModelToRxRowMap = null;

    /**
     * A hash map containing keys of type Class and values of type Integer.
     * This will get filled each time the "customiseRow" method gets called.
     * For each type of Class registered in the hash map this will record the type of view
     * that should get represented, as desired by the Android Adapter class (where view types
     * are integers)
     */
    private HashMap<Class, Integer> viewModelToViewTypeMap = null;

    /**
     * An observable over Integer that is used by the ListView to record clicks for each row.
     */
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
        viewModelToRxRowMap.put(viewModelClass, new Func1<T, RxRow>() {
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
        });

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
    public RxDataSource <T> update (@NonNull List<T> data) {

        // initialise tha adapter
        final RxAdapter <RxRow> adapter = new RxAdapter<>(this.context);

        Observable.from(data)
                .filter(new Func1<T, Boolean>() {
                    @Override
                    public Boolean call(T t) {
                        return viewModelToRxRowMap.containsKey(t.getClass());
                    }
                })
                .toList()
                .subscribe(new Action1<List<T>>() {
                    @Override
                    public void call(final List<T> ts) {

                        // set adapter number of views and the view type rule
                        adapter.setNumberOfViews(viewModelToRxRowMap.size());
                        adapter.setViewTypeRule(new Func1<Integer, Integer>() {
                            @Override
                            public Integer call(Integer pos) {
                                return viewModelToViewTypeMap.get(ts.get(pos).getClass());
                            }
                        });

                        // update data
                        RxDataSource.this.data = ts;

                        // take the valid rows and map them to RxRows based on the function
                        // for each type of class
                        Observable.from(RxDataSource.this.data )
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
