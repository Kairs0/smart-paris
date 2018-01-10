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

    private final String destinationOne = "Paris";
    private final String destinationTwo = "Chatenay%20Malabry";
    private final String destinationThree = "Antony";
    private final String destinationFour = "37.421998333333335,-122.08400000000002"; //California
    private final String destinationFive = "33.4489,70.6693"; //Santiago de Chile
    private final String destinationSix = "48.7018,2.1341"; //Gif sur Yvette

    private final String mode = "walking";

    private MatrixApiThread validParams;
    private MatrixApiThread invalidParams;

    private Thread threadValidParams;
    private Thread threadInvalidParams;

    @Before
    public void initApiInterfaces(){
        List<String> validOrigins = new ArrayList<>(Arrays.asList(destinationOne, destinationTwo));
        List<String> validDestinations = new ArrayList<>(Arrays.asList(destinationThree, destinationSix));

        List<String> crazyDestinations = new ArrayList<>(Arrays.asList(destinationFour, destinationFive, destinationSix));
        validParams = new MatrixApiThread(validOrigins, validDestinations, mode);
        invalidParams = new MatrixApiThread(validOrigins, crazyDestinations, mode);
    }

    @Before
    public void initThreads(){
        threadValidParams = new Thread(validParams);
        threadInvalidParams = new Thread(invalidParams);
    }

    @Test
    public void testApiInterfaceRunsOnValidInputs(){
        try {
            threadValidParams.start();
            threadInvalidParams.start();
        } catch (Exception e){
            fail();
        }
    }

    @Test
    public void testApiInterfacesReturnConsistantOutput(){
        //TODO écrire meilleur test
        threadValidParams.start();
        threadInvalidParams.start();

        try{
            threadValidParams.join();
            threadInvalidParams.join();
        } catch (InterruptedException e){
            fail();
        } finally {
            assertTrue(validParams.getResult().length() > 500);
            assertTrue(invalidParams.getResult().length() > 10);
        }
    }
}
