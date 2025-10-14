package com.example.reservation;


public class App 
{
    public static void main( String[] args )
    {
        System.out.println( "Welcome to Vehicle Reservation System!" );
        Authentication auth = new Authentication();
        Personne user = auth.getUser();
    }
}
