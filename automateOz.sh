#Define all paths, constants here
PROJECT_DIR='/Users/nicolluisyumang/Desktop/AndroidDealsDirect/'
OUTPUT_DIR='/Users/nicolluisyumang/Desktop/'

flavorName=ozsaleRC
buildTypeAssemble=OzsaleRCRelease
defaultCountry=Australia
expectedVersionName="4.4.0"
expectedVersionCode="238"

print_green(){
    printf "\e[1;32m$1\e[0m"
}

print_blue(){
    printf "\e[1;34m$1\e[0m"
}

print_red(){
    printf "\e[1;31m$1\e[0m"
}

print_yellow(){
    printf "\e[1;33m$1\e[0m"
}


print_yellow "\n\nSTARTING AUTOMATION"

# Getting Compile SDK Version
print_blue "\n\nCHECKING COMPILE SDK"
expectedCompileSDK="28"
currentCompileSDK=$(./gradlew -q printCompileSdkVersion -PflavorName=$flavorName)
if [ $expectedCompileSDK = "$currentCompileSDK" ]; then
print_green "\nexpected compile sdk $expectedCompileSDK and current compile sdk $currentCompileSDK is the same\n"
else
print_red "\nexpected compile sdk $expectedCompileSDK and current compile sdk $currentCompileSDK is not the same\n"
fi

# Checking min sdk version
print_blue "\n\nCHECKING MIN SDK"
expectedMinSDK="21"
currentMinSDK=$(./gradlew -q printMinSdkVersion -PflavorName=$flavorName)
if [ $expectedMinSDK = "$currentMinSDK" ]; then
print_green "\nexpected min sdk $expectedMinSDK and current min sdk $currentMinSDK is the same\n"
else
print_red "\nexpected min sdk $expectedMinSDK and current min sdk $currentMinSDK is not the same\n"
fi

# Checking target sdk version
print_blue "\n\nCHECKING TARGET SDK"
expectedTargetSDK="28"
currentTargetSDK=$(./gradlew -q printTargetSdkVersion -PflavorName=$flavorName)
if [ $expectedTargetSDK = "$currentTargetSDK" ]; then
print_green "\nexpected target sdk $expectedTargetSDK and current target sdk $currentTargetSDK is the same\n"
else
print_red "\nexpected target sdk $expectedTargetSDK and current target sdk $currentTargetSDK is not the same\n"
fi

# Checking Build tools version
print_blue "\n\nCHECKING BUILD TOOLS VERSION"
expectedBuildToolsVersion="28.0.3"
currentBuildToolsVersion=$(./gradlew -q printBuildToolsVersion -PflavorName=$flavorName)
if [ $expectedBuildToolsVersion = "$currentBuildToolsVersion" ]; then
print_green "\nexpected build tools version $expectedBuildToolsVersion and current build tools version $currentBuildToolsVersion is the same\n"
else
print_red "\nexpected build tools version $expectedBuildToolsVersion and current build tools version $currentBuildToolsVersion is not the same\n"
fi

# Checking Support library
print_blue "\n\nCHECKING SUPPORT LIBRARY"
expectedSupportLibrary="28.0.0"
currentSupportLibrary=$(./gradlew -q printSupportLibrary -PflavorName=$flavorName)
if [ $expectedSupportLibrary = "$currentSupportLibrary" ]; then
print_green "\nexpected support library $expectedSupportLibrary and current support library $currentSupportLibrary is the same\n"
else
print_red "\nexpected support library $expectedSupportLibrary and current support library $currentSupportLibrary is not the same\n"
fi

#Checking Version Name
print_blue "\n\nCHECKING VERSION NAME"
currentVersionName=$(./gradlew -q printVersionName -PflavorName=$flavorName)

if [ $expectedVersionName = "$currentVersionName" ]; then
print_green "\nexpected version name $expectedVersionName and current version name $currentVersionName is the same\n"
else
print_red "\nexpected version name $expectedVersionName and current version name $currentVersionName is not the same\n"
fi

# Checking Version Code
print_blue "\n\nCHECKING VERSION CODE"
currentVersionCode=$(./gradlew -q printVersionCode -PflavorName=$flavorName)

if [ $expectedVersionCode = "$currentVersionCode" ]; then
print_green "\nexpected version code $expectedVersionCode and current version code $currentVersionCode is the same\n"
else
print_red "\nexpected version code $expectedVersionCode and current version code $currentVersionCode is not the same\n"
fi

# Check Application Id
print_blue "\n\nCHECKING APPLICATION ID"
expectedAppId="au.com.ozsale"
currentAppId=$(./gradlew -q printApplicationId -PflavorName=$flavorName)

if [ $expectedAppId = "$currentAppId" ]; then
print_green "\nexpected app id $expectedAppId and current app id $currentAppId is the same\n"
else
print_red "\nexpected app id $expectedAppId and current app id $currentAppId is not the same\n"
fi

# Checking App name
print_blue "\n\nCHECKING APP NAME"
expectedAppName="Ozsale"
currentAppName=$(./gradlew -q printAppName -PflavorName=$flavorName)
if [[ $expectedAppName = "$currentAppName" ]]; then
print_green "\nexpected App name $expectedAppName and current App name $currentAppName is the same\n"
else
print_red "\nexpected App name $expectedAppName and current App name $currentAppName is not the same\n"
fi

# Checking Facebook app Id
print_blue "\n\nCHECKING FACEBOOK APP ID"
expectedFbAppId=421544284547550
currentFbAppId=$(./gradlew -q printFacebookAppId -PflavorName=$flavorName)
if [[ $currentFbAppId -eq $expectedFbAppId ]]; then
print_green "\nexpected FB App id $expectedFbAppId and current FB App id $currentFbAppId is the same\n"
else
print_red "\nexpected FB App id $expectedFbAppId and current FB App id $currentFbAppId is not the same\n"
fi

# Checking Facebook app secret
print_blue "\n\nCHECKING FACEBOOK APP SECRET"
expectedFacebookAppSecret="f08401cb63ac18241e51e2184d8cd80a"
currentFacebookAppSecret=$(./gradlew -q printFacebookAppSecret -PflavorName=$flavorName)

if [ $expectedFacebookAppSecret = "$currentFacebookAppSecret" ]; then
print_green "\nexpected fb app secret $expectedFacebookAppSecret and current fb app secret $currentFacebookAppSecret is the same\n"
else
print_red "\nexpected fb app secret $expectedFacebookAppSecret and current fb app secret $currentFacebookAppSecret is not the same\n"
fi

# Checking New Relic app token
print_blue "\n\nCHECKING NEW RELIC APP TOKEN"
expectedNewRelicToken="AA81128f803ab5bdb52bb2d4f6b19d29f0bb62bca1"
currentNewRelicToken=$(./gradlew -q printNewRelicAppToken -PflavorName=$flavorName)

if [ $expectedNewRelicToken = "$currentNewRelicToken" ]; then
print_green "\nexpected new relic app token $expectedNewRelicToken and current new relic app token $currentNewRelicToken is the same\n"
else
print_red "\nexpected new relic app token $expectedNewRelicToken and current new relic app token $currentNewRelicToken is not the same\n"
fi


# Check Build Type
print_blue "\n\nCHECKING BUILD TYPE"
currentBuildType=$(./gradlew -q printDebugMode -PflavorName=$flavorName -PbuildTypeAssemble=$buildTypeAssemble)
print_green "\nIs debug mode: $currentBuildType\n"

#Check Admob app id
print_blue "\n\nCHECKING ADMOB APP ID"
expectedAdmobId="ca-app-pub-4114338578467810~8730982743"
currentAdmobId=$(./gradlew -q parseAdmobAppId -PflavorName=$flavorName)
if [ $expectedAdmobId = "$currentAdmobId" ]; then
print_green "\nexpected: $expectedAdmobId and current: $currentAdmobId is the same\n"
else
print_red "\nexpected: $expectedAdmobId and current: $currentAdmobId is not the same\n"
fi

# Check Admob banners id
print_blue "\n\nCHECKING ADMOB BANNERS ID"
expectedAdmobBanners="ca-app-pub-4114338578467810/2489943804"
currentAdmobBanners=$(./gradlew -q parseAdmobBanners -PflavorName=$flavorName)
if [ $expectedAdmobBanners = "$currentAdmobBanners" ]; then
print_green "\nexpected: $expectedAdmobBanners and current: $currentAdmobBanners is the same\n"
else
print_red "\nexpected: $expectedAdmobBanners and current: $currentAdmobBanners is not the same\n"
fi

# Check Admob products id
print_blue "\nCHECKING ADMOB PRODUCTS ID"
expectedAdmobProducts="ca-app-pub-4114338578467810/1384089159"
currentAdmobProducts=$(./gradlew -q parseAdmobProducts -PflavorName=$flavorName)
if [ $expectedAdmobProducts = "$currentAdmobProducts" ]; then
print_green "\nexpected: $expectedAdmobProducts and current: $currentAdmobProducts is the same\n"
else
print_red "\nexpected: $expectedAdmobProducts and current: $currentAdmobProducts is not the same\n"
fi

# Check Admob account id
print_blue "\nCHECKING ADMOB ACCOUNT ID"
expectedAdmobAccount="ca-app-pub-4114338578467810/8879435793"
currentAdmobAccount=$(./gradlew -q parseAdmobAccount -PflavorName=$flavorName)
if [ $expectedAdmobAccount = "$currentAdmobAccount" ]; then
print_green "\nexpected: $expectedAdmobAccount and current: $currentAdmobAccount is the same\n"
else
print_red "\nexpected: $expectedAdmobAccount and current: $currentAdmobAccount is not the same\n"
fi

#Start Clean Process
print_green "\n\n\nClean app...\n"
./gradlew clean

# Install APK on device / emulator
print_blue "installing Release build...\n"
./gradlew installozsaleRCRelease
print_blue "\n\n\n Done Installing\n"

#Launch Main Activity
adb shell am start -n "au.com.ozsale/au.com.dealsdirect.ui.main.MainActivity" -a android.intent.action.MAIN -c android.intent.category.LAUNCHER
print_blue "\n\n\n Launched main activity\n"

#Get Legacy Version after Main Activity launch
print_blue "\nCHECKING LEGACY API VERSION"
expectedLegacyVersion="3.29"
currentLegacyVersion=$(./gradlew -q getLegacyVersion -PflavorName=$flavorName -PbuildTypeAssemble=$buildTypeAssemble)
if [ $expectedLegacyVersion = "$currentLegacyVersion" ]; then
print_green "\nexpected: $expectedLegacyVersion and current: $currentLegacyVersion is the same\n"
else
print_red "\nexpected: $expectedLegacyVersion and current: $currentLegacyVersion is not the same\n"
fi

#Check country id
print_blue "\nCHECKING COUNTRY ID"
expectedCountryId="AS"
currentCountryId=$(./gradlew -q printCountryId -PflavorName=$flavorName -PbuildTypeAssemble=$buildTypeAssemble -PcountryName=$defaultCountry)
if [ $expectedCountryId = "$currentCountryId" ]; then
print_green "\nexpected: $expectedCountryId and current: $currentCountryId is the same\n"
else
print_red "\nexpected: $expectedCountryId and current: $currentCountryId is not the same\n"
fi

#Check account id
print_blue "\nCHECKING ACCOUNT ID"
expectedAccountId="A816D792-E940-44F0-B752-06DE1ED5C2B9"
currentAccountId=$(./gradlew -q printAccountId -PflavorName=$flavorName -PbuildTypeAssemble=$buildTypeAssemble -PcountryName=$defaultCountry)
if [ $expectedAccountId = "$currentAccountId" ]; then
print_green "\nexpected: $expectedAccountId and current: $currentAccountId is the same\n"
else
print_red "\nexpected: $expectedAccountId and current: $currentAccountId is not the same\n"
fi

#Check genie api root
print_blue "\nCHECKING GENIE API ROOT"
expectedGenieRoot="https://www.ozsale.com.au/"
currentGenieRoot=$(./gradlew -q printGenieApi -PflavorName=$flavorName -PbuildTypeAssemble=$buildTypeAssemble -PcountryName=$defaultCountry)
if [ $expectedGenieRoot = "$currentGenieRoot" ]; then
print_green "\nexpected: $expectedGenieRoot and current: $currentGenieRoot is the same\n"
else
print_red "\nexpected: $expectedGenieRoot and current: $currentGenieRoot is not the same\n"
fi

#Check currency code
print_blue "\nCHECKING CURRENCY CODE"
expectedCurrencyCode="AUD"
currenctCurrencyCode=$(./gradlew -q printCurrencyCode -PflavorName=$flavorName -PbuildTypeAssemble=$buildTypeAssemble -PcountryName=$defaultCountry)
if [ $expectedCurrencyCode = "$currenctCurrencyCode" ]; then
print_green "\nexpected: $expectedCurrencyCode and current: $currenctCurrencyCode is the same\n"
else
print_red "\nexpected: $expectedCurrencyCode and current: $currenctCurrencyCode is not the same\n"
fi

# Check recaptcha
print_blue "\nCHECKING RECAPTCHA KEY"
expectedRecaptchaKey="6LehI6cUAAAAACrjaAGPQLQx1eomvLqrb0S_QxSi"
currentRecaptchaKey=$(./gradlew -q printRecaptcha -PflavorName=$flavorName -PbuildTypeAssemble=$buildTypeAssemble)
if [ $expectedRecaptchaKey = "$currentRecaptchaKey" ]; then
print_green "\nexpected: $expectedRecaptchaKey and current: $currentRecaptchaKey is the same\n"
else
print_red "\nexpected: $expectedRecaptchaKey and current: $currentRecaptchaKey is not the same\n"
fi

#Copy APK to output folder
cp "$PROJECT_DIR"app/build/outputs/apk/"$flavorName"/release/app-"$flavorName"-release.apk $OUTPUT_DIR
print_blue "\n\n\n Finished Copying APK to output directory\n"

print_yellow "\n\nFINISHED AUTOMATION\n"



