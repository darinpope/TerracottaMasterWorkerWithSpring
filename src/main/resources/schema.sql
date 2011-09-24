drop table country if exists;
drop table region if exists;
drop table player if exists;
drop table match if exists;
drop table playermatch if exists;

create table country (
  id integer primary key,
  code varchar(10) not null,
  country_name varchar(100) not null,
  continent varchar(100) not null
);

create table region (
  id integer primary key,
  code varchar(10) not null,
  local_code varchar(10) not null,
  region_name varchar(100) not null,
  continent varchar(100) not null,
  iso_country varchar(10) not null
);

create table player (
  id integer primary key,
  first_name varchar(100) not null,
  last_name varchar(100) not null
);

create table match (
  id integer primary key,
  field_name varchar(100) not null,
  start_time timestamp not null,
  end_time timestamp not null
);

create table playermatch (
  player_id integer not null,
  match_id integer not null
);

