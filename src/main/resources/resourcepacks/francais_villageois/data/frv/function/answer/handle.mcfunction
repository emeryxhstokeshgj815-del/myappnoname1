scoreboard players operation @s frv.opt = @s frv.ans
scoreboard players operation @s frv.opt %= #100 frv.const
scoreboard players operation #aq frv.tmp = @s frv.ans
scoreboard players operation #aq frv.tmp /= #100 frv.const
scoreboard players set @s frv.ans 0
execute unless score @s frv.sess matches 1 run return run tellraw @s {text:"Этот разговор уже закончен. Нажми ПКМ по жителю, чтобы начать новый.",color:"gray"}
execute unless score #aq frv.tmp = @s frv.q run return 0
function frv:session/tag_spk
execute unless entity @e[type=minecraft:villager,tag=frv.spk] run return run function frv:session/abandon
execute if score @s frv.opt matches 99 run return run function frv:answer/cont
execute if score @s frv.phase matches 1 run return run function frv:session/untag_spk
scoreboard players set @s frv.ok 0
function frv:session/setkey
execute if score @s frv.mode matches 0 if score @s frv.opt matches 1 run scoreboard players set @s frv.ok 1
execute if score @s frv.mode matches 0 run function frv:answer/mcq with storage frv:tmp
execute if score @s frv.mode matches 1 run function frv:answer/build with storage frv:tmp
function frv:session/untag_spk
