package br.gov.mec.aghu.prescricaomedica.action;

import java.text.SimpleDateFormat;
import java.util.Iterator;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.context.FacesContext;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.AfaMensCalculoNpt;
import br.gov.mec.aghu.model.FatItensProcedHospitalar;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.prescricaomedica.business.IPrescricaoMedicaFacade;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;

public class MensagemParaCalculoNPTPaginatorController extends ActionController implements ActionPaginator {


	/**
	 * 
	 */
	private static final long serialVersionUID = 865742L;

	@EJB
	IPrescricaoMedicaFacade prescricaoMedicaFacade; 
	
	@EJB 
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;
	
	@Inject @Paginator
	private DynamicDataModel<FatItensProcedHospitalar> dataModel;
	
	private final String PAGE_PESQUISAR_MENSAGEM_CALCULO_NPT = "mensagensParaCalculoNPTList";
	private final String PAGE_MANTER_MENSAGEM_CALCULO_NPT = "mensagensParaCalculoNPTCRUD";
	
	private AfaMensCalculoNpt filtro;
	private DominioSituacao situacao;
	private Boolean situacaoCRUD;
	private AfaMensCalculoNpt acaoSelection;
	
	private RapServidores servidorLogado;
	private Boolean blockAvitoEdit;
	
	private AfaMensCalculoNpt mensagemMouseOver;
	
	@PostConstruct
	public void inicializar() {
		begin(conversation);
		filtro = new AfaMensCalculoNpt();
		situacao = null;
		servidorLogado = servidorLogadoFacade.obterServidorLogado();
	}
	
	@Override
	public Long recuperarCount() {
		return prescricaoMedicaFacade.listarMensagensCalculoNptCount(filtro);
	}

	@Override
	public List<AfaMensCalculoNpt> recuperarListaPaginada(Integer firstResult, Integer maxResult, String orderProperty, boolean asc) {
		return prescricaoMedicaFacade.listarMensagensCalculoNpt(firstResult, maxResult, orderProperty, asc, filtro);
	}

	public void limparPesquisa(){
		Iterator<UIComponent> componentes = FacesContext.getCurrentInstance().getViewRoot().getFacetsAndChildren();
		while (componentes.hasNext()) {
			limparValoresSubmetidos(componentes.next());
		}
		filtro = new AfaMensCalculoNpt();
		situacao = null;
		dataModel.limparPesquisa();
		dataModel.setPesquisaAtiva(Boolean.FALSE);
	}
	
	public void pesquisar(){
		if(situacao != null){
			filtro.setIndSituacao(situacao.isAtivo() ? "A" : "I");
		}else{
			filtro.setIndSituacao(null);
		}
		dataModel.reiniciarPaginator();
		dataModel.setPesquisaAtiva(Boolean.TRUE);
	}
	
	public String editar(){
		if(acaoSelection != null && acaoSelection.getIndSituacao() != null && !acaoSelection.getIndSituacao().equals("")){
			situacaoCRUD = acaoSelection.getIndSituacao().equals("A") ? Boolean.TRUE: Boolean.FALSE;
			blockAvitoEdit = situacaoCRUD; 
		}
		return PAGE_MANTER_MENSAGEM_CALCULO_NPT;
	}
	
	public String novo(){
		acaoSelection = new AfaMensCalculoNpt();
		situacaoCRUD = Boolean.TRUE;
		return PAGE_MANTER_MENSAGEM_CALCULO_NPT;
	}
	
	public void excluir(){
		if(acaoSelection != null && acaoSelection.getSeq() != null){
			prescricaoMedicaFacade.excluirMensagemCalculoNpt(acaoSelection);
			apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_EXCLUSAO_MENSAGEM_CALCULO_NPT");
			acaoSelection = new AfaMensCalculoNpt();
		}
	}
	
	public String voltar(){
		acaoSelection = new AfaMensCalculoNpt();
		return PAGE_PESQUISAR_MENSAGEM_CALCULO_NPT;
	}
	
	public void confirmar(){
		Boolean novo = null;
		if(acaoSelection != null){
			acaoSelection.setIndSituacao(situacaoCRUD ? "A":"I");		
			if(acaoSelection.getSeq() == null){
				novo = Boolean.TRUE;
			}else{
				novo = Boolean.FALSE;
			}
		}
		try {
			prescricaoMedicaFacade.salvarMensagemCalculoNpt(acaoSelection, servidorLogado);
			blockAvitoEdit = situacaoCRUD;
			if(novo){
				apresentarMsgNegocio(Severity.INFO,"MSG_MENSAGEM_GRAVADA_SUCESSO");
			}else{
				apresentarMsgNegocio(Severity.INFO,"MSG_MENSAGEM_ALTERADA_SUCESSO");
			}
		} catch (ApplicationBusinessException e ) {
			apresentarExcecaoNegocio(e);
		} catch (BaseException e){
			apresentarExcecaoNegocio(e);
		}
	}
	
	public String obterHint(AfaMensCalculoNpt item){
		String data = new SimpleDateFormat("dd/MM/yyyy").format(item.getCriadoEm());
		RapServidores rapPes = prescricaoMedicaFacade.obterHintAfaMensCalculoNpt(item);
		StringBuffer hint = new StringBuffer("Criado por : ");
		hint.append(rapPes.getPessoaFisica().getNome()+StringUtils.LF);
		String criadoEm = "Criado em : "+data;
		hint.append(criadoEm);
		return hint.toString();
	}
	
	private void limparValoresSubmetidos(Object object) {
		if (object == null || object instanceof UIComponent == false) {
			return;
		}
		Iterator<UIComponent> uiComponent = ((UIComponent) object)
				.getFacetsAndChildren();
		while (uiComponent.hasNext()) {
			limparValoresSubmetidos(uiComponent.next());
		}
		if (object instanceof UIInput) {
			((UIInput) object).resetValue();
		}
	}
	
	public String obterDescricaoTruncada(String item, Integer tamanhoMaximo) {
		if (item.length() > tamanhoMaximo) {
			item = StringUtils.abbreviate(item, tamanhoMaximo);
		}
		item = StringUtils.upperCase(item);
		return item;
	}
	
	public void mostrarMensagem(){
		if(!situacaoCRUD){
			apresentarMsgNegocio(Severity.INFO,"SITUACAO_INATIVA");
		}
	}
	
	public IPrescricaoMedicaFacade getPrescricaoMedicaFacade() {
		return prescricaoMedicaFacade;
	}

	public void setPrescricaoMedicaFacade(
			IPrescricaoMedicaFacade prescricaoMedicaFacade) {
		this.prescricaoMedicaFacade = prescricaoMedicaFacade;
	}

	public AfaMensCalculoNpt getFiltro() {
		return filtro;
	}

	public void setFiltro(AfaMensCalculoNpt filtro) {
		this.filtro = filtro;
	}

	public DynamicDataModel<FatItensProcedHospitalar> getDataModel() {
		return dataModel;
	}

	public void setDataModel(DynamicDataModel<FatItensProcedHospitalar> dataModel) {
		this.dataModel = dataModel;
	}

	public AfaMensCalculoNpt getAcaoSelection() {
		return acaoSelection;
	}

	public void setAcaoSelection(AfaMensCalculoNpt acaoSelection) {
		this.acaoSelection = acaoSelection;
	}

	public DominioSituacao getSituacao() {
		return situacao;
	}

	public void setSituacao(DominioSituacao situacao) {
		this.situacao = situacao;
	}

	public Boolean getSituacaoCRUD() {
		return situacaoCRUD;
	}

	public void setSituacaoCRUD(Boolean situacaoCRUD) {
		this.situacaoCRUD = situacaoCRUD;
	}

	public RapServidores getServidorLogado() {
		return servidorLogado;
	}

	public void setServidorLogado(RapServidores servidorLogado) {
		this.servidorLogado = servidorLogado;
	}

	public IServidorLogadoFacade getServidorLogadoFacade() {
		return servidorLogadoFacade;
	}

	public void setServidorLogadoFacade(IServidorLogadoFacade servidorLogadoFacade) {
		this.servidorLogadoFacade = servidorLogadoFacade;
	}

	public IRegistroColaboradorFacade getRegistroColaboradorFacade() {
		return registroColaboradorFacade;
	}

	public void setRegistroColaboradorFacade(
			IRegistroColaboradorFacade registroColaboradorFacade) {
		this.registroColaboradorFacade = registroColaboradorFacade;
	}

	public Boolean getBlockAvitoEdit() {
		return blockAvitoEdit;
	}

	public void setBlockAvitoEdit(Boolean blockAvitoEdit) {
		this.blockAvitoEdit = blockAvitoEdit;
	}

	public AfaMensCalculoNpt getMensagemMouseOver() {
		return mensagemMouseOver;
	}

	public void setMensagemMouseOver(AfaMensCalculoNpt mensagemMouseOver) {
		this.mensagemMouseOver = mensagemMouseOver;
	}
}
