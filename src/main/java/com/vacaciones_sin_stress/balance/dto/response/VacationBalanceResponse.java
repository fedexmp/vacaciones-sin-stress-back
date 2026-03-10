package com.vacaciones_sin_stress.balance.dto.response;

import java.time.LocalDateTime;

public class VacationBalanceResponse {

    private Long id;
    private Long userId;
    private Integer year;
    private Integer totalDays;
    private Integer usedDays;
    private Integer availableDays;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public VacationBalanceResponse() {
    }

    public VacationBalanceResponse(Long id,
                                   Long userId,
                                   Integer year,
                                   Integer totalDays,
                                   Integer usedDays,
                                   Integer availableDays,
                                   LocalDateTime createdAt,
                                   LocalDateTime updatedAt) {
        this.id = id;
        this.userId = userId;
        this.year = year;
        this.totalDays = totalDays;
        this.usedDays = usedDays;
        this.availableDays = availableDays;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public Integer getAvailableDays() {
        return availableDays;
    }

    public void setAvailableDays(Integer availableDays) {
        this.availableDays = availableDays;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
