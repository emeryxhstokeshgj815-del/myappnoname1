function frv:stats/count
execute if score @s frv.pband matches ..1 if score #a1 frv.tmp matches 20.. run function frv:stats/up_a2
execute if score @s frv.pband matches 2 if score #a2 frv.tmp matches 16.. run function frv:stats/up_b1
execute if score #m frv.tmp matches 50.. run advancement grant @s only frv:fr/cinquante
execute if score #m frv.tmp matches 144.. run advancement grant @s only frv:fr/dictionnaire
