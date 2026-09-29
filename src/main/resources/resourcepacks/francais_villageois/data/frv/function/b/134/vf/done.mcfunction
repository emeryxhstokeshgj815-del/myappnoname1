function frv:fx/right
advancement grant @s only frv:fr/premier_mot
function frv:srs/right {id:134}
dialog show @s {type:"minecraft:notice",title:{text:"✔ Excellent !",color:"green",bold:true},body:[{type:"minecraft:plain_message",contents:{text:"Non, j'ai perdu en finale.",color:"green"},width:340},{type:"minecraft:plain_message",contents:{text:"» Perdre → passé composé с avoir: j'ai perdu. «Je suis perdu» — «я заблудился»! Финал — la finale.",color:"gray"},width:340}],action:{label:{text:"Дальше →",color:"green"},width:200,action:{type:"run_command",command:"trigger frv.ans set 13499"}},can_close_with_escape:true,pause:false}
scoreboard players set @s frv.phase 1
