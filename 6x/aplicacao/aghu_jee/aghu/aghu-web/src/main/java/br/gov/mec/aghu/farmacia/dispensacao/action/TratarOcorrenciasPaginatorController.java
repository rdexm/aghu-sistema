package br.gov.mec.aghu.farmacia.dispensacao.action;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import com.itextpdf.text.DocumentException;

import br.gov.mec.aghu.administracao.business.IAdministracaoFacade;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.business.SelectionQualifier;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.core.utils.AGHUUtil;
import br.gov.mec.aghu.dominio.DominioCaracteristicaMicrocomputador;
import br.gov.mec.aghu.farmacia.business.IFarmaciaFacade;
import br.gov.mec.aghu.farmacia.dispensacao.business.IFarmaciaDispensacaoFacade;
import br.gov.mec.aghu.internacao.business.IInternacaoFacade;
import br.gov.mec.aghu.model.AfaDispensacaoMdtos;
import br.gov.mec.aghu.model.AfaTipoOcorDispensacao;
import br.gov.mec.aghu.model.AghMicrocomputador;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.AinLeitos;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.paciente.business.IPacienteFacade;
import br.gov.mec.aghu.paciente.vo.CodPacienteFoneticaVO;
import net.sf.jasperreports.engine.JRException;

public class TratarOcorrenciasPaginatorController extends ActionController implements ActionPaginator{
	
	private static final long serialVersionUID = -5694728348799779501L;

	@EJB
	private IFarmaciaFacade farmaciaFacade;
	
	@EJB
	private IInternacaoFacade internacaoFacade;
	
	@EJB
	private IFarmaciaDispensacaoFacade farmaciaDispensacaoFacade;
	
	@EJB
	private IAghuFacade aghuFacade;
	
	@EJB 
	private IPacienteFacade pacienteFacade;
	
	@Inject
	private RelatorioListaOcorrenciaController relatorioListaOcorrenciaController;
	
	@Inject
	private EstornaMedicamentoDispensadoPaginatorController estornaMedicamentoDispensadoPaginatorController;
	
	@Inject
	@SelectionQualifier
	private Instance<CodPacienteFoneticaVO> codPacienteFonetica;

	private AghUnidadesFuncionais unidadeFuncional;
	private AghUnidadesFuncionais farmacia;
	private Date data;
	private AfaTipoOcorDispensacao tipoOcorDispensacao;	
	private AipPacientes paciente;
	private AinLeitos leito; 
	private Integer prontuario;
	private Integer prontuarioRecebido;
	private List<AfaDispensacaoMdtos> listaOcorrenciasMdtosDispensados;
	private AfaDispensacaoMdtos itemSelecionado;
	private Boolean btVoltarOuFechar;
	private String etiqueta;
	private Boolean ativo;
	private String unidade;
	private String dtReferencia;
	private String ocorrencia;
	private String unidFarmacia;
	
	private Boolean valorAlterado;
	//Caso a tela seja consequencia se algum refresh, então true
	//private Boolean flagRefresh;
	private String colunaOrdenada;
	private Boolean colunaOrdenadaAsc;
	private String imageOrder;
	private String urlBtVoltar;
	private Integer codigoPaciente;
	private Integer codigoPacienteParaEstorno;
	private String nomeComputadorRede;
	private AghMicrocomputador micro; 	
	private AghMicrocomputador microDispensador; 
	private Boolean origemTelaRealizarTriagem;
	private Long seqAfaDispSelecionadaCheckBox;
	private Boolean refreshPesquisa;
	private Short unfSeq;
	
	
	@Inject
	private IAdministracaoFacade administracaoFacade;
	
	@PostConstruct
	public void init(){
		begin(conversation);
	}
	
	public void iniciarPagina(){
	 

		startPagina();
		inicializaPaciente();
	
	}
	
	public void startPagina(){
		if(ativo == null){
			ativo = Boolean.FALSE;
		}
		valorAlterado = Boolean.FALSE;
		etiqueta="";
		try {
			nomeComputadorRede = getEnderecoRedeHostRemoto();
		} catch (UnknownHostException e) {
			apresentarExcecaoNegocio(new ApplicationBusinessException("NAO_FOI_POSSIVEL_RECUPERAR_COMPUTADOR", Severity.ERROR));
		}	

		if(data==null){
			data = new Date();
		}
		
		if (urlBtVoltar != null){//então o user entrou na tela de tratarOcorrencias atráves de outra tela, não o menu
			btVoltarOuFechar = Boolean.TRUE;
			prontuarioRecebido = prontuario;
		}else{
			btVoltarOuFechar = Boolean.FALSE;
		}
			
		itemSelecionado = new AfaDispensacaoMdtos();
		try {
			if(farmacia == null){
				if(unfSeq != null){
					farmacia = aghuFacade.obterAghUnidadesFuncionaisPorChavePrimaria(unfSeq);
				}else{
					farmacia = farmaciaDispensacaoFacade.getFarmaciaMicroComputador(microDispensador, nomeComputadorRede);
				}
			}
			
		} catch (ApplicationBusinessException e) {
			//nada a fazer, simplesmente a farmacia não é alterada
			farmacia = null;
		}
		
		if((origemTelaRealizarTriagem != null && origemTelaRealizarTriagem) || Boolean.TRUE.equals(refreshPesquisa)){
			efetuarPesquisa();
		}
		
		try {
			recuperaAghMicroComputador();
		} catch (ApplicationBusinessException e) {
			micro = null;
			//Não é necessário fazer nada neste catch, exceção será lançada depois
		}
	}
	
	private void recuperaAghMicroComputador() throws ApplicationBusinessException {
		micro = administracaoFacade.obterAghMicroComputadorPorNomeOuIPException(nomeComputadorRede);
		microDispensador = administracaoFacade.obterAghMicroComputadorPorNomeOuIP(nomeComputadorRede, DominioCaracteristicaMicrocomputador.DISPENSADOR);
	}
	
	
	
	private void inicializaPaciente() {
		if (paciente == null || paciente.getCodigo() == null) {
			CodPacienteFoneticaVO codPac = codPacienteFonetica.get();
			if (codPac != null && codPac.getCodigo() > 0) { 
				paciente = pacienteFacade.obterAipPacientesPorChavePrimaria(codPac.getCodigo());
				if (paciente != null) {
					prontuario = paciente.getProntuario();
					codigoPaciente = paciente.getCodigo();
				}
			}
		} else if (codigoPaciente != null) {
			limpaEProcessaBuscaPacCodigo();

		} else if (prontuario != null) {
			limpaEProcessaBuscaPacProntuario();
		}
	}

	public void limpaEProcessaBuscaPacProntuario(){
		codigoPaciente = null;
		paciente = null;
		processaBuscaPaciente();
	}
	
	public void limpaEProcessaBuscaPacCodigo(){
		prontuario = null;
		paciente = null;
		processaBuscaPaciente();
	}
	
	public void processaBuscaPaciente(){
		prontuarioRecebido = null;
		if(codigoPaciente != null){
			paciente = pacienteFacade.obterPacientePorCodigo(codigoPaciente);
			mensagemPacienteNaoEncontrado();
		}else if(prontuario != null){
			paciente = pacienteFacade.obterPacientePorProntuario(prontuario);
			mensagemPacienteNaoEncontrado();
		}
		setCodigoProntuarioPaciente();
			
	}
	private void setCodigoProntuarioPaciente() {
		if(paciente != null && paciente.getCodigo() != null){
			codigoPaciente = paciente.getCodigo();
			prontuario = paciente.getProntuario();
		}else{
			codigoPaciente = null;
			prontuario = null;
		}
	}
	
	private void mensagemPacienteNaoEncontrado() {
		if(paciente==null){
			apresentarMsgNegocio(Severity.INFO, "MENSAGEM_NENHUM_PACIENTE_ENCONTRADO");
		}
	}
	
	@Override
	public List<AfaDispensacaoMdtos> recuperarListaPaginada(Integer firstResult,
			Integer maxResult, String orderProperty, boolean asc) {		
		return listaOcorrenciasMdtosDispensados; 
	}
	
	@Override
	public Long recuperarCount() {		
		return (long) listaOcorrenciasMdtosDispensados.size();
	}
		
	/**
	 * Pesquisa Unidades Funcionais conforme o parametro recebido
	 * 
	 * @param parametro
	 * @return
	 */
	public List<AghUnidadesFuncionais> pesquisarUnidadeFuncionalPorCodigoEDescricao(String strPesquisa){
		return this.returnSGWithCount(farmaciaFacade.listarUnidadesPmeInformatizadaByPesquisa(strPesquisa),pesquisarUnidadeFuncionalPorCodigoEDescricaoCount(strPesquisa));
	}
	
	/**
	 * Count de Unidades Funcionais conforme o parametro recebido
	 * 
	 * @param parametro
	 * @return
	 */
	public Long pesquisarUnidadeFuncionalPorCodigoEDescricaoCount(String strPesquisa){
		return farmaciaFacade.listarUnidadesPmeInformatizadaByPesquisaCount(strPesquisa);
	}
	
	/**
	 * Pesquisa Motivo: Tipo Ocorrência Dispensação
	 * 
	 * @param strPesquisa
	 * @return
	 */
	public List<AfaTipoOcorDispensacao> pesquisarTodosMotivosOcorrenciaDispensacao(String strPesquisa){
		return this.returnSGWithCount(farmaciaDispensacaoFacade.pesquisarTodosMotivosOcorrenciaDispensacao(strPesquisa),pesquisarTodosMotivosOcorrenciaDispensacaoCount(strPesquisa));
	}
	
	/**
	 * Count de Motivo: Tipo Ocorrência Dispensação
	 * 
	 * @param strPesquisa
	 * @return
	 */
	public Long pesquisarTodosMotivosOcorrenciaDispensacaoCount(String strPesquisa){
		return farmaciaDispensacaoFacade.pesquisarTodosMotivosOcorrenciaDispensacaoCount(strPesquisa);
	}
	
	public List<AipPacientes> pesquisarPacientes(Object objPesquisa){
		String strPesquisa = String.valueOf(objPesquisa);
		if (strPesquisa != null && !"".equals(strPesquisa.trim()) && !"null".equals(strPesquisa.trim())) {
			strPesquisa = strPesquisa.trim().replaceAll("/", "");
			Integer nroProntuario = Integer.valueOf(strPesquisa);
			AipPacientes paci = this.pacienteFacade.obterPacienteComAtendimentoPorProntuario(nroProntuario);
			if(paci == null){
				return null;
			}
			
			List<AipPacientes> pacientes = new ArrayList<AipPacientes>();
			pacientes.add(paci);
			return pacientes;
		} else{
			return null;
		}
	}
	
	public List<AinLeitos> pesquisarLeitos(String strPesquisa){
		return this.returnSGWithCount(this.internacaoFacade.obterLeitosAtivosPorUnf(
				strPesquisa,
				unidadeFuncional != null ? unidadeFuncional
						.getSeq() : null),pesquisarLeitosCount(strPesquisa));
	}
	
	public Long pesquisarLeitosCount(String strPesquisa){
		if(unidadeFuncional != null){
			return this.internacaoFacade.obterLeitosAtivosPorUnfCount(strPesquisa, unidadeFuncional.getSeq());
		}else{
			return this.internacaoFacade.obterLeitosAtivosPorUnfCount(strPesquisa, null);			
		}
	}
	
	public List<AghUnidadesFuncionais> pesquisarFarmacias(String strPesquisa){
		return this.returnSGWithCount(farmaciaDispensacaoFacade.listarFarmaciasAtivasByPesquisa(strPesquisa),pesquisarFarmaciasCount(strPesquisa));
	}
	
	public Long pesquisarFarmaciasCount(String strPesquisa){
		return farmaciaDispensacaoFacade.listarFarmaciasAtivasByPesquisaCount(strPesquisa);
	}
	
	public String efetuarPesquisa(){
		
//		ativo = Boolean.FALSE;
				
		if (paciente != null){
			prontuario = paciente.getProntuario();		
		}else{
			if (prontuarioRecebido != null){
				paciente = this.pacienteFacade.obterPacientePorProntuario(prontuarioRecebido);	
				prontuario = paciente.getProntuario();
			}else{
				prontuario = null;
			}
		}
		try{
			if(farmacia == null){
				throw new ApplicationBusinessException("MENSAGEM_FARMACIA_OBRIGATORIA_TRATAR_OCORRENCIA", Severity.ERROR);
			}
			listaOcorrenciasMdtosDispensados = this.farmaciaDispensacaoFacade.recuperarlistaOcorrenciasMdtosDispensados(unidadeFuncional,data,tipoOcorDispensacao,prontuario,leito,farmacia);
			ativo = Boolean.TRUE;
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
		//ordenarLista();
		return null;
	}

	public String limpar(){
		unidadeFuncional = null;
		farmacia = null;
		data = null;
		tipoOcorDispensacao = null;
		paciente = null;
		leito = null;
		prontuario = null;
		prontuarioRecebido = null;
		ativo = Boolean.FALSE;
		listaOcorrenciasMdtosDispensados = null;
		codigoPaciente = null;
		paciente = null;
		startPagina();
		return null;
	}
	
	public String limparProntuarioRecebido(){
		prontuarioRecebido = null;
		return null;
	}
	
	public String fechar(){
		return urlBtVoltar;
	}
	
	public void selecionar(AfaDispensacaoMdtos selecao){
		itemSelecionado = selecao;
	}
		
	public void assinaMedicamento(AfaDispensacaoMdtos adm){
//		FacesContext context = FacesContext.getCurrentInstance();
//		context.getMessageList().clear();
		try {
			String nomeMicrocomputador = null;
			try {
				nomeMicrocomputador = super.getEnderecoRedeHostRemoto();
			} catch (UnknownHostException e) {
				apresentarExcecaoNegocio(new ApplicationBusinessException("NAO_FOI_POSSIVEL_RECUPERAR_COMPUTADOR", Severity.ERROR));
			}
			farmaciaDispensacaoFacade.assinaDispMdtoSemUsoDeEtiqueta(adm, micro, microDispensador, nomeMicrocomputador);
			ordernarListaAposDispensacao();
		}catch (BaseException e) {
			apresentarExcecaoNegocio(e);
			adm.setItemDispensadoSemEtiqueta(Boolean.FALSE);
		}
	}
	
	public void pesquisaEtiquetaComCbMedicamento() {
		try {
			final String etiquetaFormatada = farmaciaDispensacaoFacade.validarCodigoBarrasEtiqueta(etiqueta);
			farmaciaDispensacaoFacade.pesquisaEtiquetaComCbMedicamento(etiquetaFormatada, listaOcorrenciasMdtosDispensados,farmacia, micro, microDispensador);
			farmaciaDispensacaoFacade.processaCorSinaleiroPosAtualizacao(listaOcorrenciasMdtosDispensados);
			ordernarListaAposDispensacao();
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		} finally {
			etiqueta = "";
		}
	}
	
	/*public void ordenarLista(String coluna){
		if(colunaOrdenada==null || !colunaOrdenada.equals(coluna)){
			colunaOrdenadaAsc = Boolean.TRUE;//Order Inicial
		}
		else if(colunaOrdenada.equals(coluna)){
			colunaOrdenadaAsc = !colunaOrdenadaAsc;
		}
		
		imageOrder = coluna+"."+colunaOrdenadaAsc.toString().toUpperCase();
		colunaOrdenada = coluna;
		AghuUtil.ordenarLista(listaOcorrenciasMdtosDispensados, colunaOrdenada, colunaOrdenadaAsc);
	}
	
	public void ordenarLista(){
		if(colunaOrdenadaAsc!=null && colunaOrdenada!=null){
			AghuUtil.ordenarLista(listaOcorrenciasMdtosDispensados, colunaOrdenada, colunaOrdenadaAsc);
		}
	}*/
	
	private void ordernarListaAposDispensacao() {
		AGHUUtil.ordenarLista(listaOcorrenciasMdtosDispensados, "medicamento.descricaoEditada", false);
		AGHUUtil.ordenarLista(listaOcorrenciasMdtosDispensados, "corSinaleiro.codigo", true);
	}
	
	public String voltar() {
		return urlBtVoltar;
	}
	
	public String chamarTelaPesquisaFonetica() {
		paciente = null;
		codigoPaciente = null;
		return "paciente-pesquisaPacienteComponente";
	}

	public String listarOcorrencia() throws JRException, IOException, DocumentException{		

		if (unidadeFuncional ==null){
			apresentarExcecaoNegocio(new ApplicationBusinessException("MENSAGEM_PREENCHA_DATA_OCORRENCIA_UNIDADE_FUNCIONAL", Severity.ERROR));
			return null;
		}else{
			unidade = unidadeFuncional.getSeq().toString();
			dtReferencia = data.toString();
			ocorrencia = tipoOcorDispensacao.getSeq().toString();
			
			unidFarmacia = null;
			if (farmacia != null){
				unidFarmacia = farmacia.getSeq().toString();
			}
			
			try{
				this.farmaciaDispensacaoFacade.recuperarRelatorioListaOcorrenciaCount(unidade,dtReferencia,ocorrencia, unidFarmacia);
				relatorioListaOcorrenciaController.setUnidade(unidade);
				relatorioListaOcorrenciaController.setOcorrencia(ocorrencia);
				relatorioListaOcorrenciaController.setDtReferencia(dtReferencia);
				relatorioListaOcorrenciaController.setUnidFarmacia(unidFarmacia);
				relatorioListaOcorrenciaController.print();
				return "relatorioListaOcorrenciaPdf";
			}catch (ApplicationBusinessException e) {
				apresentarExcecaoNegocio(e);
				return null;
			}			
		}
	}
	
	public String gotoEstornoMedicamentosDispensados(){
		estornaMedicamentoDispensadoPaginatorController.limpar();
		estornaMedicamentoDispensadoPaginatorController.setCodigoPaciente(getCodigoPacienteParaEstorno());
		estornaMedicamentoDispensadoPaginatorController.setSeqAfaDispSelecionadaCheckBox(getSeqAfaDispSelecionadaCheckBox());
		estornaMedicamentoDispensadoPaginatorController.setUrlBtVoltar("tratarOcorrenciasList");
		estornaMedicamentoDispensadoPaginatorController.setEncerraConversacaoBtVoltar(Boolean.TRUE);
		estornaMedicamentoDispensadoPaginatorController.setBlockIniciar(Boolean.FALSE);
		return "estornarMedicamentoDispensadoList";
	}
	
	// Getters and Setters
	// ===================

	public AghUnidadesFuncionais getUnidadeFuncional() {
		return unidadeFuncional;
	}

	public void setUnidadeFuncional(AghUnidadesFuncionais unidadeFuncional) {
		this.unidadeFuncional = unidadeFuncional;
	}

	public AghUnidadesFuncionais getFarmacia() {
		return farmacia;
	}

	public void setFarmacia(AghUnidadesFuncionais farmacia) {
		this.farmacia = farmacia;
	}

	public Date getData() {
		return data;
	}

	public void setData(Date data) {
		this.data = data;
	}

	public AfaTipoOcorDispensacao getTipoOcorDispensacao() {
		return tipoOcorDispensacao;
	}

	public void setTipoOcorDispensacao(AfaTipoOcorDispensacao tipoOcorDispensacao) {
		this.tipoOcorDispensacao = tipoOcorDispensacao;
	}

	public AipPacientes getPaciente() {
		return paciente;
	}

	public void setPaciente(AipPacientes paciente) {
		this.paciente = paciente;
	}

	public AinLeitos getLeito() {
		return leito;
	}

	public void setLeito(AinLeitos leito) {
		this.leito = leito;
	}

	public Integer getProntuario() {
		return prontuario;
	}

	public void setProntuario(Integer prontuario) {
		this.prontuario = prontuario;
	}

	public Integer getProntuarioRecebido() {
		return prontuarioRecebido;
	}

	public void setProntuarioRecebido(Integer prontuarioRecebido) {
		this.prontuarioRecebido = prontuarioRecebido;
	}

	public List<AfaDispensacaoMdtos> getListaOcorrenciasMdtosDispensados() {
		return listaOcorrenciasMdtosDispensados;
	}

	public void setListaOcorrenciasMdtosDispensados(
			List<AfaDispensacaoMdtos> listaOcorrenciasMdtosDispensados) {
		this.listaOcorrenciasMdtosDispensados = listaOcorrenciasMdtosDispensados;
	}

	public AfaDispensacaoMdtos getItemSelecionado() {
		return itemSelecionado;
	}

	public void setItemSelecionado(AfaDispensacaoMdtos itemSelecionado) {
		this.itemSelecionado = itemSelecionado;
	}

	public Boolean getBtVoltarOuFechar() {
		return btVoltarOuFechar;
	}

	public void setBtVoltarOuFechar(Boolean btVoltarOuFechar) {
		this.btVoltarOuFechar = btVoltarOuFechar;
	}

	public String getEtiqueta() {
		return etiqueta;
	}

	public void setEtiqueta(String etiqueta) {
		this.etiqueta = etiqueta;
	}

	public Boolean getAtivo() {
		return ativo;
	}

	public void setAtivo(Boolean ativo) {
		this.ativo = ativo;
	}

	public String getUnidade() {
		return unidade;
	}

	public void setUnidade(String unidade) {
		this.unidade = unidade;
	}

	public String getDtReferencia() {
		return dtReferencia;
	}

	public void setDtReferencia(String dtReferencia) {
		this.dtReferencia = dtReferencia;
	}

	public String getOcorrencia() {
		return ocorrencia;
	}

	public void setOcorrencia(String ocorrencia) {
		this.ocorrencia = ocorrencia;
	}

	public Boolean getValorAlterado() {
		return valorAlterado;
	}

	public void setValorAlterado(Boolean valorAlterado) {
		this.valorAlterado = valorAlterado;
	}

	public String getColunaOrdenada() {
		return colunaOrdenada;
	}

	public void setColunaOrdenada(String colunaOrdenada) {
		this.colunaOrdenada = colunaOrdenada;
	}

	public Boolean getColunaOrdenadaAsc() {
		return colunaOrdenadaAsc;
	}

	public void setColunaOrdenadaAsc(Boolean colunaOrdenadaAsc) {
		this.colunaOrdenadaAsc = colunaOrdenadaAsc;
	}

	public String getImageOrder() {
		return imageOrder;
	}

	public void setImageOrder(String imageOrder) {
		this.imageOrder = imageOrder;
	}

	public String getUrlBtVoltar() {
		return urlBtVoltar;
	}

	public void setUrlBtVoltar(String urlBtVoltar) {
		this.urlBtVoltar = urlBtVoltar;
	}

	public Integer getCodigoPaciente() {
		return codigoPaciente;
	}

	public void setCodigoPaciente(Integer codigoPaciente) {
		this.codigoPaciente = codigoPaciente;
	}

	public Integer getCodigoPacienteParaEstorno() {
		return codigoPacienteParaEstorno;
	}

	public void setCodigoPacienteParaEstorno(Integer codigoPacienteParaEstorno) {
		this.codigoPacienteParaEstorno = codigoPacienteParaEstorno;
	}

	public String getNomeComputadorRede() {
		return nomeComputadorRede;
	}

	public void setNomeComputadorRede(String nomeComputadorRede) {
		this.nomeComputadorRede = nomeComputadorRede;
	}

	public AghMicrocomputador getMicro() {
		return micro;
	}

	public void setMicro(AghMicrocomputador micro) {
		this.micro = micro;
	}

	public AghMicrocomputador getMicroDispensador() {
		return microDispensador;
	}

	public void setMicroDispensador(AghMicrocomputador microDispensador) {
		this.microDispensador = microDispensador;
	}

	public Boolean getOrigemTelaRealizarTriagem() {
		return origemTelaRealizarTriagem;
	}

	public void setOrigemTelaRealizarTriagem(Boolean origemTelaRealizarTriagem) {
		this.origemTelaRealizarTriagem = origemTelaRealizarTriagem;
	}

	public Long getSeqAfaDispSelecionadaCheckBox() {
		return seqAfaDispSelecionadaCheckBox;
	}

	public void setSeqAfaDispSelecionadaCheckBox(Long seqAfaDispSelecionadaCheckBox) {
		this.seqAfaDispSelecionadaCheckBox = seqAfaDispSelecionadaCheckBox;
	}

	public Boolean getRefreshPesquisa() {
		return refreshPesquisa;
	}

	public void setRefreshPesquisa(Boolean refreshPesquisa) {
		this.refreshPesquisa = refreshPesquisa;
	}

	public Short getUnfSeq() {
		return unfSeq;
	}

	public void setUnfSeq(Short unfSeq) {
		this.unfSeq = unfSeq;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public String getUnidFarmacia() {
		return unidFarmacia;
	}

	public void setUnidFarmacia(String unidFarmacia) {
		this.unidFarmacia = unidFarmacia;
	}
			
}