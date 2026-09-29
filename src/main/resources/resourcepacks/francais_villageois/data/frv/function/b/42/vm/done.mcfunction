function frv:fx/right
advancement grant @s only frv:fr/premier_mot
function frv:srs/right {id:42}
dialog show @s {type:"minecraft:notice",title:{text:"✔ Parfait !",color:"green",bold:true},body:[{type:"minecraft:plain_message",contents:{text:"Non, je n'ai pas de stylo.",color:"green"},width:340},{type:"minecraft:plain_message",contents:{text:"» После отрицания un/une/des → de: je n'ai pas de stylo. Ne + ai → n'ai.",color:"gray"},width:340}],action:{label:{text:"Дальше →",color:"green"},width:200,action:{type:"run_command",command:"trigger frv.ans set 4299"}},can_close_with_escape:true,pause:false}
scoreboard players set @s frv.phase 1
