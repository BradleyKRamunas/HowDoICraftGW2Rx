package ui.console;


import io.reactivex.Observable;

public class ConsolePresenter implements ConsoleContract.Presenter {

    private ConsoleContract.View view;

    public ConsolePresenter(ConsoleContract.View view) {
        this.view = view;
    }

    private String getRecipe(String itemName, int quantity) throws InterruptedException {
        Thread.sleep(5000);
        return "Recipe fetched: " + itemName + ", " + quantity;
    }

    @Override
    public Observable<String> processRequest(String itemName, int quantity) {
        return Observable.create(subscriber -> subscriber.onNext(getRecipe(itemName, quantity)));
    }

    @Override
    public void detachView() {
        view = null;
    }

    @Override
    public boolean isViewAttached() {
        return this.view != null;
    }
}
