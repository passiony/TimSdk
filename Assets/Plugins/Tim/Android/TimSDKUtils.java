package com.jiuwei.mylibrary;

import android.content.Context;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import com.tencent.imsdk.TIMCallBack;
import com.tencent.imsdk.TIMConnListener;
import com.tencent.imsdk.TIMConversation;
import com.tencent.imsdk.TIMConversationType;
import com.tencent.imsdk.TIMFriendAllowType;
import com.tencent.imsdk.TIMFriendshipManager;
import com.tencent.imsdk.TIMGroupAddOpt;
import com.tencent.imsdk.TIMGroupEventListener;
import com.tencent.imsdk.TIMGroupManager;
import com.tencent.imsdk.TIMGroupMemberInfo;
import com.tencent.imsdk.TIMGroupTipsElem;
import com.tencent.imsdk.TIMLogLevel;
import com.tencent.imsdk.TIMManager;
import com.tencent.imsdk.TIMMessage;
import com.tencent.imsdk.TIMMessageListener;
import com.tencent.imsdk.TIMRefreshListener;
import com.tencent.imsdk.TIMSdkConfig;
import com.tencent.imsdk.TIMTextElem;
import com.tencent.imsdk.TIMUserConfig;
import com.tencent.imsdk.TIMUserProfile;
import com.tencent.imsdk.TIMUserStatusListener;
import com.tencent.imsdk.TIMValueCallBack;
import com.tencent.imsdk.ext.group.TIMGroupBaseInfo;
import com.tencent.imsdk.friendship.TIMDelFriendType;
import com.tencent.imsdk.friendship.TIMFriend;
import com.tencent.imsdk.friendship.TIMFriendPendencyItem;
import com.tencent.imsdk.friendship.TIMFriendPendencyRequest;
import com.tencent.imsdk.friendship.TIMFriendPendencyResponse;
import com.tencent.imsdk.friendship.TIMFriendRequest;
import com.tencent.imsdk.friendship.TIMFriendResponse;
import com.tencent.imsdk.friendship.TIMFriendResult;
import com.tencent.imsdk.session.SessionWrapper;
import com.unity3d.player.UnityPlayer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class TimSDKUtils implements Handler.Callback {

    private static boolean DEBUG = true;
    public static final String TAG = "TIM DEMO";

    private static Context context;
    private static String u3dGameObject;
    private static String u3dCallback;


    public TimSDKUtils(final String gameObject, final String callback) {
        if (DEBUG) {
            System.out.println("ShareSDKUtils.prepare");
        }
        if (context == null) {
            context = UnityPlayer.currentActivity.getApplicationContext();
        }
//        context = _context;

        if (!TextUtils.isEmpty(gameObject)) {
            u3dGameObject = gameObject;
        }

        if (!TextUtils.isEmpty(callback)) {
            u3dCallback = callback;
        }
    }

    //TODO:初始化
    public void Init(int appId) {
        //判断是否是在主线程
        if (SessionWrapper.isMainProcess(context)) {
//            TIMSdkConfig config = new TIMSdkConfig(GenerateTestUserSig.SDKAPPID)
            TIMSdkConfig config = new TIMSdkConfig(appId)
                    .enableLogPrint(true)
                    .setLogLevel(TIMLogLevel.DEBUG)
                    .setLogPath(Environment.getExternalStorageDirectory().getPath() + "/justfortest/");

            SetUserConfig();
            AddMessageListener();

            //初始化 SDK
            TIMManager.getInstance().init(context, config);
        }
    }

    //TODO:设置用户config
    public void SetUserConfig() {
        //基本用户配置
        TIMUserConfig userConfig = new TIMUserConfig()
                //设置用户状态变更事件监听器
                .setUserStatusListener(new TIMUserStatusListener() {
                    @Override
                    public void onForceOffline() {
                        //被其他终端踢下线
                        Log.i(TAG, "onForceOffline");
                        SendToUnity(TimJsonUtil.NotifyToJson(TAction.ForceOffline,"onForceOffline"));
                    }

                    @Override
                    public void onUserSigExpired() {
                        //用户签名过期了，需要刷新 userSig 重新登录 IM SDK
                        Log.i(TAG, "onUserSigExpired");
                        SendToUnity(TimJsonUtil.NotifyToJson(TAction.UserSigExpired,"onUserSigExpired"));
                    }
                })
                //设置连接状态事件监听器
                .setConnectionListener(new TIMConnListener() {
                    @Override
                    public void onConnected() {
                        Log.i(TAG, "onConnected");
                        SendToUnity(TimJsonUtil.NotifyToJson(TAction.Connected,"onConnected"));
                    }

                    @Override
                    public void onDisconnected(int code, String desc) {
                        Log.i(TAG, "onDisconnected");
                        SendToUnity(TimJsonUtil.NotifyToJson(TAction.Disconnected,"onDisconnected"));
                    }

                    @Override
                    public void onWifiNeedAuth(String name) {
                        Log.i(TAG, "onWifiNeedAuth");
                        SendToUnity(TimJsonUtil.NotifyToJson(TAction.WifiNeedAuth,"onWifiNeedAuth"));
                    }
                })
                //设置群组事件监听器
                .setGroupEventListener(new TIMGroupEventListener() {
                    @Override
                    public void onGroupTipsEvent(TIMGroupTipsElem elem) {
                        Log.i(TAG, "onGroupTipsEvent, type: " + elem.getTipsType());
                        SendToUnity(TimJsonUtil.GroupEventToJson(elem));
                    }
                })
                //设置会话刷新监听器
                .setRefreshListener(new TIMRefreshListener() {
                    @Override
                    public void onRefresh() {
                        Log.i(TAG, "onRefresh");
                        SendToUnity(TimJsonUtil.NotifyToJson(TAction.Refresh,"onRefresh"));
                    }

                    @Override
                    public void onRefreshConversation(List<TIMConversation> conversations) {
                        Log.i(TAG, "onRefreshConversation, conversation size: " + conversations.size());
                        SendToUnity(TimJsonUtil.ConversationToJson(conversations));
                    }
                });

        //禁用本地所有存储
        userConfig.disableStorage();
        //开启消息已读回执
        userConfig.enableReadReceipt(true);

        //将用户配置与通讯管理器进行绑定
        TIMManager.getInstance().setUserConfig(userConfig);
    }

    //TODO:添加消息监听
    public void AddMessageListener() {
        //设置消息监听器，收到新消息时，通过此监听器回调
        TIMManager.getInstance().addMessageListener(new TIMMessageListener() {//消息监听器
            @Override
            public boolean onNewMessages(List<TIMMessage> msgs) {//收到新消息
                //消息的内容解析请参考消息收发文档中的消息解析说明
                for (TIMMessage item : msgs) {
                    Log.i(TAG, item.toString());
                }

                SendToUnity(TimJsonUtil.MessageListToJson(TAction.NewMessage,msgs));
                return true; //返回true将终止回调链，不再调用下一个新消息监听器
            }
        });
    }

    //TODO:登录
    public void Login(String userID) {
        //生成userSig
        String userSig = GenerateTestUserSig.genTestUserSig(userID);

        // identifier为用户名，userSig 为用户登录凭证
        TIMManager.getInstance().login(userID, userSig, new TIMCallBack() {
            @Override
            public void onError(int code, String desc) {
                //错误码 code 和错误描述 desc，可用于定位请求失败原因
                //错误码 code 列表请参见错误码表
                Log.d(TAG, "login failed. code: " + code + " errmsg: " + desc);
                SendToUnity(TimJsonUtil.ErrorToJson(TAction.Login,code,desc));
            }

            @Override
            public void onSuccess() {
                Log.d(TAG, "login succ");
                SendToUnity(TimJsonUtil.SuccessToJson(TAction.Login,"login succ"));


            }
        });
    }


    //TODO:登出
    public void Logout() {
        TIMManager.getInstance().logout(new TIMCallBack() {
            @Override
            public void onError(int code, String desc) {

                //错误码 code 和错误描述 desc，可用于定位请求失败原因
                //错误码 code 列表请参见错误码表
                Log.d(TAG, "logout failed. code: " + code + " errmsg: " + desc);
                SendToUnity(TimJsonUtil.ErrorToJson(TAction.Logout,code,desc));
            }

            @Override
            public void onSuccess() {
                //登出成功
                Log.d(TAG, "Logout succ");
                SendToUnity(TimJsonUtil.SuccessToJson(TAction.Logout,"Logout succ"));
            }
        });
    }

    //TODO:获取自己的资料
    public void GetSelfProfile() {
        //获取服务器保存的自己的资料
        TIMFriendshipManager.getInstance().getSelfProfile(new TIMValueCallBack<TIMUserProfile>() {
            @Override
            public void onError(int code, String desc) {
                //错误码 code 和错误描述 desc，可用于定位请求失败原因
                //错误码 code 列表请参见错误码表
                Log.e(TAG, "getSelfProfile failed: " + code + " desc");
                SendToUnity(TimJsonUtil.ErrorToJson(TAction.SelfProfile,code,desc));
            }

            @Override
            public void onSuccess(TIMUserProfile result) {
                Log.e(TAG, "getSelfProfile succ");
                Log.e(TAG, "identifier: " + result.getIdentifier() + " nickName: " + result.getNickName()
                        + " allow: " + result.getAllowType());

                SendToUnity(TimJsonUtil.ProfileToJson(TAction.SelfProfile,result));
            }
        });

        //获取本地保存的自己的资料
        //TIMUserProfile selfProfile = TIMFriendshipManager.getInstance().querySelfProfile();
    }

    //TODO:修改个人信息
    public void ModifySelfProfile(String nickname,int gender,int birthday,String signature,String allowType){
        HashMap<String, Object> profileMap = new HashMap<>();
        profileMap.put(TIMUserProfile.TIM_PROFILE_TYPE_KEY_NICK, "我的昵称");
        profileMap.put(TIMUserProfile.TIM_PROFILE_TYPE_KEY_GENDER, gender);
        profileMap.put(TIMUserProfile.TIM_PROFILE_TYPE_KEY_BIRTHDAY, birthday);
        profileMap.put(TIMUserProfile.TIM_PROFILE_TYPE_KEY_SELFSIGNATURE, signature);
        profileMap.put(TIMUserProfile.TIM_PROFILE_TYPE_KEY_ALLOWTYPE, TIMFriendAllowType.TIM_FRIEND_NEED_CONFIRM);
        TIMFriendshipManager.getInstance().modifySelfProfile(profileMap, new TIMCallBack() {
            @Override
            public void onError(int code, String desc) {
                Log.e(TAG, "modifySelfProfile failed: " + code + " desc" + desc);
                SendToUnity(TimJsonUtil.ErrorToJson(TAction.ModifyProfile,code,desc));
            }

            @Override
            public void onSuccess() {
                Log.e(TAG, "modifySelfProfile success");
                SendToUnity(TimJsonUtil.SuccessToJson(TAction.ModifyProfile,"modifySelfProfile success"));
            }
        });
    }

    //TODO:获取用户资料
    public void GetUserProfile(String identifier,boolean forceUpdate) {
        //待获取用户资料的用户列表
        List<String> users = new ArrayList<String>();
        users.add(identifier);

        //获取用户资料
        TIMFriendshipManager.getInstance().getUsersProfile(users, forceUpdate, new TIMValueCallBack<List<TIMUserProfile>>() {
            @Override
            public void onError(int code, String desc) {
                //错误码 code 和错误描述 desc，可用于定位请求失败原因
                //错误码 code 列表请参见错误码表
                Log.e(TAG, "getUsersProfile failed: " + code + " desc");
                SendToUnity(TimJsonUtil.ErrorToJson(TAction.UserProfile, code, desc));
            }

            @Override
            public void onSuccess(List<TIMUserProfile> result) {
                Log.e(TAG, "getUsersProfile succ");
                for (TIMUserProfile res : result) {
                    Log.e(TAG, "identifier: " + res.getIdentifier() + " nickName: " + res.getNickName());
                    SendToUnity(TimJsonUtil.ProfileToJson(TAction.UserProfile,res));
                }

            }
        });

        //获取本地缓存的用户资料
        //TIMUserProfile userProfile = TIMFriendshipManager.getInstance().queryUserProfile("sample_user_1");
    }

    //TODO:获取好友列表
    public void GetFriendList() {
        TIMFriendshipManager.getInstance().getFriendList(new TIMValueCallBack<List<TIMFriend>>() {
            @Override
            public void onError(int code, String desc) {
                Log.e(TAG, "getFriendList err code = " + code);

                SendToUnity(TimJsonUtil.ErrorToJson(TAction.FriendsList,code,desc));
            }

            @Override
            public void onSuccess(List<TIMFriend> timFriends) {
                StringBuilder stringBuilder = new StringBuilder();
                for (TIMFriend timFriend : timFriends) {
                    stringBuilder.append(timFriend.toString());
                }
                Log.i(TAG, "getFriendList success result = " + stringBuilder.toString());

                SendToUnity(TimJsonUtil.FriendListToJson(timFriends));
            }
        });
    }


    //TODO:添加好友
    public void AddFriend(String identifier, String addword, String remark) {
        TIMFriendRequest timFriendRequest = new TIMFriendRequest(identifier);
        timFriendRequest.setAddWording(addword);
        timFriendRequest.setAddSource("AddSrource_Type_Android");
        timFriendRequest.setRemark(remark);

        TIMFriendshipManager.getInstance().addFriend(timFriendRequest, new TIMValueCallBack<TIMFriendResult>() {
            @Override
            public void onError(int code, String desc) {
                Log.e(TAG, "addFriend err code = " + code + ", desc = " + desc);
                SendToUnity(TimJsonUtil.ErrorToJson(TAction.AddFriend, code, desc));
            }

            @Override
            public void onSuccess(TIMFriendResult timFriendResult) {
                Log.i(TAG, "addFriend success result = " + timFriendResult.toString());
                SendToUnity(TimJsonUtil.FriendResultToJson(TAction.AddFriend, timFriendResult));
            }
        });
    }

    //TODO:删除好友
    public void DeleteFriend(String identifier) {
        List<String> identifiers = new ArrayList<>();
        identifiers.add(identifier);

        TIMFriendshipManager.getInstance().deleteFriends(identifiers, TIMDelFriendType.TIM_FRIEND_DEL_SINGLE, new TIMValueCallBack<List<TIMFriendResult>>() {
            @Override
            public void onError(int code, String desc) {
                Log.e(TAG, "deleteFriends err code = " + code + ", desc = " + desc);
                SendToUnity(TimJsonUtil.ErrorToJson(TAction.DeleteFriend, code, desc));

            }

            @Override
            public void onSuccess(List<TIMFriendResult> results) {
                Log.i(TAG, "deleteFriends success");
                for (TIMFriendResult res:results) {
                    SendToUnity(TimJsonUtil.FriendResultToJson(TAction.DeleteFriend, res));
                }
            }
        });
    }

    //TODO:同意/拒绝好友申请
    public void DoResponse(String identifier, int agreeType) {
        TIMFriendResponse response = new TIMFriendResponse();
        response.setIdentifier(identifier);
        response.setResponseType(agreeType);

        TIMFriendshipManager.getInstance().doResponse(response, new TIMValueCallBack<TIMFriendResult>() {
            @Override
            public void onError(int code, String desc) {
                Log.e(TAG, "deleteFriends err code = " + code + ", desc = " + desc);
                SendToUnity(TimJsonUtil.ErrorToJson(TAction.DoResponse, code, desc));
            }

            @Override
            public void onSuccess(TIMFriendResult timFriendResult) {
                Log.i(TAG, "deleteFriends success");
                SendToUnity(TimJsonUtil.FriendResultToJson(TAction.DoResponse, timFriendResult));
            }
        });
    }

    //TODO:获取未决列表 COME_IN = 1; SEND_OUT = 2; BOTH = 3;
    public void GetPendencyList(int seq,int numPerPage,int timeStamp,int type) {
        TIMFriendPendencyRequest timFriendPendencyRequest = new TIMFriendPendencyRequest();
        timFriendPendencyRequest.setSeq(seq);
        timFriendPendencyRequest.setNumPerPage(numPerPage);
        timFriendPendencyRequest.setTimestamp(timeStamp);
        timFriendPendencyRequest.setTimPendencyGetType(type);

        TIMFriendshipManager.getInstance().getPendencyList(timFriendPendencyRequest, new TIMValueCallBack<TIMFriendPendencyResponse>() {
            @Override
            public void onError(int code, String desc) {
                Log.e(TAG, "GetPendencyList err code = " + code + ", desc = " + desc);
                SendToUnity(TimJsonUtil.ErrorToJson(TAction.PendencyList, code, desc));
            }

            @Override
            public void onSuccess(TIMFriendPendencyResponse response) {
                SendToUnity(TimJsonUtil.PendentListToJson(response));
            }
        });
    }

    //TODO:发送消息
    public void SendMessage(String peer, String message, int type) {
        //构造一条消息
        TIMMessage msg = new TIMMessage();

        //添加文本内容
        TIMTextElem elem = new TIMTextElem();
        elem.setText(message);

        //将elem添加到消息
        if (msg.addElement(elem) != 0) {
            Log.d(TAG, "addElement failed");
            return;
        }

        TIMConversation conversation = TIMManager.getInstance().getConversation(
                TIMConversationType.values()[type],    //会话类型：单聊/群聊
                peer);   //会话对方用户帐号//对方ID
        //发送消息
        conversation.sendMessage(msg, new TIMValueCallBack<TIMMessage>() {//发送消息回调
            @Override
            public void onError(int code, String desc) {//发送消息失败
                //错误码 code 和错误描述 desc，可用于定位请求失败原因
                //错误码 code 含义请参见错误码表
                Log.d(TAG, "send message failed. code: " + code + " errmsg: " + desc);
                SendToUnity(TimJsonUtil.ErrorToJson(TAction.SendMessage, code, desc));

            }

            @Override
            public void onSuccess(TIMMessage msg) {//发送消息成功
                Log.e(TAG, "SendMsg ok");
                SendToUnity(TimJsonUtil.MessageToJson(TAction.SendMessage, msg));
            }
        });
    }

    //TODO:获取会话漫游信息
    public void GetMessage(String peer, int count, int type) {
        TIMConversation conversation = TIMManager.getInstance().getConversation(
                TIMConversationType.values()[type],    //会话类型：单聊/群聊
                peer);   //会话对方用户帐号//对方ID

        //获取此会话的消息
//        conversation.getLocalMessage(count, //获取此会话最近的 10 条消息
        conversation.getMessage(count, //获取此会话最近的 10 条消息
                null, //不指定从哪条消息开始获取 - 等同于从最新的消息开始往前
                new TIMValueCallBack<List<TIMMessage>>() {//回调接口
                    @Override
                    public void onError(int code, String desc) {//获取消息失败
                        //接口返回了错误码 code 和错误描述 desc，可用于定位请求失败原因
                        //错误码 code 含义请参见错误码表
                        Log.d(TAG, "get message failed. code: " + code + " errmsg: " + desc);
                        SendToUnity(TimJsonUtil.ErrorToJson(TAction.GetMessage, code, desc));

                    }

                    @Override
                    public void onSuccess(List<TIMMessage> msgs) {//获取消息成功
                        //遍历取得的消息
                        for (TIMMessage msg : msgs) {
                            //可以通过 timestamp()获得消息的时间戳, isSelf()是否为自己发送的消息
                            Log.d(TAG, "get message success: " + msg.toString());
                        }
                        SendToUnity(TimJsonUtil.MessageListToJson(TAction.GetMessage,msgs));
                    }
                });
    }


    //TODO:创建群聊
    public void CreateGroup(int addOption){
        //创建公开群，且不自定义群 ID
        TIMGroupManager.CreateGroupParam param = new TIMGroupManager.CreateGroupParam("Public", "test_group");
        //指定群简介
        param.setIntroduction("hello world");
        //指定群公告
        param.setNotification("welcome to our group");
        //加入权限
        param.setAddOption(TIMGroupAddOpt.values()[addOption]);

        //创建群组
        TIMGroupManager.getInstance().createGroup(param, new TIMValueCallBack<String>() {
            @Override
            public void onError(int code, String desc) {
                Log.d(TAG, "create group failed. code: " + code + " errmsg: " + desc);
                SendToUnity(TimJsonUtil.ErrorToJson(TAction.CreateGroup,code, desc));
            }

            @Override
            public void onSuccess(String groupId) {
                Log.d(TAG, "create group succ, groupId:" + groupId);
                SendToUnity(TimJsonUtil.GroupToJson(groupId));
            }
        });
    }

    //TODO:加入群聊
    public void ApplyJoinGroup(final String groupId, String reason){
        TIMGroupManager.getInstance().applyJoinGroup(groupId, reason, new TIMCallBack() {
            @java.lang.Override
            public void onError(int code, String desc) {
                Log.e(TAG, "applyJoinGroup err code = " + code + ", desc = " + desc);
                SendToUnity(TimJsonUtil.ErrorToJson(TAction.ApplyJoinGroup,code, desc));
            }

            @java.lang.Override
            public void onSuccess() {
                Log.i(TAG, "applyJoinGroup success");
                SendToUnity(TimJsonUtil.SuccessToJson(TAction.ApplyJoinGroup, groupId));
            }
        });
    }

    //TODO:退出群聊
    public void QuitGroup(final String groupId){
        //退出群组
        TIMGroupManager.getInstance().quitGroup(groupId,  new TIMCallBack() {
            @Override
            public void onError(int code, String desc) {
                Log.e(TAG, "QuitGroup err code = " + code + ", desc = " + desc);
                SendToUnity(TimJsonUtil.ErrorToJson(TAction.QuitGroup,code, desc));
            }

            @Override
            public void onSuccess() {
                Log.e(TAG, "quit group succ");
                SendToUnity(TimJsonUtil.SuccessToJson(TAction.QuitGroup, groupId));
            }
        });
    }

    //TODO:获得群成员
    public void GetGroupMembers(String groupId) {
        //获取群组成员信息
        TIMGroupManager.getInstance().getGroupMembers(
                groupId, new TIMValueCallBack<List<TIMGroupMemberInfo>>() {
                    @Override
                    public void onError(int code, String desc) {
                        Log.e(TAG, "QuitGroup err code = " + code + ", desc = " + desc);
                        SendToUnity(TimJsonUtil.ErrorToJson(TAction.GetGroupMembers, code, desc));
                    }

                    @Override
                    public void onSuccess(List<TIMGroupMemberInfo> infoList) {//参数返回群组成员信息
                        SendToUnity(TimJsonUtil.GroupMembersToJson(infoList));
                    }
                });
    }

    //TODO:获取加入的群组列表
    public void GetGroupList(){
        //获取已加入的群组列表
        TIMGroupManager.getInstance().getGroupList(new TIMValueCallBack<List<TIMGroupBaseInfo>>() {
            @Override
            public void onError(int code, String desc) {
                Log.e(TAG, "get gruop list failed: " + code + " desc");
                SendToUnity(TimJsonUtil.ErrorToJson(TAction.GetGroupList,code, desc));
            }

            @Override
            public void onSuccess(List<TIMGroupBaseInfo> timGroupInfos) {//参数返回各群组基本信息
                Log.d(TAG, "get gruop list succ");
                SendToUnity(TimJsonUtil.GroupListToJson(timGroupInfos));
            }
        });
    }


    //TODO:发送给Unity
    private static void SendToUnity(String message) {

        Log.d(TAG, "SendToUnity: " + message);
        UnityPlayer.UnitySendMessage(u3dGameObject, u3dCallback, message);
    }

    @Override
    public boolean handleMessage(Message message) {
        return false;
    }
}
