package br.gov.mec.aghu.estoque.business;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.dominio.DominioClassifABC;
import br.gov.mec.aghu.dominio.DominioNomeRelatorio;
import br.gov.mec.aghu.estoque.dao.SceEstoqueGeralDAO;
import br.gov.mec.aghu.estoque.dao.SceMovimentoMaterialDAO;
import br.gov.mec.aghu.estoque.vo.RelatorioMensalMateriaisClassificacaoAbcVO;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;

@Stateless
public class GerarRelatorioMateriaisClassificacaoABCON extends BaseBusiness {

	private static final Log LOG = LogFactory.getLog(GerarRelatorioMateriaisClassificacaoABCON.class);

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}

	@Inject
	private SceMovimentoMaterialDAO sceMovimentoMaterialDAO;

	@Inject
	private SceEstoqueGeralDAO sceEstoqueGeralDAO;
	
	@EJB
	private IParametroFacade parametroFacade;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -656728709036528187L;
	private static final String SEPARADOR=";";
	private static final String ENCODE="ISO-8859-1";
	private static final String EXTENSAO=".csv";
	
	/**
	 * Obtém código parametrizado do fornecedor HCPA
	 * @return
	 * @throws ApplicationBusinessException
	 */
	private Integer obterCodigoFornecedorHCPA() throws ApplicationBusinessException{
		Integer codigo = null;
		AghParametros parametro = null;
		parametro = getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_FORNECEDOR_PADRAO);
		if(parametro != null){
			codigo = parametro.getVlrNumerico().intValue();
		}
		return codigo;
	}
	
	/**
	 * Recupera lista de itens do relatório mensal de materiais Classificação ABC <br>
	 *  @param mesCompetencia;
	 *	@param grupo;
	 *  @return List<RelatorioMensalMateriaisClassificacaoAbcVO>
	 *   
	 *  @since 05/09/2011
	 */
	public List<RelatorioMensalMateriaisClassificacaoAbcVO> pesquisarDadosRelatorioMensalMateriaisClassificacaoAbc(
			Date mesCompetencia) throws ApplicationBusinessException {
		Double totalMediaTrimestre = 0.0;
		Double consumoMedioTrimestral = 0.0;
		Double percentualAcumulado = 0.0;
		Integer numeroFornecedor = obterCodigoFornecedorHCPA();
		List<RelatorioMensalMateriaisClassificacaoAbcVO> listaRelatorio = getSceEstoqueGeralDAO().
					pesquisarDadosRelatorioMensalMateriaisClassificacaoAbc(mesCompetencia, numeroFornecedor);
		
		 //RN07 de #6638
		
		consumoMedioTrimestral  = obterConsumoMediaTrimestralTotalMovimentoMaterial(mesCompetencia);
		
		if(consumoMedioTrimestral != null){
			
			totalMediaTrimestre = consumoMedioTrimestral/3;
		
		
			for(RelatorioMensalMateriaisClassificacaoAbcVO registro: listaRelatorio)
			{
				
				BigDecimal bigDez = new BigDecimal((registro.getConsumoMes()/totalMediaTrimestre)*100);
				
				//RN05 de #6638			
				registro.setPercentTotal(bigDez.setScale(2,RoundingMode.HALF_UP).doubleValue());
				//RN06 de #6638
				percentualAcumulado = percentualAcumulado + registro.getPercentTotal();
				registro.setPercentAcum(percentualAcumulado);
				//RN03 de #6638
				RelatorioMensalMateriaisClassificacaoAbcVO resultadoFunctionCfClAtual = obterClassificacaoABCAtual(mesCompetencia, registro.getCodigo(), numeroFornecedor);
				if(resultadoFunctionCfClAtual != null) {
					registro.setClassificacaoAbcClAtual(resultadoFunctionCfClAtual.getClassificacaoAbcClAtual());
					registro.setSubClassificacaoAbcClAtual(resultadoFunctionCfClAtual.getSubClassificacaoAbcClAtual());	       
				}
			}
		}
		return listaRelatorio;
	}
	
	/**
	 * Converte um número para seu formato decimal String. 
	 * @param numero
	 * @return Retorna String vazio se for nulo, senão retorna número formatado
	 */
	private String converterNumeroToString(Double numero){
		NumberFormat df = DecimalFormat.getCurrencyInstance(new Locale("pt","BR"));
		String numStr = "";
		if(numero != null){
			numStr = df.format(numero);
		}
		return numStr;			
	}
	
	/**
	 * Gera o relatório de materiais classificação ABC como arquivo CSV.
	 * @param dtCompetencia
	 * @return
	 * @throws IOException
	 * @throws ApplicationBusinessException
	 */
	public String gerarCSVRelatorioMensalMateriaisClassificacaoAbc(
			Date dtCompetencia) throws IOException, ApplicationBusinessException {
		File file = File.createTempFile(DominioNomeRelatorio.RELATORIO_CLASSIFICACAO_ABC.toString(), EXTENSAO);
		List<RelatorioMensalMateriaisClassificacaoAbcVO> listaRelatorio = this.pesquisarDadosRelatorioMensalMateriaisClassificacaoAbc(
				dtCompetencia);
		Writer out = new OutputStreamWriter(new FileOutputStream(file), ENCODE);
		Integer totalItensA=0,
				totalItensB=0,
				totalItensC=0;
		Double totalConsA=0.0,
			   totalConsB=0.0,
			   totalConsC=0.0,		
			   totalConsumo=0.0,
			   percConsumoA = 0.0,
			   percConsumoB = 0.0,
			   percConsumoC = 0.0; 
		
		//Escreve a linha referente ao cabeçalho
		out.write(SEPARADOR + "CÓDIGO" + SEPARADOR + "NOME MATERIAL" + SEPARADOR + "GR" + SEPARADOR + 
				  "E" + SEPARADOR + "MEDIA TRIM" + SEPARADOR + "CONSUMO MÊS" + SEPARADOR + "%TOTAL" + SEPARADOR + 
				  "%ACUM" + SEPARADOR + "MD.ANT" + SEPARADOR +  "MD.ATUAL\n");
		int cont = 1;
		for (RelatorioMensalMateriaisClassificacaoAbcVO registro : listaRelatorio) {
			out.write(cont++ + SEPARADOR + registro.getCodigo() + SEPARADOR + registro.getNomeMaterial() + SEPARADOR + 
					registro.getGrupoMaterialStr() + SEPARADOR + registro.getEstocavelStr() + SEPARADOR + 
					converterNumeroToString(registro.getValorTrimestre()) + SEPARADOR + converterNumeroToString(registro.getConsumoMes()) + SEPARADOR + 
					converterNumeroToString(registro.getPercentTotal()) + SEPARADOR + converterNumeroToString(registro.getPercentAcum()) + SEPARADOR + 
					registro.getMedAnterior() + SEPARADOR +  registro.getMedAtual() + "\n");
			if(DominioClassifABC.A.equals(registro.getClassificacaoAbc())){
				totalItensA++;
				totalConsA += registro.getConsumoMes();
			}else if(DominioClassifABC.B.equals(registro.getClassificacaoAbc())){
				totalItensB++;
				totalConsB += registro.getConsumoMes();
			}else if(DominioClassifABC.C.equals(registro.getClassificacaoAbc())){
				totalItensC++;
				totalConsC += registro.getConsumoMes();
			}
		}		
		totalConsumo = totalConsA + totalConsB + totalConsC;
		percConsumoA = (totalConsA / totalConsumo) * 100;
		percConsumoB = (totalConsB / totalConsumo) * 100;
		percConsumoC = (totalConsC / totalConsumo) * 100;
		out.write("\nCATEGORIA" + SEPARADOR + "TOTAL ITENS" + SEPARADOR + "TOTAL CONSUM" + SEPARADOR + "%CONSUM\n");
		out.write(DominioClassifABC.A.getDescricao() + SEPARADOR + totalItensA + SEPARADOR + converterNumeroToString(totalConsA) + SEPARADOR + converterNumeroToString(percConsumoA) + "\n");
		out.write(DominioClassifABC.B.getDescricao() + SEPARADOR + totalItensB + SEPARADOR + converterNumeroToString(totalConsB) + SEPARADOR + converterNumeroToString(percConsumoB) + "\n");
		out.write(DominioClassifABC.C.getDescricao() + SEPARADOR + totalItensC + SEPARADOR + converterNumeroToString(totalConsC) + SEPARADOR + converterNumeroToString(percConsumoC) + "\n");
		out.write(SEPARADOR + SEPARADOR + converterNumeroToString(totalConsumo) + SEPARADOR +"\n");
		out.flush();
		out.close();
		
		return file.getAbsolutePath();
	}

	/**
	 * Efetua o download do relatório gerado em CSV
	 * @param fileName
	 * @throws IOException
	 */
	public String nameHeaderEfetuarDownloadCSVMensalMateriaisClassificacaoAbc(final Date competencia) {
		String dataFormatada = new SimpleDateFormat("MM_yyyy").format(competencia);
		return "Rotina_Mensal_Materiais_CL_CPE_" + dataFormatada+".csv";
	}
	
	/**
	 * Efetua o download do relatório gerado em CSV
	 * @param fileName
	 * @throws IOException
	 */
	public void efetuarDownloadCSVMensalMateriaisClassificacaoAbc(final String fileName, final Date competencia) throws IOException {
		
		/*final FacesContext fc = FacesContext.getCurrentInstance();
		String dataFormatada = new SimpleDateFormat("MM_yyyy").format(competencia);
		
		final String nomeDownload = "Rotina_Mensal_Materiais_CL_CPE_" + dataFormatada + EXTENSAO;
		if(fc!=null && fc.getExternalContext()!=null && fc.getExternalContext().getResponse()!=null){
			final HttpServletResponse response = (HttpServletResponse) fc.getExternalContext().getResponse();
			
			response.setContentType("text/csv");
			response.setHeader("Content-Disposition","attachment;filename=" + nomeDownload);
			response.getCharacterEncoding();
			
			final OutputStream out = response.getOutputStream();
			final Scanner scanner = new Scanner(new FileInputStream(fileName), ENCODE);
			
			while (scanner.hasNextLine()){
				out.write(scanner.nextLine().getBytes(ENCODE));
				out.write(System.getProperty("line.separator").getBytes(ENCODE));
			}
			
			scanner.close();
			out.flush();
			out.close();
			fc.responseComplete();
		}*/
	}
	
	/**
	 * Pesquisa o consumo médio trimestral total de materiais de acordo com sua movimentação
	 * @ORADB: Fórmula de form VLR_TOTAL_MEDIA_3MESESFormula
	 * Muito similar a SCEC_CONS_MED_TRIM implementada em SceMovimentoMaterialRN. Por isso houve reaproveitamento.
	 * A diferença é que a divisão /3 já é feita na criteria e o parâmetro do código do material não é utilizado.
	 * @param mesCompetencia
	 * @return Consumo médio trimestral
	 */
	private Double obterConsumoMediaTrimestralTotalMovimentoMaterial(Date mesCompetencia){
		return getSceMovimentoMaterialDAO().obterConsumoMediaTrimestralMovimentoMaterial(mesCompetencia, null);
	}
	
	/**
	 * Obtém a classificação ABC atual de um material de acordo com a competência e seu código
	 * @ORADB: Fórmula de form CF_CL_ATUALFormula
	 * @return Classificação AtualRotinaMensalMateriaisController
	 */
	private RelatorioMensalMateriaisClassificacaoAbcVO obterClassificacaoABCAtual(Date mesCompetencia, 
			Integer codigoMaterial, Integer numeroFornecedor){
		return getSceEstoqueGeralDAO().obterClassificacaoABCAtual(mesCompetencia, codigoMaterial, numeroFornecedor);
	}
	
	private SceMovimentoMaterialDAO getSceMovimentoMaterialDAO() {
		return sceMovimentoMaterialDAO;
	}

	private SceEstoqueGeralDAO getSceEstoqueGeralDAO() {
		return sceEstoqueGeralDAO;
	}
	
	protected IParametroFacade getParametroFacade() {
		return parametroFacade;
	}
}
