function frv:fx/right
advancement grant @s only frv:fr/premier_mot
function frv:srs/right {id:57}
dialog show @s {type:"minecraft:notice",title:{text:"✔ Bravo !",color:"green",bold:true},body:[{type:"minecraft:plain_message",contents:{text:"J'habite à Moscou, en Russie.",color:"green"},width:340},{type:"minecraft:plain_message",contents:{text:"» Города — с à (à Moscou), страны ж.р. — с en (en Russie), страны м.р. — с au (au Canada).",color:"gray"},width:340}],action:{label:{text:"Дальше →",color:"green"},width:200,action:{type:"run_command",command:"trigger frv.ans set 5799"}},can_close_with_escape:true,pause:false}
scoreboard players set @s frv.phase 1
