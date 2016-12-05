package gabrielcoman.com.rxdatasource;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * A custom adapter class that extends ArrayAdapter over RxRow
 */
public class RxAdapter <T extends RxRow> extends ArrayAdapter<T> {

    /**
     * Private list of T elements (usually RxRow)
     */
    private List<T> data = new ArrayList<>();

    /**
     * Custom adapter constructor
     * @param context - the context this is going to run in
     */
    public RxAdapter(Context context) {
        super(context, 0);
    }

    /**
     * One of the two main RxAdapter methods - this will always update the data the adapter holds
     * Notice how the data is copied by reference, not replace or added to the current data set
     * This is in order to respect data immutability for functional programming
     * @param newData the new data set
     */
    void updateData(List<T> newData) {
        data = newData;
    }

    /**
     * This method does the actual reloading of the table by calling the Adapter method
     * notifyDataSetChanged
     */
    void reloadTable () {
        notifyDataSetChanged();
    }

    /**
     * Overridden adapter method getItem (for position)
     * @param position the current position I want to get an item for
     * @return the data or null, if the index is out of bounds
     */
    @Nullable
    @Override
    public T getItem(int position) {
        if (position < data.size()) {
            return data.get(position);
        } else {
            return null;
        }
    }

    /**
     * Overridden adapter method getCount
     * @return the size of the current data
     */
    @Override
    public int getCount() {
        return data.size();
    }

    /**
     * Overridden adapter method getView (for position, on convert view, with parent)
     * This method will get the data item at a certain position and call it's "getHolderView"
     * method. If the holder view exists and is customised, then that will be what will be
     * returned by the method and then displayed in the table
     * @param position - the position I want to get the item for
     * @param convertView - the basic convert view (that acts as a source for the row)
     * @param parent - the parent, usually the list view
     * @return - the allocated and customised list view row
     */
    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        return data.get(position).getHolderView();
    }

    /**
     * Main getter for the current data
     * @return the current data
     */
    public List<T> getData() {
        return data;
    }

}
