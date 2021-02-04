package com.github.huoyu820125.idstar.self;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * @Title 自身
 * @Athor SunQian
 * @CreateTime 2021/2/1 16:32
 * @Description: todo
 */
public class Self<T> {
    private final Logger log = LoggerFactory.getLogger(getClass());

    //用于感知自身的特征值列表,每次启动动态生成特征值
    private List<T> features = new ArrayList<>();
    private IBody<T> body;
    private List<IBody<T>> bodys;
    private IGrowAlgorithm<T> growAlgorithm;
    private Integer growCount;

    /**
     * @title: 构造
     * @author: SunQian
     * @date: 2021/2/4 14:02
     * @descritpion: todo
     * @param algorithm 生长算法
     * @param growCount 生长次数
     * @return todo
    */
    public Self(IGrowAlgorithm<T> algorithm, Integer growCount) {
        if (null == algorithm) {
            throw new RuntimeException("缺少算法");
        }
        if (null == growCount) {
            throw new RuntimeException("缺少生长次数");
        }
        this.growAlgorithm = algorithm;
        this.growCount = growCount;
    }

    /**
     * @title: 觉醒
     * @author: SunQian
     * @date: 2021/2/4 10:54
     * @descritpion: todo
     * @param bodys 可能是自己的身体列表
     * @return true觉醒认知到的属于自己的身体，false bodys中不存在自己的身体，无法觉醒
    */
    public Boolean wake(List<IBody<T>> bodys) {
        this.bodys = bodys;
        int i = 0;
        for (i = 0; i < bodys.size(); i++) {
            if (isSelf(bodys.get(i))) {
                body = bodys.get(i);
                return true;
            }
        }

        return false;
    }

    /**
     * @title: 身份
     * @author: SunQian
     * @date: 2021/2/4 14:46
     * @descritpion: todo
     * @return 觉醒到的身份(身体)，null可能尚未觉醒
     */
    public IBody body() {
        return body;
    }

    /**
     * @title: 上次长出的特征
     * @author: SunQian
     * @date: 2021/2/4 14:55
     * @descritpion: todo
     * @return todo
    */
    public T lastFeatrue() {
        if (features.isEmpty()) {
            return null;
        }

        return features.get(features.size() - 1);
    }

    /**
     * @title: 生长
     * @author: SunQian
     * @date: 2021/2/4 14:12
     * @descritpion: todo
     * @return 长出的特征
    */
    private T grow() {
        while (true) {
            T featrue = growAlgorithm.grow();
            Boolean exist = features.stream().filter(e -> growAlgorithm.isSame(e, featrue)).findAny().isPresent();
            if (!exist) {
                features.add(featrue);
                return featrue;
            }
        }
    }

    /**
     * @title: 触摸身体
     * @author: SunQian
     * @date: 2021/2/4 14:18
     * @descritpion: todo
     * @param body      身体
     * @param tryCount  没摸到，重试次数
     * @return 触摸到的特征
    */
    private T touch(IBody<T> body, Integer tryCount) {
        for (; tryCount > 0; tryCount--) {
            try {
                T featrue = body.touch();
                Thread.sleep(3000);
                return featrue;
            } catch (Exception e) {
                log.warn("触摸身体异常", e);
            }
        }

        return null;
    }

    private boolean isSelf(IBody<T> body) {
        Integer i = growCount;
        while (i > 0) {
            T newFeatrue = grow();
            T featrue = touch(body, 9);
            if (null == featrue) {
                return false;
            }

            if (!growAlgorithm.isSame(newFeatrue, featrue)) {
                return false;
            }

            i--;
        }

        return true;
    }
}
