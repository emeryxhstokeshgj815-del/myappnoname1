execute store result score #r frv.tmp run random value 1..4
execute if score #r frv.tmp matches 1 run scoreboard players set @s frv.q 211
execute if score #r frv.tmp matches 2 run scoreboard players set @s frv.q 212
execute if score #r frv.tmp matches 3 run scoreboard players set @s frv.q 213
execute if score #r frv.tmp matches 4 run scoreboard players set @s frv.q 214
