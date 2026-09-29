function frv:fx/right
advancement grant @s only frv:fr/premier_mot
function frv:srs/right {id:103}
dialog show @s {type:"minecraft:notice",title:{text:"✔ Très bien !",color:"green",bold:true},body:[{type:"minecraft:plain_message",contents:{text:"C'est moi qui l'ai faite.",color:"green"},width:340},{type:"minecraft:plain_message",contents:{text:"» «C'est moi qui» + глагол в 1-м лице: qui l'ai. Pioche — ж.р. и стоит перед avoir → faite.",color:"gray"},width:340}],action:{label:{text:"Дальше →",color:"green"},width:200,action:{type:"run_command",command:"trigger frv.ans set 10399"}},can_close_with_escape:true,pause:false}
scoreboard players set @s frv.phase 1
