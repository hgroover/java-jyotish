// shapegrid 1.06; read 27731 from ../world/tz_world
var __uninhabited = ["America/Los_Angeles","America/Chicago","America/Mexico_City","America/New_York","America/Sao_Paulo","Europe/London","Europe/Paris","Europe/Rome","Asia/Calcutta","Asia/Tokyo"];
var __bandmargin = 0.5;
var __bandcount = 16;
var __bands = [ [-90,-66] , [-66,-48] , [-48,-42] , [-42,-36] , [-36,-30] , [-30,-22] , [-22,-12] , [-12,0] , [0,12] , [12,22] , [22,30] , [30,36] , [36,42] , [42,48] , [48,66] , [66,90] ];
var g_tz = new Array();

g_tz[0] = new Array();
// 0,0 [-90.5,-65.5; -172.5,-157.5] 
g_tz[0][0]= __uninhabited;
// 0,1 [-90.5,-65.5; -157.5,-142.5] 
g_tz[0][1]= __uninhabited;
// 0,2 [-90.5,-65.5; -142.5,-127.5] 
g_tz[0][2]= __uninhabited;
// 0,3 [-90.5,-65.5; -127.5,-112.5] 
g_tz[0][3]= __uninhabited;
// 0,4 [-90.5,-65.5; -112.5,-97.5] 
g_tz[0][4]= __uninhabited;
// 0,5 [-90.5,-65.5; -97.5,-82.5] 
g_tz[0][5]= __uninhabited;
// 0,6 [-90.5,-65.5; -82.5,-67.5] 
g_tz[0][6]= __uninhabited;
// 0,7 [-90.5,-65.5; -67.5,-52.5] 
g_tz[0][7]= __uninhabited;
// 0,8 [-90.5,-65.5; -52.5,-37.5] 
g_tz[0][8]= __uninhabited;
// 0,9 [-90.5,-65.5; -37.5,-22.5] 
g_tz[0][9]= __uninhabited;
// 0,10 [-90.5,-65.5; -22.5,-7.5] 
g_tz[0][10]= __uninhabited;
// 0,11 [-90.5,-65.5; -7.5,7.5] 
g_tz[0][11]= __uninhabited;
// 0,12 [-90.5,-65.5; 7.5,22.5] 
g_tz[0][12]= __uninhabited;
// 0,13 [-90.5,-65.5; 22.5,37.5] 
g_tz[0][13]= __uninhabited;
// 0,14 [-90.5,-65.5; 37.5,52.5] 
g_tz[0][14]= __uninhabited;
// 0,15 [-90.5,-65.5; 52.5,67.5] 
g_tz[0][15]= __uninhabited;
// 0,16 [-90.5,-65.5; 67.5,82.5] 
g_tz[0][16]= __uninhabited;
// 0,17 [-90.5,-65.5; 82.5,97.5] 
g_tz[0][17]= __uninhabited;
// 0,18 [-90.5,-65.5; 97.5,112.5] 
g_tz[0][18]= __uninhabited;
// 0,19 [-90.5,-65.5; 112.5,127.5] 
g_tz[0][19]= __uninhabited;
// 0,20 [-90.5,-65.5; 127.5,142.5] 
g_tz[0][20]= __uninhabited;
// 0,21 [-90.5,-65.5; 142.5,157.5] 
g_tz[0][21]= __uninhabited;
// 0,22 [-90.5,-65.5; 157.5,172.5] 
g_tz[0][22]= __uninhabited;
// 0,23 [-90.5,-65.5; 172.5,-172.5] 
g_tz[0][23]= __uninhabited;
// band 0 max zones was 0 in lune -1

g_tz[1] = new Array();
// 1,0 [-66.5,-47.5; -172.5,-157.5] 
g_tz[1][0]= __uninhabited;
// 1,1 [-66.5,-47.5; -157.5,-142.5] 
g_tz[1][1]= __uninhabited;
// 1,2 [-66.5,-47.5; -142.5,-127.5] 
g_tz[1][2]= __uninhabited;
// 1,3 [-66.5,-47.5; -127.5,-112.5] 
g_tz[1][3]= __uninhabited;
// 1,4 [-66.5,-47.5; -112.5,-97.5] 
g_tz[1][4]= __uninhabited;
// 1,5 [-66.5,-47.5; -97.5,-82.5] 
g_tz[1][5]= __uninhabited;
// 1,6 [-66.5,-47.5; -82.5,-67.5]  count=3
g_tz[1][6]=["America/Argentina/Rio_Gallegos", "America/Argentina/Ushuaia", "America/Santiago"];
// 1,7 [-66.5,-47.5; -67.5,-52.5]  count=4
g_tz[1][7]=["America/Argentina/Rio_Gallegos", "America/Argentina/Ushuaia", "America/Santiago", "Atlantic/Stanley"];
// 1,8 [-66.5,-47.5; -52.5,-37.5]  count=1
g_tz[1][8]=["Atlantic/South_Georgia"];
// 1,9 [-66.5,-47.5; -37.5,-22.5]  count=1
g_tz[1][9]=["Atlantic/South_Georgia"];
// 1,10 [-66.5,-47.5; -22.5,-7.5] 
g_tz[1][10]= __uninhabited;
// 1,11 [-66.5,-47.5; -7.5,7.5] 
g_tz[1][11]= __uninhabited;
// 1,12 [-66.5,-47.5; 7.5,22.5] 
g_tz[1][12]= __uninhabited;
// 1,13 [-66.5,-47.5; 22.5,37.5] 
g_tz[1][13]= __uninhabited;
// 1,14 [-66.5,-47.5; 37.5,52.5] 
g_tz[1][14]= __uninhabited;
// 1,15 [-66.5,-47.5; 52.5,67.5] 
g_tz[1][15]= __uninhabited;
// 1,16 [-66.5,-47.5; 67.5,82.5]  count=1
g_tz[1][16]=["Indian/Kerguelen"];
// 1,17 [-66.5,-47.5; 82.5,97.5] 
g_tz[1][17]= __uninhabited;
// 1,18 [-66.5,-47.5; 97.5,112.5] 
g_tz[1][18]= __uninhabited;
// 1,19 [-66.5,-47.5; 112.5,127.5] 
g_tz[1][19]= __uninhabited;
// 1,20 [-66.5,-47.5; 127.5,142.5] 
g_tz[1][20]= __uninhabited;
// 1,21 [-66.5,-47.5; 142.5,157.5] 
g_tz[1][21]= __uninhabited;
// 1,22 [-66.5,-47.5; 157.5,172.5]  count=2
g_tz[1][22]=["Antarctica/Macquarie", "Pacific/Auckland"];
// 1,23 [-66.5,-47.5; 172.5,-172.5]  count=1
g_tz[1][23]=["Pacific/Auckland"];
// band 1 max zones was 4 in lune 7

g_tz[2] = new Array();
// 2,0 [-48.5,-41.5; -172.5,-157.5] 
g_tz[2][0]= __uninhabited;
// 2,1 [-48.5,-41.5; -157.5,-142.5] 
g_tz[2][1]= __uninhabited;
// 2,2 [-48.5,-41.5; -142.5,-127.5] 
g_tz[2][2]= __uninhabited;
// 2,3 [-48.5,-41.5; -127.5,-112.5] 
g_tz[2][3]= __uninhabited;
// 2,4 [-48.5,-41.5; -112.5,-97.5] 
g_tz[2][4]= __uninhabited;
// 2,5 [-48.5,-41.5; -97.5,-82.5] 
g_tz[2][5]= __uninhabited;
// 2,6 [-48.5,-41.5; -82.5,-67.5]  count=4
g_tz[2][6]=["America/Argentina/Catamarca", "America/Argentina/Rio_Gallegos", "America/Argentina/Salta", "America/Santiago"];
// 2,7 [-48.5,-41.5; -67.5,-52.5]  count=3
g_tz[2][7]=["America/Argentina/Catamarca", "America/Argentina/Rio_Gallegos", "America/Argentina/Salta"];
// 2,8 [-48.5,-41.5; -52.5,-37.5] 
g_tz[2][8]= __uninhabited;
// 2,9 [-48.5,-41.5; -37.5,-22.5] 
g_tz[2][9]= __uninhabited;
// 2,10 [-48.5,-41.5; -22.5,-7.5] 
g_tz[2][10]= __uninhabited;
// 2,11 [-48.5,-41.5; -7.5,7.5] 
g_tz[2][11]= __uninhabited;
// 2,12 [-48.5,-41.5; 7.5,22.5] 
g_tz[2][12]= __uninhabited;
// 2,13 [-48.5,-41.5; 22.5,37.5] 
g_tz[2][13]= __uninhabited;
// 2,14 [-48.5,-41.5; 37.5,52.5]  count=2
g_tz[2][14]=["Africa/Johannesburg", "Indian/Kerguelen"];
// 2,15 [-48.5,-41.5; 52.5,67.5] 
g_tz[2][15]= __uninhabited;
// 2,16 [-48.5,-41.5; 67.5,82.5]  count=1
g_tz[2][16]=["Indian/Kerguelen"];
// 2,17 [-48.5,-41.5; 82.5,97.5] 
g_tz[2][17]= __uninhabited;
// 2,18 [-48.5,-41.5; 97.5,112.5] 
g_tz[2][18]= __uninhabited;
// 2,19 [-48.5,-41.5; 112.5,127.5] 
g_tz[2][19]= __uninhabited;
// 2,20 [-48.5,-41.5; 127.5,142.5] 
g_tz[2][20]= __uninhabited;
// 2,21 [-48.5,-41.5; 142.5,157.5]  count=1
g_tz[2][21]=["Australia/Hobart"];
// 2,22 [-48.5,-41.5; 157.5,172.5]  count=1
g_tz[2][22]=["Pacific/Auckland"];
// 2,23 [-48.5,-41.5; 172.5,-172.5]  count=2
g_tz[2][23]=["Pacific/Auckland", "Pacific/Chatham"];
// band 2 max zones was 4 in lune 6

g_tz[3] = new Array();
// 3,0 [-42.5,-35.5; -172.5,-157.5] 
g_tz[3][0]= __uninhabited;
// 3,1 [-42.5,-35.5; -157.5,-142.5] 
g_tz[3][1]= __uninhabited;
// 3,2 [-42.5,-35.5; -142.5,-127.5] 
g_tz[3][2]= __uninhabited;
// 3,3 [-42.5,-35.5; -127.5,-112.5] 
g_tz[3][3]= __uninhabited;
// 3,4 [-42.5,-35.5; -112.5,-97.5] 
g_tz[3][4]= __uninhabited;
// 3,5 [-42.5,-35.5; -97.5,-82.5] 
g_tz[3][5]= __uninhabited;
// 3,6 [-42.5,-35.5; -82.5,-67.5]  count=4
g_tz[3][6]=["America/Argentina/Catamarca", "America/Argentina/Mendoza", "America/Argentina/Salta", "America/Santiago"];
// 3,7 [-42.5,-35.5; -67.5,-52.5]  count=5
g_tz[3][7]=["America/Argentina/Buenos_Aires", "America/Argentina/Catamarca", "America/Argentina/Mendoza", "America/Argentina/Salta", "America/Argentina/San_Luis"];
// 3,8 [-42.5,-35.5; -52.5,-37.5] 
g_tz[3][8]= __uninhabited;
// 3,9 [-42.5,-35.5; -37.5,-22.5] 
g_tz[3][9]= __uninhabited;
// 3,10 [-42.5,-35.5; -22.5,-7.5]  count=1
g_tz[3][10]=["Atlantic/St_Helena"];
// 3,11 [-42.5,-35.5; -7.5,7.5] 
g_tz[3][11]= __uninhabited;
// 3,12 [-42.5,-35.5; 7.5,22.5] 
g_tz[3][12]= __uninhabited;
// 3,13 [-42.5,-35.5; 22.5,37.5] 
g_tz[3][13]= __uninhabited;
// 3,14 [-42.5,-35.5; 37.5,52.5] 
g_tz[3][14]= __uninhabited;
// 3,15 [-42.5,-35.5; 52.5,67.5] 
g_tz[3][15]= __uninhabited;
// 3,16 [-42.5,-35.5; 67.5,82.5]  count=1
g_tz[3][16]=["Indian/Kerguelen"];
// 3,17 [-42.5,-35.5; 82.5,97.5] 
g_tz[3][17]= __uninhabited;
// 3,18 [-42.5,-35.5; 97.5,112.5] 
g_tz[3][18]= __uninhabited;
// 3,19 [-42.5,-35.5; 112.5,127.5] 
g_tz[3][19]= __uninhabited;
// 3,20 [-42.5,-35.5; 127.5,142.5]  count=2
g_tz[3][20]=["Australia/Adelaide", "Australia/Melbourne"];
// 3,21 [-42.5,-35.5; 142.5,157.5]  count=4
g_tz[3][21]=["Australia/Currie", "Australia/Hobart", "Australia/Melbourne", "Australia/Sydney"];
// 3,22 [-42.5,-35.5; 157.5,172.5]  count=1
g_tz[3][22]=["Pacific/Auckland"];
// 3,23 [-42.5,-35.5; 172.5,-172.5]  count=1
g_tz[3][23]=["Pacific/Auckland"];
// band 3 max zones was 5 in lune 7

g_tz[4] = new Array();
// 4,0 [-36.5,-29.5; -172.5,-157.5] 
g_tz[4][0]= __uninhabited;
// 4,1 [-36.5,-29.5; -157.5,-142.5] 
g_tz[4][1]= __uninhabited;
// 4,2 [-36.5,-29.5; -142.5,-127.5] 
g_tz[4][2]= __uninhabited;
// 4,3 [-36.5,-29.5; -127.5,-112.5] 
g_tz[4][3]= __uninhabited;
// 4,4 [-36.5,-29.5; -112.5,-97.5] 
g_tz[4][4]= __uninhabited;
// 4,5 [-36.5,-29.5; -97.5,-82.5]  count=1
g_tz[4][5]=["America/Santiago"];
// 4,6 [-36.5,-29.5; -82.5,-67.5]  count=5
g_tz[4][6]=["America/Argentina/La_Rioja", "America/Argentina/Mendoza", "America/Argentina/Salta", "America/Argentina/San_Juan", "America/Santiago"];
// 4,7 [-36.5,-29.5; -67.5,-52.5]  count=10
g_tz[4][7]=["America/Argentina/Buenos_Aires", "America/Argentina/Catamarca", "America/Argentina/Cordoba", "America/Argentina/La_Rioja", "America/Argentina/Mendoza", "America/Argentina/Salta", "America/Argentina/San_Juan", "America/Argentina/San_Luis", "America/Montevideo", "America/Sao_Paulo"];
// 4,8 [-36.5,-29.5; -52.5,-37.5]  count=1
g_tz[4][8]=["America/Sao_Paulo"];
// 4,9 [-36.5,-29.5; -37.5,-22.5] 
g_tz[4][9]= __uninhabited;
// 4,10 [-36.5,-29.5; -22.5,-7.5] 
g_tz[4][10]= __uninhabited;
// 4,11 [-36.5,-29.5; -7.5,7.5] 
g_tz[4][11]= __uninhabited;
// 4,12 [-36.5,-29.5; 7.5,22.5]  count=1
g_tz[4][12]=["Africa/Johannesburg"];
// 4,13 [-36.5,-29.5; 22.5,37.5]  count=2
g_tz[4][13]=["Africa/Johannesburg", "Africa/Maseru"];
// 4,14 [-36.5,-29.5; 37.5,52.5] 
g_tz[4][14]= __uninhabited;
// 4,15 [-36.5,-29.5; 52.5,67.5] 
g_tz[4][15]= __uninhabited;
// 4,16 [-36.5,-29.5; 67.5,82.5] 
g_tz[4][16]= __uninhabited;
// 4,17 [-36.5,-29.5; 82.5,97.5] 
g_tz[4][17]= __uninhabited;
// 4,18 [-36.5,-29.5; 97.5,112.5] 
g_tz[4][18]= __uninhabited;
// 4,19 [-36.5,-29.5; 112.5,127.5]  count=2
g_tz[4][19]=["Australia/Eucla", "Australia/Perth"];
// 4,20 [-36.5,-29.5; 127.5,142.5]  count=6
g_tz[4][20]=["Australia/Adelaide", "Australia/Broken_Hill", "Australia/Eucla", "Australia/Melbourne", "Australia/Perth", "Australia/Sydney"];
// 4,21 [-36.5,-29.5; 142.5,157.5]  count=2
g_tz[4][21]=["Australia/Melbourne", "Australia/Sydney"];
// 4,22 [-36.5,-29.5; 157.5,172.5]  count=2
g_tz[4][22]=["Australia/Lord_Howe", "Pacific/Auckland"];
// 4,23 [-36.5,-29.5; 172.5,-172.5]  count=1
g_tz[4][23]=["Pacific/Auckland"];
// band 4 max zones was 10 in lune 7

g_tz[5] = new Array();
// 5,0 [-30.5,-21.5; -172.5,-157.5]  count=1
g_tz[5][0]=["Pacific/Rarotonga"];
// 5,1 [-30.5,-21.5; -157.5,-142.5]  count=1
g_tz[5][1]=["Pacific/Tahiti"];
// 5,2 [-30.5,-21.5; -142.5,-127.5]  count=3
g_tz[5][2]=["Pacific/Gambier", "Pacific/Pitcairn", "Pacific/Tahiti"];
// 5,3 [-30.5,-21.5; -127.5,-112.5]  count=1
g_tz[5][3]=["Pacific/Pitcairn"];
// 5,4 [-30.5,-21.5; -112.5,-97.5]  count=1
g_tz[5][4]=["Pacific/Easter"];
// 5,5 [-30.5,-21.5; -97.5,-82.5] 
g_tz[5][5]= __uninhabited;
// 5,6 [-30.5,-21.5; -82.5,-67.5]  count=6
g_tz[5][6]=["America/Argentina/Catamarca", "America/Argentina/La_Rioja", "America/Argentina/Salta", "America/Argentina/San_Juan", "America/La_Paz", "America/Santiago"];
// 5,7 [-30.5,-21.5; -67.5,-52.5]  count=13
g_tz[5][7]=["America/Argentina/Catamarca", "America/Argentina/Cordoba", "America/Argentina/Jujuy", "America/Argentina/La_Rioja", "America/Argentina/Salta", "America/Argentina/San_Juan", "America/Argentina/Tucuman", "America/Asuncion", "America/Campo_Grande", "America/La_Paz", "America/Montevideo", "America/Santiago", "America/Sao_Paulo"];
// 5,8 [-30.5,-21.5; -52.5,-37.5]  count=2
g_tz[5][8]=["America/Campo_Grande", "America/Sao_Paulo"];
// 5,9 [-30.5,-21.5; -37.5,-22.5] 
g_tz[5][9]= __uninhabited;
// 5,10 [-30.5,-21.5; -22.5,-7.5] 
g_tz[5][10]= __uninhabited;
// 5,11 [-30.5,-21.5; -7.5,7.5] 
g_tz[5][11]= __uninhabited;
// 5,12 [-30.5,-21.5; 7.5,22.5]  count=3
g_tz[5][12]=["Africa/Gaborone", "Africa/Johannesburg", "Africa/Windhoek"];
// 5,13 [-30.5,-21.5; 22.5,37.5]  count=6
g_tz[5][13]=["Africa/Gaborone", "Africa/Harare", "Africa/Johannesburg", "Africa/Maputo", "Africa/Maseru", "Africa/Mbabane"];
// 5,14 [-30.5,-21.5; 37.5,52.5]  count=1
g_tz[5][14]=["Indian/Antananarivo"];
// 5,15 [-30.5,-21.5; 52.5,67.5] 
g_tz[5][15]= __uninhabited;
// 5,16 [-30.5,-21.5; 67.5,82.5] 
g_tz[5][16]= __uninhabited;
// 5,17 [-30.5,-21.5; 82.5,97.5] 
g_tz[5][17]= __uninhabited;
// 5,18 [-30.5,-21.5; 97.5,112.5] 
g_tz[5][18]= __uninhabited;
// 5,19 [-30.5,-21.5; 112.5,127.5]  count=1
g_tz[5][19]=["Australia/Perth"];
// 5,20 [-30.5,-21.5; 127.5,142.5]  count=5
g_tz[5][20]=["Australia/Adelaide", "Australia/Brisbane", "Australia/Darwin", "Australia/Perth", "Australia/Sydney"];
// 5,21 [-30.5,-21.5; 142.5,157.5]  count=2
g_tz[5][21]=["Australia/Brisbane", "Australia/Sydney"];
// 5,22 [-30.5,-21.5; 157.5,172.5]  count=2
g_tz[5][22]=["Pacific/Norfolk", "Pacific/Noumea"];
// 5,23 [-30.5,-21.5; 172.5,-172.5]  count=2
g_tz[5][23]=["Pacific/Auckland", "Pacific/Tongatapu"];
// band 5 max zones was 13 in lune 7

g_tz[6] = new Array();
// 6,0 [-22.5,-11.5; -172.5,-157.5]  count=4
g_tz[6][0]=["Pacific/Apia", "Pacific/Niue", "Pacific/Pago_Pago", "Pacific/Rarotonga"];
// 6,1 [-22.5,-11.5; -157.5,-142.5]  count=2
g_tz[6][1]=["Pacific/Rarotonga", "Pacific/Tahiti"];
// 6,2 [-22.5,-11.5; -142.5,-127.5]  count=2
g_tz[6][2]=["Pacific/Gambier", "Pacific/Tahiti"];
// 6,3 [-22.5,-11.5; -127.5,-112.5] 
g_tz[6][3]= __uninhabited;
// 6,4 [-22.5,-11.5; -112.5,-97.5] 
g_tz[6][4]= __uninhabited;
// 6,5 [-22.5,-11.5; -97.5,-82.5] 
g_tz[6][5]= __uninhabited;
// 6,6 [-22.5,-11.5; -82.5,-67.5]  count=3
g_tz[6][6]=["America/La_Paz", "America/Lima", "America/Santiago"];
// 6,7 [-22.5,-11.5; -67.5,-52.5]  count=9
g_tz[6][7]=["America/Argentina/Cordoba", "America/Argentina/Jujuy", "America/Argentina/Salta", "America/Asuncion", "America/Campo_Grande", "America/Cuiaba", "America/La_Paz", "America/Porto_Velho", "America/Sao_Paulo"];
// 6,8 [-22.5,-11.5; -52.5,-37.5]  count=6
g_tz[6][8]=["America/Araguaina", "America/Bahia", "America/Campo_Grande", "America/Cuiaba", "America/Maceio", "America/Sao_Paulo"];
// 6,9 [-22.5,-11.5; -37.5,-22.5]  count=2
g_tz[6][9]=["America/Bahia", "America/Maceio"];
// 6,10 [-22.5,-11.5; -22.5,-7.5] 
g_tz[6][10]= __uninhabited;
// 6,11 [-22.5,-11.5; -7.5,7.5]  count=1
g_tz[6][11]=["Atlantic/St_Helena"];
// 6,12 [-22.5,-11.5; 7.5,22.5]  count=4
g_tz[6][12]=["Africa/Gaborone", "Africa/Luanda", "Africa/Lusaka", "Africa/Windhoek"];
// 6,13 [-22.5,-11.5; 22.5,37.5]  count=10
g_tz[6][13]=["Africa/Blantyre", "Africa/Dar_es_Salaam", "Africa/Gaborone", "Africa/Harare", "Africa/Johannesburg", "Africa/Luanda", "Africa/Lubumbashi", "Africa/Lusaka", "Africa/Maputo", "Africa/Windhoek"];
// 6,14 [-22.5,-11.5; 37.5,52.5]  count=5
g_tz[6][14]=["Africa/Dar_es_Salaam", "Africa/Maputo", "Indian/Antananarivo", "Indian/Comoro", "Indian/Mayotte"];
// 6,15 [-22.5,-11.5; 52.5,67.5]  count=2
g_tz[6][15]=["Indian/Mauritius", "Indian/Reunion"];
// 6,16 [-22.5,-11.5; 67.5,82.5] 
g_tz[6][16]= __uninhabited;
// 6,17 [-22.5,-11.5; 82.5,97.5]  count=1
g_tz[6][17]=["Indian/Cocos"];
// 6,18 [-22.5,-11.5; 97.5,112.5] 
g_tz[6][18]= __uninhabited;
// 6,19 [-22.5,-11.5; 112.5,127.5]  count=1
g_tz[6][19]=["Australia/Perth"];
// 6,20 [-22.5,-11.5; 127.5,142.5]  count=3
g_tz[6][20]=["Australia/Brisbane", "Australia/Darwin", "Australia/Perth"];
// 6,21 [-22.5,-11.5; 142.5,157.5]  count=3
g_tz[6][21]=["Australia/Brisbane", "Australia/Lindeman", "Pacific/Port_Moresby"];
// 6,22 [-22.5,-11.5; 157.5,172.5]  count=3
g_tz[6][22]=["Pacific/Efate", "Pacific/Guadalcanal", "Pacific/Noumea"];
// 6,23 [-22.5,-11.5; 172.5,-172.5]  count=4
g_tz[6][23]=["Pacific/Apia", "Pacific/Fiji", "Pacific/Tongatapu", "Pacific/Wallis"];
// band 6 max zones was 10 in lune 13

g_tz[7] = new Array();
// 7,0 [-12.5,0.5; -172.5,-157.5]  count=4
g_tz[7][0]=["Pacific/Enderbury", "Pacific/Fakaofo", "Pacific/Pago_Pago", "Pacific/Rarotonga"];
// 7,1 [-12.5,0.5; -157.5,-142.5]  count=1
g_tz[7][1]=["Pacific/Kiritimati"];
// 7,2 [-12.5,0.5; -142.5,-127.5]  count=1
g_tz[7][2]=["Pacific/Marquesas"];
// 7,3 [-12.5,0.5; -127.5,-112.5] 
g_tz[7][3]= __uninhabited;
// 7,4 [-12.5,0.5; -112.5,-97.5] 
g_tz[7][4]= __uninhabited;
// 7,5 [-12.5,0.5; -97.5,-82.5]  count=1
g_tz[7][5]=["Pacific/Galapagos"];
// 7,6 [-12.5,0.5; -82.5,-67.5]  count=7
g_tz[7][6]=["America/Bogota", "America/Eirunepe", "America/Guayaquil", "America/La_Paz", "America/Lima", "America/Manaus", "America/Rio_Branco"];
// 7,7 [-12.5,0.5; -67.5,-52.5]  count=8
g_tz[7][7]=["America/Belem", "America/Boa_Vista", "America/Cuiaba", "America/La_Paz", "America/Manaus", "America/Porto_Velho", "America/Rio_Branco", "America/Santarem"];
// 7,8 [-12.5,0.5; -52.5,-37.5]  count=9
g_tz[7][8]=["America/Araguaina", "America/Bahia", "America/Belem", "America/Cuiaba", "America/Fortaleza", "America/Maceio", "America/Recife", "America/Santarem", "America/Sao_Paulo"];
// 7,9 [-12.5,0.5; -37.5,-22.5]  count=5
g_tz[7][9]=["America/Bahia", "America/Fortaleza", "America/Maceio", "America/Noronha", "America/Recife"];
// 7,10 [-12.5,0.5; -22.5,-7.5]  count=1
g_tz[7][10]=["Atlantic/St_Helena"];
// 7,11 [-12.5,0.5; -7.5,7.5]  count=2
g_tz[7][11]=["Africa/Malabo", "Africa/Sao_Tome"];
// 7,12 [-12.5,0.5; 7.5,22.5]  count=5
g_tz[7][12]=["Africa/Brazzaville", "Africa/Kinshasa", "Africa/Libreville", "Africa/Luanda", "Africa/Lubumbashi"];
// 7,13 [-12.5,0.5; 22.5,37.5]  count=11
g_tz[7][13]=["Africa/Blantyre", "Africa/Bujumbura", "Africa/Dar_es_Salaam", "Africa/Kampala", "Africa/Kigali", "Africa/Kinshasa", "Africa/Luanda", "Africa/Lubumbashi", "Africa/Lusaka", "Africa/Maputo", "Africa/Nairobi"];
// 7,14 [-12.5,0.5; 37.5,52.5]  count=7
g_tz[7][14]=["Africa/Dar_es_Salaam", "Africa/Maputo", "Africa/Mogadishu", "Africa/Nairobi", "Indian/Antananarivo", "Indian/Comoro", "Indian/Mahe"];
// 7,15 [-12.5,0.5; 52.5,67.5]  count=2
g_tz[7][15]=["Indian/Mahe", "Indian/Mauritius"];
// 7,16 [-12.5,0.5; 67.5,82.5]  count=2
g_tz[7][16]=["Indian/Chagos", "Indian/Maldives"];
// 7,17 [-12.5,0.5; 82.5,97.5]  count=1
g_tz[7][17]=["Indian/Cocos"];
// 7,18 [-12.5,0.5; 97.5,112.5]  count=3
g_tz[7][18]=["Asia/Jakarta", "Asia/Pontianak", "Indian/Christmas"];
// 7,19 [-12.5,0.5; 112.5,127.5]  count=5
g_tz[7][19]=["Asia/Dili", "Asia/Jakarta", "Asia/Jayapura", "Asia/Makassar", "Asia/Pontianak"];
// 7,20 [-12.5,0.5; 127.5,142.5]  count=4
g_tz[7][20]=["Asia/Jayapura", "Australia/Brisbane", "Australia/Darwin", "Pacific/Port_Moresby"];
// 7,21 [-12.5,0.5; 142.5,157.5]  count=3
g_tz[7][21]=["Australia/Brisbane", "Pacific/Guadalcanal", "Pacific/Port_Moresby"];
// 7,22 [-12.5,0.5; 157.5,172.5]  count=4
g_tz[7][22]=["Pacific/Guadalcanal", "Pacific/Nauru", "Pacific/Port_Moresby", "Pacific/Tarawa"];
// 7,23 [-12.5,0.5; 172.5,-172.5]  count=5
g_tz[7][23]=["Pacific/Enderbury", "Pacific/Fakaofo", "Pacific/Fiji", "Pacific/Funafuti", "Pacific/Tarawa"];
// band 7 max zones was 11 in lune 13

g_tz[8] = new Array();
// 8,0 [-0.5,12.5; -172.5,-157.5]  count=1
g_tz[8][0]=["Pacific/Kiritimati"];
// 8,1 [-0.5,12.5; -157.5,-142.5]  count=1
g_tz[8][1]=["Pacific/Kiritimati"];
// 8,2 [-0.5,12.5; -142.5,-127.5] 
g_tz[8][2]= __uninhabited;
// 8,3 [-0.5,12.5; -127.5,-112.5] 
g_tz[8][3]= __uninhabited;
// 8,4 [-0.5,12.5; -112.5,-97.5] 
g_tz[8][4]= __uninhabited;
// 8,5 [-0.5,12.5; -97.5,-82.5]  count=4
g_tz[8][5]=["America/Costa_Rica", "America/Managua", "America/Panama", "Pacific/Galapagos"];
// 8,6 [-0.5,12.5; -82.5,-67.5]  count=9
g_tz[8][6]=["America/Aruba", "America/Bogota", "America/Caracas", "America/Curacao", "America/Guayaquil", "America/Kralendijk", "America/Lima", "America/Manaus", "America/Panama"];
// 8,7 [-0.5,12.5; -67.5,-52.5]  count=11
g_tz[8][7]=["America/Belem", "America/Boa_Vista", "America/Bogota", "America/Caracas", "America/Cayenne", "America/Grenada", "America/Guyana", "America/Manaus", "America/Paramaribo", "America/Port_of_Spain", "America/Santarem"];
// 8,8 [-0.5,12.5; -52.5,-37.5]  count=2
g_tz[8][8]=["America/Belem", "America/Cayenne"];
// 8,9 [-0.5,12.5; -37.5,-22.5] 
g_tz[8][9]= __uninhabited;
// 8,10 [-0.5,12.5; -22.5,-7.5]  count=7
g_tz[8][10]=["Africa/Abidjan", "Africa/Bamako", "Africa/Bissau", "Africa/Conakry", "Africa/Dakar", "Africa/Freetown", "Africa/Monrovia"];
// 8,11 [-0.5,12.5; -7.5,7.5]  count=10
g_tz[8][11]=["Africa/Abidjan", "Africa/Accra", "Africa/Bamako", "Africa/Lagos", "Africa/Lome", "Africa/Monrovia", "Africa/Niamey", "Africa/Ouagadougou", "Africa/Porto-Novo", "Africa/Sao_Tome"];
// 8,12 [-0.5,12.5; 7.5,22.5]  count=10
g_tz[8][12]=["Africa/Bangui", "Africa/Brazzaville", "Africa/Douala", "Africa/Khartoum", "Africa/Kinshasa", "Africa/Lagos", "Africa/Libreville", "Africa/Lubumbashi", "Africa/Malabo", "Africa/Ndjamena"];
// 8,13 [-0.5,12.5; 22.5,37.5]  count=9
g_tz[8][13]=["Africa/Addis_Ababa", "Africa/Bangui", "Africa/Juba", "Africa/Kampala", "Africa/Khartoum", "Africa/Kinshasa", "Africa/Lubumbashi", "Africa/Nairobi", "Africa/Ndjamena"];
// 8,14 [-0.5,12.5; 37.5,52.5]  count=6
g_tz[8][14]=["Africa/Addis_Ababa", "Africa/Asmara", "Africa/Djibouti", "Africa/Mogadishu", "Africa/Nairobi", "Asia/Aden"];
// 8,15 [-0.5,12.5; 52.5,67.5]  count=1
g_tz[8][15]=["Asia/Aden"];
// 8,16 [-0.5,12.5; 67.5,82.5]  count=3
g_tz[8][16]=["Asia/Colombo", "Asia/Kolkata", "Indian/Maldives"];
// 8,17 [-0.5,12.5; 82.5,97.5]  count=3
g_tz[8][17]=["Asia/Jakarta", "Asia/Kolkata", "Asia/Rangoon"];
// 8,18 [-0.5,12.5; 97.5,112.5]  count=9
g_tz[8][18]=["Asia/Bangkok", "Asia/Ho_Chi_Minh", "Asia/Jakarta", "Asia/Kuala_Lumpur", "Asia/Kuching", "Asia/Phnom_Penh", "Asia/Pontianak", "Asia/Rangoon", "Asia/Singapore"];
// 8,19 [-0.5,12.5; 112.5,127.5]  count=6
g_tz[8][19]=["Asia/Brunei", "Asia/Jayapura", "Asia/Kuching", "Asia/Makassar", "Asia/Manila", "Asia/Pontianak"];
// 8,20 [-0.5,12.5; 127.5,142.5]  count=3
g_tz[8][20]=["Asia/Jayapura", "Pacific/Chuuk", "Pacific/Palau"];
// 8,21 [-0.5,12.5; 142.5,157.5]  count=2
g_tz[8][21]=["Pacific/Chuuk", "Pacific/Pohnpei"];
// 8,22 [-0.5,12.5; 157.5,172.5]  count=4
g_tz[8][22]=["Pacific/Kosrae", "Pacific/Kwajalein", "Pacific/Majuro", "Pacific/Pohnpei"];
// 8,23 [-0.5,12.5; 172.5,-172.5]  count=1
g_tz[8][23]=["Pacific/Tarawa"];
// band 8 max zones was 11 in lune 7

g_tz[9] = new Array();
// 9,0 [11.5,22.5; -172.5,-157.5]  count=2
g_tz[9][0]=["Pacific/Honolulu", "Pacific/Johnston"];
// 9,1 [11.5,22.5; -157.5,-142.5]  count=1
g_tz[9][1]=["Pacific/Honolulu"];
// 9,2 [11.5,22.5; -142.5,-127.5] 
g_tz[9][2]= __uninhabited;
// 9,3 [11.5,22.5; -127.5,-112.5]  count=1
g_tz[9][3]=["America/Mazatlan"];
// 9,4 [11.5,22.5; -112.5,-97.5]  count=4
g_tz[9][4]=["America/Bahia_Banderas", "America/Mazatlan", "America/Mexico_City", "America/Monterrey"];
// 9,5 [11.5,22.5; -97.5,-82.5]  count=9
g_tz[9][5]=["America/Belize", "America/Cancun", "America/El_Salvador", "America/Guatemala", "America/Havana", "America/Managua", "America/Merida", "America/Mexico_City", "America/Tegucigalpa"];
// 9,6 [11.5,22.5; -82.5,-67.5]  count=14
g_tz[9][6]=["America/Aruba", "America/Bogota", "America/Caracas", "America/Cayman", "America/Curacao", "America/Grand_Turk", "America/Havana", "America/Jamaica", "America/Kralendijk", "America/Nassau", "America/New_York", "America/Port-au-Prince", "America/Puerto_Rico", "America/Santo_Domingo"];
// 9,7 [11.5,22.5; -67.5,-52.5]  count=19
g_tz[9][7]=["America/Anguilla", "America/Antigua", "America/Barbados", "America/Caracas", "America/Dominica", "America/Grenada", "America/Guadeloupe", "America/Kralendijk", "America/Lower_Princes", "America/Marigot", "America/Martinique", "America/Montserrat", "America/Puerto_Rico", "America/St_Barthelemy", "America/St_Kitts", "America/St_Lucia", "America/St_Thomas", "America/St_Vincent", "America/Tortola"];
// 9,8 [11.5,22.5; -52.5,-37.5] 
g_tz[9][8]= __uninhabited;
// 9,9 [11.5,22.5; -37.5,-22.5]  count=1
g_tz[9][9]=["Atlantic/Cape_Verde"];
// 9,10 [11.5,22.5; -22.5,-7.5]  count=7
g_tz[9][10]=["Africa/Bamako", "Africa/Banjul", "Africa/Bissau", "Africa/Conakry", "Africa/Dakar", "Africa/El_Aaiun", "Africa/Nouakchott"];
// 9,11 [11.5,22.5; -7.5,7.5]  count=7
g_tz[9][11]=["Africa/Algiers", "Africa/Bamako", "Africa/Lagos", "Africa/Niamey", "Africa/Nouakchott", "Africa/Ouagadougou", "Africa/Porto-Novo"];
// 9,12 [11.5,22.5; 7.5,22.5]  count=7
g_tz[9][12]=["Africa/Algiers", "Africa/Douala", "Africa/Khartoum", "Africa/Lagos", "Africa/Ndjamena", "Africa/Niamey", "Africa/Tripoli"];
// 9,13 [11.5,22.5; 22.5,37.5]  count=7
g_tz[9][13]=["Africa/Addis_Ababa", "Africa/Asmara", "Africa/Cairo", "Africa/Juba", "Africa/Khartoum", "Africa/Ndjamena", "Africa/Tripoli"];
// 9,14 [11.5,22.5; 37.5,52.5]  count=8
g_tz[9][14]=["Africa/Addis_Ababa", "Africa/Asmara", "Africa/Djibouti", "Africa/Khartoum", "Africa/Mogadishu", "Asia/Aden", "Asia/Muscat", "Asia/Riyadh"];
// 9,15 [11.5,22.5; 52.5,67.5]  count=3
g_tz[9][15]=["Asia/Aden", "Asia/Muscat", "Asia/Riyadh"];
// 9,16 [11.5,22.5; 67.5,82.5]  count=1
g_tz[9][16]=["Asia/Kolkata"];
// 9,17 [11.5,22.5; 82.5,97.5]  count=4
g_tz[9][17]=["Asia/Bangkok", "Asia/Dhaka", "Asia/Kolkata", "Asia/Rangoon"];
// 9,18 [11.5,22.5; 97.5,112.5]  count=7
g_tz[9][18]=["Asia/Bangkok", "Asia/Chongqing", "Asia/Ho_Chi_Minh", "Asia/Phnom_Penh", "Asia/Rangoon", "Asia/Urumqi", "Asia/Vientiane"];
// 9,19 [11.5,22.5; 112.5,127.5]  count=6
g_tz[9][19]=["Asia/Chongqing", "Asia/Hong_Kong", "Asia/Macau", "Asia/Manila", "Asia/Shanghai", "Asia/Taipei"];
// 9,20 [11.5,22.5; 127.5,142.5] 
g_tz[9][20]= __uninhabited;
// 9,21 [11.5,22.5; 142.5,157.5]  count=2
g_tz[9][21]=["Pacific/Guam", "Pacific/Saipan"];
// 9,22 [11.5,22.5; 157.5,172.5]  count=2
g_tz[9][22]=["Pacific/Majuro", "Pacific/Wake"];
// 9,23 [11.5,22.5; 172.5,-172.5] 
g_tz[9][23]= __uninhabited;
// band 9 max zones was 19 in lune 7

g_tz[10] = new Array();
// 10,0 [21.5,30.5; -172.5,-157.5]  count=1
g_tz[10][0]=["Pacific/Honolulu"];
// 10,1 [21.5,30.5; -157.5,-142.5] 
g_tz[10][1]= __uninhabited;
// 10,2 [21.5,30.5; -142.5,-127.5] 
g_tz[10][2]= __uninhabited;
// 10,3 [21.5,30.5; -127.5,-112.5]  count=3
g_tz[10][3]=["America/Hermosillo", "America/Mazatlan", "America/Santa_Isabel"];
// 10,4 [21.5,30.5; -112.5,-97.5]  count=8
g_tz[10][4]=["America/Chicago", "America/Chihuahua", "America/Hermosillo", "America/Matamoros", "America/Mazatlan", "America/Mexico_City", "America/Monterrey", "America/Ojinaga"];
// 10,5 [21.5,30.5; -97.5,-82.5]  count=8
g_tz[10][5]=["America/Cancun", "America/Chicago", "America/Havana", "America/Matamoros", "America/Merida", "America/Mexico_City", "America/Monterrey", "America/New_York"];
// 10,6 [21.5,30.5; -82.5,-67.5]  count=4
g_tz[10][6]=["America/Grand_Turk", "America/Havana", "America/Nassau", "America/New_York"];
// 10,7 [21.5,30.5; -67.5,-52.5] 
g_tz[10][7]= __uninhabited;
// 10,8 [21.5,30.5; -52.5,-37.5] 
g_tz[10][8]= __uninhabited;
// 10,9 [21.5,30.5; -37.5,-22.5] 
g_tz[10][9]= __uninhabited;
// 10,10 [21.5,30.5; -22.5,-7.5]  count=6
g_tz[10][10]=["Africa/Algiers", "Africa/Casablanca", "Africa/El_Aaiun", "Africa/Nouakchott", "Atlantic/Canary", "Atlantic/Madeira"];
// 10,11 [21.5,30.5; -7.5,7.5]  count=4
g_tz[10][11]=["Africa/Algiers", "Africa/Bamako", "Africa/Casablanca", "Africa/Nouakchott"];
// 10,12 [21.5,30.5; 7.5,22.5]  count=5
g_tz[10][12]=["Africa/Algiers", "Africa/Ndjamena", "Africa/Niamey", "Africa/Tripoli", "Africa/Tunis"];
// 10,13 [21.5,30.5; 22.5,37.5]  count=6
g_tz[10][13]=["Africa/Cairo", "Africa/Khartoum", "Africa/Tripoli", "Asia/Amman", "Asia/Jerusalem", "Asia/Riyadh"];
// 10,14 [21.5,30.5; 37.5,52.5]  count=8
g_tz[10][14]=["Asia/Amman", "Asia/Baghdad", "Asia/Bahrain", "Asia/Dubai", "Asia/Kuwait", "Asia/Qatar", "Asia/Riyadh", "Asia/Tehran"];
// 10,15 [21.5,30.5; 52.5,67.5]  count=6
g_tz[10][15]=["Asia/Dubai", "Asia/Kabul", "Asia/Karachi", "Asia/Muscat", "Asia/Riyadh", "Asia/Tehran"];
// 10,16 [21.5,30.5; 67.5,82.5]  count=5
g_tz[10][16]=["Asia/Karachi", "Asia/Kashgar", "Asia/Kathmandu", "Asia/Kolkata", "Asia/Urumqi"];
// 10,17 [21.5,30.5; 82.5,97.5]  count=6
g_tz[10][17]=["Asia/Dhaka", "Asia/Kathmandu", "Asia/Kolkata", "Asia/Rangoon", "Asia/Thimphu", "Asia/Urumqi"];
// 10,18 [21.5,30.5; 97.5,112.5]  count=6
g_tz[10][18]=["Asia/Chongqing", "Asia/Ho_Chi_Minh", "Asia/Rangoon", "Asia/Shanghai", "Asia/Urumqi", "Asia/Vientiane"];
// 10,19 [21.5,30.5; 112.5,127.5]  count=6
g_tz[10][19]=["Asia/Chongqing", "Asia/Hong_Kong", "Asia/Macau", "Asia/Shanghai", "Asia/Taipei", "Asia/Tokyo"];
// 10,20 [21.5,30.5; 127.5,142.5]  count=1
g_tz[10][20]=["Asia/Tokyo"];
// 10,21 [21.5,30.5; 142.5,157.5]  count=1
g_tz[10][21]=["Asia/Tokyo"];
// 10,22 [21.5,30.5; 157.5,172.5] 
g_tz[10][22]= __uninhabited;
// 10,23 [21.5,30.5; 172.5,-172.5]  count=2
g_tz[10][23]=["Pacific/Honolulu", "Pacific/Midway"];
// band 10 max zones was 8 in lune 4

g_tz[11] = new Array();
// 11,0 [29.5,36.5; -172.5,-157.5] 
g_tz[11][0]= __uninhabited;
// 11,1 [29.5,36.5; -157.5,-142.5] 
g_tz[11][1]= __uninhabited;
// 11,2 [29.5,36.5; -142.5,-127.5] 
g_tz[11][2]= __uninhabited;
// 11,3 [29.5,36.5; -127.5,-112.5]  count=5
g_tz[11][3]=["America/Hermosillo", "America/Los_Angeles", "America/Phoenix", "America/Santa_Isabel", "America/Tijuana"];
// 11,4 [29.5,36.5; -112.5,-97.5]  count=8
g_tz[11][4]=["America/Chicago", "America/Chihuahua", "America/Denver", "America/Hermosillo", "America/Matamoros", "America/Monterrey", "America/Ojinaga", "America/Phoenix"];
// 11,5 [29.5,36.5; -97.5,-82.5]  count=2
g_tz[11][5]=["America/Chicago", "America/New_York"];
// 11,6 [29.5,36.5; -82.5,-67.5]  count=1
g_tz[11][6]=["America/New_York"];
// 11,7 [29.5,36.5; -67.5,-52.5]  count=1
g_tz[11][7]=["Atlantic/Bermuda"];
// 11,8 [29.5,36.5; -52.5,-37.5] 
g_tz[11][8]= __uninhabited;
// 11,9 [29.5,36.5; -37.5,-22.5] 
g_tz[11][9]= __uninhabited;
// 11,10 [29.5,36.5; -22.5,-7.5]  count=2
g_tz[11][10]=["Africa/Casablanca", "Atlantic/Madeira"];
// 11,11 [29.5,36.5; -7.5,7.5]  count=5
g_tz[11][11]=["Africa/Algiers", "Africa/Casablanca", "Africa/Ceuta", "Europe/Gibraltar", "Europe/Madrid"];
// 11,12 [29.5,36.5; 7.5,22.5]  count=6
g_tz[11][12]=["Africa/Algiers", "Africa/Tripoli", "Africa/Tunis", "Europe/Athens", "Europe/Malta", "Europe/Rome"];
// 11,13 [29.5,36.5; 22.5,37.5]  count=12
g_tz[11][13]=["Africa/Cairo", "Africa/Tripoli", "Asia/Amman", "Asia/Beirut", "Asia/Damascus", "Asia/Gaza", "Asia/Hebron", "Asia/Jerusalem", "Asia/Nicosia", "Asia/Riyadh", "Europe/Athens", "Europe/Istanbul"];
// 11,14 [29.5,36.5; 37.5,52.5]  count=6
g_tz[11][14]=["Asia/Amman", "Asia/Baghdad", "Asia/Damascus", "Asia/Kuwait", "Asia/Riyadh", "Asia/Tehran"];
// 11,15 [29.5,36.5; 52.5,67.5]  count=4
g_tz[11][15]=["Asia/Ashgabat", "Asia/Kabul", "Asia/Karachi", "Asia/Tehran"];
// 11,16 [29.5,36.5; 67.5,82.5]  count=6
g_tz[11][16]=["Asia/Kabul", "Asia/Karachi", "Asia/Kashgar", "Asia/Kathmandu", "Asia/Kolkata", "Asia/Urumqi"];
// 11,17 [29.5,36.5; 82.5,97.5]  count=4
g_tz[11][17]=["Asia/Chongqing", "Asia/Kashgar", "Asia/Kathmandu", "Asia/Urumqi"];
// 11,18 [29.5,36.5; 97.5,112.5]  count=3
g_tz[11][18]=["Asia/Chongqing", "Asia/Shanghai", "Asia/Urumqi"];
// 11,19 [29.5,36.5; 112.5,127.5]  count=2
g_tz[11][19]=["Asia/Seoul", "Asia/Shanghai"];
// 11,20 [29.5,36.5; 127.5,142.5]  count=2
g_tz[11][20]=["Asia/Seoul", "Asia/Tokyo"];
// 11,21 [29.5,36.5; 142.5,157.5] 
g_tz[11][21]= __uninhabited;
// 11,22 [29.5,36.5; 157.5,172.5] 
g_tz[11][22]= __uninhabited;
// 11,23 [29.5,36.5; 172.5,-172.5] 
g_tz[11][23]= __uninhabited;
// band 11 max zones was 12 in lune 13

g_tz[12] = new Array();
// 12,0 [35.5,42.5; -172.5,-157.5] 
g_tz[12][0]= __uninhabited;
// 12,1 [35.5,42.5; -157.5,-142.5] 
g_tz[12][1]= __uninhabited;
// 12,2 [35.5,42.5; -142.5,-127.5] 
g_tz[12][2]= __uninhabited;
// 12,3 [35.5,42.5; -127.5,-112.5]  count=4
g_tz[12][3]=["America/Boise", "America/Denver", "America/Los_Angeles", "America/Phoenix"];
// 12,4 [35.5,42.5; -112.5,-97.5]  count=4
g_tz[12][4]=["America/Boise", "America/Chicago", "America/Denver", "America/Phoenix"];
// 12,5 [35.5,42.5; -97.5,-82.5]  count=14
g_tz[12][5]=["America/Chicago", "America/Detroit", "America/Indiana/Indianapolis", "America/Indiana/Knox", "America/Indiana/Marengo", "America/Indiana/Petersburg", "America/Indiana/Tell_City", "America/Indiana/Vevay", "America/Indiana/Vincennes", "America/Indiana/Winamac", "America/Kentucky/Louisville", "America/Kentucky/Monticello", "America/New_York", "America/Toronto"];
// 12,6 [35.5,42.5; -82.5,-67.5]  count=2
g_tz[12][6]=["America/New_York", "America/Toronto"];
// 12,7 [35.5,42.5; -67.5,-52.5] 
g_tz[12][7]= __uninhabited;
// 12,8 [35.5,42.5; -52.5,-37.5] 
g_tz[12][8]= __uninhabited;
// 12,9 [35.5,42.5; -37.5,-22.5]  count=1
g_tz[12][9]=["Atlantic/Azores"];
// 12,10 [35.5,42.5; -22.5,-7.5]  count=2
g_tz[12][10]=["Europe/Lisbon", "Europe/Madrid"];
// 12,11 [35.5,42.5; -7.5,7.5]  count=8
g_tz[12][11]=["Africa/Algiers", "Africa/Casablanca", "Africa/Ceuta", "Europe/Andorra", "Europe/Gibraltar", "Europe/Lisbon", "Europe/Madrid", "Europe/Paris"];
// 12,12 [35.5,42.5; 7.5,22.5]  count=13
g_tz[12][12]=["Africa/Algiers", "Africa/Tunis", "Europe/Athens", "Europe/Belgrade", "Europe/Malta", "Europe/Paris", "Europe/Podgorica", "Europe/Rome", "Europe/Skopje", "Europe/Sofia", "Europe/Tirane", "Europe/Vatican", "Europe/Zagreb"];
// 12,13 [35.5,42.5; 22.5,37.5]  count=7
g_tz[12][13]=["Asia/Damascus", "Asia/Nicosia", "Europe/Athens", "Europe/Belgrade", "Europe/Istanbul", "Europe/Skopje", "Europe/Sofia"];
// 12,14 [35.5,42.5; 37.5,52.5]  count=10
g_tz[12][14]=["Asia/Aqtau", "Asia/Ashgabat", "Asia/Baghdad", "Asia/Baku", "Asia/Damascus", "Asia/Tbilisi", "Asia/Tehran", "Asia/Yerevan", "Europe/Istanbul", "Europe/Moscow"];
// 12,15 [35.5,42.5; 52.5,67.5]  count=9
g_tz[12][15]=["Asia/Almaty", "Asia/Aqtau", "Asia/Ashgabat", "Asia/Dushanbe", "Asia/Kabul", "Asia/Qyzylorda", "Asia/Samarkand", "Asia/Tashkent", "Asia/Tehran"];
// 12,16 [35.5,42.5; 67.5,82.5]  count=10
g_tz[12][16]=["Asia/Almaty", "Asia/Bishkek", "Asia/Dushanbe", "Asia/Kabul", "Asia/Karachi", "Asia/Kashgar", "Asia/Kolkata", "Asia/Samarkand", "Asia/Tashkent", "Asia/Urumqi"];
// 12,17 [35.5,42.5; 82.5,97.5]  count=3
g_tz[12][17]=["Asia/Chongqing", "Asia/Kashgar", "Asia/Urumqi"];
// 12,18 [35.5,42.5; 97.5,112.5]  count=4
g_tz[12][18]=["Asia/Chongqing", "Asia/Shanghai", "Asia/Ulaanbaatar", "Asia/Urumqi"];
// 12,19 [35.5,42.5; 112.5,127.5]  count=5
g_tz[12][19]=["Asia/Chongqing", "Asia/Harbin", "Asia/Pyongyang", "Asia/Seoul", "Asia/Shanghai"];
// 12,20 [35.5,42.5; 127.5,142.5]  count=5
g_tz[12][20]=["Asia/Harbin", "Asia/Pyongyang", "Asia/Seoul", "Asia/Tokyo", "Asia/Vladivostok"];
// 12,21 [35.5,42.5; 142.5,157.5]  count=1
g_tz[12][21]=["Asia/Tokyo"];
// 12,22 [35.5,42.5; 157.5,172.5] 
g_tz[12][22]= __uninhabited;
// 12,23 [35.5,42.5; 172.5,-172.5] 
g_tz[12][23]= __uninhabited;
// band 12 max zones was 14 in lune 5

g_tz[13] = new Array();
// 13,0 [41.5,48.5; -172.5,-157.5] 
g_tz[13][0]= __uninhabited;
// 13,1 [41.5,48.5; -157.5,-142.5] 
g_tz[13][1]= __uninhabited;
// 13,2 [41.5,48.5; -142.5,-127.5] 
g_tz[13][2]= __uninhabited;
// 13,3 [41.5,48.5; -127.5,-112.5]  count=4
g_tz[13][3]=["America/Boise", "America/Denver", "America/Los_Angeles", "America/Vancouver"];
// 13,4 [41.5,48.5; -112.5,-97.5]  count=6
g_tz[13][4]=["America/Boise", "America/Chicago", "America/Denver", "America/North_Dakota/Beulah", "America/North_Dakota/Center", "America/North_Dakota/New_Salem"];
// 13,5 [41.5,48.5; -97.5,-82.5]  count=9
g_tz[13][5]=["America/Atikokan", "America/Chicago", "America/Detroit", "America/Indiana/Indianapolis", "America/Menominee", "America/New_York", "America/Thunder_Bay", "America/Toronto", "America/Winnipeg"];
// 13,6 [41.5,48.5; -82.5,-67.5]  count=5
g_tz[13][6]=["America/Detroit", "America/Moncton", "America/Montreal", "America/New_York", "America/Toronto"];
// 13,7 [41.5,48.5; -67.5,-52.5]  count=7
g_tz[13][7]=["America/Glace_Bay", "America/Halifax", "America/Miquelon", "America/Moncton", "America/Montreal", "America/New_York", "America/St_Johns"];
// 13,8 [41.5,48.5; -52.5,-37.5] 
g_tz[13][8]= __uninhabited;
// 13,9 [41.5,48.5; -37.5,-22.5] 
g_tz[13][9]= __uninhabited;
// 13,10 [41.5,48.5; -22.5,-7.5]  count=2
g_tz[13][10]=["Europe/Lisbon", "Europe/Madrid"];
// 13,11 [41.5,48.5; -7.5,7.5]  count=7
g_tz[13][11]=["Europe/Andorra", "Europe/Lisbon", "Europe/Madrid", "Europe/Monaco", "Europe/Paris", "Europe/Rome", "Europe/Zurich"];
// 13,12 [41.5,48.5; 7.5,22.5]  count=20
g_tz[13][12]=["Europe/Belgrade", "Europe/Berlin", "Europe/Bratislava", "Europe/Bucharest", "Europe/Budapest", "Europe/Ljubljana", "Europe/Paris", "Europe/Podgorica", "Europe/Rome", "Europe/San_Marino", "Europe/Sarajevo", "Europe/Skopje", "Europe/Sofia", "Europe/Tirane", "Europe/Uzhgorod", "Europe/Vaduz", "Europe/Vatican", "Europe/Vienna", "Europe/Zagreb", "Europe/Zurich"];
// 13,13 [41.5,48.5; 22.5,37.5]  count=13
g_tz[13][13]=["Europe/Athens", "Europe/Belgrade", "Europe/Bucharest", "Europe/Budapest", "Europe/Chisinau", "Europe/Istanbul", "Europe/Kiev", "Europe/Moscow", "Europe/Simferopol", "Europe/Skopje", "Europe/Sofia", "Europe/Uzhgorod", "Europe/Zaporozhye"];
// 13,14 [41.5,48.5; 37.5,52.5]  count=9
g_tz[13][14]=["Asia/Aqtau", "Asia/Ashgabat", "Asia/Baku", "Asia/Oral", "Asia/Tbilisi", "Europe/Istanbul", "Europe/Kiev", "Europe/Moscow", "Europe/Volgograd"];
// 13,15 [41.5,48.5; 52.5,67.5]  count=6
g_tz[13][15]=["Asia/Almaty", "Asia/Aqtau", "Asia/Aqtobe", "Asia/Ashgabat", "Asia/Qyzylorda", "Asia/Samarkand"];
// 13,16 [41.5,48.5; 67.5,82.5]  count=6
g_tz[13][16]=["Asia/Almaty", "Asia/Bishkek", "Asia/Kashgar", "Asia/Qyzylorda", "Asia/Tashkent", "Asia/Urumqi"];
// 13,17 [41.5,48.5; 82.5,97.5]  count=5
g_tz[13][17]=["Asia/Almaty", "Asia/Chongqing", "Asia/Hovd", "Asia/Kashgar", "Asia/Urumqi"];
// 13,18 [41.5,48.5; 97.5,112.5]  count=6
g_tz[13][18]=["Asia/Choibalsan", "Asia/Chongqing", "Asia/Hovd", "Asia/Shanghai", "Asia/Ulaanbaatar", "Asia/Urumqi"];
// 13,19 [41.5,48.5; 112.5,127.5]  count=6
g_tz[13][19]=["Asia/Choibalsan", "Asia/Chongqing", "Asia/Harbin", "Asia/Pyongyang", "Asia/Shanghai", "Asia/Ulaanbaatar"];
// 13,20 [41.5,48.5; 127.5,142.5]  count=5
g_tz[13][20]=["Asia/Harbin", "Asia/Pyongyang", "Asia/Sakhalin", "Asia/Tokyo", "Asia/Vladivostok"];
// 13,21 [41.5,48.5; 142.5,157.5]  count=3
g_tz[13][21]=["Asia/Sakhalin", "Asia/Tokyo", "Asia/Ust-Nera"];
// 13,22 [41.5,48.5; 157.5,172.5] 
g_tz[13][22]= __uninhabited;
// 13,23 [41.5,48.5; 172.5,-172.5] 
g_tz[13][23]= __uninhabited;
// band 13 max zones was 20 in lune 12

g_tz[14] = new Array();
// 14,0 [47.5,66.5; -172.5,-157.5]  count=4
g_tz[14][0]=["America/Adak", "America/Anchorage", "America/Nome", "Asia/Anadyr"];
// 14,1 [47.5,66.5; -157.5,-142.5]  count=1
g_tz[14][1]=["America/Anchorage"];
// 14,2 [47.5,66.5; -142.5,-127.5]  count=9
g_tz[14][2]=["America/Anchorage", "America/Dawson", "America/Juneau", "America/Metlakatla", "America/Sitka", "America/Vancouver", "America/Whitehorse", "America/Yakutat", "America/Yellowknife"];
// 14,3 [47.5,66.5; -127.5,-112.5]  count=9
g_tz[14][3]=["America/Cambridge_Bay", "America/Creston", "America/Dawson_Creek", "America/Denver", "America/Edmonton", "America/Los_Angeles", "America/Vancouver", "America/Whitehorse", "America/Yellowknife"];
// 14,4 [47.5,66.5; -112.5,-97.5]  count=10
g_tz[14][4]=["America/Cambridge_Bay", "America/Chicago", "America/Denver", "America/Edmonton", "America/North_Dakota/Beulah", "America/Rankin_Inlet", "America/Regina", "America/Swift_Current", "America/Winnipeg", "America/Yellowknife"];
// 14,5 [47.5,66.5; -97.5,-82.5]  count=11
g_tz[14][5]=["America/Atikokan", "America/Chicago", "America/Coral_Harbour", "America/Detroit", "America/Iqaluit", "America/Nipigon", "America/Rainy_River", "America/Rankin_Inlet", "America/Thunder_Bay", "America/Toronto", "America/Winnipeg"];
// 14,6 [47.5,66.5; -82.5,-67.5]  count=7
g_tz[14][6]=["America/Coral_Harbour", "America/Goose_Bay", "America/Iqaluit", "America/Moncton", "America/Montreal", "America/Pangnirtung", "America/Toronto"];
// 14,7 [47.5,66.5; -67.5,-52.5]  count=8
g_tz[14][7]=["America/Blanc-Sablon", "America/Godthab", "America/Goose_Bay", "America/Halifax", "America/Moncton", "America/Montreal", "America/Pangnirtung", "America/St_Johns"];
// 14,8 [47.5,66.5; -52.5,-37.5]  count=1
g_tz[14][8]=["America/Godthab"];
// 14,9 [47.5,66.5; -37.5,-22.5]  count=2
g_tz[14][9]=["America/Godthab", "Atlantic/Reykjavik"];
// 14,10 [47.5,66.5; -22.5,-7.5]  count=4
g_tz[14][10]=["Atlantic/Faroe", "Atlantic/Reykjavik", "Europe/Dublin", "Europe/London"];
// 14,11 [47.5,66.5; -7.5,7.5]  count=13
g_tz[14][11]=["Atlantic/Faroe", "Europe/Amsterdam", "Europe/Berlin", "Europe/Brussels", "Europe/Dublin", "Europe/Guernsey", "Europe/Isle_of_Man", "Europe/Jersey", "Europe/London", "Europe/Luxembourg", "Europe/Oslo", "Europe/Paris", "Europe/Zurich"];
// 14,12 [47.5,66.5; 7.5,22.5]  count=19
g_tz[14][12]=["Europe/Berlin", "Europe/Bratislava", "Europe/Bucharest", "Europe/Budapest", "Europe/Copenhagen", "Europe/Helsinki", "Europe/Kaliningrad", "Europe/Mariehamn", "Europe/Oslo", "Europe/Paris", "Europe/Prague", "Europe/Riga", "Europe/Stockholm", "Europe/Tallinn", "Europe/Uzhgorod", "Europe/Vienna", "Europe/Vilnius", "Europe/Warsaw", "Europe/Zurich"];
// 14,13 [47.5,66.5; 22.5,37.5]  count=16
g_tz[14][13]=["Europe/Bratislava", "Europe/Bucharest", "Europe/Budapest", "Europe/Chisinau", "Europe/Helsinki", "Europe/Kaliningrad", "Europe/Kiev", "Europe/Minsk", "Europe/Moscow", "Europe/Riga", "Europe/Stockholm", "Europe/Tallinn", "Europe/Uzhgorod", "Europe/Vilnius", "Europe/Warsaw", "Europe/Zaporozhye"];
// 14,14 [47.5,66.5; 37.5,52.5]  count=7
g_tz[14][14]=["Asia/Aqtau", "Asia/Oral", "Asia/Yekaterinburg", "Europe/Kiev", "Europe/Moscow", "Europe/Samara", "Europe/Volgograd"];
// 14,15 [47.5,66.5; 52.5,67.5]  count=9
g_tz[14][15]=["Asia/Almaty", "Asia/Aqtau", "Asia/Aqtobe", "Asia/Oral", "Asia/Qyzylorda", "Asia/Yekaterinburg", "Europe/Moscow", "Europe/Samara", "Europe/Volgograd"];
// 14,16 [47.5,66.5; 67.5,82.5]  count=5
g_tz[14][16]=["Asia/Almaty", "Asia/Novosibirsk", "Asia/Omsk", "Asia/Qyzylorda", "Asia/Yekaterinburg"];
// 14,17 [47.5,66.5; 82.5,97.5]  count=10
g_tz[14][17]=["Asia/Almaty", "Asia/Hovd", "Asia/Irkutsk", "Asia/Krasnoyarsk", "Asia/Novokuznetsk", "Asia/Novosibirsk", "Asia/Omsk", "Asia/Ulaanbaatar", "Asia/Urumqi", "Asia/Yekaterinburg"];
// 14,18 [47.5,66.5; 97.5,112.5]  count=6
g_tz[14][18]=["Asia/Choibalsan", "Asia/Hovd", "Asia/Irkutsk", "Asia/Krasnoyarsk", "Asia/Ulaanbaatar", "Asia/Yakutsk"];
// 14,19 [47.5,66.5; 112.5,127.5]  count=6
g_tz[14][19]=["Asia/Choibalsan", "Asia/Harbin", "Asia/Irkutsk", "Asia/Shanghai", "Asia/Ulaanbaatar", "Asia/Yakutsk"];
// 14,20 [47.5,66.5; 127.5,142.5]  count=7
g_tz[14][20]=["Asia/Harbin", "Asia/Khandyga", "Asia/Magadan", "Asia/Sakhalin", "Asia/Ust-Nera", "Asia/Vladivostok", "Asia/Yakutsk"];
// 14,21 [47.5,66.5; 142.5,157.5]  count=5
g_tz[14][21]=["Asia/Kamchatka", "Asia/Magadan", "Asia/Sakhalin", "Asia/Ust-Nera", "Asia/Vladivostok"];
// 14,22 [47.5,66.5; 157.5,172.5]  count=4
g_tz[14][22]=["America/Adak", "Asia/Anadyr", "Asia/Kamchatka", "Asia/Magadan"];
// 14,23 [47.5,66.5; 172.5,-172.5]  count=4
g_tz[14][23]=["America/Adak", "America/Nome", "Asia/Anadyr", "Asia/Kamchatka"];
// band 14 max zones was 19 in lune 12

g_tz[15] = new Array();
// 15,0 [65.5,90.5; -172.5,-157.5]  count=3
g_tz[15][0]=["America/Anchorage", "America/Nome", "Asia/Anadyr"];
// 15,1 [65.5,90.5; -157.5,-142.5]  count=1
g_tz[15][1]=["America/Anchorage"];
// 15,2 [65.5,90.5; -142.5,-127.5]  count=4
g_tz[15][2]=["America/Anchorage", "America/Inuvik", "America/Whitehorse", "America/Yellowknife"];
// 15,3 [65.5,90.5; -127.5,-112.5]  count=2
g_tz[15][3]=["America/Cambridge_Bay", "America/Yellowknife"];
// 15,4 [65.5,90.5; -112.5,-97.5]  count=3
g_tz[15][4]=["America/Cambridge_Bay", "America/Rankin_Inlet", "America/Yellowknife"];
// 15,5 [65.5,90.5; -97.5,-82.5]  count=5
g_tz[15][5]=["America/Cambridge_Bay", "America/Coral_Harbour", "America/Iqaluit", "America/Rankin_Inlet", "America/Resolute"];
// 15,6 [65.5,90.5; -82.5,-67.5]  count=3
g_tz[15][6]=["America/Iqaluit", "America/Pangnirtung", "America/Thule"];
// 15,7 [65.5,90.5; -67.5,-52.5]  count=3
g_tz[15][7]=["America/Godthab", "America/Pangnirtung", "America/Thule"];
// 15,8 [65.5,90.5; -52.5,-37.5]  count=1
g_tz[15][8]=["America/Godthab"];
// 15,9 [65.5,90.5; -37.5,-22.5]  count=4
g_tz[15][9]=["America/Danmarkshavn", "America/Godthab", "America/Scoresbysund", "Atlantic/Reykjavik"];
// 15,10 [65.5,90.5; -22.5,-7.5]  count=5
g_tz[15][10]=["America/Danmarkshavn", "America/Godthab", "America/Scoresbysund", "Arctic/Longyearbyen", "Atlantic/Reykjavik"];
// 15,11 [65.5,90.5; -7.5,7.5] 
g_tz[15][11]= __uninhabited;
// 15,12 [65.5,90.5; 7.5,22.5]  count=4
g_tz[15][12]=["Arctic/Longyearbyen", "Europe/Helsinki", "Europe/Oslo", "Europe/Stockholm"];
// 15,13 [65.5,90.5; 22.5,37.5]  count=5
g_tz[15][13]=["Arctic/Longyearbyen", "Europe/Helsinki", "Europe/Moscow", "Europe/Oslo", "Europe/Stockholm"];
// 15,14 [65.5,90.5; 37.5,52.5]  count=1
g_tz[15][14]=["Europe/Moscow"];
// 15,15 [65.5,90.5; 52.5,67.5]  count=2
g_tz[15][15]=["Asia/Yekaterinburg", "Europe/Moscow"];
// 15,16 [65.5,90.5; 67.5,82.5]  count=3
g_tz[15][16]=["Asia/Krasnoyarsk", "Asia/Yekaterinburg", "Europe/Moscow"];
// 15,17 [65.5,90.5; 82.5,97.5]  count=2
g_tz[15][17]=["Asia/Krasnoyarsk", "Asia/Yekaterinburg"];
// 15,18 [65.5,90.5; 97.5,112.5]  count=2
g_tz[15][18]=["Asia/Krasnoyarsk", "Asia/Yakutsk"];
// 15,19 [65.5,90.5; 112.5,127.5]  count=2
g_tz[15][19]=["Asia/Krasnoyarsk", "Asia/Yakutsk"];
// 15,20 [65.5,90.5; 127.5,142.5]  count=5
g_tz[15][20]=["Asia/Khandyga", "Asia/Magadan", "Asia/Ust-Nera", "Asia/Vladivostok", "Asia/Yakutsk"];
// 15,21 [65.5,90.5; 142.5,157.5]  count=2
g_tz[15][21]=["Asia/Magadan", "Asia/Vladivostok"];
// 15,22 [65.5,90.5; 157.5,172.5]  count=3
g_tz[15][22]=["Asia/Anadyr", "Asia/Magadan", "Asia/Vladivostok"];
// 15,23 [65.5,90.5; 172.5,-172.5]  count=1
g_tz[15][23]=["Asia/Anadyr"];
// band 15 max zones was 5 in lune 5

// band 13 had the maximum zones in lune 12 (20)
