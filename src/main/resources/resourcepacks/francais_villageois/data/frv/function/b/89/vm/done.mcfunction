function frv:fx/right
advancement grant @s only frv:fr/premier_mot
function frv:srs/right {id:89}
dialog show @s {type:"minecraft:notice",title:{text:"✔ Exactement !",color:"green",bold:true},body:[{type:"minecraft:plain_message",contents:{text:"Non, je ne l'ai jamais eue.",color:"green"},width:340},{type:"minecraft:plain_message",contents:{text:"» L' = cette maladie (ж.р.) стоит перед avoir → причастие согласуется с ним: eue.",color:"gray"},width:340}],action:{label:{text:"Дальше →",color:"green"},width:200,action:{type:"run_command",command:"trigger frv.ans set 8999"}},can_close_with_escape:true,pause:false}
scoreboard players set @s frv.phase 1
