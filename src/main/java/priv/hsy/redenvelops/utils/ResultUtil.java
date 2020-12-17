package priv.hsy.redenvelops.utils;


import priv.hsy.redenvelops.entity.Result;
import priv.hsy.redenvelops.enums.ResultEnum;

import java.util.Objects;


public class ResultUtil {

    public static <T> Result<T> success() {
        return result(ResultEnum.SUCCESS);
    }

    public static <T> Result<T> fail() {
        return result(ResultEnum.FAIL);
    }

    public static <T> Result<T> success(T data) {
        return result(ResultEnum.SUCCESS, data);
    }

    public static <T> Result<T> fail(T data) {
        return result(ResultEnum.FAIL, data);
    }

    public static <T> Result<T> result(ResultEnum resultEnum) {
        return Result.<T>builder()
                .code(resultEnum.getValue())
                .message(resultEnum.getDisplayName())
                .build();
    }

    public static <T> Result<T> result(ResultEnum resultEnum, T data) {
        return Result.<T>builder()
                .code(resultEnum.getValue())
                .message(resultEnum.getDisplayName())
                .data(data)
                .build();
    }
    public static <T> Result<T> result(Integer code, String message, T data) {
        return Result.<T>builder().code(code).message(message).data(data).build();
    }
    public static <T> boolean succeeded(Result<T> result) {
        return Objects.nonNull(result) && ResultEnum.SUCCESS.getValue().equals(result.getCode());
    }

    public static boolean succeeded(Integer value) {
        return ResultEnum.SUCCESS.getValue().equals(value);
    }

}
