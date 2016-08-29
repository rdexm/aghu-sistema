package br.gov.mec.aghu.compras.business;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.compras.contaspagar.business.ConsultarTituloRN;
import br.gov.mec.aghu.compras.dao.ScoAfEmpenhoDAO;
import br.gov.mec.aghu.compras.dao.ScoAndamentoProcessoCompraDAO;
import br.gov.mec.aghu.compras.dao.ScoFornecedorDAO; 
import br.gov.mec.aghu.compras.dao.ScoItemAutorizacaoFornDAO;
import br.gov.mec.aghu.compras.dao.ScoLicitacaoDAO;
import br.gov.mec.aghu.compras.dao.ScoSolicitacaoDeCompraDAO;
import br.gov.mec.aghu.compras.pac.vo.RelatorioApVO;
import br.gov.mec.aghu.compras.pac.vo.RelatorioEpVo;
import br.gov.mec.aghu.compras.vo.AfPendenteCompradorVO;
import br.gov.mec.aghu.compras.vo.FiltroRelatoriosExcelVO;
import br.gov.mec.aghu.dominio.DominioNomeRelatorio;
import br.gov.mec.aghu.estoque.business.IEstoqueFacade;
import br.gov.mec.aghu.faturamento.vo.ArquivoURINomeQtdVO;
import br.gov.mec.aghu.model.ScoGrupoMaterial;
import br.gov.mec.aghu.model.ScoMaterial;
import br.gov.mec.aghu.suprimentos.vo.RelatorioESVO;
import br.gov.mec.aghu.suprimentos.vo.ScoUltimasComprasMaterialVO;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.core.utils.DateUtil;


@Stateless
public class RelatorioExcelON extends BaseBusiness {

	private static final String DD_MM_YY = "dd/MM/yy";

	private static final String REAIS = "R$ ";

	/**
	 * 
	 */
	private static final long serialVersionUID = 5391281211434930132L;
	
	private static final Log LOG = LogFactory.getLog(RelatorioExcelON.class);
	
	@EJB
	private IEstoqueFacade estoqueFacade;
	
	@Inject
	private ScoAndamentoProcessoCompraDAO scoAndamentoProcessoCompraDAO;
	
	@Inject
	private ScoLicitacaoDAO scoLicitacaoDAO;
	
	
	@Inject
	private ScoSolicitacaoDeCompraDAO scoSolicitacaoDeCompraDAO;
	
	/** Injeção do ConsultarTituloRN */
	@Inject
	private ConsultarTituloRN consultarTituloRN;
	
	@Inject
	private ScoFornecedorDAO scoFornecedorDAO;
	
	@Inject
	private ScoItemAutorizacaoFornDAO scoItemAutorizacaoFornDAO;
	
	@Inject
	private ScoAfEmpenhoDAO scoAfEmpenhoDAO;
		
	private static final String QUEBRA_LINHA = "\r\n";
	private static final String SEPARADOR=";";
	private static final String ENCODE="ISO-8859-1";
	private static final String EXTENSAO=".csv";
	
	private enum RelatorioExcelONExceptionCode implements BusinessExceptionCode {
		MENSAGEM_NENHUM_REGISTRO_ENCONTRADO,
		MENSAGEM_NENHUM_REGISTRO_ENCONTRADO_RELATORIO_ES
	}
	
	protected IEstoqueFacade getEstoqueFacade() {
		return estoqueFacade;
	}
	
	private ScoAndamentoProcessoCompraDAO getScoAndamentoProcessoCompraDAO() {
		return scoAndamentoProcessoCompraDAO;
	}
	
	private ScoLicitacaoDAO getScoLicitacaoDAO() {
		return scoLicitacaoDAO;
	}
	
	private ScoSolicitacaoDeCompraDAO getSolicitacaoDeCompraDAO() {
		return scoSolicitacaoDeCompraDAO;
	}

	private String gerarCabecalhoDoRelatorio() {
		StringBuffer buffer = new StringBuffer();
		buffer.append(this.getResourceBundleValue("HEADER_REL_AF_PENDENTE_COMPRADOR_GRUPO"))
			.append(SEPARADOR)
			.append(this.getResourceBundleValue("HEADER_REL_AF_PENDENTE_COMPRADOR_COMPRADOR"))
			.append(SEPARADOR)
			.append(this.getResourceBundleValue("HEADER_REL_AF_PENDENTE_COMPRADOR_GESTOR"))
			.append(SEPARADOR)
			.append(this.getResourceBundleValue("HEADER_REL_AF_PENDENTE_COMPRADOR_AF"))
			.append(SEPARADOR)
			.append(this.getResourceBundleValue("HEADER_REL_AF_PENDENTE_COMPRADOR_GERADA_EM"))
			.append(SEPARADOR)
			.append(this.getResourceBundleValue("HEADER_REL_AF_PENDENTE_COMPRADOR_DT_PREV_ENTREGA"))
			.append(SEPARADOR)
			.append(this.getResourceBundleValue("HEADER_REL_AF_PENDENTE_COMPRADOR_DT_VENC_CONTRATO"))
			.append(SEPARADOR)
			.append(this.getResourceBundleValue("HEADER_REL_AF_PENDENTE_COMPRADOR_MODL_LICT"))
			.append(SEPARADOR)
			.append(this.getResourceBundleValue("HEADER_REL_AF_PENDENTE_COMPRADOR_MODL_EMP"))
			.append(SEPARADOR)
			.append(this.getResourceBundleValue("HEADER_REL_AF_PENDENTE_COMPRADOR_ITEM_CONTRATO"))
			.append(SEPARADOR)
			.append(this.getResourceBundleValue("HEADER_REL_AF_PENDENTE_COMPRADOR_ITEM"))
			.append(SEPARADOR)
			.append(this.getResourceBundleValue("HEADER_REL_AF_PENDENTE_COMPRADOR_SIT"))
			.append(SEPARADOR)
			.append(this.getResourceBundleValue("HEADER_REL_AF_PENDENTE_COMPRADOR_CODIGO"))
			.append(SEPARADOR)
			.append(this.getResourceBundleValue("HEADER_REL_AF_PENDENTE_COMPRADOR_MATERIAL"))
			.append(SEPARADOR)
			.append(this.getResourceBundleValue("HEADER_REL_AF_PENDENTE_COMPRADOR_UNID"))
			.append(SEPARADOR)
			.append(this.getResourceBundleValue("HEADER_REL_AF_PENDENTE_COMPRADOR_QTDE_SOLIC"))
			.append(SEPARADOR)
			.append(this.getResourceBundleValue("HEADER_REL_AF_PENDENTE_COMPRADOR_QTDE_RECB"))
			.append(SEPARADOR)
			.append(this.getResourceBundleValue("HEADER_REL_AF_PENDENTE_COMPRADOR_CUSTO_UNIT"))
			.append(SEPARADOR)
			.append(this.getResourceBundleValue("HEADER_REL_AF_PENDENTE_COMPRADOR_VALOR_EFET"))
			.append(SEPARADOR)
			.append(this.getResourceBundleValue("HEADER_REL_AF_PENDENTE_COMPRADOR_VALOR_SALDO"))
			.append(SEPARADOR)
			.append(this.getResourceBundleValue("HEADER_REL_AF_PENDENTE_COMPRADOR_VALOR_ITEM"))
			.append(SEPARADOR)
			.append(this.getResourceBundleValue("HEADER_REL_AF_PENDENTE_COMPRADOR_FORNECEDOR"))
			.append(QUEBRA_LINHA);

		return buffer.toString();
	}
	
	private String gerarLinhasDoRelatorio(AfPendenteCompradorVO linha) {
		StringBuffer buffer = new StringBuffer();
		
		buffer.append(linha.getCodigoGrupoMaterial()).append(SEPARADOR);
		
		if (StringUtils.isEmpty(linha.getCompradorU())){
			buffer.append(linha.getComprador()).append(SEPARADOR);
		} else {
			buffer.append(linha.getCompradorU()).append(SEPARADOR);
		}
		
		if (StringUtils.isEmpty(linha.getGestorU())) {
			buffer.append(linha.getGestor()).append(SEPARADOR);
		} else {
			buffer.append(linha.getGestorU()).append(SEPARADOR);
		}
		
		String BARRA = "/";
		buffer.append(linha.getAfNumero()).append(BARRA).append(linha.getAfComplemento()).append(SEPARADOR)
		.append(DateUtil.dataToString(linha.getGeradaEm(), DD_MM_YY)).append(SEPARADOR)
		.append(DateUtil.dataToString(linha.getDtPrevEntrega(), DD_MM_YY)).append(SEPARADOR)
		.append(DateUtil.dataToString(linha.getDtVencContrato(), DD_MM_YY)).append(SEPARADOR)
		.append(linha.getModlLict()).append(SEPARADOR)
		.append(linha.getModlEmp()).append(SEPARADOR);
		if (Boolean.TRUE == linha.getItemContrato()) {
			String SIM = "S";
			buffer.append(SIM).append(SEPARADOR);
		} else {
			String NAO = "N";
			buffer.append(NAO).append(SEPARADOR);
		}
		buffer.append(linha.getItem()).append(SEPARADOR)
		.append(linha.getSit()).append(SEPARADOR)
		.append(linha.getCodigoMaterial()).append(SEPARADOR)
		.append(linha.getNomeMaterial()).append(SEPARADOR)
		.append(linha.getUnid()).append(SEPARADOR)
		.append(linha.getQtdeSolic()).append(SEPARADOR)
		.append(linha.getQtdeRecb()).append(SEPARADOR)
		.append(REAIS).append(this.formatarNumeroMoeda(linha.getCustoUnit())).append(SEPARADOR)
		.append(REAIS).append(this.formatarNumeroMoeda(linha.getValorEfet())).append(SEPARADOR)
		.append(REAIS).append(this.formatarNumeroMoeda(linha.getValorSaldo())).append(SEPARADOR)
		.append(REAIS).append(this.formatarNumeroMoeda(linha.getValorItem())).append(SEPARADOR)
		.append(linha.getFornecedor()).append(SEPARADOR)
		
		
		.append(QUEBRA_LINHA);
		
		return buffer.toString();
	}
	
	/*
	@SuppressWarnings("unchecked")
	private void ordernarLista(List<AfPendenteCompradorVO> lista) {
		final ComparatorChain comparatorChain = new ComparatorChain();
		
		final BeanComparator grupoComparator = new BeanComparator(
				AfPendenteCompradorVO.Fields.GRUPO.toString(), new NullComparator(false));
		
		final BeanComparator compradorComparator = new BeanComparator(
				AfPendenteCompradorVO.Fields.COMPRADOR.toString(), new NullComparator(false));
		
		final BeanComparator gestorComparator = new BeanComparator(
				AfPendenteCompradorVO.Fields.GESTOR.toString(), new NullComparator(false));
		
		comparatorChain.addComparator(grupoComparator);
		comparatorChain.addComparator(compradorComparator);
		comparatorChain.addComparator(gestorComparator);
		
		Collections.sort(lista, comparatorChain);
	}*/

	public String gerarRelatorioCSVAFsPendentesComprador(ScoGrupoMaterial grupoMaterial, ScoMaterial material) throws IOException, ApplicationBusinessException {
		
		List<AfPendenteCompradorVO> listaAfs = getSolicitacaoDeCompraDAO().buscarListaAFPendentesPorComprador(grupoMaterial, material);
		
		if(listaAfs.isEmpty()){
			throw new ApplicationBusinessException(RelatorioExcelONExceptionCode.MENSAGEM_NENHUM_REGISTRO_ENCONTRADO);
		}
		
		calcularValores(listaAfs);
		//ordernarLista(listaAfs);
		
		final File file = File.createTempFile(DominioNomeRelatorio.REL_AF_PENDENTE_COMPRADOR.toString(), EXTENSAO);

		final Writer out = new OutputStreamWriter(new FileOutputStream(file), ENCODE);

		out.write(gerarCabecalhoDoRelatorio());
		
		if (!listaAfs.isEmpty()) {
			for(AfPendenteCompradorVO linha : listaAfs){
				out.write(gerarLinhasDoRelatorio(linha));
			}
		}
		
		out.flush();
		out.close();
		
		return file.getAbsolutePath();
	}
	
	private String formatarNumeroMoeda(Double valor) {
		if (valor == null) {
			return "";
		} else {
			Locale loc = new Locale("pt", "BR");
			NumberFormat nb = NumberFormat.getInstance(loc);
			nb.setMinimumFractionDigits(2);
			nb.setMaximumFractionDigits(2);

			return nb.format(valor);
		}
	}

	private void calcularValores(List<AfPendenteCompradorVO> listaAfs) {
		for (AfPendenteCompradorVO vo : listaAfs) {
			
			if (vo.getQtdeRecb() == null) {
				vo.setQtdeRecb(0);
			}
			
			if (vo.getQtdeSolic() == null) {
				vo.setQtdeSolic(0);
			}
			
			if (vo.getCustoUnit() == null) {
				vo.setCustoUnit(0D);
			}
			
			if (vo.getValorEfet() == null) {
				vo.setValorEfet(0D);
			}
			
			Double valorSaldo = (vo.getQtdeSolic() - vo.getQtdeRecb()) * vo.getCustoUnit();
			vo.setValorSaldo(valorSaldo);
			Double valorItem = vo.getValorEfet() + valorSaldo;
			vo.setValorItem(valorItem);
		}
	}
	
	/**
	 * #27143
	 * @author marcelo.deus
	 * @throws ApplicationBusinessException 
	 * @throws IOException 
	 */
	public String gerarRealatorioES() throws ApplicationBusinessException, IOException{
		
		Date data = new Date();
		String dataAtual = DateUtil.dataToString(data, "ddMMyyy");
		
		List<RelatorioESVO> list = scoFornecedorDAO.listarRelatorioEntradasSemEmpenhoSemAssinaturaAF();
		
		if(list.isEmpty()){
			throw new ApplicationBusinessException(RelatorioExcelONExceptionCode.MENSAGEM_NENHUM_REGISTRO_ENCONTRADO_RELATORIO_ES);
		}
		
		final File file = File.createTempFile(DominioNomeRelatorio.ARQUIVO_DADOS_SCO_ES.toString()+dataAtual, EXTENSAO);

		final Writer out = new OutputStreamWriter(new FileOutputStream(file), ENCODE);

		out.write("SITUAÇÃO" + SEPARADOR + "FORNECEDOR" + SEPARADOR + "AF" + SEPARADOR + "SIT_AF" + SEPARADOR + "NR" + SEPARADOR
				+ "DT.ENTRADA" + SEPARADOR + "DT.EMIS.NF" + SEPARADOR + "MODL.LICT" + SEPARADOR + "ARTIGO" + SEPARADOR + "INCISO" + 
				SEPARADOR + "VALOR_EFET_VIG" + SEPARADOR + "VALOR_EMP_VIG\n");
 
		Map<Integer, Double> totalEfetivadoMap = new HashMap<Integer, Double>();
		Map<Integer, Double> totalEmpMap = new HashMap<Integer, Double>();
		for(RelatorioESVO esVO : list){
			
			esVO.setTotalEfetivado(checarTotalEfetivado(totalEfetivadoMap, esVO));
			esVO.setTotalEmpenhado(checarTotalEmpenhado(totalEmpMap, esVO));
				
			out.write(formatarDate(esVO.getSituacao()) + SEPARADOR
					+ formatarDate(esVO.getFornecedor()) + SEPARADOR
					+ formatarDate(esVO.getAf()) + SEPARADOR
					+ formatarDate(esVO.getSitAf()) + SEPARADOR
					+ formatarDate(esVO.getNr()) + SEPARADOR
					+ formatarDate(esVO.getDtentrada()) + SEPARADOR
					+ formatarDate(esVO.getDtemisnf()) + SEPARADOR
					+ formatarDate(esVO.getModllict()) + SEPARADOR 
					+ formatarDate(esVO.getArtigo()) + SEPARADOR
					+ formatarDate(esVO.getInciso()) + SEPARADOR
					+ String.format("%.2f", esVO.getTotalEfetivado()) + SEPARADOR
					+ String.format("%.2f", esVO.getTotalEmpenhado()) + "\n");
		}
		out.flush();
		out.close();
		
		return file.getAbsolutePath();		
	}

	private Double checarTotalEfetivado(Map<Integer, Double> totalEfetivadoMap,
			RelatorioESVO esVO) {
		Double tEfet;
		if (totalEfetivadoMap.get(esVO.getAfnNumero()) != null) {
			tEfet = totalEfetivadoMap.get(esVO.getAfnNumero());
		} else {
			tEfet = scoItemAutorizacaoFornDAO.totalEfetivadoRelatorioEntradasSemEmpenhoSemAssinaturaAF(esVO.getAfnNumero());
			totalEfetivadoMap.put(esVO.getAfnNumero(), tEfet);
		}
		return tEfet;
	}

	private Double checarTotalEmpenhado(Map<Integer, Double> totalEmpMap,
			RelatorioESVO esVO) {
		Double tEmp;
		if (totalEmpMap.get(esVO.getAfnNumero()) != null) {
			tEmp = totalEmpMap.get(esVO.getAfnNumero());
		} else {
			tEmp = scoAfEmpenhoDAO.totalEmpenhadoRelatorioEntradasSemEmpenhoSemAssinaturaAF(esVO.getAfnNumero());
			totalEmpMap.put(esVO.getAfnNumero(), tEmp);
		}
		return tEmp;
	}
	
		private Object formatarDate(Object objeto) {
		String retorno = StringUtils.EMPTY;

		if (objeto != null) {
			if (objeto instanceof Date) {
				retorno = DateUtil.obterDataFormatada((Date) objeto, "dd/MM/yy");
			} else {
				retorno = String.valueOf(objeto);
			}
		}
		return retorno;
	}
		
	public Integer contadorLinhasRelatorioEntradaSemEmpenhoSemAssinaturaAF() throws ApplicationBusinessException{
		List<RelatorioESVO> list = scoFornecedorDAO.listarRelatorioEntradasSemEmpenhoSemAssinaturaAF();
		return list.size();
	}
	
	public String gerarRelatorioAP(Integer numeroPac) throws IOException, ApplicationBusinessException {
		
		List<RelatorioApVO> list = getScoAndamentoProcessoCompraDAO().buscaLocalizacaoProcessoRelatorioAP(numeroPac);
		
		if(list.isEmpty()){
			throw new ApplicationBusinessException(RelatorioExcelONExceptionCode.MENSAGEM_NENHUM_REGISTRO_ENCONTRADO);
		}
		
		final File file = File.createTempFile(DominioNomeRelatorio.ANDAMENTO_PROCESSO_COMPRAS.toString(), EXTENSAO);

		final Writer out = new OutputStreamWriter(new FileOutputStream(file), ENCODE);

		out.write(gerarCabecalhoRelatorioAP());
		
		if (!list.isEmpty()) {
			for(RelatorioApVO linha : list){
				out.write(gerarLinhasDoRelatorioAP(linha));
			}
		}
		
		out.flush();
		out.close();
		
		return file.getAbsolutePath();
		
	}

	private String gerarCabecalhoRelatorioAP() {
		StringBuffer buffer = new StringBuffer();
		buffer.append(this.getResourceBundleValue("HEADER_RELATORIO_AP_EXCEL_PROCESSO"))
			.append(SEPARADOR)
			.append(this.getResourceBundleValue("HEADER_RELATORIO_AP_EXCEL_GERACAO"))
			.append(SEPARADOR)
			.append(this.getResourceBundleValue("HEADER_RELATORIO_AP_EXCEL_MODALIDADE"))
			.append(SEPARADOR)
			.append(this.getResourceBundleValue("HEADER_RELATORIO_AP_EXCEL_LOCAL"))
			.append(SEPARADOR)
			.append(this.getResourceBundleValue("HEADER_RELATORIO_AP_EXCEL_CODIGO"))
			.append(SEPARADOR)
			.append(this.getResourceBundleValue("HEADER_RELATORIO_AP_EXCEL_DT_ENTRADA"))
			.append(SEPARADOR)
			.append(this.getResourceBundleValue("HEADER_RELATORIO_AP_EXCEL_DT_SAIDA"))
			.append(SEPARADOR)
			.append(this.getResourceBundleValue("HEADER_RELATORIO_AP_EXCEL_DIAS"))
			.append(SEPARADOR)
			.append(this.getResourceBundleValue("HEADER_RELATORIO_AP_EXCEL_SITUACAO"))
			.append(QUEBRA_LINHA);

		return buffer.toString();
	}

	private String gerarLinhasDoRelatorioAP(RelatorioApVO linha) {
		StringBuffer buffer = new StringBuffer();
		
		buffer.append(linha.getProcesso()).append(SEPARADOR)
		.append(DateUtil.dataToString(linha.getGeracao(), DD_MM_YY)).append(SEPARADOR)
		.append(linha.getModalidade()).append(SEPARADOR)
		.append(linha.getLocalizacao()).append(SEPARADOR)
		.append(linha.getCod()).append(SEPARADOR)
		.append(DateUtil.dataToString(linha.getDtEntrada(), DD_MM_YY)).append(SEPARADOR)
		.append(DateUtil.dataToString(linha.getDtSaida(), DD_MM_YY)).append(SEPARADOR)
		.append(getDiasPermanencia(linha)).append(SEPARADOR);
		
		if (linha.getDtSaida() == null) {
			buffer.append("Em Trâmite").append(SEPARADOR);
		}else{
			buffer.append("").append(SEPARADOR);
		}
		
		buffer.append(QUEBRA_LINHA);
		
		return buffer.toString();
	}

	private String getDiasPermanencia(RelatorioApVO linha) {
		Date dataSaida = linha.getDtSaida() != null ? linha.getDtSaida() : new Date();
		Date dataEntrada = linha.getDtEntrada();
		
		if(DateUtil.isDatasIguais(DateUtil.truncaData(dataSaida), DateUtil.truncaData(dataEntrada))){
			return "1";
		}else{
			return Long.toString(calculaDiferencaEntreDataDiferentes(dataSaida, dataEntrada));
		}
	}

	private long calculaDiferencaEntreDataDiferentes(Date dataSaida, Date dataEntrada) {
		return Math.round(new Double(DateUtil.calcularDiasEntreDatasComPrecisao(dataEntrada, dataSaida).toString()));
	}

	public String gerarRelatorioEP(Integer anoEP) throws IOException, ApplicationBusinessException {
		
		List<RelatorioEpVo> list = getScoLicitacaoDAO().buscaDadosRelatorioEP(anoEP);
		for (RelatorioEpVo relatorioEpVo : list) {
			relatorioEpVo.setDtEncerramento(buscaDataEncerramento(relatorioEpVo));
		}
		
		if(list.isEmpty()){
			throw new ApplicationBusinessException(RelatorioExcelONExceptionCode.MENSAGEM_NENHUM_REGISTRO_ENCONTRADO);
		}
		
		final File file = File.createTempFile(DominioNomeRelatorio.ANDAMENTO_PROCESSO_COMPRAS.toString(), EXTENSAO);

		final Writer out = new OutputStreamWriter(new FileOutputStream(file), ENCODE);

		out.write(gerarCabecalhoRelatorioEP());
		
		if (!list.isEmpty()) {
			for(RelatorioEpVo linha : list){
				out.write(gerarLinhasDoRelatorioEP(linha));
			}
		}
		
		out.flush();
		out.close();
		
		return file.getAbsolutePath();
	}

	private Date buscaDataEncerramento(RelatorioEpVo relatorioEpVo) {
		if(relatorioEpVo.getPac() != null){
			return getEstoqueFacade().buscaDataEncerramento(relatorioEpVo.getPac());
		}else{
			return null;
		}
	}

	private String gerarLinhasDoRelatorioEP(RelatorioEpVo linha) {
		StringBuffer buffer = new StringBuffer();
		
		buffer.append(linha.getPac()).append(SEPARADOR)
		.append(linha.getLctDescricao()).append(SEPARADOR)
		.append(linha.getDescricao()).append(SEPARADOR)
		.append(linha.getTipo() != null ? linha.getTipo() : "").append(SEPARADOR)
		.append(linha.getInciso() != null ? linha.getInciso() : "").append(SEPARADOR)
		.append(linha.getArtigo() != null ? linha.getArtigo() : "").append(SEPARADOR)
		.append(DateUtil.dataToString(linha.getDtGeracao(), DD_MM_YY)).append(SEPARADOR)
		.append(DateUtil.dataToString(linha.getDtEncerramento(), DD_MM_YY)).append(SEPARADOR)

		.append(QUEBRA_LINHA);
		
		return buffer.toString();
	}

	private String gerarCabecalhoRelatorioEP() {
		StringBuffer buffer = new StringBuffer();
		buffer.append(this.getResourceBundleValue("HEADER_RELATORIO_EP_PAC"))
			.append(SEPARADOR)
			.append(this.getResourceBundleValue("HEADER_RELATORIO_EP_LCT_DESCRICAO"))
			.append(SEPARADOR)
			.append(this.getResourceBundleValue("HEADER_RELATORIO_EP_DESCRICAO"))
			.append(SEPARADOR)
			.append(this.getResourceBundleValue("HEADER_RELATORIO_EP_TIPO"))
			.append(SEPARADOR)
			.append(this.getResourceBundleValue("HEADER_RELATORIO_EP_INCISO"))
			.append(SEPARADOR)
			.append(this.getResourceBundleValue("HEADER_RELATORIO_EP_ARTIGO"))
			.append(SEPARADOR)
			.append(this.getResourceBundleValue("HEADER_RELATORIO_EP_DT_DIGITACAO"))
			.append(SEPARADOR)
			.append(this.getResourceBundleValue("HEADER_RELATORIO_EP_DT_GERACAO"))
			.append(QUEBRA_LINHA);

		return buffer.toString();
	}
	
	private void addText(Object texto, StringBuilder sb) {
		if (texto != null) {
			sb.append(texto);
		}
		sb.append(SEPARADOR);
	}

	
    public String geraArquivoUltimasCompras(List<ScoUltimasComprasMaterialVO> lista) throws IOException {
		
	    SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
	    Calendar c1 = Calendar.getInstance(); // today
		
		File file = File.createTempFile(DominioNomeRelatorio.ULTIMAS_COMPRAS_MATERIAL.getDescricao() + sdf.format(c1.getTime())+"_", EXTENSAO);
		
		Writer out = new OutputStreamWriter(new FileOutputStream(file),ENCODE);
		
		// GERA CABEÇALHOS DO CSV
				out.write(geraCabecalhoUltimasCompras());


  	    // ESCREVE LINHAS DO CSV
	    for (ScoUltimasComprasMaterialVO item : lista) {
			out.write(System.getProperty("line.separator"));
			out.write(geraLinhaUltimasCompras(item));
		}
		
	    out.flush();
		out.close();
		
		return file.getAbsolutePath();
	}
	
	public String geraCabecalhoUltimasCompras() {
			return  "Solicitação" + SEPARADOR + "PAC" 
					+ SEPARADOR + "Modalidade"
					+ SEPARADOR + "Inciso" + SEPARADOR + "Dt Abertura"
					+ SEPARADOR + "AF" + SEPARADOR + "NR"
					+ SEPARADOR + "Data NR" + SEPARADOR + "NF"
					+ SEPARADOR + "Forma Pgto" + SEPARADOR + "Qtde"
					+ SEPARADOR + "Fornecedor"+ SEPARADOR + "Marca"
					+ SEPARADOR + "Valor Unit";
	}
	
	@SuppressWarnings({"PMD.NPathComplexity"})
	private String geraLinhaUltimasCompras(ScoUltimasComprasMaterialVO item) {
		StringBuilder texto = new StringBuilder();
		addText(item.getSolicitacao(), texto);
		addText(item.getNroPAC(), texto);
		addText(item.getModlDesc(), texto);
		addText(item.getInciso(), texto);
		addText(DateUtil.dataToString(item.getDtAbertura(), DD_MM_YY), texto);
		addText(item.getAf(), texto);
		addText(item.getNumeroNr(), texto);
		addText(DateUtil.dataToString(item.getDataNr(), DD_MM_YY), texto);
		addText(item.getNotaFiscal(), texto);
		addText(item.getPgto(), texto);
		addText(item.getQuantidade(), texto);
		addText(item.getRazaoSocial().replace(";"," ").replace("\n"," "), texto);
		addText(item.getMarcaComercial().replace(";"," ").replace("\n"," "), texto);
		DecimalFormat format = new DecimalFormat("##0.0000");
		String valor = format.format(item.getValorUnit());
		addText("\""+valor+"\" ", texto);
		return texto.toString();
	}

	@Override
	protected Log getLogger() {
		return LOG;
	}
	
	/**
	 * Método responsável por chamar a RN que gera o arquivo texto
	 * 
	 * @param fornecedor fornecedor dos títulos
	 * @param dataDividaInicial Data inicial da apuração das dívidas
	 * @param dataDividaFinal Data final da apuração das dívidas
	 * @return Retornar um objeto com a uri e informações para download do arquivo gerado
	 **/
	public ArquivoURINomeQtdVO gerarArquivoTextoContasPeriodo(FiltroRelatoriosExcelVO filtroRelatoriosExcelVO) throws ApplicationBusinessException {
		return this.consultarTituloRN.gerarArquivoTextoContasPeriodo(filtroRelatoriosExcelVO);
	}
	
	/** 
	 * Método responsável por chamar a RN que gera o arquivo de títulos bloqueados
	 * @return Retorna um objeto ArquivoURINomeQtdVO com a uri, nome, tamanho do arquivo CSV para download
	 **/
	public ArquivoURINomeQtdVO gerarArquivoTextoTitulosBloqueados() throws ApplicationBusinessException {
		return this.consultarTituloRN.gerarArquivoTextoTitulosBloqueados();
	}
	
	/**
	 * Método para gerar o arquivo CSV dos pagamentos realizados em um período
	 * 
	 * @param dataDividaInicial Data inicial da busca dos pagamentos realizados
	 * @param dataDividaFinal Data final da busca dos pagamentos realizados
	 * 
	 * @return Retorno da lista de vo's dos pagamentos realizados
	 * */
	public ArquivoURINomeQtdVO gerarArquivoTextoPagamentosRealizadosPeriodo (FiltroRelatoriosExcelVO filtroRelatoriosExcelVO) throws ApplicationBusinessException {
		if(isPeridoValido(filtroRelatoriosExcelVO.getDataInicioDivida(), filtroRelatoriosExcelVO.getDataFimDivida())) {
			return this.consultarTituloRN.gerarArquivoTextoPagamentosRealizadosPeriodo(filtroRelatoriosExcelVO);
		} else {
			throw new ApplicationBusinessException("MENSAGEM_FILTRO_OBRIGATORIO_NAO_PREENCHIDO", Severity.ERROR, filtroRelatoriosExcelVO);
		}
	}
	
	/**
	 * Válida se o período selecionado é válido
	 * @param dataInicial
	 * @param dataFinal
	 */
	private boolean isPeridoValido(Date dataInicial, Date dataFinal) throws ApplicationBusinessException {
		if (!DateUtil.validaDataMenorIgual(dataInicial, dataFinal)) {
			throw new ApplicationBusinessException("MENSAGEM_DATA_INICIAL_MAIOR_DATA_FINAL", Severity.ERROR);
		}
		return true;
	}
}
