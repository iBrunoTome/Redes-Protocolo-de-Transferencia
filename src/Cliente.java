import java.io.*;
import java.net.*;
import java.util.*;

/**
 *
 * @author valadao, modificado por Bruno Tomé, Cláudio Menezes e Ronan Nunes
 */

public class Cliente {

	public final String VERSAO = "CLIENTE  v1.0";
	protected String DESCRICAO = "CLIENTE";
	public final String ENDERECO_SERVIDOR;
	public final int PORTO_SERVIDOR;
	public final String COMANDO_SAIR = "#SAIR";
	protected Socket soqueteCliente;
	protected DataOutputStream saida;
	protected DataInputStream entrada;

	public Cliente() {

		ENDERECO_SERVIDOR = "localhost";
		PORTO_SERVIDOR = 5001;

	}

	public Cliente(String enderecoDoServidor, int portoDoServidor) {

		ENDERECO_SERVIDOR = enderecoDoServidor;
		PORTO_SERVIDOR = portoDoServidor;

	}

	public void executar() {
		System.out.println(" -------- " + VERSAO);

		try {

			// 1- ETAPA DE ABERTURA DA CONEXÃO COM O SERVIDOR
			conectarAoServidor();
			obtemFluxosComunicacao();

			// 2- ETAPADA DE COMUNICAÇÃO ENTRE CLIENTE E SERVIDOR
			// envia e recebe mensagens
			comunicaComServidor();

			// 3 - ETAPA DE FECHAMENTO DA CONEXÃO COM O SERVIDOR
			fechaConexao();

		} catch (IOException ex) {

			System.err.println(ex.getMessage());

		}
	}

	private void conectarAoServidor() throws IOException {

		soqueteCliente = new Socket(InetAddress.getByName(ENDERECO_SERVIDOR),
				PORTO_SERVIDOR);

		System.out.println(DESCRICAO + " [INFO]  conectado ao servidor de IP "
				+ soqueteCliente.getInetAddress() + " (o porto local é "
				+ soqueteCliente.getLocalPort() + ").");

	}

	private void obtemFluxosComunicacao() throws IOException {

		ObjectOutputStream fluxoSaida = new ObjectOutputStream(
				soqueteCliente.getOutputStream());
		saida = new DataOutputStream(fluxoSaida);

		// descarrega o buffer enviando informações de cabeçalho
		saida.flush();

		ObjectInputStream fluxoEntrada = new ObjectInputStream(
				soqueteCliente.getInputStream());
		entrada = new DataInputStream(fluxoEntrada);

		System.out.println(DESCRICAO
				+ " [DEBUG] fluxos de comunicação obtidos.");

	}

	protected void recebeServidor(String nome) throws Exception {

		if (!new File("downloads/").exists()) {

			new File("downloads/").mkdir();

		}

		FileOutputStream fos = null;
		File file = new File("downloads/" + nome + ".enc");
		fos = new FileOutputStream(file);

		System.out.println(DESCRICAO + " [INFO] Arquivo Local Criado " + nome);
		System.out.println(DESCRICAO + " [INFO] Criptografando arquivo");

		// Prepara variaveis para transferencia
		byte[] cbuffer = new byte[1472];
		int bytesRead = 0;
		int iteracoes = 0;
		double sum = 0;

		long fileLenght = entrada.readLong();

		System.out.println(DESCRICAO + " [INFO] Recebendo Arquivo '"
				+ file.getName() + "' de " + fileLenght + " bytes");

		long t = System.currentTimeMillis();

		while (sum < fileLenght) {

			bytesRead = entrada.read(cbuffer);
			System.out.println(DESCRICAO + " [INFO] Recebendo ["
					+ (++iteracoes) + "] " + bytesRead + " of " + fileLenght
					+ " bytes ("
					+ (int) (((sum += bytesRead) / fileLenght) * 100) + "%)");
			fos.write(cbuffer, 0, bytesRead);
			fos.flush();

		}

		fos.close();
		System.out.println(DESCRICAO + " [INFO] Arquivo recebido!");
		t = System.currentTimeMillis() - t;
		System.out.println(DESCRICAO + " [INFO] "
				+ (String.format("O processo levou %d ms\n", t)));

		// decriptando arquivo
		System.out.println(DESCRICAO + " [INFO] Descriptografando arquivo");
		new FileEncryption("DES/ECB/PKCS5Padding", "downloads/" + nome + ".enc")
				.decrypt();
		System.out.println(DESCRICAO
				+ " [INFO] Arquivo Descriptografado com sucesso!\n");

		// deletando arquivo encriptado
		new File("downloads/" + nome + ".enc").delete();

	}

	protected void enviaServidor(String nome) throws IOException {

		long t = System.currentTimeMillis();

		FileInputStream fileIn = null;

		byte[] cbuffer = new byte[1472];
		int bytesRead;
		int iteracoes = 0;
		double sum = 0;

		try {

			if (new File(nome).exists()) {
				saida.writeUTF(DESCRICAO + " [INFO] ARQUIVO ENCONTRADO!");
				// Criando arquivo que será transferido pelo cliente
				File file = new File(nome);
				// Encriptando arquivo
				System.out
						.println(DESCRICAO + " [INFO] Criptografando arquivo");
				new FileEncryption("DES/ECB/PKCS5Padding", nome).encrypt();
				File fileEncrypted = new File(nome + ".enc");
				fileIn = new FileInputStream(fileEncrypted);

				System.out.println(DESCRICAO + " [INFO] Enviando Arquivo '"
						+ file.getName() + " de " + file.length() + " bytes");

				saida.writeLong(file.length());
				saida.flush();

				while ((bytesRead = fileIn.read(cbuffer)) != -1) {// EOF

					System.out
							.println(DESCRICAO
									+ " [INFO] Enviando ["
									+ (++iteracoes)
									+ "] "
									+ bytesRead
									+ " de "
									+ file.length()
									+ " bytes ("
									+ (int) (((sum += bytesRead) / file
											.length()) * 100) + "%)");
					saida.write(cbuffer, 0, bytesRead);
					saida.flush();

				}

				System.out.println(DESCRICAO + " [INFO] Arquivo Enviado!");

				// deletando arquivo encriptado
				fileEncrypted.delete();

			} else {

				System.out.println(DESCRICAO + " [DEBUG] Arquivo inexistente");
				saida.writeUTF("null");
				saida.flush();

			}

			t = System.currentTimeMillis() - t;
			System.out.println(DESCRICAO + " [INFO] "
					+ (String.format("O processo levou %d ms\n", t)));

		} catch (Exception e) {

			System.out.println(DESCRICAO + " [DEBUG] ERRO " + e.toString());
			saida.writeUTF(e.toString());
			saida.flush();

		}

	}

	// Token para pegar os parâmetros que vem logo após o comando.

	private String obtemArquivo(StringTokenizer token) {

		String arquivo = "";

		while (token.hasMoreTokens()) {

			arquivo += token.nextToken();
			arquivo += " ";

		}

		return arquivo.substring(0, arquivo.length() - 1);

	}

	/*
	 * procedimento para comunidar com o servidor, onde será exibido o menu para
	 * o cliente e ocorrerão as chamadas dos procedimentos para cada comando
	 */

	protected void comunicaComServidor() throws IOException {

		try {

			System.out
					.println(DESCRICAO
							+ " [DEBUG] pronto para comunicar, digite algum comando.\n");

			String mensagemParaEnviar = null;
			String mensagemRecebida = null;

			Scanner teclado = new Scanner(System.in);

			do {

				mensagemParaEnviar = teclado.nextLine();

				StringTokenizer token = new StringTokenizer(mensagemParaEnviar,
						" ");

				switch (token.nextToken()) {

				case "get":
				case "RETR":

					saida.writeUTF(mensagemParaEnviar);
					saida.flush();

					mensagemRecebida = entrada.readUTF();
					System.out.println(mensagemRecebida);

					if (!mensagemRecebida.equals("null")) {

						recebeServidor(obtemArquivo(token));

					} else {

						System.out.println("Arquivo inexistente");

					}

					break;

				case "put":
				case "STOR":
					saida.writeUTF(mensagemParaEnviar);
					saida.flush();
					enviaServidor(obtemArquivo(token));

					break;

				case "rm":
				case "DELE":
				case "mkdir":
				case "MKD":
				case "ls":
				case "NLST":
				case "lsl":
				case "LIST":
				case "?":
				case "HELP":
					saida.writeUTF(mensagemParaEnviar);
					saida.flush();
					mensagemRecebida = entrada.readUTF();
					System.out.println(mensagemRecebida);
					break;

				case "exit":
				case "QUIT":
				case "#SAIR":
					saida.writeUTF(mensagemParaEnviar);
					saida.flush();
					mensagemParaEnviar = entrada.readUTF();

					break;

				default:

					System.out.println("Comando inválido");
					break;

				}

			} while (!mensagemParaEnviar.equals(COMANDO_SAIR));

			teclado.close();

		} catch (Exception e) {

			System.out.println(e.toString());
			saida.writeUTF(e.toString());
			saida.flush();

		}

	}

	private void fechaConexao() throws IOException {

		saida.close();
		entrada.close();
		soqueteCliente.close();

		System.out.println(DESCRICAO
				+ " [INFO] Conexão fechada com o servidor!");

	}

	public static void main(String[] args) {

		if (args.length == 2) {

			Cliente cliente1 = new Cliente(args[0], Integer.parseInt(args[1]));
			cliente1.executar();

		}

		else if (args.length == 0) {

			Cliente cliente1 = new Cliente();
			cliente1.executar();

		}

		else {

			System.out
					.println("\n\nModo de uso: java Cliente ENDERECO_SERVIDOR PORTO_SERVIDOR\n\n ou java Cliente\n\n");

		}

	}

}// class
