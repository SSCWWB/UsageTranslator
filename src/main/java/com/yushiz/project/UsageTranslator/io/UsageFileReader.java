package com.yushiz.project.UsageTranslator.io;

import com.yushiz.project.UsageTranslator.model.UsageRecord;
import com.opencsv.CSVReader;

import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class UsageFileReader {

	public static List<UsageRecord> read(String csvPath, Logger logger) throws Exception {
		List<UsageRecord> records = new ArrayList<>();
		try (CSVReader reader = new CSVReader(new FileReader(csvPath))) {
			String[] row;
			// skip the header
			reader.readNext();
			while ((row = reader.readNext()) != null) {
				String partNumber = row[9];
				int partnerID = Integer.parseInt(row[0]);
				String accountGuid = row[3];
				String plan = row[7];
				int itemCount = Integer.parseInt(row[10]);
				String domains = row[5];
				UsageRecord record = new UsageRecord(partnerID, partNumber, itemCount, accountGuid, plan, domains);
				records.add(record);
			}
		}
		return records;
	}
}