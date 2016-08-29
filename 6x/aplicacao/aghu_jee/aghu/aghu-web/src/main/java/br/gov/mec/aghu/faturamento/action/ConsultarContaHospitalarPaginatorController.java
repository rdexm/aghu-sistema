package br.gov.mec.aghu.faturamento.action;

import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.event.ValueChangeEvent;
import javax.inject.Inject;

import br.gov.mec.aghu.dominio.DominioSituacaoSSM;
import br.gov.mec.aghu.faturamento.business.IFaturamentoFacade;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.VFatContaHospitalarPac;
import br.gov.mec.aghu.paciente.action.PesquisaPacienteController;
import br.gov.mec.aghu.paciente.business.IPacienteFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.core.utils.DateUtil;


public class ConsultarContaHospitalarPaginatorController extends ActionController implements ActionPaginator {

	private static final long serialVersionUID = 8188751583868367770L;

	@EJB
	private IFaturamentoFacade faturamentoFacade;

	@EJB
	private IPacienteFacade pacienteFacade;
	
	@Inject 
	private PesquisaPacienteController pesquisaPacienteController;
	
	@Inject @Paginator
	private DynamicDataModel<VFatContaHospitalarPac> dataModel;
	
	private final String PageConsultarContaHospitalar = "consultarContaHospitalar";
	private final String PageConsultarContaHospitalarList = "consultarContaHospitalarList";
	private final String PagePesquisaFonetica = "paciente-pesquisaPacienteComponente";
	
	
	// FILTROS
	private Integer prontuario;
	private Integer codigo;
	private Integer contaHospitalar;
	private String codigoDcih;
	private Long numeroAih;
	private Date competencia;
	private AipPacientes paciente;
	private VFatContaHospitalarPac contaSelecionada;
	private String voltar;
	
	public enum ConsultarContaHospitalarPaginatorControllerExceptionCode implements BusinessExceptionCode {
		MENSAGEM_INFORME_PELO_MENOS_UM_CAMPO_PESQUISA
	}	
	
	@PostConstruct
	public void init() {
		begin(conversation);
	}

	public void inicio() {
		if (voltar == null) {
			if (prontuario != null) {
				obterPacientePorProntuario();
			} else if (codigo != null) {
				obterPacientePorCodigo();
			} else if (contaHospitalar != null) {
				obterPacientePorContaHospitalar();
			} else if (numeroAih != null) {
				obterPacientePorAih();
			}
		}
		voltar = null;
	}	
	
	public void obterPacientePorCodigo(){
		limparPesquisa();
		if (codigo != null) {
			paciente = pacienteFacade.obterPacientePorCodigo(codigo);
			if (paciente != null) {
				prontuario = paciente.getProntuario();
			} 
			else {
				apresentarMsgNegocio(Severity.ERROR,"FAT_00731");
			}
		}
	}

	public String visualizarConta() {
		return PageConsultarContaHospitalar;
	}
	
	public void obterPacientePorProntuario(){
		final Integer filtro = prontuario;
		limparPesquisa();
		paciente = pacienteFacade.obterPacientePorProntuario(filtro);
		if (paciente != null) {
			codigo = paciente.getCodigo();
			prontuario = paciente.getProntuario();
		}
		else {
			apresentarMsgNegocio(Severity.ERROR,"MENSAGEM_PACIENTE_PRONTUARIO_NAO_ENCONTRADO");
		}
	}
	
	public void obterPacientePorContaHospitalar(){
		if (contaHospitalar != null) {
			final VFatContaHospitalarPac contaHospitalarPac = faturamentoFacade
					.obterVFatContaHospitalarPac(contaHospitalar);
			if (contaHospitalarPac != null) {
				paciente = contaHospitalarPac.getPaciente();
				codigo = contaHospitalarPac.getPacCodigo();
				prontuario = contaHospitalarPac.getPacProntuario();
			}
		} else if (numeroAih != null) {
			this.obterPacientePorAih();
		}
	}
	
	public void obterPacientePorAih() {
		if (numeroAih != null) {
			final VFatContaHospitalarPac contaHospitalarPac = faturamentoFacade
					.buscarPrimeiraAihPaciente(numeroAih);

			if (contaHospitalarPac != null) {
				paciente = pacienteFacade.obterAipPacientesPorChavePrimaria(contaHospitalarPac.getPaciente().getCodigo());
				codigo = paciente.getCodigo();
				prontuario = paciente.getProntuario();
			}
		}
	}
	
	public String redirecionarPesquisaFonetica() {	
		this.pesquisaPacienteController.setCameFrom(PageConsultarContaHospitalarList);
		return PagePesquisaFonetica;
	}
	
	public void pesquisaPaciente(ValueChangeEvent event){
		try {
			paciente = pacienteFacade.pesquisarPacienteComponente(event.getNewValue(), event.getComponent().getId());
			codigo = paciente.getCodigo();
			prontuario = paciente.getProntuario();
		}catch(BaseException e){
			apresentarExcecaoNegocio(e);
		}
	}
	
	public void pesquisar() {
		if (prontuario!= null || contaHospitalar != null || 
		   (codigoDcih != null && !codigoDcih.trim().equals("")) || 
			numeroAih != null || competencia != null || codigo != null) {
			obterPacientePorContaHospitalar();
			
			this.dataModel.reiniciarPaginator();
		}
		else {
			apresentarMsgNegocio(Severity.ERROR,
					ConsultarContaHospitalarPaginatorControllerExceptionCode.MENSAGEM_INFORME_PELO_MENOS_UM_CAMPO_PESQUISA.toString());
		}
	}

	public void limparPesquisa() {
		this.prontuario = null;
		this.contaHospitalar = null;
		this.codigoDcih = null;
		this.numeroAih = null;
		this.paciente = null;
		this.competencia = null;
		this.codigo = null;
		this.dataModel.setPesquisaAtiva(false);

	}
	
	@Override
	public Long recuperarCount() {		
		return this.faturamentoFacade.pesquisarContaHospitalarCount(
				prontuario, contaHospitalar, codigoDcih, numeroAih, competencia, codigo);
	}

	@Override
	public List<VFatContaHospitalarPac> recuperarListaPaginada(Integer firstResult,
			Integer maxResult, String orderProperty, boolean asc) {

		List<VFatContaHospitalarPac> lista = this.faturamentoFacade.pesquisarContaHospitalar(firstResult,
				maxResult, orderProperty, asc, prontuario, contaHospitalar, codigoDcih, numeroAih, competencia, codigo, null);
		
		for (VFatContaHospitalarPac aux : lista) {
			if (aux.getConvenioSaudePlano() != null) {
				aux.setSsmRealizado(faturamentoFacade.buscaSSM(
						aux.getCthSeq(), 
						aux.getConvenioSaudePlano().getId().getCnvCodigo(), 
						aux.getConvenioSaudePlano().getId().getSeq(), 
						DominioSituacaoSSM.R));
			
				aux.setSsmSolicitado(faturamentoFacade.buscaSSM(
						aux.getCthSeq(), 
						aux.getConvenioSaudePlano().getId().getCnvCodigo(), 
						aux.getConvenioSaudePlano().getId().getSeq(), 
						DominioSituacaoSSM.S));
			
				if (aux.getProcedimentoHospitalarInterno() != null && 
					aux.getProcedimentoHospitalarInternoRealizado() != null) {
					aux.setFinanciamentoRealizado(faturamentoFacade.buscaSsmComplexidade(
							aux.getCthSeq(), 
							aux.getConvenioSaudePlano().getId().getCnvCodigo(), 
							aux.getConvenioSaudePlano().getId().getSeq(), 
							aux.getProcedimentoHospitalarInterno().getSeq(), 
							aux.getProcedimentoHospitalarInternoRealizado().getSeq(), 
							DominioSituacaoSSM.R));
					
					aux.setFinanciamentoSolicitado(faturamentoFacade.buscaSsmComplexidade(
							aux.getCthSeq(), 
							aux.getConvenioSaudePlano().getId().getCnvCodigo(), 
							aux.getConvenioSaudePlano().getId().getSeq(), 
							aux.getProcedimentoHospitalarInterno().getSeq(), 
							aux.getProcedimentoHospitalarInternoRealizado().getSeq(), 
							DominioSituacaoSSM.S));
				}
				
				if (aux.getDciCpeAno() != null && aux.getDciCpeMes() != null) {
					aux.setCompetencia(DateUtil.obterData(aux.getDciCpeAno(), aux.getDciCpeMes() - 1, 1));
				}
			}
			aux.setSituacaoSms(faturamentoFacade.buscaSitSms(aux.getCthSeq()));
		}
		
		//55069
		if(lista != null && !lista.isEmpty()){
			if(faturamentoFacade.isPacienteTransplantado(paciente)){
				// ATENCÃO. Paciente é transplantado!
				apresentarMsgNegocio(Severity.INFO,"MBC_00537", paciente.getNome());
			}
		}
		
		return lista;
	}

	public Integer getProntuario() {
		return prontuario;
	}

	public void setProntuario(Integer prontuario) {
		this.prontuario = prontuario;
	}

	public Integer getContaHospitalar() {
		return contaHospitalar;
	}

	public void setContaHospitalar(Integer contaHospitalar) {
		this.contaHospitalar = contaHospitalar;
	}

	public String getCodigoDcih() {
		return codigoDcih;
	}

	public void setCodigoDcih(String codigoDcih) {
		this.codigoDcih = codigoDcih;
	}

	public Long getNumeroAih() {
		return numeroAih;
	}

	public void setNumeroAih(Long numeroAih) {
		this.numeroAih = numeroAih;
	}

	public Date getCompetencia() {
		return competencia;
	}

	public void setCompetencia(Date competencia) {
		this.competencia = competencia;
	}

	public Integer getCodigo() {
		return codigo;
	}

	public void setCodigo(Integer codigo) {
		this.codigo = codigo;
	}

	public String getVoltar() {
		return voltar;
	}

	public void setVoltar(String voltar) {
		this.voltar = voltar;
	}

	public DynamicDataModel<VFatContaHospitalarPac> getDataModel() {
		return dataModel;
	}

	public void setDataModel(DynamicDataModel<VFatContaHospitalarPac> dataModel) {
		this.dataModel = dataModel;
	}

	public VFatContaHospitalarPac getContaSelecionada() {
		return contaSelecionada;
	}

	public void setContaSelecionada(VFatContaHospitalarPac contaSelecionada) {
		this.contaSelecionada = contaSelecionada;
	}

	public String getPageConsultarContaHospitalarList() {
		return PageConsultarContaHospitalarList;
	}
	
	public String getPageConsultarContaHospitalar() {
		return PageConsultarContaHospitalar;
	}

	public AipPacientes getPaciente() {
		return paciente;
	}

	public void setPaciente(AipPacientes paciente) {
		this.paciente = paciente;
	}
}
