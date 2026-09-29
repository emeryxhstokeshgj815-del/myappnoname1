function frv:fx/right
advancement grant @s only frv:fr/premier_mot
function frv:srs/right {id:31}
dialog show @s {type:"minecraft:notice",title:{text:"✔ Très bien !",color:"green",bold:true},body:[{type:"minecraft:plain_message",contents:{text:"Tant pis, je les prends quand même.",color:"green"},width:340},{type:"minecraft:plain_message",contents:{text:"» Les заменяет «ces gants» (прямое дополнение, мн. ч.) и стоит перед глаголом. Leur — «им».",color:"gray"},width:340}],action:{label:{text:"Дальше →",color:"green"},width:200,action:{type:"run_command",command:"trigger frv.ans set 3199"}},can_close_with_escape:true,pause:false}
scoreboard players set @s frv.phase 1
