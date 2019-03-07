package com.knight.asus_nb.knight.Util;

import com.knight.asus_nb.knight.db.RoadLine;
import com.knight.asus_nb.knight.db.RoadLineList;
import com.knight.asus_nb.knight.db.User;

public class GlobalData {

    public static User currentUser = null;      //当前登录用户信息
    public static RoadLineList currentAddRoadLineList = null;      //当前添加的赛段信息
    public static RoadLine currentAddRoadLine = null;       //当前要添加的路线信息
    public static RoadLine currentShowRoadLine = null;       //当前正在查看的路线信息
    public static RoadLine currentDaoHangRoadLine = null;       //当前导航的路书
    public static RoadLineList currentShowRoadLineList = null;       //当前正在查看的路线信息
    public static int mapfragmentcode = -1;
}
