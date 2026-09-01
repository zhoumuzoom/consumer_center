package com.iwhalecloud.consumercenter.mvc.pojo.convert;

import com.iwhalecloud.consumercenter.mvc.pojo.dto.decision.DecisionMakerCreateDTO;
import com.iwhalecloud.consumercenter.mvc.pojo.dto.decision.DecisionMakerUpdateDTO;
import com.iwhalecloud.consumercenter.mvc.pojo.entity.DecisionMaker;
import com.iwhalecloud.consumercenter.mvc.pojo.vo.decision.DecisionMakerVO;

/**
 * 决策关键人对象转换
 */
public final class DecisionMakerConvert {

    private DecisionMakerConvert() {
    }

    public static DecisionMaker dto2entity(DecisionMakerCreateDTO source) {
        DecisionMaker target = new DecisionMaker();
        target.setName(source.getName());
        target.setPosition(source.getPosition());
        target.setDepartment(source.getDepartment());
        target.setPhone(source.getPhone());
        target.setEmail(source.getEmail());
        target.setRole(source.getRole());
        target.setCompany(source.getCompany());
        return target;
    }

    public static void dto2entity(DecisionMakerUpdateDTO source, DecisionMaker target) {
        target.setId(source.getId());
        target.setName(source.getName());
        target.setPosition(source.getPosition());
        target.setDepartment(source.getDepartment());
        target.setPhone(source.getPhone());
        target.setEmail(source.getEmail());
        target.setRole(source.getRole());
        target.setCompany(source.getCompany());
    }

    public static DecisionMakerVO entity2vo(DecisionMaker source) {
        DecisionMakerVO target = new DecisionMakerVO();
        target.setId(source.getId());
        target.setName(source.getName());
        target.setPosition(source.getPosition());
        target.setDepartment(source.getDepartment());
        target.setPhone(source.getPhone());
        target.setEmail(source.getEmail());
        target.setRole(source.getRole());
        target.setCompany(source.getCompany());
        target.setCreateTime(source.getCreateTime());
        return target;
    }

}
