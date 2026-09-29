scoreboard players operation #mult frv.tmp = @s frv.mult
execute as @e[type=minecraft:villager,tag=frv.spk,limit=1] run function frv:offers/unlock
execute as @e[type=minecraft:villager,tag=frv.spk] run function frv:npc/free
scoreboard players set @s frv.sess 0
execute if score @s frv.err matches ..1 run scoreboard players add @s frv.rel 1
execute store result storage frv:tmp vid int 1 run scoreboard players get @s frv.cvid
execute store result storage frv:tmp pid int 1 run scoreboard players get @s frv.pid
function frv:pair/save_rel with storage frv:tmp
advancement grant @s only frv:fr/bonjour
execute if score @s frv.vband matches 3 if score @s frv.band matches 3 run advancement grant @s only frv:fr/maitre
execute if score @s frv.err matches 0 run scoreboard players add @s frv.perf 1
execute if score @s frv.err matches 0 run advancement grant @s only frv:fr/sans_faute
execute if score @s frv.perf matches 10.. run advancement grant @s only frv:fr/dix_parfaits
function frv:session/summary_prep
execute if score @s frv.err matches 0 run function frv:fx/perfect
function frv:stats/levels
