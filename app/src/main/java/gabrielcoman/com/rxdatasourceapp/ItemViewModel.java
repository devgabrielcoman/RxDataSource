package gabrielcoman.com.rxdatasourceapp;

/**
 * Created by gabriel.coman on 05/12/2016.
 */

public class ItemViewModel extends ViewModel {
    private String name;
    private String details;

    public ItemViewModel(String name, String details) {
        this.name = name;
        this.details = details;
    }

    public String getName() {
        return name;
    }

    public String getDetails() {
        return details;
    }
}
