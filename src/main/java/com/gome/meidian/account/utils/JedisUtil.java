package com.gome.meidian.account.utils;


import java.util.List;

import org.apache.log4j.Logger;  
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;  
  
  
import redis.clients.jedis.Jedis;  
import redis.clients.jedis.JedisPool;  
  
/** 
 *  
 * @author vic 
 * @desc resdis service 
 * 
 */  
@Component  
public class JedisUtil {  
      
    private static Logger logger = Logger.getLogger(JedisUtil.class);  
  
    @Autowired  
    private static JedisPool jedisPool;  
      
    public static void returnResource(JedisPool pool, Jedis jedis) {  
        if (jedis != null) {  
             jedis.close();
        }  
    }   
    /** 
     *类级的内部类，也就是静态的成员式内部类，该内部类的实例与外部类的实例 
     *没有绑定关系，而且只有被调用到时才会装载，从而实现了延迟加载。 
     */  
    private static class RedisUtilHolder{  
        /** 
         * 静态初始化器，由JVM来保证线程安全 
         */  
        private static JedisUtil instance = new JedisUtil();  
    }  
  
    /** 
     *当getInstance方法第一次被调用的时候，它第一次读取 
     *RedisUtilHolder.instance，导致RedisUtilHolder类得到初始化；而这个类在装载并被初始化的时候，会初始化它的静 
     *态域，从而创建RedisUtil的实例，由于是静态的域，因此只会在虚拟机装载类的时候初始化一次，并由虚拟机来保证它的线程安全性。 
     *这个模式的优势在于，getInstance方法并没有被同步，并且只是执行一个域的访问，因此延迟初始化并没有增加任何访问成本。 
     */  
    public static JedisUtil getInstance() {  
        return RedisUtilHolder.instance;  
    }  
      
    /** 
     * 获取Redis实例. 
     * @return Redis工具类实例 
     */  
    public static Jedis getJedis() {  
        Jedis jedis  = null;  
            try{   
                jedis = jedisPool.getResource();  
            } catch (Exception e) {  
                logger.error("get redis master1 failed!", e);  
                 // 销毁对象    
                jedisPool.returnBrokenResource(jedis);    
            }  
        return jedis;  
    }  
  
    /** 
     * 释放redis实例到连接池. 
     * @param jedis redis实例 
     */  
    public void closeJedis(Jedis jedis) {  
        if(jedis != null) {  
        	jedisPool.returnResource(jedis);  
        }  
    }
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    

    /*** <p>Description: 得到值</p>
    * @author wenquan
    * @date  2017年1月5日
    * @param key
    */
    public static String get(String key){  
        String value = null;  
        Jedis jedis = null;
        try {
            jedis= getJedis();
            value = jedis.get(key); 
        } catch(Exception e){
            if(jedis != null){
                jedis.close();
            }
            e.printStackTrace();
        } finally {
            returnResource(jedisPool, jedis);  
        }
        return value;  
    }  

    /*** <p>Description: 设置键值</p>
    * @author wenquan
    * @date  2017年1月5日
    * @param key value
    */
    public static String set(String key,String value){
         Jedis jedis = null;
         String ans = null;
         try {  
             jedis = getJedis();  
             ans = jedis.set(key,value);  
         } catch (Exception e) {  
             //释放redis对象  
             if(jedis != null){
                 jedis.close();
             }
             e.printStackTrace();  
         } finally {  
             //返还到连接池  
             returnResource(jedisPool, jedis);  
         }  
         return ans;
    }


    /*** <p>Description: 设置键值 并同时设置有效期</p>
    * @author wenquan
    * @date  2017年1月5日
    * @param key seconds秒数 value
    */
    public static String setex(String key,int seconds,String value){
         Jedis jedis = null;
         String ans = null;
         try {  

             String.valueOf(100);
             jedis = getJedis();  
             ans = jedis.setex(key,seconds,value);  
         } catch (Exception e) {  
             if(jedis != null){
                jedis.close();
             }
            e.printStackTrace();  
         } finally {  
             //返还到连接池  
             returnResource(jedisPool, jedis);  
         }  
         return ans;
    }

    /*** <p>Description: </p>
     * <p>通过key 和offset 从指定的位置开始将原先value替换</p>
     * <p>下标从0开始,offset表示从offset下标开始替换</p>
     * <p>如果替换的字符串长度过小则会这样</p>
     * <p>example:</p>
     * <p>value : bigsea@zto.cn</p>
     * <p>str : abc </p>
     * <P>从下标7开始替换  则结果为</p>
     * <p>RES : bigsea.abc.cn</p>
    * @author wenquan
    * @date  2017年1月6日
    * @param 
    * @return 返回替换后  value 的长度
    */
    public static Long setrange(String key,String str,int offset){
         Jedis jedis = null;  

         try {  
             jedis = getJedis();  
             return jedis.setrange(key, offset, str);  
         } catch (Exception e) {  
            if(jedis != null){
                jedis.close();
            }
            e.printStackTrace();  
            return 0L;
         } finally {  
             //返还到连接池  
             returnResource(jedisPool, jedis);  
         }
    }

    /*** <p>Description: 通过批量的key获取批量的value</p>
    * @author wenquan
    * @date  2017年1月6日
    * @param 
    * @return 成功返回value的集合, 失败返回null的集合 ,异常返回空
    */
    public static List<String> mget(String...keys){
        Jedis jedis = null;  
        List<String> values = null;
        try {  
            jedis = getJedis();  
            values = jedis.mget(keys);  
        } catch (Exception e) {  
        if(jedis != null){
                jedis.close();
            }
            e.printStackTrace();  
        } finally {  
            //返还到连接池  
            returnResource(jedisPool, jedis);  
        }
        return values;
    }

    /*** <p>Description: 批量的设置key:value,可以一个</p>
     * <p>obj.mset(new String[]{"key2","value1","key2","value2"})</p>
    * @author wenquan
    * @date  2017年1月6日
    * @param 
    * @return
    */
    public static String mset(String...keysvalues){
        Jedis jedis = null;
        String ans = null;
        try{
            jedis = getJedis();
            ans = jedis.mset(keysvalues);
        }catch (Exception e) {  
            if(jedis != null){
                jedis.close();
            }
            e.printStackTrace();  
        } finally {  
            //返还到连接池  
            returnResource(jedisPool, jedis);  
        }
        return ans;
    }

    /*** <p>Description: 通过key向指定的value值追加值</p>
    * @author wenquan
    * @date  2017年1月6日
    * @param key str 追加字符串
    */
    public static Long append(String key,String str){
         Jedis jedis = null;  

         try {  
             jedis = getJedis();  
             return jedis.append(key, str);  
         } catch (Exception e) {  
            if(jedis != null){
                jedis.close();
            }
            e.printStackTrace();  
            return 0L;
         } finally {  
             //返还到连接池  
             returnResource(jedisPool, jedis);  
         }  
    }

    /*** <p>Description: 判断key是否存在</p>
    * @author wenquan
    * @date  2017年1月6日
    * @param 
    */
    public static Boolean exists(String key){
         Jedis jedis = null;  
         try {  
             jedis = getJedis();  
             return jedis.exists(key);  
         } catch (Exception e) {  
             if(jedis != null){
                 jedis.close();
             }
             e.printStackTrace();  
             return false;
         } finally {  
            //返还到连接池  
             returnResource(jedisPool, jedis);  
         }  
    }

    /*** <p>设置key value,如果key已经存在则返回0,nx==> not exist</p>
    * @author wenquan
    * @date  2017年1月6日
    * @param 
    * @return 成功返回1 如果存在 和 发生异常 返回 0
    */
    public static Long setnx(String key,String value){
     Jedis jedis = null;  
        try {  
            jedis = getJedis();  
            return jedis.setnx(key,value);  
        } catch (Exception e) {  
            if(jedis != null){
                 jedis.close();
            }
            e.printStackTrace();  
            return 0l;
        } finally {  
            //返还到连接池  
            returnResource(jedisPool, jedis);  
        } 
    }
}
