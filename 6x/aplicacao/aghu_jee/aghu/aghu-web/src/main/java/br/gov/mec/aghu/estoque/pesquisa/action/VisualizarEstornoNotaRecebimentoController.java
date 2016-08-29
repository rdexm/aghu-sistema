package br.gov.mec.aghu.estoque.pesquisa.action;

import java.net.UnknownHostException;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.dominio.DominioSituacaoAutorizacaoFornecimento;
import br.gov.mec.aghu.estoque.business.IEstoqueBeanFacade;
import br.gov.mec.aghu.estoque.business.IEstoqueFacade;
import br.gov.mec.aghu.model.SceItemNotaRecebimento;
import br.gov.mec.aghu.model.SceNotaRecebimento;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;

public class VisualizarEstornoNotaRecebimentoController extends ActionController {

	private static final Log LOG = LogFactory.getLog(VisualizarEstornoNotaRecebimentoController.class);

	private static final long serialVersionUID = 865721437861723297L;

	private static final String PESQUISAR_ESTORNAR_NOTA_RECEBIMENTO = "estoque-pesquisarEstornarNotaRecebimento";
	
	private SceNotaRecebimento notaRecebimento;
	private Integer seqNotaRecebimento = null;
	
	@EJB
	private IEstoqueFacade estoqueFacade;
	
	@EJB
	private IEstoqueBeanFacade estoqueBeanFacade;
	
	private Boolean exibirItens = Boolean.FALSE;
	private List<SceItemNotaRecebimento> listaItens = null;
	
	private String voltarPara; 
	
	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}
	
	public void iniciar() {
	 

		
		this.notaRecebimento = estoqueFacade.obterSceNotaRecebimentoFULL(seqNotaRecebimento);
		if (notaRecebimento != null) {
			this.setListaItens(estoqueFacade.obterItensNotaRecebimento(notaRecebimento));
		}
		if (listaItens.size() != 0) {
			this.setExibirItens(Boolean.TRUE);

		} else {
			this.setExibirItens(Boolean.FALSE);
		}
	
	}
	
	
	public Boolean exibirBotaoEstornar() {
		return !(this.notaRecebimento.getAutorizacaoFornecimento().getSituacao().equals(DominioSituacaoAutorizacaoFornecimento.EX) || this.notaRecebimento.getEstorno());
	}

	/**
	 * Realiza o estorno da nota de recebimento
	 */
	public void  estornar() {
		
		//sceReqMateriaisEstornar = this.estoqueFacade.obterRequisicaoMaterial(getReqMaterialSeq());
		String nomeMicrocomputador = null;
		try {
			nomeMicrocomputador = super.getEnderecoRedeHostRemoto();
		} catch (UnknownHostException e) {
			LOG.error("Exceção caputada:", e);
		}
				
		try {
			estoqueBeanFacade.estornarNotaRecebimento(notaRecebimento.getSeq(), nomeMicrocomputador);
			apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_ESTORNO_NR");
		
			this.seqNotaRecebimento = null;
		
		} catch (BaseException e) {
			super.apresentarExcecaoNegocio(e);
		}
	}

	public SceNotaRecebimento getNotaRecebimento() {
		return notaRecebimento;
	}

	public void setNotaRecebimento(SceNotaRecebimento notaRecebimento) {
		this.notaRecebimento = notaRecebimento;
	}

	public Integer getSeqNotaRecebimento() {
		return seqNotaRecebimento;
	}

	public void setSeqNotaRecebimento(Integer seqNotaRecebimento) {
		this.seqNotaRecebimento = seqNotaRecebimento;
	}
	
	public String getFornecedor(){
		String fornecedor = "";
		String numfornecedor = "";
		if(notaRecebimento != null && this.notaRecebimento.getAutorizacaoFornecimento()!=null && this.notaRecebimento.getAutorizacaoFornecimento().getPropostaFornecedor() !=null){
			numfornecedor = notaRecebimento.getAutorizacaoFornecimento().getPropostaFornecedor().getFornecedor()!=null?notaRecebimento.getAutorizacaoFornecimento().getPropostaFornecedor().getFornecedor().getNumero().toString():"";
			fornecedor = notaRecebimento.getAutorizacaoFornecimento().getPropostaFornecedor().getFornecedor()!=null?notaRecebimento.getAutorizacaoFornecimento().getPropostaFornecedor().getFornecedor().getRazaoSocial():"";
		}
		
		return numfornecedor +" - "+fornecedor;
		
	}
	
	public String getNomeFornecedor(){
		String fornecedor = "";
		if(this.notaRecebimento != null && this.notaRecebimento.getAutorizacaoFornecimento()!=null && this.notaRecebimento.getAutorizacaoFornecimento().getPropostaFornecedor() !=null){
			
			fornecedor = notaRecebimento.getAutorizacaoFornecimento().getPropostaFornecedor().getFornecedor()!=null?notaRecebimento.getAutorizacaoFornecimento().getPropostaFornecedor().getFornecedor().getNomeFantasia():"";
		}
		
		return fornecedor;
	}
	
	
	public Integer getNroProcCompra(){
		Integer retorno = null;
		
		if(notaRecebimento !=null && notaRecebimento.getAutorizacaoFornecimento()!=null && notaRecebimento.getAutorizacaoFornecimento().getPropostaFornecedor()!=null){
			retorno = notaRecebimento.getAutorizacaoFornecimento().getPropostaFornecedor().getId().getLctNumero();
		}
		return retorno;
	}
	
	
	public String voltar() {
		if(voltarPara != null){
			return this.voltarPara;
		} else {
			return PESQUISAR_ESTORNAR_NOTA_RECEBIMENTO;
		}
	}


	public Boolean getExibirItens() {
		return exibirItens;
	}

	public void setExibirItens(Boolean exibirItens) {
		this.exibirItens = exibirItens;
	}

	public List<SceItemNotaRecebimento> getListaItens() {
		return listaItens;
	}

	public void setListaItens(List<SceItemNotaRecebimento> listaItens) {
		this.listaItens = listaItens;
	}

	public String getVoltarPara() {
		return voltarPara;
	}

	public void setVoltarPara(String voltarPara) {
		this.voltarPara = voltarPara;
	}
}