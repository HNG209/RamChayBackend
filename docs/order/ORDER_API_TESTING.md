# 🧪 HƯỚNG DẪN TEST ORDER API

## Chuẩn Bị

### 1. Đảm bảo đã có:
- ✅ Database đang chạy
- ✅ Spring Boot application đang chạy
- ✅ Đã đăng ký và đăng nhập (có JWT token)
- ✅ Đã thêm sản phẩm vào Cart

### 2. Lấy JWT Token
```http
POST /auth/login
Content-Type: application/json

{
    "username": "customer@example.com",
    "password": "password123"
}
```

Response sẽ có `token`. Copy token này để dùng cho các request sau.

---

## API Testing

### ✅ Test 1: Tạo Order (Checkout)

**Request:**
```http
POST http://localhost:8080/orders
Authorization: Bearer YOUR_JWT_TOKEN_HERE
Content-Type: application/json

{
    "shippingAddress": "123 Nguyễn Văn Linh, Quận 7, TP.HCM",
    "customerPhone": "0901234567",
    "paymentMethod": "COD"
}
```

**Expected Result:**
```json
{
    "code": 1000,
    "result": {
        "id": 1,
        "orderDate": "2025-11-26T...",
        "total": 500000.0,
        "orderStatus": "PENDING_PAYMENT",
        "paymentMethod": "COD",
        "shippingAddress": "123 Nguyễn Văn Linh, Quận 7, TP.HCM",
        "customerPhone": "0901234567",
        "orderDetails": [
            {
                "id": 1,
                "quantity": 2,
                "unitPrice": 150000.0,
                "product": {
                    "id": 1,
                    "name": "Sản phẩm A",
                    "price": 150000.0
                }
            }
        ]
    }
}
```

**Kiểm tra:**
- ✅ Response có `orderDetails` không null
- ✅ `total` được tính đúng (quantity × unitPrice)
- ✅ `orderStatus` = "PENDING_PAYMENT"
- ✅ `orderDate` có giá trị
- ✅ Vào database kiểm tra:
  - Bảng `orders` có record mới
  - Bảng `order_details` có records tương ứng
  - Bảng `products` có `stock` giảm
  - Bảng `cart_items` đã bị xóa

---

### ✅ Test 2: Xem Tất Cả Orders Của Tôi

**Request:**
```http
GET http://localhost:8080/orders
Authorization: Bearer YOUR_JWT_TOKEN_HERE
```

**Expected Result:**
```json
{
    "code": 1000,
    "result": [
        {
            "id": 3,
            "orderDate": "2025-11-26T15:00:00",
            "total": 800000.0,
            "orderStatus": "SHIPPING",
            ...
        },
        {
            "id": 2,
            "orderDate": "2025-11-25T10:30:00",
            "total": 500000.0,
            "orderStatus": "COMPLETED",
            ...
        }
    ]
}
```

**Kiểm tra:**
- ✅ Trả về list các orders
- ✅ Orders sắp xếp theo thời gian (mới nhất trước)
- ✅ Chỉ hiển thị orders của user đang đăng nhập

---

### ✅ Test 3: Xem Chi Tiết 1 Order

**Request:**
```http
GET http://localhost:8080/orders/1
Authorization: Bearer YOUR_JWT_TOKEN_HERE
```

**Expected Result:**
```json
{
    "code": 1000,
    "result": {
        "id": 1,
        "orderDate": "2025-11-26T10:30:00",
        "total": 500000.0,
        "orderStatus": "PENDING_PAYMENT",
        "orderDetails": [...]
    }
}
```

**Kiểm tra:**
- ✅ Trả về đầy đủ thông tin order
- ✅ Có đầy đủ orderDetails

---

### ❌ Test 4: Cart Trống (Negative Test)

**Setup:** Xóa hết sản phẩm trong cart

**Request:** Same as Test 1

**Expected Result:**
```json
{
    "code": 4006,
    "message": "cart is empty"
}
```

---

### ❌ Test 5: Sản Phẩm Hết Hàng (Negative Test)

**Setup:** 
1. Thêm sản phẩm vào cart với quantity = 10
2. Update product.stock = 5 trong database

**Request:** Same as Test 1

**Expected Result:**
```json
{
    "code": 4007,
    "message": "product out of stock"
}
```

**Kiểm tra:**
- ✅ Order KHÔNG được tạo
- ✅ Stock KHÔNG bị giảm
- ✅ Cart KHÔNG bị xóa

---

### ❌ Test 6: Xem Order Không Phải Của Mình (Security Test)

**Setup:**
1. User A tạo order (id=1)
2. User B đăng nhập (lấy token mới)

**Request:**
```http
GET http://localhost:8080/orders/1
Authorization: Bearer USER_B_JWT_TOKEN
```

**Expected Result:**
```json
{
    "code": 4008,
    "message": "order not found"
}
```

---

## 🔧 Postman Collection

### Environment Variables
```
base_url: http://localhost:8080
jwt_token: (paste your token here)
```

### Request Headers
```
Authorization: Bearer {{jwt_token}}
Content-Type: application/json
```

---

## 📊 Database Verification

### Kiểm tra Order được tạo
```sql
SELECT * FROM orders ORDER BY order_id DESC LIMIT 1;
```

### Kiểm tra OrderDetail
```sql
SELECT od.*, p.name, p.price 
FROM order_details od
JOIN products p ON od.product_id = p.product_id
WHERE od.order_id = 1;
```

### Kiểm tra Stock giảm
```sql
-- Trước khi đặt hàng
SELECT product_id, name, stock FROM products WHERE product_id = 1;

-- Sau khi đặt hàng (stock phải giảm)
SELECT product_id, name, stock FROM products WHERE product_id = 1;
```

### Kiểm tra Cart đã xóa
```sql
-- Sau khi đặt hàng, cart_items phải rỗng
SELECT * FROM cart_items WHERE cart_id IN (
    SELECT cart_id FROM carts WHERE customer_id = 1
);
```

---

## 🐛 Troubleshooting

### Lỗi: "cart not found"
- Kiểm tra customer đã có cart chưa
- Kiểm tra JWT token có đúng không

### Lỗi: "cart is empty"
- Thêm sản phẩm vào cart trước
- Gọi API: POST /cart-items

### Lỗi: "product out of stock"
- Kiểm tra product.stock trong database
- Update stock nếu cần: `UPDATE products SET stock = 100 WHERE product_id = 1;`

### Lỗi: "order not found"
- Kiểm tra orderId có đúng không
- Kiểm tra order có thuộc về customer đang đăng nhập không

### Lỗi: "unauthenticated"
- JWT token hết hạn → đăng nhập lại
- JWT token sai format → kiểm tra header Authorization

---

## ✅ Success Criteria

Sau khi test thành công:

1. ✅ Có thể tạo order từ cart
2. ✅ Order có đầy đủ orderDetails
3. ✅ Total được tính đúng
4. ✅ orderStatus = PENDING_PAYMENT
5. ✅ orderDate được set tự động
6. ✅ Stock giảm khi đặt hàng
7. ✅ Cart bị xóa sau khi checkout
8. ✅ Có thể xem danh sách orders
9. ✅ Có thể xem chi tiết 1 order
10. ✅ Không thể xem order của người khác

**Nếu tất cả đều pass → Implementation hoàn tất! 🎉**

