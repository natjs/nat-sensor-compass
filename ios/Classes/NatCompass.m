//
//  NatCompass.m
//
//  Created by huangyake on 17/1/7.
//  Copyright © 2017 Instapp. All rights reserved.
//

#import "NatCompass.h"


@interface NatCompass ()<CLLocationManagerDelegate>
@property (nonatomic, strong) CLLocationManager *manager;
@property (nonatomic, strong) CMMotionManager *motionManager;
@property (nonatomic, strong)NatCallback callback;
@property (nonatomic, strong)NSDate *lastDate;
@property (nonatomic, assign)NSInteger interval;
@property (nonatomic, assign)BOOL isget;
@end

@implementation NatCompass
+ (id)singletonManger{
    static id manager = nil;
    static dispatch_once_t onceToken;
    dispatch_once(&onceToken, ^{
        manager = [[self alloc] init];
    });
    return manager;
}
- (void)get:(NatCallback)back{
    self.callback = back;
    self.isget = YES;
    _manager = [[CLLocationManager alloc]init];
    _manager.delegate = self;
    
    if ([CLLocationManager headingAvailable]) {
        _manager.headingFilter = 5;
        [_manager startUpdatingHeading];
    }else{
        back(@{@"error":@{@"code":@1,@"msg":@"COMPASS_NOT_SUPPORTED"}},nil);
    }
}

- (void)watch:(NSDictionary *)options :(NatCallback)back{
    self.isget = NO;
    self.callback = back;
    _manager = [[CLLocationManager alloc]init];
    _manager.delegate = self;
    if (options) {
        if (options[@"interval"] && [options[@"interval"] isKindOfClass:[NSNumber class]] && [options[@"interval"] integerValue]) {
            self.interval  = [options[@"interval"] integerValue];
        }
    }
    
    if ([CLLocationManager headingAvailable]) {
        _manager.headingFilter = 5;
        [_manager startUpdatingHeading];
    }else{
        back(@{@"error":@{@"code":@1,@"msg":@"COMPASS_NOT_SUPPORTED"}},nil);
    }

}

- (void)clearWatch:(NatCallback)back{
    if (_manager) {
        [_manager stopUpdatingHeading];
        _manager = nil;
    }
}
#pragma mark - CLLocationManagerDelegate
- (void)locationManager:(CLLocationManager *)manager didUpdateHeading:(CLHeading *)newHeading
{
    if (newHeading.headingAccuracy < 0){
        self.callback(@{@"error":@{@"code":@1,@"msg":@"COMPASS_INTERNAL_ERROR"}},nil);
        return;
    }
    
    CLLocationDirection  theHeading = ((newHeading.trueHeading > 0) ?
                                       newHeading.trueHeading : newHeading.magneticHeading);
    if (self.isget) {
        self.callback(nil,@{@"heading":@(theHeading)});
        [_manager stopUpdatingHeading];
        _manager = nil;
    }
    
    if (self.interval) {
        if (self.lastDate && [self.lastDate timeIntervalSince1970]*1000.0 + self.interval > [[NSDate date] timeIntervalSince1970] * 1000.0) {
            
        }else{
            self.lastDate = [NSDate date];
            self.callback(nil,@{@"heading":@(theHeading)});
        }
    }else{
        self.callback(nil,@{@"heading":@(theHeading)});
    }
    
    
    
    
    
}
- (void)close{
    if (_manager) {
        [_manager stopUpdatingHeading];
        _manager = nil;
    }
}
@end
