$scoreboard players set @s frv.s$(id) 0
$function frv:srs/due_in {id:$(id),n:1}
tellraw @s {text:"   ↻ Любой житель спросит это снова через игровой день.",color:"light_purple",italic:true}
