package ui.console;

import io.reactivex.Observable;
import io.reactivex.Observer;
import ui.BasePresenter;
import ui.BaseView;

public interface ConsoleContract {

    public interface View extends BaseView {
        void print(String message);
    }

    public interface Presenter extends BasePresenter {
        void onStart(Observer<String> progressObserver);
        Observable<String> processRequest(String itemName, int quantity);
    }

}
