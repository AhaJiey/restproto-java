package my.restproto.common.mysql.integration.support;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import my.restproto.common.mysql.model.BaseEntity;

/**
 * 集成测试实体, 继承实体基类
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@TableName("tests")
public class TestEntity extends BaseEntity {

    /** 业务字段 */
    private String someField;
}