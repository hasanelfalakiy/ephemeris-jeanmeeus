package com.andihasan7.lib.ephemeris.jeanmeeus

import com.andihasan7.lib.ephemeris.jeanmeeus.util.*

fun main() {

    val bujur = toDecimalCheck(112, 13, 0, true)
    val lintang = toDecimalCheck(7, 47, 0, false)
    val jam = 0.0
    val jm = EphemerisMeeus(
        5, 2, 2023, lintang, bujur,0.0, jam
    )


    // pemanggilan
    val test = jm.EoT
    val hari = numberAhad(jm.hari_ke)
    val pukul = toTimeFullRound2(jam)
    val lintangTempat = toDegreeFullRound2(-7.783333333)
    val bujurTempat = toDegreeFullRound2(112.2166667)

    // posisi bulan
    val posisiBulan = toDegreeFullRound2(jm.bujurB_nampak)
    val lintangB = toDegreeFullRound2(jm.lintangB)
    val jarakBB = toDegreeFullRound2(jm.jarakBB)
    val alphaBulanPukul = toDegreeFullRound2(jm.alphaBulanPukul)
    val alphaBulan = toDegreeFullRound2(jm.alphaBulan)
    val deltaBulan = toDegreeFullRound2(jm.deltaBulan)
    val azimuthBulan = toDegreeFullRound2(jm.azimuthBulan)
    val altitudeB = toDegreeFullRound2(jm.altitudeB)
    val sudutParalaksB = toDegreeFullRound2(jm.sudutParalaksB)
    val sudutJariB = toDegreeFullRound2(jm.sudutJariB)
    val iluminasiB = jm.iluminasiB

    val bujurM_nampak = toDegreeFullRound2(jm.bujurM_nampak)
    val lintangM = jm.lintangM
    val jarakBm_M = jm.jarakBm_M
    val alphaM_pukul = toDegreeFullRound2(jm.alphaM_pukul)
    val alphaMatahari = toDegreeFullRound2(jm.alphaMatahari)
    val deltaMatahari = toDegreeFullRound2(jm.deltaMatahari)
    val azimuthMatahari = toDegreeFullRound2(jm.azimuthMatahari)
    val altitudeM = toDegreeFullRound2(jm.altitudeM)
    val sudutParalaksM = toDegreeFullRound2(jm.sudutParalaksM)
    val sudutJariM = toDegreeFullRound2(jm.sudutJariM)
    val epsilon = toDegreeFullRound2(jm.epsilon)
    val BbdikurangiBm = toDegreeFullRound2(jm.BbdikurangiBm)
    val sudutElongasiBdanM = toDegreeFullRound2(jm.sudutElongasiBdanM)
    val sudutFase_d = toDegreeFullRound2(jm.sudutFase_d)
    val delatTJD = jm.delta_T * 86400

    println(test)
    println(" ")
    println("\n\nHari             = $hari")
    println("Pukul            = $pukul")
    println("Bujur Geografis  = $bujurTempat")
    println("lintang Geografis= $lintangTempat")
    println("Posisi Bulan ")
    println("Bujur Ekliptika Bulan (Lambda)         = $posisiBulan")
    println("Lintang Ekliptika Bulan (Beta)         = $lintangB")
    println("Jarak bumi-bulan (Km)                  = $jarakBB")
    println("Right Ascension Bulan (Alpha)          = $alphaBulanPukul")
    println("Right Ascension Bulan                  = $alphaBulan")
    println("Deklinasi Bulan (Delta)                = $deltaBulan")
    println("Azimuth Bulan dilihat dari lokasi      = $azimuthBulan")
    println("True Altitude Bulan dilihat dari lokasi= $altitudeB")
    println("Sudut paralaks Bulan                   = $sudutParalaksB")
    println("Sudut jari-jari bulan                  = $sudutJariB")
    println("Iluminasi bulan                        = $iluminasiB")

    println("\nPosisi Matahari ");
    println("Bujur Ekliptika Matahari (Lambda)      = $bujurM_nampak")
    println("Lintang Ekliptika Matahari (Beta)      = $lintangM")
    println("Jarak bumi-Matahari (Km)               = $jarakBm_M")
    println("Right Ascension Matahari (Alpha)       = $alphaM_pukul")
    println("Right Ascension Matahari (Alpha)       = $alphaMatahari")
    println("Deklinasi Matahari (Delta)             = $deltaMatahari")
    println("Azimuth Matahari dilihat dari lokasi   = $azimuthMatahari")
    println("True Altitude M dilihat dari lokasi    = $altitudeM")
    println("Sudut paralaks Matahari                = $sudutParalaksM")
    println("Sudut jari-jari Matahari               = $sudutJariM")

    println("\nKemiringan Bumi                        = $epsilon")
    println("bujur E bulan - bujut E matahari       = $BbdikurangiBm")
    println("Sudut elongasi bulan-matahari          = $sudutElongasiBdanM")
    println("Sudut Fase                             = $sudutFase_d")
    println("Delta T                                = $delatTJD")


    val JD_UT = jm.JD_UT
    val T_UT = jm.T_UT
    val delta_T = jm.delta_T
    val jde = jm.jde
    val T_TD = jm.T_TD
    val tau = jm.tau
    val gstpukul = jm.gstpukul
    val gstnampak = jm.gstnampak
    val lstnampak = jm.lstNampak
    val L1 =jm.L1
    val koreksibujurB = jm.koreksibujurB
    val bujurB = jm.bujurB
    val deltaPsi = jm.deltaPsi
    val bujurB_nampak = jm.bujurB_nampak
    val d = jm.d
    val m = jm.m
    val ma = jm.ma
    val f = jm.f
    val e = jm.e
    val hourAngleBulan = jm.hourAngleBulan
    val azimuthBulanS = jm.azimuthBulanS
    val sudutFai = jm.sudutFai
    val sudutFase = jm.sudutFase

    println("\n\t\t\t\tDetail Perhitungan");
    println("Lintang geografis        = $lintang")
    println("Bujur geografis          = $bujur")
    println("julian day UT            = $JD_UT")
    println("T  (UT)                  = $T_UT")
    println("delta_T                  = $delta_T")
    println("julian day E (JDE) TD    = $jde")
    println("T  (TD)                  = $T_TD")
    println("tau                      = $tau")
    println("gstpukul                 = $gstpukul")
    println("gstnampak                = $gstnampak")
    println("lstnampak                = $lstnampak")
    println("epsilon                  = $epsilon")
    println("\n\t\t\t\tDetail Perhitungan Bulan")
    println("Bujur rata2 bulan (L')   = $L1")
    println("Koreksi bujur bulan      = $koreksibujurB")
    println("Bujur bulan              = $bujurB")
    println("Koreksi DeltaPsi (Nutasi)= $deltaPsi")
    println("Bujur bulan nampak       = $bujurB_nampak")
    println("Elongasi rata2 Bulan     = $d")
    println("Anomali rata2 Matahari   = $m")
    println("Anomali rata2 bulan      = $ma")
    println("Argumen bujur bulan      = $f")
    println("Eksentrisitas orbit      = $e")
    println("lintang bulan            = $lintangB")
    println("jarak bumi-bulan         = $jarakBB")
    println("sudutParalaks            = $sudutParalaksB")
    println("sudut jari2 Bulan        = $sudutJariB")
    println("Alpha Bulan              = $alphaBulan")
    println("Delta Bulan              = $deltaBulan")
    println("Hour Angle Bulan         = $hourAngleBulan")
    println("Azimuth Bulan S          = $azimuthBulanS")
    println("Azimuth Bulan            = $azimuthBulan")
    println("Altitude Bulan           = $altitudeB")
    println("Sudut Fai                = $sudutFai")
    println("Sudut Fase               = $sudutFase")
    println("iluminasi Bulan          = $iluminasiB")

    val L = jm.L
    val theta = jm.theta
    val theta_terkoreksi = jm.theta_terkoreksi
    val koreksiAberasi = jm.koreksiAberasi
    val jarakBumi_Matahari = jm.jarakBumi_Matahari
    val hourAngleM = jm.hourAngleM
    val azimuthM_selatan = jm.azimuthM_selatan
    val elongasiBdanM = jm.elongasiBdanM
    val EoT = toTimeFullRound2(jm.EoT)

    println("\n\t\t\t\tDetail Perhitungan Matahari")
    println("tau                      = $tau")
    println("L                        = $L")
    println("Theta                    = $theta")
    println("Theta terkoreksi         = $theta_terkoreksi")
    println("DeltaPsi (Nutasi)        = $deltaPsi")
    println("koreksiAberasi           = $koreksiAberasi")
    println("bujurM_nampak            = $bujurM_nampak")
    println("Lintang M nampak (Beta)  = $lintangM")
    println("Jarak Bumi Matahari (AU) = $jarakBumi_Matahari")
    println("Sudut jari2 Matahari     = $sudutJariM")
    println("Alpha Matahari           = $alphaMatahari")
    println("Delta Matahari           = $deltaMatahari")
    println("Hour Angle Matahari      = $hourAngleM")
    println("Azimuth Matahari S       = $azimuthM_selatan")
    println("Azimuth Matahari         = $azimuthMatahari")
    println("Altitude Matahari        = $altitudeM")
    println("Sudut paralaks Matahari  = $sudutParalaksM")
    println("bujur Ek B - bujur Ek M  = $BbdikurangiBm")
    println("COS(Elongasi B M)        = $elongasiBdanM")
    println("Sudut elongasi B-M       = $sudutElongasiBdanM")
    println("Perata waktu             = $EoT")

}