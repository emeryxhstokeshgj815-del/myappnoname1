function frv:session/tag_spk
execute as @e[type=minecraft:villager,tag=frv.spk] run function frv:npc/free
function frv:session/untag_spk
scoreboard players set @s frv.sess 0
tellraw @s {text:"Разговор закончен без сделки.",color:"gray",italic:true}
