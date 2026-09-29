function frv:fx/right
advancement grant @s only frv:fr/premier_mot
function frv:srs/right {id:13}
dialog show @s {type:"minecraft:notice",title:{text:"✔ Très bien !",color:"green",bold:true},body:[{type:"minecraft:plain_message",contents:{text:"Non merci, je n'ai plus faim.",color:"green"},width:340},{type:"minecraft:plain_message",contents:{text:"» «Ne… plus» — «больше не». Pas здесь не нужно: je n'ai plus faim.",color:"gray"},width:340}],action:{label:{text:"Дальше →",color:"green"},width:200,action:{type:"run_command",command:"trigger frv.ans set 1399"}},can_close_with_escape:true,pause:false}
scoreboard players set @s frv.phase 1
