package cn.edu.xmu.oomall.freight.dao;

import cn.edu.xmu.javaee.core.exception.BusinessException;
import cn.edu.xmu.javaee.core.mapper.RedisUtil;
import cn.edu.xmu.javaee.core.model.ReturnNo;
import cn.edu.xmu.javaee.core.model.dto.UserDto;
import cn.edu.xmu.javaee.core.util.CloneFactory;
import cn.edu.xmu.oomall.freight.dao.bo.Account;
import cn.edu.xmu.oomall.freight.dao.logistics.LogisticsAdaptorFactory;
import cn.edu.xmu.oomall.freight.mapper.jpa.AccountPoMapper;
import cn.edu.xmu.oomall.freight.mapper.po.AccountPo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import static cn.edu.xmu.javaee.core.model.Constants.PLATFORM;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
/**
 * @author fan ninghan,huangzian
 * 2023-dng3-008,2023-dgn3-009
 */
@Repository
public class AccountDao {
    private final Logger logger = LoggerFactory.getLogger(AccountDao.class);

    private AccountPoMapper accountPoMapper;

    private LogisticsDao logisticsDao;

    @Autowired
    @Lazy
    public AccountDao(RedisUtil redisUtil, AccountPoMapper accountPoMapper,
                      ExpressDao expressDao, LogisticsAdaptorFactory logisticsAdaptorFactory, LogisticsDao logisticsDao) {
        this.redisUtil = redisUtil;
        this.accountPoMapper = accountPoMapper;
        this.expressDao = expressDao;
        this.logisticsAdaptorFactory = logisticsAdaptorFactory;
        this.logisticsDao = logisticsDao;
    }

    public void build(Account bo){
        bo.setExpressDao(this.expressDao);
        bo.setLogisticsDao(this.logisticsDao);
        bo.setLogisticsAdaptor(this.logisticsAdaptorFactory);
    }


    public Account build(AccountPo po, Optional<String> redisKey) throws RuntimeException{
        Account bo = CloneFactory.copy(new Account(), po);
        this.build(bo);
        redisKey.ifPresent(key -> redisUtil.set(key, bo, timeout));
        return bo;
    }

    /**
     * 插入店铺物流
     *
     * @param account
     * @param shopId
     * @param user
     *
     * @return
     */
    public Account insert(Account account, Long shopId, UserDto user) throws RuntimeException {
        // ensure not repetitive
        Account savedAccount = this.findByShopIdAndLogisticsId(shopId, account.getLogisticsId());
        if(savedAccount != null) {
            throw new BusinessException(ReturnNo.FREIGHT_LOGISTIC_EXIST);
        }

        // check & build
        this.build(account);
        account.buildLogistics();

        account.setShopId(shopId);
        account.setInvalid(Account.VALID);
        account.setGmtCreate(LocalDateTime.now());
        account.setGmtModified(null);
        account.setCreator(user);

        AccountPo savedPo = accountPoMapper.save(CloneFactory.copy(new AccountPo(), account));
        account.setId(savedPo.getId());
        return account;
    }

    /**
     * 查询店铺物流
     *
     * @param shopId
     *
     * @return
     */
    public List<Account> retrieveByShopId(Long shopId, Integer page, Integer pageSize) throws RuntimeException {
        Pageable pageable = PageRequest.of(page - 1, pageSize);
        return this.accountPoMapper.findAllByShopIdOrderByPriorityAsc(shopId, pageable).stream()
                .map(po -> {
                    Account bo = CloneFactory.copy(new Account(), po);
                    try {
                        this.build(bo);
                        bo.buildLogistics();
                        return bo;
                    }
                    catch (Exception e) { throw new RuntimeException(e); }
                })
                .collect(java.util.stream.Collectors.toList());
    }

    /**
     * 修改店铺物流
     *
     * @param bo
     *
     * @return
     */
    public void save(Account bo, Long id, UserDto user) throws RuntimeException {
        bo.setId(id);
        bo.setModifierId(user.getId());
        bo.setModifierName(user.getName());
        bo.setGmtModified(LocalDateTime.now());
        accountPoMapper.save(CloneFactory.copy(new AccountPo(), bo));

    }

    public Account findByShopIdAndLogisticsId(Long shopId, Long logisticsId) throws RuntimeException {
        AccountPo po = accountPoMapper.findByShopIdAndLogisticsId(shopId, logisticsId);
        if (po==null) {
            return null;
        } else {
            return CloneFactory.copy(new Account(),po);
        }
    }

    public static final String KEY = "SL%d";
    private RedisUtil redisUtil;
    @Lazy
    private ExpressDao expressDao;
    private LogisticsAdaptorFactory logisticsAdaptorFactory;

    @Value("${oomall.freight.shop-logistics.timeout}")
    private int timeout;

    public Account findById(Long shopId, Long id) throws RuntimeException{
        if (null == id) {
            throw new IllegalArgumentException("ShopLogistics.findById: shopLogistic id is null");
        }
        logger.debug("findObjById: id = {}", id);
        String key = String.format(KEY, id);

        Account account = (Account) redisUtil.get(key);
        if (account != null) {
            this.build(account);
            logger.debug("redis shopLogistics = {}", account);
            if(!PLATFORM.equals(shopId) && !shopId.equals(account.getShopId())){
                throw new BusinessException(ReturnNo.RESOURCE_ID_OUTSCOPE, String.format(ReturnNo.RESOURCE_ID_OUTSCOPE.getMessage(), "商铺物流", id, shopId));
            }
            return account;
        } else {
            Optional<AccountPo> po = this.accountPoMapper.findById(id);
            if (po.isPresent()) {
                if (!PLATFORM.equals(shopId) && !shopId.equals(po.get().getShopId())) {
                    throw new BusinessException(ReturnNo.RESOURCE_ID_OUTSCOPE, String.format(ReturnNo.RESOURCE_ID_OUTSCOPE.getMessage(), "商铺物流", id, shopId));
                }
                return this.build(po.get(), Optional.of(key));
            }
            throw new BusinessException(ReturnNo.RESOURCE_ID_NOTEXIST, String.format(ReturnNo.RESOURCE_ID_NOTEXIST.getMessage(), "商铺物流", id));
        }
    }


}
