function frv:fx/right
advancement grant @s only frv:fr/premier_mot
function frv:srs/right {id:77}
dialog show @s {type:"minecraft:notice",title:{text:"✔ Exactement !",color:"green",bold:true},body:[{type:"minecraft:plain_message",contents:{text:"Non, je dors mal.",color:"green"},width:340},{type:"minecraft:plain_message",contents:{text:"» Dormir: je dors. При глаголе — наречие mal («плохо»), а не прилагательное mauvais.",color:"gray"},width:340}],action:{label:{text:"Дальше →",color:"green"},width:200,action:{type:"run_command",command:"trigger frv.ans set 7799"}},can_close_with_escape:true,pause:false}
scoreboard players set @s frv.phase 1
