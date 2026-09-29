function frv:fx/right
advancement grant @s only frv:fr/premier_mot
function frv:srs/right {id:84}
dialog show @s {type:"minecraft:notice",title:{text:"✔ Parfait !",color:"green",bold:true},body:[{type:"minecraft:plain_message",contents:{text:"Oui, je fais du vélo tous les jours.",color:"green"},width:340},{type:"minecraft:plain_message",contents:{text:"» Faire du + вид спорта: faire du vélo. «Tous les jours» — каждый день (tous — во мн. ч.).",color:"gray"},width:340}],action:{label:{text:"Дальше →",color:"green"},width:200,action:{type:"run_command",command:"trigger frv.ans set 8499"}},can_close_with_escape:true,pause:false}
scoreboard players set @s frv.phase 1
