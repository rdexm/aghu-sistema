package br.gov.mec.aghu.compras.autfornecimento.business;

import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import java.util.Objects;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.compras.dao.ScoItemAutorizacaoFornDAO;
import br.gov.mec.aghu.compras.dao.ScoItemAutorizacaoFornJnDAO;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.ScoAutorizacaoForn;
import br.gov.mec.aghu.model.ScoAutorizacaoFornJn;
import br.gov.mec.aghu.model.ScoItemAutorizacaoForn;
import br.gov.mec.aghu.model.ScoItemAutorizacaoFornJn;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.dominio.DominioOperacoesJournal;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@Stateless
public class ScoItemAutorizacaoFornJnRN extends BaseBusiness{

@EJB
private ScoItemAutorizacaoFornRN scoItemAutorizacaoFornRN;

	private static final Log LOG = LogFactory.getLog(ScoItemAutorizacaoFornJnRN.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@Inject
	private ScoItemAutorizacaoFornDAO scoItemAutorizacaoFornDAO;
	
	@Inject
	private ScoItemAutorizacaoFornJnDAO scoItemAutorizacaoFornJnDAO;
	
	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;

	private static final long serialVersionUID = -7940212453470095231L;

	public enum ScoItemAutorizacaoFornJnRNExceptionCode implements BusinessExceptionCode {
		ERRO_INSERIR_ITEM_AUT_FORNECIMENTO_JN, ERRO_EXCLUIR_ITEM_AUT_FORNECIMENTO_JN;
	}

	private Boolean verificarCamposAlteradosInsercaoIafJn(ScoItemAutorizacaoForn itemAf, ScoItemAutorizacaoFornJn itemAfJn) {
		Integer matriculaServidor = null;
		Short vinculoServidor = null;
		if (itemAf.getServidor() != null && itemAf.getServidor().getId() != null) {
			if (itemAf.getServidor().getId().getMatricula() != null) {
				matriculaServidor = itemAf.getServidor().getId().getMatricula();
			}
			if (itemAf.getServidor().getId().getVinCodigo() != null) {
				vinculoServidor = itemAf.getServidor().getId().getVinCodigo();
			}
		}
		
		return (!Objects.equals(itemAf.getItemPropostaFornecedor(), itemAfJn.getItemPropostaFornecedor()) || 
				!Objects.equals(itemAf.getUnidadeMedida(), itemAfJn.getUnidadeMedida()) ||
				!Objects.equals(itemAf.getQtdeSolicitada(), itemAfJn.getQtdeSolicitada()) ||
				!Objects.equals(itemAf.getFatorConversaoForn(), itemAfJn.getFatorConversao()) ||
				!Objects.equals(itemAf.getValorUnitario(), itemAfJn.getValorUnitario()) ||
				!Objects.equals(matriculaServidor, itemAfJn.getSerMatricula() ) ||
				!Objects.equals(vinculoServidor, itemAfJn.getSerVinCodigo() ) ||
				!Objects.equals(itemAf.getIndSituacao(), itemAfJn.getIndSituacao()) ||
				!Objects.equals(itemAf.getPercIpi(), itemAfJn.getPercIpi()) ||
				!Objects.equals(itemAf.getPercAcrescimoItem(), itemAfJn.getPercAcrescimoItem()) ||
				!Objects.equals(itemAf.getPercDesconto(), itemAfJn.getPercDesconto()) ||
				!Objects.equals(itemAf.getPercAcrescimo(), itemAfJn.getPercAcrescimo()) ||
				!Objects.equals(itemAf.getPercDescontoItem(), itemAfJn.getPercDescontoItem()) ||
				!Objects.equals(itemAf.getDtExclusao(), itemAfJn.getDtExclusao()) ||
				!Objects.equals(itemAf.getSequenciaAlteracao(), itemAfJn.getSequenciaAlteracao()) ||
				!Objects.equals(itemAf.getMarcaComercial(), itemAfJn.getMarcaComercial()) ||
				!Objects.equals(itemAf.getNomeComercial(), itemAfJn.getNomeComercial()) ||
				!Objects.equals(itemAf.getModeloComercial(), itemAfJn.getMarcaModelo()) ||
				!Objects.equals(itemAf.getNomeComercial(), itemAfJn.getNomeComercial()) ||
				!Objects.equals(itemAf.getIndExclusao(), itemAfJn.getIndExclusao()) ||
				!Objects.equals(itemAf.getIndEstorno(), itemAfJn.getIndEstorno()));
	}
	
	/**
	 * Valida e insere quando necessário um item de journal da af
	 * @param autorizacaoFornecimento
	 * @throws BaseException 
	 */
	public void validarInsercaoIafJn(ScoAutorizacaoForn autorizacaoFornecimento) throws BaseException {
		List<ScoItemAutorizacaoForn> listaItens = this.getScoItemAutorizacaoFornDAO().pesquisarItemAfAtivosPorNumeroAf(autorizacaoFornecimento.getNumero(), false);
		
		for (ScoItemAutorizacaoForn itemAf : listaItens) {
			Integer maxSequenciaAlteracao =  this.getScoItemAutorizacaoFornJnDAO().obterMaxSequenciaAlteracaoItemAfJn(itemAf.getId().getAfnNumero(), itemAf.getId().getNumero());
			
			if (maxSequenciaAlteracao == null) {
				maxSequenciaAlteracao = 0;
			}
			
			ScoItemAutorizacaoFornJn itemAfJn = this.getScoItemAutorizacaoFornJnDAO().obterItemAfJnPorSequenciaAlteracao(itemAf.getId().getAfnNumero(), itemAf.getId().getNumero(), maxSequenciaAlteracao);
			
			if (itemAfJn != null) {
				if (verificarCamposAlteradosInsercaoIafJn(itemAf, itemAfJn)) {
					// Pega a sequencia da af e decrementa um pois os metodos que atualizao a iafjn e a itens af somam + 1
					Integer seqAlt = autorizacaoFornecimento.getSequenciaAlteracao().intValue() -1;
					ScoItemAutorizacaoFornJn iafJn = this.montarJournalItemAutorizacaoFornecimento(itemAf, seqAlt);					
					if (iafJn != null) {
						this.inserirItemAutorizacaoFornecimentoJn(iafJn);
						this.getScoItemAutorizacaoFornRN().atualizarSequenciaAlteracaoItemAf(itemAf.getId().getAfnNumero(), itemAf.getId().getNumero(), seqAlt);
					}
				}
			}			
		}
		
	}

	private void popularCamposJournalMarcaModelo(ScoItemAutorizacaoForn itemAf, ScoItemAutorizacaoFornJn itemAutorizacaoFornecimentoJn) {
		if (itemAf.getMarcaComercial() != null) {
			itemAutorizacaoFornecimentoJn.setMarcaComercial(itemAf.getMarcaComercial());
		}
		if (itemAf.getNomeComercial() != null) {
			itemAutorizacaoFornecimentoJn.setNomeComercial(itemAf.getNomeComercial());
		}
		if (itemAf.getModeloComercial() != null) {
			itemAutorizacaoFornecimentoJn.setMarcaModelo(itemAf.getModeloComercial());
		}
	}
	
	private void popularCamposJournalValores(ScoItemAutorizacaoForn itemAf, ScoItemAutorizacaoFornJn itemAutorizacaoFornecimentoJn) {
		if (itemAf.getPercIpi() != null) {
			itemAutorizacaoFornecimentoJn.setPercIpi(itemAf.getPercIpi());
		}
		if (itemAf.getPercAcrescimoItem() != null) {
			itemAutorizacaoFornecimentoJn.setPercAcrescimoItem(itemAf.getPercAcrescimoItem());
		}
		if (itemAf.getPercDesconto() != null) {
			itemAutorizacaoFornecimentoJn.setPercDesconto(itemAf.getPercDesconto());
		}
		if (itemAf.getPercDescontoItem() != null) {
			itemAutorizacaoFornecimentoJn.setPercDescontoItem(itemAf.getPercDescontoItem());
		}
		if (itemAf.getPercVarPreco() != null) {
			itemAutorizacaoFornecimentoJn.setPercVarPreco(itemAf.getPercVarPreco().doubleValue());
		}
	}
	
	/**
	 * Baseado em um item de AF e em uma sequencia de alteracao monta uma journal de item de af
	 * @param itemAf
	 * @param maxSequenciaAlteracao
	 * @return ScoItemAutorizacaoFornJn
	 * @throws ApplicationBusinessException
	 */
	public ScoItemAutorizacaoFornJn montarJournalItemAutorizacaoFornecimento(ScoItemAutorizacaoForn itemAf, Integer maxSequenciaAlteracao) throws ApplicationBusinessException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		ScoItemAutorizacaoFornJn itemAutorizacaoFornecimentoJn = new ScoItemAutorizacaoFornJn();
		
		itemAutorizacaoFornecimentoJn.setJnDateTime(new Date());
		itemAutorizacaoFornecimentoJn.setJnOperation(DominioOperacoesJournal.UPD.toString());
		itemAutorizacaoFornecimentoJn.setJnUser(servidorLogado.getUsuario());
		itemAutorizacaoFornecimentoJn.setAfnNumero(itemAf.getId().getAfnNumero());
		itemAutorizacaoFornecimentoJn.setNumero(itemAf.getId().getNumero());
		itemAutorizacaoFornecimentoJn.setItemPropostaFornecedor(itemAf.getItemPropostaFornecedor());
		itemAutorizacaoFornecimentoJn.setUnidadeMedida(itemAf.getUnidadeMedida());
		itemAutorizacaoFornecimentoJn.setQtdeSolicitada(itemAf.getQtdeSolicitada());
		// Melhoria em Desenvolvimento #31017
		itemAutorizacaoFornecimentoJn.setFatorConversao(itemAf.getFatorConversaoForn());
		itemAutorizacaoFornecimentoJn.setValorUnitario(itemAf.getValorUnitario());
		
		itemAutorizacaoFornecimentoJn.setIndSituacao(itemAf.getIndSituacao());
		itemAutorizacaoFornecimentoJn.setSequenciaAlteracao(maxSequenciaAlteracao+1);
		itemAutorizacaoFornecimentoJn.setQtdeRecebida(itemAf.getQtdeRecebida());
		itemAutorizacaoFornecimentoJn.setQtdeRecebidaAtual(itemAf.getQtdeRecebida());
		itemAutorizacaoFornecimentoJn.setValorEfetivadoAtual(itemAf.getValorEfetivado());
		itemAutorizacaoFornecimentoJn.setValorEfetivado(itemAf.getValorEfetivado());
		itemAutorizacaoFornecimentoJn.setIndExclusao(itemAf.getIndExclusao());
		itemAutorizacaoFornecimentoJn.setIndRecebimento(itemAf.getIndRecebimento());
		itemAutorizacaoFornecimentoJn.setIndEstorno(itemAf.getIndEstorno());		
		if (itemAf.getDtExclusao() != null) {			
			itemAutorizacaoFornecimentoJn.setDtExclusao(itemAf.getDtExclusao());
		}					
		if (itemAf.getDtEstorno() != null) {
			itemAutorizacaoFornecimentoJn.setDtEstorno(itemAf.getDtEstorno());
		}		
		if (itemAf.getServidor() != null) {
			itemAutorizacaoFornecimentoJn.setSerMatricula(itemAf.getServidor().getId().getMatricula());
			itemAutorizacaoFornecimentoJn.setSerVinCodigo(Integer.valueOf(itemAf.getServidor().getId().getVinCodigo()));
		}
		
		this.popularCamposJournalMarcaModelo(itemAf, itemAutorizacaoFornecimentoJn);
		this.popularCamposJournalValores(itemAf, itemAutorizacaoFornecimentoJn);
		return this.inserirItemAutorizacaoFornecimentoJn(itemAutorizacaoFornecimentoJn);
	}

	public ScoItemAutorizacaoFornJn inserirItemAutorizacaoFornecimentoJn(
			ScoItemAutorizacaoFornJn itemAutorizacaoFornecimentoJn) throws ApplicationBusinessException {
		try {
			this.getScoItemAutorizacaoFornJnDAO().persistir(
					itemAutorizacaoFornecimentoJn);
		} catch (Exception e) {
			throw new ApplicationBusinessException(
					ScoItemAutorizacaoFornJnRNExceptionCode.ERRO_INSERIR_ITEM_AUT_FORNECIMENTO_JN);
		}
		return itemAutorizacaoFornecimentoJn;
	}
	
	public ScoItemAutorizacaoFornJn persistirItemAutorizacaoFornecimentoJn (
			ScoItemAutorizacaoFornJn itemAutorizacaoFornecimentoJn) throws ApplicationBusinessException {
		try {
			this.getScoItemAutorizacaoFornJnDAO().persistir(
					itemAutorizacaoFornecimentoJn);
		} catch (Exception e) {
			throw new ApplicationBusinessException(
					ScoItemAutorizacaoFornJnRNExceptionCode.ERRO_INSERIR_ITEM_AUT_FORNECIMENTO_JN);
		}
		return itemAutorizacaoFornecimentoJn;
	}
	
	private void popularSituacaoAnteriorValores(ScoItemAutorizacaoForn itemAf, ScoItemAutorizacaoFornJn itemJn) {
		if (itemJn.getPercIpi() != null) {
			itemAf.setPercIpi(itemJn.getPercIpi());
		}
		if (itemJn.getPercAcrescimoItem() != null) {
			itemAf.setPercAcrescimoItem(itemJn.getPercAcrescimoItem());
		}
		if (itemJn.getPercDesconto() != null) {
			itemAf.setPercDesconto(itemJn.getPercDesconto());
		}
		if (itemJn.getPercAcrescimo() != null) {
			itemAf.setPercAcrescimo(itemJn.getPercAcrescimo());
		}
		if (itemJn.getPercDescontoItem() != null) {
			itemAf.setPercDescontoItem(itemJn.getPercDescontoItem());
		}
		if (itemJn.getPercVarPreco() != null) {
			itemAf.setPercVarPreco(itemJn.getPercVarPreco().floatValue());
		}
		if (itemJn.getQtdeRecebida() != null) {
			itemAf.setQtdeRecebida(itemJn.getQtdeRecebida());
		}
		if (itemJn.getQtdeSolicitada() != null) {
			itemAf.setQtdeSolicitada(itemJn.getQtdeSolicitada());
		}
		if (itemJn.getValorEfetivado() != null) {
			itemAf.setValorEfetivado(itemJn.getValorEfetivado());
		}
	}

	private void popularSituacaoAnteriorOutros(ScoItemAutorizacaoForn itemAf, ScoItemAutorizacaoFornJn itemJn) {
		if (itemJn.getDtEstorno() != null) {
			itemAf.setDtEstorno(itemJn.getDtEstorno());
		}
		if (itemJn.getDtExclusao() != null) {
			itemAf.setDtExclusao(itemJn.getDtExclusao());
		}
		if (itemJn.getSerMatriculaEstorno() != null) {
			itemAf.getServidorEstorno().getId().setMatricula(itemJn.getSerMatriculaEstorno());
		}
		if (itemJn.getSerVinCodigoEstorno() != null) {
			itemAf.getServidorEstorno().getId().setVinCodigo(itemJn.getSerVinCodigoEstorno().shortValue());
		}
		if (itemJn.getNomeComercial() != null) {
			itemAf.setNomeComercial(itemJn.getNomeComercial());
		}
	}
	
	/**
	 * Coloca os valores da journal do item da AF da sequencia de alteracao da autJn passada como parametro
	 * na versao "atual" da af
	 * @param autorizacaoFornecimento
	 * @param autJn
	 */
	public void voltarSituacaoAnteriorItemJn(ScoAutorizacaoForn autorizacaoFornecimento, ScoAutorizacaoFornJn autJn) {
		/*Integer seqAnterior = this.getScoItemAutorizacaoFornJnDAO().obterMaxSequenciaAlteracaoItemAfJn(autorizacaoFornecimento.getNumero(), autJn.getNumero(),autJn.getSequenciaAlteracao());
		
		if (seqAnterior == null){
			seqAnterior = autJn.getSequenciaAlteracao().intValue();
		}*/
		
		/*Integer seqAnterior = autJn.getSequenciaAlteracao().intValue();
		
		List<ScoItemAutorizacaoFornJn> listaItenJn = this.getScoItemAutorizacaoFornJnDAO().pesquisarItemAFSeqAlteracao(autorizacaoFornecimento.getNumero(),autJn.getNumero(), seqAnterior); 				
				
		if (listaItenJn == null){
			seqAnterior = seqAnterior + 1;
			listaItenJn = this.getScoItemAutorizacaoFornJnDAO().pesquisarItemAFSeqAlteracao(autorizacaoFornecimento.getNumero(),autJn.getNumero(), seqAnterior); 				
			
		}*/
		List<ScoItemAutorizacaoFornJn> listaItenJn = this.getScoItemAutorizacaoFornJnDAO().pesquisarItemAF(autJn.getNumero());
		
		//List<ScoItemAutorizacaoFornJn> listaItenJn = this.getScoItemAutorizacaoFornJnDAO().pesquisarItemAutFornJnPorNumAfSeqAlteracao(autJn.getNumero(), autJn.getSequenciaAlteracao()); 				
		
		
		if (listaItenJn != null) {
			
			for (ScoItemAutorizacaoFornJn itemJn : listaItenJn) {
				ScoItemAutorizacaoFornJn itemJn2 = this.getScoItemAutorizacaoFornJnDAO().pesquisarItemAFSeqAlteracao(itemJn.getAfnNumero(), itemJn.getNumero(), autJn.getSequenciaAlteracao().intValue() + 1); 				
				
				
				if (itemJn.getAfnNumero().equals(itemJn2.getAfnNumero()) &&
				    itemJn.getNumero().equals(itemJn2.getNumero()) &&
				    itemJn.getSequenciaAlteracao().equals(itemJn2.getSequenciaAlteracao())){
				
				ScoItemAutorizacaoForn itemAf = this.getScoItemAutorizacaoFornDAO().obterItemAutorizacaoFornPorId(itemJn.getAfnNumero(), itemJn.getNumero());
				if (itemAf != null) {					
					itemAf.setIndEstorno(itemJn.getIndEstorno());
					itemAf.setUnidadeMedida(itemJn.getUnidadeMedida());
					itemAf.setFatorConversao(itemJn.getFatorConversao());
					itemAf.setValorUnitario(itemJn.getValorUnitario());
					if (itemJn.getSerMatricula() != null && itemJn.getSerVinCodigo() != null) {
						RapServidores servidor = this.getRegistroColaboradorFacade()
								.obterRapServidorPorVinculoMatricula(itemJn.getSerMatricula(), itemJn.getSerVinCodigo().shortValue());
						if (servidor != null) {
							itemAf.setServidor(servidor);					
						}
					}
					itemAf.setIndSituacao(itemJn.getIndSituacao());
					itemAf.setIndExclusao(itemJn.getIndExclusao());
					itemAf.setSequenciaAlteracao(itemJn.getSequenciaAlteracao());
					itemAf.setMarcaComercial(itemJn.getMarcaComercial());
					itemAf.setIndRecebimento(itemJn.getIndRecebimento());
					itemAf.setIndContrato(itemJn.getIndContrato());
					itemAf.setIndConsignado(itemJn.getIndConsignado());
					
					this.popularSituacaoAnteriorValores(itemAf, itemJn);
					this.popularSituacaoAnteriorOutros(itemAf, itemJn);
					
					this.getScoItemAutorizacaoFornDAO().persistir(itemAf);
				}
			}
			}
		}
	}
	
	public void excluirItemAutorizacaoFornecimentoJn (
			ScoItemAutorizacaoFornJn itemAutorizacaoFornecimentoJn) throws ApplicationBusinessException {
		try {
			this.getScoItemAutorizacaoFornJnDAO().remover(itemAutorizacaoFornecimentoJn);
		} catch (Exception e) {
			throw new ApplicationBusinessException(
					ScoItemAutorizacaoFornJnRNExceptionCode.ERRO_EXCLUIR_ITEM_AUT_FORNECIMENTO_JN);
		}
	}
	
	protected ScoItemAutorizacaoFornJnDAO getScoItemAutorizacaoFornJnDAO() {
		return scoItemAutorizacaoFornJnDAO;
	}
	
	protected ScoItemAutorizacaoFornDAO getScoItemAutorizacaoFornDAO() {
		return scoItemAutorizacaoFornDAO;
	}

	protected ScoItemAutorizacaoFornRN getScoItemAutorizacaoFornRN() {
		return scoItemAutorizacaoFornRN;		
	}

	protected IRegistroColaboradorFacade getRegistroColaboradorFacade() {
		return this.registroColaboradorFacade;
	}

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}
	
}
