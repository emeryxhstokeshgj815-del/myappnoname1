data modify storage frv:tmp vd set from entity @s VillagerData
scoreboard players set #p frv.tmp 0
execute if data storage frv:tmp vd{profession:"minecraft:farmer"} run scoreboard players set #p frv.tmp 1
execute if data storage frv:tmp vd{profession:"minecraft:butcher"} run scoreboard players set #p frv.tmp 2
execute if data storage frv:tmp vd{profession:"minecraft:fisherman"} run scoreboard players set #p frv.tmp 3
execute if data storage frv:tmp vd{profession:"minecraft:shepherd"} run scoreboard players set #p frv.tmp 4
execute if data storage frv:tmp vd{profession:"minecraft:leatherworker"} run scoreboard players set #p frv.tmp 5
execute if data storage frv:tmp vd{profession:"minecraft:librarian"} run scoreboard players set #p frv.tmp 6
execute if data storage frv:tmp vd{profession:"minecraft:cartographer"} run scoreboard players set #p frv.tmp 7
execute if data storage frv:tmp vd{profession:"minecraft:cleric"} run scoreboard players set #p frv.tmp 8
execute if data storage frv:tmp vd{profession:"minecraft:armorer"} run scoreboard players set #p frv.tmp 9
execute if data storage frv:tmp vd{profession:"minecraft:weaponsmith"} run scoreboard players set #p frv.tmp 10
execute if data storage frv:tmp vd{profession:"minecraft:toolsmith"} run scoreboard players set #p frv.tmp 11
execute if data storage frv:tmp vd{profession:"minecraft:mason"} run scoreboard players set #p frv.tmp 12
execute if data storage frv:tmp vd{profession:"minecraft:fletcher"} run scoreboard players set #p frv.tmp 13
execute unless score @s frv.prof = #p frv.tmp run function frv:npc/setprof
execute if data entity @s Offers.Recipes[0] run data modify entity @s Offers.Recipes set value []
