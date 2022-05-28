import java.sql.*;
import java.util.Scanner;

public class Model {

	private static Connection getCon() throws SQLException {
		return DriverManager.getConnection("jdbc:postgresql://10.62.73.22:5432/?user=l3d4&password=isigods&ssl=false");
	}
	public static void newBetHouse(){
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
			pstmt.executeQuery()

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

	public static void newPlayerAtBetHouse() {
		//final String CMDQuery = "SELECT id FROM casa_apostas WHERE id = ?";
		final String CMDST = "INSERT INTO jogador(id, email, nome, nickname, estado, data_nascimento, data_registo, morada, codigo_postal, localidade, casa_apostas)" +
				"VALUES (?, ?, ?, ?, ?, ?::date, ?::date, ?, ?, ?, ?)";

		Connection conn = null;
		PreparedStatement pstmt = null;
		PreparedStatement pstmtquery = null;
		ResultSet result = null;

		try {
			conn = getCon();
			pstmt = conn.prepareStatement(CMDST);
			System.out.println("INSERT: id, email, nome, nickname, estado, data_nascimento, data_registo, morada, codigo_postal, localidade, casa_apostas");
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
			if(!estado.matches("ativo|suspenso|autoexcluído")) {
				System.out.println("Estado must be ativo or suspenso or autoexcluído");
				return;
			}
			pstmt.setString(5, estado);
			System.out.println("data_nascimento");
			//String data_nascimento = key.nextLine();
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
			pstmtquery = conn.prepareStatement(CMDQuery)
			pstmtquery.setString(1,casa_apostas)
			result = pstmtquery.executeQuery()
			result.next();
			if( result.getString("id").isEmpty()){
				throw new Exception("That casa_apostas dosen't exist")
			}*/
			pstmt.setString(11,casa_apostas );

			pstmt.executeQuery();

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


	public static void newPlayerBet() {
		final String CMDQuery_jogador = "SELECT id FROM jogador WHERE id = ?";
		final String CMDQuery_casa_apostas = "SELECT aposta_minima FROM casa_apostas WHERE id = ?";
		final String CMDQuerySaldo = "SELECT   coalesce(jogador.id,levantamento.jogador, deposito.jogador, aposta.jogador, resultado.jogador) as Jogador_Id,  coalesce(max(deposito.depo), 0.0) - coalesce(max(levantamento.leva), 0.0) - coalesce(max(aposta.apos), 0.0) + coalesce(max(resultado.res), 0.0)  as saldo \n" +
				"from\t(select id from jogador where estado = 'activo' and id = ?) as jogador\n" +
				"\t\tleft outer join(\n" +
				"\t\tSELECT t.jogador,SUM(t.valor) as leva FROM transacao t JOIN bancaria b ON (t.numero = b.transacao) WHERE b.operacao = 'levantamento' group by t.jogador order by t.jogador) as levantamento\n" +
				"\t\ton( jogador.id = levantamento.jogador)\n" +
				"\t\tfull outer join( \n" +
				"\t\tSELECT   t.jogador ,SUM(t.valor) as depo FROM transacao t JOIN bancaria b2 ON (t.numero = b2.transacao)  WHERE b2.operacao = 'depósito' group by t.jogador order by t.jogador) as deposito\n" +
				"\t\ton (greatest (jogador.id,levantamento.jogador) = deposito.jogador) \n" +
				"\t\tfull outer join( \n" +
				"\t\tSELECT  t.jogador,SUM(valor) as apos FROM transacao t JOIN aposta a ON (t.numero = a.transacao) group by t.jogador order by t.jogador)  as aposta\n" +
				"\t\ton ( greatest(levantamento.jogador,jogador.id, deposito.jogador) = aposta.jogador)\n" +
				"\t\tfull outer join(\n" +
				"\t\tSELECT  t.jogador, SUM(r.valor) as res FROM transacao t JOIN resolucao r ON (t.numero = r.aposta and r.resultado = 'vitória') group by t.jogador order by t.jogador) as resultado\n" +
				"\t\ton(greatest(jogador.id,deposito.jogador,levantamento.jogador,aposta.jogador) = resultado.jogador)\n" +
				"\t\tgroup by  jogador.id, levantamento.jogador, deposito.jogador , aposta.jogador, resultado.jogador\n" +
				"\t\torder by  jogador.id, levantamento.jogador, deposito.jogador , aposta.jogador, resultado.jogador asc \n" +
				"\t\tlimit 1"

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
				"ROLLBACK TRANSACTION;"

		Connection conn = null;
		PreparedStatement pstmt = null;
		PreparedStatement pstmtquery = null;
		PreparedStatement pstmtquery_CA = null;
		ResultSet result = null;

		try {
			conn = getCon();
			pstmt = conn.prepareStatement(CMDST_Insert);

			System.out.println("Inserir Aposta: tipo, odd, descricao, valor_Aposta, casa_apostas, jogador");
			java.util.Scanner key = new Scanner(System.in);
			System.out.println("tipo");
			pstmt.setString(1, key.nextLine());
			System.out.println("odd");
			pstmt.setFloat(2, key.nextLine());
			System.out.println("descricao");
			pstmt.setString(3, key.nextLine());
			System.out.println("valor_Aposta");
			int valor_aposta = key.nextLine()
			pstmt.setInt(4, valor_aposta);
			System.out.println("casa_apostas");
			int id_casa_apostas = key.nextLine();
			pstmt.setString(5, id_casa_apostas);
			System.out.println("jogador");
			int id_jogador = key.nextLine()
			pstmt.setString(6, key.id);

			pstmtquery_CA = conn.prepareStatement(CMDQuery_casa_apostas)
			pstmtquery.setId(1, id_casa_apostas)


			pstmtquery = conn.prepareStatement(CMDQuerySaldo)
			pstmtquery.setId(1, id_jogador)
			result.next();
			if (result.getInt("saldo") < valor_aposta) {
				throw new Exception("Saldo Insuficiente para fazer uma aposta desse valor");
			}


			pstmt.executeQuery();
		}catch (Exception err) {
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

	public static void insertBetResolution() {
	}
}