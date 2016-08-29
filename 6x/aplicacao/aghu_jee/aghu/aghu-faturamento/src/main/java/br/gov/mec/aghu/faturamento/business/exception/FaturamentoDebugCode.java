package br.gov.mec.aghu.faturamento.business.exception;


/**
 * Auto generated
 * @author Gustavo Kuhn Andriotti
 * Created on: 2011.03.03-17:01:16
 */
@SuppressWarnings("ucd")
public enum FaturamentoDebugCode {

	/**
	 * Savepoint {1}
	 */
	FAT_SAVEPOINT,
	/**
	 * Rollback {1}
	 */
	FAT_ROLLBACK,
	/**
	 * Mensagem [{1}]: {2}
	 */
	FAT_MSG_PARAM,
	/**
	 * Utilizado ({1}) > que a capacidade permitida ({2})
	 */
	FAT_FBC_UTIL_MAIOR_CAP,
	/**
	 * Cobrança de procedimento especial: IphPhoSeq {1} IphSeq {2} índice {3}
	 */
	FAT_IPH_COB_ESP,
	/**
	 * Valores para itens do modulo [{1}] para competencia {2}: servico hospitalar {3}, servico profissional {4}, SADT {5}, procedimento {6}, anestesista {7}
	 */
	FAT_IPH_VLR_ITEM,
	/**
	 * Ja existe registro para o mes/ano: {1}/{2} tipo: {3}
	 */
	FAT_SUT_REG_MES_ANO_EXISTE,
	/**
	 * Não existe registro para o mes/ano: {1}/{2} tipo: {3}
	 */
	FAT_SUT_REG_MES_ANO_NAO_EXISTE,
	/**
	 * Existe registro para de outro mes/ano do tipo: {1}
	 */
	FAT_SUT_REG_OUTRO_MES_ANO_EXISTE,
	/**
	 * Não existe registro para outro mes/ano do tipo {1} para mes {2} na competencia {3}/{4}
	 */
	FAT_SUT_REG_OUTRO_MES_ANO_NAO_EXISTE,
	/**
	 * Atualiza saldo, capacidade: {1}
	 */
	FAT_SUT_ATU_SALDO,
	/**
	 * Insere registro de saldo para o mes/ano: {1}/{2} tipo: {3}
	 */
	FAT_SUT_INS_SALDO,
	/**
	 * Erro ao inserir registro de saldo para o mes/ano: {1}/{2} tipo: {3}
	 */
	FAT_SUT_INS_SALDO_ERRO,
	/**
	 * Capacidade ({1}) insuficiente para UTI do tipo {2} para o mes {3} 
	 */
	FAT_SUT_CAP_INSUFICIENTE,
	/**
	 * Saldo ({1}) insuficiente no banco de UTI para descontar {2} diarias do tipo {3} para o mes {4} da competencia {5}/{6}
	 */
	FAT_SUT_SALDO_INSUFICIENTE,
	/**
	 * Saldo UTI atualizado, parametros: operacao {1}, ano {2}, mes {3}, diarias {4}, tipo {5}
	 */
	FAT_SUT_OPERACAO_REALIZADA,
	/**
	 * Processo encerramento/prévia: módulo {1}, etapa {2}, status {3} 
	 */
	FAT_SUS_ENC_MODULO,
	/**
	 * Módulo {1} nao apresenta competencias ativas 
	 */
	FAT_SUS_MODULO_SEM_FAT_COMP,
	/**
	 * Encontrou característica ({1}) em IPH {2} em índice {3} 
	 */
	FAT_SUS_ENCONTROU_CARACT,
	/**
	 * Nao foi possivel criar instancia de CGI DTO com entidades: CGI {1} CPH {2} IPH {3} PHI {4} Exception: {5}
	 */
	FAT_SUS_CGI_DTO,
	/**
	 * Nao foi possivel criar instancia de EGI DTO com entidades: EGI {1} CPH {2} IPH {3} PHI {4} Exception: {5}
	 */
	FAT_SUS_EGI_DTO,
	/**
	 * Arquivo gerado com sucesso. {0} registro(s) gerados em {1} arquivo(s).
	 */
	MSG_INFO_ARQ_GERADO,
	/**
	 * Geradas {1} linhas de {2} entradas no arquivo de saida {3}
	 */
	ARQ_GERADO,	
	/**
	 * [c:{0}] [p:{1}] [a:{2}] Erro convertando registro para entrada em arquivo de saida, erro: {3}
	 */
	ARQ_ERRO_GERANDO_ENTRADA_ARQUIVO,
	/**
	 * [c:null] [p:null] [a:{0}] Erro traduzindo lista de VOs, contendo {1} itens, para lista de entradas em arquivo, apenas {2} itens traduzidos
	 */
	ARQ_ERRO_GERACAO_ENTRADA_REG,
	/**
	 * Ocorreu um erro ajustando o timeout da transacao {0}, stack: {1}
	 */
	ARQ_ERRO_AJUSTE_TX_TIMEOUT,
	/**
	 * Ocorreu um erro buscando transacao por nome: {0}
	 */
	ARQ_ERRO_TX_NAO_ENCONTRADA,
	/**
	 * Arquivo DCIH, do tipo {1}, fechado. Arquivo local: {2}
	 */
	ARQ_SUS_FECHADO,
	/**
	 * Iniciando geracao de arquivo SUS para competencia {1}/{2} entre {3} e {4} com inicio em {5}
	 */
	ARQ_SUS_INI,
	/**
	 * Finalizada a geracao de arquivo SUS para competencia {1}/{2} entre {3} e {4} com inicio em {5}
	 */
	ARQ_SUS_FIM,
	/**
	 * Iniciando validacao de saldo de banco de capacidades para competencia {1}/{2}
	 */
	ARQ_SUS_ATU_SALDO_INI,
	/**
	 * Finalizada a validacao de saldo de banco de capacidades para competencia {1}/{2}
	 */
	ARQ_SUS_ATU_SALDO_FIM,
	/**
	 * [c:{0}] [p:{1}] [a:{2}] Erro processando entidade do tipo {3}. Messagem de erro {4}, stack: {5}
	 */
	ARQ_SUS_ERRO_ENTIDADE,
	/**
	 * Nao foi possivel encontrar qualquer conta para competencia {0}/{1} entre {2} e {3} com inicio em {4}
	 */
	ARQ_SUS_ERRO_VAZIO,
	/**
	 * [c:{0}] [p:null] [a:{1}] Nao foi possivel encontrar qualquer espelho para conta hospitalar {0}, arquivo {1}
	 */
	ARQ_SUS_ERRO_VAZIO_CTH,
	/**
	 * [c:{0}] [p:{1}] [a:null] Erro validando saldo do banco, operacao {2}, de capacidades com messagem {3}
	 */
	ARQ_SUS_ERRO_ATU_SALDO,
	/**
	 * [c:{0}] [p:{1}] [a:null] Paciente de prontuario {1} nao possui cartao do SUS
	 */
	ARQ_SUS_PAC_SEM_CNS,	
	/**
	 * [c:{0}] [p:null] [a:null] Paciente {1} sem prontuario e portanto sem cartao SUS
	 */
	ARQ_SUS_PAC_SEM_PRONTUARIO,	
	/**
	 * Numero de registros excedidos, maximo permitido para o tipo {0} eh de {1}, foram informados {2}, registros foram truncados e os excluidos sao: {3}
	 */
	ARQ_SUS_LISTA_REG_TRUNCADA,
	/**
	 * [c:{0}] [p:{1}] [a:{2}] Erro processando preparando dados de VO para registro do tipo {3} em arquivo, erro: {4}, stack: {5}
	 */
	ARQ_SUS_ERRO_TRADUCAO_VO_REG,
	/**
	 * [c:{0}] [p:{1}] [a:null] Espelho sem codigo DCI/DCIH
	 */
	ARQ_SUS_EAI_SEM_DCI_COD,
	/**
	 * [c:{0}] [p:{1}] [a:null] Ato medico sem identificacao do responsavel, ID ato {2}
	 */
	ARQ_SUS_AMA_SEM_IDENT_RESP,
	/**
	 * [c:{0}] [p:{1}] [a:null] Entrada OPM sem numero de nota fiscal, numero OPM.
	 */
	ARQ_SUS_OPM_SEM_NF,
	/**
	 * Iniciando geracao do arquivo CSV: {0}
	 */
	ARQ_CSV_INI,
	/**
	 * [c:{0}] [p:{1}] [a:{2}] Conta sem unidade funcional atribuida.
	 */
	ARQ_CSV_SEM_UNF,
	/**
	 * [c:null] [p:null] [a:{0}] Busca de entidades demorou {1} msecs, {2} secs
	 */
	ARQ_CSV_BUSCA_ENTIDADES,
	/**
	 * [c:null] [p:null] [a:{0}] Ajuste de entidades demorou {1} msecs, {2} secs
	 */
	ARQ_CSV_AJUSTE_ENTIDADES,
	/**
	 * Ajuste de propriedades de entidades demoraram: {0} secs
	 */
	ARQ_CSV_AJUSTE_ENTIDADES_STATS,
	;
}

