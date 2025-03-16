package cloud.java.restaurantapp;

import org.springframework.boot.SpringApplication;

public class TestRestaurantAppApplication {

    public static void main(String[] args) {
        SpringApplication.from(RestaurantAppApplication::main).with(TestcontainersConfiguration.class).run(args);
    }

}
