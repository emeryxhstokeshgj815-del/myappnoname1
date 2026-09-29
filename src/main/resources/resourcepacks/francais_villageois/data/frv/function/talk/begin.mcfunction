execute as @e[type=minecraft:villager,tag=frv.open] if score @s frv.with = #pid frv.tmp run function frv:npc/relock
tag @e[type=minecraft:villager,tag=frv.cand] add frv.busy
scoreboard players operation @e[type=minecraft:villager,tag=frv.cand] frv.with = @s frv.pid
effect give @e[type=minecraft:villager,tag=frv.cand] minecraft:slowness 300 255 true
execute as @e[type=minecraft:villager,tag=frv.cand] at @s run tp @s ~ ~ ~ facing entity @p eyes
scoreboard players operation @s frv.theme = @e[type=minecraft:villager,tag=frv.cand,limit=1] frv.theme
scoreboard players operation @s frv.cvid = @e[type=minecraft:villager,tag=frv.cand,limit=1] frv.vid
execute store result score @s frv.lvl run data get entity @e[type=minecraft:villager,tag=frv.cand,limit=1] VillagerData.level
scoreboard players set @s frv.vband 1
execute if score @s frv.lvl matches 3..4 run scoreboard players set @s frv.vband 2
execute if score @s frv.lvl matches 5.. run scoreboard players set @s frv.vband 3
execute unless score @s frv.pband matches 1.. run scoreboard players set @s frv.pband 1
scoreboard players operation @s frv.band = @s frv.vband
scoreboard players operation @s frv.band < @s frv.pband
execute store result storage frv:tmp vid int 1 run scoreboard players get @s frv.cvid
execute store result storage frv:tmp pid int 1 run scoreboard players get @s frv.pid
function frv:pair/load with storage frv:tmp
scoreboard players set @s frv.offertu 0
execute if score @s frv.tu matches 0 if score @s frv.rel matches 3.. run scoreboard players set @s frv.offertu 1
scoreboard players set @s frv.total 4
execute if score @s frv.offertu matches 1 run scoreboard players set @s frv.total 5
scoreboard players set @s frv.sess 1
scoreboard players set @s frv.step 0
scoreboard players set @s frv.err 0
scoreboard players set @s frv.err3 0
scoreboard players set @s frv.prevq 0
scoreboard players set @s frv.q 0
scoreboard players set @s frv.phase 0
tag @e[type=minecraft:villager,tag=frv.cand] remove frv.cand
function frv:session/tag_spk
function frv:session/next
function frv:session/untag_spk
