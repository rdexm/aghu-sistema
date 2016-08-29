package br.gov.mec.aghu.internacao.cadastrosbasicos.action;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import br.gov.mec.aghu.internacao.cadastrosbasicos.business.ICadastrosBasicosInternacaoFacade;
import br.gov.mec.aghu.model.AinAcomodacoes;
import br.gov.mec.aghu.model.AinTiposAltaMedica;
import br.gov.mec.aghu.model.MpmMotivoAltaMedica;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;

/**
 * Classe responsável por controlar as ações da listagem de Tipos de Alta Medica.
 */


public class TiposAltaMedicaPaginatorController extends ActionController implements ActionPaginator {

	/**
	 * 
	 */
	private static final long serialVersionUID = -143453802975578381L;

	@EJB
	private ICadastrosBasicosInternacaoFacade cadastrosBasicosInternacaoFacade;

	
	
	@Inject @Paginator
	private DynamicDataModel<AinAcomodacoes> dataModel;	

	private AinTiposAltaMedica tipoAltaMedica;
	private AinTiposAltaMedica tipoAltaMedicaSelecionada;
	
	private final String PAGE_CADASTRAR_TIPO_ALTA_MEDICA = "tiposAltaMedicaCRUD";
	
	@Inject
	private TiposAltaMedicaController tipoAltaMedicaController;

	
	@PostConstruct
	public void init() {
		begin(conversation, true);
		tipoAltaMedica = new AinTiposAltaMedica();
	}
	
	/**
	 * Ação do botão pesquisar.
	 */
	public void pesquisar() {
		this.dataModel.reiniciarPaginator();
	}

	/**
	 * Limpa a tela de pesquisa.
	 */
	public void limparPesquisa() {
		this.setTipoAltaMedica(new AinTiposAltaMedica());
		this.dataModel.limparPesquisa();
	}
	
	public String editar() {
		tipoAltaMedicaController.setUpdate(true);
		return PAGE_CADASTRAR_TIPO_ALTA_MEDICA;
	}
	
	public String iniciarInclusao() {
		tipoAltaMedicaController.setUpdate(false);
		return PAGE_CADASTRAR_TIPO_ALTA_MEDICA;
	}

	/**
	 * Método que realiza a ação do botão excluir na tela de Pesquisa de
	 * Tipos Alta Médica.
	 */
	public void excluir() {
		try {
			if (tipoAltaMedicaSelecionada != null) {
				this.cadastrosBasicosInternacaoFacade.removerTipoAltaMedica(tipoAltaMedicaSelecionada.getCodigo());
				apresentarMsgNegocio(Severity.INFO,
						"MENSAGEM_SUCESSO_REMOCAO_TIPOALTAMEDICA",
						tipoAltaMedicaSelecionada.getDescricao());
			} else {
				apresentarMsgNegocio(Severity.INFO,
						"MENSAGEM_ERRO_REMOCAO_TIPOALTAMEDICA");
			}
			this.setTipoAltaMedicaSelecionada(null);
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
	}
	
	
	
	// ### Paginação ###
	@Override
	public Long recuperarCount() {
		Long count = cadastrosBasicosInternacaoFacade.pesquisaTiposAltaMedicaCount(this.getTipoAltaMedica().getCodigo(),this.getTipoAltaMedica().getMotivoAltaMedicas(), this.getTipoAltaMedica().getIndSituacao());

		return count;
	}

	@Override
	public List<AinTiposAltaMedica> recuperarListaPaginada(
			Integer firstResult, Integer maxResults, String orderProperty,
			boolean asc) {

		List<AinTiposAltaMedica> result = this.cadastrosBasicosInternacaoFacade
					.pesquisaTiposAltaMedica(firstResult, maxResults, orderProperty,
						asc, this.getTipoAltaMedica().getCodigo(),this.getTipoAltaMedica().getMotivoAltaMedicas(), this.getTipoAltaMedica().getIndSituacao());

		if (result == null) {
			result = new ArrayList<AinTiposAltaMedica>();
		}

		return result;
	}
	
	public List<MpmMotivoAltaMedica> listarMotivosAltaMedica(){
		return cadastrosBasicosInternacaoFacade.pesquisarMotivosAltaMedica(null);
	}
	
	

	public AinTiposAltaMedica getTipoAltaMedica() {
		return tipoAltaMedica;
	}

	public void setTipoAltaMedica(AinTiposAltaMedica tipoAltaMedica) {
		this.tipoAltaMedica = tipoAltaMedica;
	}

	public AinTiposAltaMedica getTipoAltaMedicaSelecionada() {
		return tipoAltaMedicaSelecionada;
	}

	public void setTipoAltaMedicaSelecionada(AinTiposAltaMedica tipoAltaMedicaSelecionada) {
		this.tipoAltaMedicaSelecionada = tipoAltaMedicaSelecionada;
	}

	public DynamicDataModel<AinAcomodacoes> getDataModel() {
		return dataModel;
	}

	public void setDataModel(DynamicDataModel<AinAcomodacoes> dataModel) {
		this.dataModel = dataModel;
	}

}