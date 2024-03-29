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
                LOGGER.log(Level.INFO, "Customer with Thread name " + Thread.currentThread().getName().toUpperCase() + " has entered the shop.");
                waitingCustomers.add(new Customers(this));              // adding the customers to the customers waiting line.
            }
            else
            {
                LOGGER.log(Level.INFO, "Customer with Thread name " + Thread.currentThread().getName().toUpperCase() + " has left because the shop is full.");  // customer isn't added if there are no remaining chairs waiting.
            }
        }
    }

    public void leaveShop()
    {
        synchronized (lock)
        {
            LOGGER.log(Level.INFO, "Customer with Thread name " + Thread.currentThread().getName().toUpperCase() + " has left after being served.");
        }
    }

    public void serveCustomer() throws InterruptedException {
        synchronized (lock)
        {
            if (!waitingCustomers.isEmpty())
            {
                System.out.println("****");
//                LOGGER.log(Level.INFO, "****");
                Customers customer = waitingCustomers.remove(0);       // the customer is removed from the waiting list and added to the customers being served.
                LOGGER.log(Level.INFO, "Barber with Thread name " + Thread.currentThread().getName().toUpperCase() + " serving a customer.");
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


        if (noOfMaxChairs <= 0)
        {
            noOfMaxChairs = 9; // 9 chairs if the command line argument is negative or 0
        }

        if (noOfBarbers <= 0)
        {
            noOfBarbers = 3; // 3 Barbers if the command line argument is negative or 0
        }

        Barbers[] barber = new Barbers[Integer.parseInt(args[1])];
        int cores = Runtime.getRuntime().availableProcessors();
        System.out.println("No of cores in this device: " + cores);
        System.out.println("No of chairs: " + noOfMaxChairs);
        System.out.println("No of barbers: " + noOfBarbers);


        BarberShop barberShop = new BarberShop(noOfMaxChairs);
        ExecutorService executor = Executors.newFixedThreadPool(cores);

        executor.submit(new Barbers(barberShop));

        executor.execute(() -> {


            for (int i = 0; i < Integer.parseInt(args[1]); i++) {
                barber[i] = new Barbers(barberShop);
                Thread thbarber = new Thread(barber[i]);
                thbarber.start();
            }
            Customers cg = new Customers(barberShop);
            Thread custThread = new Thread(cg);
            custThread.start();


        });
        LOGGER.log(Level.INFO, "No. of total served customers till now: " + barberShop.servedCustomers);
        executor.shutdown();
    }
}