package br.gov.mec.aghu.emergencia.action;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import br.gov.mec.aghu.configuracao.vo.Especialidade;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.emergencia.business.IEmergenciaFacade;
import br.gov.mec.aghu.emergencia.vo.EspecialidadeEmergenciaVO;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;

/**
 * Controller das ações da pagina de listagem de especialidades da emergência.
 * 
 * @author luismoura
 * 
 */
public class EspecialidadeEmergenciaPaginatorController extends ActionController {
	private static final long serialVersionUID = 6589546988357451478L;

	private final String PAGE_CAD_ESP_EMERG = "especialidadeEmergenciaCRUD";

	@EJB
	private IEmergenciaFacade emergenciaFacade;
	
	@Inject
	private EspecialidadeEmergenciaController especialidadeEmergenciaController;

	private Especialidade especialidade;
	private EspecialidadeEmergenciaVO especialidadeEmergencia;
	private DominioSituacao indSituacao;

	private List<EspecialidadeEmergenciaVO> dataModel = new ArrayList<EspecialidadeEmergenciaVO>();
	private boolean pesquisaAtiva;

	@PostConstruct
	public void init() {
		begin(conversation, true);
		setEspecialidade(null);
		especialidadeEmergencia = null;
		setIndSituacao(DominioSituacao.A);
	}

	public void inicio() {
		if (this.pesquisaAtiva) {
			this.reiniciarPesquisa();
		}
	}
	
	public void confirmarExcluir(){
		
		this.dataModel.clear();
		limparPesquisa(); 
		
	}
	

	/**
	 * Usado para popular o selectBox de especialidade
	 * 
	 * @param objPesquisa
	 * @return
	 */
	public List<Especialidade> pesquisarEspecialidade(String objPesquisa) {
		try {
			return  this.returnSGWithCount(this.emergenciaFacade.pesquisarEspecialidadesAtivasPorSeqNomeOuSigla((String) objPesquisa, 100),pesquisarEspecialidadeCount(objPesquisa));
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
		return null;
	}
	
	/**
	 * Ação do botão EXCLUIR da pagina de listagem de especialidades da emergência.
	 */
	public void excluir() {
		try {
			if (especialidadeEmergencia != null && especialidadeEmergencia.getEspecialidadeEmergencia() != null) {
				this.emergenciaFacade.excluirMamEmgEspecialidades(especialidadeEmergencia.getEspecialidadeEmergencia().getSeq());
				this.pesquisar();
				apresentarMsgNegocio(Severity.INFO, "MSG_DELETE_ESPECIALIDADE_SUCESSO");
			}
			this.reiniciarPesquisa();
			
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
			apresentarMsgNegocio(Severity.INFO, "ERRO_DELETE_MSG_MAM_EMG_AGRUPA_ESPS");
		}

	}
	
	/**
	 * Usado para popular o selectBox de especialidade
	 * 
	 * @param objPesquisa
	 * @return
	 */
	public Long pesquisarEspecialidadeCount(String objPesquisa) {
		try {
			return this.emergenciaFacade.pesquisarEspecialidadesAtivasPorSeqNomeOuSiglaCount((String) objPesquisa);
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
		return null;
	}

	/**
	 * Ação do botão PESQUISAR da pagina de listagem de especialidades da emergência.
	 */
	public void pesquisar() {
		this.reiniciarPesquisa();
		this.pesquisaAtiva = true;
	}
	
	public void pesquisarBySuggestion(){
	   this.pesquisar();	
	   this.reiniciarPesquisa();
   }

	public void reiniciarPesquisa() {
		Short seq = especialidade != null ? especialidade.getSeq() : null;
		try {
			this.dataModel = this.emergenciaFacade.pesquisarEspecialidadeEmergenciaVO(seq, indSituacao);
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
	}

	/**
	 * Ação do botão LIMPAR da pagina de listagem de especialidades da emergência.
	 */
	public void limparPesquisa() {
		this.especialidade = null;
		this.especialidadeEmergencia = null;
		this.indSituacao = null;
		this.pesquisaAtiva = false;
		this.dataModel.clear();
	}


	/**
	 * Ação do botão NOVO da pagina de listagem de especialidades da emergência.
	 * 
	 * @return
	 */
	public String novo() {
		this.especialidadeEmergencia = null;
		this.especialidade = null;
		especialidadeEmergenciaController.setCreate(true);
		return PAGE_CAD_ESP_EMERG;
	}

	/**
	 * Ação do botão EDITAR da pagina de listagem de especialidades da emergência.
	 * 
	 * @return
	 */
	public String editar() {
		especialidadeEmergenciaController.setCreate(false);
		return PAGE_CAD_ESP_EMERG;
	}

	public Boolean getBolIndUsoTriagem(String indUsoTriagem) {
		return DominioSimNao.S.toString().equals(indUsoTriagem);
	}

	// ### GETs e SETs ###

	public List<EspecialidadeEmergenciaVO> getDataModel() {
		return dataModel;
	}

	public void setDataModel(List<EspecialidadeEmergenciaVO> dataModel) {
		this.dataModel = dataModel;
	}

	public Especialidade getEspecialidade() {
		return especialidade;
	}

	public void setEspecialidade(Especialidade especialidade) {
		this.especialidade = especialidade;
	}

	public EspecialidadeEmergenciaVO getEspecialidadeEmergencia() {
		return especialidadeEmergencia;
	}

	public void setEspecialidadeEmergencia(EspecialidadeEmergenciaVO especialidadeEmergencia) {
		this.especialidadeEmergencia = especialidadeEmergencia;
	}

	public DominioSituacao getIndSituacao() {
		return indSituacao;
	}

	public void setIndSituacao(DominioSituacao indSituacao) {
		this.indSituacao = indSituacao;
	}

	public boolean isPesquisaAtiva() {
		return pesquisaAtiva;
	}

	public void setPesquisaAtiva(boolean pesquisaAtiva) {
		this.pesquisaAtiva = pesquisaAtiva;
	}
}