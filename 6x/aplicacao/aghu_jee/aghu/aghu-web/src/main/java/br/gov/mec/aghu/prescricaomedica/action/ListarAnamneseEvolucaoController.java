package br.gov.mec.aghu.prescricaomedica.action;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import org.apache.commons.lang3.time.DateUtils;

import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.commons.WebUtil;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.dominio.DominioIndPendenteAmbulatorio;
import br.gov.mec.aghu.dominio.DominioSituacaoAnamnese;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.AinInternacao;
import br.gov.mec.aghu.model.MpmAnamneses;
import br.gov.mec.aghu.model.MpmEvolucoes;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.paciente.business.IPacienteFacade;
import br.gov.mec.aghu.prescricaomedica.anamneseevolucao.action.CabecalhoAnamneseEvolucaoController;
import br.gov.mec.aghu.prescricaomedica.anamneseevolucao.action.ManterAnamneseController;
import br.gov.mec.aghu.prescricaomedica.anamneseevolucao.action.ManterAnamneseEvolucaoController;
import br.gov.mec.aghu.prescricaomedica.anamneseevolucao.action.RelatorioAnamnesePacienteController;
import br.gov.mec.aghu.prescricaomedica.anamneseevolucao.action.RelatorioEvolucoesPacienteController;
import br.gov.mec.aghu.prescricaomedica.business.IPrescricaoMedicaFacade;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;


public class ListarAnamneseEvolucaoController extends ActionController {

	@PostConstruct
	protected void inicializar(){
	 this.begin(conversation);
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 5980768760902420585L;

	@EJB
	private IPrescricaoMedicaFacade prescricaoMedicaFacade;

	@EJB
	private IPacienteFacade pacienteFacade;

	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;

	@Inject
	private CabecalhoAnamneseEvolucaoController cabecalhoAnamneseEvolucaoController;
	
	@Inject
	private ManterAnamneseController manterAnamneseController;

	@Inject
	private ManterAnamneseEvolucaoController manterAnamneseEvolucaoController;	

	@Inject
	private RelatorioEvolucoesPacienteController relatorioEvolucoesPacienteController;

	@Inject
	private RelatorioAnamnesePacienteController relatorioAnamnesePacienteController;

	
	
	private List<MpmEvolucoes> evolucoes;
	private List<MpmAnamneses> anamneses;

	private Integer atdSeq;
	private Integer codPaciente;
	private Date dataReferencia;
	private Boolean atendimentoComAnamneseConcluida;
	private Boolean habilitaBotaoEvolucao;

	private MpmAnamneses anamnese;
	private MpmEvolucoes evolucao;
	private AghAtendimentos atendimento;
	private AinInternacao internacao;

	private String selectedTab;
	private String voltarPara;

	private Long seqAnamnese;
	private Long seqEvolucao;

	private Boolean showModalAnamnese;
	private Boolean showModalEvolucao;
	private Boolean showModalAdiantarEvolucao;

	private String mensagemModal;
	
	private Date dataInicioEvolucao;
	private Date dataFimEvolucao;
	
	private String paginaChamadora;
	
	private Boolean somenteLeitura;

	private static final String ABA_2 = "aba2";
	private static final String ABA_3 = "aba3";
	private static final String MANTER_EVOLUCAO_ANAMNESE = "prescricaomedica-manterAnamneseEvolucao";
	private static final String INCLUIR_NOTA_ADICIONAL_ANAMNESE = "prescricaomedica-manterAnamneseEvolucao";
	private static final String INCLUIR_NOTA_ADICIONAL_EVOLUCAO = "prescricaomedica-manterAnamneseEvolucao";
	private static final String REDIRECIONA_LISTA_PACIENTES_INTERNADOS = "prescricaomedica-pesquisarListaPacientesInternados";

	public void inicio() {
		this.manterAnamneseEvolucaoController.setIncluirNotaAdicionalEvolucao(false);
		this.manterAnamneseEvolucaoController.setIncluirNotaAdicionalAnamneses(false);
		this.manterAnamneseEvolucaoController.setIndexSelecionadoAnamnese(0);
		this.manterAnamneseEvolucaoController.setIndexSelecionadoEvolucao(0);
		
		if (this.atdSeq != null) {
			limparCampos();
			getCarregarCabecalho();
			getCarregarAtendimento();
			getCarregarAnamneseInternacao();
			getCarregarEvolucoes();
			getCarregarDataReferencia();
			verificarStatusBotaoEvolucao();
		}
	}
	
	public void filtrarPeriodoEvolucao() {
		getCarregarEvolucoes();
	}

	private void limparCampos() {
		this.showModalAnamnese = Boolean.FALSE;
		this.showModalEvolucao = Boolean.FALSE;
		this.showModalAdiantarEvolucao = Boolean.FALSE;
		this.seqAnamnese = null;
		this.seqEvolucao = null;
		this.selectedTab = null;
	}

	private void getCarregarCabecalho() {
		this.cabecalhoAnamneseEvolucaoController.setSeqAtendimento(this.atdSeq);
		this.cabecalhoAnamneseEvolucaoController.iniciar();
	}

	private void getCarregarAnamneseInternacao() {
		this.anamnese = this.prescricaoMedicaFacade.obterAnamneseAtendimento(this.atdSeq);

		this.anamneses = null;
		// Adiciona anamnese na lista
		if (this.anamnese != null) {
			this.anamneses = new ArrayList<MpmAnamneses>();
			internacao = this.atendimento.getInternacao();
			this.anamneses.add(anamnese);
		}

		this.atendimentoComAnamneseConcluida = this.prescricaoMedicaFacade.existeAnamneseValidaParaAtendimento(this.atdSeq);
		
	}

	private void getCarregarDataReferencia() {
		try {
			this.dataReferencia = this.prescricaoMedicaFacade.obterDataReferenciaEvolucao(this.atendimento);
		} catch (ApplicationBusinessException e) {
			this.apresentarExcecaoNegocio(e);
		}
	}

	private void getCarregarAtendimento() {
		this.atendimento = this.pacienteFacade.obterAtendimento(this.atdSeq);
		setCodPaciente(atendimento.getPaciente().getCodigo());
	}

	private void getCarregarEvolucoes() {

		List<DominioIndPendenteAmbulatorio> situacoes = new ArrayList<DominioIndPendenteAmbulatorio>();
		situacoes.add(DominioIndPendenteAmbulatorio.V);
		situacoes.add(DominioIndPendenteAmbulatorio.P);
		situacoes.add(DominioIndPendenteAmbulatorio.R);

		this.evolucoes = null;
		if (this.atendimentoComAnamneseConcluida) {
			this.evolucoes = this.prescricaoMedicaFacade.obterEvolucoesAnamnese(
					this.anamnese, situacoes, this.dataInicioEvolucao, this.dataFimEvolucao);
		}
	}

	private void verificarStatusBotaoEvolucao() {

		this.habilitaBotaoEvolucao = Boolean.FALSE;

		List<DominioIndPendenteAmbulatorio> situacoes = new ArrayList<DominioIndPendenteAmbulatorio>();
		situacoes.add(DominioIndPendenteAmbulatorio.P);
		situacoes.add(DominioIndPendenteAmbulatorio.R);

		if (this.atendimentoComAnamneseConcluida) {
			List<MpmEvolucoes> evolucoesNaoConcluidas = this.prescricaoMedicaFacade.obterEvolucoesAnamnese(this.anamnese, new Date(), situacoes);
			if (evolucoesNaoConcluidas.isEmpty()) {
				this.habilitaBotaoEvolucao = Boolean.TRUE;
			}
		}
	}

	public String verificarAdiantamentoEvolucao() {

		this.showModalAdiantarEvolucao = Boolean.FALSE;

		if (this.atendimentoComAnamneseConcluida && this.prescricaoMedicaFacade.verificarAdiantamentoEvolucao(atendimento, new Date())) {
			this.showModalAdiantarEvolucao = Boolean.TRUE;
			return null;
		}

		return verificaNovaEvolucaoAnamnese();
	}
	
	public String cancelar() throws ApplicationBusinessException {
		this.limparCampos();
		if(paginaChamadora != null){
			if ("prescricaomedica-pesquisarListaPacientesInternados".equalsIgnoreCase(paginaChamadora)) {
				return REDIRECIONA_LISTA_PACIENTES_INTERNADOS;
			}  
		}
		return voltarPara;
	}	

	public String adiantarEvolucao() {

		this.dataReferencia = DateUtils.addDays(dataReferencia, 1);

		return verificaNovaEvolucaoAnamnese();
	}

	public String verificaNovaEvolucaoAnamnese() {

		try {
			if (this.atendimentoComAnamneseConcluida) {
				criarEvolucao();
				this.seqAnamnese = this.anamnese.getSeq();
				this.seqEvolucao = this.evolucao.getSeq();
				this.selectedTab = ABA_3;
			} else {
				// Anamese nao existe ou nao concluida
				// verifica estado atual da anamnese do atendimento
				this.anamnese = prescricaoMedicaFacade.obterAnamneseAtendimento(atdSeq);

				if (this.anamnese == null) {
					criarAnamneseEvolucao();
				}

				this.seqAnamnese = this.anamnese.getSeq();
				this.selectedTab = ABA_2;
			}
			setarParametrosAnamneseEvolucao();
			return MANTER_EVOLUCAO_ANAMNESE;
		} catch (BaseException e) {
			super.apresentarExcecaoNegocio(e);
		}

		return null;
	}

	public Boolean verificaEdicaoEvolucao(MpmEvolucoes evolucao) {
		if (!evolucao.getPendente().equals(DominioIndPendenteAmbulatorio.V)) {
			return Boolean.TRUE;
		}

		return Boolean.FALSE;
	}

	public Boolean verificaEdicaoAnamnese() {
		if (this.anamnese != null && !this.anamnese.getPendente().equals(DominioIndPendenteAmbulatorio.V)) {
			return Boolean.TRUE;
		}
		return Boolean.FALSE;
	}

	public String editarAnamnese() {

		if (this.anamnese != null && this.anamnese.getSituacao().equals(DominioSituacaoAnamnese.U)) {
			anamnese = prescricaoMedicaFacade.obterAnamneseDetalhamento(anamnese);
			this.showModalAnamnese = Boolean.TRUE;
			String data = new SimpleDateFormat("dd/MM/yyyy HH:mm").format(anamnese.getDthrCriacao());
			String usuario = anamnese.getAtendimento().getServidor().getUsuario();
			this.mensagemModal = "Anamnese em movimentação em " + data + 
					" por " + usuario + ". Deseja atualizá-la mesmo assim?";
			return null;
		}

		return editarAnamneseEmUso();
	}

	public String editarAnamneseEmUso() {
		this.seqAnamnese = this.anamnese.getSeq();
		this.selectedTab = ABA_2;
		setarParametrosAnamneseEvolucao();
		return MANTER_EVOLUCAO_ANAMNESE;
	}

	public String visualizarAnamnese() {
		this.seqAnamnese = this.anamnese.getSeq();
		this.selectedTab = ABA_2;
		somenteLeitura = true;
		setarParametrosAnamneseEvolucao();
		return MANTER_EVOLUCAO_ANAMNESE;
	}
	
	private void criarEvolucao() throws ApplicationBusinessException, ApplicationBusinessException {
		RapServidores servidorLogado = registroColaboradorFacade.obterServidorAtivoPorUsuario(obterLoginUsuarioLogado());
		this.evolucao = this.prescricaoMedicaFacade.criarEvolucao(this.anamnese, this.dataReferencia, servidorLogado);
	}

	public String criarEvolucaoComDescricao() throws ApplicationBusinessException, ApplicationBusinessException {
		RapServidores servidorLogado = registroColaboradorFacade.obterServidorAtivoPorUsuario(obterLoginUsuarioLogado());
		this.evolucao = this.prescricaoMedicaFacade.criarMpmEvolucaoComDescricao(this.evolucao.getDescricao(), this.anamnese, this.dataReferencia, servidorLogado);
		return visualizarEvolucao(this.evolucao);
	}

	private void criarAnamneseEvolucao() {
		RapServidores servidorLogado;
		try {
			servidorLogado = registroColaboradorFacade.obterServidorAtivoPorUsuario(obterLoginUsuarioLogado());
			this.anamnese = this.prescricaoMedicaFacade.criarAnamnese(atendimento, servidorLogado);
		} catch (ApplicationBusinessException e) {
			this.apresentarExcecaoNegocio(e);
		}
	}

	public String editarEvolucao(MpmEvolucoes evolucao) {
		this.setEvolucao(evolucao);
		try {
			this.prescricaoMedicaFacade.validarEvolucaoEmUso(evolucao);
		} catch (ApplicationBusinessException e) {
			this.mensagemModal = WebUtil.initLocalizedMessage(e.getMessage(), null, e.getParameters());
			this.showModalEvolucao = Boolean.TRUE;
			return null;
		}
		return editarEvolucaoEmUso();
	}

	public String editarEvolucaoEmUso() {
		try {
			RapServidores servidorLogado = registroColaboradorFacade.obterServidorAtivoPorUsuario(obterLoginUsuarioLogado());
			this.prescricaoMedicaFacade.atualizarMpmEvolucaoEmUso(this.evolucao, servidorLogado);
		} catch (ApplicationBusinessException e) {
			this.apresentarExcecaoNegocio(e);
		}
		this.seqAnamnese = this.anamnese.getSeq();
		this.seqEvolucao = this.evolucao.getSeq();
		this.selectedTab = ABA_3;
		setarParametrosAnamneseEvolucao();
		return MANTER_EVOLUCAO_ANAMNESE;
	}
	
	public void setarParametrosAnamneseEvolucao() {
		this.manterAnamneseController.setSeqAnamnese(this.anamnese.getSeq());
		this.manterAnamneseEvolucaoController.setSeqAtendimento(this.atdSeq);
		this.manterAnamneseEvolucaoController.setSeqAnamnese(this.seqAnamnese);
		this.manterAnamneseEvolucaoController.setSeqEvolucao(this.seqEvolucao);
		this.manterAnamneseEvolucaoController.setSelectedTab(this.selectedTab);
	}
	
	public String visualizarEvolucao(MpmEvolucoes evolucao) {
		this.setEvolucao(evolucao);
		this.seqAnamnese = this.anamnese.getSeq();
		this.seqEvolucao = this.evolucao.getSeq();
		this.selectedTab = ABA_3;
		somenteLeitura = true;
		setarParametrosAnamneseEvolucao();
		return MANTER_EVOLUCAO_ANAMNESE;
	}

	public boolean habilitarIncluirNotaAdicionalAnamnese(MpmAnamneses anamnese) {
		return this.anamnese != null && this.anamnese.getPendente().equals(DominioIndPendenteAmbulatorio.V);
	}

	public boolean habilitarIncluirNotaAdicionalEvolucao(MpmEvolucoes evolucao) {
		return evolucao.getPendente().equals(DominioIndPendenteAmbulatorio.V);
	}

	public String incluirNotaAdicioanalAnamnese() {
		this.seqAnamnese = this.anamnese.getSeq();
		this.selectedTab = ABA_2;
		this.manterAnamneseEvolucaoController.setSeqAtendimento(this.atdSeq);
		this.manterAnamneseEvolucaoController.setSeqAnamnese(this.seqAnamnese);
		this.manterAnamneseEvolucaoController.setSeqEvolucao(this.seqEvolucao);
		this.manterAnamneseEvolucaoController.setSelectedTab(this.selectedTab);
		setarParametrosAnamneseEvolucao();
		this.manterAnamneseEvolucaoController.setIncluirNotaAdicionalAnamneses(true);
		return INCLUIR_NOTA_ADICIONAL_ANAMNESE;
	}

	public String incluirNotaAdicioanalEvolucao(MpmEvolucoes evolucao) {
		this.seqEvolucao = evolucao.getSeq();
		this.seqAnamnese = this.anamnese.getSeq();
		this.selectedTab = ABA_3;
		setarParametrosAnamneseEvolucao();
		this.manterAnamneseEvolucaoController.setIncluirNotaAdicionalEvolucao(true);
		return INCLUIR_NOTA_ADICIONAL_EVOLUCAO;
	}

	public void imprimirEvolucao(MpmEvolucoes evolucao) {
		this.relatorioEvolucoesPacienteController.imprimirEvolucao(evolucao.getSeq());
		this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_IMPRESSAO");
	}

	public void imprimirAnamnese() {
		this.relatorioAnamnesePacienteController.setSeqAnamnese(this.anamnese.getSeq());
		this.relatorioAnamnesePacienteController.directPrint();
	}

	public String voltar() {
		return "voltar";
	}

	public List<MpmEvolucoes> getEvolucoes() {
		return evolucoes;
	}

	public void setEvolucoes(List<MpmEvolucoes> evolucoes) {
		this.evolucoes = evolucoes;
	}

	public Integer getAtdSeq() {
		return atdSeq;
	}

	public void setAtdSeq(Integer atdSeq) {
		this.atdSeq = atdSeq;
	}

	public Date getDataReferencia() {
		return dataReferencia;
	}

	public void setDataReferencia(Date dataReferencia) {
		this.dataReferencia = dataReferencia;
	}

	public Boolean getAtendimentoComAnamneseConcluida() {
		return atendimentoComAnamneseConcluida;
	}

	public void setAtendimentoComAnamneseConcluida(Boolean atendimentoComAnamneseConcluida) {
		this.atendimentoComAnamneseConcluida = atendimentoComAnamneseConcluida;
	}

	public Boolean getHabilitaBotaoEvolucao() {
		return habilitaBotaoEvolucao;
	}

	public void setHabilitaBotaoEvolucao(Boolean habilitaBotaoEvolucao) {
		this.habilitaBotaoEvolucao = habilitaBotaoEvolucao;
	}

	public MpmEvolucoes getEvolucao() {
		return evolucao;
	}

	public void setEvolucao(MpmEvolucoes evolucao) {
		this.evolucao = evolucao;
	}

	public String getSelectedTab() {
		return selectedTab;
	}

	public void setSelectedTab(String selectedTab) {
		this.selectedTab = selectedTab;
	}

	public Long getSeqAnamnese() {
		return seqAnamnese;
	}

	public void setSeqAnamnese(Long seqAnamnese) {
		this.seqAnamnese = seqAnamnese;
	}

	public Long getSeqEvolucao() {
		return seqEvolucao;
	}

	public void setSeqEvolucao(Long seqEvolucao) {
		this.seqEvolucao = seqEvolucao;
	}

	public String getVoltarPara() {
		return voltarPara;
	}

	public void setVoltarPara(String voltarPara) {
		this.voltarPara = voltarPara;
	}

	public Boolean getShowModalAnamnese() {
		return showModalAnamnese;
	}

	public void setShowModalAnamnese(Boolean showModalAnamnese) {
		this.showModalAnamnese = showModalAnamnese;
	}

	public Boolean getShowModalEvolucao() {
		return showModalEvolucao;
	}

	public void setShowModalEvolucao(Boolean showModalEvolucao) {
		this.showModalEvolucao = showModalEvolucao;
	}

	public String getMensagemModal() {
		return mensagemModal;
	}

	public void setMensagemModal(String mensagemModal) {
		this.mensagemModal = mensagemModal;
	}

	public List<MpmAnamneses> getAnamneses() {
		return anamneses;
	}

	public void setAnamneses(List<MpmAnamneses> anamneses) {
		this.anamneses = anamneses;
	}

	public Boolean getShowModalAdiantarEvolucao() {
		return showModalAdiantarEvolucao;
	}

	public void setShowModalAdiantarEvolucao(Boolean showModalAdiantarEvolucao) {
		this.showModalAdiantarEvolucao = showModalAdiantarEvolucao;
	}

	public String getPaginaChamadora() {
		return paginaChamadora;
	}

	public void setPaginaChamadora(String paginaChamadora) {
		this.paginaChamadora = paginaChamadora;
	}

	public AinInternacao getInternacao() {
		return internacao;
	}

	public void setInternacao(AinInternacao internacao) {
		this.internacao = internacao;
	}

	public MpmAnamneses getAnamnese() {
		return anamnese;
	}

	public void setAnamnese(MpmAnamneses anamnese) {
		this.anamnese = anamnese;
	}
	
	public Integer getCodPaciente() {
		return codPaciente;
	}

	public void setCodPaciente(Integer codPaciente) {
		this.codPaciente = codPaciente;
	}

	public Boolean getSomenteLeitura() {
		return somenteLeitura;
	}

	public void setSomenteLeitura(Boolean somenteLeitura) {
		this.somenteLeitura = somenteLeitura;
	}

	public Date getDataInicioEvolucao() {
		return dataInicioEvolucao;
	}

	public void setDataInicioEvolucao(Date dataInicioEvolucao) {
		this.dataInicioEvolucao = dataInicioEvolucao;
	}

	public Date getDataFimEvolucao() {
		return dataFimEvolucao;
	}

	public void setDataFimEvolucao(Date dataFimEvolucao) {
		this.dataFimEvolucao = dataFimEvolucao;
	}


}
