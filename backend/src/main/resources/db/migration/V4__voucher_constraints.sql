-- One voucher per offer, enforced by the database rather than by a check-then-insert in code.
create unique index uq_vouchers_offer on vouchers (offer_id);

-- Optimistic locking for redemption (double scan of the same code must not pay out twice).
alter table vouchers add column version bigint not null default 0;
