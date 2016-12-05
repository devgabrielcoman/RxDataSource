package gabrielcoman.com.rxdatasourceapp;

/**
 * Created by gabriel.coman on 05/12/2016.
 */

public class HeaderViewModel extends ViewModel {

    private String title;

    public HeaderViewModel(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }
}
