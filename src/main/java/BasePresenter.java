public interface BasePresenter {
    void attachView(BaseView view);
    void detachView();
    boolean isViewAttached();
}
