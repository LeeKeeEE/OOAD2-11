package cn.edu.xmu.oomall.freight.controller.vo;

import cn.edu.xmu.javaee.core.aop.CopyFrom;
import cn.edu.xmu.javaee.core.model.vo.IdNameTypeVo;
import cn.edu.xmu.javaee.core.util.CloneFactory;
import cn.edu.xmu.oomall.freight.dao.bo.WarehouseLogistics;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * @author fan ninghan
 * 2023-dng3-008
 */
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@CopyFrom({WarehouseLogistics.class})
public class WarehouseLogisticsDto {
    public WarehouseLogisticsDto(WarehouseLogistics warehouseLogistics){
        this();
        this.shopLogistics = CloneFactory.copy(new AccountVo(warehouseLogistics.getAccount()), warehouseLogistics.getAccount());
        this.setCreator(IdNameTypeVo.builder().id(warehouseLogistics.getCreatorId())
                .name(warehouseLogistics.getCreatorName()).build());
        this.setModifier(IdNameTypeVo.builder().id(warehouseLogistics.getModifierId())
                .name(warehouseLogistics.getModifierName()).build());
    }
    private AccountVo shopLogistics;

    private LocalDateTime beginTime;

    private LocalDateTime endTime;

    private Byte invalid;

    private IdNameTypeVo creator;

    private IdNameTypeVo modifier;

    private LocalDateTime gmtCreate;

    private LocalDateTime gmtModified;

    public AccountVo getShopLogistics() {
        return shopLogistics;
    }

    public void setShopLogistics1(AccountVo shopLogisticsdto) {
        this.shopLogistics = shopLogisticsdto;
    }

    public LocalDateTime getBeginTime() {
        return beginTime;
    }

    public void setBeginTime(LocalDateTime beginTime) {
        this.beginTime = beginTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public Byte getInvalid() {
        return invalid;
    }

    public void setInvalid(Byte invalid) {
        this.invalid = invalid;
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
}
