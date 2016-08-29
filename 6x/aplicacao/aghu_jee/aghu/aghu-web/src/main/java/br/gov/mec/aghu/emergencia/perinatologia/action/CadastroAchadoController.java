package br.gov.mec.aghu.emergencia.perinatologia.action;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.emergencia.business.IEmergenciaFacade;
import br.gov.mec.aghu.exames.business.IExamesFacade;
import br.gov.mec.aghu.exames.vo.RegiaoAnatomicaVO;
import br.gov.mec.aghu.model.McoAchado;
import br.gov.mec.aghu.perinatologia.vo.AchadoVO;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.commons.seguranca.IPermissionService;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.core.locator.ServiceLocator;

public class CadastroAchadoController extends ActionController {



	/**
	 * 
	 */
	private static final long serialVersionUID = 7070905230600248441L;

	@EJB
	private IEmergenciaFacade emergenciaFacade;
	
	@EJB
	private IExamesFacade examesFacade;
	
	private Boolean indAtivo;
	private Boolean isEdicao;
	private Boolean isMsgAlertaObrig;

	private AchadoVO achadoSelecionado;
	private RegiaoAnatomicaVO regiaoAnatomicaVO;
	private McoAchado achado;
	private final String PAGE_PESQUISA_ACHADO = "pesquisaAchado";
	
	private Boolean hasPermission;

	@PostConstruct
	public void init() {
		begin(conversation);		
	}
	
	public void iniciar() {
		if(achadoSelecionado == null) {
			salvarCamposModoInsercao();
		} else {
			achado = emergenciaFacade.obterAchadoPorChavePrimaria(achadoSelecionado.getSeq());
			if(achado == null) {
				salvarCamposModoInsercao();
			} else {
				salvarCamposModoEdicao();
			}
		}
		verificarPermissoes();
	}
	
	public void verificarPermissoes() {
		hasPermission = getPermissionService().usuarioTemPermissao(obterLoginUsuarioLogado(), "manterAchados", "executar");
	}
	
	public void alterarCampoMsgAlerta() {
		this.isMsgAlertaObrig = achado.getIndExigeObs();
		achado.setMensagemAlerta(null);
	}	
	

	private void salvarCamposModoEdicao() {
		isEdicao = true;
		indAtivo = achado.getIndSituacao().isAtivo();
		regiaoAnatomicaVO = new RegiaoAnatomicaVO(achado.getAelRegiaoAnatomica().getSeq(), achadoSelecionado.getDescricaoRan());
		// Se indObservacao é necessário uma msg de alerta
		isMsgAlertaObrig = achado.getIndExigeObs();
	}

	private void salvarCamposModoInsercao() {
		isEdicao = false;
		indAtivo = true;
		isMsgAlertaObrig = false;
		regiaoAnatomicaVO = null;
		achado = new McoAchado();
		achadoSelecionado = null;
	}
	
	public String gravar() {
		try {
		if(isEdicao) {
			
			emergenciaFacade.atualizarAchado(prepararEntity());
			apresentarMsgNegocio(Severity.INFO, "MENSAGEM_ACHADO_ALTERADO_SUCESSO");
		} else {
			emergenciaFacade.inserirAchado(prepararEntity(), regiaoAnatomicaVO.getDescricao());
			apresentarMsgNegocio(Severity.INFO, "MENSAGEM_ACHADO_INSERIDO_SUCESSO");
			}
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
		return cancelar();
	}

	private McoAchado prepararEntity() {
		achado.setAelRegiaoAnatomica(examesFacade.obterRegiaoAnatomicaPeloId(regiaoAnatomicaVO.getSeq()));
		achado.setIndSituacao(DominioSituacao.getInstance(indAtivo));
		return achado;
	}
	
	private IPermissionService getPermissionService() {
		return ServiceLocator.getBeanRemote(IPermissionService.class, "aghu-casca");
	}
	
	public String cancelar() {
		//limpa os campos, deixando no modo padrão
		salvarCamposModoInsercao();
		return PAGE_PESQUISA_ACHADO;
	}
	
	public List<RegiaoAnatomicaVO> pesquisarRegioesAnatomicas(String param) {
		return  this.returnSGWithCount(emergenciaFacade.pesquisarRegioesAnatomicas((String) param),pesquisarRegioesAnatomicasCount(param));
	}
	
	public Long pesquisarRegioesAnatomicasCount(String param) {
		return emergenciaFacade.pesquisarRegioesAnatomicasCount((String) param);
	}

	public RegiaoAnatomicaVO getRegiaoAnatomicaVO() {
		return regiaoAnatomicaVO;
	}


	public void setRegiaoAnatomicaVO(RegiaoAnatomicaVO regiaoAnatomicaVO) {
		this.regiaoAnatomicaVO = regiaoAnatomicaVO;
	}

	public Boolean getIsEdicao() {
		return isEdicao;
	}

	public void setIsEdicao(Boolean isEdicao) {
		this.isEdicao = isEdicao;
	}

	public McoAchado getAchado() {
		return achado;
	}

	public void setAchado(McoAchado achado) {
		this.achado = achado;
	}

	public Boolean getIndAtivo() {
		return indAtivo;
	}

	public void setIndAtivo(Boolean indAtivo) {
		this.indAtivo = indAtivo;
	}


	public AchadoVO getAchadoSelecionado() {
		return achadoSelecionado;
	}

	public void setAchadoSelecionado(AchadoVO achadoSelecionado) {
		this.achadoSelecionado = achadoSelecionado;
	}

	public Boolean getIsMsgAlertaObrig() {
		return isMsgAlertaObrig;
	}

	public void setIsMsgAlertaObrig(Boolean isMsgAlertaObrig) {
		this.isMsgAlertaObrig = isMsgAlertaObrig;
	}

	public Boolean getHasPermission() {
		return hasPermission;
	}

	public void setHasPermission(Boolean hasPermission) {
		this.hasPermission = hasPermission;
	}
}
