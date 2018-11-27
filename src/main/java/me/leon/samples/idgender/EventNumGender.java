package me.leon.samples.idgender;

/**
 * 案件编号生成器接口
 * 案件编号格式：yyyymmdd+5位顺序号
 */
public interface EventNumGender {

    /**
     *  获取下一个案件编号
     * @return
     */
    public long getNextEventNumber();

}
