INSERT INTO ehotels.logincred VALUES
(1, 'Doe@managers.com', '123password'),
(2, 'Smith@managers.com', '123password'),
(3, 'Tremblay@managers.com', '123password'),
(4, 'John@employees.com', '123password'),
(5, 'Jane@employees.com', '123password'),
(6, 'Samir@employees.com', '123password'),
(999, 'SYSTEM@EHOTELS.COM', '123admin');

INSERT INTO ehotels.employee VALUES
(1, '123456', 'Doe', '', 'John', 123, 'Olive Street', 'Toronto', 'Ontario', 'A5A0V7', 1),
(2, '789123', 'Smith', 'M', 'Mike', 456, '10th avenue', 'Toronto', 'Ontario', 'A5A0B5', 2),
(3, '456789', 'Tremblay', '', 'Michelle', 789, 'Random Street', 'Toronto', 'Ontario', 'A5A7B6', 3),
(4, '678034', 'Marrow', '', 'John', 87, 'Olive Street', 'Toronto', 'Ontario', 'A5A8N3', 4),
(5, '263947', 'Fletcher', '', 'Jane', 123, 'Conqueror Blvd', 'Toronto', 'Ontario', 'A5A8C2', 5),
(6, '859673', 'Maalouf', '', 'Samir', 98, 'Random Street', 'Toronto', 'Ontario', 'A5A8S4', 6),
(999, '999999', 'SYSTEM', '', '', 999, 'SYSTEM', 'SYSTEM', 'SYSTEM', '999999', 999);

INSERT INTO ehotels.employeerole(employeeid, role) VALUES
(1, 'Manager'),
(2, 'Manager'),
(3, 'Manager'),
(4, 'Receptionnist'),
(5, 'Concierge'),
(6, 'Receptionnist'),
(999, 'Manager'),
(999, 'System');