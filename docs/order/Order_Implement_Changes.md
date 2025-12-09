# TRIỂN KHAI TẠO ORDER DETAIL TỪ CART

## ✅ CÁC THAY ĐỔI ĐÃ THỰC HIỆN

### 1. Tạo OrderDetailRepository
**File**: `OrderDetailRepository.java`
- Tạo repository mới để quản lý OrderDetail
- Kế thừa JpaRepository<OrderDetail, Long>

### 2. Thêm Error Codes Mới
**File**: `ErrorCode.java`
- `CART_NOT_FOUND(4005)`: Không tìm thấy giỏ hàng
- `CART_EMPTY(4006)`: Giỏ hàng trống
- `PRODUCT_OUT_OF_STOCK(4007)`: Sản phẩm hết hàng

### 3. Cập Nhật Order Entity
**File**: `Order.java`
- Thêm method `@PrePersist onCreate()`:
    - Tự động set `orderDate = LocalDateTime.now()`
    - Tự động set `orderStatus = PENDING_PAYMENT` nếu null

### 4. Cập Nhật OrderServiceImpl - LOGIC CHÍNH
**File**: `OrderServiceImpl.java`

#### Dependencies Mới:
```java
- CartRepository cartRepository
- CartItemRepository cartItemRepository
```

#### Logic Tạo Order Hoàn Chỉnh:

**Bước 1: Lấy Cart của Customer**
```java
Cart cart = cartRepository.findCartByCustomerId(customerId);
if (cart == null) {
    throw new AppException(ErrorCode.CART_NOT_FOUND);
}
```

**Bước 2: Kiểm Tra Cart Không Rỗng**
```java
List<CartItem> cartItems = cart.getCartItems();
if (cartItems == null || cartItems.isEmpty()) {
    throw new AppException(ErrorCode.CART_EMPTY);
}
```

**Bước 3: Tạo Order với Thông Tin Cơ Bản**
```java
Order order = Order.builder()
    .customer(Customer.builder().id(customerId).build())
    .shippingAddress(request.getShippingAddress())
    .customerPhone(request.getCustomerPhone())
    .paymentMethod(request.getPaymentMethod())
    .orderStatus(OrderStatus.PENDING_PAYMENT)
    .build();
```

**Bước 4: Chuyển Đổi CartItem → OrderDetail**
```java
List<OrderDetail> orderDetails = new ArrayList<>();
double total = 0.0;

for (CartItem cartItem : cartItems) {
    Product product = cartItem.getProduct();
    
    // Validate product tồn tại
    if (product == null) {
        throw new AppException(ErrorCode.PRODUCT_NOT_FOUND);
    }
    
    // Kiểm tra tồn kho
    if (product.getStock() < cartItem.getQuantity()) {
        log.error("Product {} is out of stock. Available: {}, Requested: {}", 
            product.getName(), product.getStock(), cartItem.getQuantity());
        throw new AppException(ErrorCode.PRODUCT_OUT_OF_STOCK);
    }

    // Tạo OrderDetail
    OrderDetail orderDetail = OrderDetail.builder()
        .order(order)
        .product(product)
        .quantity(cartItem.getQuantity())
        .unitPrice(product.getPrice()) // Giá hiện tại
        .build();

    orderDetails.add(orderDetail);
    
    // Tính tổng tiền
    total += orderDetail.getQuantity() * orderDetail.getUnitPrice();

    // Giảm số lượng trong kho
    product.setStock(product.getStock() - cartItem.getQuantity());
}
```

**Bước 5: Set OrderDetails và Total**
```java
order.setOrderDetails(orderDetails);
order.setTotal(total);
```

**Bước 6: Lưu Order (Cascade Tự Động Lưu OrderDetail)**
```java
Order savedOrder = orderRepository.save(order);
```

**Bước 7: Xóa CartItem Đã Checkout**
```java
cartItemRepository.deleteAll(cartItems);
```

**Bước 8: Log và Trả Về Response**
```java
log.info("Order created successfully. OrderId: {}, Total: {}, Items: {}", 
    savedOrder.getId(), savedOrder.getTotal(), orderDetails.size());

return orderMapper.toOrderCreationResponse(savedOrder);
```

---

## 🎯 TÍNH NĂNG ĐÃ HOÀN THÀNH

### ✅ Tạo OrderDetail từ Cart
- Lấy tất cả CartItem của customer
- Chuyển đổi mỗi CartItem thành OrderDetail
- Liên kết OrderDetail với Order (quan hệ One-to-Many)

### ✅ Tính Tổng Tiền (Total)
- Tính total = Σ(quantity × unitPrice) của tất cả OrderDetail
- Lưu total vào Order

### ✅ Set Trạng Thái và Thời Gian
- `orderStatus` = PENDING_PAYMENT (mặc định)
- `orderDate` = thời gian hiện tại (auto-set bởi @PrePersist)

### ✅ Validate Business Logic
- Kiểm tra Cart tồn tại
- Kiểm tra Cart không rỗng
- Kiểm tra Product tồn tại
- Kiểm tra Product còn đủ hàng trong kho

### ✅ Quản Lý Tồn Kho
- Tự động giảm số lượng sản phẩm trong kho khi đặt hàng
- `product.stock = product.stock - quantity`

### ✅ Xóa Cart Sau Khi Checkout
- Tự động xóa tất cả CartItem sau khi tạo Order thành công
- Giỏ hàng được làm sạch để sẵn sàng cho lần mua tiếp theo

### ✅ Transaction Management
- Sử dụng `@Transactional` để đảm bảo tính toàn vẹn dữ liệu
- Nếu có lỗi → rollback toàn bộ (không tạo Order, không giảm stock, không xóa Cart)

### ✅ Logging
- Log thông tin Order được tạo thành công
- Log chi tiết lỗi khi sản phẩm hết hàng

---

## 🧪 CÁCH TEST

### Test Case 1: Tạo Order Thành Công
**Điều kiện:**
1. Customer đã đăng nhập (có JWT token)
2. Cart có ít nhất 1 sản phẩm
3. Tất cả sản phẩm còn đủ hàng

**Request:**
```http
POST /orders
Authorization: Bearer {JWT_TOKEN}
Content-Type: application/json

{
    "shippingAddress": "123 Nguyễn Văn Linh, Quận 7, TP.HCM",
    "customerPhone": "0901234567",
    "paymentMethod": "COD"
}
```

**Expected Response:**
```json
{
    "code": 1000,
    "message": "Success",
    "result": {
        "id": 1,
        "orderDate": "2025-11-26T10:30:00",
        "total": 500000.0,
        "orderStatus": "PENDING_PAYMENT",
        "paymentMethod": "COD",
        "shippingAddress": "123 Nguyễn Văn Linh, Quận 7, TP.HCM",
        "customerPhone": "0901234567",
        "customer": {
            "id": 1,
            "fullName": "Nguyễn Văn A",
            ...
        },
        "orderDetails": [
            {
                "id": 1,
                "quantity": 2,
                "unitPrice": 150000.0,
                "product": {
                    "id": 1,
                    "name": "Sản phẩm A",
                    ...
                }
            },
            {
                "id": 2,
                "quantity": 1,
                "unitPrice": 200000.0,
                "product": {
                    "id": 2,
                    "name": "Sản phẩm B",
                    ...
                }
            }
        ]
    }
}
```

**Kiểm tra:**
- ✅ Order được tạo với ID
- ✅ orderDetails có 2 items
- ✅ total = 2×150000 + 1×200000 = 500000
- ✅ orderStatus = PENDING_PAYMENT
- ✅ orderDate được set tự động
- ✅ Product stock giảm (Product 1: stock-2, Product 2: stock-1)
- ✅ Cart items bị xóa

### Test Case 2: Cart Trống
**Request:** Same as above

**Expected Response:**
```json
{
    "code": 4006,
    "message": "cart is empty"
}
```

### Test Case 3: Sản Phẩm Hết Hàng
**Điều kiện:** Cart có sản phẩm với quantity > product.stock

**Expected Response:**
```json
{
    "code": 4007,
    "message": "product out of stock"
}
```

**Kiểm tra:**
- ✅ Order KHÔNG được tạo
- ✅ Stock KHÔNG bị giảm
- ✅ Cart items KHÔNG bị xóa (rollback)

### Test Case 4: Cart Không Tồn Tại
**Điều kiện:** Customer chưa có cart

**Expected Response:**
```json
{
    "code": 4005,
    "message": "cart not found"
}
```

---

## 📊 SO SÁNH TRƯỚC VÀ SAU

### ❌ TRƯỚC ĐÂY:
```json
{
    "id": 1,
    "orderDate": null,
    "total": 0.0,
    "orderStatus": null,
    "paymentMethod": "COD",
    "shippingAddress": "...",
    "customerPhone": "...",
    "customer": {...},
    "orderDetails": null  // ❌ KHÔNG CÓ SẢN PHẨM
}
```

### ✅ SAU KHI SỬA:
```json
{
    "id": 1,
    "orderDate": "2025-11-26T10:30:00",  // ✅ Có thời gian
    "total": 500000.0,                    // ✅ Có tổng tiền
    "orderStatus": "PENDING_PAYMENT",     // ✅ Có trạng thái
    "paymentMethod": "COD",
    "shippingAddress": "...",
    "customerPhone": "...",
    "customer": {...},
    "orderDetails": [                     // ✅ CÓ SẢN PHẨM
        {
            "id": 1,
            "quantity": 2,
            "unitPrice": 150000.0,
            "product": {...}
        }
    ]
}
```

---

## 🔄 FLOW HOÀN CHỈNH

```
1. User thêm sản phẩm vào Cart
   └─> CartItem được tạo

2. User xem Cart và nhấn "Checkout"
   └─> Gọi POST /orders

3. Backend xử lý:
   ├─> Validate Cart tồn tại
   ├─> Validate Cart không rỗng
   ├─> Validate sản phẩm còn hàng
   ├─> Tạo Order
   ├─> Tạo OrderDetail từ CartItem
   ├─> Tính total
   ├─> Giảm stock
   ├─> Lưu Order (cascade save OrderDetail)
   └─> Xóa CartItem

4. Response trả về Order hoàn chỉnh
   └─> Frontend hiển thị thông tin đơn hàng

5. Cart của user bây giờ trống
   └─> Sẵn sàng cho lần mua tiếp theo
```

---

## ⚠️ LƯU Ý

### Transaction Rollback
- Nếu bất kỳ bước nào fail → toàn bộ rollback
- Order KHÔNG được tạo nếu có lỗi
- Stock KHÔNG bị giảm nếu rollback
- CartItem KHÔNG bị xóa nếu rollback

### Concurrency Issues (Vấn đề đồng thời)
- Nếu 2 user cùng mua 1 sản phẩm cuối cùng → cần xử lý thêm
- Có thể dùng optimistic locking hoặc pessimistic locking
- Hiện tại: first-come-first-served (ai gọi API trước được mua)

### Price Snapshot
- `unitPrice` lưu giá tại thời điểm mua
- Nếu sau này Product price thay đổi → OrderDetail vẫn giữ giá cũ
- Đúng với logic kinh doanh

---

## 🎉 KẾT QUẢ

**Vấn đề "Tạo OrderDetail từ Cart" đã được giải quyết hoàn toàn!**

✅ Order có OrderDetail
✅ Order có total được tính chính xác
✅ Order có orderStatus và orderDate
✅ Validate đầy đủ
✅ Quản lý tồn kho
✅ Transaction safety
✅ Cart cleanup sau checkout

**Hệ thống bây giờ có thể:**
- Tạo đơn hàng hoàn chỉnh từ giỏ hàng
- Theo dõi sản phẩm trong đơn hàng
- Quản lý tồn kho tự động
- Xử lý lỗi nghiệp vụ đúng cách
