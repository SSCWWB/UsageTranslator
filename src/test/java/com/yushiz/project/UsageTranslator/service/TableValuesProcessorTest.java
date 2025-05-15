package com.yushiz.project.UsageTranslator.service;

import com.yushiz.project.UsageTranslator.model.UsageRecord;
import org.junit.jupiter.api.Test;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.logging.Logger;
import static org.junit.jupiter.api.Assertions.*;

public class TableValuesProcessorTest {
	private static final Logger logger = Logger.getLogger(TableValuesProcessorTest.class.getName());

	/**
	 * Test valid usage records with correct data, no skip
	 */
	@Test
	public void testProcessWithValidRecords() {
		// test data
		List<UsageRecord> records = new ArrayList<>();
		records.add(new UsageRecord(26668, "EA000001GB0O", 2000, "ff633524c35f4de8acdaa7cdee38cd15",
				"E2016_Exch_1_HOSTWAY", "test.net"));
		records.add(new UsageRecord(26668, "PMQ00005GB0R", 5000, "ff633524c35f4de8acdaa7cdee38cd15",
				"E2016_Exch_1_HOSTWAY", "test.net"));

		Map<String, String> typeMap = new HashMap<>();
		typeMap.put("EA000001GB0O", "core.test1");
		typeMap.put("PMQ00005GB0R", "core.test2");

		Map<String, List<String>> result = TableValuesProcessor.process(records, typeMap, logger);

		// check results
		assertNotNull(result);
		assertTrue(result.containsKey("chargeable"));
		assertTrue(result.containsKey("domains"));

		List<String> chargeableValues = result.get("chargeable");
		List<String> domainValues = result.get("domains");

		// check chargeable table values
		assertEquals(2, chargeableValues.size());
		assertTrue(chargeableValues.get(0).contains("core.test1"));
		assertTrue(chargeableValues.get(1).contains("core.test2"));

		// check domain table values
		assertEquals(1, domainValues.size());
		assertTrue(domainValues.get(0).contains("test.net"));
	}

	/**
	 * Test processing invalid usage records
	 */
	@Test
	public void testProcessWithInvalidRecords() {
		List<UsageRecord> records = new ArrayList<>();

		// invalid PartNumber
		records.add(new UsageRecord(26668, "InvalidPartNo", 2000, "ff633524c35f4de8acdaa7cdee38cd15",
				"E2016_Exch_1_HOSTWAY", "test.serverdata.net"));

		// negative itemCount
		records.add(new UsageRecord(26668, "EA000001GB0O", -1, "ff633524c35f4de8acdaa7cdee38cd15",
				"E2016_Exch_1_HOSTWAY", "test.serverdata.net"));

		// partnerID in skipped list
		records.add(new UsageRecord(26392, "EA000001GB0O", 2000, "ff633524c35f4de8acdaa7cdee38cd15",
				"E2016_Exch_1_HOSTWAY", "test.serverdata.net"));

		Map<String, String> typeMap = new HashMap<>();
		typeMap.put("EA000001GB0O", "core.chargeable.exchange");

		Map<String, List<String>> result = TableValuesProcessor.process(records, typeMap, logger);

		// check results
		assertNotNull(result);
		assertTrue(result.containsKey("chargeable"));
		assertTrue(result.containsKey("domains"));

		List<String> chargeableValues = result.get("chargeable");
		List<String> domainValues = result.get("domains");

		// all invalid records are skipped, the return list should be empty
		assertEquals(0, chargeableValues.size());
	}

	/**
	 * Test unit reduction rules
	 */
	@Test
	public void testUnitReduction() {
		// test data
		List<UsageRecord> records = new ArrayList<>();
		records.add(new UsageRecord(26668, "EA000001GB0O", 2000, "ff633524c35f4de8acdaa7cdee38cd15",
				"E2016_Exch_1_HOSTWAY", "test.serverdata.net"));
		records.add(new UsageRecord(26668, "PMQ00005GB0R", 5000, "ff633524c35f4de8acdaa7cdee38cd15",
				"E2016_Exch_1_HOSTWAY", "test.serverdata.net"));

		Map<String, String> typeMap = new HashMap<>();
		typeMap.put("EA000001GB0O", "core.test1");
		typeMap.put("PMQ00005GB0R", "core.test2");

		Map<String, List<String>> result = TableValuesProcessor.process(records, typeMap, logger);

		// check results
		List<String> chargeableValues = result.get("chargeable");
		// 2000/1000 =2 so it should be 2
		assertTrue(chargeableValues.get(0).contains("2"));
		// 5000/5000 =1 so it should be 1
		assertTrue(chargeableValues.get(1).contains("1"));
	}

	/**
	 * Test data with long partnerPurchasedPlanID
	 */
	@Test
	public void testProcessWithLongPartnerPurchasedPlanID() {
		// test data with long partnerPurchasedPlanID
		String longGuid = "ff633524c35f4de8acdaa7cdee38cd15ff633524c35f4de8acdaa7cdee38cd15"; // 64 characters
		List<UsageRecord> records = new ArrayList<>();
		records.add(
				new UsageRecord(26668, "EA000001GB0O", 2000, longGuid, "E2016_Exch_1_HOSTWAY", "test.serverdata.net"));

		Map<String, String> typeMap = new HashMap<>();
		typeMap.put("EA000001GB0O", "core.test1");

		Map<String, List<String>> result = TableValuesProcessor.process(records, typeMap, logger);

		// check results
		assertNotNull(result);
		assertTrue(result.get("chargeable").isEmpty());
		assertTrue(result.get("domains").isEmpty());
	}
}