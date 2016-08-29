package br.gov.mec.aghu.emergencia.action;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import br.gov.mec.aghu.configuracao.vo.Especialidade;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.emergencia.business.IEmergenciaFacade;
import br.gov.mec.aghu.emergencia.vo.AgrupamentoEspecialidadeEmergenciaVO;
import br.gov.mec.aghu.emergencia.vo.EspecialidadeEmergenciaVO;
import br.gov.mec.aghu.model.MamEmgAgrupaEsp;
import br.gov.mec.aghu.model.MamEmgAgrupaEspId;
import br.gov.mec.aghu.model.MamEmgEspecialidades;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;

/**
 * Controller das ações da pagina de criação e edição de especialidades da emergência.
 * 
 * @author luismoura
 * 
 */
public class EspecialidadeEmergenciaController extends ActionController {
	
	private static final long serialVersionUID = 5486509478357451284L;

	private final String PAGE_LIST_ESP_EMERG = "especialidadeEmergenciaList";

	@EJB
	private IEmergenciaFacade emergenciaFacade;
	
	@Inject
	private EspecialidadeEmergenciaPaginatorController especialidadeEmergenciaPaginatorController;

	private Especialidade especialidade;
	private Short seq;
	private EspecialidadeEmergenciaVO especialidadeEmergencia;
	private MamEmgEspecialidades especEmerg;
	private Boolean indSituacao;
	private DominioSituacao espDominio;

	private boolean create;

	private Boolean bloqueiaEspecialidade;

	private Boolean habilitaEspecialidadeEmergencia;
	
	private boolean pesquisaAtiva;

	private AgrupamentoEspecialidadeEmergenciaVO agrupamentoEspecialidadeEmergencia;
	private Especialidade especialidadeAssociada;
	private Boolean indSituacaoEspAssoc;
	private List<AgrupamentoEspecialidadeEmergenciaVO> dataModel = new ArrayList<AgrupamentoEspecialidadeEmergenciaVO>();
	private List<EspecialidadeEmergenciaVO> dataModelEsp = new ArrayList<EspecialidadeEmergenciaVO>();

	@PostConstruct
	public void init() {
		begin(conversation);
		setEspecialidade(null);
	}

	public void inicio() {
		
		if (this.pesquisaAtiva) {
			this.reiniciarPesquisa();
		}
		
		if (this.especialidadeEmergencia != null && this.especialidadeEmergencia.getEspecialidade() != null
				&& this.especialidadeEmergencia.getEspecialidade().getSeq() != null) {

			setEspecialidade(especialidadeEmergencia.getEspecialidade());

			setIndSituacao(this.especialidadeEmergencia.getEspecialidadeEmergencia().getIndSituacao() != null
					&& this.especialidadeEmergencia.getEspecialidadeEmergencia().getIndSituacao().equals(DominioSituacao.A));

			this.create = false;

			setHabilitaEspecialidadeEmergencia(Boolean.TRUE);
			setBloqueiaEspecialidade(Boolean.TRUE);
			setIndSituacaoEspAssoc(Boolean.TRUE);

			this.pesquisar();

		} else {
			this.especialidadeEmergencia = new EspecialidadeEmergenciaVO();
			this.especialidadeEmergencia.setEspecialidadeEmergencia(new MamEmgEspecialidades());
			setEspecialidade(null);
			setIndSituacao(Boolean.TRUE);
			setHabilitaEspecialidadeEmergencia(Boolean.FALSE);
			setBloqueiaEspecialidade(Boolean.FALSE);
			setIndSituacaoEspAssoc(Boolean.TRUE);
			this.dataModel.clear();

			this.create = true;
		}
		this.limparEmgAgrupaEsp();
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
	 * Ação do primeiro botão GRAVAR (especialidades de emergência) da pagina de cadastro de especialidades da emergência.
	 */
	public void confirmar() {
		try {

			especialidadeEmergencia.getEspecialidadeEmergencia().setIndSituacao(DominioSituacao.getInstance(this.indSituacao));
			especialidadeEmergencia.getEspecialidadeEmergencia().setEspSeq(this.especialidade.getSeq());
			especialidadeEmergencia.setEspecialidade(this.especialidade);
			this.especialidadeEmergenciaPaginatorController.setEspecialidade(especialidadeEmergencia.getEspecialidade());
			//try
			setEspecEmerg(especialidadeEmergencia.getEspecialidadeEmergencia());
			this.emergenciaFacade.persistirMamEmgEspecialidades(especEmerg, create);
			
			this.setHabilitaEspecialidadeEmergencia(Boolean.TRUE);
			this.setBloqueiaEspecialidade(Boolean.TRUE);
			this.setIndSituacaoEspAssoc(Boolean.TRUE);

			this.pesquisar();

			if (create) {
				apresentarMsgNegocio(Severity.INFO, "MSG_INSERCAO_ESPECIALIDADE_SUCESSO");
			} else {
				apresentarMsgNegocio(Severity.INFO, "MSG_ALTERACAO_ESPECIALIDADE_SUCESSO");
			}

			this.create = false;

		} catch (ApplicationBusinessException e) {
			create = true;
			apresentarExcecaoNegocio(e);
		}
	}

	/**
	 * Método de listagem de agrupamento de especialidade de emergência
	 */
	private void pesquisar() {
		Short seq = especialidade != null ? especialidade.getSeq() : null;
		try {
			this.dataModel = this.emergenciaFacade.pesquisarAgrupamentoEspecialidadeEmergenciaVO(seq);
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
	}

	/**
	 * Ação do segundo botão GRAVAR (agrupamento de especialidade de emergência) da pagina de cadastro de especialidades da emergência.
	 */
	public void gravarEmgAgrupaEsp() {
		try {
			MamEmgAgrupaEsp mamEmgAgrupaEsp = new MamEmgAgrupaEsp();
			mamEmgAgrupaEsp.setId(new MamEmgAgrupaEspId(especialidadeEmergencia.getEspecialidade().getSeq(), especialidadeAssociada.getSeq()));
			mamEmgAgrupaEsp.setMamEmgEspecialidades(especialidadeEmergencia.getEspecialidadeEmergencia());
			mamEmgAgrupaEsp.setIndSituacao(DominioSituacao.getInstance(this.getIndSituacaoEspAssoc()).toString());

			this.emergenciaFacade.inserirMamEmgAgrupaEsp(mamEmgAgrupaEsp);
			
			this.limparEmgAgrupaEsp();

			this.pesquisar();
			
			this.especialidadeEmergenciaPaginatorController.setEspecialidade(especialidadeEmergencia.getEspecialidade());

			apresentarMsgNegocio(Severity.INFO, "MSG_ASSOCIACAO_ESPECIALIDADE_SUCESSO");

		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}

	}

	/**
	 * Ação do segundo botão ATIVAR/INATIVAR da pagina de cadastro de especialidades da emergência.
	 */
	public void inativarCaracSitEmergencia() {
		try {
			if (agrupamentoEspecialidadeEmergencia.getAgrupamentoEspecialidadeEmergencia().getId() != null) {
				this.emergenciaFacade.ativarInativarMamEmgAgrupaEsp(agrupamentoEspecialidadeEmergencia.getAgrupamentoEspecialidadeEmergencia());
				this.pesquisar();
				apresentarMsgNegocio(Severity.INFO, "MSG_ALTERACAO_SITUACAO_ESPECIALIDADE_SUCESSO");
			}
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
	}
	
	/**
	 * Ação do botão EXCLUIR da pagina de listagem de especialidades da emergência.
	 */
	public void excluir() {
		try {
			if (especialidadeEmergencia != null && especialidadeEmergencia.getEspecialidadeEmergencia() != null) {
				this.emergenciaFacade.excluirMamEmgEspecialidades(especialidadeEmergencia.getEspecialidadeEmergencia().getSeq());
				this.limparEmgAgrupaEsp();
				this.pesquisar();
				this.especialidadeEmergenciaPaginatorController.reiniciarPesquisa();
				apresentarMsgNegocio(Severity.INFO, "MSG_DELETE_ESPECIALIDADE_SUCESSO");
			}
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);			
		}
		this.limparEmgAgrupaEsp();
	}
	
	public void confirmarExcluir(){
		
		this.dataModelEsp.clear();
		
	}
	
	/**
	 * Ação de Reiniciar Pesquisa
	 */
	
	public void reiniciarPesquisa() {
		
		if(getEspecialidade() != null){
				seq = (especialidade.getSeq());
		}else{
			seq = getSeq();
		}
		if(indSituacao == null || indSituacao == false){
			setEspDominio(DominioSituacao.I);
		}else{
			setEspDominio(DominioSituacao.A);
		}
			
		try {
			this.dataModelEsp = this.emergenciaFacade.pesquisarEspecialidadeEmergenciaVO(seq, espDominio);
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
	}
	
	/**
	 * Ação do segundo botão EXCLUIR da pagina de cadastro de especialidades da emergência.
	 */
	public void excluirCaracSitEmergencia() {
		try {
			if (agrupamentoEspecialidadeEmergencia.getAgrupamentoEspecialidadeEmergencia().getId() != null) {
				this.emergenciaFacade.excluirMamEmgAgrupaEsp(agrupamentoEspecialidadeEmergencia.getAgrupamentoEspecialidadeEmergencia());
				this.reiniciarPesquisa();
				this.pesquisar();
				apresentarMsgNegocio(Severity.INFO, "MSG_DELETE_ASSOCIACAO_SUCESSO");
			}
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
	}

	private void limparEmgAgrupaEsp() {
		this.setAgrupamentoEspecialidadeEmergencia(null);
		this.setEspecialidadeAssociada(null);
		this.setIndSituacaoEspAssoc(Boolean.TRUE);

	}
	
	public void pesquisarBySuggestion(){
		   this.pesquisar();	
		   this.reiniciarPesquisa();
	   }

	/**
	 * Ação do segundo botão VOLTAR da pagina de cadastro de especialidades da emergência.
	 */
	public String cancelar() {
		this.especialidadeEmergencia = new EspecialidadeEmergenciaVO();
		setIndSituacao(Boolean.TRUE);
		return PAGE_LIST_ESP_EMERG;
	}

	public Boolean getBolIndSituacao(String indSituacao) {
		return indSituacao != null && indSituacao.equals(DominioSituacao.A.toString());
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


	// // ### GETs e SETs ###
	public EspecialidadeEmergenciaVO getEspecialidadeEmergencia() {
		return especialidadeEmergencia;
	}

	public void setEspecialidadeEmergencia(EspecialidadeEmergenciaVO especialidadeEmergencia) {
		this.especialidadeEmergencia = especialidadeEmergencia;
	}

	public Especialidade getEspecialidade() {
		return especialidade;
	}

	public void setEspecialidade(Especialidade especialidade) {
		this.especialidade = especialidade;
	}

	public Boolean getIndSituacao() {
		return indSituacao;
	}

	public void setIndSituacao(Boolean indSituacao) {
		this.indSituacao = indSituacao;
	}

	public List<AgrupamentoEspecialidadeEmergenciaVO> getDataModel() {
		return dataModel;
	}

	public void setDataModel(List<AgrupamentoEspecialidadeEmergenciaVO> dataModel) {
		this.dataModel = dataModel;
	}

	public AgrupamentoEspecialidadeEmergenciaVO getAgrupamentoEspecialidadeEmergencia() {
		return agrupamentoEspecialidadeEmergencia;
	}

	public void setAgrupamentoEspecialidadeEmergencia(AgrupamentoEspecialidadeEmergenciaVO agrupamentoEspecialidadeEmergencia) {
		this.agrupamentoEspecialidadeEmergencia = agrupamentoEspecialidadeEmergencia;
	}

	public Boolean getIndSituacaoEspAssoc() {
		return indSituacaoEspAssoc;
	}

	public void setIndSituacaoEspAssoc(Boolean indSituacaoEspAssoc) {
		this.indSituacaoEspAssoc = indSituacaoEspAssoc;
	}

	public Boolean getHabilitaEspecialidadeEmergencia() {
		return habilitaEspecialidadeEmergencia;
	}

	public void setHabilitaEspecialidadeEmergencia(Boolean habilitaEspecialidadeEmergencia) {
		this.habilitaEspecialidadeEmergencia = habilitaEspecialidadeEmergencia;
	}

	public Boolean getBloqueiaEspecialidade() {
		return bloqueiaEspecialidade;
	}

	public void setBloqueiaEspecialidade(Boolean bloqueiaEspecialidade) {
		this.bloqueiaEspecialidade = bloqueiaEspecialidade;
	}

	public Especialidade getEspecialidadeAssociada() {
		return especialidadeAssociada;
	}

	public void setEspecialidadeAssociada(Especialidade especialidadeAssociada) {
		this.especialidadeAssociada = especialidadeAssociada;
	}

	public boolean isCreate() {
		return create;
	}

	public void setCreate(boolean create) {
		this.create = create;
	}

	public Short getSeq() {
		return seq;
	}

	public void setSeq(Short seq) {
		this.seq = seq;
	}

	public MamEmgEspecialidades getEspecEmerg() {
		return especEmerg;
	}

	public void setEspecEmerg(MamEmgEspecialidades especEmerg) {
		this.especEmerg = especEmerg;
	}

	public List<EspecialidadeEmergenciaVO> getDataModelEsp() {
		return dataModelEsp;
	}

	public void setDataModelEsp(List<EspecialidadeEmergenciaVO> dataModelEsp) {
		this.dataModelEsp = dataModelEsp;
	}

	public DominioSituacao getEspDominio() {
		return espDominio;
	}

	public void setEspDominio(DominioSituacao espDominio) {
		this.espDominio = espDominio;
	}

	public boolean isPesquisaAtiva() {
		return pesquisaAtiva;
	}

	public void setPesquisaAtiva(boolean pesquisaAtiva) {
		this.pesquisaAtiva = pesquisaAtiva;
	}
	
	
	
	
	
	
}
