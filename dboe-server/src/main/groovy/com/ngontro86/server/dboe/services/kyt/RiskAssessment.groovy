package com.ngontro86.server.dboe.services.kyt

class RiskAssessment {
    public String address
    public String risk

    @Override
    String toString() {
        "${address}:${risk}"
    }
}
