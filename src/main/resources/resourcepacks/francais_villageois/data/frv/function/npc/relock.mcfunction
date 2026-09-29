tag @s remove frv.open
scoreboard players set @s frv.timer 0
execute store result storage frv:tmp vid int 1 run scoreboard players get @s frv.vid
data modify storage frv:tmp live set value []
data modify storage frv:tmp live set from entity @s Offers.Recipes
data modify storage frv:tmp work set value []
function frv:offers/unlock_m with storage frv:tmp
data modify storage frv:tmp out set value []
function frv:npc/relock_loop
data modify storage frv:tmp out append from storage frv:tmp live[]
function frv:npc/relock_save with storage frv:tmp
data modify entity @s Offers.Recipes set value []
