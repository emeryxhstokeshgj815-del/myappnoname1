execute if entity @s[tag=frv.open] run function frv:npc/relock
execute store result storage frv:tmp vid int 1 run scoreboard players get @s frv.vid
function frv:uninstall_m with storage frv:tmp
execute unless entity @s[tag=frv.named] run data remove entity @s CustomName
effect clear @s minecraft:slowness
tag @s remove frv.npc
tag @s remove frv.named
tag @s remove frv.busy
tag @s remove frv.open
