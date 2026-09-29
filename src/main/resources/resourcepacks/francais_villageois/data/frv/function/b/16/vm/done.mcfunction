function frv:fx/right
advancement grant @s only frv:fr/premier_mot
function frv:srs/right {id:16}
dialog show @s {type:"minecraft:notice",title:{text:"✔ Super !",color:"green",bold:true},body:[{type:"minecraft:plain_message",contents:{text:"Parfait, je le prends.",color:"green"},width:340},{type:"minecraft:plain_message",contents:{text:"» Le заменяет прямое дополнение (ce saumon, м.р.) и стоит перед глаголом. Lui — для косвенного (кому?).",color:"gray"},width:340}],action:{label:{text:"Дальше →",color:"green"},width:200,action:{type:"run_command",command:"trigger frv.ans set 1699"}},can_close_with_escape:true,pause:false}
scoreboard players set @s frv.phase 1
