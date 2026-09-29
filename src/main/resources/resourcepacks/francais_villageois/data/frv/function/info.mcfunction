execute unless score @s frv.pband matches 1.. run scoreboard players set @s frv.pband 1
execute if score @s frv.info matches 3 run return run scoreboard players set @s frv.info 0
execute if score @s frv.info matches 2 run return run function frv:info_help
scoreboard players set @s frv.info 0
function frv:stats/count
data modify storage frv:tmp lvl set value "A1"
execute if score @s frv.pband matches 2 run data modify storage frv:tmp lvl set value "A2"
execute if score @s frv.pband matches 3 run data modify storage frv:tmp lvl set value "B1"
data modify storage frv:tmp nxt set value "до A2: выучи 20 фраз A1"
execute if score @s frv.pband matches 2 run data modify storage frv:tmp nxt set value "до B1: выучи 16 фраз A2"
execute if score @s frv.pband matches 3 run data modify storage frv:tmp nxt set value "максимальный уровень!"
execute store result storage frv:tmp a1 int 1 run scoreboard players get #a1 frv.tmp
execute store result storage frv:tmp a2 int 1 run scoreboard players get #a2 frv.tmp
execute store result storage frv:tmp m int 1 run scoreboard players get #m frv.tmp
execute store result storage frv:tmp due int 1 run scoreboard players get #due frv.tmp
execute store result storage frv:tmp best int 1 run scoreboard players get @s frv.best
execute store result storage frv:tmp fr int 1 run scoreboard players get @s frv.friends
execute store result storage frv:tmp perf int 1 run scoreboard players get @s frv.perf
execute store result storage frv:tmp rep int 10 run scoreboard players get @s frv.rep
function frv:info_show with storage frv:tmp
