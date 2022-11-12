package turing;

import java.util.*;

public class Tape {
	private Cell currentCell;

	Tape() {
		currentCell = new Cell(' ');
	}

	public Cell getCurrentCell() {
		return currentCell;
	}

	public char getContent() {
		return currentCell.getContent();
	}

	public void setContent(char content) {
		currentCell.setContent(content);
	}

	public void moveLeft() {
		if (currentCell.prev == null) {
			Cell cell = new Cell(' ');
			currentCell.prev = cell;
			cell.next = currentCell;
			currentCell = cell;
		} else {
			currentCell = currentCell.prev;
		}
	}

	public void moveRight() {
		if (currentCell.next == null) {
			Cell cell = new Cell(' ');
			currentCell.next = cell;
			cell.prev = currentCell;
			currentCell = cell;
		} else {
			currentCell = currentCell.next;
		}
	}

	public String getTapeContents() {
		String contents = "";
		Cell initial = new Cell();
		Cell end = new Cell();

		initial = currentCell;
		end = currentCell;
		while (initial.prev != null) {
			initial = initial.prev;
		}
		while (end.next != null) {
			end = end.next;
		}
		while (initial.getContent() == ' ') {
			initial = initial.next;
		}
		while (end.getContent() == ' ') {
			end = end.prev;
		}
		while (initial.next != end) {
			contents += initial.getContent();
			initial = initial.next;
		}
		contents += initial.getContent();
		initial = initial.next;
		contents += initial.getContent();

		return contents;
	}
}
