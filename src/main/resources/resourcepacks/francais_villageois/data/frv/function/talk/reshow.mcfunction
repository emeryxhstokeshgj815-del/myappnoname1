tag @e[type=minecraft:villager,tag=frv.cand] remove frv.cand
function frv:session/tag_spk
execute if score @s frv.phase matches 1 run function frv:session/advance
execute unless score @s frv.phase matches 1 run function frv:session/show
function frv:session/untag_spk
