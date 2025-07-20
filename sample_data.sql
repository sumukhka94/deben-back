-- Sample data for expense splitting application

-- Insert users
INSERT INTO users (id, name, email) VALUES 
(1, 'Alice', 'alice@example.com'),
(2, 'Bob', 'bob@example.com'),
(3, 'Charlie', 'charlie@example.com'),
(4, 'Diana', 'diana@example.com');

-- Insert groups
INSERT INTO `groups` (id, name, description, created_by, created_at) VALUES 
(1, 'Weekend Trip', 'Our amazing weekend getaway', 1, '2024-01-15 10:00:00'),
(2, 'Office Lunch', 'Weekly team lunch expenses', 2, '2024-01-20 12:00:00'),
(3, 'Roommates', 'Shared apartment expenses', 3, '2024-01-10 09:00:00');

-- Insert group members
INSERT INTO group_members (group_id, user_id) VALUES 
(1, 1), (1, 2), (1, 3),
(2, 1), (2, 2), (2, 4),
(3, 2), (3, 3), (3, 4);

-- Insert expenses
INSERT INTO expenses (id, group_id, title, description, total_amount, paid_at) VALUES 
(1, 1, 'Hotel Booking', 'Two nights at mountain resort', 300.00, '2024-01-15 14:00:00'),
(2, 1, 'Gas & Tolls', 'Road trip fuel and highway tolls', 80.00, '2024-01-15 16:00:00'),
(3, 1, 'Groceries', 'Food and drinks for the trip', 120.00, '2024-01-16 10:00:00'),
(4, 2, 'Pizza Lunch', 'Team pizza party', 45.00, '2024-01-22 13:00:00'),
(5, 3, 'Electricity Bill', 'Monthly electricity payment', 150.00, '2024-01-25 09:00:00');

-- Insert expense payers
INSERT INTO expense_payers (expense_id, user_id, amount_paid) VALUES 
(1, 1, 300.00),
(2, 2, 80.00),
(3, 3, 120.00),
(4, 2, 45.00),
(5, 3, 150.00);

-- Insert expense splits
INSERT INTO expense_splits (expense_id, user_id, amount_owed) VALUES 
-- Hotel split equally among 3 people
(1, 1, 100.00), (1, 2, 100.00), (1, 3, 100.00),
-- Gas split equally among 3 people
(2, 1, 26.67), (2, 2, 26.67), (2, 3, 26.66),
-- Groceries split equally among 3 people
(3, 1, 40.00), (3, 2, 40.00), (3, 3, 40.00),
-- Pizza split equally among 3 people (Alice, Bob, Diana)
(4, 1, 15.00), (4, 2, 15.00), (4, 4, 15.00),
-- Electricity split equally among 3 roommates
(5, 2, 50.00), (5, 3, 50.00), (5, 4, 50.00);

-- Insert settlements
INSERT INTO settlements (id, group_id, from_user_id, to_user_id, amount, description, settled_at) VALUES 
(1, 1, 2, 1, 73.33, 'Bob pays Alice for hotel difference', '2024-01-17 10:00:00'),
(2, 1, 3, 2, 13.34, 'Charlie pays Bob for gas difference', '2024-01-17 11:00:00'),
(3, 2, 1, 2, 15.00, 'Alice pays Bob for pizza', '2024-01-23 14:00:00'),
(4, 3, 2, 3, 50.00, 'Bob pays Charlie for electricity', '2024-01-26 16:00:00');