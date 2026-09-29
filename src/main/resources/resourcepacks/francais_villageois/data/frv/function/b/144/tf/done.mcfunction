function frv:fx/right
advancement grant @s only frv:fr/premier_mot
function frv:srs/right {id:144}
dialog show @s {type:"minecraft:notice",title:{text:"✔ Parfait !",color:"green",bold:true},body:[{type:"minecraft:plain_message",contents:{text:"Je suis ravie qu'il t'ait plu !",color:"green"},width:340},{type:"minecraft:plain_message",contents:{text:"» После «je suis ravi que» — subjonctif: qu'il t'ait plu. Причастие от plaire — plu.",color:"gray"},width:340}],action:{label:{text:"Дальше →",color:"green"},width:200,action:{type:"run_command",command:"trigger frv.ans set 14499"}},can_close_with_escape:true,pause:false}
scoreboard players set @s frv.phase 1
