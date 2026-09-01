package com.iwhalecloud.consumercenter.mvc.pojo.convert;

import com.iwhalecloud.consumercenter.mvc.pojo.entity.SystemUser;
import com.iwhalecloud.consumercenter.mvc.pojo.vo.auth.SystemUserVO;

/**
 * 系统用户对象转换
 */
public final class SystemUserConvert {

    private SystemUserConvert() {
    }

    public static SystemUserVO entity2vo(SystemUser source) {
        SystemUserVO target = new SystemUserVO();
        target.setId(source.getId());
        target.setUsername(source.getUsername());
        target.setRealName(source.getRealName());
        target.setPhone(source.getPhone());
        target.setEmail(source.getEmail());
        target.setStatus(source.getStatus() == null ? null : source.getStatus().intValue());
        target.setCreateTime(source.getCreateTime());
        return target;
    }
}
