package com.ureca.uble.domain.brand.fixture;

import com.ureca.uble.entity.Benefit;
import com.ureca.uble.entity.Brand;
import com.ureca.uble.entity.enums.Period;
import com.ureca.uble.entity.enums.Rank;

public class BenefitFixtures {
    public static Benefit createTmpBenefit(Brand brand, int number) {
        return Benefit.createTmpBenefit(brand, Rank.NORMAL, Period.DAILY, number);
    }
}
