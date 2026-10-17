-- One fictitious customer order for the prototype demo. Prices are typical Zalando list prices, brands are placeholders.
insert into order_items (id, order_number, brand, title, category, size, price_cents, image_url, delivered_at) values
    ('0f3a1c2e-8c1b-4a7f-9e1d-1a2b3c4d5e01', '10512-7788-2041', 'Selected Homme', 'Slim Fit Anzughose, dunkelblau', 'TROUSERS', '50', 8995, null, '2026-10-12T09:30:00Z'),
    ('0f3a1c2e-8c1b-4a7f-9e1d-1a2b3c4d5e02', '10512-7788-2041', 'Hugo', 'Slim Fit Sakko, dunkelblau', 'BLAZERS', '50', 24995, null, '2026-10-12T09:30:00Z'),
    ('0f3a1c2e-8c1b-4a7f-9e1d-1a2b3c4d5e03', '10512-7788-2041', 'Vero Moda', 'Cocktailkleid, smaragd', 'DRESSES', '38', 7999, null, '2026-10-12T09:30:00Z'),
    ('0f3a1c2e-8c1b-4a7f-9e1d-1a2b3c4d5e04', '10512-7788-2041', 'Olymp', 'Businesshemd Level Five, weiß', 'SHIRTS', '40', 5995, null, '2026-10-12T09:30:00Z'),
    ('0f3a1c2e-8c1b-4a7f-9e1d-1a2b3c4d5e05', '10512-7788-2041', 'Nike', 'Air Max, weiß', 'SHOES', '43', 12999, null, '2026-10-12T09:30:00Z'),
    ('0f3a1c2e-8c1b-4a7f-9e1d-1a2b3c4d5e06', '10512-7788-2041', 'Pier One', 'T-Shirt Basic, 3er-Pack', 'SPORTSWEAR', 'M', 1999, null, '2026-10-12T09:30:00Z');
