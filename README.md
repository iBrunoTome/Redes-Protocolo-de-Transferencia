# Protocolo Confiável de Transferência de Arquivos em Rede

Protocolo confiável de transferência de arquivos em rede. Trabalho realizado no 5º período de Ciência da Computação no Instituto Federal de Minas Gerais (IFMG) - Campus Formiga para a disciplina de Redes.

#### Integrantes
- Bruno Tomé
- Cláudio Menezes
- Ronan Nunes

#### Instruções Para Compilar
- Basta abrir o terminal, navegar até a pasta protocoloBCR e compilar usando ./compilar.sh

#### Como executar o programa no sistema Ubuntu
- No terminal, estando no diretório protocoloBCR, basta executarmos primeiro o servidor com o comando ./servidor.sh (passando como parâmetro, o número do porto, se esse argumento não for passado, será atribuído o porto 5001 como default), e logo após em outra aba do terminal, executarmos o cliente com o comando ./cliente.sh (passando como parâmetros: o ENDERECO_SERVIDOR e o PORTO_SERVIDOR, se tais argumentos não forem passados, serão atribuídos valores default localhost e 5001).

#### Observaçoes
- Os arquivos disponíveis para o cliente baixar, precisam estar no diretório arquivos/, e a pasta downloads precisa existir para que esses arquivos sejam armazenados.
- Os arquivos que o cliente envia para o servidor com o comando put, devem estar no diretório raiz.
