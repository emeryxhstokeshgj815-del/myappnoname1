function frv:fx/wrong
scoreboard players add @s frv.err 1
execute if score @s frv.step matches ..3 run scoreboard players add @s frv.err3 1
function frv:srs/wrong {id:103}
dialog show @s {type:"minecraft:notice",title:{text:"✘ Pas tout à fait…",color:"red",bold:true},body:[{type:"minecraft:plain_message",contents:{text:"Правильно:",color:"gray"},width:340},{type:"minecraft:plain_message",contents:{text:"C'est moi qui l'ai faite.",color:"green"},width:340},{type:"minecraft:plain_message",contents:{text:"» «C'est moi qui» + глагол в 1-м лице: qui l'ai. Pioche — ж.р. и стоит перед avoir → faite.",color:"gray"},width:340},{type:"minecraft:plain_message",contents:{text:"↻ Любой житель спросит это снова через игровой день.",color:"light_purple"},width:340}],action:{label:{text:"Дальше →",color:"green"},width:200,action:{type:"run_command",command:"trigger frv.ans set 10399"}},can_close_with_escape:true,pause:false}
scoreboard players set @s frv.phase 1
