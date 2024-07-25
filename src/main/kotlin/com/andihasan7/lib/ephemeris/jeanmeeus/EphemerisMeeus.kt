package com.andihasan7.lib.ephemeris.jeanmeeus

class EphemerisMeeus(
    date: Int, // tanggal masehi
    month: Int, // bulan masehi
    year: Int, // tahun masehi
    latitude: Double = 0.0, // lintang tempat
    longitude: Double = 0.0, // bujur tempat
    timeZone: Double = 0.0, // zona waktu
    time: Double = 0.0 // jam/waktu
) {

    // aslinya : (jam * 3600 + menit * 60 + detik) / 86400
    val pukul_keJD = (time * 3600) / 86400
    public val jam = time
    val lintang_r = Math.toRadians(latitude)
    /**
     * jam local ke jam UT
     */
    val jam_UTInt = (time - timeZone).toInt()

    // hitung nilai julian day
    /**
     * bulan masehi terkoreksi
     */
    val bulan = if (month <= 2) {
        month + 12
    } else {
        month
    }
    /**
     * tahun masehi terkoreksi
     */
    val tahun = if (month <= 2) {
        year - 1
    } else {
        year
    }

    // bila gergorian
    val a = if (tahun == 1582 && bulan >= 10 && date > 4 || tahun > 1582 || tahun == 1582 && bulan > 10) {
        tahun / 100
    } else {
        tahun
    }
    val b = if (tahun == 1582 && bulan >= 10 && date > 4 || tahun > 1582 || tahun == 1582 && bulan > 10) {
        2 + a / 4 - a
    } else {
        0
    }

    // rumus jika dms manual : (jam + menit / 60 + detik / 3600) / 24
    val JD =
        1720994.5 + (365.25 * tahun).toInt() + (30.60001 * (bulan + 1)).toInt() + b + date + (time) / 24 - timeZone / 24
    val JD_UT =
        1720994.5 + (365.25 * tahun).toInt() + (30.60001 * (bulan + 1)).toInt() + b + date + (time) / 24 - timeZone / 24

    // nama hari
    val hari_ke = ((JD + 1.5) % 7 + 1).toInt()

    /**
     * delta T
     */
    val delta_T = 0.0

    /**
     * JDE waktu TD(Dynamical time)
     */
    val jde = JD_UT + delta_T
    val T_UT = (JD_UT - 2451545) / 36525
    val T_TD = (jde - 2451545) / 36525
    val tau = T_TD / 10

    // Greenwich sideral time
    val _gst0 = 6.6973745583 + 2400.0513369072 * T_TD + 0.0000258622 * T_TD * T_TD
    val gst0 = if (_gst0 < 0) {
        _gst0 + 24.0
    } else {
        _gst0 - 24.0
    }
    val gstUT = (gst0 + 1.0027379035 * jam_UTInt).mod(24.0)
    val _gstLokal = ((gst0 + (time - timeZone) * 1.00273790935) % 24) / 15

    //lokal sideral time
    val _LST = if (longitude > 0) {
        _gstLokal + longitude / 15
    } else {
        _gstLokal - longitude / 15
    }

    val LST = (_LST).mod(24.0)

    val gstpukul = ((280.46061837 + 360.98564736629 * (JD_UT - 2451545) + 0.000387933 * T_UT * T_UT - T_UT * T_UT * T_UT / 38710000) % 360) / 15
    val gstLokal = if (gstpukul < 0) {
        _gstLokal + 360.0
    } else {
        _gstLokal
    }

    val deltaPsi = Nutasi.deltaPsiDanEpsilon(T_TD)[2]
    val epsilon = Nutasi.deltaPsiDanEpsilon(T_TD)[6]
    val epsilon_r = Math.toRadians(epsilon)
    val gstnampak = gstpukul + deltaPsi * Math.cos(epsilon_r) / 15
    var _lstnampak = (gstnampak + longitude / 15) % 24
    val lstNampak = if (_lstnampak < 0) {
        _lstnampak + 24.0
    } else {
        _lstnampak
    }

    //Bulan
    //l1= bujur rata-rata bulan
    val L1 =
        (218.3164591 + 481267.88134236 * T_TD - 0.0013268 * T_TD * T_TD + T_TD * T_TD * T_TD / 538841 - T_TD * T_TD * T_TD * T_TD / 65194000) % 360

    //elongasi rata2 bulan
    val d: Double = TabelBulan.sukuPeriodik(T_TD, L1).get(1)

    //Anomali rata2 Matahari
    val m: Double = TabelBulan.sukuPeriodik(T_TD, L1).get(2)

    //Anomali rata2 bulan
    val ma: Double = TabelBulan.sukuPeriodik(T_TD, L1).get(3)

    //Argumen bujur bulan
    val f: Double = TabelBulan.sukuPeriodik(T_TD, L1).get(4)

    //Eksentrisitas orbit
    val e: Double = TabelBulan.sukuPeriodik(T_TD, L1).get(5)

    //Koreksi bujur bulan
    val koreksibujurB: Double = TabelBulan.sukuPeriodik(T_TD, L1).get(6)
    val bujurB = (L1 + koreksibujurB) % 360
    val bujurB_nampak = ((bujurB + deltaPsi) % 360).mod(360.0)
    val bujurB_nampak_r = Math.toRadians(bujurB_nampak)

    //Koreksi lintang bulan
    val lintangB: Double = TabelBulan.sukuPeriodik(T_TD, L1).get(7)
    val lintangB_r = Math.toRadians(lintangB)

    //Koreksi jarak bumi-bulan
    val jarakBB: Double = 385000.56 + TabelBulan.sukuPeriodik(T_TD, L1).get(8)
    val sudutParalaksB = Math.toDegrees(Math.asin(6378.14 / jarakBB))
    val sudutJariB = 358473400 / (jarakBB * 3600)
    var alphaBulan = (Math.toDegrees(
        Math.atan2(
            Math.sin(bujurB_nampak_r) * Math.cos(epsilon_r) - Math.tan(lintangB_r) * Math.sin(epsilon_r),
            Math.cos(bujurB_nampak_r)
        )
    )).mod(360.0) % 360

    val alphaBulanPukul = alphaBulan / 15
    val deltaBulan = Math.toDegrees(
        Math.asin(
            Math.sin(lintangB_r) * Math.cos(epsilon_r) + Math.cos(lintangB_r) * Math.sin(epsilon_r) * Math.sin(
                bujurB_nampak_r
            )
        )
    )
    val deltaBulan_r = Math.toRadians(deltaBulan)
    val hourAngleBulan = lstNampak * 15 - alphaBulan
    val haBulan_r = Math.toRadians(hourAngleBulan)

    val azimuthBulanS = Math.toDegrees(
        Math.atan2(
            Math.sin(haBulan_r),
            Math.cos(haBulan_r) * Math.sin(lintang_r) - Math.tan(deltaBulan_r) * Math.cos(lintang_r)
        )
    )
    val azimuthBulan = (azimuthBulanS + 180) % 360
    val altitudeB = Math.toDegrees(
        Math.asin(
            Math.sin(lintang_r) * Math.sin(deltaBulan_r) + Math.cos(lintang_r) * Math.cos(deltaBulan_r) * Math.cos(
                haBulan_r
            )
        )
    )

    //Matahari
    val L = TabelMatahari.bujurEkliptik(tau, koreksibujurB)[1]
    val theta = TabelMatahari.bujurEkliptik(tau, koreksibujurB)[2]
    val lambdaM = TabelMatahari.bujurEkliptik(tau, koreksibujurB)[3]
    val lambdaM_r = Math.toRadians(lambdaM)
    val Delta_theta = TabelMatahari.bujurEkliptik(tau, koreksibujurB)[4]
    val theta_terkoreksi = (TabelMatahari.bujurEkliptik(tau, koreksibujurB)[5]).mod(360.0)
    val jarakBumi_Matahari = TabelMatahari.jarakBumiMat(tau)
    val jarakBm_M = 149598000 * jarakBumi_Matahari

    /**
     * Apparent Latitude Matahari
     * Lintang Ekliptika Matahari (Beta)
     */
    val lintangM = TabelMatahari.lintangEkliptikB(tau, lambdaM_r)[2]
    val beta_M_r = Math.toRadians(lintangM / 3600)
    val koreksiAberasi = -20.4898 / (3600 * jarakBumi_Matahari)

    /**
     * Apparent Longitude Matahari
     * Bujur Ekliptika Matahari (Lambda)
     */
    val bujurM_nampak = ((theta_terkoreksi + deltaPsi + koreksiAberasi) % 360).mod(360.0)
    val bujurM_nampak_r = Math.toRadians(bujurM_nampak)
    val sudutJariM = 959.63 / 3600 / jarakBumi_Matahari

    /**
     * Apparent Right Ascension Matahari (Alpha)
     */
    val alphaMatahari = (Math.toDegrees(
        Math.atan2(
            Math.sin(bujurM_nampak_r) * Math.cos(epsilon_r) - Math.tan(beta_M_r) * Math.sin(epsilon_r),
            Math.cos(bujurM_nampak_r)
        )
    )).mod(360.0) % 360

    /**
     * RA hour angle
     */
    val alphaM_pukul = alphaMatahari / 15
    val deltaMatahari = Math.toDegrees(
        Math.asin(
            Math.sin(beta_M_r) * Math.cos(epsilon_r) + Math.cos(beta_M_r) * Math.sin(epsilon_r) * Math.sin(
                bujurM_nampak_r
            )
        )
    )
    val deltaM_r = Math.toRadians(deltaMatahari)
    val hourAngleM = lstNampak * 15 - alphaMatahari
    val hourAngleM_r = Math.toRadians(hourAngleM)
    val azimuthM_selatan = Math.toDegrees(
        Math.atan2(
            Math.sin(hourAngleM_r),
            Math.cos(hourAngleM_r) * Math.sin(lintang_r) - Math.tan(deltaM_r) * Math.cos(lintang_r)
        )
    )

    val azimuthMatahari = (azimuthM_selatan + 180) % 360
    val altitudeM = Math.toDegrees(
        Math.asin(
            Math.sin(lintang_r) * Math.sin(deltaM_r) + Math.cos(lintang_r) * Math.cos(deltaM_r) * Math.cos(
                hourAngleM_r
            )
        )
    )
    val sudutParalaksM = Math.toDegrees(Math.atan(6378.14 / jarakBm_M))
    val U = (JD - 2451545) / 36525
    //bujur rata2 matahari
    val L0 = Math.toRadians((280.46607 + 36000.7698 * U) % 360)
    val _EoT =
        (-1 * (1789 + 237 * U) * Math.sin(L0) - (7146 - 62 * U) * Math.cos(L0) + (9934 - 14 * U) * Math.sin(2 * L0) - (29 + 5 * U) * Math.cos(
            2 * L0
        ) + (74 + 10 * U) * Math.sin(3 * L0) + (320 - 4 * U) * Math.cos(3 * L0) - 212 * Math.sin(4 * L0)) / 1000
    val EoT = _EoT / 60.0 //jadikan menit
    val sudutFai = Math.acos(
        Math.sin(deltaBulan_r) * Math.sin(deltaM_r) + Math.cos(deltaBulan_r) * Math.cos(deltaM_r) * Math.cos(
            Math.toRadians(alphaBulan - alphaMatahari)
        )
    )
    val sudutFase = Math.atan2(jarakBm_M * Math.sin(sudutFai), jarakBB - jarakBm_M * Math.cos(sudutFai))
    val sudutFase_d = Math.toDegrees(sudutFase)
    val iluminasiB = (1 + Math.cos(sudutFase)) / 2

    //bujur E bulan - bujur E matahari
    val BbdikurangiBm = bujurB_nampak - bujurM_nampak
    val elongasiBdanM = Math.sin(Math.toRadians(altitudeB)) * Math.sin(Math.toRadians(altitudeM)) + Math.cos(
        Math.toRadians(altitudeB)
    ) * Math.cos(
        Math.toRadians(altitudeM)
    ) * Math.cos(Math.toRadians(azimuthBulan - azimuthMatahari))
    val sudutElongasiBdanM = Math.toDegrees(Math.acos(elongasiBdanM))

    // tampilan tabel ephemeris




    // daftar menampilkan rincian perhitungan
    /*

    println("\n\nHari             = "+namahari(hari_ke));
    println("Pukul            = "+(int)jam+":"+(int)menit+":"+(int)detik);
    println("Bujur Geografis  = "+(int)derajat_bujur+"\u00B0"+(int)menit_bujur+"\u2032"+(int)detik_bujur+"\u2033");
    println("lintang Geografis= "+(int)derajat_lintang+"\u00B0"+(int)menit_lintang+"\u2032"+(int)detik_lintang+"\u2033");
    println("Posisi Bulan ");
    println("Bujur Ekliptika Bulan (Lambda)         = "+desimal_ke_derajat(bujurB_nampak)[1]+"\u00B0"+desimal_ke_derajat(bujurB_nampak)[2]+"\u2032"+desimal_ke_derajat(bujurB_nampak)[3]+"\u2033");
    println("Lintang Ekliptika Bulan (Beta)         = "+desimal_ke_derajat(lintangB)[1]+"\u00B0"+desimal_ke_derajat(lintangB)[2]+"\u2032"+desimal_ke_derajat(lintangB)[3]+"\u2033");
    println("Jarak bumi-bulan (Km)                  = "+(float)jarakBB);
    println("Right Ascension Bulan (Alpha)          = "+desimal_ke_derajat(alphaBulanPukul)[1]+":"+desimal_ke_derajat(alphaBulanPukul)[2]+":"+desimal_ke_derajat(alphaBulanPukul)[3]);
    println("Right Ascension Bulan                  = "+desimal_ke_derajat(alphaBulan)[1]+":"+desimal_ke_derajat(alphaBulan)[2]+":"+desimal_ke_derajat(alphaBulan)[3]);
    println("Deklinasi Bulan (Delta)                = "+desimal_ke_derajat(deltaBulan)[1]+"\u00B0"+desimal_ke_derajat(deltaBulan)[2]+"\u2032"+desimal_ke_derajat(deltaBulan)[3]+"\u2033");
    println("Azimuth Bulan dilihat dari lokasi      = "+desimal_ke_derajat(azimuthBulan)[1]+"\u00B0"+desimal_ke_derajat(azimuthBulan)[2]+"\u2032"+desimal_ke_derajat(azimuthBulan)[3]+"\u2033");
    println("True Altitude Bulan dilihat dari lokasi= "+desimal_ke_derajat(altitudeB)[1]+"\u00B0"+desimal_ke_derajat(altitudeB)[2]+"\u2032"+desimal_ke_derajat(altitudeB)[3]+"\u2033");
    println("Sudut paralaks Bulan                   = "+desimal_ke_derajat(sudutParalaksB)[1]+"\u00B0"+desimal_ke_derajat(sudutParalaksB)[2]+"\u2032"+desimal_ke_derajat(sudutParalaksB)[3]+"\u2033");
    println("Sudut jari-jari bulan                  = "+desimal_ke_derajat(sudutJariB)[1]+"\u00B0"+desimal_ke_derajat(sudutJariB)[2]+"\u2032"+desimal_ke_derajat(sudutJariB)[3]+"\u2033");
    println("Iluminasi bulan                        = "+(float)iluminasiB);

    println("\nPosisi Matahari ");
    println("Bujur Ekliptika Matahari (Lambda)      = "+desimal_ke_derajat(bujurM_nampak)[1]+"\u00B0"+desimal_ke_derajat(bujurM_nampak)[2]+"\u2032"+desimal_ke_derajat(bujurM_nampak)[3]+"\u2033");
    println("Lintang Ekliptika Matahari (Beta)      = "+(float)lintangM);
    println("Jarak bumi-Matahari (Km)               = "+(float)jarakBm_M);
    println("Right Ascension Matahari (Alpha)       = "+desimal_ke_derajat(alphaM_pukul)[1]+":"+desimal_ke_derajat(alphaM_pukul)[2]+":"+desimal_ke_derajat(alphaM_pukul)[3]);
    println("Right Ascension Matahari (Alpha)       = "+desimal_ke_derajat(alphaMatahari)[1]+":"+desimal_ke_derajat(alphaMatahari)[2]+":"+desimal_ke_derajat(alphaMatahari)[3]);
    println("Deklinasi Matahari (Delta)             = "+desimal_ke_derajat(deltaMatahari)[1]+"\u00B0"+desimal_ke_derajat(deltaMatahari)[2]+"\u2032"+desimal_ke_derajat(deltaMatahari)[3]+"\u2033");
    println("Azimuth Matahari dilihat dari lokasi   = "+desimal_ke_derajat(azimuthMatahari)[1]+"\u00B0"+desimal_ke_derajat(azimuthMatahari)[2]+"\u2032"+desimal_ke_derajat(azimuthMatahari)[3]+"\u2033");
    println("True Altitude M dilihat dari lokasi    = "+desimal_ke_derajat(altitudeM)[1]+"\u00B0"+desimal_ke_derajat(altitudeM)[2]+"\u2032"+desimal_ke_derajat(altitudeM)[3]+"\u2033");
    println("Sudut paralaks Matahari                = "+desimal_ke_derajat(sudutParalaksM)[1]+"\u00B0"+desimal_ke_derajat(sudutParalaksM)[2]+"\u2032"+desimal_ke_derajat(sudutParalaksM)[3]+"\u2033");
    println("Sudut jari-jari Matahari               = "+desimal_ke_derajat(sudutJariM)[1]+"\u00B0"+desimal_ke_derajat(sudutJariM)[2]+"\u2032"+desimal_ke_derajat(sudutJariM)[3]+"\u2033");

    println("\nKemiringan Bumi                        = "+desimal_ke_derajat(epsilon)[1]+"\u00B0"+desimal_ke_derajat(epsilon)[2]+"\u2032"+desimal_ke_derajat(epsilon)[3]+"\u2033");
    println("bujur E bulan - bujut E matahari       = "+desimal_ke_derajat(BbdikurangiBm)[1]+"\u00B0"+desimal_ke_derajat(BbdikurangiBm)[2]+"\u2032"+desimal_ke_derajat(BbdikurangiBm)[3]+"\u2033");
    println("Sudut elongasi bulan-matahari          = "+desimal_ke_derajat(sudutElongasiBdanM)[1]+"\u00B0"+desimal_ke_derajat(sudutElongasiBdanM)[2]+"\u2032"+desimal_ke_derajat(sudutElongasiBdanM)[3]+"\u2033");
    println("Sudut Fase                             = "+desimal_ke_derajat(sudutFase_d)[1]+"\u00B0"+desimal_ke_derajat(sudutFase_d)[2]+"\u2032"+desimal_ke_derajat(sudutFase_d)[3]+"\u2033");
    println("Delta T                                = "+(float)delta_T*86400);


    println("\n\t\t\t\tDetail Perhitungan");
    println("Lintang geografis        = "+lintang);
    println("Bujur geografis          = "+bujur);
    println("julian day UT            = "+JD_UT);
    println("T  (UT)                  = "+T_UT);
    println("delta_T                  = "+(float)delta_T);
    println("julian day E (JDE) TD    = "+jde);
    println("T  (TD)                  = "+T_TD);
    println("tau                      = "+tau);
    println("gstpukul                 = "+gstpukul);
    println("gstnampak                = "+gstnampak);
    println("lstnampak                = "+lstnampak);
    println("epsilon                  = "+epsilon);
    println("\n\t\t\t\tDetail Perhitungan Bulan");
    println("Bujur rata2 bulan (L')   = "+L1);
    println("Koreksi bujur bulan      = "+koreksibujurB);
    println("Bujur bulan              = "+bujurB);
    println("Koreksi DeltaPsi (Nutasi)= "+deltaPsi);
    println("Bujur bulan nampak       = "+bujurB_nampak);
    println("Elongasi rata2 Bulan     = "+d);
    println("Anomali rata2 Matahari   = "+m);
    println("Anomali rata2 bulan      = "+ma);
    println("Argumen bujur bulan      = "+f);
    println("Eksentrisitas orbit      = "+e);
    println("lintang bulan            = "+lintangB);
    println("jarak bumi-bulan         = "+jarakBB);
    println("sudutParalaks            = "+sudutParalaksB);
    println("sudut jari2 Bulan        = "+sudutJariB);
    println("Alpha Bulan              = "+alphaBulan);
    println("Delta Bulan              = "+deltaBulan);
    println("Hour Angle Bulan         = "+hourAngleBulan);
    println("Azimuth Bulan S          = "+azimuthBulanS);
    println("Azimuth Bulan            = "+azimuthBulan);
    println("Altitude Bulan           = "+altitudeB);
    println("Sudut Fai                = "+sudutFai);
    println("Sudut Fase               = "+sudutFase);
    println("iluminasi Bulan          = "+iluminasiB);

    println("\n\t\t\t\tDetail Perhitungan Matahari");
    println("tau                      = "+tau);
    println("L                        = "+L);
    println("Theta                    = "+theta);
    println("Theta terkoreksi         = "+theta_terkoreksi);
    println("DeltaPsi (Nutasi)        = "+deltaPsi);
    println("koreksiAberasi           = "+koreksiAberasi);
    println("bujurM_nampak            = "+bujurM_nampak);
    println("Lintang M nampak (Beta)  = "+lintangM);
    println("Jarak Bumi Matahari (AU) = "+jarakBumi_Matahari);
    println("Sudut jari2 Matahari     = "+sudutJariM);
    println("Alpha Matahari           = "+alphaMatahari);
    println("Delta Matahari           = "+deltaMatahari);
    println("Hour Angle Matahari      = "+hourAngleM);
    println("Azimuth Matahari S       = "+azimuthM_selatan);
    println("Azimuth Matahari         = "+azimuthMatahari);
    println("Altitude Matahari        = "+altitudeM);
    println("Sudut paralaks Matahari  = "+sudutParalaksM);
    println("bujur Ek B - bujur Ek M  = "+BbdikurangiBm);
    println("COS(Elongasi B M)        = "+elongasiBdanM);
    println("Sudut elongasi B-M       = "+sudutElongasiBdanM);
    println("Perata waktu             = "+desimal_ke_derajat(EoT)[1]+"\u00B0"+desimal_ke_derajat(EoT)[2]+"\u2032"+desimal_ke_derajat(EoT)[3]+"\u2033");

    // */




}