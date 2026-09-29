function frv:fx/right
advancement grant @s only frv:fr/premier_mot
function frv:srs/right {id:129}
dialog show @s {type:"minecraft:notice",title:{text:"✔ Bravo !",color:"green",bold:true},body:[{type:"minecraft:plain_message",contents:{text:"Non, je joue au football.",color:"green"},width:340},{type:"minecraft:plain_message",contents:{text:"» Игры и спорт — jouer à: au football (à + le = au).",color:"gray"},width:340}],action:{label:{text:"Дальше →",color:"green"},width:200,action:{type:"run_command",command:"trigger frv.ans set 12999"}},can_close_with_escape:true,pause:false}
scoreboard players set @s frv.phase 1
