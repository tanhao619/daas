package com.youedata.daas.rest.modular.model.dto;

/**
 * Created by Tanhao on 2017/12/28.
 */
public class WarningOverview {
    private Integer overtimeCounts;
    private Integer outtransportCounts;
    private Integer outcycleCounts;
    private Integer errorstopCounts;
    private Integer repeatFilesCounts;

    public Integer getOvertimeCounts() {
        return overtimeCounts;
    }

    public void setOvertimeCounts(Integer overtimeCounts) {
        this.overtimeCounts = overtimeCounts;
    }

    public Integer getOuttransportCounts() {
        return outtransportCounts;
    }

    public void setOuttransportCounts(Integer outtransportCounts) {
        this.outtransportCounts = outtransportCounts;
    }

    public Integer getOutcycleCounts() {
        return outcycleCounts;
    }

    public void setOutcycleCounts(Integer outcycleCounts) {
        this.outcycleCounts = outcycleCounts;
    }


    public Integer getRepeatFilesCounts() {
        return repeatFilesCounts;
    }

    public void setRepeatFilesCounts(Integer repeatFilesCounts) {
        this.repeatFilesCounts = repeatFilesCounts;
    }

    public Integer getErrorstopCounts() {
        return errorstopCounts;
    }

    public void setErrorstopCounts(Integer errorstopCounts) {
        this.errorstopCounts = errorstopCounts;
    }
}
