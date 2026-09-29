function frv:fx/right
advancement grant @s only frv:fr/premier_mot
function frv:srs/right {id:56}
dialog show @s {type:"minecraft:notice",title:{text:"✔ Excellent !",color:"green",bold:true},body:[{type:"minecraft:plain_message",contents:{text:"Merci ! C'est loin d'ici ?",color:"green"},width:340},{type:"minecraft:plain_message",contents:{text:"» «Loin d'ici» — далеко отсюда; de + ici → d'ici.",color:"gray"},width:340}],action:{label:{text:"Дальше →",color:"green"},width:200,action:{type:"run_command",command:"trigger frv.ans set 5699"}},can_close_with_escape:true,pause:false}
scoreboard players set @s frv.phase 1
