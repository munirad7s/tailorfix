create table tailors (
    id              uuid primary key,
    name            varchar(120) not null,
    street          varchar(160) not null,
    postal_code     varchar(10)  not null,
    city            varchar(80)  not null,
    lat             double precision not null,
    lon             double precision not null,
    services        varchar(255) not null,
    certified       boolean      not null default true,
    turnaround_days integer      not null default 5,
    opening_hours   varchar(160),
    website         varchar(255)
);

create table order_items (
    id           uuid primary key,
    order_number varchar(40)  not null,
    brand        varchar(80)  not null,
    title        varchar(160) not null,
    category     varchar(20)  not null,
    size         varchar(20)  not null,
    price_cents  bigint       not null,
    image_url    varchar(255),
    delivered_at timestamp with time zone not null
);

create table return_intents (
    id               uuid primary key,
    order_item_id    uuid        not null references order_items (id),
    reason           varchar(32) not null,
    eligible         boolean     not null,
    rejection_reason varchar(64),
    customer_lat     double precision,
    customer_lon     double precision,
    created_at       timestamp with time zone not null
);

create table tailor_fix_offers (
    id                          uuid primary key,
    return_intent_id            uuid        not null references return_intents (id),
    alteration_type             varchar(32) not null,
    voucher_value_cents         bigint      not null,
    estimated_return_cost_cents bigint      not null,
    estimated_saving_cents      bigint      not null,
    estimated_co2_saved_grams   bigint      not null,
    requires_tailor_check       boolean     not null,
    expires_at                  timestamp with time zone not null
);

create table vouchers (
    id                uuid primary key,
    code              varchar(16) not null unique,
    offer_id          uuid        not null references tailor_fix_offers (id),
    tailor_id         uuid        not null references tailors (id),
    value_cents       bigint      not null,
    brand_share_cents bigint      not null,
    status            varchar(16) not null,
    issued_at         timestamp with time zone not null,
    expires_at        timestamp with time zone not null,
    redeemed_at       timestamp with time zone
);

create index idx_return_intents_order_item on return_intents (order_item_id);
create index idx_offers_return_intent on tailor_fix_offers (return_intent_id);
create index idx_vouchers_offer on vouchers (offer_id);
