import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
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
                LOGGER.log(Level.INFO, "Customer with Thread name " + Thread.currentThread().getName() + " has entered the shop.");
                waitingCustomers.add(new Customers(this));              // adding the customers to the customers waiting line.
            }
            else
            {
                LOGGER.log(Level.INFO, "Customer with Thread name " + Thread.currentThread().getName() + " has left because the shop is full.");  // customer isn't added if there are no remaining chairs waiting.
            }
        }
    }

    public void leaveShop()
    {
        synchronized (lock)
        {
            LOGGER.log(Level.INFO, "Customer with Thread name " + Thread.currentThread().getName() + " has left after being served.");
        }
    }

    public void serveCustomer() throws InterruptedException {
        synchronized (lock)
        {
            if (!waitingCustomers.isEmpty())
            {
                System.out.println("****");
                Customers customer = waitingCustomers.remove(0);       // the customer is removed from the waiting list and added to the customers being served.
                LOGGER.log(Level.INFO, "Barber with Thread name" + Thread.currentThread().getName() + " serving a customer.");
                servedCustomers++;
                customer.run();
            }
            else
            {
                LOGGER.log(Level.INFO, "Barber sleeps, no customers."); // Barber sleeps in his chair when there are no more customers for serving.
                lock.wait();
            }
        }
    }

    public static void main(String[] args)
    {

        int noOfMaxChairs = 0;
        int noOfBarbers = 0;
        try
        {
            noOfBarbers = Integer.parseInt(args[0]);            //fetching the command line argument for no of barbers.
        }
        catch (NumberFormatException nFE)
        {
            System.out.println("Enter the number of barbers");
            System.exit(0);
        }
        try
        {
            noOfMaxChairs = Integer.parseInt(args[1]);          //fetching the command line argument for no of chairs for customers.
        }
        catch (NumberFormatException nFE)
        {
            System.out.println("Enter the number of Chairs for customers");
            System.exit(0);
        }


        int cores = Runtime.getRuntime().availableProcessors();
        System.out.println("No of cores in this device: " + cores);
        System.out.println("No of chairs: " + noOfMaxChairs);
        System.out.println("No of barbers: " + noOfBarbers);

        if (noOfMaxChairs <= 0)
        {
            noOfMaxChairs = 9; // 9 chairs if the command line argument is negative or 0
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