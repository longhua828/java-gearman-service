package org.gearman.impl;

import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;

import org.gearman.Gearman;
import org.gearman.impl.util.ArgumentParser;
import org.gearman.impl.util.ArgumentParser.Option;
import org.gearman.properties.GearmanProperties;

/**
 * The class that starts the standalone server
 * @author isaiah.v
 */
class Main {
	
	private static String HELP = 
		GearmanConstants.PROJECT_NAME + " v" + GearmanConstants.VERSION + "\n" +
		"\n" +
		"usage:\n" +
		"java [jvm options] -jar "+GearmanConstants.PROJECT_NAME+ "-" +GearmanConstants.VERSION+".jar [server options]\n" +
		"\n" +
		"Options:\n" +
		"   -p PORT   --port=PORT     Defines what port number the server will listen on (Default: 4730)\n" +
		"   -w        --writefile     Write the gearman.properties file to the current working directory and exit" +
		"   -v        --version       Display the version of java gearman service and exit\n" +
		"   -h        --help          Print this help menu and exit";
	
	/**
	 * Prints the current version and 
	 * @param out
	 */
	private static final void printHelp(PrintStream out) {
		out.println(HELP);
		System.exit(0);
	}
	
	private static final void printVersion() {
		System.out.println(GearmanConstants.VERSION);
		System.exit(0);
	}
	
	/**
	 * Starts the standalone gearman job server.
	 * @throws IOException 
	 */
	public static void main(final String[] args) {
		try {
			Main main = new Main(args);
			
			// Create a gearman instance
			Gearman gearman = Gearman.createGearman();
			
			// Create the server and listen on the given port
			gearman.startGearmanServer(main.getPort());
			
		} catch (Throwable th) {
			System.err.println(th.getMessage());
			System.err.println();
			printHelp(System.err);
		}
	}
	
	private int port = GearmanConstants.PORT;
	
	private Main(final String[] args) {
		final ArgumentParser ap = new ArgumentParser();
		
		boolean t1, t2, t3, t4;
		t1 = ap.addOption('p', "port", true);
		t2 = ap.addOption('v', "version", false);
		t3 = ap.addOption('w', "writefile", false);
		t4 = ap.addOption('h', "help", false);
		
		assert t1&&t2&&t3&&t4;
		
		ArrayList<String> arguments = ap.parse(args);
		if(arguments==null) {
			System.err.println("argument parsing failed");
			System.err.println();
			printHelp(System.err);
		} else if(!arguments.isEmpty()) {
			System.err.print("received unexpected arguments:");
			for(String arg : arguments) {
				System.err.print(" "+arg);
			}
			System.err.println('\n');
			printHelp(System.err);
		}
		
		for(Option op : ap) {
			switch(op.getShortName()) {
			case 'p':
				try {
					this.port = Integer.parseInt(op.getValue());
				} catch(NumberFormatException nfe) {
					System.err.println("failed to parse port to integer: "+op.getValue());
					System.err.println();
					printHelp(System.err);
				}
				break;
			case 'w':
				writefile();
			case 'v':
				printVersion();
			case 'h':
				printHelp(System.out);
			}
		}
	}
	
	private void writefile() {
		try {
			GearmanProperties.save();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		System.exit(0);
	}
	
	private int getPort() {
		return this.port;
	}
}
