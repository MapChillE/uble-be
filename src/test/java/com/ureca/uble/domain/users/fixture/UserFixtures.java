package com.ureca.uble.domain.users.fixture;

import com.ureca.uble.entity.User;
import com.ureca.uble.entity.enums.Rank;
import com.ureca.uble.entity.enums.Role;

public class UserFixtures {
    public static User createUser() {
        return User.createTmpUser(Rank.VVIP, Role.USER);
    }
}
