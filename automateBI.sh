PROJECT_DIR='/Users/nicolluisyumang/Desktop/AndroidDealsDirect/'
OUTPUT_DIR='/Users/nicolluisyumang/Desktop/'

flavorName=buyinviteRC
buildTypeAssemble=BuyinviteRCRelease
resourceLocationFolder=buyinviteRC
australia=Australia
SPACE=" "
nz="New${SPACE}Zealand"
expectedVersionName="5.4.3"
expectedVersionCode="396"

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

#Start Clean Process
print_green "\n\n\nClean app...\n"
./gradlew clean

# Install APK on device / emulator
print_blue "installing Release build...\n"
./gradlew installbuyinviteRCRelease
print_blue "\n\n\n Done Installing\n"

#Launch Main Activity
adb shell am start -n "au.com.buyinvite.bi/au.com.dealsdirect.ui.main.MainActivity" -a android.intent.action.MAIN -c android.intent.category.LAUNCHER
print_blue "\n\n\n Launched main activity\n"

# Getting Compile SDK Version
print_blue "\n\nCHECKING COMPILE SDK"
expectedCompileSDK="30"
currentCompileSDK=$(./gradlew -q printCompileSdkVersion -PflavorName=$flavorName)
if [ $expectedCompileSDK = "$currentCompileSDK" ]; then
print_green "\nexpected: $expectedCompileSDK and current: $currentCompileSDK is the same\n"
else
print_red "\nexpected: $expectedCompileSDK and current: $currentCompileSDK is not the same\n"
fi

# Checking min sdk version
print_blue "\n\nCHECKING MIN SDK"
expectedMinSDK="21"
currentMinSDK=$(./gradlew -q printMinSdkVersion -PflavorName=$flavorName)
if [ $expectedMinSDK = "$currentMinSDK" ]; then
print_green "\nexpected: $expectedMinSDK and current: $currentMinSDK is the same\n"
else
print_red "\nexpected: $expectedMinSDK and current: $currentMinSDK is not the same\n"
fi

# Checking target sdk version
print_blue "\n\nCHECKING TARGET SDK"
expectedTargetSDK="30"
currentTargetSDK=$(./gradlew -q printTargetSdkVersion -PflavorName=$flavorName)
if [ $expectedTargetSDK = "$currentTargetSDK" ]; then
print_green "\nexpected: $expectedTargetSDK and current: $currentTargetSDK is the same\n"
else
print_red "\nexpected: $expectedTargetSDK and current: $currentTargetSDK is not the same\n"
fi

# Checking Build tools version
print_blue "\n\nCHECKING BUILD TOOLS VERSION"
expectedBuildToolsVersion="30.0.2"
currentBuildToolsVersion=$(./gradlew -q printBuildToolsVersion -PflavorName=$flavorName)
if [ $expectedBuildToolsVersion = "$currentBuildToolsVersion" ]; then
print_green "\nexpected: $expectedBuildToolsVersion and current: $currentBuildToolsVersion is the same\n"
else
print_red "\nexpected: $expectedBuildToolsVersion and current: $currentBuildToolsVersion is not the same\n"
fi

# Checking Support library
print_blue "\n\nCHECKING SUPPORT LIBRARY"
expectedSupportLibrary="28.0.0"
currentSupportLibrary=$(./gradlew -q printSupportLibrary -PflavorName=$flavorName)
if [ $expectedSupportLibrary = "$currentSupportLibrary" ]; then
print_green "\nexpected: $expectedSupportLibrary and current: $currentSupportLibrary is the same\n"
else
print_red "\nexpected: $expectedSupportLibrary and current: $currentSupportLibrary is not the same\n"
fi

#Checking Version Name
print_blue "\n\nCHECKING VERSION NAME"
currentVersionName=$(./gradlew -q printVersionName -PflavorName=$flavorName)
if [ $expectedVersionName = "$currentVersionName" ]; then
print_green "\nexpected: $expectedVersionName and current: $currentVersionName is the same\n"
else
print_red "\nexpected: $expectedVersionName and current: $currentVersionName is not the same\n"
fi

# Checking Version Code
print_blue "\n\nCHECKING VERSION CODE"
currentVersionCode=$(./gradlew -q printVersionCode -PflavorName=$flavorName)
if [ $expectedVersionCode = "$currentVersionCode" ]; then
print_green "\nexpected: $expectedVersionCode and current: $currentVersionCode is the same\n"
else
print_red "\nexpected: $expectedVersionCode and current: $currentVersionCode is not the same\n"
fi

# Check Application Id
print_blue "\n\nCHECKING APPLICATION ID"
expectedAppId="au.com.buyinvite.bi"
currentAppId=$(./gradlew -q printApplicationId -PflavorName=$flavorName)
if [ $expectedAppId = "$currentAppId" ]; then
print_green "\nexpected: $expectedAppId and current: $currentAppId is the same\n"
else
print_red "\nexpected: $expectedAppId and current: $currentAppId is not the same\n"
fi

# Checking App name
print_blue "\n\nCHECKING APP NAME"
expectedAppName="buyinvite"
currentAppName=$(./gradlew -q printAppName -PflavorName=$flavorName)
if [[ $expectedAppName = "$currentAppName" ]]; then
print_green "\nexpected: $expectedAppName and current: $currentAppName is the same\n"
else
print_red "\nexpected: $expectedAppName and current: $currentAppName is not the same\n"
fi

# Checking Facebook app Id
print_blue "\n\nCHECKING FACEBOOK APP ID"
expectedFbAppId=391019637656000
currentFbAppId=$(./gradlew -q printFacebookAppId -PflavorName=$flavorName)
if [[ $currentFbAppId -eq $expectedFbAppId ]]; then
print_green "\nexpected: $expectedFbAppId and current: $currentFbAppId is the same\n"
else
print_red "\nexpected: $expectedFbAppId and current: $currentFbAppId is not the same\n"
fi

# Checking Facebook app secret
print_blue "\n\nCHECKING FACEBOOK APP SECRET"
expectedFacebookAppSecret="1dea9436118690813bcc315ecfc28705"
currentFacebookAppSecret=$(./gradlew -q printFacebookAppSecret -PflavorName=$flavorName)
if [ $expectedFacebookAppSecret = "$currentFacebookAppSecret" ]; then
print_green "\nexpected: $expectedFacebookAppSecret and current: $currentFacebookAppSecret is the same\n"
else
print_red "\nexpected: $expectedFacebookAppSecret and current: $currentFacebookAppSecret is not the same\n"
fi

# Checking New Relic app token
print_blue "\n\nCHECKING NEW RELIC APP TOKEN"
expectedNewRelicToken="AAf4ce8e1466bf9d9b7c3dcfdf7c569defebda9edc"
currentNewRelicToken=$(./gradlew -q printNewRelicAppToken -PflavorName=$flavorName)
if [ $expectedNewRelicToken = "$currentNewRelicToken" ]; then
print_green "\nexpected: $expectedNewRelicToken and current: $currentNewRelicToken is the same\n"
else
print_red "\nexpected: $expectedNewRelicToken and current: $currentNewRelicToken is not the same\n"
fi

# Check Build Type
print_blue "\n\nCHECKING BUILD TYPE"
currentBuildType=$(./gradlew -q printDebugMode -PflavorName=$flavorName -PbuildTypeAssemble=$buildTypeAssemble)
print_green "\nIs debug mode: $currentBuildType\n"

#Check Admob app id
print_blue "\n\nCHECKING ADMOB APP ID"
expectedAdmobId="ca-app-pub-4114338578467810~7913838480"
currentAdmobId=$(./gradlew -q parseAdmobAppId -PflavorName=$resourceLocationFolder)
if [ $expectedAdmobId = "$currentAdmobId" ]; then
print_green "\nexpected: $expectedAdmobId and current: $currentAdmobId is the same\n"
else
print_red "\nexpected: $expectedAdmobId and current: $currentAdmobId is not the same\n"
fi

# Check Admob banners id
print_blue "\n\nCHECKING ADMOB BANNERS ID"
expectedAdmobBanners="ca-app-pub-4114338578467810/2519354652"
currentAdmobBanners=$(./gradlew -q parseAdmobBanners -PflavorName=$resourceLocationFolder)
if [ $expectedAdmobBanners = "$currentAdmobBanners" ]; then
print_green "\nexpected: $expectedAdmobBanners and current: $currentAdmobBanners is the same\n"
else
print_red "\nexpected: $expectedAdmobBanners and current: $currentAdmobBanners is not the same\n"
fi

# Check Admob products id
print_blue "\nCHECKING ADMOB PRODUCTS ID"
expectedAdmobProducts="ca-app-pub-4114338578467810/7987303752"
currentAdmobProducts=$(./gradlew -q parseAdmobProducts -PflavorName=$resourceLocationFolder)
if [ $expectedAdmobProducts = "$currentAdmobProducts" ]; then
print_green "\nexpected: $expectedAdmobProducts and current: $currentAdmobProducts is the same\n"
else
print_red "\nexpected: $expectedAdmobProducts and current: $currentAdmobProducts is not the same\n"
fi

# Check Admob account id
print_blue "\nCHECKING ADMOB ACCOUNT ID"
expectedAdmobAccount="ca-app-pub-4114338578467810/5361140411"
currentAdmobAccount=$(./gradlew -q parseAdmobAccount -PflavorName=$resourceLocationFolder)
if [ $expectedAdmobAccount = "$currentAdmobAccount" ]; then
print_green "\nexpected: $expectedAdmobAccount and current: $currentAdmobAccount is the same\n"
else
print_red "\nexpected: $expectedAdmobAccount and current: $currentAdmobAccount is not the same\n"
fi

#Get Legacy Version after Main Activity launch
print_blue "\nCHECKING LEGACY API VERSION"
expectedLegacyVersion="3.31"
currentLegacyVersion=$(./gradlew -q getLegacyVersion -PflavorName=$flavorName -PbuildTypeAssemble=$buildTypeAssemble)
if [ $expectedLegacyVersion = "$currentLegacyVersion" ]; then
print_green "\nexpected: $expectedLegacyVersion and current: $currentLegacyVersion is the same\n"
else
print_red "\nexpected: $expectedLegacyVersion and current: $currentLegacyVersion is not the same\n"
fi

#Check country id for au
print_blue "\nCHECKING AU COUNTRY ID"
expectedAuCountryId="BA"
currentAuCountryId=$(./gradlew -q printCountryId -PflavorName=$flavorName -PbuildTypeAssemble=$buildTypeAssemble -PcountryName=$australia)
if [ $expectedAuCountryId = "$currentAuCountryId" ]; then
print_green "\nexpected: $expectedAuCountryId and current: $currentAuCountryId is the same\n"
else
print_red "\nexpected: $expectedAuCountryId and current: $currentAuCountryId is not the same\n"
fi

#Check au account id
print_blue "\nCHECKING AU ACCOUNT ID"
expectedAuAccountId="FD6E7F98-F8B4-49D9-8FEF-D1AA02BCB43A"
currentAuAccountId=$(./gradlew -q printAccountId -PflavorName=$flavorName -PbuildTypeAssemble=$buildTypeAssemble -PcountryName=$australia)
if [ $expectedAuAccountId = "$currentAuAccountId" ]; then
print_green "\nexpected: $expectedAuAccountId and current: $currentAuAccountId is the same\n"
else
print_red "\nexpected: $expectedAuAccountId and current: $currentAuAccountId is not the same\n"
fi

#Check au genie api root
print_blue "\nCHECKING AU GENIE API ROOT"
expectedAuGenieRoot="https://www.buyinvite.com.au/"
currentAuGenieRoot=$(./gradlew -q printGenieApi -PflavorName=$flavorName -PbuildTypeAssemble=$buildTypeAssemble -PcountryName=$australia)
if [ $expectedAuGenieRoot = "$currentAuGenieRoot" ]; then
print_green "\nexpected: $expectedAuGenieRoot and current: $currentAuGenieRoot is the same\n"
else
print_red "\nexpected: $expectedAuGenieRoot and current: $currentAuGenieRoot is not the same\n"
fi

#Check au currency code
print_blue "\nCHECKING AU CURRENCY CODE"
expectedAuCurrencyCode="AUD"
currenctAuCurrencyCode=$(./gradlew -q printCurrencyCode -PflavorName=$flavorName -PbuildTypeAssemble=$buildTypeAssemble -PcountryName=$australia)
if [ $expectedAuCurrencyCode = "$currenctAuCurrencyCode" ]; then
print_green "\nexpected: $expectedAuCurrencyCode and current: $currenctAuCurrencyCode is the same\n"
else
print_red "\nexpected: $expectedAuCurrencyCode and current: $currenctAuCurrencyCode is not the same\n"
fi

#Check country id for nz
print_blue "\nCHECKING NZ COUNTRY ID"
expectedNzCountryId="BN"
currentNzCountryId=$(./gradlew -q printCountryId -PflavorName=$flavorName -PbuildTypeAssemble=$buildTypeAssemble -PcountryName="$nz")
if [ $expectedNzCountryId = "$currentNzCountryId" ]; then
print_green "\nexpected: $expectedNzCountryId and current: $currentNzCountryId is the same\n"
else
print_red "\nexpected: $expectedNzCountryId and current: $currentNzCountryId is not the same\n"
fi

#Check nz account id
print_blue "\nCHECKING NZ ACCOUNT ID"
expectedNzAccountId="461308FD-59C6-4BE1-A3D0-FCA74BCD338F"
currentNzAccountId=$(./gradlew -q printAccountId -PflavorName=$flavorName -PbuildTypeAssemble=$buildTypeAssemble -PcountryName="$nz")
if [ $expectedNzAccountId = "$currentNzAccountId" ]; then
print_green "\nexpected: $expectedNzAccountId and current: $currentNzAccountId is the same\n"
else
print_red "\nexpected: $expectedNzAccountId and current: $currentNzAccountId is not the same\n"
fi

#Check nz genie api root
print_blue "\nCHECKING NZ GENIE API ROOT"
expectedNzGenieRoot="https://www.buyinvite.co.nz/"
currentNzGenieRoot=$(./gradlew -q printGenieApi -PflavorName=$flavorName -PbuildTypeAssemble=$buildTypeAssemble -PcountryName="$nz")
if [ $expectedNzGenieRoot = "$currentNzGenieRoot" ]; then
print_green "\nexpected: $expectedNzGenieRoot and current: $currentNzGenieRoot is the same\n"
else
print_red "\nexpected: $expectedNzGenieRoot and current: $currentNzGenieRoot is not the same\n"
fi

#Check nz currency code
print_blue "\nCHECKING NZ CURRENCY CODE"
expectedNzCurrencyCode="NZD"
currenctNzCurrencyCode=$(./gradlew -q printCurrencyCode -PflavorName=$flavorName -PbuildTypeAssemble=$buildTypeAssemble -PcountryName="$nz")
if [ $expectedNzCurrencyCode = "$currenctNzCurrencyCode" ]; then
print_green "\nexpected: $expectedNzCurrencyCode and current: $currenctNzCurrencyCode is the same\n"
else
print_red "\nexpected: $expectedNzCurrencyCode and current: $currenctNzCurrencyCode is not the same\n"
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