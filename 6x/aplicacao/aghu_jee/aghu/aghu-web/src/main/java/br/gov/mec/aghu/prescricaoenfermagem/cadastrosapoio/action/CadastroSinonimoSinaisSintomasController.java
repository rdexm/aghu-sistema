package br.gov.mec.aghu.prescricaoenfermagem.cadastrosapoio.action;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.business.prescricaoenfermagem.cadastrosapoio.IPrescricaoEnfermagemApoioFacade;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.EpeCaractDefinidora;
import br.gov.mec.aghu.model.EpeSinCaractDefinidora;
import br.gov.mec.aghu.model.EpeSinCaractDefinidoraId;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;

public class CadastroSinonimoSinaisSintomasController extends ActionController implements ActionPaginator {

	private static final long serialVersionUID = 8377124220277489320L;

	private static final Log LOG = LogFactory.getLog(CadastroSinonimoSinaisSintomasController.class);

	private static final String PAGE_SINAIS_SINTOMAS_LIST = "sinaisSintomasList";

	@EJB
	private IPrescricaoEnfermagemApoioFacade prescricaoEnfermagemApoioFacade;
	
	@Inject @Paginator
	private DynamicDataModel<EpeSinCaractDefinidora> dataModel;

	private List<EpeSinCaractDefinidora> listaSinonimos;

	private EpeCaractDefinidora sinaisSintomas;
	private EpeSinCaractDefinidora sinonimoExclusao;

	private Integer cdeCodigo;
	private String descricao;
	private DominioSituacao situacao;

	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}

	public void inicio() {
	 

		if (!dataModel.getPesquisaAtiva()) {
			dataModel.reiniciarPaginator();
		}
	
	}

	@Override
	public Long recuperarCount() {
		return prescricaoEnfermagemApoioFacade.buscarSinonimoSinaisSintomasCount(cdeCodigo);
	}

	@Override
	public List<EpeSinCaractDefinidora> recuperarListaPaginada(Integer firstResult, Integer maxResult, String orderProperty,
			boolean asc) {
		return prescricaoEnfermagemApoioFacade
				.buscarSinonimoSinaisSintomas(cdeCodigo, firstResult, maxResult, orderProperty, asc);
	}

	/* Método voltar */
	public String voltar() {
		dataModel.setPesquisaAtiva(Boolean.FALSE);
		return PAGE_SINAIS_SINTOMAS_LIST;
	}

	/* Método Adicionar */
	public void adicionar() {

		try {

			EpeSinCaractDefinidoraId caracteristicaDefinidoraId = new EpeSinCaractDefinidoraId();
			caracteristicaDefinidoraId.setCdeCodigo(cdeCodigo);
			caracteristicaDefinidoraId.setCdeCodigoPossui(sinaisSintomas.getCodigo());

			EpeSinCaractDefinidora novaCaracteristicaDefinidora = new EpeSinCaractDefinidora();
			novaCaracteristicaDefinidora.setId(caracteristicaDefinidoraId);

			novaCaracteristicaDefinidora.setCaractDefinidoraByCdeCodigo(sinaisSintomas);
			novaCaracteristicaDefinidora.setCaractDefinidoraByCdeCodigoPossui(sinaisSintomas);

			prescricaoEnfermagemApoioFacade.inserirEpeSinCaractDefinidora(novaCaracteristicaDefinidora);

		} catch (ApplicationBusinessException e) {
			LOG.error("Exceção capturada em 'EpeSinCaractDefinidoraRN', método 'preInsert'.", e);
			apresentarExcecaoNegocio(e);
		} finally {
			setSinaisSintomas(null);
			dataModel.reiniciarPaginator();
		}

	}

	/* Método Excluir */
	public void excluir() {
		String msgRetorno = this.prescricaoEnfermagemApoioFacade.removerEpeSinCaractDefinidora(sinonimoExclusao.getId());
		apresentarMsgNegocio(Severity.INFO, msgRetorno);
		dataModel.reiniciarPaginator();
	}

	/* Suggestion "Sinais e Sintomas" */
	public List<EpeCaractDefinidora> pesquisarSinaisSintomas(String objSinaisSintomas) {
		Long maximo = prescricaoEnfermagemApoioFacade.buscarSinonimoSinaisSintomasCount(cdeCodigo);
		
		this.listaSinonimos = this.prescricaoEnfermagemApoioFacade.buscarSinonimoSinaisSintomas(cdeCodigo, 0, maximo.intValue(), null, true);
		return prescricaoEnfermagemApoioFacade.pesquisarCaracteristicasDefinidoras(objSinaisSintomas, this.listaSinonimos);
	}

	/* Getters and Setters */
	public void setCdeCodigo(Integer cdeCodigo) {
		this.cdeCodigo = cdeCodigo;
	}

	public Integer getCdeCodigo() {
		return cdeCodigo;
	}

	public void setSinaisSintomas(EpeCaractDefinidora sinaisSintomas) {
		this.sinaisSintomas = sinaisSintomas;
	}

	public EpeCaractDefinidora getSinaisSintomas() {
		return sinaisSintomas;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setSituacao(DominioSituacao situacao) {
		this.situacao = situacao;
	}

	public DominioSituacao getSituacao() {
		return situacao;
	}

	public void setSinonimoExclusao(EpeSinCaractDefinidora sinonimoExclusao) {
		this.sinonimoExclusao = sinonimoExclusao;
	}

	public EpeSinCaractDefinidora getSinonimoExclusao() {
		return sinonimoExclusao;
	}

	public void setListaSinonimos(List<EpeSinCaractDefinidora> listaSinonimos) {
		this.listaSinonimos = listaSinonimos;
	}

	public List<EpeSinCaractDefinidora> getListaSinonimos() {
		return listaSinonimos;
	}

	public DynamicDataModel<EpeSinCaractDefinidora> getDataModel() {
		return dataModel;
	}

	public void setDataModel(DynamicDataModel<EpeSinCaractDefinidora> dataModel) {
		this.dataModel = dataModel;
	}
}
