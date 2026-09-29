function frv:session/setkey
function frv:session/hdr
execute if score @s frv.mode matches 0 run function frv:session/show_q with storage frv:tmp
execute if score @s frv.mode matches 1 run function frv:session/show_b with storage frv:tmp
