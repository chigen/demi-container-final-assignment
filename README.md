# demi-container-final-assignment
## project name
order processing system

## implementing features
This project about a microservice-based architecture for a simple e-commerce system

- Service A (Order service): Receives order requests from the client and can be **accessed from outside the cluster (exposed port)**

- Service B (Inventory service): Called by Service A, used to check inventory and update product inventory

- User Account Management: Manage user registration, login, and profile information

- Product Catalog Service: Maintain information about available products (name, description, price, stock-keeping unit)

- Promotion Service: Manage discount campaigns, apply discounts to eligible orders

## API design

1. Inventory Service (B):
    - GET /inventory/:item_id: Query inventory
    - POST /inventory/reserve: Reserve inventory (request body: {item_id, quantity})

2. Order Service (A):
    - POST /order: Create an order (request body: {user_id, item_id, quantity}), internally call B's /inventory/reserve and Promotion Service
    - GET /orders/:order_id: Retrieve order details

3. User Service (C):
    - POST /users/register: Register a new user
    - POST /users/login: Authenticate a user
    - GET /users/:user_id: Retrieve user profile

4. Product Catalog Service (D):
    - GET /products: List all products
    - GET /products/:item_id: Retrieve details of a product

5. Promotion Service (E):
    - GET /promotions: List current promotions
    - POST /promotions/apply: Apply promotion to an order (request body: {user_id, item_id, quantity})

## DB schema

### Tables

- Users: user_id, username, password_hash, email, created_at
- Products: item_id, name, description, price, sku, stock
- Orders: order_id, user_id, item_id, quantity, total_price, created_at
- Promotions: promo_id, item_id, discount_percentage, start_date, end_date

## Technology stack
- Backend: java
- Container: k8s
- DB: Mysql

### k8s deployment 
- Apply the ArgoCD:
```kubectl apply -f argocd/application.yaml```

- Check the ArgoCD status
    ```
    kubectl get applications -n argocd
    NAME                      SYNC STATUS   HEALTH STATUS
    order-processing-system   Unknown       Healthy
    ```
- check commmands
    ```
    kubectl get pods -n demi
    kubectl get services -n demi
    kubectl get all -n demi
    ```
## Deploy process

code changes (github) -> build image (ghcr.io) -> update k8s deployment (manifest) -> push deployment changes to git (git repo) -> deploy into k8s (eks cluster)

## Services port
- Order service: 8080
- Inventory service: 8081
