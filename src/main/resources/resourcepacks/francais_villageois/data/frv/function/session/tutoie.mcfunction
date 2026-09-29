execute store result storage frv:tmp vid int 1 run scoreboard players get @s frv.cvid
execute store result storage frv:tmp pid int 1 run scoreboard players get @s frv.pid
function frv:pair/set_tu with storage frv:tmp
scoreboard players add @s frv.friends 1
advancement grant @s only frv:fr/ami
execute if score @s frv.friends matches 5.. run advancement grant @s only frv:fr/cinq_amis
execute at @e[type=minecraft:villager,tag=frv.spk,limit=1] run particle minecraft:heart ~ ~2.1 ~ 0.3 0.2 0.3 0 6
execute at @e[type=minecraft:villager,tag=frv.spk,limit=1] run playsound minecraft:entity.villager.celebrate neutral @s ~ ~ ~ 1 1
tellraw @s {text:"",extra:[{text:""},{text:"   ♥ Теперь вы с ",color:"light_purple"},{selector:"@e[type=minecraft:villager,tag=frv.spk,limit=1]",color:"gold"},{text:" на «ты»! Со следующего раза весь разговор будет на «tu».",color:"light_purple"}]}
