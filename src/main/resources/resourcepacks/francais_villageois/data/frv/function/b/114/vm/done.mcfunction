function frv:fx/right
advancement grant @s only frv:fr/premier_mot
function frv:srs/right {id:114}
dialog show @s {type:"minecraft:notice",title:{text:"✔ Parfait !",color:"green",bold:true},body:[{type:"minecraft:plain_message",contents:{text:"Elle est à côté du salon.",color:"green"},width:340},{type:"minecraft:plain_message",contents:{text:"» De + le = du: à côté du salon. Cuisine — ж.р. → elle.",color:"gray"},width:340}],action:{label:{text:"Дальше →",color:"green"},width:200,action:{type:"run_command",command:"trigger frv.ans set 11499"}},can_close_with_escape:true,pause:false}
scoreboard players set @s frv.phase 1
