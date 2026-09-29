function frv:fx/right
advancement grant @s only frv:fr/premier_mot
function frv:srs/right {id:70}
dialog show @s {type:"minecraft:notice",title:{text:"✔ Super !",color:"green",bold:true},body:[{type:"minecraft:plain_message",contents:{text:"Ah bon ? Qu'est-ce qui a changé ?",color:"green"},width:340},{type:"minecraft:plain_message",contents:{text:"» «Qu'est-ce qui» — «что» в роли подлежащего. «Qu'est-ce que» — «что» в роли дополнения, «qui est-ce qui» — «кто».",color:"gray"},width:340}],action:{label:{text:"Дальше →",color:"green"},width:200,action:{type:"run_command",command:"trigger frv.ans set 7099"}},can_close_with_escape:true,pause:false}
scoreboard players set @s frv.phase 1
