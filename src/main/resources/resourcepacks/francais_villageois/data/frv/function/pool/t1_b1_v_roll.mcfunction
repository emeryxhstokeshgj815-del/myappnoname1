execute store result score #r frv.tmp run random value 1..7
execute if score #r frv.tmp matches 1 run scoreboard players set @s frv.q 1
execute if score #r frv.tmp matches 2 run scoreboard players set @s frv.q 2
execute if score #r frv.tmp matches 3 run scoreboard players set @s frv.q 3
execute if score #r frv.tmp matches 4 run scoreboard players set @s frv.q 4
execute if score #r frv.tmp matches 5 run scoreboard players set @s frv.q 5
execute if score #r frv.tmp matches 6 run scoreboard players set @s frv.q 6
execute if score #r frv.tmp matches 7 run scoreboard players set @s frv.q 7
