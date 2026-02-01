create table users (
  id bigserial primary key,
  email varchar(255) unique not null,
  password_hash varchar(255) not null,
  created_at timestamptz not null default now()
);

create table products (
  id bigserial primary key,
  name varchar(255) not null,
  description text,
  price_cents integer not null,
  currency varchar(10) not null default 'usd',
  stock integer not null,
  created_at timestamptz not null default now()
);

create table orders (
  id bigserial primary key,
  user_id bigint not null references users(id),
  status varchar(50) not null,
  total_cents integer not null,
  currency varchar(10) not null,
  stripe_session_id varchar(255),
  created_at timestamptz not null default now()
);

create table order_items (
  id bigserial primary key,
  order_id bigint not null references orders(id) on delete cascade,
  product_id bigint not null references products(id),
  quantity integer not null,
  price_cents integer not null
);

-- reservation table to handle concurrent checkout safely
create table inventory_reservations (
  id bigserial primary key,
  user_id bigint not null references users(id),
  product_id bigint not null references products(id),
  quantity integer not null,
  status varchar(30) not null, -- ACTIVE, RELEASED, CONSUMED
  expires_at timestamptz not null,
  created_at timestamptz not null default now()
);

create index idx_products_stock on products(stock);
create index idx_res_status_expires on inventory_reservations(status, expires_at);
