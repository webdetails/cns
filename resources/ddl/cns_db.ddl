

create table if not exists notifications (
	id INT PRIMARY KEY AUTO_INCREMENT, 
	eventtype VARCHAR(64) NOT NULL,
	author VARCHAR(1024) NOT NULL,
	rcpt VARCHAR,
	title VARCHAR(2048),
	message VARCHAR,
	style VARCHAR(64) NOT NULL,
	link VARCHAR
	);

