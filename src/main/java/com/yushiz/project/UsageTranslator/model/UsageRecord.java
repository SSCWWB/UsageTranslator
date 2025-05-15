package com.yushiz.project.UsageTranslator.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * This class maps data from a CSV file. I omitted some columns since they are
 * not used in this project
 */
@Getter
@Setter
@ToString
public class UsageRecord {
	private int partnerID;
	private String partNumber;
	private int itemCount;
	private String accountGuid;
	private String plan;
	private String domains;

	public UsageRecord(int partnerID, String partNumber, int itemCount, String accountGuid, String plan,
			String domains) {
		this.partnerID = partnerID;
		this.partNumber = partNumber;
		this.itemCount = itemCount;
		this.accountGuid = accountGuid;
		this.plan = plan;
		this.domains = domains;
	}

}