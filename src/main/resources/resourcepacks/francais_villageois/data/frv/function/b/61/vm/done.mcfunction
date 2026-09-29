function frv:fx/right
advancement grant @s only frv:fr/premier_mot
function frv:srs/right {id:61}
dialog show @s {type:"minecraft:notice",title:{text:"✔ Très bien !",color:"green",bold:true},body:[{type:"minecraft:plain_message",contents:{text:"Il est trois heures et demie.",color:"green"},width:340},{type:"minecraft:plain_message",contents:{text:"» Время: il est … heures. После heures — et demie (с -e, потому что heure — ж.р.).",color:"gray"},width:340}],action:{label:{text:"Дальше →",color:"green"},width:200,action:{type:"run_command",command:"trigger frv.ans set 6199"}},can_close_with_escape:true,pause:false}
scoreboard players set @s frv.phase 1
