function frv:fx/right
advancement grant @s only frv:fr/premier_mot
function frv:srs/right {id:110}
dialog show @s {type:"minecraft:notice",title:{text:"✔ Excellent !",color:"green",bold:true},body:[{type:"minecraft:plain_message",contents:{text:"Oui, j'ai un frère et une sœur.",color:"green"},width:340},{type:"minecraft:plain_message",contents:{text:"» Frère — м.р. (un), sœur — ж.р. (une).",color:"gray"},width:340}],action:{label:{text:"Дальше →",color:"green"},width:200,action:{type:"run_command",command:"trigger frv.ans set 11099"}},can_close_with_escape:true,pause:false}
scoreboard players set @s frv.phase 1
