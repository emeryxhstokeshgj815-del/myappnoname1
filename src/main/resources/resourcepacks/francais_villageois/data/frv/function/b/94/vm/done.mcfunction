function frv:fx/right
advancement grant @s only frv:fr/premier_mot
function frv:srs/right {id:94}
dialog show @s {type:"minecraft:notice",title:{text:"✔ Super !",color:"green",bold:true},body:[{type:"minecraft:plain_message",contents:{text:"Oui, j'ai besoin d'un marteau.",color:"green"},width:340},{type:"minecraft:plain_message",contents:{text:"» Avoir besoin de + существительное. Marteau — м.р.",color:"gray"},width:340}],action:{label:{text:"Дальше →",color:"green"},width:200,action:{type:"run_command",command:"trigger frv.ans set 9499"}},can_close_with_escape:true,pause:false}
scoreboard players set @s frv.phase 1
