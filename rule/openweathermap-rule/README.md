# OpenWeatherMap Rule

This rule lets you to segment users by the weather of their location.

Rule evaluation is based on geolocation extracted from client IP. For testing purposes there is a possibility to manually enter the client IP address.

At the moment there are two rules:

 * By temperature 
 * By weather code (cloudy, sunny etc.)


This rule caches the weather for a single IP for 30 mins. It also demonstrates using the Hystrix circuibreaker for timeouting the weather polling in case of a problem.


# Installation Notes

 * This project is depending on content-targeting-api-extension project

 


