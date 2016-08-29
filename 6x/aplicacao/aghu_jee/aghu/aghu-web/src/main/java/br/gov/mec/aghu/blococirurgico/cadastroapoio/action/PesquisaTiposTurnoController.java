package br.gov.mec.aghu.blococirurgico.cadastroapoio.action;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import br.gov.mec.aghu.blococirurgico.cadastroapoio.business.IBlocoCirurgicoCadastroApoioFacade;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.MbcTurnos;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;


public class PesquisaTiposTurnoController extends ActionController {

	@PostConstruct
	protected void inicializar(){
	 this.begin(conversation);
	}
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -4615760901841389986L;
	
	@EJB
	private IBlocoCirurgicoCadastroApoioFacade blocoCirurgicoCadastroApoioFacade;
	
	// Campos de filtro
	private MbcTurnos turnoFiltro = new MbcTurnos();
	
	//Campos de cadastro/edicao
	private MbcTurnos turno = new MbcTurnos();
	private Boolean editando = false;
	private boolean pesquisou = false;
	
	
	private List<MbcTurnos> listaTiposTurno;

	public void pesquisar(){
		listaTiposTurno = blocoCirurgicoCadastroApoioFacade.pesquisarTiposTurnoPorFiltro(turnoFiltro);
		turno = new MbcTurnos();
		turno.setAtivo(true);
	    setPesquisou(true);	
	}

	public void gravar(){		
		try {
			blocoCirurgicoCadastroApoioFacade.persistirMbcTurnos(turno);
			this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_INSERIR_TIPOS_TURNO");
			pesquisar();			
		} catch (ApplicationBusinessException e) {			
			apresentarExcecaoNegocio(e);
		}
	}
	
	public void atualizar(){
		try {
			blocoCirurgicoCadastroApoioFacade.atualizarMbcTurnos(turno);
			this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_ATUALIZAR_TIPOS_TURNO");
			pesquisar();
			this.setEditando(Boolean.FALSE);
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
	}
	
	public boolean isItemSelecionado(String turno){
		if(this.editando && turno != null && turno.equals(this.turno.getTurno())){
			return true;
		}
		return false;
	}
	
	public void ativarDesativar(MbcTurnos turnoAtivarDesativar){
		turnoAtivarDesativar.setSituacao(turnoAtivarDesativar.getSituacao().equals(DominioSituacao.A) ? DominioSituacao.I : DominioSituacao.A);
		try {
			blocoCirurgicoCadastroApoioFacade.persistirMbcTurnos(turnoAtivarDesativar);			
			this.apresentarMsgNegocio(Severity.INFO, 
				"MENSAGEM_SUCESSO_ATUALIZAR_TIPOS_TURNO");
		} catch (ApplicationBusinessException e) {			
			apresentarExcecaoNegocio(e);
		}
	}
	
	public void cancelarEdicao(){		
		turno = new MbcTurnos();
		pesquisar();
		editando = false;
	}	
	
	public void limparPesquisa(){
		turno = new MbcTurnos();
		turno.setAtivo(true);
		turnoFiltro = new MbcTurnos();
		editando = false;
		setPesquisou(false);
		listaTiposTurno = new ArrayList<MbcTurnos>();
	}

	
	/*
	 * GETs SETs
	 */

	public MbcTurnos getTurnoFiltro() {
		return turnoFiltro;
	}
	public void setTurnoFiltro(MbcTurnos turnoFiltro) {
		this.turnoFiltro = turnoFiltro;
	}
	public MbcTurnos getTurno() {
		return turno;
	}
	public void setTurno(MbcTurnos turno) {
		this.turno = turno;
	}
	public Boolean getEditando() {
		return editando;
	}
	public void setEditando(Boolean editando) {
		this.editando = editando;
	}
	public List<MbcTurnos> getListaTiposTurno() {
		return listaTiposTurno;
	}
	public void setListaTiposTurno(List<MbcTurnos> listaTiposTurno) {
		this.listaTiposTurno = listaTiposTurno;
	}
	public boolean isPesquisou() {
		return pesquisou;
	}
	public void setPesquisou(boolean pesquisou) {
		this.pesquisou = pesquisou;
	}

}