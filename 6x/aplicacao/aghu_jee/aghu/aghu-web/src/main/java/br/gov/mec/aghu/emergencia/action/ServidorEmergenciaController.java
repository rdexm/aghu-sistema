package br.gov.mec.aghu.emergencia.action;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import br.gov.mec.aghu.configuracao.vo.Especialidade;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.emergencia.business.IEmergenciaFacade;
import br.gov.mec.aghu.emergencia.vo.ServidorEmergenciaVO;
import br.gov.mec.aghu.emergencia.vo.ServidorEspecialidadeEmergenciaVO;
import br.gov.mec.aghu.model.MamEmgServEspCoop;
import br.gov.mec.aghu.model.MamEmgServEspecialidade;
import br.gov.mec.aghu.model.MamEmgServEspecialidadeId;
import br.gov.mec.aghu.model.MamEmgServidor;
import br.gov.mec.aghu.model.MamTipoCooperacao;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.registrocolaborador.vo.Servidor;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;

/**
 * Controller das ações da pagina de criação e edição de servidores da emergência.
 * 
 * @author luismoura
 * 
 */
public class ServidorEmergenciaController extends ActionController {
	private static final long serialVersionUID = 5486509478357451284L;

	private final String PAGE_LIST_ESP_EMERG = "servidorEmergenciaList";

	@EJB
	private IEmergenciaFacade emergenciaFacade;
	
	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;

	private Servidor servidor;
	private ServidorEmergenciaVO servidorEmergencia;
	private Boolean indSituacao;

	private Boolean bloqueiaServidor;

	private Boolean habilitaServidorEmergencia;
	private ServidorEspecialidadeEmergenciaVO servidorEspecialidadeEmergencia;
	private Especialidade especialidade;
	private Boolean indSituacaoEspecialidade;
	private List<ServidorEspecialidadeEmergenciaVO> dataModel = new ArrayList<ServidorEspecialidadeEmergenciaVO>();

	private Boolean habilitaEmergenciaCooperacoes;
	private MamEmgServEspecialidadeId emgServSelecionado;

	private MamEmgServEspCoop mamEmgServEspCoop;
	private List<MamEmgServEspCoop> dataModelCooperacao = new ArrayList<MamEmgServEspCoop>();
	private MamTipoCooperacao mamTipoCooperacao;

	@PostConstruct
	public void init() {
		begin(conversation);
		limparEmgServEspCoop();
	}

	public void inicio() {
		
		this.setIndSituacaoEspecialidade(Boolean.TRUE);
		if (this.servidorEmergencia != null && this.servidorEmergencia.getServidor() != null && this.servidorEmergencia.getServidor() != null) {
			this.setServidor(servidorEmergencia.getServidor());
			this.setIndSituacao(DominioSituacao.A.toString().equals(this.servidorEmergencia.getServidorEmergencia().getIndSituacao()));
			this.setHabilitaServidorEmergencia(Boolean.TRUE);
			this.setBloqueiaServidor(Boolean.TRUE);
			this.pesquisar();
		} else {
			this.servidorEmergencia = new ServidorEmergenciaVO();
			this.servidorEmergencia.setServidorEmergencia(new MamEmgServidor());
			this.setServidor(null);
			this.setIndSituacao(Boolean.TRUE);
			this.setHabilitaServidorEmergencia(Boolean.FALSE);
			this.setBloqueiaServidor(Boolean.FALSE);
		}
		this.limparEmgServEsp();
	}

	/**
	 * Usado para popular o selectBox de servidor
	 * 
	 * @param objPesquisa
	 * @return
	 */
	public List<Servidor> pesquisarServidor(String objPesquisa) {
		try {
			return  this.returnSGWithCount(this.emergenciaFacade.pesquisarServidoresAtivosPorNomeOuVinculoMatricula((String) objPesquisa),pesquisarServidorCount(objPesquisa));
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
		return null;
	}

	/**
	 * Usado para popular o selectBox de servidor
	 * 
	 * @param objPesquisa
	 * @return
	 */
	public Long pesquisarServidorCount(String objPesquisa) {
		try {
			return this.emergenciaFacade.pesquisarServidoresAtivosPorNomeOuVinculoMatriculaCount((String) objPesquisa);
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
	public List<Especialidade> pesquisarEspecialidade(String objPesquisa) {
		try {
			return  this.returnSGWithCount(this.emergenciaFacade.pesquisarEspecialidadesEmergenciaAtivasPorSeqNomeOuSigla((String) objPesquisa),pesquisarEspecialidadeCount(objPesquisa));
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
			return this.emergenciaFacade.pesquisarEspecialidadesEmergenciaAtivasPorSeqNomeOuSiglaCount((String) objPesquisa);
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
		return null;
	}

	/**
	 * Ação do primeiro botão GRAVAR (servidores de emergência) da pagina de cadastro de servidores da emergência.
	 */
	public void confirmar() {
		limparEmgServEspCoop();
		try {

			boolean create = this.servidorEmergencia.getServidorEmergencia().getSeq() == null;

			servidorEmergencia.getServidorEmergencia().setIndSituacao(DominioSituacao.getInstance(this.indSituacao).toString());
			servidorEmergencia.getServidorEmergencia()
					.setRapServidoresByMamEseSerFk1(
							registroColaboradorFacade
									.obterRapServidorPorVinculoMatricula(
											this.servidor.getMatricula(),
											this.servidor.getVinculo()));
			servidorEmergencia.setServidor(this.servidor);

			this.emergenciaFacade.persistirMamEmgServidor(servidorEmergencia.getServidorEmergencia());

			this.setHabilitaServidorEmergencia(Boolean.TRUE);
			this.setBloqueiaServidor(Boolean.TRUE);
			this.pesquisar();

			if (create) {
				apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_CADASTRO_SERVIDOR_EMERGENCIA");
			} else {
				apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_ALTERACAO_SERVIDOR_EMERGENCIA");
			}

		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
	}

	/**
	 * Método de listagem de agrupamento de servidor de emergência
	 */
	public void pesquisar() {
		Integer seq = servidorEmergencia != null && servidorEmergencia.getServidorEmergencia() != null ? servidorEmergencia.getServidorEmergencia().getSeq()
				: null;
		try {
			this.dataModel = this.emergenciaFacade.pesquisarServidorEspecialidadeEmergenciaVO(seq);
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
	}

	/**
	 * Ação do segundo botão GRAVAR da pagina de cadastro de servidores da emergência.
	 */
	public void gravarEmgServidorEspecialidade() {
		limparEmgServEspCoop();
		
		try {
			MamEmgServEspecialidade mamEmgServEspecialidade = new MamEmgServEspecialidade();
			mamEmgServEspecialidade.setId(new MamEmgServEspecialidadeId(servidorEmergencia.getServidorEmergencia().getSeq(), especialidade.getSeq()));
			mamEmgServEspecialidade.setMamEmgServidor(servidorEmergencia.getServidorEmergencia());
			mamEmgServEspecialidade.setIndSituacao(DominioSituacao.getInstance(this.getIndSituacaoEspecialidade()).toString());

			this.emergenciaFacade.inserirMamEmgServEspecialidade(mamEmgServEspecialidade);

			this.inicio();

			apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_CADASTRO_ESPECIALIDADE_SERVIDOR");

		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}

	}

	/**
	 * Ação do segundo botão ATIVAR/INATIVAR da pagina de cadastro de servidores da emergência.
	 */
	public void inativarEmgServidorEspecialidade() {
		limparEmgServEspCoop();
		try {
			if (servidorEspecialidadeEmergencia.getMamEmgServEspecialidade() != null) {
				this.emergenciaFacade.ativarInativarMamEmgServEspecialidade(servidorEspecialidadeEmergencia.getMamEmgServEspecialidade());
				this.inicio();
				apresentarMsgNegocio(Severity.INFO, "MENSAGEM_ALTERACAO_SITUACAO_ESPECIALIDADE_SERVIDOR");
			}
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
	}

	/**
	 * Ação do segundo botão EXCLUIR da pagina de cadastro de servidores da emergência.
	 */
	public void excluirEmgServidorEspecialidade() {
		limparEmgServEspCoop();
		try {
			if (servidorEspecialidadeEmergencia.getMamEmgServEspecialidade() != null) {
				this.emergenciaFacade.excluirMamEmgServEspecialidade(servidorEspecialidadeEmergencia.getMamEmgServEspecialidade());
				this.inicio();
				apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_EXCLUSAO_ESPECIALIDADE_SERVIDOR");
			}
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
	}

	private void limparEmgServEsp() {
		this.setEspecialidade(null);
		this.setIndSituacaoEspecialidade(Boolean.TRUE);
	}

	private void limparEmgServEspCoop() {
		this.setHabilitaEmergenciaCooperacoes(Boolean.FALSE);
		this.emgServSelecionado = null;
	}

	/**
	 * Ação do segundo botão COOPERAÇÕES da pagina de cadastro de servidores da emergência.
	 */
	public void cooperacoesServidorEspecialidade() {
		if (servidorEspecialidadeEmergencia.getMamEmgServEspecialidade() != null) {
			if (this.servidorEspecialidadeEmergencia.getMamEmgServEspecialidade().getId().equals(this.emgServSelecionado)) {
				limparEmgServEspCoop();
			} else {
				this.emgServSelecionado = this.servidorEspecialidadeEmergencia.getMamEmgServEspecialidade().getId();
				this.pesquisarMamEmgServEspCoop();
				this.setHabilitaEmergenciaCooperacoes(Boolean.TRUE);
			}
		}
	}

	public Boolean isSelecionado(MamEmgServEspecialidadeId idItem) {
		return idItem != null && idItem.equals(emgServSelecionado);
	}

	/**
	 * Ação do segundo botão VOLTAR da pagina de cadastro de servidores da emergência.
	 */
	public String cancelar() {
		this.servidorEmergencia = new ServidorEmergenciaVO();
		setIndSituacao(Boolean.TRUE);
		this.limparEmgServEsp();
		this.limparEmgServEspCoop();
		return PAGE_LIST_ESP_EMERG;
	}

	public Boolean getBolIndSituacao(String indSituacao) {
		return indSituacao != null && indSituacao.equals(DominioSituacao.A.toString());
	}

	/**
	 * Método de listagem de servidor de emergência coop
	 */
	public void pesquisarMamEmgServEspCoop() {
		this.dataModelCooperacao = this.emergenciaFacade.pesquisarMamEmgServEspCoopPorMamEmgServEspecialidade(servidorEspecialidadeEmergencia
				.getMamEmgServEspecialidade().getId().getEseSeq(), servidorEspecialidadeEmergencia.getMamEmgServEspecialidade().getId().getEepEspSeq());
	}

	public List<MamTipoCooperacao> obterTiposCooperacao() {
		return this.emergenciaFacade.pesquisarMamTipoCooperacaoAtivos(null);
	}

	/**
	 * Ação do terceiro botão GRAVAR da pagina de cadastro de servidores da emergência.
	 */
	public void gravarEmgEspecialidadeCooperacao() {
		try {
			MamEmgServEspCoop mamEmgServEspCoop = new MamEmgServEspCoop();
			mamEmgServEspCoop.setMamEmgServEspecialidade(servidorEspecialidadeEmergencia.getMamEmgServEspecialidade());
			mamEmgServEspCoop.setMamTipoCooperacao(mamTipoCooperacao);
			
			this.emergenciaFacade.inserirMamEmgServEspCoop(mamEmgServEspCoop);
			this.setMamTipoCooperacao(null);
			this.pesquisarMamEmgServEspCoop();
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
	}

	/**
	 * Ação do terceiro botão EXCLUIR da pagina de cadastro de servidores da emergência.
	 */
	public void excluirEmgServidorEspecialidadeCooperacao() {
		try {
			if (servidorEspecialidadeEmergencia.getMamEmgServEspecialidade() != null) {
				this.emergenciaFacade.excluirMamEmgServEspCoop(mamEmgServEspCoop);
				// this.setPesquisaEmergenciaCooperacoes(Boolean.TRUE);
				this.pesquisarMamEmgServEspCoop();
				apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_EXCLUSAO_COOPERACAO_SERVIDOR");
			}
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
	}

	// ### GETs e SETs ###
	public ServidorEmergenciaVO getServidorEmergencia() {
		return servidorEmergencia;
	}

	public void setServidorEmergencia(ServidorEmergenciaVO servidorEmergencia) {
		this.servidorEmergencia = servidorEmergencia;
	}

	public Servidor getServidor() {
		return servidor;
	}

	public void setServidor(Servidor servidor) {
		this.servidor = servidor;
	}

	public Boolean getIndSituacao() {
		return indSituacao;
	}

	public void setIndSituacao(Boolean indSituacao) {
		this.indSituacao = indSituacao;
	}

	public List<ServidorEspecialidadeEmergenciaVO> getDataModel() {
		return dataModel;
	}

	public void setDataModel(List<ServidorEspecialidadeEmergenciaVO> dataModel) {
		this.dataModel = dataModel;
	}

	public ServidorEspecialidadeEmergenciaVO getServidorEspecialidadeEmergencia() {
		return servidorEspecialidadeEmergencia;
	}

	public void setServidorEspecialidadeEmergencia(ServidorEspecialidadeEmergenciaVO servidorEspecialidadeEmergencia) {
		this.servidorEspecialidadeEmergencia = servidorEspecialidadeEmergencia;
	}

	public Boolean getIndSituacaoEspecialidade() {
		return indSituacaoEspecialidade;
	}

	public void setIndSituacaoEspecialidade(Boolean indSituacaoEspecialidade) {
		this.indSituacaoEspecialidade = indSituacaoEspecialidade;
	}

	public Boolean getHabilitaServidorEmergencia() {
		return habilitaServidorEmergencia;
	}

	public void setHabilitaServidorEmergencia(Boolean habilitaServidorEmergencia) {
		this.habilitaServidorEmergencia = habilitaServidorEmergencia;
	}

	public Boolean getBloqueiaServidor() {
		return bloqueiaServidor;
	}

	public void setBloqueiaServidor(Boolean bloqueiaServidor) {
		this.bloqueiaServidor = bloqueiaServidor;
	}

	public Especialidade getEspecialidade() {
		return especialidade;
	}

	public void setEspecialidade(Especialidade especialidade) {
		this.especialidade = especialidade;
	}

	public Boolean getHabilitaEmergenciaCooperacoes() {
		return habilitaEmergenciaCooperacoes;
	}

	public void setHabilitaEmergenciaCooperacoes(Boolean habilitaEmergenciaCooperacoes) {
		this.habilitaEmergenciaCooperacoes = habilitaEmergenciaCooperacoes;
	}

	public MamEmgServEspecialidadeId getEmgServSelecionado() {
		return emgServSelecionado;
	}

	public void setEmgServSelecionado(MamEmgServEspecialidadeId emgServSelecionado) {
		this.emgServSelecionado = emgServSelecionado;
	}

	public MamEmgServEspCoop getMamEmgServEspCoop() {
		return mamEmgServEspCoop;
	}

	public void setMamEmgServEspCoop(MamEmgServEspCoop mamEmgServEspCoop) {
		this.mamEmgServEspCoop = mamEmgServEspCoop;
	}

	public List<MamEmgServEspCoop> getDataModelCooperacao() {
		return dataModelCooperacao;
	}

	public void setDataModelCooperacao(List<MamEmgServEspCoop> dataModelCooperacao) {
		this.dataModelCooperacao = dataModelCooperacao;
	}

	public MamTipoCooperacao getMamTipoCooperacao() {
		return mamTipoCooperacao;
	}

	public void setMamTipoCooperacao(MamTipoCooperacao mamTipoCooperacao) {
		this.mamTipoCooperacao = mamTipoCooperacao;
	}
}
