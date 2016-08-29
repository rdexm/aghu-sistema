package br.gov.mec.aghu.internacao.leitos.action;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.dominio.DominioSituacaoSolicitacaoInternacao;
import br.gov.mec.aghu.internacao.cadastrosbasicos.business.ICadastrosBasicosInternacaoFacade;
import br.gov.mec.aghu.internacao.leitos.business.ILeitosInternacaoFacade;
import br.gov.mec.aghu.internacao.vo.RapServidoresTransPacienteVO;
import br.gov.mec.aghu.model.AghEspecialidades;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.AinAcomodacoes;
import br.gov.mec.aghu.model.AinSolicTransfPacientes;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.RapServidoresId;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;

public class SolicitarTransferenciaPacienteController extends ActionController {

	private static final long serialVersionUID = 2992530688980388540L;

	private static final Log LOG = LogFactory.getLog(SolicitarTransferenciaPacienteController.class);

	private static final String SOLICITA_TRANSFERENCIA_PACIENTE_LIST = "solicitaTransferenciaPacienteList";

	private static final String PACIENTE = "paciente";
	private static final String CONVENIO = "convenio";
	private static final String ESPECIALIDADE = "especialidade";

	@EJB
	private ILeitosInternacaoFacade leitosInternacaoFacade;

	@EJB
	private IAghuFacade aghuFacade;

	@EJB
	private ICadastrosBasicosInternacaoFacade cadastrosBasicosInternacaoFacade;

	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;

	@EJB
	private IParametroFacade parametroFacade;

	private AghEspecialidades especialidade;

	private RapServidoresTransPacienteVO professorAux;

	/**
	 * LOV solicitante
	 */
	private RapServidores rapServidores = null;

	/**
	 * Lista de solicitantes
	 */
	private List<RapServidores> solicitantes = new ArrayList<RapServidores>();

	/**
	 * Nome Responsável
	 */
	private String nomeResponsavel = "";

	/**
	 * Codigo VRapPessoaServidor
	 */
	private Integer codigoResponsavel;

	/**
	 * Matricula VRapPessoaServidor
	 */
	private Integer matriculaResponsavel;

	/**
	 * Matricula ou Nome do Responsavel
	 */
	private Object responsavel;

	private AinAcomodacoes acomodacao;

	private AghUnidadesFuncionais unidade;

	private AinSolicTransfPacientes solicitacaoTransfPac;

	private Integer internacaoSeq;

	private String cameFrom = "";

	private Integer solicitacaoSeq;

	private Boolean novo = false;

	private boolean desabilitarAcomodacao = false;

	private Integer prontuario;
	private String leitoID;

	public void inicio() {
	 

		HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(true);
		if (this.solicitacaoSeq != null) {
			this.solicitacaoTransfPac = this.leitosInternacaoFacade.obterSolicTransfPacientePorId(this.solicitacaoSeq);
			this.novo = false;
		} else if (this.internacaoSeq != null) {
			this.solicitacaoTransfPac = new AinSolicTransfPacientes();
			this.solicitacaoTransfPac.setInternacao(this.leitosInternacaoFacade.obterInternacaoPorId(this.internacaoSeq));
			this.solicitacaoTransfPac.setIndSitSolicLeito(DominioSituacaoSolicitacaoInternacao.P);
			this.especialidade = null;
			this.rapServidores = null;
			this.codigoResponsavel = null;
			this.matriculaResponsavel = null;
			this.acomodacao = null;
			this.professorAux = null;
			this.unidade = this.leitosInternacaoFacade.unidadeFuncionalInternacao(this.solicitacaoTransfPac.getInternacao());
			session.setAttribute(PACIENTE, this.solicitacaoTransfPac.getInternacao().getPaciente());
			this.novo = true;
		}
		this.leitoIsolamento();
		session.setAttribute(CONVENIO, this.solicitacaoTransfPac.getInternacao().getConvenioSaude());
		session.setAttribute(ESPECIALIDADE, this.solicitacaoTransfPac.getEspecialidades());
	
	}

	public void selecionouEspecialidade() {
		HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(true);
		session.setAttribute(ESPECIALIDADE, this.especialidade);
		this.professorAux = null;
	}

	/**
	 * Método que realiza a ação do botão gravar na tela de cadastro de
	 * Solicitação de Transferência de pacientes
	 */
	public String gravar() {
		// Força a atualizar rapServidores de acordo com os valores digitados na
		// tela para codigoResponsavel e matriculaResponsavel
		buscarSolicitante();

		if (this.rapServidores != null && (this.rapServidores.getId().getVinCodigo() == null || this.rapServidores.getId().getMatricula() == null)) {
			apresentarMsgNegocio(Severity.ERROR, "MENSAGEM_RESPONSAVEL_INVALIDO");
			return "erro";
		}

		this.solicitacaoTransfPac.setAcomodacoes(this.acomodacao);
		this.solicitacaoTransfPac.setEspecialidades(this.especialidade);

		if (this.professorAux != null) {
			final RapServidoresId id = new RapServidoresId();
			id.setMatricula(this.professorAux.getMatricula());
			id.setVinCodigo(this.professorAux.getVinCodigo());
			this.solicitacaoTransfPac.setServidorProfessor(this.registroColaboradorFacade.obterServidor(id));
		}

		this.solicitacaoTransfPac.setServidorSolicitante(this.rapServidores);
		this.solicitacaoTransfPac.setUnfSolicitante(this.unidade);

		try {
			this.leitosInternacaoFacade.persistirAinSolicTransfPacientes(this.solicitacaoTransfPac);
			apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_INCLUSAO_SOLIC_TRANSF_PAC");
		} catch (final BaseException e) {
			e.getMessage();
			apresentarExcecaoNegocio(e);
			return null;
		}

		return SOLICITA_TRANSFERENCIA_PACIENTE_LIST;
	}

	public String cancelar() {
		LOG.info("Cancelado");
		return SOLICITA_TRANSFERENCIA_PACIENTE_LIST;
	}

	public void leitoIsolamento() {
		try {
			if (this.solicitacaoTransfPac.isIsolamento()) {
				final AghParametros parametro = this.parametroFacade.buscarAghParametro(AghuParametrosEnum.P_COD_ACOM_LTO_ISOLAMENTO);
				if (parametro != null) {
					this.acomodacao = this.cadastrosBasicosInternacaoFacade.obterAcomodacao(parametro.getVlrNumerico().intValue());
					this.desabilitarAcomodacao = true;
				}
			} else {
				this.desabilitarAcomodacao = false;
			}
		} catch (final ApplicationBusinessException e) {
			e.getMessage();
			apresentarExcecaoNegocio(e);
		}
	}

	public List<RapServidoresTransPacienteVO> pesquisarProfessor(final String strPesquisa) {
		try {
			return this.leitosInternacaoFacade.pesquisarServidoresPorNomeOuCRM((String) strPesquisa, this.especialidade, this.solicitacaoTransfPac
					.getInternacao().getConvenioSaude());
		} catch (final ApplicationBusinessException e) {
			e.getMessage();
			apresentarExcecaoNegocio(e);
			return null;
		}
	}

	public void limparSolicitante() {
		this.codigoResponsavel = null;
		this.matriculaResponsavel = null;
		// Limpa solicitante
		this.rapServidores = null;
	}

	/**
	 * Busca Status pela pk.
	 */
	public void buscarSolicitante() {
		if (this.rapServidores != null && this.rapServidores.getId() == null) {
			this.rapServidores = null;
		}
	}

	/**
	 * Pesquisa Status para lista de valores.
	 */
	public List<RapServidores> pesquisarSolicitantes(String param) {
		this.solicitantes = this.leitosInternacaoFacade.pesquisarSolicitantes(param);
		return this.solicitantes;
	}

	public List<AghEspecialidades> pesquisarEspecialidades(final String strPesquisa) {
		return this.aghuFacade.listarEspecialidadeTransPaciente(strPesquisa, this.solicitacaoTransfPac.getInternacao().getPaciente().getIdade());
	}

	public List<AinAcomodacoes> pesquisarAcomodacoes(final String strPesquisa) {
		return this.cadastrosBasicosInternacaoFacade.pesquisarAcomodacoesPorCodigoOuDescricaoOrdenado(strPesquisa);
	}

	public List<AghUnidadesFuncionais> pesquisarUnidades(final String strPesquisa) {
		return this.leitosInternacaoFacade.pesquisarUnidadeFuncional(strPesquisa);
	}

	public void limparProfessor() {
		this.professorAux = null;
	}

	public AinSolicTransfPacientes getSolicitacaoTransfPac() {
		return this.solicitacaoTransfPac;
	}

	public void setSolicitacaoTransfPac(final AinSolicTransfPacientes solicitacaoTransfPac) {
		this.solicitacaoTransfPac = solicitacaoTransfPac;
	}

	public AghEspecialidades getEspecialidade() {
		return this.especialidade;
	}

	public void setEspecialidade(final AghEspecialidades especialidade) {
		this.especialidade = especialidade;
	}

	public Integer getInternacaoSeq() {
		return this.internacaoSeq;
	}

	public void setInternacaoSeq(final Integer internacaoSeq) {
		this.internacaoSeq = internacaoSeq;
	}

	public AinAcomodacoes getAcomodacao() {
		return this.acomodacao;
	}

	public void setAcomodacao(final AinAcomodacoes acomodacao) {
		this.acomodacao = acomodacao;
	}

	public AghUnidadesFuncionais getUnidade() {
		return this.unidade;
	}

	public void setUnidade(final AghUnidadesFuncionais unidade) {
		this.unidade = unidade;
	}

	public boolean isDesabilitarAcomodacao() {
		return this.desabilitarAcomodacao;
	}

	public void setDesabilitarAcomodacao(final boolean desabilitarAcomodacao) {
		this.desabilitarAcomodacao = desabilitarAcomodacao;
	}

	public Integer getSolicitacaoSeq() {
		return this.solicitacaoSeq;
	}

	public void setSolicitacaoSeq(final Integer solicitacaoSeq) {
		this.solicitacaoSeq = solicitacaoSeq;
	}

	public String getCameFrom() {
		return this.cameFrom;
	}

	public void setCameFrom(final String cameFrom) {
		this.cameFrom = cameFrom;
	}

	public Integer getCodigoResponsavel() {
		return this.codigoResponsavel;
	}

	public void setCodigoResponsavel(final Integer codigoResponsavel) {
		this.codigoResponsavel = codigoResponsavel;
	}

	public Integer getMatriculaResponsavel() {
		return this.matriculaResponsavel;
	}

	public void setMatriculaResponsavel(final Integer matriculaResponsavel) {
		this.matriculaResponsavel = matriculaResponsavel;
	}

	public Object getResponsavel() {
		return this.responsavel;
	}

	public void setResponsavel(final Object responsavel) {
		this.responsavel = responsavel;
	}

	public RapServidores getRapServidores() {
		return this.rapServidores;
	}

	public void setRapServidores(final RapServidores rapServidores) {
		this.rapServidores = rapServidores;
	}

	public List<RapServidores> getSolicitantes() {
		return this.solicitantes;
	}

	public void setSolicitantes(final List<RapServidores> solicitantes) {
		this.solicitantes = solicitantes;
	}

	public String getNomeResponsavel() {
		return this.nomeResponsavel;
	}

	public void setNomeResponsavel(final String nomeResponsavel) {
		this.nomeResponsavel = nomeResponsavel;
	}

	public RapServidoresTransPacienteVO getProfessorAux() {
		return this.professorAux;
	}

	public void setProfessorAux(final RapServidoresTransPacienteVO professorAux) {
		this.professorAux = professorAux;
	}

	public Integer getProntuario() {
		return prontuario;
	}

	public void setProntuario(Integer prontuario) {
		this.prontuario = prontuario;
	}

	public String getLeitoID() {
		return leitoID;
	}

	public void setLeitoID(String leitoID) {
		this.leitoID = leitoID;
	}

	public Boolean getNovo() {
		return novo;
	}

	public void setNovo(Boolean novo) {
		this.novo = novo;
	}

}