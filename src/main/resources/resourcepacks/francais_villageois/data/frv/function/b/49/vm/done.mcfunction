function frv:fx/right
advancement grant @s only frv:fr/premier_mot
function frv:srs/right {id:49}
dialog show @s {type:"minecraft:notice",title:{text:"✔ Très bien !",color:"green",bold:true},body:[{type:"minecraft:plain_message",contents:{text:"Pas encore, il me reste deux exercices.",color:"green"},width:340},{type:"minecraft:plain_message",contents:{text:"» «Il me reste…» — «у меня осталось…» (безличная конструкция). Deux exercices — мн. ч.",color:"gray"},width:340}],action:{label:{text:"Дальше →",color:"green"},width:200,action:{type:"run_command",command:"trigger frv.ans set 4999"}},can_close_with_escape:true,pause:false}
scoreboard players set @s frv.phase 1
