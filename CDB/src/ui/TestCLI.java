package ui;

import java.sql.SQLException;
import java.util.Scanner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import service.ActionCLI;

public class TestCLI {
	
	static int choice;
	
	
	public static void main(String[] args) throws SQLException {
		Logger logger = LoggerFactory.getLogger(TestCLI.class);
	    logger.info("TestCLI");
	    
	    ActionCLI actionCLI = new ActionCLI();
	    
		while(true) {
			System.out.println("Welcome in our Command Line Interface (CLI)");
			System.out.println("\tFeatured by OXYL");
			System.out.println("\n1- Computer List");
			System.out.println("2- Company List");
			System.out.println("3- Computer Detail");
			System.out.println("4- Create Computer");
			System.out.println("5- Update Computer");
			System.out.println("6- Delete Computer");
			System.out.println("\nChoose your option:");
	
			Scanner scan = new Scanner(System.in);
			
			try {
				choice = scan.nextInt();

			}
			catch(Exception e){
				choice=-1;
			}
			
			EnumChoice test = EnumChoice.testChoice(choice);
			
			switch(test) {
				case QUIT:
					actionCLI.stopSystem();
					
				case COMPUTER_LIST:
					actionCLI.listComputer(scan);
					break;
					
				case COMPANY_LIST:
					actionCLI.listCompany(scan);
					break;
					
				case COMPUTER_DETAIL:
					actionCLI.computerGetDetail(scan);
					break;
					
				case CREATE_COMPUTER:
					actionCLI.computerCreation(scan);
					break;
					
				case UPDATE_COMPUTER:
					actionCLI.computerUpdate(scan);
					break;
					
				case DELETE_COMPUTER:
					actionCLI.computerDelition(scan);
					break;
					
				case ERROR:
					System.out.println("Invalide Entry\n");
				default:
					
			}
		}


	}

}
