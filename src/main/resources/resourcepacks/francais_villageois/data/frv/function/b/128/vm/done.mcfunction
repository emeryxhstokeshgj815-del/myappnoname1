function frv:fx/right
advancement grant @s only frv:fr/premier_mot
function frv:srs/right {id:128}
dialog show @s {type:"minecraft:notice",title:{text:"✔ Excellent !",color:"green",bold:true},body:[{type:"minecraft:plain_message",contents:{text:"Oui, je joue de la guitare.",color:"green"},width:340},{type:"minecraft:plain_message",contents:{text:"» Инструменты — jouer de (de la guitare, du piano), игры — jouer à (au football).",color:"gray"},width:340}],action:{label:{text:"Дальше →",color:"green"},width:200,action:{type:"run_command",command:"trigger frv.ans set 12899"}},can_close_with_escape:true,pause:false}
scoreboard players set @s frv.phase 1
