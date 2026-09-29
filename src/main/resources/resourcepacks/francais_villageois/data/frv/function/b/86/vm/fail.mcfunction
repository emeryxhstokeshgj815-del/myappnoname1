function frv:fx/wrong
scoreboard players add @s frv.err 1
execute if score @s frv.step matches ..3 run scoreboard players add @s frv.err3 1
function frv:srs/wrong {id:86}
dialog show @s {type:"minecraft:notice",title:{text:"✘ Pas tout à fait…",color:"red",bold:true},body:[{type:"minecraft:plain_message",contents:{text:"Правильно:",color:"gray"},width:340},{type:"minecraft:plain_message",contents:{text:"Vous avez raison, je vais faire attention.",color:"green"},width:340},{type:"minecraft:plain_message",contents:{text:"» «Faire attention» — «быть внимательным», без артикля. «Il faut que vous buviez» — subjonctif от boire.",color:"gray"},width:340},{type:"minecraft:plain_message",contents:{text:"↻ Любой житель спросит это снова через игровой день.",color:"light_purple"},width:340}],action:{label:{text:"Дальше →",color:"green"},width:200,action:{type:"run_command",command:"trigger frv.ans set 8699"}},can_close_with_escape:true,pause:false}
scoreboard players set @s frv.phase 1
