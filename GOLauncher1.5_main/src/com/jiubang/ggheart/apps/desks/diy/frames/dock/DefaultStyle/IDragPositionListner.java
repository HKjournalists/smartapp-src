package com.jiubang.ggheart.apps.desks.diy.frames.dock.DefaultStyle;

import java.util.ArrayList;

import android.graphics.Rect;


/**
 * 
 * <br>������:dockͼ���϶�������
 * <br>������ϸ����:
 * 
 * @author  ruxueqin
 * @date  [2012-10-16]
 */
public interface IDragPositionListner {
	public abstract void onLeft(DockIconView dockIconView);

	public abstract void onMiddle(DockIconView dockIconView, Rect rect, int indexinrow);

	public abstract void onRight(DockIconView dockIconView);

	public abstract void setRecycleDragCache();

	public abstract void setAddIconIndex(ArrayList<Integer> list);
}
