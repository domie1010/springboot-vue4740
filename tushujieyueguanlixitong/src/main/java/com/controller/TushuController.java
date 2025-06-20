
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
 * 图书
 * 后端接口
 * @author
 * @email
*/
@RestController
@Controller
@RequestMapping("/tushu")
public class TushuController {
    private static final Logger logger = LoggerFactory.getLogger(TushuController.class);

    private static final String TABLE_NAME = "tushu";

    @Autowired
    private TushuService tushuService;


    @Autowired
    private TokenService tokenService;
    @Autowired
    private DictionaryService dictionaryService;

    @Autowired
    private TushuOrderService tushuOrderService;
    @Autowired
    private TushuCollectionService tushuCollectionService;
    //级联表非注册的service
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
        params.put("tushuDeleteStart",1);params.put("tushuDeleteEnd",1);
        CommonUtil.checkMap(params);
        PageUtils page = tushuService.queryPage(params);

        //字典表数据转换
        List<TushuView> list =(List<TushuView>)page.getList();
        for(TushuView c:list){
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
        TushuEntity tushu = tushuService.selectById(id);
        if(tushu !=null){
            //entity转view
            TushuView view = new TushuView();
            BeanUtils.copyProperties( tushu , view );//把实体数据重构到view中
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
    public R save(@RequestBody TushuEntity tushu, HttpServletRequest request){
        logger.debug("save方法:,,Controller:{},,tushu:{}",this.getClass().getName(),tushu.toString());

        String role = String.valueOf(request.getSession().getAttribute("role"));
        if(false)
            return R.error(511,"永远不会进入");

        Wrapper<TushuEntity> queryWrapper = new EntityWrapper<TushuEntity>()
            .eq("tushu_name", tushu.getTushuName())
            .eq("tushu_address", tushu.getTushuAddress())
            .eq("tushu_types", tushu.getTushuTypes())
            .eq("tushu_kucun_number", tushu.getTushuKucunNumber())
            .eq("tushu_clicknum", tushu.getTushuClicknum())
            .eq("shangxia_types", tushu.getShangxiaTypes())
            .eq("tushu_delete", tushu.getTushuDelete())
            ;

        logger.info("sql语句:"+queryWrapper.getSqlSegment());
        TushuEntity tushuEntity = tushuService.selectOne(queryWrapper);
        if(tushuEntity==null){
            tushu.setTushuClicknum(1);
            tushu.setShangxiaTypes(1);
            tushu.setTushuDelete(1);
            tushu.setInsertTime(new Date());
            tushu.setCreateTime(new Date());
            tushuService.insert(tushu);
            return R.ok();
        }else {
            return R.error(511,"表中有相同数据");
        }
    }

    /**
    * 后端修改
    */
    @RequestMapping("/update")
    public R update(@RequestBody TushuEntity tushu, HttpServletRequest request) throws NoSuchFieldException, ClassNotFoundException, IllegalAccessException, InstantiationException {
        logger.debug("update方法:,,Controller:{},,tushu:{}",this.getClass().getName(),tushu.toString());
        TushuEntity oldTushuEntity = tushuService.selectById(tushu.getId());//查询原先数据

        String role = String.valueOf(request.getSession().getAttribute("role"));
//        if(false)
//            return R.error(511,"永远不会进入");
        if("".equals(tushu.getTushuPhoto()) || "null".equals(tushu.getTushuPhoto())){
                tushu.setTushuPhoto(null);
        }

            tushuService.updateById(tushu);//根据id更新
            return R.ok();
    }



    /**
    * 删除
    */
    @RequestMapping("/delete")
    public R delete(@RequestBody Integer[] ids, HttpServletRequest request){
        logger.debug("delete:,,Controller:{},,ids:{}",this.getClass().getName(),ids.toString());
        List<TushuEntity> oldTushuList =tushuService.selectBatchIds(Arrays.asList(ids));//要删除的数据
        ArrayList<TushuEntity> list = new ArrayList<>();
        for(Integer id:ids){
            TushuEntity tushuEntity = new TushuEntity();
            tushuEntity.setId(id);
            tushuEntity.setTushuDelete(2);
            list.add(tushuEntity);
        }
        if(list != null && list.size() >0){
            tushuService.updateBatchById(list);
        }

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
            List<TushuEntity> tushuList = new ArrayList<>();//上传的东西
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
                            TushuEntity tushuEntity = new TushuEntity();
//                            tushuEntity.setTushuName(data.get(0));                    //图书名称 要改的
//                            tushuEntity.setTushuUuidNumber(data.get(0));                    //图书编号 要改的
//                            tushuEntity.setTushuPhoto("");//详情和图片
//                            tushuEntity.setTushuAddress(data.get(0));                    //图书位置 要改的
//                            tushuEntity.setTushuTypes(Integer.valueOf(data.get(0)));   //图书类型 要改的
//                            tushuEntity.setTushuKucunNumber(Integer.valueOf(data.get(0)));   //图书数量 要改的
//                            tushuEntity.setTushuClicknum(Integer.valueOf(data.get(0)));   //图书热度 要改的
//                            tushuEntity.setTushuContent("");//详情和图片
//                            tushuEntity.setShangxiaTypes(Integer.valueOf(data.get(0)));   //是否上架 要改的
//                            tushuEntity.setTushuDelete(1);//逻辑删除字段
//                            tushuEntity.setInsertTime(date);//时间
//                            tushuEntity.setCreateTime(date);//时间
                            tushuList.add(tushuEntity);


                            //把要查询是否重复的字段放入map中
                                //图书编号
                                if(seachFields.containsKey("tushuUuidNumber")){
                                    List<String> tushuUuidNumber = seachFields.get("tushuUuidNumber");
                                    tushuUuidNumber.add(data.get(0));//要改的
                                }else{
                                    List<String> tushuUuidNumber = new ArrayList<>();
                                    tushuUuidNumber.add(data.get(0));//要改的
                                    seachFields.put("tushuUuidNumber",tushuUuidNumber);
                                }
                        }

                        //查询是否重复
                         //图书编号
                        List<TushuEntity> tushuEntities_tushuUuidNumber = tushuService.selectList(new EntityWrapper<TushuEntity>().in("tushu_uuid_number", seachFields.get("tushuUuidNumber")).eq("tushu_delete", 1));
                        if(tushuEntities_tushuUuidNumber.size() >0 ){
                            ArrayList<String> repeatFields = new ArrayList<>();
                            for(TushuEntity s:tushuEntities_tushuUuidNumber){
                                repeatFields.add(s.getTushuUuidNumber());
                            }
                            return R.error(511,"数据库的该表中的 [图书编号] 字段已经存在 存在数据为:"+repeatFields.toString());
                        }
                        tushuService.insertBatch(tushuList);
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
    * 个性推荐
    */
    @IgnoreAuth
    @RequestMapping("/gexingtuijian")
    public R gexingtuijian(@RequestParam Map<String, Object> params, HttpServletRequest request){
        logger.debug("gexingtuijian方法:,,Controller:{},,params:{}",this.getClass().getName(),JSONObject.toJSONString(params));
        CommonUtil.checkMap(params);
        List<TushuView> returnTushuViewList = new ArrayList<>();

        //查询订单
        Map<String, Object> params1 = new HashMap<>(params);params1.put("sort","id");params1.put("yonghuId",request.getSession().getAttribute("userId"));
        PageUtils pageUtils = tushuOrderService.queryPage(params1);
        List<TushuOrderView> orderViewsList =(List<TushuOrderView>)pageUtils.getList();
        Map<Integer,Integer> typeMap=new HashMap<>();//购买的类型list
        for(TushuOrderView orderView:orderViewsList){
            Integer tushuTypes = orderView.getTushuTypes();
            if(typeMap.containsKey(tushuTypes)){
                typeMap.put(tushuTypes,typeMap.get(tushuTypes)+1);
            }else{
                typeMap.put(tushuTypes,1);
            }
        }
        List<Integer> typeList = new ArrayList<>();//排序后的有序的类型 按最多到最少
        typeMap.entrySet().stream().sorted((o1, o2) -> o2.getValue() - o1.getValue()).forEach(e -> typeList.add(e.getKey()));//排序
        Integer limit = Integer.valueOf(String.valueOf(params.get("limit")));
        for(Integer type:typeList){
            Map<String, Object> params2 = new HashMap<>(params);params2.put("tushuTypes",type);
            PageUtils pageUtils1 = tushuService.queryPage(params2);
            List<TushuView> tushuViewList =(List<TushuView>)pageUtils1.getList();
            returnTushuViewList.addAll(tushuViewList);
            if(returnTushuViewList.size()>= limit) break;//返回的推荐数量大于要的数量 跳出循环
        }
        //正常查询出来商品,用于补全推荐缺少的数据
        PageUtils page = tushuService.queryPage(params);
        if(returnTushuViewList.size()<limit){//返回数量还是小于要求数量
            int toAddNum = limit - returnTushuViewList.size();//要添加的数量
            List<TushuView> tushuViewList =(List<TushuView>)page.getList();
            for(TushuView tushuView:tushuViewList){
                Boolean addFlag = true;
                for(TushuView returnTushuView:returnTushuViewList){
                    if(returnTushuView.getId().intValue() ==tushuView.getId().intValue()) addFlag=false;//返回的数据中已存在此商品
                }
                if(addFlag){
                    toAddNum=toAddNum-1;
                    returnTushuViewList.add(tushuView);
                    if(toAddNum==0) break;//够数量了
                }
            }
        }else {
            returnTushuViewList = returnTushuViewList.subList(0, limit);
        }

        for(TushuView c:returnTushuViewList)
            dictionaryService.dictionaryConvert(c, request);
        page.setList(returnTushuViewList);
        return R.ok().put("data", page);
    }

    /**
    * 前端列表
    */
    @IgnoreAuth
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params, HttpServletRequest request){
        logger.debug("list方法:,,Controller:{},,params:{}",this.getClass().getName(),JSONObject.toJSONString(params));

        CommonUtil.checkMap(params);
        PageUtils page = tushuService.queryPage(params);

        //字典表数据转换
        List<TushuView> list =(List<TushuView>)page.getList();
        for(TushuView c:list)
            dictionaryService.dictionaryConvert(c, request); //修改对应字典表字段

        return R.ok().put("data", page);
    }

    /**
    * 前端详情
    */
    @RequestMapping("/detail/{id}")
    public R detail(@PathVariable("id") Long id, HttpServletRequest request){
        logger.debug("detail方法:,,Controller:{},,id:{}",this.getClass().getName(),id);
        TushuEntity tushu = tushuService.selectById(id);
            if(tushu !=null){

                //点击数量加1
                tushu.setTushuClicknum(tushu.getTushuClicknum()+1);
                tushuService.updateById(tushu);

                //entity转view
                TushuView view = new TushuView();
                BeanUtils.copyProperties( tushu , view );//把实体数据重构到view中

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
    public R add(@RequestBody TushuEntity tushu, HttpServletRequest request){
        logger.debug("add方法:,,Controller:{},,tushu:{}",this.getClass().getName(),tushu.toString());
        Wrapper<TushuEntity> queryWrapper = new EntityWrapper<TushuEntity>()
            .eq("tushu_name", tushu.getTushuName())
            .eq("tushu_uuid_number", tushu.getTushuUuidNumber())
            .eq("tushu_address", tushu.getTushuAddress())
            .eq("tushu_types", tushu.getTushuTypes())
            .eq("tushu_kucun_number", tushu.getTushuKucunNumber())
            .eq("tushu_clicknum", tushu.getTushuClicknum())
            .eq("shangxia_types", tushu.getShangxiaTypes())
            .eq("tushu_delete", tushu.getTushuDelete())
            ;
        logger.info("sql语句:"+queryWrapper.getSqlSegment());
        TushuEntity tushuEntity = tushuService.selectOne(queryWrapper);
        if(tushuEntity==null){
            tushu.setTushuDelete(1);
            tushu.setInsertTime(new Date());
            tushu.setCreateTime(new Date());
        tushuService.insert(tushu);

            return R.ok();
        }else {
            return R.error(511,"表中有相同数据");
        }
    }

}
