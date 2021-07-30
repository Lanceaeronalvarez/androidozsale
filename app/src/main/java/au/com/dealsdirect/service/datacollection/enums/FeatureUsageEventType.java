package au.com.dealsdirect.service.datacollection.enums;

public class FeatureUsageEventType {
    public static class Addresses {
        public static final int ADDRESS_SUGGESTION = 1001;
        public static final int ADDRESS_SUGGESTION_REQUESTS = 1068;
    }

    public static class Orders {
        public static final int CHANGE_ADDRESS = 1002;
        public static final int CONTACT_US = 1003;
        public static final int CANCEL_ORDER = 1004;
        public static final int CANCEL_ITEM = 1005;
        public static final int RECEIVED = 1006;
        public static final int RETURN = 1007;
        public static final int VIEW_RETURN = 1008;
        public static final int VIEW_PRODUCT = 1009;
        public static final int VIEW_BRAND = 1010;
        public static final int DOWNLOAD_INVOICE = 1047;
        public static final int EMPTY_MY_ORDERS = 1073;
        public static final int TRACKING_LINK_POPUP = 1082;
        public static final int PARTIAL_REFUND_MESSAGE = 1083;
    }

    public static class Returns {
        public static final int ATTACH_IMAGE = 1011;
        public static final int CREATE_CONTACT = 1012;
        public static final int VIEW_PRODUCT = 1076;
        public static final int VIEW_BRAND = 1077;
        public static final int LINK_TO_MY_DETAILS_HEADER = 1078;
        public static final int APPROVE_LINK_TO_MY_DETAILS = 1079;
        public static final int DECLINED_LINK_TO_RETURNS_POLICY = 1080;
        public static final int LINK_TO_MY_DETAILS_REQUEST_RETURNS_PAGE = 1081;
    }

    public static class ContactUs {
        public static final int ATTACH_IMAGE = 1013;
        public static final int NEW_CONTACT = 1038;
        public static final int CLOSE_THREAD = 1039;
        public static final int FEEDBACK = 1040;
        public static final int ENTRY_INVOICE = 1041;
        public static final int FILTER_INVOICE = 1042;
    }

    public static class Payments {
        public static final int ADD_PAYMENT = 1043;
        public static final int REMOVE_PAYMENT = 1044;
    }

    public static class Vouchers {
        public static final int SHOW_VOUCHER = 1045;
        public static final int VOUCHER_CLICK = 1046;
    }

    public static class OrderTracking {
        public static final int TRACKING_DESCRIPTION = 1049;
    }

    public static class Stores {
        public static final int OPEN_STORE = 1048;
    }

    public static class Brands {
        public static final int OPEN_A_BRAND_PAGE = 1070;
        public static final int CLICK_ON_A_BRAND = 1071;
        public static final int CLICK_ON_A_BRAND_FROM_TOP_MENU = 1072;
    }

    public static class ShopPage {
        public static final int TOGGLE_SALE_BANNER_SIZE = 1141;
    }

    public static class Search {
        public static final int TOGGLE_GRID_SIZE_TO_LARGE = 1118;
        public static final int TOGGLE_GRID_SIZE_TO_SMALL = 1119;
    }

    public static class FacetSearch {
        public static final int SEARCH_FOR_A_BRAND = 1050;
        public static final int SEARCH_FOR_A_SIZE = 1069;
    }

    public static class Breadcrumbs {
        public static final int BREADCRUMBS_CLICK = 1051;
    }

    public static class Suggestions {
        public static final int WHERE_ORDER = 1014;
        public static final int AMEND_ORDER = 1015;
        public static final int AMEND_HELP = 1016;
        public static final int CHANGE_ADDRESS = 1017;
        public static final int RETURN_INCORRECT = 1018;
        public static final int RETURNS_INQUIRY = 1019;
        public static final int RETURNS_HELP = 1020;
        public static final int OURPAY = 1021;
        public static final int OURPAY_SITE = 1022;
        public static final int SELECT = 1023;
        public static final int SELECT_SITE = 1024;
        public static final int PAYMENTS = 1025;
        public static final int PAYMENTS_HELP = 1026;
        public static final int INVITE_FRIEND = 1027;
        public static final int RESET_PASSWORD = 1028;
        public static final int TECH_HELP = 1029;
        public static final int SEARCH = 1030;
        public static final int OTHERS_HELP = 1031;
    }

    public static class Navigations {
        public static final int CONTACT_MENU = 1035;
        public static final int CONTACT_HEADER = 1036;
        public static final int NEW_CONTACT = 1037;
        public static final int DETAILS_MENU = 1052;
        public static final int DETAILS_HEADER = 1053;
        public static final int ADDRESSES_MENU = 1054;
        public static final int ADDRESSES_HEADER = 1055;
        public static final int ORDERS_MENU = 1056;
        public static final int ORDERS_HEADER = 1057;
        public static final int VOUCHERS_MENU = 1058;
        public static final int VOUCHERS_HEADER = 1059;
        public static final int RETURNS_MENU = 1060;
        public static final int RETURNS_HEADER = 1061;
        public static final int PAYMENTS_MENU = 1062;
        public static final int PAYMENTS_HEADER = 1063;
        public static final int OURPAY_MENU = 1064;
        public static final int OURPAY_HEADER = 1065;
        public static final int INVITE_A_FRIEND_MENU = 1066;
        public static final int INVITE_A_FRIEND_HEADER = 1067;
        public static final int ABOUT_US = 1084;
        public static final int TERMS_AND_CONDITIONS = 1085;
        public static final int RETURNS_POLICY = 1086;
        public static final int PRIVACY_POLICY = 1088;
        public static final int GIFTS_MENU = 1131;
        public static final int GIFTS_HEADER = 1132;
    }

    public static class Codes {
        public static final int SHOW_CODES = 1074;
        public static final int CLICK_ON_A_CODE = 1075;
    }
}
