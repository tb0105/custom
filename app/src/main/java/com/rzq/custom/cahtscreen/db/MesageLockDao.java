package com.rzq.custom.cahtscreen.db;

import android.content.Context;

public class MesageLockDao extends BaseDao<MessageLockBean> {
    public MesageLockDao(Context context) {
        super(context, MessageLockBean.class);
    }

    public void replace(MessageLockBean bean) {
        try {
            dao.createOrUpdate(bean);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public MessageLockBean query(int id) {
        try {
            return
                    dao.queryBuilder().where().eq(MessageLockBean.MSGID, id).queryForFirst();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean Exist(int msgid) {
        try {
            return dao.queryBuilder().where().eq(MessageLockBean.MSGID, msgid).queryForFirst() != null;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
