package cn.edu.xmu.oomall.freight.controller;

import cn.edu.xmu.javaee.core.aop.Audit;
import cn.edu.xmu.javaee.core.aop.LoginUser;
import cn.edu.xmu.javaee.core.model.ReturnNo;
import cn.edu.xmu.javaee.core.model.ReturnObject;
import cn.edu.xmu.javaee.core.model.vo.PageVo;
import cn.edu.xmu.javaee.core.model.dto.UserDto;
import cn.edu.xmu.javaee.core.util.CloneFactory;
import cn.edu.xmu.oomall.freight.controller.vo.AccountVo;
import cn.edu.xmu.oomall.freight.controller.vo.UndeliverableVo;
import cn.edu.xmu.oomall.freight.controller.dto.ModifyShopLogisticsDto;
import cn.edu.xmu.oomall.freight.controller.dto.ShopLogisticsDto;
import cn.edu.xmu.oomall.freight.controller.dto.UndeliverableDto;
import cn.edu.xmu.oomall.freight.dao.bo.Account;
import cn.edu.xmu.oomall.freight.dao.bo.Undeliverable;
import cn.edu.xmu.oomall.freight.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import java.util.List;

/**
 * @author fan ninghan
 * 2023-dng3-008
 */
@RestController
@RequestMapping(value = "/shops/{shopId}", produces = "application/json;charset=UTF-8")
@RequiredArgsConstructor
public class AccountController {
    private final static Logger logger = LoggerFactory.getLogger(AccountController.class);

    private final AccountService accountService;

    /**
     * 商家新增物流合作信息
     *
     */
    @PostMapping("/shoplogistics")
    @Audit(departName = "shops")
    public ReturnObject addShopLogistics(@PathVariable Long shopId,
                                         @RequestBody ShopLogisticsDto shopLogisticsDto,
                                         @LoginUser UserDto user){
        Account account = CloneFactory.copy(new Account(), shopLogisticsDto);
        account = accountService.addAccount(account, shopId, user);

        return new ReturnObject(ReturnNo.CREATED, CloneFactory.copy(new AccountVo(account), account));
    }


    /**
     * 商家获取物流合作信息
     * @param shopId
     * @param page  (not required)
     * @param pageSize (not required)
     * @return
     */
    @GetMapping("/shoplogistics")
    @Audit(departName = "shops")
    public ReturnObject getShopLogistics(@PathVariable Long shopId,
                                         @RequestParam(required = false, defaultValue = "1") Integer page,
                                         @RequestParam(required = false, defaultValue = "10") Integer pageSize,
                                         @LoginUser UserDto user){
        List<Account> shopLogistics = accountService.retrieveAccount(shopId, page, pageSize);
        List<AccountVo> accountVoList = shopLogistics.stream()
                .map(shopLogistic -> CloneFactory.copy(new AccountVo(shopLogistic), shopLogistic))
                .collect(java.util.stream.Collectors.toList());
        return new ReturnObject(new PageVo<>(accountVoList, page, pageSize));
    }

    /**
     * 商家修改物流模板
     *
     */
    @PutMapping("/shoplogistics/{id}")
    @Audit(departName = "shops")
    public ReturnObject modifyShopLogistics(@PathVariable Long shopId,
                                            @PathVariable Long id,
                                            @RequestBody ModifyShopLogisticsDto modifyShopLogisticsDto,
                                            @LoginUser UserDto user){
        Account account = CloneFactory.copy(new Account(), modifyShopLogisticsDto);
        accountService.modifyAccount(account, shopId, id, user);
        return new ReturnObject();
    }

    /**
     * 商铺停用物流合作
     *
     */
    @PutMapping("/shoplogistics/{id}/suspend")
    @Audit(departName = "shops")
    public ReturnObject suspendShopLogistics(@PathVariable Long shopId,
                                             @PathVariable Long id,
                                             @LoginUser UserDto user){
        accountService.modifyAccountValidity(Account.INVALID, shopId, id, user);
        return new ReturnObject();
    }

    /**
     * 商铺启用物流合作
     *
     */
    @PutMapping("/shoplogistics/{id}/resume")
    @Audit(departName = "shops")
    public ReturnObject resumeShopLogistics(@PathVariable Long shopId,
                                             @PathVariable Long id,
                                             @LoginUser UserDto user){
        accountService.modifyAccountValidity(Account.VALID, shopId, id, user);
        return new ReturnObject();
    }

    /**
     * 商家指定无法配送
     *
     */
    @PostMapping("/shoplogistics/{id}/regions/{rid}/undeliverable")
    @Audit(departName = "shops")
    public ReturnObject addUndeliverableRegion(@PathVariable Long shopId,
                                               @PathVariable Long rid,
                                               @PathVariable Long id,
                                               @RequestBody UndeliverableDto undeliverableDto,
                                               @LoginUser UserDto user){
        Undeliverable undeliverable = CloneFactory.copy(new Undeliverable(), undeliverableDto);
        accountService.addUndeliverableRegion(undeliverable, shopId, rid, id, user);
        return new ReturnObject(ReturnNo.CREATED);
    }

    /**
     * 商家更改无法配送
     *
     */
    @PutMapping("/shoplogistics/{id}/regions/{rid}/undeliverable")
    @Audit(departName = "shops")
    public ReturnObject modifyUndeliverableRegion(@PathVariable Long shopId,
                                                  @PathVariable Long rid,
                                                  @PathVariable Long id,
                                                  @RequestBody UndeliverableDto undeliverableDto,
                                                  @LoginUser UserDto user){
        Undeliverable undeliverable = CloneFactory.copy(new Undeliverable(), undeliverableDto);
        accountService.modifyUndeliverableRegion(undeliverable, shopId, rid, id, user);
        return new ReturnObject();
    }

    /**
     * 商家删除无法配送
     *
     */
    @DeleteMapping("/shoplogistics/{id}/regions/{rid}/undeliverable")
    @Audit(departName = "shops")
    public ReturnObject deleteUndeliverableRegion(@PathVariable Long shopId,
                                                  @PathVariable Long rid,
                                                  @PathVariable Long id,
                                                  @LoginUser UserDto user){
        accountService.deleteUndeliverableRegion(shopId, rid, id, user);
        return new ReturnObject();
    }

    /**
     * 商家获取无法配送
     *
     */
    @GetMapping("/shoplogistics/{id}/undeliverableregions")
    @Audit(departName = "shops")
    public ReturnObject getUndeliverableRegion(@PathVariable Long shopId,
                                               @PathVariable Long id,
                                               @RequestParam(required = false, defaultValue = "1") Integer page,
                                               @RequestParam(required = false, defaultValue = "10") Integer pageSize,
                                               @LoginUser UserDto user){
        List<Undeliverable> undeliverables = accountService.retrieveUndeliverableRegion(shopId, id, page, pageSize);
        List<UndeliverableVo> undeliverableVos = undeliverables.stream()
                .map(bo -> CloneFactory.copy(new UndeliverableVo(bo), bo))
                .collect(java.util.stream.Collectors.toList());
        return new ReturnObject(new PageVo<>(undeliverableVos, page, pageSize));
    }
}
