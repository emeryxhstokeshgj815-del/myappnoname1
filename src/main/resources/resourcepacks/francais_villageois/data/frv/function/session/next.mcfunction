scoreboard players add @s frv.step 1
execute if score @s frv.step matches 1 run function frv:session/pick_greet
execute if score @s frv.step matches 2..3 run function frv:session/pick_theme
execute if score @s frv.step matches 4 run function frv:session/pick_price
execute if score @s frv.step matches 5 if score @s frv.offertu matches 1 run function frv:session/pick_tutoie
execute if score @s frv.step matches 5 unless score @s frv.offertu matches 1 run scoreboard players set @s frv.step 6
execute if score @s frv.step matches 6.. run return run function frv:session/finish
function frv:session/show
