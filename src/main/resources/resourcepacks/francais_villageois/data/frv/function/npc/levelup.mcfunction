tellraw @a[distance=..12] {text:"",extra:[{text:""},{text:"★ ",color:"gold"},{selector:"@s",color:"gold"},{text:" набирается опыта! Уровень ",color:"yellow"},{score:{name:"#l",objective:"frv.tmp"},color:"gold"},{text:"/5",color:"yellow"}]}
particle minecraft:happy_villager ~ ~1.5 ~ 0.4 0.6 0.4 0 20
execute if score #l frv.tmp matches 3 run tellraw @a[distance=..12] {text:"   Теперь этот житель может говорить на A2.",color:"aqua"}
execute if score #l frv.tmp matches 5 run tellraw @a[distance=..12] {text:"   Мастер! Может говорить на B1.",color:"aqua"}
