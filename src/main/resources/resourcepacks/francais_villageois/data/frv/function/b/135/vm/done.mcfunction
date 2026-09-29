function frv:fx/right
advancement grant @s only frv:fr/premier_mot
function frv:srs/right {id:135}
dialog show @s {type:"minecraft:notice",title:{text:"✔ Bravo !",color:"green",bold:true},body:[{type:"minecraft:plain_message",contents:{text:"Oui, je faisais de la natation.",color:"green"},width:340},{type:"minecraft:plain_message",contents:{text:"» Faire de + вид спорта: de la natation (ж.р.). Привычка в прошлом — imparfait.",color:"gray"},width:340}],action:{label:{text:"Дальше →",color:"green"},width:200,action:{type:"run_command",command:"trigger frv.ans set 13599"}},can_close_with_escape:true,pause:false}
scoreboard players set @s frv.phase 1
