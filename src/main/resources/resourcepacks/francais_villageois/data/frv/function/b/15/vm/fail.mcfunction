function frv:fx/wrong
scoreboard players add @s frv.err 1
execute if score @s frv.step matches ..3 run scoreboard players add @s frv.err3 1
function frv:srs/wrong {id:15}
dialog show @s {type:"minecraft:notice",title:{text:"✘ Pas tout à fait…",color:"red",bold:true},body:[{type:"minecraft:plain_message",contents:{text:"Правильно:",color:"gray"},width:340},{type:"minecraft:plain_message",contents:{text:"Avec plaisir, il a l'air délicieux !",color:"green"},width:340},{type:"minecraft:plain_message",contents:{text:"» «Avoir l'air» — выглядеть. Gâteau — м.р. → délicieux. А житель сказал «il faut que vous goûtiez» — это subjonctif.",color:"gray"},width:340},{type:"minecraft:plain_message",contents:{text:"↻ Любой житель спросит это снова через игровой день.",color:"light_purple"},width:340}],action:{label:{text:"Дальше →",color:"green"},width:200,action:{type:"run_command",command:"trigger frv.ans set 1599"}},can_close_with_escape:true,pause:false}
scoreboard players set @s frv.phase 1
