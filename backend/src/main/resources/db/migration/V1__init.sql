-- Optional: enable pgvector if you want vector embeddings later.
-- create extension if not exists vector;

create table trade_event (
  id bigserial primary key,
  env varchar(16) not null,
  event_type varchar(32) not null, -- INTENT/APPROVED/REJECTED/EXECUTED
  symbol varchar(32) not null,
  side varchar(8) not null, -- BUY/SELL/HOLD
  amount numeric,
  confidence numeric,
  rationale text,
  reason text,
  payload jsonb,
  created_at timestamptz not null default now()
);

create index trade_event_created_at_idx on trade_event(created_at desc);
