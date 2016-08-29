package br.gov.mec.aghu.compras.pac.business;

import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.compras.dao.ScoItemAutorizacaoFornDAO;
import br.gov.mec.aghu.compras.dao.ScoItemPropostaFornecedorDAO;
import br.gov.mec.aghu.compras.dao.ScoItemLicitacaoDAO;
import br.gov.mec.aghu.compras.dao.ScoCondicaoPagamentoProposDAO;
import br.gov.mec.aghu.compras.dao.ScoParcelasPagamentoDAO;
import br.gov.mec.aghu.dominio.DominioMotivoDesclassificacaoItemProposta;
import br.gov.mec.aghu.dominio.DominioSimNaoTodos;
import br.gov.mec.aghu.dominio.DominioSituacaoJulgamento;
import br.gov.mec.aghu.model.ScoCondicaoPagamentoPropos;
import br.gov.mec.aghu.model.ScoCriterioEscolhaProposta;
import br.gov.mec.aghu.model.ScoFornecedor;
import br.gov.mec.aghu.model.ScoItemLicitacao;
import br.gov.mec.aghu.model.ScoItemPropostaFornecedor;
import br.gov.mec.aghu.model.ScoItemPropostaFornecedorId;
import br.gov.mec.aghu.model.ScoParcelasPagamento;
import br.gov.mec.aghu.suprimentos.vo.ScoItemPropostaVO;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;


@Stateless
public class RegistraJulgamentoPacON extends BaseBusiness {

	@EJB
	private DadosItemLicitacaoON dadosItemLicitacaoON;
	@EJB
	private ScoItemPropostaFornecedorON scoItemPropostaFornecedorON;
	@EJB
	private ScoItemLicitacaoON scoItemLicitacaoON;	
	@EJB
	private ScoItemPropostaFornecedorRN scoItemPropostaFornecedorRN;
	@EJB
	private ScoItemLicitacaoRN scoItemLicitacaoRN;

	
	private static final Log LOG = LogFactory.getLog(RegistraJulgamentoPacON.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
	return LOG;
	}
	
	
	@Inject
	private ScoItemAutorizacaoFornDAO scoItemAutorizacaoFornDAO;
	
	@Inject
	private ScoItemPropostaFornecedorDAO scoItemPropostaFornecedorDAO;
	
	@Inject
	private ScoItemLicitacaoDAO scoItemLicitacaoDAO;
		
	@Inject
	private ScoCondicaoPagamentoProposDAO scoCondicaoPagamentoProposDAO;
	
	@Inject
	private ScoParcelasPagamentoDAO scoParcelasPagamentoDAO;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -893064524258218268L;

	public enum RegistraJulgamentoPacONExceptionCode implements BusinessExceptionCode { 
		MENSAGEM_JULGAMENTO_MSG004;
	}

	/**
	 * Realiza as operacoes de julgamento em Lote
	 * @param listaItensProposta
	 * @param condicaoPagamentoEscolhida
	 * @param criterioEscolha
	 * @throws ApplicationBusinessException
	 */
	public void registrarJulgamentoPacLote(List<ScoItemPropostaVO> listaItensProposta,
			ScoCondicaoPagamentoPropos condicaoPagamentoEscolhida,
			ScoCriterioEscolhaProposta criterioEscolha) throws ApplicationBusinessException {
		
		if (listaItensProposta != null) {
			for (ScoItemPropostaVO itemVO : listaItensProposta) {
				ScoItemPropostaFornecedorId idItem = new ScoItemPropostaFornecedorId(
						itemVO.getNumeroPac(), itemVO.getFornecedorProposta().getNumero(),itemVO.getNumeroItemProposta());
				ScoItemPropostaFornecedor itemProposta = this.getScoItemPropostaFornecedorDAO().obterPorChavePrimaria(idItem);
				
				if (itemProposta != null) {
					
					if (!itemVO.getIndEscolhido() && itemVO.getMarcadoJulgamento()) {
						this.julgarPac(itemProposta, condicaoPagamentoEscolhida, criterioEscolha);
					}
					
					if (itemVO.getIndEscolhido() && !itemVO.getMarcadoJulgamento()) {
						this.reverterJulgamentoPac(itemProposta);
					}
				}
			}
		}
			
	}
	
	/**
	 * Pesquisa propostas de um fornecedor dentro de determinado PAC que ainda nao possua ind_escolhido
	 * @param numeroPac
	 * @param fornecedor
	 * @param julgados
	 * @return List
	 */
	public List<ScoItemPropostaVO> pesquisarPropostaFornecedorParaJulgamentoLote(Integer numeroPac, ScoFornecedor fornecedor, DominioSimNaoTodos julgados) {
		List<ScoItemPropostaVO> listaRetorno = this.getScoItemPropostaFornecedorDAO().pesquisarItemPropostaPorNumeroLicitacao(numeroPac, fornecedor, Boolean.TRUE, julgados); 
		
		if (listaRetorno != null) {
			for (ScoItemPropostaVO item : listaRetorno) {
				ScoItemPropostaFornecedor itemProposta = scoItemPropostaFornecedorDAO.obterItemPorLicitacaoFornecedorNumero(item.getFornecedorProposta().getNumero(), item.getNumeroPac(), item.getNumeroItemProposta());
				
				if (itemProposta != null) {
					item.setNomeMaterialServico(this.getDadosItemLicitacaoON().obterNomeMaterialServico(itemProposta.getItemLicitacao(), false));
					item.setDescricaoMaterialServico(this.getDadosItemLicitacaoON().obterDescricaoMaterialServico(itemProposta.getItemLicitacao()));
					item.setDescricaoSolicitacao(this.getDadosItemLicitacaoON().obterDescricaoSolicitacao(itemProposta.getItemLicitacao()));
					item.setTipoSolicitacao(this.getDadosItemLicitacaoON().obterTipoSolicitacao(itemProposta.getItemLicitacao()));
					
					item.setMarcadoJulgamento(itemProposta.getIndEscolhido());
					item.setParecerTecnicoMarca(this.getScoItemPropostaFornecedorON().obterParecerTecnicoItemProposta(itemProposta));
					item.setDesabilitaCheckboxJulgamentoLote(this.desabilitarCheckBoxSelecao(itemProposta, fornecedor));
				}
			}
		}
		return listaRetorno;
	}
	
	private Boolean desabilitarCheckBoxSelecao(ScoItemPropostaFornecedor itemProposta, ScoFornecedor fornecedor) {
		Boolean ret = Boolean.FALSE;
		
		// se item em AF desabilita
		if (getScoItemAutorizacaoFornDAO().verificarItemPropostaFornecedorEmAf(itemProposta.getId().getPfrLctNumero(), itemProposta.getId().getNumero(), fornecedor)) {
			ret = Boolean.TRUE;
		}
			
		// se tem o parametro do parecer tecnico e sem parecer (ou nao autorizado), desabilita			
		if (!itemProposta.getIndAutorizUsr()) {
			try {
				this.getScoItemPropostaFornecedorON().validarLiberacaoParecerTecnico(itemProposta);
			} catch (ApplicationBusinessException e) {
				ret = Boolean.TRUE;
			}
		}
				
		return ret;
	}
	
	/**
	 * Escolhe o item da proposta passado como parâmetro como o vencedor para um item de PAC
	 * @param itemProposta
	 * @param condicaoPagamentoEscolhida
	 * @param criterioEscolha
	 * @throws AGHUNegocioException
	 */
	public void julgarPac(ScoItemPropostaFornecedor itemProposta,ScoCondicaoPagamentoPropos condicaoPagamentoEscolhida,
			ScoCriterioEscolhaProposta criterioEscolha) throws ApplicationBusinessException {				

		this.getScoItemLicitacaoON().verificarPropostasItemLicitacaoEmAf(itemProposta.getItemLicitacao().getId().getLctNumero(), 
				itemProposta.getItemLicitacao().getId().getNumero());

		// RN 10
		this.getScoItemPropostaFornecedorON().validarLiberacaoParecerTecnico(itemProposta);
		if (condicaoPagamentoEscolhida == null) {
			throw new ApplicationBusinessException(
					RegistraJulgamentoPacONExceptionCode.MENSAGEM_JULGAMENTO_MSG004);
		}

		ScoItemPropostaFornecedor itProp = this.getScoItemPropostaFornecedorDAO().obterPorChavePrimaria(itemProposta.getId());
		ScoCondicaoPagamentoPropos condPag = this.getScoCondicaoPagamentoProposDAO().obterCondicaoPagamentoPropostaPorNumero(condicaoPagamentoEscolhida.getNumero());  
		List<ScoParcelasPagamento> listaParcelas = this.getScoParcelasPagamentoDAO().obterParcelasPagamento(condicaoPagamentoEscolhida.getNumero());
		if (listaParcelas != null && listaParcelas.size() > 0) {
			for (ScoParcelasPagamento item : listaParcelas) {
				condPag.getParcelas().add(item);
			}
		}

		if (itProp != null) {
			if (itProp.getIndEscolhido() == Boolean.FALSE) {
				itProp.setIndEscolhido(Boolean.TRUE);
				itProp.setDtEscolha(new Date());
				itProp.setCondicaoPagamentoPropos(condPag);
				itProp.setCriterioEscolhaProposta(criterioEscolha);
			} else if (!itProp.getCriterioEscolhaProposta().equals(criterioEscolha)) {
				itProp.setCriterioEscolhaProposta(criterioEscolha);
			}

			this.getScoItemPropostaFornecedorRN().atualizarItemPropostaFornecedor(itProp);

			ScoItemLicitacao itemLicitacao = this.getScoItemLicitacaoDAO().obterPorChavePrimaria(itemProposta.getItemLicitacao().getId());

			if (itemLicitacao != null) {
				itemLicitacao.setPropostaEscolhida(Boolean.TRUE);
				itemLicitacao.setSituacaoJulgamento(DominioSituacaoJulgamento.JU);
				itemLicitacao.setServidorJulgParcial(null);
				itemLicitacao.setJulgParcial(Boolean.FALSE);
				if (itemLicitacao.getMotivoCancel() != null) {
					itemLicitacao.setMotivoCancel(null);
				}
				this.getScoItemLicitacaoRN().atualizarItemLicitacao(itemLicitacao);
			}
		}
	}

	/**
	 * Desfaz a escolha de determinado item de proposta do fornecedor como vencedor de uma licitação (PAC)
	 * @param itemProposta
	 * @throws AGHUNegocioException
	 */
	public void reverterJulgamentoPac(ScoItemPropostaFornecedor itemProposta)  throws ApplicationBusinessException{
		this.getScoItemLicitacaoON().verificarPropostasItemLicitacaoEmAf(itemProposta.getItemLicitacao().getId().getLctNumero(),	
				itemProposta.getItemLicitacao().getId().getNumero());

		ScoItemPropostaFornecedor itProp = this.getScoItemPropostaFornecedorDAO().obterPorChavePrimaria(itemProposta.getId());

		if (itProp != null && itProp.getIndEscolhido() == Boolean.TRUE) {
			itProp.setIndEscolhido(Boolean.FALSE);
			itProp.setDtEscolha(null);
			itProp.setCondicaoPagamentoPropos(null);
			itProp.setCriterioEscolhaProposta(null);
			this.getScoItemPropostaFornecedorRN().atualizarItemPropostaFornecedor(itProp);

			ScoItemLicitacao itemLicitacao = this.getScoItemLicitacaoDAO().obterPorChavePrimaria(itemProposta.getItemLicitacao().getId());

			if (itemLicitacao != null) {
				itemLicitacao.setPropostaEscolhida(Boolean.FALSE);
				itemLicitacao.setSituacaoJulgamento(DominioSituacaoJulgamento.SJ);
				this.getScoItemLicitacaoRN().atualizarItemLicitacao(itemLicitacao);
			}
		}
	}
	

	/**
	 * Desclassifica um item de proposta do fornecedor do processo de julgamento de um item do PAC
	 * @param itemProposta
	 * @param motivoDesclassificacao
	 * @throws AGHUNegocioException
	 */
	public void desclassificarItemProposta(ScoItemPropostaFornecedor itemProposta, DominioMotivoDesclassificacaoItemProposta motivoDesclassificacao) throws ApplicationBusinessException {	
		this.getScoItemLicitacaoON().verificarPropostasItemLicitacaoEmAf(itemProposta.getItemLicitacao().getId().getLctNumero(),	
				itemProposta.getItemLicitacao().getId().getNumero());

		ScoItemPropostaFornecedor itProp = this.getScoItemPropostaFornecedorDAO().obterPorChavePrimaria(itemProposta.getId());
		if (itProp != null) {
			itProp.setMotDesclassif(motivoDesclassificacao);
			itProp.setIndDesclassificado(Boolean.TRUE);
			itProp.setIndEscolhido(Boolean.FALSE);
			itProp.setDtEscolha(null);
			itProp.setCondicaoPagamentoPropos(null);
			itProp.setCriterioEscolhaProposta(null);
			this.getScoItemPropostaFornecedorRN().atualizarItemPropostaFornecedor(itProp);

			ScoItemLicitacao itemLicitacao = this.getScoItemLicitacaoDAO().obterPorChavePrimaria(itemProposta.getItemLicitacao().getId());

			if (itemLicitacao != null) {
				itemLicitacao.setPropostaEscolhida(Boolean.FALSE);
				itemLicitacao.setSituacaoJulgamento(DominioSituacaoJulgamento.SJ);
				this.getScoItemLicitacaoRN().atualizarItemLicitacao(itemLicitacao);
			}
		}
	}

	/**
	 * Desfaz a desclassificação de um item de proposta do fornecedor
	 * @param itemProposta
	 * @throws AGHUNegocioException
	 */
	public void reverterDesclassificacaoItemProposta(ScoItemPropostaFornecedor itemProposta) throws ApplicationBusinessException {		
		this.getScoItemLicitacaoON().verificarPropostasItemLicitacaoEmAf(itemProposta.getItemLicitacao().getId().getLctNumero(),	
				itemProposta.getItemLicitacao().getId().getNumero());

		ScoItemPropostaFornecedor itProp = this.getScoItemPropostaFornecedorDAO().obterPorChavePrimaria(itemProposta.getId());

		if (itProp != null) {
			itProp.setMotDesclassif(null);
			itProp.setIndDesclassificado(Boolean.FALSE);
			this.getScoItemPropostaFornecedorRN().atualizarItemPropostaFornecedor(itProp);
		}
	}

	private ScoItemPropostaFornecedorON getScoItemPropostaFornecedorON() {
		return scoItemPropostaFornecedorON;
	}
	
	private ScoItemPropostaFornecedorDAO getScoItemPropostaFornecedorDAO() {
		return scoItemPropostaFornecedorDAO;
	}	
	
	private DadosItemLicitacaoON getDadosItemLicitacaoON() {
		return dadosItemLicitacaoON;
	}
	
	private ScoItemAutorizacaoFornDAO getScoItemAutorizacaoFornDAO() {
		return scoItemAutorizacaoFornDAO;
	}
		
	private ScoItemLicitacaoON getScoItemLicitacaoON() {
		return scoItemLicitacaoON;
	}

	private ScoItemPropostaFornecedorRN getScoItemPropostaFornecedorRN() {
		return scoItemPropostaFornecedorRN;
	}
	
	private ScoItemLicitacaoDAO getScoItemLicitacaoDAO() {
		return scoItemLicitacaoDAO;
	}

	private ScoItemLicitacaoRN getScoItemLicitacaoRN() {
		return scoItemLicitacaoRN;
	}

	private ScoCondicaoPagamentoProposDAO getScoCondicaoPagamentoProposDAO() {
		return scoCondicaoPagamentoProposDAO;
	}

	private ScoParcelasPagamentoDAO getScoParcelasPagamentoDAO() {
		return scoParcelasPagamentoDAO;
	}
}