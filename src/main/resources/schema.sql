drop table if exists members;
drop table if exists product_categories;
drop table if exists products;
drop table if exists stocks;
drop table if exists orders;
drop table if exists order_items;
drop table if exists payments;
drop table if exists cart_items;
drop table if exists coupons;
drop table if exists coupon_frames;

create table if not exists members
(
    id         bigint auto_increment not null,
    name       varchar(255)          not null,
    email      varchar(255)          not null unique,
    password   varchar(255)          not null,
    role       varchar(255)          not null,
    created_at datetime,
    updated_at datetime,
    primary key (id)
);

create table if not exists product_categories
(
    product_id bigint not null,
    category   varchar(255)
);

create table if not exists products
(
    id         bigint auto_increment not null,
    name       varchar(255)          not null,
    image_url  varchar(255),
    price      bigint                not null,
    quantity   bigint                not null,
    status     varchar(255)          not null,
    brand      varchar(255)          not null,
    created_at datetime              not null,
    updated_at datetime              not null,
    primary key (id)
);

create table if not exists stocks
(
    id          bigint auto_increment not null,
    expiry_date date                  not null,
    quantity    bigint                not null,
    product_id  bigint                not null,
    stock_type  varchar(255),
    created_at  datetime              not null,
    updated_at  datetime              not null,
    primary key (id)
);

CREATE TABLE if not exists order_items
(
    id         bigint auto_increment not null,
    order_id   bigint,
    product_id bigint                not null,
    coupon_id  bigint,
    name       varchar(20)           not null,
    image      varchar(255)          not null,
    price      bigint                not null,
    quantity   bigint                not null,
    created_at datetime              not null,
    updated_at datetime              not null,
    primary key (id)
);



create table if not exists orders
(
    id                 bigint auto_increment not null,
    order_status       varchar(255)          not null,
    total_price        bigint                not null,
    origin_total_price bigint                not null,
    member_id          bigint                not null,
    uuid               varchar(255)          not null,
    created_at         datetime              not null,
    updated_at         datetime              not null,
    primary key (id)
);

create table if not exists payments
(
    id          bigint auto_increment not null,
    order_id    bigint                not null,
    payment_key varchar(255)          not null,
    total_price bigint                not null,
    created_at  datetime              not null,
    updated_at  datetime              not null,
    primary key (id)
);

create table if not exists cart_items
(
    id         bigint auto_increment not null,
    member_id  bigint                not null,
    product_id bigint                not null,
    quantity   bigint                not null,
    created_at datetime              not null,
    updated_at datetime              not null,
    primary key (id)
);

create table if not exists product_sales
(
    id         bigint auto_increment not null,
    product_id bigint                not null,
    sale       bigint                not null,
    sale_date  date                  not null,
    created_at datetime              not null,
    updated_at datetime              not null,
    primary key (id)
);

create table if not exists coupon_groups
(
    id                   bigint auto_increment not null,
    name                 varchar(255)          not null,
    duration             bigint,
    end_date             date                  not null,
    minimum_order_amount int                   not null,
    coupon_target_type   varchar(255)          not null,
    coupon_deploy_type   varchar(255)          not null,
    coupon_deploy_amount int                   not null,
    product_brand        varchar(255),
    product_category     varchar(255),
    product_id           bigint,
    coupon_type          varchar(25)           not null,
    coupon_group_status  varchar(25)           not null,
    discount             int,
    created_at           datetime              not null,
    updated_at           datetime              not null,
    primary key (id)
);

create table if not exists coupons
(
    id                   bigint auto_increment not null,
    name                 varchar(255)          not null,
    expiry_date          date                  not null,
    minimum_order_amount int                   not null,
    coupon_target_type   varchar(255)          not null,
    member_id            bigint                not null,
    product_brand        varchar(255),
    product_category     varchar(255),
    product_id           bigint,
    discount             int,
    enabled              BOOLEAN default true,
    coupon_type          varchar(25)           not null,
    created_at           datetime              not null,
    updated_at           datetime              not null,
    primary key (id)
);

