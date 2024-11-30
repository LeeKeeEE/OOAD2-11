package cn.edu.xmu.oomall.freight.service;

import cn.edu.xmu.javaee.core.model.dto.UserDto;
import cn.edu.xmu.oomall.freight.dao.AccountDao;
import cn.edu.xmu.oomall.freight.dao.UndeliverableDao;
import cn.edu.xmu.oomall.freight.dao.bo.Account;
import cn.edu.xmu.oomall.freight.dao.bo.Undeliverable;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import java.util.List;
/**
 * @author fan ninghan
 * 2023-dng3-008
 */
@Service
@Transactional(propagation = Propagation.REQUIRED)
@RequiredArgsConstructor
public class AccountService {
    private final static Logger logger = LoggerFactory.getLogger(AccountService.class);

    private final AccountDao accountDao;

    private final UndeliverableDao undeliverableDao;

    /**
     * 新增商铺的物流合作
     *
     */
    public Account addAccount(Account account, Long shopId, UserDto user){

        return accountDao.insert(account, shopId, user);
    }

    /**
     * 查询商铺的物流合作
     *
     */
    public List<Account> retrieveAccount(Long shopId, Integer page, Integer pageSize){
        return accountDao.retrieveByShopId(shopId, page, pageSize);
    }

    /**
     * 修改商铺的物流合作
     *
     */
    public void modifyAccount(Account bo, Long shopId, Long shopLogisticsId, UserDto user){
        // find原因：检查shopId与shopLogisticsId是否匹配
        Account savedBo = accountDao.findById(shopId, shopLogisticsId);
        accountDao.save(bo, shopLogisticsId, user);
    }

    /**
     * 停用商铺的物流合作
     */
    public void modifyAccountValidity(Byte invalid, Long shopId, Long shopLogisticsId, UserDto user){
        // find原因：检查shopId与shopLogisticsId是否匹配
        Account account = this.accountDao.findById(shopId, shopLogisticsId);
        account.setValidity(invalid);
        accountDao.save(account, shopLogisticsId, user);
    }

    /**
     * 商铺指定无法配送地区
     *
     */
    public void addUndeliverableRegion(Undeliverable undeliverable, Long shopId, Long regionId, Long shopLogisticsId, UserDto user){
        Account account = this.accountDao.findById(shopId, shopLogisticsId);
        undeliverableDao.insert(undeliverable, regionId, shopLogisticsId, user);
    }

    /**
     * 商铺修改无法配送地区
     *
     */
    public void modifyUndeliverableRegion(Undeliverable undeliverable, Long shopId, Long regionId, Long shopLogisticsId, UserDto user){
        Account account = this.accountDao.findById(shopId, shopLogisticsId);
        Undeliverable savedBo = this.undeliverableDao.findByRegionIdAndShopLogisticsId(regionId, shopLogisticsId);
        undeliverable.setId(savedBo.getId());
        undeliverableDao.save(undeliverable, user);
    }

    /**
     * 商铺删除无法配送地区
     *
     */
    public void deleteUndeliverableRegion(Long shopId, Long regionId, Long shopLogisticsId, UserDto user){
        Account account = this.accountDao.findById(shopId, shopLogisticsId);
        Undeliverable savedBo = this.undeliverableDao.findByRegionIdAndShopLogisticsId(regionId, shopLogisticsId);
        undeliverableDao.delete(savedBo.getId());
    }

    /**
     * 商铺查询无法配送地区
     *
     */
    public List<Undeliverable> retrieveUndeliverableRegion(Long shopId, Long shopLogisticsId, Integer page, Integer pageSize){
        Account account = this.accountDao.findById(shopId, shopLogisticsId);
        return undeliverableDao.retrieveByShopLogisticsId(shopId, shopLogisticsId, page, pageSize);
    }
}
