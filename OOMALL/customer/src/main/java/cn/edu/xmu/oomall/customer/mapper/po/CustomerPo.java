package cn.edu.xmu.oomall.customer.mapper.po;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "customer")
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class CustomerPo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String userName;   // 用户名
    private String password;   // 密码
    private String name;       // 真实姓名

    private boolean invalid;      // 0 有效，1 无效
    private boolean beDelete;     // 删除标志位
    private String mobile;     // 联系电话
    private Integer point;     // 积分

    private LocalDateTime gmtCreate;   // 创建时间
    private LocalDateTime gmtModified; // 修改时间
}
