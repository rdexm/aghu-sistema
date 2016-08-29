package br.gov.mec.aghu.emergencia.perinatologia.action;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;

import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.configuracao.vo.CidVO;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.emergencia.business.IEmergenciaFacade;
import br.gov.mec.aghu.model.McoDiagnostico;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.commons.seguranca.IPermissionService;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.core.locator.ServiceLocator;

/**
 * Controller Incluir/Editar um diagnostico
 * 
 * @author fsantos
 * 
 */
public class DiagnosticoPerinatoController  extends ActionController  {
	
	private static final String TELA_ORIGEM = "/emergencia/pages/perinatologia/diagnosticoCRUD.xhtml";
	
	private static final long serialVersionUID = 6589546988357451478L;
	private final String PAGE_PESQUISA_DIAGNOSTICO = "diagnosticoList"; 
	private McoDiagnostico diagnostico = null;
	private CidVO cidVo;
	private boolean permManterDiagnostico;
	private boolean edit;
	private Integer cidSeq;
	private boolean indSituacao;
	private Integer seq;
	
	@Inject
	private IEmergenciaFacade emergenciaFacade;
	
	@EJB
	private IAghuFacade aghuFacade;
	
	@PostConstruct
	public void init() {
		begin(conversation);
		this.permManterDiagnostico = getPermissionService().usuarioTemPermissao(obterLoginUsuarioLogado(), "manterDiagnostico", "alterar");
	
		if(StringUtils.isNotBlank(getRequestParameter("seqDiagnostico"))){
			this.setSeq(Integer.valueOf(this.getRequestParameter("seqDiagnostico")));
		}
		
		if(StringUtils.isNotBlank(getRequestParameter("cidSeq"))){
			this.setCidSeq(Integer.valueOf(this.getRequestParameter("cidSeq")));
		}
	}
	
	
	public void inicio() {
		// atualiza permissao para alterar
		if (getSeq() == null) {
			diagnostico = new McoDiagnostico();
			diagnostico.setIndPlacar(true);
			diagnostico.setIndSituacao(DominioSituacao.A);
			setIndSituacao(true);
			setCidVo(null);
			setEdit(false);
			if(this.cidSeq != null){
				try {
					cidVo = this.emergenciaFacade.obterCidVO(this.cidSeq);
				} catch (ApplicationBusinessException e) {
					apresentarExcecaoNegocio(e);
				}
			}
		} else {
			diagnostico = emergenciaFacade.obterMcoDiagnostico(getSeq());
			setIndSituacao(diagnostico.getIndSituacao().isAtivo());
			setEdit(true);
			try {
				if(this.cidSeq != null){
					cidVo = this.emergenciaFacade.obterCidVO(this.cidSeq);
				} else if (this.diagnostico.getAghCid() != null) {
					cidVo = this.emergenciaFacade.obterCidVO(this.diagnostico.getAghCid().getSeq());
				}
			} catch (ApplicationBusinessException e) {
				apresentarExcecaoNegocio(e);
			}

		}
		
	}
	
	public String gravar() {
		try {
			diagnostico.setIndSituacao(getSituacao(isIndSituacao()));
			if (getCidVo() != null) {
				diagnostico.setAghCid(aghuFacade.obterAghCidsPorChavePrimaria(getCidVo().getSeq()));
			} else {
				diagnostico.setAghCid(null);
			}
			
			this.emergenciaFacade.persistirDiagnostico(getDiagnostico());
			if (!isEdit()) {
				apresentarMsgNegocio(Severity.INFO,
						"MENSAGEM_SUCESSO_INSERCAO_DIAGNOSTICO");
			} else {
				apresentarMsgNegocio(Severity.INFO,
						"MENSAGEM_SUCESSO_ALTERACAO_DIAGNOSTICO");
			}
			
			 limpar();
			
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}

		return PAGE_PESQUISA_DIAGNOSTICO;
	}

	
	public String cancelar(){
		limpar();
		return PAGE_PESQUISA_DIAGNOSTICO;
	}
	
	/**
	 * Returno o dominio da situaçao
	 * @param valor
	 * @return
	 */
	private DominioSituacao getSituacao(boolean valor) {
		if (valor) {
			return DominioSituacao.A;
		} else {
			return DominioSituacao.I;
		}
	}
	
	public List<CidVO> obterCids(String objPesquisa){
		try {
			return  this.returnSGWithCount(this.emergenciaFacade.obterCids(objPesquisa),obterCidsCount(objPesquisa));
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}	
		return null;
	}

	private  void limpar(){
		seq = null;
		diagnostico = null;
		cidSeq = null;
	}
	
	public Long obterCidsCount(String objPesquisa){
		try {
			return this.emergenciaFacade.obterCidsCount(objPesquisa);
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
		return null;
	}
	
	public String getTelaOrigem() {
		return TELA_ORIGEM;
	}
	
	public CidVO getCidVo() {
		return cidVo;
	}

	public void setCidVo(CidVO cidVo) {
		this.cidVo = cidVo;
	}

	
	private IPermissionService getPermissionService() {
		return ServiceLocator.getBeanRemote(IPermissionService.class, "aghu-casca");
	}

	public boolean isPermManterDiagnostico() {
		return permManterDiagnostico;
	}

	public void setPermManterDiagnostico(boolean permManterDiagnostico) {
		this.permManterDiagnostico = permManterDiagnostico;
	}

	public McoDiagnostico getDiagnostico() {
		return diagnostico;
	}

	public void setDiagnostico(McoDiagnostico diagnostico) {
		this.diagnostico = diagnostico;
	}

	public boolean isEdit() {
		return edit;
	}

	public void setEdit(boolean edit) {
		this.edit = edit;
	}

	public Integer getCidSeq() {
		return cidSeq;
	}

	public void setCidSeq(Integer cidSeq) {
		this.cidSeq = cidSeq;
	}

	public boolean isIndSituacao() {
		return indSituacao;
	}

	public void setIndSituacao(boolean indSituacao) {
		this.indSituacao = indSituacao;
	}


	public Integer getSeq() {
		return seq;
	}


	public void setSeq(Integer seq) {
		this.seq = seq;
	}
}
