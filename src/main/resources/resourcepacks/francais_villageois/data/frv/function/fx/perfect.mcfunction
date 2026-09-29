title @s times 5 50 15
title @s subtitle {text:"Ни одной ошибки — скидка 30%",color:"green"}
title @s title {text:"Sans faute !",color:"gold",bold:true}
execute at @e[type=minecraft:villager,tag=frv.spk,limit=1] run particle minecraft:totem_of_undying ~ ~1.2 ~ 0.4 0.6 0.4 0.3 40
playsound minecraft:entity.player.levelup master @s ~ ~ ~ 0.6 1.2
