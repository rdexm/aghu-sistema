package br.gov.mec.aghu.procedimentoterapeutico.cadastroapoio.action;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import br.gov.mec.aghu.controleinfeccao.vo.HistoricoSalaJnVO;
import br.gov.mec.aghu.model.MptSalas;
import br.gov.mec.aghu.procedimentoterapeutico.business.IProcedimentoTerapeuticoFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;

public class VisualizaHistoricoSalasPaginatorController extends ActionController implements ActionPaginator {

	private static final long serialVersionUID = 749484173380840753L;

	private static final String PAGE_PESQUISA_ACOMODACOES = "pesquisaAcomodacoes";

	@EJB
	private IProcedimentoTerapeuticoFacade procedimentoTerapeuticoFacade;
	

	@Inject @Paginator
	private DynamicDataModel<HistoricoSalaJnVO> dataModel;
	
	private Short salaSeq;
	private MptSalas mptSala;
	
	private MptSalas itemSelecionado;
	
	private List<HistoricoSalaJnVO> listaHistorico = new ArrayList<HistoricoSalaJnVO>();
	private boolean descricaoDiferente;
	private boolean especialidadeDiferente;
	private boolean tipoSessaoDiferente;
	private boolean situacaoDiferente;

	@PostConstruct
	public void init() {
		begin(conversation, true);
	}
	
	public void inicio() {
		
		if (salaSeq != null) {
			mptSala = this.procedimentoTerapeuticoFacade.obterMptSalaPorSeq(salaSeq);
			
			this.dataModel.reiniciarPaginator();
		}
		
		this.controlarUltimaAlteracao();
	}
	
	public void pesquisar() {
		this.dataModel.reiniciarPaginator();
	}
	
	@Override
	public Long recuperarCount() { 
		return this.procedimentoTerapeuticoFacade.obterHistoricoSalaJnCount(salaSeq);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<HistoricoSalaJnVO> recuperarListaPaginada(Integer firstResult, Integer maxResults, String orderProperty, boolean asc) {
		return this.procedimentoTerapeuticoFacade.obterHistoricoSalaJn(firstResult, maxResults, orderProperty, asc, salaSeq);
	}	

	public void limpar() {
		this.salaSeq = null;
		this.itemSelecionado = null;
		this.dataModel.limparPesquisa();
	}
	
	public String voltar() {
		this.limpar();
		return PAGE_PESQUISA_ACOMODACOES;
	}
	
	public void controlarUltimaAlteracao() {
		listaHistorico = this.procedimentoTerapeuticoFacade.obterHistoricoSalaJn(0, 1, null, true, salaSeq);
		if (listaHistorico != null && !listaHistorico.isEmpty()) {
			this.verificarDescricaoDiferente();
			this.verificarEspecialidadeDiferente();
			this.verificarTipoSessaoDiferente();
			this.verificarSituacaoDiferente();
		}
	}
	
	private void verificarDescricaoDiferente() {
		if (listaHistorico.get(0).getDescSalaJn().equalsIgnoreCase(mptSala.getDescricao())) {
			this.descricaoDiferente = false;
		} else {
			this.descricaoDiferente = true;
		}
	}
	
	private void verificarEspecialidadeDiferente() {
		if (listaHistorico.get(0).getEspSeq().equals(mptSala.getEspecialidade().getSeq())) {
			this.especialidadeDiferente = false;
		} else {
			this.especialidadeDiferente = true;
		}
	}
	
	private void verificarTipoSessaoDiferente() {
		if (listaHistorico.get(0).getTpSeq().equals(mptSala.getTipoSessao().getSeq())) {
			this.tipoSessaoDiferente = false;
		} else {
			this.tipoSessaoDiferente = true;
		}
	}
	
	private void verificarSituacaoDiferente() {
		if (listaHistorico.get(0).getSituacao().equals(mptSala.getIndSituacao())) {
			this.situacaoDiferente = false;
		} else {
			this.situacaoDiferente = true;
		}
	}
	
	
	// GETTERS AND SETTERS

	public DynamicDataModel<HistoricoSalaJnVO> getDataModel() {
		return dataModel;
	}

	public void setDataModel(DynamicDataModel<HistoricoSalaJnVO> dataModel) {
		this.dataModel = dataModel;
	}

	public Short getSalaSeq() {
		return salaSeq;
	}

	public void setSalaSeq(Short salaSeq) {
		this.salaSeq = salaSeq;
	}

	public MptSalas getItemSelecionado() {
		return itemSelecionado;
	}

	public void setItemSelecionado(MptSalas itemSelecionado) {
		this.itemSelecionado = itemSelecionado;
	}

	public List<HistoricoSalaJnVO> getListaHistorico() {
		return listaHistorico;
	}

	public void setListaHistorico(List<HistoricoSalaJnVO> listaHistorico) {
		this.listaHistorico = listaHistorico;
	}

	public MptSalas getMptSala() {
		return mptSala;
	}

	public void setMptSala(MptSalas mptSala) {
		this.mptSala = mptSala;
	}

	public boolean isDescricaoDiferente() {
		return descricaoDiferente;
	}

	public void setDescricaoDiferente(boolean descricaoDiferente) {
		this.descricaoDiferente = descricaoDiferente;
	}

	public boolean isEspecialidadeDiferente() {
		return especialidadeDiferente;
	}

	public void setEspecialidadeDiferente(boolean especialidadeDiferente) {
		this.especialidadeDiferente = especialidadeDiferente;
	}

	public boolean isTipoSessaoDiferente() {
		return tipoSessaoDiferente;
	}

	public void setTipoSessaoDiferente(boolean tipoSessaoDiferente) {
		this.tipoSessaoDiferente = tipoSessaoDiferente;
	}

	public boolean isSituacaoDiferente() {
		return situacaoDiferente;
	}

	public void setSituacaoDiferente(boolean situacaoDiferente) {
		this.situacaoDiferente = situacaoDiferente;
	}
}
