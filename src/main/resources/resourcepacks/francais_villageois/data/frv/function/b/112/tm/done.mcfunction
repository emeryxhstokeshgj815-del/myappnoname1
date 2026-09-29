function frv:fx/right
advancement grant @s only frv:fr/premier_mot
function frv:srs/right {id:112}
dialog show @s {type:"minecraft:notice",title:{text:"✔ Super !",color:"green",bold:true},body:[{type:"minecraft:plain_message",contents:{text:"Il y a trois pièces.",color:"green"},width:340},{type:"minecraft:plain_message",contents:{text:"» «Il y a» — «есть, имеется», форма не меняется. После trois — мн. ч.",color:"gray"},width:340}],action:{label:{text:"Дальше →",color:"green"},width:200,action:{type:"run_command",command:"trigger frv.ans set 11299"}},can_close_with_escape:true,pause:false}
scoreboard players set @s frv.phase 1
