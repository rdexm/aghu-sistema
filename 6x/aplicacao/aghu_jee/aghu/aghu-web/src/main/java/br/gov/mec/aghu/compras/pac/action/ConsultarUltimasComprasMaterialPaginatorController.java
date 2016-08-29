package br.gov.mec.aghu.compras.pac.action;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.MathContext;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;
import javax.transaction.SystemException;

import net.sf.jasperreports.engine.JRException;

import org.apache.commons.lang3.StringUtils;

import br.gov.mec.aghu.business.exception.AghuCSVBaseException.AghuCSVBaseExceptionExceptionCode;
import br.gov.mec.aghu.compras.business.IComprasFacade;
import br.gov.mec.aghu.compras.cadastrosbasicos.business.IComprasCadastrosBasicosFacade;
import br.gov.mec.aghu.compras.pac.business.IPacFacade;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.SceTransferencia;
import br.gov.mec.aghu.model.ScoLocalizacaoProcesso;
import br.gov.mec.aghu.model.ScoMaterial;
import br.gov.mec.aghu.model.ScoModalidadeLicitacao;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.suprimentos.vo.ScoUltimasComprasMaterialVO;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.utils.AghuNumberFormat;

public class ConsultarUltimasComprasMaterialPaginatorController extends ActionController implements ActionPaginator {
	private static final long serialVersionUID = -5774965552335019985L;
	
	private static final String PLANEJAMENTO_SOLICITACAO_COMPRAS = "compras-planejamentoSolicitacaoCompras";
	private static final String CONSULTAR_ULTIMAS_COMPRAS = "compras-ultimasCompras";
	private static final String ITEM_PAC_LIST = "compras-itemPacList";
	private static final String PESQUISA_AUTORIZACAO_FORNECIMENTO = "compras-pesquisaAutorizacaoFornecimentoList";
	private static final String CONSULTAR_NOTA_RECEBIMENTO = "estoque-consultarNotaRecebimento";
	private static final String PAGE_IMPRIMIR_RELATORIO = "imprimirUltimasComprasPdf";

	@EJB
	private IPacFacade pacFacade;
	
	@EJB
	private IComprasFacade comprasFacade;
	
	@EJB
	private IComprasCadastrosBasicosFacade comprasCadastrosBasicosFacade;
	
	@EJB
	protected IRegistroColaboradorFacade registroColaboradorFacade;
	
	@Inject
	protected VisualizarRelatorioUltimasComprasController visualizarRelatorioUltimasComprasController;
	
	private ScoModalidadeLicitacao modalidadeCompra;
	
	private Integer nroAF;
	
	private Boolean historico = Boolean.FALSE;
	
	private Date dataNR;
	
	private ScoMaterial material;
	
	private Integer codigoMaterial;
	
	private String  voltarParaUrl;
	
	private List<ScoUltimasComprasMaterialVO> lista = new ArrayList<ScoUltimasComprasMaterialVO>();
	
	@Inject @Paginator
	private DynamicDataModel<SceTransferencia> dataModel;
	
	private Boolean gerouArquivo = Boolean.FALSE;
	
	private Boolean limpou = Boolean.FALSE;
	
	private Boolean pesquisouHistorico = Boolean.FALSE;
	
	private Boolean desabilitaData = Boolean.FALSE;
	
	private RapServidores servidorResponsavel;
	
	private String fileName;	
	
	
	private static final String EXTENSAO_CSV = ".csv";
	private static final String CONTENT_TYPE_CSV = "text/csv";	

	
	/**
	 * Limpa.
	 */
	public void limpar() {
		codigoMaterial = null;
		material = null;		
		modalidadeCompra = null;
		dataNR = null;
		historico = Boolean.FALSE;
		limpou = Boolean.TRUE;
		setAtivo(false);
		lista = new ArrayList<ScoUltimasComprasMaterialVO>();
	}
	
	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
		
	}
	
	public void inicio() {

		setAtivo(false);
		if(!limpou && !desabilitaData){
			Calendar calendarData = Calendar.getInstance();  
			calendarData.setTime(new Date());  
			calendarData.add(Calendar.YEAR,-3);  
			dataNR = calendarData.getTime();
		}
		
		if (material != null){
			codigoMaterial = material.getCodigo();
		}
		
		if(codigoMaterial!=null){
			material = comprasFacade.obterMaterialPorId(codigoMaterial);
			historico = Boolean.FALSE;
			pesquisar();
		}
	}
	
	
	public void mudarHistorico(){
		desabilitaData = Boolean.TRUE;
	}
	
	
	public String voltar() {
		return "voltar";
	}
	
	@Override
	public Long recuperarCount() {
		String modCod = null;
		if(modalidadeCompra!=null){
			modCod = modalidadeCompra.getCodigo();
		}
		Integer matCod=null;
		if(material!=null){
			matCod = material.getCodigo();
		}
		
		return pacFacade.pesquisarUltimasComprasMateriasCount(modCod, dataNR, matCod,historico);
	}


	@Override
	public List<ScoUltimasComprasMaterialVO> recuperarListaPaginada(Integer first, Integer max, String order,boolean asc) {
		String modCod = null;
		if(modalidadeCompra!=null){
			modCod = modalidadeCompra.getCodigo();
		}
		Integer matCod=null;
		if(material!=null){
			matCod = material.getCodigo();
		}
		if(historico){
			pesquisouHistorico = Boolean.TRUE;
		}else{
			pesquisouHistorico = Boolean.FALSE;
		}
		lista = pacFacade.pesquisarUltimasComprasMaterias(first, max, order, asc, modCod, dataNR, matCod, historico);
		return lista;
	}

	public String obterCustoUnitario(Integer qtde, Double valor){
		
		BigDecimal bigValor = new BigDecimal(valor);
		BigDecimal quantidade = new BigDecimal(qtde);
		BigDecimal custo = bigValor.divide(quantidade,new MathContext(5));
		String custoUnitario = AghuNumberFormat.formatarValor(custo, "#0.0000");
		return custoUnitario;
	}
	
	public String getEmailsHint(Integer numeroFornecedor){
		StringBuffer bufferEmails = new StringBuffer(100);
		bufferEmails.append("E-mails: ");
		List<String> emails = new ArrayList<String>();
		emails = pacFacade.obterEmailsFornecedor(numeroFornecedor);
		for (Iterator<String> iterator = emails.iterator(); iterator.hasNext();) {
			String email = (String) iterator.next();
			bufferEmails.append(email);
			if(iterator.hasNext()){
				bufferEmails.append("; \n");
			}
		}
	
		return bufferEmails.toString();
	}
	
	public String getRazaoSocialHint(String razaoSocial){
		StringBuffer bufferRazaoSocial = new StringBuffer(100);
		if(razaoSocial!=null){
			bufferRazaoSocial.append("Razão Social: ");
			bufferRazaoSocial.append(razaoSocial);
		}
		return bufferRazaoSocial.toString();
	}
	
	public String vaiParaPlanejamentoSolicitacaoCompras() {
		return PLANEJAMENTO_SOLICITACAO_COMPRAS;
	}
	
	public String irParaItemPacList() {
		return ITEM_PAC_LIST;
	}
	
	public String irParaPesquisaAutorizacaoFornecimentoList() {
		return PESQUISA_AUTORIZACAO_FORNECIMENTO;
	}
	
	public String irParaConsultarNotaRecebimento() {
		return CONSULTAR_NOTA_RECEBIMENTO;
	}
	
	public String getCnpjCpfHint(Long cnpj,Long cpf){
		StringBuffer bufferCnpj = new StringBuffer(100);
		bufferCnpj.append("CNPJ/CPF: ");
		if(cnpj!=null){
			bufferCnpj.append(CoreUtil.formatarCNPJ(cnpj));
		}else{
			if(cpf!=null){
				bufferCnpj.append(CoreUtil.formataCPF(cpf));
			}
		}
		return bufferCnpj.toString();
	}
	
	public String getTelefoneHint(Short ddi, Short ddd, Long fone){
		StringBuffer bufferFone = new StringBuffer(100);
		String telefone = "Fone: ";
		String in="(";
		String out=") ";
		bufferFone.append(telefone);
		if(ddi!=null && ddi>Short.valueOf("0")){
			bufferFone.append(in);
			bufferFone.append(ddi);
			bufferFone.append(out);
		}
		if(ddd!=null && ddd >Short.valueOf("0")){
			bufferFone.append(in);
			bufferFone.append(ddd);
			bufferFone.append(out);
		}
		if(fone!=null && fone >Long.valueOf("0")){
			bufferFone.append(fone);
		}
		return bufferFone.toString();
	}
	
	
	public String getMarcaHint(String marca){
		StringBuffer bufferRazaoSocial = new StringBuffer(100);
		if(marca!=null){
			bufferRazaoSocial.append("Marca: ");
			bufferRazaoSocial.append(marca);
		}
		return bufferRazaoSocial.toString();
	}
	
	public String getNomeComercialHint(String nomeComercial){
		StringBuffer nomeComercialBuffer = new StringBuffer(100);
		if(nomeComercial!=null){
			nomeComercialBuffer.append("Nome Comercial: ");
			nomeComercialBuffer.append(nomeComercial);
		}
		return nomeComercialBuffer.toString();
	}
	
	public void pesquisar(){
		setAtivo(true);
		dataModel.reiniciarPaginator();
	}
	
	public String imprimirRelatorio() throws BaseException, JRException, SystemException, IOException {
		if(lista.size() > 0 && !pesquisouHistorico){
			visualizarRelatorioUltimasComprasController.setLista(lista);
			visualizarRelatorioUltimasComprasController.setMaterial(material.getCodNome());
			if(material.getUnidadeMedida()!=null && material.getUnidadeMedida().getCodigo()!=null){
				visualizarRelatorioUltimasComprasController.setUnidade(material.getUnidadeMedida().getCodigo());
				visualizarRelatorioUltimasComprasController.setVoltarPara(CONSULTAR_ULTIMAS_COMPRAS);
				return PAGE_IMPRIMIR_RELATORIO;
			}
		}else{
			apresentarExcecaoNegocio(new BaseException(AghuCSVBaseExceptionExceptionCode.ERRO_HISTORICO_MARCADO));
			gerouArquivo = Boolean.FALSE;
		}
		return null;
	}
	
	public String visualizarRelatorio(){
		return "relatorioUltimasComprasMateriais";
	}

	public void gerarArquivo() throws ApplicationBusinessException {
		
		if(lista.size() > 0 && !pesquisouHistorico){
			// gera csv
			try {
				for (ScoUltimasComprasMaterialVO item : lista) {
					if(item.getValor() != null && item.getQuantidade() != null){
						BigDecimal valor = new BigDecimal(item.getValor());
						BigDecimal quantidade = new BigDecimal(item.getQuantidade());
						BigDecimal valorUn = valor.divide(quantidade,new MathContext(5));
						item.setValorUnit(valorUn);
					}else{
						item.setValorUnit(null);
					}
					// <número da AF>/<complemento>
					item.setAf(item.getNroAF().toString()+"/"+item.getCp().toString());
				}
			   fileName = comprasFacade.geraArquivoUltimasCompras(lista);
			   gerouArquivo = Boolean.TRUE;
			   this.dispararDownload();
			} catch(IOException e) {
				gerouArquivo = Boolean.FALSE;
				apresentarExcecaoNegocio(new BaseException(
						AghuCSVBaseExceptionExceptionCode.ERRO_GERACAO_ARQUIVO_CSV, e, e.getLocalizedMessage()));
			}
		} else{
			apresentarExcecaoNegocio(new BaseException(AghuCSVBaseExceptionExceptionCode.ERRO_HISTORICO_MARCADO));
			gerouArquivo = Boolean.FALSE;
		}
			
	}
	
	 /*
	  * Dispara o download para o arquivo CSV do relatório.
	 */
	public void dispararDownload(){
		if(fileName != null){
			try {
				SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
				Calendar c1 = Calendar.getInstance(); // today
				this.download(fileName, "LISTA_ULT_COMPRAS_"+sdf.format(c1.getTime())+EXTENSAO_CSV, CONTENT_TYPE_CSV);
				setGerouArquivo(Boolean.FALSE);
				fileName = null;				
			} catch (IOException e) {
				apresentarExcecaoNegocio(new BaseException(AghuCSVBaseExceptionExceptionCode.ERRO_GERACAO_ARQUIVO_CSV, e, e.getLocalizedMessage()));
			}
		}
	}
	
	
	
	
	
	
	
	/**
	 * Obtém lista para suggestionBox de Material (Apenas Ativos)
	 * @param param
	 * @return
	 */
	public List<ScoMaterial> listarMateriais(String param){
		return this.returnSGWithCount(this.comprasFacade.listarScoMateriaisAtivos(param),listarMateriaisCount(param));
	}
	
	public Long listarMateriaisCount(String param){
		return this.comprasFacade.listarScoMateriaisCount(param);
	}
	
	
	public List<ScoLocalizacaoProcesso> pesquisarLocalizacoes(Object filter) {
		return pacFacade.pesquisarScoLocalizacaoProcesso(filter, 100);
	}
	
	public Long pesquisarLocalizacoesCount(Object filter) {
		return pacFacade.pesquisarScoLocalizacaoProcessoCount(filter);
	}
	
	// suggestions
	public List<ScoModalidadeLicitacao> pesquisarModalidades(String filter) {
		return this.returnSGWithCount(this.comprasCadastrosBasicosFacade.listarModalidadeLicitacaoAtivas(filter),pesquisarModalidadesCount(filter));
	}
	
	public Long pesquisarModalidadesCount(String filter) {
		return this.comprasCadastrosBasicosFacade.listarModalidadeLicitacaoAtivasCount(filter);
	}
	
	public List<RapServidores> pesquisarServidorPorMatriculaOuNome(Object parametro) {
		return registroColaboradorFacade.pesquisarServidorPorMatriculaNome(parametro);
	}
	
	public Long pesquisarServidorPorMatriculaOuNomeCount(Object parametro) {
		return registroColaboradorFacade.pesquisarServidorPorMatriculaNomeCount(parametro);
	}
	
	public String abreviar(String str, int maxWidth){
		String abreviado = null;
		if(str != null){
			abreviado = " " + StringUtils.abbreviate(str, maxWidth);
		}
		return abreviado;
	}
	
	public String executaAcaoBotaoVoltar() {
		this.material = null;
		return getVoltarParaUrl();
	}
	
		// Getters/Setters
	public ScoModalidadeLicitacao getModalidadeCompra() {
		return modalidadeCompra;
	}

	public void setModalidadeCompra(ScoModalidadeLicitacao modalidadeCompra) {
		this.modalidadeCompra = modalidadeCompra;
	}

	public Integer getNroAF() {
		return nroAF;
	}

	public void setNroAF(Integer nroAF) {
		this.nroAF = nroAF;
	}

	public Date getDtEntrada() {
		return dataNR;
	}

	public void setDtEntrada(Date dtEntrada) {
		this.dataNR = dtEntrada;
	}

	public RapServidores getServidorResponsavel() {
		return servidorResponsavel;
	}

	public void setServidorResponsavel(RapServidores servidorResponsavel) {
		this.servidorResponsavel = servidorResponsavel;
	}

	public Boolean getHistorico() {
		return historico;
	}


	public void setHistorico(Boolean historico) {
		this.historico = historico;
	}


	public Date getDataNR() {
		return dataNR;
	}


	public void setDataNR(Date dataNR) {
		this.dataNR = dataNR;
	}


	public ScoMaterial getMaterial() {
		return material;
	}


	public void setMaterial(ScoMaterial material) {
		this.material = material;
	}


	public Integer getCodigoMaterial() {
		return codigoMaterial;
	}


	public void setCodigoMaterial(Integer codigoMaterial) {
		this.codigoMaterial = codigoMaterial;
	}


	public String getVoltarParaUrl() {
		return voltarParaUrl;
	}


	public void setVoltarParaUrl(String voltarParaUrl) {
		this.voltarParaUrl = voltarParaUrl;
	}

	public Boolean getGerouArquivo() {
		return gerouArquivo;
	}


	public void setGerouArquivo(Boolean gerouArquivo) {
		this.gerouArquivo = gerouArquivo;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	

	public Boolean getLimpou() {
		return limpou;
	}


	public void setLimpou(Boolean limpou) {
		this.limpou = limpou;
	}


	public Boolean getPesquisouHistorico() {
		return pesquisouHistorico;
	}


	public void setPesquisouHistorico(Boolean pesquisouHistorico) {
		this.pesquisouHistorico = pesquisouHistorico;
	}

	public static String getConsultarUltimasCompras() {
		return CONSULTAR_ULTIMAS_COMPRAS;
	}

	public DynamicDataModel<SceTransferencia> getDataModel() {
		return dataModel;
	}

	public void setDataModel(DynamicDataModel<SceTransferencia> dataModel) {
		this.dataModel = dataModel;
	}
	
	public void setAtivo(boolean ativo){
		this.dataModel.setPesquisaAtiva(ativo);
	}

	public boolean isAtivo(){
		return this.dataModel.getPesquisaAtiva();
	}
}
