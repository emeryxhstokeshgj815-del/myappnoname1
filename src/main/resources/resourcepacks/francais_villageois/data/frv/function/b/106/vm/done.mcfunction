function frv:fx/right
advancement grant @s only frv:fr/premier_mot
function frv:srs/right {id:106}
dialog show @s {type:"minecraft:notice",title:{text:"✔ Super !",color:"green",bold:true},body:[{type:"minecraft:plain_message",contents:{text:"C'est pour ça que vous travaillez si bien !",color:"green"},width:340},{type:"minecraft:plain_message",contents:{text:"» Bien — наречие при глаголе, bon — прилагательное. Vous travaillez — спрягаем, не инфинитив.",color:"gray"},width:340}],action:{label:{text:"Дальше →",color:"green"},width:200,action:{type:"run_command",command:"trigger frv.ans set 10699"}},can_close_with_escape:true,pause:false}
scoreboard players set @s frv.phase 1
