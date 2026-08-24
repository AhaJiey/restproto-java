package my.restproto.common.mysql.integration.support;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 集成测试 Mapper
 */
@Mapper
public interface TestMapper extends BaseMapper<TestEntity> {
}