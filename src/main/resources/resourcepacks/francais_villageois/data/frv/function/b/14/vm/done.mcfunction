function frv:fx/right
advancement grant @s only frv:fr/premier_mot
function frv:srs/right {id:14}
dialog show @s {type:"minecraft:notice",title:{text:"✔ Excellent !",color:"green",bold:true},body:[{type:"minecraft:plain_message",contents:{text:"J'achèterais un grand champ de blé.",color:"green"},width:340},{type:"minecraft:plain_message",contents:{text:"» Si + imparfait → conditionnel présent: j'achèterais. Achèterai (без -s) — это будущее время.",color:"gray"},width:340}],action:{label:{text:"Дальше →",color:"green"},width:200,action:{type:"run_command",command:"trigger frv.ans set 1499"}},can_close_with_escape:true,pause:false}
scoreboard players set @s frv.phase 1
