package my.restproto.common.mysql.intergration.support;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import my.restproto.common.mysql.model.BaseEntity;

/**
 * 测试实体, 继承实体基类
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("tests")
public class TestEntity extends BaseEntity {

    /** 业务编码 */
    private String someField;
}
