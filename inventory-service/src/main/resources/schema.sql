-- Create inventory table for H2 database
CREATE TABLE IF NOT EXISTS inventory (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    item_id VARCHAR(255) NOT NULL UNIQUE,
    stock INT NOT NULL,
    reserved_stock INT NOT NULL DEFAULT 0,
    available_stock INT NOT NULL
);

-- Create index for better performance
CREATE INDEX IF NOT EXISTS idx_item_id ON inventory(item_id); 
