SELECT d.chain, d.instr_id, d.currency, d.dsp
FROM dboe_dsp_mtm d
INNER join
(
SELECT MAX(DATE) AS latest_date, chain, instr_id, currency FROM dboe_dsp_mtm GROUP BY chain, instr_id, currency
) l ON d.instr_id = l.instr_id AND d.date = l.latest_date and d.chain = l.chain and d.currency = l.currency