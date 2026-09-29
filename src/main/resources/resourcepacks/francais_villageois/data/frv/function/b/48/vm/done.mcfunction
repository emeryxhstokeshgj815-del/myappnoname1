function frv:fx/right
advancement grant @s only frv:fr/premier_mot
function frv:srs/right {id:48}
dialog show @s {type:"minecraft:notice",title:{text:"✔ Parfait !",color:"green",bold:true},body:[{type:"minecraft:plain_message",contents:{text:"Oui, je le connais bien.",color:"green"},width:340},{type:"minecraft:plain_message",contents:{text:"» Connaître — знать человека или место; savoir — знать факт, уметь. Le стоит перед глаголом.",color:"gray"},width:340}],action:{label:{text:"Дальше →",color:"green"},width:200,action:{type:"run_command",command:"trigger frv.ans set 4899"}},can_close_with_escape:true,pause:false}
scoreboard players set @s frv.phase 1
