function frv:fx/right
advancement grant @s only frv:fr/premier_mot
function frv:srs/right {id:111}
dialog show @s {type:"minecraft:notice",title:{text:"✔ Bravo !",color:"green",bold:true},body:[{type:"minecraft:plain_message",contents:{text:"Non, elle est assez petite.",color:"green"},width:340},{type:"minecraft:plain_message",contents:{text:"» Maison — ж.р. → elle, petite. Assez стоит перед прилагательным.",color:"gray"},width:340}],action:{label:{text:"Дальше →",color:"green"},width:200,action:{type:"run_command",command:"trigger frv.ans set 11199"}},can_close_with_escape:true,pause:false}
scoreboard players set @s frv.phase 1
