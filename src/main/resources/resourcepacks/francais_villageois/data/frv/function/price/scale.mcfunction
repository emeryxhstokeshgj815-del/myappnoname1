scoreboard players operation #n frv.tmp *= @s frv.mult
scoreboard players operation #n frv.tmp += #50 frv.const
scoreboard players operation #n frv.tmp /= #100 frv.const
execute if score #n frv.tmp matches ..0 run scoreboard players set #n frv.tmp 1
