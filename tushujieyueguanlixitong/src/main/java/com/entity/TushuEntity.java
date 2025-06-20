package com.entity;

import com.annotation.ColumnInfo;
import javax.validation.constraints.*;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.lang.reflect.InvocationTargetException;
import java.io.Serializable;
import java.util.*;
import org.apache.tools.ant.util.DateUtils;
import org.springframework.format.annotation.DateTimeFormat;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.beanutils.BeanUtils;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;
import com.baomidou.mybatisplus.enums.FieldFill;
import com.utils.DateUtil;


/**
 * 图书
 *
 * @author 
 * @email
 */
@TableName("tushu")
public class TushuEntity<T> implements Serializable {
    private static final long serialVersionUID = 1L;


	public TushuEntity() {

	}

	public TushuEntity(T t) {
		try {
			BeanUtils.copyProperties(this, t);
		} catch (IllegalAccessException | InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}


    /**
     * 主键
     */
    @TableId(type = IdType.AUTO)
    @ColumnInfo(comment="主键",type="int(11)")
    @TableField(value = "id")

    private Integer id;


    /**
     * 图书名称
     */
    @ColumnInfo(comment="图书名称",type="varchar(200)")
    @TableField(value = "tushu_name")

    private String tushuName;


    /**
     * 图书编号
     */
    @ColumnInfo(comment="图书编号",type="varchar(200)")
    @TableField(value = "tushu_uuid_number")

    private String tushuUuidNumber;


    /**
     * 图书照片
     */
    @ColumnInfo(comment="图书照片",type="varchar(200)")
    @TableField(value = "tushu_photo")

    private String tushuPhoto;


    /**
     * 图书位置
     */
    @ColumnInfo(comment="图书位置",type="varchar(200)")
    @TableField(value = "tushu_address")

    private String tushuAddress;


    /**
     * 图书类型
     */
    @ColumnInfo(comment="图书类型",type="int(11)")
    @TableField(value = "tushu_types")

    private Integer tushuTypes;


    /**
     * 图书数量
     */
    @ColumnInfo(comment="图书数量",type="int(11)")
    @TableField(value = "tushu_kucun_number")

    private Integer tushuKucunNumber;


    /**
     * 图书热度
     */
    @ColumnInfo(comment="图书热度",type="int(11)")
    @TableField(value = "tushu_clicknum")

    private Integer tushuClicknum;


    /**
     * 内容介绍
     */
    @ColumnInfo(comment="内容介绍",type="text")
    @TableField(value = "tushu_content")

    private String tushuContent;


    /**
     * 是否上架
     */
    @ColumnInfo(comment="是否上架",type="int(11)")
    @TableField(value = "shangxia_types")

    private Integer shangxiaTypes;


    /**
     * 逻辑删除
     */
    @ColumnInfo(comment="逻辑删除",type="int(11)")
    @TableField(value = "tushu_delete")

    private Integer tushuDelete;


    /**
     * 录入时间
     */
    @JsonFormat(locale="zh", timezone="GMT+8", pattern="yyyy-MM-dd HH:mm:ss")
	@DateTimeFormat
    @ColumnInfo(comment="录入时间",type="timestamp")
    @TableField(value = "insert_time",fill = FieldFill.INSERT)

    private Date insertTime;


    /**
     * 创建时间
     */
    @JsonFormat(locale="zh", timezone="GMT+8", pattern="yyyy-MM-dd HH:mm:ss")
	@DateTimeFormat
    @ColumnInfo(comment="创建时间",type="timestamp")
    @TableField(value = "create_time",fill = FieldFill.INSERT)

    private Date createTime;


    /**
	 * 获取：主键
	 */
    public Integer getId() {
        return id;
    }
    /**
	 * 设置：主键
	 */

    public void setId(Integer id) {
        this.id = id;
    }
    /**
	 * 获取：图书名称
	 */
    public String getTushuName() {
        return tushuName;
    }
    /**
	 * 设置：图书名称
	 */

    public void setTushuName(String tushuName) {
        this.tushuName = tushuName;
    }
    /**
	 * 获取：图书编号
	 */
    public String getTushuUuidNumber() {
        return tushuUuidNumber;
    }
    /**
	 * 设置：图书编号
	 */

    public void setTushuUuidNumber(String tushuUuidNumber) {
        this.tushuUuidNumber = tushuUuidNumber;
    }
    /**
	 * 获取：图书照片
	 */
    public String getTushuPhoto() {
        return tushuPhoto;
    }
    /**
	 * 设置：图书照片
	 */

    public void setTushuPhoto(String tushuPhoto) {
        this.tushuPhoto = tushuPhoto;
    }
    /**
	 * 获取：图书位置
	 */
    public String getTushuAddress() {
        return tushuAddress;
    }
    /**
	 * 设置：图书位置
	 */

    public void setTushuAddress(String tushuAddress) {
        this.tushuAddress = tushuAddress;
    }
    /**
	 * 获取：图书类型
	 */
    public Integer getTushuTypes() {
        return tushuTypes;
    }
    /**
	 * 设置：图书类型
	 */

    public void setTushuTypes(Integer tushuTypes) {
        this.tushuTypes = tushuTypes;
    }
    /**
	 * 获取：图书数量
	 */
    public Integer getTushuKucunNumber() {
        return tushuKucunNumber;
    }
    /**
	 * 设置：图书数量
	 */

    public void setTushuKucunNumber(Integer tushuKucunNumber) {
        this.tushuKucunNumber = tushuKucunNumber;
    }
    /**
	 * 获取：图书热度
	 */
    public Integer getTushuClicknum() {
        return tushuClicknum;
    }
    /**
	 * 设置：图书热度
	 */

    public void setTushuClicknum(Integer tushuClicknum) {
        this.tushuClicknum = tushuClicknum;
    }
    /**
	 * 获取：内容介绍
	 */
    public String getTushuContent() {
        return tushuContent;
    }
    /**
	 * 设置：内容介绍
	 */

    public void setTushuContent(String tushuContent) {
        this.tushuContent = tushuContent;
    }
    /**
	 * 获取：是否上架
	 */
    public Integer getShangxiaTypes() {
        return shangxiaTypes;
    }
    /**
	 * 设置：是否上架
	 */

    public void setShangxiaTypes(Integer shangxiaTypes) {
        this.shangxiaTypes = shangxiaTypes;
    }
    /**
	 * 获取：逻辑删除
	 */
    public Integer getTushuDelete() {
        return tushuDelete;
    }
    /**
	 * 设置：逻辑删除
	 */

    public void setTushuDelete(Integer tushuDelete) {
        this.tushuDelete = tushuDelete;
    }
    /**
	 * 获取：录入时间
	 */
    public Date getInsertTime() {
        return insertTime;
    }
    /**
	 * 设置：录入时间
	 */

    public void setInsertTime(Date insertTime) {
        this.insertTime = insertTime;
    }
    /**
	 * 获取：创建时间
	 */
    public Date getCreateTime() {
        return createTime;
    }
    /**
	 * 设置：创建时间
	 */

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    @Override
    public String toString() {
        return "Tushu{" +
            ", id=" + id +
            ", tushuName=" + tushuName +
            ", tushuUuidNumber=" + tushuUuidNumber +
            ", tushuPhoto=" + tushuPhoto +
            ", tushuAddress=" + tushuAddress +
            ", tushuTypes=" + tushuTypes +
            ", tushuKucunNumber=" + tushuKucunNumber +
            ", tushuClicknum=" + tushuClicknum +
            ", tushuContent=" + tushuContent +
            ", shangxiaTypes=" + shangxiaTypes +
            ", tushuDelete=" + tushuDelete +
            ", insertTime=" + DateUtil.convertString(insertTime,"yyyy-MM-dd") +
            ", createTime=" + DateUtil.convertString(createTime,"yyyy-MM-dd") +
        "}";
    }
}
