package gabrielcoman.com.rxdatasourceapp;

import android.util.Log;

public class SwitchViewModel {

    private String title;

    private boolean active;

    public SwitchViewModel(String title, boolean active) {
        this.title = title;
        this.active = active;
    }

    public String getTitle() {
        return title;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        Log.d("RxDataSource", "Just puttin switch to " + active);
        this.active = active;
    }
}
