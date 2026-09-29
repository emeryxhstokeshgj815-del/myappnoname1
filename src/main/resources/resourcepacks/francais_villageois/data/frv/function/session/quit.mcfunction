scoreboard players set @s frv.quit 0
execute if score @s frv.sess matches 1 run function frv:session/abandon
