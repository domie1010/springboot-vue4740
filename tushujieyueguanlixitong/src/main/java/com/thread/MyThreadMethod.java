package com.thread;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.entity.TushuOrderEntity;
import com.entity.YonghuEntity;
import com.entity.view.TushuOrderView;
import com.service.TushuOrderService;
import com.service.YonghuService;
import com.utils.CommonUtil;
import com.utils.PageUtils;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 线程执行方法（做一些项目启动后 一直要执行的操作，比如根据时间自动更改订单状态，比如订单签收30天自动收货功能，比如根据时间来更改状态）
 */
public class MyThreadMethod extends Thread  {
    private YonghuService yonghuService;

    private TushuOrderService tushuOrderService;

    public YonghuService getYonghuService() {
        return yonghuService;
    }

    public void setYonghuService(YonghuService yonghuService) {
        this.yonghuService = yonghuService;
    }

    public TushuOrderService getTushuOrderService() {
        return tushuOrderService;
    }

    public void setTushuOrderService(TushuOrderService tushuOrderService) {
        this.tushuOrderService = tushuOrderService;
    }

    public void run() {
        while (!this.isInterrupted()) {// 线程未中断执行循环
            try {
                Thread.sleep(5000); //每隔2000ms执行一次


                Map<String, Object> params = new HashMap<>();
                params.put("limit","10000");
                params.put("tushuOrderTypes",103);
                params.put("tushuOrderTimeEnd",new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
                CommonUtil.checkMap(params);
                PageUtils page = tushuOrderService.queryPage(params);
                List<TushuOrderView> list =(List<TushuOrderView>)page.getList();
                if(list.size()>0){
                    List<YonghuEntity> yonghuEntities = new ArrayList<>();
                    for(TushuOrderView tushuOrderView:list){
                        YonghuEntity yonghuEntity = new YonghuEntity();
                        yonghuEntity.setId(tushuOrderView.getYonghuId());
                        yonghuEntity.setYonghuTypes(2);
                        yonghuEntities.add(yonghuEntity);
                    }
                    yonghuService.updateBatchById(yonghuEntities);//设置用户不可登录
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

//			 ------------------ 开始执行 ---------------------------
//            System.out.println("线程执行中:" + System.currentTimeMillis());
        }
    }
}
