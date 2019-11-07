using System;
using System.Runtime.InteropServices;

public class IOSTimSDKImpl : ITimImpl
{

    [DllImport("__Internal")]
    private static extern void __Init(int appId);
    [DllImport("__Internal")]
    private static extern void __Login(string userId);
    [DllImport("__Internal")]
    private static extern void __Logout();

    [DllImport("__Internal")]
    private static extern void __GetSelfProfile();
    [DllImport("__Internal")]
    private static extern void __ModifySelfProfile(string nickname, int gender, int birthday, string signature, string allowType);
    [DllImport("__Internal")]
    private static extern void __GetUsersProfile(string identifier, bool forceUpdate);

    [DllImport("__Internal")]
    private static extern void __GetFriendList();
    [DllImport("__Internal")]
    private static extern void __AddFriend(string identifier, string addword, string remark);
    [DllImport("__Internal")]
    private static extern void __DeleteFriend(string identifier);
    [DllImport("__Internal")]
    private static extern void __DoResponse(int type);
    [DllImport("__Internal")]
    private static extern void __GetPendencyList(int seq, int numPerPage, int timeStamp, int type);

    [DllImport("__Internal")]
    private static extern void __SendMessage(string peer, string message, int type);
    [DllImport("__Internal")]
    private static extern void __GetMessage(string peer, int count, int type);

    [DllImport("__Internal")]
    private static extern void __CreateGroup(int addOption);
    [DllImport("__Internal")]
    private static extern void __ApplyJoinGroup(string groupId, string reason);
    [DllImport("__Internal")]
    private static extern void __QuitGroup(string groupId);
    [DllImport("__Internal")]
    private static extern void __GetGroupMembers(string groupId);
    [DllImport("__Internal")]
    private static extern void __GetGroupList();
    

    #region Init
    public void Init(int appID)
    {
        __Init(appID);
    }

    public void Login(string userId)
    {
        __Login(userId);
    }

    public void Logout()
    {
        __Logout();
    }

#endregion

#region Profile
    public void GetSelfProfile()
    {
        __GetSelfProfile();
    }

    public void ModifySelfProfile(string nickname, int gender, int birthday, string signature, string allowType)
    {
        __ModifySelfProfile(nickname, gender, birthday, signature, allowType);
    }

    public void GetUsersProfile(string identifier, bool forceUpdate)
    {
        __GetUsersProfile(identifier, forceUpdate);
    }

#endregion


#region Friend
    public void AddFriend(string identifier, string addword, string remark)
    {
        __AddFriend(identifier, addword, remark);
    }

    public void DeleteFriend(string identifier)
    {
        __DeleteFriend(identifier);
    }

    public void GetFriendList()
    {
        __GetFriendList();
    }

    public void GetPendencyList(int seq, int numPerPage, int timeStamp, int type)
    {
        __GetPendencyList(seq, numPerPage, timeStamp, type);
    }

    public void DoResponse(int type)
    {
        __DoResponse(type);
    }

    public void SendMessage(string peer, string message,int type)
    {
        __SendMessage(peer, message, type);
    }

    public void GetMessage(string peer, int count, int type)
    {
        __GetMessage(peer, count, type);
    }
#endregion


#region Group
    public void CreateGroup(int addOption)
    {
        __CreateGroup(addOption);
    }

    public void ApplyJoinGroup(string groupId, string reason)
    {
        __ApplyJoinGroup(groupId, reason);
    }

    public void QuitGroup(string groupId)
    {
        __QuitGroup(groupId);
    }
    
    public void GetGroupMembers(string groupId)
    {
        __GetGroupMembers(groupId);
    }

    public void GetGroupList()
    {
        __GetGroupList();
    }
    #endregion

}

