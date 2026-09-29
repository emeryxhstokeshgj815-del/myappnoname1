scoreboard players set @s frv.ok 0
execute if score @s frv.bpos matches 0 if score @s frv.opt matches 13 run scoreboard players set @s frv.ok 1
execute if score @s frv.bpos matches 1 if score @s frv.opt matches 10 run scoreboard players set @s frv.ok 1
execute if score @s frv.bpos matches 2 if score @s frv.opt matches 17 run scoreboard players set @s frv.ok 1
execute if score @s frv.bpos matches 3 if score @s frv.opt matches 12 run scoreboard players set @s frv.ok 1
execute if score @s frv.bpos matches 4 if score @s frv.opt matches 15 run scoreboard players set @s frv.ok 1
execute if score @s frv.bpos matches 5 if score @s frv.opt matches 14 run scoreboard players set @s frv.ok 1
execute if score @s frv.bpos matches 6 if score @s frv.opt matches 18 run scoreboard players set @s frv.ok 1
execute if score @s frv.ok matches 0 run return run function frv:b/28/tm/fail
playsound minecraft:block.note_block.hat master @s ~ ~ ~ 0.5 1.4
scoreboard players add @s frv.bpos 1
execute if score @s frv.bpos matches 7.. run return run function frv:b/28/tm/done
function frv:session/show
