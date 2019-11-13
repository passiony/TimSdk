//
//  IMSDK_Unity3DBridge.m
//
//  Created by Passion on 2019/10/14.
//  Copyright © 2019年 jiuwei. All rights reserved.
//

#import "TIMSDK_Unity3DBridge.h"
#import <ImSDK/ImSDK.h>
#import "TIMJsonUtil.h"
#import "GenerateTestUserSig.h"

@implementation TIMSDK_Unity3DBridge: NSObject

+ (TIMSDK_Unity3DBridge *)shareInstance{
    static TIMSDK_Unity3DBridge * _singleton = nil ;
    static dispatch_once_t onceToken;
    dispatch_once(&onceToken, ^{
        if (_singleton == nil) {
            _singleton = [[TIMSDK_Unity3DBridge alloc] init];
        }
    });
    return _singleton;
}

// @implementation TIMConnListenerImpl
- (void)onConnSucc {
    SendToUnity([TIMJsonUtil NotifyToJson:TActionConnected desc:@"onConnSucc"]);
}
- (void)onConnFailed:(int)code err:(NSString*)err {
// code 错误码：具体参见错误码表
    NSLog(@"Connect Failed: code=%d, err=%@", code, err);
}
- (void)onDisconnect:(int)code err:(NSString*)err {
    SendToUnity([TIMJsonUtil NotifyToJson:TActionDisconnected desc:@"onDisconnect"]);
}

// @implementation TIMUserStatusListenerImpl 
- (void)onForceOffline {
    SendToUnity([TIMJsonUtil NotifyToJson:TActionForceOffline desc:@"onForceOffline"]);
}
- (void)onUserSigExpired {
    SendToUnity([TIMJsonUtil NotifyToJson:TActionUserSigExpired desc:@"onUserSigExpired"]);
}
- (void)onGroupTipsEvent:(TIMGroupTipsElem*)elem{
    SendToUnity([TIMJsonUtil NotifyToJson:TActionGroupEvent desc:@"onGroupTipsEvent"]);
}

// @implementation TIMRefreshListener
- (void)onRefresh{
    SendToUnity([TIMJsonUtil NotifyToJson:TActionRefresh desc:@"onRefresh"]);
}
- (void)onRefreshConversations:(NSArray<TIMConversation *>*)conversations{
    SendToUnity([TIMJsonUtil NotifyToJson:TActionConversationRefresh desc:@"onRefreshConversations"]);
}

// @implementation TIMMessageListenerImpl
- (void)onNewMessage:(NSArray*) msgs {
    SendToUnity([TIMJsonUtil MessageListToJson:TActionNewMessage timMessges:msgs]);
}

@end

    
#if defined (__cplusplus)
extern "C" {
#endif
    
    void __ctor(char* _u3dGameObject, char* _u3dCallback)
    {
        NSLog(@"TimSdk_Unity3DBridge ctor success");
        
        //缓存u3d信息
        U3dGameObject = [NSString stringWithCString:_u3dGameObject encoding:NSUTF8StringEncoding];
        U3dCallback = [NSString stringWithCString:_u3dCallback encoding:NSUTF8StringEncoding];
        
        NSLog(@"u3d object:%@",U3dGameObject);
    }
    
    void __Init(int appId){
        //设置Config
        TIMSdkConfig *sdkcfg = [[TIMSdkConfig alloc] init];
        sdkcfg.sdkAppId = SDKAPPID;
        sdkcfg.logLevel = TIM_LOG_NONE; //Log输出级别（debug级别会很多）
        //监听网络事件
        sdkcfg.connListener = [TIMSDK_Unity3DBridge shareInstance];
        //初始化TIM
        [[TIMManager sharedInstance] initSdk:sdkcfg];
        
        TIMUserConfig* usercfg=[[TIMUserConfig alloc] init];
        //用户在线状态变更
        usercfg.userStatusListener = [TIMSDK_Unity3DBridge shareInstance];
        usercfg.groupEventListener = [TIMSDK_Unity3DBridge shareInstance];
        usercfg.refreshListener=[TIMSDK_Unity3DBridge shareInstance];
        
        //监听新消息
        [[TIMManager sharedInstance] addMessageListener:[TIMSDK_Unity3DBridge shareInstance]];
    }
    
    void __Login(void* _identifier)
    {
    	NSString* identifier = [NSString stringWithCString:_identifier encoding:NSUTF8StringEncoding];

		TIMLoginParam * login_param = [[TIMLoginParam alloc ]init];
		// identifier 为用户名
		login_param.identifier = identifier;
		//userSig 为用户登录凭证
		login_param.userSig = [GenerateTestUserSig genTestUserSig:identifier];

		[[TIMManager sharedInstance] login: login_param succ:^(){
		    NSLog(@"Login Succ");
            SendToUnity([TIMJsonUtil SuccessToJson:TActionLogin desc:@"login succ"]);
		} fail:^(int code, NSString * err) {
		    NSLog(@"Login Failed: %d->%@", code, err);
            SendToUnity([TIMJsonUtil ErrorToJson:TActionLogin code:code desc:err]);
		}];
    }

    void __Logout()
    {
		[[TIMManager sharedInstance] logout:^() {
		    NSLog(@"logout succ");
            SendToUnity([TIMJsonUtil SuccessToJson:TActionLogout desc:@"logout succ"]);
		} fail:^(int code, NSString * err) {
		    NSLog(@"logout fail: code=%d err=%@", code, err);
            SendToUnity([TIMJsonUtil ErrorToJson:TActionLogout code:code desc:err]);
		}];
    }
    
    void __GetSelfProfile()
    {
        [[TIMFriendshipManager sharedInstance] getSelfProfile:^(TIMUserProfile *profile) {
            NSLog(@"user=%@", profile);
            SendToUnity([TIMJsonUtil ProfileToJson:TActionSelfProfile profile:profile]);
        } fail:^(int code, NSString *err) {
            NSLog(@"GetFriendsProfile fail: code=%d err=%@", code, err);
            SendToUnity([TIMJsonUtil ErrorToJson:TActionSelfProfile code:code desc:err]);
        }];
    }

    void __ModifySelfProfile(void* _nickname,int gender,int birthday,void* _signature,void* _allowType)
    {
        NSString* nickname = [NSString stringWithCString:_nickname encoding:NSUTF8StringEncoding];
        NSString* signature = [NSString stringWithCString:_signature encoding:NSUTF8StringEncoding];
        NSString* allowType = [NSString stringWithCString:_allowType encoding:NSUTF8StringEncoding];
        
        NSDictionary* dict=@{
            TIMProfileTypeKey_Nick:nickname,
            TIMProfileTypeKey_Gender:@(gender),
            TIMProfileTypeKey_Birthday:@(birthday),
            TIMProfileTypeKey_SelfSignature:signature,
            TIMProfileTypeKey_AllowType:allowType};
        
        [[TIMFriendshipManager sharedInstance] modifySelfProfile:dict succ:^(){
            NSLog(@"logout succ");
            SendToUnity([TIMJsonUtil SuccessToJson:TActionModifyProfile desc:@"modify succ"]);
        } fail:^(int code, NSString* err){
            NSLog(@"__ModifySelfProfile fail: code=%d err=%@", code, err);
            SendToUnity([TIMJsonUtil ErrorToJson:TActionModifyProfile code:code desc:err]);
        }];
    }
    
    void __GetUserProfile(void* _identifier, bool forceUpdate)
    {
    	NSString* identifier = [NSString stringWithCString:_identifier encoding:NSUTF8StringEncoding];

    	NSMutableArray * arr = [[NSMutableArray alloc] init];
		[arr addObject:identifier];
		[[TIMFriendshipManager sharedInstance] getUsersProfile:arr forceUpdate:forceUpdate succ:^(NSArray * arr) {
            
            NSLog(@"__GetUserProfile succ");
		    for (TIMUserProfile * profile in arr) {
                SendToUnity([TIMJsonUtil ProfileToJson:TActionUserProfile profile:profile]);
		    }
		}fail:^(int code, NSString * err) {
		    NSLog(@"__GetUserProfile fail: code=%d err=%@", code, err);
            SendToUnity([TIMJsonUtil ErrorToJson:TActionUserProfile code:code desc:err]);
		}];
    }

    void __GetFriendList()
    {
		[[TIMFriendshipManager sharedInstance] getFriendList:^(NSArray<TIMFriend *> *friends) {
            SendToUnity([TIMJsonUtil FriendListToJson:friends]);
		} fail:^(int code, NSString *err) {
			 NSLog(@"__GetFriendList fail: code=%d err=%@", code, err);
            SendToUnity([TIMJsonUtil ErrorToJson:TActionFriendsList code:code desc:err]);
		}];
    }

    void __AddFriend(void* _identifier, void* _addword, void* _remark)
    {
    	NSString* identifier = [NSString stringWithCString:_identifier encoding:NSUTF8StringEncoding];
    	NSString* addword = [NSString stringWithCString:_addword encoding:NSUTF8StringEncoding];
        NSString* remark = [NSString stringWithCString:_remark encoding:NSUTF8StringEncoding];

		TIMFriendRequest *q = [TIMFriendRequest new];
		q.identifier = identifier; // 加好友 abc
		q.addWording = addword;
		q.addSource = @"AddSource_Type_iOS";
		q.remark = remark;

		[[TIMFriendshipManager sharedInstance] addFriend:q succ:^(TIMFriendResult *result) {
            SendToUnity([TIMJsonUtil FriendResultToJson:TActionAddFriend timFriend:result]);
		} fail:^(int code, NSString *err) {
            NSLog(@"失败：%d, %@", code, err);
            SendToUnity([TIMJsonUtil ErrorToJson:TActionAddFriend code:code desc:err]);
		}];
    }

    void __DeleteFriend(void* _identifier,int delType)
    {
    	NSString* identifier = [NSString stringWithCString:_identifier encoding:NSUTF8StringEncoding];

		NSMutableArray * del_users = [[NSMutableArray alloc] init];
		[del_users addObject:identifier];
		
		// TIM_FRIEND_DEL_BOTH 指定删除双向好友
		[[TIMFriendshipManager sharedInstance] deleteFriends:del_users delType:delType succ:^(NSArray<TIMFriendResult *> *results) {
		    for (TIMFriendResult * res in results) {
                SendToUnity([TIMJsonUtil FriendResultToJson:TActionDeleteFriend timFriend:res]);
		    }
		} fail:^(int code, NSString * err) {
		    NSLog(@"deleteFriends failed: code=%d err=%@", code, err);
            SendToUnity([TIMJsonUtil ErrorToJson:TActionDeleteFriend code:code desc:err]);
		}];
    }

    void __DoResponse(void* _identifier, int agreeType)
    {
    	NSString* identifier = [NSString stringWithCString:_identifier encoding:NSUTF8StringEncoding];

    	TIMFriendResponse *response = [[TIMFriendResponse alloc] init];
        response.responseType=(TIMFriendResponseType)agreeType;
        response.identifier=identifier;

		[[TIMFriendshipManager sharedInstance] doResponse:response succ:^(TIMFriendResult *result) {
            SendToUnity([TIMJsonUtil FriendResultToJson:TActionDoResponse timFriend:result]);
		} fail:^(int code, NSString * err) {
		    NSLog(@"deleteFriends failed: code=%d err=%@", code, err);
            SendToUnity([TIMJsonUtil ErrorToJson:TActionDoResponse code:code desc:err]);
		}];
    }

    void __GetPendencyList(int seq,int numPerPage,int timeStamp,int type)
    {
    	TIMFriendPendencyRequest *request= [[TIMFriendPendencyRequest alloc] init];
        request.seq=seq;
        request.numPerPage=numPerPage;
        request.timestamp=timeStamp;
        request.type=type;
        
		[[TIMFriendshipManager sharedInstance] getPendencyList:request succ:^(TIMFriendPendencyResponse *result) {
            SendToUnity([TIMJsonUtil PendentListToJson:result]);
        } fail:^(int code, NSString * err) {
		    NSLog(@"__GetPendencyList failed: code=%d err=%@", code, err);
            SendToUnity([TIMJsonUtil ErrorToJson:TActionPendencyList code:code desc:err]);
		}];
    }
    
    void __SendMessage(void* _peer, void* _message, int type)
    {
    	NSString* peer = [NSString stringWithCString:_peer encoding:NSUTF8StringEncoding];
    	NSString* message = [NSString stringWithCString:_message encoding:NSUTF8StringEncoding];
    	
		//获取聊天会话
		TIMConversation * conversation = [[TIMManager sharedInstance] getConversation:type receiver:peer];

		//构造文本内容
		TIMTextElem * text_elem = [[TIMTextElem alloc] init];
		[text_elem setText:message];

		//构造一条消息
		TIMMessage * msg = [[TIMMessage alloc] init];
		[msg addElem:text_elem];

        
		//发送消息
		[conversation sendMessage:msg succ:^(){  //成功
		       NSLog(@"SendMsg Succ");
            SendToUnity([TIMJsonUtil MessageToJson:TActionSendMessage message:msg]);
		}fail:^(int code, NSString * err) {  //失败
            NSLog(@"SendMsg Failed:%d->%@", code, err);
            SendToUnity([TIMJsonUtil ErrorToJson:TActionSendMessage code:code desc:err]);
		}];
    }

    void __GetMessage(void* _peer, int count, int type)
    {
    	NSString* peer = [NSString stringWithCString:_peer encoding:NSUTF8StringEncoding];

    	//获取聊天会话
		TIMConversation * conversation = [[TIMManager sharedInstance] getConversation:TIM_C2C receiver:peer];
		
		[conversation getMessage:10 last:nil succ:^(NSArray * msgList) {
            SendToUnity([TIMJsonUtil MessageListToJson:TActionGetMessage timMessges:msgList]);
		}fail:^(int code, NSString * err) {
		    NSLog(@"Get Message Failed:%d->%@", code, err);
            SendToUnity([TIMJsonUtil ErrorToJson:TActionGetMessage code:code desc:err]);
		}];
    }

    void __CreateGroup(int addOption){
        // 创建群组信息
        TIMCreateGroupInfo *groupInfo = [[TIMCreateGroupInfo alloc] init];
        groupInfo.groupName = @"group_public";
        groupInfo.groupType = @"Public";
        groupInfo.addOpt = addOption;
        groupInfo.maxMemberNum = 0;
        groupInfo.notification = @"this is a notification";
        groupInfo.introduction = @"this is a introduction";
        groupInfo.faceURL = nil;
        
        // 创建指定属性群组
        [[TIMGroupManager sharedInstance] createGroup:groupInfo succ:^(NSString * group) {
            NSLog(@"create group succ, sid=%@", group);
            SendToUnity([TIMJsonUtil GroupToJson:group]);
        } fail:^(int code, NSString* err) {
            NSLog(@"failed code: %d %@", code, err);
            SendToUnity([TIMJsonUtil ErrorToJson:TActionCreateGroup code:code desc:err]);
        }];
    }
    
    void __ApplyJoinGroup(void* _groupId, void* _reason){
        NSString* groupId = [NSString stringWithCString:_groupId encoding:NSUTF8StringEncoding];
        NSString* reason = [NSString stringWithCString:_reason encoding:NSUTF8StringEncoding];

        [[TIMGroupManager sharedInstance] joinGroup:groupId msg:reason succ:^(){
            NSLog(@"Join Succ");
            SendToUnity([TIMJsonUtil SuccessToJson:TActionApplyJoinGroup desc:groupId]);
        }fail:^(int code, NSString * err) {
            NSLog(@"code=%d, err=%@", code, err);
            SendToUnity([TIMJsonUtil ErrorToJson:TActionApplyJoinGroup code:code desc:err]);
        }];
    }
    
    void __QuitGroup(void* _groupId){
        NSString* groupId = [NSString stringWithCString:_groupId encoding:NSUTF8StringEncoding];
        
        [[TIMGroupManager sharedInstance] quitGroup:groupId succ:^() {
            NSLog(@"succ");
            SendToUnity([TIMJsonUtil SuccessToJson:TActionQuitGroup desc:groupId]);
        } fail:^(int code, NSString* err) {
            NSLog(@"failed code: %d %@", code, err);
            SendToUnity([TIMJsonUtil ErrorToJson:TActionQuitGroup code:code desc:err]);
        }];
    }
    
    void __GetGroupMembers(void* _groupId){
        NSString* groupId = [NSString stringWithCString:_groupId encoding:NSUTF8StringEncoding];
        
        [[TIMGroupManager sharedInstance] getGroupMembers:groupId succ:^(NSArray* list) {
            SendToUnity([TIMJsonUtil GroupMembersToJson:list]);
        } fail:^(int code, NSString * err) {
            NSLog(@"failed code: %d %@", code, err);
            SendToUnity([TIMJsonUtil ErrorToJson:TActionGetGroupMembers code:code desc:err]);
        }];
    }
    
    void __GetGroupList(){
        
        [[TIMGroupManager sharedInstance] getGroupList:^(NSArray * list) {
            SendToUnity([TIMJsonUtil GroupListToJson:list]);
        } fail:^(int code, NSString* err) {
            SendToUnity([TIMJsonUtil ErrorToJson:TActionGroupList code:code desc:err]);
        }];
    }


    //TODO:发送给Unity
    void SendToUnity(NSString* message) {
        NSLog(@"SendToUnity: %@", message);
    	UnitySendMessage([U3dGameObject UTF8String], [U3dCallback UTF8String], [message UTF8String]);
    }
    
#if defined (__cplusplus)
}
#endif

