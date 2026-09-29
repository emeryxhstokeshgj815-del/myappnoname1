scoreboard players set @s frv.mode 0
execute store result score @s frv.rot run random value 0..3
scoreboard players set @s frv.q 0
execute store result storage frv:tmp t int 1 run scoreboard players get @s frv.theme
execute store result storage frv:tmp b int 1 run scoreboard players get @s frv.band
data modify storage frv:tmp r set value "v"
execute if score @s frv.tu matches 1 run data modify storage frv:tmp r set value "t"
function frv:session/pick_theme_m with storage frv:tmp
scoreboard players operation @s frv.prevq = @s frv.q
scoreboard players set @s frv.bpos 0
execute store result storage frv:tmp id int 1 run scoreboard players get @s frv.q
function frv:session/pick_mode with storage frv:tmp
