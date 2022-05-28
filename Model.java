import java.sql.*;
import java.util.Scanner;

public class Model {

	private static Connection getCon() throws SQLException {
		return DriverManager.getConnection("jdbc:postgresql://10.62.73.22:5432/l3d9?user=l3d9&password=cryptogods&ssl=false");
	}
	public static void RegisterBookMaker(){
		final String CMDST = "INSERT INTO casa_apostas(id,nome, NIPC, aposta_minima)" +
				"VALUES (?, ?, ?, ?::decimal)";

		Connection conn = null;
		PreparedStatement pstmt = null;
		PreparedStatement pstmtquery = null;
		ResultSet result = null;

		try {
			conn = getCon();
			pstmt = conn.prepareStatement(CMDST);
			System.out.println("INSERT: id,nome, NIPC, aposta_minima");
			java.util.Scanner key = new Scanner(System.in);
			System.out.println("id");
			pstmt.setString(1, key.nextLine());
			System.out.println("nome");
			pstmt.setString(2, key.nextLine());
			System.out.println("NIPC");
			pstmt.setString(3, key.nextLine());
			System.out.println("aposta_minima");
			float aposta_minima = key.nextFloat();
			pstmt.setFloat(4, key.nextFloat());
			if(aposta_minima <= 0.05) {
				System.out.println("Aposta minima must be greater than 0.05");
				return;
			}
		} catch (Exception err) {
			System.out.println(err);
			//Nothing to do. The option was not a valid one. Read another.
		} finally {
			try {
				if (conn != null) conn.close();
				if (pstmt != null) pstmt.close();
				if (pstmtquery != null) pstmtquery.close();
				if (result != null) result.close();
			} catch (SQLException ignored) {
			}
		}
	}
}