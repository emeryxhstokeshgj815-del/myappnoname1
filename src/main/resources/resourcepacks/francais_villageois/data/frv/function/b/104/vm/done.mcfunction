function frv:fx/right
advancement grant @s only frv:fr/premier_mot
function frv:srs/right {id:104}
dialog show @s {type:"minecraft:notice",title:{text:"✔ Excellent !",color:"green",bold:true},body:[{type:"minecraft:plain_message",contents:{text:"Je fabriquerais une épée légendaire.",color:"green"},width:340},{type:"minecraft:plain_message",contents:{text:"» Гипотеза → conditionnel: fabriquerais. Épée — ж.р.",color:"gray"},width:340}],action:{label:{text:"Дальше →",color:"green"},width:200,action:{type:"run_command",command:"trigger frv.ans set 10499"}},can_close_with_escape:true,pause:false}
scoreboard players set @s frv.phase 1
