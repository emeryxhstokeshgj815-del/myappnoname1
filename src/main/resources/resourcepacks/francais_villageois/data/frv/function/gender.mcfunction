execute if score @s frv.gender matches 1 run scoreboard players set @s frv.fem 0
execute if score @s frv.gender matches 2 run scoreboard players set @s frv.fem 1
execute if score @s frv.gender matches 1 run tellraw @s {text:"Ок: ответы будут в мужском роде (je suis fatigué).",color:"aqua"}
execute if score @s frv.gender matches 2 run tellraw @s {text:"Ок: ответы будут в женском роде (je suis fatiguée).",color:"light_purple"}
scoreboard players set @s frv.gender 0
