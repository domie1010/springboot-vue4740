package com.entity.vo;

import com.entity.TushuOrderEntity;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.util.Date;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;

/**
 * 图书借阅
 * 手机端接口返回实体辅助类
 * （主要作用去除一些不必要的字段）
 */
@TableName("tushu_order")
public class TushuOrderVO implements Serializable {
    private static final long serialVersionUID = 1L;


    /**
     * 主键
     */

    @TableField(value = "id")
    private Integer id;


    /**
     * 借阅编号
     */

    @TableField(value = "tushu_order_uuid_number")
    private String tushuOrderUuidNumber;


    /**
     * 图书
     */

    @TableField(value = "tushu_id")
    private Integer tushuId;


    /**
     * 用户
     */

    @TableField(value = "yonghu_id")
    private Integer yonghuId;


    /**
     * 借阅数量
     */

    @TableField(value = "buy_number")
    private Integer buyNumber;


    /**
     * 还书时间
     */
    @JsonFormat(locale="zh", timezone="GMT+8", pattern="yyyy-MM-dd HH:mm:ss")
	@DateTimeFormat

    @TableField(value = "tushu_order_time")
    private Date tushuOrderTime;


    /**
     * 借阅状态
     */

    @TableField(value = "tushu_order_types")
    private Integer tushuOrderTypes;


    /**
     * 订单创建时间
     */
    @JsonFormat(locale="zh", timezone="GMT+8", pattern="yyyy-MM-dd HH:mm:ss")
	@DateTimeFormat

    @TableField(value = "insert_time")
    private Date insertTime;


    /**
     * 创建时间 show3 listShow
     */
    @JsonFormat(locale="zh", timezone="GMT+8", pattern="yyyy-MM-dd HH:mm:ss")
	@DateTimeFormat

    @TableField(value = "create_time")
    private Date createTime;


    /**
	 * 设置：主键
	 */
    public Integer getId() {
        return id;
    }


    /**
	 * 获取：主键
	 */

    public void setId(Integer id) {
        this.id = id;
    }
    /**
	 * 设置：借阅编号
	 */
    public String getTushuOrderUuidNumber() {
        return tushuOrderUuidNumber;
    }


    /**
	 * 获取：借阅编号
	 */

    public void setTushuOrderUuidNumber(String tushuOrderUuidNumber) {
        this.tushuOrderUuidNumber = tushuOrderUuidNumber;
    }
    /**
	 * 设置：图书
	 */
    public Integer getTushuId() {
        return tushuId;
    }


    /**
	 * 获取：图书
	 */

    public void setTushuId(Integer tushuId) {
        this.tushuId = tushuId;
    }
    /**
	 * 设置：用户
	 */
    public Integer getYonghuId() {
        return yonghuId;
    }


    /**
	 * 获取：用户
	 */

    public void setYonghuId(Integer yonghuId) {
        this.yonghuId = yonghuId;
    }
    /**
	 * 设置：借阅数量
	 */
    public Integer getBuyNumber() {
        return buyNumber;
    }


    /**
	 * 获取：借阅数量
	 */

    public void setBuyNumber(Integer buyNumber) {
        this.buyNumber = buyNumber;
    }
    /**
	 * 设置：还书时间
	 */
    public Date getTushuOrderTime() {
        return tushuOrderTime;
    }


    /**
	 * 获取：还书时间
	 */

    public void setTushuOrderTime(Date tushuOrderTime) {
        this.tushuOrderTime = tushuOrderTime;
    }
    /**
	 * 设置：借阅状态
	 */
    public Integer getTushuOrderTypes() {
        return tushuOrderTypes;
    }


    /**
	 * 获取：借阅状态
	 */

    public void setTushuOrderTypes(Integer tushuOrderTypes) {
        this.tushuOrderTypes = tushuOrderTypes;
    }
    /**
	 * 设置：订单创建时间
	 */
    public Date getInsertTime() {
        return insertTime;
    }


    /**
	 * 获取：订单创建时间
	 */

    public void setInsertTime(Date insertTime) {
        this.insertTime = insertTime;
    }
    /**
	 * 设置：创建时间 show3 listShow
	 */
    public Date getCreateTime() {
        return createTime;
    }


    /**
	 * 获取：创建时间 show3 listShow
	 */

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

}
