SELECT DISTINCT o.underlying, o.expiry, o.ltt, o.currency, o.option_factory_address
FROM dboe_all_options o
left outer join dboe_fsp f on o.chain = f.chain and o.underlying = f.underlying and o.expiry = f.expiry and o.option_factory_address = f.option_factory_address
WHERE o.CHAIN = '%s' and o.expiry = %d and f.fsp is null