package com.ngontro86.market.price

interface SpotPricer {

    Double spot(String underlying)

    void update(String underlying, double spot)

}