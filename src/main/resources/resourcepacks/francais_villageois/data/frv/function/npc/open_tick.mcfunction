scoreboard players add @s frv.timer 1
scoreboard players operation #w frv.tmp = @s frv.with
scoreboard players set #found frv.tmp 0
execute as @a[distance=..6] if score @s frv.pid = #w frv.tmp run scoreboard players set #found frv.tmp 1
execute if score #found frv.tmp matches 0 run scoreboard players add @s frv.away 1
execute if score #found frv.tmp matches 1 run scoreboard players set @s frv.away 0
execute if score @s frv.away matches 3.. run return run function frv:npc/relock
execute if score @s frv.timer matches 170 as @a[distance=..8] if score @s frv.pid = #w frv.tmp run tellraw @s {text:"Житель: «Je ferme bientôt !» — лавка закроется через 10 секунд.",color:"gold",italic:true}
execute if score @s frv.timer matches 180.. run return run function frv:npc/relock
execute store result score #l frv.tmp run data get entity @s VillagerData.level
execute if score #l frv.tmp > @s frv.lvl run function frv:npc/levelup
scoreboard players operation @s frv.lvl = #l frv.tmp
