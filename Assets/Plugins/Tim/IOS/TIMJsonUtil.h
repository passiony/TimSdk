//
//  TIMJsonUtil.m
//  Unity-iPhone
//
//  Created by 李政奇 on 2019/11/7.
//

#import <ImSDK/ImSDK.h>

typedef enum
{
    TStateFailed = 0,
    TStateSuccess,
    TStateNotify,
}TState;

typedef enum
{
    TActionForceOffline = 0,
    TActionUserSigExpired,
    TActionConnected,
    TActionDisconnected,
    TActionWifiNeedAuth,
    TActionGroupEvent,
    TActionRefresh,
    TActionConversationRefresh,
    TActionNewMessage,
    TActionLogin,
    TActionLogout,
    TActionSelfProfile,
    TActionModifyProfile,
    TActionUserProfile,
    TActionFriendsList,
    TActionAddFriend,
    TActionDeleteFriend,
    TActionDoResponse,
    TActionPendencyList,
    TActionSendMessage,
    TActionGetMessage,
    TActionCreateGroup,
    TActionApplyJoinGroup,
    TActionQuitGroup,
    TActionGetGroupMembers,
    TActionGroupList,
}TAction;


@interface TIMJsonUtil : NSObject

//TODO:JSON解析
+ (NSString*) ErrorToJson:(TAction)action code:(int)code desc:(NSString*) desc;
+ (NSString*) SuccessToJson:(TAction) action desc:(NSString*)desc;
+ (NSString*) NotifyToJson:(TAction)action desc:(NSString*)desc;
//用户信息
+ (NSString*) ProfileToJson:(TAction) action profile:(TIMUserProfile*)profile;
//好友处理结果
+ (NSString*) FriendResultToJson:(TAction) action timFriend:(TIMFriendResult*)timFriend;
  //好友列表
+ (NSString*) FriendListToJson:(NSArray<TIMFriend*> *) timFriends;
//未决列表
+ (NSString*) PendentListToJson:(TIMFriendPendencyResponse*) response;
//会话信息
+ (NSString*) ConversationToJson:(NSArray<TIMConversation*>*)conversations;
//聊天消息
+ (NSString*) MessageToJson:(TAction)action message:(TIMMessage*)message;
//聊天消息列表
+ (NSString*) MessageListToJson:(TAction) action timMessges:(NSArray<TIMMessage*>*) timMessages;

//Tim的消息转Json
+ (NSDictionary*) TimMessage2Dic:(TIMMessage*) message;

//Group消息
+ (NSString*) GroupEventToJson:(TIMGroupTipsElem*)elem;
+ (NSString*) GroupToJson:(NSString*) group;
+ (NSString*) GroupMembersToJson:(NSArray<TIMGroupMemberInfo*>*) infoArray;
+ (NSString*) GroupListToJson:(NSArray<TIMGroupInfo*>*) timInfos;

@end
