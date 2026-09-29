execute store result score #r frv.tmp run random value 1..5
execute if score #r frv.tmp matches 1 run scoreboard players set @s frv.q 14
execute if score #r frv.tmp matches 2 run scoreboard players set @s frv.q 15
execute if score #r frv.tmp matches 3 run scoreboard players set @s frv.q 16
execute if score #r frv.tmp matches 4 run scoreboard players set @s frv.q 17
execute if score #r frv.tmp matches 5 run scoreboard players set @s frv.q 18
