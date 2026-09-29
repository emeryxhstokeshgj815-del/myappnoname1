scoreboard players operation @s frv.prof = #p frv.tmp
scoreboard players set @s frv.theme 0
execute if score @s frv.prof matches 1 run scoreboard players set @s frv.theme 1
execute if score @s frv.prof matches 2 run scoreboard players set @s frv.theme 1
execute if score @s frv.prof matches 3 run scoreboard players set @s frv.theme 1
execute if score @s frv.prof matches 4 run scoreboard players set @s frv.theme 2
execute if score @s frv.prof matches 5 run scoreboard players set @s frv.theme 2
execute if score @s frv.prof matches 6 run scoreboard players set @s frv.theme 3
execute if score @s frv.prof matches 7 run scoreboard players set @s frv.theme 4
execute if score @s frv.prof matches 8 run scoreboard players set @s frv.theme 5
execute if score @s frv.prof matches 9 run scoreboard players set @s frv.theme 6
execute if score @s frv.prof matches 10 run scoreboard players set @s frv.theme 6
execute if score @s frv.prof matches 11 run scoreboard players set @s frv.theme 6
execute if score @s frv.prof matches 12 run scoreboard players set @s frv.theme 7
execute if score @s frv.prof matches 13 run scoreboard players set @s frv.theme 8
execute store result score @s frv.lvl run data get storage frv:tmp vd.level
scoreboard players operation @s frv.rday = #today frv.day
function frv:npc/capture
execute unless entity @s[tag=frv.named] run function frv:npc/rename
