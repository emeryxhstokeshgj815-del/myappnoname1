function frv:fx/right
advancement grant @s only frv:fr/premier_mot
function frv:srs/right {id:6}
dialog show @s {type:"minecraft:notice",title:{text:"✔ Parfait !",color:"green",bold:true},body:[{type:"minecraft:plain_message",contents:{text:"Non merci, je ne mange pas de poisson.",color:"green"},width:340},{type:"minecraft:plain_message",contents:{text:"» После отрицания du/de la/des превращаются в de: je ne mange pas de poisson.",color:"gray"},width:340}],action:{label:{text:"Дальше →",color:"green"},width:200,action:{type:"run_command",command:"trigger frv.ans set 699"}},can_close_with_escape:true,pause:false}
scoreboard players set @s frv.phase 1
