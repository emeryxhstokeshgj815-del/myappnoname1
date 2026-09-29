execute store result score #r frv.tmp run random value 1..6
execute if score #r frv.tmp matches 1 run scoreboard players set @s frv.q 98
execute if score #r frv.tmp matches 2 run scoreboard players set @s frv.q 99
execute if score #r frv.tmp matches 3 run scoreboard players set @s frv.q 100
execute if score #r frv.tmp matches 4 run scoreboard players set @s frv.q 101
execute if score #r frv.tmp matches 5 run scoreboard players set @s frv.q 102
execute if score #r frv.tmp matches 6 run scoreboard players set @s frv.q 103
