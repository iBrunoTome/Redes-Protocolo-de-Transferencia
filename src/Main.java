/**
 *
 * @author valadao, modificado por Bruno Tomé, Cláudio Menezes e Ronan Nunes
 */
public class Main {

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
					.println("\n\nModo de uso: java Main PORTO_SERVIDOR\n\nou\n\n java Main\n\n");

		}

	}

}
