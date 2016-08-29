package br.gov.mec.aghu.paciente.prontuarioonline.action;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import br.gov.mec.aghu.paciente.prontuarioonline.business.IProntuarioOnlineFacade;
import br.gov.mec.aghu.paciente.prontuarioonline.vo.AltasAmbulatoriasPolVO;
import br.gov.mec.aghu.paciente.prontuarioonline.vo.NodoPOLVO;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.action.SecurityController;
import br.gov.mec.aghu.core.business.SelectionQualifier;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.BaseException;

public class AltasAmbulatoriaisPOLController extends ActionController implements ActionPaginator {

	private static final long serialVersionUID = 7137082321232271674L;

	@EJB
	private IProntuarioOnlineFacade prontuarioOnlineFacade;
	
	@Inject
	private SecurityController securityController;

	@Inject @SelectionQualifier @RequestScoped
	private NodoPOLVO itemPOL;	

	
	@Inject @Paginator
	private DynamicDataModel<AltasAmbulatoriasPolVO> dataModel;

	private List<AltasAmbulatoriasPolVO> consultas;
	
	private AltasAmbulatoriasPolVO registroSelecionado;
	
	private Boolean temPermissaoAcessoAdminPOL;
	

	@PostConstruct
	protected void inicializar(){
		this.begin(conversation);
		temPermissaoAcessoAdminPOL = securityController.usuarioTemPermissao("acessoAdminPOL", "acessar");
	}
	
	@Override
	public Long recuperarCount() {
		return prontuarioOnlineFacade.pesquisarAltasAmbulatoriaisPolCount((Integer) itemPOL.getParametros().get(NodoPOLVO.COD_PACIENTE));
	}
	
	public Boolean getUsuarioAdministrativo(){
		if(temPermissaoAcessoAdminPOL){
			return Boolean.TRUE;
		}else{
			return Boolean.FALSE;
		}
	}

	@Override
	public List<AltasAmbulatoriasPolVO> recuperarListaPaginada(Integer firstResult, Integer maxResult, String orderProperty, boolean asc) {
		try {
			return prontuarioOnlineFacade.pesquisarAltasAmbulatoriaisPol((Integer) itemPOL.getParametros().get(NodoPOLVO.COD_PACIENTE), firstResult, maxResult, orderProperty, asc);
		} catch ( BaseException e) {
			apresentarExcecaoNegocio(e);
			return  new ArrayList<AltasAmbulatoriasPolVO>();
		}	
	}
	
	public void inicio() {
		registroSelecionado = new AltasAmbulatoriasPolVO();
		dataModel.reiniciarPaginator();
		dataModel.setDefaultMaxRow(5);
	}

	public Boolean habilitarBotaoVisualizar(){
		Boolean result = registroSelecionado != null  && registroSelecionado.getvAlta()!=null;
		//NAO pode habilitar se tiver a permissao de acessoAdminPOL
		if(temPermissaoAcessoAdminPOL){
			return Boolean.FALSE;
		}
		
		return result/* && permiteImprimirAltaAmbulatorialPOL*/;
	}

	public List<AltasAmbulatoriasPolVO> getConsultas() {
		return consultas;
	}

	public void setConsultas(List<AltasAmbulatoriasPolVO> consultas) {
		this.consultas = consultas;
	}

	public AltasAmbulatoriasPolVO getRegistroSelecionado() {
		return registroSelecionado;
	}

	public void setRegistroSelecionado(AltasAmbulatoriasPolVO registroSelecionado) {
		this.registroSelecionado = registroSelecionado;
	}

	public DynamicDataModel<AltasAmbulatoriasPolVO> getDataModel() {
		return dataModel;
	}

	public void setDataModel(DynamicDataModel<AltasAmbulatoriasPolVO> dataModel) {
		this.dataModel = dataModel;
	}

	public NodoPOLVO getItemPOL() {
		return itemPOL;
	}

	public void setItemPOL(NodoPOLVO itemPOL) {
		this.itemPOL = itemPOL;
	}
}