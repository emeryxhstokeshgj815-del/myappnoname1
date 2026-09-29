function frv:fx/right
advancement grant @s only frv:fr/premier_mot
function frv:srs/right {id:38}
dialog show @s {type:"minecraft:notice",title:{text:"✔ Excellent !",color:"green",bold:true},body:[{type:"minecraft:plain_message",contents:{text:"Oui, j'étudie les langues.",color:"green"},width:340},{type:"minecraft:plain_message",contents:{text:"» Étudier: j'étudie, tu étudies. Étude — это существительное («учёба»).",color:"gray"},width:340}],action:{label:{text:"Дальше →",color:"green"},width:200,action:{type:"run_command",command:"trigger frv.ans set 3899"}},can_close_with_escape:true,pause:false}
scoreboard players set @s frv.phase 1
