execute if score @s frv.rep matches ..4 run scoreboard players add @s frv.rep 1
execute at @e[type=minecraft:villager,tag=frv.spk,limit=1] run particle minecraft:angry_villager ~ ~2 ~ 0.3 0.2 0.3 0 5
advancement grant @s only frv:fr/malpoli
tellraw @s {text:"",extra:[{text:""},{text:"   ",color:"red"},{selector:"@e[type=minecraft:villager,tag=frv.spk,limit=1]",color:"gold"},{text:" хмурится: такая фамильярность с незнакомцем — невежливо. Цены в деревне для тебя +10%.",color:"red",italic:true}]}
