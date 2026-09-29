function frv:fx/right
advancement grant @s only frv:fr/premier_mot
function frv:srs/right {id:98}
dialog show @s {type:"minecraft:notice",title:{text:"✔ Excellent !",color:"green",bold:true},body:[{type:"minecraft:plain_message",contents:{text:"Avant, je travaillais dans une école.",color:"green"},width:340},{type:"minecraft:plain_message",contents:{text:"» Привычное, длительное действие в прошлом — imparfait: je travaillais.",color:"gray"},width:340}],action:{label:{text:"Дальше →",color:"green"},width:200,action:{type:"run_command",command:"trigger frv.ans set 9899"}},can_close_with_escape:true,pause:false}
scoreboard players set @s frv.phase 1
