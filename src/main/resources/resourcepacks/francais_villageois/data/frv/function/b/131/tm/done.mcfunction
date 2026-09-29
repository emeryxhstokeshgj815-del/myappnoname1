function frv:fx/right
advancement grant @s only frv:fr/premier_mot
function frv:srs/right {id:131}
dialog show @s {type:"minecraft:notice",title:{text:"✔ Exactement !",color:"green",bold:true},body:[{type:"minecraft:plain_message",contents:{text:"Oui, c'est très amusant !",color:"green"},width:340},{type:"minecraft:plain_message",contents:{text:"» После c'est прилагательное — в мужском роде: c'est amusant. Amusé — «развеселившийся» (о человеке).",color:"gray"},width:340}],action:{label:{text:"Дальше →",color:"green"},width:200,action:{type:"run_command",command:"trigger frv.ans set 13199"}},can_close_with_escape:true,pause:false}
scoreboard players set @s frv.phase 1
