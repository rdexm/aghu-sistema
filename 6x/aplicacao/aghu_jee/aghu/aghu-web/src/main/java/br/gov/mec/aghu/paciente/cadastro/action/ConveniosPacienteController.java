package br.gov.mec.aghu.paciente.cadastro.action;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.persistence.OptimisticLockException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.faturamento.cadastrosapoio.business.IFaturamentoApoioFacade;
import br.gov.mec.aghu.model.AipConveniosSaudePaciente;
import br.gov.mec.aghu.model.AipConveniosSaudePacienteId;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.FatConvenioSaudePlano;
import br.gov.mec.aghu.paciente.business.IPacienteFacade;
import br.gov.mec.aghu.paciente.cadastro.business.ICadastroPacienteFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;

/**
 * Classe responsável por controlar as ações da tela de cadastro e edição de
 * convenios do paciente
 * 
 * @author gmneto
 * 
 */
public class ConveniosPacienteController extends ActionController {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5820257583987853376L;

	private static final Log LOG = LogFactory.getLog(ConveniosPacienteController.class);

	/**
	 * Atributo do contexto que representa o paciente em edição.
	 */
	private AipPacientes paciente;

	@EJB
	private IFaturamentoApoioFacade faturamentoApoioFacade;

	@EJB
	private ICadastroPacienteFacade cadastroPacienteFacade;

	@EJB
	private IPacienteFacade pacienteFacade;

	/**
	 * Codigo do paciente, obtido via page parameter.
	 */
	private Integer aipPacientesCodigo;

	/**
	 * Lista de convênios associados a este paciente.
	 */
	private List<AipConveniosSaudePaciente> planosPaciente;

	/**
	 * Plano de saude sendo vinculado ao usuário.
	 */
	private FatConvenioSaudePlano plano;

	/**
	 * Plano paciente sendo criado/editado.
	 */
	private AipConveniosSaudePaciente planoPaciente;

	private Short convenioId;

	private Byte planoId;

	private Integer idConversacaoAnterior;

	private boolean edicao;

	private static final String REDIRECT_CONVENIOS_PACIENTE = "conveniosPaciente";

	private static final String REDIRECT_PLANOS_CONVENIOS_PACIENTE = "planoConveniosPaciente";

	private static final String REDIRECT_CADASTRO_PACIENTE = "cadastroPaciente";

	private static final String REDIRECT_CADASTRO_INTERNACAO = "internacao-cadastroInternacao";

	public Short getConvenioId() {
		return convenioId;
	}

	public void setConvenioId(final Short convenioId) {
		this.convenioId = convenioId;
	}

	public Byte getPlanoId() {
		return planoId;
	}

	public void setPlanoId(final Byte planoId) {
		this.planoId = planoId;
	}

	/**
	 * variável que controla fechamento do modal.
	 */
	private boolean operacaoConcluida = false;

	/**
	 * Código do convênio, passado via parâmetro quando vier da tela de
	 * internação
	 */
	private Short cnvCodigo;

	/**
	 * Seq do convênio, passado via parâmetro quando vier da tela de internação
	 */
	private Byte cnvSeq;

	/**
	 * Parâmetro adicionado para saber de onde esta tela foi chamada
	 */
	private String cameFrom;

	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}

	public void preparaInicioConvenioPaciente() {
	 

		if (paciente == null) {
			paciente = this.pacienteFacade.obterPaciente(aipPacientesCodigo);
			planosPaciente = this.pacienteFacade.pesquisarConveniosPaciente(this.paciente);

		}
		if (this.planosPaciente == null) {
			planosPaciente = this.pacienteFacade.pesquisarConveniosPaciente(this.paciente);
		}

		for (final AipConveniosSaudePaciente plano : planosPaciente) {
			plano.setPaciente(this.paciente);
		}

		/*---Trecho utilizado quando vier da tela de internação---*/
		if ("internacaoPaciente".equalsIgnoreCase(cameFrom)) {
			if (cnvCodigo != null && cnvSeq != null) {
				boolean jaPossui = false;
				for (final AipConveniosSaudePaciente conv : planosPaciente) {
					if (conv.getConvenio().getId().getCnvCodigo().equals(cnvCodigo)) {
						jaPossui = true;
						break;
					}
				}
				if (!jaPossui) {
					this.adicionarConvenioDaInternacao();
				}
			}
		}
		/*---------------------------------------------------------*/
	
	}

	public String cancelar() {
		paciente = null;

		String retorno = REDIRECT_CADASTRO_PACIENTE;
		
		if ("internacaoPaciente".equalsIgnoreCase(cameFrom)) {
			retorno = REDIRECT_CADASTRO_INTERNACAO;
		}
		
		return retorno;
	}

	public String cancelarEdicao() {
		this.convenioId = null;
		this.planoId = null;

		return REDIRECT_CONVENIOS_PACIENTE;
	}

	/**
	 * Método que adiciona um convênio vindo da internação de paciente.
	 */
	public void adicionarConvenioDaInternacao() {
		this.planoPaciente = new AipConveniosSaudePaciente();
		this.planoPaciente.setSituacao(DominioSituacao.A);
		final AipConveniosSaudePacienteId planoPacienteId = new AipConveniosSaudePacienteId();
		planoPacienteId.setPacCodigo(this.paciente.getCodigo());
		this.planoPaciente.setId(planoPacienteId);
		final FatConvenioSaudePlano convenio = faturamentoApoioFacade.obterConvenioSaudePlano(cnvCodigo, cnvSeq);

		this.planoPaciente.setConvenio(convenio);
		this.planosPaciente.add(this.planoPaciente);
		this.planoPaciente.setPaciente(this.paciente);
		this.planoPaciente.setCriadoEm(new Date());

		this.plano = null;
		this.operacaoConcluida = true;
	}

	/**
	 * 
	 */
	public String prepararAdicionarPlanoPaciente() {
		this.planoPaciente = new AipConveniosSaudePaciente();
		this.planoPaciente.setSituacao(DominioSituacao.A);
		final AipConveniosSaudePacienteId planoPacienteId = new AipConveniosSaudePacienteId();
		planoPacienteId.setPacCodigo(this.paciente.getCodigo());
		this.planoPaciente.setId(planoPacienteId);
		this.operacaoConcluida = false;
		this.plano = null;
		this.edicao = false;
		return REDIRECT_PLANOS_CONVENIOS_PACIENTE;
	}

	/**
	 * método que persiste um AipConvenioSaudePaciente
	 */
	public String incluirPlanoPaciente() {
		if (this.plano == null) {
			this.apresentarMsgNegocio(Severity.ERROR, "PLANO_OBRIGATORIO");
			return null;
		}

		this.planoPaciente.setConvenio(plano);
		if (!edicao) {
			this.planosPaciente.add(this.planoPaciente);
		}
		this.planoPaciente.setPaciente(this.paciente);

		// Caso esteja editando o registro, não altera data de criação
		if (this.planoPaciente.getCriadoEm() == null) {
			this.planoPaciente.setCriadoEm(new Date());
		}

		if (this.planoPaciente.getSituacao() == DominioSituacao.I && this.planoPaciente.getEncerradoEm() == null) {
			this.planoPaciente.setEncerradoEm(new Date());
		}

		this.convenioId = null;
		this.planoId = null;
		this.plano = null;
		this.edicao = false;
		this.operacaoConcluida = true;

		return REDIRECT_CONVENIOS_PACIENTE;
	}

	/**
	 * Método usado para confirmar as alterações nos convênios do paciente.
	 */
	public String confirmar() {
		try {
			this.cadastroPacienteFacade.persistirPlanoPaciente(planosPaciente);
		} catch (final BaseException e) {
			LOG.error(e.getClass().getName(), e);
			this.apresentarExcecaoNegocio(e);
			return null;
		} catch (final OptimisticLockException e) {
			LOG.error(e.getMessage(), e);
			this.planosPaciente = null;
			throw e;
		}

		this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_LISTA_PLANOS_PACIENTE");
		paciente = null;
		String retorno = REDIRECT_CADASTRO_PACIENTE;
		if ("internacaoPaciente".equalsIgnoreCase(cameFrom)) {
			retorno = REDIRECT_CADASTRO_INTERNACAO;
		}
		return retorno;

	}

	public String editarPlanoPaciente(final AipConveniosSaudePaciente planoPaciente) {
		this.planoPaciente = planoPaciente;
		this.plano = planoPaciente.getConvenio();
		this.planoId = planoPaciente.getConvenio().getId().getSeq();
		this.convenioId = planoPaciente.getConvenio().getConvenioSaude().getCodigo();

		this.edicao = true;
		this.operacaoConcluida = false;

		return REDIRECT_PLANOS_CONVENIOS_PACIENTE;
	}

	/**
	 * 
	 * @param planoPaciente
	 */
	public void removerPlanoPaciente(final AipConveniosSaudePaciente planoPaciente) {
		try {
			this.planosPaciente.remove(planoPaciente);
			this.cadastroPacienteFacade.removerPlanoPaciente(planoPaciente.getId().getPacCodigo(), planoPaciente.getId().getSeq());
			this.operacaoConcluida = false;
		} catch (final BaseException e) {
			LOG.error(e.getMessage(), e);
			apresentarExcecaoNegocio(e);
		}
	}

	public AipPacientes getPaciente() {
		return paciente;
	}

	/**
	 * @return the conveniosDoPaciente
	 */
	public List<AipConveniosSaudePaciente> getPlanosPaciente() {
		return planosPaciente;
	}

	/**
	 * @param conveniosDoPaciente
	 *            the conveniosDoPaciente to set
	 */
	public void setPlanosPaciente(final List<AipConveniosSaudePaciente> conveniosDoPaciente) {
		this.planosPaciente = conveniosDoPaciente;
	}

	/**
	 * @return the plano
	 */
	public FatConvenioSaudePlano getPlano() {
		return plano;
	}

	/**
	 * @param plano
	 *            the plano to set
	 */
	public void setPlano(final FatConvenioSaudePlano plano) {
		this.plano = plano;
	}

	public void atribuirPlano(final FatConvenioSaudePlano plano) {

		if (plano != null) {
			this.plano = plano;
			this.convenioId = plano.getConvenioSaude().getCodigo();
			this.planoId = plano.getId().getSeq();
		} else {
			this.plano = null;
			this.convenioId = null;
			this.planoId = null;

		}
	}

	public void atribuirPlano() {

		if (this.plano != null) {
			this.convenioId = this.plano.getConvenioSaude().getCodigo();
			this.planoId = this.plano.getId().getSeq();
		} else {
			this.plano = null;
			this.convenioId = null;
			this.planoId = null;
		}
	}

	/**
	 * @return the planoPaciente
	 */
	public AipConveniosSaudePaciente getPlanoPaciente() {
		return planoPaciente;
	}

	/**
	 * @param planoPaciente
	 *            the planoPaciente to set
	 */
	public void setPlanoPaciente(final AipConveniosSaudePaciente planoPaciente) {
		this.planoPaciente = planoPaciente;
	}

	/**
	 * @return the operacaoConcluida
	 */
	public boolean isOperacaoConcluida() {
		return operacaoConcluida;
	}

	/**
	 * @param operacaoConcluida
	 *            the operacaoConcluida to set
	 */
	public void setOperacaoConcluida(final boolean operacaoConcluida) {
		this.operacaoConcluida = operacaoConcluida;
	}

	public Integer getAipPacientesCodigo() {
		return aipPacientesCodigo;
	}

	public void setAipPacientesCodigo(final Integer aipPacientesCodigo) {
		this.aipPacientesCodigo = aipPacientesCodigo;
	}

	public String getStyleClass(final AipConveniosSaudePaciente convenio) {
		String retorno = "";

		if (convenio.getId().getSeq() == null) {
			retorno = "marcado";
		}
		return retorno;

	}

	public String getStyleProntuario() {
		String retorno = "";
		if (paciente != null && paciente.isProntuarioVirtual()) {
			retorno = "background-color:#0000ff";
		}
		return retorno;
	}

	public void escolherPlanoConvenio() {
		if (this.planoId != null && this.convenioId != null) {
			final FatConvenioSaudePlano plano = this.faturamentoApoioFacade.obterPlanoPorIdConvenioInternacao(this.planoId, this.convenioId);
			if (plano == null) {
				this.apresentarMsgNegocio(Severity.WARN, "MENSAGEM_CONVENIO_PLANO_NAO_ENCONTRADO", this.convenioId, this.planoId);
			}
			this.atribuirPlano(plano);

		}

	}

	public List<FatConvenioSaudePlano> pesquisarConvenioSaudePlanos(final String filtro) {
		final List<FatConvenioSaudePlano> result = this.faturamentoApoioFacade.pesquisarConvenioSaudePlanosInternacao((String) filtro);
		if (result == null) {
			return new ArrayList<FatConvenioSaudePlano>(0);
		}
		return result;
	}

	public String getCameFrom() {
		return cameFrom;
	}

	public void setCameFrom(final String cameFrom) {
		this.cameFrom = cameFrom;
	}

	public Short getCnvCodigo() {
		return cnvCodigo;
	}

	public void setCnvCodigo(final Short cnvCodigo) {
		this.cnvCodigo = cnvCodigo;
	}

	public Byte getCnvSeq() {
		return cnvSeq;
	}

	public void setCnvSeq(final Byte cnvSeq) {
		this.cnvSeq = cnvSeq;
	}

	public Integer getIdConversacaoAnterior() {
		return idConversacaoAnterior;
	}

	public void setIdConversacaoAnterior(final Integer idConversacaoAnterior) {
		this.idConversacaoAnterior = idConversacaoAnterior;
	}

}
