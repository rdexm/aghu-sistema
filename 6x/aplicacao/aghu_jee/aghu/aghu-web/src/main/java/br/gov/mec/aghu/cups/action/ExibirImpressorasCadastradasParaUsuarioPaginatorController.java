package br.gov.mec.aghu.cups.action;

import java.net.UnknownHostException;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.cups.cadastrosbasicos.business.ICadastrosBasicosCupsFacade;
import br.gov.mec.aghu.model.cups.ImpComputadorImpressora;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;


public class ExibirImpressorasCadastradasParaUsuarioPaginatorController extends ActionController implements ActionPaginator {

	@PostConstruct
	protected void inicializar(){
	 this.begin(conversation);
		this.dataModel.reiniciarPaginator();
		
		try {
			this.ipAddress = super.getEnderecoIPv4HostRemoto().getHostAddress();
		} catch (UnknownHostException e) {
			LOG.error(
					"Erro ao obter endereço de rede de origem do acesso ao AGHU.",
					e);
		}
	}

	@Inject @Paginator
	private DynamicDataModel<ImpComputadorImpressora> dataModel;

	private static final Log LOG = LogFactory.getLog(ExibirImpressorasCadastradasParaUsuarioPaginatorController.class);

	private static final long serialVersionUID = -8064600302690590918L;



	@EJB
	private ICadastrosBasicosCupsFacade cadastrosBasicosCupsFacade;

	private String ipAddress;

	@Override
	public Long recuperarCount() {
		return cadastrosBasicosCupsFacade
				.pesquisarComputadorImpressoraCount(ipAddress);
	}

	@Override
	public List<ImpComputadorImpressora> recuperarListaPaginada(
			Integer firstResult, Integer maxResult, String orderProperty,
			boolean asc) {
		return cadastrosBasicosCupsFacade.pesquisarComputadorImpressora(
				firstResult, maxResult, orderProperty, asc, ipAddress);
	}

	public String getIpAddress() {
		return ipAddress;
	}

	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}
 


	public DynamicDataModel<ImpComputadorImpressora> getDataModel() {
	 return dataModel;
	}

	public void setDataModel(DynamicDataModel<ImpComputadorImpressora> dataModel) {
	 this.dataModel = dataModel;
	}
}
