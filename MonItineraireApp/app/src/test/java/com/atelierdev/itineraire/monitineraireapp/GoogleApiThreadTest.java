package com.atelierdev.itineraire.monitineraireapp;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

public class GoogleApiThreadTest {

    private final String destinationOne = "Paris";
    private final String destinationTwo = "Chatenay%20Malabry";
    private final String destinationThree = "Antony";
    private final String destinationFour = "37.421998333333335,-122.08400000000002"; //California
    private final String destinationFive = "33.4489,70.6693"; //Santiago de Chile
    private final String destinationSix = "48.7018, 2.1341"; //Gif sur Yvette

    private final String mode = "walking";

    private GoogleApiThread pathDirect;
    private GoogleApiThread pathWithWayPoint;
    private GoogleApiThread pathWithWayPoints;
    private GoogleApiThread noPath;

    private Thread threadPathDirect;
    private Thread threadPathWithWaypoint;
    private Thread threadPathWithWaypoints;
    private Thread threadNoPath;


    @Before
    public void initApiInterfaces(){
        List<String> noWaypoint = new ArrayList<>();

        List<String> oneWayPoint = new ArrayList<>(
            Arrays.asList(destinationThree));

        List<String> twoWayPoints = new ArrayList<>(
                Arrays.asList(destinationTwo, destinationThree));

        pathDirect = new GoogleApiThread(destinationThree, destinationSix, mode, noWaypoint);
        pathWithWayPoint = new GoogleApiThread(destinationOne, destinationTwo, mode, oneWayPoint);
        pathWithWayPoints = new GoogleApiThread(destinationOne, destinationSix, mode, twoWayPoints);
        noPath = new GoogleApiThread(destinationFour, destinationFive, mode, noWaypoint);
    }

    @Before
    public void initThreads(){
        threadPathDirect = new Thread(pathDirect);
        threadPathWithWaypoint = new Thread(pathWithWayPoint);
        threadPathWithWaypoints = new Thread(pathWithWayPoints);
        threadNoPath = new Thread(noPath);
    }

    @Test
    public void testRunOnAnyValidInput(){
        try {
            threadPathDirect.start();
            threadPathWithWaypoint.start();
            threadPathWithWaypoints.start();
            threadNoPath.start();
        } catch (Exception e){
            fail();
        }
    }

    @Test
    public void testApiInterfacesReturnConsistantOutput(){
        threadPathDirect.start();
        try{
            threadPathDirect.join();
        } catch (InterruptedException e){
            fail();
        } finally {
            assertTrue(pathDirect.getResult().length() > 15000);
        }
    }
}
