package cn.edu.xmu.oomall.customer.dao;

import cn.edu.xmu.oomall.customer.dao.bo.Customer;
import cn.edu.xmu.oomall.customer.mapper.CustomerPoMapper;
import cn.edu.xmu.oomall.customer.mapper.po.CustomerPo;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class CustomerDao {

    private final CustomerPoMapper customerPoMapper;

    /**
     * 根据用户名查询顾客
     */
    public Customer findByUserName(String userName) {
        CustomerPo customerPo = customerPoMapper.findByUserName(userName);
        if (customerPo == null) return null;

        Customer customer = new Customer();
        BeanUtils.copyProperties(customerPo, customer);
        return customer;
    }

    /**
     * 根据 ID 查询顾客
     */
    public Optional<Customer> findById(Long id) {
        return customerPoMapper.findById(id).map(po -> {
            Customer bo = new Customer();
            BeanUtils.copyProperties(po, bo);
            return bo;
        });
    }

    /**
     * 保存或更新顾客
     */
    public Customer save(Customer customer) {
        CustomerPo customerPo = new CustomerPo();
        BeanUtils.copyProperties(customer, customerPo);
        CustomerPo savedPo = customerPoMapper.save(customerPo);

        Customer bo = new Customer();
        BeanUtils.copyProperties(savedPo, bo);
        return bo;
    }
}
