function frv:fx/right
advancement grant @s only frv:fr/premier_mot
function frv:srs/right {id:63}
dialog show @s {type:"minecraft:notice",title:{text:"✔ Bravo !",color:"green",bold:true},body:[{type:"minecraft:plain_message",contents:{text:"Oui, j'y suis allé l'année dernière.",color:"green"},width:340},{type:"minecraft:plain_message",contents:{text:"» Aller → passé composé с être: je suis allé. Y заменяет место (à Paris) и стоит перед глаголом.",color:"gray"},width:340}],action:{label:{text:"Дальше →",color:"green"},width:200,action:{type:"run_command",command:"trigger frv.ans set 6399"}},can_close_with_escape:true,pause:false}
scoreboard players set @s frv.phase 1
