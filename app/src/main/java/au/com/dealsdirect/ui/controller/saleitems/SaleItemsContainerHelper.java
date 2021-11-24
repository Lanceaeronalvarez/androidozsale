package au.com.dealsdirect.ui.controller.saleitems;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import au.com.dealsdirect.data.network.model.saleitems.SaleItemProduct;

public class SaleItemsContainerHelper {

    private final Map<Integer, List<SaleItemProduct>> items;
    private final List<Integer> indices; //should always be sorted
    private List<SaleItemProduct> mergedItems;
    private int size;

    public SaleItemsContainerHelper() {
        items = new HashMap<>();
        indices = new ArrayList<>();
        mergedItems = null;
    }

    public SaleItemsContainerHelper(SaleItemsContainerHelper existingContainer) {
        items = new HashMap<>(existingContainer.items);
        indices = new ArrayList<>(existingContainer.indices);
        mergedItems = new ArrayList<>(existingContainer.mergedItems);
        size = mergedItems.size();
    }

    public void clearItems() {
        items.clear();
        indices.clear();
        size = 0;
    }

    // atIndex can be page number or offset
    public void setItemsInSlot(int atIndex, List<SaleItemProduct> items, boolean willReplace) {
        boolean isExisting = this.items.get(atIndex) != null;
        if (!willReplace && isExisting) {
            return;
        }
        if (!isExisting) {
            size += items.size();
        }
        this.items.put(atIndex, items);

        // use an insert function that presorts
        // the indices to make merging faster
        insertIndex(atIndex);

        // clear merged list
        mergedItems = null;
    }

    public void removeItem(SaleItemProduct item) {
        for (int i = 0; i < indices.size(); i++) {
            List<SaleItemProduct> list = items.get(indices.get(i));
            if (list != null && list.contains(item)) {
                list.remove(item);
                size--;
                mergedItems = null;
                return;
            }
        }
    }

    public void removeItem(String productId) {
        for (int i = 0; i < indices.size(); i++) {
            List<SaleItemProduct> list = items.get(indices.get(i));
            if (list != null) {
                for (int j = 0; j < list.size(); j++) {
                    if (list.get(j).getId().equals(productId)) {
                        list.remove(j);
                        size--;
                        mergedItems = null;
                        return;
                    }
                }
            }
        }
    }

    public int indexOfItem(SaleItemProduct item) {
        List<SaleItemProduct> list = getItems();
        return list.indexOf(item);
    }

    public int indexOfItem(String productId) {
        List<SaleItemProduct> list = getItems();
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getId().equals(productId)) {
                return i;
            }
        }
        return -1;
    }

    public int getSize() {
        return size;
    }

    public boolean isSlotEmpty(int atIndex) {
        return items.get(atIndex) == null;
    }

    public int getLastIndex() {
        return indices.get(indices.size() - 1);
    }

    public List<SaleItemProduct> getItems() {
        if (mergedItems == null) {
            mergedItems = new ArrayList<>();
            for (int i = 0; i < indices.size(); i++) {
                // indices have to be sorted
                List<SaleItemProduct> items = this.items.get(indices.get(i));
                if (items != null) {
                    mergedItems.addAll(items);
                }
            }
            size = mergedItems.size();
        }
        return mergedItems;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    private void insertIndex(int index) {
        // assures that the indices will always be sorted
        // without having to run a sort function on it
        if (indices.isEmpty() || indices.get(indices.size() - 1) < index) {
            indices.add(index);
        } else {
            for (int i = 0; i < indices.size(); i++) {
                if (indices.get(i) > index) {
                    indices.add(i, index);
                    break;
                }
            }
        }
    }
}
