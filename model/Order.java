package model;

import java.util.concurrent.atomic.AtomicInteger;

//класс заказа
public class Order {
    private static final AtomicInteger idCounter = new AtomicInteger(1);
    
    private final int id;            //id заказа
    private final Customer customer; //кто сделал заказ
    private final Dish dish;         //заказанное блюдо
    private OrderStatus status;      //текущий статус заказа
    private final long creationTime; //время создания заказа
    
    public Order(Customer customer, Dish dish) {
        this.id = idCounter.getAndIncrement();
        this.customer = customer;
        this.dish = dish;
        this.status = OrderStatus.CREATED;
        this.creationTime = System.currentTimeMillis();
    }
    
    public int getId() { return id; }
    public Customer getCustomer() { return customer; }
    public Dish getDish() { return dish; }
    public OrderStatus getStatus() { return status; }
    public long getCreationTime() { return creationTime; }
    
    public void setStatus(OrderStatus status) {
        this.status = status;
    }
    
    //вычисление времени ожидания заказа
    public long getWaitingTime() {
        return System.currentTimeMillis() - creationTime;
    }
    
    @Override
    public String toString() {
    return String.format("Заказ #%d: %s для %s | Статус: %s | Ожидание: %d мс",
            id, dish.getName(), customer, status, getWaitingTime());
    }
    
    //перечисление статусов заказа
    public enum OrderStatus {
        CREATED("Создан"),
        ACCEPTED("Принят официантом"),
        COOKING("Готовится"),
        READY("Готов"),
        DELIVERED("Отдан");
        
        private final String description;
        
        OrderStatus(String description) {
            this.description = description;
        }
        
        @Override
        public String toString() {
            return description;
        }
    }
}