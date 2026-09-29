execute at @e[type=minecraft:villager,tag=frv.spk,limit=1] run particle minecraft:happy_villager ~ ~1.9 ~ 0.3 0.3 0.3 0 8
execute at @e[type=minecraft:villager,tag=frv.spk,limit=1] run playsound minecraft:entity.villager.yes neutral @s ~ ~ ~ 0.8 1
playsound minecraft:block.note_block.chime master @s ~ ~ ~ 0.5 1.6
scoreboard players add @s frv.streak 1
execute if score @s frv.streak > @s frv.best run scoreboard players operation @s frv.best = @s frv.streak
execute if score @s frv.streak matches 10.. run advancement grant @s only frv:fr/serie10
execute if score @s frv.streak matches 25.. run advancement grant @s only frv:fr/serie25
