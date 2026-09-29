function frv:fx/right
advancement grant @s only frv:fr/premier_mot
function frv:srs/right {id:43}
dialog show @s {type:"minecraft:notice",title:{text:"✔ Très bien !",color:"green",bold:true},body:[{type:"minecraft:plain_message",contents:{text:"Je lis le soir, avant de dormir.",color:"green"},width:340},{type:"minecraft:plain_message",contents:{text:"» Lire: je lis, il lit. «Avant de» + инфинитив.",color:"gray"},width:340}],action:{label:{text:"Дальше →",color:"green"},width:200,action:{type:"run_command",command:"trigger frv.ans set 4399"}},can_close_with_escape:true,pause:false}
scoreboard players set @s frv.phase 1
