
## System Requirements

- Java 8 or higher
- Maven 3.x
- IDE (Eclipse or IntelliJ IDEA recommended)

## Project Dependencies

- Jackson Databind 2.15.2
- OpenCSV 5.7.1
- SLF4J Simple 2.0.7
- Lombok 1.18.38
- JUnit 3.8.1 (for testing)

## Running the Application

### Option 1: Using Maven Command Line

1. Open terminal and navigate to project root directory
2. Execute the following command:
   `mvn clean compile exec:java`

### Option 2: Running from IDE

#### Eclipse
1. Import project into Eclipse
   - File -> Import -> Existing Maven Projects
   - Select project root directory
   - Wait for Maven dependencies to download

2. Run the project
   - Locate `src/main/java/com/yushiz/project/UsageTranslator/UsageTranslator.java`
   - Right-click on the file
   - Select Run As -> Java Application

#### IntelliJ IDEA
1. Import project into IntelliJ IDEA
   - File -> Open
   - Select project root directory
   - Wait for Maven dependencies to download

2. Run the project
   - Locate `src/main/java/com/yushiz/project/UsageTranslator/UsageTranslator.java`
   - Right-click on the file
   - Select Run 'UsageTranslator.main()'

## Verification

1. Console Output:
   - When running from IDE: SQL statements and logs will be displayed in the IDE's console
   - When running from Maven: SQL statements and logs will be displayed in the terminal/command shell
   - Verify that SQL statements and logs are being printed
   - Look for any error messages or warnings

2. Alternative Output Check:
   If you prefer not to check the console output, I have also generated the following files at the project root level:
     `logs.txt`: Contains all application logs
     `sql_insert_chargeable.txt`: Contains SQL insert statements for chargeable data
     `sql_insert_domains.txt`: Contains SQL insert statements for domains data
   - Each time you run the application, the following files will be overwritten:
    `logs.txt`
    `sql_insert_chargeable.txt`
    `sql_insert_domains.txt`

## Important Notes

- Ensure Java environment variables are properly configured
- If running from IDE, make sure Lombok plugin is installed- Maven will automatically download required dependencies on first run
- Generated files will be overwritten on each run
- If you encounter any issues running the application, sample output files are available in the `sampleOutput` directory for reference
