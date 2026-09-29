scoreboard players enable @a frv.ans
scoreboard players enable @a frv.info
scoreboard players enable @a frv.quit
scoreboard players enable @a frv.gender
execute as @a unless score @s frv.pid matches 1.. run function frv:player/join
execute as @a[scores={frv.ans=1..}] at @s run function frv:answer/handle
execute as @a[scores={frv.info=1..}] run function frv:info
execute as @a[scores={frv.quit=1..}] at @s run function frv:session/quit
execute as @a[scores={frv.gender=1..}] run function frv:gender
