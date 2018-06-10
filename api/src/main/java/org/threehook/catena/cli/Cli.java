package org.threehook.catena.cli;

import org.apache.commons.cli.*;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.threehook.catena.api.CatenaApi;
import org.threehook.catena.core.util.ByteUtils;
import uk.org.lidalia.sysoutslf4j.context.SysOutOverSLF4J;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.security.Security;

@SpringBootApplication
//@EnableLoadTimeWeaving(aspectjWeaving = EnableLoadTimeWeaving.AspectJWeaving.ENABLED)
@ComponentScan("org.threehook.catena")
public class Cli implements ApplicationRunner {

    @Value("${node.server.port}")
    private int nodeServerPort;

    @Autowired
    private CliHelper cliHelper;
    @Autowired
    private CatenaApi catenaService;

    /**
     * Main executable method.
     *
     * @param args Commmand-line arguments.
     */
    public static void main(String[] args) {
        final SpringApplication cli = new SpringApplication(Cli.class);
        cli.run(args);
    }

    @Override
    public void run(ApplicationArguments applicationArguments) throws Exception {
        SysOutOverSLF4J.sendSystemOutAndErrToSLF4J();
        Security.addProvider(new BouncyCastleProvider());
        displayHeader(System.out);
        displayBlankLines(2, System.out);
        parse(applicationArguments.getSourceArgs());
    }

    /**
     * Write the provided number of blank lines to the provided OutputStream.
     *
     * @param numberBlankLines Number of blank lines to write.
     * @param out              OutputStream to which to write the blank lines.
     */
    private static void displayBlankLines(final int numberBlankLines, final OutputStream out) {
        try {
            for (int i = 0; i < numberBlankLines; ++i) {
                out.write(ByteUtils.stringToBytes("\n"));
            }
        } catch (IOException ioEx) {
            for (int i = 0; i < numberBlankLines; ++i) {
                System.out.println();
            }
        }
    }

    /**
     * Display example application header.
     *
     * @out OutputStream to which header should be written.
     */
    private static void displayHeader(final OutputStream out) {
        final String header = "[Catena, a simple blockchain brought to you by Threehook]";
        try {
            out.write(ByteUtils.stringToBytes(header));
        } catch (IOException ioEx) {
            System.out.println(header);
        }
    }

    /**
     * Write "help" to the provided OutputStream.
     */
    private void printHelp(final Options options, final int printedRowWidth, final String header, final String footer, final int spacesBeforeOption,
                                  final int spacesBeforeOptionDescription, final boolean displayUsage, final OutputStream out) {
        final String commandLineSyntax = "java -jar " + cliHelper.getApplicationNameTech() + "-exec.jar";
        final PrintWriter writer = new PrintWriter(out);
        final HelpFormatter helpFormatter = new HelpFormatter();
        helpFormatter.printHelp(writer, printedRowWidth, commandLineSyntax, header, options, spacesBeforeOption, spacesBeforeOptionDescription, footer, displayUsage);
        writer.flush();
    }

    /**
     * Construct and provide Options.
     *
     * @return Options expected from command-line.
     */
    private static Options constructOptions() {
        final Options options = new Options();
        Option helpOption = Option.builder("help").longOpt("help").desc("Show help.").build();
        Option cbOption = Option.builder("cb").longOpt("createBlockchain").desc("Create/initialize a new blockchain.").numberOfArgs(1).argName("address").hasArgs().build();
        Option cwOption = Option.builder("cw").longOpt("createWallet").desc("Generates a new key-pair and saves it into the wallet file.").build();
        Option laOption = Option.builder("la").longOpt("listAddresses").desc("Lists all addresses from the wallet file.").build();
        Option gbOption = Option.builder("gb").longOpt("getBalance").desc("Get balance of address.").numberOfArgs(1).argName("address").hasArgs().build();
        Option pcOption = Option.builder("pc").longOpt("printChain").desc("Print all the blocks of the blockchain.").build();
        Option ruOption = Option.builder("ru").longOpt("reindexUtxo").desc("Rebuilds the UTXO set.").build();
        Option sdOption = Option.builder("sd").longOpt("send").desc("Send amount of coins from 'from' address to 'to' address. Mine (true|false) on the same node.").numberOfArgs(4).argName("from address> <to address> <amount> <mine").hasArgs().build();
        Option ssOption = Option.builder("sn").longOpt("startNode").desc("Start Catena server node with optional mining address to send reward to.").numberOfArgs(1).argName("address").hasArgs().build();
        options.addOption(helpOption);
        options.addOption(cbOption);
        options.addOption(cwOption);
        options.addOption(laOption);
        options.addOption(gbOption);
        options.addOption(pcOption);
        options.addOption(ruOption);
        options.addOption(sdOption);
        options.addOption(ssOption);
        return options;
    }

    /**
     * Apply Apache Commons CLI parser to command-line arguments.
     *
     * @param commandLineArguments Command-line arguments to be processed with Posix-style parser.
     */
    private void parse(final String[] commandLineArguments) {
        final CommandLineParser cmdLineParser = new DefaultParser();
        final Options options = constructOptions();
//        String nodeId = System.getenv("NODE_ID");
        CommandLine commandLine;
        try {
            commandLine = cmdLineParser.parse(options, commandLineArguments);
            if (commandLine.hasOption("help")) {
                printHelp(options, 180, null, null, 3, 5, false, System.out);
            }
            else if (commandLine.hasOption("createBlockchain")) {
                if (commandLineArguments.length != 2) {
                    Options cbOptions = new Options().addOption(options.getOption("cb"));
                    printHelp(cbOptions, 180, null, null, 3, 5, false, System.out);
                } else {
                    String address = commandLineArguments[1];
                    catenaService.createBlockchain(address);
                    System.out.println("Done!");
                }
            } else if (commandLine.hasOption("createWallet")) {
                if (commandLineArguments.length != 1) {
                    Options cbOptions = new Options().addOption(options.getOption("cw"));
                    printHelp(cbOptions, 180, null, null, 3, 5, false, System.out);
                } else {
                    String address = catenaService.createWallet();
                    System.out.printf("Your new address: %s\n", address);
                }
            } else if (commandLine.hasOption("listAddresses")) {
                if (commandLineArguments.length != 1) {
                    Options laOptions = new Options().addOption(options.getOption("la"));
                    printHelp(laOptions, 180, null, null, 3, 5, false, System.out);
                } else {
                    catenaService.listAddresses();
                }
            } else if (commandLine.hasOption("getBalance")) {
                if (commandLineArguments.length != 2) {
                    Options gbOptions = new Options().addOption(options.getOption("gb"));
                    printHelp(gbOptions, 180, null, null, 3, 5, false, System.out);
                } else {
                    String address = commandLineArguments[1];
                    int balance = catenaService.getBalance(address);
                    System.out.printf("Balance of '%s': %d\n", address, balance);
                }
            } else if (commandLine.hasOption("printChain")) {
                if (commandLineArguments.length != 1) {
                    Options pcOptions = new Options().addOption(options.getOption("pc"));
                    printHelp(pcOptions, 180, null, null, 3, 5, false, System.out);
                } else {
                    catenaService.printChain();
                }
            } else if (commandLine.hasOption("reindexUtxo")) {
                if (commandLineArguments.length != 1) {
                    Options ruOptions = new Options().addOption(options.getOption("ru"));
                    printHelp(ruOptions, 180, null, null, 3, 5, false, System.out);
                } else {
                    int transactionCount = catenaService.reindexUtxo();
                    System.out.printf("Done! There are %d transactions in the UTXO set.\n", transactionCount);
                }
            } else if (commandLine.hasOption("send")) {
                String address = null;
                if (commandLineArguments.length != 5) {
                    Options gbOptions = new Options().addOption(options.getOption("sd"));
                    printHelp(gbOptions, 180, null, null, 3, 5, false, System.out);
                } else {
                    String fromAddress = commandLineArguments[1];
                    String toAddress = commandLineArguments[2];
                    int amount = Integer.parseInt(commandLineArguments[3]);
                    boolean mineNow = new Boolean(commandLineArguments[4]);
                    catenaService.send(fromAddress, toAddress, amount, mineNow);
                }
            } else if (commandLine.hasOption("startNode")) {
                if (commandLineArguments.length < 1 || commandLineArguments.length > 2) {
                    Options gbOptions = new Options().addOption(options.getOption("sn"));
                    printHelp(gbOptions, 180, null, null, 3, 5, false, System.out);
                } else {
                    String minerAddress = commandLineArguments[1];
                    catenaService.startNode(minerAddress, nodeServerPort);
                }
            } else {
                printHelp(constructOptions(), 180, null, null, 3, 5, true, System.out);
            }
        } catch (ParseException parseException) { // checked exception
            System.err.println("Encountered exception while parsing:\n" + parseException.getMessage() + "\n");
            printHelp(constructOptions(), 180, null, null, 3, 5, true, System.out);
        }
    }
}
