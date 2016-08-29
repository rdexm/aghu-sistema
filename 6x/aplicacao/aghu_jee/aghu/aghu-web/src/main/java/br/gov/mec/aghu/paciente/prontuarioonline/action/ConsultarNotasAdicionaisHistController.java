package br.gov.mec.aghu.paciente.prontuarioonline.action;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import br.gov.mec.aghu.exames.business.IExamesFacade;
import br.gov.mec.aghu.model.AelNotasAdicionaisHist;
import br.gov.mec.aghu.paciente.prontuarioonline.vo.ItemSolicitacaoExamePolVO;
import br.gov.mec.aghu.core.action.ActionController;


public class ConsultarNotasAdicionaisHistController extends ActionController {

	private static final long serialVersionUID = 7729286667497407646L;

	@EJB
	private IExamesFacade examesFacade;
	
	private ItemSolicitacaoExamePolVO itemSolicitacaoExameSelecionado;
	private List<AelNotasAdicionaisHist> listaNotasAdicionais;
	private AelNotasAdicionaisHist selecionado;

	
	@PostConstruct
	protected void inicializar(){
		this.begin(conversation);
		listaNotasAdicionais = null;
	}
	
	public void selecionarItemSolicitacaoExame(ItemSolicitacaoExamePolVO itemSolicitacaoExame) {
		itemSolicitacaoExameSelecionado = itemSolicitacaoExame;
		if(listaNotasAdicionais == null){
			listaNotasAdicionais = examesFacade.pesquisarNotasAdicionaisItemSolicitacaoExameHist( itemSolicitacaoExameSelecionado.getSoeSeq(),
																								  itemSolicitacaoExameSelecionado.getSeqp());
		}
	}

	public List<AelNotasAdicionaisHist> getListaNotasAdicionais() {
		return listaNotasAdicionais;
	}

	public void setListaNotasAdicionais(List<AelNotasAdicionaisHist> listaNotasAdicionais) {
		this.listaNotasAdicionais = listaNotasAdicionais;
	}

	public ItemSolicitacaoExamePolVO getItemSolicitacaoExameSelecionado() {
		return itemSolicitacaoExameSelecionado;
	}

	public void setItemSolicitacaoExameSelecionado(ItemSolicitacaoExamePolVO itemSolicitacaoExameSelecionado) {
		this.itemSolicitacaoExameSelecionado = itemSolicitacaoExameSelecionado;
	}

	public AelNotasAdicionaisHist getSelecionado() {
		return selecionado;
	}

	public void setSelecionado(AelNotasAdicionaisHist selecionado) {
		this.selecionado = selecionado;
	}
}
