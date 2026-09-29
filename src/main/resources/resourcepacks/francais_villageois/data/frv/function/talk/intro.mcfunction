tellraw @s {text:"━━━━━━━━━━━━━━━━━━━━━━━━━━",color:"dark_gray"}
tellraw @s {text:"",extra:[{text:""},{text:"» ",color:"gold"},{selector:"@e[type=minecraft:villager,tag=frv.spk,limit=1]",color:"gold"},{text:" говорит только по-французски",color:"gray"}]}
execute if score @s frv.theme matches 1 if score @s frv.band matches 1 run tellraw @s {text:"   Тема: Еда и покупки · язык A1",color:"dark_aqua"}
execute if score @s frv.theme matches 1 if score @s frv.band matches 2 run tellraw @s {text:"   Тема: Еда и покупки · язык A2",color:"dark_aqua"}
execute if score @s frv.theme matches 1 if score @s frv.band matches 3 run tellraw @s {text:"   Тема: Еда и покупки · язык B1",color:"dark_aqua"}
execute if score @s frv.theme matches 2 if score @s frv.band matches 1 run tellraw @s {text:"   Тема: Одежда и цвета · язык A1",color:"dark_aqua"}
execute if score @s frv.theme matches 2 if score @s frv.band matches 2 run tellraw @s {text:"   Тема: Одежда и цвета · язык A2",color:"dark_aqua"}
execute if score @s frv.theme matches 2 if score @s frv.band matches 3 run tellraw @s {text:"   Тема: Одежда и цвета · язык B1",color:"dark_aqua"}
execute if score @s frv.theme matches 3 if score @s frv.band matches 1 run tellraw @s {text:"   Тема: Учёба и книги · язык A1",color:"dark_aqua"}
execute if score @s frv.theme matches 3 if score @s frv.band matches 2 run tellraw @s {text:"   Тема: Учёба и книги · язык A2",color:"dark_aqua"}
execute if score @s frv.theme matches 3 if score @s frv.band matches 3 run tellraw @s {text:"   Тема: Учёба и книги · язык B1",color:"dark_aqua"}
execute if score @s frv.theme matches 4 if score @s frv.band matches 1 run tellraw @s {text:"   Тема: Город и дорога · язык A1",color:"dark_aqua"}
execute if score @s frv.theme matches 4 if score @s frv.band matches 2 run tellraw @s {text:"   Тема: Город и дорога · язык A2",color:"dark_aqua"}
execute if score @s frv.theme matches 4 if score @s frv.band matches 3 run tellraw @s {text:"   Тема: Город и дорога · язык B1",color:"dark_aqua"}
execute if score @s frv.theme matches 5 if score @s frv.band matches 1 run tellraw @s {text:"   Тема: Здоровье и чувства · язык A1",color:"dark_aqua"}
execute if score @s frv.theme matches 5 if score @s frv.band matches 2 run tellraw @s {text:"   Тема: Здоровье и чувства · язык A2",color:"dark_aqua"}
execute if score @s frv.theme matches 5 if score @s frv.band matches 3 run tellraw @s {text:"   Тема: Здоровье и чувства · язык B1",color:"dark_aqua"}
execute if score @s frv.theme matches 6 if score @s frv.band matches 1 run tellraw @s {text:"   Тема: Работа и профессии · язык A1",color:"dark_aqua"}
execute if score @s frv.theme matches 6 if score @s frv.band matches 2 run tellraw @s {text:"   Тема: Работа и профессии · язык A2",color:"dark_aqua"}
execute if score @s frv.theme matches 6 if score @s frv.band matches 3 run tellraw @s {text:"   Тема: Работа и профессии · язык B1",color:"dark_aqua"}
execute if score @s frv.theme matches 7 if score @s frv.band matches 1 run tellraw @s {text:"   Тема: Дом и семья · язык A1",color:"dark_aqua"}
execute if score @s frv.theme matches 7 if score @s frv.band matches 2 run tellraw @s {text:"   Тема: Дом и семья · язык A2",color:"dark_aqua"}
execute if score @s frv.theme matches 7 if score @s frv.band matches 3 run tellraw @s {text:"   Тема: Дом и семья · язык B1",color:"dark_aqua"}
execute if score @s frv.theme matches 8 if score @s frv.band matches 1 run tellraw @s {text:"   Тема: Досуг и спорт · язык A1",color:"dark_aqua"}
execute if score @s frv.theme matches 8 if score @s frv.band matches 2 run tellraw @s {text:"   Тема: Досуг и спорт · язык A2",color:"dark_aqua"}
execute if score @s frv.theme matches 8 if score @s frv.band matches 3 run tellraw @s {text:"   Тема: Досуг и спорт · язык B1",color:"dark_aqua"}
execute if score @s frv.vband > @s frv.band run tellraw @s {text:"   Житель опытный, но подстраивает речь под твой уровень. Учи фразы — откроешь сложнее.",color:"dark_gray",italic:true}
execute if score @s frv.tu matches 1 run tellraw @s {text:"   ♥ Вы на «ты» — говори по-дружески.",color:"light_purple"}
tellraw @s {text:"   Открой чат (T) и кликай по ответам. Наведи на реплику — перевод.",color:"dark_gray",italic:true}
