package my.restproto.common.mysql.model;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 实体基类, 提供雪花主键、审计时间字段与时间戳逻辑删除字段
 */
@Data
public abstract class BaseEntity {

    /** 主键, 雪花 */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /** 创建时间, 插入时自动填充 */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /** 更新时间, 插入与更新时自动填充 */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    /** 逻辑删除时间, 未删除为 null, 删除时由数据库写入 now() */
    @TableLogic(value = "null", delval = "now()")
    private LocalDateTime deleteTime;
}
