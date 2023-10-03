INSERT INTO dboe_whitelist_wallets
SELECT email, wallet_address AS wallet_id, 'Airdrop' as country_residence
FROM referral_info WHERE TIMESTAMP <= 20230727090357

INSERT dboe_whitelist_wallets
SELECT email, wallet_address AS wallet_id, 'Airdrop' as country_residence
FROM referral_info WHERE TIMESTAMP between 20230727090357 and 20230807000900
AND email NOT IN ('cyberivan9@gmail.com', 'sunvershasan@gmail.com')

INSERT dboe_whitelist_wallets
SELECT email, wallet_address AS wallet_id, 'Airdrop' as country_residence
FROM referral_info WHERE TIMESTAMP between 20230807000901 and 20230813000000