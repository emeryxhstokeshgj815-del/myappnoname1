data modify storage frv:tmp key set value "vm"
execute if score @s frv.tu matches 0 if score @s frv.fem matches 1 run data modify storage frv:tmp key set value "vf"
execute if score @s frv.tu matches 1 unless score @s frv.fem matches 1 run data modify storage frv:tmp key set value "tm"
execute if score @s frv.tu matches 1 if score @s frv.fem matches 1 run data modify storage frv:tmp key set value "tf"
execute store result storage frv:tmp id int 1 run scoreboard players get @s frv.q
function frv:session/resolve with storage frv:tmp
