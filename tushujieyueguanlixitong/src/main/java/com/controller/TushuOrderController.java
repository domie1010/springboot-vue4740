
package com.controller;

import java.io.File;
import java.math.BigDecimal;
import java.net.URL;
import java.text.SimpleDateFormat;
import com.alibaba.fastjson.JSONObject;
import java.util.*;
import org.springframework.beans.BeanUtils;
import javax.servlet.http.HttpServletRequest;
import org.springframework.web.context.ContextLoader;
import javax.servlet.ServletContext;
import com.service.TokenService;
import com.utils.*;
import java.lang.reflect.InvocationTargetException;

import com.service.DictionaryService;
import org.apache.commons.lang3.StringUtils;
import com.annotation.IgnoreAuth;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.entity.*;
import com.entity.view.*;
import com.service.*;
import com.utils.PageUtils;
import com.utils.R;
import com.alibaba.fastjson.*;

/**
 * 图书借阅
 * 后端接口
 * @author
 * @email
*/
@RestController
@Controller
@RequestMapping("/tushuOrder")
public class TushuOrderController {
    private static final Logger logger = LoggerFactory.getLogger(TushuOrderController.class);

    private static final String TABLE_NAME = "tushuOrder";

    @Autowired
    private TushuOrderService tushuOrderService;


    @Autowired
    private TokenService tokenService;
    @Autowired
    private DictionaryService dictionaryService;

    //级联表非注册的service
    @Autowired
    private TushuService tushuService;
    //注册表service
    @Autowired
    private YonghuService yonghuService;


    /**
    * 后端列表
    */
    @RequestMapping("/page")
    public R page(@RequestParam Map<String, Object> params, HttpServletRequest request){
        logger.debug("page方法:,,Controller:{},,params:{}",this.getClass().getName(),JSONObject.toJSONString(params));
        String role = String.valueOf(request.getSession().getAttribute("role"));
        if(false)
            return R.error(511,"永不会进入");
        else if("用户".equals(role))
            params.put("yonghuId",request.getSession().getAttribute("userId"));
        CommonUtil.checkMap(params);
        PageUtils page = tushuOrderService.queryPage(params);

        //字典表数据转换
        List<TushuOrderView> list =(List<TushuOrderView>)page.getList();
        for(TushuOrderView c:list){
            //修改对应字典表字段
            dictionaryService.dictionaryConvert(c, request);
        }
        return R.ok().put("data", page);
    }

    /**
    * 后端详情
    */
    @RequestMapping("/info/{id}")
    public R info(@PathVariable("id") Long id, HttpServletRequest request){
        logger.debug("info方法:,,Controller:{},,id:{}",this.getClass().getName(),id);
        TushuOrderEntity tushuOrder = tushuOrderService.selectById(id);
        if(tushuOrder !=null){
            //entity转view
            TushuOrderView view = new TushuOrderView();
            BeanUtils.copyProperties( tushuOrder , view );//把实体数据重构到view中
            //级联表 图书
            //级联表
            TushuEntity tushu = tushuService.selectById(tushuOrder.getTushuId());
            if(tushu != null){
            BeanUtils.copyProperties( tushu , view ,new String[]{ "id", "createTime", "insertTime", "updateTime", "yonghuId"});//把级联的数据添加到view中,并排除id和创建时间字段,当前表的级联注册表
            view.setTushuId(tushu.getId());
            }
            //级联表 用户
            //级联表
            YonghuEntity yonghu = yonghuService.selectById(tushuOrder.getYonghuId());
            if(yonghu != null){
            BeanUtils.copyProperties( yonghu , view ,new String[]{ "id", "createTime", "insertTime", "updateTime", "yonghuId"});//把级联的数据添加到view中,并排除id和创建时间字段,当前表的级联注册表
            view.setYonghuId(yonghu.getId());
            }
            //修改对应字典表字段
            dictionaryService.dictionaryConvert(view, request);
            return R.ok().put("data", view);
        }else {
            return R.error(511,"查不到数据");
        }

    }

    /**
    * 后端保存
    */
    @RequestMapping("/save")
    public R save(@RequestBody TushuOrderEntity tushuOrder, HttpServletRequest request){
        logger.debug("save方法:,,Controller:{},,tushuOrder:{}",this.getClass().getName(),tushuOrder.toString());

        String role = String.valueOf(request.getSession().getAttribute("role"));
        if(false)
            return R.error(511,"永远不会进入");
        else if("用户".equals(role))
            tushuOrder.setYonghuId(Integer.valueOf(String.valueOf(request.getSession().getAttribute("userId"))));

        tushuOrder.setCreateTime(new Date());
        tushuOrder.setInsertTime(new Date());
        tushuOrderService.insert(tushuOrder);

        return R.ok();
    }

    /**
    * 后端修改
    */
    @RequestMapping("/update")
    public R update(@RequestBody TushuOrderEntity tushuOrder, HttpServletRequest request) throws NoSuchFieldException, ClassNotFoundException, IllegalAccessException, InstantiationException {
        logger.debug("update方法:,,Controller:{},,tushuOrder:{}",this.getClass().getName(),tushuOrder.toString());
        TushuOrderEntity oldTushuOrderEntity = tushuOrderService.selectById(tushuOrder.getId());//查询原先数据

        String role = String.valueOf(request.getSession().getAttribute("role"));
//        if(false)
//            return R.error(511,"永远不会进入");
//        else if("用户".equals(role))
//            tushuOrder.setYonghuId(Integer.valueOf(String.valueOf(request.getSession().getAttribute("userId"))));

            tushuOrderService.updateById(tushuOrder);//根据id更新
            return R.ok();
    }



    /**
    * 删除
    */
    @RequestMapping("/delete")
    public R delete(@RequestBody Integer[] ids, HttpServletRequest request){
        logger.debug("delete:,,Controller:{},,ids:{}",this.getClass().getName(),ids.toString());
        List<TushuOrderEntity> oldTushuOrderList =tushuOrderService.selectBatchIds(Arrays.asList(ids));//要删除的数据
        tushuOrderService.deleteBatchIds(Arrays.asList(ids));

        return R.ok();
    }


    /**
     * 批量上传
     */
    @RequestMapping("/batchInsert")
    public R save( String fileName, HttpServletRequest request){
        logger.debug("batchInsert方法:,,Controller:{},,fileName:{}",this.getClass().getName(),fileName);
        Integer yonghuId = Integer.valueOf(String.valueOf(request.getSession().getAttribute("userId")));
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            List<TushuOrderEntity> tushuOrderList = new ArrayList<>();//上传的东西
            Map<String, List<String>> seachFields= new HashMap<>();//要查询的字段
            Date date = new Date();
            int lastIndexOf = fileName.lastIndexOf(".");
            if(lastIndexOf == -1){
                return R.error(511,"该文件没有后缀");
            }else{
                String suffix = fileName.substring(lastIndexOf);
                if(!".xls".equals(suffix)){
                    return R.error(511,"只支持后缀为xls的excel文件");
                }else{
                    URL resource = this.getClass().getClassLoader().getResource("static/upload/" + fileName);//获取文件路径
                    File file = new File(resource.getFile());
                    if(!file.exists()){
                        return R.error(511,"找不到上传文件，请联系管理员");
                    }else{
                        List<List<String>> dataList = PoiUtil.poiImport(file.getPath());//读取xls文件
                        dataList.remove(0);//删除第一行，因为第一行是提示
                        for(List<String> data:dataList){
                            //循环
                            TushuOrderEntity tushuOrderEntity = new TushuOrderEntity();
//                            tushuOrderEntity.setTushuOrderUuidNumber(data.get(0));                    //借阅编号 要改的
//                            tushuOrderEntity.setTushuId(Integer.valueOf(data.get(0)));   //图书 要改的
//                            tushuOrderEntity.setYonghuId(Integer.valueOf(data.get(0)));   //用户 要改的
//                            tushuOrderEntity.setBuyNumber(Integer.valueOf(data.get(0)));   //借阅数量 要改的
//                            tushuOrderEntity.setTushuOrderTime(sdf.parse(data.get(0)));          //还书时间 要改的
//                            tushuOrderEntity.setTushuOrderTypes(Integer.valueOf(data.get(0)));   //借阅状态 要改的
//                            tushuOrderEntity.setInsertTime(date);//时间
//                            tushuOrderEntity.setCreateTime(date);//时间
                            tushuOrderList.add(tushuOrderEntity);


                            //把要查询是否重复的字段放入map中
                                //借阅编号
                                if(seachFields.containsKey("tushuOrderUuidNumber")){
                                    List<String> tushuOrderUuidNumber = seachFields.get("tushuOrderUuidNumber");
                                    tushuOrderUuidNumber.add(data.get(0));//要改的
                                }else{
                                    List<String> tushuOrderUuidNumber = new ArrayList<>();
                                    tushuOrderUuidNumber.add(data.get(0));//要改的
                                    seachFields.put("tushuOrderUuidNumber",tushuOrderUuidNumber);
                                }
                        }

                        //查询是否重复
                         //借阅编号
                        List<TushuOrderEntity> tushuOrderEntities_tushuOrderUuidNumber = tushuOrderService.selectList(new EntityWrapper<TushuOrderEntity>().in("tushu_order_uuid_number", seachFields.get("tushuOrderUuidNumber")));
                        if(tushuOrderEntities_tushuOrderUuidNumber.size() >0 ){
                            ArrayList<String> repeatFields = new ArrayList<>();
                            for(TushuOrderEntity s:tushuOrderEntities_tushuOrderUuidNumber){
                                repeatFields.add(s.getTushuOrderUuidNumber());
                            }
                            return R.error(511,"数据库的该表中的 [借阅编号] 字段已经存在 存在数据为:"+repeatFields.toString());
                        }
                        tushuOrderService.insertBatch(tushuOrderList);
                        return R.ok();
                    }
                }
            }
        }catch (Exception e){
            e.printStackTrace();
            return R.error(511,"批量插入数据异常，请联系管理员");
        }
    }




    /**
    * 前端列表
    */
    @IgnoreAuth
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params, HttpServletRequest request){
        logger.debug("list方法:,,Controller:{},,params:{}",this.getClass().getName(),JSONObject.toJSONString(params));

        CommonUtil.checkMap(params);
        PageUtils page = tushuOrderService.queryPage(params);

        //字典表数据转换
        List<TushuOrderView> list =(List<TushuOrderView>)page.getList();
        for(TushuOrderView c:list)
            dictionaryService.dictionaryConvert(c, request); //修改对应字典表字段

        return R.ok().put("data", page);
    }

    /**
    * 前端详情
    */
    @RequestMapping("/detail/{id}")
    public R detail(@PathVariable("id") Long id, HttpServletRequest request){
        logger.debug("detail方法:,,Controller:{},,id:{}",this.getClass().getName(),id);
        TushuOrderEntity tushuOrder = tushuOrderService.selectById(id);
            if(tushuOrder !=null){


                //entity转view
                TushuOrderView view = new TushuOrderView();
                BeanUtils.copyProperties( tushuOrder , view );//把实体数据重构到view中

                //级联表
                    TushuEntity tushu = tushuService.selectById(tushuOrder.getTushuId());
                if(tushu != null){
                    BeanUtils.copyProperties( tushu , view ,new String[]{ "id", "createDate"});//把级联的数据添加到view中,并排除id和创建时间字段
                    view.setTushuId(tushu.getId());
                }
                //级联表
                    YonghuEntity yonghu = yonghuService.selectById(tushuOrder.getYonghuId());
                if(yonghu != null){
                    BeanUtils.copyProperties( yonghu , view ,new String[]{ "id", "createDate"});//把级联的数据添加到view中,并排除id和创建时间字段
                    view.setYonghuId(yonghu.getId());
                }
                //修改对应字典表字段
                dictionaryService.dictionaryConvert(view, request);
                return R.ok().put("data", view);
            }else {
                return R.error(511,"查不到数据");
            }
    }


    /**
    * 前端保存
    */
    @RequestMapping("/add")
    public R add(@RequestBody TushuOrderEntity tushuOrder, HttpServletRequest request){
        logger.debug("add方法:,,Controller:{},,tushuOrder:{}",this.getClass().getName(),tushuOrder.toString());


        Integer userId = (Integer) request.getSession().getAttribute("userId");
        //计算所获得积分
        YonghuEntity yonghuEntity = yonghuService.selectById(userId);
        if(yonghuEntity == null)
            return R.error(511,"用户不能为空");

        TushuOrderEntity tushuOrderEntity = tushuOrderService.selectOne(new EntityWrapper<TushuOrderEntity>()
                .eq("yonghu_id", userId)
                .in("tushu_order_types", new Integer[]{103, 101})
        );
        if(tushuOrderEntity != null){
            if(tushuOrderEntity.getTushuOrderTypes()==101){
                return R.error("有正在申请借阅的图书,请等待审核通过或者取消借阅后再借阅图书");
            }else if(tushuOrderEntity.getTushuOrderTypes()==103){
                return R.error("有已借出的图书,请还书后再借阅");
            }
        }


        TushuEntity tushuEntity = tushuService.selectById(tushuOrder.getTushuId());
            if(tushuEntity == null){
                return R.error(511,"查不到该图书");
            }
            // Double tushuNewMoney = tushuEntity.getTushuNewMoney();

            if(false){
            }
            else if((tushuEntity.getTushuKucunNumber() -tushuOrder.getBuyNumber())<1){
                return R.error(511,"借阅数量不能大于库存数量");
            }

            tushuOrder.setTushuOrderTypes(101); //设置订单状态为已申请借阅
            tushuOrder.setYonghuId(userId); //设置订单支付人id
            tushuOrder.setTushuOrderUuidNumber(String.valueOf(new Date().getTime()));
            tushuOrder.setInsertTime(new Date());
            tushuOrder.setCreateTime(new Date());


                tushuEntity.setTushuKucunNumber( tushuEntity.getTushuKucunNumber() -tushuOrder.getBuyNumber());
                tushuService.updateById(tushuEntity);



                tushuOrderService.insert(tushuOrder);//新增订单


            return R.ok();
    }


    /**
    * 取消借阅
    */
    @RequestMapping("/refund")
    public R refund(Integer id, HttpServletRequest request){
        logger.debug("refund方法:,,Controller:{},,id:{}",this.getClass().getName(),id);
        String role = String.valueOf(request.getSession().getAttribute("role"));

            TushuOrderEntity tushuOrder = tushuOrderService.selectById(id);
            Integer buyNumber = tushuOrder.getBuyNumber();
            Integer tushuId = tushuOrder.getTushuId();
            if(tushuId == null)
                return R.error(511,"查不到该图书");
            TushuEntity tushuEntity = tushuService.selectById(tushuId);
            if(tushuEntity == null)
                return R.error(511,"查不到该图书");

            Integer userId = (Integer) request.getSession().getAttribute("userId");
            YonghuEntity yonghuEntity = yonghuService.selectById(userId);


            tushuEntity.setTushuKucunNumber(tushuEntity.getTushuKucunNumber() + buyNumber);



            tushuOrder.setTushuOrderTypes(102);//设置订单状态为已取消借阅
            tushuOrderService.updateById(tushuOrder);//根据id更新
            yonghuService.updateById(yonghuEntity);//更新用户信息
            tushuService.updateById(tushuEntity);//更新订单中图书的信息

            return R.ok();
    }

    /**
     * 确认借出
     */
    @RequestMapping("/deliver")
    public R deliver(Integer id ){
        logger.debug("refund:,,Controller:{},,ids:{}",this.getClass().getName(),id.toString());
        TushuOrderEntity  tushuOrderEntity = tushuOrderService.selectById(id);
        tushuOrderEntity.setTushuOrderTypes(103);//设置订单状态为确认借出
        tushuOrderService.updateById( tushuOrderEntity);

        return R.ok();
    }


    /**
     * 确认归还
     */
    @RequestMapping("/receiving")
    public R receiving(Integer id){
        logger.debug("refund:,,Controller:{},,ids:{}",this.getClass().getName(),id.toString());
        TushuOrderEntity  tushuOrderEntity = tushuOrderService.selectById(id);
        tushuOrderEntity.setTushuOrderTypes(104);//设置订单状态为确认归还
        tushuOrderService.updateById( tushuOrderEntity);

        TushuEntity tushuEntity = tushuService.selectById(tushuOrderEntity.getTushuId());
        if(tushuEntity!= null){
            tushuEntity.setTushuKucunNumber(tushuEntity.getTushuKucunNumber()+tushuOrderEntity.getBuyNumber());
            tushuService.updateById(tushuEntity);
        }


        return R.ok();
    }

}
