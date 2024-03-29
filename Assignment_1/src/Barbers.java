package src;

import java.util.concurrent.TimeUnit;

public class Barbers implements Runnable {
    private final BarberShop barberShop;

    public Barbers(BarberShop barberShop) {
        this.barberShop = barberShop;
    }

    @Override
    public void run() {
        while (true)
        {
            try
            {
                barberShop.serveCustomer();
                TimeUnit.SECONDS.sleep(2); // Simulating hair cutting time
            }
            catch (InterruptedException e)
            {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }
}