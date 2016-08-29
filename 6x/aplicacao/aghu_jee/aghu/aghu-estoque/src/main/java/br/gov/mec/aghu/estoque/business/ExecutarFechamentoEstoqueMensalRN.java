package br.gov.mec.aghu.estoque.business;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.inject.Inject;
import javax.transaction.UserTransaction;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.estoque.dao.SceEstoqueAlmoxarifadoDAO;
import br.gov.mec.aghu.estoque.dao.SceEstoqueGeralDAO;
import br.gov.mec.aghu.estoque.dao.SceEstqAlmoxMvtoDAO;
import br.gov.mec.aghu.estoque.dao.SceHistoricoEstoqueAlmoxarifadoDAO;
import br.gov.mec.aghu.estoque.dao.SceHistoricoEstoqueAlmoxarifadoMovimentoDAO;
import br.gov.mec.aghu.estoque.dao.SceHistoricoFechamentoMensalDAO;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.SceHistoricoFechamentoMensal;
import br.gov.mec.aghu.model.SceHistoricoFechamentoMensalId;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.utils.DateUtil;

/**
 * ORADB PROCEDURE SCEP_EXEC_FECH_MENS
 * @author aghu
 */
@Stateless
@TransactionManagement(TransactionManagementType.BEAN)
public class ExecutarFechamentoEstoqueMensalRN extends BaseBusiness {
	
	private static final String BR = " <br /> ";
	private static final long serialVersionUID = -487299959784199304L;
	private static final Log LOG = LogFactory.getLog(ExecutarFechamentoEstoqueMensalRN.class);
	
	@EJB
	private AtualizarPontoPedidoRN atualizarPontoPedidoRN;
	@EJB
	private GerarClassificacaoAbcRN gerarClassificacaoAbcRN;
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@Inject
	private SceHistoricoEstoqueAlmoxarifadoDAO sceHistoricoEstoqueAlmoxarifadoDAO;
	
	@EJB
	private IEstoqueFacade estoqueFacade;
	
	@Inject
	private SceHistoricoFechamentoMensalDAO sceHistoricoFechamentoMensalDAO;
	@Inject
	private SceEstqAlmoxMvtoDAO sceEstqAlmoxMvtoDAO;
	
	@EJB
	private IParametroFacade parametroFacade;
	
	@Inject
	private SceEstoqueGeralDAO sceEstoqueGeralDAO;
	@Inject
	private SceEstoqueAlmoxarifadoDAO sceEstoqueAlmoxarifadoDAO;
	@Inject
	private SceHistoricoEstoqueAlmoxarifadoMovimentoDAO sceHistoricoEstoqueAlmoxarifadoMovimentoDAO;
	
	@Resource
	private UserTransaction userTransaction;
	
	public enum ExecutarFechamentoEstoqueMensalRNExceptionCode implements BusinessExceptionCode {
		ERROR_EXECUCAO_ROTINA_FECHAMENTO_MENSAL,
		MENSAGEM_ROTINA_FECHAMENTO_MENSAL_MATERIAL_JA_EXECUTADA,
		MENSAGEM_ROTINA_FECHAMENTO_MENSAL_MATERIAL_ULTIMO_DIA_MES,
		MENSAGEM_ROTINA_FECHAMENTO_MENSAL_MATERIAL_HORARIO_APOS,
		MENSAGEM_ROTINA_FECHAMENTO_EM_EXECUCAO,
		MENSAGEM_GRAVACAO_ESTOQUE_NAO_CONFERE,
		MENSAGEM_GRAVACAO_ESTOQUE_MOVIMENTO_NAO_CONFERE,
		MENSAGEM_GRAVACAO_ESTOQUE_GERAIS_NAO_CONFERE;
	}
	
	@Override @Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	/**
	 * ORADB PROCEDURE SCEP_EXEC_FECH_MENS
	 */
	public StringBuilder executarFechamentoEstoqueMensal(RapServidores servidorLogado) throws BaseException {
		// Busca a data de competência do mês que está fechando
		final AghParametros parametroCompetencia = this.getParametroFacade()
				.buscarAghParametro(AghuParametrosEnum.P_COMPETENCIA);

		// Obtém a data de competência do mês
		final Date dataCompetencia = parametroCompetencia.getVlrData();
		Calendar calendarCompetencia = Calendar.getInstance();
		calendarCompetencia.setTime(parametroCompetencia.getVlrData());
		final int valorMesCompetencia = calendarCompetencia.get(Calendar.MONTH);

		// Obtém transação de usuário
		// UserTransaction userTransaction = obterUserTransaction(null);

		StringBuilder mensagemEtapasFechamento = new StringBuilder();
		// Executa as etapas de fechamento...

		// Etapa 1 - Início do fechamento mensal
		Short valorEtapaFechamento = this.executarEtapa1(
				mensagemEtapasFechamento, dataCompetencia, valorMesCompetencia);
		//this.commitUserTransaction(userTransaction); // COMMIT

		// Etapa 2 - Bloqueio do movimento de materiais
		valorEtapaFechamento = this.executarEtapa2(mensagemEtapasFechamento,
				valorEtapaFechamento, dataCompetencia);
		//this.commitUserTransaction(userTransaction); // COMMIT

		// Etapa 3 - Gravação do histórico de estoque almoxarifado
		valorEtapaFechamento = this.executarEtapa3(mensagemEtapasFechamento,
				valorEtapaFechamento, dataCompetencia, servidorLogado);
	//	this.commitUserTransaction(userTransaction); // COMMIT

		// Etapa 4 - Gravação do histórico de estoque almoxarifado
		// "movimento"
		valorEtapaFechamento = this.executarEtapa4(mensagemEtapasFechamento,
				valorEtapaFechamento, dataCompetencia);
	//	this.commitUserTransaction(userTransaction); // COMMIT

		// Etapa 5 - Gravação de Estoque Gerais
		valorEtapaFechamento = this.executarEtapa5(mensagemEtapasFechamento,
				valorEtapaFechamento, dataCompetencia);
	//	this.commitUserTransaction(userTransaction); // COMMIT

		// Etapa 6 - Limpeza do histórico de estoque almoxarifado
		// "movimento"
		valorEtapaFechamento = this.executarEtapa6(mensagemEtapasFechamento,
				valorEtapaFechamento, dataCompetencia);
		//this.commitUserTransaction(userTransaction); // COMMIT

		// Etapa 7 - Inicialização de nova competência
		valorEtapaFechamento = this.executarEtapa7(mensagemEtapasFechamento,
				valorEtapaFechamento, parametroCompetencia);
		//this.commitUserTransaction(userTransaction); // COMMIT

		// Etapa 8 - Cálculo ponto de pedido
		valorEtapaFechamento = this.executarEtapa8(mensagemEtapasFechamento,
				valorEtapaFechamento, dataCompetencia);
	//	this.commitUserTransaction(userTransaction); // COMMIT

		// Etapa 9 - Cálculo da classificação ABC
		valorEtapaFechamento = this.executarEtapa9(mensagemEtapasFechamento,
				valorEtapaFechamento, dataCompetencia);
		//this.commitUserTransaction(userTransaction); // COMMIT

		// Etapa 10 - Fim do fechamento mensal
		valorEtapaFechamento = this.executarEtapa10(mensagemEtapasFechamento,
				valorEtapaFechamento, dataCompetencia);
		//this.commitUserTransaction(userTransaction); // COMMIT

		return mensagemEtapasFechamento;
	}
	
	/**
	 * Etapa 1 - Início do fechamento mensal
	 * @param dataCompetencia
	 * @param valorMesCompetencia
	 * @return
	 * @throws BaseException
	 */
	protected Short executarEtapa1(StringBuilder mensagemEtapasFechamento,
			final Date dataCompetencia, final int valorMesCompetencia)
			throws BaseException {
		LOG.info("Inicio etapa 1");

		try {
			userTransaction.setTransactionTimeout(60 * 60 * 8);
			userTransaction.begin();

			// Se a data de competência do sistema for encontrada no histórico
			// com fechamento concluído, o mês foi fechado!
			if (this.getSceHistoricoFechamentoMensalDAO()
					.existeHistoricoFechamentoMensalConcluidoPorDataCompetencia(
							dataCompetencia)) {
				// Rotina de Fechamento Mensal de materiais já executada para a
				// competência atual
				throw new ApplicationBusinessException(
						ExecutarFechamentoEstoqueMensalRNExceptionCode.MENSAGEM_ROTINA_FECHAMENTO_MENSAL_MATERIAL_JA_EXECUTADA);
			}

			// Obtém a data atual
			final Date dataAtual = new Date();

			/*
			 * Verifica se é último dia do mês e busca dia atual e mês/ano atual
			 * Se o mês atual for igual o mês da competência e não for último
			 * dia do mês, não permite fechamento Mas, se já iniciou o mês
			 * seguinte e o mês anterior ainda não fechou, permite executar o
			 * fechamento (do mês anterior).
			 */
			if (!this.verificaUltimoDiaMes(dataAtual)
					&& this.comparaMesAtualMesCompetencia(dataAtual,
							valorMesCompetencia)) {
				// Rotina de Fechamento Mensal de materiais deve ser executada
				// no último dia do mês
				throw new ApplicationBusinessException(
						ExecutarFechamentoEstoqueMensalRNExceptionCode.MENSAGEM_ROTINA_FECHAMENTO_MENSAL_MATERIAL_ULTIMO_DIA_MES);
			}

			// Se for último dia do mês e antes das 23:50, não permite
			// fechamento
			if (this.verificaUltimoDiaMes(dataAtual)
					&& this.verificaHoraCompetenciaValida(dataAtual)) {
				// Rotina de fechamento mensal de materiais deve ser executada
				// somente após 23:50
				throw new ApplicationBusinessException(
						ExecutarFechamentoEstoqueMensalRNExceptionCode.MENSAGEM_ROTINA_FECHAMENTO_MENSAL_MATERIAL_HORARIO_APOS);
			}

			// Busca o valor da etapa de fechamento mensal
			Short valorEtapaFechamento = this
					.getSceHistoricoFechamentoMensalDAO()
					.obterValorEtapaHistoricoFechamentoMensal(dataCompetencia);

			if (valorEtapaFechamento == 0) {

				// INSERIR histórico de fechamento mensal
				this.inserirSceHistoricoFechamentoMensal(
						mensagemEtapasFechamento, dataCompetencia, new Date(),
						1, "INICIO FECHAMENTO MENSAL", null, null, false, 0);

				// ATUALIZA o valor da etapa de fechamento
				valorEtapaFechamento = 1;
			}

			userTransaction.commit();
			
			// Retorna o valor da etapa de fechamento atual!
			return valorEtapaFechamento;
		} catch (BaseException e) {
			LOG.error(e.getMessage(),e);
			try{
				userTransaction.rollback();
			}catch(Exception a){
				LOG.error(e.getMessage(),e);
			}
			throw e;
		} catch (Exception e) {
			LOG.error(e.getMessage(),e);
			try{
				userTransaction.rollback();
			}catch(Exception a){
				LOG.error(e.getMessage(),e);
			}
			throw new ApplicationBusinessException(ExecutarFechamentoEstoqueMensalRNExceptionCode.ERROR_EXECUCAO_ROTINA_FECHAMENTO_MENSAL);
		}

	}
	
	/**
	 * Etapa 2 - Bloqueio do movimento de materiais
	 * @param valorEtapaFechamento
	 * @param dataCompetencia
	 * @return
	 * @throws BaseException
	 */
	protected Short executarEtapa2(StringBuilder mensagemEtapasFechamento,
			Short valorEtapaFechamento, final Date dataCompetencia)
			throws BaseException {
		LOG.info("Inicio etapa 2");

		try {
			userTransaction.setTransactionTimeout(60 * 60 * 8);
			userTransaction.begin();

			if (valorEtapaFechamento < 2) {
				// INSERIR histórico de fechamento mensal
				this.inserirSceHistoricoFechamentoMensal(
						mensagemEtapasFechamento, dataCompetencia, new Date(),
						2, "BLOQUEIO MOVIMENTO MATERIAIS", null, null, false, 0);

				// ATUALIZA o valor da etapa de fechamento
				valorEtapaFechamento = 2;
			}

			userTransaction.commit();

			return valorEtapaFechamento;
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
			try {
				userTransaction.rollback();
			} catch (Exception a) {
				LOG.error(e.getMessage(), e);
			}
			throw new ApplicationBusinessException(
					ExecutarFechamentoEstoqueMensalRNExceptionCode.ERROR_EXECUCAO_ROTINA_FECHAMENTO_MENSAL);
		}
	}
	
	/**
	 * Etapa 3 - Gravação do histórico de estoque almoxarifado
	 * @param valorEtapaFechamento
	 * @param dataCompetencia
	 * @return
	 * @throws BaseException
	 */
	protected Short executarEtapa3(StringBuilder mensagemEtapasFechamento,
			Short valorEtapaFechamento, final Date dataCompetencia, RapServidores servidorLogado)
			throws BaseException {
		LOG.info("Inicio etapa 3");

		try {
			userTransaction.setTransactionTimeout(60 * 60 * 8);
			userTransaction.begin();

			if (valorEtapaFechamento < 3) {

				// Obtém a quantidade disponível de registros de estoque
				// almoxarifados
				Long quantidadeEstoqueAlmoxarifado = this
						.getSceEstoqueAlmoxarifadoDAO()
						.obterQuantidadeRegistros();

				/*
				 * Persiste Históricos de Estoque Almoxarifado para Fechamento
				 * de Estoque Mensal Obtém a quantidade de registros persistidos
				 * na operação
				 */
				Integer quantidadeRegistrosGravados = this
						.getSceHistoricoEstoqueAlmoxarifadoDAO()
						.persistirHistoricoEstoqueAlmoxarifadoFechamentoEstoqueMensal(
								dataCompetencia, servidorLogado);

				// Verifica se a quantidade de registros gravados corresponde a
				// quantidade de estoque almoxarifados disponíveis
				if (!(quantidadeEstoqueAlmoxarifado.longValue() == quantidadeRegistrosGravados.longValue())) {
					throw new ApplicationBusinessException(
							ExecutarFechamentoEstoqueMensalRNExceptionCode.MENSAGEM_GRAVACAO_ESTOQUE_NAO_CONFERE);
				}

				// INSERIR histórico de fechamento mensal
				this.inserirSceHistoricoFechamentoMensal(
						mensagemEtapasFechamento, dataCompetencia, new Date(),
						3, "GRAVAÇÃO SCE_HIST_ESTQ_ALMOXS",
						quantidadeRegistrosGravados, null, false, 0);

				// ATUALIZA o valor da etapa de fechamento
				valorEtapaFechamento = 3;
			}

			userTransaction.commit();

			return valorEtapaFechamento;
		} catch (BaseException e) {
			LOG.error(e.getMessage(), e);
			try {
				userTransaction.rollback();
			} catch (Exception a) {
				LOG.error(e.getMessage(), e);
			}
			throw e;
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
			try {
				userTransaction.rollback();
			} catch (Exception a) {
				LOG.error(e.getMessage(), e);
			}
			throw new ApplicationBusinessException(
					ExecutarFechamentoEstoqueMensalRNExceptionCode.ERROR_EXECUCAO_ROTINA_FECHAMENTO_MENSAL);
		}
	}
	
	/**
	 * Etapa 4 - Gravação do histórico de estoque almoxarifado "movimento"
	 * @param valorEtapaFechamento
	 * @param dataCompetencia
	 * @return
	 * @throws BaseException
	 */
	protected Short executarEtapa4(StringBuilder mensagemEtapasFechamento,
			Short valorEtapaFechamento, final Date dataCompetencia)
			throws BaseException {
		LOG.info("Inicio etapa 4");

		try {
			userTransaction.setTransactionTimeout(60 * 60 * 8);
			userTransaction.begin();

			if (valorEtapaFechamento < 4) {

				// Obtém a quantidade disponível de registros de estoque
				// almoxarifados movimentos
				Long quantidadeEstoqueAlmoxarifadoMovimento = this
						.getSceEstqAlmoxMvtoDAO().obterQuantidadeRegistros();

				/*
				 * Persiste Históricos de Estoque Almoxarifado Movimento para
				 * Fechamento de Estoque Mensal Obtém a quantidade de registros
				 * persistidos na operação
				 */
				Integer quantidadeRegistrosGravados = this
						.getSceHistoricoEstoqueAlmoxarifadoMovimentoDAO()
						.persistirHistoricoEstoqueAlmoxarifadoMovimentoFechamentoEstoqueMensal(
								dataCompetencia);

				// Verifica se a quantidade de registros gravados corresponde a
				// quantidade de estoque almoxarifados disponíveis
				if (! (quantidadeEstoqueAlmoxarifadoMovimento.longValue() ==  quantidadeRegistrosGravados.longValue())) {
					throw new ApplicationBusinessException(
							ExecutarFechamentoEstoqueMensalRNExceptionCode.MENSAGEM_GRAVACAO_ESTOQUE_MOVIMENTO_NAO_CONFERE);
				}

				// INSERIR histórico de fechamento mensal
				this.inserirSceHistoricoFechamentoMensal(
						mensagemEtapasFechamento, dataCompetencia, new Date(),
						4, "GRAVAÇÃO SCE_HIST_ESTQ_ALMOX_MVTOS",
						quantidadeRegistrosGravados, null, false, 0);

				// ATUALIZA o valor da etapa de fechamento
				valorEtapaFechamento = 4;
			}

			userTransaction.commit();

			return valorEtapaFechamento;
		} catch (BaseException e) {
			LOG.error(e.getMessage(), e);
			try {
				userTransaction.rollback();
			} catch (Exception a) {
				LOG.error(e.getMessage(), e);
			}
			throw e;
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
			try {
				userTransaction.rollback();
			} catch (Exception a) {
				LOG.error(e.getMessage(), e);
			}
			throw new ApplicationBusinessException(
					ExecutarFechamentoEstoqueMensalRNExceptionCode.ERROR_EXECUCAO_ROTINA_FECHAMENTO_MENSAL);
		}
	}
	
	/**
	 * Etapa 5 - Gravação de Estoque Gerais
	 * @param valorEtapaFechamento
	 * @param dataCompetencia
	 * @return
	 * @throws BaseException
	 */
	protected Short executarEtapa5(StringBuilder mensagemEtapasFechamento,
			Short valorEtapaFechamento, final Date dataCompetencia)
			throws BaseException {
		LOG.info("Inicio etapa 5");

		try {
			userTransaction.setTransactionTimeout(60 * 60 * 8);
			userTransaction.begin();

			if (valorEtapaFechamento < 5) {

				// Obtém a quantidade disponível de registros de estoque
				// almoxarifados
				Long quantidadeEstoqueGeralPorDataCompetencia = this
						.getSceEstoqueGeralDAO()
						.obterQuantidadeRegistrosPorDataCompetencia(
								dataCompetencia);

				/*
				 * Persiste Estoque Gerais para Fechamento de Estoque Mensal
				 * Obtém a quantidade de registros persistidos na operação
				 */
				Integer quantidadeRegistrosGravados = this
						.getSceEstoqueGeralDAO()
						.persistirEstoqueGeralFechamentoEstoqueMensal(
								dataCompetencia);

				// Verifica se a quantidade de registros gravados corresponde a
				// quantidade de estoque almoxarifados disponíveis
				if (! (quantidadeEstoqueGeralPorDataCompetencia.longValue() == quantidadeRegistrosGravados.longValue())) {
					throw new ApplicationBusinessException(
							ExecutarFechamentoEstoqueMensalRNExceptionCode.MENSAGEM_GRAVACAO_ESTOQUE_GERAIS_NAO_CONFERE);
				}

				// INSERIR histórico de fechamento mensal
				this.inserirSceHistoricoFechamentoMensal(
						mensagemEtapasFechamento, dataCompetencia, new Date(),
						5, "GRAVAÇÃO SCE_ESTQ_GERAIS",
						quantidadeRegistrosGravados, null, false, 0);

				// ATUALIZA o valor da etapa de fechamento
				valorEtapaFechamento = 5;
			}

			userTransaction.commit();

			return valorEtapaFechamento;
		} catch (BaseException e) {
			LOG.error(e.getMessage(), e);
			try {
				userTransaction.rollback();
			} catch (Exception a) {
				LOG.error(e.getMessage(), e);
			}
			throw e;
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
			try {
				userTransaction.rollback();
			} catch (Exception a) {
				LOG.error(e.getMessage(), e);
			}
			throw new ApplicationBusinessException(
					ExecutarFechamentoEstoqueMensalRNExceptionCode.ERROR_EXECUCAO_ROTINA_FECHAMENTO_MENSAL);
		}
	}
		
	/**
	 * Etapa 6 - Limpeza do histórico de estoque almoxarifado "movimento"
	 * @param valorEtapaFechamento
	 * @param dataCompetencia
	 * @return
	 * @throws BaseException
	 */
	protected Short executarEtapa6(StringBuilder mensagemEtapasFechamento,
			Short valorEtapaFechamento, final Date dataCompetencia)
			throws BaseException {
		LOG.info("Inicio etapa 6");

		try {
			userTransaction.setTransactionTimeout(60 * 60 * 8);
			userTransaction.begin();

			if (valorEtapaFechamento < 6) {
				final Long quantidadeEstoqueAlmoxarifadoMovimento = this
						.getSceEstqAlmoxMvtoDAO().obterQuantidadeRegistros();
				final Long quantidadeEstoqueGeralPorDataCompetencia = this
						.getSceHistoricoEstoqueAlmoxarifadoMovimentoDAO()
						.pesquisarHistoricoEstoqueAlmoxarifadoMovimentoPorDataCompetenciaCount(
								dataCompetencia);

				if (! (quantidadeEstoqueAlmoxarifadoMovimento.longValue() == quantidadeEstoqueGeralPorDataCompetencia.longValue())) {
					throw new ApplicationBusinessException(
							ExecutarFechamentoEstoqueMensalRNExceptionCode.MENSAGEM_GRAVACAO_ESTOQUE_MOVIMENTO_NAO_CONFERE);
				}

				// Limpa tabela SCE_ESTQ_ALMOX_MVTOS para receber movimentos do
				// novo mês
				Integer quantidadeRegistrosRemovidos = this
						.getSceEstqAlmoxMvtoDAO()
						.limparEstqAlmoxMvtoFechamentoEstoqueMensal();

				// INSERIR histórico de fechamento mensal
				this.inserirSceHistoricoFechamentoMensal(
						mensagemEtapasFechamento, dataCompetencia, new Date(),
						6, "LIMPEZA SCE_ESTQ_ALMOX_MVTOS",
						quantidadeRegistrosRemovidos, null, false, 0);

				// ATUALIZA o valor da etapa de fechamento
				valorEtapaFechamento = 6;
			}

			userTransaction.commit();

			return valorEtapaFechamento;
		} catch (BaseException e) {
			LOG.error(e.getMessage(), e);
			try {
				userTransaction.rollback();
			} catch (Exception a) {
				LOG.error(e.getMessage(), e);
			}
			throw e;
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
			try {
				userTransaction.rollback();
			} catch (Exception a) {
				LOG.error(e.getMessage(), e);
			}
			throw new ApplicationBusinessException(
					ExecutarFechamentoEstoqueMensalRNExceptionCode.ERROR_EXECUCAO_ROTINA_FECHAMENTO_MENSAL);
		}
	}
		
	/**
	 * Etapa 6 - Limpeza do histórico de estoque almoxarifado "movimento"
	 * @param valorEtapaFechamento
	 * @param dataCompetencia
	 * @return
	}
	
	/**
	 * Etapa 7 - Inicialização de nova competência
	 * @param valorEtapaFechamento
	 * @param parametroCompetencia
	 * @param dataCompetencia
	 * @return
	 * @throws BaseException
	 */
	protected Short executarEtapa7(StringBuilder mensagemEtapasFechamento,
			Short valorEtapaFechamento, final AghParametros parametroCompetencia)
			throws BaseException {
		LOG.info("Inicio etapa 7");

		try {
			userTransaction.setTransactionTimeout(60 * 60 * 8);
			userTransaction.begin();

			if (valorEtapaFechamento < 7) {
				final Date dataCompetencia = parametroCompetencia.getVlrData();

				// Acrescenta 1 mês a data de competência atual
				Calendar calendarNovaDataCompetencia = Calendar.getInstance();
				calendarNovaDataCompetencia.setTime(dataCompetencia); // Seta
																		// data
																		// de
																		// competência
																		// atual
				calendarNovaDataCompetencia.add(Calendar.MONTH, 1); // ADD_MONTHS(dt_competencia,
																	// 1)

				final Date novaDataCompetencia = calendarNovaDataCompetencia
						.getTime();
				parametroCompetencia.setVlrData(novaDataCompetencia);

				// Atualiza instância de AGH parâmetro contendo a data da
				// competência (P_COMPETENCIA)
				this.getParametroFacade()
						.setAghpParametro(parametroCompetencia);

				// Gera a descrição para a nova data de competência
				SimpleDateFormat dateFormat = new SimpleDateFormat("MM/yyyy");
				final String descricao = "INICIALIZAÇÃO NOVA COMPETÊNCIA - "
						+ dateFormat.format(novaDataCompetencia);

				// INSERIR histórico de fechamento mensal
				this.inserirSceHistoricoFechamentoMensal(
						mensagemEtapasFechamento, dataCompetencia, new Date(),
						7, descricao, null, null, false, 0);

				// ATUALIZA o valor da etapa de fechamento
				valorEtapaFechamento = 7;

			}

			userTransaction.commit();

			return valorEtapaFechamento;
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
			try {
				userTransaction.rollback();
			} catch (Exception a) {
				LOG.error(e.getMessage(), e);
			}
			throw new ApplicationBusinessException(
					ExecutarFechamentoEstoqueMensalRNExceptionCode.ERROR_EXECUCAO_ROTINA_FECHAMENTO_MENSAL);
		}
	}
	
	/**
	 * Etapa 8 - Cálculo ponto de pedido
	 * @param valorEtapaFechamento
	 * @param dataCompetencia
	 * @return
	 * @throws BaseException
	 */
	protected Short executarEtapa8(StringBuilder mensagemEtapasFechamento,
			Short valorEtapaFechamento, final Date dataCompetencia)
			throws BaseException {
		LOG.info("Inicio etapa 8");

		try {
			userTransaction.setTransactionTimeout(60 * 60 * 8);
			userTransaction.begin();

			if (valorEtapaFechamento < 8) {
				// Chamada para procedure SCEP_ATU_PTO_PEDIDO
				this.getAtualizarPontoPedidoRN().atualizarPontoPedido(
						dataCompetencia, null);

				// INSERIR histórico de fechamento mensal
				this.inserirSceHistoricoFechamentoMensal(
						mensagemEtapasFechamento, dataCompetencia, new Date(),
						8, "CÁLCULO PONTO DE PEDIDO", null, null, false, 0);

				// ATUALIZA o valor da etapa de fechamento
				valorEtapaFechamento = 8;
			}

			userTransaction.commit();

			return valorEtapaFechamento;
		} catch (BaseException e) {
			LOG.error(e.getMessage(), e);
			try {
				userTransaction.rollback();
			} catch (Exception a) {
				LOG.error(e.getMessage(), e);
			}
			throw e;
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
			try {
				userTransaction.rollback();
			} catch (Exception a) {
				LOG.error(e.getMessage(), e);
			}
			throw new ApplicationBusinessException(
					ExecutarFechamentoEstoqueMensalRNExceptionCode.ERROR_EXECUCAO_ROTINA_FECHAMENTO_MENSAL);
		}
	}
	
	/**
	 * Etapa 9 - Cálculo da classificação ABC
	 * @param valorEtapaFechamento
	 * @param dataCompetencia
	 * @return
	 * @throws BaseException
	 */
	protected Short executarEtapa9(StringBuilder mensagemEtapasFechamento,
			Short valorEtapaFechamento, final Date dataCompetencia)
			throws BaseException {
		LOG.info("Inicio etapa 9");

		try {
			userTransaction.setTransactionTimeout(60 * 60 * 8);
			userTransaction.begin();

			if (valorEtapaFechamento < 9) {

				// Chamada para procedure SCEP_GERA_CLAS_ABC
				this.getGerarClassificacaoAbcRN().gerarClassificacaoAbc(
						dataCompetencia, 2);

				// INSERIR histórico de fechamento mensal
				this.inserirSceHistoricoFechamentoMensal(
						mensagemEtapasFechamento, dataCompetencia, new Date(),
						9, "CÁLCULO CLASSIFICAÇÃO ABC", null, null, false, 0);

				// ATUALIZA o valor da etapa de fechamento
				valorEtapaFechamento = 9;
			}

			userTransaction.commit();

			return valorEtapaFechamento;
		} catch (BaseException e) {
			LOG.error(e.getMessage(), e);
			try {
				userTransaction.rollback();
			} catch (Exception a) {
				LOG.error(e.getMessage(), e);
			}
			throw e;
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
			try {
				userTransaction.rollback();
			} catch (Exception a) {
				LOG.error(e.getMessage(), e);
			}
			throw new ApplicationBusinessException(
					ExecutarFechamentoEstoqueMensalRNExceptionCode.ERROR_EXECUCAO_ROTINA_FECHAMENTO_MENSAL);
		}
	}
	
	/**
	 * Etapa 10 - Fim do fechamento mensal
	 * @param valorEtapaFechamento
	 * @param dataCompetencia
	 * @return
	 * @throws BaseException
	 */
	protected Short executarEtapa10(StringBuilder mensagemEtapasFechamento,
			Short valorEtapaFechamento, final Date dataCompetencia)
			throws BaseException {
		LOG.info("Inicio etapa 10");

		try {
			userTransaction.setTransactionTimeout(60 * 60 * 8);
			userTransaction.begin();

			if (valorEtapaFechamento < 10) {
				// INSERIR histórico de fechamento mensal
				this.inserirSceHistoricoFechamentoMensal(
						mensagemEtapasFechamento, dataCompetencia, new Date(),
						10, "FIM FECHAMENTO MENSAL - OK", null,
						"PROXIMO FECHAMENTO: ", true, 0);

				// ATUALIZA o valor da etapa de fechamento
				valorEtapaFechamento = 10;
			}

			userTransaction.commit();

			return valorEtapaFechamento;
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
			try {
				userTransaction.rollback();
			} catch (Exception a) {
				LOG.error(e.getMessage(), e);
			}
			throw new ApplicationBusinessException(
					ExecutarFechamentoEstoqueMensalRNExceptionCode.ERROR_EXECUCAO_ROTINA_FECHAMENTO_MENSAL);
		}
	}
	
	/* Métodos utilitários */

	/**
	 * Inserir histórico de fechamento mensal conforme a procedure PROCEDURE SCEP_EXEC_FECH_MENS
	 * @param dataCompetencia
	 * @param dataGeracao
	 * @param etapa
	 * @param descricao
	 * @param quantidadeRegistrosProcessados
	 * @param ocorrencia
	 * @param fechamentoConcluido
	 * @param version
	 */
	protected void inserirSceHistoricoFechamentoMensal(StringBuilder mensagemEtapasFechamento, final Date dataCompetencia,
			final Date dataGeracao, final Integer etapa, final String descricao, final Integer quantidadeRegistrosProcessados,
			final String ocorrencia, final Boolean fechamentoConcluido, final Integer version) {
		SceHistoricoFechamentoMensal historicoFechamentoMensal = new SceHistoricoFechamentoMensal();
		
		SceHistoricoFechamentoMensalId id = new SceHistoricoFechamentoMensalId();
		id.setDataCompetencia(dataCompetencia);
		id.setDataGeracao(dataGeracao);
		id.setEtapa(etapa != null ? etapa.shortValue() : null);
		id.setVersion(version);
		historicoFechamentoMensal.setId(id);
		
		historicoFechamentoMensal.setDescricao(descricao);
		historicoFechamentoMensal.setQuantidadeRegistrosProcessados(quantidadeRegistrosProcessados);
		historicoFechamentoMensal.setOcorrencia(ocorrencia);
		historicoFechamentoMensal.setFechamentoConcluido(fechamentoConcluido);
		
		this.getSceHistoricoFechamentoMensalDAO().persistir(historicoFechamentoMensal);
		this.getSceHistoricoFechamentoMensalDAO().flush();
		
		mensagemEtapasFechamento.append(String.format("\t * Etapa %d - %s", etapa, descricao));
	}

	/**
	 * Verifica se é o último dia do mês de acordo com a data de competência
	 * @param dataAtual
	 * @return
	 */
	protected Boolean verificaUltimoDiaMes(final Date dataAtual){
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(dataAtual);
		
		final Integer ultimoDiaMes = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
		final Integer diaAtual = calendar.get(Calendar.DAY_OF_MONTH);
		
		return diaAtual.equals(ultimoDiaMes);
	}
	
	/**
	 * Verifica o mês atual com o mês da data de competência
	 * @param dataAtual
	 * @param valorMesCompetencia
	 * @return
	 */
	protected Boolean comparaMesAtualMesCompetencia(final Date dataAtual, final Integer valorMesCompetencia){
		// Calendar com mês atual
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(dataAtual);
		
		final Integer mesAtual = calendar.get(Calendar.MONTH) + 1;
		
		return valorMesCompetencia.equals(mesAtual);
	}
	
	/**
	 * Verifica se a hora da data de competência é válido
	 * Obs. Rotina de fechamento mensal de materiais deve ser executada somente após 23:50
	 * @param dataAtual
	 * @return
	 */
	protected Boolean verificaHoraCompetenciaValida(final Date dataAtual) throws BaseException{
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(dataAtual);
		
		final AghParametros parametroHoraCompetencia =  this.getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_HORA_ATUAL_FECHAMENTO_ESTOQUE);

		// O parâmetro recebe em seu valor texto o seguinte formado: HH:mm
		String[] valor = parametroHoraCompetencia.getVlrTexto().split(":");
		calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(valor[0]));
		calendar.set(Calendar.MINUTE, Integer.parseInt(valor[1]));
	
		final Date horaValida = calendar.getTime();
		
		return dataAtual.before(horaValida);
	}
	
	
	/** Commit do userTransaction
	 * 
	 * @param userTransaction
	 * @param tentativas
	 * @return
	 */
	public UserTransaction commitUserTransaction(UserTransaction userTransaction) throws ApplicationBusinessException {
		try {
			this.flush();
		} catch (Exception e) {
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			e.printStackTrace(pw);
			String stringException = sw.toString();								
			
			String logTxt = "##### AGHU - Exception - erro no commit do userTransaction nivel 1 " + DateUtil.obterDataFormatadaHoraMinutoSegundo(new Date()) + BR 
				   + BR
				   + stringException;
			LOG.error("erro: " + logTxt, e);
		}finally {
			try {
				userTransaction.commit();
			} catch (Exception e1) {
				StringWriter sw = new StringWriter();
				PrintWriter pw = new PrintWriter(sw);
				e1.printStackTrace(pw);
				String stringException = sw.toString();								
				
				String logTxt = "##### AGHU - Exception - erro no commit do userTransaction nivel 2 " + DateUtil.obterDataFormatadaHoraMinutoSegundo(new Date()) + BR 
					   + BR
					   + stringException;
				LOG.error("erro: "+ logTxt, e1);
			}
		}		
		return userTransaction;
	}

	/**
	 * Getters para RNs e DAOs
	 */
	protected IParametroFacade getParametroFacade() {
		return parametroFacade;
	}	
	protected IEstoqueFacade getEstoqueFacade() {
		return this.estoqueFacade;
	}
	protected SceHistoricoFechamentoMensalDAO getSceHistoricoFechamentoMensalDAO() {
		return sceHistoricoFechamentoMensalDAO;
	}
	protected SceHistoricoEstoqueAlmoxarifadoDAO getSceHistoricoEstoqueAlmoxarifadoDAO() {
		return sceHistoricoEstoqueAlmoxarifadoDAO;
	}	
	protected SceHistoricoEstoqueAlmoxarifadoMovimentoDAO getSceHistoricoEstoqueAlmoxarifadoMovimentoDAO() {
		return sceHistoricoEstoqueAlmoxarifadoMovimentoDAO;
	}
	protected SceEstoqueAlmoxarifadoDAO getSceEstoqueAlmoxarifadoDAO() {
		return sceEstoqueAlmoxarifadoDAO;
	}
	protected SceEstqAlmoxMvtoDAO getSceEstqAlmoxMvtoDAO() {
		return sceEstqAlmoxMvtoDAO;
	}
	protected SceEstoqueGeralDAO getSceEstoqueGeralDAO() {
		return sceEstoqueGeralDAO;
	}
	protected AtualizarPontoPedidoRN getAtualizarPontoPedidoRN() {
		return atualizarPontoPedidoRN;
	}
	protected GerarClassificacaoAbcRN getGerarClassificacaoAbcRN() {
		return gerarClassificacaoAbcRN;
	}
	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}
	
}
