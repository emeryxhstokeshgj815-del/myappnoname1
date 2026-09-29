execute unless data storage frv:tmp work[0] run return 0
execute if data storage frv:tmp live[0] run data modify storage frv:tmp work[0].uses set from storage frv:tmp live[0].uses
data modify storage frv:tmp out append from storage frv:tmp work[0]
data remove storage frv:tmp work[0]
data remove storage frv:tmp live[0]
function frv:npc/relock_loop
