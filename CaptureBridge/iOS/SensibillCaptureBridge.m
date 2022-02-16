#import "SensibillCaptureBridge.h"

@import Sensibill;

@interface SensibillCaptureBridge() <SBLCaptureNavigationControllerDelegate>

@end

@implementation SensibillCaptureBridge

RCT_EXPORT_MODULE();

RCT_EXPORT_METHOD(log:(NSString *)data)
{
  NSLog(@"WE ARE LOGGING SOMETHING %@", data);
}

RCT_EXPORT_METHOD(launchCapture)
{
  // Get configuration bundle (optional)
  NSBundle *customBundle = [NSBundle mainBundle];
  
  // Create Defaults
  SBLDefaultTheme *defaults = [[SBLDefaultTheme alloc] initWithOverrideBundle:customBundle];
  defaults.primary = [UIColor blackColor];
  defaults.onPrimary = [UIColor whiteColor];
  
  // Colors defined in the default theme will have module wide effects
  
  // Create Capture theme with defaults
  SBLCaptureTheme *captureTheme = [[SBLCaptureTheme alloc] initWithDefaults:defaults];
  captureTheme.capturePreviewDoneButtonBackgroundColor = [UIColor blueColor];
  
  // Colors defined in the capture theme will customize individual components, overriding colors provided by the default theme
  
  // Create Config with capture theme
  SBLCaptureConfigurationBuilder * configBuilder = [[SBLCaptureConfigurationBuilder alloc] addWithTheme:captureTheme];
  SBLCaptureConfiguration *config = [configBuilder build];
  
  // Present UI on main thread
  dispatch_async(dispatch_get_main_queue(), ^{
    
    // Create and pass configs to the capture module
    SBLCaptureNavigationController *capture = [[SBLCaptureNavigationController alloc] initWithConfiguration:config];
    
    // Assign self to receive the captured image
    capture.captureDelegate = self;
    
    // Present it
    capture.modalPresentationStyle = UIModalPresentationFullScreen;
    [[UIApplication sharedApplication].delegate.window.rootViewController presentViewController:capture animated:YES completion:nil];
  });
}

- (void)captureNavigationController:(SBLCaptureNavigationController * _Nonnull)controller didFinishCapture:(SBLCaptureResult * _Nonnull)result { 
  
  if ([[result images] count] == 0) {
    NSLog(@"No images returned");
    return;
  }
  
  NSData *firstImageData = [[result images] firstObject];
  
  if (firstImageData) {
    NSString *base64String = [firstImageData base64EncodedStringWithOptions:0];
    [SensibillCaptureBridgeEventEmitter sendBase64Image:base64String];
  } else {
    NSLog(@"Unable to retrieve NSData.");
  }
  
}

@end

@implementation SensibillCaptureBridgeEventEmitter

RCT_EXPORT_MODULE();

//The list of available events
- (NSArray<NSString *> *)supportedEvents {
  return @[@"sendImage"];
}

- (void)startObserving
{
  [[NSNotificationCenter defaultCenter] addObserver:self
                                           selector:@selector(emitEventInternal:)
                                               name:@"event-emitted"
                                             object:nil];
}

- (void)stopObserving
{
  [[NSNotificationCenter defaultCenter] removeObserver:self];
}

- (void)emitEventInternal:(NSNotification *)notification {
  NSArray *eventDetails = [notification.userInfo valueForKey:@"detail"];
  
  [self sendEventWithName:@"sendImage"
                     body:eventDetails];
}

+ (void)sendBase64Image:(NSString *)base64Image {
  NSDictionary *eventDetail = @{@"detail":base64Image};
  [[NSNotificationCenter defaultCenter] postNotificationName:@"event-emitted"
                                                      object:self
                                                    userInfo:eventDetail];
}

@end
