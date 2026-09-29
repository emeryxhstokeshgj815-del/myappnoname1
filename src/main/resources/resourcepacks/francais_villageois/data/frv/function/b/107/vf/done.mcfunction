function frv:fx/right
advancement grant @s only frv:fr/premier_mot
function frv:srs/right {id:107}
dialog show @s {type:"minecraft:notice",title:{text:"✔ Exactement !",color:"green",bold:true},body:[{type:"minecraft:plain_message",contents:{text:"Désolée, je ne savais pas que vous aviez besoin d'aide.",color:"green"},width:340},{type:"minecraft:plain_message",contents:{text:"» Savoir que — «знать, что» (факт). Connaître с que не употребляется. «Avoir besoin d'aide» — с d'.",color:"gray"},width:340}],action:{label:{text:"Дальше →",color:"green"},width:200,action:{type:"run_command",command:"trigger frv.ans set 10799"}},can_close_with_escape:true,pause:false}
scoreboard players set @s frv.phase 1
