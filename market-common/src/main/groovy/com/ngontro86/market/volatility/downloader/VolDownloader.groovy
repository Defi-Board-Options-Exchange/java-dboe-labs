package com.ngontro86.market.volatility.downloader

interface VolDownloader {
    Map<Long, Map<Double, Double>> loadVols(String underlying)
}