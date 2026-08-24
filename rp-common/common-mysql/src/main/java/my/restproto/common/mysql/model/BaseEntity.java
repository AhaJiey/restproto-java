package my.restproto.common.mysql.model;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.Instant;

/**
 * 实体基类, 提供雪花主键、审计时间与逻辑删除字段
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public abstract class BaseEntity {

    /** 主键, 雪花 */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    @TableField(fill = FieldFill.INSERT)
    private Instant createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Instant updateTime;

    /** 逻辑删除时间, 未删除为 null, 删除时由数据库写入 now() */
    @TableLogic(value = "null", delval = "now()")
    private Instant deleteTime;
}
