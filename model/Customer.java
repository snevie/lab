package model;

import java.util.concurrent.atomic.AtomicInteger;

//класс гостя
public class Customer {
    private static final AtomicInteger idCounter = new AtomicInteger(1);
    
    private final int id; //id гостя
    
    public Customer() {
        this.id = idCounter.getAndIncrement();
    }
    
    public int getId() {
        return id;
    }
    
    //получение готового блюда
    public void receiveOrder(Order order) {
        System.out.printf("Гость #%d получил %s (заказ #%d, время ожидания: %d мс)%n",
                id, order.getDish().getName(), order.getId(), order.getWaitingTime());
    }
    
    @Override
    public String toString() {
        return "Гость #" + id;
    }
}