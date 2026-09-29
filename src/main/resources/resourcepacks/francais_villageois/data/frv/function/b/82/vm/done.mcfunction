function frv:fx/right
advancement grant @s only frv:fr/premier_mot
function frv:srs/right {id:82}
dialog show @s {type:"minecraft:notice",title:{text:"✔ Super !",color:"green",bold:true},body:[{type:"minecraft:plain_message",contents:{text:"Je dois le prendre avant ou après le repas ?",color:"green"},width:340},{type:"minecraft:plain_message",contents:{text:"» После devoir — инфинитив (prendre), а местоимение le — перед ним.",color:"gray"},width:340}],action:{label:{text:"Дальше →",color:"green"},width:200,action:{type:"run_command",command:"trigger frv.ans set 8299"}},can_close_with_escape:true,pause:false}
scoreboard players set @s frv.phase 1
