package br.gov.mec.aghu.prescricaomedica.cadastroapoio.action;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.ambulatorio.pesquisa.vo.ConsultasAgendaVO;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.farmacia.business.IFarmaciaFacade;
import br.gov.mec.aghu.internacao.vo.AghUnidadesFuncionaisVO;
import br.gov.mec.aghu.model.AfaViaAdmUnf;
import br.gov.mec.aghu.model.AfaViaAdministracao;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.prescricaomedica.vo.AfaViaAdmUnfVO;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;

/**
 * @author Carlos Leilson
 * 
 */

public class VincularViasAdmPermUndController extends ActionController implements ActionPaginator {

	@Inject @Paginator
	private DynamicDataModel<ConsultasAgendaVO> dataModel;

	private static final Log LOG = LogFactory.getLog(VincularViasAdmPermUndController.class);

	/**
	 * 
	 */
	private static final long serialVersionUID = 6011824563325990071L;
	
	@EJB
	private IAghuFacade aghuFacade;
	
	@EJB
	private IFarmaciaFacade farmaciaFacade;
	
	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;
	
	private RapServidores servidor;
	
	private AfaViaAdministracao viaAdministracao;

	private AfaViaAdmUnf unf;
	
	private AghUnidadesFuncionais unidFuncionais;
	
	private AfaViaAdmUnfVO viaVinculadoUnidade;
	
	private List<AfaViaAdmUnfVO> vias;
	
	@PostConstruct
	protected void inicializar(){
	 this.begin(conversation);
	}
	
	//suggestions unidade funcional
	public List<AghUnidadesFuncionais> listarUnidFuncional(String param){
		return returnSGWithCount(getAghuFacade().listarUnidadeExecutora(param), getAghuFacade().listarUnidadeExecutoraCount(param));
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<AfaViaAdmUnfVO> recuperarListaPaginada(Integer firstResult,
			Integer maxResult, String orderProperty, boolean asc) {
		List<AfaViaAdmUnfVO> result = new ArrayList<AfaViaAdmUnfVO>();
		if(unidFuncionais != null) {
			result = this.farmaciaFacade.listarViaAdmUnfVO(firstResult, maxResult, orderProperty, asc, unidFuncionais);
		} else {
			result = this.farmaciaFacade.listarViaAdmUnfVO(firstResult, maxResult, orderProperty, asc, new AghUnidadesFuncionais());
		}
		
		if (result == null){
			result = new ArrayList<AfaViaAdmUnfVO>();
		}		
		
		return result;
	}

	@Override
	public Long recuperarCount() {
		Long count = null;
		AghUnidadesFuncionaisVO vo = new AghUnidadesFuncionaisVO();
		if(unidFuncionais != null) {
			vo.setSeq(unidFuncionais.getSeq());
		}
		count =  this.farmaciaFacade.listarViaAdmUnfCount(vo);
		return count;
	}
	
	public void inicio() throws ApplicationBusinessException{
		servidor=registroColaboradorFacade.obterServidorAtivoPorUsuario(obterLoginUsuarioLogado(), new Date());
		this.dataModel.reiniciarPaginator();
	}	
	
	public void pesquisar(){
		this.dataModel.reiniciarPaginator();
	}
	
	public void cadastrarTodos(){
		try {
			this.farmaciaFacade.incluirTodasViasUnf(this.unidFuncionais,getServidor());
			apresentarMsgNegocio("MENSAGEM_VIAS_UNIDADES_VINCULADOS_SUCESSO");
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
			LOG.error(e.getMessage(),e);
		}		
		this.dataModel.reiniciarPaginator();
	}
	
	public void vincularUnidades() {
		try {
			this.farmaciaFacade.incluirViasUnfs(this.unidFuncionais,getServidor());
			apresentarMsgNegocio("MENSAGEM_VIAS_UNIDADE_VINCULADOS_SUCESSO");
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
			LOG.error(e.getMessage(),e);
		}		
		this.dataModel.reiniciarPaginator();
	}
	
	public void excluir(){
		try {
			this.farmaciaFacade.excluir(viaVinculadoUnidade,getServidor());
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
			LOG.error(e.getMessage(),e);
		}
		this.dataModel.reiniciarPaginator();
	}		
	
	public AfaViaAdministracao getViaAdministracao() {
		return viaAdministracao;
	}

	public void setViaAdministracao(AfaViaAdministracao viaAdministracao) {
		this.viaAdministracao = viaAdministracao;
	}

	public IAghuFacade getAghuFacade() {
		return aghuFacade;
	}

	public void setAghuFacade(IAghuFacade aghuFacade) {
		this.aghuFacade = aghuFacade;
	}

	public AfaViaAdmUnf getUnf() {
		return unf;
	}

	public void setUnf(AfaViaAdmUnf unf) {
		this.unf = unf;
	}

	public AfaViaAdmUnfVO getViaVinculadoUnidade() {
		return viaVinculadoUnidade;
	}

	public void setViaVinculadoUnidade(AfaViaAdmUnfVO viaVinculadoUnidade) {
		this.viaVinculadoUnidade = viaVinculadoUnidade;
	}

	public List<AfaViaAdmUnfVO> getVias() {
		return vias;
	}

	public void setVias(List<AfaViaAdmUnfVO> vias) {
		this.vias = vias;
	}

	public AghUnidadesFuncionais getUnidFuncionais() {
		return unidFuncionais;
	}

	public void setUnidFuncionais(AghUnidadesFuncionais unidFuncionais) {
		this.unidFuncionais = unidFuncionais;
	}

	public RapServidores getServidor() {
		return servidor;
	}

	public void setServidor(RapServidores servidor) {
		this.servidor = servidor;
	}
	
	public DynamicDataModel<ConsultasAgendaVO> getDataModel() {
		return dataModel;
	}

	public void setDataModel(DynamicDataModel<ConsultasAgendaVO> dataModel) {
		this.dataModel = dataModel;
	}

}
