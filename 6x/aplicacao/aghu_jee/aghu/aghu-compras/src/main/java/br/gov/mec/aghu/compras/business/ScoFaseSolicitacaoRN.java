package br.gov.mec.aghu.compras.business;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.compras.dao.ScoAutorizacaoFornDAO;
import br.gov.mec.aghu.compras.dao.ScoFaseSolicitacaoDAO;
import br.gov.mec.aghu.compras.dao.ScoLicitacaoDAO;
import br.gov.mec.aghu.compras.dao.ScoRefCodesDAO;
import br.gov.mec.aghu.compras.dao.ScoSolicitacaoServicoDAO;
import br.gov.mec.aghu.compras.dao.ScoSolicitacoesDeComprasDAO;
import br.gov.mec.aghu.compras.pac.business.IPacFacade;
import br.gov.mec.aghu.dominio.DominioOperacaoBanco;
import br.gov.mec.aghu.dominio.DominioTipoFaseSolicitacao;
import br.gov.mec.aghu.estoque.business.IEstoqueFacade;
import br.gov.mec.aghu.model.ScoAutorizacaoForn;
import br.gov.mec.aghu.model.ScoFaseSolicitacao;
import br.gov.mec.aghu.model.ScoLicitacao;
import br.gov.mec.aghu.model.ScoSolicitacaoDeCompra;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;


@Stateless
public class ScoFaseSolicitacaoRN extends BaseBusiness{

private static final Log LOG = LogFactory.getLog(ScoFaseSolicitacaoRN.class);

	@Override
	@Deprecated
	protected Log getLogger() {
	return LOG;
	}
	
	
	@Inject
	private ScoSolicitacoesDeComprasDAO scoSolicitacoesDeComprasDAO;
	
	@Inject
	private ScoRefCodesDAO scoRefCodesDAO;
	
	@Inject
	private ScoAutorizacaoFornDAO scoAutorizacaoFornDAO;
	
	@Inject
	private ScoLicitacaoDAO scoLicitacaoDAO;
	
	@EJB
	private IPacFacade pacFacade;
	
	@EJB
	private IEstoqueFacade estoqueFacade;
	
	@Inject
	private ScoSolicitacaoServicoDAO scoSolicitacaoServicoDAO;
	
	@Inject
	private ScoFaseSolicitacaoDAO scoFaseSolicitacaoDAO;

	/**
	 * 
	 */
	private static final long serialVersionUID = 4700050836781652284L;

	public enum ScoFaseSolicitacaoRNExceptionCode implements BusinessExceptionCode {
		SCO_00483, TIPO_FASE_SOLICITACAO_INVALIDA,
		MENSAGEM_ITEMPAC_M02, MENSAGEM_SS_SEM_ORCAMENTO, MENSAGEM_SC_SEM_ORCAMENTO;
	}

	public void inserir(ScoFaseSolicitacao faseSolicitacao) throws BaseException {
		this.preInserir(faseSolicitacao);
		this.getScoFaseSolicitacaoDAO().persistir(faseSolicitacao);
		this.posInserir(faseSolicitacao);
	}

	public void atualizar(ScoFaseSolicitacao faseSolicitacao, ScoFaseSolicitacao faseSolicitacaoOld) throws BaseException {
		this.preAtualizar(faseSolicitacao, faseSolicitacaoOld);
		this.getScoFaseSolicitacaoDAO().atualizar(faseSolicitacao);
		this.getScoFaseSolicitacaoDAO().flush();
		this.posAtualizar(faseSolicitacao);
	}

	public void excluirFaseSolicitacao(ScoFaseSolicitacao faseSolicitacao) throws ApplicationBusinessException {
		this.preExcluirFaseSolicitacao(faseSolicitacao);
		this.getScoFaseSolicitacaoDAO().remover(faseSolicitacao);
		this.posExcluirFaseSolicitacao(faseSolicitacao);
	}
	
	/**
	 * @ORADB SCOT_FSC_BRD
	 * @param faseSolicitacao
	 * @throws ApplicationBusinessException
	 */
	private void preExcluirFaseSolicitacao(ScoFaseSolicitacao faseSolicitacao) throws ApplicationBusinessException {
		if (faseSolicitacao.getItemLicitacao() != null) {
			if (this.getPacFacade().verificarLicitacaoProposta(faseSolicitacao.getItemLicitacao().getId().getLctNumero(), 
					faseSolicitacao.getItemLicitacao().getId().getNumero(), true)) {
				throw new ApplicationBusinessException(
						ScoFaseSolicitacaoRNExceptionCode.MENSAGEM_ITEMPAC_M02,
						faseSolicitacao.getItemLicitacao().getId().getNumero());
			}
		}
	}
	
	/**
	 * @param faseSolicitacao
	 * @throws ApplicationBusinessException
	 */
	private void posExcluirFaseSolicitacao(ScoFaseSolicitacao faseSolicitacao) throws ApplicationBusinessException {
		//
	}
	
	/**
	 * @ORADB SCOT_FSC_BRI	
	 * @throws BaseException
	 */
	public void preInserir(ScoFaseSolicitacao faseSolicitacao) throws BaseException{
		if(!faseSolicitacao.getExclusao()) {
			faseSolicitacao.setDtExclusao(null);
		}
		if(faseSolicitacao.getTipo() != null) {
			if(getScoRefCodesDAO().buscarScoRefCodesPorTipoOperConversao(faseSolicitacao.getTipo().toString(), "OBJETO_LICITACAO").isEmpty()) {
				throw new ApplicationBusinessException(ScoFaseSolicitacaoRNExceptionCode.TIPO_FASE_SOLICITACAO_INVALIDA);
			}			
		}
	}

	/**
	 * @ORADB SCOT_FSC_ASI	
	 * @throws BaseException
	 */
	public void posInserir(ScoFaseSolicitacao faseSolicitacao) throws BaseException{
		this.enforceFscRules(faseSolicitacao, DominioOperacaoBanco.INS);
	}
	
	/**
	 * @ORADB SCOT_FSC_BRI - SCOT_FSC_BRU	
	 * @throws BaseException
	 */
	public void preAtualizar(ScoFaseSolicitacao faseSolicitacao, ScoFaseSolicitacao faseSolicitacaoOld) throws BaseException{
		if(!faseSolicitacao.getExclusao()) {
			faseSolicitacao.setDtExclusao(null);
		}
	}
	
	/**
	 * @ORADB SCO_FASES_SOLICITACOES - SCOT_FSC_ASU	
	 * @throws BaseException
	 */
	public void posAtualizar(ScoFaseSolicitacao faseSolicitacao) throws BaseException{
		this.enforceFscRules(faseSolicitacao, DominioOperacaoBanco.UPD);
	}

	/**
	 * @ORADB SCOP_ENFORCE_FSC_RULES
	 * @throws BaseException
	 */
	public void enforceFscRules(ScoFaseSolicitacao faseSolicitacao, DominioOperacaoBanco operacao) throws BaseException{
		Long quantidadeSC = getScoFaseSolicitacaoDAO().obterQuantidadeFaseSolicitacaoPeloItemLicitacaoPeloTipoEIndExclusao(faseSolicitacao.getItemLicitacao(), DominioTipoFaseSolicitacao.C, false);
		Long quantidadeSS = getScoFaseSolicitacaoDAO().obterQuantidadeFaseSolicitacaoPeloItemLicitacaoPeloTipoEIndExclusao(faseSolicitacao.getItemLicitacao(), DominioTipoFaseSolicitacao.S, false);
		
		if(quantidadeSC >= 1 && quantidadeSS >=1 ) {
			throw new ApplicationBusinessException(ScoFaseSolicitacaoRNExceptionCode.SCO_00483);
		}
		
		if((faseSolicitacao.getSolicitacaoDeCompra() != null || faseSolicitacao.getSolicitacaoServico() != null) && faseSolicitacao.getItemLicitacao() != null) {
			this.reFscpVerNatDesp(
					faseSolicitacao.getSolicitacaoDeCompra() != null ? faseSolicitacao.getSolicitacaoDeCompra().getNumero(): null, 
					faseSolicitacao.getSolicitacaoServico() != null  ? faseSolicitacao.getSolicitacaoServico() .getNumero(): null);
		}
		
		if(DominioOperacaoBanco.INS.equals(operacao)) {		
			// Somente p/ incl de Item Licitação para SC
			if(faseSolicitacao.getItemLicitacao() != null && faseSolicitacao.getSolicitacaoDeCompra() != null) {
				// Atualiza o TEMPO_PREV_EXECUCAO do PAC conforme regra (ver na procedure abaixo).
				this.rnLctpAtuTempoPac(faseSolicitacao.getItemLicitacao().getLicitacao().getNumero(), faseSolicitacao.getSolicitacaoDeCompra().getNumero());
			}
		}
	}

	/**
	 * @ORADB SCOK_FSC_RN - RN_FSCP_VER_NAT_DESP
	 * @throws BaseException
	 */
	public void reFscpVerNatDesp(Integer numeroSolCompra, Integer numeroSolServico) throws BaseException{
		if(numeroSolCompra != null && this.getScoSolicitacoesDeComprasDAO().verificarSCNaturezaDespEVerbaGestaoNulo(numeroSolCompra)) {
			throw new ApplicationBusinessException(ScoFaseSolicitacaoRNExceptionCode.MENSAGEM_SC_SEM_ORCAMENTO, numeroSolCompra);
		}

		if(numeroSolServico != null && getScoSolicitacaoServicoDAO().verificarSSNaturezaDespEVerbaGestaoNulo(numeroSolServico)) {
			throw new ApplicationBusinessException(ScoFaseSolicitacaoRNExceptionCode.MENSAGEM_SS_SEM_ORCAMENTO, numeroSolServico);
		}
	}
	
	/**
	 * @ORADB SCOK_LCT_RN - RN_LCTP_ATU_TEMPOPAC
	 * @throws BaseException
	 */
	public void rnLctpAtuTempoPac(Integer numeroLict, Integer numeroSolCompra) throws BaseException {
		ScoSolicitacaoDeCompra solCompra = getScoSolicitacoesDeComprasDAO().obterPorChavePrimaria(numeroSolCompra);
		Integer codigoMaterial = solCompra.getMaterial().getCodigo();
		Integer quantidade = getEstoqueFacade().obterConsumoTotalNosUltimosSeisMeses(codigoMaterial);
		Integer tempoPacCal = 0;
		Integer duracaoDias = 0;
		if(quantidade > 0) {
			duracaoDias = getEstoqueFacade().obterConsumoTotalNosUltimosSeisMesesPeloMedicamento(codigoMaterial);
		}
		
		Integer tempoMedPerm = getScoLicitacaoDAO().obterNumeroMaximoDeDiasDePermaneciaPelaLicitacao(numeroLict);
		
		if (tempoMedPerm == null) {
			tempoMedPerm = 0;
		}
		
		ScoLicitacao licitacao = getScoLicitacaoDAO().obterPorChavePrimaria(numeroLict);
		Integer tempoItensAnt = licitacao.getTempoPrevExecucao();
		
		if (tempoItensAnt == null) {
			tempoItensAnt = 0;
		}
		
		if((duracaoDias-10) > tempoMedPerm) {  // 10 dias de segurança
			tempoPacCal = tempoMedPerm; //Tempo Máximo calculado
		}
		else if((duracaoDias-10) > (tempoMedPerm - (tempoMedPerm*0.3))) { 
			tempoPacCal  = (duracaoDias - 10); // Tempo interm. calculado  = Duração Estoque - 10 dias
		}
		else {
			tempoPacCal = (int) ((tempoMedPerm - (tempoMedPerm * 0.3))); // Tempo Mínimo calculado
		}
		/*
		 Grava o Tempo PAC relativo ao Item incluído, se Menor que o Tempo dos Itens anteriores.
		 O objetivo é definir o menor Tempo possível dentro dos limites permitidos (Entre o Tempo Médio da Modl e este menos 30 %).
		 Ex.  Se o Tempo Pac da Modalidade é 100 dias, o Tempo previsto do PAC deverá ficar entre 70 e 100 dias.
		
		         Os Itens cuja Duração Estoque é menor que o  v_tempo_pac_calc serão comprados via Dispensa de Licitação.
		         O Serv. Planejamento receberá a relação desses itens via
		*/
		if(tempoPacCal < tempoItensAnt) {
	//		ScoLicitacao licitacaoClone = getScoLicitacaoRN().clonarLicitacao(licitacao);
			licitacao.setTempoPrevExecucao(tempoPacCal);
	//		getScoLicitacaoRN().atualizar(licitacao, licitacaoClone);
		}
		else if(tempoItensAnt == 0) {
	//		ScoLicitacao licitacaoClone = getScoLicitacaoRN().clonarLicitacao(licitacao);
			licitacao.setTempoPrevExecucao(tempoPacCal);
	//		getScoLicitacaoRN().atualizar(licitacao, licitacaoClone);
		}
	}
	
	
	/**
	 * Responsabilidade de buscar um Tipo de FaseSolicitacao, conforme:<br>
	 * DominioTipoFaseSolicitacao a = ScoAutorizacaoForn.ListOfScoItemAutorizacaoForn.ListOfScoFaseSolicitacao.tipo<br>
	 * Substitui o codigo:<br>
	 * tipoSolicitacao = autorizacaoForn.getItensAutorizacaoForn().get(0).getScoFaseSolicitacao().get(0).getTipo();
	 * 
	 * @param autorizacaoForn
	 * @return a {@link br.gov.mec.aghu.dominio.DominioTipoFaseSolicitacao}
	 */
	public DominioTipoFaseSolicitacao getItemAutorizacaoFornecedorFaseSolicitacaoTipoPorAutorizacaoFornecimento(ScoAutorizacaoForn autorizacaoForn) {
		ScoAutorizacaoFornDAO dao = getScoAutorizacaoFornDAO();
		
		ScoAutorizacaoForn aScoAutorizacaoForn = dao.obterPorChavePrimaria(autorizacaoForn.getNumero());
		DominioTipoFaseSolicitacao tipoSolicitacao = aScoAutorizacaoForn.getItensAutorizacaoForn().get(0).getScoFaseSolicitacao().get(0).getTipo();
		
		return tipoSolicitacao;
	}
		
	private ScoAutorizacaoFornDAO getScoAutorizacaoFornDAO() {
		return scoAutorizacaoFornDAO;
	}

	protected IEstoqueFacade getEstoqueFacade() {		
		return this.estoqueFacade;
	}

	private ScoRefCodesDAO getScoRefCodesDAO() {
		return scoRefCodesDAO;
	}
	
	private ScoFaseSolicitacaoDAO getScoFaseSolicitacaoDAO() {
		return scoFaseSolicitacaoDAO;
	}
	
	private ScoSolicitacoesDeComprasDAO getScoSolicitacoesDeComprasDAO() {
		return scoSolicitacoesDeComprasDAO;
	}
	
	private ScoSolicitacaoServicoDAO getScoSolicitacaoServicoDAO() {
		return scoSolicitacaoServicoDAO;
	}
	
	private ScoLicitacaoDAO getScoLicitacaoDAO() {
		return scoLicitacaoDAO;
	}
	
	protected IPacFacade getPacFacade() {		
		return this.pacFacade;
	}
}