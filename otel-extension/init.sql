-- demo_cart.carts definition

-- Drop table

-- DROP TABLE demo_cart.carts;

CREATE TABLE demo_cart.carts (
	id varchar NOT NULL,
	create_time timestamp NOT NULL,
	amount int4 NOT NULL,
	status text NOT NULL,
	update_time timestamp NULL,
	CONSTRAINT cart_pk PRIMARY KEY (id)
);

-- Permissions

ALTER TABLE demo_cart.carts OWNER TO postgres;
GRANT ALL ON TABLE demo_cart.carts TO postgres;

CREATE UNIQUE INDEX cart_pk ON demo_cart.carts USING btree (id);

