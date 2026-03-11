package com.vacaciones_sin_stress.vacation.specification;

import com.vacaciones_sin_stress.common.enums.VacationRequestStatus;
import com.vacaciones_sin_stress.user.entity.User;
import com.vacaciones_sin_stress.vacation.entity.VacationRequest;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;

/**
 * Reusable specifications for filtering vacation-request history views.
 */
public final class VacationRequestSpecifications {

    private VacationRequestSpecifications() {
    }

    public static Specification<VacationRequest> withUserId(Long userId) {
        return (root, query, cb) -> userId == null ? cb.conjunction() : cb.equal(root.get("userId"), userId);
    }

    public static Specification<VacationRequest> withStatus(VacationRequestStatus status) {
        return (root, query, cb) -> status == null ? cb.conjunction() : cb.equal(root.get("status"), status);
    }

    public static Specification<VacationRequest> withRequestYear(Integer year) {
        return (root, query, cb) -> year == null ? cb.conjunction() : cb.equal(root.get("requestYear"), year);
    }

    public static Specification<VacationRequest> withFromDate(LocalDate fromDate) {
        return (root, query, cb) -> fromDate == null ? cb.conjunction()
                : cb.greaterThanOrEqualTo(root.get("startDate"), fromDate);
    }

    public static Specification<VacationRequest> withToDate(LocalDate toDate) {
        return (root, query, cb) -> toDate == null ? cb.conjunction()
                : cb.lessThanOrEqualTo(root.get("endDate"), toDate);
    }

    public static Specification<VacationRequest> forUser(Long userId) {
        return (root, query, cb) -> cb.equal(root.get("userId"), userId);
    }

    public static Specification<VacationRequest> forLeaderInvolvement(Long leaderId) {
        return (root, query, cb) -> {
            var reviewedByLeader = cb.equal(root.get("approvedByLeaderId"), leaderId);
            var directReportSubquery = query.subquery(Long.class);
            var userRoot = directReportSubquery.from(User.class);
            directReportSubquery.select(userRoot.get("id"))
                    .where(cb.equal(userRoot.get("leaderId"), leaderId));
            var pendingForDirectReports = cb.and(
                    cb.equal(root.get("status"), VacationRequestStatus.PENDING_LEADER),
                    root.get("userId").in(directReportSubquery)
            );
            return cb.or(reviewedByLeader, pendingForDirectReports);
        };
    }
}
