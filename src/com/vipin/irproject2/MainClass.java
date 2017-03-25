package com.vipin.irproject2;

import java.io.IOException;

/**
 * This Class is where magic begins :P But don't ask me the magic trick. It just
 * happens.
 * 
 * @author infinity
 *
 */
public class MainClass {
	public static void main(String[] args) {
		try {
			MasterClass masterClassObj = new MasterClass(args);
			masterClassObj.getIngredients();
			masterClassObj.cookRecipe();
			masterClassObj.serveRecipe();
			masterClassObj.cleanDishes();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
