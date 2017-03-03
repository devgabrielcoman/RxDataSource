package gabrielcoman.com.rxdatasource;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class RxRow {

    private View holderView = null;

    private int rowId = 0;
    private Class rowClass;

    RxRow(Context context, int rowId, Class rowClass, ViewGroup parent) {

        this.rowId = rowId;
        this.rowClass = rowClass;

        if (holderView == null) {
            holderView = LayoutInflater.from(context).inflate(rowId, parent, false);
        }
    }

    public View getHolderView () {
        return holderView;
    }

    public int getRowId() {
        return rowId;
    }

    public Class getRowClass() {
        return rowClass;
    }
}
