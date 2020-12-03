package au.com.dealsdirect.ui.controller.saleitems;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import au.com.dealsdirect.data.network.model.category.GetCategoryTreeResponse;

public class BrandNames {
    public static final int UNLIMITED_RESULTS = -1;

    private Set<String> brandNames = null;

    private HashMap<Integer, Set<String>> mappedBrandNames = null;

    private int longestWordLength = -1;

    public BrandNames(List<GetCategoryTreeResponse> getCategoryTreeResponses) {
        processBrandNames(getCategoryTreeResponses);
        mapBrandNames();
    }

    private void processBrandNames(List<GetCategoryTreeResponse> categoryTrees) {
        Set<String> brands = new HashSet<>();
        for (GetCategoryTreeResponse categoryTreeResponse : categoryTrees) {
            if (categoryTreeResponse.getBrands() != null) {
                for (GetCategoryTreeResponse.Brand brand : categoryTreeResponse.getBrands()) {
                    if (brand.getName() != null && !brand.getName().isEmpty()) {
                        brands.add(brand.getName());
                    }
                }
            }
        }
        brandNames = brands;
    }

    private void mapBrandNames() {
        if (brandNames == null) {
            return;
        }

        mappedBrandNames = new HashMap<>();
        for (String brandName : brandNames) {
            final int key = brandName.length();
            if (longestWordLength < key) {
                longestWordLength = key;
            }
            Set<String> subset = mappedBrandNames.get(key);
            if (subset == null) {
                subset = new HashSet<>();
                mappedBrandNames.put(key, subset);
            }
            subset.add(brandName);
        }
    }

    public Set<String> getBrandNames() {
        return brandNames;
    }

    public List<String> getBrandNamesMatchingString(String string) {
        return getBrandNamesMatchingString(string, true, UNLIMITED_RESULTS);
    }

    public List<String> getBrandNamesMatchingString(String string, int maxResults) {
        return getBrandNamesMatchingString(string, true, maxResults);
    }

    public List<String> getBrandNamesMatchingString(String string, boolean caseInsensitive) {
        return getBrandNamesMatchingString(string, caseInsensitive, UNLIMITED_RESULTS);
    }

    public List<String> getBrandNamesMatchingString(String string, boolean caseInsensitive, int maxResults) {
        final String prefix = caseInsensitive ? string.toLowerCase() : string;
        final Pattern pattern = Pattern.compile("^(" + prefix + ")+");
        final List<String> results = new LinkedList<>();
        for (int i = string.length(); i <= longestWordLength; i++) {
            Set<String> subset = mappedBrandNames.get(i);
            if (subset != null) {
                for (String brandName : subset) {
                    final String brandNameToTest = caseInsensitive ? brandName.toLowerCase() : brandName;
                    final Matcher matcher = pattern.matcher(brandNameToTest);
                    if (matcher.find()) {
                        results.add(brandName);
                    }
                }
            }
            if (maxResults != UNLIMITED_RESULTS && results.size() >= maxResults) {
                break;
            }
        }
        return results;
    }

    public List<String> getBrandNamesRegex(String regex, int maxResults) {
        final List<String> results = new LinkedList<>();
        for (String brandName : brandNames) {
            if (brandName.matches(regex)) {
                results.add(brandName);
            }
            if (maxResults != UNLIMITED_RESULTS && results.size() >= maxResults) {
                break;
            }
        }
        return results;
    }
}
