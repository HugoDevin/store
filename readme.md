# Ecommerce Demo

一個可直接展示的電商平台 Demo，使用 **Spring Boot 3 + Thymeleaf + Spring Security + Spring Data JPA + H2 Database** 建立，提供：

- RESTful API
- Thymeleaf 頁面
- Guest / Buyer / Seller 三種使用情境
- H2 in-memory database 與初始化測試資料

---

## 技術棧

- Java 17
- Spring Boot 3
- Spring Web
- Spring Data JPA
- Spring Security
- Thymeleaf
- H2 Database
- Maven
- Bootstrap 5

---

## 專案特色

- **標準分層架構**：Controller / Service / Repository / DTO / Form DTO / Exception / Config
- **頁面與 API 共用 Service 層邏輯**
- **角色權限控制**：BUYER / SELLER
- **商品 soft delete**：刪除改為 `INACTIVE`
- **樂觀鎖**：`Product`、`Order` 皆使用 `@Version`
- **訂單快照欄位**：保留 `product_name`、`unit_price`
- **Payment Gateway 解耦**：目前支援貨到付款，信用卡保留擴充點
- **可直接 Demo**：系統啟動後自動建立測試帳號與商品

---

## 專案結構

```text
com.example.ecommerce
├── controller
│   ├── api
│   └── web
├── service
│   └── impl
├── repository
├── model
├── dto
│   ├── request
│   ├── response
│   └── form
├── payment
│   └── impl
├── exception
└── config
```

前端模板位於：

```text
src/main/resources/templates
├── auth
├── buyer
├── layout
├── product
└── seller
```

---

## 預設測試帳號

系統啟動後會自動建立以下使用者：

| 角色 | 帳號 | 密碼 |
|---|---|---|
| Buyer | `buyer1` | `password` |
| Seller | `seller1` | `password` |

---

## 初始化商品

由 `seller1` 自動建立以下商品：

1. `Keyboard` / Mechanical keyboard / 1999 / stock 10 / ACTIVE
2. `Mouse` / Gaming mouse / 899 / stock 5 / ACTIVE
3. `Monitor` / 27 inch monitor / 4999 / stock 0 / INACTIVE

---

## 啟動方式

### 1. 安裝需求

請先準備：

- JDK 17+
- Maven 3.9+

### 2. 啟動專案

```bash
mvn spring-boot:run
```

或先打包後執行：

```bash
mvn clean package
java -jar target/ecommerce-demo-0.0.1-SNAPSHOT.jar
```

### 3. 開啟網站

- 首頁 / 商品列表：http://localhost:8080/products
- 登入頁：http://localhost:8080/login
- 註冊頁：http://localhost:8080/register
- H2 Console：http://localhost:8080/h2-console

H2 連線資訊：

- JDBC URL: `jdbc:h2:mem:ecommerce`
- Username: `sa`
- Password: 空白

---

## 角色與功能

### Guest

- 可瀏覽商品列表
- 可查看商品詳情
- 不可下單
- 不可進入 Buyer / Seller 後台

### Buyer

- 可登入
- 可瀏覽商品
- 可建立訂單
- 可查看自己的訂單列表與訂單詳情
- 可取消自己的 `PENDING` 訂單

### Seller

- 可登入
- 可新增 / 編輯 / 上下架自己的商品
- 可查看與自己商品相關的訂單
- 可更新訂單狀態

---

## 頁面流程

### 公開頁面

- `/` -> 轉址到 `/products`
- `/products` 商品列表
- `/products/{id}` 商品詳情
- `/login` 登入頁
- `/register` 註冊頁

### Buyer 頁面

- `/buyer/checkout`
- `/buyer/orders`
- `/buyer/orders/{id}`

### Seller 頁面

- `/seller/products`
- `/seller/products/new`
- `/seller/products/{id}/edit`
- `/seller/orders`
- `/seller/orders/{id}`

---

## REST API

Base URL：`/api/v1`

### Auth API

- `POST /api/v1/auth/register`
- `POST /api/v1/auth/login`
- `POST /api/v1/auth/logout`

### Product API

- `GET /api/v1/products`
- `GET /api/v1/products/{pid}`
- `POST /api/v1/products`
- `PUT /api/v1/products/{pid}`
- `PATCH /api/v1/products/{pid}/status`
- `DELETE /api/v1/products/{pid}`

### Order API

- `POST /api/v1/orders`
- `GET /api/v1/orders`
- `GET /api/v1/orders/{oid}`
- `PATCH /api/v1/orders/{oid}/status`
- `DELETE /api/v1/orders/{oid}`

### API 回應格式

成功：

```json
{
  "code": 200,
  "message": "success",
  "data": {}
}
```

失敗：

```json
{
  "code": 400,
  "message": "錯誤描述",
  "data": null
}
```

---

## 商業規則

### 商品

- 商品刪除採 **soft delete**，實際上只改 `status=INACTIVE`
- 只有 Seller 可以管理自己的商品
- 公開商品列表只顯示 `ACTIVE` 商品

### 訂單

- 訂單建立時會寫入商品快照
- `total_amount` 由後端重新計算
- 訂單建立、明細寫入、庫存扣減在同一交易中處理
- 取消訂單僅限 `PENDING`

### 訂單狀態流轉

僅允許：

- `PENDING -> CONFIRMED`
- `CONFIRMED -> SHIPPED`
- `SHIPPED -> COMPLETED`
- `PENDING -> CANCELLED`（買家取消）

---

## PaymentGateway 設計

目前支援：

- `CASH_ON_DELIVERY`

已預留：

- `CREDIT_CARD`

`OrderService` 只依賴 `PaymentGateway` 介面，不直接依賴具體實作，便於後續擴充金流。

---

## 安全性設定

- 使用 `Spring Security 6`
- 使用 `BCryptPasswordEncoder`
- 頁面採 `formLogin + session`
- H2 Console 已允許 frame 顯示
- 登入成功後依角色導向：
  - Buyer -> `/products`
  - Seller -> `/seller/products`

---

## Demo 限制說明

本專案以展示完整流程為優先，因此 Seller 訂單列表採簡化模型：

- **賣家可看到「包含自己商品的整張訂單」**
- 尚未實作多賣家拆單（sub-order）模型

此限制已在 Seller 訂單頁面中標示，方便面試或展示時說明。

---

## 建議展示流程

1. 使用 Guest 角色查看商品
2. 註冊或登入 Buyer 帳號
3. 從商品詳情進入結帳並建立訂單
4. 查看 Buyer 訂單列表 / 訂單詳情
5. 登出並登入 Seller 帳號
6. 管理商品狀態
7. 打開 Seller 訂單管理並更新訂單狀態

---

## 後續可擴充方向

- JWT 驗證完整化
- 購物車功能
- 多商品結帳
- 圖片上傳
- 多賣家拆單
- 真實信用卡 / 第三方金流串接
- 單元測試與整合測試補齊
- Docker 化部署

