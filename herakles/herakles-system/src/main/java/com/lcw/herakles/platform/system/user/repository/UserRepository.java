package com.lcw.herakles.platform.system.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.lcw.herakles.platform.system.user.entity.UserPo;

/**
 * @author chenwulou
 *
 */
public interface UserRepository
        extends JpaRepository<UserPo, String>, JpaSpecificationExecutor<UserPo> {

    UserPo findByNickName(String nickName);

    UserPo findByMobile(String mobile);

}
