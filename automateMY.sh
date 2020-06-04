PROJECT_DIR='/users/admin/documents/project/AndroidDD/'
OUTPUT_DIR='/users/admin/desktop/'

flavorName=mysaleRC
buildTypeAssemble=MysaleRCRelease
expectedVersionName="4.3.0"
expectedVersionCode="231"
SPACE=" "
philippines="Philippines"
thailand="Thailand"
malaysia="Malaysia"
uk="United${SPACE}Kingdom"
hongkong="HongKong"

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
expectedTargetSDK="28"
currentTargetSDK=$(./gradlew -q printTargetSdkVersion -PflavorName=$flavorName)
if [ $expectedTargetSDK = "$currentTargetSDK" ]; then
print_green "\nexpected: $expectedTargetSDK and current: $currentTargetSDK is the same\n"
else
print_red "\nexpected: $expectedTargetSDK and current: $currentTargetSDK is not the same\n"
fi

# Checking Build tools version
print_blue "\n\nCHECKING BUILD TOOLS VERSION"
expectedBuildToolsVersion="28.0.3"
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
expectedAppId="my.mysale.mysale"
currentAppId=$(./gradlew -q printApplicationId -PflavorName=$flavorName)
if [ $expectedAppId = "$currentAppId" ]; then
print_green "\nexpected: $expectedAppId and current: $currentAppId is the same\n"
else
print_red "\nexpected: $expectedAppId and current: $currentAppId is not the same\n"
fi

# Checking App name
print_blue "\n\nCHECKING APP NAME"
expectedAppName="Mysale"
currentAppName=$(./gradlew -q printAppName -PflavorName=$flavorName)
if [[ $expectedAppName = "$currentAppName" ]]; then
print_green "\nexpected: $expectedAppName and current: $currentAppName is the same\n"
else
print_red "\nexpected: $expectedAppName and current: $currentAppName is not the same\n"
fi

# Checking Facebook app Id
print_blue "\n\nCHECKING FACEBOOK APP ID"
expectedFbAppId=485479254797079
currentFbAppId=$(./gradlew -q printFacebookAppId -PflavorName=$flavorName)
if [[ $currentFbAppId -eq $expectedFbAppId ]]; then
print_green "\nexpected: $expectedFbAppId and current: $currentFbAppId is the same\n"
else
print_red "\nexpected: $expectedFbAppId and current: $currentFbAppId is not the same\n"
fi

# Checking Facebook app secret
print_blue "\n\nCHECKING FACEBOOK APP SECRET"
expectedFacebookAppSecret="fee7b274d99b23eca0c283d48b2f453a"
currentFacebookAppSecret=$(./gradlew -q printFacebookAppSecret -PflavorName=$flavorName)
if [ $expectedFacebookAppSecret = "$currentFacebookAppSecret" ]; then
print_green "\nexpected: $expectedFacebookAppSecret and current: $currentFacebookAppSecret is the same\n"
else
print_red "\nexpected: $expectedFacebookAppSecret and current: $currentFacebookAppSecret is not the same\n"
fi

# Checking New Relic app token
print_blue "\n\nCHECKING NEW RELIC APP TOKEN"
expectedNewRelicToken="AAfb90a146f4b17ba00df83492608b2d7057e99e16"
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
expectedAdmobId="ca-app-pub-4114338578467810~5930495366"
currentAdmobId=$(./gradlew -q parseAdmobAppId -PflavorName=$flavorName)
if [ $expectedAdmobId = "$currentAdmobId" ]; then
print_green "\nexpected: $expectedAdmobId and current: $currentAdmobId is the same\n"
else
print_red "\nexpected: $expectedAdmobId and current: $currentAdmobId is not the same\n"
fi

# Check Admob banners id
print_blue "\n\nCHECKING ADMOB BANNERS ID"
expectedAdmobBanners="ca-app-pub-4114338578467810/4517518957"
currentAdmobBanners=$(./gradlew -q parseAdmobBanners -PflavorName=$flavorName)
if [ $expectedAdmobBanners = "$currentAdmobBanners" ]; then
print_green "\nexpected: $expectedAdmobBanners and current: $currentAdmobBanners is the same\n"
else
print_red "\nexpected: $expectedAdmobBanners and current: $currentAdmobBanners is not the same\n"
fi

# Check Admob products id
print_blue "\nCHECKING ADMOB PRODUCTS ID"
expectedAdmobProducts="ca-app-pub-4114338578467810/3707656707"
currentAdmobProducts=$(./gradlew -q parseAdmobProducts -PflavorName=$flavorName)
if [ $expectedAdmobProducts = "$currentAdmobProducts" ]; then
print_green "\nexpected: $expectedAdmobProducts and current: $currentAdmobProducts is the same\n"
else
print_red "\nexpected: $expectedAdmobProducts and current: $currentAdmobProducts is not the same\n"
fi

# Check Admob account id
print_blue "\nCHECKING ADMOB ACCOUNT ID"
expectedAdmobAccount="ca-app-pub-4114338578467810/3012865599"
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
./gradlew installmysaleRCRelease
print_blue "\n\n\n Done Installing\n"

#Launch Main Activity
adb shell am start -n "my.mysale.mysale/au.com.dealsdirect.ui.main.MainActivity" -a android.intent.action.MAIN -c android.intent.category.LAUNCHER
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

# Checking per country 
# Checking ph country id
print_blue "\nCHECKING PHILIPPINES"
expectedPhCountryId="PH"
currentPhCountryId=$(./gradlew -q printCountryId -PflavorName=$flavorName -PbuildTypeAssemble=$buildTypeAssemble  -PcountryName=$philippines)
if [ $expectedPhCountryId = "$currentPhCountryId" ]; then
print_green "\nexpected: $expectedPhCountryId and current: $currentPhCountryId are the same\n"
else
print_red "\nexpected: $expectedPhCountryId and current: $currentPhCountryId are not the same\n"
fi

# Checking ph account id
print_blue "\nCHECKING PH ACCOUNT ID"
expectedPhAccountId="4EBF093C-0D81-43E5-A284-CB5F24F7361C"
currentPhAccountId=$(./gradlew -q printAccountId -PflavorName=$flavorName -PbuildTypeAssemble=$buildTypeAssemble  -PcountryName=$philippines)
if [ $expectedPhAccountId = "$currentPhAccountId" ]; then
print_green "\nexpected: $expectedPhAccountId and current: $currentPhAccountId are the same\n"
else
print_red "\nexpected: $expectedPhAccountId and current: $currentPhAccountId are not the same\n"
fi

#Check ph genie api root
print_blue "\nCHECKING PH GENIE API ROOT"
expectedPhGenieRoot="https://www.mysale.ph/"
currentPhGenieRoot=$(./gradlew -q printGenieApi -PflavorName=$flavorName -PbuildTypeAssemble=$buildTypeAssemble -PcountryName=$philippines)
if [ $expectedPhGenieRoot = "$currentPhGenieRoot" ]; then
print_green "\nexpected: $expectedPhGenieRoot and current: $currentPhGenieRoot is the same\n"
else
print_red "\nexpected: $expectedPhGenieRoot and current: $currentPhGenieRoot is not the same\n"
fi

#Check ph currency code
print_blue "\nCHECKING PH CURRENCY CODE"
expectedPhCurrencyCode="PHP"
currenctPhCurrencyCode=$(./gradlew -q printCurrencyCode -PflavorName=$flavorName -PbuildTypeAssemble=$buildTypeAssemble -PcountryName=$philippines)
if [ $expectedPhCurrencyCode = "$currenctPhCurrencyCode" ]; then
print_green "\nexpected: $expectedPhCurrencyCode and current: $currenctPhCurrencyCode is the same\n"
else
print_red "\nexpected: $expectedPhCurrencyCode and current: $currenctPhCurrencyCode is not the same\n"
fi

# Checking th country id
print_blue "\nCHECKING THAILAND"
expectedThCountryId="TH"
currentThCountryId=$(./gradlew -q printCountryId -PflavorName=$flavorName -PbuildTypeAssemble=$buildTypeAssemble  -PcountryName=$thailand)
if [ $expectedThCountryId = "$currentThCountryId" ]; then
print_green "\nexpected: $expectedThCountryId and current: $currentThCountryId are the same\n"
else
print_red "\nexpected: $expectedThCountryId and current: $currentThCountryId are not the same\n"
fi

# Checking th account id
print_blue "\nCHECKING TH ACCOUNT ID"
expectedThAccountId="28F20D19-6E9E-4B43-98AB-765F96DC0E5C"
currentThAccountId=$(./gradlew -q printAccountId -PflavorName=$flavorName -PbuildTypeAssemble=$buildTypeAssemble  -PcountryName=$thailand)
if [ $expectedThAccountId = "$currentThAccountId" ]; then
print_green "\nexpected: $expectedThAccountId and current: $currentThAccountId are the same\n"
else
print_red "\nexpected: $expectedThAccountId and current: $currentThAccountId are not the same\n"
fi

#Check th genie api root
print_blue "\nCHECKING TH GENIE API ROOT"
expectedThGenieRoot="https://www.mysale.co.th/"
currentThGenieRoot=$(./gradlew -q printGenieApi -PflavorName=$flavorName -PbuildTypeAssemble=$buildTypeAssemble -PcountryName=$thailand)
if [ $expectedThGenieRoot = "$currentThGenieRoot" ]; then
print_green "\nexpected: $expectedThGenieRoot and current: $currentThGenieRoot is the same\n"
else
print_red "\nexpected: $expectedThGenieRoot and current: $currentThGenieRoot is not the same\n"
fi

#Check th currency code
print_blue "\nCHECKING TH CURRENCY CODE"
expectedThCurrencyCode="THB"
currenctThCurrencyCode=$(./gradlew -q printCurrencyCode -PflavorName=$flavorName -PbuildTypeAssemble=$buildTypeAssemble -PcountryName=$thailand)
if [ $expectedThCurrencyCode = "$currenctThCurrencyCode" ]; then
print_green "\nexpected: $expectedThCurrencyCode and current: $currenctThCurrencyCode is the same\n"
else
print_red "\nexpected: $expectedThCurrencyCode and current: $currenctThCurrencyCode is not the same\n"
fi

# Checking my country id
print_blue "\nCHECKING MALAYSIA"
expectedMyCountryId="MY"
currentMyCountryId=$(./gradlew -q printCountryId -PflavorName=$flavorName -PbuildTypeAssemble=$buildTypeAssemble  -PcountryName=$malaysia)
if [ $expectedMyCountryId = "$currentMyCountryId" ]; then
print_green "\nexpected: $expectedMyCountryId and current: $currentMyCountryId are the same\n"
else
print_red "\nexpected: $expectedMyCountryId and current: $currentMyCountryId are not the same\n"
fi

# Checking my account id
print_blue "\nCHECKING MY ACCOUNT ID"
expectedMyAccountId="34849BC9-EB96-4E2B-9698-B71F11F73297"
currentMyAccountId=$(./gradlew -q printAccountId -PflavorName=$flavorName -PbuildTypeAssemble=$buildTypeAssemble  -PcountryName=$malaysia)
if [ $expectedMyAccountId = "$currentMyAccountId" ]; then
print_green "\nexpected: $expectedMyAccountId and current: $currentMyAccountId are the same\n"
else
print_red "\nexpected: $expectedMyAccountId and current: $currentMyAccountId are not the same\n"
fi

#Check my genie api root
print_blue "\nCHECKING MY GENIE API ROOT"
expectedMyGenieRoot="https://www.mysale.my/"
currentMyGenieRoot=$(./gradlew -q printGenieApi -PflavorName=$flavorName -PbuildTypeAssemble=$buildTypeAssemble -PcountryName=$malaysia)
if [ $expectedMyGenieRoot = "$currentMyGenieRoot" ]; then
print_green "\nexpected: $expectedMyGenieRoot and current: $currentMyGenieRoot is the same\n"
else
print_red "\nexpected: $expectedMyGenieRoot and current: $currentMyGenieRoot is not the same\n"
fi

#Check my currency code
print_blue "\nCHECKING MY CURRENCY CODE"
expectedMyCurrencyCode="MYR"
currenctMyCurrencyCode=$(./gradlew -q printCurrencyCode -PflavorName=$flavorName -PbuildTypeAssemble=$buildTypeAssemble -PcountryName=$malaysia)
if [ $expectedMyCurrencyCode = "$currenctMyCurrencyCode" ]; then
print_green "\nexpected: $expectedMyCurrencyCode and current: $currenctMyCurrencyCode is the same\n"
else
print_red "\nexpected: $expectedMyCurrencyCode and current: $currenctMyCurrencyCode is not the same\n"
fi

# Checking uk country id
print_blue "\nCHECKING UNITED KINGDOM"
expectedUkCountryId="UK"
currentUkCountryId=$(./gradlew -q printCountryId -PflavorName=$flavorName -PbuildTypeAssemble=$buildTypeAssemble  -PcountryName="$uk")
if [ $expectedUkCountryId = "$currentUkCountryId" ]; then
print_green "\nexpected: $expectedUkCountryId and current: $currentUkCountryId are the same\n"
else
print_red "\nexpected: $expectedUkCountryId and current: $currentUkCountryId are not the same\n"
fi

# Checking uk account id
print_blue "\nCHECKING UK ACCOUNT ID"
expectedUkAccountId="314D2B32-21F9-431D-975C-129BE4ED6BA0"
currentUkAccountId=$(./gradlew -q printAccountId -PflavorName=$flavorName -PbuildTypeAssemble=$buildTypeAssemble  -PcountryName="$uk")
if [ $expectedUkAccountId = "$currentUkAccountId" ]; then
print_green "\nexpected: $expectedUkAccountId and current: $currentUkAccountId are the same\n"
else
print_red "\nexpected: $expectedUkAccountId and current: $currentUkAccountId are not the same\n"
fi

#Check uk genie api root
print_blue "\nCHECKING UK GENIE API ROOT"
expectedUkGenieRoot="https://www.mysale.co.uk/"
currentUkGenieRoot=$(./gradlew -q printGenieApi -PflavorName=$flavorName -PbuildTypeAssemble=$buildTypeAssemble -PcountryName="$uk")
if [ $expectedMyGenieRoot = "$currentMyGenieRoot" ]; then
print_green "\nexpected: $expectedMyGenieRoot and current: $currentMyGenieRoot is the same\n"
else
print_red "\nexpected: $expectedMyGenieRoot and current: $currentMyGenieRoot is not the same\n"
fi

#Check uk currency code
print_blue "\nCHECKING UK CURRENCY CODE"
expectedUkCurrencyCode="GBP"
currenctUkCurrencyCode=$(./gradlew -q printCurrencyCode -PflavorName=$flavorName -PbuildTypeAssemble=$buildTypeAssemble -PcountryName="$uk")
if [ $expectedUkCurrencyCode = "$currenctUkCurrencyCode" ]; then
print_green "\nexpected: $expectedUkCurrencyCode and current: $currenctUkCurrencyCode is the same\n"
else
print_red "\nexpected: $expectedUkCurrencyCode and current: $currenctUkCurrencyCode is not the same\n"
fi

# Checking hk country id
print_blue "\nCHECKING HONGKONG"
expectedHkCountryId="HK"
currentHkCountryId=$(./gradlew -q printCountryId -PflavorName=$flavorName -PbuildTypeAssemble=$buildTypeAssemble  -PcountryName=$hongkong)
if [ $expectedHkCountryId = "$currentHkCountryId" ]; then
print_green "\nexpected: $expectedHkCountryId and current: $currentHkCountryId are the same\n"
else
print_red "\nexpected: $expectedHkCountryId and current: $currentHkCountryId are not the same\n"
fi

# Checking hk account id
print_blue "\nCHECKING HK ACCOUNT ID"
expectedHkAccountId="ED03076F-912B-4287-9580-6E2F52D33580"
currentHkAccountId=$(./gradlew -q printAccountId -PflavorName=$flavorName -PbuildTypeAssemble=$buildTypeAssemble  -PcountryName=$hongkong)
if [ $expectedHkAccountId = "$currentHkAccountId" ]; then
print_green "\nexpected: $expectedHkAccountId and current: $currentHkAccountId are the same\n"
else
print_red "\nexpected: $expectedHkAccountId and current: $currentHkAccountId are not the same\n"
fi

#Check hk genie api root
print_blue "\nCHECKING HK GENIE API ROOT"
expectedHkGenieRoot="https://www.mysale.hk/"
currentHkGenieRoot=$(./gradlew -q printGenieApi -PflavorName=$flavorName -PbuildTypeAssemble=$buildTypeAssemble -PcountryName=$hongkong)
if [ $expectedHkGenieRoot = "$currentHkGenieRoot" ]; then
print_green "\nexpected: $expectedHkGenieRoot and current: $currentHkGenieRoot is the same\n"
else
print_red "\nexpected: $expectedHkGenieRoot and current: $currentHkGenieRoot is not the same\n"
fi

#Check hk currency code
print_blue "\nCHECKING HK CURRENCY CODE"
expectedHkCurrencyCode="HKD"
currenctHkCurrencyCode=$(./gradlew -q printCurrencyCode -PflavorName=$flavorName -PbuildTypeAssemble=$buildTypeAssemble -PcountryName=$hongkong)
if [ $expectedHkCurrencyCode = "$currenctHkCurrencyCode" ]; then
print_green "\nexpected: $expectedHkCurrencyCode and current: $currenctHkCurrencyCode is the same\n"
else
print_red "\nexpected: $expectedHkCurrencyCode and current: $currenctHkCurrencyCode is not the same\n"
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