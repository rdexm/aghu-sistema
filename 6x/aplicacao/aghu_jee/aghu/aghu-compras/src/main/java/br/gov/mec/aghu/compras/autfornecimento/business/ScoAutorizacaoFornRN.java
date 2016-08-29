package br.gov.mec.aghu.compras.autfornecimento.business;

import java.math.BigDecimal;
import java.util.Date;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import java.util.Objects;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.compras.autfornecimento.business.AutFornecimentoRN.AutFornecimentoRNExceptionCode;
import br.gov.mec.aghu.compras.dao.ScoAutorizacaoFornDAO;
import br.gov.mec.aghu.dominio.DominioSituacaoAutorizacaoFornecedor;
import br.gov.mec.aghu.dominio.DominioSituacaoAutorizacaoFornecimento;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.ScoAutorizacaoForn;
import br.gov.mec.aghu.model.ScoAutorizacaoFornJn;
import br.gov.mec.aghu.model.ScoItemAutorizacaoForn;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;

@Stateless
public class ScoAutorizacaoFornRN extends BaseBusiness {

	@EJB
	private ScoAutorizacaoFornJnRN scoAutorizacaoFornJnRN;
	@EJB
	private ScoItemAutorizacaoFornRN scoItemAutorizacaoFornRN;
	@EJB
	private ScoAutorizacaoFornON scoAutorizacaoFornON;
	@EJB
	private ScoItemAutorizacaoFornJnRN scoItemAutorizacaoFornJnRN;
	@EJB
	private MantemItemAutFornValidacoesON mantemItemAutFornValidacoesON;
	
	private static final Log LOG = LogFactory.getLog(ScoAutorizacaoFornRN.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@Inject
	private ScoAutorizacaoFornDAO scoAutorizacaoFornDAO;

	private static final long serialVersionUID = -464829745171165125L;

	private Boolean verificarAlteracaoControlada(ScoAutorizacaoForn autorizacaoFornecimento, ScoAutorizacaoForn afOriginal) {
		return (!Objects.equals(this.getScoAutorizacaoFornON().obterDataTruncada(autorizacaoFornecimento.getDtExclusao()), this.getScoAutorizacaoFornON().obterDataTruncada(afOriginal.getDtExclusao())) || 
			!Objects.equals(autorizacaoFornecimento.getVerbaGestao(), afOriginal.getVerbaGestao())	||
			!Objects.equals(CoreUtil.nvl(autorizacaoFornecimento.getValorFrete(), BigDecimal.ZERO), CoreUtil.nvl(afOriginal.getValorFrete(), BigDecimal.ZERO)) ||
			!Objects.equals(autorizacaoFornecimento.getCondicaoPagamentoPropos(), afOriginal.getCondicaoPagamentoPropos()) ||
			!Objects.equals(autorizacaoFornecimento.getValorEmpenho(), afOriginal.getValorEmpenho()) ||
			!Objects.equals(autorizacaoFornecimento.getModalidadeEmpenho(), afOriginal.getModalidadeEmpenho()) ||
			!Objects.equals(autorizacaoFornecimento.getExclusao(), afOriginal.getExclusao()) ||
			!Objects.equals(autorizacaoFornecimento.getNaturezaDespesa(), afOriginal.getNaturezaDespesa()) ||
			!Objects.equals(autorizacaoFornecimento.getMoeda(), afOriginal.getMoeda()) ||
			!Objects.equals(autorizacaoFornecimento.getServidorExcluido(), afOriginal.getServidorExcluido()) ||
			!Objects.equals(autorizacaoFornecimento.getMotivoAlteracaoAf(), afOriginal.getMotivoAlteracaoAf()));
	}
	
	private Boolean verificarReativacaoAutorizacaoFornecimento(ScoAutorizacaoForn autorizacaoFornecimento, ScoAutorizacaoForn afOriginal) {		 
		return (afOriginal.getExclusao() == Boolean.TRUE && autorizacaoFornecimento.getExclusao() == Boolean.FALSE);
	}
	
	private Boolean verificarExclusaoAutorizacaoFornecimento(ScoAutorizacaoForn autorizacaoFornecimento, ScoAutorizacaoForn afOriginal) {		 
		return (afOriginal.getExclusao() == Boolean.FALSE && autorizacaoFornecimento.getExclusao() == Boolean.TRUE);
	}
	
	private Boolean verificarAlteracaoSemNecessidadeMotivoAlteracao(ScoAutorizacaoForn autorizacaoFornecimento, ScoAutorizacaoForn afOriginal) {

		return (!Objects.equals(autorizacaoFornecimento.getDtPrevEntrega(), this.getScoAutorizacaoFornON().obterDataTruncada(afOriginal.getDtPrevEntrega())) || 
				!Objects.equals(autorizacaoFornecimento.getNroContrato(), afOriginal.getNroContrato())	||
				!Objects.equals(this.getScoAutorizacaoFornON().obterDataTruncada(autorizacaoFornecimento.getDtVenctoContrato()), this.getScoAutorizacaoFornON().obterDataTruncada(afOriginal.getDtVenctoContrato())) ||				
				!Objects.equals(autorizacaoFornecimento.getEntregaProgramada(), afOriginal.getEntregaProgramada()) ||
				!Objects.equals(autorizacaoFornecimento.getServidorGestor(), afOriginal.getServidorGestor()) ||
				!Objects.equals(autorizacaoFornecimento.getObservacao(), afOriginal.getObservacao()));
	}
	
	private Boolean verificarAlteracoesOrcamentarias(ScoAutorizacaoForn autorizacaoFornecimento, ScoAutorizacaoForn afOriginal) {
		return (!Objects.equals(autorizacaoFornecimento.getGrupoNaturezaDespesa(), afOriginal.getGrupoNaturezaDespesa()) || 
				!Objects.equals(autorizacaoFornecimento.getNaturezaDespesa(), afOriginal.getNaturezaDespesa())	||
				!Objects.equals(autorizacaoFornecimento.getVerbaGestao(), afOriginal.getVerbaGestao()));
	}
				
	/**
	 * @ORADB SCOT_AFN_BRU
	 * Atualiza os dados possíveis de uma AF com a validações necessárias
	 * @param autorizacaoFornecimento
	 * @throws ApplicationBusinessException
	 */
	public void atualizarAutorizacaoFornecimento(ScoAutorizacaoForn autorizacaoFornecimento) throws BaseException {
		ScoAutorizacaoForn afOriginal = this.getScoAutorizacaoFornDAO().obterOriginal(autorizacaoFornecimento);
		
		// RN 11
		if (verificarAlteracoesOrcamentarias(autorizacaoFornecimento, afOriginal)) {
			this.getManterItemAutFornValidacoesON().validarParametrosOrcamentariosItensAf(autorizacaoFornecimento.getItensAutorizacaoForn());
		}

		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		// RN 06
		if (verificarAlteracaoControlada(autorizacaoFornecimento, afOriginal)) {
			autorizacaoFornecimento.setServidorControlado(servidorLogado);
			autorizacaoFornecimento.setDtAlteracao(new Date());
		}
		
		// RN 07
		if (verificarReativacaoAutorizacaoFornecimento(autorizacaoFornecimento, afOriginal)) {
			autorizacaoFornecimento.setServidorExcluido(null);
			autorizacaoFornecimento.setDtExclusao(null);
			autorizacaoFornecimento.setSituacao(DominioSituacaoAutorizacaoFornecimento.AE);
			
			for (ScoItemAutorizacaoForn item : autorizacaoFornecimento.getItensAutorizacaoForn()) {
				item.setIndSituacao(DominioSituacaoAutorizacaoFornecedor.AE);
				item.setIndExclusao(Boolean.FALSE);
				this.getScoItemAutorizacaoFornRN().atualizarItemAutorizacaoFornecimento(item);
			}
		}
		
		// RN 08		
		if (verificarExclusaoAutorizacaoFornecimento(autorizacaoFornecimento, afOriginal)) {
			autorizacaoFornecimento.setServidorExcluido(servidorLogado);
			autorizacaoFornecimento.setDtExclusao(new Date());
			autorizacaoFornecimento.setSituacao(DominioSituacaoAutorizacaoFornecimento.EX);
			
			for (ScoItemAutorizacaoForn item : autorizacaoFornecimento.getItensAutorizacaoForn()) {
				item.setIndSituacao(DominioSituacaoAutorizacaoFornecedor.EX);
				item.setIndExclusao(Boolean.TRUE);
				this.getScoItemAutorizacaoFornRN().atualizarItemAutorizacaoFornecimento(item);
			}
		}
				
		// RN 10
		if (autorizacaoFornecimento.getMotivoAlteracaoAf() != null) {
			// insere a journal
			ScoAutorizacaoFornJn autJn =  this.getScoAutorizacaoFornJnRN().inserirAutorizacaoFornecimentoJn(autorizacaoFornecimento, false);
			
			// limpa os campos de motivo de alteracao da AF
			autorizacaoFornecimento.setMotivoAlteracaoAf(null);
			autorizacaoFornecimento.setSequenciaAlteracao(autJn.getSequenciaAlteracao());
			
			// verifica e atualiza quando necessario os itens da AF
			getScoItemAutorizacaoFornJnRN().validarInsercaoIafJn(autorizacaoFornecimento);
		} else {
			// RN 09
			if (verificarAlteracaoSemNecessidadeMotivoAlteracao(autorizacaoFornecimento, afOriginal)) {
				this.getScoAutorizacaoFornJnRN().atualizarAutorizacaoFornecimentoJnSemMotivoAlteracao(autorizacaoFornecimento);
			}
		}
		this.getScoAutorizacaoFornDAO().merge(autorizacaoFornecimento);
		//this.getScoAutorizacaoFornDAO().persistir(autorizacaoFornecimento);
	}
	
	public ScoAutorizacaoForn inserir(
			ScoAutorizacaoForn autorizacaoFornecimento) throws ApplicationBusinessException {
		try {
			this.getScoAutorizacaoFornDAO().persistir(autorizacaoFornecimento);
		} catch (Exception e) {
			throw new ApplicationBusinessException(
					AutFornecimentoRNExceptionCode.ERRO_INSERCAO_AUT_FORNECIMENTO);
		}

		return autorizacaoFornecimento;
	}	
			
	protected MantemItemAutFornValidacoesON getManterItemAutFornValidacoesON() {
		return mantemItemAutFornValidacoesON;
	}
	
	protected ScoAutorizacaoFornDAO getScoAutorizacaoFornDAO() {
		return scoAutorizacaoFornDAO;
	}

	protected ScoAutorizacaoFornJnRN getScoAutorizacaoFornJnRN() {
		return scoAutorizacaoFornJnRN;
	}
	
	protected ScoItemAutorizacaoFornJnRN getScoItemAutorizacaoFornJnRN() {
		return scoItemAutorizacaoFornJnRN;
	}
	
	protected ScoAutorizacaoFornON getScoAutorizacaoFornON() {
		return scoAutorizacaoFornON;
	}
	
	protected ScoItemAutorizacaoFornRN getScoItemAutorizacaoFornRN() {
		return scoItemAutorizacaoFornRN;
	}

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}
	
}
