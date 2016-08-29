package br.gov.mec.aghu.exames.contratualizacao.business;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//import javax.ejb.Asynchronous;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;
//import javax.transaction.Transactional;
import javax.transaction.UserTransaction;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.configuracao.dao.AghArquivoProcessamentoDAO;
import br.gov.mec.aghu.configuracao.dao.AghArquivoProcessamentoLogDAO;
import br.gov.mec.aghu.core.business.BaseBMTBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.exames.contratualizacao.util.Detalhes;
import br.gov.mec.aghu.exames.contratualizacao.util.Header;
import br.gov.mec.aghu.exames.contratualizacao.util.SolicitacaoExame;
import br.gov.mec.aghu.exames.contratualizacao.util.retorno.Item;
import br.gov.mec.aghu.exames.contratualizacao.vo.ItemContratualizacaoVO;
import br.gov.mec.aghu.model.AelArquivoIntegracao;
import br.gov.mec.aghu.model.AelArquivoSolicitacao;
import br.gov.mec.aghu.model.AghArquivoProcessamento;
import br.gov.mec.aghu.model.AghArquivoProcessamentoLog;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.recursoshumanos.Pessoa;

@Stateless
public class ProcessadorArquivosContratualizacaoRN extends BaseBMTBusiness {
	private static final long serialVersionUID = 7439743103164192223L;
	
	private static final Log LOG = LogFactory.getLog(ProcessadorArquivosContratualizacaoRN.class);
	
	private int nrRegistrosProcesso;
	private int nrRegistrosProcessados;
	private String lineSeparator;
	private static final int MAX_TENTATIVAS = 3;
	
	@EJB
	private IParametroFacade parametroFacade;
	@EJB
	private IAghuFacade aghuFacade;
	
	@Inject
	AghArquivoProcessamentoLogDAO aghArquivoProcessamentoLogDAO;
	@Inject
	AghArquivoProcessamentoDAO aghArquivoProcessamentoDAO;

	int countPacientesErro = 0;
	int countPacientesSucesso = 0;
	
	
	private static final String QUEBRA_LINHA= "\n";
	private static final String ERRO_GENERICO_CONTRATUALIZACAO = "Erro: Ocorreu um erro ao processar a integração de exames. Entre em contato com o administrador";

	
	public void processar(Detalhes detalhes, String nomeArquivo, String nomeArquivoOriginal, Header headerIntegracao, String caminhoAbsoluto, RapServidores servidorLogado, String nomeMicrocomputador) throws ApplicationBusinessException {
		List<SolicitacaoExame> solicitacoes = detalhes.getSolicitacoes().getSolicitacaoExame();
		final Date inicio = new Date();
		
		this.iniciarImportacao(null);
		this.nrRegistrosProcesso = solicitacoes.size();

		AghArquivoProcessamento aghArquivo = aghuFacade.obterArquivoNaoProcessado(parametroFacade.buscarAghParametro(AghuParametrosEnum.P_SIGLA_EXAME).getVlrTexto(), nomeArquivo);
		aghArquivo.setDthrInicioProcessamento(new Date(inicio.getTime()));

		logarMensagens("Processando registros do arquivo " + nomeArquivoOriginal + ".\nNúmero de pacientes a processar: " + nrRegistrosProcesso + QUEBRA_LINHA, aghArquivo, inicio, null, null);

		List<Item> listaItensRetorno = new ArrayList<Item>();
		List<AelArquivoSolicitacao> listaArquivosSolicitacao = new ArrayList<AelArquivoSolicitacao>();
		//monta lista geral com os itens de retorno - #14603
		for (nrRegistrosProcessados = 0; nrRegistrosProcessados < nrRegistrosProcesso; nrRegistrosProcessados++) {
			logarMensagens("Importando paciente/solicitação "+(nrRegistrosProcessados+1), aghArquivo, inicio, null, null);
			
			AelArquivoSolicitacao aelArquivoSolicitacao = null;
			try {
				aelArquivoSolicitacao = getInformacoesProcessamentoArquivosON().montarDadosPadraoArquivoSolicitacao(solicitacoes.get(nrRegistrosProcessados));
			} catch (ApplicationBusinessException e) {
				getLogger().error(e.getMessage(), e);
			}
			
			List<br.gov.mec.aghu.exames.contratualizacao.util.retorno.Item> listRetorno = null;
			try {
				listRetorno = executarAcoesContratualizacao(solicitacoes.get(nrRegistrosProcessados), headerIntegracao, inicio, aghArquivo, aelArquivoSolicitacao, nomeMicrocomputador, servidorLogado);
				listaItensRetorno.addAll(listRetorno);
			} catch (Exception e) {
				logarErro(aghArquivo, inicio, null, e, ERRO_GENERICO_CONTRATUALIZACAO);
			}
			if (aelArquivoSolicitacao != null) {
				listaArquivosSolicitacao.add(aelArquivoSolicitacao);
			}
		}
		
		logarMensagens("Pacientes recebidos: "+nrRegistrosProcesso+"\nPacientes com solicitações  geradas: "+countPacientesSucesso+"\nPacientes recusados por erro: "+countPacientesErro, aghArquivo, inicio, 99, null);
		String arquivoRetorno = null;
		try {
			arquivoRetorno = getGravacaoArquivoXmlRetornoON().executarAcoesGravarXMLRetorno(listaItensRetorno, caminhoAbsoluto, nomeArquivoOriginal, nrRegistrosProcesso);
			logarMensagens("Arquivo " + arquivoRetorno + " gerado com sucesso", aghArquivo, inicio, 100, new Date());
		} catch (ApplicationBusinessException e) {
			getLogger().error(e.getMessage(), e);
			logarMensagens(e.getMessage(), aghArquivo, inicio, 100, new Date());
		} catch (Exception e) {
			getLogger().error(e.getMessage(), e);
			logarMensagens("Arquivo de retorno não foi gerado. Contate o administrador do sistema.", aghArquivo, inicio, 100, new Date());
		}
		AelArquivoIntegracao aelArquivoIntegracao = null;
		try {
			aelArquivoIntegracao = getInformacoesProcessamentoArquivosON().gravarRegistrosControleArquivo(detalhes, caminhoAbsoluto, nomeArquivoOriginal, arquivoRetorno, countPacientesSucesso, countPacientesErro);
			getInformacoesProcessamentoArquivosON().gravarRegistrosControleSolicitacao(listaArquivosSolicitacao, aelArquivoIntegracao);
		} catch (Exception e) {
			getLogger().error(e.getMessage(), e);
			logarMensagens("Não foi possível salvar informações de auditoria. Contate o administrador do sistema. " + e.getMessage(), aghArquivo, inicio, 100, new Date());
		}

		commitTransaction();
	}
	
	
	private void logarMensagens(String mensagem, AghArquivoProcessamento aghArquivo, Date inicio, Integer percent, Date fim) {
		StringBuilder sb = new StringBuilder("");
		sb.append(getDataAtualFormatada() + mensagem+ QUEBRA_LINHA);
		atualizarArquivo(aghArquivo, sb, inicio, percent, 100, null, 0, fim);
	}
	
	private void logarErro(AghArquivoProcessamento aghArquivo, Date inicio,Integer percent, Exception e, String mensagem) {
		logarMensagens(mensagem, aghArquivo, inicio, percent, null);
		countPacientesErro++;
		if (e != null) {
			getLogger().error(e.getMessage(), e);
		}
	}

	private String getDataAtualFormatada() {
		DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
		return dateFormat.format(new Date()) + " : ";
	}

	@SuppressWarnings("unchecked")
	private List<br.gov.mec.aghu.exames.contratualizacao.util.retorno.Item> executarAcoesContratualizacao(SolicitacaoExame solicitacaoExame, Header headerIntegracao, Date inicio, AghArquivoProcessamento aghArquivo, AelArquivoSolicitacao aelArquivoSolicitacao, String nomeMicrocomputador, RapServidores servidorLogado) throws InterruptedException, NumberFormatException, ApplicationBusinessException, ParseException {
		ContratualizacaoCommandManager acoes = new ContratualizacaoCommandManager();
		Map<String, Object> parametros = new HashMap<String, Object>();

		List<br.gov.mec.aghu.exames.contratualizacao.util.retorno.Item> listaItensRetorno = new ArrayList<br.gov.mec.aghu.exames.contratualizacao.util.retorno.Item>();
		
		parametros.put(ContratualizacaoCommand.PACIENTE_INTEGRACAO, solicitacaoExame.getPaciente());
		parametros.put(ContratualizacaoCommand.MEDICO_INTEGRACAO, solicitacaoExame.getMedicoSolicitante());
		parametros.put(ContratualizacaoCommand.HEADER_INTEGRACAO, headerIntegracao);
		parametros.put(ContratualizacaoCommand.SOLICITACAO_INTEGRACAO, solicitacaoExame);
		parametros.put(ContratualizacaoCommand.NOME_MICROCOMPUTADOR, nomeMicrocomputador);
		parametros.put(ContratualizacaoCommand.SERVIDOR_LOGADO, servidorLogado);
		List<ItemContratualizacaoVO> listaItensRetornoVO = null;
		String mensagemErroSolicitacao = null;
		try {
			for (ContratualizacaoCommand acao : acoes.getActions()) {
				parametros = acao.executar(parametros);
				if (acao.comitar()) {
					commitTransaction();
				}
			}
			
			//Ocorreu tudo ok: Grava os itens de retorno
			//Pode ter ocorrido erro em apenas um item. Logar o erro no log e na tela
			listaItensRetornoVO = (List<ItemContratualizacaoVO>) parametros.get(ContratualizacaoCommand.ITENS_SOLICITACAO_INTEGRACAO);
			String mensagemErro = existeItemErro(listaItensRetornoVO);
			if (StringUtils.isEmpty(mensagemErro)) {
				countPacientesSucesso++;
			} else {
				logarErro(aghArquivo, inicio, null, null, mensagemErro +"\n");
			}
			listaItensRetorno = getGravacaoArquivoXmlRetornoON().montarListaItensXMLRetornoSolicitacao(listaItensRetornoVO);
		} catch (ApplicationBusinessException e) {
			listaItensRetorno = getGravacaoArquivoXmlRetornoON().invalidarItensSolicitacao(solicitacaoExame, e.getMessage());
			logarErro(aghArquivo, inicio, null, e, "Erro: " + e.getMessage() +"\n");
			mensagemErroSolicitacao = e.getMessage();
		} catch (Exception e) {
			listaItensRetorno = getGravacaoArquivoXmlRetornoON().invalidarItensSolicitacao(solicitacaoExame, ERRO_GENERICO_CONTRATUALIZACAO);
			logarErro(aghArquivo, inicio, null, e, "Erro: Ocorreu um erro ao processar a integração de exames. Entre em contato com o administrador \n");
			mensagemErroSolicitacao = ERRO_GENERICO_CONTRATUALIZACAO;
		}
		try {
			getInformacoesProcessamentoArquivosON().montarDadosComplementaresArquivoSolicitacao(aelArquivoSolicitacao, listaItensRetornoVO, mensagemErroSolicitacao);
		} catch (ApplicationBusinessException e) {
			getLogger().error(e.getMessage(), e);
		}
		return listaItensRetorno;
	}
	
	private String existeItemErro(List<ItemContratualizacaoVO> listaItensRetornoVO) {
		StringBuffer retorno = new StringBuffer(40);
		if (listaItensRetornoVO != null) {
			for (ItemContratualizacaoVO item : listaItensRetornoVO) {
				if (StringUtils.isNotEmpty(item.getMensagemErro())) {
					retorno.append("\n- Erro no item com idExterno " + item.getIdExterno() +  " : " + item.getMensagemErro());
				}
			}
		}
		return retorno.toString();
	}

	private void iniciarImportacao(final Pessoa pessoa) {
//		if (pessoa != null) {
//			this.iniciarContexto(pessoa);
//		}
//		this.userTransaction = this.obterUserTransaction(null);
		this.lineSeparator = System.getProperty("line.separator");
		if (this.lineSeparator == null) {
			this.lineSeparator = QUEBRA_LINHA;
		}
	}

//	private void finalizarImportacao() {
//		try {
////			if (userTransaction != null && userTransaction.isActive() && !userTransaction.isCommitted()) {
//			if (userTransaction != null) {
//				userTransaction.commit();
//			}
//		} catch (Exception e) {
//			this.reIniciarTransacao(userTransaction);
//			getLogger().error(e.getMessage(), e);
//		}
////		entityManager.close();
//		this.end();
//	}

//	private void iniciarContexto(final Pessoa pessoa) {
//		final Identity identity = Identity.instance();
//		final Principal principal = new SimplePrincipal(pessoa.getLogin());
//		identity.acceptExternallyAuthenticatedPrincipal(principal);
//		Contexts.getConversationContext().set("org.jboss.seam.security.identity", identity);
//	}

//	@End
//	public void end() {
//		if (getLogger().isDebugEnabled()) {
//			getLogger().debug("Fim");
//		}
//	}

//	private UserTransaction obterUserTransaction(UserTransaction userTransaction) {
//		if (userTransaction == null) {
//			userTransaction = (UserTransaction) org.jboss.seam.Component.getInstance("org.jboss.seam.transaction.transaction");
//			try {
//				userTransaction.setTransactionTimeout(60 * 60 * 24); // um dia
//			} catch (Exception e) {
//				getLogger().error(e.getMessage(), e);
//			}
//		}
//		try {
//			if (userTransaction.isNoTransaction()) {
//				userTransaction.begin();
//			}
//			else {
//				userTransaction.commit();
//				userTransaction.begin();
//			}
//			if (userTransaction.isActive()) {
//				entityManager.joinTransaction();
//			}
//		} catch (Exception e) {
//			getLogger().error(e.getMessage(), e);
//		}
//		return userTransaction;
//	}

//	private UserTransaction reIniciarTransacao(UserTransaction userTx) {
//		if (userTx != null) {
//			try {
//				userTx.rollback();
//			} catch (Exception e1) {
//				getLogger().error(e1.getMessage(), e1);
//			}
//		}
//		return userTx;
//	}

//	private UserTransaction commit(UserTransaction userTx) {
//		try {
//			userTx.commit();
//		} catch (Exception e) {
//			userTx = obterUserTransaction(userTx);
//			try {
//				userTx.commit();
//			} catch (Exception e1) {
//				getLogger().error(e1.getMessage(), e1);
//			}
//		}
//		return obterUserTransaction(null);
//	}

//	@SuppressWarnings("unchecked")
//	protected <T> T obterDoContexto(Class<T> clazz) {
//		return (T) Component.getInstance(clazz, true);
//	}

	private AghArquivoProcessamento atualizarArquivo(AghArquivoProcessamento aghArquivo, StringBuilder log, final Date date,
			final Integer percent, final Integer peso, UserTransaction userTx, final int tentativas, final Date fimProcessamento) {
		//		UserTransaction userTx;
		//		if (userTransaction == null) {
		//			userTx = obterUserTransaction();
		//		} else {
		//			userTx = userTransaction;
		//		}
//		if(userTx == null){
//			userTx = obterUserTransaction(null);
//		}
		try {
			//			 userTx = obterUserTransaction();
			// recuperando objeto desatachado
			aghArquivo = aghuFacade.obterAghArquivoProcessamentoPorChavePrimaria(aghArquivo.getSeq());
			aghArquivo.setDthrUltimoProcessamento(date);
			if (percent == null) {
				int perc = (int) ((double) nrRegistrosProcessados / (double) nrRegistrosProcesso * peso);
				aghArquivo.setPercentualProcessado(perc > 100 ? 99 : Integer.valueOf(perc));
			} else {
				aghArquivo.setPercentualProcessado(percent > 100 ? 100 : Integer.valueOf(percent));
			}
			if (fimProcessamento != null) {
				aghArquivo.setDthrFimProcessamento(fimProcessamento);
			}

			this.aghArquivoProcessamentoDAO.merge(aghArquivo);
			this.aghArquivoProcessamentoDAO.flush();
			// desatachando objetos (performance)
			// this.entityManager.clear();
//			userTx = this.commit(userTx);
			userTx.commit();
		} catch (Exception e) {
//			userTx = reIniciarTransacao(userTx);
			rollbackTransaction();
//			userTx = obterUserTransaction(null);
			getLogger().error(e.getMessage(), e);
			if (tentativas < MAX_TENTATIVAS + 1) {
				return atualizarArquivo(aghuFacade.obterAghArquivoProcessamentoPorChavePrimaria(aghArquivo.getSeq()), log, date, percent, peso,
						userTx, (tentativas + 1), fimProcessamento);
			}
		}
		gravarLog(log, aghArquivo);
		return aghArquivo;
	}

	private void gravarLog(final StringBuilder log, final AghArquivoProcessamento aghArquivo) {
		if (aghArquivo != null && log != null && log.toString().trim().length() > 0) {
			AghArquivoProcessamentoLog arquivoProcessamentoLog = new AghArquivoProcessamentoLog(aghArquivo.getSeq(), log.toString());

			this.aghArquivoProcessamentoLogDAO.persistir(arquivoProcessamentoLog);
			this.aghArquivoProcessamentoLogDAO.flush();
//			userTx = this.commit(userTx);
			try {
				commitTransaction();
			} catch (Exception e) {
				getLogger().error(e.getMessage(), e);
				rollbackTransaction();
			}
			this.aghArquivoProcessamentoLogDAO.entityManagerClear(); // performance
		}
	}
	
	protected InformacoesProcessamentoArquivosON getInformacoesProcessamentoArquivosON() {
		return new InformacoesProcessamentoArquivosON();
	}

	protected GravacaoArquivoXmlRetornoON getGravacaoArquivoXmlRetornoON() {
		return new GravacaoArquivoXmlRetornoON();
	}


	@Override
	protected Log getLogger() {
		return LOG;
	}

}
