package br.gov.mec.aghu.farmacia.dispensacao.action;

import java.net.UnknownHostException;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.enterprise.inject.Instance;
import javax.faces.event.ValueChangeEvent;
import javax.inject.Inject;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.business.SelectionQualifier;
import br.gov.mec.aghu.core.commons.WebUtil;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.farmacia.business.IFarmaciaFacade;
import br.gov.mec.aghu.farmacia.dispensacao.business.IFarmaciaDispensacaoFacade;
import br.gov.mec.aghu.internacao.business.IInternacaoFacade;
import br.gov.mec.aghu.internacao.pesquisa.business.IPesquisaInternacaoFacade;
import br.gov.mec.aghu.model.AfaDispMdtoCbSps;
import br.gov.mec.aghu.model.AfaDispensacaoMdtos;
import br.gov.mec.aghu.model.AfaMedicamento;
import br.gov.mec.aghu.model.AfaTipoOcorDispensacao;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.paciente.business.IPacienteFacade;
import br.gov.mec.aghu.paciente.vo.CodPacienteFoneticaVO;


public class EstornaMedicamentoDispensadoPaginatorController extends ActionController{
	
	private static final long serialVersionUID = 4069474221866660610L;
	
	@Inject
	private IFarmaciaFacade farmaciaFacade;
	
	@Inject
	private IFarmaciaDispensacaoFacade farmaciaDispensacaoFacade;
	
	@Inject 
	private IPacienteFacade pacienteFacade;
	
	@Inject 
	private IPesquisaInternacaoFacade pesquisaInternacaoFacade;
	
	@Inject 
	private IParametroFacade parametroFacade;
	
	@Inject 
	private IInternacaoFacade internacaoFacade;

	private AipPacientes paciente;
	private Date dthrInternacao;
	private Date dthrAltaMedica;
	private AfaMedicamento medicamento;
	private Date dataDeReferenciaInicio;
	private Date dataDeReferenciaFim;
	private String etiqueta;
	private Boolean valorAlterado = false;
	private Integer codigoPaciente;
	private Integer numeroProntuario;

	//listaDispensacaoModificada não consiste que todos os objetos desta lista foram modificados
	private List<AfaDispensacaoMdtos> listaDispensacaoModificada;
	private List<AfaDispensacaoMdtos> listaDispensacaoOriginal;
	
	private List<AfaTipoOcorDispensacao> tiposOcorrenciaDispensacaoEstornado;
	
	private Boolean controleBtnGravar;
	private String indexRegistroProblema;
	//Armazena index do último registro selecionado
	//A última linha selecionada, mantinha-se com style selecionado, por este motivo força-se o style antigo
	private String indexRegistroUltimoClick;
	
	private String erroProcessarEtiqueta;
	private String msgSucessoEtiqueta;
	private String msgMedicamentoVencido;
	
	private Boolean pacienteSemAtendimento;
	
	private String urlBtVoltar;
	private Boolean encerraConversacaoBtVoltar;
	
	private Long seqAfaDispSelecionadaCheckBox;
	
	private Boolean ativo;
	private Boolean blockIniciar = Boolean.FALSE;
	
	@Inject
	@SelectionQualifier
	private Instance<CodPacienteFoneticaVO> codPacienteFonetica;
	
	@Inject
	private DispMdtosCbPaginatorController dispMdtosCbPaginatorController;
	
	private Short seqMotivoEstornoPrescricaNaoEletronica;
	
	@PostConstruct
	public void init(){
		begin(conversation);
	}
	
	// INICIAR
	public void iniciarPagina(){
	 
		this.limparMsgErroEtiqueta();
		this.controleBtnGravar = Boolean.FALSE;
		try {
			seqMotivoEstornoPrescricaNaoEletronica = 
				parametroFacade.obterAghParametro(
						AghuParametrosEnum.P_AGHU_MOTIVO_ESTORNO_MDTO_PMM).getVlrNumerico().shortValue();
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}

		if(!blockIniciar){
			if(seqAfaDispSelecionadaCheckBox != null){
				processarEstornoDispensacaoUnica();
			}else{
				processaBuscaPaciente();
			}
			
			tiposOcorrenciaDispensacaoEstornado = farmaciaFacade.pesquisarTipoOcorrenciasAtivasEstornadas(seqMotivoEstornoPrescricaNaoEletronica);

			if(dataDeReferenciaInicio == null){
				try {
					dataDeReferenciaInicio = farmaciaDispensacaoFacade.recuperarDataLimite();
				} catch (ApplicationBusinessException e) {
					apresentarExcecaoNegocio(e);
				}
			}
			if(dataDeReferenciaFim == null){
				dataDeReferenciaFim = new Date();
			}
		}
		blockIniciar = Boolean.FALSE;
	
	}
	public void pesquisaPaciente(ValueChangeEvent event){
		try {
			paciente = pacienteFacade.pesquisarPacienteComponente(event.getNewValue(), event.getComponent().getId());
			numeroProntuario = paciente.getProntuario();
			codigoPaciente = paciente.getCodigo();
			setarDatasInternacoes();
		}catch(BaseException e){
			apresentarExcecaoNegocio(e);
		}
	}
	public void setarDatasInternacoes(){
		if(paciente != null){
			paciente.setInternacoes(new HashSet<>(pesquisaInternacaoFacade.pesquisarInternacaoPorPaciente(codigoPaciente)));
		}
	}
	/**
	 * Encapsula os métodos necessários para consultar AfaDispMdto específica passada via parametro
	 */
	private void processarEstornoDispensacaoUnica() {
		try{
			paciente = pacienteFacade.obterPacientePorCodigo(codigoPaciente);
			setarDatasInternacoes();
			listaDispensacaoOriginal = this.farmaciaDispensacaoFacade.recuperarListaDispensacaoMedicamentos(dataDeReferenciaInicio, dataDeReferenciaFim, medicamento, paciente, Boolean.FALSE, seqAfaDispSelecionadaCheckBox);
			listaDispensacaoModificada = this.farmaciaDispensacaoFacade.recuperarListaDispensacaoMedicamentos(dataDeReferenciaInicio, dataDeReferenciaFim,medicamento, paciente, Boolean.TRUE, seqAfaDispSelecionadaCheckBox);
			//codigoPaciente = paciente.getCodigo();
			numeroProntuario = paciente.getProntuario();
			dataDeReferenciaInicio = listaDispensacaoOriginal.get(0).getDthrDispensacao();
			dataDeReferenciaFim = dataDeReferenciaInicio;
			medicamento = listaDispensacaoOriginal.get(0).getMedicamento();
			setAtivo(Boolean.TRUE);
		}catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
	}
	
	public String voltar() throws ApplicationBusinessException{
		farmaciaDispensacaoFacade.refresh(listaDispensacaoModificada);
		
		if("dispensacaoMdtosCodBarrasList".equals(urlBtVoltar)){
			dispMdtosCbPaginatorController.iniciarPagina();
		}
		return urlBtVoltar;
	}
	
	//##ACOES	
	public String estornarDispensacoesMdtoDaLista(){
		try {
			
			String nomeMicrocomputador = null;
			try {
				nomeMicrocomputador = super.getEnderecoRedeHostRemoto();
			} catch (UnknownHostException e) {
				apresentarExcecaoNegocio(new ApplicationBusinessException("NAO_FOI_POSSIVEL_RECUPERAR_COMPUTADOR", Severity.ERROR));
			}
			this.farmaciaDispensacaoFacade.realizaEstornoMedicamentoDispensadoDaLista(listaDispensacaoModificada, listaDispensacaoOriginal, nomeMicrocomputador);
			indexRegistroUltimoClick = "-1";
			this.controleBtnGravar = Boolean.FALSE;
			this.valorAlterado = Boolean.FALSE;
			efetuarPesquisaDispensacao();
			this.apresentarMsgNegocio(Severity.INFO, "MESSAGE_ESTORNO_LISTA_SUCESSO");
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
			indexRegistroProblema = getIndexComProblemaByListaTriagem(listaDispensacaoModificada);
		}
		
		iniciarPagina();
		return null;
	}
	
	
	public void estornarMedicamentoEtiquetaComCb(){
		this.limparEtiquetaMensagem();
		try {
			
			String nomeMicrocomputador = null;
			try {
				nomeMicrocomputador = super.getEnderecoRedeHostRemoto();
			} catch (UnknownHostException e) {
				apresentarExcecaoNegocio(new ApplicationBusinessException("NAO_FOI_POSSIVEL_RECUPERAR_COMPUTADOR", Severity.ERROR));
			}
			
			final String etiquetaFormatada = farmaciaDispensacaoFacade.validarCodigoBarrasEtiqueta(etiqueta);
			msgMedicamentoVencido = farmaciaDispensacaoFacade.validaSeMedicamentoVencidoByEtiqueta(etiquetaFormatada);
			farmaciaDispensacaoFacade.estornarMedicamentoDispensadoByEtiquetaComCb(etiquetaFormatada, nomeMicrocomputador);
			msgSucessoEtiqueta = WebUtil.initLocalizedMessage("MESSAGE_ESTORNO_ETIQUETA_SUCESSO", null);
		} catch (BaseException e) {
			String message =  WebUtil.initLocalizedMessage(e.getLocalizedMessage(), null, e.getParameters());
			erroProcessarEtiqueta = message;
		} finally {
			etiqueta = null;
		}
	}
	
	public void limparMsgErroEtiqueta(){
		this.erroProcessarEtiqueta = null;
		this.msgSucessoEtiqueta =  null;
		this.msgMedicamentoVencido = null;
	}
	
	public String cancelar(){
		
		return limpar();
	}
	
	public String limpar() {
		this.paciente = null;
		this.dthrInternacao = null;
		this.dthrAltaMedica = null;
		this.medicamento = null;
		this.dataDeReferenciaInicio = null;
		this.dataDeReferenciaFim = null;
		this.listaDispensacaoModificada = null;
		this.listaDispensacaoOriginal = null;
		this.setAtivo(false);
		this.controleBtnGravar = Boolean.FALSE;
		this.valorAlterado = Boolean.FALSE;
		this.codigoPaciente = null;
		this.numeroProntuario = null;
		this.erroProcessarEtiqueta = null;
		this.etiqueta = null;
		this.blockIniciar = Boolean.TRUE;
		//iniciarPagina();
		return null;
		//return "reload";
	}

	//##PESQUISAS
	
	/**
	 * Pesquisa Paciente para utilizar como filtro de busca de dispensação
	 * de medicamentos
	 * 
	 */
	
	public void limpaEProcessaBuscaPacProntuario(){
		codigoPaciente = null;
		paciente = null;
		processaBuscaPaciente();
	}
	
	public void processaBuscaPaciente(){
		if (paciente == null || paciente.getCodigo() == null) {
			CodPacienteFoneticaVO codPac = codPacienteFonetica.get();
			if (codPac != null && codPac.getCodigo() > 0) { 
				paciente = pacienteFacade.obterAipPacientesPorChavePrimaria(codPac.getCodigo());
			}else if(codigoPaciente != null){
				try {
					paciente = farmaciaDispensacaoFacade.obterPacienteComAtendimentoPorProntuarioOUCodigo(numeroProntuario,codigoPaciente);
					mensagemPacienteNaoEncontrado();
				} catch (ApplicationBusinessException e) {
					pacienteSemAtendimento = Boolean.TRUE;
					apresentarExcecaoNegocio(e);
				}
			}
			if (paciente != null) {
				numeroProntuario = paciente.getProntuario();
				codigoPaciente = paciente.getCodigo();
			}
		}else if(numeroProntuario != null){
			try {
				paciente = farmaciaDispensacaoFacade.obterPacienteComAtendimentoPorProntuarioOUCodigo(numeroProntuario,codigoPaciente);
				mensagemPacienteNaoEncontrado();
			} catch (ApplicationBusinessException e) {
				pacienteSemAtendimento = Boolean.TRUE;
				apresentarExcecaoNegocio(e);
			}
		}
		setarDatasInternacoes();
		setCodigoProntuarioPaciente();
			
	}
	
	private void setCodigoProntuarioPaciente() {
		if(paciente != null && paciente.getCodigo() != null){
			codigoPaciente = paciente.getCodigo();
			numeroProntuario = paciente.getProntuario();
		}else{
			codigoPaciente = null;
			numeroProntuario = null;
		}
	}
	
	private void mensagemPacienteNaoEncontrado() {
		if(paciente==null || (paciente!=null && paciente.getCodigo()==null)) {
			this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_NENHUM_PACIENTE_ENCONTRADO");
		}
	}
	
	public String chamarTelaPesquisaFonetica() {
		blockIniciar = Boolean.FALSE;
		paciente=null;
		return "paciente-pesquisaPacienteComponente";
	}
	
	public void limparEtiquetaMensagem() {
		limparMsgErroEtiqueta();
	}
	
	/**
	 * Pesquisa Medicamentos para utilizar como filtro de busca de dispensação
	 * de medicamentos
	 * i
	 * @param strPesquisa
	 * @return
	 */
	public List<AfaMedicamento> pesquisarMedicamentos(String strPesquisa){
		return farmaciaFacade.pesquisarListaMedicamentos(strPesquisa);
	}
	
	public void processaAfaOcorDispSelecao(AfaDispensacaoMdtos adm){
		try{
			farmaciaDispensacaoFacade.processaAfaTipoOcorBySeqInAfaDispMdtoEstorno(adm,seqMotivoEstornoPrescricaNaoEletronica);
			controleBtnGravarTrue();
		}catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
	}

	
	/**
	 * Busca dispensacao de medicamentos para estorno
	 * 
	 * @throws AGHUNegocioExceptionSemRollback
	 */
	public void efetuarPesquisaDispensacao() throws ApplicationBusinessException {
		//if(!Boolean.TRUE.equals(pacienteSemAtendimento)){
		try {
			paciente = null;
			if(numeroProntuario != null){
				paciente = pacienteFacade.obterPacientePorProntuario(numeroProntuario);
			}else{ 
				if(codigoPaciente != null){
					paciente = pacienteFacade.obterPacientePorCodigo(codigoPaciente);
				}
			}
			if(paciente == null){
				//getStatusMessages().addToControlFromResourceBundle("pesquisaPacienteComponent_pesqPacientePacCodigo", Severity.ERROR, "CAMPO_OBRIGATORIO", "Código Paciente");
				apresentarExcecaoNegocio(new ApplicationBusinessException("PACIENTE_CAMPO_OBRIGATORIO", Severity.ERROR));
				return;
			}
			codigoPaciente = paciente != null ? paciente.getCodigo() : null;
			numeroProntuario = paciente != null ? paciente.getProntuario() : null;
			paciente.setInternacoes(new HashSet<>(internacaoFacade.listarInternacoes(null, paciente.getProntuario())));
			this.farmaciaDispensacaoFacade.validarDatas(dataDeReferenciaInicio, dataDeReferenciaFim);
			indexRegistroProblema = "-1";
			indexRegistroUltimoClick = "-1";
			listaDispensacaoOriginal = this.farmaciaDispensacaoFacade
					.recuperarListaDispensacaoMedicamentos(
							dataDeReferenciaInicio, dataDeReferenciaFim,
							medicamento, paciente, false, null);
			listaDispensacaoModificada = this.farmaciaDispensacaoFacade
					.recuperarListaDispensacaoMedicamentos(
							dataDeReferenciaInicio, dataDeReferenciaFim,
							medicamento, paciente, true, null);
			if (listaDispensacaoModificada.size() > 0) {
				this.setAtivo(true);
			} else {
				this.setAtivo(false);
			}
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
	}

	/*private boolean validaSePacientePesquisado() {
		return (paciente == null || paciente.getProntuario() == null || paciente.getCodigo() == null);
	}*/
	
	public void controleBtnGravarTrue(){
		this.controleBtnGravar = Boolean.TRUE;
	}
	
	//##UTIL
	
	private String getIndexComProblemaByListaTriagem(
			List<AfaDispensacaoMdtos> lista) {
		for(AfaDispensacaoMdtos adm: lista){
			if(adm.getIndexItemSendoAtualizado() !=null && adm.getIndexItemSendoAtualizado()){
				return String.valueOf(lista.indexOf(adm));
			}
		}
		return null;
	}
	
	public Boolean verificarPossuiDispensacaoCodBarras(AfaDispensacaoMdtos dispensacaoMdto) {
		Boolean retorno = false;
		if(seqMotivoEstornoPrescricaNaoEletronica.toString().equals(dispensacaoMdto.getSeqAfaTipoOcorSelecionada())){
			return true;
		}

		Set<AfaDispMdtoCbSps> dispCodBarrasList = dispensacaoMdto.getAfaDispMdtoCbSpss();
		if (dispCodBarrasList != null && !dispCodBarrasList.isEmpty()) {
			retorno = true;
		}
		return retorno;	
	}
	
	// Getters and Setters
	// ===================

	public List<AfaDispensacaoMdtos> getListaDispensacaoModificada() {
		return listaDispensacaoModificada;
	}

	public void setListaDispensacaoModificada(
			List<AfaDispensacaoMdtos> listaDispensacaoModificada) {
		this.listaDispensacaoModificada = listaDispensacaoModificada;
	}

	public List<AfaDispensacaoMdtos> getListaDispensacaoOriginal() {
		return listaDispensacaoOriginal;
	}

	public void setListaDispensacaoOriginal(
			List<AfaDispensacaoMdtos> listaDispensacaoOriginal) {
		this.listaDispensacaoOriginal = listaDispensacaoOriginal;
	}

	public List<AfaTipoOcorDispensacao> getTiposOcorrenciaDispensacaoEstornado() {
		return tiposOcorrenciaDispensacaoEstornado;
	}

	public void setTiposOcorrenciaDispensacaoEstornadoo(
			List<AfaTipoOcorDispensacao> tiposOcorrenciaDispensacaoEstornado) {
		this.tiposOcorrenciaDispensacaoEstornado = tiposOcorrenciaDispensacaoEstornado;
	}

	public Boolean getControleBtnGravar() {
		return controleBtnGravar;
	}

	public void setControleBtnGravar(Boolean controleBtnGravar) {
		this.controleBtnGravar = controleBtnGravar;
	}
	
	public AipPacientes getPaciente() {
		return paciente;
	}

	public void setPaciente(AipPacientes paciente) {
		this.paciente = paciente;
	}

	public Date getDthrInternacao() {
		return dthrInternacao;
	}

	public void setDthrInternacao(Date dthrInternacao) {
		this.dthrInternacao = dthrInternacao;
	}

	public Date getDthrAltaMedica() {
		return dthrAltaMedica;
	}

	public void setDthrAltaMedica(Date dthrAltaMedica) {
		this.dthrAltaMedica = dthrAltaMedica;
	}

	public AfaMedicamento getMedicamento() {
		return medicamento;
	}

	public void setMedicamento(AfaMedicamento medicamento) {
		this.medicamento = medicamento;
	}

	public Date getDataDeReferenciaInicio() {
		return dataDeReferenciaInicio;
	}

	public void setDataDeReferenciaInicio(Date dataDeReferenciaInicio) {
		this.dataDeReferenciaInicio = dataDeReferenciaInicio;
	}

	public Date getDataDeReferenciaFim() {
		return dataDeReferenciaFim;
	}

	public void setDataDeReferenciaFim(Date dataDeReferenciaFim) {
		this.dataDeReferenciaFim = dataDeReferenciaFim;
	}

	public void setEtiqueta(String etiqueta) {
		this.etiqueta = etiqueta;
	}

	public String getEtiqueta() {
		return etiqueta;
	}

	public String getIndexRegistroProblema() {
		return indexRegistroProblema;
	}

	public void setIndexRegistroProblema(String indexRegistroProblema) {
		this.indexRegistroProblema = indexRegistroProblema;
	}

	public String getIndexRegistroUltimoClick() {
		return indexRegistroUltimoClick;
	}

	public void setIndexRegistroUltimoClick(String indexRegistroUltimoClick) {
		this.indexRegistroUltimoClick = indexRegistroUltimoClick;
	}

	public Boolean getValorAlterado() {
		return valorAlterado;
	}

	public void setValorAlterado(Boolean valorAlterado) {
		this.valorAlterado = valorAlterado;
	}

	public IPacienteFacade getPacienteFacade() {
		return pacienteFacade;
	}

	public void setPacienteFacade(IPacienteFacade pacienteFacade) {
		this.pacienteFacade = pacienteFacade;
	}

	public Integer getCodigoPaciente() {
		return codigoPaciente;
	}

	public void setCodigoPaciente(Integer codigoPaciente) {
		this.codigoPaciente = codigoPaciente;
	}

	public Integer getNumeroProntuario() {
		return numeroProntuario;
	}

	public void setNumeroProntuario(Integer numeroProntuario) {
		this.numeroProntuario = numeroProntuario;
	}

	public String getErroProcessarEtiqueta() {
		return erroProcessarEtiqueta;
	}

	public void setErroProcessarEtiqueta(String erroProcessarEtiqueta) {
		this.erroProcessarEtiqueta = erroProcessarEtiqueta;
	}

	public void setTiposOcorrenciaDispensacaoEstornado(
			List<AfaTipoOcorDispensacao> tiposOcorrenciaDispensacaoEstornado) {
		this.tiposOcorrenciaDispensacaoEstornado = tiposOcorrenciaDispensacaoEstornado;
	}

	public String getMsgSucessoEtiqueta() {
		return msgSucessoEtiqueta;
	}

	public void setMsgSucessoEtiqueta(String msgSucessoEtiqueta) {
		this.msgSucessoEtiqueta = msgSucessoEtiqueta;
	}

	public String getMsgMedicamentoVencido() {
		return msgMedicamentoVencido;
	}

	public void setMsgMedicamentoVencido(String msgMedicamentoVencido) {
		this.msgMedicamentoVencido = msgMedicamentoVencido;
	}

	public Boolean getPacienteSemAtendimento() {
		return pacienteSemAtendimento;
	}

	public void setPacienteSemAtendimento(Boolean pacienteSemAtendimento) {
		this.pacienteSemAtendimento = pacienteSemAtendimento;
	}

	public String getUrlBtVoltar() {
		return urlBtVoltar;
	}

	public Boolean getEncerraConversacaoBtVoltar() {
		return encerraConversacaoBtVoltar;
	}

	public void setUrlBtVoltar(String urlBtVoltar) {
		this.urlBtVoltar = urlBtVoltar;
	}

	public void setEncerraConversacaoBtVoltar(Boolean encerraConversacaoBtVoltar) {
		this.encerraConversacaoBtVoltar = encerraConversacaoBtVoltar;
	}

	public Long getSeqAfaDispSelecionadaCheckBox() {
		return seqAfaDispSelecionadaCheckBox;
	}

	public void setSeqAfaDispSelecionadaCheckBox(
			Long seqAfaDispSelecionadaCheckBox) {
		this.seqAfaDispSelecionadaCheckBox = seqAfaDispSelecionadaCheckBox;
	}

	public Boolean getAtivo() {
		return ativo;
	}

	public void setAtivo(Boolean ativo) {
		this.ativo = ativo;
	}

	public Boolean getBlockIniciar() {
		return blockIniciar;
	}

	public void setBlockIniciar(Boolean blockIniciar) {
		this.blockIniciar = blockIniciar;
	}
	public Short getSeqMotivoEstornoPrescricaNaoEletronica() {
		return seqMotivoEstornoPrescricaNaoEletronica;
	}

	public void setSeqMotivoEstornoPrescricaNaoEletronica(
			Short seqMotivoEstornoPrescricaNaoEletronica) {
		this.seqMotivoEstornoPrescricaNaoEletronica = seqMotivoEstornoPrescricaNaoEletronica;
	}


}