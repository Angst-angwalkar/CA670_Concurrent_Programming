//
//import java.util.Date;
//import java.util.LinkedList;
//import java.util.List;
//import java.util.concurrent.TimeUnit;
//
//import BarberShop;
//import Customers;
//
//public class Barbers implements Runnable {
//
//    BarberShop shop;
//    int id;
//
//
//    public Barbers(BarberShop shop, int id){
//
//        this.shop = shop;
//        this.id = id;
//    }
//
//
//    public void run(){
//        try{
//            Thread.sleep(1000);
//        }
//        catch(InterruptedException interex){
//            interex.printStackTrace();
//        }
//        while(true){
//            shop.hairCut(id);
//        }
//    }
//}

import java.util.concurrent.TimeUnit;

public class Barbers implements Runnable {
    private final BarberShop barberShop;

    public Barbers(BarberShop barberShop) {
        this.barberShop = barberShop;
    }

    @Override
    public void run() {
        while (true) {
            try {
                barberShop.serveCustomer();
                TimeUnit.SECONDS.sleep(2); // Simulating hair cutting time
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }
}