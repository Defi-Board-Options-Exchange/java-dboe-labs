package com.ngontro86.market.instruments

interface ExchangeSpecsLoader {
    Collection loadOptions(String chain)

    Collection loadClobs(String chain)
}