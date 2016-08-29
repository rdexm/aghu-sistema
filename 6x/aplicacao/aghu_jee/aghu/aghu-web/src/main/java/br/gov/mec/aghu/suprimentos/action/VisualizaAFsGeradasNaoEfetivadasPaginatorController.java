/**
 * 
 */
package br.gov.mec.aghu.suprimentos.action;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.compras.business.IComprasFacade;
import br.gov.mec.aghu.dominio.DominioModalidadeEmpenho;
import br.gov.mec.aghu.model.ScoAutorizacaoForn;
import br.gov.mec.aghu.model.ScoFornecedor;
import br.gov.mec.aghu.model.ScoRefCodes;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseListException;

/**
 * @author bruno.mourao
 * 
 */


public class VisualizaAFsGeradasNaoEfetivadasPaginatorController extends ActionController implements ActionPaginator {
    @Inject @Paginator
	private DynamicDataModel<ScoAutorizacaoForn> dataModel;
	private static final Log LOG = LogFactory.getLog(VisualizaAFsGeradasNaoEfetivadasPaginatorController.class);
	private static final long serialVersionUID = -927528372948670957L;
	
	private Integer numeroLicitacao;
	private Short nroComplemento;
	private Date dtGeracao;
	private Date dtPrevEntrega;
	private DominioModalidadeEmpenho modalidadeEmpenho;
	private ScoRefCodes situacao;
	private List<ScoAutorizacaoForn> listaAutorizacaoForn;
	
	@EJB
	protected IComprasFacade comprasFacade;
	
	
	@PostConstruct
	protected void inicializar(){
		LOG.debug("Begin conversation");
		this.begin(conversation);
	}
	
	public List<DominioModalidadeEmpenho> obterModEmpenhos() {
		DominioModalidadeEmpenho[] modalidadeEmpenhoArray = DominioModalidadeEmpenho.values();
		List<DominioModalidadeEmpenho> modalidadeEmpenhoList = Arrays.asList(modalidadeEmpenhoArray);		
		return modalidadeEmpenhoList;
	}

	public void pesquisar() {
		try {
			validaPreenchimentoCampos(this.numeroLicitacao, this.nroComplemento);
			dataModel.reiniciarPaginator();
			dataModel.setPesquisaAtiva(true);
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
			dataModel.setPesquisaAtiva(false);
		}
	}

	public List<ScoAutorizacaoForn> recuperarListaPaginada(Integer firstResult,
		Integer maxResult, String orderProperty, boolean asc) {

		try {
			listaAutorizacaoForn = comprasFacade.visualizarAFsGeradas(numeroLicitacao, nroComplemento, dtGeracao, dtPrevEntrega, 
					modalidadeEmpenho, situacao, firstResult, maxResult, orderProperty, asc);
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
			return null;
		}

		return listaAutorizacaoForn;
	}

	@Override
	public Long recuperarCount() {
		Long count = null;
		try {
			count = comprasFacade.visualizarAFsGeradasCount(numeroLicitacao, nroComplemento, dtGeracao, dtPrevEntrega, modalidadeEmpenho, situacao);
		} catch (BaseListException e) {
			apresentarExcecaoNegocio(e);
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
		return count;
	}

	/**
	 * Método do suggestionBox para obter listas de situações
	 * 
	 * @param parametro
	 * @return
	 *  
	 */
	public List<ScoRefCodes> obterListaSituacao(String strPesquisa) throws ApplicationBusinessException {
		try {
			return  this.returnSGWithCount(this.comprasFacade.buscarScoRefCodesPorSituacao(strPesquisa),null);
		} catch (BaseListException e) {
			apresentarExcecaoNegocio(e);
		}
		return null;
	}

	public void limparCampos() {
		numeroLicitacao = null;
		nroComplemento = null;
		dtGeracao = null;
		dtPrevEntrega = null;
		modalidadeEmpenho = null;
		situacao = null;
		this.listaAutorizacaoForn = null;
		this.dataModel.setPesquisaAtiva(false);
	}
	
	public String voltar() {
		return "voltar";
	}
	
	public String getCpfCnpjFormatado(ScoFornecedor fornecedor) {
		String retorno = null;
		if(fornecedor.getCgc() != null){
			retorno = CoreUtil.formatarCNPJ(fornecedor.getCgc());
		}
		if(fornecedor.getCpf() != null){
			retorno = CoreUtil.formataCPF(fornecedor.getCpf());
		}
		return retorno;
	}
	
	public String formataHintNatureza(ScoAutorizacaoForn scoAutorizacaoForn) {
		StringBuffer retorno = new StringBuffer();
		if(scoAutorizacaoForn.getGrupoNaturezaDespesa() != null && scoAutorizacaoForn.getNaturezaDespesa().getId().getGndCodigo() != null){
			retorno.append(scoAutorizacaoForn.getNaturezaDespesa().getId().getGndCodigo()).append(" - ");
		}
		if(scoAutorizacaoForn.getGrupoNaturezaDespesa() != null && scoAutorizacaoForn.getGrupoNaturezaDespesa().getDescricao() != null){
			retorno.append(scoAutorizacaoForn.getGrupoNaturezaDespesa().getDescricao());
		}
		retorno.append('\n');
		if(scoAutorizacaoForn.getNaturezaDespesa() != null && scoAutorizacaoForn.getNaturezaDespesa().getId().getCodigo() != null){
			retorno.append(scoAutorizacaoForn.getNaturezaDespesa().getId().getCodigo()).append(" - ");
		}
		if(scoAutorizacaoForn.getNaturezaDespesa() != null && scoAutorizacaoForn.getNaturezaDespesa().getDescricao() != null){
			retorno.append(scoAutorizacaoForn.getNaturezaDespesa().getDescricao());
		}
		return retorno.toString();
	}
	
	public String formataHintFornecedor(ScoAutorizacaoForn scoAutorizacaoForn) {
		StringBuffer retorno = new StringBuffer("Forncedor: ")
									.append(scoAutorizacaoForn.getPropostaFornecedor().getFornecedor().getNumero())
									.append(" - ")
									.append(scoAutorizacaoForn.getPropostaFornecedor().getFornecedor().getRazaoSocial());
		if(scoAutorizacaoForn.getObservacao() != null){
			retorno.append(" Obs: ").append(scoAutorizacaoForn.getObservacao());
		}
		return retorno.toString();
	}
	
	public String recuperaRvMeaning(ScoAutorizacaoForn scoAutorizacaoForn) {
		String retorno = null;
		try{
			List<ScoRefCodes> scoRefCodes = this.comprasFacade.buscarScoRefCodesPorSituacao(scoAutorizacaoForn.getSituacao().toString());
			retorno = scoRefCodes.get(0).getRvMeaning();
		} catch (BaseListException e) {
			apresentarExcecaoNegocio(e);
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
		return retorno;
	}
	
	/**
	 * Verifica se os campos obrigatórios para pesquisa foram preenchidos
	 * Caso seja digitado algum valor para um campo o outro torna-se obrigatório
	 * Campos: Núm. A.F. e Núm. Compl.
	 * @throws ApplicationBusinessException 
	 */
	private void validaPreenchimentoCampos(Integer numeroLicitacao, Short nroComplemento) throws ApplicationBusinessException {
		this.comprasFacade.validaPreenchimentoCamposPesquisaAFsGeradas(numeroLicitacao, nroComplemento);
	}
	
	// Getters and Setters

	public Integer getNumeroLicitacao() {
		return numeroLicitacao;
	}

	public void setNumeroLicitacao(Integer numeroLicitacao) {
		this.numeroLicitacao = numeroLicitacao;
	}

	public Short getNroComplemento() {
		return nroComplemento;
	}

	public void setNroComplemento(Short nroComplemento) {
		this.nroComplemento = nroComplemento;
	}

	public Date getDtGeracao() {
		return dtGeracao;
	}

	public void setDtGeracao(Date dtGeracao) {
		this.dtGeracao = dtGeracao;
	}

	public Date getDtPrevEntrega() {
		return dtPrevEntrega;
	}

	public void setDtPrevEntrega(Date dtPrevEntrega) {
		this.dtPrevEntrega = dtPrevEntrega;
	}

	public DominioModalidadeEmpenho getModalidadeEmpenho() {
		return modalidadeEmpenho;
	}

	public void setModalidadeEmpenho(DominioModalidadeEmpenho modalidadeEmpenho) {
		this.modalidadeEmpenho = modalidadeEmpenho;
	}

	public ScoRefCodes getSituacao() {
		return situacao;
	}

	public void setSituacao(ScoRefCodes situacao) {
		this.situacao = situacao;
	}

	public void setListaAutorizacaoForn(List<ScoAutorizacaoForn> listaAutorizacaoForn) {
		this.listaAutorizacaoForn = listaAutorizacaoForn;
	}

	public List<ScoAutorizacaoForn> getListaAutorizacaoForn() {
		return listaAutorizacaoForn;
	} 


	public DynamicDataModel<ScoAutorizacaoForn> getDataModel() {
	 return dataModel;
	}

	public void setDataModel(DynamicDataModel<ScoAutorizacaoForn> dataModel) {
	 this.dataModel = dataModel;
	}
}
