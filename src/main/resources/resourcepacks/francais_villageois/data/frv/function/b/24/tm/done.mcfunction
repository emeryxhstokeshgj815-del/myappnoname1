function frv:fx/right
advancement grant @s only frv:fr/premier_mot
function frv:srs/right {id:24}
dialog show @s {type:"minecraft:notice",title:{text:"✔ Parfait !",color:"green",bold:true},body:[{type:"minecraft:plain_message",contents:{text:"Ma couleur préférée, c'est le vert.",color:"green"},width:340},{type:"minecraft:plain_message",contents:{text:"» Couleur — женского рода: ma couleur préférée. А сами цвета как существительные — мужского: le vert.",color:"gray"},width:340}],action:{label:{text:"Дальше →",color:"green"},width:200,action:{type:"run_command",command:"trigger frv.ans set 2499"}},can_close_with_escape:true,pause:false}
scoreboard players set @s frv.phase 1
