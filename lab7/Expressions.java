package lab7;

import java.util.concurrent.TimeUnit;

/**
 * A class for experimenting with expression trees. This class includes a nested
 * abstract class and several subclasses that represent nodes in an expression
 * tree. It also includes several methods that work with these classes.
 */
public class Expressions {
	// Constants to identify each of the operations
	final static int POPULATION = 1000;
	final static int COS = 1;
	final static int SIN = 2;
	final static int ABS = 3;
	final static int EXP = 4;
	int noNodes = 0;

	/**
	 * The main routine tests some of the things that are defined in this class.
	 */
	public static void main(String[] args) {
		System.out.println("Testing expression creation and evaluation...\n");
		ExpNode e1 = new BinOpNode('+', new VariableNode(), new ConstNode(3));
		ExpNode e2 = new BinOpNode('*', new ConstNode(2), new VariableNode());
		ExpNode e3 = new BinOpNode('-', e1, e2);
		ExpNode e4 = new BinOpNode('/', e1, new ConstNode(-3));
		ExpNode e5 = new BinOpNode('+', e4, new UnaryOpNode(COS));
		System.out.println("For x = 3:");
		System.out.println("   " + e1 + " = " + e1.value(3));
		System.out.println("   " + e2 + " = " + e2.value(3));
		System.out.println("   " + e3 + " = " + e3.value(3));
		System.out.println("   " + e4 + " = " + e4.value(3));
		System.out.println("   " + e5 + " = " + e5.value(3));
		System.out.println("\nTesting copying...");
		System.out.println("   copy of " + e1 + " gives " + copy(e1));
		System.out.println("   copy of " + e2 + " gives " + copy(e2));
		System.out.println("   copy of " + e3 + " gives " + copy(e3));
		System.out.println("   copy of " + e4 + " gives " + copy(e4));
		System.out.println("   copy of " + e5 + " gives " + copy(e5));
		ExpNode e3copy = copy(e3); // make a copy of e3, where e3.left is e1
		((BinOpNode) e1).left = new ConstNode(17); // make a modification to e1
		System.out.println("   modified e3: " + e3 + "; copy should be unmodified: " + e3copy);
		System.out.println("\nChecking test data...");
		double[][] dt = makeTestData();
		for (int i = 0; i < dt.length; i++) {
			System.out.println("   x = " + dt[i][0] + "; y = " + dt[i][1]);
		}

		System.out.println("\nRandom expressions...\n");
		int numberOfExpressions = 10;
		int maxHeight = 6;
		ExpNode auxNode;
		for (int i = 0; i < numberOfExpressions; i++) {
			auxNode = randomExpression(maxHeight);
			System.out.println("Random expression " + (i + 1) + " : " + auxNode);
			System.out.println("\nRMSError :  " + RMSError(auxNode, dt));
		}

		System.out.println("\nRMSError test...");
		// for during 10 seconds
		double currentRMSError = 0.0;
		double bestRMSError = 0.0;
		ExpNode currentExpression = null;
		ExpNode bestExpression = null;
		int i = 0;

	/*	for (long stop = System.nanoTime() + TimeUnit.SECONDS.toNanos(10); stop > System.nanoTime(); i++) {
			currentExpression = randomExpression(maxHeight);
			currentRMSError = RMSError(currentExpression, dt);
			if (i == 0) {
				bestRMSError = currentRMSError;
				bestExpression = currentExpression;
			} else {
				if (currentRMSError < bestRMSError) {
					bestRMSError = currentRMSError;
					bestExpression = currentExpression;
				}
			}
		}*/

		System.out.println("\nBest Expression : " + bestExpression);
		System.out.println("RMSError : " + bestRMSError);

		System.out.println("\nGenetic programming... ");
		Individual[] individuals = new Individual[POPULATION * 3];
		double fitness;
		ExpNode exp;
		for (int k = 0; k < 1000; k++) {
			exp = randomExpression(10);
			fitness = RMSError(exp, dt);
			if (Double.isNaN(fitness) || Double.isInfinite(fitness)) {
				k--;
			} else {
				individuals[k] = new Individual(exp, fitness);
				System.out.println("\n\nExpression number " + k);
				System.out.println("Expression :  " + individuals[k].exp);
				System.out.println("RMS :  " + fitness);
			}
		}

	}

	// *********************review NaN and Infinite number/0 and
	// 0/0************************************************************
	static double RMSError(ExpNode f, double[][] sampleData) {
		double[][] expressionData = applyExpression(f);
		double average = 0.0;
		double result = 0.0;
		int n = expressionData.length;
		for (int i = 0; i < n; i++) {
			average += Math.pow((expressionData[i][1] - sampleData[i][1]), 2);
		}
		result = Math.sqrt(average / n);
		return result;
	}

	/*
	 * static ExpNode randomExpression(int maxHeight) { double random =
	 * Math.random(); if (maxHeight == 0) { if (Math.random() < 0.5) { return new
	 * ConstNode(Math.floor(Math.random() * (100.0 - 10.0 + 1) + 10.0)); } else {
	 * return new VariableNode(); } } else { if (random < 0.5) { return new
	 * BinOpNode(getRandomOperand(), randomExpression(maxHeight - 1),
	 * randomExpression(maxHeight - 1)); } else if (random < 0.75) { return new
	 * ConstNode(Math.floor(Math.random() * (100.0 - 10.0 + 1) + 10.0)); } else {
	 * return new VariableNode(); } } }
	 */
	static ExpNode randomExpression(int maxHeight) {
		double random = Math.random();
		if (maxHeight == 0) {
			if (random < 0.25) {
				return new ConstNode(Math.floor(Math.random() * (100.0 - 10.0 + 1) + 10.0));
			} else if (random < 0.5) {
				return new UnaryOpNode(getRandomFunction());
			} else {
				return new VariableNode();
			}
		} else {
			if (random < 0.5) {
				return new BinOpNode(getRandomOperand(), randomExpression(maxHeight - 1),
						randomExpression(maxHeight - 1));
			} else if (random < 0.6) {
				return new ConstNode(Math.floor(Math.random() * (100.0 - 10.0 + 1) + 10.0));
			} else if (random < 0.8) {
				return new UnaryOpNode(getRandomFunction());
			} else {
				return new VariableNode();
			}
		}
	}

	// choose a random operand
	static char getRandomOperand() {
		double rand = Math.random();
		if (rand < 0.25)
			return '+';
		else if (rand < 0.5)
			return '-';
		else if (rand < 0.75)
			return '*';
		else
			return '/';
	}

	// choose a random operation
	static int getRandomFunction() {
		double rand = Math.random();
		if (rand < 0.25)
			return COS;
		else if (rand < 0.5)
			return SIN;
		else if (rand < 0.75)
			return EXP;
		else
			return ABS;
	}

	static double[][] applyExpression(ExpNode expression) {
		double[][] data = new double[21][2];
		double xmax = 5;
		double xmin = -5;
		double dx = (xmax - xmin) / (data.length - 1);
		for (int i = 0; i < data.length; i++) {
			double x = xmin + dx * i;
			double y = expression.value(x);
			data[i][0] = x;
			data[i][1] = y;
		}
		return data;
	}

	/**
	 * Given an ExpNode that is the root of an expression tree, this method makes a
	 * full copy of the tree. The tree that is returned is constructed entirely of
	 * freshly made nodes. (That is, there are no pointers back into the old copy.)
	 */
	static ExpNode copy(ExpNode root) {
		if (root instanceof ConstNode)
			return new ConstNode(((ConstNode) root).number);
		else if (root instanceof VariableNode)
			return new VariableNode();
		else if (root instanceof UnaryOpNode)
			return new UnaryOpNode(((UnaryOpNode) root).op);
		else {
			BinOpNode node = (BinOpNode) root;
			// Note that left and right operand trees have to be COPIED,
			// not just referenced.
			return new BinOpNode(node.op, copy(node.left), copy(node.right));
		}
	}

	/**
	 * Returns an n-by-2 array containing sample input/output pairs. If the return
	 * value is called data, then data[i][0] is the i-th input (or x) value and
	 * data[i][1] is the corresponding output (or y) value. (This method is
	 * currently unused, except to test it.)
	 */
	static double[][] makeTestData() {
		double[][] data = new double[21][2];
		double xmax = 5;
		double xmin = -5;
		double dx = (xmax - xmin) / (data.length - 1);
		for (int i = 0; i < data.length; i++) {
			double x = xmin + dx * i;
			double y = 2.5 * x * x * x - x * x / 3 + 3 * x;
			data[i][0] = x;
			data[i][1] = y;
		}
		return data;
	}

	// ------------------- Definitions of Expression node classes ---------

	/**
	 * An abstract class that represents a general node in an expression tree. Every
	 * such node must be able to compute its own value at a given input value, x.
	 * Also, nodes should override the standard toString() method to return a fully
	 * parameterized string representation of the expression.
	 */
	static abstract class ExpNode {
		abstract double value(double x);
		// toString method should also be defined in each subclass
	}

	/**
	 * A node in an expression tree that represents a constant numerical value.
	 */
	static class ConstNode extends ExpNode {
		double number; // the number in this node.

		ConstNode(double number) {
			this.number = number;
		}

		double value(double x) {
			return number;
		}

		public String toString() {
			if (number < 0)
				return "(" + number + ")"; // add parentheses around negative number
			else
				return "" + number; // just convert the number to a string
		}

		public boolean equals(Object o) {
			return (o instanceof ConstNode) && ((ConstNode) o).number == number;
		}
	}

	/**
	 * A node in an expression tree that represents the variable x.
	 */
	static class VariableNode extends ExpNode {
		VariableNode() {
		}

		double value(double x) {
			return x;
		}

		public String toString() {
			return "x";
		}

		public boolean equals(Object o) {
			return (o instanceof VariableNode);
		}
	}

	/**
	 * A node in an expression tree that represents one of the binary operators +,
	 * -, *, or /.
	 */
	static class BinOpNode extends ExpNode {
		char op; // the operator, which must be '+', '-', '*', or '/'
		ExpNode left, right; // the expression trees for the left and right operands.

		BinOpNode(char op, ExpNode left, ExpNode right) {
			if (op != '+' && op != '-' && op != '*' && op != '/')
				throw new IllegalArgumentException("'" + op + "' is not a legal operator.");
			this.op = op;
			this.left = left;
			this.right = right;
		}

		double value(double x) {
			double a = left.value(x); // value of the left operand expression tree
			double b = right.value(x); // value of the right operand expression tree
			switch (op) {
			case '+':
				return a + b;
			case '-':
				return a - b;
			case '*':
				return a * b;
			default:
				return a / b;
			}
		}

		public String toString() {
			return "(" + left.toString() + op + right.toString() + ")";
		}

		public boolean equals(Object o) {
			return (o instanceof BinOpNode) && ((BinOpNode) o).op == op && ((BinOpNode) o).left.equals(left)
					&& ((BinOpNode) o).right.equals(right);
		}

	}

	static class UnaryOpNode extends ExpNode {
		int op;
		String stringOp;

		UnaryOpNode(int op) {
			this.op = op;
		}

		double value(double x) {
			switch (op) {
			case 1:
				x = Math.toRadians(x);
				return Math.cos(x);
			case 2:
				x = Math.toRadians(x);
				return Math.sin(x);
			case 3:
				return Math.abs(x);
			case 4:
				return Math.exp(x);
			default:
				return 0.0;
			}
		}

		public String toString() {
			switch (op) {
			case 1:
				stringOp = "cos";
				break;
			case 2:
				stringOp = "sin";
				break;
			case 3:
				stringOp = "abs";
				break;
			case 4:
				stringOp = "exp";
				break;
			default:
				break;
			}
			return stringOp + "(x)";
		}

		public boolean equals(Object o) {
			return (o instanceof UnaryOpNode) && ((UnaryOpNode) o).op == op;
		}
	}

	static class Individual {
		ExpNode exp;
		double fitness;

		Individual(ExpNode exp, double fitness) {
			this.exp = exp;
			this.fitness = fitness;
		}
	}

	static void quickSort(Individual[] array, int lo, int hi) {
		int mid = quickSortStep(array, lo, hi);
		if (mid - 1 > lo)
			quickSort(array, lo, mid - 1);
		if (mid + 1 < hi)
			quickSort(array, mid + 1, hi);
	}

	static int quickSortStep(Individual[] array, int lo, int hi) {
		Individual temp = array[lo];
		while (true) {
			while (hi > lo && array[hi].fitness > temp.fitness)
				hi--;
			if (hi == lo)
				break;
			array[lo] = array[hi];
			lo++;
			while (hi > lo && array[lo].fitness < temp.fitness)
				lo++;
			if (hi == lo)
				break;
			array[hi] = array[lo];
			hi--;
		}
		array[lo] = temp;
		return lo;
	}

	static void testMutate() {
		int changed = 0;
		int unchanged = 0;
		for (int i = 0; i < 100; i++) {
			ExpNode e = randomExpression(6);
			ExpNode f = copy(e);
			System.out.println(e);
			// mutate(f);
			System.out.println(f);
			System.out.println(f.equals(e) ? "equal" : "changed");
			System.out.println();
			if (f.equals(e))
				unchanged++;
			else
				changed++;
		}
		System.out.println(changed + " changed; " + unchanged + " unchanged");
	}

	static void testCrossover() {
		int changed1 = 0, changed2 = 0;
		for (int i = 0; i < 100; i++) {
			ExpNode e1 = randomExpression(6);
			ExpNode e2 = randomExpression(6);
			ExpNode f1 = copy(e1);
			ExpNode f2 = copy(e2);
			// cross(e1, e2);
			System.out.println(f1);
			System.out.println(f2);
			System.out.println(e1);
			System.out.println(e2);
			System.out.println();
			if (!e1.equals(f1))
				changed1++;
			if (!e2.equals(f2))
				changed2++;
		}
		System.out.println("crossover changed first  expreesion " + changed1 + " times");
		System.out.println("crossover changed second expreesion " + changed2 + " times");
	}
	
	static void mutate(ExpNode e) {
		//Choose random node in e 
		if(e instanceof ConstNode) {
			//choose random number 
		} else if(e instanceof BinOpNode) {
			//change the operand
		} else {
			return;
		}
	}
	
}
