using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public enum TIMErrType
{
    OK,
}

public enum TIMConversationType
{
    Invalid,
    C2C,
    Group,
    System
}

public enum TIMResponseType
{
    AGREE = 0,
    AGREE_AND_ADD = 1,
    REJECT = 2,
}

public class TimSDK : MonoBehaviour
{
    public int APPID = 1400265313;

    private ITimImpl TimSdkImpl;

    void Awake()
    {
        this.name = "UnityTimSDK";
#if UNITY_ANDROID
        TimSdkImpl = new AndroidTimSDKImpl(gameObject, "_Callback");
#elif UNITY_IOS
        TimSdkImpl = new IOSTimSDKImpl(gameObject,"_Callback");
#endif
    }

    /// <summary>
    /// 初始化
    /// </summary>
    /// <param name="APPID">SDKAPPID：后台获取</param>
    public void Init()
    {
        Debug.Log("TimSDK.Init()");
        TimSdkImpl.Init(APPID);
    }

    /// <summary>
    /// 登录
    /// </summary>
    /// <param name="userId">用户名</param>
    /// <param name="userSig">用户登录凭证</param>
    public void Login(string userId)
    {
        Debug.Log("TimSDK.Login()");
        TimSdkImpl.Login(userId);
    }

    /// <summary>
    /// 登出
    /// </summary>
    public void Logout()
    {
        Debug.Log("TimSDK.Logout()");
        TimSdkImpl.Logout();
    }

    /// <summary>
    /// 获取自己的信息
    /// </summary>
    public void GetSelfProfile()
    {
        Debug.Log("TimSDK.GetSelfProfile()");
        TimSdkImpl.GetSelfProfile();
    }


    /// <summary>
    /// 
    /// </summary>
    /// <param name="nickname"></param>
    /// <param name="gender"></param>
    /// <param name="birthday"></param>
    /// <param name="signature"></param>
    /// <param name="allowType">"AllowType_Type_Invalid";"AllowType_Type_AllowAny";"AllowType_Type_DenyAny";"AllowType_Type_NeedConfirm";</param>
    public void ModifySelfProfile(string nickname, int gender, int birthday, string signature, string allowType)
    {
        Debug.Log("TimSDK.ModifySelfProfile()");
        TimSdkImpl.ModifySelfProfile(nickname, gender, birthday, signature, allowType);
    }

    /// <summary>
    /// 获取用户资料
    /// </summary>
    /// <param name="identifier">用户名列表</param>
    /// <param name="forceUpdate">制从后台拉取</param>
    /// 当 forceUpdate = true 时，会强制从后台拉取数据，并把返回的数据缓存下来。
    /// 当 forceUpdate = false 时，则先在本地查找，如果本地没有数据则再向后台请求数据。
    /// 建议只在显示资料时强制拉取，以减少等待时间。
    public void GetUserProfile(string identifier, bool forceUpdate)
    {
        Debug.Log("TimSDK.GetUserProfile()");

        TimSdkImpl.GetUserProfile(identifier, forceUpdate);
    }

    /// <summary>
    /// 获取所有好友
    /// </summary>
    public void GetFriendsList()
    {
        Debug.Log("TimSDK.GetFriendList()");

        TimSdkImpl.GetFriendList();
    }

    /// <summary>
    /// 添加好友
    /// </summary>
    /// <param name="identifier">添加用户名</param>
    /// <param name="addword">请求说明</param>
    /// <param name="remark">备注</param>
    public void AddFriend(string identifier, string addword, string remark)
    {
        Debug.Log("TimSDK.AddFriend()");

        TimSdkImpl.AddFriend(identifier, addword, remark);
    }

    /// <summary>
    /// 删除好友
    /// </summary>
    /// <param name="identifiers"></param>
    public void DeleteFriend(string identifier,int delType)
    {
        Debug.Log("TimSDK.DeleteFriend()");

        TimSdkImpl.DeleteFriend(identifier, delType);
    }

    /// <summary>
    /// 同意/拒绝好友申请
    /// </summary>
    /// <param name="type">请求类型:无/通过/拒绝：TIMResponseType</param>
    public void DoResponse(int type)
    {
        Debug.Log("TimSDK.DoResponse()");

        TimSdkImpl.DoResponse(type);
    }


    /// <summary>
    /// 获取未决列表
    /// </summary>
    public void GetPendencyList()
    {
        Debug.Log("TimSDK.GetPendencyList()");

        TimSdkImpl.GetPendencyList(0, 10, 0, 3);
    }


    /// <summary>
    /// 
    /// </summary>
    /// <param name="peer">接收方id</param>
    /// <param name="message">消息string；表情用[微笑]代替</param>
    /// <param name="type">TIMConversationType</param>
    public void SendMessage(string peer, string message, int type)
    {
        Debug.Log("TimSDK.SendMessage()");

        TimSdkImpl.SendMessage(peer, message, type);
    }


    /// <summary>
    /// 获取会话漫游消息
    /// </summary>
    /// <param name="peer">接受方id</param>
    /// <param name="count">从最后一条消息往前的消息数</param>
    /// <param name="type">会话类型:单聊/群聊：TIMConversationType</param>
    public void GetMessages(string peer, int count, int type)
    {
        Debug.Log("TimSDK.GetMessage()");

        TimSdkImpl.GetMessage(peer, count, type);
    }



    /// <summary>
    /// 创建群聊
    /// </summary>
    public void CreateGroup(int addOption)
    {
        Debug.Log("TimSDK.CreateGroup()");

        TimSdkImpl.CreateGroup(addOption);
    }


    /// <summary>
    /// 加入群组
    /// </summary>
    /// <param name="groupId">群组 ID</param>
    /// <param name="reason">申请理由</param>
    public void ApplyJoinGroup(string groupId, string reason)
    {
        Debug.Log("TimSDK.ApplyJoinGroup()");

        TimSdkImpl.ApplyJoinGroup(groupId, reason);
    }

    /// <summary>
    /// 退出群聊
    /// </summary>
    /// <param name="groupId">群组ID</param>
    public void QuitGroup(string groupId)
    {
        Debug.Log("TimSDK.QuitGroup()");

        TimSdkImpl.QuitGroup(groupId);
    }

    /// <summary>
    /// 获取群组成员列表
    /// </summary>
    /// <param name="groupId"></param>
    public void GetGroupMembers(string groupId)
    {
        Debug.Log("TimSDK.GetGroupMembers()");

        TimSdkImpl.GetGroupMembers(groupId);
    }

    public void GetGroupList()
    {
        Debug.Log("TimSDK.GetGroupMembers()");

        TimSdkImpl.GetGroupList();
    }




    public event System.Action<string> MessageCallback;
    /// <summary>
    /// 回调给Unity
    /// </summary>
    public void _Callback(string data)
    {
        Debug.Log("TimSDK._Callback()");
        Debug.Log(data);

        if(MessageCallback!=null)
        {
            MessageCallback(data);
        }
    }
}
