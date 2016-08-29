package br.gov.mec.aghu.registrocolaborador.cadastrosbasicos.action;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.model.RapRamalTelefonico;
import br.gov.mec.aghu.registrocolaborador.cadastrosbasicos.business.ICadastrosBasicosFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;

public class RamalTelefonicoPaginatorController extends ActionController implements ActionPaginator {

	private static final long serialVersionUID = 4253926443481955207L;

	@EJB
	private ICadastrosBasicosFacade cadastrosBasicosFacade;

	private Integer numeroRamal;
	private DominioSimNao indUrgencia;
	
	private static final String CADASTRAR_RAMAL_TELEFONICO = "cadastrarRamalTelefonico";

	@Inject @Paginator
	private DynamicDataModel<RapRamalTelefonico> dataModel;
	
	private RapRamalTelefonico selecionado;
	
	

	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}

	@Override
	public Long recuperarCount() {
		return cadastrosBasicosFacade.pesquisarRamalTelefonicoCount(numeroRamal, getIndUrgenciaAsString());
	}

	@Override
	public List<RapRamalTelefonico> recuperarListaPaginada(Integer firstResult,Integer maxResult, String orderProperty, boolean asc) {
		return cadastrosBasicosFacade.pesquisarRamalTelefonico(numeroRamal, getIndUrgenciaAsString(), firstResult,
																maxResult, RapRamalTelefonico.Fields.NUMERORAMAL.toString(), true);
	}

	public void pesquisar() {
		dataModel.reiniciarPaginator();
	}
	
	public String editar(){
		return CADASTRAR_RAMAL_TELEFONICO;
	}
	
	public String inserir(){
		return CADASTRAR_RAMAL_TELEFONICO;
	}

	public void limpar() {
		numeroRamal = null;
		indUrgencia = null;
		dataModel.limparPesquisa();
	}

	public void excluir() {
		try {
			this.cadastrosBasicosFacade.excluirRamalTelefonico(selecionado.getNumero());
			apresentarMsgNegocio(Severity.INFO,"MENSAGEM_SUCESSO_REMOCAO_RAMAL",selecionado.getNumeroRamal());

		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
	}

	private String getIndUrgenciaAsString() {
		if (indUrgencia != null) {
			return String.valueOf(indUrgencia);
		}
		return null;
	}

	// getters & setters

	public void setIndUrgencia(DominioSimNao indUrgencia) {
		this.indUrgencia = indUrgencia;
	}

	public DominioSimNao getIndUrgencia() {
		return indUrgencia;
	}

	public void setNumeroRamal(Integer numeroRamal) {
		this.numeroRamal = numeroRamal;
	}

	public Integer getNumeroRamal() {
		return numeroRamal;
	}

	public DynamicDataModel<RapRamalTelefonico> getDataModel() {
		return dataModel;
	}

	public void setDataModel(DynamicDataModel<RapRamalTelefonico> dataModel) {
		this.dataModel = dataModel;
	}

	public RapRamalTelefonico getSelecionado() {
		return selecionado;
	}

	public void setSelecionado(RapRamalTelefonico selecionado) {
		this.selecionado = selecionado;
	}
}
