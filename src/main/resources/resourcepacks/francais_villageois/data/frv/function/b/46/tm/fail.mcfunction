function frv:fx/wrong
scoreboard players add @s frv.err 1
execute if score @s frv.step matches ..3 run scoreboard players add @s frv.err3 1
function frv:srs/wrong {id:46}
dialog show @s {type:"minecraft:notice",title:{text:"✘ Pas tout à fait…",color:"red",bold:true},body:[{type:"minecraft:plain_message",contents:{text:"Правильно:",color:"gray"},width:340},{type:"minecraft:plain_message",contents:{text:"Oui, je te le rends demain.",color:"green"},width:340},{type:"minecraft:plain_message",contents:{text:"» Порядок местоимений: me/te/nous/vous стоят перед le/la/les: je te le rends.",color:"gray"},width:340},{type:"minecraft:plain_message",contents:{text:"↻ Любой житель спросит это снова через игровой день.",color:"light_purple"},width:340}],action:{label:{text:"Дальше →",color:"green"},width:200,action:{type:"run_command",command:"trigger frv.ans set 4699"}},can_close_with_escape:true,pause:false}
scoreboard players set @s frv.phase 1
