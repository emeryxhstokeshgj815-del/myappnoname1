function frv:fx/right
advancement grant @s only frv:fr/premier_mot
function frv:srs/right {id:95}
dialog show @s {type:"minecraft:notice",title:{text:"✔ Exactement !",color:"green",bold:true},body:[{type:"minecraft:plain_message",contents:{text:"Je commence à huit heures.",color:"green"},width:340},{type:"minecraft:plain_message",contents:{text:"» «Во сколько» — à + время: à huit heures (мн. ч.). Перед e пишется c, не ç.",color:"gray"},width:340}],action:{label:{text:"Дальше →",color:"green"},width:200,action:{type:"run_command",command:"trigger frv.ans set 9599"}},can_close_with_escape:true,pause:false}
scoreboard players set @s frv.phase 1
