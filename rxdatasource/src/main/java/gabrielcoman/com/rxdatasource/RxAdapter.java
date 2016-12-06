package gabrielcoman.com.rxdatasource;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import java.util.ArrayList;
import java.util.List;

import rx.functions.Func1;

/**
 * A custom adapter class that extends ArrayAdapter over RxRow
 */
public class RxAdapter <T extends RxRow> extends ArrayAdapter<T> {

    /**
     * The internal array of data to be used by the adapter to populate the adapter
     */
    private List<T> data = new ArrayList<>();

    /**
     * Member variable that holds the number of views the adapter should be able to present
     */
    private int numberOfViews = 1;

    /**
     * Function that will be used by the "getItemViewType" method to define what view it should
     * display based on the position of the row
     */
    private Func1<Integer, Integer> viewTypeRule = null;

    /**
     * Constructor for the Adapter
     * @param context the base context the adapter works in
     */
    RxAdapter(Context context) {
        super(context, 0);
    }

    /**
     * Method that updates the data for the adapter.
     * Please note the data is copied by reference from the parameter and not added to an
     * existing array.
     * @param newData the new array of data of type T
     */
    void updateData(List<T> newData) {
        data = newData;
    }

    /**
     * A wrapper arround the inaplty named "notifyDataSetChanged"
     */
    void reloadTable () {
        notifyDataSetChanged();
    }

    /**
     * Method to get an item at a certain position from the adapter
     * @param position the position I want to get an item from
     * @return the actual item, of type T
     */
    @Override
    public T getItem(int position) {
        return data.get(position);
    }

    /**
     * This method returns the number of views the adapter is able to represent
     * @return an integer
     */
    @Override
    public int getViewTypeCount() {
        return numberOfViews;
    }

    /**
     * This method returns the index of the view to be represented based on the current row
     * position; It's value in the RxAdapter case is given by a member var of type Func1<int, int>
     * that will be defined by the RxDataSource object
     * @param position current row position
     * @return the index of the type of view to display
     */
    @Override
    public int getItemViewType(int position) {
        return viewTypeRule.call(position);
    }

    /**
     * Get the number of elements in the adapter
     * @return an integer defining the nr. elements in the adapter
     */
    @Override
    public int getCount() {
        return data.size();
    }

    /**
     * This method is used with RxRow type elements in the adapter to actually display a type of
     * row for a given position
     * @param position position of the current row
     * @param convertView the basic view the ListView provides
     * @param parent the parent, usually the ListView object containing the rows
     * @return a valid, configured View
     */
    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        return data.get(position).getHolderView();
    }

    /**
     * Setter method that sets the number of views the adapter should display.
     * Usually it's called by the RxDataSource
     * @param numberOfViews new number of views
     */
    void setNumberOfViews(int numberOfViews) {
        this.numberOfViews = numberOfViews;
    }

    /**
     * Setter method that sets the function that will guide what view type to display for a certain
     * row number
     * @param viewTypeRule the rule, as a Func1<Integer, Integer> type defined by RxJava
     */
    void setViewTypeRule(Func1<Integer, Integer> viewTypeRule) {
        this.viewTypeRule = viewTypeRule;
    }
}
