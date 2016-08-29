package br.gov.mec.aghu.blococirurgico.portalplanejamento.action;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.event.ValueChangeEvent;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.primefaces.event.TabChangeEvent;

import br.gov.mec.aghu.blococirurgico.portalplanejamento.business.IBlocoCirurgicoPortalPlanejamentoFacade;
import br.gov.mec.aghu.blococirurgico.portalplanejamento.vo.PortalPesquisaCirurgiasC2VO;
import br.gov.mec.aghu.blococirurgico.portalplanejamento.vo.PortalPesquisaCirurgiasParametrosVO;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.model.AghCaractUnidFuncionais;
import br.gov.mec.aghu.model.AghEspecialidades;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.MbcProcedimentoCirurgicos;
import br.gov.mec.aghu.paciente.business.IPacienteFacade;
import br.gov.mec.aghu.prescricaomedica.vo.LinhaReportVO;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.core.utils.DateUtil;


public class PortalPesquisaCirurgiasController extends ActionController{

	@PostConstruct
	protected void inicializar(){
	 this.begin(conversation);
	}

	private static final long serialVersionUID = 1001254663147702012L;

	private static final String PESQUISA_FONETICA = "paciente-pesquisaPacienteComponente";
	private static final String ESCALA_SALAS = "consultaEscalaSalas";
	private static final String PORTAL_PESQUISA = "portalPesquisaCirurgias";
	
	private static final Integer ABA_PESQUISA_FECHADA = -1;
	private static final Integer ABA_PESQUISA_ABERTA = 0;

	@EJB
	private IBlocoCirurgicoPortalPlanejamentoFacade blocoCirurgicoPortalPlanejamentoFacade;
	
	@EJB
	private IAghuFacade aghuFacade;
	
	@EJB
	private IPacienteFacade pacienteFacade;
	
	@Inject
	private ListaCirurgiasCanceladasController listaCirurgiasCanceladasController;
	
	@Inject
	private VisualizarListaEsperaController visualizarListaEsperaController;
	
	@Inject
	private VisualizarAgendaEscalaController visualizarAgendaEscalaController;
	
	@Inject
	private AgendamentosExcluidosController agendamentosExcluidosController;
	
	@Inject
	private ConsultaEscalaSalasController consultaEscalaSalasController;
	
	private PortalPesquisaCirurgiasParametrosVO parametrosVO;
	
		
	private Boolean habilitaResultadoPesquisa;
	private MbcProcedimentoCirurgicos procedimento;
	private Integer abaAtiva;
	private Integer seq;
	private String justificativa;
	private String titulo;
	private String cameFrom;
	private String voltarPara;
	private Integer openTogglePesquisa;
	private String titleSlider;
	
	private AipPacientes paciente;
	
	//FILTRO
	private AghCaractUnidFuncionais localRealizacao; //private List<AghCaractUnidFuncionais> listLocaisRealizacao;
	private AghEspecialidades especialidade;
	private PortalPesquisaCirurgiasC2VO equipe;
	private LinhaReportVO salaCirurgica; //private List<SelectItem> listSalas;	
	private Boolean pesquisaRealizada = Boolean.FALSE;
	
	private Boolean msgPacienteNulo;
	
	public void inicio() {
		titleSlider = "";
		setEquipe(null);
		openTogglePesquisa = ABA_PESQUISA_ABERTA;
		
		if(parametrosVO == null){
			setParametrosVO(new PortalPesquisaCirurgiasParametrosVO());
			pesquisaRealizada = Boolean.FALSE;
		}else{
			if(parametrosVO.getPacCodigo() != null){
				processarBuscaPacientePorCodigo(parametrosVO.getPacCodigo());
			}
			obterTitleSlider();
			pesquisaRealizada = Boolean.TRUE;
		}
	
		if(abaAtiva == null){
			setAbaAtiva(0);
		}
	}
	
	public void collapseTogglePesquisa() {
		if (openTogglePesquisa == ABA_PESQUISA_ABERTA) {
			openTogglePesquisa = ABA_PESQUISA_FECHADA;
		} else {
			openTogglePesquisa = ABA_PESQUISA_ABERTA;
		}
	}
	
	private void obterTitleSlider() {
		StringBuffer title = new StringBuffer();
		if(localRealizacao != null) {
			title.append(" | Unidade: ".concat(localRealizacao.getSiglaUnidadeFuncional()));
		}
		if(parametrosVO != null){
			if(parametrosVO.getDataInicio() != null) {
				title.append((" | Data de Início: ").concat(DateUtil.obterDataFormatada(parametrosVO.getDataInicio(), "dd/MM/yyyy")));
			}
			if(parametrosVO.getDataFim() != null) {
				title.append((" | Data de Fim: ").concat(DateUtil.obterDataFormatada(parametrosVO.getDataFim(), "dd/MM/yyyy")));
			}
		}
		if(especialidade != null) {
			title.append((" | Especialidade: ").concat(especialidade.getNomeEspecialidade()));
		}
		if(parametrosVO != null){
			if(parametrosVO.getPacProntuario() != null) {
				title.append((" | Prontuário: ").concat(parametrosVO.getPacProntuario().toString()));
			}
			if(parametrosVO.getPacNome() != null){
				title.append((" | Nome do Paciente: ").concat(parametrosVO.getPacNome()));
			}
			if(parametrosVO.getConvenio() != null){
				title.append((" | Convênio: ").concat(parametrosVO.getConvenio().getDescricao()));
			}
		}
		if(equipe != null){
			title.append((" | Equipe: ").concat(equipe.getNome()));
		}
		if(procedimento != null){
			title.append((" | Procedimento: ").concat(procedimento.getDescricao()));
		}		
		titleSlider = title.toString();
		
	}
		
	public List<AghCaractUnidFuncionais> listarAghCaractUnidFuncionais(String objPesquisa) {	
		return this.returnSGWithCount(blocoCirurgicoPortalPlanejamentoFacade.listarAghCaractUnidFuncionais((String) objPesquisa),listarAghCaractUnidFuncionaisCount(objPesquisa));
	}
	
	public Long listarAghCaractUnidFuncionaisCount(String objPesquisa) {
		return blocoCirurgicoPortalPlanejamentoFacade.listarAghCaractUnidFuncionaisCount((String) objPesquisa);
	}	
	
	public List<LinhaReportVO> buscarSalasCirurgicas(String objPesquisa){ 
		String pesquisa = objPesquisa != null ? objPesquisa : null;
		return this.returnSGWithCount(blocoCirurgicoPortalPlanejamentoFacade.listarCaracteristicaSalaCirgPorUnidade(pesquisa, localRealizacao.getId().getUnfSeq()),buscarSalasCirurgicasCount(objPesquisa));
	}
	
	public Long buscarSalasCirurgicasCount(String objPesquisa){ 
		String pesquisa = objPesquisa != null ? objPesquisa : null;
		return blocoCirurgicoPortalPlanejamentoFacade.listarCaracteristicaSalaCirgPorUnidadeCount(pesquisa, localRealizacao.getId().getUnfSeq());
	}	

	public List<AghEspecialidades> listarEspecialidades(final String strPesquisa) {
		return this.returnSGWithCount(this.aghuFacade.pesquisarEspecialidades((String) strPesquisa),listarEspecialidadesCount(strPesquisa));
	}

	public Long listarEspecialidadesCount(final String strPesquisa) {
		return this.aghuFacade.pesquisarEspecialidadesCount((String) strPesquisa);
	}
	
	public List<PortalPesquisaCirurgiasC2VO> pesquisarSuggestionEquipe(final String strPesquisa) {

		return this.returnSGWithCount(blocoCirurgicoPortalPlanejamentoFacade.listarMbcProfAtuaUnidCirgsPorUnfSeq(parametrosVO.getUnfSeq() != null ? parametrosVO.getUnfSeq() : null, (String) strPesquisa),pesquisarSuggestionEquipeCount(strPesquisa));
	}
	
	public Long pesquisarSuggestionEquipeCount(final String strPesquisa) {
		return blocoCirurgicoPortalPlanejamentoFacade.listarMbcProfAtuaUnidCirgsPorUnfSeqCount(parametrosVO.getUnfSeq(), (String) strPesquisa);
	}
	
	public List<MbcProcedimentoCirurgicos> pesquisarSuggestionProcedimento(final String strPesquisa) {
		return this.returnSGWithCount(blocoCirurgicoPortalPlanejamentoFacade.listarMbcProcedimentoCirurgicoPorTipo((String) strPesquisa),pesquisarSuggestionProcedimentoCount(strPesquisa));
	}
	
	public Long pesquisarSuggestionProcedimentoCount(final String strPesquisa) {
		return blocoCirurgicoPortalPlanejamentoFacade.listarMbcProcedimentoCirurgicoPorTipoCount((String) strPesquisa);
	}
	
	public void limparPesquisa() {
		setProcedimento(null);
		setEspecialidade(null);
		setEquipe(null);
		setLocalRealizacao(null);		
		setParametrosVO(new PortalPesquisaCirurgiasParametrosVO());		
		setHabilitaResultadoPesquisa(Boolean.FALSE);
		setAbaAtiva(0);		
		setSalaCirurgica(null);	//setListSalas(null);		
		setOpenTogglePesquisa(ABA_PESQUISA_ABERTA);
		setPesquisaRealizada(Boolean.FALSE);
		setTitleSlider(null);
		setPaciente(null);
	}
	
	public void limparSuggestions() {					
		setSalaCirurgica(null);			
	}	
	
	public String pesquisar() {
		//validarCamposObrigatorios
		if ((parametrosVO.getPacProntuario() != null && parametrosVO.getPacCodigo() == null)){				
			this.tratarResultadoBuscaPaciente(pacienteFacade.obterPacientePorProntuario(parametrosVO.getPacProntuario()));
			if(msgPacienteNulo){
				return null;
			}			
		}		
		
		if(equipe != null){
			parametrosVO.setPucSerVinCodigo(equipe.getSerVinCodigo());
			parametrosVO.setPucSerMatricula(equipe.getSerMatricula());
			parametrosVO.setPucIndFuncaoProf(equipe.getIndFuncaoProf());
			
		}else{
			parametrosVO.setPucSerVinCodigo(null);
			parametrosVO.setPucSerMatricula(null);
			parametrosVO.setPucIndFuncaoProf(null);
			parametrosVO.setPucUnfSeq(null);
		}
		
		if(procedimento != null){
			parametrosVO.setPciSeqPortal(procedimento.getSeq());
		}else{
			parametrosVO.setPciSeqPortal(null);
		}
		
		if(especialidade != null){
			parametrosVO.setEspSeq(especialidade.getSeq());
		}else{
			parametrosVO.setEspSeq(null);
		}
		
		if(localRealizacao != null && localRealizacao.getId().getUnfSeq() != null){
			parametrosVO.setUnfSeq(localRealizacao.getId().getUnfSeq());
		}else{
			parametrosVO.setUnfSeq(null);
		}
		
		if(salaCirurgica != null){
			parametrosVO.setSala(salaCirurgica.getNumero4());
		}else{
			parametrosVO.setSala(null);
		}
		
		try{
			blocoCirurgicoPortalPlanejamentoFacade.validarPesquisaPortalCirurgias(parametrosVO);
		}catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
			return null;
		}
		
		setHabilitaResultadoPesquisa(Boolean.TRUE);
		
		visualizarListaEsperaController.inicializaParametros(parametrosVO);
		listaCirurgiasCanceladasController.recebeParametros(parametrosVO);
		agendamentosExcluidosController.recebeParametros(parametrosVO);
		
		if(getAbaAtiva() == 2){
			visualizarAgendaEscalaController.recebeParametros(parametrosVO);
		}
		
		obterTitleSlider();
		openTogglePesquisa = ABA_PESQUISA_FECHADA;
		pesquisaRealizada = Boolean.TRUE;
		
		return null;
	}
	
	
	public void visualizarAgendaEscala(){
		visualizarAgendaEscalaController.recebeParametros(parametrosVO);
		setAbaAtiva(2);
	}
	
	public void processarBuscaPacientePorCodigo(Integer pacCodigo){
		if(pacCodigo != null){
			this.setPaciente(pacienteFacade.buscaPaciente(pacCodigo));
		}
	}
	
	public String redirecionarEscalaSala() {
		consultaEscalaSalasController.setUrlVoltar(PORTAL_PESQUISA);
		return ESCALA_SALAS;
	}
	
	public void pesquisaPaciente(ValueChangeEvent event){
		try {
			this.setPaciente(pacienteFacade.pesquisarPacienteComponente(event.getNewValue(), event.getComponent().getId()));
			tratarResultadoBuscaPaciente(paciente);
		}catch(BaseException e){
			apresentarExcecaoNegocio(e);
		}
	}

	
	private void tratarResultadoBuscaPaciente(AipPacientes paciente) {		
		if(paciente != null){
			parametrosVO.setPacProntuario(paciente.getProntuario());
			parametrosVO.setPacCodigo(paciente.getCodigo());
			parametrosVO.setPacNome(paciente.getNome());
			msgPacienteNulo=Boolean.FALSE;
		}else{
			parametrosVO.setPacProntuario(null);
			parametrosVO.setPacCodigo(null);
			parametrosVO.setPacNome(null);
			this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_NENHUM_PACIENTE_ENCONTRADO");		
			msgPacienteNulo=Boolean.TRUE;
		}
	}	
	
	public String abreviar(String str, int maxWidth){
		String abreviado = null;
		if(str != null){
			abreviado = " " + StringUtils.abbreviate(str, maxWidth);
		}
		return abreviado;
	}
	
	public String redirecionarPesquisaFonetica(){
		return PESQUISA_FONETICA;
	}

	public PortalPesquisaCirurgiasParametrosVO getParametrosVO() {
		return parametrosVO;
	}

	public void setParametrosVO(PortalPesquisaCirurgiasParametrosVO parametrosVO) {
		this.parametrosVO = parametrosVO;
	}

	public void setLocalRealizacao(AghCaractUnidFuncionais localRealizacao) {
		this.localRealizacao = localRealizacao;
	}

	public AghCaractUnidFuncionais getLocalRealizacao() {
		return localRealizacao;
	}

	public void setEquipe(PortalPesquisaCirurgiasC2VO equipe) {
		this.equipe = equipe;
	}

	public PortalPesquisaCirurgiasC2VO getEquipe() {
		return equipe;
	}

	public void setHabilitaResultadoPesquisa(Boolean habilitaResultadoPesquisa) {
		this.habilitaResultadoPesquisa = habilitaResultadoPesquisa;
	}

	public Boolean getHabilitaResultadoPesquisa() {
		return habilitaResultadoPesquisa;
	}

	public void setProcedimento(MbcProcedimentoCirurgicos procedimento) {
		this.procedimento = procedimento;
	}

	public MbcProcedimentoCirurgicos getProcedimento() {
		return procedimento;
	}

	public void setEspecialidade(AghEspecialidades especialidade) {
		this.especialidade = especialidade;
	}

	public AghEspecialidades getEspecialidade() {
		return especialidade;
	}

	public void setAbaAtiva(Integer abaAtiva) {
		this.abaAtiva = abaAtiva;
	}

	public Integer getAbaAtiva() {
		return abaAtiva;
	}
	
	public Integer getSeq() {
		return seq;
	}

	public void setSeq(Integer seq) {
		this.seq = seq;
	}

	public String getJustificativa() {
		return justificativa;
	}

	public void setJustificativa(String justificativa) {
		this.justificativa = justificativa;
	}

	public String getTitulo() {
		return titulo;
	}

	public void setTitulo(String titulo) {
		this.titulo = titulo;
	}

	public String getCameFrom() {
		return cameFrom;
	}

	public void setCameFrom(String cameFrom) {
		this.cameFrom = cameFrom;
	}

	public String getVoltarPara() {
		return voltarPara;
	}

	public void setVoltarPara(String voltarPara) {
		this.voltarPara = voltarPara;
	}

	public LinhaReportVO getSalaCirurgica() {
		return salaCirurgica;
	}

	public void setSalaCirurgica(LinhaReportVO salaCirurgica) {
		this.salaCirurgica = salaCirurgica;
	}

	public Boolean getMsgPacienteNulo() {
		return msgPacienteNulo;
	}

	public void setMsgPacienteNulo(Boolean msgPacienteNulo) {
		this.msgPacienteNulo = msgPacienteNulo;
	}
	
	public Integer getOpenTogglePesquisa() {
		return openTogglePesquisa;
	}

	public void setOpenTogglePesquisa(Integer openTogglePesquisa) {
		this.openTogglePesquisa = openTogglePesquisa;
	}

	public String getTitleSlider() {
		return titleSlider;
	}

	public void setTitleSlider(String titleSlider) {
		this.titleSlider = titleSlider;
	}

	public Boolean getPesquisaRealizada() {
		return pesquisaRealizada;
	}

	public void setPesquisaRealizada(Boolean pesquisaRealizada) {
		this.pesquisaRealizada = pesquisaRealizada;
	}

	public AipPacientes getPaciente() {
		return paciente;
	}

	public void setPaciente(AipPacientes paciente) {
		if(paciente != null){
			parametrosVO.setPacProntuario(paciente.getProntuario());
			parametrosVO.setPacCodigo(paciente.getCodigo());
			parametrosVO.setPacNome(paciente.getNome());
		}
		this.paciente = paciente;
	}

	public void onTabChange(TabChangeEvent event) {

		if (event.getTab().getId().equals("tab3")) {
			this.visualizarAgendaEscala();
		}
	}
}
