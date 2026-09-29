function frv:fx/right
advancement grant @s only frv:fr/premier_mot
function frv:srs/right {id:46}
dialog show @s {type:"minecraft:notice",title:{text:"✔ Super !",color:"green",bold:true},body:[{type:"minecraft:plain_message",contents:{text:"Oui, je te le rends demain.",color:"green"},width:340},{type:"minecraft:plain_message",contents:{text:"» Порядок местоимений: me/te/nous/vous стоят перед le/la/les: je te le rends.",color:"gray"},width:340}],action:{label:{text:"Дальше →",color:"green"},width:200,action:{type:"run_command",command:"trigger frv.ans set 4699"}},can_close_with_escape:true,pause:false}
scoreboard players set @s frv.phase 1
