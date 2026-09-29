execute if score @s frv.ok matches 1 run function frv:fx/right
execute if score @s frv.ok matches 0 run function frv:fx/wrong
execute if score @s frv.ok matches 0 run scoreboard players add @s frv.err 1
execute if score @s frv.ok matches 0 if score @s frv.step matches ..3 run scoreboard players add @s frv.err3 1
execute if score @s frv.ok matches 1 run function frv:srs/right {id:110}
execute if score @s frv.ok matches 0 run function frv:srs/wrong {id:110}
execute if score @s frv.ok matches 1 run dialog show @s {type:"minecraft:notice",title:{text:"✔ Excellent !",color:"green",bold:true},body:[{type:"minecraft:plain_message",contents:{text:"Oui, j'ai un frère et une sœur.",color:"green"},width:340},{type:"minecraft:plain_message",contents:{text:"» Frère — м.р. (un), sœur — ж.р. (une).",color:"gray"},width:340}],action:{label:{text:"Дальше →",color:"green"},width:200,action:{type:"run_command",command:"trigger frv.ans set 11099"}},can_close_with_escape:true,pause:false}
execute if score @s frv.ok matches 0 run dialog show @s {type:"minecraft:notice",title:{text:"✘ Pas tout à fait…",color:"red",bold:true},body:[{type:"minecraft:plain_message",contents:{text:"Правильно:",color:"gray"},width:340},{type:"minecraft:plain_message",contents:{text:"Oui, j'ai un frère et une sœur.",color:"green"},width:340},{type:"minecraft:plain_message",contents:{text:"» Frère — м.р. (un), sœur — ж.р. (une).",color:"gray"},width:340},{type:"minecraft:plain_message",contents:{text:"↻ Любой житель спросит это снова через игровой день.",color:"light_purple"},width:340}],action:{label:{text:"Дальше →",color:"green"},width:200,action:{type:"run_command",command:"trigger frv.ans set 11099"}},can_close_with_escape:true,pause:false}
scoreboard players set @s frv.phase 1
