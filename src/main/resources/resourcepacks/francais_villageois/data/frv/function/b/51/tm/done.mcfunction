function frv:fx/right
advancement grant @s only frv:fr/premier_mot
function frv:srs/right {id:51}
dialog show @s {type:"minecraft:notice",title:{text:"✔ Bravo !",color:"green",bold:true},body:[{type:"minecraft:plain_message",contents:{text:"Ah oui, je me souviens de ce livre !",color:"green"},width:340},{type:"minecraft:plain_message",contents:{text:"» Se souvenir de qqch — возвратный глагол с de. А se rappeler — без de: je me rappelle ce livre.",color:"gray"},width:340}],action:{label:{text:"Дальше →",color:"green"},width:200,action:{type:"run_command",command:"trigger frv.ans set 5199"}},can_close_with_escape:true,pause:false}
scoreboard players set @s frv.phase 1
