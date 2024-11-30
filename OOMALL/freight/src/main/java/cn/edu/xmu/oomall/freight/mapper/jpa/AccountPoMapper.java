package cn.edu.xmu.oomall.freight.mapper.jpa;

import cn.edu.xmu.oomall.freight.mapper.po.AccountPo;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author fan ninghan
 * 2023-dng3-008
 */
@Repository
public interface AccountPoMapper extends JpaRepository<AccountPo, Long> {
    public AccountPo findByIdAndInvalidEquals(Long id, Byte invalid);

    public List<AccountPo> findAllByShopIdOrderByPriorityAsc(Long shopId, Pageable pageable);

    public AccountPo findByShopIdAndLogisticsId(Long shopId, Long logisticsId);

}
