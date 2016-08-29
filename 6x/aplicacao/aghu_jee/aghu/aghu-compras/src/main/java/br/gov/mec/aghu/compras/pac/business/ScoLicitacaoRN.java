package br.gov.mec.aghu.compras.pac.business;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.io.FileExistsException;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.business.ISchedulerFacade;
import br.gov.mec.aghu.casca.business.ICascaFacade;
import br.gov.mec.aghu.compras.business.IComprasFacade;
import br.gov.mec.aghu.compras.cadastrosbasicos.business.IComprasCadastrosBasicosFacade;
import br.gov.mec.aghu.compras.dao.ScoAndamentoProcessoCompraDAO;
import br.gov.mec.aghu.compras.dao.ScoCondicaoPgtoLicitacaoDAO;
import br.gov.mec.aghu.compras.dao.ScoFaseSolicitacaoDAO;
import br.gov.mec.aghu.compras.dao.ScoFormaPagamentoDAO;
import br.gov.mec.aghu.compras.dao.ScoItemLicitacaoDAO;
import br.gov.mec.aghu.compras.dao.ScoItemPropostaFornecedorDAO;
import br.gov.mec.aghu.compras.dao.ScoLicitacaoDAO;
import br.gov.mec.aghu.compras.dao.ScoLocalizacaoProcessoDAO;
import br.gov.mec.aghu.compras.dao.ScoModalidadeLicitacaoDAO;
import br.gov.mec.aghu.compras.dao.ScoParcelasPagamentoDAO;
import br.gov.mec.aghu.dominio.DominioSituacaoLicitacao;
import br.gov.mec.aghu.model.AghJobDetail;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.ScoAndamentoProcessoCompra;
import br.gov.mec.aghu.model.ScoCondicaoPgtoLicitacao;
import br.gov.mec.aghu.model.ScoFaseSolicitacao;
import br.gov.mec.aghu.model.ScoItemLicitacao;
import br.gov.mec.aghu.model.ScoLicitacao;
import br.gov.mec.aghu.model.ScoModalidadeLicitacao;
import br.gov.mec.aghu.model.ScoParcelasPagamento;
import br.gov.mec.aghu.paciente.vo.AghParametrosVO;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.core.utils.DateUtil;

@Stateless
public class ScoLicitacaoRN extends BaseBusiness {

	private static final String LICITACAO = "Licitação ";

	private static final String NAO_ENCONTRADA = " não encontrada";

	@EJB
	private ScoItemLicitacaoRN scoItemLicitacaoRN;
	
	@EJB
	private PregaoBancoBrasilRN pregaoBancoBrasilRN;
	
	private static final Log LOG = LogFactory.getLog(ScoLicitacaoRN.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@EJB
	private ICascaFacade cascaFacade;
		
	@Inject
	private ScoModalidadeLicitacaoDAO scoModalidadeLicitacaoDAO;
	
	@Inject
	private ScoFormaPagamentoDAO scoFormaPagamentoDAO;
	
	@Inject
	private ScoLicitacaoDAO scoLicitacaoDAO;
	
	@EJB
	private IComprasCadastrosBasicosFacade comprasCadastrosBasicosFacade;
	
	@Inject
	private ScoAndamentoProcessoCompraDAO scoAndamentoProcessoCompraDAO;
	
	@EJB
	private IComprasFacade comprasFacade;

	@EJB
	private ISchedulerFacade schedulerFacade;
	
	@Inject
	private ScoFaseSolicitacaoDAO scoFaseSolicitacaoDAO;
	
	@EJB
	private IParametroFacade parametroFacade;
	
	@Inject
	private ScoItemPropostaFornecedorDAO scoItemPropostaFornecedorDAO;
	
	@Inject
	private ScoCondicaoPgtoLicitacaoDAO scoCondicaoPgtoLicitacaoDAO;
	
	@Inject
	private ScoItemLicitacaoDAO scoItemLicitacaoDAO;
	
	@Inject
	private ScoLocalizacaoProcessoDAO scoLocalizacaoProcessoDAO;
	
	@Inject
	private ScoParcelasPagamentoDAO scoParcelasPagamentoDAO;
	
//	@Inject
//	private ScoLicitacaoImpressaoRN scoLicitacaoImpressaoRN;
	
	@EJB
	private ScoItemLicitacaoON scoItemLicitacaoON;

	
	private static final long serialVersionUID = -5922834917785524834L;
	
	public enum LicitacaoRNExceptionCode implements BusinessExceptionCode {
		MENSAGEM_ERRO_FREQ_COMPRAS, MENSAGEM_DT_LIMITE_ACEITE_MENOR, MENSAGEM_DT_LIM_AC_MAIOR_DT_ABERTURA, 
		MENSAGEM_DT_PUBLICACAO_MENOR, MENSAGEM_ARTIGO_INCISO_OBRIG, FREQ_COMPRAS_SO_POD_ALTR_CHEF, 
		FREQ_COMPRAS_MAIOR_PARAM, MENSAGEM_PAC_JA_JULGADO, ERRO_CLONE_FASE_SOLICITACAO,
		ERRO_ALTERAR_FASE_SOLICITACAO, ERRO_ALTERAR_ITEM_LICITACAO
	}

	/**
	 * Método que faz verificação antes de inserir uma ScoLicitação
	 * 
	 * @oradb SCOT_LCT_BRI
	 * @param ScoLicitacao
	 *            licitacao
	 * @throws ApplicationBusinessException
	 */
	private void preInserirLicitacao(ScoLicitacao licitacao) throws ApplicationBusinessException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();

		if (licitacao.getDthrAberturaProposta() != null) {
			licitacao.setHrAberturaProposta(licitacao.getDthrAberturaProposta().getHours());
		}

		licitacao.setServidorDigitado(servidorLogado);
		licitacao.setDtDigitacao(new Date());
		licitacao.setSituacao(DominioSituacaoLicitacao.GR);
		licitacao.setExclusao(false);

		/* RN3 - Estória #5195 - Gerar Pac */
		//this.validaDataLimiteAcProposta(licitacao);
		//this.validaDtLimiteDtAbertura(licitacao);
		//this.validaDtPublicacao(licitacao);
		this.validaArtigoObrigatorio(licitacao);

		/* RN5 - Estória #5195 - Gerar Pac */
		this.atualizaModLicitacao(licitacao);
	}

	private void posInserirLicitacao(ScoLicitacao licitacao) throws ApplicationBusinessException {

		/* RN7 - Estória #5195 - Gerar Pac */
		this.inluirProcessoAndamentoCompras(licitacao);
		
		/* RN15 - Estória #5195 - Gerar Pac */
		Integer numeroCondPag = this.inluirCondicaoPagamento(licitacao);
		this.incluirParcelasPagamentoCompras(numeroCondPag);
		
	}

	/**
	 * Método que inclui um andamento no Processo de Compra apos inserir uma
	 * licitacao
	 * 
	 * @oradb scot_lct_asI -> SCOP_ENFORCE_LCT_RULES
	 * @param ScoLicitacao
	 *            licitacao
	 * @throws ApplicationBusinessException
	 */
	private void inluirProcessoAndamentoCompras(ScoLicitacao licitacao) throws ApplicationBusinessException {
		ScoAndamentoProcessoCompra andamento = new ScoAndamentoProcessoCompra();
		AghParametros pLocalDefault = getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_LOCAL_ORIGINARIO_PADRAO_PROCESSO);

		andamento.setLicitacao(licitacao);
		andamento.setServidor(licitacao.getServidorDigitado());
		andamento.setDtEntrada(new Date());
		andamento.setLocalizacaoProcesso(this.getScoLocalizacaoProcessoDAO().obterLocalizacaoProcessoPeloResponsavelOuComLocalOriginario(
				licitacao.getServidorDigitado(), pLocalDefault.getVlrNumerico().shortValue()));
		this.getScoAndamentoProcessoCompraDAO().persistir(andamento);
		this.getScoAndamentoProcessoCompraDAO().flush();
	}
	
	/**
	 * @oradb SCOT_LCT_ASU -> SCOP_ENFORCE_LCT_RULES
	 * @param freqCompras
	 * @throws ApplicationBusinessException
	 * @throws ApplicationBusinessException
	 */

	private void validaFreqComprasParametro(Integer freqCompras) throws ApplicationBusinessException {

		AghParametros parametro = getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_MAXIMO_FREQ_COMPRAS);

		if (freqCompras.intValue() > parametro.getVlrNumerico().intValue()) {
			throw new ApplicationBusinessException(LicitacaoRNExceptionCode.FREQ_COMPRAS_MAIOR_PARAM);
		}
	}

	private void validaArtigoObrigatorio(ScoLicitacao licitacao) throws ApplicationBusinessException {
		if (this.getScoModalidadeLicitacaoDAO().obterQuantidadeArtigosPorCodigo(licitacao.getModalidadeLicitacao().getCodigo()) > 0
				&& (licitacao.getArtigoLicitacao() == null || StringUtils.isBlank(licitacao.getIncisoArtigoLicitacao().trim()))) {
			throw new ApplicationBusinessException(LicitacaoRNExceptionCode.MENSAGEM_ARTIGO_INCISO_OBRIG);
		}
	}

	public void inserirLicitacao(ScoLicitacao licitacao) throws ApplicationBusinessException {

		this.preInserirLicitacao(licitacao);
		this.getScoLicitacaoDAO().persistir(licitacao);
		this.posInserirLicitacao(licitacao);
	}
	
	/**
	 * Validar as regras de trigger antes de alterar uma ScoLicitacao
	 * 
	 * @oradb scot_lct_asU -> SCOP_ENFORCE_LCT_RULES
	 * @param licitacao
	 * @throws ApplicationBusinessException
	 * @throws ApplicationBusinessException
	 */

	private void preAlterarLicitacao(ScoLicitacao licitacao, ScoLicitacao licitacaoClone) throws ApplicationBusinessException,
			ApplicationBusinessException {
		SimpleDateFormat yyyy = new SimpleDateFormat("yyyy");
		Integer anoAtual = Integer.valueOf(yyyy.format(new Date()));

				
		/* RN4, RN9 e RN10 - Estória #5195 - Gerar Pac */
		this.validaArtigoObrigatorio(licitacao);

		if (licitacao.getExclusao() != licitacaoClone.getExclusao()) {
			this.alteraExclusaoPac(licitacao);
		}
		if (!licitacao.getModalidadeLicitacao().getCodigo().equalsIgnoreCase(licitacaoClone.getModalidadeLicitacao().getCodigo())) {

			if (licitacao.getAnoComplemento() == null){
			   this.atualizaModLicitacao(licitacao);
			}
			else if (licitacao.getAnoComplemento().intValue() == anoAtual.intValue()){
			   /* RN6- Estória #5195 - Gerar Pac */
			   this.editaModLicitacao(licitacao);
			}
		}

		/* RN8- Estória #5195 - Gerar Pac */
		if ((licitacao.getFrequenciaEntrega() != null && licitacaoClone.getFrequenciaEntrega() == null)
				|| (licitacao.getFrequenciaEntrega().intValue() != licitacaoClone.getFrequenciaEntrega().intValue())) {
			Long contProp = 0l;

			contProp = getScoItemPropostaFornecedorDAO().obterQuantidadePropostasEscolhidasPeloNumLicitacao(licitacao.getNumero());
			if (contProp > 0) {
				RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
				
				if (!getICascaFacade().usuarioTemPermissao(servidorLogado != null ? servidorLogado.getUsuario() : null, "permiteAlterCriticasSupr")) {
					throw new ApplicationBusinessException(LicitacaoRNExceptionCode.FREQ_COMPRAS_SO_POD_ALTR_CHEF);
				}
			}
		}

		this.validaFreqComprasParametro(licitacao.getFrequenciaEntrega());
	}

	/**
	 * @oradb SCOP_DELETE_ITL_ROWS
	 * @oradb SCOP_RETORN_ITL_ROWS
	 * @param licitacao
	 */
	public void alteraExclusaoPac(ScoLicitacao licitacao) throws ApplicationBusinessException {

		if (licitacao.getExclusao()) {
			RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
			
			licitacao.setServidorExcluido(servidorLogado);
			licitacao.setDtExclusao(new Date());
			
			// alterar itens licitacao e fases
			this.excluirItensDaLicitacao(licitacao);
			this.alterarExclusaFasesDaLicitacao(licitacao);
		} else {
		 	licitacao.setServidorExcluido(null);
			licitacao.setDtExclusao(null);
			// alterar itens licitacao e fases
			this.ativarItensDaLicitacao(licitacao);
			this.ativarFasesDaLicitacao(licitacao);

		 }
	}
	
	private void ativarItensDaLicitacao(ScoLicitacao licitacao) throws ApplicationBusinessException{
		List<ScoItemLicitacao> listaItens = licitacao.getItensLicitacao();
		if (listaItens!=null && listaItens.size()>0){
			for (ScoItemLicitacao item: listaItens){
				if (item.getExclusao()){
					item.setDtExclusao(null);
					item.setMotivoExclusao(null);
					item.setExclusao(false);
					try {
						this.getScoItemLicitacaoRN().atualizarItemLicitacao(item);	
						scoItemLicitacaoON.alternarPontosParadaSolicitacao(item.getId().getLctNumero(),item.getId().getNumero());
					} catch (BaseException e) {
						throw new ApplicationBusinessException(LicitacaoRNExceptionCode.ERRO_ALTERAR_ITEM_LICITACAO);
					}
					
					
				}	
			}
		}
	}
	
	private void ativarFasesDaLicitacao(ScoLicitacao licitacao) throws ApplicationBusinessException{
		
		List<ScoFaseSolicitacao> listaFases = this.getComprasFacade().pesquisarFasePorLicitacaoNumero(licitacao.getNumero());
		if (listaFases!=null && listaFases.size()>0){
			for (ScoFaseSolicitacao fase: listaFases){
				if (fase.getExclusao()){
					ScoFaseSolicitacao faseSolicitacaoOld = null;
					try {
						faseSolicitacaoOld = (ScoFaseSolicitacao) BeanUtils.cloneBean(fase);
					} catch (Exception e) {
						throw new ApplicationBusinessException(LicitacaoRNExceptionCode.ERRO_CLONE_FASE_SOLICITACAO);
					}
					fase.setDtExclusao(null);
					fase.setExclusao(false);
					try {
						this.getComprasFacade().atualizarScoFaseSolicitacao(fase,faseSolicitacaoOld);
					} catch (BaseException e) {
						throw new ApplicationBusinessException(LicitacaoRNExceptionCode.ERRO_ALTERAR_FASE_SOLICITACAO);
					}
					
				}
			 }
		}
	}
	
	private void excluirItensDaLicitacao(ScoLicitacao licitacao) throws ApplicationBusinessException{
		List<ScoItemLicitacao> listaItens = licitacao.getItensLicitacao();
		if (listaItens!=null && listaItens.size()>0){
			for (ScoItemLicitacao item: listaItens){
				if (!item.getExclusao()){
					item.setDtExclusao(new Date());
					item.setMotivoExclusao(licitacao.getMotivoExclusao());
					item.setExclusao(true);
					try {
						this.getScoItemLicitacaoRN().atualizarItemLicitacao(item);
						scoItemLicitacaoON.alternarPontosParadaSolicitacao(item.getId().getLctNumero(),item.getId().getNumero());
					} catch (BaseException e) {
						throw new ApplicationBusinessException(LicitacaoRNExceptionCode.ERRO_ALTERAR_ITEM_LICITACAO);
					}
				}	
			}
		}
	}
	
	private void alterarExclusaFasesDaLicitacao(ScoLicitacao licitacao) throws ApplicationBusinessException{
		List<ScoFaseSolicitacao> listaFases = this.getComprasFacade().pesquisarFasePorLicitacaoNumero(licitacao.getNumero());
		if (listaFases!=null && listaFases.size()>0){
			for (ScoFaseSolicitacao fase: listaFases){
				if (!fase.getExclusao()){
					ScoFaseSolicitacao faseSolicitacaoOld = null;
					try {
						faseSolicitacaoOld = (ScoFaseSolicitacao) BeanUtils.cloneBean(fase);
					} catch (Exception e) {
						throw new ApplicationBusinessException(LicitacaoRNExceptionCode.ERRO_CLONE_FASE_SOLICITACAO);
					}
					fase.setDtExclusao(new Date());
					fase.setExclusao(true);
					try {
						this.getComprasFacade().atualizarScoFaseSolicitacao(fase,faseSolicitacaoOld);
					} catch (BaseException e) {
						throw new ApplicationBusinessException(LicitacaoRNExceptionCode.ERRO_ALTERAR_FASE_SOLICITACAO);
					}
					
				}
			 }
		}
		
	}
	private void posAlterarLicitacao(ScoLicitacao licitacao, ScoLicitacao licitacaoClone) 
										throws ApplicationBusinessException {
		/* RN8- Estória #5195 - Gerar Pac */
		if ((licitacao.getFrequenciaEntrega()!=null && licitacaoClone.getFrequenciaEntrega()==null)
				|| (licitacao.getFrequenciaEntrega().intValue()!= licitacaoClone.getFrequenciaEntrega().intValue())){
			Long contProp = getScoItemPropostaFornecedorDAO().obterQuantidadePropostasEscolhidasPeloNumLicitacao(licitacao.getNumero());
			if (contProp > 0) {
				RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
				
				if (!getICascaFacade().usuarioTemPermissao(servidorLogado != null ? servidorLogado.getUsuario() : null, "permiteAlterCriticasSupr")) {
					throw new ApplicationBusinessException(LicitacaoRNExceptionCode.FREQ_COMPRAS_SO_POD_ALTR_CHEF);
				}
			}
		}

		this.validaFreqComprasParametro(licitacao.getFrequenciaEntrega());
		
		/*RN9 e RN10  - Estória #5195 - Gerar Pac */
		if (licitacao.getExclusao()!=licitacaoClone.getExclusao()){
			this.alteraExclusaoPac(licitacao);
		}	
	}

	public void alterarLicitacao(ScoLicitacao licitacao, ScoLicitacao licitacaoClone) throws ApplicationBusinessException,
			ApplicationBusinessException {
		this.preAlterarLicitacao(licitacao, licitacaoClone);
		licitacao = this.getScoLicitacaoDAO().merge(licitacao);
		//this.getScoLicitacaoDAO().persistir(licitacao);
		this.posAlterarLicitacao(licitacao , licitacaoClone);
	}

	
	
	/**
	 * @ORADB SCOP_NUM_LICIT_EDIT
	 * @throws ApplicationBusinessException
	 */
	public void atualizaModLicitacao(ScoLicitacao licitacao) throws ApplicationBusinessException {

		SimpleDateFormat yyyy = new SimpleDateFormat("yyyy");
		AghParametros parametro = getParametroFacade().buscarAghParametro(AghuParametrosEnum.ANO_CORRENTE);

		final Date agora = new Date();

		Integer anoParam = Integer.valueOf(yyyy.format(parametro.getVlrData()));
		Integer anoAtual = Integer.valueOf(yyyy.format(agora));

		final IComprasCadastrosBasicosFacade comprasCadastrosBasicosFacade = this.getComprasCadastrosBasicosFacade();

		if (licitacao.getModalidadeLicitacao().getDocLicitAno()) {
			if (anoAtual.intValue() > anoParam.intValue()) {
				this.atualizarNumDocLicit(licitacao, anoAtual, comprasCadastrosBasicosFacade);
			}
		}
		final List<ScoModalidadeLicitacao> listModalidadeLicitacaoEdital = this.getScoModalidadeLicitacaoDAO().pesquisarScoModalidadeLicitacaoPorEditalAno(
				Boolean.TRUE);
		if (licitacao.getModalidadeLicitacao().getEditalAno()) {
			if (anoAtual.intValue() > anoParam.intValue()) {
				if (licitacao.getModalidadeLicitacao().getEdital()) {
					this.atualizarNumEdital(licitacao, anoAtual, listModalidadeLicitacaoEdital, 1);
					// this.getScoModalidadeLicitacaoDAO().updateEditalModLicitacao(Integer.valueOf(1));
					// this.getScoModalidadeLicitacaoDAO().flush();

					licitacao.setNumEdital(1);
					licitacao.setAnoComplemento(anoAtual);
				} else {
					this.atualizarNumEdital(licitacao, anoAtual, listModalidadeLicitacaoEdital, 0);
					// this.getScoModalidadeLicitacaoDAO().updateEditalModLicitacao(Integer.valueOf(0));
					// this.getScoModalidadeLicitacaoDAO().flush();
				}

				// Atualizar ANO_CORRENTE
				AghParametrosVO aghParam = new AghParametrosVO();
				aghParam.setNome("ANO_CORRENTE");
				aghParam.setVlrData(agora);

				getParametroFacade().setAghpParametro(aghParam, null, true);
			}
		}

		if (anoAtual.intValue() <= anoParam.intValue()) {

			if (licitacao.getModalidadeLicitacao().getDocLicitacao()) {
				licitacao.setNumDocLicit(licitacao.getModalidadeLicitacao().getNumDocLicit() == null ? 1 : Integer.valueOf(licitacao.getModalidadeLicitacao()
						.getNumDocLicit()) + 1);
				licitacao.setAnoComplemento(anoAtual);

				licitacao.getModalidadeLicitacao().setNumDocLicit(
						licitacao.getModalidadeLicitacao().getNumDocLicit() == null ? "1" : String.valueOf(Integer.valueOf(licitacao.getModalidadeLicitacao()
								.getNumDocLicit()) + 1));
				comprasCadastrosBasicosFacade.alterarModalidadeLicitacao(licitacao.getModalidadeLicitacao());
				// this.getScoModalidadeLicitacaoDAO().atualizar(licitacao.getModalidadeLicitacao());
				// this.getScoModalidadeLicitacaoDAO().flush();
			}

			if (licitacao.getModalidadeLicitacao().getEdital()) {
				Integer numEdital = getScoModalidadeLicitacaoDAO().obterMaxEditalScoModalidadeLicitacao();
				if (numEdital == null) {
					numEdital = 1;
				} else {
					numEdital += 1;
				}

				this.atualizarNumEdital(licitacao, anoAtual, listModalidadeLicitacaoEdital, numEdital);
				// getScoModalidadeLicitacaoDAO().updateMaxEditalLicitacoes();
				// this.getScoModalidadeLicitacaoDAO().flush();

				licitacao.setNumEdital(numEdital);
				licitacao.setAnoComplemento(anoAtual);

			}
		}
		this.atualizarModalidadeLicitacao(listModalidadeLicitacaoEdital, comprasCadastrosBasicosFacade);
	}

	private void atualizarModalidadeLicitacao(final List<ScoModalidadeLicitacao> listModalidadeLicitacaoEdital,
			final IComprasCadastrosBasicosFacade comprasCadastrosBasicosFacade) throws ApplicationBusinessException {
		for (final ScoModalidadeLicitacao scoModalidadeLicitacao : listModalidadeLicitacaoEdital) {
			comprasCadastrosBasicosFacade.alterarModalidadeLicitacao(scoModalidadeLicitacao);
		}
	}

	private void atualizarNumEdital(final ScoLicitacao licitacao, final Integer anoAtual, final List<ScoModalidadeLicitacao> listModalidadeLicitacaoEdital,
			final Integer numEdital) {
		for (final ScoModalidadeLicitacao scoModalidadeLicitacao : listModalidadeLicitacaoEdital) {
			scoModalidadeLicitacao.setNumEdital(numEdital);
		}
	}

	private void atualizarNumDocLicit(final ScoLicitacao licitacao, final Integer anoAtual, final IComprasCadastrosBasicosFacade comprasCadastrosBasicosFacade)
			throws ApplicationBusinessException {
		final List<ScoModalidadeLicitacao> listModalidadeLicitacao = this.getScoModalidadeLicitacaoDAO().pesquisarScoModalidadeLicitacaoPorDocLicitAno(
				Boolean.TRUE);
		if (licitacao.getModalidadeLicitacao().getDocLicitacao()) {
			for (final ScoModalidadeLicitacao scoModalidadeLicitacao : listModalidadeLicitacao) {
				scoModalidadeLicitacao.setNumDocLicit("1");
				comprasCadastrosBasicosFacade.alterarModalidadeLicitacao(scoModalidadeLicitacao);
			}
			// this.getScoModalidadeLicitacaoDAO().updateNumDocModLicitacao(Integer.valueOf(1));
			// this.getScoModalidadeLicitacaoDAO().flush();

			licitacao.setNumDocLicit(1);
			licitacao.setAnoComplemento(anoAtual);
		} else {
			for (final ScoModalidadeLicitacao scoModalidadeLicitacao : listModalidadeLicitacao) {
				scoModalidadeLicitacao.setNumDocLicit("0");
				comprasCadastrosBasicosFacade.alterarModalidadeLicitacao(scoModalidadeLicitacao);
			}
			// this.getScoModalidadeLicitacaoDAO().updateNumDocModLicitacao(Integer.valueOf(0));
			// this.getScoModalidadeLicitacaoDAO().flush();
		}
	}

	/**
	 * @ORADB SCOP_NUM_LICIT_EDIT_ATU
	 * @throws ApplicationBusinessException
	 */
	public void editaModLicitacao(ScoLicitacao licitacao) throws ApplicationBusinessException {

		Integer novoNumDoc = null;
		Integer novoNumEdital = null;

		if (licitacao.getModalidadeLicitacao().getDocLicitacao() || licitacao.getModalidadeLicitacao().getEdital()) {

			if (licitacao.getModalidadeLicitacao().getDocLicitacao()) {
				novoNumDoc = licitacao.getModalidadeLicitacao().getNumDocLicit() != null ? Integer.valueOf(licitacao.getModalidadeLicitacao().getNumDocLicit()) + 1
						: Integer.valueOf(1);
				licitacao.setNumDocLicit(novoNumDoc);
				licitacao.getModalidadeLicitacao().setNumDocLicit(novoNumDoc.toString());
			}

			if (licitacao.getModalidadeLicitacao().getEdital()) {
				novoNumEdital = licitacao.getModalidadeLicitacao().getNumEdital() != null ? Integer.valueOf(licitacao.getModalidadeLicitacao().getNumEdital()) + 1
						: Integer.valueOf(1);
				licitacao.setNumEdital(novoNumEdital);
				licitacao.getModalidadeLicitacao().setNumEdital(novoNumEdital);
			}

			this.getScoModalidadeLicitacaoDAO().atualizar(licitacao.getModalidadeLicitacao());
			this.getScoModalidadeLicitacaoDAO().flush();
		}
	}

	public Boolean verificarPermissoesPac(String login, Boolean gravar) {
		Boolean ret = Boolean.FALSE;

		if (gravar) {
			ret = this.getICascaFacade().usuarioTemPermissao(login, "cadastrarPAC", "gravar");
		} else {
			ret = this.getICascaFacade().usuarioTemPermissao(login, "cadastrarPAC", "visualizar");
		}
		return ret;
	}

	/**
	 * Método que inclui a condição de pagamento apos inserir uma licitacao
	 * @oradb SCOF_MAN_LICITacao
	 * @param ScoLicitacao licitacao
	 * @throws ApplicationBusinessException 
	 */
	private Integer inluirCondicaoPagamento(ScoLicitacao licitacao) throws ApplicationBusinessException{
		ScoCondicaoPgtoLicitacao condPagtoLicitacao  = new ScoCondicaoPgtoLicitacao();
		
		condPagtoLicitacao.setNumero(Integer.valueOf(1));
		condPagtoLicitacao.setLicitacao(licitacao);
		
		AghParametros parametro = getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_COND_PAGAMENTO);
		if (parametro != null && parametro.getVlrNumerico() != null) {
			condPagtoLicitacao.setFormaPagamento(getScoFormaPagamentoDAO().obterPorChavePrimaria(parametro.getVlrNumerico().shortValueExact()));
		} else {
			condPagtoLicitacao.setFormaPagamento(getScoFormaPagamentoDAO().obterPorChavePrimaria(1));
		}
		
		condPagtoLicitacao.setVersion(Integer.valueOf(0));
		this.getScoCondicaoPgtoLicitacaoDAO().persistir(condPagtoLicitacao);
		this.getScoCondicaoPgtoLicitacaoDAO().flush();
		
		return condPagtoLicitacao.getSeq();
	}
	
	/**
	 * Método que realiza a regra de negócio da rotina para 
	 * importação do arquivo de retorno do pregão BB movendo o arquivo
	 * do diretório de retorno para o diretório de processado.
	 * @param job Rotina agendada automaticamente.
	 * @throws ApplicationBusinessException Quando há erro ao mover o arquivo entre os diretórios.
	 */
	public void importarArqPregaoBBRetorno(AghJobDetail job) throws ApplicationBusinessException {
		File arquivoAtual = null;
		String diretorioRetorno = StringUtils.EMPTY;
		String diretorioProcessado  = StringUtils.EMPTY;
		try{
			diretorioRetorno = parametroFacade.buscarValorTexto(AghuParametrosEnum.P_AGHU_PG_BB_DIR_RET);
			diretorioProcessado = parametroFacade.buscarValorTexto(AghuParametrosEnum.P_AGHU_PG_BB_DIR_RET_PROC);
			File dirRet = new File(diretorioRetorno);
			File destDir = new File(diretorioProcessado);
			
			if (dirRet.listFiles() != null){
				for (File file : dirRet.listFiles()){
					arquivoAtual = file;
					processarArquivo(job, destDir, file);
				}
			}
		} catch (IOException e){
			String mensagemErro  = "Data: "
					+ DateUtil.obterDataFormatada(Calendar.getInstance().getTime(),"dd/MM/yyyy HH:mm:ss")
					+" Arquivo: " + arquivoAtual.getName()
					+" Origem: " + diretorioRetorno
					+" Destino: " + diretorioProcessado
					+" Motivo: " + e.getMessage();
			throw new ApplicationBusinessException(mensagemErro, Severity.ERROR);
		} catch (Exception e) {
			LOG.error(e.getMessage());
			schedulerFacade.adicionarLog(job, e.getMessage());
		}
	}

	public void processarArquivo(AghJobDetail job, File destDir, File file) throws IOException, ApplicationBusinessException {
		List<String> linhasArquivo;
		ScoLicitacao licitacao;
		Integer numeroLicitacao;
		List<Integer> licitacoes = new ArrayList<Integer>();
		if (file != null && file.isFile() ){
			linhasArquivo = FileUtils.readLines(file);
			
			if(file.getName().startsWith("AOP715") && file.getName().toUpperCase().endsWith(".RET")){
				for (String linha : linhasArquivo){
					if(linha != null && !linha.isEmpty() && linha.length() >= 2 && linha.substring(0,2).equals("01")){
						numeroLicitacao = Integer.parseInt(linha.substring(301, 307).trim());
						licitacao = scoLicitacaoDAO.obterPorChavePrimaria(numeroLicitacao);
						if (licitacao != null){
							licitacao.setDtLeituraArqRetorno(Calendar.getInstance().getTime());
							licitacao.setNomeArqRetorno(file.getName());
							scoLicitacaoDAO.atualizar(licitacao);
							String gerarPropostaAut = parametroFacade.buscarValorTexto(AghuParametrosEnum.P_AGHU_PG_GERA_PROPOSTAS_AUT);
							if(gerarPropostaAut != null && gerarPropostaAut.equals("S")){
								licitacoes.add(licitacao.getNumero());
							}
						} else{
							LOG.info(LICITACAO + numeroLicitacao.toString() +  NAO_ENCONTRADA);
							schedulerFacade.adicionarLog(job, LICITACAO + numeroLicitacao.toString() +  NAO_ENCONTRADA);
						}
					}
				}
				try{
					FileUtils.moveFileToDirectory(file, destDir, Boolean.TRUE);
				} catch(FileExistsException e){
					FileUtils.copyFileToDirectory(file, destDir);
					file.delete();
				}
				for (Integer nroLicitacao : licitacoes) {
					try {
						pregaoBancoBrasilRN.gerarPropostaPregaoBB(nroLicitacao, file.getName());
					} catch (ApplicationBusinessException e) {
						LOG.info("Proposta Pregão não pode ser gerada para licitação " + nroLicitacao.toString() +  " deve ser gerada manualmente.");
						schedulerFacade.adicionarLog(job, "Proposta Pregão não pode ser gerada para licitação " + nroLicitacao.toString() +  " deve ser gerada manualmente.");
					}
				}

			}else if(file.getName().startsWith("AOP712") && file.getName().toUpperCase().endsWith(".RET")){
				for (String linha : linhasArquivo){
					if(linha != null && !linha.isEmpty() && linha.substring(0,2).equals("01")){
						numeroLicitacao = Integer.parseInt(linha.substring(51, 57).trim());
						licitacao = scoLicitacaoDAO.obterPorChavePrimaria(numeroLicitacao);
						if (licitacao != null){
							licitacao.setDtLeituraArqCad(Calendar.getInstance().getTime());
							licitacao.setNomeArqCad(file.getName());
							scoLicitacaoDAO.atualizar(licitacao);
						} else{
							LOG.info(LICITACAO + numeroLicitacao.toString() +  NAO_ENCONTRADA);
							schedulerFacade.adicionarLog(job, LICITACAO + numeroLicitacao.toString() +  NAO_ENCONTRADA);
						}
					}
				}
				try{
					FileUtils.moveFileToDirectory(file, destDir, Boolean.TRUE);
				} catch(FileExistsException e){
					FileUtils.copyFileToDirectory(file, destDir);
					file.delete();
				}
			}			
		}
	}
	
	/**
	 * Método que inclui um parcelas pagamento apos inserir uma
	 * licitacao
	 * 
	 * @oradb 
	 * @param Integer
	 *        seqCondicaoPgto   
	 * @throws ApplicationBusinessException
	 */
	private void incluirParcelasPagamentoCompras(Integer seqCondicaoPgto) throws ApplicationBusinessException {
		
		AghParametros pPrazoPagamento = getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_PRAZO_PAGAMENTO);

		if (pPrazoPagamento.getVlrNumerico().shortValue() > 0) {		
			ScoParcelasPagamento parcelaPagamento = new ScoParcelasPagamento();
			
			ScoCondicaoPgtoLicitacao scoCondicaoPgtoLicitacao= this.getScoCondicaoPgtoLicitacaoDAO().buscarCondicaoPagamentoPK(seqCondicaoPgto);
			
			parcelaPagamento.setCondicaoPgtoLicitacao(scoCondicaoPgtoLicitacao);
			parcelaPagamento.setParcela(((short) 1));
			parcelaPagamento.setPrazo(pPrazoPagamento.getVlrNumerico().shortValue());
			parcelaPagamento.setPercPagamento(BigDecimal.valueOf(100));
					
			this.scoParcelasPagamentoDAO.persistir(parcelaPagamento);
			this.scoParcelasPagamentoDAO.flush();
		}
	}
				
	protected ScoCondicaoPgtoLicitacaoDAO getScoCondicaoPgtoLicitacaoDAO() {
		return scoCondicaoPgtoLicitacaoDAO;
	}

	protected ScoFormaPagamentoDAO getScoFormaPagamentoDAO() {
		return scoFormaPagamentoDAO;
	}
	
	protected ICascaFacade getICascaFacade() {
		return cascaFacade;
	}

	protected IComprasCadastrosBasicosFacade getComprasCadastrosBasicosFacade() {
		return comprasCadastrosBasicosFacade;
	}

	protected ScoModalidadeLicitacaoDAO getScoModalidadeLicitacaoDAO() {
		return scoModalidadeLicitacaoDAO;
	}

	protected ScoLicitacaoDAO getScoLicitacaoDAO() {
		return scoLicitacaoDAO;
	}

	protected ScoItemLicitacaoDAO getScoItemLicitacaoDAO() {
		return scoItemLicitacaoDAO;
	}

	protected IParametroFacade getParametroFacade() {
		return parametroFacade;
	}

	protected ScoItemPropostaFornecedorDAO getScoItemPropostaFornecedorDAO() {
		return scoItemPropostaFornecedorDAO;
	}

	protected ScoFaseSolicitacaoDAO getScoFaseSolicitacaoDAO() {
		return scoFaseSolicitacaoDAO;
	}

	protected ScoLocalizacaoProcessoDAO getScoLocalizacaoProcessoDAO() {
		return scoLocalizacaoProcessoDAO;
	}

	protected ScoAndamentoProcessoCompraDAO getScoAndamentoProcessoCompraDAO() {
		return scoAndamentoProcessoCompraDAO;
	}
	
	protected ScoItemLicitacaoRN getScoItemLicitacaoRN() {
		return scoItemLicitacaoRN;
	}
	
	protected IComprasFacade getComprasFacade() {
		return comprasFacade;
	}

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}
	
}
