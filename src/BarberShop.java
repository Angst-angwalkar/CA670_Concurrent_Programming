import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
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

    public BarberShop(int maxChairs)
    {
        this.maxChairs = maxChairs;
        waitingCustomers = new LinkedList<>();
    }

    public void enterShop() throws InterruptedException
    {
        synchronized (lock)
        {
            if (waitingCustomers.size() < maxChairs)
            {
                LOGGER.log(Level.INFO, "Customer has entered the shop.");
                waitingCustomers.add(new Customers(this));
            }
            else
            {
                LOGGER.log(Level.INFO, "Customer has left because the shop is full.");
            }
        }
    }

    public void leaveShop()
    {
        synchronized (lock)
        {
            LOGGER.log(Level.INFO, "Customer has left the shop.");
        }
    }

    public void serveCustomer() throws InterruptedException {
        synchronized (lock)
        {
            if (!waitingCustomers.isEmpty())
            {
                Customers customer = waitingCustomers.remove(0);
                LOGGER.log(Level.INFO, "Barber serving a customer.");
                servedCustomers++;
                customer.run();
            }
            else
            {
                LOGGER.log(Level.INFO, "Barber sleeps, no customers.");
                lock.wait();
            }
        }
    }

    public static void main(String[] args) //throws IOException
    {

        int noOfMaxChairs = 0;
        int noOfBarbers = 0;
        try
        {
            noOfBarbers = Integer.parseInt(args[0]);
        }
        catch (NumberFormatException nFE)
        {
            System.out.println("Enter the number of barbers");
            System.exit(0);
        }
        try
        {
            noOfMaxChairs = Integer.parseInt(args[1]);
        }
        catch (NumberFormatException nFE)
        {
            System.out.println("Enter the number of Chairs for customers");
            System.exit(0);
        }


        int cores = Runtime.getRuntime().availableProcessors();
        System.out.println("No of cores in this device: " + cores);
        System.out.println("No of chairs entered: " + noOfMaxChairs);
        System.out.println("No of barbers entered: " + noOfBarbers);

        if (noOfMaxChairs <= 0)
        {
            noOfMaxChairs = 3; // 3 chairs if the command line argument is negative or 0
        }

        BarberShop barberShop = new BarberShop(noOfMaxChairs);
        ExecutorService executor = Executors.newCachedThreadPool();
        executor.submit(new Barbers(barberShop));


        if (noOfBarbers <= 0)
        {
            noOfBarbers = 3; // 3 chairs if the command line argument is negative or 0
        }

        for (int i = 0; i < noOfBarbers; i++) {
            executor.submit(new Customers(barberShop));
        }

        LOGGER.log(Level.INFO, "No. of total served customers till now: " + barberShop.servedCustomers);
        executor.shutdown();
    }
}