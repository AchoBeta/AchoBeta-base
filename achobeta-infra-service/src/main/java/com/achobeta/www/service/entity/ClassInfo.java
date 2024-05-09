package com.achobeta.www.service.entity;

import com.achobeta.www.common.entity.BaseIncrIDEntity;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
/**
 * @author jett
 * @since 2024/1/25
 */
@TableName("t_class_info")
@Getter
@Setter
@NoArgsConstructor
@Accessors(chain = true)
public class ClassInfo extends BaseIncrIDEntity {
    @TableField(value = "c_class_name")
    private String className;

    @TableField(value = "c_grade")
    private Integer grade;

    @TableField(value = "c_college_name")
    private String collegeName;

    @TableField(value = "c_student_num")
    private Integer studentNum;

    @TableField(value = "c_college")
    private Integer college;
}
