package com.nizo.board;

import com.nizo.board.ui.MainMenu;
import java.sql.SQLException;

public class BoardApplication {

	public static void main(String[] args) throws SQLException{
		new MainMenu().execute();
	}

}
