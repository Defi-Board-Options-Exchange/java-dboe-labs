SELECT
    lower(i.wallet_address) AS Address,
    count(distinct r.wallet_address) AS numOfInvitations
FROM referral_info i
inner JOIN referral_ack r ON right(i.wallet_address, 8) = r.referral_code AND r.timestamp > i.timestamp
GROUP BY 1