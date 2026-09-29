function frv:fx/right
advancement grant @s only frv:fr/premier_mot
function frv:srs/right {id:45}
dialog show @s {type:"minecraft:notice",title:{text:"✔ Bravo !",color:"green",bold:true},body:[{type:"minecraft:plain_message",contents:{text:"J'apprends le français depuis un mois.",color:"green"},width:340},{type:"minecraft:plain_message",contents:{text:"» Действие длится до сих пор → présent + depuis. «Pour un mois» — «на месяц» (вперёд).",color:"gray"},width:340}],action:{label:{text:"Дальше →",color:"green"},width:200,action:{type:"run_command",command:"trigger frv.ans set 4599"}},can_close_with_escape:true,pause:false}
scoreboard players set @s frv.phase 1
