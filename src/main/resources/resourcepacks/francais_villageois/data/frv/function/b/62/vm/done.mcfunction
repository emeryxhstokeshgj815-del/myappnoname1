function frv:fx/right
advancement grant @s only frv:fr/premier_mot
function frv:srs/right {id:62}
dialog show @s {type:"minecraft:notice",title:{text:"✔ Excellent !",color:"green",bold:true},body:[{type:"minecraft:plain_message",contents:{text:"Oui, il y en a une en face de l'église.",color:"green"},width:340},{type:"minecraft:plain_message",contents:{text:"» «Il y en a une» — «есть одна (такая)»: en обязателен. Boulangerie — ж.р. → une. «En face de» — напротив.",color:"gray"},width:340}],action:{label:{text:"Дальше →",color:"green"},width:200,action:{type:"run_command",command:"trigger frv.ans set 6299"}},can_close_with_escape:true,pause:false}
scoreboard players set @s frv.phase 1
