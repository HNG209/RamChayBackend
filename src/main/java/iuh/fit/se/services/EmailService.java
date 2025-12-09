package iuh.fit.se.services;

import iuh.fit.se.entities.Order;

public interface EmailService {
    /**
     * Gửi email xác nhận đơn hàng cho khách hàng
     * @param order Đơn hàng vừa tạo
     * @param customerEmail Email của khách hàng
     */
    void sendOrderConfirmationEmail(Order order, String customerEmail);
}

