package ui.console;

import io.reactivex.*;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

import java.util.Scanner;
import java.util.concurrent.CountDownLatch;

public class ConsoleView implements ConsoleContract.View {

    private ConsoleContract.Presenter presenter;
    private Scanner scanner;
    private Observable<String> inputObservable;
    private Observer<String> inputObserver;
    private Disposable inputDisposable;
    private Disposable resultDisposable;
    private CountDownLatch countDownLatch;

    public ConsoleView() {
        presenter = new ConsolePresenter(this);
        scanner = new Scanner(System.in);
        printWelcomeMessage();
        inputObservable = Observable.create(subscriber -> {
           while(!subscriber.isDisposed()) {
               String input = scanner.nextLine();
               if(input.equals("quit")) {
                    inputDisposable.dispose();
               } else {
                   subscriber.onNext(input);
               }
           }
        });
        inputObserver = new Observer<String>() {
            @Override
            public void onSubscribe(Disposable d) {
                inputDisposable = d;
            }

            @Override
            public void onNext(String s) {
                try {
                    String[] split = s.split(",");
                    String itemName = split[0];
                    int quantity = Integer.parseInt(split[1]);
                    inputDisposable.dispose();
                    makeCraftingRequest(itemName, quantity);
                } catch (Exception e) {
                    onError(e);
                }
            }

            @Override
            public void onError(Throwable e) {
                System.err.println("There was an error understanding your request: ");
                System.err.println(e.getMessage());
            }

            @Override
            public void onComplete() {

            }
        };
        inputObservable.subscribe(inputObserver);
    }

    private void printWelcomeMessage() {
        print("Welcome to HowDoICraftGW2, now with RxJava!\n");
        print("Please follow the instructions to craft the item(s) you wish to craft.\n");
    }

    private void reset() {
        inputObservable.subscribe(inputObserver);
    }

    private void makeCraftingRequest(String itemName, int quantity) throws InterruptedException {
        Observable<String> result = presenter.processRequest(itemName, quantity);
        Observer<String> resultObserver = new Observer<String>() {
            @Override
            public void onSubscribe(Disposable d) {
                resultDisposable = d;
            }

            @Override
            public void onNext(String s) {
                System.out.println(s);
                countDownLatch.countDown();
                resultDisposable.dispose();
            }

            @Override
            public void onError(Throwable e) {
                System.err.println(e.getMessage());
            }

            @Override
            public void onComplete() {

            }
        };
        result.subscribeOn(Schedulers.io()).subscribe(resultObserver);
        countDownLatch = new CountDownLatch(1);
        countDownLatch.await();
        reset();
    }

    @Override
    public void print(String message) {
        System.out.print(message);
    }
}
