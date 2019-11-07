
using System;

public interface ITimImpl
{

    // 初始化
    void Init(int appId);
    // 登录
    void Login(string userId);
    // 登出
    void Logout();



    // 获取自己的信息
    void GetSelfProfile();
    // 修改自己的信息()"AllowType_Type_Invalid";"AllowType_Type_AllowAny";"AllowType_Type_DenyAny";"AllowType_Type_NeedConfirm";
    void ModifySelfProfile(string nickname, int gender, int birthday, string signature, string allowType);
    // 获取用户资料
    void GetUserProfile(string identifier, bool forceUpdate);



    // 获取好友列表()
    void GetFriendList();
    // 添加好友
    void AddFriend(string identifier , string addword,string remark);
    //删除好友
    void DeleteFriend(string identifier);
    // 处理好友请求(同意/同意并添加/拒绝)
    void DoResponse(int type);
    // 获取未决列表()
    void GetPendencyList(int seq, int numPerPage, int timeStamp, int type);



    // 发送消息(Invalid/C2C/群聊/系统)
    void SendMessage(string peer, string message, int type);
    // 获取会话漫游消息(Invalid/C2C/群聊/系统)
    void GetMessage(string peer, int count, int type);


    /// 创建群聊(0:拒绝;1:授权;2:任意)
    void CreateGroup(int addOption);
    /// 加入群组
    void ApplyJoinGroup(string groupId, string reason);
    /// 退出群聊
    void QuitGroup(string groupId);
    /// 获取群组成员列表
    void GetGroupMembers(string groupId);
    /// 获取群列表
    void GetGroupList();
}
