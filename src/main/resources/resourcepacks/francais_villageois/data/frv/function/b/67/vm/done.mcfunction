function frv:fx/right
advancement grant @s only frv:fr/premier_mot
function frv:srs/right {id:67}
dialog show @s {type:"minecraft:notice",title:{text:"✔ Très bien !",color:"green",bold:true},body:[{type:"minecraft:plain_message",contents:{text:"Je peux vous aider ? Où voulez-vous aller ?",color:"green"},width:340},{type:"minecraft:plain_message",contents:{text:"» После модального глагола — инфинитив (aider). Местоимение — перед инфинитивом: je peux vous aider.",color:"gray"},width:340}],action:{label:{text:"Дальше →",color:"green"},width:200,action:{type:"run_command",command:"trigger frv.ans set 6799"}},can_close_with_escape:true,pause:false}
scoreboard players set @s frv.phase 1
