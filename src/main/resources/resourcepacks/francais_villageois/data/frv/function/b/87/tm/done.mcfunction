function frv:fx/right
advancement grant @s only frv:fr/premier_mot
function frv:srs/right {id:87}
dialog show @s {type:"minecraft:notice",title:{text:"✔ Bravo !",color:"green",bold:true},body:[{type:"minecraft:plain_message",contents:{text:"C'est vrai, j'aurais dû faire attention.",color:"green"},width:340},{type:"minecraft:plain_message",contents:{text:"» Сожаление о прошлом — conditionnel passé: j'aurais dû + инфинитив.",color:"gray"},width:340}],action:{label:{text:"Дальше →",color:"green"},width:200,action:{type:"run_command",command:"trigger frv.ans set 8799"}},can_close_with_escape:true,pause:false}
scoreboard players set @s frv.phase 1
