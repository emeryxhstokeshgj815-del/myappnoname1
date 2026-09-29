execute as @e[type=minecraft:villager,tag=frv.npc] run function frv:uninstall_v
schedule clear frv:loop
tellraw @a {text:"[Français Villageois] Жители в загруженных чанках вернули свои обычные сделки. Теперь удали zip из папки datapacks и введи /reload.",color:"gold"}
scoreboard objectives remove frv.ans
scoreboard objectives remove frv.info
scoreboard objectives remove frv.quit
scoreboard objectives remove frv.gender
