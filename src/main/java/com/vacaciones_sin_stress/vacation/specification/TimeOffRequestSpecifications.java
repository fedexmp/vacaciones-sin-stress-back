package com.vacaciones_sin_stress.vacation.specification;

import com.vacaciones_sin_stress.common.enums.TimeOffRequestStatus;
import com.vacaciones_sin_stress.user.entity.User;
import com.vacaciones_sin_stress.vacation.entity.TimeOffRequest;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;

/**
 * Reusable specifications for filtering vacation-request history views.
 */
public final class TimeOffRequestSpecifications {

    private TimeOffRequestSpecifications() {
    }

    public static Specification<TimeOffRequest> withUserId(Long userId) {
        return (root, query, cb) -> userId == null ? cb.conjunction() : cb.equal(root.get("userId"), userId);
    }

    public static Specification<TimeOffRequest> withStatus(TimeOffRequestStatus status) {
        return (root, query, cb) -> status == null ? cb.conjunction() : cb.equal(root.get("status"), status);
    }

    public static Specification<TimeOffRequest> withRequestYear(Integer year) {
        return (root, query, cb) -> year == null ? cb.conjunction() : cb.equal(root.get("requestYear"), year);
    }

    public static Specification<TimeOffRequest> withFromDate(LocalDate fromDate) {
        return (root, query, cb) -> fromDate == null ? cb.conjunction()
                : cb.greaterThanOrEqualTo(root.get("startDate"), fromDate);
    }

    public static Specification<TimeOffRequest> withToDate(LocalDate toDate) {
        return (root, query, cb) -> toDate == null ? cb.conjunction()
                : cb.lessThanOrEqualTo(root.get("endDate"), toDate);
    }

    public static Specification<TimeOffRequest> forUser(Long userId) {
        return (root, query, cb) -> cb.equal(root.get("userId"), userId);
    }

    public static Specification<TimeOffRequest> forLeaderInvolvement(Long leaderId) {
        return (root, query, cb) -> {
            var reviewedByLeader = cb.equal(root.get("approvedByLeaderId"), leaderId);
            var directReportSubquery = query.subquery(Long.class);
            var userRoot = directReportSubquery.from(User.class);
            directReportSubquery.select(userRoot.get("id"))
                    .where(cb.equal(userRoot.get("leaderId"), leaderId));
            var pendingForDirectReports = cb.and(
                    cb.equal(root.get("status"), TimeOffRequestStatus.PENDING_LEADER),
                    root.get("userId").in(directReportSubquery)
            );
            return cb.or(reviewedByLeader, pendingForDirectReports);
        };
    }
}

