package iuh.fit.se.services.impl;

import iuh.fit.se.entities.Order;
import iuh.fit.se.services.EmailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailServiceImpl implements EmailService {
    private final JavaMailSender mailSender;
    private final SpringTemplateEngine templateEngine;

    @Value("${app.mail.from}")
    private String fromEmail;

    @Value("${app.mail.from-name}")
    private String fromName;

    /**
     * Gửi email xác nhận đơn hàng cho khách hàng (bất đồng bộ)
     * @param order Đơn hàng vừa tạo
     * @param customerEmail Email của khách hàng
     */
    @Override
    @Async
    public void sendOrderConfirmationEmail(Order order, String customerEmail) {
        try {
            log.info("Sending order confirmation email to {} for order {}", customerEmail, order.getId());

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail, fromName);
            helper.setTo(customerEmail);
            helper.setSubject("Xác nhận đơn hàng #" + order.getId() + " - RamChay Store");

            // Prepare template data
            Context context = new Context();
            context.setVariable("orderId", order.getId());
            context.setVariable("orderDate", order.getOrderDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
            context.setVariable("receiverName", order.getReceiverName());
            context.setVariable("receiverPhone", order.getReceiverPhone());
            context.setVariable("shippingAddress", order.getShippingAddress());
            context.setVariable("paymentMethod", formatPaymentMethod(order.getPaymentMethod().name()));
            context.setVariable("orderStatus", formatOrderStatus(order.getOrderStatus().name()));

            // Format order items
            NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
            var orderItems = order.getOrderDetails().stream()
                    .map(detail -> {
                        Map<String, Object> item = new HashMap<>();
                        item.put("productName", detail.getProduct().getName());
                        item.put("quantity", detail.getQuantity());
                        item.put("unitPrice", currencyFormat.format(detail.getUnitPrice()));
                        item.put("subtotal", currencyFormat.format(detail.getQuantity() * detail.getUnitPrice()));
                        return item;
                    })
                    .toList();

            context.setVariable("orderItems", orderItems);
            context.setVariable("total", currencyFormat.format(order.getTotal()));

            // Determine if customer is guest or registered
            boolean isGuest = order.getCustomer() == null;
            context.setVariable("isGuest", isGuest);

            // Process template
            String templateName = isGuest ? "email/guest-order-confirmation" : "email/customer-order-confirmation";
            String htmlContent = templateEngine.process(templateName, context);
            helper.setText(htmlContent, true);

            // Send email
            mailSender.send(message);
            log.info("Order confirmation email sent successfully to {} for order {}", customerEmail, order.getId());

        } catch (MessagingException e) {
            log.error("Failed to send order confirmation email to {} for order {}: {}",
                    customerEmail, order.getId(), e.getMessage());
        } catch (Exception e) {
            log.error("Unexpected error while sending email to {} for order {}: {}",
                    customerEmail, order.getId(), e.getMessage(), e);
        }
    }

    private String formatPaymentMethod(String method) {
        return switch (method) {
            case "CASH_ON_DELIVERY" -> "Thanh toán khi nhận hàng";
            case "BANK_TRANSFER" -> "Chuyển khoản ngân hàng";
            case "CREDIT_CARD" -> "Thẻ tín dụng";
            default -> method;
        };
    }

    private String formatOrderStatus(String status) {
        return switch (status) {
            case "PENDING_PAYMENT" -> "Chờ thanh toán";
            case "PROCESSING" -> "Đang xử lý";
            case "SHIPPING" -> "Đang giao hàng";
            case "DELIVERED" -> "Đã giao hàng";
            case "CANCELLED" -> "Đã hủy";
            default -> status;
        };
    }
}

