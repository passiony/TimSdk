//
//  TIMJsonUtil.m
//  Unity-iPhone
//
//  Created by 李政奇 on 2019/11/7.
//

#import "TIMJsonUtil.h"
#import "YYModel.h"


@implementation TIMJsonUtil


+ (NSString*) ErrorToJson:(TAction)action code:(int)code desc:(NSString*) desc{
    NSMutableDictionary *dict = [NSMutableDictionary dictionary];
    [dict setObject:@(TStateFailed) forKey:@"state"];
    [dict setObject:@(action) forKey:@"action"];
    [dict setObject:@(code) forKey:@"code"];
    [dict setObject:desc forKey:@"desc"];
    
    return [dict yy_modelToJSONString];
}
+ (NSString*) SuccessToJson:(TAction) action desc:(NSString*)desc{
    NSMutableDictionary *dict = [NSMutableDictionary dictionary];
    [dict setObject:@(TStateSuccess) forKey:@"state"];
    [dict setObject:@(action) forKey:@"action"];
    [dict setObject:desc forKey:@"desc"];
    
    return [dict yy_modelToJSONString];
}
+ (NSString*) NotifyToJson:(TAction)action desc:(NSString*)desc{
    NSMutableDictionary *dict = [NSMutableDictionary dictionary];
    [dict setObject:@(TStateNotify) forKey:@"state"];
    [dict setObject:@(action) forKey:@"action"];
    [dict setObject:desc forKey:@"desc"];

    return [dict yy_modelToJSONString];
}
//用户信息
+ (NSString*) ProfileToJson:(TAction)action profile:(TIMUserProfile*)profile{
    NSMutableDictionary *dict = [NSMutableDictionary dictionary];
        [dict setObject:@(TStateSuccess) forKey:@"state"];
        [dict setObject:@(action) forKey:@"action"];
    
        NSMutableDictionary * obj= [NSMutableDictionary dictionary];
        [obj setObject:profile.identifier forKey:@"identifier"];
        [obj setObject:profile.nickname forKey:@"nickName"];
        [obj setObject:profile.faceURL forKey:@"faceUrl"];
        [obj setObject:@(profile.gender) forKey:@"gender"];
        [obj setObject:@(profile.birthday) forKey:@"birthday"];
        [obj setObject:@(profile.allowType) forKey:@"allow"];
        [obj setObject:profile.selfSignature forKey:@"selfSignature"];
    
        [dict setObject:obj forKey:@"json"];

        return [dict yy_modelToJSONString];
}

//好友处理结果
+ (NSString*) FriendResultToJson:(TAction) action timFriend:(TIMFriendResult*)timFriend{
    NSMutableDictionary *dict = [NSMutableDictionary dictionary];
    [dict setObject:@(TStateSuccess) forKey:@"state"];
    [dict setObject:@(action) forKey:@"action"];
    
    NSMutableDictionary * obj= [NSMutableDictionary dictionary];
    [obj setObject:timFriend.identifier forKey:@"identifier"];
    [obj setObject:@(timFriend.result_code) forKey:@"resultCode"];
    [obj setObject:timFriend.result_info forKey:@"resultInfo"];
    
    [dict setObject:obj forKey:@"json"];
    return [dict yy_modelToJSONString];
}
  //好友列表
+ (NSString*) FriendListToJson:(NSArray<TIMFriend*> *) timFriends{
    NSMutableDictionary *dict = [NSMutableDictionary dictionary];
    [dict setObject:@(TStateSuccess) forKey:@"state"];
    [dict setObject:@(TActionFriendsList) forKey:@"action"];
    
    NSMutableArray *array=[NSMutableArray array];
    for (TIMFriend* result in timFriends) {
        NSMutableDictionary * obj= [NSMutableDictionary dictionary];
        [obj setObject:result.profile.identifier forKey:@"identifier"];
        [obj setObject:result.profile.nickname forKey:@"nickName"];
        [obj setObject:result.remark forKey:@"remark"];
        [obj setObject:result.profile.faceURL forKey:@"faceUrl"];
        [obj setObject:@(result.profile.gender) forKey:@"gender"];
        [obj setObject:@(result.profile.birthday) forKey:@"birthday"];
        [obj setObject:@(result.profile.allowType) forKey:@"allow"];
        [obj setObject:result.profile.selfSignature forKey:@"selfSignature"];
        
        [array addObject:obj];
    }
    
    [dict setObject:array forKey:@"json"];
    return [dict yy_modelToJSONString];
}
//未决列表
+ (NSString*) PendentListToJson:(TIMFriendPendencyResponse*) response{
    NSMutableDictionary *dict = [NSMutableDictionary dictionary];
    [dict setObject:@(TStateSuccess) forKey:@"state"];
    [dict setObject:@(TActionPendencyList) forKey:@"action"];
    
    NSMutableArray *array=[NSMutableArray array];
    for (TIMFriendPendencyItem* result in response.pendencies) {
        NSMutableDictionary * obj= [NSMutableDictionary dictionary];
        [obj setObject:result.identifier forKey:@"identifier"];
        [obj setObject:result.nickname forKey:@"nickName"];
        [obj setObject:result.addWording forKey:@"addWording"];
        [obj setObject:result.addSource forKey:@"addSource"];
        [obj setObject:@(result.type) forKey:@"type"];
        
        [array addObject:obj];
    }
    
    [dict setObject:array forKey:@"json"];
    return [dict yy_modelToJSONString];
}
//会话信息
+ (NSString*) ConversationToJson:(NSArray<TIMConversation*>*)conversations{
    NSMutableDictionary *dict = [NSMutableDictionary dictionary];
    [dict setObject:@(TStateSuccess) forKey:@"state"];
    [dict setObject:@(TActionConversationRefresh) forKey:@"action"];
    
    NSMutableArray *array=[NSMutableArray array];
    for (TIMConversation* result in conversations) {
        NSMutableDictionary * obj= [NSMutableDictionary dictionary];
        [obj setObject:[result getReceiver] forKey:@"peer"];
        [obj setObject:@([result getType]) forKey:@"type"];
        [obj setObject:[TIMJsonUtil TimMessage2Dic:[result getLastMsg]] forKey:@"lastMsg"];
        
        [array addObject:obj];
    }
    
    [dict setObject:array forKey:@"json"];
    return [dict yy_modelToJSONString];
}
//聊天消息
+ (NSString*) MessageToJson:(TAction)action message:(TIMMessage*)message{
    NSMutableDictionary *dict = [NSMutableDictionary dictionary];
    [dict setObject:@(TStateSuccess) forKey:@"state"];
    [dict setObject:@(action) forKey:@"action"];
    
    [dict setObject:[TIMJsonUtil TimMessage2Dic:message] forKey:@"json"];
    return [dict yy_modelToJSONString];
}
//聊天消息列表
+ (NSString*) MessageListToJson:(TAction) action timMessges:(NSArray<TIMMessage*>*) timMessages{
    NSMutableDictionary *dict = [NSMutableDictionary dictionary];
    [dict setObject:@(TStateSuccess) forKey:@"state"];
    [dict setObject:@(action) forKey:@"action"];
    
    NSMutableArray *array=[NSMutableArray array];
    for (TIMMessage* msg in timMessages) {
        if ([msg isKindOfClass:[TIMMessage class]]) {
            [array addObject:[TIMJsonUtil TimMessage2Dic:msg]];
        }
    }
    
    [dict setObject:array forKey:@"json"];
    return [dict yy_modelToJSONString];
}

//Tim的消息转Json字典
+ (NSDictionary*) TimMessage2Dic:(TIMMessage*) message{
    NSMutableDictionary* dict=[NSMutableDictionary dictionary];
    TIMElem* elem=[message getElem:0];
    
    [dict setObject:message.sender forKey:@"sender"];
    [dict setObject:@(message.isSelf) forKey:@"isSelf"];
    [dict setObject:@([[message getConversation] getType]) forKey:@"conversationType"];
    
    if ([elem isKindOfClass:[TIMTextElem class]]) {
        [dict setObject:@"Text" forKey:@"type"];
        [dict setObject:((TIMTextElem*)elem).text forKey:@"text"];
    }
    else if ([elem isKindOfClass:[TIMImageElem class]]){
        [dict setObject:@"Image" forKey:@"type"];
        [dict setObject:@"" forKey:@"image"];
    }
    else if ([elem isKindOfClass:[TIMVideoElem class]]){
        [dict setObject:@"Video" forKey:@"type"];
        [dict setObject:@"" forKey:@"video"];
    }
    return dict;
}

//Group Event消息
+ (NSString*) GroupEventToJson:(TIMGroupTipsElem*)elem{
    NSMutableDictionary *dict = [NSMutableDictionary dictionary];
     [dict setObject:@(TStateNotify) forKey:@"state"];
     [dict setObject:@(TActionGroupEvent) forKey:@"action"];
    
     NSMutableDictionary *obj = [NSMutableDictionary dictionary];
     [obj setObject:elem.group forKey:@"groupId"];
     [obj setObject:elem.groupName forKey:@"groupName"];
     [obj setObject:@(elem.memberNum) forKey:@"memberNum"];
     [obj setObject:@(elem.type) forKey:@"tipsType"];
     
     [dict setObject:obj forKey:@"json"];
     return [dict yy_modelToJSONString];
}

//创建群
+ (NSString*) GroupToJson:(NSString*) group{
    NSMutableDictionary *dict = [NSMutableDictionary dictionary];
    [dict setObject:@(TStateNotify) forKey:@"state"];
    [dict setObject:@(TActionCreateGroup) forKey:@"action"];
    
    [dict setObject:group forKey:@"json"];
    return [dict yy_modelToJSONString];
}

//群成员
+ (NSString*) GroupMembersToJson:(NSArray<TIMGroupMemberInfo*>*) infoArray{
    NSMutableDictionary *dict = [NSMutableDictionary dictionary];
     [dict setObject:@(TStateSuccess) forKey:@"state"];
     [dict setObject:@(TActionGetGroupMembers) forKey:@"action"];
            
     NSMutableArray *array=[NSMutableArray array];
     for (TIMGroupMemberInfo* mem in infoArray) {
         [array addObject:mem.member];
     }
            
    [dict setObject:array forKey:@"json"];
     return [dict yy_modelToJSONString];
}
+ (NSString*) GroupListToJson:(NSArray<TIMGroupInfo*>*) timInfos{
    NSMutableDictionary *dict = [NSMutableDictionary dictionary];
     [dict setObject:@(TStateSuccess) forKey:@"state"];
     [dict setObject:@(TActionGroupList) forKey:@"action"];
            
     NSMutableArray *array=[NSMutableArray array];
     for (TIMGroupInfo* mem in timInfos) {
         NSMutableDictionary* obj=[NSMutableDictionary dictionary];
         [obj setObject:mem.group forKey:@"groupId"];
         [obj setObject:mem.groupName forKey:@"groupName"];
         [obj setObject:mem.groupType forKey:@"groupType"];
         
         [array addObject:obj];
     }
    
    [dict setObject:array forKey:@"json"];
     return [dict yy_modelToJSONString];
}


@end
