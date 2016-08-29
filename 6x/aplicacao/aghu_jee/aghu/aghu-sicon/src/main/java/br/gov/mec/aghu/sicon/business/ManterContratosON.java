package br.gov.mec.aghu.sicon.business;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.StringTokenizer;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.compras.autfornecimento.business.IAutFornecimentoFacade;
import br.gov.mec.aghu.compras.pac.business.IPacFacade;
import br.gov.mec.aghu.dominio.DominioOrigemContrato;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioSituacaoEnvioContrato;
import br.gov.mec.aghu.dominio.DominioTipoGarantia;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.ScoAfContrato;
import br.gov.mec.aghu.model.ScoAfContratoJn;
import br.gov.mec.aghu.model.ScoAutorizacaoForn;
import br.gov.mec.aghu.model.ScoContrato;
import br.gov.mec.aghu.model.ScoContratoJn;
import br.gov.mec.aghu.model.ScoItensContrato;
import br.gov.mec.aghu.model.ScoLicitacao;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.sicon.dao.ScoAfContratoJnDAO;
import br.gov.mec.aghu.sicon.dao.ScoAfContratosDAO;
import br.gov.mec.aghu.sicon.dao.ScoContratoDAO;
import br.gov.mec.aghu.sicon.dao.ScoContratoJnDAO;
import br.gov.mec.aghu.sicon.dao.ScoItensContratoDAO;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.dominio.DominioOperacoesJournal;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.factory.BaseJournalFactory;

/**
 * Classe para utilização das regras negócio necessárias para o cadastro de
 * contratos manuais.
 * 
 * @author agerling
 * 
 */
@SuppressWarnings({"PMD.AghuTooManyMethods", "PMD.CyclomaticComplexity"})
@Stateless
public class ManterContratosON extends BaseBusiness {

	private static final Log LOG = LogFactory.getLog(ManterContratosON.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@EJB
	private IPacFacade pacFacade;
	
	@Inject
	private ScoAfContratoJnDAO scoAfContratoJnDAO;
	
	@Inject
	private ScoContratoDAO scoContratoDAO;
	
	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;
	
	@EJB
	private IAutFornecimentoFacade autFornecimentoFacade;
	
	@EJB
	private IParametroFacade parametroFacade;
	
	@Inject
	private ScoItensContratoDAO scoItensContratoDAO;
	
	@Inject
	private ScoContratoJnDAO scoContratoJnDAO;
	
	@Inject
	private ScoAfContratosDAO scoAfContratosDAO;

	private static final long serialVersionUID = -3022047931026430451L;

	public enum ManterContratoManualONExceptionCode implements
			BusinessExceptionCode {
		CONTRATO_MANUAL_OBRIGATORIO, MODALIDADE_OBRIGATORIA, INCISO_OBRIGATORIO
		, NAO_PERMITE_INCISO, LICITACAO_INEXISTENTE, UASG_RESP_LICITACAO_OBRIGATORIO
		, ERRO_CONTRATO_MANUAL_JA_EXISTENTE, DATA_FIM_MAIOR_DATA_INICIO
		, DATA_ASSINATURA_MAIOR_DATA_INICIO, CAMPO_OBSERVACAO_EXCEDEU_TAMANHO
		, MENSAGEM_VALOR_GARANTIA_OBRIGATORIO, MENSAGEM_TP_CONTRATO_INCOMPATIVEL;
	}

	public void inserirContratoManual(ScoContrato contratoManual)
			throws ApplicationBusinessException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();

		validaContrato(contratoManual);
		validaNumeroContrato(contratoManual);
		validaModalidade(contratoManual);
		validaIncisoSemPermissao(contratoManual);
		validaInciso(contratoManual);
		validaProcessoLicitacao(contratoManual);
		validaUASGResposavelLicitacao(contratoManual);
		validaVigenciaContrato(contratoManual);
		validaDataAssinaturaContrato(contratoManual);
		validaTamanhoCampoObservacao(contratoManual);
		validaValorTipoGarantia(contratoManual);

		contratoManual.setObjetoContrato(trataLetraInicial(contratoManual
				.getObjetoContrato()));
		contratoManual.setFundamentoLegal(trataLetraInicial(contratoManual
				.getFundamentoLegal()));
		contratoManual.setObservacao(trataLetraInicial(contratoManual
				.getObservacao()));

		contratoManual.setIndOrigem(DominioOrigemContrato.M);
		contratoManual.setCriadoEm(new Date());
		contratoManual.setServidor(servidorLogado);
		removeSpc(contratoManual);
		
		ScoContratoDAO scoContratoDAO = this.getScoContratoDAO();
		scoContratoDAO.persistir(contratoManual);
		scoContratoDAO.flush();
		
		inserirContratoJn(contratoManual);
	}

	/**
	 * Transforma em uppercase a primeira letra da string de entrada.
	 * 
	 * @param _textoOriginal
	 * @return string contendo a entrada com a inicial uppercase.
	 */
	private String trataLetraInicial(String _textoOriginal) {

		if (StringUtils.isNotBlank(_textoOriginal)) {

			StringBuffer texto = new StringBuffer(_textoOriginal);

			texto.setCharAt(0, Character.toUpperCase(_textoOriginal.charAt(0)));

			return texto.toString();
		}

		return null;
	}

	public void inserirContratoAutomatico(ScoContrato input)
			throws ApplicationBusinessException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		ScoAfContratosDAO scoAfContratosDAO = this.getScoAfContratosDAO();
		IAutFornecimentoFacade autFornecimentoFacade = this.getAutFornecimentoFacade();
		
		validaContrato(input);
		validaNumeroContrato(input);
		validaProcessoLicitacao(input);
		validaUASGResposavelLicitacao(input);
		validaVigenciaContrato(input);
		validaDataAssinaturaContrato(input);
		validaTamanhoCampoObservacao(input);
		validaValorTipoGarantia(input);		

		if(!input.getTipoContratoSicon().getIndInsereItens()) {
			throw new ApplicationBusinessException(
					ManterContratoManualONExceptionCode.MENSAGEM_TP_CONTRATO_INCOMPATIVEL);
		}
		
		input.setObjetoContrato(trataLetraInicial(input.getObjetoContrato()));
		input.setFundamentoLegal(trataLetraInicial(input.getFundamentoLegal()));
		input.setObservacao(trataLetraInicial(input.getObservacao()));

		input.setIndOrigem(DominioOrigemContrato.A);
		input.setCriadoEm(new Date());
		input.setServidor(servidorLogado);
		
		removeSpc(input);
		List<ScoAfContrato> afconts = input.getScoAfContratos();
		
		ScoContratoDAO scoContratoDAO = this.getScoContratoDAO();
		scoContratoDAO.persistir(input);
		scoContratoDAO.flush();
		
		inserirContratoJn(input);
		
		for (ScoAfContrato afcont : afconts) {
			// Gravação em ScoAfContratos
			afcont.setScoContrato(input);
			afcont.setCriadoEm(new Date());
			afcont.setServidor(servidorLogado);
			ScoAutorizacaoForn af = afcont.getScoAutorizacoesForn();
			scoAfContratosDAO.persistir(afcont);
			scoAfContratosDAO.flush();

			// Gravação em ScoAfContratoJn
			inserirAfContratoJn(afcont);

			// Atualização de ScoAutorizacaoForn
			af.setNroContrato(input.getNrContrato().intValue());
			af.setDtVenctoContrato(input.getDtFimVigencia());
			autFornecimentoFacade.atualizarAutorizacaoForn(af, true);
		}
	}

	private void removeSpc(ScoContrato contratoManual) {
		if (contratoManual.getObservacao() != null) {
			contratoManual.setObservacao(contratoManual.getObservacao().trim());
		}
		if (contratoManual.getObjetoContrato() != null) {
			contratoManual.setObjetoContrato(contratoManual.getObjetoContrato()
					.trim());
		}
		if (contratoManual.getFundamentoLegal() != null) {
			contratoManual.setFundamentoLegal(contratoManual
					.getFundamentoLegal().trim());
		}
	}
	
	public void alterarContratoManual(ScoContrato contratoManual)
			throws ApplicationBusinessException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		// Validação da Data de Assinatura para todos os tipos de contrato
		validaDataAssinaturaContrato(contratoManual);

		if (contratoManual.getIndOrigem() == DominioOrigemContrato.M) {
			validaContrato(contratoManual);
			validaModalidade(contratoManual);
			validaIncisoSemPermissao(contratoManual);
			validaNumeroContratoAlteracao(contratoManual);
			validaInciso(contratoManual);
			validaProcessoLicitacao(contratoManual);
			validaUASGResposavelLicitacao(contratoManual);
			validaVigenciaContrato(contratoManual);
			validaTamanhoCampoObservacao(contratoManual);
			removeSpc(contratoManual);
			validaValorTipoGarantia(contratoManual);
			validaItensContrato(contratoManual);

			contratoManual.setObjetoContrato(trataLetraInicial(contratoManual
					.getObjetoContrato()));
			contratoManual.setFundamentoLegal(trataLetraInicial(contratoManual
					.getFundamentoLegal()));
			contratoManual.setObservacao(trataLetraInicial(contratoManual
					.getObservacao()));

			contratoManual.setIndOrigem(DominioOrigemContrato.M);
			contratoManual.setAlteradoEm(new Date());
			contratoManual.setServidor(servidorLogado);

			if (contratoManual.getSituacao().equals(DominioSituacaoEnvioContrato.E)){
				contratoManual.setSituacao(DominioSituacaoEnvioContrato.AR);
			}

			// Grava dados na tabela Journal
			alterarContratoJn(contratoManual);
			
			ScoContratoDAO scoContratoDAO = this.getScoContratoDAO();
			scoContratoDAO.atualizar(contratoManual);
			scoContratoDAO.flush();
		}
	}

	public void alterarContratoAutomatico(ScoContrato contratoAutomatico)
			throws ApplicationBusinessException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		IAutFornecimentoFacade autFornecimentoFacade = this.getAutFornecimentoFacade();
		
		validaContrato(contratoAutomatico);
		validaNumeroContratoAlteracao(contratoAutomatico);
		validaUASGResposavelLicitacao(contratoAutomatico);
		validaVigenciaContrato(contratoAutomatico);
		validaTamanhoCampoObservacao(contratoAutomatico);
		removeSpc(contratoAutomatico);
		validaValorTipoGarantia(contratoAutomatico);
		validaItensContrato(contratoAutomatico);
		
		contratoAutomatico
				.setObjetoContrato(trataLetraInicial(contratoAutomatico
						.getObjetoContrato()));
		contratoAutomatico
				.setFundamentoLegal(trataLetraInicial(contratoAutomatico
						.getFundamentoLegal()));
		contratoAutomatico.setObservacao(trataLetraInicial(contratoAutomatico
				.getObservacao()));

		contratoAutomatico.setAlteradoEm(new Date());
		contratoAutomatico.setServidor(servidorLogado);

		// Grava dados na tabela Journal
		alterarContratoJn(contratoAutomatico);

		ScoContratoDAO scoContratoDAO = this.getScoContratoDAO();
		contratoAutomatico = scoContratoDAO.merge(contratoAutomatico);

		List<ScoAfContrato> scoAfContratos = getScoAfContratosDAO()
				.obterAfByContrato(contratoAutomatico);		
		
		for (ScoAfContrato afcont : scoAfContratos) {
			if (afcont.getScoAutorizacoesForn() != null) {
				if (!contratoAutomatico.getNrContrato().equals(
						afcont.getScoAutorizacoesForn().getNroContrato())) {

					afcont.getScoAutorizacoesForn().setNroContrato(
							contratoAutomatico.getNrContrato().intValue());

					autFornecimentoFacade.atualizarAutorizacaoForn(
							afcont.getScoAutorizacoesForn(), true);
				}
			}
		}

		atualizaAfsDtVencimentoContrato(contratoAutomatico);
	}

	public void atualizaAfsDtVencimentoContrato(ScoContrato input) {
		IAutFornecimentoFacade autFornecimentoFacade = this.getAutFornecimentoFacade();
		
		// Nova consulta ao contrato, atualiza sua lista de aditivos
		ScoContrato contrato = getScoContratoDAO()
				.obterContratoPorNumeroContrato(input.getNrContrato());
		getScoContratoDAO().desatachar(contrato);
		contrato = getScoContratoDAO().obterContratoPorNumeroContrato(
				input.getNrContrato());

		if (contrato.getIndOrigem() == DominioOrigemContrato.A) {
			List<ScoAfContrato> afContrato = getScoAfContratosDAO()
					.obterAfByContrato(contrato);

			for (ScoAfContrato aF : afContrato) {

				aF.getScoAutorizacoesForn().setDtVenctoContrato(
						contrato.getDataFimVigComAditivos());

				autFornecimentoFacade.atualizarAutorizacaoForn(aF.getScoAutorizacoesForn(), true);
			}
		}
	}

	public List<RapServidores> pesquisarServidorAtivoGestorEFiscalContrato(
			Object paramPesquisa) throws BaseException {

		List<Short> vinculos = new ArrayList<Short>();

		StringTokenizer stringParametro = new StringTokenizer(
				getParametroFacade()
						.buscarAghParametro(
								AghuParametrosEnum.P_VIN_GESTOR_FISCAL_CONTRATO)
						.getVlrTexto().trim(), ",");

		while (stringParametro.hasMoreTokens()) {
			String vinculoStr = stringParametro.nextToken();
			vinculos.add(Short.valueOf(vinculoStr));
		}

		return getColaboradorFacade().pesquisarServidorAtivoPorVinculos(
				paramPesquisa, vinculos);
	}

	private void validaContrato(ScoContrato contratoManual)
			throws ApplicationBusinessException {

		if (contratoManual == null) {
			throw new ApplicationBusinessException(
					ManterContratoManualONExceptionCode.CONTRATO_MANUAL_OBRIGATORIO);
		}
	}

	private void validaNumeroContrato(ScoContrato contratoManual)
			throws ApplicationBusinessException {

		// Verifica se contrato já existe.
		if (contratoManual.getNrContrato() != null) {
			if (getScoContratoDAO().numeroContratoJaExiste(
					contratoManual.getNrContrato())) {
				throw new ApplicationBusinessException(
						ManterContratoManualONExceptionCode.ERRO_CONTRATO_MANUAL_JA_EXISTENTE);
			}
		}
	}

	private void validaNumeroContratoAlteracao(ScoContrato contratoManual)
			throws ApplicationBusinessException {
		
		Long numContrOrig = getScoContratoDAO().obterOriginal(contratoManual).getNrContrato();
		
		if ((!contratoManual.getNrContrato().equals(numContrOrig))
				&& (getScoContratoDAO().numeroContratoJaExiste(contratoManual
						.getNrContrato()))) {
			throw new ApplicationBusinessException(
					ManterContratoManualONExceptionCode.ERRO_CONTRATO_MANUAL_JA_EXISTENTE);

		}
	}

	private void validaModalidade(ScoContrato contratoManual)
			throws ApplicationBusinessException {

		// Se o tipo de contrato for "CONTRATO", a modalidade é obrigatória

		if (contratoManual.getTipoContratoSicon() != null
				&& contratoManual.getTipoContratoSicon().getCodigoSicon()
						.equals(Integer.valueOf(50))
				&& contratoManual.getModalidadeLicitacao() == null) {
			throw new ApplicationBusinessException(
					ManterContratoManualONExceptionCode.MODALIDADE_OBRIGATORIA);
		}
	}

	private void validaIncisoSemPermissao(ScoContrato contratoManual)
			throws ApplicationBusinessException {

		// Obrigatoriedade Variável conforme a modalidade

		// Regra para não permitir preenchimento do Inciso
		if (contratoManual.getModalidadeLicitacao() != null
				&& contratoManual.getModalidadeLicitacao().getArtigo()
						.equals(DominioSimNao.N)
				&& StringUtils.isNotBlank(contratoManual.getInciso())) {
			throw new ApplicationBusinessException(
					ManterContratoManualONExceptionCode.NAO_PERMITE_INCISO);
		}
	}

	private void validaInciso(ScoContrato contratoManual)
			throws ApplicationBusinessException {

		// Obrigatoriedade Variável conforme a modalidade

		// Regra para exigir a obrigatoriedade do preenchimento do inciso
		if (contratoManual.getModalidadeLicitacao() != null
				&& contratoManual.getModalidadeLicitacao().getArtigo()
						.equals(DominioSimNao.S)
				&& StringUtils.isBlank(contratoManual.getInciso())) {
			throw new ApplicationBusinessException(
					ManterContratoManualONExceptionCode.INCISO_OBRIGATORIO);
		}
	}

	private void validaProcessoLicitacao(ScoContrato contratoManual)
			throws ApplicationBusinessException {

		if (contratoManual.getLicitacao() != null
				&& contratoManual.getLicitacao().getNumero() == null) {
			contratoManual.setLicitacao(null);
		} else {
			if (contratoManual.getLicitacao() != null
					&& contratoManual.getLicitacao().getNumero() != null) {
				ScoLicitacao licitacao = getPacFacade()
						.obterLicitacao(
								contratoManual.getLicitacao().getNumero());


				if (licitacao == null || licitacao.getNumero() == null) {
					throw new ApplicationBusinessException(
							ManterContratoManualONExceptionCode.LICITACAO_INEXISTENTE);
				}

				contratoManual.setLicitacao(licitacao);
			}
		}
	}

	private void validaUASGResposavelLicitacao(ScoContrato contratoManual)
			throws ApplicationBusinessException {

		if (contratoManual.getUasgSubrog() != null
				&& contratoManual.getUasgLicit() == null) {
			throw new ApplicationBusinessException(
					ManterContratoManualONExceptionCode.UASG_RESP_LICITACAO_OBRIGATORIO);
		}
	}

	private void validaVigenciaContrato(ScoContrato contratoManual)
			throws ApplicationBusinessException {

		if (contratoManual.getDtInicioVigencia() != null
				&& contratoManual.getDtFimVigencia() != null) {

			if (contratoManual.getDtFimVigencia().before(
					contratoManual.getDtInicioVigencia())) {
				throw new ApplicationBusinessException(
						ManterContratoManualONExceptionCode.DATA_FIM_MAIOR_DATA_INICIO);
			}
		}
	}

	private void validaDataAssinaturaContrato(ScoContrato contratoManual)
			throws ApplicationBusinessException {
		if (contratoManual.getDtInicioVigencia() != null
				&& contratoManual.getDtAssinatura() != null) {
			if (contratoManual.getDtAssinatura().after(
					contratoManual.getDtInicioVigencia())) {
				throw new ApplicationBusinessException(
						ManterContratoManualONExceptionCode.DATA_ASSINATURA_MAIOR_DATA_INICIO);
			}
		}
	}

	private void validaTamanhoCampoObservacao(ScoContrato contrato)
			throws ApplicationBusinessException {
		String obs = contrato.getObservacao();
		if (StringUtils.isNotBlank(obs) && obs.length() > 80) {
			throw new ApplicationBusinessException(
					ManterContratoManualONExceptionCode.CAMPO_OBSERVACAO_EXCEDEU_TAMANHO);
		}

	}

	private void validaValorTipoGarantia(ScoContrato contrato)
			throws ApplicationBusinessException {
		if (contrato.getValorGarantia().compareTo(BigDecimal.ZERO) == 0) {
			if (!DominioTipoGarantia.I.equals(contrato.getIndTipoGarantia())) {
				throw new ApplicationBusinessException(
						ManterContratoManualONExceptionCode.MENSAGEM_VALOR_GARANTIA_OBRIGATORIO);
			}
		}

	}
	
	private void validaItensContrato(ScoContrato contrato)
			throws ApplicationBusinessException {
		boolean devePossuirItens = contrato.getTipoContratoSicon()
				.getIndInsereItens();

		if (contrato.getIndOrigem().equals(DominioOrigemContrato.A)) {
			List<ScoAfContrato> afContrato = getScoAfContratosDAO()
					.obterAfByContrato(contrato);
			if (!afContrato.isEmpty() && !devePossuirItens) {
				throw new ApplicationBusinessException(
						ManterContratoManualONExceptionCode.MENSAGEM_TP_CONTRATO_INCOMPATIVEL);
			} else {
				List<ScoItensContrato> itensContrato = getScoItensContratoDAO()
						.getItensContratoByContrato(contrato);
				if (!itensContrato.isEmpty() && !devePossuirItens) {
					throw new ApplicationBusinessException(
							ManterContratoManualONExceptionCode.MENSAGEM_TP_CONTRATO_INCOMPATIVEL);
				}
			}
		}
	}

	private void inserirAfContratoJn(ScoAfContrato afContrato) {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		ScoAfContratoJn afContratoJn = BaseJournalFactory.getBaseJournal(
				DominioOperacoesJournal.INS, ScoAfContratoJn.class, servidorLogado != null ? servidorLogado.getUsuario() : null);

		afContratoJn.setSeq(afContrato.getSeq());
		afContratoJn
				.setScoAutorizacoesForn(afContrato.getScoAutorizacoesForn());
		afContratoJn.setScoContrato(afContrato.getScoContrato());

		getScoAfContratoJnDAO().persistir(afContratoJn);
		getScoAfContratoJnDAO().flush();
	}
	
	private void inserirContratoJn(ScoContrato contrato) {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		ScoContratoJn contratoJn = BaseJournalFactory.getBaseJournal(
				DominioOperacoesJournal.INS, ScoContratoJn.class, servidorLogado != null ? servidorLogado.getUsuario() : null);

		preencherContratoJn(contratoJn, contrato);

		getScoContratoJnDAO().persistir(contratoJn);
		getScoContratoJnDAO().flush();
	}

	private void alterarContratoJn(ScoContrato contrato) {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		ScoContrato contratoOriginal = this.getScoContratoDAO().obterOriginal(
				contrato);

		ScoContratoJn contratoJn = BaseJournalFactory.getBaseJournal(
				DominioOperacoesJournal.UPD, ScoContratoJn.class, servidorLogado != null ? servidorLogado.getUsuario() : null);

		preencherContratoJn(contratoJn, contratoOriginal);

		getScoContratoJnDAO().persistir(contratoJn);
		getScoContratoJnDAO().flush();
	}

	private void preencherContratoJn(ScoContratoJn contratoJn,
			ScoContrato contrato) {
		contratoJn.setSeq(contrato.getSeq());
		contratoJn.setCriterioReajusteContrato(contrato
				.getCriterioReajusteContrato());
		contratoJn.setTipoContratoSicon(contrato.getTipoContratoSicon());
		contratoJn.setNrContrato(contrato.getNrContrato());
		contratoJn.setFornecedor(contrato.getFornecedor());
		contratoJn.setUasgSubrog(contrato.getUasgLicit());
		contratoJn.setUasgLicit(contrato.getUasgLicit());
		contratoJn.setModalidadeLicitacao(contrato.getModalidadeLicitacao());
		contratoJn.setInciso(contrato.getInciso());
		contratoJn.setDtPublicacao(contrato.getDtPublicacao());
		contratoJn.setObjetoContrato(contrato.getObjetoContrato());
		contratoJn.setFundamentoLegal(contrato.getFundamentoLegal());
		contratoJn.setDtInicioVigencia(contrato.getDtInicioVigencia());
		contratoJn.setDtFimVigencia(contrato.getDtFimVigencia());
		contratoJn.setDtAssinatura(contrato.getDtAssinatura());
		contratoJn.setValorTotal(contrato.getValorTotal());
		contratoJn.setSituacao(contrato.getSituacao());
		contratoJn.setLicitacao(contrato.getLicitacao());
		contratoJn.setServidorGestor(contrato.getServidorGestor());
		contratoJn.setServidorFiscal(contrato.getServidorFiscal());
	}

	// DAOs
	protected ScoContratoDAO getScoContratoDAO() {
		return scoContratoDAO;
	}

	protected IParametroFacade getParametroFacade() {
		// return IparametroFacade;
		return this.parametroFacade;
	}

	protected ScoAfContratosDAO getScoAfContratosDAO() {
		return scoAfContratosDAO;
	}

	protected ScoContratoJnDAO getScoContratoJnDAO() {
		return scoContratoJnDAO;
	}

	protected ScoAfContratoJnDAO getScoAfContratoJnDAO() {
		return scoAfContratoJnDAO;
	}

	// Facades
	
	protected IPacFacade getPacFacade() {
		return pacFacade;
	}
	
	protected IRegistroColaboradorFacade getColaboradorFacade() {
		return registroColaboradorFacade;
	}
	
	protected IAutFornecimentoFacade getAutFornecimentoFacade(){
		return autFornecimentoFacade;
	}

	protected ScoItensContratoDAO getScoItensContratoDAO() {
		return scoItensContratoDAO;
	}

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}
	
}