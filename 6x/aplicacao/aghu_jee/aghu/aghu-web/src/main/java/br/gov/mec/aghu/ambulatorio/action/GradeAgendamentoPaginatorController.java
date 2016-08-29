package br.gov.mec.aghu.ambulatorio.action;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import org.apache.commons.lang3.time.DateUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.ambulatorio.business.IAmbulatorioFacade;
import br.gov.mec.aghu.ambulatorio.vo.GradeAgendamentoVO;
import br.gov.mec.aghu.ambulatorio.vo.VAacSiglaUnfSalaVO;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.dominio.DominioGradeConsultaProcedimento;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.exames.business.IExamesFacade;
import br.gov.mec.aghu.faturamento.business.IFaturamentoFacade;
import br.gov.mec.aghu.internacao.vo.EspCrmVO;
import br.gov.mec.aghu.internacao.vo.ProfessorCrmInternacaoVO;
import br.gov.mec.aghu.model.AacGradeAgendamenConsultas;
import br.gov.mec.aghu.model.AelProjetoPesquisas;
import br.gov.mec.aghu.model.AghEquipes;
import br.gov.mec.aghu.model.AghEspecialidades;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.commons.WebUtil;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;

/**
 * Controller da tela de pesquisa de grades de agendamento
 * 
 * @author diego.pacheco / georgenes.zapalaglio
 * 
 */


public class GradeAgendamentoPaginatorController extends ActionController implements ActionPaginator {

	@PostConstruct
	protected void inicializar(){
		this.begin(conversation);
		if(this.indSituacao==null) {
			this.indSituacao = DominioSituacao.A;
		}
	}

	@Inject @Paginator
	private DynamicDataModel<GradeAgendamentoVO> dataModel;

	private static final Log LOG = LogFactory.getLog(GradeAgendamentoPaginatorController.class);
	
	private static final long serialVersionUID = -3744812806893136737L;

	@EJB
	private IAmbulatorioFacade ambulatorioFacade;
	
	@EJB
	private IExamesFacade exameFacade;
	
	@EJB
	private IAghuFacade aghuFacade;
	
	@EJB
	private IFaturamentoFacade faturamentoFacade;
	
	@EJB
	private IParametroFacade parametroFacade;
	
	
	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;
	
	private AacGradeAgendamenConsultas gradeAgendamenConsultas;
	
	private GradeAgendamentoVO selecionado;
	
	private Integer seqGerado;
	
	private Integer seqGradeAnteriorCopia;
	
	// resultado da pesquisa na lista de valores
	private List<EspCrmVO> listaEspCrmVO = new ArrayList<EspCrmVO>();
	
	// Lista de VOs para suggestion da zona
	private List<VAacSiglaUnfSalaVO> listaZonaVO = new ArrayList<VAacSiglaUnfSalaVO>();
	
	// FILTRO
	private Integer seq;
	private DominioGradeConsultaProcedimento indProcedimento;
	private DominioSimNao indEnviaSamis;
	private DominioSituacao indSituacao;
	private Date dtEm;
	private Date dtInicio;
	private Date dtFim;	
	private Date dtInicioUltGeracao;
	private Date dtFimUltGeracao;
	
	/* CRM PROFESSOR */
	private ProfessorCrmInternacaoVO professorPesq;
	
	private AghEspecialidades especialidade;
	private AghEquipes equipe;
	private AelProjetoPesquisas projetoPesquisa;
	private RapServidores profissional;
	private VAacSiglaUnfSalaVO siglaUnfSalaVO;
	
	// SELEÇÃO
	private Integer seqSelected;
	
	// Labels parametrizados
	private String labelZona;
	private String labelZonaSala;
	private String titleZona;
	
	private Boolean gradeNova;
	
	@Inject
	private ManterGradeAgendamentoController manterGradeAgendamentoController;
	
	public GradeAgendamentoPaginatorController() {
	}
	
	/**
	 * Método executado ao iniciar a controller
	 */
	public void iniciar() {
		this.carregarParametros();
		if(seqGerado!=null && gradeNova) {
			this.limparDadosPesquisa();
			if (seqGradeAnteriorCopia != null){
				this.seq = seqGradeAnteriorCopia;
				seqGradeAnteriorCopia = null;
			}
			else{
				this.seq = seqGerado;				
			}
			this.dataModel.reiniciarPaginator();
		}
	
	}
	
	
	private void carregarParametros() {
		try {
			labelZona = parametroFacade.buscarAghParametro(AghuParametrosEnum.P_AGHU_LABEL_ZONA).getVlrTexto();
			labelZonaSala = labelZona + "/" +  parametroFacade.buscarAghParametro(AghuParametrosEnum.P_AGHU_LABEL_SALA).getVlrTexto();
	
			String message = WebUtil.initLocalizedMessage("TITLE_ZONA_GRADE_AGENDAMENTO", null);
			this.titleZona = MessageFormat.format(message, this.labelZona);
			
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
			LOG.error("Erro ao buscar parâmetro", e);
		}
		
	}
	
	/**
	 * Método executado ao clicar no botão pesquisar
	 */
	public void pesquisar() {
		
		if (dtFim != null && dtInicio != null && DateUtils.truncate(dtFim,Calendar.DATE).before(DateUtils.truncate(dtInicio,Calendar.DATE))){
			this.apresentarMsgNegocio("dtFim", Severity.ERROR, "AAC_00145");
			return;
		}
		if(dtFimUltGeracao != null && dtInicioUltGeracao != null && DateUtils.truncate(dtFimUltGeracao,Calendar.DATE).before(DateUtils.truncate(dtInicioUltGeracao,Calendar.DATE))){
			this.apresentarMsgNegocio("dtFimUltGeracao", Severity.ERROR, "AAC_00145");
			return;
		}
		if (this.seq == null) {
			this.seqGerado=null;
		}
		this.gradeNova = false;
		this.seqSelected = null;
//		this.setOrder("seq asc");
		this.dataModel.reiniciarPaginator();
		this.carregarParametros();
	}
	
	/**
	 * Método que limpa os dados da pesquisa presentes na tela
	 */	
	public void limparDadosPesquisa() {
		seq = null;
		siglaUnfSalaVO = null;
		indProcedimento = null;
		indEnviaSamis = null;
		indSituacao = null;
		dtEm = null;
		dtInicio = null;
		dtFim = null;
		especialidade = null;
		equipe = null;
		projetoPesquisa = null;
		profissional = null;		
		seqSelected = null;
		dtInicioUltGeracao = null;
		dtFimUltGeracao = null;		
		gradeNova = false;
		this.dataModel.limparPesquisa();
		this.carregarParametros();
	}
	
	/**
	 * Método executado ao clicar no botão limpar
	 * (inclusive apagando a grade pesquisada e passada por parâmetro de outras telas)
	 */	
	public void limparPesquisa() {
		limparDadosPesquisa();
		seqGerado = null;
	}
	
	@Override
	public Long recuperarCount() {
		Short unfSeq = null;
		
		if (siglaUnfSalaVO != null) {
			unfSeq = siglaUnfSalaVO.getUnfSeq(); 
		}
		return ambulatorioFacade.listarAgendamentoConsultasCount(seq, unfSeq, 
				indProcedimento != null ? indProcedimento.isProcedimento() : null,
				indEnviaSamis != null ? indEnviaSamis.isSim() : null,
				indSituacao, especialidade, equipe, profissional, projetoPesquisa,
				dtEm, dtInicio, dtFim, dtInicioUltGeracao, dtFimUltGeracao);
	}
	
	@Override
	public List<GradeAgendamentoVO> recuperarListaPaginada(Integer firstResult,
			Integer maxResult, String orderProperty, boolean asc) {
		
		try {
			faturamentoFacade.commit(30*60);
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
		
		Short unfSeq = null;
		
		if (siglaUnfSalaVO != null) {
			unfSeq = siglaUnfSalaVO.getUnfSeq(); 
		}
		
		List<GradeAgendamentoVO> lista = ambulatorioFacade.listarAgendamentoConsultas(
					firstResult, maxResult, orderProperty, asc, seq, unfSeq, 
					indProcedimento != null ? indProcedimento.isProcedimento() : null,
					indEnviaSamis != null ? indEnviaSamis.isSim() : null,
					indSituacao, especialidade, equipe, profissional, projetoPesquisa,
					dtEm, dtInicio, dtFim, dtInicioUltGeracao, dtFimUltGeracao);
		
		if (lista == null) {
			return new ArrayList<GradeAgendamentoVO>();
		}
		
		return lista;
	}

	public void gerarDisponibilidade(Integer grdSeq){
		this.manterGradeAgendamentoController.setGradeAgendamenConsultas(this.obterGradeAgendamentoConsulta(grdSeq));
	}
	
	public AacGradeAgendamenConsultas obterGradeAgendamentoConsulta(Integer grdSeq) {
		return this.ambulatorioFacade.obterGrade(grdSeq);
	}
	
	public boolean possuiConsultas(Integer grdSeq) {
		return this.ambulatorioFacade.possuiConsultasPorGradeAgendamento(grdSeq);
	}
	
	public boolean possuiHorariosGradeConsulta(Integer grdSeq) {
		return this.ambulatorioFacade.possuiHorariosGradeConsultaPorGradeAgendamento(grdSeq);
	}
	
	/**
	 * Método para Suggestion Box de Zona
	 */	
	public List<VAacSiglaUnfSalaVO> obterZona(String objPesquisa) throws BaseException  {		
		listaZonaVO = ambulatorioFacade.pesquisarZonas(objPesquisa);
		return listaZonaVO;
	}
	
	public Integer getSeq() {
		return seq;
	}

	public void setSeq(Integer seq) {
		this.seq = seq;
	}

	public DominioGradeConsultaProcedimento getIndProcedimento() {
		return indProcedimento;
	}

	public void setIndProcedimento(DominioGradeConsultaProcedimento indProcedimento) {
		this.indProcedimento = indProcedimento;
	}

	public Integer getSeqSelected() {
		return seqSelected;
	}

	public void setSeqSelected(Integer seqSelected) {
		this.seqSelected = seqSelected;
	}

	public DominioSimNao getIndEnviaSamis() {
		return indEnviaSamis;
	}

	public void setIndEnviaSamis(DominioSimNao indEnviaSamis) {
		this.indEnviaSamis = indEnviaSamis;
	}

	public DominioSituacao getIndSituacao() {
		return indSituacao;
	}

	public void setIndSituacao(DominioSituacao indSituacao) {
		this.indSituacao = indSituacao;
	}

	public Date getDtEm() {
		return dtEm;
	}

	public void setDtEm(Date dtEm) {
		this.dtEm = dtEm;
	}

	public Date getDtInicio() {
		return dtInicio;
	}

	public void setDtInicio(Date dtInicio) {
		this.dtInicio = dtInicio;
	}

	public Date getDtFim() {
		return dtFim;
	}

	public void setDtFim(Date dtFim) {
		this.dtFim = dtFim;
	}
	
	public List<AghEspecialidades> obterEspecialidade(String parametro) {
		return aghuFacade.getListaEspecialidades((String) parametro);
	}
	
	public List<AghEquipes> obterEquipe(String parametro) {
		return aghuFacade.getListaEquipes((String) parametro);
	}
	
	public List<RapServidores> obterMedicos(String parametro){
		return registroColaboradorFacade.listarServidoresComPessoaFisicaPorEquipe((String) parametro, equipe);
	}
	/*
	public List<EspCrmVO> obterMedicos(Object parametro) throws ApplicationBusinessException {
		listaEspCrmVO = new ArrayList<EspCrmVO>();
		if (especialidade != null) {
			listaEspCrmVO = ambulatorioFacade.getSolicitacaoInternacaoON()
					.pesquisarEspCrmVOComAmbulatorio(parametro, especialidade,
							DominioSimNao.S);
		}
		return listaEspCrmVO;
	}*/

	public List<AelProjetoPesquisas> obterProjetoPesquisa(String objParam) {
		List<AelProjetoPesquisas> retorno;
		String strPesquisa = (String) objParam;
		retorno = exameFacade.pesquisarProjetosPesquisa(strPesquisa);

		return retorno;
	}	
	
	public AacGradeAgendamenConsultas getGradeAgendamenConsultas() {
		return gradeAgendamenConsultas;
	}

	public void setGradeAgendamenConsultas(
			AacGradeAgendamenConsultas gradeAgendamenConsultas) {
		this.gradeAgendamenConsultas = gradeAgendamenConsultas;
	}

	public void setEspecialidade(AghEspecialidades especialidade) {
		this.especialidade = especialidade;
	}

	public AghEspecialidades getEspecialidade() {
		return especialidade;
	}

	public void setEquipe(AghEquipes equipe) {
		this.equipe = equipe;
	}

	public AghEquipes getEquipe() {
		return equipe;
	}

	public void setProjetoPesquisa(AelProjetoPesquisas projetoPesquisa) {
		this.projetoPesquisa = projetoPesquisa;
	}

	public AelProjetoPesquisas getProjetoPesquisa() {
		return projetoPesquisa;
	}

	public List<EspCrmVO> getListaEspCrmVO() {
		return listaEspCrmVO;
	}

	public void setListaEspCrmVO(List<EspCrmVO> listaEspCrmVO) {
		this.listaEspCrmVO = listaEspCrmVO;
	}

	public ProfessorCrmInternacaoVO getProfessorPesq() {
		return professorPesq;
	}

	public void setProfessorPesq(ProfessorCrmInternacaoVO professorPesq) {
		this.professorPesq = professorPesq;
	}

	public IAmbulatorioFacade getAmbulatorioFacade() {
		return ambulatorioFacade;
	}

	public void setAmbulatorioFacade(IAmbulatorioFacade ambulatorioFacade) {
		this.ambulatorioFacade = ambulatorioFacade;
	}

	public String getLabelZona() {
		return labelZona;
	}

	public void setLabelZona(String labelZona) {
		this.labelZona = labelZona;
	}
	
	public VAacSiglaUnfSalaVO getSiglaUnfSalaVO() {
		return siglaUnfSalaVO;
	}

	public void setSiglaUnfSalaVO(VAacSiglaUnfSalaVO siglaUnfSalaVO) {
		this.siglaUnfSalaVO = siglaUnfSalaVO;
	}
	
	public void removerGradeAgendamento(AacGradeAgendamenConsultas gradeAgendamenConsulta) {
		try {
			ambulatorioFacade.removerGradeConsulta(gradeAgendamenConsulta.getSeq());
			apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_EXCLUSAO_GRADE");
			this.dataModel.reiniciarPaginator();
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
			LOG.error(e.getMessage() , e);
		}	
	}

	public String getLabelZonaSala() {
		return labelZonaSala;
	}

	public void setLabelZonaSala(String labelZonaSala) {
		this.labelZonaSala = labelZonaSala;
	}
	
	public Date getDtInicioUltGeracao() {
		return dtInicioUltGeracao;
	}

	public void setDtInicioUltGeracao(Date dtInicioUltGeracao) {
		this.dtInicioUltGeracao = dtInicioUltGeracao;
	}

	public Date getDtFimUltGeracao() {
		return dtFimUltGeracao;
	}

	public void setDtFimUltGeracao(Date dtFimUltGeracao) {
		this.dtFimUltGeracao = dtFimUltGeracao;
	}

	public String getTitleZona() {
		return titleZona;
	}

	public void setTitleZona(String titleZona) {
		this.titleZona = titleZona;
	}

	public RapServidores getProfissional() {
		return profissional;
	}

	public void setProfissional(RapServidores profissional) {
		this.profissional = profissional;
	}
	
	public void setSeqGerado(Integer seqGerado) {
		this.seqGerado = seqGerado;
	}

	public Integer getSeqGerado() {
		return seqGerado;
	}

	public DynamicDataModel<GradeAgendamentoVO> getDataModel() {
	 return dataModel;
	}

	public void setDataModel(DynamicDataModel<GradeAgendamentoVO> dataModel) {
	 this.dataModel = dataModel;
	}
	
	public void setGradeNova(Boolean gradeNova) {
		this.gradeNova = gradeNova;
	}

	public Boolean getGradeNova() {
		return gradeNova;
	}
	
	public Integer getSeqGradeAnteriorCopia() {
		return seqGradeAnteriorCopia;
	}

	public void setSeqGradeAnteriorCopia(Integer seqGradeAnteriorCopia) {
		this.seqGradeAnteriorCopia = seqGradeAnteriorCopia;
	}

	public GradeAgendamentoVO getSelecionado() {
		return selecionado;
	}

	public void setSelecionado(GradeAgendamentoVO selecionado) {
		this.selecionado = selecionado;
	}
	
	
}
