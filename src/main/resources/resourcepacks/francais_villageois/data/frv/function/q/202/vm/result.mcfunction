execute if score @s frv.ok matches 1 run function frv:fx/right
execute if score @s frv.ok matches 0 run function frv:fx/wrong
execute if score @s frv.ok matches 0 run scoreboard players add @s frv.err 1
execute if score @s frv.ok matches 0 if score @s frv.step matches ..3 run scoreboard players add @s frv.err3 1
execute if score @s frv.ok matches 0 if score @s frv.opt matches 2 run function frv:session/rude
execute if score @s frv.ok matches 0 if score @s frv.opt matches 3 run function frv:session/rude
execute if score @s frv.ok matches 0 if score @s frv.opt matches 4 run function frv:session/rude
execute if score @s frv.ok matches 1 if score @s frv.rep matches 1.. run scoreboard players remove @s frv.rep 1
execute if score @s frv.ok matches 1 run dialog show @s {type:"minecraft:notice",title:{text:"✔ Super !",color:"green",bold:true},body:[{type:"minecraft:plain_message",contents:{text:"Bonjour ! Qu'est-ce que vous vendez aujourd'hui ?",color:"green"},width:340},{type:"minecraft:plain_message",contents:{text:"» С незнакомым продавцом — «vous», «bonjour» и «s'il vous plaît». «Tu», «salut», «coucou», «ouais» — только для друзей.",color:"gray"},width:340}],action:{label:{text:"Дальше →",color:"green"},width:200,action:{type:"run_command",command:"trigger frv.ans set 20299"}},can_close_with_escape:true,pause:false}
execute if score @s frv.ok matches 0 run dialog show @s {type:"minecraft:notice",title:{text:"✘ Pas tout à fait…",color:"red",bold:true},body:[{type:"minecraft:plain_message",contents:{text:"Правильно:",color:"gray"},width:340},{type:"minecraft:plain_message",contents:{text:"Bonjour ! Qu'est-ce que vous vendez aujourd'hui ?",color:"green"},width:340},{type:"minecraft:plain_message",contents:{text:"» С незнакомым продавцом — «vous», «bonjour» и «s'il vous plaît». «Tu», «salut», «coucou», «ouais» — только для друзей.",color:"gray"},width:340}],action:{label:{text:"Дальше →",color:"green"},width:200,action:{type:"run_command",command:"trigger frv.ans set 20299"}},can_close_with_escape:true,pause:false}
scoreboard players set @s frv.phase 1
