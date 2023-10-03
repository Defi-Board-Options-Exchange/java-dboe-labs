package com.ngontro86.market.price

interface SpotPricer {

    double spot(String underlying)

    void update(String underlying, double spot)

}