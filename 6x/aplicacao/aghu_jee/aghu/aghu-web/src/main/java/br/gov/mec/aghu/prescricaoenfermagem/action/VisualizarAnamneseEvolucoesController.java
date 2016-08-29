package br.gov.mec.aghu.prescricaoenfermagem.action;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.ejb.EJB;

import br.gov.mec.aghu.core.exception.Severity;

import br.gov.mec.aghu.core.action.ActionController;
import javax.annotation.PostConstruct;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.business.prescricaoenfermagem.IPrescricaoEnfermagemFacade;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.MpmAnamneses;
import br.gov.mec.aghu.model.MpmEvolucoes;
import br.gov.mec.aghu.model.MpmNotaAdicionalAnamneses;
import br.gov.mec.aghu.model.MpmNotaAdicionalEvolucoes;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.paciente.business.IPacienteFacade;
import br.gov.mec.aghu.prescricaomedica.anamneseevolucao.action.RelatorioAnamnesePacienteController;
import br.gov.mec.aghu.prescricaomedica.anamneseevolucao.action.RelatorioEvolucoesPacienteController;
import br.gov.mec.aghu.prescricaomedica.business.IPrescricaoMedicaFacade;
import br.gov.mec.aghu.core.utils.DateFormatUtil;
import br.gov.mec.aghu.core.utils.DateUtil;


public class VisualizarAnamneseEvolucoesController extends ActionController {

	@PostConstruct
	protected void inicializar(){
	 this.begin(conversation);
	}

	private static final Log LOG = LogFactory.getLog(VisualizarAnamneseEvolucoesController.class);
	private static final long serialVersionUID = -1123456765786L;

	@EJB
	private IPacienteFacade pacienteFacade;

	@EJB
	private IAghuFacade aghuFacade;

	@EJB
	private IPrescricaoEnfermagemFacade prescricaoEnfermagemFacade;

	@EJB
	private IPrescricaoMedicaFacade prescricaoMedicaFacade;

	@EJB
	private RelatorioEvolucoesPacienteController relatorioEvolucoesPacienteController;

	@EJB
	private RelatorioAnamnesePacienteController relatorioAnamnesePacienteController;

	private Integer codigoPaciente;
	private Integer prontuario;
	private AipPacientes paciente;
	private AghAtendimentos atendimento;
	private MpmAnamneses anamnese;
	private MpmEvolucoes evolucao;
	private List<MpmEvolucoes> listaEvolucoes;
	private List<AghAtendimentos> listaAtendimentos;
	private List<MpmNotaAdicionalAnamneses> notasAdicionaisAnamnese;
	private MpmNotaAdicionalAnamneses notaAdicionalSelecionada;
	private String dataReferencia;
	private List<String> listaDataReferencia;
	private List<MpmNotaAdicionalEvolucoes> listaNotaAdicionalEvolucoes;
	private MpmNotaAdicionalEvolucoes notaAdicionalEvolucaoSelecionada;

	private List<RapServidores> listaProfissionais;
	private RapServidores profissional;

	public void iniciar() {
		if (codigoPaciente != null || prontuario != null) {
			if (codigoPaciente != null) {
				paciente = pacienteFacade.obterPacientePorCodigo(codigoPaciente);
			} else if (prontuario != null) {
				paciente = pacienteFacade.obterPacientePorProntuario(prontuario);
			}
		}
		setarCamposAtendimento();
		carregarAnamnese();
		carregarNotasAdicionaisAnamnese();
	}

	public void setarCamposAtendimento() {
		if (paciente != null) {
			codigoPaciente = paciente.getCodigo();
			prontuario = paciente.getProntuario();
			try {
				atendimento = prescricaoEnfermagemFacade.obterUltimoAtendimentoEmAndamentoPorPaciente(codigoPaciente, paciente.getNome());
				carregarAnamnese();
			} catch (ApplicationBusinessException e) {
				limparCampos();
				apresentarExcecaoNegocio(e);
			}
		} else {
			limparCampos();
		}
	}

	public String getNomeEquipe() {
		if (atendimento != null && atendimento.getServidor() != null) {
			return aghuFacade.obterDescricaoEquipe(atendimento.getServidor().getId().getMatricula(), atendimento.getServidor().getId().getVinCodigo());
		}
		return "";
	}

	public void limparCampos() {
		paciente = null;
		codigoPaciente = null;
		prontuario = null;
		atendimento = null;
		limparPanelAnamnese();
		limparPanelEvolucoes();
	}

	private void limparPanelEvolucoes() {
		evolucao = null;
		listaDataReferencia = new ArrayList<String>();
		listaEvolucoes = null;
	}

	private void limparPanelAnamnese() {
		anamnese = null;
		notaAdicionalSelecionada = null;
		notasAdicionaisAnamnese = null;
	}

	public String redirecionarPesquisaFonetica() {
		return "pesquisaFonetica";
	}

	private void carregarAnamnese() {
		if (this.atendimento != null) {
			this.anamnese = prescricaoMedicaFacade.obterAnamneseValidaParaAtendimento(this.atendimento.getSeq());
			carregarNotasAdicionaisAnamnese();
			carregarListaEvolucoesAnamnese();
		}
	}

	private void carregarNotasAdicionaisAnamnese() {
		if (this.anamnese != null) {
			this.notasAdicionaisAnamnese = prescricaoMedicaFacade.listarNotasAdicionaisAnamnese(this.anamnese.getSeq());
		}

		if (this.notasAdicionaisAnamnese != null && !this.notasAdicionaisAnamnese.isEmpty()) {
			this.notaAdicionalSelecionada = this.notasAdicionaisAnamnese.get(0);
		}
	}

	public void selecionarNotaAdicional(MpmNotaAdicionalAnamneses notaAdicionalSelecionada) {
		this.notaAdicionalSelecionada = notaAdicionalSelecionada;
	}

	public String formatarDataNotaAdicional(Date data) {
		return DateUtil.obterDataFormatada(data, "dd/MM/yyyy HH:mm");
	}

	public void carregarListaEvolucoesAnamnese() {
		if (anamnese != null) {
			listaEvolucoes = prescricaoMedicaFacade.listarEvolucoesConcluidasAnamnese(anamnese.getSeq());
			if (listaEvolucoes != null && !listaEvolucoes.isEmpty()) {
				this.evolucao = this.listaEvolucoes.get(0);				
				listaDataReferencia = new ArrayList<String>();
				Set<RapServidores> profissionais = new HashSet<RapServidores>();
				for (MpmEvolucoes evolucao : listaEvolucoes) {
					String dataFormatada = DateFormatUtil.fomataDiaMesAno(evolucao.getDthrReferencia());
					if (!this.listaDataReferencia.contains(dataFormatada)) {
						listaDataReferencia.add(dataFormatada);
					}
					profissionais.add(evolucao.getServidor());
				}
				dataReferencia = listaDataReferencia.get(0);
				listaProfissionais = new ArrayList<RapServidores>(profissionais);
			}
		}
	}

	public void marcarDataReferencia() {
		if (evolucao != null) {
			dataReferencia = DateUtil.dataToString(evolucao.getDthrReferencia(), "dd/MM/yyyy");
		}
	}

	private Date obterDataReferencia() {
		SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
		try {
			return new Date(format.parse(dataReferencia).getTime());
		} catch (ParseException e) {
			LOG.error("Erro ao convereter data.");
		}
		return null;
	}

	public void filtrarEvolucoes() {
		
		Date referencia = obterDataReferencia();
		List<MpmEvolucoes> evolucoes = prescricaoMedicaFacade.listarEvolucoesConcluidasAnamnese(anamnese.getSeq());
		listaEvolucoes.clear();

		for (MpmEvolucoes evolucao : evolucoes) {
			if (DateUtil.isDatasIguais(DateUtil.truncaData(referencia), DateUtil.truncaData(evolucao.getDthrReferencia()))) {
				if (profissional == null) {
					listaEvolucoes.add(evolucao);
				} else if (profissional.equals(evolucao.getServidor())) {
					listaEvolucoes.add(evolucao);
				}
			}
		}

		if (listaEvolucoes.isEmpty()) {
			this.evolucao = null;
		} else {
			this.evolucao = this.listaEvolucoes.get(0);
		}
	}

	public boolean verificarEvolucaoPossuiNotas(Long seqEvolucao) {
		return prescricaoMedicaFacade.possuiNotaAdicionalEvolucao(seqEvolucao);
	}

	public void carregarNotaAdicionalEvolucao(Long seqEvolucao) {
		listaNotaAdicionalEvolucoes = prescricaoMedicaFacade.listarNotasAdicionaisEvolucoes(seqEvolucao);
		if (listaNotaAdicionalEvolucoes != null && !listaNotaAdicionalEvolucoes.isEmpty()) {
			notaAdicionalEvolucaoSelecionada = listaNotaAdicionalEvolucoes.get(0);
		}
	}

	public void imprimirEvolucao(Long seqEvolucao) {
		this.relatorioEvolucoesPacienteController.imprimirEvolucao(seqEvolucao);
		this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_IMPRESSAO");
	}

	public void imprimirAnamnese() {
		this.relatorioAnamnesePacienteController.setSeqAnamnese(this.anamnese.getSeq());
		this.relatorioAnamnesePacienteController.directPrint();
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

	public AipPacientes getPaciente() {
		return paciente;
	}

	public void setPaciente(AipPacientes paciente) {
		this.paciente = paciente;
	}

	public AghAtendimentos getAtendimento() {
		return atendimento;
	}

	public void setAtendimento(AghAtendimentos atendimento) {
		this.atendimento = atendimento;
	}

	public MpmAnamneses getAnamnese() {
		return anamnese;
	}

	public void setAnamnese(MpmAnamneses anamnese) {
		this.anamnese = anamnese;
	}

	public MpmEvolucoes getEvolucao() {
		return evolucao;
	}

	public void setEvolucao(MpmEvolucoes evolucao) {
		this.evolucao = evolucao;
	}

	public List<MpmEvolucoes> getListaEvolucoes() {
		return listaEvolucoes;
	}

	public void setListaEvolucoes(List<MpmEvolucoes> listaEvolucoes) {
		this.listaEvolucoes = listaEvolucoes;
	}

	public void setListaAtendimentos(List<AghAtendimentos> listaAtendimentos) {
		this.listaAtendimentos = listaAtendimentos;
	}

	public List<AghAtendimentos> getListaAtendimentos() {
		return listaAtendimentos;
	}

	public List<MpmNotaAdicionalAnamneses> getNotasAdicionaisAnamnese() {
		return notasAdicionaisAnamnese;
	}

	public void setNotasAdicionaisAnamnese(List<MpmNotaAdicionalAnamneses> notasAdicionaisAnamnese) {
		this.notasAdicionaisAnamnese = notasAdicionaisAnamnese;
	}

	public MpmNotaAdicionalAnamneses getNotaAdicionalSelecionada() {
		return notaAdicionalSelecionada;
	}

	public void setNotaAdicionalSelecionada(MpmNotaAdicionalAnamneses notaAdicionalSelecionada) {
		this.notaAdicionalSelecionada = notaAdicionalSelecionada;
	}

	public String getDataReferencia() {
		return dataReferencia;
	}

	public void setDataReferencia(String dataReferencia) {
		this.dataReferencia = dataReferencia;
	}

	public List<String> getListaDataReferencia() {
		return listaDataReferencia;
	}

	public void setListaDataReferencia(List<String> listaDataReferencia) {
		this.listaDataReferencia = listaDataReferencia;
	}

	public List<MpmNotaAdicionalEvolucoes> getListaNotaAdicionalEvolucoes() {
		return listaNotaAdicionalEvolucoes;
	}

	public void setListaNotaAdicionalEvolucoes(List<MpmNotaAdicionalEvolucoes> listaNotaAdicionalEvolucoes) {
		this.listaNotaAdicionalEvolucoes = listaNotaAdicionalEvolucoes;
	}

	public MpmNotaAdicionalEvolucoes getNotaAdicionalEvolucaoSelecionada() {
		return notaAdicionalEvolucaoSelecionada;
	}

	public void setNotaAdicionalEvolucaoSelecionada(MpmNotaAdicionalEvolucoes notaAdicionalEvolucaoSelecionada) {
		this.notaAdicionalEvolucaoSelecionada = notaAdicionalEvolucaoSelecionada;
	}

	public List<RapServidores> getListaProfissionais() {
		return listaProfissionais;
	}

	public void setListaProfissionais(List<RapServidores> listaProfissionais) {
		this.listaProfissionais = listaProfissionais;
	}

	public RapServidores getProfissional() {
		return profissional;
	}

	public void setProfissional(RapServidores profissional) {
		this.profissional = profissional;
	}

}
