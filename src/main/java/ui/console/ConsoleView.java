package ui.console;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

import java.util.Scanner;

public class ConsoleView implements ConsoleContract.View {

    private ConsoleContract.Presenter presenter;
    private Scanner scanner;
    private Observable<String> inputObservable;
    private Observer<String> inputObserver;
    private Disposable inputDisposable;

    public ConsoleView() {
        presenter = new ConsolePresenter(this);
        presenter.onStart(new Observer<String>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(String s) {
                System.out.print("\r" + s);
            }

            @Override
            public void onError(Throwable e) {
                e.printStackTrace();
            }

            @Override
            public void onComplete() {

            }
        });
        printWelcomeMessage();
        scanner = new Scanner(System.in);
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
                    s = s.replaceAll("'", "''");
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
        print("Please enter the item name and quantity you'd like to craft.\n");
        print("Follow this pattern: 'item name,quantity', or: 'Zojja's Reaver,1' (with no quotes).\n");
        print("Type 'quit' to quit. Also, do be warned that some crafting steps are very long, scroll up to not miss ingredients!");
    }

    private void reset() {
        inputObservable.subscribe(inputObserver);
    }

    private void makeCraftingRequest(String itemName, int quantity) {
        Observable<String> result = presenter.processRequest(itemName, quantity);
        result.subscribeOn(Schedulers.io()).blockingSubscribe(System.out::println);
        reset();
    }

    @Override
    public void print(String message) {
        System.out.print(message);
    }
}
