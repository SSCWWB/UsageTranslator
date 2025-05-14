package com.yushiz.project.UsageTranslator.service;

import com.yushiz.project.UsageTranslator.model.UsageRecord;

import java.util.*;
import java.util.logging.Logger;

import org.apache.commons.lang3.StringUtils;

public class TableValuesProcessor {
	private static final Map<String, Integer> UNIT_REDUCTION = Map.of("EA000001GB0O", 1000, "PMQ00005GB0R", 5000,
			"SSX006NR", 1000, "SPQ00001MB0R", 2000);

	private static final List<Integer> SKIP_PARTNERS = List.of(26392);

	public static Map<String, List<String>> process(List<UsageRecord> records, Map<String, String> typeMap,
			Logger logger) {
		List<String> chargeableTablevalues = new ArrayList<>();
		List<String> domainTablevalues = new ArrayList<>();
		Map<String, List<String>> result = new HashMap<>();
		Set<String> seen = new HashSet<>();

		Map<String, Long> totalProductCount = new HashMap<>();

		for (UsageRecord r : records) {
			// First handle records for domain table
			String domain = r.getDomains().trim();

			// strip any non-alphanumeric characters
			String partnerPurchasedPlanID = r.getAccountGuid().replaceAll("[^A-Za-z0-9]", "");

			// check if partnerPurchasedPlanID is longer than 32
			if (partnerPurchasedPlanID.length() > 32) {
				logger.warning("partnerPurchasedPlanID too long: " + partnerPurchasedPlanID);
				continue;
			}

			if (!StringUtils.isBlank(domain)) {
				String key = partnerPurchasedPlanID + "|" + domain;
				if (!seen.contains(key)) {
					seen.add(key);
					// prevent from injecting arbitrary SQL
					String value = String.format("('%s', '%s')", escape(partnerPurchasedPlanID), escape(domain));
					domainTablevalues.add(value);
				}
			}

			// Handle records for chargeable table
			String partNo = r.getPartNumber().trim();
			// skip and log when PartNumber is empty
			if (StringUtils.isBlank(partNo)) {
				logger.severe("Missing PartNumber for record: " + r.toString());
				continue;
			}

			// skip and log when itemCount is not positive
			int itemCount = r.getItemCount();
			if (itemCount <= 0) {
				logger.severe("Non positive itemCount for record: " + r.toString());
				continue;
			}

			// skip when partner id is in the configurable list
			int partnerID = r.getPartnerID();
			if (SKIP_PARTNERS.contains(partnerID))
				continue;

			// get product from PartNumber mapped from typemap.json
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
		for (Map.Entry<String, Long> p : totalProductCount.entrySet()) {
			System.out.println("Product: " + p.getKey() + " total count is " + p.getValue());
			logger.info("Product: " + p.getKey() + " total count is " + p.getValue());
		}
		result.put("chargeable", chargeableTablevalues);
		result.put("domain", domainTablevalues);

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