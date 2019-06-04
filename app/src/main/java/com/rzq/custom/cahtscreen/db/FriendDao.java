package com.rzq.custom.cahtscreen.db;

import android.content.Context;


import com.rzq.custom.cahtscreen.UserInfo;

import java.util.List;

public class FriendDao extends BaseDao<FrendBean> {

    public FriendDao(Context context) {
        super(context, FrendBean.class);
    }

    public void insert(List<FrendBean> frendBeanList) {
        try {
            if (dao != null)
                dao.create(frendBeanList);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void insert(FrendBean frendBean) {
        try {
            if (dao != null)
                dao.create(frendBean);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<FrendBean> queryAll() {
        try {
//            if (dao != null)
//                return dao.queryBuilder().where()
//                        .eq(FrendBean.COLUMNNAME_USERID, UserInfo.getUserid()).query();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<FrendBean> queryOrderby(String conditions) {
        try {
//            if (dao != null)
//                return dao.queryBuilder().groupBy(FrendBean.COLUMNNAME_GROP).where().eq(FrendBean.COLUMNNAME_GROP, conditions).and()
//                        .eq(FrendBean.COLUMNNAME_USERID, UserInfo.getUserid()).query();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public long querymaxdt() {
        try {
            if (dao != null)
                return dao.queryBuilder().orderBy(FrendBean.COLUMNNAME_UPDATE, false).where()
                        .eq(FrendBean.COLUMNNAME_USERID, UserInfo.getUserId()).queryForFirst().getUpdatedt().getTime();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public boolean has(FrendBean frendBean) {
        try {
            if (dao != null)
                return dao.queryBuilder().where().eq(FrendBean.COLUMNNAME_USERID, frendBean.getUserid()).query().size() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public void update(FrendBean frendBean) {
        try {
            if (dao != null)
                dao.update(frendBean);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<FrendBean> querygrop(String conditions) {
        try {
            if (dao != null)
                return dao.queryBuilder().where().eq(FrendBean.COLUMNNAME_GROP, conditions).and()
                        .ne(FrendBean.COLUMNNAME_FRIEND, UserInfo.getUserId()).and().eq(FrendBean.COLUMNNAME_USERID, UserInfo.getUserId()).query();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void replace(FrendBean frendBean) {
        try {
            if (dao != null)
                dao.createOrUpdate(frendBean);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public FrendBean query(String s) {
        try {
            if (dao != null)
                return dao.queryBuilder().where().eq(FrendBean.COLUMNNAME_FRIEND, s).queryForFirst();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


}
