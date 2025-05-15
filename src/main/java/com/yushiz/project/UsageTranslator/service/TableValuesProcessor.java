package com.yushiz.project.UsageTranslator.service;

import com.yushiz.project.UsageTranslator.model.UsageRecord;

import java.util.*;
import java.util.logging.Logger;

import org.apache.commons.lang3.StringUtils;

/**
 * This class will process the list of records read from csv file
 */
public class TableValuesProcessor {
	// unit reduction list
	private static final Map<String, Integer> UNIT_REDUCTION;
	static {
		Map<String, Integer> m = new HashMap<>();
		m.put("EA000001GB0O", 1000);
		m.put("PMQ00005GB0R", 5000);
		m.put("SSX006NR", 1000);
		m.put("SPQ00001MB0R", 2000);
		UNIT_REDUCTION = Collections.unmodifiableMap(m);
	}

	// partner id need to be skipped
	private static final List<Integer> SKIP_PARTNERS = Collections.unmodifiableList(Arrays.asList(26392));

	public static Map<String, List<String>> process(List<UsageRecord> records, Map<String, String> typeMap,
			Logger logger) {
		List<String> chargeableTablevalues = new ArrayList<>();
		List<String> domainTablevalues = new ArrayList<>();
		Map<String, List<String>> result = new HashMap<>();
		Set<String> seen = new HashSet<>();

		Map<String, Long> totalProductCount = new HashMap<>();

		// doing one loop for both domains and chargeable tables, first handle domains
		// table then chargeable
		for (UsageRecord r : records) {
			// First handle records for domain table
			String domain = r.getDomains().trim();

			// strip any non-alphanumeric characters
			String partnerPurchasedPlanID = r.getAccountGuid().replaceAll("[^A-Za-z0-9]", "");

			// check if partnerPurchasedPlanID longer than 32 after stripping.
			// Although the sample CSV file does not contain such case, it's still good
			// practice to add this check
			if (partnerPurchasedPlanID.length() > 32) {
				logger.warning("partnerPurchasedPlanID too long: " + partnerPurchasedPlanID);
				continue;
			}

			// skip insert domains table if domain is empty
			if (!StringUtils.isBlank(domain)) {
				// ensure no duplicate domains are inserted
				String key = partnerPurchasedPlanID + "|" + domain;
				if (!seen.contains(key)) {
					seen.add(key);
					// prevent from injecting arbitrary SQL by using escape
					String value = String.format("('%s', '%s')", escape(partnerPurchasedPlanID), escape(domain));
					domainTablevalues.add(value);
				}
			}

			// Handle records for chargeable table
			String partNo = r.getPartNumber().trim();
			// skip and log when PartNumber is empty
			if (StringUtils.isBlank(partNo)) {
				logger.warning("Missing PartNumber for record: " + r.toString());
				continue;
			}

			// skip and log when itemCount is not positive
			int itemCount = r.getItemCount();
			if (itemCount <= 0) {
				logger.warning("Non positive itemCount for record: " + r.toString());
				continue;
			}

			// skip when partner id is in the configurable skip list
			int partnerID = r.getPartnerID();
			if (SKIP_PARTNERS.contains(partnerID))
				continue;

			// get product from PartNumber mapped from typemap.json
			// if can't get product for the PartNumber then skip
			String product = typeMap.get(partNo);
			if (product == null) {
				logger.info("No mapping for PartNumber= " + r.getPartNumber());
				continue;
			}

			// compute usage according to reduction rules
			int usage = getUsage(partNo, itemCount);

			// sum total item count per product
			totalProductCount.put(product, totalProductCount.getOrDefault(product, 0L) + itemCount);

			// generate table values for each row
			String newValue = String.format("(%d, '%s', '%s', '%s', %d)", partnerID, escape(product),
					escape(partnerPurchasedPlanID), escape(r.getPlan().trim()), usage);
			chargeableTablevalues.add(newValue);
		}

		// print out and log stats of running totals over 'itemCount' for each of the
		// products in a success operation
		logger.info("=== Product Usage Statistics ===");
		for (Map.Entry<String, Long> p : totalProductCount.entrySet()) {
			System.out.println("Product: " + p.getKey() + " total count is " + p.getValue());
			logger.info("Product: " + p.getKey() + " total count is " + p.getValue());
		}
		logger.info("=== End of Statistics ===");
		result.put("chargeable", chargeableTablevalues);
		result.put("domains", domainTablevalues);

		return result;
	}

	// apply unit reduction
	private static int getUsage(String partnerNo, int itemCount) {
		if (!UNIT_REDUCTION.containsKey(partnerNo))
			return itemCount;
		int reduction = UNIT_REDUCTION.get(partnerNo);
		return itemCount / reduction;
	}

	// prevent from injecting arbitrary SQL
	public static String escape(String s) {
		return s == null ? null : s.replace("'", "''");
	}
}