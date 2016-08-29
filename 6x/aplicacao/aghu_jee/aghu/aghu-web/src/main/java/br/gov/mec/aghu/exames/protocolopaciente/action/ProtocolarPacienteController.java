package br.gov.mec.aghu.exames.protocolopaciente.action;

import java.util.LinkedList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.event.ValueChangeEvent;

import org.apache.commons.lang.StringUtils;

import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.exames.business.IExamesFacade;
import br.gov.mec.aghu.exames.cadastrosapoio.business.ICadastrosApoioExamesFacade;
import br.gov.mec.aghu.exameselaudos.business.IExamesLaudosFacade;
import br.gov.mec.aghu.model.AelEquipamentos;
import br.gov.mec.aghu.model.AelItemSolicitacaoExames;
import br.gov.mec.aghu.model.AelItemSolicitacaoExamesId;
import br.gov.mec.aghu.model.AelPacUnidFuncionais;
import br.gov.mec.aghu.model.AelProtocoloInternoUnids;
import br.gov.mec.aghu.model.AelUnidExecUsuario;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.VAelUnfExecutaExames;
import br.gov.mec.aghu.paciente.business.IPacienteFacade;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;

public class ProtocolarPacienteController extends ActionController {

	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}

	private static final long serialVersionUID = 2196976764520066458L;

	private static final String PAGE_EXAMES_PESQUISA_PROTOCOLAR_PACIENTE = "exames-pesquisaProtocolarPaciente";

	private static final String PAGE_PACIENTE_PESQUISA_PACIENTE_COMPONENTE = "paciente-pesquisaPacienteComponente";

	@EJB
	private IPacienteFacade pacienteFacade;

	@EJB
	private IAghuFacade aghuFacade;

	@EJB
	private IExamesFacade examesFacade;

	@EJB
	private IExamesLaudosFacade examesLaudosFacade;

	@EJB
	private ICadastrosApoioExamesFacade cadastrosApoioExamesFacade;

	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;

	private AelProtocoloInternoUnids protocoloInterno = new AelProtocoloInternoUnids();

	private AelPacUnidFuncionais pacUnidFuncionais = new AelPacUnidFuncionais();

	List<AelPacUnidFuncionais> pacUnidFuncionaisList = new LinkedList<AelPacUnidFuncionais>();

	private AghUnidadesFuncionais unidadeExecutora;
	private Integer prontuario;
	private String nomePaciente;
	private Integer pacCodigoFonetica;
	private AelUnidExecUsuario usuarioUnidadeExecutora;
	private AipPacientes paciente;

	// Flags que determinam comportamento da tela
	private Boolean emEdicao = Boolean.FALSE;
	private Boolean emEdicaoItem = Boolean.FALSE;

	// suggestion exame
	private VAelUnfExecutaExames exame;

	// Parâmetros da conversação
	private Short unfSeq;
	private Integer codigoPaciente;

	private String voltarPara; // O padrão é voltar para interface de pesquisa

	// parâmetro para exclusão
	private AelPacUnidFuncionais itemExclusao = new AelPacUnidFuncionais();

	public void pesquisaPaciente(ValueChangeEvent event){
		try {
			paciente = pacienteFacade.pesquisarPacienteComponente(event.getNewValue(), event.getComponent().getId());
		}catch(BaseException e){
			apresentarExcecaoNegocio(e);
		}
	}
	
	public void iniciar() {
	 


		// edicao
		if (this.unfSeq != null && this.codigoPaciente != null) {
			this.protocoloInterno = this.examesLaudosFacade.obterProtocoloInterno(this.codigoPaciente, this.unfSeq, null);
			this.unidadeExecutora = this.protocoloInterno.getUnidadeFuncional();

			this.paciente = this.pacienteFacade.obterPacientePorCodigo(this.codigoPaciente);
			prontuario = paciente.getProntuario();
			nomePaciente = paciente.getNome();
			codigoPaciente = paciente.getCodigo();
			pacCodigoFonetica = paciente.getCodigo();
			this.emEdicao = Boolean.TRUE;
			this.pacUnidFuncionaisList = this.examesLaudosFacade.listarUnidadesFuncionaisPaciente(this.codigoPaciente, this.unfSeq);
			this.pacUnidFuncionais.setItemSolicitacaoExames(new AelItemSolicitacaoExames(new AelItemSolicitacaoExamesId()));
		} else {
			this.obterUsuarioUnidadeExecutora();
		}

		if (this.pacCodigoFonetica != null) {
			this.paciente = this.pacienteFacade.obterPacientePorCodigo(pacCodigoFonetica);
			prontuario = paciente.getProntuario();
			nomePaciente = paciente.getNome();
			codigoPaciente = paciente.getCodigo();
		}
	
	}

	public String redirecionarPesquisaFonetica() {
		return PAGE_PACIENTE_PESQUISA_PACIENTE_COMPONENTE;
	}

	/**
	 * Metodo para pesquisa na suggestion box de unidade executora
	 */
	public List<AghUnidadesFuncionais> obterAghUnidadesFuncionaisExecutoras(String objPesquisa) {
		return this.aghuFacade.pesquisarUnidadesExecutorasPorCodigoOuDescricao(objPesquisa);
	}

	public void confirmar() {
		try {

			if (!this.emEdicao) {
				if (this.paciente == null && this.codigoPaciente == null && this.prontuario == null) {
					this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_INFO_PACIENTE_PROTOCOLO_NAO_INFORMADO");
					return;
				} else if (this.paciente != null){
					this.protocoloInterno.setPaciente(this.paciente);
				} else if (this.codigoPaciente != null) {
					this.protocoloInterno.setPaciente(this.pacienteFacade.obterNomePacientePorCodigo(this.codigoPaciente));
				} else {
					this.protocoloInterno.setPaciente(this.pacienteFacade.obterPacientePorProntuario(this.prontuario));
				}
				// this.obterUsuarioUnidadeExecutora();
				this.protocoloInterno.setUnidadeFuncional(this.unidadeExecutora);
				this.examesLaudosFacade.inserirAelProtocoloInternoUnids(this.protocoloInterno);

				this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_INSERIR_PROTOCOLAR_PACIENTE");
			}
			this.unfSeq = this.protocoloInterno.getId().getUnidadeFuncional().getSeq();
			this.codigoPaciente = this.protocoloInterno.getId().getPacCodigo();
			this.emEdicao = Boolean.TRUE;
			this.iniciar();
		} catch (BaseException e) {
			this.protocoloInterno = new AelProtocoloInternoUnids();
			super.apresentarExcecaoNegocio(e);
		}
	}

	public List<VAelUnfExecutaExames> pesquisarExame(String parametro) {
		return this.examesFacade.pesquisarExamePorSiglaOuDescricao(parametro, this.unfSeq);
	}

	/**
	 * Suggestion de Convênios
	 * 
	 * @param objPesquisa
	 * @return
	 */
	public List<AelEquipamentos> pesquisarEquipamento(String objPesquisa) {
		try {
			return this.cadastrosApoioExamesFacade.pesquisarEquipamentosPorSiglaOuDescricao((String) objPesquisa, this.unfSeq);
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
			return new LinkedList<AelEquipamentos>();
		}
	}

	public void gravarExameProtocolado() {
		try {
			this.pacUnidFuncionais.setUnfExecutaExames(this.examesFacade.obterAelUnfExecutaExames(this.exame.getId().getSigla(), this.exame.getId().getManSeq(), this.exame.getId().getUnfSeq()));
			
			if (!this.emEdicaoItem) {
				this.pacUnidFuncionais.setAelProtocoloInternoUnids(this.protocoloInterno);
				this.examesLaudosFacade.inserirExameProtocolado(this.pacUnidFuncionais);
				this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_INSERIR_PROTOCOLAR_PACIENTE_INTERNO");
			} else {
				this.examesLaudosFacade.atualizarExameProtocolado(this.pacUnidFuncionais);
				this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_ALTERAR_PROTOCOLAR_PACIENTE_INTERNO");
			}
			this.limpar();
			this.iniciar();
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}
	}

	public void cancelarEdicao() {
		this.limpar();
	}

	public void limpar() {
		this.emEdicaoItem = Boolean.FALSE;
		this.exame = null;
		this.pacUnidFuncionais = new AelPacUnidFuncionais();
		this.pacUnidFuncionais.setItemSolicitacaoExames(new AelItemSolicitacaoExames(new AelItemSolicitacaoExamesId()));
	}

	public void editar() {
		this.exame = this.examesFacade.obterVAelUnfExecutaExames(this.pacUnidFuncionais.getUnfExecutaExames().getId().getEmaExaSigla(), this.pacUnidFuncionais.getUnfExecutaExames().getId()
				.getEmaManSeq(), this.pacUnidFuncionais.getUnfExecutaExames().getId().getUnfSeq().getSeq());

		if (this.pacUnidFuncionais.getItemSolicitacaoExames() == null) {
			this.pacUnidFuncionais.setItemSolicitacaoExames(new AelItemSolicitacaoExames(new AelItemSolicitacaoExamesId()));
		}

		this.emEdicaoItem = Boolean.TRUE;
	}

	public void excluir() {
		try {
			this.examesLaudosFacade.excluirAelPacienteUnidadeFuncional(this.itemExclusao);
			this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_EXCLUIR_PROTOCOLAR_PACIENTE_INTERNO");
			this.limpar();
			this.iniciar();
		} catch (BaseException e) {
			this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_ERRO_EXCLUIR_PROTOCOLAR_PACIENTE_INTERNO");
			apresentarExcecaoNegocio(e);
		}
	}

	/**
	 * Método chamado para o botão voltar
	 */
	public String voltar() {

		this.protocoloInterno = new AelProtocoloInternoUnids();
		this.pacUnidFuncionais = new AelPacUnidFuncionais();
		this.pacUnidFuncionaisList = new LinkedList<AelPacUnidFuncionais>();
		this.unidadeExecutora = null;
		this.prontuario = null;
		this.nomePaciente = null;
		this.pacCodigoFonetica = null;
		this.usuarioUnidadeExecutora = null;
		this.emEdicao = Boolean.FALSE;
		this.emEdicaoItem = Boolean.FALSE;
		this.exame = null;
		this.unfSeq = null;
		this.codigoPaciente = null;
		if (StringUtils.isBlank(voltarPara)){
			this.voltarPara = PAGE_EXAMES_PESQUISA_PROTOCOLAR_PACIENTE; // O padrão é voltar para interface de pesquisa
		}
		this.itemExclusao = new AelPacUnidFuncionais();

		return voltarPara;
	}

	/**
	 * Verifica se o item AelPacUnidFuncionais foi selecionado na lista
	 * 
	 * @param item
	 * @return
	 */
	public boolean isItemSelecionado(final AelPacUnidFuncionais item) {
		if (this.pacUnidFuncionais != null && this.pacUnidFuncionais.equals(item)) {
			return true;
		}
		return false;
	}

	public void obterUsuarioUnidadeExecutora() {
		// Obtem o USUARIO da unidade executora
		try {
			this.usuarioUnidadeExecutora = this.examesFacade.obterUnidExecUsuarioPeloId(registroColaboradorFacade.obterServidorAtivoPorUsuario(this.obterLoginUsuarioLogado()).getId());
		} catch (ApplicationBusinessException e) {
			this.usuarioUnidadeExecutora = null;
		}
		if (this.usuarioUnidadeExecutora != null && this.usuarioUnidadeExecutora.getUnfSeq() != null) {
			// Resgata a unidade executora associada ao usuario
			this.unidadeExecutora = this.usuarioUnidadeExecutora.getUnfSeq();
		}
	}

	// ** GET/SET **/
	public AghUnidadesFuncionais getUnidadeExecutora() {
		return unidadeExecutora;
	}

	public void setUnidadeExecutora(AghUnidadesFuncionais unidadeExecutora) {
		this.unidadeExecutora = unidadeExecutora;
	}

	public Integer getCodigoPaciente() {
		return codigoPaciente;
	}

	public void setCodigoPaciente(Integer codigoPaciente) {
		this.codigoPaciente = codigoPaciente;
	}

	public Integer getProntuario() {
		return prontuario;
	}

	public void setProntuario(Integer prontuario) {
		this.prontuario = prontuario;
	}

	public String getNomePaciente() {
		return nomePaciente;
	}

	public void setNomePaciente(String nomePaciente) {
		this.nomePaciente = nomePaciente;
	}

	public Integer getPacCodigoFonetica() {
		return pacCodigoFonetica;
	}

	public void setPacCodigoFonetica(Integer pacCodigoFonetica) {
		this.pacCodigoFonetica = pacCodigoFonetica;
	}

	public Boolean getEmEdicao() {
		return emEdicao;
	}

	public void setEmEdicao(Boolean emEdicao) {
		this.emEdicao = emEdicao;
	}

	public AelPacUnidFuncionais getPacUnidFuncionais() {
		return pacUnidFuncionais;
	}

	public void setPacUnidFuncionais(AelPacUnidFuncionais pacUnidFuncionais) {
		this.pacUnidFuncionais = pacUnidFuncionais;
	}

	public VAelUnfExecutaExames getExame() {
		return exame;
	}

	public void setExame(VAelUnfExecutaExames exame) {
		this.exame = exame;
	}

	public Short getUnfSeq() {
		return unfSeq;
	}

	public void setUnfSeq(Short unfSeq) {
		this.unfSeq = unfSeq;
	}

	public String getVoltarPara() {
		return voltarPara;
	}

	public void setVoltarPara(String voltarPara) {
		this.voltarPara = voltarPara;
	}

	public List<AelPacUnidFuncionais> getPacUnidFuncionaisList() {
		return pacUnidFuncionaisList;
	}

	public void setPacUnidFuncionaisList(List<AelPacUnidFuncionais> pacUnidFuncionaisList) {
		this.pacUnidFuncionaisList = pacUnidFuncionaisList;
	}

	public AelProtocoloInternoUnids getProtocoloInterno() {
		return protocoloInterno;
	}

	public void setProtocoloInterno(AelProtocoloInternoUnids protocoloInterno) {
		this.protocoloInterno = protocoloInterno;
	}

	public Boolean getEmEdicaoItem() {
		return emEdicaoItem;
	}

	public void setEmEdicaoItem(Boolean emEdicaoItem) {
		this.emEdicaoItem = emEdicaoItem;
	}

	public AelPacUnidFuncionais getItemExclusao() {
		return itemExclusao;
	}

	public void setItemExclusao(AelPacUnidFuncionais itemExclusao) {
		this.itemExclusao = itemExclusao;
	}
	
	public AipPacientes getPaciente() {
		return paciente;
	}

	public void setPaciente(AipPacientes paciente) {
		this.paciente = paciente;
	}	
}
