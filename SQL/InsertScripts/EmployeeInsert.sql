INSERT INTO ehotels.logincred VALUES
(1, 'SYSTEM@EHOTELS.COM', '123admin'),
(2, 'Doe@managers.com', '123password'),
(3, 'Smith@managers.com', '123password'),
(4, 'Tremblay@managers.com', '123password'),
(5, 'John@employees.com', '123password'),
(6, 'Jane@employees.com', '123password'),
(7, 'Samir@employees.com', '123password');

INSERT INTO ehotels.employee VALUES
(1, '999999999', 'SYSTEM', 'SYSTEM', 'SYSTEM', 1, 'SYSTEM', 'SYSTEM', 'SYSTEM', 'Z9Z9Z9', 1),
(2, '437110039', 'Doe', '', 'John', 123, 'Olive Street', 'Toronto', 'Ontario', 'A5A0V7', 2),
(3, '313823442', 'Smith', 'M', 'Mike', 456, '10th avenue', 'Toronto', 'Ontario', 'A5A0B5', 3),
(4, '478830650', 'Tremblay', '', 'Michelle', 789, 'Random Street', 'Toronto', 'Ontario', 'A5A7B6', 4),
(5, '773943687', 'Marrow', '', 'John', 87, 'Olive Street', 'Toronto', 'Ontario', 'A5A8N3', 5),
(6, '815530860', 'Fletcher', '', 'Jane', 123, 'Conqueror Blvd', 'Toronto', 'Ontario', 'A5A8C2', 6),
(7, '416426592', 'Maalouf', '', 'Samir', 98, 'Random Street', 'Toronto', 'Ontario', 'A5A8S4', 7);

INSERT INTO ehotels.employeerole(employeeid, role) VALUES
(1, 'Manager'),
(1, 'System'),
(2, 'Manager'),
(3, 'Manager'),
(4, 'Manager'),
(5, 'Receptionnist'),
(6, 'Concierge'),
(7, 'Receptionnist');