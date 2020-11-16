package priv.hsy.redenvelops.utils;

import lombok.extern.slf4j.Slf4j;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

@Slf4j
public class GetMoney {

    public static void line_cut(int money,int people ) {
        List<Double> team=new ArrayList<>();
        List<Double> result=new ArrayList<>();
        ThreadLocalRandom random=ThreadLocalRandom.current();

        double m=money-1;
        while(team.size() < people -1) {
            //不让nextDouble 为0
            double nextDouble = random.nextDouble(m)+1;
            DecimalFormat df = new DecimalFormat( "0.00" );
            String s = df.format(nextDouble);
            nextDouble = Double.parseDouble(s);

            if(!team.contains(nextDouble)) {
                team.add(nextDouble);
//                log.info("nextDouble="+nextDouble);
                System.out.println(nextDouble);
            }
        }
        Collections.sort(team);
        System.out.println("team="+team);

        for (int i = 0; i < team.size(); i++) {
            if(i== 0) {
                result.add(team.get(i));
            }else {
                result.add(team.get(i)-team.get(i-1));
                if(i==team.size()-1) {
                    result.add(money-team.get(i));
                }
            }
        }
        System.out.println("result="+result);
        //验证分割后的数是否是输入的总金额
        Optional<Double> r = result.stream().reduce(Double::sum);
        System.out.println("r.get()="+r.get());
    }
    //二倍均值法（公平版）
    public static double getRandomMoney(int remainSize,double remainMoney) {
        // remainSize 剩余的红包数量
        // remainMoney 剩余的钱
        if (remainSize == 1) {
            remainSize--;
            return (double) Math.round(remainMoney * 100) / 100;
        }
        Random r = new Random();
        double min = 0.01; //
        double max = remainMoney / remainSize * 2;

        double money = r.nextDouble() * max;
        money = money <= min ? 0.01 : money;
        money = Math.floor(money * 100) / 100;
        remainSize--;
        remainMoney-=money;
        return money;
    }

    public static void main(String[] args) throws ParseException {
//        List<Double> list=new ArrayList<>();
//        int remainSize=10;
//
//        double remainMoney=100;
//        while(remainSize>0){
//                double result = GetMoney.getRandomMoney(remainSize, remainMoney);
//                list.add(result);
//                remainMoney -= result;
//                remainSize--;
//        }
//        double total=0.0;
//        for (double a:list
//             ) {
//            total+=a;
//        }
//        log.info("list={} {} ",list,total);
//        line_cut(100,10);

        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
        String date = df.format(new Date());// new Date()为获取当前系统时间，也可使用当前时间戳
        java.text.SimpleDateFormat formatter =
                new SimpleDateFormat( "yyyy-MM-dd HH:mm:ss");
        Date Time=formatter.parse(date);
        log.info(date);
        log.info(String.valueOf(Time));
    }
}
