package com.onceas.work;

/**
 * <p>
 * Title: Work Manager的实现
 * </p>
 * 
 * <p>
 * Description: 基于OnceAS平台
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2006
 * </p>
 * 
 * <p>
 * Company: 中国科学院软件研究所
 * </p>
 * 
 * @author 张磊
 * @version 1.0
 */

public interface Work extends Runnable {

	public abstract Runnable overloadAction(String s);

	public abstract Runnable cancel(String s);
}
