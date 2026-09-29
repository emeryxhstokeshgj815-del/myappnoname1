execute store result storage frv:tmp vid int 1 run scoreboard players get @s frv.vid
function frv:npc/capture_m with storage frv:tmp
data modify entity @s Offers.Recipes set value []
