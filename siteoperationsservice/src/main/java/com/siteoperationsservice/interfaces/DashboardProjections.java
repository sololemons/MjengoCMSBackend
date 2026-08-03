package com.siteoperationsservice.interfaces;

public interface DashboardProjections {

    interface MaterialUsageProjection {
        String getMaterialName();
        Long getTotalQuantityUsed();
    }

}
