package org.threehook.catena;

import org.threehook.catena.block.Block;
import org.threehook.catena.transaction.Transaction;
import org.threehook.catena.transaction.TransactionFactory;
import org.threehook.catena.transaction.TransactionOutput;
import org.threehook.catena.util.Base58;
import org.threehook.catena.util.ByteUtils;
import org.threehook.catena.wallet.Wallet;
import org.threehook.catena.wallet.Wallets;
import org.apache.commons.cli.*;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.security.Security;
import java.util.Arrays;
import java.util.List;

public class Main {

    /**
     * Main executable method.
     *
     * @param commandLineArguments Commmand-line arguments.
     */
    public static void main(final String[] commandLineArguments) {
        final String applicationName = "Catena";
        Security.addProvider(new BouncyCastleProvider());
//        displayBlankLines(1, System.out);
        displayHeader(System.out);
        displayBlankLines(2, System.out);
//        if (commandLineArguments.length < 1) {
//            System.out.println("-- USAGE --");
//            printUsage(applicationName + " (Posix)", constructPosixOptions(), System.out);
//            displayBlankLines(1, System.out);
//            printUsage(applicationName + " (Gnu)", constructGnuOptions(), System.out);

//            displayBlankLines(4, System.out);

//            System.out.println("-- HELP --");
//            printHelp(constructOptions(), 80, null, null, 3, 5, true, System.out);
//            displayBlankLines(1, System.out);
//            printHelp(constructGnuOptions(), 80, "GNU HELP", "End of GNU Help", 5, 3, true, System.out);
//        }
//        displayProvidedCommandLineArguments(commandLineArguments, System.out);
        parse(commandLineArguments);
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
     * Print usage information to provided OutputStream.
     *
     * @param applicationName Name of application to list in usage.
     * @param options         Command-line options to be part of usage.
     * @param out             OutputStream to which to write the usage information.
     */
    public static void printUsage(final String applicationName, final Options options, final OutputStream out) {
        final PrintWriter writer = new PrintWriter(out);
        final HelpFormatter usageFormatter = new HelpFormatter();
        usageFormatter.printUsage(writer, 80, applicationName, options);
        writer.flush();
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
    private static void printHelp(final Options options, final int printedRowWidth, final String header, final String footer, final int spacesBeforeOption,
                                 final int spacesBeforeOptionDescription, final boolean displayUsage, final OutputStream out) {
        final String commandLineSyntax = "java -cp ApacheCommonsCLI.jar";
        final PrintWriter writer = new PrintWriter(out);
        final HelpFormatter helpFormatter = new HelpFormatter();
        helpFormatter.printHelp(writer, printedRowWidth, commandLineSyntax, header, options, spacesBeforeOption, spacesBeforeOptionDescription,
                footer, displayUsage);
        writer.flush();
    }

    /**
     * Construct and provide Options.
     *
     * @return Options expected from command-line.
     */
    private static Options constructOptions() {
        final Options options = new Options();
        Option cbOption = Option.builder("cb").longOpt("createBlockchain").desc( "Create/initialize a new blockchain.").numberOfArgs(1).argName("address").hasArgs().build();
        Option cwOption = Option.builder("cw").longOpt("createWallet").desc( "Generates a new key-pair and saves it into the wallet file.").build();
        Option laOption = Option.builder("la").longOpt("listAddresses").desc( "Lists all addresses from the wallet file.").build();
        Option gbOption = Option.builder("gb").longOpt("getBalance").desc( "Get balance of address.").numberOfArgs(1).argName("address").hasArgs().build();
        Option sdOption = Option.builder("sd").longOpt("send").desc( "Send amount of coins from 'from' address to 'to' address. Optionally mine on the same node.").numberOfArgs(4).argName("from address> <to address> <amount> <mine").hasArgs().build();
        options.addOption(cbOption);
        options.addOption(cwOption);
        options.addOption(laOption);
        options.addOption(gbOption);
        options.addOption(sdOption);
        return options;
    }

//    /**
//     * Display command-line arguments without processing them in any further way.
//     *
//     * @param commandLineArguments Command-line arguments to be displayed.
//     */
//    public static void displayProvidedCommandLineArguments(final String[] commandLineArguments, final OutputStream out) {
//        final StringBuffer buffer = new StringBuffer();
//        for (final String argument : commandLineArguments) {
//            buffer.append(argument).append(" ");
//        }
//        try {
//            out.write((buffer.toString() + "\n").getBytes());
//        } catch (IOException ioEx) {
//            System.err.println("WARNING: Exception encountered trying to write to OutputStream:\n" + ioEx.getMessage());
//            System.out.println(buffer.toString());
//        }
//    }

    /**
     * Apply Apache Commons CLI parser to command-line arguments.
     *
     * @param commandLineArguments Command-line arguments to be processed with Posix-style parser.
     */
    private static void parse(final String[] commandLineArguments) {
        final CommandLineParser cmdLineParser = new DefaultParser();
        final Options options = constructOptions();
        CommandLine commandLine;
        try {
            commandLine = cmdLineParser.parse(options, commandLineArguments);
            if (commandLine.hasOption("createBlockchain")) {
                if (commandLineArguments.length < 2) {
                    Options cbOptions = new Options().addOption(options.getOption("cb"));
                    printHelp(cbOptions, 120, null, null, 3, 5, false, System.out);
                } else {
                    System.out.println("Address is: " + commandLineArguments[1]);
                    System.out.println("NodeId is: " + System.getenv("NODE_ID"));
                    Blockchain bc = BlockchainFactory.createBlockchain(commandLineArguments[1], System.getenv("NODE_ID"));
                    UTXOSet utxoSet = new UTXOSet(bc);
                    utxoSet.reindex();
                    System.out.println("Done!");
                }
            }
            else if (commandLine.hasOption("createWallet")) {
                if (commandLineArguments.length < 1) {
                    Options cbOptions = new Options().addOption(options.getOption("cw"));
                    printHelp(cbOptions, 120, null, null, 3, 5, false, System.out);
                } else {
                    System.out.println("NodeId is: " + System.getenv("NODE_ID"));
                    Wallets wallets = new Wallets(System.getenv("NODE_ID"));
                    String address = wallets.createWallet();
                    wallets.saveToFile(System.getenv("NODE_ID"));
                    System.out.printf("Your new address: %s\n", address);
                }

            }
            else if (commandLine.hasOption("listAddresses")) {
                if (commandLineArguments.length < 1) {
                    Options laOptions = new Options().addOption(options.getOption("la"));
                    printHelp(laOptions, 120, null, null, 3, 5, false, System.out);
                } else {
                    System.out.println("NodeId is: " + System.getenv("NODE_ID"));
                    Wallets wallets = new Wallets(System.getenv("NODE_ID"));
                    for (String address : wallets.getAddresses()) {
                        System.out.println(address);
                    }
                }
            }
            else if (commandLine.hasOption("getBalance")) {
                String address = null;
                int balance = 0;
                if (commandLineArguments.length < 2) {
                    Options gbOptions = new Options().addOption(options.getOption("gb"));
                    printHelp(gbOptions, 120, null, null, 3, 5, false, System.out);
                } else {
                    System.out.println("NodeId is: " + System.getenv("NODE_ID"));
                    address = commandLineArguments[1];
                    Blockchain blockchain = BlockchainFactory.getBlockchain(System.getenv("NODE_ID"));
                    UTXOSet utxoSet = new UTXOSet(blockchain);
                    byte[] pubKeyHash = Base58.decode(address);

                    //index   0   1   2   3   4
//                    int[] arr = {10, 20, 30, 40, 50};
//                    Arrays.copyOfRange(arr, 0, 2);          // returns {10, 20}
//                    Arrays.copyOfRange(arr, 1, 4);          // returns {20, 30, 40}
//                    Arrays.copyOfRange(arr, 2, arr.length); // returns {30, 40, 50} (length = 5)

                    pubKeyHash = Arrays.copyOfRange(pubKeyHash, 1, pubKeyHash.length-4);
                    List<TransactionOutput> utxos = utxoSet.findUTXO(pubKeyHash);

                    for (TransactionOutput utxo : utxos) {
                        balance += utxo.getValue();
                    }
                }
                System.out.printf("Balance of '%s': %d\n", address, balance);
            }
            else if (commandLine.hasOption("send")) {
                String address = null;
                if (commandLineArguments.length < 5) {
                    Options gbOptions = new Options().addOption(options.getOption("sd"));
                    printHelp(gbOptions, 120, null, null, 3, 5, false, System.out);
                } else {
                    System.out.println("NodeId is: " + System.getenv("NODE_ID"));
                    String fromAddress = commandLineArguments[1];
                    String toAddress = commandLineArguments[2];
                    int amount = Integer.parseInt(commandLineArguments[3]);
                    boolean mineNow = new Boolean(commandLineArguments[4]);
                    if (!Wallet.validateAddress(fromAddress)) {
                        System.out.println("ERROR: Sender address is not valid");
                    }
                    if (!Wallet.validateAddress(toAddress)) {
                        System.out.println("ERROR: Recipient address is not valid");
                    }
                    Blockchain blockchain = BlockchainFactory.getBlockchain(System.getenv("NODE_ID"));
                    UTXOSet utxoSet = new UTXOSet(blockchain);
                    Wallets wallets = new Wallets(System.getenv("NODE_ID"));
                    Wallet wallet = wallets.getWallet(fromAddress);

                    Transaction tx = TransactionFactory.createUTXOTransaction(wallet, toAddress, amount, utxoSet);
                    if (mineNow) {
                        Transaction cbtx = TransactionFactory.createCoinBaseTransaction(fromAddress, "");
                        Transaction[] txs = new Transaction[] {cbtx, tx};
                        Block newBlock = blockchain.mineBlock(txs);
                        utxoSet.update(newBlock);
                    } else {
                        //TODO
                        //server.sendTx(server.getKnownNodes[0], tx);
                    }
                }
            }
        } catch (ParseException parseException) { // checked exception
            System.err.println("Encountered exception while parsing:\n" + parseException.getMessage() + "\n");
            printHelp(constructOptions(), 120, null, null, 3, 5, true, System.out);
        }
    }
}
