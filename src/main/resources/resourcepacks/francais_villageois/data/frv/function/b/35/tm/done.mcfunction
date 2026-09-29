function frv:fx/right
advancement grant @s only frv:fr/premier_mot
function frv:srs/right {id:35}
dialog show @s {type:"minecraft:notice",title:{text:"✔ Exactement !",color:"green",bold:true},body:[{type:"minecraft:plain_message",contents:{text:"Celui qui est accroché près de la porte.",color:"green"},width:340},{type:"minecraft:plain_message",contents:{text:"» Celui — «тот» для м.р. (manteau). Qui — подлежащее придаточного. «Près de» — с de.",color:"gray"},width:340}],action:{label:{text:"Дальше →",color:"green"},width:200,action:{type:"run_command",command:"trigger frv.ans set 3599"}},can_close_with_escape:true,pause:false}
scoreboard players set @s frv.phase 1
