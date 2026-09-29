execute unless data storage frv:tmp scan[0] run return 0
execute if data storage frv:tmp scan[0].buy{id:"minecraft:emerald"} run return run execute store result score #n frv.tmp run data get storage frv:tmp scan[0].buy.count
data remove storage frv:tmp scan[0]
function frv:price/scan
