tag @s remove frv.npc
tag @s remove frv.open
tag @s remove frv.busy
effect clear @s minecraft:slowness
data modify storage frv:tmp prof0 set from entity @s VillagerData.profession
data modify entity @s VillagerData.profession set value "minecraft:none"
data modify entity @s VillagerData.profession set from storage frv:tmp prof0
