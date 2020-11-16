package priv.hsy.redenvelops.enums;

import com.baomidou.mybatisplus.core.enums.IEnum;

import java.io.Serializable;

public interface BaseEnum<T extends Serializable> extends IEnum<T> {
    /**
     * 获取枚举的显示值
     * @return 显示值
     */
    String getDisplayName();
    /**
     * 获取枚举值
     * @return 枚举值
     */

    @Override
    T getValue();
}

