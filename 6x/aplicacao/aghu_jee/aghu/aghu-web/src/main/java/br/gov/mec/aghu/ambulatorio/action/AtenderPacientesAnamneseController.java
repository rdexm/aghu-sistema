package br.gov.mec.aghu.ambulatorio.action;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import org.primefaces.context.RequestContext;

import br.gov.mec.aghu.ambulatorio.business.IAmbulatorioFacade;
import br.gov.mec.aghu.ambulatorio.vo.AnamneseVO;
import br.gov.mec.aghu.ambulatorio.vo.DocumentosPacienteVO;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.casca.business.ICascaFacade;
import br.gov.mec.aghu.constante.ConstanteAghCaractUnidFuncionais;
import br.gov.mec.aghu.dominio.DominioModulo;
import br.gov.mec.aghu.model.AacConsultas;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.AghEspecialidades;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.CseCategoriaProfissional;
import br.gov.mec.aghu.model.MamAnamneses;
import br.gov.mec.aghu.model.MamItemAnamneses;
import br.gov.mec.aghu.model.MamNotaAdicionalAnamneses;
import br.gov.mec.aghu.model.MamTipoItemAnamneses;
import br.gov.mec.aghu.model.MpmPrescricaoMedica;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;

/**
 * Controller da tela Anamnese
 * 
 * @author rafael.silvestre
 */
public class AtenderPacientesAnamneseController extends ActionController {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 5069853012736722909L;
	
	private static final String PAGE_SOLICITACAO_EXAME_CRUD = "exames-solicitacaoExameCRUD";
	private static final String PAGE_VERIFICA_PRESCRICAO_MEDICA = "prescricaomedica-verificaPrescricaoMedica";
	private static final String PAGE_PESQUISAR_PACIENTES_AGENDADOS = "ambulatorio-pesquisarPacientesAgendados";
	
	@EJB
	private IAghuFacade aghuFacade;

	@EJB
	private IAmbulatorioFacade ambulatorioFacade;
	
    @EJB
    private ICascaFacade cascaFacade;
    
    @EJB
    private IRegistroColaboradorFacade registroColaboradorFacade;
    
    @EJB
    private IServidorLogadoFacade servidorLogadoFacade;

	private Integer selectedTab;

	private Boolean readonlyAnamnese;

	private Integer sliderAtual;

	private Boolean habilitaAnamnese = false;
	
	private MpmPrescricaoMedica prescricaoMedica;
	
	private AghEspecialidades especialidade;
	
	private AipPacientes paciente;
	
	private String cameFrom;
	
	private AacConsultas consultaSelecionada;
	
	private String titleAccordion;
	
	private Integer cagSeq;
	
	private List<AnamneseVO> listaBotoes;
	
	private Boolean permiteColar;
	
	private String idadeFormatada;
	
	private Long anaSeq;
	
	private String motivoPendencia;
	
	private List<MamNotaAdicionalAnamneses> notasAdicionaisAnamnesesList;

	private String descrNotaAdicionalAnamnese;
	
	private Boolean modoInsercaoNotaAdicional;
	
	private MamNotaAdicionalAnamneses notaAdicionalAnamneses;
	
	private RapServidores servidorLogadoSemFimVinculo;
	
	private AghAtendimentos atendimento;
	
	private boolean executouIniciar;
	
	@Inject
	private PesquisarPacientesAgendadosController pesquisarPacientesAgendadosController;
	
	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}
	
	/**
	 * Método invocado ao carregar a tela.
	 */
	public void iniciar() {
		if (!this.executouIniciar) {
			motivoPendencia = "POS";
			modoInsercaoNotaAdicional = true;
			anaSeq = null;
			try {
				atendimento = aghuFacade.obterAtendimentoPorConsulta(this.consultaSelecionada.getNumero());
				servidorLogadoSemFimVinculo = registroColaboradorFacade.obterServidorAtivoPorUsuario(obterLoginUsuarioLogado());
				permiteColar = ambulatorioFacade.verificaColar();
				notasAdicionaisAnamnesesList = this.ambulatorioFacade.obterNotaAdicionalAnamnesesConsulta(this.consultaSelecionada.getNumero());
			} catch (ApplicationBusinessException e) {
				apresentarExcecaoNegocio(e);
			}
			
			List<MamAnamneses> C1 = ambulatorioFacade.pesquisarMamAnamnesesPaciente(consultaSelecionada.getNumero());
			if (C1 != null && !C1.isEmpty()) {
				anaSeq = C1.get(0).getSeq();
			}
			
			List<CseCategoriaProfissional> catProf = cascaFacade.pesquisarCategoriaProfissional(servidorLogadoFacade.obterServidorLogado());
			if (catProf != null && !catProf.isEmpty()) {
				cagSeq = catProf.get(0).getSeq();
			}
			titleAccordion = "Anamnese";
			List<MamTipoItemAnamneses> C2 = ambulatorioFacade.pesquisarTipoItemAnamneseBotoes(cagSeq);
			if (C2 != null && !C2.isEmpty()) {
				titleAccordion = C2.get(0).getDescricao();
				listaBotoes = new ArrayList<AnamneseVO>();
				for (MamTipoItemAnamneses item : C2) {
					AnamneseVO ana = new AnamneseVO();
					ana.setTipoItemAnamnese(item);
					ana.setRender(Boolean.FALSE);
					if (anaSeq != null) {
						List<MamItemAnamneses> C3 = ambulatorioFacade.pesquisarItemAnamnesePorAnamneseTipoItem(anaSeq, item.getSeq());
						if (C3 != null && C3.size() > 0) {
							ana.setTexto(C3.get(0).getDescricao());
						}
					}
					listaBotoes.add(ana);
				}
				listaBotoes.get(0).setRender(Boolean.TRUE);
			}
			this.executouIniciar = true;
		}
	}
	
	public void alteraTab(AnamneseVO botao) {
		for (AnamneseVO evolucaoVO : listaBotoes) {
			evolucaoVO.setRender(Boolean.FALSE);
		}
		botao.setRender(Boolean.TRUE);
		titleAccordion = botao.getTipoItemAnamnese().getDescricao();
		RequestContext.getCurrentInstance().execute("binds();");
	}

	/**
	 * Ação do botão Voltar
	 */
	public String voltar() {
		apresentarMsgNegocio(Severity.INFO, "MSG2_VOLTAR_ANAMNESE");
		return null;
	}
	
	/**
	 * Encaminha para a tela de origem
	 */
	public String redirect() {
		titleAccordion = null;
		listaBotoes = null;
		cagSeq = null;
		consultaSelecionada = null;
		anaSeq = null;		
		executouIniciar = false;
		return cameFrom;
	}
	
	/**
	 * Ação do botão Gravar
	 */
	public void gravar() {
		try {
			ambulatorioFacade.gravarAnamnese(consultaSelecionada.getNumero(), anaSeq, listaBotoes);
			this.executouIniciar = false;
			iniciar();
			apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_ANAMNESE");
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
	}
	
	/**
	 * Ação do botão Ok
	 */
	public String gravarOk() {
		try {
			ambulatorioFacade.gravarAnamnese(consultaSelecionada.getNumero(), anaSeq, listaBotoes);
			apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_ANAMNESE");
			return redirect();
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
		return null;
	}
	
	/**
	 * Ação do botão Excluir
	 */
	public String excluir() {
		try {
			ambulatorioFacade.excluirAnamnese(consultaSelecionada.getNumero(), anaSeq);
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
			return null;
		}
		return redirect();
	}	
	
	/**
	 * Ação do botão de confirmar do modal de Pendencia
	 */
	public String gravarMotivoPendencia() {
		try {
			String nomeMicrocomputador = super.getEnderecoRedeHostRemoto();
			ambulatorioFacade.gravarAnamneseMotivoPendente(consultaSelecionada.getNumero(), anaSeq, motivoPendencia, nomeMicrocomputador);
			closeDialog("modalPendenteWG");
			List<DocumentosPacienteVO> listaDocumentosPaciente = ambulatorioFacade.obterListaDocumentosPaciente(consultaSelecionada.getNumero(), null, true);
			if (!listaDocumentosPaciente.isEmpty()) {
				pesquisarPacientesAgendadosController.setListaDocumentosPaciente(listaDocumentosPaciente);
				pesquisarPacientesAgendadosController.setFlagModalImprimir(Boolean.TRUE);
				openDialog("modalFinalizarAtendimentoWG");
				return null;
			} else {
				titleAccordion = null;
				listaBotoes = null;
				cagSeq = null;
				consultaSelecionada = null;
				anaSeq = null;		
				executouIniciar = false;
				pesquisarPacientesAgendadosController.pesquisar();
				return PAGE_PESQUISAR_PACIENTES_AGENDADOS;
			}
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		} catch (UnknownHostException e) {
			apresentarMsgNegocio(Severity.ERROR, e.getMessage());
		}
	    return null;
	}

	public String getProntuarioFormatado() {
		return CoreUtil.formataProntuarioRelatorio(paciente.getProntuario());
	}
	
	/**
	 * Ação do botão Gravar do painel de Notas Adicionais
	 */
	public void inserirNotaAdicionalAnamnese() {
		try {
			if (descrNotaAdicionalAnamnese != null) {
				if (descrNotaAdicionalAnamnese.length() <= 4000) {
					if (modoInsercaoNotaAdicional) {
						notasAdicionaisAnamnesesList.add(this.ambulatorioFacade.inserirNotaAdicionalAnamneses(descrNotaAdicionalAnamnese, this.consultaSelecionada));
					} else {
						this.ambulatorioFacade.editarNotaAdicionalAnamneses(notaAdicionalAnamneses, descrNotaAdicionalAnamnese, this.consultaSelecionada);
					}
					notasAdicionaisAnamnesesList = this.ambulatorioFacade.obterNotaAdicionalAnamnesesConsulta(this.consultaSelecionada.getNumero());
					apresentarMsgNegocio(Severity.INFO, "MSG_ATENDER_PACIENTES_AGENDADOS_REGISTRO_SALVO");
					descrNotaAdicionalAnamnese = null;
					modoInsercaoNotaAdicional = true;
				} else {
					apresentarMsgNegocio(Severity.ERROR, "MENSAGEM_VALIDACAO_DESCRICAO_ITEM_ANAMNESE");
				}
			} else {
				apresentarMsgNegocio(Severity.ERROR, "MENSAGEM_OBRIGATORIEDADE_CAMPO_NOTA_ADICIONAL");
			}
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
			return;
		}
	}

	/**
	 * Ação do link Editar da grid de Notas Adicionais
	 */
	public void editarNotaAdicionalAnamnese(MamNotaAdicionalAnamneses notaAdicional) {
		modoInsercaoNotaAdicional = false;
		notaAdicionalAnamneses = notaAdicional;
		descrNotaAdicionalAnamnese = notaAdicional.getDescricao();
	}
	
	/**
	 * Ação do botão Cancelar do panel de Notas Adicionais
	 */
	public void limparNotaAdicionalAnamnese() {
		modoInsercaoNotaAdicional = true;
		notaAdicionalAnamneses = null;
		descrNotaAdicionalAnamnese = null;
	}

	/**
	 * Ação do botão Confirmar do modal de exclusão de Notas Adicionais
	 */
	public void excluirNotaAdicionalAnamnese() {
		try {
			this.ambulatorioFacade.excluiNotaAdicionalAnamnese(notaAdicionalAnamneses);
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
			return;
		}
		apresentarMsgNegocio(Severity.INFO, "MSG_ATENDER_PACIENTES_AGENDADOS_REGISTRO_EXCLUIDO");
		notasAdicionaisAnamnesesList = this.ambulatorioFacade.obterNotaAdicionalAnamnesesConsulta(this.consultaSelecionada.getNumero());
		notaAdicionalAnamneses = null;
		descrNotaAdicionalAnamnese = null;
		modoInsercaoNotaAdicional = true;
	}
	
	/**
	 * Redireciona para a pagina de Solicitação de Exames
	 */
	public String solicitarExames(){
		return PAGE_SOLICITACAO_EXAME_CRUD;
	}
	
	public Boolean verificarModuloExameAtivo() {
		return cascaFacade.verificarSeModuloEstaAtivo(DominioModulo.EXAMES_LAUDOS.getDescricao());
	}
	
	public Boolean verificarGravarSolicitacaoExame() {
		try {
			return this.ambulatorioFacade.validarProcessoExecutaAnamnese();
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
			return false;
		}
	}
	
	public String efetuarPrescricaoAmbulatorial(){
		Long unfPmeInf = aghuFacade.pesquisarUnidadesFuncionaisPorCodigoDescricaoECaracteristicasCount(
				atendimento.getUnidadeFuncional().getSeq(), null, Boolean.TRUE, Boolean.FALSE, Boolean.TRUE,
				ConstanteAghCaractUnidFuncionais.PME_INFORMATIZADA);
		if (unfPmeInf > 0) {
			return PAGE_VERIFICA_PRESCRICAO_MEDICA;
		} else {
			apresentarMsgNegocio(Severity.ERROR, "UNIDADE_FUNCIONAL_NAO_POSSUI_CARACTERISTICA", 
					ConstanteAghCaractUnidFuncionais.PME_INFORMATIZADA.getDescricao());
		}
		return null;
	}
	
	public Boolean verificarPrescricaoAmbulatorialAtiva() {
		return ambulatorioFacade.pesquisarAtendimentoParaPrescricaoMedica(consultaSelecionada.getPaciente().getCodigo(), null).contains(atendimento);
	}

	/**
	 * 
	 * Getters and Setters
	 * 
	 */
	public Integer getSelectedTab() {
		return selectedTab;
	}

	public void setSelectedTab(Integer selectedTab) {
		this.selectedTab = selectedTab;
	}

	public void setReadonlyAnamnese(Boolean readonlyAnamnese) {
		this.readonlyAnamnese = readonlyAnamnese;
	}

	public Boolean getReadonlyAnamnese() {
		return readonlyAnamnese;
	}

	public Boolean getHabilitaAnamnese() {
		return habilitaAnamnese;
	}

	public void setHabilitaAnamnese(Boolean habilitaAnamnese) {
		this.habilitaAnamnese = habilitaAnamnese;
	}

	public void setSliderAtual(Integer sliderAtual) {
		this.sliderAtual = sliderAtual;
	}

	public Integer getSliderAtual() {
		return sliderAtual;
	}

	public MpmPrescricaoMedica getPrescricaoMedica() {
		return prescricaoMedica;
	}

	public void setPrescricaoMedica(MpmPrescricaoMedica prescricaoMedica) {
		this.prescricaoMedica = prescricaoMedica;
	}

	public AghEspecialidades getEspecialidade() {
		return especialidade;
	}

	public void setEspecialidade(AghEspecialidades especialidade) {
		this.especialidade = especialidade;
	}

	public AipPacientes getPaciente() {
		return paciente;
	}

	public void setPaciente(AipPacientes paciente) {
		this.paciente = paciente;
	}

	public String getCameFrom() {
		return cameFrom;
	}

	public void setCameFrom(String cameFrom) {
		this.cameFrom = cameFrom;
	}

	public AacConsultas getConsultaSelecionada() {
		return consultaSelecionada;
	}

	public void setConsultaSelecionada(AacConsultas consultaSelecionada) {
		this.consultaSelecionada = consultaSelecionada;
	}

	public String getTitleAccordion() {
		return titleAccordion;
	}

	public void setTitleAccordion(String titleAccordion) {
		this.titleAccordion = titleAccordion;
	}

	public Integer getCagSeq() {
		return cagSeq;
	}

	public void setCagSeq(Integer cagSeq) {
		this.cagSeq = cagSeq;
	}

	public List<AnamneseVO> getListaBotoes() {
		return listaBotoes;
	}

	public void setListaBotoes(List<AnamneseVO> listaBotoes) {
		this.listaBotoes = listaBotoes;
	}

	public Boolean getPermiteColar() {
		return permiteColar;
	}

	public void setPermiteColar(Boolean permiteColar) {
		this.permiteColar = permiteColar;
	}

	public String getIdadeFormatada() {
		return idadeFormatada;
	}

	public void setIdadeFormatada(String idadeFormatada) {
		this.idadeFormatada = idadeFormatada;
	}

	public Long getAnaSeq() {
		return anaSeq;
	}

	public void setAnaSeq(Long anaSeq) {
		this.anaSeq = anaSeq;
	}

	public String getMotivoPendencia() {
		return motivoPendencia;
	}

	public void setMotivoPendencia(String motivoPendencia) {
		this.motivoPendencia = motivoPendencia;
	}

	public boolean isExecutouIniciar() {
		return executouIniciar;
	}

	public void setExecutouIniciar(boolean executouIniciar) {
		this.executouIniciar = executouIniciar;
	}

	public List<MamNotaAdicionalAnamneses> getNotasAdicionaisAnamnesesList() {
		return notasAdicionaisAnamnesesList;
	}

	public void setNotasAdicionaisAnamnesesList(List<MamNotaAdicionalAnamneses> notasAdicionaisAnamnesesList) {
		this.notasAdicionaisAnamnesesList = notasAdicionaisAnamnesesList;
	}

	public String getDescrNotaAdicionalAnamnese() {
		return descrNotaAdicionalAnamnese;
	}

	public void setDescrNotaAdicionalAnamnese(String descrNotaAdicionalAnamnese) {
		this.descrNotaAdicionalAnamnese = descrNotaAdicionalAnamnese;
	}

	public Boolean getModoInsercaoNotaAdicional() {
		return modoInsercaoNotaAdicional;
	}

	public void setModoInsercaoNotaAdicional(Boolean modoInsercaoNotaAdicional) {
		this.modoInsercaoNotaAdicional = modoInsercaoNotaAdicional;
	}

	public MamNotaAdicionalAnamneses getNotaAdicionalAnamneses() {
		return notaAdicionalAnamneses;
	}

	public void setNotaAdicionalAnamneses(MamNotaAdicionalAnamneses notaAdicionalAnamneses) {
		this.notaAdicionalAnamneses = notaAdicionalAnamneses;
	}

	public RapServidores getServidorLogadoSemFimVinculo() {
		return servidorLogadoSemFimVinculo;
	}

	public void setServidorLogadoSemFimVinculo(RapServidores servidorLogadoSemFimVinculo) {
		this.servidorLogadoSemFimVinculo = servidorLogadoSemFimVinculo;
	}

	public AghAtendimentos getAtendimento() {
		return atendimento;
	}

	public void setAtendimento(AghAtendimentos atendimento) {
		this.atendimento = atendimento;
	}
}
