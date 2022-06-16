//package isel.isi;

import java.sql.*;
import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class Model {
	public static Scanner input = new Scanner(System.in);
	private static final Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
	private static final int CURRENT_YEAR = cal.get(Calendar.YEAR);
	private static final int CURRENT_MONTH = cal.get(Calendar.MONTH)+1; //STARTS COUNTING ON 0 = JAN



	private static Connection getCon() throws SQLException {
		String jdbcURL = "jdbc:postgresql://10.62.73.22:5432/l3n4";
		String username = "l3n4";
		String password = "isigods";
		return  DriverManager.getConnection(jdbcURL, username, password);
		//return DriverManager.getConnection("jdbc:postgresql://10.62.73.22:5432/?user=l3n4&password=isigods");
	}

	public static void newBetHouse(){
		final String CMDST = "Begin;\n" +
				"INSERT INTO casa_apostas(id,nome, NIPC, aposta_minima)\n" +
				"\tVALUES ((select id from casa_apostas ORDER BY id DESC LIMIT 1) + 1, ?, ?, ?::decimal);\n" +
				"INSERT INTO administrador(id,email,nome,perfil,casa_apostas)\n" +
				"\tValues((select id from administrador ORDER BY id DESC LIMIT 1) +1, ? , ?, 'administrador' , (select id from casa_apostas ORDER BY id DESC LIMIT 1) );\n" +
				"COMMIT TRANSACTION;\n";

		Connection conn = null;
		PreparedStatement pstmt = null;
		PreparedStatement pstmtquery = null;
		ResultSet result = null;

		try {
			conn = getCon();
			pstmt = conn.prepareStatement(CMDST);
			System.out.println("INSERT: nome, NIPC, aposta_minima");
			java.util.Scanner key = new Scanner(System.in);

			System.out.println("nome:");
			String nomeCasaApostas = key.nextLine();
			pstmt.setString(1, nomeCasaApostas );

			System.out.println("NIPC:");
			String NIPC = key.nextLine();
			while(NIPC.length() != 9){
				System.out.println("NIPC invalido, insira um número com 9 digitos");
				NIPC = key.nextLine();
			}
			pstmt.setString(2, NIPC );

			System.out.println("aposta_minima:");
			float aposta_minima = Float.parseFloat(key.nextLine());
			while (aposta_minima < (float) 0.01) {
				System.out.println("Aposta minima must be greater than 0.01");
				aposta_minima = Float.parseFloat(key.nextLine());
			}

			pstmt.setFloat(3, aposta_minima);


			System.out.println("É necessario criar um admin para a casa de apostas!\n" +
					"Por favor insira o nome e o email para o admin\n");
			System.out.print("nome admin:");
			pstmt.setString(5, key.nextLine());
			System.out.print("email admin:");
			String emailAdmin = key.nextLine();
			String auxNome = nomeCasaApostas.toLowerCase().replace(" ", "");
			while(!emailAdmin.contains("@"+auxNome+".")){
				System.out.println("Email do admin tem que ter o nome da casa de apostas! e.g ..."+ "@"+auxNome+".");
				emailAdmin = key.nextLine();
			}
			pstmt.setString(4, emailAdmin);



			pstmt.executeQuery();
			key.close();

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

	//Funções para confriamr as datas
	private static String getDate(){
			System.out.println("Ano");
			int year = checkBetweenBoundaries(1950,2100);
			System.out.println("Mês: \nEntre 1 e 12 ");
			int month;
			if(year == CURRENT_YEAR ) month = checkBetweenBoundaries(CURRENT_MONTH, 12);
			else month = checkBetweenBoundaries(1, 12);
			int lastdaymonth = lastDayMonth(month, year);
			System.out.println("Dia: " + "\nEntre 1 e " + lastdaymonth);
			int day = checkBetweenBoundaries(1, lastdaymonth);
			return getStringDate(year,month,day);
		}

		//obter ultimo dia de cada mês
		public static int lastDayMonth(int month, int year){
			int lastDay = -1;
			int[] lastDayArray = {-1, 31, -1, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};
			if(month == 2) {
				Calendar testCal = Calendar.getInstance();
				testCal.set(Calendar.YEAR, year);
				if (cal.getActualMaximum(Calendar.DAY_OF_YEAR) > 365) lastDay = 29;
				else lastDay = 28;
			}
			if(month != 2) lastDay = lastDayArray[month];
			return lastDay;
		}

		//passar dados de data para uma String dde formato Date de PSQL
		public static String getStringDate(int year, int month, int day){
			String date = year + "-";
			if(checkIfBelowMax(month,10)) date += month + "-";
			else date += "0" + month + "-";
			if(checkIfBelowMax(day,10)) date += day;
			else date += "0" + day;
			return date;
		}

		public static int checkBetweenBoundaries(int min, int max) {
			int var;
			do{var = getValInt();
			}while(var < min || var > max);
			return var;
		}

		public static boolean checkIfBelowMax(int var, int max) {return var >= max;}

		private static int getValInt(){
			System.out.print("> ");
			int val = input.nextInt();
			//consume rest of line
			input.nextLine();
			return val;
		}

		//obter valores inseridos pelo utilizador
		private static String getValString(){
			System.out.print("> ");
			return input.nextLine();
		}



	public static void newPlayerAtBetHouse() {
		final String CMDQuery = "SELECT email FROM administrador where casa_apostas = ?";
		final String CMDQueryNickname = "SELECT nickname FROM jogador where casa_apostas = ?";
		final String CMDQueryEmail = "SELECT email FROM jogador where casa_apostas = ?";
		final String CMDST = "INSERT INTO jogador(id, email, nome, nickname, estado, data_nascimento, data_registo, morada, codigo_postal, localidade, casa_apostas)" +
				"VALUES ((select id from jogador ORDER BY id DESC LIMIT 1) +1, ?, ?, ?, ?, ?, ? , ?, ?, ?, ?)";

		Connection conn = null;
		PreparedStatement pstmt = null;
		PreparedStatement pstmtquery = null;
		Statement stmt = null;
		ResultSet result = null;
		ResultSet result2 = null;

		try {
			conn = getCon();
			pstmt = conn.prepareStatement(CMDST);

			java.util.Scanner key = new Scanner(System.in);


			stmt = conn.createStatement();
			result = stmt.executeQuery("SELECT id, nome FROM casa_apostas");
			ResultSetMetaData md = result.getMetaData();
			System.out.println("Casas de apostas disponiveis: \n");

			LinkedList[] table= printTable(result);
			while(table[0].size() == 0) {
				System.out.print("\nNão existem casa de apostas disponiveis" +
						"\nPor favor crie casa de apostas e tente outra vez! \n" +
						"\n Press 1 to exit --> ");
				int exit = Integer.parseInt(input.nextLine());
				if(exit == 1) return;
			}
			System.out.print("\nEscolha o id da casa de apostas pretendida:");

			int id_casa_apostas = Integer.parseInt(key.nextLine());
			while (!table[0].contains(String.valueOf(id_casa_apostas))) {
				System.out.print("Id invalido, escolha um dentro da lista acima: ");
				id_casa_apostas = Integer.parseInt(key.nextLine());
			}
			pstmt.setInt(10, id_casa_apostas);

			System.out.println("INSERT: email, nome, nickname, estado, data_nascimento, data_registo, morada, codigo_postal, localidade\n");

			System.out.println("email");
			String email = key.nextLine();
			pstmtquery = conn.prepareStatement(CMDQuery,ResultSet.TYPE_SCROLL_SENSITIVE,
					ResultSet.CONCUR_UPDATABLE);
			pstmtquery.setInt(1, id_casa_apostas);
			result2 = pstmtquery.executeQuery();


			while (result2.next()) {
				String testEmail = result2.getString("email");
				if (testEmail.equals(email)) {
					System.out.println("O email " + email + " corresponde a um administrador e administradores não podem ser jogadores!");
					System.out.print("Escolha outro email: ");
					email = key.nextLine();
					result2.beforeFirst();
				}
			}

			pstmtquery = conn.prepareStatement(CMDQueryEmail, ResultSet.TYPE_SCROLL_SENSITIVE,
					ResultSet.CONCUR_UPDATABLE);
			pstmtquery.setInt(1, id_casa_apostas);
			result2 = pstmtquery.executeQuery();



			while (result2.next())
				if (result2.getString("email").equals(email)) {
					System.out.print(email + " já está a ser usado!\nEscolha outro email: ");
					email = key.nextLine();
					result2.beforeFirst();
				}



			pstmt.setString(1, email);
			System.out.println("nome");
			pstmt.setString(2, key.nextLine());


			System.out.println("nickname");
			String nickname = key.nextLine();
			pstmtquery = conn.prepareStatement(CMDQueryNickname, ResultSet.TYPE_SCROLL_SENSITIVE,
					ResultSet.CONCUR_UPDATABLE);
			pstmtquery.setInt(1, id_casa_apostas);
			result2 = pstmtquery.executeQuery();


			while (result2.next())
				if (result2.getString("nickname").equals(nickname)) {
					System.out.print(nickname + " já está a ser usado!\nEscolha outro nickname: ");
					nickname = key.nextLine();
					result2.beforeFirst();
				}
			pstmt.setString(3,nickname );


			System.out.println("estado");
			String estado = key.nextLine();
			while(!estado.matches("activo|suspenso|autoexcluído")) {
				System.out.println("Estado must be activo or suspenso or autoexcluído");
				estado = key.nextLine();
			}
			pstmt.setString(4, estado);

			System.out.println("data_nascimento");
			String data_nasc = getDate();
			pstmt.setDate(5, Date.valueOf(data_nasc));
			System.out.println("data_registo");
			String data_res = getDate();
			pstmt.setDate(6, Date.valueOf(data_res));
			System.out.println("morada");
			pstmt.setString(7, key.nextLine());
			System.out.println("codigo_postal");
			pstmt.setInt(8, Integer.parseInt(key.nextLine()));
			System.out.println("localidade");
			pstmt.setString(9, key.nextLine());


			pstmt.executeQuery();
			key.close();

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
		final String CMDQuery_CA_minimo = "SELECT aposta_minima FROM casa_apostas WHERE id = ?";
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
				"\t\tSELECT  t.jogador, SUM(r.valor) as res FROM transacao t JOIN resolucao r ON (t.numero = r.aposta) group by t.jogador order by t.jogador) as resultado\n" +
				"\t\ton(greatest(jogador.id,deposito.jogador,levantamento.jogador,aposta.jogador) = resultado.jogador)\n" +
				"\t\tgroup by  jogador.id, levantamento.jogador, deposito.jogador , aposta.jogador, resultado.jogador\n" +
				"\t\torder by  jogador.id, levantamento.jogador, deposito.jogador , aposta.jogador, resultado.jogador asc \n" +
				"\t\tlimit 1";

		final String CMDST_Insert = "BEGIN;\n " +
				"INSERT INTO transacao(numero, valor, data_transacao, casa_apostas, jogador)\n" +
				"VALUES((SELECT numero\n" +
				"  FROM transacao \n" +
				"  ORDER BY numero DESC LIMIT 1)+1, ?, current_date, ?, ?);  \n" +
				"  \n" +
				"INSERT INTO aposta(transacao,tipo,odd,descricao)\n" +
				"VALUES((\n" +
				"  SELECT numero\n" +
				"  FROM transacao \n" +
				"  ORDER BY numero DESC LIMIT 1), ?, ?, ?); \n" +
				"INSERT  INTO resolucao(id, resultado, data_resolucao, aposta)\n" +
				"VALUES((\n" +
				"  SELECT numero\n" +
				"  FROM transacao \n" +
				"  ORDER BY numero DESC LIMIT 1),null , null, (SELECT numero FROM transacao ORDER BY numero DESC LIMIT 1));\n" +
				"COMMIT TRANSACTION;";


		Connection conn = null;
		PreparedStatement pstmt = null;
		PreparedStatement pstmtquery = null;
		PreparedStatement pstmtquery_CA = null;
		Statement stmt = null;

		ResultSet result = null;
		ResultSet result2 = null;


		try {
			conn = getCon();
			java.util.Scanner key = new Scanner(System.in);
			stmt = conn.createStatement();
			result = stmt.executeQuery("SELECT id, nome FROM casa_apostas");

			System.out.println("Casas de apostas disponiveis: \n");
			LinkedList[] table  =printTable(result);
			while(table[0].size() == 0) {
				System.out.print("\nNão existem casa de apostas disponiveis" +
						"\nPor favor crie casa de apostas e tente outra vez! \n" +
						"\n Press 1 to exit --> ");
				int exit = Integer.parseInt(key.nextLine());
				if(exit == 1) return;
			}

			System.out.print("\nEscolha o id da casa de apostas pretendida:");
			int id_casa_apostas = Integer.parseInt(key.nextLine());
			while (!table[0].contains(String.valueOf(id_casa_apostas))) {
				System.out.print("Id invalido, escolha um dentro da lista acima: ");
				id_casa_apostas = Integer.parseInt(key.nextLine());
			}
			if(table[0].getFirst() == null) {
				System.out.print("Não existem jogador com estado 'Activo' nesta casa de apostas ");
			}



			stmt = conn.createStatement();
			result = stmt.executeQuery("SELECT id, nome FROM jogador where estado = 'activo' and casa_apostas ="+id_casa_apostas);
			System.out.println("Jogadores disponiveis na casa de apsotas ID "+id_casa_apostas+" com estado 'ACTIVO': \n");

			table = printTable(result);
			while(table[0].size() == 0) {
				System.out.print("\nNão existem jogadores com estado 'Activo' nesta casa de apostas!" +
						"\nPor favor insira um jogador e tente outra vez ou mude de casa de apostas! \n" +
						"\n Press 1 to exit --> ");
				int exit = Integer.parseInt(key.nextLine());
				if(exit == 1) return;
			}
			System.out.print("\nEscolha o id do jogador pretendida: ");
			int id_jogador = Integer.parseInt(key.nextLine());
			while (!table[0].contains(String.valueOf(id_jogador))) {
				System.out.print("Id invalido, escolha um dentro da lista acima: ");
				id_jogador = Integer.parseInt(key.nextLine());
			}

			pstmt = conn.prepareStatement(CMDST_Insert);

			pstmt.setInt(2, id_casa_apostas);
			pstmt.setInt(3, id_jogador);

			System.out.println("Inserir Aposta: tipo, odd, descricao, valor_Aposta");
			System.out.println("tipo");
			pstmt.setString(4, key.nextLine());
			System.out.println("odd");
			pstmt.setFloat(5, Float.parseFloat(key.nextLine()));
			System.out.println("descricao");
			pstmt.setString(6, key.nextLine());


			System.out.println("valor_Aposta");
			float valor_aposta = Float.parseFloat(key.nextLine());


			//Verifica se o valor da aposta cumpre o valor minimo da casa de apostas
			pstmtquery_CA = conn.prepareStatement(CMDQuery_CA_minimo);
			pstmtquery_CA.setInt(1, id_casa_apostas);
			result = pstmtquery_CA.executeQuery();
			result.next();
			while (result.getFloat("aposta_minima") >= (float) valor_aposta) {
				System.out.println("Valor da aposta superior ao valor minimo definido pela casa de apostas: "+ result.getFloat("aposta_minima")+"\nEscolha outro valor");
				valor_aposta = Float.parseFloat(key.nextLine());
			}

			pstmt.setFloat(1, valor_aposta);



			pstmtquery = conn.prepareStatement(CMDQuerySaldo);
			pstmtquery.setInt(1, id_jogador);
			result2 = pstmtquery.executeQuery();
			result2.next();
			float saldo = result2.getFloat("saldo");
			while (saldo < valor_aposta) {
				System.out.println("Saldo Insuficiente ("+saldo+") para fazer uma aposta desse valor");
				System.out.print("Introduza um novo valor para a aposta:");
				valor_aposta = Float.parseFloat(key.nextLine());
			}


			pstmt.executeQuery();
			System.out.print("Aposta Introduzida :)");
			key.close();

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
			LinkedList[] table = printTable(result);
			while(table[0].size() == 0) {
				System.out.print("\nNão existem jogadores na Bd com estado 'activo'" +
						"\nPor favor crie casa de apostas e tente outra vez! \n" +
						"\n Press 1 to exit --> ");
				int exit = Integer.parseInt(input.nextLine());
				if(exit == 1) return;
			}

			pstmt = conn.prepareStatement(update);
			System.out.println("Id do utilizador que pretende remover?");
			int id_jogador = Integer.parseInt(input.nextLine());
			while (!table[0].contains(String.valueOf(id_jogador))) {
				System.out.print("Id invalido, escolha um dentro da lista acima: ");
				id_jogador = Integer.parseInt(input.nextLine());
			}
			pstmt.setInt(1, id_jogador);
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
			System.out.println("Casas de apostas disponiveis: \n");
			LinkedList[] table = printTable(result);

			while(table[0].size() == 0) {
				System.out.print("\nNão existem casa de apostas disponiveis" +
						"\nPor favor crie casa de apostas e tente outra vez! \n" +
						"\n Press 1 to exit --> ");
				int exit = Integer.parseInt(input.nextLine());
				if(exit == 1) return;
			}

			result = null;

			pstmt = conn.prepareStatement(countQuerie);
			System.out.println("\nId da Casa de Apostas que pretende ter o nº de utilizadores?");

			int id_aposta = Integer.parseInt(input.nextLine());
			while (!table[0].contains(String.valueOf(id_aposta))) {
				System.out.print("Id invalido, escolha um dentro da lista acima: ");
				id_aposta = Integer.parseInt(input.nextLine());
			}
			pstmt.setInt(1, id_aposta);
			result = pstmt.executeQuery();
			System.out.print("\nNúmero de Jogadores nessa Casa de Apostas = ");
			result.next();
			System.out.print(result.getInt("count") + "\n");
			TimeUnit.SECONDS.sleep(5);

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
		final String aposta_sem_resolucao = "select  id, descricao from resolucao r\n" +
				"join aposta a on r.aposta = a.transacao\n" +
				"where resultado is null";

		final String updateResolucao = "update resolucao\n" +
				"SET data_resolucao = ?::date , valor = ?, resultado = ? \n" +
				"Where aposta = ?";

		final String valorAposta = "select valor\n" +
				"    from transacao t join aposta a on t.numero = a.transacao\n" +
				"where t.numero = ?";

		Connection conn = null;
		Statement stmt = null;
		PreparedStatement pstmt = null;
		PreparedStatement pstmtValor = null;
		ResultSet result = null;
		ResultSet rsValor = null;
		java.util.Scanner key = new Scanner(System.in);

		try {

			conn = getCon();
			stmt = conn.createStatement();
			result = stmt.executeQuery(aposta_sem_resolucao);
			ResultSetMetaData md = result.getMetaData();
			System.out.println("Apostas sem Resolucao: \n \n");

			LinkedList[] table  =printTable(result);
			while(table[0].size() == 0) {
				System.out.print("\nNão existem Apostas sem Resolucao" +
						"\n\n" +
						"\n Press 1 to exit --> ");
				int exit = Integer.parseInt(key.nextLine());
				if(exit == 1) return;
			}

			pstmt = conn.prepareStatement(updateResolucao);
			System.out.println("\nId da aposta que pretende alterar a resolução?");
			int id_aposta = Integer.parseInt(key.nextLine());
			while (!table[0].contains(String.valueOf(id_aposta))) {
				System.out.print("Id invalido, escolha um dentro da lista acima: ");
				id_aposta = Integer.parseInt(key.nextLine());
			}


			pstmt.setDate(1, java.sql.Date.valueOf(java.time.LocalDate.now()));
			pstmt.setInt(4, id_aposta);
			System.out.println("Valor da nova resolução:");
			float valorResolucao = Float.parseFloat(key.nextLine());
			pstmt.setFloat(2,valorResolucao);
			String resultado;
			if(valorResolucao == 0) resultado = "derrota";
			else {
				pstmtValor = conn.prepareStatement(valorAposta);
				pstmtValor.setInt(1, id_aposta);
				rsValor = pstmtValor.executeQuery();
				rsValor.next();
				float valorApo = rsValor.getFloat("valor");
				if( valorApo > valorResolucao) resultado = "cashout";
				else if( valorApo  == valorResolucao) resultado = "reembolso"; // questionavel mas nas casa de apostas q conheço faz sentido
				else resultado = "vitória";
			}
			pstmt.setString(3, resultado);

			pstmt.executeQuery();

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

	public static void showPlayersBets() {
		// Querie para mostrar as Apostas de um dado Jogador
		final String getPlayersNames = "select nome from jogador";
		final String getPlayersBetsQuerie = "select  t.numero as num, tipo, odd, descricao FROM aposta as a JOIN transacao as t ON (a.transacao = t.numero) WHERE t.jogador IN(SELECT distinct id FROM jogador j WHERE j.nome = ?) GROUP BY t.numero, a.tipo, a .odd, a.descricao";

		Connection conn = null;
		PreparedStatement pstmt = null;
		Statement stmt = null;
		ResultSet result = null;

		try {
			conn = getCon();
			pstmt = conn.prepareStatement(getPlayersBetsQuerie);
			conn = getCon();
			stmt = conn.createStatement();
			result = stmt.executeQuery(getPlayersNames);

			System.out.println("Nome dos jogadores: \n \n");
			LinkedList[] table  = printTable(result);

			System.out.println("\nQual é o Nome do Jogador do qual quer ver as Apostas?");
			System.out.print("Nome: ");
			pstmt.setString(1, input.nextLine());
			result = pstmt.executeQuery();

			printTable(result);

		} catch (Exception err) {
			System.out.println(err);
			// Nothing to do. The option was not a valid one. Read another.
		} finally {
			try {
				if (conn != null)
					conn.close();
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

	private static LinkedList[] printTable(ResultSet rs) throws SQLException {
		ResultSetMetaData meta = rs.getMetaData();
		int columnsCount = meta.getColumnCount();
		LinkedList[] table = new LinkedList[columnsCount];

		for (int i = 0; i < columnsCount; i++)
			table[i] = new LinkedList();

		// header
		for (int i = 1; i <= columnsCount; i++) {
			System.out.print(meta.getColumnLabel(i));
			System.out.print("\t");
		}
		System.out.print("\n");
		// Step 4 - Get result
		while (rs.next()) {
			// results print
			for (int i = 1; i <= columnsCount; i++) {
				Object sut = rs.getObject(i);
				table[--i].add(sut.toString());
				i++;
				System.out.print(sut.toString());
				System.out.print("\t");
			}
			System.out.println();
		}
		return table;
	}


}
