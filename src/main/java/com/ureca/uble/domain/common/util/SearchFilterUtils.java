package com.ureca.uble.domain.common.util;

import co.elastic.clients.elasticsearch._types.FieldValue;
import co.elastic.clients.elasticsearch._types.query_dsl.DateRangeQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch._types.query_dsl.TermQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.TermsQuery;
import com.ureca.uble.entity.enums.BenefitType;
import com.ureca.uble.entity.enums.Gender;
import com.ureca.uble.entity.enums.Rank;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class SearchFilterUtils {

    public static List<Query> getAdminStatisticFilters(Gender gender, Integer ageRange, Rank rank, BenefitType benefitType) {
        List<Query> filters = new ArrayList<>();

        // 성별 Filter
        if(gender != null) {
            filters.add(TermQuery.of(t -> t.field("userGender").value(gender.toString()))._toQuery());
        }

        // 연령대 Filter
        if(ageRange != null) {
            int currentYear = LocalDate.now().getYear();
            String fromBirthDate = LocalDate.of(currentYear - ageRange - 9, 1, 1).format(DateTimeFormatter.ISO_DATE);
            String toBirthDate = LocalDate.of(currentYear - ageRange, 12, 31).format(DateTimeFormatter.ISO_DATE);

            filters.add(DateRangeQuery.of(r -> r
                .field("userBirthDate")
                .gte(fromBirthDate)
                .lte(toBirthDate)
            )._toRangeQuery()._toQuery());
        }

        // 사용자 등급 Filter
        if(rank != null) {
            filters.add(TermQuery.of(t -> t
                .field("userRank")
                .value(rank.toString())
            )._toQuery());
        }

        // 혜택 종류 Filter
        if(benefitType != null) {
            filters.add(TermsQuery.of(t -> t
                .field("brandBenefitType")
                .terms(v -> v.value(
                    switch (benefitType) {
                        case VIP -> Stream.of("VIP", "VIP_NORMAL").map(FieldValue::of).toList();
                        case NORMAL -> Stream.of("NORMAL", "VIP_NORMAL").map(FieldValue::of).toList();
                        case LOCAL -> Stream.of("LOCAL").map(FieldValue::of).toList();
                    }
                ))
            )._toQuery());
        }
        return filters;
    }
}
