-- begin update procCardType for Mpa
update wf_proc set card_types = regexp_replace(card_types, ',bratsk\\$Mpa', '') where code in ('Endorsement', 'Resolution', 'Acquaintance', 'Registration')^
update wf_proc set updated_by='system', card_types = card_types || 'bratsk$Mpa,' where code in ('Endorsement', 'Resolution', 'Acquaintance', 'Registration')^
-- end update procCardType for Mpa
