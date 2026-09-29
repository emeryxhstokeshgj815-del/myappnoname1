function frv:fx/right
advancement grant @s only frv:fr/premier_mot
function frv:srs/right {id:119}
dialog show @s {type:"minecraft:notice",title:{text:"✔ Exactement !",color:"green",bold:true},body:[{type:"minecraft:plain_message",contents:{text:"Il en faut au moins cent.",color:"green"},width:340},{type:"minecraft:plain_message",contents:{text:"» En заменяет «de pierres»: il en faut cent. Cent — без -s (только deux cents, trois cents…).",color:"gray"},width:340}],action:{label:{text:"Дальше →",color:"green"},width:200,action:{type:"run_command",command:"trigger frv.ans set 11999"}},can_close_with_escape:true,pause:false}
scoreboard players set @s frv.phase 1
