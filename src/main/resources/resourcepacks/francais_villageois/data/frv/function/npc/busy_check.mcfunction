scoreboard players operation #w frv.tmp = @s frv.with
scoreboard players set #found frv.tmp 0
execute as @a[distance=..12] if score @s frv.pid = #w frv.tmp if score @s frv.sess matches 1 run scoreboard players set #found frv.tmp 1
execute if score #found frv.tmp matches 1 run return 0
function frv:npc/free
execute as @a if score @s frv.pid = #w frv.tmp if score @s frv.sess matches 1 run function frv:session/walked_away
