import java.sql.*;
import java.util.*;

public class Model {
	
	static Scanner in = new Scanner(System.in);
	
	private static Connection getCon() throws SQLException {
		return DriverManager.getConnection("jdbc:postgresql://10.62.73.22:5432/?user=l3n4&password=isigods");
	}
	
	public static void newBetHouse() {
		
	}

	public static void newPlayerAtBetHouse() {
	}
	
	public static void newPlayerBet() {

	}

	public static void suspendPlayer() {

		// Retornar os jogadores com estador ativo
		final String jogadoresAtivosQuerie = "select id,nome from jogador where estado='activo'";
		final String update = "UPDATE jogador SET estado='suspenso' WHERE id=?";

		Connection conn = null;
		PreparedStatement pstmt = null;
		PreparedStatement pstmtquery = null;
		ResultSet result = null;

		try {

			conn = getCon();
			pstmtquery = conn.createStatement();
			result = pstmt.executeQuery(jogadoresAtivosQuerie);
			ResultSetMetaData md = result.getMetaData();
			int columns = md.getColumnCount();
			printTable(result, columns);

			pstmt = null;
			pstmtquery = null;
			result = null;

			pstmtquery = conn.createStatement();
			pstmt = conn.prepareStatement(update);
			System.out.println("Id do utilizador que pretende remover?");
			System.out.print("id: ");
			pstmt.setInt(1, in.nextInt());
			pstmt.execute();

		} catch (Exception err) {
			System.out.println(err);
			// Nothing to do. The option was not a valid one. Read another.
		} finally {
			try {
				if (conn != null)
					conn.close();
				if (pstmt != null)
					pstmt.close();
				if (pstmtquery != null)
					pstmtquery.close();
				if (result != null)
					result.close();
			} catch (SQLException ignored) {
			}
		}
	}

	public static void totalPlayersInBetHouse() {
		// Querie para mostrar casas de apostas
		final String getBetHousesQuerie = "SELECT id,nome FROM casa_apostas";
		// Querie para contar os jogadores numa dada casa de apostas
		final String countQuerie = "SELECT count(casa_apostas) FROM jogador WHERE casa_apostas=?";

		Connection conn = null;
		PreparedStatement pstmt = null;
		PreparedStatement pstmtquery = null;
		ResultSet result = null;

		try {

			conn = getCon();
			pstmtquery = conn.createStatement();
			result = pstmt.executeQuery(getBetHousesQuerie);
			ResultSetMetaData md = result.getMetaData();
			int columns = md.getColumnCount();
			printTable(result, columns);

			pstmt = null;
			pstmtquery = null;
			result = null;

			pstmtquery = conn.createStatement();
			pstmt = conn.prepareStatement(countQuerie);
			System.out.println("Id da Casa de Apostas que pretende ter o nº de utilizadores?");
			System.out.print("Id: ");
			pstmt.setInt(1, in.nextInt());
			pstmt.execute();
			System.out.println("\nNúmero de Jogadores nessa Casa de Apostas = ");
			System.out.print(result.getInt(1));

		} catch (Exception err) {
			System.out.println(err);
			// Nothing to do. The option was not a valid one. Read another.
		} finally {
			try {
				if (conn != null)
					conn.close();
				if (pstmt != null)
					pstmt.close();
				if (pstmtquery != null)
					pstmtquery.close();
				if (result != null)
					result.close();
			} catch (SQLException ignored) {
			}
		}
	}

	public static void insertBetResolution() {
	}

	public static void showPlayersBets() {
			// Querie para mostrar as Apostas de um dado Jogador
			final String getPlayersBetsQuerie = "select distinct  t.numero as Aposta_Num,tipo, odd, descricao FROM aposta as a JOIN transacao as t ON (a.transacao = t.numero) WHERE t.jogador IN(SELECT id FROM jogador j WHERE j.nome = ?) GROUP BY t.numero, a.tipo, a .odd, a.descricao";

			Connection conn = null;
			PreparedStatement pstmt = null;
			PreparedStatement pstmtquery = null;
			ResultSet result = null;
	
			try {
				conn = getCon();
				pstmtquery = conn.createStatement();
				pstmt = pstmt.prepareStatement(getPlayersBetsQuerie);
				System.out.println("Qual é o Nome do Jogador do qual quer ver as Apostas?");
				System.out.print("Nome: ");
				pstmt.setString(1, in.nextLine());
				pstmt.execute();

				ResultSetMetaData md = result.getMetaData();
				int columns = md.getColumnCount();
				printTable(result, columns);
	
			} catch (Exception err) {
				System.out.println(err);
				// Nothing to do. The option was not a valid one. Read another.
			} finally {
				try {
					if (conn != null)
						conn.close();
					if (pstmt != null)
						pstmt.close();
					if (pstmtquery != null)
						pstmtquery.close();
					if (result != null)
						result.close();
				} catch (SQLException ignored) {
				}
			}
	}

	public static void exit() {
	}

	public static void printTable(ResultSet rs, int columnsNumber) throws SQLException {
		while (rs.next()) {
			// Print one row
			for (int i = 1; i <= columnsNumber; i++) {
				System.out.print(rs.getString(i) + " "); // Print one element of a row
			}
		}
	}

}