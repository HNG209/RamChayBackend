package iuh.fit.se.services;

import iuh.fit.se.entities.Product;

public interface CartService {

    void addItem(Product product, int quantity);

    void removeItem(Product product);

    void updateItem(Product product, int quantity);

    double calculate();


}
