function frv:fx/right
advancement grant @s only frv:fr/premier_mot
function frv:srs/right {id:125}
dialog show @s {type:"minecraft:notice",title:{text:"✔ Exactement !",color:"green",bold:true},body:[{type:"minecraft:plain_message",contents:{text:"Il n'est jamais trop tard pour l'agrandir !",color:"green"},width:340},{type:"minecraft:plain_message",contents:{text:"» «Il n'est jamais trop tard pour…» — «никогда не поздно…». L' = maison (прямое дополнение).",color:"gray"},width:340}],action:{label:{text:"Дальше →",color:"green"},width:200,action:{type:"run_command",command:"trigger frv.ans set 12599"}},can_close_with_escape:true,pause:false}
scoreboard players set @s frv.phase 1
