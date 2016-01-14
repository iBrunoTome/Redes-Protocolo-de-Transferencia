import java.io.*;
import java.net.*;

/**
 *
 * @author valadao, modificado por Bruno Tomé, Cláudio Menezes e Ronan Nunes
 */

public class ServidorMultiCliente extends Servidor {

	public ServidorMultiCliente() {

		super();
		VERSAO = "SERVIDOR v2.0 (MULTICLIENTE) ";

	}

	public ServidorMultiCliente(int portoDoServico) {

		super(portoDoServico);
		VERSAO = "SERVIDOR v2.0 (MULTICLIENTE) ";

	}

	@Override
	public void executar() {

		System.out.println("\t-------- " + VERSAO);

		try {

			// 1- ETAPA DE ABERTURA DO SOQUETE QUE RECEBERÁ AS CONEXÕES
			// abre o soquete onde os clientes conectarão
			abreSoqueteServidor();

			// laço que executa eternamente, atendendo um cliente por vez
			while (true) {

				// 1- ETAPA DE ESPERA E ABERTURA DA CONEXÃO COM O CLIENTE
				Socket novaConexaoCliente = esperaConexaoCliente();

				// repassa a conexão pro tratador lidar
				TratadorDeCliente novoTratador = criaTratadorDeCliente(novaConexaoCliente);

				// cria e inicia uma thread (multiprocessamento) para não
				// interromper o do servidor de receber novos clientes
				Thread novaThread = new Thread(novoTratador);
				novaThread.start();

			}

		} catch (IOException erro) {

			System.err.println(VERSAO + ": ERRO " + erro.toString());

		}
	}

	protected TratadorDeCliente criaTratadorDeCliente(Socket conexaoCliente) {

		return new TratadorDeCliente(conexaoCliente);

	}

	class TratadorDeCliente extends Servidor implements Runnable {

		private short codCliente = 0;

		TratadorDeCliente(Socket conexao) {

			super();

			this.conexaoCliente = conexao;
			this.codCliente = Servidor.contadorClientes;

			this.DESCRICAO = "TRATADOR#" + codCliente + "@"
					+ conexao.getInetAddress().getHostAddress();

		}

		public void run() {
			try {
				// 1- ETAPA DE PREPARAÇÃO PARA COMUNICAÇÃO COM O CLIENTE
				obtemFluxosComunicacao();

				// 2- ETAPADA DE COMUNICAÇÃO ENTRE SERVIDOR E CLIENTE
				// envia e recebe mensagens
				comunicaComCliente();

				// 3 - ETAPA DE FECHAMENTO DA CONEXÃO COM O SERVIDOR
				fechaConexao();

			} catch (IOException erro) {

				System.err.println(DESCRICAO + " " + erro.toString());

			}
		}

	}

	public static void main(String[] args) {

		if (args.length == 1) {

			final ServidorMultiCliente servidor1 = new ServidorMultiCliente(
					Integer.parseInt(args[0]));
			new Thread() {
				public void run() {
					servidor1.executar();
				}
			}.start();

		}

		else if (args.length == 0) {

			final ServidorMultiCliente servidor1 = new ServidorMultiCliente();
			new Thread() {
				public void run() {
					servidor1.executar();
				}
			}.start();

		}

		else {

			System.out
					.println("\n\nModo de uso: java ServidorMultiCliente PORTO_SERVIDOR\n\nou\n\n java ServidorMultiCliente\n\n");

		}

	}

}
