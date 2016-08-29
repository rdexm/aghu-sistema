package br.gov.mec.aghu.exames.action;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.event.ValueChangeEvent;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.business.prescricaoenfermagem.IPrescricaoEnfermagemFacade;
import br.gov.mec.aghu.exames.business.IExamesFacade;
import br.gov.mec.aghu.exames.vo.LiberacaoLimitacaoExameVO;
import br.gov.mec.aghu.internacao.business.IInternacaoFacade;
import br.gov.mec.aghu.model.AelExamesLimitadoAtend;
import br.gov.mec.aghu.model.AelExamesLimitadoAtendId;
import br.gov.mec.aghu.model.AelExamesMaterialAnaliseId;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.AinLeitos;
import br.gov.mec.aghu.model.AinQuartos;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.VAelExameMatAnalise;
import br.gov.mec.aghu.paciente.business.IPacienteFacade;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.exception.Severity;

public class ExameLimitadoAtendController extends ActionController {

	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}

	private static final Log LOG = LogFactory.getLog(ExameLimitadoAtendController.class);
	
	private static final String PAGE_PACIENTE_PESQUISA_PACIENTE_COMPONENTE = "paciente-pesquisaPacienteComponente";

	private static final long serialVersionUID = 8907427566285621297L;

	@EJB
	private IExamesFacade examesFacade;

	@EJB
	private IPrescricaoEnfermagemFacade prescricaoEnfermagemFacade;

	@EJB
	private IPacienteFacade pacienteFacade;

	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;

	@EJB
	private IInternacaoFacade internacaoFacade;

	private Boolean edicao = false;
	private AinQuartos quarto;
	private AghUnidadesFuncionais unidadeFuncional;
	private VAelExameMatAnalise exameMatAnalise;
	private Date dthrLimite;
	private AinLeitos leito;
	private AghAtendimentos atendimento;
	private AipPacientes paciente;
	private Integer codPac;
	private Integer prontuario;
	private Integer atdSeq;
	private List<LiberacaoLimitacaoExameVO> listaExames;
	private Boolean primeiraPesquisa = false;
	private LiberacaoLimitacaoExameVO liberacaoLimitacaoExameVO;
	private String sigla;
	private Integer manSeq;
	private Integer pacCodigoFonetica;

	public void iniciar() {
	 

		// pac_codigo de um paciente selecionado na pesquisa fonética
		if (pacCodigoFonetica != null) {
			limpar();
			this.paciente = this.pacienteFacade.obterPacientePorCodigo(pacCodigoFonetica);
			this.codPac = this.paciente.getCodigo();
			this.prontuario = this.getPaciente().getProntuario();
			this.carregarAtendimentoPorPaciente();
		}
	
	}

	public Integer getPacCodigoFonetica() {
		return pacCodigoFonetica;
	}

	public void setPacCodigoFonetica(Integer pacCodigoFonetica) {
		this.pacCodigoFonetica = pacCodigoFonetica;
	}

	public enum ExameLimitadoAtendControllerExceptionCode implements BusinessExceptionCode {
		ERRO_LIBERA_LIMITACAO_EXAME_SOLICITACAO_ATENDIMENTO, EXAME_INCLUIDO_COM_SUCESSO, EXAME_ATUALIZADO_COM_SUCESSO, EXAME_EXCLUIDO_COM_SUCESSO, ERRO_ATENDIMENTO_OBRIGATORIO_EXAME_LIMITADO
	}

	public AghAtendimentos getAtendimento() {
		return atendimento;
	}

	public void setAtendimento(AghAtendimentos atendimento) {
		this.atendimento = atendimento;
	}

	public void pesquisar() {
		this.primeiraPesquisa = true;
		if (atdSeq == null || atdSeq.intValue() == 0) {
			this.apresentarMsgNegocio(Severity.ERROR, ExameLimitadoAtendControllerExceptionCode.ERRO_ATENDIMENTO_OBRIGATORIO_EXAME_LIMITADO.toString());
		} else {
			this.listaExames = this.examesFacade.pesquisarLiberacaoLimitacaoExames(atdSeq);
		}
		// carregar lista de exames
	}

	public List<VAelExameMatAnalise> sbObterMaterialAnalise(String objPesquisa) {
		return this.examesFacade.listarVExamesMaterialAnalise(objPesquisa);
	}

	private void carregarAtendimento() {
		this.setPaciente(this.getAtendimento().getPaciente());
		this.setProntuario(this.getPaciente().getProntuario());
		this.setCodPac(this.getPaciente().getCodigo());
		this.setLeito(this.getAtendimento().getLeito());
		this.setQuarto(this.getAtendimento().getQuarto());
		this.setUnidadeFuncional(this.getAtendimento().getUnidadeFuncional());
		this.atdSeq = this.getAtendimento().getSeq();
	}

	public void pesquisaPaciente(ValueChangeEvent event){
		try {
			paciente = pacienteFacade.pesquisarPacienteComponente(event.getNewValue(), event.getComponent().getId());
			if (paciente != null) {
				codPac = paciente.getCodigo();
				carregarAtendimentoPorPaciente();
			}
		}catch(BaseException e){
			apresentarExcecaoNegocio(e);
		}
	}
	
	public void carregarAtendimentoPorLeito() {
		try {
			if (leito != null) {
				AghAtendimentos atendimento = examesFacade.obterAtendimentoPorLeito(leito.getLeitoID());
				if (atendimento != null) {
					this.setAtendimento(atendimento);
					this.carregarAtendimento();
				} else {
					this.apresentarMsgNegocio(Severity.ERROR, ExameLimitadoAtendControllerExceptionCode.ERRO_LIBERA_LIMITACAO_EXAME_SOLICITACAO_ATENDIMENTO.toString());
					limparCampos();
				}

			}
		} catch (ApplicationBusinessException e) {
			this.apresentarExcecaoNegocio(e);
			LOG.error("Exceção caputada:", e);
		}
	}

	public void carregarAtendimentoPorPaciente() {
		try {
			if (codPac != null) {
				AipPacientes paciente = this.getPacienteFacade().obterPaciente(codPac);
				this.prontuario = paciente.getProntuario();
			}
			if (prontuario != null) {
				AghAtendimentos atendimento = examesFacade.obterAtendimentoAtualPorProntuario(prontuario);
				if (atendimento != null) {
					this.setAtendimento(atendimento);
					this.carregarAtendimento();
				} else {
					this.apresentarMsgNegocio(Severity.ERROR, ExameLimitadoAtendControllerExceptionCode.ERRO_LIBERA_LIMITACAO_EXAME_SOLICITACAO_ATENDIMENTO.toString());
					limparCampos();
				}
			}
		} catch (ApplicationBusinessException e) {
			this.apresentarExcecaoNegocio(e);
			LOG.error("Exceção caputada:", e);
		}

	}

	public void limparCampos() {
		this.atdSeq = null;
		this.prontuario = null;
		this.codPac = null;
		this.dthrLimite = null;
		this.exameMatAnalise = null;
		this.paciente = null;
		this.leito = null;
		this.quarto = null;
		this.unidadeFuncional = null;
		this.atendimento = null;
	}

	public String redirecionarPesquisaFonetica() {
		return PAGE_PACIENTE_PESQUISA_PACIENTE_COMPONENTE;
	}

	public void limpar() {
		this.primeiraPesquisa = false;
		this.listaExames = null;
		this.liberacaoLimitacaoExameVO = null;
		this.edicao = false;
		this.limparCampos();
	}

	public void editar(LiberacaoLimitacaoExameVO liberacaoLimitacaoExameVO) {
		this.liberacaoLimitacaoExameVO = liberacaoLimitacaoExameVO;
		edicao = true;
		this.dthrLimite = liberacaoLimitacaoExameVO.getDthrLimite();
		AelExamesMaterialAnaliseId id = new AelExamesMaterialAnaliseId();
		id.setExaSigla(liberacaoLimitacaoExameVO.getSigla());
		id.setManSeq(liberacaoLimitacaoExameVO.getManSeq());
		exameMatAnalise = this.examesFacade.obterVAelExameMaterialAnalise(id);
	}

	public void cancelarEdicao() {
		this.liberacaoLimitacaoExameVO = null;
		this.exameMatAnalise = null;
		this.dthrLimite = null;
		edicao = false;
	}

	public void excluir() {
		AelExamesLimitadoAtendId id = new AelExamesLimitadoAtendId();
		AelExamesLimitadoAtend exameLimitadoAtend;
		id.setAtdSeq(this.getAtdSeq());
		id.setEmaExaSigla(this.getSigla());
		id.setEmaManSeq(this.getManSeq());
		exameLimitadoAtend = this.examesFacade.obterAelExamesLimitadoAtendPorId(id);
		try {
			this.examesFacade.removerExameLimitadoAtend(exameLimitadoAtend);
			this.listaExames = this.examesFacade.pesquisarLiberacaoLimitacaoExames(atdSeq);
			edicao = false;
			this.apresentarMsgNegocio(Severity.INFO, ExameLimitadoAtendControllerExceptionCode.EXAME_EXCLUIDO_COM_SUCESSO.toString());
			this.cancelarEdicao();
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
	}

	public void gravar() {
		try {
			AelExamesLimitadoAtendId id = new AelExamesLimitadoAtendId();
			AelExamesLimitadoAtend exameLimitadoAtend = new AelExamesLimitadoAtend();
			id.setAtdSeq(this.getAtdSeq());
			id.setEmaExaSigla(exameMatAnalise.getId().getExaSigla());
			id.setEmaManSeq(exameMatAnalise.getId().getManSeq());
			exameLimitadoAtend.setCriadoEm(new Date());
			exameLimitadoAtend.setServidor(registroColaboradorFacade.obterServidorAtivoPorUsuario(this.obterLoginUsuarioLogado()));
			exameLimitadoAtend.setDthrLimite(this.getDthrLimite());
			exameLimitadoAtend.setId(id);
			this.examesFacade.persistirExameLimitadoAtend(exameLimitadoAtend);
			this.listaExames = this.examesFacade.pesquisarLiberacaoLimitacaoExames(atdSeq);
			edicao = false;
			this.apresentarMsgNegocio(Severity.INFO, ExameLimitadoAtendControllerExceptionCode.EXAME_INCLUIDO_COM_SUCESSO.toString());
			this.cancelarEdicao();
		} catch (ApplicationBusinessException e) {
			this.apresentarExcecaoNegocio(e);
			LOG.error("Exceção caputada:", e);
		}

	}

	public void alterar() {
		AelExamesLimitadoAtendId id = new AelExamesLimitadoAtendId();
		AelExamesLimitadoAtend exameLimitadoAtend = new AelExamesLimitadoAtend();
		id.setAtdSeq(liberacaoLimitacaoExameVO.getAtdSeq());
		id.setEmaExaSigla(liberacaoLimitacaoExameVO.getSigla());
		id.setEmaManSeq(liberacaoLimitacaoExameVO.getManSeq());
		exameLimitadoAtend = this.examesFacade.obterAelExamesLimitadoAtendPorId(id);
		exameLimitadoAtend.setDthrLimite(this.getDthrLimite());
		exameLimitadoAtend.setId(id);
		try {
			this.examesFacade.atualizarExameLimitadoAtend(exameLimitadoAtend);
			this.listaExames = this.examesFacade.pesquisarLiberacaoLimitacaoExames(atdSeq);
			edicao = false;
			this.apresentarMsgNegocio(Severity.INFO, ExameLimitadoAtendControllerExceptionCode.EXAME_ATUALIZADO_COM_SUCESSO.toString());
			this.cancelarEdicao();
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
	}

	public List<AinLeitos> pesquisarLeito(String objParam) throws ApplicationBusinessException {
		List<AinLeitos> leitos = new ArrayList<AinLeitos>();
		String strPesquisa = (String) objParam;
		if (!StringUtils.isBlank(strPesquisa)) {
			strPesquisa = strPesquisa.toUpperCase();
		}
		leitos = this.internacaoFacade.obterLeitosAtivosPorUnf(strPesquisa, null);

		return leitos;
	}

	public Boolean getEdicao() {
		return edicao;
	}

	public void setEdicao(Boolean edicao) {
		this.edicao = edicao;
	}

	public IExamesFacade getExamesFacade() {
		return examesFacade;
	}

	public void setExamesFacade(IExamesFacade examesFacade) {
		this.examesFacade = examesFacade;
	}

	public AinQuartos getQuarto() {
		return quarto;
	}

	public void setQuarto(AinQuartos quarto) {
		this.quarto = quarto;
	}

	public AghUnidadesFuncionais getUnidadeFuncional() {
		return unidadeFuncional;
	}

	public void setUnidadeFuncional(AghUnidadesFuncionais unidadeFuncional) {
		this.unidadeFuncional = unidadeFuncional;
	}

	public VAelExameMatAnalise getExameMatAnalise() {
		return exameMatAnalise;
	}

	public void setExameMatAnalise(VAelExameMatAnalise exameMatAnalise) {
		this.exameMatAnalise = exameMatAnalise;
	}

	public AinLeitos getLeito() {
		return leito;
	}

	public void setLeito(AinLeitos leito) {
		this.leito = leito;
	}

	public AipPacientes getPaciente() {
		return paciente;
	}

	public void setPaciente(AipPacientes paciente) {
		this.paciente = paciente;
	}

	public Date getDthrLimite() {
		return dthrLimite;
	}

	public void setDthrLimite(Date dthrLimite) {
		this.dthrLimite = dthrLimite;
	}

	public Integer getCodPac() {
		return codPac;
	}

	public void setCodPac(Integer codPac) {
		this.codPac = codPac;
	}

	public Integer getProntuario() {
		return prontuario;
	}

	public void setProntuario(Integer prontuario) {
		this.prontuario = prontuario;
	}

	public Integer getAtdSeq() {
		return atdSeq;
	}

	public void setAtdSeq(Integer atdSeq) {
		this.atdSeq = atdSeq;
	}

	public IPrescricaoEnfermagemFacade getPrescricaoEnfermagemFacade() {
		return prescricaoEnfermagemFacade;
	}

	public void setPrescricaoEnfermagemFacade(IPrescricaoEnfermagemFacade prescricaoEnfermagemFacade) {
		this.prescricaoEnfermagemFacade = prescricaoEnfermagemFacade;
	}

	public List<LiberacaoLimitacaoExameVO> getListaExames() {
		return listaExames;
	}

	public void setListaExames(List<LiberacaoLimitacaoExameVO> listaExames) {
		this.listaExames = listaExames;
	}

	public IPacienteFacade getPacienteFacade() {
		return pacienteFacade;
	}

	public void setPacienteFacade(IPacienteFacade pacienteFacade) {
		this.pacienteFacade = pacienteFacade;
	}

	public Boolean getPrimeiraPesquisa() {
		return primeiraPesquisa;
	}

	public void setPrimeiraPesquisa(Boolean primeiraPesquisa) {
		this.primeiraPesquisa = primeiraPesquisa;
	}

	public LiberacaoLimitacaoExameVO getLiberacaoLimitacaoExameVO() {
		return liberacaoLimitacaoExameVO;
	}

	public void setLiberacaoLimitacaoExameVO(LiberacaoLimitacaoExameVO liberacaoLimitacaoExameVO) {
		this.liberacaoLimitacaoExameVO = liberacaoLimitacaoExameVO;
	}

	public String getSigla() {
		return sigla;
	}

	public void setSigla(String sigla) {
		this.sigla = sigla;
	}

	public Integer getManSeq() {
		return manSeq;
	}

	public void setManSeq(Integer manSeq) {
		this.manSeq = manSeq;
	}

}
