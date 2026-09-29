function frv:fx/right
advancement grant @s only frv:fr/premier_mot
function frv:srs/right {id:96}
dialog show @s {type:"minecraft:notice",title:{text:"✔ Parfait !",color:"green",bold:true},body:[{type:"minecraft:plain_message",contents:{text:"Non, je ne travaille pas le samedi.",color:"green"},width:340},{type:"minecraft:plain_message",contents:{text:"» Отрицание — ne… pas вокруг глагола (в устной речи ne глотают, но писать его надо). «Le samedi» — по субботам.",color:"gray"},width:340}],action:{label:{text:"Дальше →",color:"green"},width:200,action:{type:"run_command",command:"trigger frv.ans set 9699"}},can_close_with_escape:true,pause:false}
scoreboard players set @s frv.phase 1
