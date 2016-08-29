package br.gov.mec.aghu.internacao.cadastrosbasicos.action;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import br.gov.mec.aghu.internacao.cadastrosbasicos.business.ICadastrosBasicosInternacaoFacade;
import br.gov.mec.aghu.model.AinAcomodacoes;
import br.gov.mec.aghu.model.AinTipoCaracteristicaLeito;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;

/**
 * Classe responsável por controlar as ações da listagem de tipo de
 * caracteristicas de leito.
 */

public class TiposCaracteristicasLeitoPaginatorController extends ActionController implements ActionPaginator {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1526326580217454955L;

	/**
	 * 
	 */
	@EJB
	private ICadastrosBasicosInternacaoFacade cadastrosBasicosInternacaoFacade;
	
	@Inject @Paginator
	private DynamicDataModel<AinAcomodacoes> dataModel;

	private AinTipoCaracteristicaLeito tipoCaracteristicaLeito;
	
	private AinTipoCaracteristicaLeito tipoCaracteristicaSelecionado;
	
	private final String PAGE_CADASTRAR_TIPO_CARACTERISTICA_LEITO = "tiposCaracteristicaLeitoCRUD";
	
	@PostConstruct
	public void init() {
		begin(conversation, true);
		tipoCaracteristicaLeito = new AinTipoCaracteristicaLeito();
	}
	
	public void pesquisar() {
		this.dataModel.reiniciarPaginator();
	}

	public void limparPesquisa() {
		this.setTipoCaracteristicaLeito(new AinTipoCaracteristicaLeito());
		this.dataModel.limparPesquisa();
	}

	/**
	 * Método que realiza a ação do botão excluir na tela de Pesquisa de Tipo de
	 * Característica de Leito.
	 */
	public void excluir() {
		try{
			if(this.getTipoCaracteristicaSelecionado()!=null){
				this.cadastrosBasicosInternacaoFacade
				.removerTiposCaracteristicaLeito(tipoCaracteristicaSelecionado.getCodigo());
				apresentarMsgNegocio(Severity.INFO,
						"MENSAGEM_SUCESSO_REMOCAO_TIPO_CARACTERISTICA_LEITO",
						tipoCaracteristicaSelecionado.getDescricao());
			} else {
				apresentarMsgNegocio(Severity.INFO,
						"MENSAGEM_ERRO_REMOCAO_TIPO_CARACTERISTICA_LEITO_INVALIDA");
			}
			this.setTipoCaracteristicaSelecionado(null);
		}
			catch (ApplicationBusinessException e) {
				apresentarExcecaoNegocio(e);
			}
	}
	
	// ### Paginação ###

	@Override
	public Long recuperarCount() {
		Long count = cadastrosBasicosInternacaoFacade
				.pesquisaTiposCaracteristicaLeitoCount(
						this.getTipoCaracteristicaLeito().getCodigo(),
						this.getTipoCaracteristicaLeito().getDescricao());

		return count;
	}

	@Override
	public List<AinTipoCaracteristicaLeito> recuperarListaPaginada(
			Integer firstResult, Integer maxResults, String orderProperty,
			boolean asc) {

		List<AinTipoCaracteristicaLeito> result = this.cadastrosBasicosInternacaoFacade
				.pesquisaTiposCaracteristicaLeito(firstResult, maxResults,
						orderProperty, asc,
						this.getTipoCaracteristicaLeito().getCodigo(),
						this.getTipoCaracteristicaLeito().getDescricao());

		if (result == null) {
			result = new ArrayList<AinTipoCaracteristicaLeito>();
		}

		return result;
	}
	
	public String editar() {
		return PAGE_CADASTRAR_TIPO_CARACTERISTICA_LEITO;
	}

	public DynamicDataModel<AinAcomodacoes> getDataModel() {
		return dataModel;
	}

	public void setDataModel(DynamicDataModel<AinAcomodacoes> dataModel) {
		this.dataModel = dataModel;
	}

	public AinTipoCaracteristicaLeito getTipoCaracteristicaLeito() {
		return tipoCaracteristicaLeito;
	}

	public void setTipoCaracteristicaLeito(AinTipoCaracteristicaLeito tipoCaracteristicaLeito) {
		this.tipoCaracteristicaLeito = tipoCaracteristicaLeito;
	}

	public AinTipoCaracteristicaLeito getTipoCaracteristicaSelecionado() {
		return tipoCaracteristicaSelecionado;
	}

	public void setTipoCaracteristicaSelecionado(AinTipoCaracteristicaLeito tipoCaracteristicaSelecionado) {
		this.tipoCaracteristicaSelecionado = tipoCaracteristicaSelecionado;
	}


}
