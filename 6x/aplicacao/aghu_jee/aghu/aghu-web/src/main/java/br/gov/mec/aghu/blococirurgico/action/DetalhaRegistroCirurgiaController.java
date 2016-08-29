package br.gov.mec.aghu.blococirurgico.action;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import br.gov.mec.aghu.blococirurgico.business.IBlocoCirurgicoFacade;
import br.gov.mec.aghu.casca.business.ICascaFacade;
import br.gov.mec.aghu.internacao.business.IInternacaoFacade;
import br.gov.mec.aghu.model.MbcAnestesiaCirurgias;
import br.gov.mec.aghu.model.MbcCirurgias;
import br.gov.mec.aghu.model.MbcDestinoPaciente;
import br.gov.mec.aghu.model.MbcProcEspPorCirurgias;
import br.gov.mec.aghu.paciente.prontuarioonline.action.RelatorioDescricaoCirurgiaController;
import br.gov.mec.aghu.paciente.prontuarioonline.action.RelatorioListarCirurgiasPdtDescProcCirurgiaController;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;


public class DetalhaRegistroCirurgiaController extends ActionController {

	@PostConstruct
	protected void inicializar(){
	 this.begin(conversation);	 
	}


	private static final long serialVersionUID = -4875456670019542338L;

	@EJB
	private IBlocoCirurgicoFacade blocoCirurgicoFacade;

	@EJB
	private IInternacaoFacade internacaoFacade;

	@Inject
	private ExtratoCirurgiaController extratoCirurgiaController;

	@Inject
	private SolicitacaoHemoterapicaController solicitacaoHemoterapicaController;

	@Inject
	private ProfissionaisCirurgiaListController profissionaisCirurgiaListController;
	
	@Inject
	private RelatorioDescricaoCirurgiaController relatorioDescricaoCirurgiaController;
	
	@Inject
	private RelatorioListarCirurgiasPdtDescProcCirurgiaController relatorioListarCirurgiasPdtDescProcCirurgiaController;
	
	@EJB
	private ICascaFacade cascaFacade;

	private Integer crgSeq;
	private Boolean edicao;
	//private Boolean botaoDescricaoCirurgica;
	private String cameFrom; // variavel que armazena o retorno da tela Detalhar Registro de Cirurgia
	private MbcCirurgias cirurgia;
	private Integer abaAtiva;
	private String convenioPlano;
	private String idadePaciente;
	private String localPaciente;
	private List<MbcProcEspPorCirurgias> mbcProcEspPorCirurgiasList;
	private List<MbcAnestesiaCirurgias> mbcAnestesiaCirurgiasList;

	//private Short seqp;
	private String voltarPara;
	private Boolean print;
	
	//#24877
	private static final String PAGE_RELATORIO_LISTAR_CIRURGIAS = "blococirurgico-relatorio-CIR";	
	private static final String PAGE_RELATORIO_LISTAR_CIRURGIAS_PDT = "blococirurgico-relatorio-PDT";
	private static final String PAGE_DETALHAR_CIRURGIA = "blococirurgico-iniciarEdicao";

	public void inicio() {

		edicao = cascaFacade.temPermissao(this.obterLoginUsuarioLogado(), "detalheRegistroDeCirurgias","editar");
		cirurgia = blocoCirurgicoFacade.obterCirurgiaComUnidadePacienteDestinoPorCrgSeq(crgSeq);
		
		if (cirurgia.getConvenioSaudePlano() != null){
		   convenioPlano = internacaoFacade.obterConvenioPlano(cirurgia.getConvenioSaudePlano().getId().getSeq(), cirurgia.getConvenioSaudePlano().getId().getCnvCodigo());
		}
		//setIdadePaciente(blocoCirurgicoFacade.mbccIdaAnoMesDia(cirurgia.getPaciente().getCodigo()));
		setIdadePaciente(blocoCirurgicoFacade.mbccIdadeExtFormat(cirurgia.getPaciente().getDtNascimento(), cirurgia.getData()));
		localPaciente = blocoCirurgicoFacade.obterQuarto(cirurgia.getPaciente().getCodigo());
		mbcProcEspPorCirurgiasList = blocoCirurgicoFacade.pesquisarProcedimentoCirurgicoEscalaCirurgica(cirurgia);
		mbcAnestesiaCirurgiasList = blocoCirurgicoFacade.pesquisarAnestesiaCirurgiaTipoAnestesiaPorCrgSeq(crgSeq, null);

		extratoCirurgiaController.inicializaParametros(crgSeq);
		solicitacaoHemoterapicaController.inicializaParametros(crgSeq);
		profissionaisCirurgiaListController.inicializar(crgSeq);

		if (abaAtiva == null) {
			setAbaAtiva(0);
		}
		
		//seqp = blocoCirurgicoFacade.obterMenorSeqpDescricaoCirurgicaPorCrgSeq(cirurgia.getSeq());
		/*if(seqp != null){
			botaoDescricaoCirurgica = Boolean.TRUE;
		}else{
			botaoDescricaoCirurgica = Boolean.FALSE;
		}*/
	
	}
	
	
	public List<MbcDestinoPaciente> listarDestinos(final String strPesquisa) {
		return this.returnSGWithCount(this.blocoCirurgicoFacade.pesquisarDestinoPacientePorSeqOuDescricao(strPesquisa, 0, 100, null, true, null),listarDestinosCount(strPesquisa));
	}

	public Long listarDestinosCount(final String strPesquisa) {
		return this.blocoCirurgicoFacade.pesquisarDestinoPacientePorSeqOuDescricaoCount((String) strPesquisa);
	}

	public String gravar() {
		String msg;
		try {
			msg = blocoCirurgicoFacade.gravarMbcCirurgias(cirurgia);
			apresentarMsgNegocio(Severity.INFO, msg);
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}
		return null;
	}

	public String cancelar() {
		return cameFrom;
	}

	public String encaminharDescricaoCirurgica() {
		try{
			String retorno = this.blocoCirurgicoFacade.mbcImpressao(getCrgSeq());
			if(retorno.equals("PDT")){
				relatorioListarCirurgiasPdtDescProcCirurgiaController.setCrgSeq(getCrgSeq());
				relatorioListarCirurgiasPdtDescProcCirurgiaController.setVoltarPara(PAGE_DETALHAR_CIRURGIA);
				return PAGE_RELATORIO_LISTAR_CIRURGIAS_PDT;
			} else if (retorno.equals("CIR")){
				relatorioDescricaoCirurgiaController.setCrgSeq(getCrgSeq());
				relatorioDescricaoCirurgiaController.setVoltarPara(PAGE_DETALHAR_CIRURGIA);
				return PAGE_RELATORIO_LISTAR_CIRURGIAS;
			}	
			
			
		}catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
		return null;
	}

	public void limpar() {
		setAbaAtiva(0);
	}
	
	// Getters and Setters
	public void setCrgSeq(Integer crgSeq) {
		this.crgSeq = crgSeq;
	}

	public Integer getCrgSeq() {
		return crgSeq;
	}

	public void setEdicao(Boolean edicao) {
		this.edicao = edicao;
	}

	public Boolean getEdicao() {
		return edicao;
	}

	public void setCirurgia(MbcCirurgias cirurgia) {
		this.cirurgia = cirurgia;
	}

	public MbcCirurgias getCirurgia() {
		return cirurgia;
	}

	public Integer getAbaAtiva() {
		return abaAtiva;
	}

	public void setAbaAtiva(Integer abaAtiva) {
		this.abaAtiva = abaAtiva;
	}

	public void setConvenioPlano(String convenioPlano) {
		this.convenioPlano = convenioPlano;
	}

	public String getConvenioPlano() {
		return convenioPlano;
	}

	public void setIdadePaciente(String idadePaciente) {
		this.idadePaciente = idadePaciente;
	}

	public String getIdadePaciente() {
		return idadePaciente;
	}

	public void setLocalPaciente(String localPaciente) {
		this.localPaciente = localPaciente;
	}

	public String getLocalPaciente() {
		return localPaciente;
	}

	public void setMbcProcEspPorCirurgiasList(List<MbcProcEspPorCirurgias> mbcProcEspPorCirurgiasList) {
		this.mbcProcEspPorCirurgiasList = mbcProcEspPorCirurgiasList;
	}

	public List<MbcProcEspPorCirurgias> getMbcProcEspPorCirurgiasList() {
		return mbcProcEspPorCirurgiasList;
	}

	public void setMbcAnestesiaCirurgiasList(List<MbcAnestesiaCirurgias> mbcAnestesiaCirurgiasList) {
		this.mbcAnestesiaCirurgiasList = mbcAnestesiaCirurgiasList;
	}

	public List<MbcAnestesiaCirurgias> getMbcAnestesiaCirurgiasList() {
		return mbcAnestesiaCirurgiasList;
	}

	public String getVoltarPara() {
		return voltarPara;
	}

	public void setVoltarPara(String voltarPara) {
		this.voltarPara = voltarPara;
	}

	public Boolean getPrint() {
		return print;
	}

	public void setPrint(Boolean print) {
		this.print = print;
	}

	public String getCameFrom() {
		return cameFrom;
	}

	public void setCameFrom(String cameFrom) {
		this.cameFrom = cameFrom;
	}

}
