## mstuser

| Item name | Key | Type | Length | Necessary |
|---|---|---|---|---|
| psn_cd | PK | integer | 4 | x |
| user_id | | character | 8 | |
| username | | character | 8 | |
| password | | character | 255 | |
| role | | smallint | 2 | |
| status | | bit | 1 | |
| deletetime | | timestamp | | |
| createtime | | timestamp | | |
| create_psn_cd | FK | integer | 4 | |
| updatetime | | timestamp | | |
| update_psn_cd | FK | integer | 4 | |

## mstproducttype

| Item name | Key | Type | Length | Necessary |
|---|---|---|---|---|
| producttype_id | PK | integer | 4 | x |
| name | | varchar | 200 | |
| status | | bit | 1 | |
| createtime | | timestamp | | |
| create_user | FK | integer | 4 | |
| updatetime | | timestamp | | |
| update_user | FK | integer | 4 | |

## mstproduct

| Item name | Key | Type | Length | Necessary |
|---|---|---|---|---|
| product_id | PK | bigint | 8 | x |
| product_name | | varchar | 200 | |
| status | | bit | 1 | |
| description | | varchar | 400 | |
| product_img | | varchar | 500 | |
| product_amount | | integer | 4 | |
| price | | bigint | 8 | |
| producttype_id | FK | integer | 4 | |
| createtime | | timestamp | | |
| create_user | FK | integer | 4 | |
| updatetime | | timestamp | | |
| update_user | FK | integer | 4 | |

## trproductorder

| Item name | Key | Type | Length | Necessary |
|---|---|---|---|---|
| id | PK | bigint | 8 | x |
| custom_name | | varchar | 200 | |
| order_product_id | FK | bigint | 8 | |
| order_product_amount | | integer | 4 | |
| unit_price | | bigint | 8 | |
| total_price | | bigint | 8 | |
| order_status | | varchar | 50 | |
| order_delivery_address | | varchar | 400 | |
| order_delivery_date | | timestamp | | |
| createtime | | timestamp | | |
| create_user | FK | integer | 4 | |
| updatetime | | timestamp | | |
| update_user | FK | integer | 4 | |