function frv:fx/right
advancement grant @s only frv:fr/premier_mot
function frv:srs/right {id:73}
dialog show @s {type:"minecraft:notice",title:{text:"✔ Très bien !",color:"green",bold:true},body:[{type:"minecraft:plain_message",contents:{text:"Oui, je suis très fatiguée.",color:"green"},width:340},{type:"minecraft:plain_message",contents:{text:"» Быть уставшим — être fatigué (être + прилагательное). Fatigue — существительное.",color:"gray"},width:340}],action:{label:{text:"Дальше →",color:"green"},width:200,action:{type:"run_command",command:"trigger frv.ans set 7399"}},can_close_with_escape:true,pause:false}
scoreboard players set @s frv.phase 1
