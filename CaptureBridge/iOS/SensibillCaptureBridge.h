#import <Foundation/Foundation.h>
#import <React/RCTBridge.h>
#import <React/RCTEventEmitter.h>
#import <React/RCTRootView.h>

@interface SensibillCaptureBridge : NSObject <RCTBridgeModule>
@end

@interface SensibillCaptureBridgeEventEmitter : RCTEventEmitter <RCTBridgeModule>
+ (void)sendBase64Image:(NSString *)base64Image;
@end
