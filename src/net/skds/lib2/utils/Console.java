package net.skds.lib2.utils;

import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.function.Function;

@SuppressWarnings("unused")
public class Console {

	private static final DecimalFormat percentForm = new DecimalFormat("00.0%");

	private final Scanner scanner;
	private final Map<String, Command> commands = new ConcurrentHashMap<>();
	private boolean started = false;
	private boolean basicInit = false;

	public Console() {
		this.scanner = new Scanner(System.in);
	}

	public void addBasicCommands() {
		if (basicInit) {
			return;
		}
		basicInit = true;

		regCommand("kill", arg -> System.exit(0), _ -> "kill java");
		regCommand("gc", arg -> gc(), _ -> "call System.gc()");
		regCommand("mem", arg -> mem(), _ -> "print memory state");
		regCommand("dump", arg -> SKDSUtils.dumpHeap("heap.hprof"), _ -> "save dump to heap.hprof");
		regCommand("help", this::help, arg -> "prints help command with args");
	}

	private void gc() {
		System.out.println("Performing gc...");
		System.gc();
		mem();
	}

	private void mem() {
		Runtime runtime = Runtime.getRuntime();
		long max = runtime.maxMemory();
		long free = runtime.freeMemory();
		long allocated = runtime.totalMemory();
		long used = allocated - free;
		float allocatedP = (float) allocated / max;
		float usedP = (float) used / max;
		System.out.println("=========== Memory ===========");
		System.out.println("Total:     " + SKDSUtils.memoryCompact(max));
		System.out.println("Allocated: " + percentForm.format(allocatedP) + "   " + SKDSUtils.memoryCompact(allocated));
		System.out.println("Used:      " + percentForm.format(usedP) + "   " + SKDSUtils.memoryCompact(used));
		System.out.println("========= Memory end =========");
	}

	private void help(String[] arg) {
		if (arg.length == 1) {
			System.out.println(this.commands.keySet());
		} else {
			Command command = this.commands.get(arg[1]);
			if (command == null) {
				System.err.println("cant find command \"" + arg[1] + "\"");
			} else {
				if (command.help == null) {
					System.err.println("command \"" + arg[1] + "\" has no help");
				} else {
					String help = command.help.apply(arg);
					if (help == null) {
						System.err.println("command \"" + arg[1] + "\" with args:\n" + Arrays.toString(arg) + "\n has incorrect result");
					} else {
						System.out.println("help \"" + arg[1] + "\" -> " + help);
					}
				}
			}
		}
	}

	public void start() {
		if (started) {
			throw new RuntimeException("This console has been already started!");
		}
		started = true;
		ThreadUtils.runNewThreadMainGroupDaemon(() -> {
			while (true) {
				try {
					processLine(scanner.nextLine());
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}, "Console");

	}

	private void processLine(String line) {
		String[] args = line.split(" ");
		if (args.length > 0) {
			Command c = commands.get(args[0]);
			if (c != null) {
				c.action.accept(args);
				return;
			}
		}
		System.out.println("Unknown command \"" + line + "\"");
	}

	public void regCommand(String name, Consumer<String[]> action) {
		regCommand(name, action, null);
	}

	public void regCommand(String name, Consumer<String[]> action, Function<String[], String> help) {
		Command command = new Command(name, action, help);
		commands.put(name, command);
	}

	private record Command(String name, Consumer<String[]> action, Function<String[], String> help) {

	}

}
