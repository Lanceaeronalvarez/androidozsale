source $(dirname $0)/common_var.txt

flavorName=nzsaleRC
buildTypeAssemble=NzsaleRCRelease
SPACE=" "
defaultCountry="New${SPACE}Zealand"

print_yellow "\n\nSTARTING AUTOMATION"

# Getting Compile SDK Version
print_blue "\n\nCHECKING COMPILE SDK"
expectedCompileSDK="34"
currentCompileSDK=$(./gradlew -q printCompileSdkVersion -PflavorName=$flavorName)
if [ $expectedCompileSDK = "$currentCompileSDK" ]; then
print_green "\nexpected: $expectedCompileSDK and current: $currentCompileSDK is the same\n"
else
print_red "\nexpected: $expectedCompileSDK and current: $currentCompileSDK is not the same\n"
flag_error 1
fi

# Checking min sdk version
print_blue "\n\nCHECKING MIN SDK"
expectedMinSDK="21"
currentMinSDK=$(./gradlew -q printMinSdkVersion -PflavorName=$flavorName)
if [ $expectedMinSDK = "$currentMinSDK" ]; then
print_green "\nexpected: $expectedMinSDK and current: $currentMinSDK is the same\n"
else
print_red "\nexpected: $expectedMinSDK and current: $currentMinSDK is not the same\n"
flag_error 1
fi

# Checking target sdk version
print_blue "\n\nCHECKING TARGET SDK"
expectedTargetSDK="34"
currentTargetSDK=$(./gradlew -q printTargetSdkVersion -PflavorName=$flavorName)
if [ $expectedTargetSDK = "$currentTargetSDK" ]; then
print_green "\nexpected: $expectedTargetSDK and current: $currentTargetSDK is the same\n"
else
print_red "\nexpected: $expectedTargetSDK and current: $currentTargetSDK is not the same\n"
flag_error 1
fi

# Checking Build tools version
print_blue "\n\nCHECKING BUILD TOOLS VERSION"
expectedBuildToolsVersion="33.0.2"
currentBuildToolsVersion=$(./gradlew -q printBuildToolsVersion -PflavorName=$flavorName)
if [ $expectedBuildToolsVersion = "$currentBuildToolsVersion" ]; then
print_green "\nexpected: $expectedBuildToolsVersion and current: $currentBuildToolsVersion is the same\n"
else
print_red "\nexpected: $expectedBuildToolsVersion and current: $currentBuildToolsVersion is not the same\n"
flag_error 1
fi

# Checking Support library
print_blue "\n\nCHECKING SUPPORT LIBRARY"
expectedSupportLibrary="28.0.0"
currentSupportLibrary=$(./gradlew -q printSupportLibrary -PflavorName=$flavorName)
if [ $expectedSupportLibrary = "$currentSupportLibrary" ]; then
print_green "\nexpected: $expectedSupportLibrary and current: $currentSupportLibrary is the same\n"
else
print_red "\nexpected: $expectedSupportLibrary and current: $currentSupportLibrary is not the same\n"
flag_error 1
fi

#Checking Version Name
print_blue "\n\nCHECKING VERSION NAME"
currentVersionName=$(./gradlew -q printVersionName -PflavorName=$flavorName)

if [ $expectedVersionName = "$currentVersionName" ]; then
print_green "\nexpected: $expectedVersionName and current: $currentVersionName is the same\n"
else
print_red "\nexpected: $expectedVersionName and current: $currentVersionName is not the same\n"
flag_error 1
fi

# Checking Version Code
print_blue "\n\nCHECKING VERSION CODE"
currentVersionCode=$(./gradlew -q printVersionCode -PflavorName=$flavorName)

if [ $expectedVersionCode = "$currentVersionCode" ]; then
print_green "\nexpected: $expectedVersionCode and current: $currentVersionCode is the same\n"
else
print_red "\nexpected: $expectedVersionCode and current: $currentVersionCode is not the same\n"
flag_error 1
fi

# Check Application Id
print_blue "\n\nCHECKING APPLICATION ID"
expectedAppId="nz.co.nzsale"
currentAppId=$(./gradlew -q printApplicationId -PflavorName=$flavorName)
if [ $expectedAppId = "$currentAppId" ]; then
print_green "\nexpected: $expectedAppId and current: $currentAppId is the same\n"
else
print_red "\nexpected: $expectedAppId and current: $currentAppId is not the same\n"
flag_error 1
fi

# Checking App name
print_blue "\n\nCHECKING APP NAME"
expectedAppName="NZSALE"
currentAppName=$(./gradlew -q printAppName -PflavorName=$flavorName)
if [[ $expectedAppName = "$currentAppName" ]]; then
print_green "\nexpected: $expectedAppName and current: $currentAppName is the same\n"
else
print_red "\nexpected: $expectedAppName and current: $currentAppName is not the same\n"
flag_error 1
fi

# Checking Facebook app Id
print_blue "\n\nCHECKING FACEBOOK APP ID"
expectedFbAppId=122094841270614
currentFbAppId=$(./gradlew -q printFacebookAppId -PflavorName=$flavorName)
if [[ $currentFbAppId -eq $expectedFbAppId ]]; then
print_green "\nexpected: $expectedFbAppId and current: $currentFbAppId is the same\n"
else
print_red "\nexpected: $expectedFbAppId and current: $currentFbAppId is not the same\n"
flag_error 1
fi

# Checking Facebook app secret
print_blue "\n\nCHECKING FACEBOOK APP SECRET"
expectedFacebookAppSecret="c427c4000b760c98c818350619930764"
currentFacebookAppSecret=$(./gradlew -q printFacebookAppSecret -PflavorName=$flavorName)
if [ $expectedFacebookAppSecret = "$currentFacebookAppSecret" ]; then
print_green "\nexpected: $expectedFacebookAppSecret and current: $currentFacebookAppSecret is the same\n"
else
print_red "\nexpected: $expectedFacebookAppSecret and current: $currentFacebookAppSecret is not the same\n"
flag_error 1
fi

# Checking New Relic app token
print_blue "\n\nCHECKING NEW RELIC APP TOKEN"
expectedNewRelicToken="AAfebece95884f3f5bc6acaacb76d02440c0f7eb85"
currentNewRelicToken=$(./gradlew -q printNewRelicAppToken -PflavorName=$flavorName)
if [ $expectedNewRelicToken = "$currentNewRelicToken" ]; then
print_green "\nexpected: $expectedNewRelicToken and current: $currentNewRelicToken is the same\n"
else
print_red "\nexpected: $expectedNewRelicToken and current: $currentNewRelicToken is not the same\n"
flag_error 1
fi

# Check Build Type
print_blue "\n\nCHECKING BUILD TYPE"
currentBuildType=$(./gradlew -q printDebugMode -PflavorName=$flavorName -PbuildTypeAssemble=$buildTypeAssemble)
print_green "\nIs debug mode: $currentBuildType\n"

#Check Admob app id
print_blue "\n\nCHECKING ADMOB APP ID"
expectedAdmobId="ca-app-pub-4114338578467810~5978596822"
currentAdmobId=$(./gradlew -q parseAdmobAppId -PflavorName=$flavorName)
if [ $expectedAdmobId = "$currentAdmobId" ]; then
print_green "\nexpected: $expectedAdmobId and current: $currentAdmobId is the same\n"
else
print_red "\nexpected: $expectedAdmobId and current: $currentAdmobId is not the same\n"
flag_error 1
fi

# Check Admob banners id
print_blue "\n\nCHECKING ADMOB BANNERS ID"
expectedAdmobBanners="ca-app-pub-4114338578467810/4437627904"
currentAdmobBanners=$(./gradlew -q parseAdmobBanners -PflavorName=$flavorName)
if [ $expectedAdmobBanners = "$currentAdmobBanners" ]; then
print_green "\nexpected: $expectedAdmobBanners and current: $currentAdmobBanners is the same\n"
else
print_red "\nexpected: $expectedAdmobBanners and current: $currentAdmobBanners is not the same\n"
flag_error 1
fi

# Check Admob products id
print_blue "\nCHECKING ADMOB PRODUCTS ID"
expectedAdmobProducts="ca-app-pub-4114338578467810/8413188472"
currentAdmobProducts=$(./gradlew -q parseAdmobProducts -PflavorName=$flavorName)
if [ $expectedAdmobProducts = "$currentAdmobProducts" ]; then
print_green "\nexpected: $expectedAdmobProducts and current: $currentAdmobProducts is the same\n"
else
print_red "\nexpected: $expectedAdmobProducts and current: $currentAdmobProducts is not the same\n"
flag_error 1
fi

# Check Admob account id
print_blue "\nCHECKING ADMOB ACCOUNT ID"
expectedAdmobAccount="ca-app-pub-4114338578467810/1811464561"
currentAdmobAccount=$(./gradlew -q parseAdmobAccount -PflavorName=$flavorName)
if [ $expectedAdmobAccount = "$currentAdmobAccount" ]; then
print_green "\nexpected: $expectedAdmobAccount and current: $currentAdmobAccount is the same\n"
else
print_red "\nexpected: $expectedAdmobAccount and current: $currentAdmobAccount is not the same\n"
flag_error 1
fi

#Start Clean Process
print_green "\n\n\nClean app...\n"
./gradlew clean
flag_error $?

# Install APK on device / emulator
print_blue "installing Release build...\n"
./gradlew installnzsaleRCRelease
flag_error $?
print_blue "\n\n\n Done Installing\n"

#Launch Main Activity
adb shell am start -n "nz.co.nzsale/au.com.dealsdirect.ui.main.MainActivity" -a android.intent.action.MAIN -c android.intent.category.LAUNCHER
print_blue "\n\n\n Launched main activity\n"

#Get Legacy Version after Main Activity launch
print_blue "\nCHECKING LEGACY API VERSION"
expectedLegacyVersion="3.31"
currentLegacyVersion=$(./gradlew -q getLegacyVersion -PflavorName=$flavorName -PbuildTypeAssemble=$buildTypeAssemble)
if [ $expectedLegacyVersion = "$currentLegacyVersion" ]; then
print_green "\nexpected: $expectedLegacyVersion and current: $currentLegacyVersion is the same\n"
else
print_red "\nexpected: $expectedLegacyVersion and current: $currentLegacyVersion is not the same\n"
flag_error 1
fi

#Check country id
print_blue "\nCHECKING COUNTRY ID"
expectedCountryId="NZ"
currentCountryId=$(./gradlew -q printCountryId -PflavorName=$flavorName -PbuildTypeAssemble=$buildTypeAssemble -PcountryName="$defaultCountry")
if [ $expectedCountryId = "$currentCountryId" ]; then
print_green "\nexpected: $expectedCountryId and current: $currentCountryId is the same\n"
else
print_red "\nexpected: $expectedCountryId and current: $currentCountryId is not the same\n"
flag_error 1
fi

#Check account id
print_blue "\nCHECKING ACCOUNT ID"
expectedAccountId="B31F92DA-08DF-40A8-B156-B145A9B209D0"
currentAccountId=$(./gradlew -q printAccountId -PflavorName=$flavorName -PbuildTypeAssemble=$buildTypeAssemble -PcountryName="$defaultCountry")
if [ $expectedAccountId = "$currentAccountId" ]; then
print_green "\nexpected: $expectedAccountId and current: $currentAccountId is the same\n"
else
print_red "\nexpected: $expectedAccountId and current: $currentAccountId is not the same\n"
flag_error 1
fi

#Check genie api root
print_blue "\nCHECKING GENIE API ROOT"
expectedGenieRoot="https://www.nzsale.co.nz/"
currentGenieRoot=$(./gradlew -q printGenieApi -PflavorName=$flavorName -PbuildTypeAssemble=$buildTypeAssemble -PcountryName="$defaultCountry")
if [ $expectedGenieRoot = "$currentGenieRoot" ]; then
print_green "\nexpected: $expectedGenieRoot and current: $currentGenieRoot is the same\n"
else
print_red "\nexpected: $expectedGenieRoot and current: $currentGenieRoot is not the same\n"
flag_error 1
fi

#Check currency code
print_blue "\nCHECKING CURRENCY CODE"
expectedCurrencyCode="NZD"
currenctCurrencyCode=$(./gradlew -q printCurrencyCode -PflavorName=$flavorName -PbuildTypeAssemble=$buildTypeAssemble -PcountryName="$defaultCountry")
if [ $expectedCurrencyCode = "$currenctCurrencyCode" ]; then
print_green "\nexpected: $expectedCurrencyCode and current: $currenctCurrencyCode is the same\n"
else
print_red "\nexpected: $expectedCurrencyCode and current: $currenctCurrencyCode is not the same\n"
flag_error 1
fi

# Check recaptcha
print_blue "\nCHECKING RECAPTCHA KEY"
expectedRecaptchaKey="6LehI6cUAAAAACrjaAGPQLQx1eomvLqrb0S_QxSi"
currentRecaptchaKey=$(./gradlew -q printRecaptcha -PflavorName=$flavorName -PbuildTypeAssemble=$buildTypeAssemble)
if [ $expectedRecaptchaKey = "$currentRecaptchaKey" ]; then
print_green "\nexpected: $expectedRecaptchaKey and current: $currentRecaptchaKey is the same\n"
else
print_red "\nexpected: $expectedRecaptchaKey and current: $currentRecaptchaKey is not the same\n"
flag_error 1
fi

#Copy APK to output folder
cp "$PROJECT_DIR"app/build/outputs/apk/"$flavorName"/release/app-"$flavorName"-release.apk "$OUTPUT_DIR"
flag_error $?
print_blue "\n\n\n Finished Copying APK to output directory\n"

if [ $error -eq 0 ]; then
print_yellow "\n\nFINISHED AUTOMATION\n"
else
print_yellow "\n\nFINISHED AUTOMATION WITH ERROR(S)\n"
fi

exit $error
