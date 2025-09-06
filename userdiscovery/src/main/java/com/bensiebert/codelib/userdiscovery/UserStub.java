package com.bensiebert.codelib.userdiscovery;

import com.bensiebert.codelib.auth.data.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserStub {

    private String id;
    private String name;
    private String role;

    public static UserStub of(User user) {
        return new UserStub(user.getId(), user.getName(), user.getRole());
    }
}
