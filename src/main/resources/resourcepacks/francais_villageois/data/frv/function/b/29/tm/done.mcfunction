function frv:fx/right
advancement grant @s only frv:fr/premier_mot
function frv:srs/right {id:29}
dialog show @s {type:"minecraft:notice",title:{text:"✔ Exactement !",color:"green",bold:true},body:[{type:"minecraft:plain_message",contents:{text:"Je préfère la rouge, elle est plus jolie.",color:"green"},width:340},{type:"minecraft:plain_message",contents:{text:"» «La rouge» = красное (платье): robe — ж.р., поэтому elle и jolie.",color:"gray"},width:340}],action:{label:{text:"Дальше →",color:"green"},width:200,action:{type:"run_command",command:"trigger frv.ans set 2999"}},can_close_with_escape:true,pause:false}
scoreboard players set @s frv.phase 1
