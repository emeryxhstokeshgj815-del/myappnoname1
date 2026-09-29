function frv:fx/right
advancement grant @s only frv:fr/premier_mot
function frv:srs/right {id:15}
dialog show @s {type:"minecraft:notice",title:{text:"✔ Bravo !",color:"green",bold:true},body:[{type:"minecraft:plain_message",contents:{text:"Avec plaisir, il a l'air délicieux !",color:"green"},width:340},{type:"minecraft:plain_message",contents:{text:"» «Avoir l'air» — выглядеть. Gâteau — м.р. → délicieux. А житель сказал «il faut que tu goûtes» — это subjonctif.",color:"gray"},width:340}],action:{label:{text:"Дальше →",color:"green"},width:200,action:{type:"run_command",command:"trigger frv.ans set 1599"}},can_close_with_escape:true,pause:false}
scoreboard players set @s frv.phase 1
