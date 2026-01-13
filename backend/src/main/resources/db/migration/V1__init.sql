create table app_user (
  id bigserial primary key,
  email varchar(255) not null unique,
  password_hash varchar(255) not null,
  created_at timestamptz not null default now()
);

create table trade_event (
  id bigserial primary key,
  user_id bigint not null,
  env varchar(16) not null, -- testnet/prod
  symbol varchar(32) not null,
  decision varchar(64) not null,
  risk_allowed boolean not null,
  reason text,
  raw_request jsonb,
  raw_response jsonb,
  created_at timestamptz not null default now()
);
