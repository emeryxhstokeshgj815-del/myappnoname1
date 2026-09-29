function frv:fx/right
advancement grant @s only frv:fr/premier_mot
function frv:srs/right {id:1}
dialog show @s {type:"minecraft:notice",title:{text:"✔ Très bien !",color:"green",bold:true},body:[{type:"minecraft:plain_message",contents:{text:"Je voudrais du pain, s'il vous plaît.",color:"green"},width:340},{type:"minecraft:plain_message",contents:{text:"» Хлеб «немного, какое-то количество» — частичный артикль: du pain (de + le). «De pain» — только после отрицания или количества.",color:"gray"},width:340}],action:{label:{text:"Дальше →",color:"green"},width:200,action:{type:"run_command",command:"trigger frv.ans set 199"}},can_close_with_escape:true,pause:false}
scoreboard players set @s frv.phase 1
