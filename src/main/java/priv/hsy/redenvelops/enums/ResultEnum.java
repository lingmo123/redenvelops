package priv.hsy.redenvelops.enums;

public enum ResultEnum implements BaseEnum<Integer> {

    /**
     * 红包金额最小值
     */
    REDMONEY_MIN(1000,"红包金额最小值为0.01！"),
    /**
     * 红包金额最大值
     */
    REDMONEY_MAX(1001,"红包金额最大值为200！"),
    /**
     * 红包金额最小值
     */
    REDCOUNT_MIN(2000,"红包数量最小为1！"),
    /**
     * 红包已经抢完
     */
    REDCOUNT_NO(2001,"红包已经抢完了！"),
    /**
     * 余额不足
     */
    USERMONEY_NO(3000," 你的余额不足，无法设置红包！"),
    /**
     * 余额不足
     */
    USERGET_NO(3001,"您已经抢过红包了！"),
    /**
     * 红包设置成功
     */
    REDSET_SUCCESS(8000, "红包设置成功!"),
    /**
     * 成功发送红包
     */
    REDSEND_SUCCESS(8001, "成功发送红包！"),
    /**
     * 成功抢到红包
     */
    REDGET_SUCCESS(8002, "成功抢到红包！"),
    /**
     * 成功
     */
    SUCCESS(200, "success"),
    /**
     * 失败
     */
    FAIL(-1, "fail");


    private final Integer value;
    private final String name;


    ResultEnum(Integer value, String name) {
        this.name = name;
        this.value = value;
    }

    @Override
    public String getDisplayName() {
        return this.name;
    }

    @Override
    public Integer getValue() {
        return this.value;
    }
}
