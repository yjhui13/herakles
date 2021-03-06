package com.lcw.herakles.platform.system.dict.enums;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.lcw.herakles.platform.common.enums.DBStrEnum;
import com.lcw.herakles.platform.common.util.DBStrEnumSerializer;

/**
 * @author chenwulou
 *
 */
@JsonSerialize(using = DBStrEnumSerializer.class)
public enum EDictCategory implements DBStrEnum {
  
    NULL("",""),
    REGION("REGION","所属区域"),
    ORG_INDUS("ORG_INDUS","行业类别"),
    ORG_NATURE("ORG_NATURE","组织形态"),
    POSITION("POSITION","职位"),
    ID_TYPE("ID_TYPE","证件类型");
  
    EDictCategory(String code, String text) {
        this.code = code;
        this.text = text;
    }
  
    private String code;
  
    private String text;
  
    @Override
    public String getText() {
      return text;
    }
  
    @Override
    public void setText(String text) {
      this.text = text;
    }
  
    @Override
    public String getCode() {
      return code;
    }
  
    @Override
    public void setCode(String code) {
      this.code = code;
    }

}
