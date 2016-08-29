package br.gov.mec.aghu.controleinfeccao.action;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import br.gov.mec.aghu.controleinfeccao.business.IControleInfeccaoFacade;
import br.gov.mec.aghu.controleinfeccao.vo.BacteriaPacienteVO;
import br.gov.mec.aghu.controleinfeccao.vo.GMRPacienteVO;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.MciCriterioGmr;
import br.gov.mec.aghu.paciente.business.IPacienteFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.commons.CoreUtil;

/**
 * Controller de #37928 - Lista de Germes Multirresistentes do Paciente
 * 
 * @author aghu
 *
 */
public class ListaGMRPacienteController extends ActionController {

	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}

	private static final long serialVersionUID = 9132716167505181960L;

	private static final String DETALHES_PACIENTE = "controleinfeccao-detalharPacienteCCIH";

	@EJB
	private IPacienteFacade pacienteFacade;

	@EJB
	private IControleInfeccaoFacade controleInfeccaoFacade;

	/*
	 * Parâmetros da tela
	 */
	private Integer codigoPaciente;
	private String voltarPara = DETALHES_PACIENTE;

	/*
	 * Campos do Cabeçalho
	 */
	private AipPacientes paciente;
	private String prontuarioFormatado;
	private Date dataInternacao;
	private String leito;

	// Bactéria (suggestionBox)
	private BacteriaPacienteVO bacteria;

	// Resistência ao Antimicrobiano (comboBox)
	private MciCriterioGmr resistenciaAntimicrobianoSelecionada;

	// Lista principal
	private List<GMRPacienteVO> listaNotificacao;

	// Item selecionado na lista principal que será desativado
	private GMRPacienteVO notificacaoSelecionada;

	public void iniciar() {
		this.paciente = this.pacienteFacade.obterPaciente(this.codigoPaciente);
		this.prontuarioFormatado = CoreUtil.formataProntuario(this.paciente.getProntuario());
		this.dataInternacao = this.controleInfeccaoFacade.obterDataUltimaInternacaoPaciente(this.codigoPaciente); // P3
		this.leito = this.controleInfeccaoFacade.obterLocalPaciente(this.codigoPaciente); // P2
		pesquisar();
	}

	private void pesquisar() {
		this.listaNotificacao = this.controleInfeccaoFacade.pesquisarGermesMultirresistentesPaciente(this.paciente.getProntuario());
	}

	/**
	 * Pesquisa da suggestionBox de bactérias
	 * 
	 * @param param
	 * @return
	 */
	public List<BacteriaPacienteVO> pesquisarBacteria(final String parametro) {
		return this.returnSGWithCount(this.controleInfeccaoFacade.pesquisarGermesDisponiveisListaGMRPaciente(parametro), this.controleInfeccaoFacade.pesquisarGermesDisponiveisListaGMRPacienteCount(parametro));
	}

	/**
	 * C3: Lista antimicrobianos resistentes ao GMR (suggestionBox de bactérias)
	 * 
	 * @param seq
	 */
	public List<MciCriterioGmr> pesquisarAntimicrobianosResistentes() {
		List<MciCriterioGmr> retorno = new ArrayList<MciCriterioGmr>();
		if (this.bacteria != null) {
			retorno = this.controleInfeccaoFacade.pesquisarMciCriterioGrmAtivoPorBmrSeq(this.bacteria.getSeq());
		}
		return retorno;
	}

	/**
	 * RN2: Insere nova notificação
	 */
	public void adicionar() {
		final Integer ambSeq = this.resistenciaAntimicrobianoSelecionada != null ? this.resistenciaAntimicrobianoSelecionada.getId().getAmbSeq() : null;
		this.controleInfeccaoFacade.criarNotificacaoGMR(this.codigoPaciente, ambSeq, this.bacteria.getSeq());
		pesquisar();
		this.bacteria = null;
		this.resistenciaAntimicrobianoSelecionada = null;
	}

	/**
	 * RN3: Desativa notificação
	 */
	public void desativarNotificacao() {
		try {
			this.controleInfeccaoFacade.desativarNotificacao(this.notificacaoSelecionada.getSeq());
			pesquisar();
			apresentarMsgNegocio("MENSAGEM_NOTIFICACAO_GMR_DESATIVADA");
		} finally {
			this.notificacaoSelecionada = null;
		}
	}

	public void limpar() {
		this.codigoPaciente = null;
		this.paciente = null;
		this.prontuarioFormatado = null;
		this.bacteria = null;
		this.listaNotificacao = null;
		this.notificacaoSelecionada = null;
		this.resistenciaAntimicrobianoSelecionada = null;
	}

	private void limparParametros() {
		this.codigoPaciente = null;
		this.voltarPara = DETALHES_PACIENTE;
		this.paciente = null;
		this.prontuarioFormatado = null;
		this.dataInternacao = null;
		this.leito = null;
		this.bacteria = null;
		this.resistenciaAntimicrobianoSelecionada = null;
		this.listaNotificacao = null;
		this.notificacaoSelecionada = null;
	}

	public String voltar() {
		final String retorno = this.voltarPara;
		this.limparParametros();
		return retorno;
	}

	// Getters and Setters

	public Integer getCodigoPaciente() {
		return codigoPaciente;
	}

	public void setCodigoPaciente(Integer codigoPaciente) {
		this.codigoPaciente = codigoPaciente;
	}

	public AipPacientes getPaciente() {
		return paciente;
	}

	public void setPaciente(AipPacientes paciente) {
		this.paciente = paciente;
	}

	public String getProntuarioFormatado() {
		return prontuarioFormatado;
	}

	public void setProntuarioFormatado(String prontuarioFormatado) {
		this.prontuarioFormatado = prontuarioFormatado;
	}

	public Date getDataInternacao() {
		return dataInternacao;
	}

	public void setDataInternacao(Date dataInternacao) {
		this.dataInternacao = dataInternacao;
	}

	public String getLeito() {
		return leito;
	}

	public void setLeito(String leito) {
		this.leito = leito;
	}

	public String getVoltarPara() {
		return voltarPara;
	}

	public void setVoltarPara(String voltarPara) {
		this.voltarPara = voltarPara;
	}

	public BacteriaPacienteVO getBacteria() {
		return bacteria;
	}

	public void setBacteria(BacteriaPacienteVO bacteria) {
		this.bacteria = bacteria;
	}

	public MciCriterioGmr getResistenciaAntimicrobianoSelecionada() {
		return resistenciaAntimicrobianoSelecionada;
	}

	public void setResistenciaAntimicrobianoSelecionada(MciCriterioGmr resistenciaAntimicrobianoSelecionada) {
		this.resistenciaAntimicrobianoSelecionada = resistenciaAntimicrobianoSelecionada;
	}

	public List<GMRPacienteVO> getListaNotificacao() {
		return listaNotificacao;
	}

	public void setListaNotificacao(List<GMRPacienteVO> listaNotificacao) {
		this.listaNotificacao = listaNotificacao;
	}

	public GMRPacienteVO getNotificacaoSelecionada() {
		return notificacaoSelecionada;
	}

	public void setNotificacaoSelecionada(GMRPacienteVO notificacaoSelecionada) {
		this.notificacaoSelecionada = notificacaoSelecionada;
	}
}