execute as @e[type=minecraft:villager,tag=frv.npc,dx=0,dy=0,dz=0] positioned ~-0.99 ~-0.99 ~-0.99 if entity @s[dx=0,dy=0,dz=0] run tag @s add frv.cand
execute if entity @e[type=minecraft:villager,tag=frv.cand] run return 0
scoreboard players add #ray frv.tmp 1
execute if score #ray frv.tmp matches 40.. run return 0
execute positioned ^ ^ ^0.125 run function frv:talk/ray
