function frv:fx/right
advancement grant @s only frv:fr/premier_mot
function frv:srs/right {id:26}
dialog show @s {type:"minecraft:notice",title:{text:"✔ Excellent !",color:"green",bold:true},body:[{type:"minecraft:plain_message",contents:{text:"Je l'ai acheté au marché.",color:"green"},width:340},{type:"minecraft:plain_message",contents:{text:"» À + le = au: au marché. L' = ce chapeau (м.р.), поэтому acheté без -e.",color:"gray"},width:340}],action:{label:{text:"Дальше →",color:"green"},width:200,action:{type:"run_command",command:"trigger frv.ans set 2699"}},can_close_with_escape:true,pause:false}
scoreboard players set @s frv.phase 1
