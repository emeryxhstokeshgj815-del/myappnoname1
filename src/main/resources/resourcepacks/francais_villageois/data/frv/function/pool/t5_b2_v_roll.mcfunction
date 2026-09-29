execute store result score #r frv.tmp run random value 1..6
execute if score #r frv.tmp matches 1 run scoreboard players set @s frv.q 80
execute if score #r frv.tmp matches 2 run scoreboard players set @s frv.q 81
execute if score #r frv.tmp matches 3 run scoreboard players set @s frv.q 82
execute if score #r frv.tmp matches 4 run scoreboard players set @s frv.q 83
execute if score #r frv.tmp matches 5 run scoreboard players set @s frv.q 84
execute if score #r frv.tmp matches 6 run scoreboard players set @s frv.q 85
