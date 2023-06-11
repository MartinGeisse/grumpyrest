
curl http://localhost:8080/categories/0

curl http://localhost:8080/products/0

curl -X POST -H 'Content-Type: application/json' --data-binary '{"productId": 7, "quantity": 2}' http://localhost:8080/cart/0/add

curl http://localhost:8080/cart/0
