-- Seed Hong Kong merchants and sample HKD settlement orders

INSERT INTO merchants (merchant_code, name, district, tier, contact_email)
SELECT 'CYM-HK-001', 'Cymbal Flagship Causeway Bay', 'Causeway Bay', 'VIP', 'cwb-ops@cymbalhk.example'
WHERE NOT EXISTS (SELECT 1 FROM merchants WHERE merchant_code = 'CYM-HK-001');

INSERT INTO merchants (merchant_code, name, district, tier, contact_email)
SELECT 'CYM-HK-002', 'Harbour City Express Kiosk', 'Tsim Sha Tsui', 'STANDARD', 'tst-kiosk@cymbalhk.example'
WHERE NOT EXISTS (SELECT 1 FROM merchants WHERE merchant_code = 'CYM-HK-002');

INSERT INTO merchants (merchant_code, name, district, tier, contact_email)
SELECT 'CYM-HK-003', 'New Town Plaza Logistics Hub', 'Sha Tin', 'VIP', 'shatin-hub@cymbalhk.example'
WHERE NOT EXISTS (SELECT 1 FROM merchants WHERE merchant_code = 'CYM-HK-003');

INSERT INTO merchants (merchant_code, name, district, tier, contact_email)
SELECT 'CYM-HK-004', 'Central IFC Pop-Up Counter', 'Central', 'STANDARD', 'ifc-counter@cymbalhk.example'
WHERE NOT EXISTS (SELECT 1 FROM merchants WHERE merchant_code = 'CYM-HK-004');

INSERT INTO merchant_orders (order_ref, merchant_id, amount_hkd, fee_hkd, net_payout_hkd, status, created_by)
SELECT 'ORD-2016-1001', 1, 68000.00, 1224.00, 66776.00, 'SETTLED', 'opsadmin'
WHERE NOT EXISTS (SELECT 1 FROM merchant_orders WHERE order_ref = 'ORD-2016-1001');

INSERT INTO merchant_orders (order_ref, merchant_id, amount_hkd, fee_hkd, net_payout_hkd, status, created_by)
SELECT 'ORD-2016-1002', 2, 4200.00, 105.00, 4095.00, 'APPROVED', 'opsadmin'
WHERE NOT EXISTS (SELECT 1 FROM merchant_orders WHERE order_ref = 'ORD-2016-1002');

INSERT INTO merchant_orders (order_ref, merchant_id, amount_hkd, fee_hkd, net_payout_hkd, status, created_by)
SELECT 'ORD-2016-1003', 3, 25000.00, 450.00, 24550.00, 'PENDING', 'opsadmin'
WHERE NOT EXISTS (SELECT 1 FROM merchant_orders WHERE order_ref = 'ORD-2016-1003');

INSERT INTO merchant_orders (order_ref, merchant_id, amount_hkd, fee_hkd, net_payout_hkd, status, created_by)
SELECT 'ORD-2016-1004', 4, 400.00, 15.00, 385.00, 'PENDING', 'opsadmin'
WHERE NOT EXISTS (SELECT 1 FROM merchant_orders WHERE order_ref = 'ORD-2016-1004');
