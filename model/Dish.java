package model;

//класс блюда
public enum Dish {
    //блюда с разным временем приготовления
    SALAD("Салат", 2000, 150),
    SOUP("Суп", 3000, 200),
    PIZZA("Пицца", 5000, 350),
    PASTA("Паста", 4000, 300),
    BURGER("Бургер", 3500, 250),
    DESSERT("Десерт", 2500, 180);
    
    private final String name;
    private final int preparationTime; //время приготовления в миллисекундах
    private final int price;
    
    Dish(String name, int preparationTime, int price) {
        this.name = name;
        this.preparationTime = preparationTime;
        this.price = price;
    }
    
    public String getName() {
        return name;
    }
    
    public int getPreparationTime() {
        return preparationTime;
    }
    
    public int getPrice() {
        return price;
    }
    
    //получение случайного блюда из меню
    public static Dish getRandomDish() {
        Dish[] dishes = values();
        return dishes[(int) (Math.random() * dishes.length)];
    }
    
    @Override
    public String toString() {
        return name + " (" + price + " руб)";
    }
}