tag @e[type=minecraft:villager,tag=frv.spk] remove frv.spk
scoreboard players operation #pid frv.tmp = @s frv.pid
execute as @e[type=minecraft:villager,tag=frv.busy] if score @s frv.with = #pid frv.tmp run tag @s add frv.spk
