------------------------
| README PROTOCOLO BCR |
------------------------

Protocolo confiável de transferência de arquivos em rede. Trabalho de Redes, realizado no 5º período de Ciência da Computação no IFMG Campus Formiga.

(i) Como compilar:
	- Basta abrir o terminal, navegar até a pasta protocoloBCR e compilar usando ./compilar.sh

(ii) Como executar o programa no sistema Ubuntu
	- No terminal, estando no diretório protocoloBCR, basta executarmos primeiro o servidor com o comando ./servidor.sh (passando como parâmetro, o número do porto, se esse argumento não for passado, será atribuído o porto 5001 como default), e logo após em outra aba do terminal, executarmos o cliente com o comando ./cliente.sh (passando como parâmetros: o ENDERECO_SERVIDOR e o PORTO_SERVIDOR, se tais argumentos não forem passados, serão atribuídos valores default localhost e 5001).

(iii) Observaçoes:
	- Os arquivos disponíveis para o cliente baixar, precisam estar no diretório arquivos/, e a pasta downloads precisa existir para que esses arquivos sejam armazenados.
	- Os arquivos que o cliente envia para o servidor com o comando put, devem estar no diretório raiz.
