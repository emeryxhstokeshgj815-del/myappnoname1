scoreboard players add #next frv.pid 1
scoreboard players operation @s frv.pid = #next frv.pid
scoreboard players set @s frv.rep 0
scoreboard players set @s frv.sess 0
scoreboard players set @s frv.pband 1
advancement grant @s only frv:fr/root
function frv:help
