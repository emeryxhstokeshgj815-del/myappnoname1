scoreboard players set @s frv.ok 0
execute if score @s frv.bpos matches 0 if score @s frv.opt matches 12 run scoreboard players set @s frv.ok 1
execute if score @s frv.bpos matches 1 if score @s frv.opt matches 11 run scoreboard players set @s frv.ok 1
execute if score @s frv.bpos matches 2 if score @s frv.opt matches 10 run scoreboard players set @s frv.ok 1
execute if score @s frv.bpos matches 3 if score @s frv.opt matches 14 run scoreboard players set @s frv.ok 1
execute if score @s frv.ok matches 0 run return run function frv:b/16/tm/fail
playsound minecraft:block.note_block.hat master @s ~ ~ ~ 0.5 1.4
scoreboard players add @s frv.bpos 1
execute if score @s frv.bpos matches 4.. run return run function frv:b/16/tm/done
function frv:session/show
