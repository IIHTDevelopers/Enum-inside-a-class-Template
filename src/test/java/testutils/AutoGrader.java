package testutils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.EnumDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.TypeDeclaration;

public class AutoGrader {

	// Test if the code demonstrates proper enums and enum inside a class
	public boolean testEnumsAndEnumInsideClass(String filePath) throws IOException {
		System.out.println("Starting testEnumsAndEnumInsideClass with file: " + filePath);

		File participantFile = new File(filePath); // Path to participant's file
		if (!participantFile.exists()) {
			System.out.println("File does not exist at path: " + filePath);
			return false;
		}

		FileInputStream fileInputStream = new FileInputStream(participantFile);
		JavaParser javaParser = new JavaParser();
		CompilationUnit cu;
		try {
			cu = javaParser.parse(fileInputStream).getResult()
					.orElseThrow(() -> new IOException("Failed to parse the Java file"));
		} catch (IOException e) {
			System.out.println("Error parsing the file: " + e.getMessage());
			throw e;
		}

		System.out.println("Parsed the Java file successfully.");

		// Use AtomicBoolean to allow modifications inside lambda expressions
		AtomicBoolean enumFound = new AtomicBoolean(false);
		AtomicBoolean enumInsideClassFound = new AtomicBoolean(false);
		AtomicBoolean methodFound = new AtomicBoolean(false);
		AtomicBoolean methodExecutedInMain = new AtomicBoolean(false);

		// Check for enum and method implementations
		System.out.println("------ Enum and Method Check ------");
		for (TypeDeclaration<?> typeDecl : cu.findAll(TypeDeclaration.class)) {
			if (typeDecl instanceof EnumDeclaration) {
				EnumDeclaration enumDecl = (EnumDeclaration) typeDecl;

				// Check for the Day enum
				if (enumDecl.getNameAsString().equals("Day")) {
					System.out.println("Enum 'Day' found.");
					enumFound.set(true);
				}
			}

			if (typeDecl instanceof ClassOrInterfaceDeclaration) {
				ClassOrInterfaceDeclaration classDecl = (ClassOrInterfaceDeclaration) typeDecl;

				// Check for the WeekStatus enum inside WeekScheduler class
				if (classDecl.getNameAsString().equals("WeekScheduler")) {
					classDecl.getMembers().forEach(member -> {
						// Ensure it's an EnumDeclaration
						if (member instanceof EnumDeclaration) {
							EnumDeclaration enumDecl = (EnumDeclaration) member;
							if (enumDecl.getNameAsString().equals("WeekStatus")) {
								System.out.println("Enum 'WeekStatus' found inside class 'WeekScheduler'.");
								enumInsideClassFound.set(true);
							}
						}
					});

					// Check for getDayStatus method in the WeekScheduler class
					classDecl.getMethods().forEach(method -> {
						if (method.getNameAsString().equals("getDayStatus")) {
							methodFound.set(true);
							System.out.println("Method 'getDayStatus' found in 'WeekScheduler' class.");
						}
					});
				}
			}
		}

		// Ensure enum and method are correctly implemented
		if (!enumFound.get()) {
			System.out.println("Error: Enum 'Day' not found.");
			return false;
		}

		if (!enumInsideClassFound.get()) {
			System.out.println("Error: Enum 'WeekStatus' inside 'WeekScheduler' class not found.");
			return false;
		}

		if (!methodFound.get()) {
			System.out.println("Error: Method 'getDayStatus' not found in 'WeekScheduler' class.");
			return false;
		}

		// Check if the method is executed in the main method
		System.out.println("------ Method Execution Check in Main ------");

		AtomicBoolean processMessageExecuted = new AtomicBoolean(false);
		AtomicBoolean getDayStatusExecuted = new AtomicBoolean(false);

		for (MethodDeclaration method : cu.findAll(MethodDeclaration.class)) {
			if (method.getNameAsString().equals("main")) {
				if (method.getBody().isPresent()) {
					method.getBody().get().findAll(com.github.javaparser.ast.expr.MethodCallExpr.class)
							.forEach(callExpr -> {
								// Check if processMessage() is executed (if it exists)
								if (callExpr.getNameAsString().equals("processMessage")) {
									processMessageExecuted.set(true);
									System.out.println("Method 'processMessage' is executed in the main method.");
								}
								// Check if getDayStatus() is executed (if it exists)
								if (callExpr.getNameAsString().equals("getDayStatus")) {
									getDayStatusExecuted.set(true);
									System.out.println("Method 'getDayStatus' is executed in the main method.");
								}
							});
				}
			}
		}

		// Fail the test if neither method was executed
		if (!processMessageExecuted.get() && !getDayStatusExecuted.get()) {
			System.out
					.println("Error: Neither 'processMessage' nor 'getDayStatus' method executed in the main method.");
			return false;
		}

		// If all checks pass
		System.out.println("Test passed: Enums and enum inside class methods are correctly implemented and accessed.");
		return true;
	}
}
