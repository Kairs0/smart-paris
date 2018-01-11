package com.atelierdev.itineraire.monitineraireapp;

import org.junit.Before;
import org.junit.Test;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Created by Arnaud on 10/01/2018.
 */

public class MatrixApiThreadTest {

    private final String coordParis = "Paris";
    private final String coordChatenay = "Chatenay%20Malabry";
    private final String coordAntony = "Antony";
    private final String coordGoogle = "37.421998333333335,-122.08400000000002"; //California
    private final String coordSantiago = "33.4489,70.6693"; //Santiago de Chile
    private final String coordGif = "48.7018,2.1341"; //Gif sur Yvette
    private final String coordLyon = "45.7640,4.8357";
    private final String coordGrenoble = "43.7102,7.2620" ;
    private final String coordDijon = "47.3220,5.0415" ;
    private final String coordLille = "50.6292,3.0573" ;
    private final String coordToulouse = "43.6047,1.4442" ;
    private final String coordNantes = "47.2184,1.5536" ;
    private final String coordNancy = "48.6921,6.1844" ;
    private final String coordLaRochelle = "46.1603,1.1511" ;
    private final String coordBordeaux = "44.8378,0.5792" ;
    private final String coordMarseille = "43.2965,5.3698" ;
    private final String coordNice = "43.7102,7.2620" ;

    private final String randomCoord0 = "13.387492797199,34.95732917725018" ;
    private final String randomCoord1 = "-44.30401523601945,-124.76601226354262" ;
    private final String randomCoord2 = "73.61062615634224,-169.40679346578548" ;
    private final String randomCoord3 = "-65.10211233725216,-106.66181554491829" ;
    private final String randomCoord4 = "-90.81073280903813,130.13449318943631" ;
    private final String randomCoord5 = "68.83195076616268,93.23417142194444" ;
    private final String randomCoord6 = "-6.44581315731393,163.903291282023" ;
    private final String randomCoord7 = "10.15807505102329,-50.80713043468524" ;
    private final String randomCoord8 = "81.88659395372258,167.97168298467851" ;
    private final String randomCoord9 = "19.39330867482114,-15.46727930883276" ;
    private final String randomCoord10 = "-26.34544791834102,-57.85631741546690" ;
    private final String randomCoord11 = "87.97235317228728,-162.65010512042714" ;
    private final String randomCoord12 = "-45.98369305271849,95.19297301732367" ;
    private final String randomCoord13 = "25.6668530320584,88.81586106973717" ;
    private final String randomCoord14 = "-12.7069153938467,-8.42241433797459" ;
    private final String randomCoord15 = "32.26852645956834,132.14252991643000" ;
    private final String randomCoord16 = "50.96450912181953,53.60804657283846" ;
    private final String randomCoord17 = "29.51637819769989,41.4364891555393" ;
    private final String randomCoord18 = "-43.86262086790903,-123.97746844420734" ;
    private final String randomCoord19 = "14.49203443236541,76.27166066805174" ;
    private final String randomCoord20 = "-9.1677856948151,22.2183621245506" ;
    private final String randomCoord21 = "-90.79778466778053,113.62687096468131" ;
    private final String randomCoord22 = "-25.67837498947605,179.39483254475847" ;
    private final String randomCoord23 = "-70.58848294580729,-96.77576647020115" ;
    private final String randomCoord24 = "-50.26303286788784,2.33864389021036" ;
    private final String randomCoord25 = "-83.21804598218030,56.97830526294349" ;
    private final String randomCoord26 = "-64.4538295861363,3.6392356508170" ;
    private final String randomCoord27 = "-60.23352574338231,-45.38858393676800" ;
    private final String randomCoord28 = "-75.88064160802751,-139.59521235800467" ;
    private final String randomCoord29 = "-52.66053780374778,-175.19466460673517" ;
    private final String randomCoord30 = "41.87872407789273,77.84388336453020" ;
    private final String randomCoord31 = "-31.76834639124118,126.53789103090690" ;
    private final String randomCoord32 = "32.64077722825945,142.71075370032009" ;
    private final String randomCoord33 = "-76.1364817113196,9.90245416579456" ;
    private final String randomCoord34 = "46.25282732228990,78.3071942968827" ;
    private final String randomCoord35 = "-40.12209036924564,82.38258360400648" ;
    private final String randomCoord36 = "-54.67952314166034,-155.88265080169396" ;
    private final String randomCoord37 = "-24.99213060576520,21.59338864405990" ;
    private final String randomCoord38 = "44.56446875544794,-71.15655981475620" ;
    private final String randomCoord39 = "74.17811017957467,176.22672748148893" ;
    private final String randomCoord40 = "65.29239789610247,62.64297851355741" ;
    private final String randomCoord41 = "26.96893111722241,-145.82927141383827" ;
    private final String randomCoord42 = "-25.46901957805187,71.21454680624918" ;
    private final String randomCoord43 = "-72.66988153783282,-141.76328657779564" ;
    private final String randomCoord44 = "-68.38704022589208,-142.9552725452921" ;
    private final String randomCoord45 = "-66.99523541361239,-63.52980705056060" ;
    private final String randomCoord46 = "-46.9940814781383,-171.5584327590523" ;
    private final String randomCoord47 = "15.69703138234260,152.31108696407694" ;
    private final String randomCoord48 = "67.558677189631,-161.67073064843072" ;
    private final String randomCoord49 = "-59.87509685112225,40.40559437997403" ;
    private final String randomCoord50 = "56.80863911871967,-53.28070676274115" ;
    private final String randomCoord51 = "60.7756737085458,-109.42666095068382" ;
    private final String randomCoord52 = "8.18330813672541,-51.69384472161379" ;
    private final String randomCoord53 = "66.64564338243415,138.90565753035889" ;
    private final String randomCoord54 = "-21.53227333186928,-59.3669440460901" ;
    private final String randomCoord55 = "-77.43953562710853,68.34570307942744" ;
    private final String randomCoord56 = "-53.56549472669150,-143.90300943662447" ;
    private final String randomCoord57 = "-79.8840487984963,62.46079280696941" ;
    private final String randomCoord58 = "-21.67747452182792,40.75635268970083" ;
    private final String randomCoord59 = "-33.14426273923917,-90.66476345946217" ;


    private final String mode = "walking";

    private MatrixApiThread validParams;
    private MatrixApiThread invalidParams;
    private MatrixApiThread lotsOfParams;
    private MatrixApiThread tooManyParams;

    private Thread threadValidParams;
    private Thread threadInvalidParams;
    private Thread threadLotsOfParams;
    private Thread threadTooManyParams;

    @Before
    public void initApiInterfaces(){
        List<String> validOrigins = new ArrayList<>(Arrays.asList(coordParis, coordChatenay));
        List<String> validDestinations = new ArrayList<>(Arrays.asList(coordAntony, coordGif));
        List<String> crazyDestinations = new ArrayList<>(Arrays.asList(coordGoogle, coordSantiago, coordGif));

        List<String> lotsOfOrigins = new ArrayList<>();
        List<String> lotsOfDestinations = new ArrayList<>();
        List<String> tooManyDestinations = new ArrayList<>();
        lotsOfOrigins.add(randomCoord0);
        lotsOfOrigins.add(randomCoord1);
        lotsOfOrigins.add(randomCoord2);
        lotsOfOrigins.add(randomCoord3);
        lotsOfOrigins.add(randomCoord4);
        lotsOfOrigins.add(randomCoord5);
        lotsOfOrigins.add(randomCoord6);
        lotsOfOrigins.add(randomCoord7);
        lotsOfOrigins.add(randomCoord8);
        lotsOfOrigins.add(randomCoord9);
        lotsOfOrigins.add(randomCoord10);
        lotsOfOrigins.add(randomCoord11);
        lotsOfOrigins.add(randomCoord12);
        lotsOfOrigins.add(randomCoord13);
        lotsOfOrigins.add(randomCoord14);
        lotsOfOrigins.add(randomCoord15);
        lotsOfOrigins.add(randomCoord16);
        lotsOfOrigins.add(randomCoord17);
        lotsOfOrigins.add(randomCoord18);
        lotsOfOrigins.add(randomCoord19);
        lotsOfOrigins.add(randomCoord20);
        lotsOfOrigins.add(randomCoord21);
        lotsOfOrigins.add(randomCoord22);
        lotsOfOrigins.add(randomCoord23);
        lotsOfOrigins.add(randomCoord24);
        lotsOfOrigins.add(randomCoord25);
        lotsOfOrigins.add(randomCoord26);


        lotsOfDestinations.add(randomCoord30);
        lotsOfDestinations.add(randomCoord31);

        tooManyDestinations.add(randomCoord31);
        tooManyDestinations.add(randomCoord32);
        tooManyDestinations.add(randomCoord33);
        tooManyDestinations.add(randomCoord34);
        tooManyDestinations.add(randomCoord35);

        validParams = new MatrixApiThread(validOrigins, validDestinations, mode);
        invalidParams = new MatrixApiThread(validOrigins, crazyDestinations, mode);
        lotsOfParams = new MatrixApiThread(lotsOfOrigins, lotsOfDestinations, mode);
        tooManyParams = new MatrixApiThread(lotsOfOrigins, tooManyDestinations, mode);
    }

    @Before
    public void initThreads(){
        threadValidParams = new Thread(validParams);
        threadInvalidParams = new Thread(invalidParams);
        threadLotsOfParams = new Thread(lotsOfParams);
        threadTooManyParams = new Thread(tooManyParams);
    }

    @Test
    public void testApiInterfaceRunsOnValidInputs(){
        try {
            threadValidParams.start();
            threadInvalidParams.start();
            threadLotsOfParams.start();
            threadTooManyParams.start();
        } catch (Exception e){
            fail();
        }
    }

    @Test
    public void testApiInterfacesReturnConsistantOutput(){
        //TODO écrire meilleur test
        threadLotsOfParams.start();
        threadTooManyParams.start();
        try{
            threadLotsOfParams.join();
            threadTooManyParams.join();
        } catch (InterruptedException e){
            fail();
        } finally {
            assertTrue(lotsOfParams.getResult().length() > 500);
            assertTrue(tooManyParams.getResult().length() == 179);

            /**EXECTED Json for too long : (179 char)
             {
             "destination_addresses": [],
             "error_message": "You have exceeded your rate-limit for this API.",
             "origin_addresses": [],
             "rows": [],
             "status": "OVER_QUERY_LIMIT"
             }
             */
        }
    }
}
