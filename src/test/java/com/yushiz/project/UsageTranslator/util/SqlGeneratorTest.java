package com.yushiz.project.UsageTranslator.util;

import org.junit.jupiter.api.Test;
import java.util.Arrays;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public class SqlGeneratorTest {

	/**
	 * Test generated insert sql for chargeable table with 2 valid records
	 */
	@Test
	public void testGenerateChargeableInsert() {
		// test data
		List<String> values = Arrays.asList(
				"(26668, 'core.test1', 'ff633524c35f4de8acdaa7cdee38cd15', 'E2016_Exch_1_HOSTWAY', 3)",
				"(26668, 'core.test2', 'ff633524c35f4de8acdaa7cdee38cd15', 'E2016_Exch_1_HOSTWAY', 2)");

		String result = SqlGenerator.generateChargeableInsert(values);

		// check results
		assertNotNull(result);
		assertTrue(result.startsWith("INSERT INTO chargeable"));
		assertTrue(result.contains("core.test1"));
		assertTrue(result.contains("core.test2"));
		assertTrue(result.endsWith(";"));
	}

	/**
	 * Test generated insert sql for domains table with 2 valid records
	 */
	@Test
	public void testGenerateDomainInsert() {
		// test data
		List<String> values = Arrays.asList("('ff633524c35f4de8acdaa7cdee38cd15', 'test.net')",
				"('6a1e663c829e4e79ac16fca89d61f143', 'test2.net')");

		String result = SqlGenerator.generateDomainInsert(values);

		// check results
		assertNotNull(result);
		assertTrue(result.startsWith("INSERT INTO domains"));
		assertTrue(result.contains("test.net"));
		assertTrue(result.contains("test2.net"));
		assertTrue(result.endsWith(";"));
	}

	/**
	 * Test generated insert sql for domains table with empty inputs
	 */
	@Test
	public void testGenerateEmptyInsert() {
		// test data
		List<String> emptyValues = Arrays.asList();

		String chargeableResult = SqlGenerator.generateChargeableInsert(emptyValues);
		String domainResult = SqlGenerator.generateDomainInsert(emptyValues);

		// check results
		assertNotNull(chargeableResult);
		assertNotNull(domainResult);
		assertTrue(chargeableResult.startsWith("INSERT INTO chargeable"));
		assertTrue(domainResult.startsWith("INSERT INTO domains"));
		assertTrue(chargeableResult.endsWith(";"));
		assertTrue(domainResult.endsWith(";"));
	}
}