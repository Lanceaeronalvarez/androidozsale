package au.com.dealsdirect.ui.controller.account.model;

import java.util.List;
import java.util.Objects;

/**
 * Created by smartwave on 24/05/2018.
 */

public class AccountItem {
    private AccountOption option;
    private List<AccountItem> subItems;

    public AccountItem(AccountOption option, List<AccountItem> subItems) {
        this.option = option;
        this.subItems = subItems;
    }

    public AccountOption getOption() {
        return option;
    }

    public List<AccountItem> getSubItems() {
        return subItems;
    }

    @Override
    public int hashCode() {
        return Objects.hash(option, subItems);
    }
}
