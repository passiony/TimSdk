package com.jiuwei.mylibrary;

import com.tencent.imsdk.TIMConversation;
import com.tencent.imsdk.TIMElem;
import com.tencent.imsdk.TIMElemType;
import com.tencent.imsdk.TIMGroupMemberInfo;
import com.tencent.imsdk.TIMGroupTipsElem;
import com.tencent.imsdk.TIMImageElem;
import com.tencent.imsdk.TIMMessage;
import com.tencent.imsdk.TIMSoundElem;
import com.tencent.imsdk.TIMTextElem;
import com.tencent.imsdk.TIMUserProfile;
import com.tencent.imsdk.ext.group.TIMGroupBaseInfo;
import com.tencent.imsdk.friendship.TIMFriend;
import com.tencent.imsdk.friendship.TIMFriendPendencyItem;
import com.tencent.imsdk.friendship.TIMFriendPendencyResponse;
import com.tencent.imsdk.friendship.TIMFriendResult;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

enum TState
{
    Failed,
    Success,
    Notify,
}

enum TAction
{
    ForceOffline,
    UserSigExpired,
    Connected,
    Disconnected,
    WifiNeedAuth,
    GroupEvent,
    Refresh,
    ConversationRefresh,
    NewMessage,
    Login,
    Logout,
    SelfProfile,
    ModifyProfile,
    UserProfile,
    FriendsList,
    AddFriend,
    DeleteFriend,
    DoResponse,
    PendencyList,
    SendMessage,
    GetMessage,
    CreateGroup,
    ApplyJoinGroup,
    QuitGroup,
    GetGroupMembers,
    GetGroupList
}

public class TimJsonUtil {

    public static String ErrorToJson(TAction action, int code, String desc) {
        JSONObject map = new JSONObject();

        try {
            map.put("state", TState.Failed);
            map.put("action", action);
            map.put("code", code);
            map.put("desc", desc);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return map.toString();
    }

    public static String SuccessToJson(TAction action,String desc) {
        JSONObject map = new JSONObject();

        try {
            map.put("state", TState.Success);
            map.put("action", action);
            map.put("desc", desc);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return map.toString();
    }

    public static String NotifyToJson(TAction action, String desc) {
        JSONObject map = new JSONObject();

        try {
            map.put("state", TState.Notify);
            map.put("action", action);
            map.put("desc", desc);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return map.toString();
    }

    //用户信息
    public static String ProfileToJson(TAction action,TIMUserProfile self) {
        JSONObject map = new JSONObject();

        try {
            map.put("state", TState.Success);
            map.put("action", action);

            JSONObject obj = new JSONObject();
            obj.put("identifier", self.getIdentifier());
            obj.put("nickName", self.getNickName());
            obj.put("faceUrl", self.getFaceUrl());
            obj.put("gender", self.getGender());
            obj.put("birthday", self.getBirthday());
            obj.put("allow", self.getAllowType());
            obj.put("selfSignature", self.getSelfSignature());

            map.put("json", obj);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return map.toString();
    }

    //好友处理结果
    public static String FriendResultToJson(TAction action, TIMFriendResult timFriend) {
        JSONObject map = new JSONObject();

        try {
            map.put("state", TState.Success);
            map.put("action", action);

            JSONObject obj = new JSONObject();
            obj.put("identifier", timFriend.getIdentifier());
            obj.put("resultCode", timFriend.getResultCode());
            obj.put("resultInfo", timFriend.getResultInfo());

            map.put("json", obj);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return map.toString();
    }

    //好友列表
    public static String FriendListToJson(List<TIMFriend> timFriends) {

        JSONObject map = new JSONObject();

        try {
            map.put("state", TState.Success);
            map.put("action", TAction.FriendsList);

            JSONArray array = new JSONArray();
            for (TIMFriend friend : timFriends) {
                JSONObject obj = new JSONObject();
                obj.put("identifier", friend.getTimUserProfile().getIdentifier());
                obj.put("nickName", friend.getTimUserProfile().getNickName());
                obj.put("remark", friend.getRemark());
                obj.put("faceUrl", friend.getTimUserProfile().getFaceUrl());
                obj.put("gender", friend.getTimUserProfile().getGender());
                obj.put("birthday", friend.getTimUserProfile().getBirthday());
                obj.put("allow", friend.getTimUserProfile().getAllowType());
                obj.put("selfSignature", friend.getTimUserProfile().getSelfSignature());

                array.put(obj);
            }
            map.put("json", array);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return map.toString();
    }

    //未决列表
    public static String PendentListToJson(TIMFriendPendencyResponse response) {
        JSONObject map = new JSONObject();

        try {
            map.put("state", TState.Success);
            map.put("action", TAction.PendencyList);

            JSONArray array = new JSONArray();
            List<TIMFriendPendencyItem> timPendents = response.getItems();
            for (TIMFriendPendencyItem item : timPendents) {
                JSONObject obj = new JSONObject();
                obj.put("identifier", item.getIdentifier());
                obj.put("nickName", item.getNickname());
                obj.put("addWording", item.getAddWording());
                obj.put("addSource", item.getAddSource());
                obj.put("type", item.getType());
                array.put(obj);
            }
            map.put("json", array);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return map.toString();
    }


    //会话信息
    public static String ConversationToJson(List<TIMConversation> conversations) {
        JSONObject map = new JSONObject();

        try {
            map.put("state", TState.Success);
            map.put("action", TAction.ConversationRefresh);

            JSONArray array = new JSONArray();
            for (TIMConversation con : conversations) {
                JSONObject obj = new JSONObject();
                obj.put("peer", con.getPeer());
                obj.put("type", con.getType());
                obj.put("lastMsg", TIMMessage2Dic(con.getLastMsg()));
                array.put(obj);
            }
            map.put("json", array);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return map.toString();
    }

    //聊天消息
    public static String MessageToJson(TAction action, TIMMessage message) {
        JSONObject map = new JSONObject();

        try {
            map.put("state", TState.Success);
            map.put("action", action);
            map.put("json", TIMMessage2Dic(message));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return map.toString();
    }

    //聊天消息列表
    public static String MessageListToJson(TAction action, List<TIMMessage> timMessages) {
        JSONObject map = new JSONObject();

        try {
            map.put("state", TState.Success);
            map.put("action", action);

            JSONArray array = new JSONArray();
            for (TIMMessage message : timMessages) {
                JSONObject msg = TIMMessage2Dic(message);

                array.put(msg);
            }
            map.put("json", array);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return map.toString();
    }

    //Tim的消息转Json
    static JSONObject TIMMessage2Dic(TIMMessage timMessage) {
        JSONObject map = new JSONObject();

        try {
            TIMElem timElem = timMessage.getElement(0);
            TIMElemType type = timElem.getType();

            map.put("type", type);//消息类型
            map.put("conversationType", timMessage.getConversation().getType());//消息类型
            map.put("sender", timMessage.getSender());//发送方ID
            map.put("isSelf", timMessage.isSelf());//是否是自己发送的

            switch (type) {
                case Text://文本
                    TIMTextElem text = (TIMTextElem) timElem;
                    map.put("text", text.getText());//消息
                    break;
                case Image://图片
                    TIMImageElem image = (TIMImageElem) timElem;
                    map.put("image", "");//消息
                    break;
                case Sound://语音
                    TIMSoundElem sound = (TIMSoundElem) timElem;
                    map.put("sound", "");//消息
                    break;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return map;
    }

    //群组事件
    public static String GroupEventToJson(TIMGroupTipsElem elem) {
        JSONObject map = new JSONObject();

        try {
            map.put("state", TState.Notify);
            map.put("action", TAction.GroupEvent);

            JSONObject obj = new JSONObject();
            obj.put("groupId", elem.getGroupId());
            obj.put("groupName", elem.getGroupName());
            obj.put("memberNum", elem.getMemberNum());
            obj.put("tipsType", elem.getTipsType());

            map.put("json", obj);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return map.toString();
    }

    //创建群
    public static String GroupToJson(String groupId)
    {
        JSONObject map = new JSONObject();

        try {
            map.put("state", TState.Success);
            map.put("action", TAction.CreateGroup);
            map.put("json", groupId);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return map.toString();
    }

    //群成员
    public static String GroupMembersToJson(List<TIMGroupMemberInfo> infoList)
    {
        JSONObject map = new JSONObject();

        try {
            map.put("state", TState.Success);
            map.put("action", TAction.GetGroupMembers);

            JSONArray array = new JSONArray();
            for (TIMGroupMemberInfo info : infoList) {
                array.put(info.getUser());
            }
            map.put("json", array);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return map.toString();
    }

    //群列表
    public static String GroupListToJson(List<TIMGroupBaseInfo> timGroupInfos)
    {
        JSONObject map = new JSONObject();

        try {
            map.put("state", TState.Success);
            map.put("action", TAction.GetGroupList);

            JSONArray array = new JSONArray();
            for (TIMGroupBaseInfo info : timGroupInfos) {
                JSONObject obj=new JSONObject();
                obj.put("groupId",info.getGroupId());
                obj.put("groupName",info.getGroupName());
                obj.put("groupType",info.getGroupType());

                array.put(obj);
            }
            map.put("json", array);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return map.toString();
    }
}
