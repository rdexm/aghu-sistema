package br.gov.mec.aghu.paciente.prontuarioonline.action;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import br.gov.mec.aghu.model.AipOrigemDocGEDs;
import br.gov.mec.aghu.paciente.prontuarioonline.business.IProntuarioOnlineFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.Severity;


public class ManterOrigemDocGEDsPaginatorController extends ActionController implements ActionPaginator {
	private static final String PAGE_EDIT="origemDocGEDsCRUD";

	@PostConstruct
	protected void inicializar(){
	 this.begin(conversation);
	}

	@Inject @Paginator
	private DynamicDataModel<AipOrigemDocGEDs> dataModel;
	
	private AipOrigemDocGEDs selecionado;

	private static final long serialVersionUID = 1067170323927794105L;

	@EJB
	private IProntuarioOnlineFacade polFacade;

	private AipOrigemDocGEDs origemFiltro = new AipOrigemDocGEDs();

	public AipOrigemDocGEDs getOrigemFiltro() {
		return origemFiltro;
	}

	public void setOrigemFiltro(AipOrigemDocGEDs origemFiltro) {
		this.origemFiltro = origemFiltro;
	}

	public void pesquisar() {
		dataModel.reiniciarPaginator();
	}
	
	public String editar(){
		return PAGE_EDIT;
	}
	
	public String inserir(){
		return PAGE_EDIT;
	}

	public void limpar() {
		this.setOrigemFiltro(new AipOrigemDocGEDs());
		dataModel.limparPesquisa();
	}

	@Override
	public Long recuperarCount() {
		return polFacade.pesquisarAipOrigemDocGEDsCount(origemFiltro);
	}

	@Override
	public List<AipOrigemDocGEDs> recuperarListaPaginada(Integer firstResult, Integer maxResult, String orderProperty, boolean asc) {
		List<AipOrigemDocGEDs> retorno = polFacade.pesquisarAipOrigemDocGEDs(origemFiltro, firstResult, maxResult, orderProperty, asc);

		if (retorno == null) {
			retorno = new ArrayList<AipOrigemDocGEDs>();
			this.apresentarMsgNegocio(Severity.INFO,"MENSAGEM_REGISTRO_NAO_LOCALIZADO");
		}

		return retorno;
	}

	public DynamicDataModel<AipOrigemDocGEDs> getDataModel() {
		return dataModel;
	}

	public void setDataModel(DynamicDataModel<AipOrigemDocGEDs> dataModel) {
		this.dataModel = dataModel;
	}

	public AipOrigemDocGEDs getSelecionado() {
		return selecionado;
	}

	public void setSelecionado(AipOrigemDocGEDs selecionado) {
		this.selecionado = selecionado;
	}
}
