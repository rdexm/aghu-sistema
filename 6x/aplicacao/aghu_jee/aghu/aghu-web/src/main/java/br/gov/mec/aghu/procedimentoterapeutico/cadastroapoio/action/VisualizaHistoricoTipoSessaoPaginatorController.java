package br.gov.mec.aghu.procedimentoterapeutico.cadastroapoio.action;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import br.gov.mec.aghu.model.MptTipoSessao;
import br.gov.mec.aghu.procedimentoterapeutico.business.IProcedimentoTerapeuticoFacade;
import br.gov.mec.aghu.procedimentoterapeutico.vo.HistoricoSessaoJnVO;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;

public class VisualizaHistoricoTipoSessaoPaginatorController extends ActionController implements ActionPaginator {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7494841733808417534L;
	
	private static final String PAGE_PESQUISAR_TIPO_SESSAO = "procedimentoterapeutico-pesquisaTiposSessao";
	
	@Inject @Paginator
	private DynamicDataModel<HistoricoSessaoJnVO> dataModel;
	
	@EJB
	private IProcedimentoTerapeuticoFacade procedimentoTerapeuticoFacade;
	
	private Short sessaoSeq;
	private MptTipoSessao mptTipoSessao;
	private List <HistoricoSessaoJnVO> listaHistorico = new ArrayList<HistoricoSessaoJnVO>();
	private boolean descricaoDiferente;
	private boolean unidadeFuncionalDiferente;
	private boolean tipoAgendaDiferente;
	private boolean tempoDiferente;
	private boolean disponibilidadeDiferente;
	private boolean apacDiferente;
	private boolean consentimentoDiferente;
	private boolean frequenciaDiferente;
	private boolean situacaoDiferente;
	
	private HistoricoSessaoJnVO itemOnMouseOver;
	
	@PostConstruct
	public void init() {
		begin(conversation, true);
	}
	
	public void inicio(){
		if(sessaoSeq != null){
			mptTipoSessao = procedimentoTerapeuticoFacade.obterTipoSessaoOriginal(sessaoSeq);
			this.dataModel.reiniciarPaginator();
		}
		controlarUltimaAlteracao();
	}
	
	public void limpar() {
		sessaoSeq = null;
		mptTipoSessao = null;
		dataModel.limparPesquisa();
	}
	
	@Override
	public Long recuperarCount() {
		return procedimentoTerapeuticoFacade.obterHistoricoSessaoJnCount(sessaoSeq);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<HistoricoSessaoJnVO> recuperarListaPaginada(Integer firstResult, Integer maxResults, String orderProperty, boolean asc) {
		return procedimentoTerapeuticoFacade.obterHistoricoSessaoJn(firstResult, maxResults, orderProperty, asc, sessaoSeq);
	}

	public void controlarUltimaAlteracao() {
		listaHistorico = procedimentoTerapeuticoFacade.obterHistoricoSessaoJn(0,1,null,true, sessaoSeq);
		if(listaHistorico != null && !listaHistorico.isEmpty()){
			verificaDescricaoDiferente();
			verificaUnidadeFuncionalDiferente();
			verificaTipoAgendaDiferente();
			verificaTempoDiferente();
			verificaDisponibilidadeDiferente();
			verificaApacDiferente();
			verificaConsentimentoDiferente();
			verificaFrenquenciaDiferente();
			verificaSituacaoDiferente();
		}
	}
	
	private void verificaDescricaoDiferente(){
		if(listaHistorico.get(0).getDescricao()==null){
			return;
		}
		if (listaHistorico.get(0).getDescricao().equalsIgnoreCase(mptTipoSessao.getDescricao())) {
			descricaoDiferente = false;
		} else {
			descricaoDiferente = true;
		}
	}
	
	private void verificaUnidadeFuncionalDiferente(){
		if (listaHistorico.get(0).getDescricaoUnidade().equalsIgnoreCase(mptTipoSessao.getUnidadeFuncional().getDescricao())) {
			unidadeFuncionalDiferente = false;
		} else {
			unidadeFuncionalDiferente = true;
		}
	}

	private void verificaTipoAgendaDiferente(){
		if (listaHistorico.get(0).getTipoAgenda().equals(mptTipoSessao.getTipoAgenda())) {
			tipoAgendaDiferente = false;
		} else {
			tipoAgendaDiferente = true;
		}
	}

	private void verificaTempoDiferente(){
		if(listaHistorico.get(0).getTempoFixo() == null && mptTipoSessao.getTempoFixo() == null){
			tempoDiferente = false;
		}else if(listaHistorico.get(0).getTempoFixo() == null || mptTipoSessao.getTempoFixo() == null){
			tempoDiferente = true;
		}else if (listaHistorico.get(0).getTempoFixo().equals(mptTipoSessao.getTempoFixo())) {
			tempoDiferente = false;
		} else {
			tempoDiferente = true;
		}
	}

	private void verificaDisponibilidadeDiferente(){
		if(listaHistorico.get(0).getTempoDisponivel() == null && mptTipoSessao.getTempoDisponivel() == null){
			disponibilidadeDiferente = false;
		}else if(listaHistorico.get(0).getTempoDisponivel() == null || mptTipoSessao.getTempoDisponivel() == null){
			disponibilidadeDiferente = true;
		}else if (listaHistorico.get(0).getTempoDisponivel().equals(mptTipoSessao.getTempoDisponivel())) {
			disponibilidadeDiferente = false;
		} else {
			disponibilidadeDiferente = true;
		}
	}
	
	private void verificaApacDiferente(){
		if (listaHistorico.get(0).getIndApac() == mptTipoSessao.getIndApac()) {
			apacDiferente = false;
		} else {
			apacDiferente = true;
		}
	}

	private void verificaConsentimentoDiferente(){
		if (listaHistorico.get(0).getIndConsentimento() == mptTipoSessao.getIndConsentimento()) {
			consentimentoDiferente = false;
		} else {
			consentimentoDiferente = true;
		}
	}

	private void verificaFrenquenciaDiferente(){
		if (listaHistorico.get(0).getIndFrequencia() == mptTipoSessao.getIndFrequencia()) {
			frequenciaDiferente = false;
		} else {
			frequenciaDiferente = true;
		}
	}
	

	private void verificaSituacaoDiferente(){
		if (listaHistorico.get(0).getIndSituacao().equals(mptTipoSessao.getIndSituacao())) {
			situacaoDiferente = false;
		} else {
			situacaoDiferente = true;
		}
	}
	
	public String voltar(){
		limpar();
		return PAGE_PESQUISAR_TIPO_SESSAO;
	}
	
	public Short getSessaoSeq() {
		return sessaoSeq;
	}

	public void setSessaoSeq(Short sessaoSeq) {
		this.sessaoSeq = sessaoSeq;
	}


	public IProcedimentoTerapeuticoFacade getProcedimentoTerapeuticoFacade() {
		return procedimentoTerapeuticoFacade;
	}

	public void setProcedimentoTerapeuticoFacade(
			IProcedimentoTerapeuticoFacade procedimentoTerapeuticoFacade) {
		this.procedimentoTerapeuticoFacade = procedimentoTerapeuticoFacade;
	}

	public MptTipoSessao getMptTipoSessao() {
		return mptTipoSessao;
	}

	public void setMptTipoSessao(MptTipoSessao mptTipoSessao) {
		this.mptTipoSessao = mptTipoSessao;
	}

	public DynamicDataModel<HistoricoSessaoJnVO> getDataModel() {
		return dataModel;
	}

	public void setDataModel(DynamicDataModel<HistoricoSessaoJnVO> dataModel) {
		this.dataModel = dataModel;
	}

	public List<HistoricoSessaoJnVO> getListaHistorico() {
		return listaHistorico;
	}

	public void setListaHistorico(List<HistoricoSessaoJnVO> listaHistorico) {
		this.listaHistorico = listaHistorico;
	}

	public boolean isDescricaoDiferente() {
		return descricaoDiferente;
	}

	public void setDescricaoDiferente(boolean descricaoDiferente) {
		this.descricaoDiferente = descricaoDiferente;
	}

	public boolean isUnidadeFuncionalDiferente() {
		return unidadeFuncionalDiferente;
	}

	public void setUnidadeFuncionalDiferente(boolean unidadeFuncionalDiferente) {
		this.unidadeFuncionalDiferente = unidadeFuncionalDiferente;
	}

	public boolean isTipoAgendaDiferente() {
		return tipoAgendaDiferente;
	}

	public void setTipoAgendaDiferente(boolean tipoAgendaDiferente) {
		this.tipoAgendaDiferente = tipoAgendaDiferente;
	}

	public boolean isTempoDiferente() {
		return tempoDiferente;
	}

	public void setTempoDiferente(boolean tempoDiferente) {
		this.tempoDiferente = tempoDiferente;
	}

	public boolean isApacDiferente() {
		return apacDiferente;
	}

	public void setApacDiferente(boolean apacDiferente) {
		this.apacDiferente = apacDiferente;
	}

	public boolean isConsentimentoDiferente() {
		return consentimentoDiferente;
	}

	public void setConsentimentoDiferente(boolean consentimentoDiferente) {
		this.consentimentoDiferente = consentimentoDiferente;
	}

	public boolean isSituacaoDiferente() {
		return situacaoDiferente;
	}

	public void setSituacaoDiferente(boolean situacaoDiferente) {
		this.situacaoDiferente = situacaoDiferente;
	}

	public boolean isDisponibilidadeDiferente() {
		return disponibilidadeDiferente;
	}

	public void setDisponibilidadeDiferente(boolean disponibilidadeDiferente) {
		this.disponibilidadeDiferente = disponibilidadeDiferente;
	}

	public boolean isFrequenciaDiferente() {
		return frequenciaDiferente;
	}

	public void setFrequenciaDiferente(boolean frequenciaDiferente) {
		this.frequenciaDiferente = frequenciaDiferente;
	}

	public HistoricoSessaoJnVO getItemOnMouseOver() {
		return itemOnMouseOver;
	}

	public void setItemOnMouseOver(HistoricoSessaoJnVO itemOnMouseOver) {
		this.itemOnMouseOver = itemOnMouseOver;
	}
}
