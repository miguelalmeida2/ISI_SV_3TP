
import java.sql.*;
import java.util.*;

public class Model {

	static Scanner in = new Scanner(System.in);

	private static Connection getCon() throws SQLException {
		String jdbcURL = "jdbc:postgresql://10.62.73.22:5432/l3n4";
		String username = "l3n4";
		String password = "isigods";
		return DriverManager.getConnection(jdbcURL, username, password);
	}

	public static void newBetHouse() {
		final String CMDST = "INSERT INTO casa_apostas(id,nome, NIPC, aposta_minima)" +
				"VALUES (?, ?, ?, ?::decimal)";

		Connection conn = null;
		Statement stmt = null;
		PreparedStatement pstmt = null;
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
			if (aposta_minima <= 0.05) {
				System.out.println("Aposta minima must be greater than 0.05");
				key.close();
				return;
			}
			pstmt.executeQuery();
			key.close();

		} catch (Exception err) {
			System.out.println(err);
			// Nothing to do. The option was not a valid one. Read another.
		} finally {
			try {
				if (conn != null)
					conn.close();
				if (stmt != null)
					pstmt.close();
				if (pstmt != null)
					pstmt.close();
				if (result != null)
					result.close();
			} catch (SQLException ignored) {
			}
		}
	}

	public static void newPlayerAtBetHouse() {
		final String CMDQuery = "SELECT id FROM casa_apostas WHERE id = ?";
		final String CMDST = "INSERT INTO jogador(id, email, nome, nickname, estado, data_nascimento, data_registo, morada, codigo_postal, localidade, casa_apostas)"
				+
				"VALUES (?, ?, ?, ?, ?, ?::date, ?::date, ?, ?, ?, ?)";

		Connection conn = null;
		Statement stmt = null;
		PreparedStatement pstmt = null;
		ResultSet result = null;

		try {
			conn = getCon();
			pstmt = conn.prepareStatement(CMDST);
			System.out.println(
					"INSERT: id, email, nome, nickname, estado, data_nascimento, data_registo, morada, codigo_postal, localidade, casa_apostas");
			java.util.Scanner key = new Scanner(System.in);
			System.out.println("id");
			pstmt.setString(1, key.nextLine());
			System.out.println("email");
			pstmt.setString(2, key.nextLine());
			System.out.println("nome");
			pstmt.setString(3, key.nextLine());
			System.out.println("nickname");
			pstmt.setString(4, key.nextLine());
			System.out.println("estado");
			String estado = key.nextLine();
			if (!estado.matches("ativo|suspenso|autoexcluído")) {
				key.close();
				throw new Exception("Estado must be ativo or suspenso or autoexcluído");
			}
			pstmt.setString(5, estado);
			System.out.println("data_nascimento");
			// String data_nascimento = key.nextLine();
			pstmt.setString(6, key.nextLine());
			System.out.println("data_registo");
			pstmt.setString(7, key.nextLine());
			System.out.println("morada");
			pstmt.setString(8, key.nextLine());
			System.out.println("codigo_postal");
			pstmt.setString(9, key.nextLine());
			System.out.println("localidade");
			pstmt.setString(10, key.nextLine());
			System.out.println("casa_apostas");
			String casa_apostas = key.nextLine();

			/*
			 * pstmtquery = conn.prepareStatement(CMDQuery)
			 * pstmtquery.setString(1,casa_apostas)
			 * result = pstmtquery.executeQuery()
			 * result.next();
			 * if( result.getString("id").isEmpty()){
			 * throw new Exception("That casa_apostas dosen't exist")
			 * }
			 */
			pstmt.setString(11, casa_apostas);
			pstmt.executeQuery();
			key.close();

		} catch (Exception err) {
			System.out.println(err);
			// Nothing to do. The option was not a valid one. Read another.
		} finally {
			try {
				if (conn != null)
					conn.close();
				if (pstmt != null)
					pstmt.close();
				if (pstmt != null)
					pstmt.close();
				if (result != null)
					result.close();
			} catch (SQLException ignored) {
			}
		}
	}

	public static void newPlayerBet() {
		final String CMDQuery_casa_apostas = "SELECT aposta_minima FROM casa_apostas WHERE id = ?";
		final String CMDQuerySaldo = "SELECT   coalesce(jogador.id,levantamento.jogador, deposito.jogador, aposta.jogador, resultado.jogador) as Jogador_Id,  coalesce(max(deposito.depo), 0.0) - coalesce(max(levantamento.leva), 0.0) - coalesce(max(aposta.apos), 0.0) + coalesce(max(resultado.res), 0.0)  as saldo \n"
				+
				"from\t(select id from jogador where estado = 'activo' and id = ?) as jogador\n" +
				"\t\tleft outer join(\n" +
				"\t\tSELECT t.jogador,SUM(t.valor) as leva FROM transacao t JOIN bancaria b ON (t.numero = b.transacao) WHERE b.operacao = 'levantamento' group by t.jogador order by t.jogador) as levantamento\n"
				+
				"\t\ton( jogador.id = levantamento.jogador)\n" +
				"\t\tfull outer join( \n" +
				"\t\tSELECT   t.jogador ,SUM(t.valor) as depo FROM transacao t JOIN bancaria b2 ON (t.numero = b2.transacao)  WHERE b2.operacao = 'depósito' group by t.jogador order by t.jogador) as deposito\n"
				+
				"\t\ton (greatest (jogador.id,levantamento.jogador) = deposito.jogador) \n" +
				"\t\tfull outer join( \n" +
				"\t\tSELECT  t.jogador,SUM(valor) as apos FROM transacao t JOIN aposta a ON (t.numero = a.transacao) group by t.jogador order by t.jogador)  as aposta\n"
				+
				"\t\ton ( greatest(levantamento.jogador,jogador.id, deposito.jogador) = aposta.jogador)\n" +
				"\t\tfull outer join(\n" +
				"\t\tSELECT  t.jogador, SUM(r.valor) as res FROM transacao t JOIN resolucao r ON (t.numero = r.aposta and r.resultado = 'vitória') group by t.jogador order by t.jogador) as resultado\n"
				+
				"\t\ton(greatest(jogador.id,deposito.jogador,levantamento.jogador,aposta.jogador) = resultado.jogador)\n"
				+
				"\t\tgroup by  jogador.id, levantamento.jogador, deposito.jogador , aposta.jogador, resultado.jogador\n"
				+
				"\t\torder by  jogador.id, levantamento.jogador, deposito.jogador , aposta.jogador, resultado.jogador asc \n"
				+
				"\t\tlimit 1";

		final String CMDST_Insert = "BEGIN transaction;\n " +
				"INSERT INTO transacao(numero, valor, data_transacao, casa_apostas, jogador)\n" +
				"VALUES((SELECT numero\n" +
				"  FROM transacao \n" +
				"  ORDER BY numero DESC LIMIT 1)+1, ?, current_date, ?, ?);  \n" +
				"  \n" +
				"INSERT INTO aposta(transacao,tipo,odd,descricao)\n" +
				"VALUES((\n" +
				"  SELECT numero\n" +
				"  FROM transacao \n" +
				"  ORDER BY numero DESC LIMIT 1), ?, ?, ?)\n" +
				"COMMIT TRANSACTION;\n" +
				"ROLLBACK TRANSACTION;";

		Connection conn = null;
		PreparedStatement pstmt = null;
		PreparedStatement pstmtquery = null;
		PreparedStatement pstmtquery_CA = null;
		ResultSet result = null;
		ResultSet result2 = null;

		try {
			conn = getCon();
			pstmt = conn.prepareStatement(CMDST_Insert);

			System.out.println("Inserir Aposta: tipo, odd, descricao, valor_Aposta, casa_apostas, jogador");
			java.util.Scanner key = new Scanner(System.in);
			System.out.println("tipo");
			pstmt.setString(1, key.nextLine());
			System.out.println("odd");
			pstmt.setString(2, key.nextLine());
			System.out.println("descricao");
			pstmt.setString(3, key.nextLine());
			System.out.println("valor_Aposta");
			Integer valor_aposta = key.nextInt();
			pstmt.setInt(4, valor_aposta);
			System.out.println("casa_apostas");
			Integer id_casa_apostas = key.nextInt();
			pstmt.setInt(5, id_casa_apostas);
			System.out.println("jogador");
			Integer id_jogador = key.nextInt();
			pstmt.setInt(6, id_jogador);

			pstmtquery_CA = conn.prepareStatement(CMDQuery_casa_apostas);
			pstmtquery_CA.setInt(1, id_casa_apostas);
			result = pstmtquery_CA.executeQuery();
			result.next();
			if (result.getInt("aposta_minima") < valor_aposta) {
				key.close();
				throw new Exception("Valor da aposta superior ao valor minimo definido pela casa de apostas");
			}

			pstmtquery = conn.prepareStatement(CMDQuerySaldo);
			pstmtquery.setInt(1, id_jogador);
			result2 = pstmtquery.executeQuery();
			result2.next();
			if (result2.getInt("saldo") < valor_aposta) {
				key.close();
				throw new Exception("Saldo Insuficiente para fazer uma aposta desse valor");
			}
			pstmt.executeQuery();
			key.close();

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

	public static void suspendPlayer() {

		// Retornar os jogadores com estador ativo
		final String jogadoresAtivosQuerie = "select id,nome from jogador where estado='activo'";
		final String update = "UPDATE jogador SET estado='suspenso' WHERE id=?";

		Connection conn = null;
		Statement stmt = null;
		PreparedStatement pstmt = null;
		ResultSet result = null;

		try {

			conn = getCon();
			stmt = conn.createStatement();
			result = stmt.executeQuery(jogadoresAtivosQuerie);
			ResultSetMetaData md = result.getMetaData();
			int columns = md.getColumnCount();
			printTable(result);

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
				if (stmt != null)
					stmt.close();
				if (pstmt != null)
					pstmt.close();
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
		Statement stmt = null;
		PreparedStatement pstmt = null;
		ResultSet result = null;

		try {

			conn = getCon();
			stmt = conn.createStatement();
			result = stmt.executeQuery(getBetHousesQuerie);
			ResultSetMetaData md = result.getMetaData();
			int columns = md.getColumnCount();
			printTable(result);

			stmt = null;
			pstmt = null;
			result = null;

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
				if (stmt != null)
					stmt.close();
				if (pstmt != null)
					pstmt.close();
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
		Statement stmt = null;
		PreparedStatement pstmt = null;
		ResultSet result = null;

		try {
			conn = getCon();
			pstmt = conn.prepareStatement(getPlayersBetsQuerie);
			System.out.println("Qual é o Nome do Jogador do qual quer ver as Apostas?");
			System.out.print("Nome: ");
			pstmt.setString(1, in.nextLine());
			pstmt.execute();

			printTable(result);

		} catch (Exception err) {
			System.out.println(err);
			// Nothing to do. The option was not a valid one. Read another.
		} finally {
			try {
				if (conn != null)
					conn.close();
				if (stmt != null)
					stmt.close();
				if (pstmt != null)
					pstmt.close();
				if (result != null)
					result.close();
			} catch (SQLException ignored) {
			}
		}
	}

	public static void exit() {
		System.exit(0);
	}

	private static void printTable(ResultSet rs) throws SQLException {
		final int TAB_SIZE = 8;
		ResultSetMetaData meta = rs.getMetaData();
		int columnsCount = meta.getColumnCount();
		StringBuffer sep = new StringBuffer("");

		// header
		for (int i = 1; i <= columnsCount; i++) {
			System.out.print(meta.getColumnLabel(i));
			System.out.print("\t");
		}
		System.out.println(sep);
		// Step 4 - Get result
		while (rs.next()) {
			// results print
			for (int i = 1; i <= columnsCount; i++) {
				System.out.print(rs.getObject(i));
				System.out.print("\t");
			}
			System.out.println();
		}
	}
}
