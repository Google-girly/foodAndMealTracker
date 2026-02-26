-- 1) extension (safe: "IF NOT EXISTS")
create extension if not exists pgcrypto;

-- 2) create enum only if it doesn't already exist
do $$
begin
  if not exists (
    select 1 from pg_type t
    join pg_namespace n on t.typnamespace = n.oid
    where t.typname = 'meal_type_enum'
  ) then
    create type meal_type_enum as enum ('breakfast','lunch','dinner','snack');
  end if;
end$$;

-- 3) users table (safe: CREATE TABLE IF NOT EXISTS)
create table if not exists users (
  id uuid primary key default gen_random_uuid(),
  email varchar(255) not null unique,
  full_name varchar(255),
  admin boolean not null default false,
  created_at timestamptz not null default now()
);

-- 4) foods table
create table if not exists foods (
  id uuid primary key default gen_random_uuid(),
  name varchar(255) not null,
  calories integer,
  protein numeric(8,2),
  carbs numeric(8,2),
  fat numeric(8,2),
  created_by uuid references users(id) on delete set null,
  is_public boolean not null default false,
  created_at timestamptz not null default now()
);

-- 5) meals table
create table if not exists meals (
  id uuid primary key default gen_random_uuid(),
  users_id uuid not null references users(id) on delete cascade,
  name varchar(255) not null,
  meal_type meal_type_enum not null,
  meal_date timestamptz,
  description text,
  created_at timestamptz not null default now(),
  updated_at timestamptz not null default now()
);

-- 6) trigger function: replace (safe) — replacing a function does not drop data
create or replace function set_updated_at()
returns trigger language plpgsql as $$
begin
  new.updated_at = now();
  return new;
end;
$$;

-- 7) create trigger only if it doesn't already exist 
do $$
begin
  if not exists (
    select 1 from pg_trigger
    where tgname = 'trg_set_updated_at_meals'
  ) then
    create trigger trg_set_updated_at_meals
    before update on meals
    for each row
    execute function set_updated_at();
  end if;
end$$;

-- 8) meal_foods join table
create table if not exists meal_foods (
  id uuid primary key default gen_random_uuid(),
  meal_id uuid not null references meals(id) on delete cascade,
  food_id uuid not null references foods(id) on delete restrict,
  quantity numeric(10,3) not null,
  unit varchar(50) not null
);

create index if not exists idx_meals_users_id on meals(users_id);
create index if not exists idx_meals_meal_date on meals(meal_date);
create index if not exists idx_foods_created_by on foods(created_by);