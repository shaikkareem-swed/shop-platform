#!/usr/bin/env bash
# Exercises the whole saga through the API gateway.  Usage: bash scripts/test-flow.sh [http://localhost:8080]
set -euo pipefail
GW="${1:-http://localhost:8080}"
j() { curl -s -H 'Content-Type: application/json' "$@"; echo; }

echo "--- product";  j -X POST $GW/api/products -d '{"name":"Running Shoe","category":"Footwear","description":"Light road shoe"}'
echo "--- article";  j -X POST $GW/api/articles -d '{"productId":1,"sku":"SHOE-42-BLK","price":89.90,"description":"Size 42 black"}'
echo "--- stock=10"; j -X PUT  $GW/api/inventory/1 -d '{"quantity":10}'
echo "--- order qty 2 (expect CONFIRMED)"
j -X POST $GW/api/orders -d '{"customerId":"alice","articleId":1,"quantity":2}'
sleep 3
j "$GW/api/orders?customerId=alice"
echo "--- inventory after reservation"; j $GW/api/inventory/1
echo "--- order qty 100 (expect CANCELLED - insufficient stock)"
j -X POST $GW/api/orders -d '{"customerId":"bob","articleId":1,"quantity":100}'
sleep 3
j "$GW/api/orders?customerId=bob"
echo "--- validation error (RFC 9457 ProblemDetail)"; j -X POST $GW/api/orders -d '{"customerId":"","quantity":0}'
echo "--- not found"; j $GW/api/articles/9999
