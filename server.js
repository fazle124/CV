const express = require('express');
const mysql = require('mysql2');
const cors = require('cors');
require('dotenv').config();

const app = express();
app.use(cors());
app.use(express.json());

// Aiven MySQL Database Connection
const db = mysql.createConnection({
    host: process.env.DB_HOST,
    port: process.env.DB_PORT,
    user: process.env.DB_USER,
    password: process.env.DB_PASSWORD,
    database: process.env.DB_NAME,
    ssl: { rejectUnauthorized: false }
});

db.connect(err => {
    if (err) {
        console.error('MySQL Connection Error:', err);
    } else {
        console.log('Connected to Aiven MySQL Database!');
        
        // অটোমেটিক Admin ও CV টেবিল তৈরি
        db.query(`CREATE TABLE IF NOT EXISTS admin (
            id INT AUTO_INCREMENT PRIMARY KEY,
            phone VARCHAR(20) NOT NULL UNIQUE,
            password VARCHAR(255) NOT NULL
        )`);

        db.query(`INSERT IGNORE INTO admin (id, phone, password) VALUES (1, '01720094069', 'Rabbi+AA')`);

        db.query(`CREATE TABLE IF NOT EXISTS cv_info (
            id INT PRIMARY KEY DEFAULT 1,
            name VARCHAR(100),
            phone VARCHAR(20),
            email VARCHAR(100),
            linkedin VARCHAR(250),
            objective TEXT
        )`);
    }
});

// Admin Login API Route
app.post('/api/login', (req, res) => {
    const { phone, password } = req.body;
    db.query('SELECT * FROM admin WHERE phone = ? AND password = ?', [phone, password], (err, results) => {
        if (err) return res.status(500).json({ success: false, message: 'Database error' });
        if (results.length > 0) {
            res.json({ success: true, message: 'Login successful' });
        } else {
            res.status(401).json({ success: false, message: 'Invalid phone or password' });
        }
    });
});

const PORT = process.env.PORT || 5000;
app.listen(PORT, () => console.log(`Server running on port ${PORT}`));
