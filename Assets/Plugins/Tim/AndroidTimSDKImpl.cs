using System;
using UnityEngine;

public class AndroidTimSDKImpl : ITimImpl
{
    private AndroidJavaObject imsdk;

    public AndroidTimSDKImpl(GameObject go, string callback)
    {
        Debug.Log("AndroidImpl  ===>>>  AndroidImpl");
        try
        {
            string path = "com.jiuwei.timdemo.TimSDKUtils";
            Debug.Log(path);
            imsdk = new AndroidJavaObject(path, go.name, callback);
        }
        catch (Exception e)
        {
            Debug.LogFormat("{0} Exception caught.", e);
        }
    }

    #region Init
    public void Init(int appID)
    {
        imsdk.Call("Init" , appID);
    }

    public void Login(string userId)
    {
        imsdk.Call("Login" , userId);
    }

    public void Logout()
    {
        imsdk.Call("Logout");
    }

    #endregion

    #region Profile
    public void GetSelfProfile()
    {
        imsdk.Call("GetSelfProfile");
    }
    public void ModifySelfProfile(string nickname, int gender, int birthday, string signature, string allowType)
    {
        imsdk.Call("ModifySelfProfile", nickname, gender, birthday, signature, allowType);
    }

    public void GetUserProfile(string identifier,bool forceUpdate)
    {
        imsdk.Call("GetUsersProfile", identifier, forceUpdate);
    }

    #endregion


    #region Friend
    public void AddFriend(string identifier, string addword, string remark)
    {
        imsdk.Call("AddFriend" , identifier, addword, remark);
    }

    public void DeleteFriend(string identifier)
    {
        imsdk.Call("DeleteFriend", identifier);
    }

    public void GetFriendList()
    {
        imsdk.Call("GetFriendList");
    }

    public void GetPendencyList(int seq, int numPerPage, int timeStamp, int type)
    {
        imsdk.Call("GetPendencyList", seq, numPerPage, timeStamp, type);
    }

    public void DoResponse(int type)
    {
        imsdk.Call("DoResponse" , type);
    }

    public void SendMessage(string peer, string message, int type)
    {
        imsdk.Call("SendMessage", peer, message, type);
    }

    public void GetMessage(string peer, int count, int type)
    {
        imsdk.Call("GetMessage", peer, count, type);
    }
    #endregion


    #region Group
    public void CreateGroup(int addOption)
    {
        imsdk.Call("CreateGroup",addOption);
    }

    public void ApplyJoinGroup(string groupId, string reason)
    {
        imsdk.Call("ApplyJoinGroup", groupId, reason);
    }

    public void QuitGroup(string groupId)
    {
        imsdk.Call("QuitGroup", groupId);
    }

    public void GetGroupMembers(string groupId)
    {
        imsdk.Call("GetGroupMembers", groupId);
    }

    public void GetGroupList()
    {
        imsdk.Call("GetGroupList");
    }

    #endregion

}
