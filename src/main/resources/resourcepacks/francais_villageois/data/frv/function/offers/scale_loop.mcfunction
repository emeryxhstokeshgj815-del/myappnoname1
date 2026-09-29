execute unless data storage frv:tmp work[0] run return 0
execute store result score #c frv.tmp run data get storage frv:tmp work[0].buy.count
scoreboard players operation #c frv.tmp *= #mult frv.tmp
scoreboard players operation #c frv.tmp += #50 frv.const
scoreboard players operation #c frv.tmp /= #100 frv.const
execute if score #c frv.tmp matches ..0 run scoreboard players set #c frv.tmp 1
execute if score #c frv.tmp matches 65.. run scoreboard players set #c frv.tmp 64
execute store result storage frv:tmp work[0].buy.count int 1 run scoreboard players get #c frv.tmp
execute if score #reset frv.tmp matches 1 run data modify storage frv:tmp work[0].uses set value 0
execute if score #reset frv.tmp matches 1 run data modify storage frv:tmp work[0].demand set value 0
data modify storage frv:tmp out append from storage frv:tmp work[0]
data remove storage frv:tmp work[0]
function frv:offers/scale_loop
