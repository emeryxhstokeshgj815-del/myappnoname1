execute store result storage frv:tmp vid int 1 run scoreboard players get @s frv.vid
data modify storage frv:tmp work set value []
function frv:offers/unlock_m with storage frv:tmp
data modify storage frv:tmp out set value []
scoreboard players set #reset frv.tmp 0
execute if score #today frv.day > @s frv.rday run scoreboard players set #reset frv.tmp 1
execute if score #reset frv.tmp matches 1 run scoreboard players operation @s frv.rday = #today frv.day
function frv:offers/scale_loop
data modify entity @s Offers.Recipes set from storage frv:tmp out
tag @s add frv.open
scoreboard players set @s frv.timer 0
scoreboard players set @s frv.away 0
