function frv:fx/right
advancement grant @s only frv:fr/premier_mot
function frv:srs/right {id:64}
dialog show @s {type:"minecraft:notice",title:{text:"✔ Super !",color:"green",bold:true},body:[{type:"minecraft:plain_message",contents:{text:"Tu traverses le pont et tu tournes à gauche.",color:"green"},width:340},{type:"minecraft:plain_message",contents:{text:"» Traverser что-то — без предлога: traverser le pont. Налево — à gauche. На «ты»: tu traverses, tu tournes.",color:"gray"},width:340}],action:{label:{text:"Дальше →",color:"green"},width:200,action:{type:"run_command",command:"trigger frv.ans set 6499"}},can_close_with_escape:true,pause:false}
scoreboard players set @s frv.phase 1
