package priv.hsy.redenvelops.entity;

import lombok.*;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RedInfoPageBean<T> implements Serializable {
    //当前页数
    private Integer page;
    //每页显示数
    private Integer limit;
    //总页数
    private Integer totalPage;
    //总记录数
    private Integer total;
    //当前页面红包详情的数据集合
    private List<T> pageRecode;
    //返回页数的集合，用于显示index页面的上一页、下一页
    private List<Integer> pages;
}
