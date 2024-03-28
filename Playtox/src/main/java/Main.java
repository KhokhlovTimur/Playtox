import service.App;
import service.ExecutingService;
import service.MoneyTransferService;

public class Main {

    public static void main(String[] args) {
        int threadsCount = 4;
        int accountsCount = 8;

        ExecutingService app = new App(
                new MoneyTransferService(),
                threadsCount,
                accountsCount,
                500);

        app.start();

    }
}
