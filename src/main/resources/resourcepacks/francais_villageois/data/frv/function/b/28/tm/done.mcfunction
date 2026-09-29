function frv:fx/right
advancement grant @s only frv:fr/premier_mot
function frv:srs/right {id:28}
dialog show @s {type:"minecraft:notice",title:{text:"✔ Super !",color:"green",bold:true},body:[{type:"minecraft:plain_message",contents:{text:"Je mets un imperméable et des bottes.",color:"green"},width:340},{type:"minecraft:plain_message",contents:{text:"» Mettre: je mets, tu mets, il met. Во мн. ч. неопределённый артикль — des bottes.",color:"gray"},width:340}],action:{label:{text:"Дальше →",color:"green"},width:200,action:{type:"run_command",command:"trigger frv.ans set 2899"}},can_close_with_escape:true,pause:false}
scoreboard players set @s frv.phase 1
