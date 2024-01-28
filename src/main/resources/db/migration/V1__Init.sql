CREATE TABLE IF NOT EXISTS products (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    image_url VARCHAR(255),
    quantity INT NOT NULL,
    price DECIMAL(10,2) NOT NULL,
    category VARCHAR(255),
    manufacturer VARCHAR(255),
    created_at TIMESTAMP DEFAULT NOW() NOT NULL
);
