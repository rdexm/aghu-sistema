package br.gov.mec.aghu.procedimentoterapeutico.cadastroapoio.action;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import br.gov.mec.aghu.controleinfeccao.vo.HistoricoAcomodacaoJnVO;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.model.MptLocalAtendimento;
import br.gov.mec.aghu.procedimentoterapeutico.business.IProcedimentoTerapeuticoFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;

public class VisualizaHistoricoAcomodacoesPaginatorController extends ActionController implements ActionPaginator {

	private static final long serialVersionUID = 749484173380840753L;

	private static final String PAGE_CADASTRA_LOCAIS_ATENDIMENTO = "cadastraAcomodacoes";

	@EJB
	private IProcedimentoTerapeuticoFacade procedimentoTerapeuticoFacade;
	

	@Inject @Paginator
	private DynamicDataModel<HistoricoAcomodacaoJnVO> dataModel;
	
	private Short localSeq;
	private MptLocalAtendimento mptLocalAtendimento;
	
	private MptLocalAtendimento itemSelecionado;
	
	private List<HistoricoAcomodacaoJnVO> listaHistorico = new ArrayList<HistoricoAcomodacaoJnVO>();
	private boolean descricaoDiferente;
	private boolean indReservaDiferente;
	private boolean tipoAcomodacaoDiferente;
	private boolean situacaoDiferente;

	@PostConstruct
	public void init() {
		begin(conversation, true);
	}
	
	public void inicio() {
		
		if (localSeq != null) {
			mptLocalAtendimento = procedimentoTerapeuticoFacade.obterMptLocalAtendimentoPorSeq(localSeq);
			
			this.dataModel.reiniciarPaginator();
		}
		
		this.controlarUltimaAlteracao();
	}
	
	public void pesquisar() {
		this.dataModel.reiniciarPaginator();
	}
	
	@Override
	public Long recuperarCount() { 
		return this.procedimentoTerapeuticoFacade.obterHistoricoAcomodacaoJnCount(localSeq);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<HistoricoAcomodacaoJnVO> recuperarListaPaginada(Integer firstResult, Integer maxResults, String orderProperty, boolean asc) {
		return this.procedimentoTerapeuticoFacade.obterHistoricoAcomodacaoJn(firstResult, maxResults, orderProperty, asc, localSeq);
	}	

	public void limpar() {
		this.localSeq = null;
		this.itemSelecionado = null;
		this.dataModel.limparPesquisa();
	}
	
	public String voltar() {
		this.limpar();
		return PAGE_CADASTRA_LOCAIS_ATENDIMENTO;
	}
	
	public String obterDominioSimNaoIndReserva(Boolean indReserva) {
		return DominioSimNao.getInstance(indReserva).getDescricao();
	}
	
	public void controlarUltimaAlteracao() {
		listaHistorico = this.procedimentoTerapeuticoFacade.obterHistoricoAcomodacaoJn(0, 1, null, true, localSeq);
		if (listaHistorico != null && !listaHistorico.isEmpty()) {
			this.verificarDescricaoDiferente();
			this.verificarIndReservaDiferente();
			this.verificarTipoAcomodacaoDiferente();
			this.verificarSituacaoDiferente();
		}
	}
	
	private void verificarDescricaoDiferente() {
		if (listaHistorico.get(0).getDescAcomodacaoJn().equalsIgnoreCase(mptLocalAtendimento.getDescricao())) {
			this.descricaoDiferente = false;
		} else {
			this.descricaoDiferente = true;
		}
	}
	
	private void verificarIndReservaDiferente() {
		if (listaHistorico.get(0).getReserva().equals(mptLocalAtendimento.getIndReserva())) {
			this.indReservaDiferente = false;
		} else {
			this.indReservaDiferente = true;
		}
	}
	
	private void verificarTipoAcomodacaoDiferente() {
		if (listaHistorico.get(0).getTipoLocal().equals(mptLocalAtendimento.getTipoLocal())) {
			this.tipoAcomodacaoDiferente = false;
		} else {
			this.tipoAcomodacaoDiferente = true;
		}
	}
	
	private void verificarSituacaoDiferente() {
		if (listaHistorico.get(0).getSituacao().equals(mptLocalAtendimento.getIndSituacao())) {
			this.situacaoDiferente = false;
		} else {
			this.situacaoDiferente = true;
		}
	}
	
	// GETTERS AND SETTERS

	public DynamicDataModel<HistoricoAcomodacaoJnVO> getDataModel() {
		return dataModel;
	}

	public void setDataModel(DynamicDataModel<HistoricoAcomodacaoJnVO> dataModel) {
		this.dataModel = dataModel;
	}

	public Short getLocalSeq() {
		return localSeq;
	}

	public void setLocalSeq(Short localSeq) {
		this.localSeq = localSeq;
	}

	public MptLocalAtendimento getItemSelecionado() {
		return itemSelecionado;
	}

	public void setItemSelecionado(MptLocalAtendimento itemSelecionado) {
		this.itemSelecionado = itemSelecionado;
	}

	public MptLocalAtendimento getMptLocalAtendimento() {
		return mptLocalAtendimento;
	}

	public void setMptLocalAtendimento(MptLocalAtendimento mptLocalAtendimento) {
		this.mptLocalAtendimento = mptLocalAtendimento;
	}

	public boolean isDescricaoDiferente() {
		return descricaoDiferente;
	}

	public void setDescricaoDiferente(boolean descricaoDiferente) {
		this.descricaoDiferente = descricaoDiferente;
	}

	public boolean isIndReservaDiferente() {
		return indReservaDiferente;
	}

	public void setIndReservaDiferente(boolean indReservaDiferente) {
		this.indReservaDiferente = indReservaDiferente;
	}

	public boolean isTipoAcomodacaoDiferente() {
		return tipoAcomodacaoDiferente;
	}

	public void setTipoAcomodacaoDiferente(boolean tipoAcomodacaoDiferente) {
		this.tipoAcomodacaoDiferente = tipoAcomodacaoDiferente;
	}

	public boolean isSituacaoDiferente() {
		return situacaoDiferente;
	}

	public void setSituacaoDiferente(boolean situacaoDiferente) {
		this.situacaoDiferente = situacaoDiferente;
	}

	public List<HistoricoAcomodacaoJnVO> getListaHistorico() {
		return listaHistorico;
	}

	public void setListaHistorico(List<HistoricoAcomodacaoJnVO> listaHistorico) {
		this.listaHistorico = listaHistorico;
	}	
}
