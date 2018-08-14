package ui.console;

import io.reactivex.Observable;
import ui.BasePresenter;
import ui.BaseView;

public interface ConsoleContract {

    public interface View extends BaseView {
        void print(String message);
    }

    public interface Presenter extends BasePresenter {
        Observable<String> processRequest(String itemName, int quantity);
    }

}
