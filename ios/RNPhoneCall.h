#import <Foundation/Foundation.h>
#import <UIKit/UIKit.h>
#import <CallKit/CXCallObserver.h>
#import <CallKit/CXCall.h>
#if __has_include("RCTBridgeModule.h")
#import "RCTBridgeModule.h"
#else
#import <React/RCTBridgeModule.h>
#endif

@interface RNPhoneCall : NSObject <RCTBridgeModule>

@property (nonatomic) CXCallObserver *callObserver;
@property (nonatomic, strong) RCTPromiseResolveBlock resolve;
@property (nonatomic, strong) RCTPromiseRejectBlock reject;
@property (nonatomic) BOOL hasConnected;
@property (nonatomic) NSNumber *callStartTime;

@end
  