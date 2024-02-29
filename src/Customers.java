import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.Random;

public class Customers implements Runnable {
    private final BarberShop barberShop;

    public Customers(BarberShop barberShop) {
        this.barberShop = barberShop;
    }

    @Override
    public void run() {
        try {
            barberShop.enterShop();
            TimeUnit.SECONDS.sleep(1); // Simulating hair cutting time
            barberShop.leaveShop();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}