$data modify storage frv:tmp name set from storage frv:data names$(g)[$(i)]
$data modify storage frv:tmp title set from storage frv:data titles$(g)[$(p)]
function frv:npc/rename_apply with storage frv:tmp
