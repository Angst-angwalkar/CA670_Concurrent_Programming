

public class SleepingBarbers {
    private static final int NUM_CHAIRS = 5; // Number of chairs in the waiting room
    private static final int NUM_BARBERS = 3; // Number of barbers

    public static void main(String[] args) {
        BarberShop barberShop = new BarberShop(NUM_CHAIRS, NUM_BARBERS);

        // Create and start the barber threads
        for (int i = 1; i <= NUM_BARBERS; i++) {
            Thread barberThread = new Thread(new Barber(barberShop, i));
            barberThread.start();
        }

        // Create and start the customer threads
        for (int i = 1; i <= 20; i++) {
            Thread customerThread = new Thread(new Customer(barberShop, i));
            customerThread.start();
            try {
                Thread.sleep(100); // Introduce a delay between customer arrivals
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
