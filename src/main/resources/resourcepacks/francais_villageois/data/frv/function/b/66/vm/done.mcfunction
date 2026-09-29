function frv:fx/right
advancement grant @s only frv:fr/premier_mot
function frv:srs/right {id:66}
dialog show @s {type:"minecraft:notice",title:{text:"✔ Parfait !",color:"green",bold:true},body:[{type:"minecraft:plain_message",contents:{text:"Je vais aller au Canada.",color:"green"},width:340},{type:"minecraft:plain_message",contents:{text:"» Страны м.р. — au: au Canada, au Japon. Futur proche: aller + инфинитив.",color:"gray"},width:340}],action:{label:{text:"Дальше →",color:"green"},width:200,action:{type:"run_command",command:"trigger frv.ans set 6699"}},can_close_with_escape:true,pause:false}
scoreboard players set @s frv.phase 1
