-- begin update discriminator for ExtTask
update WF_CARD
set CARD_TYPE = '200'
where CARD_TYPE = '20' ^
-- end update discriminator for ExtTask
