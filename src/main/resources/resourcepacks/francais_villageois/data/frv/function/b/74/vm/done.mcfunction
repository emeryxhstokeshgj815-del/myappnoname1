function frv:fx/right
advancement grant @s only frv:fr/premier_mot
function frv:srs/right {id:74}
dialog show @s {type:"minecraft:notice",title:{text:"✔ Excellent !",color:"green",bold:true},body:[{type:"minecraft:plain_message",contents:{text:"J'ai mal à la tête.",color:"green"},width:340},{type:"minecraft:plain_message",contents:{text:"» «Avoir mal à» + часть тела: j'ai mal à la tête, au ventre (à + le = au).",color:"gray"},width:340}],action:{label:{text:"Дальше →",color:"green"},width:200,action:{type:"run_command",command:"trigger frv.ans set 7499"}},can_close_with_escape:true,pause:false}
scoreboard players set @s frv.phase 1
