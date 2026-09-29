$execute if score @s frv.d$(id) matches 1.. if score @s frv.d$(id) <= #today frv.day run advancement grant @s only frv:fr/revision
$scoreboard players add @s frv.s$(id) 1
$execute if score @s frv.d$(id) matches 1.. if score @s frv.s$(id) matches 1 run function frv:srs/due_in {id:$(id),n:3}
$execute if score @s frv.d$(id) matches 1.. if score @s frv.s$(id) matches 2 run function frv:srs/due_in {id:$(id),n:7}
$execute if score @s frv.s$(id) matches 3.. run scoreboard players set @s frv.d$(id) 0
