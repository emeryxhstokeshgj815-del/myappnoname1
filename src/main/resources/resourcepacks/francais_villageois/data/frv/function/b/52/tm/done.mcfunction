function frv:fx/right
advancement grant @s only frv:fr/premier_mot
function frv:srs/right {id:52}
dialog show @s {type:"minecraft:notice",title:{text:"✔ Super !",color:"green",bold:true},body:[{type:"minecraft:plain_message",contents:{text:"D'accord, je le lirai ce soir.",color:"green"},width:340},{type:"minecraft:plain_message",contents:{text:"» Будущее от lire — je lirai. Lirais (с -s) — conditionnel, «прочитал бы».",color:"gray"},width:340}],action:{label:{text:"Дальше →",color:"green"},width:200,action:{type:"run_command",command:"trigger frv.ans set 5299"}},can_close_with_escape:true,pause:false}
scoreboard players set @s frv.phase 1
