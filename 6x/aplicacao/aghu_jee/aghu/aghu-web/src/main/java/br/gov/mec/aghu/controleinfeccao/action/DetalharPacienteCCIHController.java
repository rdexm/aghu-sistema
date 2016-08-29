package br.gov.mec.aghu.controleinfeccao.action;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import br.gov.mec.aghu.blococirurgico.business.IBlocoCirurgicoFacade;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.controleinfeccao.business.IControleInfeccaoFacade;
import br.gov.mec.aghu.controleinfeccao.vo.NotificacoesGeraisVO;
import br.gov.mec.aghu.dominio.DominioPacAtendimento;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.paciente.business.IPacienteFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.utils.DateUtil;

public class DetalharPacienteCCIHController extends ActionController {

	private static final long serialVersionUID = -3785496128971620888L;
	
	private static final String DETALHES_PACIENTE = "controleinfeccao-detalharPacienteCCIH";

	private static final String LISTA_PACIENTES = "controleinfeccao-listaPacientes";

	private static final String FATOR_PREDISPONENTE = "controleinfeccao-notificacaoFatorPredisponente";

	private static final String NOTIFICACAO_TOPOGRAFIA = "controleinfeccao-notificacaoTopografia";

	private static final String PROCEDIMENTO_RISCO = "controleinfeccao-notificacaoProcedimentoRisco";
	
	private static final String NOTIFICACAO_PREVENTIVA = "controleinfeccao-notificacaoMedidaPreventiva";
	
	private static final String PATOLOGIA_INFECCAO = "controleinfeccao-pesquisaPatologiasInfeccao";
	
	private static final String LISTA_GMR_PACIENTE = "controleinfeccao-listaGMRPaciente";
	
	private static final String NOTAS_CCIH = "controleinfeccao-cadastroNotasAdicionaisCCIH";

	@EJB
	private IAghuFacade aghuFacade;
	
	@EJB
	private IPacienteFacade pacienteFacade;
	
	@EJB
	private IBlocoCirurgicoFacade blocoCirurgicoFacade;
	
	@EJB
	private IControleInfeccaoFacade controleInfeccaoFacade;
	
	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}
	
	private String voltarPara = DETALHES_PACIENTE;
	
	private Integer codigoPaciente;
	private AipPacientes paciente;
	private String prontuarioFormatado;
	private String mensagemDetalhePaciente;
	
	// Listas
	private List<NotificacoesGeraisVO> listaDoencasCondicoes = new ArrayList<NotificacoesGeraisVO>();
	private List<NotificacoesGeraisVO> listaNotificacoesTopografias = new ArrayList<NotificacoesGeraisVO>();
	private List<NotificacoesGeraisVO> listaNotificacoesProcedimentosRisco = new ArrayList<NotificacoesGeraisVO>();
	private List<NotificacoesGeraisVO> listaNotificacoesFatoresPredisponentes = new ArrayList<NotificacoesGeraisVO>();
	private List<NotificacoesGeraisVO> listaNotasCCIH = new ArrayList<NotificacoesGeraisVO>();
	private List<NotificacoesGeraisVO> listaCirurgias = new ArrayList<NotificacoesGeraisVO>();
	
	
	public void iniciar() {
		this.paciente = pacienteFacade.obterPaciente(this.codigoPaciente);
		this.prontuarioFormatado = CoreUtil.formataProntuario(this.paciente.getProntuario());
		this.mensagemDetalhePaciente = preencherMensagemDetalhePaciente();
		pesquisar();
	}
	
	private String preencherMensagemDetalhePaciente() {
		String retorno = null;
		List<AghAtendimentos> atendimentos = this.aghuFacade.obterAtendimentoPorCodigoEOrigem(this.codigoPaciente);
		if (!atendimentos.isEmpty() && atendimentos.get(0).getIndPacAtendimento().equals(DominioPacAtendimento.S)) {
			String descricaoUnidade = atendimentos.get(0).getUnidadeFuncional().getDescricao();
			String dtIngresso = DateUtil.obterDataFormatada(atendimentos.get(0).getDthrInicio(), "dd/MM/yyyy");
			String leito = "";
			if (atendimentos.get(0).getLeito() != null) {
				leito = atendimentos.get(0).getLeito().getLeitoID();
			}
			retorno = "Internado Unidade: ".concat(descricaoUnidade).concat("  Ingresso: ").concat(dtIngresso)
					.concat("  Leito: ").concat(leito);
			
		} else {
			retorno = this.getBundle().getString("MENSAGEM_PACIENTE_NAO_INTERNADO");
		}
		return retorno;
	}
	
	public void limpar() {
		this.codigoPaciente = null;
		this.paciente = null;
		this.prontuarioFormatado = null;
		this.mensagemDetalhePaciente = null;
		this.listaDoencasCondicoes = new ArrayList<NotificacoesGeraisVO>();
		this.listaNotificacoesTopografias = new ArrayList<NotificacoesGeraisVO>();
		this.listaNotificacoesProcedimentosRisco = new ArrayList<NotificacoesGeraisVO>();
		this.listaNotificacoesFatoresPredisponentes = new ArrayList<NotificacoesGeraisVO>();
		this.listaNotasCCIH = new ArrayList<NotificacoesGeraisVO>();
		this.listaCirurgias = new ArrayList<NotificacoesGeraisVO>();
	}
	
	public void pesquisar() {
		// Preenche todas as listas
		this.listaDoencasCondicoes = this.controleInfeccaoFacade.listarNotificacoesDoencasCondicoes(this.codigoPaciente);
		this.listaNotificacoesTopografias = this.controleInfeccaoFacade.listarNotificacoesTopografias(this.codigoPaciente);
		this.listaNotificacoesProcedimentosRisco = this.controleInfeccaoFacade.listarNotificacoesProcedimentosRisco(this.codigoPaciente);
		this.listaNotificacoesFatoresPredisponentes = this.controleInfeccaoFacade.listarNotificacoesFatoresPredisponentes(this.codigoPaciente);
		this.listaNotasCCIH = this.controleInfeccaoFacade.listarNotasCCIH(this.codigoPaciente);
		this.listaCirurgias = this.blocoCirurgicoFacade.listarNotificacoesCirurgia(this.codigoPaciente);
		
	}
	
	public String obterIdadeFormatada() {
		Date dtMenos3Anos = DateUtil.adicionaMeses(new Date(), -36);
		if (DateUtil.validaDataMenor(dtMenos3Anos, this.paciente.getDtNascimento())) {
			return paciente.getIdadeAnoMesFormat();
		}
		return DateUtil.getIdade(this.paciente.getDtNascimento()).toString().concat(" anos");
	}
	
	public String voltar() {
		limpar();
		return LISTA_PACIENTES;
	}
	
	public String fatorPredisponente() {
		return FATOR_PREDISPONENTE;
	}
	
	public String notificarGMR() {
		return LISTA_GMR_PACIENTE;
	}
	
	public String cadastrarNotasCCIH() {
		return NOTAS_CCIH;
	}
	
	public String notificacarTopografia() {
		return NOTIFICACAO_TOPOGRAFIA;
	}
	
	public String procedimentoRisco() {
		return PROCEDIMENTO_RISCO;
	}
	
	public String notificacarPreventiva() {
		return NOTIFICACAO_PREVENTIVA;				
	}	
	
	public String patologiasInfeccao() {
		return PATOLOGIA_INFECCAO;
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

	public String getMensagemDetalhePaciente() {
		return mensagemDetalhePaciente;
	}

	public void setMensagemDetalhePaciente(String mensagemDetalhePaciente) {
		this.mensagemDetalhePaciente = mensagemDetalhePaciente;
	}

	public List<NotificacoesGeraisVO> getListaDoencasCondicoes() {
		return listaDoencasCondicoes;
	}

	public void setListaDoencasCondicoes(List<NotificacoesGeraisVO> listaDoencasCondicoes) {
		this.listaDoencasCondicoes = listaDoencasCondicoes;
	}

	public List<NotificacoesGeraisVO> getListaNotificacoesTopografias() {
		return listaNotificacoesTopografias;
	}

	public void setListaNotificacoesTopografias(
			List<NotificacoesGeraisVO> listaNotificacoesTopografias) {
		this.listaNotificacoesTopografias = listaNotificacoesTopografias;
	}

	public List<NotificacoesGeraisVO> getListaNotificacoesProcedimentosRisco() {
		return listaNotificacoesProcedimentosRisco;
	}

	public void setListaNotificacoesProcedimentosRisco(
			List<NotificacoesGeraisVO> listaNotificacoesProcedimentosRisco) {
		this.listaNotificacoesProcedimentosRisco = listaNotificacoesProcedimentosRisco;
	}

	public List<NotificacoesGeraisVO> getListaNotificacoesFatoresPredisponentes() {
		return listaNotificacoesFatoresPredisponentes;
	}

	public void setListaNotificacoesFatoresPredisponentes(
			List<NotificacoesGeraisVO> listaNotificacoesFatoresPredisponentes) {
		this.listaNotificacoesFatoresPredisponentes = listaNotificacoesFatoresPredisponentes;
	}

	public List<NotificacoesGeraisVO> getListaNotasCCIH() {
		return listaNotasCCIH;
	}

	public void setListaNotasCCIH(List<NotificacoesGeraisVO> listaNotasCCIH) {
		this.listaNotasCCIH = listaNotasCCIH;
	}

	public List<NotificacoesGeraisVO> getListaCirurgias() {
		return listaCirurgias;
	}

	public void setListaCirurgias(List<NotificacoesGeraisVO> listaCirurgias) {
		this.listaCirurgias = listaCirurgias;
	}

	public String getVoltarPara() {
		return voltarPara;
	}

	public void setVoltarPara(String voltarPara) {
		this.voltarPara = voltarPara;
	}
}
