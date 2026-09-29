advancement revoke @s only frv:talk
tag @e[type=minecraft:villager,tag=frv.cand] remove frv.cand
scoreboard players set #ray frv.tmp 0
execute anchored eyes positioned ^ ^ ^ run function frv:talk/ray
execute unless entity @e[type=minecraft:villager,tag=frv.cand] run tag @e[type=minecraft:villager,tag=frv.npc,distance=..3.5,sort=nearest,limit=1] add frv.cand
execute unless entity @e[type=minecraft:villager,tag=frv.cand] run return 0
execute if entity @e[type=minecraft:villager,tag=frv.cand,tag=frv.open] run return run tag @e[type=minecraft:villager,tag=frv.cand] remove frv.cand
execute unless entity @e[type=minecraft:villager,tag=frv.cand,scores={frv.prof=1..}] run return run tag @e[type=minecraft:villager,tag=frv.cand] remove frv.cand
scoreboard players operation #pid frv.tmp = @s frv.pid
scoreboard players set #mine frv.tmp 0
execute as @e[type=minecraft:villager,tag=frv.cand,tag=frv.busy] if score @s frv.with = #pid frv.tmp run scoreboard players set #mine frv.tmp 1
execute if score #mine frv.tmp matches 1 run return run function frv:talk/reshow
execute if entity @e[type=minecraft:villager,tag=frv.cand,tag=frv.busy] run return run function frv:talk/occupied
execute if score @s frv.sess matches 1 run function frv:session/abandon
function frv:talk/begin
