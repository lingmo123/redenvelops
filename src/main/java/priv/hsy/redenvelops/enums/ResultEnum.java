package priv.hsy.redenvelops.enums;

public enum ResultEnum implements BaseEnum<Integer> {

    /**
     *
     */
    INVALID_PARAMETERS(10001, "参数校验不通过"),
    INTERFACE_METHOD_ERROR(10002, "接口方法错误"),
    DATA_SELECT_NULL(10003,"未查询到相关红包数据"),
    /**
     * 红包
     */
    RED_MONEY_MIN(20000,"红包金额最小值为0.01！"),
    RED_MONEY_MAX(20001,"红包金额最大值为200！"),
    RED_COUNT_NO(20002,"红包已经抢完了！"),
    USER_MONEY_NO(20003," 你的余额不足，无法设置红包！"),
    USE_RGET_NO(20004,"您已经抢过红包了！"),
    RED_SET_SUCCESS(20005, "红包设置成功!"),
    RED_SEND_SUCCESS(20006, "成功发送红包！"),
    RED_GET_SUCCESS(20007, "成功抢到红包！"),
    RED_DETAIL_FAIL(20008, "您还没有抢过红包！"),

    SUCCESS(200, "success"),
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
