package com.yushiz.project.UsageTranslator;

import com.yushiz.project.UsageTranslator.io.UsageFileReader;
import com.yushiz.project.UsageTranslator.io.JsonTypeMapper;
import com.yushiz.project.UsageTranslator.model.UsageRecord;
import com.yushiz.project.UsageTranslator.service.TableValuesProcessor;
import com.yushiz.project.UsageTranslator.util.SqlGenerator;

import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.SimpleFormatter;

/**
 * main class for this project
 */
public class UsageTranslator {
	private static final Logger logger = Logger.getLogger(UsageTranslator.class.getName());
	static {
		try {
			FileHandler fh = new FileHandler("./logs.txt", false);
			fh.setFormatter(new SimpleFormatter());
			logger.addHandler(fh);
			logger.setUseParentHandlers(false);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static final String CSV_PATH = "./Data/Sample_Report.csv";
	private static final String JSON_PATH = "./Data/typemap.json";
	private static final String CHARGE_SQL = "./sql_insert_chargeable.txt";
	private static final String DOMAIN_SQL = "./sql_insert_domains.txt";

	public static void process() throws Exception {

		// read csv and json file
		Map<String, String> typeMap = JsonTypeMapper.loadTypeMap(Paths.get(JSON_PATH));
		List<UsageRecord> records = UsageFileReader.read(CSV_PATH, logger);


		// process the csv files and extract insert values
		Map<String, List<String>> values = TableValuesProcessor.process(records, typeMap, logger);
		// get each insert sql value list
		// the format is like a list of following :
		// (26668, 'core.chargeable.exchange', 'ff633524c35f4de8acdaa7cdee38cd15',
		// 'E2016_Exch_1_HOSTWAY', 2),
		// (26668, 'core.chargeable.advancemailsec', 'ff633524c35f4de8acdaa7cdee38cd15',
		// 'E2016_Exch_1_HOSTWAY', 2),
		List<String> chargeableValues = values.get("chargeable");
		List<String> domainValues = values.get("domains");

		// Generate SQL statements
		String insertChargeableSql = SqlGenerator.generateChargeableInsert(chargeableValues);
		String insertDomainSql = SqlGenerator.generateDomainInsert(domainValues);

		// print out insert sqls
		System.out.println(insertChargeableSql);
		System.out.println(insertDomainSql);

		// write chargeable table insert sql to txt files
		try (FileWriter fileWriter = new FileWriter(CHARGE_SQL, false);
				BufferedWriter writer = new BufferedWriter(fileWriter)) {
			writer.write(insertChargeableSql);
			System.out.println("Successfully wrote SQL statements to " + CHARGE_SQL);
		} catch (IOException e) {
			System.err.println("Error writing to file: " + e.getMessage());
		}

		// write domains table insert sql to txt files
		try (FileWriter fileWriter2 = new FileWriter(DOMAIN_SQL, false);
				BufferedWriter writer2 = new BufferedWriter(fileWriter2)) {
			writer2.write(insertDomainSql);
			System.out.println("Successfully wrote SQL statements to " + DOMAIN_SQL);
		} catch (IOException e) {
			System.err.println("Error writing to file: " + e.getMessage());
		}
	}

	public static void main(String[] args) throws Exception {
		UsageTranslator.process();
	}
}