//
//  NatCompass.h
//
//  Created by huangyake on 17/1/7.
//  Copyright © 2017 Instapp. All rights reserved.
//

#import <Foundation/Foundation.h>
#import <CoreLocation/CoreLocation.h>
#import <CoreLocation/CLLocationManager.h>
#import <CoreLocation/CLLocationManagerDelegate.h>
#import <CoreLocation/CLHeading.h>
#import <CoreMotion/CoreMotion.h>

@interface NatCompass : NSObject
typedef void (^NatCallback)(id error,id result);
+ (id)singletonManger;
- (void)get:(NatCallback)back;
- (void)watch:(NSDictionary *)options :(NatCallback)back;
- (void)clearWatch:(NatCallback)back;
- (void)close;
@end
