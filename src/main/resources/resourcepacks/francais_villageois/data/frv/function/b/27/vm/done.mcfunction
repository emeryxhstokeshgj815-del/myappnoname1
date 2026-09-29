function frv:fx/right
advancement grant @s only frv:fr/premier_mot
function frv:srs/right {id:27}
dialog show @s {type:"minecraft:notice",title:{text:"✔ Bravo !",color:"green",bold:true},body:[{type:"minecraft:plain_message",contents:{text:"Oui, je voudrais une taille plus grande.",color:"green"},width:340},{type:"minecraft:plain_message",contents:{text:"» Taille (размер) — ж.р.: une taille plus grande.",color:"gray"},width:340}],action:{label:{text:"Дальше →",color:"green"},width:200,action:{type:"run_command",command:"trigger frv.ans set 2799"}},can_close_with_escape:true,pause:false}
scoreboard players set @s frv.phase 1
