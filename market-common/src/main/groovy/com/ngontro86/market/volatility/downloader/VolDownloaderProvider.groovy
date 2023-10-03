package com.ngontro86.market.volatility.downloader


import com.ngontro86.common.annotations.DBOEComponent
import org.springframework.context.annotation.Bean

@DBOEComponent
class VolDownloaderProvider {

    @Bean
    VolDownloader getVolDownloader() {
        return new DeribitVolDownloader()
    }

}
