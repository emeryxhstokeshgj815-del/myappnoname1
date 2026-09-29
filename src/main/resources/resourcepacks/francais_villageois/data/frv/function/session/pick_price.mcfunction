scoreboard players set @s frv.mode 0
execute store result score @s frv.rot run random value 0..3
scoreboard players set @s frv.mult 100
execute if score @s frv.err3 matches 0 run scoreboard players set @s frv.mult 70
execute if score @s frv.err3 matches 2 run scoreboard players set @s frv.mult 125
execute if score @s frv.err3 matches 3.. run scoreboard players set @s frv.mult 150
scoreboard players operation #rp frv.tmp = @s frv.rep
scoreboard players operation #rp frv.tmp *= #10 frv.const
scoreboard players operation @s frv.mult += #rp frv.tmp
scoreboard players set #n frv.tmp 0
data modify storage frv:tmp scan set value []
execute as @e[type=minecraft:villager,tag=frv.spk,limit=1] run function frv:price/load
function frv:price/scan
execute if score #n frv.tmp matches 1.. run function frv:price/scale
execute if score #n frv.tmp matches ..0 store result score #n frv.tmp run random value 1..64
execute if score #n frv.tmp matches 65.. run scoreboard players set #n frv.tmp 64
scoreboard players operation @s frv.q = #n frv.tmp
execute if score @s frv.band matches 2 run scoreboard players operation @s frv.q *= #3 frv.const
execute if score @s frv.band matches 3 run scoreboard players operation @s frv.q *= #10 frv.const
execute if score @s frv.band matches 1 run scoreboard players add @s frv.q 1000
execute if score @s frv.band matches 2 run scoreboard players add @s frv.q 2000
execute if score @s frv.band matches 3 run scoreboard players add @s frv.q 3000
