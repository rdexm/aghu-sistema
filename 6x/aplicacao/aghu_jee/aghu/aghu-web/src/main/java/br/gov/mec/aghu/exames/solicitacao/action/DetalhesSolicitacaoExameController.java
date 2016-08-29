package br.gov.mec.aghu.exames.solicitacao.action;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.exames.solicitacao.business.ISolicitacaoExameFacade;
import br.gov.mec.aghu.exames.solicitacao.vo.SolicitacaoExameVO;
import br.gov.mec.aghu.model.AelAmostraItemExames;
import br.gov.mec.aghu.model.AelItemSolicitacaoExames;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.BaseException;



public class DetalhesSolicitacaoExameController extends ActionController {

	@PostConstruct
	protected void inicializar(){
	 this.begin(conversation);
	}

	private static final Log LOG = LogFactory.getLog(DetalhesSolicitacaoExameController.class);
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -6291618719986275783L;

	@EJB
	private ISolicitacaoExameFacade solicitacaoExameFacade;
	
	private Integer soeSeq;
	private String voltarPara;
	private SolicitacaoExameVO solicitacaoExame;
	private List<AelItemSolicitacaoExames> listaItensExames = new ArrayList<AelItemSolicitacaoExames>();
	private Integer itemExameSelecionadoId;
	private Integer itemExameId;
	private AelItemSolicitacaoExames exameItem;
	private List<AelAmostraItemExames> listaAmostrasItem = new ArrayList<AelAmostraItemExames>();
	
	public void iniciar() {
	 

		try {
			this.solicitacaoExame = this.solicitacaoExameFacade.buscaDetalhesSolicitacaoExameVO(soeSeq);
			this.carregarListItensExames();
			
		} catch (BaseException e) {
			LOG.error(e.getMessage(), e);
			super.apresentarExcecaoNegocio(e);
		}
	
	}
	
	public void limpar() {
		this.iniciar();
	}
	
	public String cancelar() {
		return "cancelado";
	}
	
	public List<AghUnidadesFuncionais> buscarUnidadeFuncionais(Object objPesquisa) {
		return solicitacaoExameFacade.buscarUnidadeFuncionais((String) objPesquisa);
	}
	
	public void carregarListItensExames() throws BaseException {
		listaItensExames = this.solicitacaoExameFacade.buscarItensExames(soeSeq);
		this.exameItem = listaItensExames.get(0);

		this.carregarItens();
	}
	
	/**
	 * Método chamado para o botão voltar
	 */
	public String voltar() {
		return this.voltarPara;
	}

	public void carregarItens() throws BaseException {
		itemExameSelecionadoId = Integer.valueOf(exameItem.getId().getSeqp());
		listaAmostrasItem =  this.solicitacaoExameFacade.buscarAmostrasItemExame(exameItem.getId().getSoeSeq() , itemExameSelecionadoId.shortValue());
	}
	
	public List<AelAmostraItemExames> getListaAmostrasItem() {
		return listaAmostrasItem;
	}

	public void setListaAmostrasItem(List<AelAmostraItemExames> listaAmostrasItem) {
		this.listaAmostrasItem = listaAmostrasItem;
	}

	public List<AelItemSolicitacaoExames> getListaItensExames() {
		return listaItensExames;
	}

	public void setListaItensExames(List<AelItemSolicitacaoExames> listaItensExames) {
		this.listaItensExames = listaItensExames;
	}

	public Integer getItemExameSelecionadoId() {
		return itemExameSelecionadoId;
	}

	public void setItemExameSelecionadoId(Integer itemExameSelecionadoId) {
		this.itemExameSelecionadoId = itemExameSelecionadoId;
	}

	public Integer getItemExameId() {
		return itemExameId;
	}

	public void setItemExameId(Integer itemExameId) {
		this.itemExameId = itemExameId;
	}

	

	public AelItemSolicitacaoExames getExameItem() {
		return exameItem;
	}

	public void setExameItem(AelItemSolicitacaoExames exameItem) {
		this.exameItem = exameItem;
	}


	public void selecionarItemExame(AelItemSolicitacaoExames exame) {
		try {
			this.exameItem = exame;
			this.carregarItens();
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}			
	}
	
	public List<RapServidores> buscarServidoresSolicitacaoExame(Object objPesquisa) {
		return solicitacaoExameFacade.buscarServidoresSolicitacaoExame((String) objPesquisa);
	}

	public Integer getSoeSeq() {
		return soeSeq;
	}

	public void setSoeSeq(Integer seq) {
		this.soeSeq = seq;
	}

	public SolicitacaoExameVO getSolicitacaoExame() {
		return solicitacaoExame;
	}

	public void setSolicitacaoExame(SolicitacaoExameVO solicitacaoExame) {
		this.solicitacaoExame = solicitacaoExame;
	}

	public String getVoltarPara() {
		return voltarPara;
	}

	public void setVoltarPara(String voltarPara) {
		this.voltarPara = voltarPara;
	}
}