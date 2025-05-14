package com.yushiz.project.UsageTranslator.util;

import java.util.List;

public class SqlGenerator {
	public static String generateChargeableInsert(List<String> values) {
		String joined = String.join(",\n", values);
		return "INSERT INTO chargeable (partnerID, product, partnerPurchasedPlanID, plan, usage) VALUES\n" + joined
				+ ";";
	}

	public static String generateDomainInsert(List<String> values) {
		String joined = String.join(",\n", values);
		return "INSERT INTO domains (partnerPurchasedPlanID, domain) VALUES\n" + joined + ";";
	}
}
