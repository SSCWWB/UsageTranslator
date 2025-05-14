package com.yushiz.project.UsageTranslator.util;

import java.util.List;

public class SqlGenerator {
	public static String generateChargeableInsert(List<String> values) {
		StringBuilder sql = new StringBuilder();
		sql.append("INSERT INTO chargeable (partnerID, product, partnerPurchasedPlanID, plan, usage) VALUES\n");
		for (int i = 0; i < values.size(); i++) {
			sql.append(values.get(i));
			if (i < values.size() - 1) {
				sql.append(",\n");
			}
		}
		sql.append(";");
		return sql.toString();
	}

	public static String generateDomainInsert(List<String> values) {
		StringBuilder sql = new StringBuilder();
		sql.append("INSERT INTO domains (partnerPurchasedPlanID, domain) VALUES\n");
		for (int i = 0; i < values.size(); i++) {
			sql.append(values.get(i));
			if (i < values.size() - 1) {
				sql.append(",\n");
			}
		}
		sql.append(";");
		return sql.toString();
	}
}
