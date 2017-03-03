package gabrielcoman.com.rxdatasource;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import java.util.ArrayList;
import java.util.List;

import rx.functions.Func1;

public class RxAdapter extends ArrayAdapter<RxRow> {

    private List<RxRow> data = new ArrayList<>();
    private int viewTypeCount = 1;
    private Func1<Integer, Integer> itemViewType = null;

    RxAdapter(Context context) {
        super(context, 0);
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public int getViewTypeCount() {
        return viewTypeCount;
    }

    public void setViewTypeCount (int vtc) {
        viewTypeCount = vtc;
    }

    @Override
    public int getItemViewType(int p) {
        return itemViewType.call(p);
    }

    public void setItemViewType (Func1<Integer, Integer> ivt) {
        itemViewType = ivt;
    }

    void updateData(List<RxRow> newData) {
        data = newData;
    }

    void reloadTable () {
        notifyDataSetChanged();
    }

    @Nullable
    @Override
    public RxRow getItem(int position) {
        return data.get(position);
    }

    @NonNull
    @Override
    public View getView(int i, View v, @NonNull ViewGroup p) {
        return data.get(i).getHolderView();
    }
}
