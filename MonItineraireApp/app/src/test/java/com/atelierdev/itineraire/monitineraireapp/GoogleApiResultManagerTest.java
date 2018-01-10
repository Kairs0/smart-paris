package com.atelierdev.itineraire.monitineraireapp;

import org.json.JSONException;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class GoogleApiResultManagerTest {

    private String TEST_JSON_VALID = "";
    private String TEST_JSON_EMPTY = "";
    private String TEST_JSON_PARTIAL = "";
    private String TEST_JSON_INCORRECT = "";
    private String TEST_JSON_PATH_NOT_FOUND = "";

    private GoogleApiResultManager validJsonManager;
    private GoogleApiResultManager emptyJsonManager;
    private GoogleApiResultManager partialJsonManager;
    private GoogleApiResultManager incorrectJsonManager;
    private GoogleApiResultManager pathNotFoundJsonManager;


    @Before
    public void initJsonTests(){
        TEST_JSON_EMPTY = "{}";

        TEST_JSON_INCORRECT = "ad";

        TEST_JSON_PARTIAL = "{\n" +
                "    \"geocoded_waypoints\": [\n" +
                "        {\n" +
                "            \"geocoder_status\": \"OK\",\n" +
                "            \"place_id\": \"ChIJf8cFRhF35kcR0D6LaMOCCwQ\",\n" +
                "            \"types\": [\n" +
                "                \"locality\",\n" +
                "                \"political\"\n" +
                "            ]\n" +
                "        },\n" +
                "        {\n" +
                "            \"geocoder_status\": \"OK\",\n" +
                "            \"place_id\": \"ChIJbWOHsll35kcRtDMWhakvdQo\",\n" +
                "            \"types\": [\n" +
                "                \"locality\",\n" +
                "                \"political\"\n" +
                "            ]\n" +
                "        }\n" +
                "    ],\n" +
                "    \"routes\": [\n" +
                "        {\n" +
                "            \"bounds\": {\n" +
                "                \"northeast\": {\n" +
                "                    \"lat\": 48.771837,\n" +
                "                    \"lng\": 2.3025573\n" +
                "                },\n" +
                "                \"southwest\": {\n" +
                "                    \"lat\": 48.7593013,\n" +
                "                    \"lng\": 2.268509\n" +
                "                }\n" +
                "            },\n" +
                "            \"copyrights\": \"Données cartographiques ©2018 Google\",\n" +
                "            \"legs\": [\n" +
                "                {\n" +
                "                    \"distance\": {\n" +
                "                        \"text\": \"3,5 km\",\n" +
                "                        \"value\": 3539\n" +
                "                    },\n" +
                "                    \"duration\": {\n" +
                "                        \"text\": \"47 minutes\",\n" +
                "                        \"value\": 2834\n" +
                "                    },\n" +
                "                    \"end_address\": \"92290 Châtenay-Malabry, France\",\n" +
                "                    \"end_location\": {\n" +
                "                        \"lat\": 48.7718067,\n" +
                "                        \"lng\": 2.2707166\n" +
                "                    },\n" +
                "                    \"start_address\": \"Antony, France\",\n" +
                "                    \"start_location\": {\n" +
                "                        \"lat\": 48.7593013,\n" +
                "                        \"lng\": 2.3023274\n" +
                "                    },\n" +
                "                    \"steps\": [\n" +
                "                        {\n" +
                "                            \"distance\": {\n" +
                "                                \"text\": \"49 m\",\n" +
                "                                \"value\": 49\n" +
                "                            },\n" +
                "                            \"duration\": {\n" +
                "                                \"text\": \"1 minute\",\n" +
                "                                \"value\": 36\n" +
                "                            },\n" +
                "                            \"end_location\": {\n" +
                "                                \"lat\": 48.7597174,\n" +
                "                                \"lng\": 2.3025573\n" +
                "                            },\n" +
                "                            \"html_instructions\": \"Prendre la direction <b>nord</b> sur <b>Rue d'Alsace Lorraine</b> vers <b>Rue de la Grande Couture</b>\",\n" +
                "                            \"polyline\": {\n" +
                "                                \"points\": \"shrhHqt`MCA_A_@OK\"\n" +
                "                            },\n" +
                "                            \"start_location\": {\n" +
                "                                \"lat\": 48.7593013,\n" +
                "                                \"lng\": 2.3023274\n" +
                "                            },\n" +
                "                            \"travel_mode\": \"WALKING\"\n" +
                "                        },\n" +
                "                        {\n" +
                "                            \"distance\": {\n" +
                "                                \"text\": \"85 m\",\n" +
                "                                \"value\": 85\n" +
                "                            },\n" +
                "                            \"duration\": {\n" +
                "                                \"text\": \"1 minute\",\n" +
                "                                \"value\": 65\n" +
                "                            },\n" +
                "                            \"end_location\": {\n" +
                "                                \"lat\": 48.7599527,\n" +
                "                                \"lng\": 2.3014482\n" +
                "                            },\n" +
                "                            \"html_instructions\": \"Prendre <b>à gauche</b> sur <b>Rue de la Grande Couture</b>\",\n" +
                "                            \"maneuver\": \"turn-left\",\n" +
                "                            \"polyline\": {\n" +
                "                                \"points\": \"gkrhH_v`Mm@|E\"\n" +
                "                            },\n" +
                "                            \"start_location\": {\n" +
                "                                \"lat\": 48.7597174,\n" +
                "                                \"lng\": 2.3025573\n" +
                "                            },\n" +
                "                            \"travel_mode\": \"WALKING\"\n" +
                "                        },\n" +
                "                        {\n" +
                "                            \"distance\": {\n" +
                "                                \"text\": \"24 m\",\n" +
                "                                \"value\": 24\n" +
                "                            },\n" +
                "                            \"duration\": {\n" +
                "                                \"text\": \"1 minute\",\n" +
                "                                \"value\": 18\n" +
                "                            },\n" +
                "                            \"end_location\": {\n" +
                "                                \"lat\": 48.76016389999999,\n" +
                "                                \"lng\": 2.3015288\n" +
                "                            },\n" +
                "                            \"html_instructions\": \"Prendre <b>à droite</b> sur <b>Avenue de Sceaux</b>/<b>Avenue Léon Blum</b>\",\n" +
                "                            \"maneuver\": \"turn-right\",\n" +
                "                            \"polyline\": {\n" +
                "                                \"points\": \"ulrhHao`MKAIAKECACC\"\n" +
                "                            },\n" +
                "                            \"start_location\": {\n" +
                "                                \"lat\": 48.7599527,\n" +
                "                                \"lng\": 2.3014482\n" +
                "                            },\n" +
                "                            \"travel_mode\": \"WALKING\"\n" +
                "                        },\n" +
                "                        {\n" +
                "                            \"distance\": {\n" +
                "                                \"text\": \"0,1 km\",\n" +
                "                                \"value\": 142\n" +
                "                            },\n" +
                "                            \"duration\": {\n" +
                "                                \"text\": \"2 minutes\",\n" +
                "                                \"value\": 113\n" +
                "                            },\n" +
                "                            \"end_location\": {\n" +
                "                                \"lat\": 48.76112699999999,\n" +
                "                                \"lng\": 2.300827\n" +
                "                            },\n" +
                "                            \"html_instructions\": \"Tourner à <b>gauche</b> vers <b>Rue de la Renaissance</b>\",\n" +
                "                            \"maneuver\": \"turn-left\",\n" +
                "                            \"polyline\": {\n" +
                "                                \"points\": \"_nrhHqo`MCJBZ?NCFGBE?YQQGOFMFGFMPGHIHMLKJEAIFSB\"\n" +
                "                            },\n" +
                "                            \"start_location\": {\n" +
                "                                \"lat\": 48.76016389999999,\n" +
                "                                \"lng\": 2.3015288\n" +
                "                            },\n" +
                "                            \"travel_mode\": \"WALKING\"\n" +
                "                        },\n" +
                "                        {\n" +
                "                            \"distance\": {\n" +
                "                                \"text\": \"81 m\",\n" +
                "                                \"value\": 81\n" +
                "                            },\n" +
                "                            \"duration\": {\n" +
                "                                \"text\": \"1 minute\",\n" +
                "                                \"value\": 54\n" +
                "                            },\n" +
                "                            \"end_location\": {\n" +
                "                                \"lat\": 48.7613935,\n" +
                "                                \"lng\": 2.3001864\n" +
                "                            },\n" +
                "                            \"html_instructions\": \"Continuer sur <b>Rue de la Renaissance</b>\",\n" +
                "                            \"polyline\": {\n" +
                "                                \"points\": \"atrhHek`MCAOCKAIBE?EDEDAFA@?DJdB?@\"\n" +
                "                            },\n" +
                "                            \"start_location\": {\n" +
                "                                \"lat\": 48.76112699999999,\n" +
                "                                \"lng\": 2.300827\n" +
                "                            },\n" +
                "                            \"travel_mode\": \"WALKING\"\n" +
                "                        },\n" +
                "                        {\n" +
                "                            \"distance\": {\n" +
                "                                \"text\": \"22 m\",\n" +
                "                                \"value\": 22\n" +
                "                            },\n" +
                "                            \"duration\": {";

        TEST_JSON_PATH_NOT_FOUND = "{\n" +
                "    \"geocoded_waypoints\": [\n" +
                "        {\n" +
                "            \"geocoder_status\": \"ZERO_RESULTS\"\n" +
                "        },\n" +
                "        {\n" +
                "            \"geocoder_status\": \"OK\",\n" +
                "            \"place_id\": \"ChIJIQBpAG2ahYAR_6128GcTUEo\",\n" +
                "            \"types\": [\n" +
                "                \"locality\",\n" +
                "                \"political\"\n" +
                "            ]\n" +
                "        }\n" +
                "    ],\n" +
                "    \"routes\": [],\n" +
                "    \"status\": \"NOT_FOUND\"\n" +
                "}";

        TEST_JSON_VALID = "{\n" +
                "    \"geocoded_waypoints\": [\n" +
                "        {\n" +
                "            \"geocoder_status\": \"OK\",\n" +
                "            \"place_id\": \"ChIJf8cFRhF35kcR0D6LaMOCCwQ\",\n" +
                "            \"types\": [\n" +
                "                \"locality\",\n" +
                "                \"political\"\n" +
                "            ]\n" +
                "        },\n" +
                "        {\n" +
                "            \"geocoder_status\": \"OK\",\n" +
                "            \"place_id\": \"ChIJbWOHsll35kcRtDMWhakvdQo\",\n" +
                "            \"types\": [\n" +
                "                \"locality\",\n" +
                "                \"political\"\n" +
                "            ]\n" +
                "        }\n" +
                "    ],\n" +
                "    \"routes\": [\n" +
                "        {\n" +
                "            \"bounds\": {\n" +
                "                \"northeast\": {\n" +
                "                    \"lat\": 48.771837,\n" +
                "                    \"lng\": 2.3025573\n" +
                "                },\n" +
                "                \"southwest\": {\n" +
                "                    \"lat\": 48.7593013,\n" +
                "                    \"lng\": 2.268509\n" +
                "                }\n" +
                "            },\n" +
                "            \"copyrights\": \"Données cartographiques ©2018 Google\",\n" +
                "            \"legs\": [\n" +
                "                {\n" +
                "                    \"distance\": {\n" +
                "                        \"text\": \"3,5 km\",\n" +
                "                        \"value\": 3539\n" +
                "                    },\n" +
                "                    \"duration\": {\n" +
                "                        \"text\": \"47 minutes\",\n" +
                "                        \"value\": 2834\n" +
                "                    },\n" +
                "                    \"end_address\": \"92290 Châtenay-Malabry, France\",\n" +
                "                    \"end_location\": {\n" +
                "                        \"lat\": 48.7718067,\n" +
                "                        \"lng\": 2.2707166\n" +
                "                    },\n" +
                "                    \"start_address\": \"Antony, France\",\n" +
                "                    \"start_location\": {\n" +
                "                        \"lat\": 48.7593013,\n" +
                "                        \"lng\": 2.3023274\n" +
                "                    },\n" +
                "                    \"steps\": [\n" +
                "                        {\n" +
                "                            \"distance\": {\n" +
                "                                \"text\": \"49 m\",\n" +
                "                                \"value\": 49\n" +
                "                            },\n" +
                "                            \"duration\": {\n" +
                "                                \"text\": \"1 minute\",\n" +
                "                                \"value\": 36\n" +
                "                            },\n" +
                "                            \"end_location\": {\n" +
                "                                \"lat\": 48.7597174,\n" +
                "                                \"lng\": 2.3025573\n" +
                "                            },\n" +
                "                            \"html_instructions\": \"Prendre la direction <b>nord</b> sur <b>Rue d'Alsace Lorraine</b> vers <b>Rue de la Grande Couture</b>\",\n" +
                "                            \"polyline\": {\n" +
                "                                \"points\": \"shrhHqt`MCA_A_@OK\"\n" +
                "                            },\n" +
                "                            \"start_location\": {\n" +
                "                                \"lat\": 48.7593013,\n" +
                "                                \"lng\": 2.3023274\n" +
                "                            },\n" +
                "                            \"travel_mode\": \"WALKING\"\n" +
                "                        },\n" +
                "                        {\n" +
                "                            \"distance\": {\n" +
                "                                \"text\": \"85 m\",\n" +
                "                                \"value\": 85\n" +
                "                            },\n" +
                "                            \"duration\": {\n" +
                "                                \"text\": \"1 minute\",\n" +
                "                                \"value\": 65\n" +
                "                            },\n" +
                "                            \"end_location\": {\n" +
                "                                \"lat\": 48.7599527,\n" +
                "                                \"lng\": 2.3014482\n" +
                "                            },\n" +
                "                            \"html_instructions\": \"Prendre <b>à gauche</b> sur <b>Rue de la Grande Couture</b>\",\n" +
                "                            \"maneuver\": \"turn-left\",\n" +
                "                            \"polyline\": {\n" +
                "                                \"points\": \"gkrhH_v`Mm@|E\"\n" +
                "                            },\n" +
                "                            \"start_location\": {\n" +
                "                                \"lat\": 48.7597174,\n" +
                "                                \"lng\": 2.3025573\n" +
                "                            },\n" +
                "                            \"travel_mode\": \"WALKING\"\n" +
                "                        },\n" +
                "                        {\n" +
                "                            \"distance\": {\n" +
                "                                \"text\": \"24 m\",\n" +
                "                                \"value\": 24\n" +
                "                            },\n" +
                "                            \"duration\": {\n" +
                "                                \"text\": \"1 minute\",\n" +
                "                                \"value\": 18\n" +
                "                            },\n" +
                "                            \"end_location\": {\n" +
                "                                \"lat\": 48.76016389999999,\n" +
                "                                \"lng\": 2.3015288\n" +
                "                            },\n" +
                "                            \"html_instructions\": \"Prendre <b>à droite</b> sur <b>Avenue de Sceaux</b>/<b>Avenue Léon Blum</b>\",\n" +
                "                            \"maneuver\": \"turn-right\",\n" +
                "                            \"polyline\": {\n" +
                "                                \"points\": \"ulrhHao`MKAIAKECACC\"\n" +
                "                            },\n" +
                "                            \"start_location\": {\n" +
                "                                \"lat\": 48.7599527,\n" +
                "                                \"lng\": 2.3014482\n" +
                "                            },\n" +
                "                            \"travel_mode\": \"WALKING\"\n" +
                "                        },\n" +
                "                        {\n" +
                "                            \"distance\": {\n" +
                "                                \"text\": \"0,1 km\",\n" +
                "                                \"value\": 142\n" +
                "                            },\n" +
                "                            \"duration\": {\n" +
                "                                \"text\": \"2 minutes\",\n" +
                "                                \"value\": 113\n" +
                "                            },\n" +
                "                            \"end_location\": {\n" +
                "                                \"lat\": 48.76112699999999,\n" +
                "                                \"lng\": 2.300827\n" +
                "                            },\n" +
                "                            \"html_instructions\": \"Tourner à <b>gauche</b> vers <b>Rue de la Renaissance</b>\",\n" +
                "                            \"maneuver\": \"turn-left\",\n" +
                "                            \"polyline\": {\n" +
                "                                \"points\": \"_nrhHqo`MCJBZ?NCFGBE?YQQGOFMFGFMPGHIHMLKJEAIFSB\"\n" +
                "                            },\n" +
                "                            \"start_location\": {\n" +
                "                                \"lat\": 48.76016389999999,\n" +
                "                                \"lng\": 2.3015288\n" +
                "                            },\n" +
                "                            \"travel_mode\": \"WALKING\"\n" +
                "                        },\n" +
                "                        {\n" +
                "                            \"distance\": {\n" +
                "                                \"text\": \"81 m\",\n" +
                "                                \"value\": 81\n" +
                "                            },\n" +
                "                            \"duration\": {\n" +
                "                                \"text\": \"1 minute\",\n" +
                "                                \"value\": 54\n" +
                "                            },\n" +
                "                            \"end_location\": {\n" +
                "                                \"lat\": 48.7613935,\n" +
                "                                \"lng\": 2.3001864\n" +
                "                            },\n" +
                "                            \"html_instructions\": \"Continuer sur <b>Rue de la Renaissance</b>\",\n" +
                "                            \"polyline\": {\n" +
                "                                \"points\": \"atrhHek`MCAOCKAIBE?EDEDAFA@?DJdB?@\"\n" +
                "                            },\n" +
                "                            \"start_location\": {\n" +
                "                                \"lat\": 48.76112699999999,\n" +
                "                                \"lng\": 2.300827\n" +
                "                            },\n" +
                "                            \"travel_mode\": \"WALKING\"\n" +
                "                        },\n" +
                "                        {\n" +
                "                            \"distance\": {\n" +
                "                                \"text\": \"22 m\",\n" +
                "                                \"value\": 22\n" +
                "                            },\n" +
                "                            \"duration\": {\n" +
                "                                \"text\": \"1 minute\",\n" +
                "                                \"value\": 16\n" +
                "                            },\n" +
                "                            \"end_location\": {\n" +
                "                                \"lat\": 48.7615843,\n" +
                "                                \"lng\": 2.3000933\n" +
                "                            },\n" +
                "                            \"html_instructions\": \"Tourner à <b>droite</b> pour rester sur <b>Rue de la Renaissance</b>\",\n" +
                "                            \"maneuver\": \"turn-right\",\n" +
                "                            \"polyline\": {\n" +
                "                                \"points\": \"uurhHeg`Me@R\"\n" +
                "                            },\n" +
                "                            \"start_location\": {\n" +
                "                                \"lat\": 48.7613935,\n" +
                "                                \"lng\": 2.3001864\n" +
                "                            },\n" +
                "                            \"travel_mode\": \"WALKING\"\n" +
                "                        },\n" +
                "                        {\n" +
                "                            \"distance\": {\n" +
                "                                \"text\": \"0,1 km\",\n" +
                "                                \"value\": 104\n" +
                "                            },\n" +
                "                            \"duration\": {\n" +
                "                                \"text\": \"1 minute\",\n" +
                "                                \"value\": 78\n" +
                "                            },\n" +
                "                            \"end_location\": {\n" +
                "                                \"lat\": 48.7612874,\n" +
                "                                \"lng\": 2.2987485\n" +
                "                            },\n" +
                "                            \"html_instructions\": \"Tourner à <b>gauche</b> pour rester sur <b>Rue de la Renaissance</b>\",\n" +
                "                            \"maneuver\": \"turn-left\",\n" +
                "                            \"polyline\": {\n" +
                "                                \"points\": \"{vrhHqf`MDh@d@fDHd@BR\"\n" +
                "                            },\n" +
                "                            \"start_location\": {\n" +
                "                                \"lat\": 48.7615843,\n" +
                "                                \"lng\": 2.3000933\n" +
                "                            },\n" +
                "                            \"travel_mode\": \"WALKING\"\n" +
                "                        },\n" +
                "                        {\n" +
                "                            \"distance\": {\n" +
                "                                \"text\": \"21 m\",\n" +
                "                                \"value\": 21\n" +
                "                            },\n" +
                "                            \"duration\": {\n" +
                "                                \"text\": \"1 minute\",\n" +
                "                                \"value\": 17\n" +
                "                            },\n" +
                "                            \"end_location\": {\n" +
                "                                \"lat\": 48.7614387,\n" +
                "                                \"lng\": 2.2985685\n" +
                "                            },\n" +
                "                            \"html_instructions\": \"Tourner à <b>droite</b> pour rester sur <b>Rue de la Renaissance</b>\",\n" +
                "                            \"maneuver\": \"turn-right\",\n" +
                "                            \"polyline\": {\n" +
                "                                \"points\": \"aurhHe~_MQTKL\"\n" +
                "                            },\n" +
                "                            \"start_location\": {\n" +
                "                                \"lat\": 48.7612874,\n" +
                "                                \"lng\": 2.2987485\n" +
                "                            },\n" +
                "                            \"travel_mode\": \"WALKING\"\n" +
                "                        },\n" +
                "                        {\n" +
                "                            \"distance\": {\n" +
                "                                \"text\": \"0,5 km\",\n" +
                "                                \"value\": 538\n" +
                "                            },\n" +
                "                            \"duration\": {\n" +
                "                                \"text\": \"8 minutes\",\n" +
                "                                \"value\": 460\n" +
                "                            },\n" +
                "                            \"end_location\": {\n" +
                "                                \"lat\": 48.7612546,\n" +
                "                                \"lng\": 2.2916874\n" +
                "                            },\n" +
                "                            \"html_instructions\": \"Prendre <b>à gauche</b> sur <b>Avenue du Général de Gaulle</b>/<b>D986</b>\",\n" +
                "                            \"maneuver\": \"turn-left\",\n" +
                "                            \"polyline\": {\n" +
                "                                \"points\": \"_vrhHa}_MDTLr@F^Hf@F^D\\\\DLD^PfBVbC@JHp@PbA`@rB@J@H@L@VATALCJ?BEPa@tASt@ETEPCPGXGZCRAPIt@Gj@OxB\"\n" +
                "                            },\n" +
                "                            \"start_location\": {\n" +
                "                                \"lat\": 48.7614387,\n" +
                "                                \"lng\": 2.2985685\n" +
                "                            },\n" +
                "                            \"travel_mode\": \"WALKING\"\n" +
                "                        },\n" +
                "                        {\n" +
                "                            \"distance\": {\n" +
                "                                \"text\": \"0,7 km\",\n" +
                "                                \"value\": 669\n" +
                "                            },\n" +
                "                            \"duration\": {\n" +
                "                                \"text\": \"9 minutes\",\n" +
                "                                \"value\": 562\n" +
                "                            },\n" +
                "                            \"end_location\": {\n" +
                "                                \"lat\": 48.7624553,\n" +
                "                                \"lng\": 2.2828599\n" +
                "                            },\n" +
                "                            \"html_instructions\": \"Tourner légèrement à <b>droite</b> pour continuer sur <b>Avenue du Général de Gaulle</b>/<b>D986</b><div style=\\\"font-size:0.9em\\\">Continuer de suivre D986</div><div style=\\\"font-size:0.9em\\\">Traverser le rond-point</div>\",\n" +
                "                            \"maneuver\": \"turn-slight-right\",\n" +
                "                            \"polyline\": {\n" +
                "                                \"points\": \"ytrhHar~LIROv@?@A?A@A@?@A??@A@?@A@?@?@A@?@?@?@A@?@?@?@?@?@?@?@?@@@?@?@?B@@?@?@@??@@@?@@??@@?EpBcBdVG`AY`ECZCX?LEd@k@lIEd@GX\"\n" +
                "                            },\n" +
                "                            \"start_location\": {\n" +
                "                                \"lat\": 48.7612546,\n" +
                "                                \"lng\": 2.2916874\n" +
                "                            },\n" +
                "                            \"travel_mode\": \"WALKING\"\n" +
                "                        },\n" +
                "                        {\n" +
                "                            \"distance\": {\n" +
                "                                \"text\": \"0,3 km\",\n" +
                "                                \"value\": 259\n" +
                "                            },\n" +
                "                            \"duration\": {\n" +
                "                                \"text\": \"3 minutes\",\n" +
                "                                \"value\": 205\n" +
                "                            },\n" +
                "                            \"end_location\": {\n" +
                "                                \"lat\": 48.7645761,\n" +
                "                                \"lng\": 2.2814567\n" +
                "                            },\n" +
                "                            \"html_instructions\": \"Au rond-point, prendre la <b>1re</b> sortie sur <b>Rue Vincent Fayo</b>/<b>D128</b>\",\n" +
                "                            \"maneuver\": \"roundabout-right\",\n" +
                "                            \"polyline\": {\n" +
                "                                \"points\": \"k|rhH{z|LA?A??@A??@A??@A??@GAI@IBKDGBA@EH}A`AMHsAx@y@d@o@`@{@f@\"\n" +
                "                            },\n" +
                "                            \"start_location\": {\n" +
                "                                \"lat\": 48.7624553,\n" +
                "                                \"lng\": 2.2828599\n" +
                "                            },\n" +
                "                            \"travel_mode\": \"WALKING\"\n" +
                "                        },\n" +
                "                        {\n" +
                "                            \"distance\": {\n" +
                "                                \"text\": \"52 m\",\n" +
                "                                \"value\": 52\n" +
                "                            },\n" +
                "                            \"duration\": {\n" +
                "                                \"text\": \"1 minute\",\n" +
                "                                \"value\": 47\n" +
                "                            },\n" +
                "                            \"end_location\": {\n" +
                "                                \"lat\": 48.7650034,\n" +
                "                                \"lng\": 2.2812056\n" +
                "                            },\n" +
                "                            \"html_instructions\": \"Tourner légèrement à <b>gauche</b> pour rester sur <b>Rue Vincent Fayo</b>/<b>D128</b>\",\n" +
                "                            \"maneuver\": \"turn-slight-left\",\n" +
                "                            \"polyline\": {\n" +
                "                                \"points\": \"sishHcr|LINYPSJ[B\"\n" +
                "                            },\n" +
                "                            \"start_location\": {\n" +
                "                                \"lat\": 48.7645761,\n" +
                "                                \"lng\": 2.2814567\n" +
                "                            },\n" +
                "                            \"travel_mode\": \"WALKING\"\n" +
                "                        },\n" +
                "                        {\n" +
                "                            \"distance\": {\n" +
                "                                \"text\": \"0,1 km\",\n" +
                "                                \"value\": 117\n" +
                "                            },\n" +
                "                            \"duration\": {\n" +
                "                                \"text\": \"2 minutes\",\n" +
                "                                \"value\": 100\n" +
                "                            },\n" +
                "                            \"end_location\": {\n" +
                "                                \"lat\": 48.7657872,\n" +
                "                                \"lng\": 2.280171\n" +
                "                            },\n" +
                "                            \"html_instructions\": \"Prendre <b>à gauche</b> sur <b>Rue Garnier</b>\",\n" +
                "                            \"maneuver\": \"turn-left\",\n" +
                "                            \"polyline\": {\n" +
                "                                \"points\": \"glshHqp|L?DAB?@ADEDEFk@r@aBzB\"\n" +
                "                            },\n" +
                "                            \"start_location\": {\n" +
                "                                \"lat\": 48.7650034,\n" +
                "                                \"lng\": 2.2812056\n" +
                "                            },\n" +
                "                            \"travel_mode\": \"WALKING\"\n" +
                "                        },\n" +
                "                        {\n" +
                "                            \"distance\": {\n" +
                "                                \"text\": \"0,1 km\",\n" +
                "                                \"value\": 131\n" +
                "                            },\n" +
                "                            \"duration\": {\n" +
                "                                \"text\": \"2 minutes\",\n" +
                "                                \"value\": 115\n" +
                "                            },\n" +
                "                            \"end_location\": {\n" +
                "                                \"lat\": 48.7667003,\n" +
                "                                \"lng\": 2.2792258\n" +
                "                            },\n" +
                "                            \"html_instructions\": \"Continuer sur <b>Rue de l'Église</b>\",\n" +
                "                            \"polyline\": {\n" +
                "                                \"points\": \"eqshHaj|LMNSLEDGFEHELERAJ?JAD?B?@A@A@CBIBIDE@EFSPGDC@GBG@K@K@\"\n" +
                "                            },\n" +
                "                            \"start_location\": {\n" +
                "                                \"lat\": 48.7657872,\n" +
                "                                \"lng\": 2.280171\n" +
                "                            },\n" +
                "                            \"travel_mode\": \"WALKING\"\n" +
                "                        },\n" +
                "                        {\n" +
                "                            \"distance\": {\n" +
                "                                \"text\": \"0,2 km\",\n" +
                "                                \"value\": 223\n" +
                "                            },\n" +
                "                            \"duration\": {\n" +
                "                                \"text\": \"3 minutes\",\n" +
                "                                \"value\": 181\n" +
                "                            },\n" +
                "                            \"end_location\": {\n" +
                "                                \"lat\": 48.76699199999999,\n" +
                "                                \"lng\": 2.276257\n" +
                "                            },\n" +
                "                            \"html_instructions\": \"Prendre <b>à gauche</b> sur <b>Rue du Dr le Savoureux</b>\",\n" +
                "                            \"maneuver\": \"turn-left\",\n" +
                "                            \"polyline\": {\n" +
                "                                \"points\": \"{vshHed|LAjB?TATCh@In@O~@Kv@EVERCd@AJAZ@h@@PBp@\"\n" +
                "                            },\n" +
                "                            \"start_location\": {\n" +
                "                                \"lat\": 48.7667003,\n" +
                "                                \"lng\": 2.2792258\n" +
                "                            },\n" +
                "                            \"travel_mode\": \"WALKING\"\n" +
                "                        },\n" +
                "                        {\n" +
                "                            \"distance\": {\n" +
                "                                \"text\": \"0,4 km\",\n" +
                "                                \"value\": 366\n" +
                "                            },\n" +
                "                            \"duration\": {\n" +
                "                                \"text\": \"5 minutes\",\n" +
                "                                \"value\": 305\n" +
                "                            },\n" +
                "                            \"end_location\": {\n" +
                "                                \"lat\": 48.7679257,\n" +
                "                                \"lng\": 2.2718766\n" +
                "                            },\n" +
                "                            \"html_instructions\": \"Tourner à <b>droite</b> pour rester sur <b>Rue du Dr le Savoureux</b>\",\n" +
                "                            \"maneuver\": \"turn-right\",\n" +
                "                            \"polyline\": {\n" +
                "                                \"points\": \"uxshHsq{LOHy@~@KNEHERg@bBg@fBABGZMt@IpAGfA?F?TAFD`@Fd@@THt@FzA?`@\"\n" +
                "                            },\n" +
                "                            \"start_location\": {\n" +
                "                                \"lat\": 48.76699199999999,\n" +
                "                                \"lng\": 2.276257\n" +
                "                            },\n" +
                "                            \"travel_mode\": \"WALKING\"\n" +
                "                        },\n" +
                "                        {\n" +
                "                            \"distance\": {\n" +
                "                                \"text\": \"0,2 km\",\n" +
                "                                \"value\": 232\n" +
                "                            },\n" +
                "                            \"duration\": {\n" +
                "                                \"text\": \"3 minutes\",\n" +
                "                                \"value\": 159\n" +
                "                            },\n" +
                "                            \"end_location\": {\n" +
                "                                \"lat\": 48.7696494,\n" +
                "                                \"lng\": 2.2701659\n" +
                "                            },\n" +
                "                            \"html_instructions\": \"Prendre <b>à droite</b> sur <b>Rue de Chateaubriand</b>\",\n" +
                "                            \"maneuver\": \"turn-right\",\n" +
                "                            \"polyline\": {\n" +
                "                                \"points\": \"q~shHgvzLOVc@z@Wb@q@v@o@j@oBxAy@^\"\n" +
                "                            },\n" +
                "                            \"start_location\": {\n" +
                "                                \"lat\": 48.7679257,\n" +
                "                                \"lng\": 2.2718766\n" +
                "                            },\n" +
                "                            \"travel_mode\": \"WALKING\"\n" +
                "                        },\n" +
                "                        {\n" +
                "                            \"distance\": {\n" +
                "                                \"text\": \"5 m\",\n" +
                "                                \"value\": 5\n" +
                "                            },\n" +
                "                            \"duration\": {\n" +
                "                                \"text\": \"1 minute\",\n" +
                "                                \"value\": 5\n" +
                "                            },\n" +
                "                            \"end_location\": {\n" +
                "                                \"lat\": 48.7696402,\n" +
                "                                \"lng\": 2.2701046\n" +
                "                            },\n" +
                "                            \"html_instructions\": \"Prendre <b>à gauche</b> sur <b>Avenue Jean Jaurès</b>\",\n" +
                "                            \"maneuver\": \"turn-left\",\n" +
                "                            \"polyline\": {\n" +
                "                                \"points\": \"iithHqkzL@L\"\n" +
                "                            },\n" +
                "                            \"start_location\": {\n" +
                "                                \"lat\": 48.7696494,\n" +
                "                                \"lng\": 2.2701659\n" +
                "                            },\n" +
                "                            \"travel_mode\": \"WALKING\"\n" +
                "                        },\n" +
                "                        {\n" +
                "                            \"distance\": {\n" +
                "                                \"text\": \"0,2 km\",\n" +
                "                                \"value\": 231\n" +
                "                            },\n" +
                "                            \"duration\": {\n" +
                "                                \"text\": \"3 minutes\",\n" +
                "                                \"value\": 162\n" +
                "                            },\n" +
                "                            \"end_location\": {\n" +
                "                                \"lat\": 48.7713812,\n" +
                "                                \"lng\": 2.268509\n" +
                "                            },\n" +
                "                            \"html_instructions\": \"Prendre <b>à droite</b> sur <b>Rue de Chateaubriand</b>\",\n" +
                "                            \"maneuver\": \"turn-right\",\n" +
                "                            \"polyline\": {\n" +
                "                                \"points\": \"githHckzLcBj@SFSJWPUPYZSVUXW\\\\ORU^MTO^AB\"\n" +
                "                            },\n" +
                "                            \"start_location\": {\n" +
                "                                \"lat\": 48.7696402,\n" +
                "                                \"lng\": 2.2701046\n" +
                "                            },\n" +
                "                            \"travel_mode\": \"WALKING\"\n" +
                "                        },\n" +
                "                        {\n" +
                "                            \"distance\": {\n" +
                "                                \"text\": \"6 m\",\n" +
                "                                \"value\": 6\n" +
                "                            },\n" +
                "                            \"duration\": {\n" +
                "                                \"text\": \"1 minute\",\n" +
                "                                \"value\": 9\n" +
                "                            },\n" +
                "                            \"end_location\": {\n" +
                "                                \"lat\": 48.7714217,\n" +
                "                                \"lng\": 2.2685666\n" +
                "                            },\n" +
                "                            \"html_instructions\": \"Tourner à <b>droite</b>\",\n" +
                "                            \"maneuver\": \"turn-right\",\n" +
                "                            \"polyline\": {\n" +
                "                                \"points\": \"ctthHeazLGK\"\n" +
                "                            },\n" +
                "                            \"start_location\": {\n" +
                "                                \"lat\": 48.7713812,\n" +
                "                                \"lng\": 2.268509\n" +
                "                            },\n" +
                "                            \"travel_mode\": \"WALKING\"\n" +
                "                        },\n" +
                "                        {\n" +
                "                            \"distance\": {\n" +
                "                                \"text\": \"25 m\",\n" +
                "                                \"value\": 25\n" +
                "                            },\n" +
                "                            \"duration\": {\n" +
                "                                \"text\": \"1 minute\",\n" +
                "                                \"value\": 19\n" +
                "                            },\n" +
                "                            \"end_location\": {\n" +
                "                                \"lat\": 48.7714125,\n" +
                "                                \"lng\": 2.2688968\n" +
                "                            },\n" +
                "                            \"html_instructions\": \"Tourner à <b>droite</b>\",\n" +
                "                            \"maneuver\": \"turn-right\",\n" +
                "                            \"polyline\": {\n" +
                "                                \"points\": \"ktthHqazL@G@G@C?CACAe@\"\n" +
                "                            },\n" +
                "                            \"start_location\": {\n" +
                "                                \"lat\": 48.7714217,\n" +
                "                                \"lng\": 2.2685666\n" +
                "                            },\n" +
                "                            \"travel_mode\": \"WALKING\"\n" +
                "                        },\n" +
                "                        {\n" +
                "                            \"distance\": {\n" +
                "                                \"text\": \"78 m\",\n" +
                "                                \"value\": 78\n" +
                "                            },\n" +
                "                            \"duration\": {\n" +
                "                                \"text\": \"1 minute\",\n" +
                "                                \"value\": 52\n" +
                "                            },\n" +
                "                            \"end_location\": {\n" +
                "                                \"lat\": 48.771837,\n" +
                "                                \"lng\": 2.269732\n" +
                "                            },\n" +
                "                            \"html_instructions\": \"Tourner à <b>gauche</b>\",\n" +
                "                            \"maneuver\": \"turn-left\",\n" +
                "                            \"polyline\": {\n" +
                "                                \"points\": \"itthHsczLIGEEUa@O_@CK[iA\"\n" +
                "                            },\n" +
                "                            \"start_location\": {\n" +
                "                                \"lat\": 48.7714125,\n" +
                "                                \"lng\": 2.2688968\n" +
                "                            },\n" +
                "                            \"travel_mode\": \"WALKING\"\n" +
                "                        },\n" +
                "                        {\n" +
                "                            \"distance\": {\n" +
                "                                \"text\": \"79 m\",\n" +
                "                                \"value\": 79\n" +
                "                            },\n" +
                "                            \"duration\": {\n" +
                "                                \"text\": \"1 minute\",\n" +
                "                                \"value\": 56\n" +
                "                            },\n" +
                "                            \"end_location\": {\n" +
                "                                \"lat\": 48.7718067,\n" +
                "                                \"lng\": 2.2707166\n" +
                "                            },\n" +
                "                            \"html_instructions\": \"Tourner à <b>droite</b>\",\n" +
                "                            \"maneuver\": \"turn-right\",\n" +
                "                            \"polyline\": {\n" +
                "                                \"points\": \"_wthHyhzLHYJ[BS?MGQEKEQAQ?Q@U?C\"\n" +
                "                            },\n" +
                "                            \"start_location\": {\n" +
                "                                \"lat\": 48.771837,\n" +
                "                                \"lng\": 2.269732\n" +
                "                            },\n" +
                "                            \"travel_mode\": \"WALKING\"\n" +
                "                        }\n" +
                "                    ],\n" +
                "                    \"traffic_speed_entry\": [],\n" +
                "                    \"via_waypoint\": []\n" +
                "                }\n" +
                "            ],\n" +
                "            \"overview_polyline\": {\n" +
                "                \"points\": \"shrhHqt`McAa@OKm@|EUCOGCCCJBj@KJ_@QQGOFUNm@r@KJEAIFSBSEU@KDGLHnBe@RDh@n@lEBRQTKLDTTrAPfAJj@VfCXnCZtBf@vC?l@EX{@`DWrAWvBWdDIROv@A@A@ABCBCJALBVBB@BCpB_E`l@M~@A?A@C@A@Q@_@NEH}A`AaBbAiBfA{@f@INm@\\\\[B?DADGJaDfEYRMPK`@AVCLYNg@`@_@HK@AjBAj@MxA[vBKj@Ep@?dADbAOHy@~@QXwAbFUpAQxC?\\\\Bh@Hz@Ht@FzA?`@OV{@~AaBbBoBxAy@^@LcBj@g@Rm@b@m@r@sAjB]t@ABGKBO@GCi@OMe@aA_@uATu@Ba@M]Gc@@k@\"\n" +
                "            },\n" +
                "            \"summary\": \"Avenue de la Division Leclerc/D986\",\n" +
                "            \"warnings\": [\n" +
                "                \"Le calcul d'itinéraires piétons est en bêta. Faites attention – Cet itinéraire n'est peut-être pas complètement aménagé pour les piétons.\"\n" +
                "            ],\n" +
                "            \"waypoint_order\": []\n" +
                "        }\n" +
                "    ],\n" +
                "    \"status\": \"OK\"\n" +
                "}";

    }

    @Before
    public void initManagers(){
        validJsonManager = new GoogleApiResultManager(TEST_JSON_VALID);
        emptyJsonManager = new GoogleApiResultManager(TEST_JSON_EMPTY);
        partialJsonManager = new GoogleApiResultManager(TEST_JSON_PARTIAL);
        incorrectJsonManager = new GoogleApiResultManager(TEST_JSON_INCORRECT);
        pathNotFoundJsonManager = new GoogleApiResultManager(TEST_JSON_PATH_NOT_FOUND);
    }

    @Test
    public void testCalculTime(){
        validJsonManager.calculTime();
        emptyJsonManager.calculTime();
        partialJsonManager.calculTime();
        incorrectJsonManager.calculTime();
        pathNotFoundJsonManager.calculTime();

        assertTrue(validJsonManager.getDurationInSeconds() > 0);
        assertTrue(emptyJsonManager.getDurationInSeconds() == 0);
        assertTrue(partialJsonManager.getDurationInSeconds() == 0);
        assertTrue(incorrectJsonManager.getDurationInSeconds() == 0);
        assertTrue(pathNotFoundJsonManager.getDurationInSeconds() == 0);
    }

    @Test
    public void testManageCoordinatesOnValidJson(){
        try{
            validJsonManager.ManageCoordinates();
            assertTrue(validJsonManager.getCoordinatesLatLng().size() > 0);
        } catch (Exception e){
            fail();
        }
    }

    @Test
    public void testManageCoordinatesOnIncorrectJson(){
        try{
            emptyJsonManager.ManageCoordinates();
        } catch (Exception e){
            assertTrue(e.getClass() == JSONException.class);
        }
        try{
            partialJsonManager.ManageCoordinates();
        } catch (Exception e){
            assertTrue(e.getClass() == JSONException.class);
        }
        try{
            incorrectJsonManager.ManageCoordinates();
        } catch (Exception e){
            assertTrue(e.getClass() == JSONException.class);
        }
        try{
            pathNotFoundJsonManager.ManageCoordinates();
        } catch (Exception e){
            assertTrue(e.getClass() == JSONException.class);
        }
    }

    @Test
    public void testManageTextInstructions(){
        validJsonManager.ManageTextInstructions();
        emptyJsonManager.ManageTextInstructions();
        partialJsonManager.ManageTextInstructions();
        incorrectJsonManager.ManageTextInstructions();
        pathNotFoundJsonManager.ManageTextInstructions();

        assertTrue(emptyJsonManager.getInstructionsResult().size() > 0);
        assertTrue(emptyJsonManager.getInstructionsResult().get(0).equals("Pas d'itinéraire"));
        assertTrue(partialJsonManager.getInstructionsResult().get(0).equals("Pas d'itinéraire"));
        assertTrue(incorrectJsonManager.getInstructionsResult().get(0).equals("Pas d'itinéraire"));
        assertTrue(pathNotFoundJsonManager.getInstructionsResult().get(0).equals("Pas d'itinéraire"));
    }

    @Test
    public void testGetStatus(){
        try {
            assertTrue(validJsonManager.getStatus().equals("OK"));
        }
        catch (JSONException e){
            fail();
        }
        try {
            emptyJsonManager.getStatus();
        } catch (JSONException e){
            assertTrue(e.getClass() == JSONException.class);
        }
        try {
            partialJsonManager.getStatus();
        } catch (JSONException e){
            assertTrue(e.getClass() == JSONException.class);
        }
        try {
            incorrectJsonManager.getStatus();
        } catch (JSONException e){
            assertTrue(e.getClass() == JSONException.class);
        }
        try {
            assertTrue(pathNotFoundJsonManager.getStatus().equals("NOT_FOUND"));
        } catch (JSONException e){
            fail();
        }
    }
}
