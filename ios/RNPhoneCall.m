
#import "RNPhoneCall.h"

@implementation RNPhoneCall

- (dispatch_queue_t)methodQueue
{
    return dispatch_get_main_queue();
}
RCT_EXPORT_MODULE()

RCT_REMAP_METHOD(call,
                 telephone: (NSString *)phoneNum
                 resolver:(RCTPromiseResolveBlock)resolve
                 rejecter:(RCTPromiseRejectBlock)reject)
{
  NSURL *url= [NSURL URLWithString:[NSString stringWithFormat:@"telprompt://%@", phoneNum]];
  [[UIApplication sharedApplication] openURL:url];
  self.hasConnected = false;
  self.reject = reject;
  self.resolve = resolve;
  self.callObserver = [[CXCallObserver alloc] init];
  [self.callObserver setDelegate:self queue:nil];
}

-(NSNumber*) getCurrentTimeMillions {
  NSTimeInterval timeStamp = [[NSDate date] timeIntervalSince1970];
  return [NSNumber numberWithLong: timeStamp * 1000];
}

- (void)callObserver:(CXCallObserver *)callObserver callChanged:(CXCall *)call {
  if (call.hasEnded && call.isOutgoing) {
    if (self.hasConnected) {
      NSNumber *endCallTime = [self getCurrentTimeMillions];
      long duration = ([endCallTime integerValue] - [[self callStartTime] integerValue]) / 1000;
      self.resolve(@{@"call_duration": [NSNumber numberWithLong: duration]});
    } else {
      self.reject(@"PhoneCall", @"Phone call is not connected", NULL);
    }
    [self.callObserver setDelegate:nil queue:nil];
    self.callObserver = nil;
    return;
  }

  if (call.hasConnected) {
    self.hasConnected = true;
    self.callStartTime = [self getCurrentTimeMillions];
  }
}

@end
  