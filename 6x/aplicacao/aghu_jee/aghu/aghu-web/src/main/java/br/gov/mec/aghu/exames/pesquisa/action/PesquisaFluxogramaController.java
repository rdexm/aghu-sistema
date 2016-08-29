package br.gov.mec.aghu.exames.pesquisa.action;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;
import javax.transaction.SystemException;

import net.sf.jasperreports.engine.JRException;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.Period;
import org.joda.time.PeriodType;

import br.gov.mec.aghu.action.impressao.SistemaImpressao;
import br.gov.mec.aghu.ambulatorio.action.AtenderPacientesEvolucaoController;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.exames.business.IExamesFacade;
import br.gov.mec.aghu.exames.pesquisa.business.IPesquisaExamesFacade;
import br.gov.mec.aghu.exames.pesquisa.business.PesquisaExamesExceptionCode;
import br.gov.mec.aghu.exames.pesquisa.vo.FluxogramaLaborarorialDadosDataValorVO;
import br.gov.mec.aghu.exames.pesquisa.vo.FluxogramaLaborarorialDadosVO;
import br.gov.mec.aghu.exames.pesquisa.vo.FluxogramaLaborarorialVO;
import br.gov.mec.aghu.exames.pesquisa.vo.PesquisaExamesFiltroVO;
import br.gov.mec.aghu.internacao.business.IInternacaoFacade;
import br.gov.mec.aghu.model.AelSolicitacaoExames;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.AghEspecialidades;
import br.gov.mec.aghu.model.AinLeitos;
import br.gov.mec.aghu.paciente.prontuarioonline.action.ArvorePOLController;
import br.gov.mec.aghu.paciente.prontuarioonline.vo.NodoPOLVO;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;

import com.itextpdf.text.DocumentException;


public class PesquisaFluxogramaController extends ActionController {

	private static final long serialVersionUID = 3661785096909101631L;
	private static final String PAGE_CANCELAR_EXAME="exames-cancelarExamesResponsabilidadeSolicitante";
	private static final String PAGE_PACIENTE_EVOLUCAO="ambulatorio-atenderPacientesEvolucao";
	

	@EJB
	private IPesquisaExamesFacade pesquisaExamesFacade;
	
	@EJB
	private IExamesFacade examesFacade;

	@EJB
	private IAghuFacade aghuFacade;

	@EJB
	private IInternacaoFacade internacaoFacade;	

	@Inject
	private RelatorioFluxogramaController relatorioFluxogramaController;
	
	@Inject
	private SistemaImpressao sistemaImpressao;	

	
	
	@Inject
	private ArvorePOLController arvorePOLController;
	
	@Inject AtenderPacientesEvolucaoController atenderPacientesEvolucaoController;
	
	private AghAtendimentos atendimento;

	private boolean filtroAberto = true;
	private boolean resultAberto = false;
	private Boolean historico = false;
	private Boolean downloadLink=false;
	private Boolean polPage=false;
	
	private static final Integer VALOR_ACCORDION_FLUXOGRAMA_ABERTO = 0;
	private static final Integer VALOR_ACCORDION_FLUXOGRAMA_FECHADO = -1;
	private Integer openToggle = -1;

	/*	fitro da tela de pesquisa	*/
	private PesquisaExamesFiltroVO filtro = new PesquisaExamesFiltroVO();

	private List<FluxogramaLaborarorialDadosVO> dadosFluxograma = new ArrayList<FluxogramaLaborarorialDadosVO>();
	private List<Date> datasExibicao = new ArrayList<Date>();

	/**
	 * Retorna para a lista de pacientes da prescricao
	 */
	private boolean exibirBotaoVoltar = false;
	
	/**
	 * Controle de retorno de pagina
	 */
	private String voltarPara;	
	private Integer soeSeq;	
	private Integer prontuario;	
	private Boolean inibeAlteracaoProntuario;
	private Boolean isFluxograma = false;

    
	@PostConstruct
	protected void inicializar(){
		this.begin(conversation);
		downloadLink = !sistemaImpressao.getFlagCups();
	}		
	
	/**
	 * 
	 */	
	public void inicio() {
		if(getRequestParameter("prontuario") != null){
			this.prontuario = Integer.valueOf(getRequestParameter("prontuario").trim());
		}

		historico=false;
				
		NodoPOLVO itemPOL = arvorePOLController.getNodoSelecionado();
		if (itemPOL!=null){
			filtro.setProntuarioPac(itemPOL.getProntuario());
			if (itemPOL.getParametros().containsKey(NodoPOLVO.IS_HISTORICO)){
				historico = (Boolean) itemPOL.getParametros().get(NodoPOLVO.IS_HISTORICO);
			}	
			exibirBotaoVoltar = false;			
			polPage=true;				
				
			
		}else if(prontuario != null){
			filtro.setProntuarioPac(prontuario);								
		}	
		
		if(this.filtro.getProntuarioPac()!=null || this.filtro.getConsultaPac()!=null || this.filtro.getLeitoPac()!=null){
			this.pesquisar();
		} else {
			setFiltroAberto(true);
			setResultAberto(false);
			openToggle=VALOR_ACCORDION_FLUXOGRAMA_ABERTO;
		}
		
		if(StringUtils.isNotEmpty(this.voltarPara)){
			exibirBotaoVoltar = true;		
		}
		if(inibeAlteracaoProntuario == null){
			inibeAlteracaoProntuario = Boolean.FALSE;
		}
	
	}
	
	/**
	 * Metodo que limpa os campos de filtro<br>
	 * na tele de pesquisa de exames.
	 */
	public void pesquisar() {

		try {
			if(this.prontuario != null && this.isFluxograma){
				 openToggle=VALOR_ACCORDION_FLUXOGRAMA_ABERTO;
			}else{
			    openToggle=VALOR_ACCORDION_FLUXOGRAMA_FECHADO;
			}       
			
			if(this.filtro.getProntuarioPac()==null && this.filtro.getConsultaPac()==null && this.filtro.getLeitoPac()==null){
				throw new ApplicationBusinessException(PesquisaExamesExceptionCode.AEL_00833);
			}else{

				atendimento = 	this.aghuFacade.obterAtendimentoPorNumeroConsultaLeitoProntuario(
								this.filtro.getProntuarioPac(), 
								this.filtro.getConsultaPac(),
								(this.filtro.getLeitoPac()!=null?this.filtro.getLeitoPac().getLeitoID():null));

				if(atendimento != null){
					this.filtro.setProntuarioPac(atendimento.getProntuario());
					if(atendimento.getConsulta()!= null){
						this.filtro.setConsultaPac(atendimento.getConsulta().getNumero());
					}
					this.filtro.setLeitoPac(atendimento.getLeito());
					//buscar solicitacao
					final AelSolicitacaoExames solicitacaoExame = this.examesFacade
						.obterSolicitacaoExamePorAtendimento(this.atendimento.getSeq());
					if(solicitacaoExame != null) {
						this.soeSeq = solicitacaoExame.getSeq();
					}
				} else {
					this.soeSeq = null;
				}
				
				FluxogramaLaborarorialVO fluxograma = this.examesFacade.pesquisarFluxograma((this.filtro.getEspecialidade()!=null?this.filtro.getEspecialidade().getSeq():null), this.filtro.getProntuarioPac(), historico); 
				this.dadosFluxograma = fluxograma.getDadosFluxograma();
				this.datasExibicao = fluxograma.getDatasExibicao();
				
				
			}

			renderPesquisa();

		} catch (BaseException e) {
			setFiltroAberto(true);
			setResultAberto(false);
			openToggle=VALOR_ACCORDION_FLUXOGRAMA_ABERTO;
			apresentarExcecaoNegocio(e);
		}
	}

	private void renderPesquisa() {
		setFiltroAberto(false);
		setResultAberto(true);
	}

	public Integer calculaIdadePacienteAnos() {
		if(atendimento==null || atendimento.getPaciente()==null){
			return null;
		}else{
			Date dtNascimento = atendimento.getPaciente().getDtNascimento();
			if (dtNascimento == null) {
				return null;
			}
			Period period = new Period(dtNascimento.getTime(), Calendar.getInstance().getTimeInMillis(), PeriodType.years());
			return period.getYears();
		}
	}

	/**
	 * Metodo que limpa os campos de filtro<br>
	 */
	public void limparPesquisa(){
		setFiltro(new PesquisaExamesFiltroVO());
		setAtendimento(null);
		setFiltroAberto(true);
		setResultAberto(false);
		this.setDadosFluxograma(null);
		this.setProntuario(null);
		setSoeSeq(null);
		openToggle=VALOR_ACCORDION_FLUXOGRAMA_ABERTO;
	}

	public String print() throws BaseException, JRException, SystemException, IOException, DocumentException{
		relatorioFluxogramaController.setColecao(this.dadosFluxograma);
		relatorioFluxogramaController.setDatas(this.datasExibicao);

		if(this.filtro.getProntuarioPac()!=null){
			relatorioFluxogramaController.setProntuario(this.filtro.getProntuarioPac());
		}
		if(this.filtro.getConsultaPac()!=null){
			relatorioFluxogramaController.setConsulta(this.filtro.getConsultaPac());
		}
		if(this.filtro.getLeitoPac()!= null){
			relatorioFluxogramaController.setLeito(this.filtro.getLeitoPac().getLeitoID());
		}
		if(this.atendimento != null && this.atendimento.getUnidadeFuncional()!= null){
			relatorioFluxogramaController.setAndar(atendimento.getUnidadeFuncional().getAndar().toString());
		}
		if(this.atendimento != null && atendimento.getLeito() != null && atendimento.getLeito().getQuarto() != null && atendimento.getLeito().getQuarto().getAla() != null){
			String descricao = internacaoFacade.obterLeitoComAla(atendimento.getLeito().getLeitoID()).getQuarto().getAla().getDescricao();
			relatorioFluxogramaController.setAla(descricao);
		}
		if(atendimento!= null && atendimento.getPaciente()!= null){
			relatorioFluxogramaController.setNomePaciente(atendimento.getPaciente().getNome());
			relatorioFluxogramaController.setIdade(this.calculaIdadePacienteAnos());
		}
		

		return relatorioFluxogramaController.print();
	}
	
	public String directPrint() throws BaseException, JRException, SystemException, IOException{
		relatorioFluxogramaController.setColecao(this.dadosFluxograma);
		relatorioFluxogramaController.setDatas(this.datasExibicao);

		if(this.filtro.getProntuarioPac()!=null){
			relatorioFluxogramaController.setProntuario(this.filtro.getProntuarioPac());
		}
		if(this.filtro.getConsultaPac()!=null){
			relatorioFluxogramaController.setConsulta(this.filtro.getConsultaPac());
		}
		if(this.filtro.getLeitoPac()!= null){
			relatorioFluxogramaController.setLeito(this.filtro.getLeitoPac().getLeitoID());
		}
		if(this.atendimento != null && this.atendimento.getUnidadeFuncional()!= null){
			relatorioFluxogramaController.setAndar(atendimento.getUnidadeFuncional().getAndar().toString());
		}
		if(this.atendimento != null && atendimento.getLeito() != null && atendimento.getLeito().getQuarto() != null && atendimento.getLeito().getQuarto().getAla() != null){
			relatorioFluxogramaController.setAla(atendimento.getLeito().getQuarto().getAla().getDescricao());
		}
		if(atendimento!= null && atendimento.getPaciente()!= null){
			relatorioFluxogramaController.setNomePaciente(atendimento.getPaciente().getNome());
			relatorioFluxogramaController.setIdade(this.calculaIdadePacienteAnos());
		}
		relatorioFluxogramaController.directPrint();
		
		return null;
	}

	// Metódo para Suggestion Box de Agrupamentos de pesquisa
	public List<AghEspecialidades> pesquisarEspecialidades(String objPesquisa){
		return this.returnSGWithCount(this.aghuFacade.pesquisarEspecialidadeFluxogramaPorNomeOuSigla((String)objPesquisa),pesquisarEspecialidadeFluxogramaPorNomeOuSiglaCount(objPesquisa));
	}
	
	public Long pesquisarEspecialidadeFluxogramaPorNomeOuSiglaCount(String objPesquisa) {
		return this.aghuFacade.pesquisarEspecialidadeFluxogramaPorNomeOuSiglaCount(objPesquisa);
	}

	// Metódo para Suggestion Box de Leitos
	public List<AinLeitos> pesquisarLeito(String objPesquisa){
		return this.returnSGWithCount(this.pesquisaExamesFacade.obterLeitosAtivosPorUnf(objPesquisa, (this.getFiltro().getAelUnffuncionalPac()==null?null:this.getFiltro().getAelUnffuncionalPac().getSeq())),obterLeitosAtivosPorUnfCount(objPesquisa));
		
	}
	
	public Long obterLeitosAtivosPorUnfCount(String objPesquisa) {
		return this.pesquisaExamesFacade.obterLeitosAtivosPorUnfCount(objPesquisa, (this.getFiltro().getAelUnffuncionalPac()==null?null:this.getFiltro().getAelUnffuncionalPac().getSeq()));
	}
	
	public FluxogramaLaborarorialDadosDataValorVO getValorExameData(FluxogramaLaborarorialDadosVO dadosFlux, Date data){
		return dadosFlux.getDatasValores().get(new SimpleDateFormat("dd/MM/yyyy HH:mm").format(data));
	}

	public String obterCorValor(FluxogramaLaborarorialDadosVO dadosFlux, Date dataEvento) throws ParseException{
		
		if (dadosFlux != null) {
			if (dadosFlux.getDatasValores().containsKey(new SimpleDateFormat("dd/MM/yyyy HH:mm").format(dataEvento))) {
				boolean temNotaAdicional = ((FluxogramaLaborarorialDadosDataValorVO) dadosFlux.getDatasValores().get(new SimpleDateFormat("dd/MM/yyyy HH:mm").format(dataEvento))).isPossuiNotaAdicional();
				if (temNotaAdicional) {
					return "#FF1A1A";
				} else {
					return this.examesFacade.verificaNormalidade(dadosFlux,dataEvento);
				}
			}
		}
		return "";
		
	}

	public String voltar(){		
		String retorno = null;
		this.dadosFluxograma = null;
		limparPesquisa();
		
		if(this.isFluxograma){
			atenderPacientesEvolucaoController.setAcao(null);
			this.setIsFluxograma(false);
			this.setExibirBotaoVoltar(false);
			retorno = PAGE_PACIENTE_EVOLUCAO;
		}else{
			retorno = voltarPara != null ? voltarPara : "voltaFluxogramaExames";
		}				
		return retorno;
	}

	
	public String abrirCancelarExames(){
		return PAGE_CANCELAR_EXAME;
	}
	
	public void colapsePanel(){
		if(openToggle == VALOR_ACCORDION_FLUXOGRAMA_ABERTO){
			openToggle=VALOR_ACCORDION_FLUXOGRAMA_FECHADO;
		} else {
			openToggle=VALOR_ACCORDION_FLUXOGRAMA_ABERTO;
		}	
	}
		
	/** getters e setters **/
	public PesquisaExamesFiltroVO getFiltro() {
		return filtro;
	}

	public void setFiltro(PesquisaExamesFiltroVO filtro) {
		this.filtro = filtro;
	}

	public boolean isExibirBotaoVoltar() {
		return exibirBotaoVoltar;
	}

	public void setExibirBotaoVoltar(boolean exibirBotaoVoltar) {
		this.exibirBotaoVoltar = exibirBotaoVoltar;
	}

	public boolean isFiltroAberto() {
		return filtroAberto;
	}

	public void setFiltroAberto(boolean filtroAberto) {
		this.filtroAberto = filtroAberto;
	}

	public AghAtendimentos getAtendimento() {
		return atendimento;
	}

	public void setAtendimento(AghAtendimentos atendimento) {
		this.atendimento = atendimento;
	}

	public List<FluxogramaLaborarorialDadosVO> getDadosFluxograma() {
		return dadosFluxograma;
	}

	public void setDadosFluxograma(List<FluxogramaLaborarorialDadosVO> dadosFluxograma) {
		this.dadosFluxograma = dadosFluxograma;
	}

	public List<Date> getDatasExibicao() {
		return datasExibicao;
	}

	public void setDatasExibicao(List<Date> datasExibicao) {
		this.datasExibicao = datasExibicao;
	}

	public boolean isResultAberto() {
		return resultAberto;
	}

	public void setResultAberto(boolean resultAberto) {
		this.resultAberto = resultAberto;
	}
	
	public String getVoltarPara() {
		return voltarPara;
	}

	public void setVoltarPara(String voltarPara) {
		this.voltarPara = voltarPara;
	}

	public Integer getSoeSeq() {
		return soeSeq;
	}

	public void setSoeSeq(Integer soeSeq) {
		this.soeSeq = soeSeq;
	}

	public Integer getProntuario() {
		return prontuario;
	}

	public void setProntuario(Integer prontuario) {
		this.prontuario = prontuario;
	}

	public Boolean getHistorico() {
		return historico;
	}

	public void setHistorico(Boolean historico) {
		this.historico = historico;
	}

	public Boolean getInibeAlteracaoProntuario() {
		return inibeAlteracaoProntuario;
	}

	public void setInibeAlteracaoProntuario(Boolean inibeAlteracaoProntuario) {
		this.inibeAlteracaoProntuario = inibeAlteracaoProntuario;
	}

	public Boolean getDownloadLink() {
		return downloadLink;
	}

	public void setDownloadLink(Boolean downloadLink) {
		this.downloadLink = downloadLink;
	}

	public Boolean getPolPage() {
		return polPage;
	}

	public void setPolPage(Boolean polPage) {
		this.polPage = polPage;
	}
	
	public Integer getOpenToggle() {
		return openToggle;
	}

	public void setOpenToggle(Integer openToggle) {
		this.openToggle = openToggle;
	}

	public Boolean getIsFluxograma() {
		return isFluxograma;
	}
	public void setIsFluxograma(Boolean isFluxograma) {
		this.isFluxograma = isFluxograma;
	}	
}