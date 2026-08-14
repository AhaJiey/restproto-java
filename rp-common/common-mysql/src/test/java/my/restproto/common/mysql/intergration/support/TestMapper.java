package my.restproto.common.mysql.intergration.support;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户测试 Mapper
 */
@Mapper
public interface TestMapper extends BaseMapper<TestEntity> {
}
