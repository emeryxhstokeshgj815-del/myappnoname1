execute store result storage frv:tmp i int 1 run scoreboard players get @s frv.nm
execute store result storage frv:tmp p int 1 run scoreboard players get @s frv.prof
execute store result storage frv:tmp g int 1 run scoreboard players get @s frv.g
function frv:npc/rename_m with storage frv:tmp
