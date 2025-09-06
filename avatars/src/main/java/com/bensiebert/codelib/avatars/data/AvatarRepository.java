package com.bensiebert.codelib.avatars.data;

import org.springframework.data.repository.CrudRepository;

public interface AvatarRepository extends CrudRepository<Avatar, String> {
    Avatar getAvatarByUserId(String userId);
}