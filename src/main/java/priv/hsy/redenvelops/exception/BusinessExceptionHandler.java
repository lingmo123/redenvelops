package priv.hsy.redenvelops.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import priv.hsy.redenvelops.entity.Result;
import priv.hsy.redenvelops.enums.ResultEnum;
import priv.hsy.redenvelops.utils.RequestUtil;
import priv.hsy.redenvelops.utils.ResultUtil;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolationException;

/**
 * @author 20013062
 */
@ControllerAdvice
@Slf4j
public class BusinessExceptionHandler {

    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    @ResponseBody
    public Result<Object> validationBodyException(MethodArgumentNotValidException exception) {
        FieldError fieldError = exception.getBindingResult().getFieldErrors().get(0);
        log.error("参数转换错误【原因 : 【{}】{}】", fieldError.getField(), fieldError.getDefaultMessage());
        return ResultUtil.result(ResultEnum.INVALID_PARAMETERS, "【" + fieldError.getField() + "】" + fieldError.getDefaultMessage());
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    @ResponseBody
    public Result<Object> validationBodyException(MissingServletRequestParameterException exception) {
        log.error("参数转换错误【原因 : 【{}】{}】", exception.getParameterName(), exception.getMessage());
        return ResultUtil.result(400, "请求参数有误:" + exception.getParameterName(), null);
    }


    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseBody
    public Result<Object> validationBodyException(ConstraintViolationException exception) {
        return ResultUtil.result(400, "请求参数有误:" + exception.getMessage(), null);
    }

    @ExceptionHandler(value = HttpRequestMethodNotSupportedException.class)
    @ResponseBody
    public Result<Object> Handle(HttpRequestMethodNotSupportedException e, HttpServletRequest request) {
        log.error("接口【{}】不支持{}方法", RequestUtil.getPathUrl(), RequestUtil.getPathMethod());
        RequestUtil.setResponseStatus(405);
        return ResultUtil.result(ResultEnum.INTERFACE_METHOD_ERROR, "接口不支持" + RequestUtil.getPathMethod() + "方法");
    }

}
