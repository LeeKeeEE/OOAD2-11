//School of Informatics Xiamen University, GPL-3.0 license
package cn.edu.xmu.oomall.freight.dao.logistics;

import cn.edu.xmu.oomall.freight.dao.bo.Express;

import cn.edu.xmu.oomall.freight.dao.bo.Account;
import cn.edu.xmu.oomall.freight.dao.logistics.exception.MethodNoSupportException;
import cn.edu.xmu.oomall.freight.dao.logistics.retObj.PostCreatePackageAdaptorDto;
import java.security.NoSuchAlgorithmException;
/**
 * @author 张宁坚
 * @Task 2023-dgn3-005
 * 物流平台接口
 */


/**
 * @author 曹志逸
 * @Task 2023-dgn3-006
 * 物流平台接口
 */
/**
 * 物流渠道适配器接口
 * 适配器模式
 */
public interface LogisticsAdaptor {
    /**
     * @Author:丁圳杰
     * @return
     */
    default MethodNoSupportException notSupport() {
        return new MethodNoSupportException();
    }
    /**
     * 创建运单
     * @param account
     * @param express
     * @return
     * @throws NoSuchAlgorithmException
     */
    PostCreatePackageAdaptorDto createPackage(Account account, Express express) ;

    /**
     * 查询运单
     * @param account
     * @param billCode
     * @return
     * @throws NoSuchAlgorithmException
     */
    Express getPackage(Account account, String billCode) ;

    /**
     * 取消运单
     * @param account
     * @param express
     * @throws NoSuchAlgorithmException
     */
    void cancelPackage(Account account, Express express) ;

    /**
     * 商户发出揽收
     * @param account
     * @param billCode
     */
    void sendPackage(Account account, String billCode, String orderId) ;

}