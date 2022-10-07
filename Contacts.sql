CREATE TABLE contacts(
	Name VARCHAR NOT NULL,
	id SERIAL,
	Phone_Number VARCHAR NOT NULL,
	Telegram_UserName VARCHAR,
	PRIMARY KEY(id)
	
);
INSERT into contacts(Name, Phone_Number, Telegram_UserName) values('Sergiy', '11111111', '@invmm');
UPDATE contacts set Name = 'Sergiy Fatkula213y' where id = 1;
DELETE from contacts where id = 1;
DROP TABLE contacts;
