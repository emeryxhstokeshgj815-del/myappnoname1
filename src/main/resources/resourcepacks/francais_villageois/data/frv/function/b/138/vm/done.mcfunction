function frv:fx/right
advancement grant @s only frv:fr/premier_mot
function frv:srs/right {id:138}
dialog show @s {type:"minecraft:notice",title:{text:"✔ Parfait !",color:"green",bold:true},body:[{type:"minecraft:plain_message",contents:{text:"Oui, je l'ai vu deux fois.",color:"green"},width:340},{type:"minecraft:plain_message",contents:{text:"» «Раз» — fois: deux fois (temps — «время»). L' = film (м.р.) → vu.",color:"gray"},width:340}],action:{label:{text:"Дальше →",color:"green"},width:200,action:{type:"run_command",command:"trigger frv.ans set 13899"}},can_close_with_escape:true,pause:false}
scoreboard players set @s frv.phase 1
