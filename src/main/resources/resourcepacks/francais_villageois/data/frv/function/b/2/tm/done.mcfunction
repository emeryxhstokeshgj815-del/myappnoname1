function frv:fx/right
advancement grant @s only frv:fr/premier_mot
function frv:srs/right {id:2}
dialog show @s {type:"minecraft:notice",title:{text:"✔ Excellent !",color:"green",bold:true},body:[{type:"minecraft:plain_message",contents:{text:"Oui, j'adore les pommes !",color:"green"},width:340},{type:"minecraft:plain_message",contents:{text:"» После aimer/adorer/détester — определённый артикль: les pommes. Перед гласной je → j'. Прилагательное согласуется: rouges.",color:"gray"},width:340}],action:{label:{text:"Дальше →",color:"green"},width:200,action:{type:"run_command",command:"trigger frv.ans set 299"}},can_close_with_escape:true,pause:false}
scoreboard players set @s frv.phase 1
