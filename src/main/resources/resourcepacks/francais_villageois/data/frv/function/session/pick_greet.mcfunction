scoreboard players set @s frv.mode 0
execute store result score @s frv.rot run random value 0..3
execute if score @s frv.tu matches 1 run function frv:pool/greet_t
execute unless score @s frv.tu matches 1 run function frv:pool/greet_v
