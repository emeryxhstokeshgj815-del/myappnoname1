function frv:fx/right
advancement grant @s only frv:fr/premier_mot
function frv:srs/right {id:54}
dialog show @s {type:"minecraft:notice",title:{text:"✔ Parfait !",color:"green",bold:true},body:[{type:"minecraft:plain_message",contents:{text:"C'est vrai, on dirait qu'il est neuf.",color:"green"},width:340},{type:"minecraft:plain_message",contents:{text:"» Neuf — «новый, неношеный»; nouveau — «новый, другой». После «on dirait que» — индикатив. А «bien que» у жителя требует subjonctif.",color:"gray"},width:340}],action:{label:{text:"Дальше →",color:"green"},width:200,action:{type:"run_command",command:"trigger frv.ans set 5499"}},can_close_with_escape:true,pause:false}
scoreboard players set @s frv.phase 1
