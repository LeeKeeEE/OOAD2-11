package cn.edu.xmu.oomall.freight.controller.vo;

import cn.edu.xmu.javaee.core.aop.CopyFrom;
import cn.edu.xmu.javaee.core.model.vo.IdNameTypeVo;
import cn.edu.xmu.oomall.freight.dao.bo.Account;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * @author fan ninghan
 * 2023-dng3-008
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@NoArgsConstructor
@CopyFrom({Account.class})
public class AccountVo {
    public AccountVo(Account account){
        this();
        this.setLogistics1(IdNameTypeVo.builder().id(account.getLogistics().getId())
                .name(account.getLogistics().getName()).build());
        this.setCreator(IdNameTypeVo.builder().id(account.getCreatorId())
                .name(account.getCreatorName()).build());
        this.setModifier(IdNameTypeVo.builder().id(account.getModifierId())
                .name(account.getModifierName()).build());
    }

    private Long id;

    private IdNameTypeVo logistics;

    private Byte invalid;

    private String secret;

    private Integer priority;

    private LocalDateTime gmtCreate;

    private LocalDateTime gmtModified;

    private IdNameTypeVo creator;

    private IdNameTypeVo modifier;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public IdNameTypeVo getLogistics() {
        return logistics;
    }

    public void setLogistics1(IdNameTypeVo logistics) {
        this.logistics = logistics;
    }

    public Byte getInvalid() {
        return invalid;
    }

    public void setInvalid(Byte invalid) {
        this.invalid = invalid;
    }

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    public LocalDateTime getGmtCreate() {
        return gmtCreate;
    }

    public void setGmtCreate(LocalDateTime gmtCreate) {
        this.gmtCreate = gmtCreate;
    }

    public LocalDateTime getGmtModified() {
        return gmtModified;
    }

    public void setGmtModified(LocalDateTime gmtModified) {
        this.gmtModified = gmtModified;
    }

    public IdNameTypeVo getCreator() {
        return creator;
    }

    public void setCreator(IdNameTypeVo creator) {
        this.creator = creator;
    }

    public IdNameTypeVo getModifier() {
        return modifier;
    }

    public void setModifier(IdNameTypeVo modifier) {
        this.modifier = modifier;
    }
}
