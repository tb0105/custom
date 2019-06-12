package com.rzq.custom.cahtscreen.db;

import android.content.Context;

import com.j256.ormlite.stmt.Where;
import com.rzq.custom.cahtscreen.UserInfo;

import java.util.Date;
import java.util.List;

public class MessageDao extends BaseDao<MessageBean> {
    public MessageDao(Context context) {
        super(context, MessageBean.class);
    }

    public void replace(MessageBean messageBean) {
        try {
            dao.createOrUpdate(messageBean);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public MessageBean query(String idx) {
        try {
            return dao.queryBuilder().where().eq(MessageBean.COLUMNNAME_IDX, idx)
                    .queryForFirst();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void updateStatus(MessageBean bean) {
        try {
            dao.update(bean);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<MessageBean> querychat(int friendid) {
        try {
            Where<MessageBean, Integer> where = dao.queryBuilder().orderBy(MessageBean.COLUMNNAME_SERVERIDX, false).where();
            where.or(
                    where.and(
                            where.eq(MessageBean.COLUMNNAME_SENDERID, UserInfo.getUserId()),
                            where.eq(MessageBean.COLUMNNAME_RECEIVERID, friendid)),
                    where.and(
                            where.eq(MessageBean.COLUMNNAME_SENDERID, friendid),
                            where.eq(MessageBean.COLUMNNAME_RECEIVERID, UserInfo.getUserId()))
            );
            return where.query();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void delete(String friend) {
        try {
            dao.deleteBuilder().where().eq(MessageBean.COLUMNNAME_SENDERID, friend);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<MessageBean> queryChatOline() {
        try {
            return dao.queryBuilder().distinct().groupBy(MessageBean.COLUMNNAME_SENDERID)
                    .orderBy(MessageBean.COLUMNNAME_SENDTIME, false).where()
                    .ne(MessageBean.COLUMNNAME_SENDERID, UserInfo.getUserId())
                    .or().eq(MessageBean.COLUMNNAME_RECEIVERID, UserInfo.getUserId()).query();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<MessageBean> queryStatusCount(int senderid) {
        try {
            if (dao != null)
                return dao.queryBuilder().where().eq(MessageBean.COLUMNNAME_SENDERID, senderid + "").and()
                        .eq(MessageBean.COLUMNNAME_RECEIVERID, UserInfo.getUserId()).query();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public String queryStatusTomsg(int senderid) {
        try {
            if (dao != null)
                return dao.queryBuilder().orderBy(MessageBean.COLUMNNAME_SENDTIME, false)
                        .where().eq(MessageBean.COLUMNNAME_SENDERID, senderid).or().eq(MessageBean.COLUMNNAME_RECEIVERID, senderid)
                        .queryForFirst().getMsginfo();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public Date queryStatusToUpdatedt(int senderid) {
        try {
            if (dao != null)
                return dao.queryBuilder().where().eq(MessageBean.COLUMNNAME_SENDERID, senderid + "").queryForFirst().getUpdatedt();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;

    }

    public List<MessageBean> queryAll() {
        try {
            return dao.queryBuilder().where().eq(MessageBean.COLUMNNAME_SENDERID, UserInfo.getUserId()).and()
                    .eq(MessageBean.COLUMNNAME_RECEIVERID, UserInfo.getUserId()).query();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public MessageBean querymsgid(MessageBean messageBean) {
        try {
            return dao.queryBuilder().where().eq(MessageBean.COLUMNNAME_SERVERIDX, messageBean.getMsgid()).queryForFirst();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public MessageBean queryupdatedt(Date updatedt) {
        try {
            return dao.queryBuilder().where().eq(MessageBean.COLUMNNAME_UPDATEDT, updatedt).queryForFirst();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void updateIdx(MessageBean bean) {
        try {
            dao.update(bean);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void replacemsgid(MessageBean bean) {
        try {
            if (null == dao.queryBuilder().where().eq(MessageBean.COLUMNNAME_SERVERIDX, bean.getMsgid()).queryForFirst()) {
                dao.create(bean);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public int querymaxIdx() {
        try {
            return dao.queryBuilder().orderBy(MessageBean.COLUMNNAME_IDX, false).queryForFirst().getIdx();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public int querymaxMsg() {
        try {
            return dao.queryBuilder().orderBy(MessageBean.COLUMNNAME_SERVERIDX, false).queryForFirst().getMsgid();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }
}
