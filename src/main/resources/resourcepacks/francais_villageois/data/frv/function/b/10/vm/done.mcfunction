function frv:fx/right
advancement grant @s only frv:fr/premier_mot
function frv:srs/right {id:10}
dialog show @s {type:"minecraft:notice",title:{text:"✔ Super !",color:"green",bold:true},body:[{type:"minecraft:plain_message",contents:{text:"Oui, je fais souvent la cuisine le soir.",color:"green"},width:340},{type:"minecraft:plain_message",contents:{text:"» «Faire la cuisine» — готовить (cuisine — ж.р.). «Le soir» — «по вечерам», без предлога.",color:"gray"},width:340}],action:{label:{text:"Дальше →",color:"green"},width:200,action:{type:"run_command",command:"trigger frv.ans set 1099"}},can_close_with_escape:true,pause:false}
scoreboard players set @s frv.phase 1
