package WorkManager.ThreadAutoAdjust;

/**
 * <p>
 * Title: Work Manager��ʵ��
 * </p>
 * 
 * <p>
 * Description: ����OnceASƽ̨
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2006
 * </p>
 * 
 * <p>
 * Company: �й���ѧԺ����о���
 * </p>
 * 
 * @author ����
 * @version 1.0
 */

public interface Work extends Runnable {

	public abstract Runnable overloadAction(String s);

	public abstract Runnable cancel(String s);
}
