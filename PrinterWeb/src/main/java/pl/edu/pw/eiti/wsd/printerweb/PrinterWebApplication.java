package pl.edu.pw.eiti.wsd.printerweb;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import jade.Boot;
import pl.edu.pw.eiti.wsd.printerweb.printer.PrinterAgent;
import pl.edu.pw.eiti.wsd.printerweb.user.UserAgent;

public class PrinterWebApplication {

	public static void main(String[] args) {
		String[] jadeArgs = new String[] {"-gui", "userAgent:" + PrinterAgent.class.getName()};
		Boot.main(jadeArgs);
	}
}
