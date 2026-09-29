scoreboard players set @s frv.rel 0
scoreboard players set @s frv.tu 0
$execute store result score @s frv.rel run data get storage frv:rel p$(vid)_$(pid).rel
$execute store result score @s frv.tu run data get storage frv:rel p$(vid)_$(pid).tu
