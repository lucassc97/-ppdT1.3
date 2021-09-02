# ppdT1.3

Trabalho 1.3 de PPD

Membros do grupo:
 - Franco Schmidt
 - Lucas Santana
 - Vinícius Risso

Considerações gerais sobre o trabalho:
 - Utilizamos rabbitmq em java para a comunicação entre as Threads.
 - Criadas as classes Node, BrokerMQTT e Client para a construção da nossa dht.
 - Cada node, o broker e o cliente são representados em Threads em nossa implementação.
 - A comunicação entre os nós, broker e cliente utiliza a mensageria fornecida pela interface do rabbitmq.

Considerações gerais sobre o boot:
 - A etapa de boot do trabalho acontece com a inicalização dos nós.
 - Cada nó irá enviar uma mensagem de Join(k) para a broker.
 - Após todos os nós terem publicado seu Join(k), o broker irá publicar uma mensagem a todos os nós com a lista de keys, onde cada nó irá encontrar a sua key e o nó antecessor e sucessor a ele.
 - Os nós irão receber mensagens de put(k,v) e get(k) após os mesmos terem conhecimento sobre a sua responsabilidade na dht (antecessor e sucessor).

Considerações específicas sobre o trabalho:
 - Todo o get(k) enviado, terá um getOk(v).
 - Todo o put(k, v) enviado, terá um putOk(k).

Considerações tecnicas sobre o trabalho:
 - Necessário java instalado.
 - Necessario docker instalado.
 - (!Importante) Após a instalação do docker, deve ser executado o comando docker seguir:
 - docker run -it --rm --name rabbitmq -p 5672:5672 -p 15672:15672 rabbitmq:3.9-management
 - Deve-se executar a classe "Main" para a inicialização do serviço.
 - Na classe Client existem alguns testes de put(k,v) e get(k).
 - Também existe uma interface onde o professor poderá digitar comandos de put(k,v) e get(k) via terminal.
 - Utilizamos alguns print's no trabalho para sinalizar o andamento das mensagens entre as Threads.

Valores experimentais obtidos:

 [xxx]
 [x] Send Client Put '0 , Borda do mundo!'
 [x] Received Broker Put: '0 , Borda do mundo!' 
 [x] Send Broker Put: '0 , Borda do mundo!'
 [x] Received Node Put nodeId: 580326651 key: 0 value: Borda do mundo!
 [x] Send node PutOK: '0' nodeId: 580326651
 [x] Received Client PutOK: '0' 
 [xxx]
 [x] Send Client Get'0'
 [x] Received Broker: '0' 
 [x] Send Broker: '0'
 [x] Received Node: '0' nodeId: 580326651
 [x] Send node GetOK: 'Borda do mundo!' nodeId: 580326651
 [x] Received Client GetOK: 'Borda do mundo!' 
 [xxx]
 [x] Send Client Put '229495846 , Olá Mundo!'
 [x] Received Broker Put: '229495846 , Olá Mundo!' 
 [x] Send Broker Put: '229495846 , Olá Mundo!'
 [x] Received Node Put nodeId: 580326651 key: 229495846 value: Olá Mundo!
 [x] Send node PutOK: '229495846' nodeId: 580326651
 [x] Received Client PutOK: '229495846' 
 [xxx]
 [x] Send Client Get'229495846'
 [x] Received Broker: '229495846' 
 [x] Send Broker: '229495846'
 [x] Received Node: '229495846' nodeId: 580326651
 [x] Send node GetOK: 'Olá Mundo!' nodeId: 580326651
 [x] Received Client GetOK: 'Olá Mundo!' 
 [xxx]
 [x] Send Client Put '4294967295 , Aqui é a Borda do Mundo!'
 [x] Received Broker Put: '4294967295 , Aqui é a Borda do Mundo!' 
 [x] Send Broker Put: '4294967295 , Aqui é a Borda do Mundo!'
 [x] Received Node Put nodeId: 580326651 key: 4294967295 value: Aqui é a Borda do Mundo!
 [x] Send node PutOK: '4294967295' nodeId: 580326651
 [x] Received Client PutOK: '4294967295' 
 [xxx]
 [x] Send Client Get'4294967295'
 [x] Received Broker: '4294967295' 
 [x] Send Broker: '4294967295'
 [x] Received Node: '4294967295' nodeId: 580326651
 [x] Send node GetOK: 'Aqui é a Borda do Mundo!' nodeId: 580326651
 [x] Received Client GetOK: 'Aqui é a Borda do Mundo!' 
 [xxx]
Utilize a interface padrão (via console):
get(key) ou put(key, value)