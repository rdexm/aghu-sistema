package br.gov.mec.aghu.certificacaodigital.action;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.certificacaodigital.business.ICertificacaoDigitalFacade;
import br.gov.mec.aghu.dominio.DominioSituacaoVersaoDocumento;
import br.gov.mec.aghu.dominio.DominioTipoDocumento;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.FccCentroCustos;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.VAghVersaoDocumento;
import br.gov.mec.aghu.paciente.business.IPacienteFacade;
import br.gov.mec.aghu.paciente.prontuarioonline.vo.NodoPOLVO;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.business.SelectionQualifier;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.utils.DateUtil;



public class DocumentosPacientePaginatorController extends ActionController implements ActionPaginator {

	@Inject @Paginator
	private DynamicDataModel<VAghVersaoDocumento> dataModel;

	private static final long serialVersionUID = -3693454278076947303L;
	private static final String PAGE_VISUALIZAR_DOC_CERTIF="pol-visualizarDocumentoCertificado";
	private static final String PAGE_DOCUMETO_ORIGINAL="certificacaodigital-visualizarDocumentoOriginal";
	


	@EJB
	private ICertificacaoDigitalFacade certificacaoDigitalFacade;

	@EJB
	private IPacienteFacade pacienteFacade;
	
	@EJB
	protected IParametroFacade parametroFacade;

	@Inject
	private TransferirDocumentoPacienteController transferirDocumentoPacienteController;	
	
	@Inject @SelectionQualifier @RequestScoped
	private NodoPOLVO itemPOL;
	
	private Integer prontuario;
	private Date dataInicial;
	private Date dataFinal;
	private Short vinculo;
	private RapServidores responsavel;
	private FccCentroCustos servico;
	private AipPacientes paciente;
	private DominioSituacaoVersaoDocumento situacao;
	private DominioTipoDocumento tipoDocumento;
	private boolean prontuarioOnLine = false;
	private String origem;
	private static Calendar referenceCalendar = Calendar.getInstance();	

	private List<DominioTipoDocumento> listaTipoDocumentoAtivoItens;
	
	/** 
	 * Comparator usado na lista de Centro de Custos
	 */
	private static final Comparator<FccCentroCustos> COMPARATOR_CENTRO_CUSTO_DESCRICAO = new Comparator<FccCentroCustos>() {
		@Override
		public int compare(FccCentroCustos o1, FccCentroCustos o2) {
			return o1.getDescricao().compareTo(o2.getDescricao());
		}
	};

	@PostConstruct
	protected void inicializar(){
	 this.begin(conversation);
	 listaTipoDocumentoAtivoItens = certificacaoDigitalFacade.listarTipoDocumentoPacienteAtivoDistinct();
	}	
	
	
	public String abreDocumentoOriginal(){
		return PAGE_DOCUMETO_ORIGINAL;
	}	
	
	public void iniciar() {

		if (transferirDocumentoPacienteController.isFecharModal()) {
			this.transferirDocumentoPacienteController.setFecharModal(false);
			this.pesquisar();
		}

		if (this.dataInicial == null && this.dataFinal == null) {
			this.carregarPeriodoInicial();
		}
	
	}
	
	public void iniciarPOL() {
		if (itemPOL!=null){
			prontuario = itemPOL.getProntuario();
		}
		
		this.setProntuarioOnLine(true);
		this.carregarPeriodoInicial();
		this.setPaciente(pacienteFacade.obterPacientePorProntuario(prontuario));
	}
	
	public String abrirVisualizarDocumentoCertificado(){
		return PAGE_VISUALIZAR_DOC_CERTIF;
	}
	
	public void carregarPeriodoInicial() {
		Calendar dtInicial = Calendar.getInstance();
		
		Integer periodoMaximoEmDias = 0; 		
		
		try {
			periodoMaximoEmDias = this.certificacaoDigitalFacade.obterPeriodoPadraoPesquisa();			
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}
		
		try {			
			periodoMaximoEmDias = this.certificacaoDigitalFacade
				.obterPeriodoMaximoPesquisa() - 1;			
			dtInicial.add(Calendar.DAY_OF_MONTH, periodoMaximoEmDias*(-1));
			dataInicial = dtInicial.getTime();
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);										
			dtInicial.add(Calendar.DAY_OF_MONTH, periodoMaximoEmDias*(-1));
			dataInicial = dtInicial.getTime();
		}
					
		Calendar dtFinal = Calendar.getInstance();
		dataFinal = dtFinal.getTime();
	}
	
	public void carregarPeriodoFinal() {
		
		Calendar dtInicial = (Calendar) referenceCalendar.clone();
		
		Integer periodoMaximoEmDias = 0; 		
		
		try {
			periodoMaximoEmDias = this.certificacaoDigitalFacade.obterPeriodoPadraoPesquisa();			
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}
		
		try {			
			periodoMaximoEmDias = this.certificacaoDigitalFacade
				.obterPeriodoMaximoPesquisa() - 1;		
			dtInicial.setTime(dataInicial);
			dtInicial.add(Calendar.DAY_OF_MONTH, periodoMaximoEmDias);
			dataFinal = dtInicial.getTime();
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);										
			dtInicial.add(Calendar.DAY_OF_MONTH, periodoMaximoEmDias);
			dataFinal = dtInicial.getTime();
		}
						
	}
	
	@Override
	public Long recuperarCount() {
		Integer count = this.certificacaoDigitalFacade.pesquisarDocumentosPaciente(0, 0,
				null, true, paciente, dataInicial, dataFinal, responsavel,
				servico, situacao, tipoDocumento).size();
		return count.longValue();
	}

	@Override
	public List<VAghVersaoDocumento> recuperarListaPaginada(
			Integer firstResult, Integer maxResult, String orderProperty,
			boolean asc) {

		List<VAghVersaoDocumento> listaVersaoDocumento = this.certificacaoDigitalFacade
				.pesquisarDocumentosPaciente(firstResult, maxResult,
						orderProperty, asc, paciente, dataInicial, dataFinal,
						responsavel, servico, situacao, tipoDocumento);

		// CoreUtil.ordenarLista(listaVersaoDocumento, orderProperty, asc);

		return listaVersaoDocumento;
	}

	public void pesquisar() {
		try {			
			this.atualizarDatasComHorario();						
			this.certificacaoDigitalFacade
					.validarFiltrosPesquisaDocumentosPaciente(paciente,
							dataInicial, dataFinal, responsavel, servico,
							situacao, tipoDocumento);			
			try{
				this.certificacaoDigitalFacade.verificarPeriodoPesquisa(dataInicial, dataFinal);				
			}catch (BaseException e){
				apresentarExcecaoNegocio(e);
				Integer qtDias = this.certificacaoDigitalFacade
						.obterPeriodoPadraoPesquisa();

				AghParametros aghParametro = parametroFacade
						.buscarAghParametro(AghuParametrosEnum.P_AGHU_NRO_MAX_DIAS_PESQ_CERT_DIGITAL);

				if (aghParametro == null) {
					dataFinal = DateUtil.adicionaDias(dataInicial, qtDias);
				}
			}
			dataModel.reiniciarPaginator();			
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}
	}
	
	public List<AipPacientes> pesquisarPaciente(String objParam)
			throws ApplicationBusinessException {		
		
		List<AipPacientes> pacientes = new ArrayList<AipPacientes>();	
		String strPesquisa = (String) objParam;
		Integer prontuario = null;
		
		if (NumberUtils.isNumber(strPesquisa)) {
			if (!StringUtils.isBlank(strPesquisa)) {
				prontuario = Integer.valueOf(strPesquisa);
				this.setPaciente(pacienteFacade.obterPacientePorProntuario(prontuario));
			}
	
			if (this.paciente != null) {
				pacientes.add(this.paciente);
			}
		}
		return pacientes;
	}	
	
	private void atualizarDatasComHorario(){

		Calendar dtInicial = Calendar.getInstance();
		dtInicial.setTime(dataInicial);
		dtInicial.set(Calendar.HOUR_OF_DAY,0);
		dtInicial.set(Calendar.MINUTE,0);
		dtInicial.set(Calendar.SECOND,0);
		
		dataInicial.setTime(dtInicial.getTimeInMillis());
		
		Calendar dtFinal = Calendar.getInstance();
		dtFinal.setTime(dataFinal);
		dtFinal.set(Calendar.HOUR_OF_DAY,23);
		dtFinal.set(Calendar.MINUTE,59);
		dtFinal.set(Calendar.SECOND,59);
		
		dataFinal.setTime(dtFinal.getTimeInMillis());
		
	}
	
	/**
	 * limpar campos no posDeleteAction da suggestion do prontuário
	 */
	public void limparCampos(){
		this.setPaciente(null);
	}
	
	/**
	 * limpar campos através do botão 'limpar'
	 */
	public void limpar() {
		
		atualizarDatasComHorario();
		this.setVinculo(null);
		this.setResponsavel(null);
		this.setServico(null);
		this.setSituacao(null);
		this.setTipoDocumento(null);
		this.getDataModel().limparPesquisa();
		
		// somente limpa as informações do paciente quando a pesquisa não for
		// realizada do prontuário on-line.
		if (!this.prontuarioOnLine) {
			this.setProntuario(null);
			this.setPaciente(null);
		}
		this.carregarPeriodoInicial();	
	}
	
	public List<RapServidores> obterServidor(String parametroConsulta) {
		return this
				.returnSGWithCount(
						this.certificacaoDigitalFacade
								.pesquisarServidorComCertificacaoDigital(parametroConsulta),
						this.certificacaoDigitalFacade
								.pesquisarServidorComCertificacaoDigitalCount(parametroConsulta));
	}
	
	public List<FccCentroCustos> obterCentroCusto(String parametroConsulta) {

		List<FccCentroCustos> listaCentroCustos = this.returnSGWithCount(this.certificacaoDigitalFacade
				.pesquisarCentroCustoComCertificadoDigital(parametroConsulta), this.pesquisarCentroCustoComCertificadoDigitalCount(parametroConsulta)); 

		Collections.sort(listaCentroCustos, COMPARATOR_CENTRO_CUSTO_DESCRICAO);
		return listaCentroCustos;
	}
	
	public Long pesquisarCentroCustoComCertificadoDigitalCount(
			Object objPesquisa) {
		return this.certificacaoDigitalFacade
				.pesquisarCentroCustoComCertificadoDigitalCount(objPesquisa);
	}

	public void atualizarVinculo() {
		if (responsavel != null) {
			this.setVinculo(responsavel.getId().getVinCodigo());
		}
	}

	public void limparVinculo() {
		this.setVinculo(null);
	}

	public String realizarChamada(String target){
		return target;
	}
	
	public Integer getProntuario() {
		return prontuario;
	}

	public void setProntuario(Integer prontuario) {
		this.prontuario = prontuario;
	}

	public Date getDataInicial() {
		return dataInicial;
	}

	public void setDataInicial(Date dataInicial) {
		this.dataInicial = dataInicial;
	}

	public Date getDataFinal() {
		return dataFinal;
	}

	public void setDataFinal(Date dataFinal) {
		this.dataFinal = dataFinal;
	}

	public Short getVinculo() {
		return vinculo;
	}

	public void setVinculo(Short vinculo) {
		this.vinculo = vinculo;
	}

	public RapServidores getResponsavel() {
		return responsavel;
	}

	public void setResponsavel(RapServidores responsavel) {
		this.responsavel = responsavel;
	}

	public FccCentroCustos getServico() {
		return servico;
	}

	public void setServico(FccCentroCustos servico) {
		this.servico = servico;
	}

	public DominioSituacaoVersaoDocumento getSituacao() {
		return situacao;
	}

	public void setSituacao(DominioSituacaoVersaoDocumento situacao) {
		this.situacao = situacao;
	}

	public AipPacientes getPaciente() {
		return paciente;
	}

	public void setPaciente(AipPacientes paciente) {
		this.paciente = paciente;
	}	
	
	public DominioTipoDocumento getTipoDocumento() {
		return tipoDocumento;
	}

	public void setTipoDocumento(DominioTipoDocumento tipoDocumento) {
		this.tipoDocumento = tipoDocumento;
	}

	public boolean isProntuarioOnLine() {
		return prontuarioOnLine;
	}

	public void setProntuarioOnLine(boolean prontuarioOnLine) {
		this.prontuarioOnLine = prontuarioOnLine;
	}

	public String getOrigem() {
		return origem;
	}

	public void setOrigem(String origem) {
		this.origem = origem;
	}	
		
	public static void setReferenceCalendar(Calendar dataInicial){
		referenceCalendar = dataInicial;
	} 

	public DynamicDataModel<VAghVersaoDocumento> getDataModel() {
	 return dataModel;
	}

	public void setDataModel(DynamicDataModel<VAghVersaoDocumento> dataModel) {
	 this.dataModel = dataModel;
	}

	public List<DominioTipoDocumento> getListaTipoDocumentoAtivoItens() {
		return listaTipoDocumentoAtivoItens;
	}

	public void setListaTipoDocumentoAtivoItens(List<DominioTipoDocumento> listaTipoDocumentoAtivoItens) {
		this.listaTipoDocumentoAtivoItens = listaTipoDocumentoAtivoItens;
	}

}
