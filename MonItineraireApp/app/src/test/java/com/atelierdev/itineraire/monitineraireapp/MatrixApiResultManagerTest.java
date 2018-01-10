package com.atelierdev.itineraire.monitineraireapp;

import org.json.JSONException;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Created by Arnaud on 10/01/2018.
 */

public class MatrixApiResultManagerTest {
    private String TEST_JSON_VALID = "";
    private String TEST_JSON_EMPTY = "";
    private String TEST_JSON_PARTIAL = "";
    private String TEST_JSON_INCORRECT = "";

    private MatrixApiResultManager validJsonManager;
    private MatrixApiResultManager emptyJsonManager;
    private MatrixApiResultManager partialJsonManager;
    private MatrixApiResultManager incorrectJsonManager;

    @Before
    public void initAll(){
        TEST_JSON_EMPTY = "{}";

        TEST_JSON_INCORRECT = "ad";

        TEST_JSON_PARTIAL = "{\n" +
                "    \"destination_addresses\": [\n" +
                "        \"5-7 Boulevard du Palais, 75001 Paris, France\",\n" +
                "        \"2 Rue de Rivoli, 75004 Paris, France\",\n" +
                "        \"6 Rue du Faubourg Saint-Honoré, 75008 Paris, France\"\n" +
                "    ],\n" +
                "    \"origin_addresses\": [\n" +
                "        \"1 Parvis Notre-Dame - Pl. Jean-Paul II, 75004 Paris-4E-Arrondissement, France\",\n" +
                "        \"2 Place Charles de Gaulle, 75008 Paris, France\",\n" +
                "        \"5 Avenue Anatole France, 75007 Paris, France\"\n" +
                "    ],\n" +
                "    \"rows\": [\n" +
                "        {\n" +
                "            \"elements\": [\n" +
                "                {\n" +
                "                    \"distance\": {\n" +
                "                        \"text\": \"0.4 km\",\n" +
                "                        \"value\": 360\n" +
                "                    },\n" +
                "                    \"duration\": {\n" +
                "                        \"text\": \"4 mins\",\n" +
                "                        \"value\": 266\n" +
                "                    },\n" +
                "                    \"status\": \"OK\"\n" +
                "                },\n" +
                "                {\n" +
                "                    \"distance\": {\n" +
                "                        \"text\": \"1.3 km\",\n" +
                "                        \"value\": 1268\n" +
                "                    },\n" +
                "                    \"duration\": {\n" +
                "                        \"text\": \"16 mins\",\n" +
                "                        \"value\": 973\n" +
                "                    },\n" +
                "                    \"status\": \"OK\"\n" +
                "                },\n" +
                "                {\n" +
                "                    \"distance\": {\n" +
                "                        \"text\": \"2.8 km\",\n" +
                "                        \"value\": 2848";

        TEST_JSON_VALID = "{\n" +
                "    \"destination_addresses\": [\n" +
                "        \"5-7 Boulevard du Palais, 75001 Paris, France\",\n" +
                "        \"2 Rue de Rivoli, 75004 Paris, France\",\n" +
                "        \"6 Rue du Faubourg Saint-Honoré, 75008 Paris, France\"\n" +
                "    ],\n" +
                "    \"origin_addresses\": [\n" +
                "        \"1 Parvis Notre-Dame - Pl. Jean-Paul II, 75004 Paris-4E-Arrondissement, France\",\n" +
                "        \"2 Place Charles de Gaulle, 75008 Paris, France\",\n" +
                "        \"5 Avenue Anatole France, 75007 Paris, France\"\n" +
                "    ],\n" +
                "    \"rows\": [\n" +
                "        {\n" +
                "            \"elements\": [\n" +
                "                {\n" +
                "                    \"distance\": {\n" +
                "                        \"text\": \"0.4 km\",\n" +
                "                        \"value\": 360\n" +
                "                    },\n" +
                "                    \"duration\": {\n" +
                "                        \"text\": \"4 mins\",\n" +
                "                        \"value\": 266\n" +
                "                    },\n" +
                "                    \"status\": \"OK\"\n" +
                "                },\n" +
                "                {\n" +
                "                    \"distance\": {\n" +
                "                        \"text\": \"1.3 km\",\n" +
                "                        \"value\": 1268\n" +
                "                    },\n" +
                "                    \"duration\": {\n" +
                "                        \"text\": \"16 mins\",\n" +
                "                        \"value\": 973\n" +
                "                    },\n" +
                "                    \"status\": \"OK\"\n" +
                "                },\n" +
                "                {\n" +
                "                    \"distance\": {\n" +
                "                        \"text\": \"2.8 km\",\n" +
                "                        \"value\": 2848\n" +
                "                    },\n" +
                "                    \"duration\": {\n" +
                "                        \"text\": \"37 mins\",\n" +
                "                        \"value\": 2215\n" +
                "                    },\n" +
                "                    \"status\": \"OK\"\n" +
                "                }\n" +
                "            ]\n" +
                "        },\n" +
                "        {\n" +
                "            \"elements\": [\n" +
                "                {\n" +
                "                    \"distance\": {\n" +
                "                        \"text\": \"4.8 km\",\n" +
                "                        \"value\": 4799\n" +
                "                    },\n" +
                "                    \"duration\": {\n" +
                "                        \"text\": \"1 hour 0 mins\",\n" +
                "                        \"value\": 3578\n" +
                "                    },\n" +
                "                    \"status\": \"OK\"\n" +
                "                },\n" +
                "                {\n" +
                "                    \"distance\": {\n" +
                "                        \"text\": \"5.5 km\",\n" +
                "                        \"value\": 5487\n" +
                "                    },\n" +
                "                    \"duration\": {\n" +
                "                        \"text\": \"1 hour 9 mins\",\n" +
                "                        \"value\": 4151\n" +
                "                    },\n" +
                "                    \"status\": \"OK\"\n" +
                "                },\n" +
                "                {\n" +
                "                    \"distance\": {\n" +
                "                        \"text\": \"2.3 km\",\n" +
                "                        \"value\": 2290\n" +
                "                    },\n" +
                "                    \"duration\": {\n" +
                "                        \"text\": \"28 mins\",\n" +
                "                        \"value\": 1699\n" +
                "                    },\n" +
                "                    \"status\": \"OK\"\n" +
                "                }\n" +
                "            ]\n" +
                "        },\n" +
                "        {\n" +
                "            \"elements\": [\n" +
                "                {\n" +
                "                    \"distance\": {\n" +
                "                        \"text\": \"4.2 km\",\n" +
                "                        \"value\": 4226\n" +
                "                    },\n" +
                "                    \"duration\": {\n" +
                "                        \"text\": \"54 mins\",\n" +
                "                        \"value\": 3232\n" +
                "                    },\n" +
                "                    \"status\": \"OK\"\n" +
                "                },\n" +
                "                {\n" +
                "                    \"distance\": {\n" +
                "                        \"text\": \"5.5 km\",\n" +
                "                        \"value\": 5547\n" +
                "                    },\n" +
                "                    \"duration\": {\n" +
                "                        \"text\": \"1 hour 9 mins\",\n" +
                "                        \"value\": 4143\n" +
                "                    },\n" +
                "                    \"status\": \"OK\"\n" +
                "                },\n" +
                "                {\n" +
                "                    \"distance\": {\n" +
                "                        \"text\": \"2.9 km\",\n" +
                "                        \"value\": 2906\n" +
                "                    },\n" +
                "                    \"duration\": {\n" +
                "                        \"text\": \"37 mins\",\n" +
                "                        \"value\": 2230\n" +
                "                    },\n" +
                "                    \"status\": \"OK\"\n" +
                "                }\n" +
                "            ]\n" +
                "        }\n" +
                "    ],\n" +
                "    \"status\": \"OK\"\n" +
                "}";

        validJsonManager = new MatrixApiResultManager(TEST_JSON_VALID);
        emptyJsonManager = new MatrixApiResultManager(TEST_JSON_EMPTY);
        partialJsonManager = new MatrixApiResultManager(TEST_JSON_PARTIAL);
        incorrectJsonManager = new MatrixApiResultManager(TEST_JSON_INCORRECT);
    }

    @Test
    public void testCalculTime(){
        try{
            validJsonManager.calculTime();
            List<List<Integer>> result = validJsonManager.getTimesResult();

            boolean correct = true;

            for (int i = 0; i < result.size(); i++){
                List<Integer> element = result.get(i);
                for (int j = 0; j < element.size(); j++){
                    int value = element.get(j);
                    if (value == 0){
                        correct = false;
                    }
                }
            }
            assertTrue(correct);
        } catch (Exception e) {
            fail();
        }

        try {
            emptyJsonManager.calculTime();
        } catch (Exception e){
            assertTrue(e.getClass() == JSONException.class);
        }

        try {
            partialJsonManager.calculTime();
        } catch (Exception e){
            assertTrue(e.getClass() == JSONException.class);
        }

        try {
            incorrectJsonManager.calculTime();
        } catch (Exception e){
            assertTrue(e.getClass() == JSONException.class);
        }
    }
}
