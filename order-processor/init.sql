-- demo_order.orders definition

-- Drop table

-- DROP TABLE demo_order.orders;

CREATE TABLE demo_order.orders (
	id varchar NOT NULL,
	create_time timestamp NOT NULL,
	amount int4 NOT NULL,
	status text NOT NULL,
	cart_id text NOT NULL,
	update_time timestamp NULL,
	payment_txn_id text NULL,
	payment_url text NULL,
	CONSTRAINT order_pk PRIMARY KEY (id)
);

-- Permissions

ALTER TABLE demo_order.orders OWNER TO postgres;
GRANT ALL ON TABLE demo_order.orders TO postgres;

CREATE UNIQUE INDEX order_pk ON demo_order.orders USING btree (id);
