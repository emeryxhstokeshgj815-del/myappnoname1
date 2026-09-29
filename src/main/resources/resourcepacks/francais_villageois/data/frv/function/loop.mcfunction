execute store result score #today frv.day run time query day
scoreboard players add #slot frv.tmp 1
execute if score #slot frv.tmp matches 5.. run scoreboard players set #slot frv.tmp 0
execute as @e[type=minecraft:villager,tag=frv.npc] unless score @s frv.vid matches 1.. run function frv:npc/migrate
execute as @e[type=minecraft:villager,tag=frv.open] at @s run function frv:npc/open_tick
execute as @e[type=minecraft:villager,tag=frv.busy] at @s run function frv:npc/busy_check
execute as @a at @s as @e[type=minecraft:villager,tag=frv.npc,tag=!frv.open,tag=!frv.busy,distance=..8] if data entity @s Offers.Recipes[0] run data modify entity @s Offers.Recipes set value []
execute as @e[type=minecraft:villager,tag=frv.npc,tag=!frv.open,tag=!frv.busy] if score @s frv.slot = #slot frv.tmp run function frv:npc/slow
execute if score #slot frv.tmp matches 0 as @e[type=minecraft:villager,tag=!frv.npc,predicate=!frv:baby] run function frv:npc/convert_check
schedule function frv:loop 20t replace
