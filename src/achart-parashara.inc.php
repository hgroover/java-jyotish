<?php
// achart-parashara.inc.php - Brihat-parashara-hora-shastra rule evaluation
// Copyright (C) 2012-2017 San Diego College of Ayurveda. All rights reserved
// Copyright (C) 2017-2024 Narayana Ayurveda and Yoga Academy LLC. All rights reserved.
// Copyright (C) 2012-2024 Henry Groover. All rights reserved.

// All lead-ins for properties are capital letters
global $allcaps;
$allcaps = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
// Rule chain separators must not exist in property contents
global $chainseps;
$chainseps = "&|!()";

// Collection of rules to evaluate
// Currently these are only simple if / elseif / else rules with no nesting
// Syntax: conditionstatement <tab> expression <tab> text
// conditionstatement :: conditionop <space> pointid
// conditionop :: 'if' | 'else' | 'elseif'
// pointid :: planetid | lordid | setid
// planetid :: planetabbr '-' divnum
// planetabbr :: 'as' | 'su' | 'mo' | 'me' | 've' | 'ma' | 'ju' | 'sa' | 'ra' | 'ke'
// divnum :: '01' | '09'
// lordid :: 'L' houseid
// houseid :: '01' .. '12'
global $rules;

$rules[] = 'if ju-01	HasValue($value,"H02") || HasValue($value,"Cma")	15.3: dhanAdhipo gurur yasya dhana-bhAva-gato bhavet | bhaumena sahito vA \'pi dhanavAn sa naro bhavet';
$rules[] = 'if L02	(HasValue($value,"H11") && HasValue($p[$lords["L11"]],"H02")) || (GetValue($value,"H")==GetValue($p[$lords["L11"]],"H") && InKendraHouse($value))	15.4: dhaneze lAbha-bhAvasthe lAbheze vA dhanaM gate | tAv ubhau kendra-koNa-sthau dhanavAn sa naro bhavet';
// Also applies if Jupiter has conjunction or aspect of Venus and neither are debilitated
// Handled in code below
//$rules[] = 'if L02	(InKendraHouse($value) && InTrineFrom($value,$p[$lords["L11"]]))	15.5: dhaneze kendra-rAzi-sthe lAbheze tat-trikoNage | guru-zukra-yute dRSTe dhana-lAbham udIrayet';
// 15.6-7 involves house aspect and house malefic occupation
$rules[] = 'if L02	InTrikHouse($value) && InTrikHouse($p[$lords["L11"]]) && HasValue($p["ma-01"],"H11") && HasValue($p["ra-01"],"H02")	15.8: SaSThe \'STame vyaye vA \'pi dhana-lAbhAdhipau yadi | lAbhe kujo dhane rAhU rAja-daNDAd dhana-kSayaH';
// 15.9 involves house benefic occupation
$rules[] = 'if L02	IsExalted($value) || InOwnSign($value)	15.10: svabhAva-sthe dhanAdhIze jAtako jana-poSakaH | paropakArI khyAtaz ca vijJeyo dvija-sattama';
// 15.11 involves determination of parvatamsa etc.
// 15.12 requires sad-bala of L02
// 15.13 requires house occupation of malefic
$rules[] = 'if L02	IsHighlyExalted($value) || (InOwnSign($value) && HasValue($value,"Rju"))	15.14: dhaneze paramocca-sthe kiM vA svabhavanAzrite | guruNA vIkSate jAtaH khyAtaH sarva-jana-priyaH';
// 15.15-18 to be coded

// 16.1 requires house aspect or benefic occupation
// 16.2 can also use house aspect
$rules[] = 'if L03	HasValue($value,"Cma") && HasValue($value,"H03")	16.2: sa-bhaumo bhrAtR-bhAvezo bhrAtR-bhAvaM prapazyati | bhrAtR-kSetragato vA \'pi bhrAtR-saukhyaM vinirdizet';
// 16.4 requires generalization of gender


// Chapter 26, conditional rules not covered by bhaveza-phala
$rules[] = 'if L01	HasValue($value,"H12") && !(HasValue($value,"Dcx0") || HasValue($value,"Drx0"))	26.12b: vyartha-vyayI mahA-krodhI zubha-dRg-yoga-varjite';
$rules[] = 'if L02	HasValue($value,"H04") && (HasValue($value,"Cju") || IsExalted($value))	26.16b: guruNA saMyute svocce rAja-tulyo naro bhavet';

// Test rules
/************
$rules[] = 'if mo-01	HasValue($value,"H01") && HasValue($value,"Cma")	test 1: moon/mars in lagna';
$rules[] = 'else mo-01	0	test 1b: else for moon/mars in lagna';
$rules[] = 'if mo-01	InTrikHouse($value)	test 2: moon in trik';
$rules[] = 'elseif mo-01	HasValue($value,"Dcx2") && !InKendraHouse($value)	test2b: moon with malefic and not in kendra';
$rules[] = 'else mo-01	0	test2c: moon not in trik or with malefic but not in kendra';
$rules[] = 'if L11	InKendraHouse($value) && HasValue($value,"Dcx0")	test3: lord of 11th in kendra with benefic';
$rules[] = 'if L02	HasValue($value,"H11") && HasValue($p[$lords["L11"]])	test4: lord of 2 in 11 and lord of 11 in 2';
$rules[] = 'elseif L02	LogOutput("got " . $p[$lords["L11"]] . " house compare " . $value) && LogOutput(GetValue($value,"H") . "==" . GetValue($p[$lords["L11"]],"H")) && GetValue($value,"H")==GetValue($p[$lords["L11"]],"H")	test4b: lord of 2 and lord of 11 in same house';
************/


global $hl;
global $hlcond;
// Ch. 26, bhAveza-phala (effects of lords of houses)
// from pg. 274 in Volume I of Pt. Girish Chandra Sharma's edition
$hl["L01H01"] = "L1 in asc (26.1): lagneze lagnage deha-sukhabhAg bhuja-vikramI | manasvI caJcalaz caiva dvi-bhAryo purago 'pi vA";
$hl["L01H02"] = "L1 in H2 (26.2): lagneze dhanage bAlo lAbhavAn paNDitaH sukhI | suzIlo dharma-vin mAnI bahu-dAro guNair yutaH";
$hl["L01H03"] = "L1 in H3 (26.3): lagneze sahaje jAtaH siMha-tulya-parAkramI | sarva-sampad-yuto mAnI dvi-bhAryo matimAn sukhI";
$hl["L01H04"] = "L1 in H4 (26.4): lagneze sukhage bAlaH pitR-mAtR-sukhAnvitaH | bahu-bhRatR-yutaH kAmI guNa-rUpa-samAnvitaH";
$hl["L01H05"] = "L1 in H5 (26.5): lagneze sutage janto suta-saukhyaM ca madhyamam | prathamApatya-nAzaH syAn mAnI krodhI nRpa-priyaH";
$hl["L01H06"] = "L1 in H6 (26.6): lagneze SaSThage jAto deha-saukhya-vivarjitaH";
$hlcond["L01H06"] = array( "L01Dcx2&!L01Drx0", "26.6a: pApADhye zatrutaH pIDA saumya-dRSTi-vivarjitaH" );
$hl["L01H07"] = "";
$hlcond["L01H07"] = array( "L01X2", "26.7a: lagneze saptame pApe bhAryA tasya na jIvati", "L01X0", "26.7b: zubhe 'Tano daridro vA virakto vA nRpo 'pi vA" );
$hl["L01H08"] = "L1 in H8 (26.8): lagneze 'STamage jAtaH siddha-vidyA-vizAradaH | rogI caurI mahA-krodhI dyUtI ca para-dAragaH";
$hl["L01H09"] = "L1 in H9 (26.9): lagneze bhAgyage jAto bhAgyavAJ jana-vallabhaH | viSNu-bhaktaH paTur vAgmI dAra-putra-dhanair yutaH";
$hl["L01H10"] = "L1 in H10 (26.10): lagneze dazame jAtaH pitR-saukha-samanvitaH | nRpamAnye jane khyAtaH svArjitasvo na saMzayaH";
$hl["L01H11"] = "L1 in H11 (26.11): lagneze lAbhage jAtaH sadA lAbha-samanvitaH | suzIlaH khyAta-kIrtiz ca bahu-dAra-guNair yutaH";
$hl["L01H12"] = "L1 in H12 (26.12): lagneze vyaya-bhAvasthe deha-saukhya-vivarjitaH";
// Other condition in rules: vyartha-vyayI mahA-krodhI zubha-dRg-yoga-varjite

$hl["L02H01"] = "L2 in asc (26.13): dhaneze lagnage jAtaH putravAn dhana-saMyutaH | kuTumba-kaNTakaH kAmI niSThuraH para-kArya-kRt";
$hl["L02H02"] = "L2 in H2 (26.14): dhaneze dhanage jAto dhanavAn garva-saMyutaH | dvi-bhAryo bahu-bhAryo vA suta-hInaH prajAyate";
$hl["L02H03"] = "L2 in H3 (26.15): dhaneze sahaje jAto vikramI matimAn guNI";
$hlcond["L02H03"] = array( "Dcx2X2", "26.15b: pApADhye deva-nindakaH", "Dcx0X0", "26.15a: kAmI lobhI zubhADhye ca" );
$hl["L02H04"] = "L2 in H4 (26.16): dhaneze sukha-bhAvasthe sarva-sampat-samanvitaH";
// Other condition in rules: guruNA saMyute svocce rAja-tulyo naro bhavet
$hl["L02H05"] = "L2 in H5 (26.17): dhaneze suta-bhAva-sthe jAto dhana-samAnvitaH | dhanopArjana-zIlAz ca jAyante tat-sutA api";
$hl["L02H06"] = "";
$hlcond["L02H06"] = array( "L02Dcx0", "26.18a: dhaneze ripu-bhAva-sthe sazubhe zatruto dhanam", "L02Dcx2", "26.18b: sapApe zatruto hAnir jaGkAvaikalyavAn bhavet" );
$hl["L02H07"] = "L2 in H7 (26.19a): dhaneze saptame jAtaH paradAra-rato bhiSak";
$hlcond["L02H07"] = array( "L02Drx2|L02Dcx2", "26.19b: pApekSita-yute tasya bhAryA ca vyabhicAriNI" );
$hl["L02H08"] = "L2 in H8 (26.20): dhaneze 'STame jAto bhuri-bhUmi-dhanair yataH | patnI-sukhaM bhavet svalpaM jyeSTha-bhrAtR-sukhaM na hi";
$hl["L02H09"] = "L2 in H9 (26.21): dhaneze dharma-bhAva-sthe dhanavAn udyamI paTuH | bAlye rogI sukhI pazcAt tIrtha-dharma-vratAdikRt";
$hl["L02H10"] = "L2 in H10 (26.22): dhaneze karmage jAtaH kAmI mAnI ca paNDitaH | bahu-dAra-dhanair yuktaH kiM ca putra-sukhojjitaH";
$hl["L02H11"] = "L2 in H11 (26.23): dhaneze lAbhu-bhAva-sthe sarva-lAbha-samanvitaH | sadodyoga-yuto mAnI kIrtimAn jAyate naraH";
$hl["L02H12"] = "L2 in H12 (26.24): dhaneze vyaya-bhAva-sthe sAhasI dhana-varjitaH | para-bhAgya-ratas tasya jyeSThApatya-sukhaM na hi";

$hl["L03H01"] = "L3 in asc (26.25): lagnage sahajAdhIze sva-bhujArjita-vitta-vAn | sevAjJaH sAhasI jAto vidyA-hIno 'pi buddhimAn";
$hl["L03H02"] = "L3 in H2 (26.26): dvitIye sahajAdhIze sthUlo vikrama-varjitaH | svalpArambhI sukhI na syAt para-strI-dhana-kAmukaH";
$hl["L03H03"] = "L3 in H3 (26.27): sahaje sahajAdhIze sahodara-sukhAnvitaH | dhana-putra-yuto hraSTo bhunakti sukham adbhutam";
$hl["L03H04"] = "L3 in H4 (26.28): sukha-sthe sahajAdhIze sukhI ca dhana-saMyutaH | matimAn jAyate bAlo duSTa-bhAryA-patiz ca saH";
$hl["L03H05"] = "L3 in H5 (26.29): sutasthe sahajAdhIze putravAn guNasaMyutaH | bhAryA tasya bhavet krUrA krUra-graha-yutekSite";
$hl["L03H06"] = "L3 in H6 (26.30): SaSTha-bhAve tRtIyeze bhRatR-zatrur mahA-dhanI | mAtulaiz ca samaM vairaM mAtulAnI-priyo naraH";
$hl["L03H07"] = "L3 in H7 (26.31): saptame sahajAdhIze rAja-sevA-paro naraH | bAlye duHkhI sukhI cAnte jAyate nAtra saMzayaH";
$hl["L03H08"] = "L3 in H8 (26.32): aSTame sahajAdhIze jAtaz cauro naro bhavet | dAsa-vRttopajIvI ca rAja-dvAre mRtir bhavet";
$hl["L03H09"] = "L3 in H9 (26.33): navame sahajAdhIze pituH sukha-vivarjitaH | strIbhir bhAgyodayas tasya putrAdi-sukha-saMyutaH";
$hl["L03H10"] = "L3 in H10 (26.34): dazame sahajAdhIze jAtaH sarva-sukhAnvitaH | sva-bhujArjita-vittaz ca duSTa-strI-bharaNe rataH";
$hl["L03H11"] = "L3 in H11 (26.35): lAbhage sahajAdhIze vyApAre lAbhavAn sadA | vidyA-hIno 'pi medhAvI sAhasI para-sevakaH";
$hl["L03H12"] = "L3 in H12 (26.36): vyayasthe sahajAdhIze kukArye vyayakRj janaH | pitA tasya bhavet krUraH strIbhir bhAgyodayas tathA";

$hl["L04H01"] = "L4 in asc (26.37): sukheze lagnage jAto vidyA-guNa-vibhUSitaH | bhUmi-vAhana-saMyukto mAtuH sukha-samanvitaH";
$hl["L04H02"] = "L4 in H2 (26.38): sukheze dhanage jAto bhogI sarva-dhanAnvitaH | kuTumba-sahito mAnI sAhasI kuhakAnvitaH";
$hl["L04H03"] = "L4 in H3 (26.39): sukheze sahaje jAto vikramI bhRtya-saMyutaH | udAro 'rug guNI dAtA sva-bhujArjita-vittavAn";
$hl["L04H04"] = "L4 in H4 (26.40): sukheze sukha-bhAvasthe mantrI sarva-dhanAnvitaH | caturaH zIlavAn mAnI jJAnavAn strI-priyaH sukhI";
$hl["L04H05"] = "L4 in H5 (26.41): sukheze putra-bhAvasthe sukhI sarva-jana-priyaH | viSNubhakto guNI mAnI svabhujArjita-vittavAn";
$hl["L04H06"] = "L4 in H6 (26.42): sukheze ripu-bhAvasthe mAtuH sukha-vivarjitaH | krodhI cauro 'bhicArI ca svecchAcAraz ca durmanAH";
$hl["L04H07"] = "L4 in H7 (26.43): sukheze saptame jAto bahu-vidyA-samanvitaH | priyArjita-dhana-tyAgI sabhAyAM mUkavad bhavet";
$hl["L04H08"] = "L4 in H8 (26.44): sukheze randhrbhAvasthe gRhAdi-sukha-varjitaH | pitroH sukhaM bhaved alpaM jAtaH klIba-samo bhavet";
$hl["L04H09"] = "L4 in H9 (26.45): sukheze bhAgyabhAvasthe jAtaH sarva-jana-priyaH | deva-bhakto guNI mAnI bhavet sarva-sukhAnvitaH";
$hl["L04H10"] = "L4 in H10 (26.46): sukheze karma-bhAvasthe rAja-mAnyo naro bhavet | rasAyanI mahA-hRSTo sukha-bhogI jitendriyaH";
$hl["L04H11"] = "L4 in H11 (26.47): sukheze lAbhage jAto gupta-roga-bhayAnvitaH | udAro guNavAn dAtA paropakaraNe rataH";
$hl["L04H12"] = "L4 in H12 (26.48): sukheze vyayabhAvasthe gRhAdi-sukha-varjitaH | jAto durvyasanI mUDhaH sadA 'lasya-samanvitaH";

$hl["L05H01"] = "L5 in asc (26.49): suteze lagnage jAto vidvAn putra-sukhAnvitaH | kadaryo vakra-cittaz ca para-dravyApahArakaH";
$hl["L05H02"] = "L5 in H2 (26.50): suteze dhanage jAto bahu-putro dhanAnvitaH | kuTumba-poSako mAnI strI-priyaH suyazA bhuvi";
$hl["L05H03"] = "L5 in H3 (26.51): suteze sahaje bhAve jAyate sodara-priyaH | pizunaz ca kadaryaz ca svakArya-nirataH sadA";
$hl["L05H04"] = "L5 in H4 (26.52): suteze sukhabhAvasthe sukhI mAtR-sukhAnvitaH | lakSmI-yuktaH subuddhiz ca rAjJo 'mAtyo 'thavA guruH";
$hl["L05H05"] = "";
$hlcond["L05H05"] = array( "L05Dcx0", "26.53a: suteze sutabhAvasthe zubhADhye putravAn naraH", "L05Dcx2", "26.53b: pApADhye 'patya-hIno 'sau guNavAn mitra-vatsalaH" );
$hl["L05H06"] = "L5 in H6 (26.54): suteze ripu-bhAvasthe putraH zatru-samo bhavet | mRtApatyo 'thavA jAto datta-krIta-suto 'thavA";
$hl["L05H07"] = "L5 in H7 (26.55): suteze saptame mAnI sarva-dharma-samanvitaH | putrAdi-sukha-yuktaz ca paropakaraNe rataH";
$hl["L05H08"] = "L5 in H8 (26.56): suteze randhra-bhAvasthe svalpa-putra-sukhAnvitaH | kAsa-zvAsa-samAyuktaH krodhI ca sukha-varjitaH";
$hl["L05H09"] = "L5 in H9 (26.57): suteze bhAgyage putro bhUpo vA tat-samo bhavet | svayaM vA grantha-kartA ca vikhyAtaH kula-dIpakaH";
$hl["L05H10"] = "L5 in H10 (26.58): suteze rAjyabhAva-sthe rAja-yogo hi jAyate | aneka-sukha-bhogI ca khyAta-kIrtir naro bhavet";
$hl["L05H11"] = "L5 in H11 (26.59): suteze lAbhage jAto vidyAvAn jana-vallabhaH | grantha-kartA mahA-dakSo bahu-putra-dhanAnvitaH";
$hl["L05H12"] = "L5 in H12 (26.60): suteze vyaya-bhAvasthe jAtaH putra-sukhojjhitaH | datta-putra-yuto vA 'sau krIta-putrAnvito 'thavA";

$hl["L06H01"] = "L6 in asc (26.61): SaSTheze lagnane jAto rogavAn kIrtisaMyutaH | Atma-zatrur dhanI mAnI sAhasI guNavAn naraH";
$hl["L06H02"] = "L6 in H2 (26.62): SaSTheze dhana-bhAvasthe sAhasI kula-vizrutaH | paradezI sukhI vaktA svakarma-nirataH sadA";
$hl["L06H03"] = "L6 in H3 (26.63): SaSTheze sahaje jAtaH krodhI vikrama-varjitaH | bhrAtA zatru-samas tasya bhRtyaz cottara-dAyakaH";
$hl["L06H04"] = "L6 in H4 (26.64): SaSTheze sukha-bhAvasthe mAtuH sukha-vivarjitaH | manasvI pizuno dveSI cala-citto 'ti-vittavAn";
$hl["L06H05"] = "L6 in H5 (26.65): SaSThezaH sutago yasya calaM tasya dhanAdikam | zatruto putra-mitraiz ca sukhI svArthI dayAnvitaH";
$hl["L06H06"] = "L6 in H6 (26.66): SaSTheze ripu-bhAvasthe vairaM svastrajJAti-maNDalAt | anyaiH saha bhaven maitrI sukhaM madhyaM dhanAdikam";
$hl["L06H07"] = "L6 in H7 (26.67): SaSTheze dAra-bhAvasthe jAto dAra-sukhojjitaH | kIrtimAn guNavAn mAnI sAhasI dhana-saMyutaH";
$hl["L06H08"] = "L6 in H8 (26.68): SaSTheze 'STamage jAto rogI zatrur manISiNAm | para-dravyAbhilASI ca para-dAra-rato 'zuciH";
$hl["L06H09"] = "L6 in H9 (26.69): SaSTheze bhAgyage jAtaH kASTha-pASANa-vikrayI | vyavahAre kvacid dhAniH kvacid vRddhiz ca jAyate";
$hl["L06H10"] = "L6 in H10 (26.70): SaSTheze dazane bhAve mAnavaH kula-vizrutaH | abhaktaz ca pitur vaktA videze ca sukhI bhavet";
$hl["L06H11"] = "L6 in H11 (26.71): SaSTheze lAbhage jAtaH zatruto dhanam ApnuyAt | guNavAn sAhasI mAnI kintu putra-sukhojjhitaH";
$hl["L06H12"] = "L6 in H12 (26.72): SaSTheze vyayabhAva-sthe vyasane vyayakRt sadA | vidvad-dveSI bhavej jAto jIva-hiMsAsu tat-paraH";

$hl["L07H01"] = "L7 in asc (26.73): dAreze lagneze jAtaH para-dAreSu lampaTaH | duSTo vicakSaNo 'dhIro jano vAta-rujAnvitaH";
$hl["L07H02"] = "L7 in H2 (26.74): dAreze dhanage jAto bahu-strIbhiH samanvitaH | dAra-yogAd dhanAptiz ca dIrghasUtrI ca mAnavaH";
$hl["L07H03"] = "L7 in H3 (26.75): dAreze sahaje jAto mRtApatyo hi mAnavaH | kadAciJ jAyate putrI yatnAt putro 'pi jIvati";
$hl["L07H04"] = "L7 in H4 (26.76): dAreze sukha-bhAvasthe jAyA nAsya vaze sadA | svayaM satya-paro dhImAn dharmAtmA danta-roga-yuk";
$hl["L07H05"] = "L7 in H5 (26.77): dAreze paJcame jAto mAnI sarva-guNAnvitaH | sarvadA harSa-yuktaz ca tathA sarva-dhanAdhipaH";
$hl["L07H06"] = "L7 in H6 (26.78): dAreze ripubhAvasthe bhAryA tasya rujAnvitA | striyA sahA 'tha vA vairaM svayaM krodhI sukhojjhitaH";
$hl["L07H07"] = "L7 in H7 (26.79): dAreze saptame bhAve jAto dAra-sukhAnvitaH | dhIro vicakSaNo dhImAn kevalaM vAta-rogavAn";
$hl["L07H08"] = "L7 in H8 (26.80): dAreze mRtyubhAvasthe jAto dAra-sukhojjhitaH | bhAryA 'pi roga-yuktA 'sya duHzIlA 'pi na cAnugA";
$hl["L07H09"] = "L7 in H9 (26.81): dAreze dharma-bhAvasthe nAnA-strIbhiH samAgamaH | jAyA-hRta-manA jAto bahvArambhakaro naraH";
$hl["L07H10"] = "L7 in H10 (26.82): dAreze karmabhAvasthe nAsya jAyA vazAnugA | svayaM dharma-rato jAto dhana-putrAdi-saMyutaH";
$hl["L07H11"] = "L7 in H11 (26.83): dAreze lAbhabhAvasthe dArair artha-samAgamaH | putrAdi-sukham alpaM ca janaH kanyA-prajo bhavet";
$hl["L07H12"] = "L7 in H12 (26.84): dAreze vyayage jAto daridraH kRpaNo 'pi vA | bhAryA 'pi vyaya-zIlA 'sya vastrA-jIvI naro bhavet";

$hl["L08H01"] = "L8 in asc (26.85): aSTameze tanau jAtas tanu-saukhya-vivarjitaH | devAnAM brAhmaNAnAM ca nindako vraNa-saMyutaH";
$hl["L08H02"] = "L8 in H2 (26.86): aSTameze dhane bAhu-bala-hInaH prajAyate | dhanaM tasya bhavet svalpaM naSTa-vittaM na labhyate";
$hl["L08H03"] = "L8 in H3 (26.87): randhreze sahaje bhAve bhrAtR-saukhyaM na jAyate | sAlasyo bhRtya-hInaz ca jAyate bala-varjitaH";
$hl["L08H04"] = "L8 in H4 (26.88): randhreze sukha-bhAvasthe mAtR-hIno bhavec chizuH | gRha-bhUmi-sukhair hIno mitra-drohI na saMzayaH";
$hl["L08H05"] = "L8 in H5 (26.89): randhreze suta-bhAvasthe jaDa-buddhiH prajAyate | svalpa-prajJo bhavej jAto dIrghAyuz ca dhanAnvitaH";
$hl["L08H06"] = "L8 in H6 (26.90): randhreze ripu-bhAvasthe zatru-jetA bhavej janaH | roga-yukta-zarIraz ca bAlye sarpa-jalAd bhayam";
$hl["L08H07"] = "L8 in H7 (26.91): randhreze dAra-bhAvasthe tasya bhAryA dvayaM bhavet | vyApAre ca bhaved dhAnis tasmin pApa-yute dhruvam";
$hl["L08H08"] = "L8 in H8 (26.92): randhreze mRtyu-bhAvasthe jAto dIrghAyuSA yutaH | nirbale madhyamAyuH syAc cauro nindyo 'nya-nindakaH";
$hl["L08H09"] = "L8 in H9 (26.93): aSTameze tapaH-sthAne dharma-drohI ca nAstikaH | duSTa-bhAryA patiz caiva para-dravyApahArakaH";
$hl["L08H10"] = "L8 in H10 (26.94): randhreze karma-bhAvasthe pitR-saukhya-vivarjitaH | pizunaH karma-hInaz ca";
$hlcond["L08H10"] = array( "L08Drx0", "(26.94b): yadi naiva zubhekSite" );
$hl["L08H11"] = "L8 in H11 (26.95): randhreze lAbha-bhAvasthe ... | bAlye duHkhI sukhI pazcAt";
$lhcond["L08H11"] = array( "L08Dcx2", "(26.95b): sapApe dhana-varjitaH", "L08Dcx0", "(26.95c): dIrghAyuz ca zubhAnvite" );
$hl["L08H12"] = "L8 in H12 (26.96): randhreze vyaya-bhAvasthe kukArye vyayakRt sadA | alpAyuz ca bhavej jAtaH";
$hlcond["L08H12"] = array( "L08Dcx2", "(26.96b): sapApe ca vizeSataH (alpAyuH)" );

$hl["L09H01"] = "L9 in asc (26.97): bhAgyeze lagnage jAto bhAgyavAn bhUpa-vanditaH | suzIlaz ca surUpaz ca vidyAvAn jana-pUjitaH";
$hl["L09H02"] = "L9 in H2 (26.98): bhAgyeze dhana-bhAvasthe paNDito jana-vallabhaH | jAyate dhanavAn kAmI strI-putrAdi-sukhAnvitaH";
$hl["L09H03"] = "L9 in H3 (26.99): bhAgyeze bhrAtR-bhAvasthe jAto bhrAtR-sukhAnvitaH | dhanavAn guNavAMz cApi rUpa-zIla-samanvitaH";
$hl["L09H04"] = "L9 in H4 (26.100): bhAgyeze turya-bhAvasthe gRha-yAna-sukhAnvitaH | sarva-sampatti-yuktaz ca mAtR-bhakto bhaven naraH";
$hl["L09H05"] = "L9 in H5 (26.101): bhAgyeze sutabhAva-sthe suta-bhAgya-samAnvitaH | guru-bhakti-rato dhIro dharmAtmA paNDito naraH";
$hl["L09H06"] = "L9 in H6 (26.102): bhAgyeze ripubhAvasthe svalpa-bhAgyo bhaven naraH | mAtulAdi-sukhair hInaH zatrubhiH pIDitaH sadA";
$hl["L09H07"] = "L9 in H7 (26.103): bhAgyeze dAra-bhAvasthe dAra-yogAt sukhodayaH | guNavAn kIrtimAMz cApi jAyate dvija-sattamaH";
$hl["L09H08"] = "L9 in H8 (26.104): bhAgyeze mRtyu-bhAvasthe bhAgya-hIno naro bhavet | jyeSTha-bhrAtR-sukhaM naiva tasya jAtasya jAyate";
$hl["L09H09"] = "L9 in H9 (26.105): bhAgyeze bhAgyabhAvasthe bahu-bhAgya-samanvitaH | guNa-saundarya-sampanno sahajebhyaH sukhaM bahu";
$hl["L09H10"] = "L9 in H10 (26.106): bhAgyeze karmabhAvasthe jAto rAjA 'tha tat-samaH | mantrI senApatir vA 'pi guNavAn jana-pUjitaH";
$hl["L09H11"] = "L9 in H11 (26.107): bhAgyeze lAbha-bhAvasthe dhana-lAbho dine dine | bhakto guru-janAnAM ca guNavAn puNyavAn api";
$hl["L09H12"] = "L9 in H12 (26.108): bhAgyeze vyayabhAva-sthe bhAgya-hAni-karo nRNAm | zubha-kArye vyayo nityaM nirdhano 'tithi-saGgamAt";

$hl["L10H01"] = "L10 in asc (26.109): karmeze lagnage jAto vidvAn khyAto dhanI kaviH | bAlye rogI sukhI pazcAd dhana-vRddhir dine dine";
$hl["L10H02"] = "L10 in H2 (26.110): rAjyeze dhana-bhAvasthe dhanavAn guNa-saMyutaH | rAja-mAnyo vadAnyaz ca pitrAdi-sukha-saMyutaH";
$hl["L10H03"] = "L10 in H3 (26.111): karmeze sahaje jAto bhrAtR-bhRtya-sukhAnvitaH | vikramI guNa-sampannaH vAgmI satya-rato naraH";
$hl["L10H04"] = "L10 in H4 (26.112): karmeze sukhabhAva-sthe sukhI mAtR-hite rataH | yAna-bhUmi-gRhAdhIzo guNavAn dhanavAn api";
$hl["L10H05"] = "L10 in H5 (26.113): karmeze sutabhAva-sthe sarva-vidyA-samanvitaH | sarvadA harSa-saMyukto dhanavAn putravAn api";
$hl["L10H06"] = "L10 in H6 (26.114): karmeze ripu-bhAvasthe pitR-saukhya-vivarjitaH | caturo 'pi dhanair hInaH zatrubhiH paripIDitaH";
$hl["L10H07"] = "L10 in H7 (26.115): rAjyeze dAra-bhAvasthe jAto dAra-sukhAnvitaH | manasvI guNavAn vAgmI satya-dharma-rataH sadA";
$hl["L10H08"] = "L10 in H8 (26.116): karmeze randhrabhAvasthe karma-hIno bhaven naraH | dIrghAyur apy asau jAtaH para-nindA-parAyaNaH";
$hl["L10H09"] = "L10 in H9 (26.117): rAjyeze bhAgyabhe jAto rAjA rAja-kulodbhavaH | tat-samo 'nya-kulotpanno dhana-putrAdi-saMyutaH";
$hl["L10H10"] = "L10 in H10 (26.118): karmeze rAjyabhAvasthe sarva-karma-puTaH sukhI | vikramI satya-vaktA ca guru-bhakti-rato naraH";
$hl["L10H11"] = "L10 in H11 (26.119): rAjyeze lAbha-bhAvasthe jAto dhana-sutAnvitaH | harSavAn guNavAMz cApi satya-vaktA sadA sukhI";
$hl["L10H12"] = "L10 in H12 (26.120): rAjyeze vyaya-bhAvasthe tasya rAja-gRhe vyayaH | zatruto 'pi bhayaM nityam caturaz cApi cintitaH";

$hl["L11H01"] = "L11 in asc (26.121): lAbheze lagnane jAtaH sAttviko dhanavAn sukhI | samadRSTiH kavir vAgmI sadA lAbha-samanvitaH";
$hl["L11H02"] = "L11 in H2 (26.122): lAbheze dhana-bhAvasthe jAtaH sarva-dhanAnvitaH | sarva-siddhi-yuto dAtA dhArmikaz ca sukhI sadA";
$hl["L11H03"] = "L11 in H3 (26.123): lAbheze sahaje jAtaH kuzalaH sarva-karmasu | dhanI bhrAtR-sukhopetaH zUla-roga-bhayaM kvacit";
$hl["L11H04"] = "L11 in H4 (26.124): lAbheze sukhabhAvasthe lAbho mAtR-kulAd bhavet | tIrtha-yAtrA-karo jAto gRha-bhUmi-sukhAnvitaH";
$hl["L11H05"] = "L11 in H5 (26.125): lAbheze suta-bhAvasthe bhavanti sukhinaH sutAH | vidyavanto 'pi sac-chIlAH svayaM dharma-rataH sukhI";
$hl["L11H06"] = "L11 in H6 (26.126): lAbheze roga-bhAvasthe jAto roga-samanvitaH | krUra-buddhiH pravAsI ca zatrubhiH paripIDitaH";
$hl["L11H07"] = "L11 in H7 (26.127): lAbheze dAra-bhAvasthe lAbho dAra-kulAt sadA | udAraz ca guNI kAmI jano bhAryA-vazAnugaH";
$hl["L11H08"] = "L11 in H8 (26.128): lAbheze randhra-bhAvasthe hAniH kAryezu jAyate | tasyAyuz ca bhaved dIrgha prathamaM maraNaM striyaH";
$hl["L11H09"] = "L11 in H9 (26.129): lAbheze bhAgyabhAvasthe bhAgyavAn jayate naraH | caturaH satyavAdI ca rAja-pUjyo dhanAdhipaH";
$hl["L11H10"] = "L11 in H10 (26.130): lAbheze karma-bhAvasthe bhUpa-vandyo guNAnvitaH | nija-dharma-rato dhImAn satya-vAdI jitendriyaH";
$hl["L11H11"] = "L11 in H11 (26.131): lAbheze lAbhabhAva-sthe lAbhaH sarveSu karmasu | pANDityaM ca sukhaM tasya varddhate ca dine dine";
$hl["L11H12"] = "L11 in H12 (26.132): lAbheze vyaya-bhAvasthe sat-kAryeSu vyayaH sadA | kAmuko bahu-patnIko mleccha-saMsarga-kArakaH";

$hl["L12H01"] = "L12 in asc (26.133): vyayeze lagnane jAto vyaya-zIlo jano bhavet | durbalaH kapha-rogI ca dhana-vidyA-vivarjitaH";
$hl["L12H02"] = "L12 in H2 (26.134): vyayeze dhana-bhAvasthe zubha-kArye vyayaH sadA | dhArmikaH priya-vAdI ca guNa-saukhya-samanvitaH";
$hl["L12H03"] = "L12 in H3 (26.135): vyayeze sahaje jAto bhrAtR-saukhya-vivarjitaH | bhaved anya-jana-dveSI sva-zarIrasya poSakaH";
$hl["L12H04"] = "L12 in H4 (26.136): vyayeze sukha-bhAva-sthe mAtuH sukha-vivarjitaH | bhUmi-yAna-gRhAdInAM hAnis tasya dine dine";
$hl["L12H05"] = "L12 in H5 (26.137): vyayeze suta-bhAvasthe suta-vidyA-vivarjitaH | putrArthe ca vyayas tasya tIrthATana-paro naraH";
$hl["L12H06"] = "L12 in H6 (26.138): vyayeze ripu-bhAvasthe jAtaH svajana-vaira-kRt | krodhI pApI ca duHkhI ca para-jAyA-rato naraH";
$hl["L12H07"] = "L12 in H7 (26.139): vyayeze dAra-bhAvasthe vyayo dAra-kRtaH sadA | tasya bhArya-sukhaM naiva bala-vidyA-vivarjitaH";
$hl["L12H08"] = "L12 in H8 (26.140): vyayeze mRtyubhAvasthe jAto lAbhAnvitaH sadA | priyavAG madhyamAyuz ca sampUrna-guNa-saMyutaH";
$hl["L12H09"] = "L12 in H9 (26.141): vyayeze bhAgyabhAvasthe guru-dveSI bhaven naraH | mitrair api bhaved vairaM svArtha-sAdhana-tat-paraH";
$hl["L12H10"] = "L12 in H10 (26.142): vyayeze rAjyabhAvasthe vyayo rAjya-kulAd bhavet | pitRto 'pi sukhaM tasya svalpam eva hi jAyate";
$hl["L12H11"] = "L12 in H11 (26.143): vyayeze lAbhabhAvasthe lAbhe hAniH prajAyate | pareNa rakSitaM dravyaM kadAcil labhate naraH";
$hl["L12H12"] = "L12 in H12 (26.144): vyayeze vyaya-bhAvasthe vyayAdhikyaM hi jAyate | na zarIra-sukhaM tasya krodhI dveSa-paro nRNAm";

// php8 will throw an exception calling sizeof($errlog)
$errlog = array();

// Evaluate data and return array of text lines which can be wrapped in <p>...</p>
function EvaluateParashara( $data, $with_dump = 0 )
{

  global $allcaps;
  global $errlog;
  global $hl;
  global $hlcond;
  global $rules;
  global $p;
  global $lords;
  global $out;
  global $chainseps;

$s = strtok( $data, " \t\r\n" );
while ($s)
{
  $a = explode( ":", $s );
  if (sizeof($a)==2)
  {
	$p[$a[0]] = $a[1];
  }
  $s = strtok( " \t\r\n" );
}

  if (sizeof($p) < 7 || strlen($data) < 128)
  {
	return array( sprintf( "Error: no data specified (length=%d) or insufficient elements parsed (%d)", strlen($data), sizeof($p) ) );
  }

// $p is indexed by "su-01", "mo-01", etc.
// Construct secondary indices, e.g. $lords["L03"] = "su-01";
// Also add derived values such as Dcx0, Dcx2, Drx0, Drx2, etc.
for ($h = 1; $h <= 12; $h++)
{
	$hk = sprintf( "H%02d", $h );
	$houses[$hk] = array();
	$house_mal[$hk] = 0;
	$house_ben[$hk] = 0;
}
foreach ($p as $key => $value)
{
  // For now, just handle rashi lords. Derived values for Navamsa would also be useful though...
  if (strpos( $key, "-01" ) === FALSE) continue;
  $a = GetValues($value, "L");
  if (sizeof($a)>0)
  {
	for ($n = 0; $n < sizeof($a); $n++)
	{
		$lords[$a[$n]] = $key;
	}
  }
  // Add derived values
  $a = GetValues($value, "C");
  for ($n = 0; $n < sizeof($a); $n++)
  {
	// Skip "C"
	$ckey = substr($a[$n],1) . "-01";
	if (!array_key_exists( $ckey, $p ))
	{
		$errlog[] = sprintf( "Did not find %s for a[%d] in p", $ckey, $n );
		continue;
	}
	$mal = GetValue( $p[$ckey], "X" );
	#$errlog .= sprintf( "Got mal %s from p[%s]<br/>", $mal, $ckey );
	if ($mal)
	{
		$p[$key] .= sprintf( "Dc%s", strtolower($mal) );
	}
  }
  $a = GetValues( $value, "R" );
  for ($n = 0; $n < sizeof($a); $n++)
  {
	// Skip "R" and get planet key
	$rkey = substr( $a[$n], 1, 2 ) . "-01";
	if (!array_key_exists( $rkey, $p ))
	{
		$errlog[] = sprintf( "Did not find %s for a[%d] in p", $rkey, $n );
		continue;
	}
	$mal = GetValue( $p[$rkey], "X" );
	if ($mal)
	{
		$p[$key] .= sprintf( "Dr%s", strtolower($mal) );
	}
  }
  if ($key != "as-01")
  {
	$h = GetValue( $value, "H" );
	$houses[$h][] = $key;
	if (HasValue( $value, "X2" )) $house_mal[$h]++;
	else if (HasValue( $value, "X0" )) $house_ben[$h]++;
  }
}

if (sizeof($lords) < 12)
{
	$errlog[] = sprintf( "Error: only got %d lords from rasi, need 12", sizeof($lords) );
	if ($with_dump)
	{
		$errlog[] = "Data dump follows:";
		$errlog[] = $data;
		foreach ($p as $key => $value) $errlog[] = sprintf( "%s %s", $value, $p[$value] );
	}
	return $errlog;
}

// Now we have something like
// $p["as-01"] = "P03S02N07Q1H01"
// $p["ma-01"] = "P06S02N07Q2H01Asu[2]05Ame[2]05Aju[4]07Asa[4]07L06L11"
// Lordship rules (Ch. 26) are simple
// Rules in Ch. 14-25 require derived values
// Chapter 14 begins from pg. 194 Volume I of Pt. Girish Chand Sharma's edition
foreach ($p as $key => $value)
{
  // For now, just handle rashi lords. Derived values for Navamsa would also be useful though...
  if (strpos( $key, "-01" ) === FALSE) continue;
  $a = GetValues($value, "L");
  $h = GetValue($value, "H");
  if ($h===FALSE) continue;
  if (sizeof($a) < 1) continue;
  $display_prefix = "[" . substr($key,0,2) . "] ";
  // If a planet rules two houses, the grouping is important since they may nullify each other
  if (sizeof($a) > 1)
  {
	$out[] = sprintf( "%s rules both house %d and house %d:", substr($key,0,2), substr($a[0],1), substr($a[1],1) );
  }
  for ($n = 0; $n < sizeof($a); $n++)
  {
	$search = $a[$n] . $h;
	if (!array_key_exists( $search, $hl ))
	{
		$errlog[] = sprintf( "ERROR: could not find [%s] in hl(length=%d)",
			$search, sizeof($hl) );
		continue;
	}
	// We can have a main condition text and additional optional
	if ($hl[$search]) $out[] = sprintf( "%shl[%s] = %s", $display_prefix, $search, $hl[$search] );
	if (array_key_exists( $search, $hlcond ))
	{
		// For now, simple pairs of "condition", "text"
		// Condition can have & | and ! with ! given a higher precedence than & (and) and | (or)
		$pass = FALSE;
		for ($i = 0; !$pass && $i < sizeof($hlcond[$search]) - 1; $i += 2)
		{
			// Split into components such as ! ( ) & |
			// Precedence is ( ) followed by ! followed by & followed by |
			$chain = SplitRuleChain( $hlcond[$search][$i] );
			$expr_depth = 0; // Paren depth
			$expr_flags[$expr_depth] = 0x08; // 0x01 = NOT, 0x02 = AND, 0x04 = OR, 0x08 = assign
			$expr_result[$expr_depth] = FALSE; // Depth 0 result is the outcome
			for ($chainIndex = 0; $chainIndex < sizeof($chain); $chainIndex++)
			{
				$pass = TRUE;
				// Handling rule combiner or expression?
				if (strpos( $chainseps, $chain[$chainIndex] ) !== FALSE)
				{
					$completion = FALSE;
					if ($chain[$chainIndex] == "!")
					{
						$expr_flags[$expr_depth] &= 0xf7; // Turn off assignment
						$expr_flags[$expr_depth] |= 0x01; // NOT result of next completion
					}
					else if ($chain[$chainIndex] == "(")
					{
						$expr_depth++;
						$expr_result[$expr_depth] = FALSE;
						$expr_flags[$expr_depth] = 0x08;
					}
					else if ($chain[$chainIndex] == ")")
					{
						$pass = $expr_result[$expr_depth];
						$expr_depth--;
						if ($expr_depth < 0)
						{
							$errlog[] = sprintf( "ERROR: mismatched paren at element %d in chain expression for %s", $chainIndex, $search );
							$expr_depth = 0;
						}
						// We have a completed evaluation with result in $pass
						$completion = TRUE;
					}
					else if ($chain[$chainIndex] == "&")
					{
						$expr_flags[$expr_depth] |= 0x02;
						if ($expr_flags[$expr_depth] & 0x04) $errlog[] = sprintf( "ERROR: AND and OR combined at element %d for %s", $chainIndex, $search );
					}
					else if ($chain[$chainIndex] == "|")
					{
						$expr_flags[$expr_depth] |= 0x04;
						if ($expr_flags[$expr_depth] & 0x02) $errlog[] = sprintf( "ERROR: OR and AND combined at element %d for %s", $chainIndex, $search );
					}
					else
					{
						$errlog[] = sprintf( "ERROR: unexpected combiner %s at element %d for %s", $chain[$chainIndex], $chainIndex, $search );
					}
				}
				else
				{
					$completion = TRUE;
					// else we have an expression
					$ac = SplitCombination( $chain[$chainIndex] );
					// Check for all specified attributes present
					$pass = TRUE;
					for ($j = 0; $pass && $j < sizeof($ac); $j++)
					{
						$pass = HasValue( $hlcond[$search][$i], $ac[$j] );
					}
				}
				// Apply results only if we had an expression or ) (expression depth return)
				if (!$completion) continue;
				// Apply result according to flags
				if ($expr_flags[$expr_depth] & 0x01)
				{
					$pass = !$pass;
					$expr_flags[$expr_depth] &= 0xfe;
				}
				// Assign, AND and OR should be mutually exclusive
				if ($expr_flags[$expr_depth] & 0x08)
				{
                                        $expr_result[$expr_depth] = $pass;
					$expr_flags[$expr_depth] &= 0xf7;
				}
				if ($expr_flags[$expr_depth] & 0x04)
				{
					$expr_result[$expr_depth] = ($expr_result[$expr_depth] || $pass);
					$expr_flags[$expr_depth] &= 0xfb;
				}
				if ($expr_flags[$expr_depth] & 0x02)
				{
					$expr_result[$expr_depth] = ($expr_result[$expr_depth] && $pass);
					$expr_flags[$expr_depth] &= 0xfd;
				}
			}
			if ($expr_result[0])
			{
				$extra = "";
				if ($with_dump) $extra = sprintf( " (%s)", $hlcond[$search][$i] );
				$out[] = sprintf( "%shl[%s][%d] = %s%s", $display_prefix, $search, $i, $hlcond[$search][$i+1], $extra );
			}
		}
		if (!$pass && $hl[$search] == "") $out[] = sprintf( "WARNING: no matches in condition chain length %d for %s", sizeof($hlcond[$search]), $search );
	}
  }
}

// Process rules as described in declaration
// This is the actual processing of dynamic rules
$wastrue = FALSE; // A previous if / ifelse in the current chain of 'if ...' was true
$waselse = FALSE; // Previous statement was a closing 'else'
for ($ri = 0; $ri < sizeof($rules); $ri++)
{
	$a = explode( "\t", $rules[$ri] );
	if (sizeof($a)<3)
	{
		$errlog[] = sprintf( "Error: rule[%d] yielded %d components, expected 3: %s", $ri, sizeof($a), $rules[$ri] );
		continue;
	}
	$conditionop = strtok( $a[0], " \t" );
	$pointid = strtok( " \t" );
	$pass = FALSE;
	if ($conditionop == "else")
	{
		if ($waselse) $errlog[] = sprintf( "Error: rule[%d] is a duplicate else: %s", $ri, $rules[$ri] );
		$waselse = TRUE;
		$pass = !$wastrue;
		$wastrue = FALSE;
	}
	else if ($conditionop != "if" && $conditionop != "elseif")
	{
		$waselse = FALSE;
		$wastrue = FALSE;
		$errlog[] = sprintf( "Error: rule[%d] is an unknown conditionop: %s", $ri, $rules[$ri] );
	}
	else
	{
		$waselse = FALSE;
		// Skip elseif evaluation if already true
		if ($wastrue && $conditionop == "elseif")
		{
			$pass = FALSE;
		}
		else
		{
			// Determine type of pointid
			if (substr( $pointid, 0, 1 ) == "L")
			{
				$key = $lords[$pointid];
			}
			else
			{
				$key = $pointid;
			}
			$value = $p[$key];
			if ($value == "")
			{
				$errlog[] = sprintf( "Error: rule[%d] references undefined pointid %s: %s", $ri, $pointid, $rules[$ri] );
				$pass = FALSE;
			}
			else
			{
				$pass = eval( "return " . $a[1] . ";" );
			}
			$wastrue = $pass;
		}
	}
	if ($pass)
	{
		$out[] = $a[2];
	}
}

// Older deprecated rule processing. These could all be handled by the newer $rules[]

// Run through a sample rule chain for 14.1 (pg. 194)
// BNF for expression evaluation (+ shorthand for "1 or more"):
// rulechain :: id ruleset
// ruleset :: rule-exp +
// rule-exp :: conditional-action | action-block
// conditional-action :: condition-test action-block
// condition-test :: 'if (' condition-expr ') '
// condition-expr :: expr
$asclord = $lords["L01"];
$asclordhouse = substr(GetValue($p[$asclord], "H" ),1);
//printf( "<p>asclord = %s, house = %s, code = %s</p>\n", $asclord, $asclordhouse, $p[$asclord] );

if (HasValue( $p[$asclord], "Dcx2" ) ||
	TrikHouse($asclordhouse))
{
	$out[] = sprintf( "14.1a: sapApo dehapoSThAri-vyayago deha-saukhya-hRt" );
}
else if (KendraHouse($asclordhouse) || TrineHouse($asclordhouse))
{
	$out[] = sprintf( "14.1b: kendra-koNe sthite 'GgezaH sadA deha-sukhaM dizet" );
}

// 15.1
$secondlord = $lords["L02"];
$house = GetValue($p[$secondlord], "H");
$housen = substr($house,1);
if ($housen == "02" || KendraHouse( $housen ) || TrineHouse( $housen ))
{
	$out[] = sprintf( "15.1a: dhanezo dhanabhAva-sthaH kendra-koNa-gato 'pi vA || dhana-vRddhi-karo jJeyaH" );
}
else if (TrikHouse( $housen ))
{
	$out[] = sprintf( "15.1b: trikastho dhana-hAni-kRt" );
}
if ($house_ben["H02"]>0)
{
	$out[] = sprintf( "15.1c: dhanadaz ca dhane saumyaH (%d)", $house_ben["H02"] );
}
if ($house_mal["H02"]>0)
{
	$out[] = sprintf( "15.1d: pApo dhana-vinAza-kRt (%d)", $house_mal["H02"] );
}

$eleventhlord = $lords["L11"];
$house2n = substr(GetValue($p[$eleventhlord], "H"),1);
$l2l11dist = ForwardDistance( $housen, $house2n );
$ju_exalt = GetValue($p["ju-01"], "E");
$ve_exalt = GetValue($p["ve-01"], "E");
if ($ju_exalt) $ju_exalt = substr($ju_exalt,1); else $ju_exalt = 0;
if ($ve_exalt) $ve_exalt = substr($ve_exalt,1); else $ve_exalt = 0;
$verse = "dhaneze kendra-rAzisthe lAbheze tat-trikoNage | guru-zukra-yute dRSTe dhana-lAbham udIrayet";
if (KendraHouse( $housen ) && ($l2l11dist == 5 || $l2l11dist == 9))
{
	$out[] = sprintf( "15.3a: %s", $verse );
}
else if ((HasValue( $p["ju-01"], "Cve" ) || HasValue( $p["ju-01"], "Rve" ))
	&& $ju_exalt >= 0 && $ve_exalt >= 0)
{
        $out[] = sprintf( "15.3b (ju/ve %d/%d): %s", $ju_exalt, $ve_exalt, $verse );
}

// Rules from Chapter 12

// Sort by H value (house) plus Z value (longitude)
foreach ($p as $key => $value) $skey[GetValue($value, "H") . GetValue($value, "Z") . "-{$key}"] = $key;
ksort($skey);

if ($with_dump)
{
	//printf( "<div style=\"font-size:8pt;\">\n" );
	//printf( "<p>%d: %s</p>\n", $index, $pdname[$index] );
	foreach ($skey as $key => $value) $out[] = sprintf( "%s %s", $value, $p[$value] );
	foreach ($lords as $key => $value) $out[] = sprintf( "%s %s", $key, $value );
	//printf( "</div>\n" );
}

if (sizeof($errlog)>0)
{
	$out[] = sprintf( "%d errors / diagnostics:", sizeof($errlog) );
	return array_merge( $out, $errlog );
}
else
{
    return $out;
}

} // EvaluateParashara()

// Functions

// Get a single value when we assume there is exactly zero or 1
// (or don't care about subsequent duplicates)
function GetValue($value, $key)
{
  global $allcaps;
  global $errlog;
  $s = strstr( $value, $key );
  if ($s === FALSE) return $s;
  // Retain initial capital plus following characters only
  // until (but not including) next capital letter
  $len = 1 + strcspn( substr( $s, 1 ), $allcaps );
  return substr( $s, 0, $len ) /* . sprintf( "(key=%s,len=%d,rem=%s,allcaps=%s)", $key, $len, substr($s,1), $allcaps ) */;
}

// Get multiple values as an array when there are zero, 1 or more
// instances
function GetValues($value, $key)
{
  global $allcaps;
  global $errlog;
  // Declare empty array
  $a = array();
  while ($s = strstr( $value, $key ))
  {
	// Length including initial cap plus following characters
	$len = 1 + strcspn( substr( $s, 1 ), $allcaps );
	$a[] = substr( $s, 0, $len );
	// Skip to rest of string
	$value = substr( $s, $len );
  }
  return $a;
}

// Split a condition set combined with | & ( ) and ! into separate expressions
function SplitRuleChain($s)
{
  global $chainseps;
  global $errlog;
  // Declare empty array
  $a = array();
  while (strlen($s) > 0)
  {
	// Currently we split only one separator at a time, e.g. !& will become two elements as will !(exp1|exp2)
	$seplength = strspn( $s, $chainseps );
	if ($seplength > 0)
	{
		$seplength = 1;
		$a[] = substr( $s, 0, $seplength );
		$s = substr( $s, $seplength );
		continue;
	}
	$explength = strcspn( $s, $chainseps );
	if ($explength == 0 && strlen($s) > 0)
	{
		$errlog[] = sprintf( "Error: splitrulechain(%s) has unexpected remnant", $s );
		$a[] = $s;
		$s = "";
	}
	else
	{
		$a[] = substr( $s, 0, $explength );
		$s = substr( $s, $explength );
	}
  }
  return $a;
}

// Split a combination e.g. "L01X2" into components "L01", "X2"
function SplitCombination($value)
{
  global $allcaps;
  global $errlog;
  // Declare empty array
  $a = array();
  while ($caplength = strspn( $value, $allcaps ))
  {
	$vlength = strcspn( substr( $value, $caplength ), $allcaps );
	$a[] = substr( $value, 0, $caplength + $vlength );
	$value = substr( $value, $caplength + $vlength );
  }
  return $a;
}

// Check for a specific key combination present, e.g. "Dcx2"
function HasValue($value, $keycombination)
{
  global $errlog;
  if (strstr( $value, $keycombination ))
  {
	// Use of caps will not require checking for overlap
	return TRUE;
  }
  return FALSE;
}

function TrikHouse( $h )
{
	return ($h=="06" || $h=="08" || $h=="12");
}

function TrineHouse( $h )
{
	return ((($h-1) % 4) == 0);
}

function KendraHouse( $h )
{
	return ((($h-1) % 3) == 0);
}

function InTrikHouse( $value )
{
	return TrikHouse( substr( GetValue( $value, "H" ), 1 ) );
}

function InTrineHouse( $value )
{
	return TrineHouse( substr( GetValue( $value, "H" ), 1 ) );
}

function InKendraHouse( $value )
{
	return KendraHouse( substr( GetValue( $value, "H" ), 1 ) );
}

function LogOutput( $msg )
{
	global $out;
	$out[] = $msg;
	return TRUE;
}

function LogError( $msg )
{
	global $errlog;
	$errlog[] = $msg;
	return TRUE;
}

// $value2 is in trine from $value1
function InTrineFrom( $value1, $value2 )
{
	$hn1 = substr( GetValue( $value1, "H" ), 1 );
	$hn2 = substr( GetValue( $value2, "H" ), 1 );
	$dist = ForwardDistance( $hn1, $hn2 );
	return ($dist == 5 || $dist == 9);
}

// $value2 is in kendra from $value1
function InKendraFrom( $value1, $value2 )
{
	$hn1 = substr( GetValue( $value1, "H" ), 1 );
	$hn2 = substr( GetValue( $value2, "H" ), 1 );
	$dist = ForwardDistance( $hn1, $hn2 );
	return ($dist == 4 || $dist == 7 || $dist == 10);
}

// Planet is exalted, highly exalted or mtk
function IsExalted( $value )
{
	return (GetExaltation( $value ) >= 1);
}

// Planet is highly exalted
function IsHighlyExalted( $value )
{
	return (GetExaltation( $value ) >= 2);
}

// Planet is debilitated
function IsDebilitated( $value )
{
	return (GetExaltation( $value ) < 0);
}

// Get exaltation as a numeric value, 0 if not exalted or debilitated, negative if debilitated
function GetExaltation( $value )
{
	$s = GetValue( $value, "E" );
	if (strlen($s) < 2) return 0;
	return substr( $s, 1 );
}

// In own sign
function InOwnSign( $value )
{
	return HasValue( $value, "O3" );
}

// Origin:1 forward distance
function ForwardDistance( $h1, $h2 )
{
	// Difference is always origin:0
	$dist = $h2 - $h1;
	if ($dist < 0) $dist += 12;
	// Convert back to origin:1
	return $dist + 1;
}

// Transliterate Harvard-Kyoto to Unicode, optionally with devanagari
function Translit( $line, $with_translit, $with_nagari )
{
	// Optional header with colon, optional suffix in parens
	$s = $line;
	$prefix = "";
	$suffix = "";
	$prepos = strpos( $line, ":" );
	$sufpos = strrpos( $line, "(" );
	if ($prepos !== FALSE)
	{
		$prefix = substr( $line, 0, $prepos + 1 );
		$s = substr( $line, $prepos + 1 );
	}
	else $prepos = 0;
	if ($sufpos !== FALSE && $sufpos > $prepos)
	{
		$suffix = substr( $line, $sufpos );
		$s = substr( $s, 0, strrpos($s, "(") );
	}
	if ($s != "")
	{
		$hk = array("A","I","U","R","M","H","G","J","T","D","N","z","S");
		$uni = array("ā", "ī", "ū", "ṛ", "ṁ", "ḥ", "ṅ", "ñ", "ṭ", "ḍ", "ṇ", "ś", "ṣ");
		$s = str_replace( $hk, $uni, $s );
		$s = "<span style='font-size:8pt;'>" . $s . "</span>";	
	}
	return $prefix . $s . $suffix;
}

// Closing tag left open

