data modify storage frv:tmp ttl set value "✔ Pas mal !"
data modify storage frv:tmp m1 set value "Одна ошибка — обычные цены."
execute if score @s frv.err3 matches 0 run data modify storage frv:tmp ttl set value "★ Sans faute !"
execute if score @s frv.err3 matches 0 run data modify storage frv:tmp m1 set value "Без ошибок — скидка 30% на все товары!"
execute if score @s frv.err3 matches 2 run data modify storage frv:tmp ttl set value "✘ Bof…"
execute if score @s frv.err3 matches 2 run data modify storage frv:tmp m1 set value "Две ошибки — цены +25%."
execute if score @s frv.err3 matches 3.. run data modify storage frv:tmp ttl set value "✘ Aïe !"
execute if score @s frv.err3 matches 3.. run data modify storage frv:tmp m1 set value "Три ошибки — цены +50%. Поговори с жителем ещё раз."
data modify storage frv:tmp m2 set value ""
execute store result storage frv:tmp rep int 10 run scoreboard players get @s frv.rep
execute if score @s frv.rep matches 1.. run function frv:session/summary_rep with storage frv:tmp
data modify storage frv:tmp m3 set value "♥ Житель скоро предложит перейти на «ты»."
execute if score @s frv.tu matches 1 run data modify storage frv:tmp m3 set value "♥ Вы с этим жителем на «ты»."
execute if score @s frv.offertu matches 1 run data modify storage frv:tmp m3 set value "♥ Вы с этим жителем теперь на «ты»!"
execute if score @s frv.tu matches 0 unless score @s frv.offertu matches 1 if score @s frv.rel matches 0 run data modify storage frv:tmp m3 set value "♥ Дружба с жителем: 0/3 — поговори почти без ошибок, и он предложит «ты»."
execute if score @s frv.tu matches 0 unless score @s frv.offertu matches 1 if score @s frv.rel matches 1 run data modify storage frv:tmp m3 set value "♥ Дружба с жителем: 1/3 — поговори почти без ошибок, и он предложит «ты»."
execute if score @s frv.tu matches 0 unless score @s frv.offertu matches 1 if score @s frv.rel matches 2 run data modify storage frv:tmp m3 set value "♥ Дружба с жителем: 2/3 — поговори почти без ошибок, и он предложит «ты»."
function frv:session/summary with storage frv:tmp
