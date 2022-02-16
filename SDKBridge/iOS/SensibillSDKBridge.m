#import "SensibillSDKBridge.h"

@import Sensibill;

@interface SensibillSDKBridge() <SBLCaptureFlowCoordinatorDelegate, SensibillUICoordinatorDelegate, SBLTokenProvider>

@property (nonatomic, strong) SBLCaptureFlowCoordinator *captureFlowCoordinator;

@property (nonatomic, strong) SensibillUICoordinator *uiCoordinator;

@end

@implementation SensibillSDKBridge

RCT_EXPORT_MODULE();

RCT_EXPORT_METHOD(log:(NSString *)data)
{
  NSLog(@"WE ARE LOGGING SOMETHING %@", data);
}

RCT_EXPORT_METHOD(launchReceiptCapture)
{
  NSLog(@"Native: SDK cancelled receipt capture.");
  
  dispatch_async(dispatch_get_main_queue(), ^{
    
    UIViewController *host = [UIApplication sharedApplication].delegate.window.rootViewController;
    
    if (host) {
      SBLCaptureFlowCoordinator *coordinator = [[SBLCaptureFlowCoordinator alloc] initWithHost: host];
      coordinator.delegate = self;
      [coordinator start];
      self.captureFlowCoordinator = coordinator;
    }
    
  });
}

#pragma mark - SBLCaptureFlowCoordinatorDelegate

- (void)coordinatorDidCancelCapture:(SBLCaptureFlowCoordinator *)coordinator {
  self.captureFlowCoordinator = nil;
  NSLog(@"Native: SDK cancelled receipt capture.");
}

- (void)coordinatorDidFinishCapture:(SBLCaptureFlowCoordinator * _Nonnull)coordinator transactions:(NSArray<SBLTransaction *> * _Nonnull)transactions { 
  self.captureFlowCoordinator = nil;
  NSLog(@"Native: SDK finished receipt capture.");
}

RCT_EXPORT_METHOD(startSDK)
{
  NSLog(@"Native: Requesting to start SDK.");
  
  [[SBLSDKConfiguration shared] setCertificatePinningEnabled:NO];
  NSLog(@"Native: Certificate Pinning is OFF.");
  
  NSString *userId = @"userId"; // Provide unique user identifier here.
  NSLog(@"Native: Using cached identifier : %@", userId);
  
  [[SensibillSDK shared] startWithTokenProvider:self
                                cacheIdentifier:userId
                                     completion:^(enum SensibillError error) {
    
    if (error == SensibillErrorNone) {
      NSLog(@"Native: SDK started successfully.");
    } if (error == SensibillErrorSdkMissingConfigurationFile) {
      NSLog(@"Native: SDK reported missing configuration file Sensibill.plist.");
    } else {
      NSLog(@"Native: SDK reported error code: %ld", error);
    }
    
  }];
}

RCT_EXPORT_METHOD(stopSDK)
{
  NSLog(@"Native: Requesting to stop SDK.");
  NSLog(@"Earlier provided access token would be invalidated on sensibill backend.");
  
  [[SensibillSDK shared] stopWithCompletion:^(enum SensibillError error) {
    
    if (error == SensibillErrorNone) {
      NSLog(@"Native: SDK stopped successfully.");
    } else {
      NSLog(@"Native: SDK stopped but reported error code: %ld", error);
    }
    
  }];
}

RCT_EXPORT_METHOD(launchSpendManager:(NSString*)brandJSON)
{
  NSLog(@"Native: SDK will launch spend manager ui.");
  NSLog(@"Native: Make sure required access token is provided.");
  
  // Parse Brand JSON String to Dictionary to retrieve Colors and Fonts.
  
  // Set individual colors retrieved from brandJSON
  // SBLSDKConfiguration.shared.colors.primary =
  
  // Set individual font retrieved from brandJSON
  // SBLSDKConfiguration.shared.fonts.regularFont =
  
  dispatch_async(dispatch_get_main_queue(), ^{
    
    UIViewController *host = [UIApplication sharedApplication].delegate.window.rootViewController;
    
    SensibillUICoordinatorStartOptionsBuilder *builder = [[SensibillUICoordinatorStartOptionsBuilder alloc] init];
    SensibillUICoordinatorStartOptions *startOptions = [[builder addWithAnimated:YES] build];
    
    if (host) {
      SensibillUICoordinator *coordinator = [[SensibillUICoordinator alloc] initWithHost:host];
      coordinator.delegate = self;
      [coordinator startWithOptions: startOptions];
      self.uiCoordinator = coordinator;
    }
    
  });
}

#pragma mark - SensibillUICoordinatorDelegate

- (void)coordinatorWillTerminateWebUI:(SensibillUICoordinator *)coordinator {
  self.uiCoordinator = nil;
  NSLog(@"Native: SDK will terminate spend manager ui.");
}

#pragma mark - SBLTokenProvider

- (void)provideTokenReplacementWithCompletion:(void (^ _Nonnull)(SBLCredentials * _Nullable, NSError * _Nullable))completion {
  
  NSLog(@"Native: Host application is providing access token.");
  
  /* --- Following line should be replaced with procedure to obtain access token.
    Note that token needs to be provided for the configured environment specified via Sensibill.plist which is added to application target. --- */
  NSString *accessToken = @""; // TODO: Place your access token here.
  SBLCredentials *credentials = [[SBLCredentials alloc] initWithAccessToken:accessToken refreshToken:nil];
  
  completion(credentials, nil);
}

@end
