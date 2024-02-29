import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

public class BarberShop {
    private static final Logger LOGGER = Logger.getLogger(BarberShop.class.getName());
    private final int maxChairs;
    private final List<Customers> waitingCustomers;
    private final Object lock = new Object();
    private int servedCustomers = 0;

    public BarberShop(int maxChairs) {
        this.maxChairs = maxChairs;
        waitingCustomers = new LinkedList<>();
    }

    public void enterShop() throws InterruptedException {
        synchronized (lock) {
            if (waitingCustomers.size() < maxChairs) {
                LOGGER.log(Level.INFO, "Customer entered the shop.");
                waitingCustomers.add(new Customers(this));
            } else {
                LOGGER.log(Level.INFO, "Customer left, shop is full.");
            }
        }
    }

    public void leaveShop() {
        synchronized (lock) {
            LOGGER.log(Level.INFO, "Customer left the shop.");
        }
    }

    public void serveCustomer() throws InterruptedException {
        synchronized (lock) {
            if (!waitingCustomers.isEmpty()) {
                Customers customer = waitingCustomers.remove(0);
                LOGGER.log(Level.INFO, "Barber serving customer.");
                servedCustomers++;
                customer.run();
            } else {
                LOGGER.log(Level.INFO, "Barber sleeping, no customers.");
                lock.wait(); // Barber sleeps if no customers
            }
        }
    }

    public static void main(String[] args) {
        BarberShop barberShop = new BarberShop(3); // 3 chairs
        ExecutorService executor = Executors.newCachedThreadPool();
        executor.submit(new Barbers(barberShop));
        for (int i = 0; i < 10; i++) {
            executor.submit(new Customers(barberShop));
        }

        LOGGER.log(Level.INFO, "No. of total served customers till now: " + barberShop.servedCustomers);
        executor.shutdown();
    }
}