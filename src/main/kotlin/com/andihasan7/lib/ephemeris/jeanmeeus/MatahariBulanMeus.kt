
package com.andihasan7.lib.ephemeris.jeanmeeus

object MatahariBulanMeeus {

    @JvmStatic
    fun main(args: Array<String>) {
        val tanggal = 5
        var bulan = 2
        var tahun = 2023
        val timezone = 0.0
        val jam = 0.0
        val menit = 0.0
        val detik = 0.0
        val pukul_keJD = (jam * 3600 + menit * 60 + detik) / 86400
        println("\n\t\t\t\t\t\t\t" + "Program Ephemeris Algoritma Jean Meeus")
        println("\t\t\t\t\t\t\t\t\t\t$tanggal-$bulan-$tahun\n")
        val derajat_bujur = 112.0
        val menit_bujur = 13.0
        val detik_bujur = 0.0
        val bujur = derajat_ke_desimal(derajat_bujur, menit_bujur, detik_bujur)
        val derajat_lintang = -7.0
        val menit_lintang = 47.0
        val detik_lintang = 0.0
        val lintang = derajat_ke_desimal(derajat_lintang, menit_lintang, detik_lintang)
        val lintang_r = Math.toRadians(lintang)


        //waktu lokal ke UT
        val UT = (jam - timezone).toInt()

        //hitung nilai Julian day
        if (bulan <= 2) {
            bulan += 12
            tahun -= 1
        }
        var A = 0
        var B = 0

        //bila gregorian
        if (tahun == 1582 && bulan >= 10 && tanggal > 4 || tahun > 1582 || tahun == 1582 && bulan > 10) {
            A = tahun / 100
            B = 2 + A / 4 - A
        }
        val JD =
            1720994.5 + (365.25 * tahun).toInt() + (30.60001 * (bulan + 1)).toInt() + B + tanggal + (jam + menit / 60 + detik / 3600) / 24 - timezone / 24
        val JD_UT =
            1720994.5 + (365.25 * tahun).toInt() + (30.60001 * (bulan + 1)).toInt() + B + tanggal + (jam + menit / 60 + detik / 3600) / 24 - timezone / 24

        //nama hari
        val hari_ke = ((JD + 1.5) % 7 + 1).toInt()
        val delta_T = 0.0

        //JDE waktu TD(Dynamical time)
        val jde = JD_UT + delta_T
        val T_UT = (JD_UT - 2451545) / 36525
        val T_TD = (jde - 2451545) / 36525
        val tau = T_TD / 10

        //Greenwich sideral time
        var gst0 = 6.6973745583 + 2400.0513369072 * T_TD + 0.0000258622 * T_TD * T_TD

        //fungsi MOD(?) kalou di Excel/LibreCalc
        if (gst0 < 0) {
            while (gst0 < 0) {
                gst0 += 24.0
            }
        } else if (gst0 > 24) {
            while (gst0 > 24) {
                gst0 -= 24.0
            }
        }
        var gstUT = gst0 + 1.0027379035 * UT
        var gstLokal = (gst0 + (jam + menit / 60 + detik / 3600 - timezone) * 1.00273790935) % 24
        if (gstUT >= 24) gstUT -= 24.0
        gstLokal /= 15.0

        //lokal sideral time
        var LST = 0.0
        LST = if (bujur > 0) gstLokal + bujur / 15 else gstLokal - bujur / 15
        if (LST >= 24) LST -= 24.0
        var gstpukul =
            (280.46061837 + 360.98564736629 * (JD_UT - 2451545) + 0.000387933 * T_UT * T_UT - T_UT * T_UT * T_UT / 38710000) % 360
        if (gstpukul < 0) gstLokal += 360.0
        gstpukul /= 15.0
        val deltaPsi = Nutasi.deltaPsiDanEpsilon(T_TD)[2]
        val epsilon = Nutasi.deltaPsiDanEpsilon(T_TD)[6]
        val epsilon_r = Math.toRadians(epsilon)
        val gstnampak = gstpukul + deltaPsi * Math.cos(epsilon_r) / 15
        var lstnampak = (gstnampak + bujur / 15) % 24
        if (lstnampak < 0) lstnampak += 24.0

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
        var bujurB_nampak = (bujurB + deltaPsi) % 360
        if (bujurB_nampak < 0) bujurB_nampak += 360.0
        val bujurB_nampak_r = Math.toRadians(bujurB_nampak)

        //Koreksi lintang bulan
        val lintangB: Double = TabelBulan.sukuPeriodik(T_TD, L1).get(7)
        val lintangB_r = Math.toRadians(lintangB)

        //Koreksi jarak bumi-bulan
        val jarakBB: Double = 385000.56 + TabelBulan.sukuPeriodik(T_TD, L1).get(8)
        val sudutParalaksB = Math.toDegrees(Math.asin(6378.14 / jarakBB))
        val sudutJariB = 358473400 / (jarakBB * 3600)
        var alphaBulan = Math.toDegrees(
            Math.atan2(
                Math.sin(bujurB_nampak_r) * Math.cos(epsilon_r) - Math.tan(lintangB_r) * Math.sin(epsilon_r),
                Math.cos(bujurB_nampak_r)
            )
        ) % 360
        if (alphaBulan < 0) alphaBulan = (alphaBulan + 360) % 360
        val alphaBulanPukul = alphaBulan / 15
        val deltaBulan = Math.toDegrees(
            Math.asin(
                Math.sin(lintangB_r) * Math.cos(epsilon_r) + Math.cos(lintangB_r) * Math.sin(epsilon_r) * Math.sin(
                    bujurB_nampak_r
                )
            )
        )
        val deltaBulan_r = Math.toRadians(deltaBulan)
        val hourAngleBulan = lstnampak * 15 - alphaBulan
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
        var theta_terkoreksi = TabelMatahari.bujurEkliptik(tau, koreksibujurB)[5]
        if (theta_terkoreksi < 0) theta_terkoreksi += 360.0
        val jarakBumi_Matahari = TabelMatahari.jarakBumiMat(tau)
        val jarakBm_M = 149598000 * jarakBumi_Matahari
        val lintangM = TabelMatahari.lintangEkliptikB(tau, lambdaM_r)[2]
        val beta_M_r = Math.toRadians(lintangM / 3600)
        val koreksiAberasi = -20.4898 / (3600 * jarakBumi_Matahari)
        var bujurM_nampak = (theta_terkoreksi + deltaPsi + koreksiAberasi) % 360
        if (bujurM_nampak < 0) bujurM_nampak += 360.0
        val bujurM_nampak_r = Math.toRadians(bujurM_nampak)
        val sudutJariM = 959.63 / 3600 / jarakBumi_Matahari
        var alphaMatahari = Math.toDegrees(
            Math.atan2(
                Math.sin(bujurM_nampak_r) * Math.cos(epsilon_r) - Math.tan(beta_M_r) * Math.sin(epsilon_r),
                Math.cos(bujurM_nampak_r)
            )
        ) % 360
        if (alphaMatahari < 0) alphaMatahari = (alphaMatahari + 360) % 360
        val alphaM_pukul = alphaMatahari / 15
        val deltaMatahari = Math.toDegrees(
            Math.asin(
                Math.sin(beta_M_r) * Math.cos(epsilon_r) + Math.cos(beta_M_r) * Math.sin(epsilon_r) * Math.sin(
                    bujurM_nampak_r
                )
            )
        )
        val deltaM_r = Math.toRadians(deltaMatahari)
        val hourAngleM = lstnampak * 15 - alphaMatahari
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
        var EoT =
            (-1 * (1789 + 237 * U) * Math.sin(L0) - (7146 - 62 * U) * Math.cos(L0) + (9934 - 14 * U) * Math.sin(2 * L0) - (29 + 5 * U) * Math.cos(
                2 * L0
            ) + (74 + 10 * U) * Math.sin(3 * L0) + (320 - 4 * U) * Math.cos(3 * L0) - 212 * Math.sin(4 * L0)) / 1000
        EoT /= 60.0 //jadikan menit
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

        //menampilkan hasil
        //formatter jarak antara teks yang di print biar layoutnya rapi
        val formatter = "%-8s%-15s%-15s%-15s%-15s%-15s%-15s%5s%n"
        println(
            """
    
    Data Matahari
    """.trimIndent()
        )
        System.out.printf(
            formatter,
            "Jam",
            "Ecliptic",
            "Ecliptic",
            "Right",
            "Apparent",
            "Semi",
            "Kemiringan",
            "Equation"
        )
        System.out.printf(
            formatter,
            "Gmt",
            "Longitude",
            "Latitude",
            "Ascension",
            "Declination",
            "Diameter",
            "(Epsilon)",
            "of time"
        )
        System.out.printf(
            formatter,
            jam.toInt(),
            desimal_ke_derajat(theta_terkoreksi)[1].toString() + "°" + desimal_ke_derajat(
                theta_terkoreksi
            )[2] + "′" + desimal_ke_derajat(theta_terkoreksi)[3] + "″",
            lintangM.toFloat(),
            desimal_ke_derajat(alphaMatahari)[1].toString() + ":" + desimal_ke_derajat(
                alphaMatahari
            )[2] + ":" + desimal_ke_derajat(alphaMatahari)[3],
            desimal_ke_derajat(deltaMatahari)[1].toString() + "°" + desimal_ke_derajat(
                deltaMatahari
            )[2] + "′" + desimal_ke_derajat(deltaMatahari)[3] + "″",
            desimal_ke_derajat(sudutJariM)[1].toString() + "°" + desimal_ke_derajat(
                sudutJariM
            )[2] + "′" + desimal_ke_derajat(sudutJariM)[3] + "″",
            desimal_ke_derajat(epsilon)[1].toString() + "°" + desimal_ke_derajat(
                epsilon
            )[2] + "′" + desimal_ke_derajat(epsilon)[3] + "″",
            desimal_ke_derajat(EoT)[1].toString() + "°" + desimal_ke_derajat(EoT)[2] + "′" + desimal_ke_derajat(
                EoT
            )[3] + "″"
        )
        println(
            """
    
    
    Data Bulan
    """.trimIndent()
        )
        System.out.printf(
            formatter,
            "Jam",
            "Apparent",
            "Apparent",
            "Right",
            "Apparent",
            "Semi",
            "Horizontal",
            "Iluminasi"
        )
        System.out.printf(
            formatter,
            "Gmt",
            "Longitude",
            "Latitude",
            "Ascension",
            "Declination",
            "Diameter",
            "Parallax",
            "Bulan"
        )
        System.out.printf(
            formatter,
            jam.toInt(),
            desimal_ke_derajat(bujurB_nampak)[1].toString() + "\u00B0" + desimal_ke_derajat(
                bujurB_nampak
            )[2] + "\u2032" + desimal_ke_derajat(bujurB_nampak)[3] + "\u2033",
            desimal_ke_derajat(lintangB)[1].toString() + "\u00B0" + desimal_ke_derajat(
                lintangB
            )[2] + "\u2032" + desimal_ke_derajat(lintangB)[3] + "\u2033",
            desimal_ke_derajat(alphaBulan)[1].toString() + ":" + desimal_ke_derajat(
                alphaBulan
            )[2] + ":" + desimal_ke_derajat(alphaBulan)[3],
            desimal_ke_derajat(deltaBulan)[1].toString() + "\u00B0" + desimal_ke_derajat(
                deltaBulan
            )[2] + "\u2032" + desimal_ke_derajat(deltaBulan)[3] + "\u2033",
            desimal_ke_derajat(sudutJariB)[1].toString() + "\u00B0" + desimal_ke_derajat(
                sudutJariB
            )[2] + "\u2032" + desimal_ke_derajat(sudutJariB)[3] + "\u2033",
            desimal_ke_derajat(sudutParalaksB)[1].toString() + "\u00B0" + desimal_ke_derajat(
                sudutParalaksB
            )[2] + "\u2032" + desimal_ke_derajat(sudutParalaksB)[3] + "\u2033",
            String.format("%.5f", iluminasiB)
        )
        println(" ")
        println(EoT)

        //Hilangkan tanda dibawah ini "/*", bila ingin menampilkan rincian perhitungan


        System.out.println("\n\nHari             = "+namahari(hari_ke));
        System.out.println("Pukul            = "+jam+":"+menit+":"+detik);
        System.out.println("Bujur Geografis  = "+derajat_bujur+"\u00B0"+menit_bujur+"\u2032"+detik_bujur+"\u2033");
        System.out.println("lintang Geografis= "+derajat_lintang+"\u00B0"+menit_lintang+"\u2032"+detik_lintang+"\u2033");
        System.out.println("Posisi Bulan ");
        System.out.println("Bujur Ekliptika Bulan (Lambda)         = "+desimal_ke_derajat(bujurB_nampak)[1]+"\u00B0"+desimal_ke_derajat(bujurB_nampak)[2]+"\u2032"+desimal_ke_derajat(bujurB_nampak)[3]+"\u2033");
        System.out.println("Lintang Ekliptika Bulan (Beta)         = "+desimal_ke_derajat(lintangB)[1]+"\u00B0"+desimal_ke_derajat(lintangB)[2]+"\u2032"+desimal_ke_derajat(lintangB)[3]+"\u2033");
        System.out.println("Jarak bumi-bulan (Km)                  = "+jarakBB);
        System.out.println("Right Ascension Bulan (Alpha)          = "+desimal_ke_derajat(alphaBulanPukul)[1]+":"+desimal_ke_derajat(alphaBulanPukul)[2]+":"+desimal_ke_derajat(alphaBulanPukul)[3]);
        System.out.println("Right Ascension Bulan                  = "+desimal_ke_derajat(alphaBulan)[1]+":"+desimal_ke_derajat(alphaBulan)[2]+":"+desimal_ke_derajat(alphaBulan)[3]);
        System.out.println("Deklinasi Bulan (Delta)                = "+desimal_ke_derajat(deltaBulan)[1]+"\u00B0"+desimal_ke_derajat(deltaBulan)[2]+"\u2032"+desimal_ke_derajat(deltaBulan)[3]+"\u2033");
        System.out.println("Azimuth Bulan dilihat dari lokasi      = "+desimal_ke_derajat(azimuthBulan)[1]+"\u00B0"+desimal_ke_derajat(azimuthBulan)[2]+"\u2032"+desimal_ke_derajat(azimuthBulan)[3]+"\u2033");
        System.out.println("True Altitude Bulan dilihat dari lokasi= "+desimal_ke_derajat(altitudeB)[1]+"\u00B0"+desimal_ke_derajat(altitudeB)[2]+"\u2032"+desimal_ke_derajat(altitudeB)[3]+"\u2033");
        System.out.println("Sudut paralaks Bulan                   = "+desimal_ke_derajat(sudutParalaksB)[1]+"\u00B0"+desimal_ke_derajat(sudutParalaksB)[2]+"\u2032"+desimal_ke_derajat(sudutParalaksB)[3]+"\u2033");
        System.out.println("Sudut jari-jari bulan                  = "+desimal_ke_derajat(sudutJariB)[1]+"\u00B0"+desimal_ke_derajat(sudutJariB)[2]+"\u2032"+desimal_ke_derajat(sudutJariB)[3]+"\u2033");
        System.out.println("Iluminasi bulan                        = "+iluminasiB);

        System.out.println("\nPosisi Matahari ");
        System.out.println("Bujur Ekliptika Matahari (Lambda)      = "+desimal_ke_derajat(bujurM_nampak)[1]+"\u00B0"+desimal_ke_derajat(bujurM_nampak)[2]+"\u2032"+desimal_ke_derajat(bujurM_nampak)[3]+"\u2033");
        System.out.println("Lintang Ekliptika Matahari (Beta)      = "+lintangM);
        System.out.println("Jarak bumi-Matahari (Km)               = "+jarakBm_M);
        System.out.println("Right Ascension Matahari (Alpha)       = "+desimal_ke_derajat(alphaM_pukul)[1]+":"+desimal_ke_derajat(alphaM_pukul)[2]+":"+desimal_ke_derajat(alphaM_pukul)[3]);
        System.out.println("Right Ascension Matahari (Alpha)       = "+desimal_ke_derajat(alphaMatahari)[1]+":"+desimal_ke_derajat(alphaMatahari)[2]+":"+desimal_ke_derajat(alphaMatahari)[3]);
        System.out.println("Deklinasi Matahari (Delta)             = "+desimal_ke_derajat(deltaMatahari)[1]+"\u00B0"+desimal_ke_derajat(deltaMatahari)[2]+"\u2032"+desimal_ke_derajat(deltaMatahari)[3]+"\u2033");
        System.out.println("Azimuth Matahari dilihat dari lokasi   = "+desimal_ke_derajat(azimuthMatahari)[1]+"\u00B0"+desimal_ke_derajat(azimuthMatahari)[2]+"\u2032"+desimal_ke_derajat(azimuthMatahari)[3]+"\u2033");
        System.out.println("True Altitude M dilihat dari lokasi    = "+desimal_ke_derajat(altitudeM)[1]+"\u00B0"+desimal_ke_derajat(altitudeM)[2]+"\u2032"+desimal_ke_derajat(altitudeM)[3]+"\u2033");
        System.out.println("Sudut paralaks Matahari                = "+desimal_ke_derajat(sudutParalaksM)[1]+"\u00B0"+desimal_ke_derajat(sudutParalaksM)[2]+"\u2032"+desimal_ke_derajat(sudutParalaksM)[3]+"\u2033");
        System.out.println("Sudut jari-jari Matahari               = "+desimal_ke_derajat(sudutJariM)[1]+"\u00B0"+desimal_ke_derajat(sudutJariM)[2]+"\u2032"+desimal_ke_derajat(sudutJariM)[3]+"\u2033");

        System.out.println("\nKemiringan Bumi                        = "+desimal_ke_derajat(epsilon)[1]+"\u00B0"+desimal_ke_derajat(epsilon)[2]+"\u2032"+desimal_ke_derajat(epsilon)[3]+"\u2033");
        System.out.println("bujur E bulan - bujut E matahari       = "+desimal_ke_derajat(BbdikurangiBm)[1]+"\u00B0"+desimal_ke_derajat(BbdikurangiBm)[2]+"\u2032"+desimal_ke_derajat(BbdikurangiBm)[3]+"\u2033");
        System.out.println("Sudut elongasi bulan-matahari          = "+desimal_ke_derajat(sudutElongasiBdanM)[1]+"\u00B0"+desimal_ke_derajat(sudutElongasiBdanM)[2]+"\u2032"+desimal_ke_derajat(sudutElongasiBdanM)[3]+"\u2033");
        System.out.println("Sudut Fase                             = "+desimal_ke_derajat(sudutFase_d)[1]+"\u00B0"+desimal_ke_derajat(sudutFase_d)[2]+"\u2032"+desimal_ke_derajat(sudutFase_d)[3]+"\u2033");
        System.out.println("Delta T                                = "+ delta_T * 86400);


        System.out.println("\n\t\t\t\tDetail Perhitungan");
        System.out.println("Lintang geografis        = "+lintang);
        System.out.println("Bujur geografis          = "+bujur);
        System.out.println("julian day UT            = "+JD_UT);
        System.out.println("T  (UT)                  = "+T_UT);
        System.out.println("delta_T                  = "+delta_T);
        System.out.println("julian day E (JDE) TD    = "+jde);
        System.out.println("T  (TD)                  = "+T_TD);
        System.out.println("tau                      = "+tau);
        System.out.println("gstpukul                 = "+gstpukul);
        System.out.println("gstnampak                = "+gstnampak);
        System.out.println("lstnampak                = "+lstnampak);
        System.out.println("epsilon                  = "+epsilon);
        System.out.println("\n\t\t\t\tDetail Perhitungan Bulan");
        System.out.println("Bujur rata2 bulan (L')   = "+L1);
        System.out.println("Koreksi bujur bulan      = "+koreksibujurB);
        System.out.println("Bujur bulan              = "+bujurB);
        System.out.println("Koreksi DeltaPsi (Nutasi)= "+deltaPsi);
        System.out.println("Bujur bulan nampak       = "+bujurB_nampak);
        System.out.println("Elongasi rata2 Bulan     = "+d);
        System.out.println("Anomali rata2 Matahari   = "+m);
        System.out.println("Anomali rata2 bulan      = "+ma);
        System.out.println("Argumen bujur bulan      = "+f);
        System.out.println("Eksentrisitas orbit      = "+e);
        System.out.println("lintang bulan            = "+lintangB);
        System.out.println("jarak bumi-bulan         = "+jarakBB);
        System.out.println("sudutParalaks            = "+sudutParalaksB);
        System.out.println("sudut jari2 Bulan        = "+sudutJariB);
        System.out.println("Alpha Bulan              = "+alphaBulan);
        System.out.println("Delta Bulan              = "+deltaBulan);
        System.out.println("Hour Angle Bulan         = "+hourAngleBulan);
        System.out.println("Azimuth Bulan S          = "+azimuthBulanS);
        System.out.println("Azimuth Bulan            = "+azimuthBulan);
        System.out.println("Altitude Bulan           = "+altitudeB);
        System.out.println("Sudut Fai                = "+sudutFai);
        System.out.println("Sudut Fase               = "+sudutFase);
        System.out.println("iluminasi Bulan          = "+iluminasiB);

        System.out.println("\n\t\t\t\tDetail Perhitungan Matahari");
        System.out.println("tau                      = "+tau);
        System.out.println("L                        = "+L);
        System.out.println("Theta                    = "+theta);
        System.out.println("Theta terkoreksi         = "+theta_terkoreksi);
        System.out.println("DeltaPsi (Nutasi)        = "+deltaPsi);
        System.out.println("koreksiAberasi           = "+koreksiAberasi);
        System.out.println("bujurM_nampak            = "+bujurM_nampak);
        System.out.println("Lintang M nampak (Beta)  = "+lintangM);
        System.out.println("Jarak Bumi Matahari (AU) = "+jarakBumi_Matahari);
        System.out.println("Sudut jari2 Matahari     = "+sudutJariM);
        System.out.println("Alpha Matahari           = "+alphaMatahari);
        System.out.println("Delta Matahari           = "+deltaMatahari);
        System.out.println("Hour Angle Matahari      = "+hourAngleM);
        System.out.println("Azimuth Matahari S       = "+azimuthM_selatan);
        System.out.println("Azimuth Matahari         = "+azimuthMatahari);
        System.out.println("Altitude Matahari        = "+altitudeM);
        System.out.println("Sudut paralaks Matahari  = "+sudutParalaksM);
        System.out.println("bujur Ek B - bujur Ek M  = "+BbdikurangiBm);
        System.out.println("COS(Elongasi B M)        = "+elongasiBdanM);
        System.out.println("Sudut elongasi B-M       = "+sudutElongasiBdanM);
        System.out.println("Perata waktu             = "+desimal_ke_derajat(EoT)[1]+"\u00B0"+desimal_ke_derajat(EoT)[2]+"\u2032"+desimal_ke_derajat(EoT)[3]+"\u2033");

        //
    }

    fun desimal_ke_derajat(desimal: Double): IntArray {
        //cek nilai negatif atau bukan
        var desimal = desimal
        var negatif = false
        if (desimal < 0) negatif = true

        //ini menghitungnya mengabaikan nilai negatif
        desimal = Math.abs(desimal)
        var jah = desimal.toInt()
        var qoh = Math.abs(desimal % 1 * 60)
        var ni = Math.round(qoh % 1 * 60).toDouble()
        //ini pembulatan
        if (ni.toInt() > 59) {
            ni -= 60.0
            qoh += 1.0
        }
        if (qoh.toInt() > 59) {
            qoh -= 60.0
            jah += 1
        }

        //bila negatif
        return if (negatif) {
            if (jah == 0) {
                if (qoh.toInt() == 0) intArrayOf(0, -jah, -qoh.toInt(), -ni.toInt()) else intArrayOf(
                    0,
                    -jah,
                    -qoh.toInt(),
                    ni.toInt()
                )
            } else intArrayOf(0, -jah, qoh.toInt(), ni.toInt())
        } else intArrayOf(0, jah, qoh.toInt(), ni.toInt())
    }

    fun derajat_ke_desimal(derajat: Double, menit: Double, detik: Double): Double {
        return if (derajat < 0) derajat - menit / 60 - detik / 3600 else derajat + menit / 60 + detik / 3600
    }

    fun namahari(hari: Int): String {
        var namahari = ""
        when (hari) {
            1 -> namahari = "Ahad"
            2 -> namahari = "Senin"
            3 -> namahari = "Selasa"
            4 -> namahari = "Rabu"
            5 -> namahari = "Kamis"
            6 -> namahari = "Jum´at"
            7, 0 -> namahari = "Sabtu"
        }
        return namahari
    }
}