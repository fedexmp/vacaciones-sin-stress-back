package com.vacaciones_sin_stress.balance.dto.request;

public class CreateOrUpdateVacationBalanceRequest {

    private Long userId;
    private Integer year;
    private Integer totalDays;
    private Integer usedDays;

    public CreateOrUpdateVacationBalanceRequest() {
    }

    public CreateOrUpdateVacationBalanceRequest(Long userId, Integer year, Integer totalDays, Integer usedDays) {
        this.userId = userId;
        this.year = year;
        this.totalDays = totalDays;
        this.usedDays = usedDays;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public Integer getTotalDays() {
        return totalDays;
    }

    public void setTotalDays(Integer totalDays) {
        this.totalDays = totalDays;
    }

    public Integer getUsedDays() {
        return usedDays;
    }

    public void setUsedDays(Integer usedDays) {
        this.usedDays = usedDays;
    }
}
