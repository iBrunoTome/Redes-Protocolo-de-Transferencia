import java.io.*;
import java.net.*;
import java.util.*;

/**
 *
 * @author valadao, modificado por Bruno Tomé, Cláudio Menezes e Ronan Nunes
 */
public class Servidor {

	public final int PORTO_SERVIDOR;
	public final String COMANDO_SAIR = "#SAIR";
	private ServerSocket soqueteServidor;
	protected Socket conexaoCliente;
	protected DataOutputStream saida;
	protected DataInputStream entrada;
	protected static short contadorClientes = 0;
	protected String DESCRICAO = "SERVIDOR";
	protected String VERSAO = "v1.0 (MONOCLIENTE) ";

	public Servidor() {

		PORTO_SERVIDOR = 5001;

	}

	public Servidor(int portoDoServico) {

		PORTO_SERVIDOR = portoDoServico;

	}

	public void executar() {

		System.out.println(" -------- " + VERSAO);

		try {

			// 1- ETAPA DE ABERTURA DO SOQUETE QUE RECEBERÁ AS CONEXÕES
			// abre o soquete onde o cliente conectará
			abreSoqueteServidor();

			// laço que executa eternamente, atendendo um cliente por vez
			while (true) {

				// 1- ETAPA DE ESPERA E ABERTURA DA CONEXÃO COM O CLIENTE
				esperaConexaoCliente();
				obtemFluxosComunicacao();

				// 2- ETAPADA DE COMUNICAÇÃO ENTRE SERVIDOR E CLIENTE
				// envia e recebe mensagens
				comunicaComCliente();

				// 3 - ETAPA DE FECHAMENTO DA CONEXÃO COM O SERVIDOR
				fechaConexao();
			}

		} catch (IOException ex) {

			System.err.println(DESCRICAO + VERSAO + ex.getMessage());

		}
	}

	protected void abreSoqueteServidor() throws IOException {

		soqueteServidor = new ServerSocket(PORTO_SERVIDOR);

		System.out.println(DESCRICAO + " [INFO] Soquete aberto no PORTO "
				+ soqueteServidor.getLocalPort());

	}

	protected Socket esperaConexaoCliente() throws IOException {
		System.out.println(DESCRICAO
				+ " [INFO] Aguardando algum cliente se conectar.");

		conexaoCliente = soqueteServidor.accept();
		Servidor.contadorClientes++;

		System.out.println(DESCRICAO + " [INFO] Conectado ao cliente de IP "
				+ conexaoCliente.getInetAddress() + " No PORTO "
				+ conexaoCliente.getPort());

		return conexaoCliente;
	}

	protected void obtemFluxosComunicacao() throws IOException {

		ObjectOutputStream fluxoSaida = new ObjectOutputStream(
				conexaoCliente.getOutputStream());
		saida = new DataOutputStream(fluxoSaida);

		ObjectInputStream fluxoEntrada = new ObjectInputStream(
				conexaoCliente.getInputStream());
		entrada = new DataInputStream(fluxoEntrada);

		System.out.println(DESCRICAO
				+ " [DEBUG] Fluxos de comunicação obtidos.");

	}

	protected void comunicaComCliente() throws IOException {

		System.out.println(DESCRICAO
				+ " [DEBUG] Pronto para comunicar com o cliente.");

		String mensagemRecebida;

		do {

			mensagemRecebida = entrada.readUTF();

			if (!mensagemRecebida.equals(COMANDO_SAIR)) {

				menuBCR(mensagemRecebida);

			}

		} while (!mensagemRecebida.equals(COMANDO_SAIR));

	}

	private String obtemArquivo(StringTokenizer token) {

		String arquivo = "";

		while (token.hasMoreTokens()) {

			arquivo += token.nextToken();
			arquivo += " ";

		}

		return arquivo.substring(0, arquivo.length() - 1);

	}

	private void deletaArquivo(String nome) throws IOException {

		System.out.println(DESCRICAO + " [DEBUG] Cliente digitou: rm ou DELE");

		File file = new File("arquivos/" + nome);

		System.out.println(DESCRICAO + " [INFO] Deletando "
				+ file.getAbsolutePath());

		if (file.exists()) {

			file.delete();
			saida.writeUTF(DESCRICAO + " [INFO] Arquivo deletado com sucesso!");
			saida.flush();

		}

		else {

			saida.writeUTF(DESCRICAO + " [INFO] Arquivo não encontrado!");
			saida.flush();

		}

	}

	private void criaDiretorio(String nome) throws IOException {

		System.out
				.println(DESCRICAO + " [DEBUG] Cliente digitou: mkdir ou MKD");

		try {

			if (!new File("arquivos/" + nome).exists()) {

				new File("arquivos/" + nome).mkdir();

				saida.writeUTF("Diretório \"" + nome
						+ "\" foi criado com sucesso!");
				saida.flush();

			}

			else {

				saida.writeUTF(DESCRICAO + " [INFO] Diretório já existe!: ");
				saida.flush();

			}

		}

		catch (Exception e) {

			saida.writeUTF(DESCRICAO + " [INFO] Erro ao criar diretório: "
					+ e.toString());
			saida.flush();

		}

	}

	private void listaArquivos() throws IOException {

		System.out.println(DESCRICAO + " [DEBUG] Cliente digitou: ls ou NLST");

		File diretorio = new File("arquivos/");
		File[] arquivos = diretorio.listFiles();
		String lista = "";

		if (arquivos != null) {

			int length = arquivos.length;

			for (int i = 0; i < length; ++i) {

				File f = arquivos[i];

				if (f.isFile()) {

					lista += (f.getName() + "\n");

				}

				else if (f.isDirectory()) {

					lista += (f.getName() + "/\n");

				}

			}

		}

		saida.writeUTF(lista);
		saida.flush();

	}

	private void listaInformacoes() throws IOException {

		System.out.println(DESCRICAO + " [DEBUG] Cliente digitou: lsl ou LIST");

		File diretorio = new File("arquivos/");
		File[] arquivos = diretorio.listFiles();
		String lista = "";

		if (arquivos != null) {

			int length = arquivos.length;

			for (int i = 0; i < length; ++i) {

				File f = arquivos[i];

				if (f.isFile()) {

					lista += ("Nome: " + f.getName()
							+ " | Última modificação: "
							+ new Date((f.lastModified())) + " | Tamanho: "
							+ f.length() / 1000 + "kb\n");

				}

				else if (f.isDirectory()) {

					lista += ("Nome: " + f.getName()
							+ "/ | Última modificação: "
							+ new Date((f.lastModified())) + " | Tamanho: "
							+ f.length() / 1000 + "kb\n");

				}

			}

		}

		saida.writeUTF(lista);
		saida.flush();

	}

	private void help(String mensagemRecebida) throws IOException {

		System.out.println(DESCRICAO + " [DEBUG] Cliente digitou: "
				+ mensagemRecebida);

		if (mensagemRecebida.equals("?") || mensagemRecebida.equals("HELP")) {

			saida.writeUTF("\n\nget | RETR : Obtém uma cópia do arquivo especificado (download para o cliente)\n"
					+ "put | STOR : Envia uma cópia do arquivo especificado (upload para o servidor).\n"
					+ "rm | DELE : Apaga um arquivo.\n"
					+ "mkdir | MKD : Cria um diretório.\n"
					+ "ls  | NLST : Lista os nomes dos arquivos de um diretório.\n"
					+ "lsl | LIST : Lista os nomes dos arquivos de um diretório.\n"
					+ "exit | QUIT : Desconecta do servidor, fecha aplicação.\n\n");
			saida.flush();

		}

		else {

			StringTokenizer token = new StringTokenizer(mensagemRecebida, " ");

			token.nextToken();

			switch (token.nextToken()) {

			case "get":
			case "RETR":

				saida.writeUTF("Obtém uma cópia do arquivo especificado (download para o cliente).\nForma de uso: get <NOME ARQUIVO>\nForma de uso: RETR <NOME ARQUIVO>");
				saida.flush();

				break;

			case "put":
			case "STOR":

				saida.writeUTF("Envia uma cópia do arquivo especificado (upload para o servidor).\nForma de uso: put <NOME ARQUIVO>\nForma de uso: STOR <NOME ARQUIVO>");
				saida.flush();

				break;

			case "rm":
			case "DELE":

				saida.writeUTF("Apaga um arquivo.\nForma de uso: rm <NOME ARQUIVO>\nForma de uso: DELE <NOME ARQUIVO>");
				saida.flush();

				break;

			case "mkdir":
			case "MKD":

				saida.writeUTF("Cria um diretório.\nForma de uso: mkdir <NOME DIRETÓRIO>\nForma de uso: MKD <NOME DIRETÓRIO>");
				saida.flush();

				break;

			case "ls":
			case "NLST":

				saida.writeUTF("Lista os nomes dos arquivos de um diretório.\nForma de uso: ls\nForma de uso: NLST");
				saida.flush();

				break;

			case "lsl":
			case "LIST":

				saida.writeUTF("Retorna informações* do arquivo ou diretório especificado (* ex.: nome, tamanho, data de\n"
						+ "modificação, etc)\nForma de uso: lsl\nForma de uso: LIST");
				saida.flush();

				break;

			case "exit":
			case "QUIT":

				saida.writeUTF("Desconecta do servidor, fecha aplicação.\nForma de uso: exit\nForma de uso: QUIT");
				saida.flush();

				break;

			default:

				saida.writeUTF("Opção Inválida!");
				saida.flush();

				break;

			}

		}

	}

	protected void enviaParaCliente(String nome) throws IOException {
		
		FileInputStream fileIn = null;

		byte[] cbuffer = new byte[1472];
		int bytesRead;
		int iteracoes = 0;
		double sum = 0;

		try {

			/* Só envia se o diretório + nome do arquivo existirem */

			if (new File("arquivos/" + nome).exists()) {

				saida.writeUTF(DESCRICAO + " [INFO] ARQUIVO ENCONTRADO!");
				saida.flush();
				// Criando arquivo que sera transferido pelo cliente
				File file = new File("arquivos/" + nome);
				// Encriptando arquivo
				System.out.println(DESCRICAO + " [INFO] Criptografando arquivo");
				new FileEncryption("DES/ECB/PKCS5Padding", "arquivos/" + nome).encrypt();
				File fileEncrypted = new File("arquivos/" + nome + ".enc");
				fileIn = new FileInputStream(fileEncrypted);

				System.out.println(DESCRICAO + " [INFO] Enviando Arquivo '"
						+ file.getName() + " de " + file.length() + " bytes");

				saida.writeLong(file.length());
				saida.flush();
				
				long t = System.currentTimeMillis();

				while ((bytesRead = fileIn.read(cbuffer)) != -1) {// EOF

					System.out
							.println(DESCRICAO + " [INFO] Enviando ["
									+ (++iteracoes) + "] " + bytesRead + " of "
									+ file.length() + " bytes ("
									+ (int) (((sum += bytesRead) / file.length())
									* 100) + "%)");
					saida.write(cbuffer, 0, bytesRead);
					saida.flush();

				}
				
				t = System.currentTimeMillis() - t;  
			    System.out.println(DESCRICAO + " [INFO] " + (String.format ("O processo levou %d ms", t)));

				System.out.println(DESCRICAO + " [INFO] Arquivo Enviado!");
				
				// deletando arquivo encriptado
				fileEncrypted.delete();

			} else {

				System.out.println(DESCRICAO + " [DEBUG] Arquivo inexistente");
				saida.writeUTF("null");
				saida.flush();

			}

		} catch (Exception e) {

			System.out.println(DESCRICAO + " [DEBUG] ERRO " + e.toString());
			saida.writeUTF(e.toString());
			saida.flush();

		}

	}

	protected void recebeCliente(String nome) throws Exception {

		if (!new File("arquivos/").exists()) {

			new File("arquivos/").mkdir();

		}
		
		long t = System.currentTimeMillis();

		FileOutputStream fos = null;
		File file = new File("arquivos/" + nome + ".enc");
		fos = new FileOutputStream(file);

		System.out.println(DESCRICAO + " [INFO] Arquivo Local Criado " + nome);
		System.out.println(DESCRICAO + " [INFO] Criptografando arquivo");

		// Prepara variaveis para transferencia
		byte[] cbuffer = new byte[1472];
		int bytesRead = 0;
		int iteracoes = 0;
		double sum = 0;

		long fileLenght = entrada.readLong();

		System.out.println(DESCRICAO + " [INFO] Recebendo Arquivo '" + file.getName()
				+ "' de " + fileLenght + " bytes");

		while (sum < fileLenght) {

			bytesRead = entrada.read(cbuffer);
			System.out.println(DESCRICAO + " [INFO] Recebendo [" + (++iteracoes) + "] " + bytesRead
					+ " de " + fileLenght + " bytes ("
					+ (int) (((sum += bytesRead) / fileLenght) * 100) + "%)");
			fos.write(cbuffer, 0, bytesRead);
			fos.flush();

		}

		fos.close();
		System.out.println(DESCRICAO + " [INFO] Arquivo recebido!");
		t = System.currentTimeMillis() - t;  
	    System.out.println(DESCRICAO + " [INFO] " + (String.format ("O processo levou %d ms", t)));
	    
	    // decriptando arquivo
	    System.out.println(DESCRICAO + " [INFO] Descriptografando arquivo");
		new FileEncryption("DES/ECB/PKCS5Padding", "arquivos/" + nome + ".enc").decrypt();
		System.out.println(DESCRICAO + " [INFO] Arquivo Descriptografado com sucesso!");
		
		// deletando arquivo encriptado
		new File("arquivos/" + nome + ".enc").delete();

	}

	// BCR de Bruno, Cláudio e Ronan, que é como nomeamos nosso protocolo.

	private void menuBCR(String mensagemRecebida) throws IOException {

		try {

			StringTokenizer token = new StringTokenizer(mensagemRecebida, " ");

			switch (token.nextToken()) {

			case "get":
			case "RETR":

				enviaParaCliente(obtemArquivo(token));

				break;

			case "put":
			case "STOR":

				mensagemRecebida = entrada.readUTF();

				if (!mensagemRecebida.equals("null")) {

					System.out.println(mensagemRecebida);
					recebeCliente(obtemArquivo(token));

				}

				else {

					System.out.println(DESCRICAO
							+ " [DEBUG] Arquivo não existe!");

				}

				break;

			case "rm":
			case "DELE":

				deletaArquivo(obtemArquivo(token));

				break;

			case "mkdir":
			case "MKD":

				criaDiretorio(obtemArquivo(token));

				break;

			case "ls":
			case "NLST":

				listaArquivos();

				break;

			case "lsl":
			case "LIST":

				listaInformacoes();

				break;

			case "?":
			case "HELP":

				help(mensagemRecebida);

				break;

			case "exit":
			case "QUIT":
			case "#SAIR":

				saida.writeUTF("#SAIR");
				saida.flush();

				break;

			default:

				saida.writeUTF("Opção inválida");
				saida.flush();

				break;

			}

		}

		catch (Exception e) {

			System.out.println(DESCRICAO + " [DEBUG] ERRO " + e.toString());
			saida.writeUTF(e.toString());
			saida.flush();

		}

	}

	protected void fechaConexao() throws IOException {

		saida.close();
		entrada.close();
		conexaoCliente.close();

		System.out.println(DESCRICAO
				+ " [INFO]  conexão fechada com o cliente!");

	}

	public static void main(String[] args) {

		if(args.length == 1) {

			final Servidor servidor1 = new Servidor(Integer.parseInt(args[0]));
			servidor1.executar();

		}

		else if(args.length == 0) {

			final Servidor servidor1 = new Servidor();
			servidor1.executar();

		}

		else {

			System.out.println("\n\nModo de uso: java Servidor PORTO_SERVIDOR\n\nou\n\n java Servidor\n\n");

		}

	}

	@Override
	public String toString() {

		return this.DESCRICAO;

	}

}// class
