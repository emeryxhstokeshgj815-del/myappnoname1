function frv:fx/right
advancement grant @s only frv:fr/premier_mot
function frv:srs/right {id:116}
dialog show @s {type:"minecraft:notice",title:{text:"✔ Excellent !",color:"green",bold:true},body:[{type:"minecraft:plain_message",contents:{text:"J'ai rendu visite à mes grands-parents.",color:"green"},width:340},{type:"minecraft:plain_message",contents:{text:"» Навестить людей — rendre visite à qqn. Visiter — только места (visiter un musée).",color:"gray"},width:340}],action:{label:{text:"Дальше →",color:"green"},width:200,action:{type:"run_command",command:"trigger frv.ans set 11699"}},can_close_with_escape:true,pause:false}
scoreboard players set @s frv.phase 1
