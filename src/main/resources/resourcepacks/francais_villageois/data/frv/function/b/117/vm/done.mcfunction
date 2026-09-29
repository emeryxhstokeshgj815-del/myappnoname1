function frv:fx/right
advancement grant @s only frv:fr/premier_mot
function frv:srs/right {id:117}
dialog show @s {type:"minecraft:notice",title:{text:"✔ Bravo !",color:"green",bold:true},body:[{type:"minecraft:plain_message",contents:{text:"Oui, je la construis depuis un an.",color:"green"},width:340},{type:"minecraft:plain_message",contents:{text:"» La = maison (ж.р.). Действие длится — depuis; «il y a» — «тому назад».",color:"gray"},width:340}],action:{label:{text:"Дальше →",color:"green"},width:200,action:{type:"run_command",command:"trigger frv.ans set 11799"}},can_close_with_escape:true,pause:false}
scoreboard players set @s frv.phase 1
