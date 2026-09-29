function frv:fx/right
advancement grant @s only frv:fr/premier_mot
function frv:srs/right {id:40}
dialog show @s {type:"minecraft:notice",title:{text:"✔ Super !",color:"green",bold:true},body:[{type:"minecraft:plain_message",contents:{text:"Oui, merci, il est intéressant.",color:"green"},width:340},{type:"minecraft:plain_message",contents:{text:"» Livre — м.р. → il est intéressant (с двумя s).",color:"gray"},width:340}],action:{label:{text:"Дальше →",color:"green"},width:200,action:{type:"run_command",command:"trigger frv.ans set 4099"}},can_close_with_escape:true,pause:false}
scoreboard players set @s frv.phase 1
