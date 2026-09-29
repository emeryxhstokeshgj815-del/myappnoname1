execute at @e[type=minecraft:villager,tag=frv.spk,limit=1] run particle minecraft:smoke ~ ~2 ~ 0.2 0.2 0.2 0.01 6
execute at @e[type=minecraft:villager,tag=frv.spk,limit=1] run playsound minecraft:entity.villager.no neutral @s ~ ~ ~ 0.8 1
playsound minecraft:block.note_block.bass master @s ~ ~ ~ 0.5 0.7
execute if score @s frv.streak matches 5.. run tellraw @s {text:"",extra:[{text:""},{text:"   Серия прервалась на ",color:"gray"},{score:{name:"@s",objective:"frv.streak"},color:"gold"},{text:". Ничего, начнём новую!",color:"gray"}]}
scoreboard players set @s frv.streak 0
