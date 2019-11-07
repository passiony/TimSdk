//
//  IMSDK_Unity3DBridge.m
//
//  Created by Passion on 2019/10/14.
//  Copyright © 2019年 jiuwei. All rights reserved.
//

#import <ImSDK/ImSDK.h>

static NSString * U3dGameObject =@"";
static NSString * U3dCallback =@"";

#if defined (__cplusplus)
extern "C" {
#endif
    //声明
    extern void SendToUnity(NSString* message) ;
   
#if defined (__cplusplus)
}
#endif

@interface TIMSDK_Unity3DBridge : NSObject<TIMConnListener,TIMUserStatusListener,TIMMessageListener,TIMGroupEventListener,TIMRefreshListener>

+ (TIMSDK_Unity3DBridge *)shareInstance;

// @implementation TIMConnListenerImpl
- (void)onConnSucc;
- (void)onConnFailed:(int)code err:(NSString*)err;
- (void)onDisconnect:(int)code err:(NSString*)err;

// @implementation TIMUserStatusListenerImpl
- (void)onForceOffline;
- (void)onUserSigExpired;

// @implementation TIMGroupEventListener
- (void)onGroupTipsEvent:(TIMGroupTipsElem*)elem;

// @implementation TIMRefreshListener
- (void)onRefresh;
- (void)onRefreshConversations:(NSArray<TIMConversation *>*)conversations;

// @implementation TIMMessageListenerImpl
- (void)onNewMessage:(NSArray*) msgs;

@end

