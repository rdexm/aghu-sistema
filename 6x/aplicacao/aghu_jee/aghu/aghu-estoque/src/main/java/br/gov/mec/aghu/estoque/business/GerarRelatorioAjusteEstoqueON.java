package br.gov.mec.aghu.estoque.business;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
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
import br.gov.mec.aghu.dominio.DominioNomeRelatorio;
import br.gov.mec.aghu.estoque.dao.SceMovimentoMaterialDAO;
import br.gov.mec.aghu.estoque.vo.RelatorioAjusteEstoqueVO;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;

@Stateless
public class GerarRelatorioAjusteEstoqueON extends BaseBusiness{

	private static final Log LOG = LogFactory.getLog(GerarRelatorioAjusteEstoqueON.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}


	@EJB
	private IParametroFacade parametroFacade;
	
	@Inject
	private SceMovimentoMaterialDAO sceMovimentoMaterialDAO;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -2556113587693865867L;
	private static final String SEPARADOR=";";
	private static final String ENCODE="ISO-8859-1";
	private static final String EXTENSAO=".csv";
	
	protected SceMovimentoMaterialDAO getSceMovimentoMaterialDAO(){
		return sceMovimentoMaterialDAO;
	}
	
	protected IParametroFacade getParametroFacade() {
		return parametroFacade;
	}
	
	/**
	 * Realiza a pesquisa dos dados do relatório
	 * @param dataCompetencia
	 * @param siglasTipoMovimento
	 * @return
	 */
	public List<RelatorioAjusteEstoqueVO> pesquisarDadosRelatorioAjusteEstoque(
			Date dataCompetencia, List<String> siglasTipoMovimento) {
		List<RelatorioAjusteEstoqueVO> ajustes = getSceMovimentoMaterialDAO().pesquisarDadosRelatorioAjusteEstoque(dataCompetencia, siglasTipoMovimento);
		return 	ajustes;
	}

	/**
	 * Gera o relatório
	 * @param dtCompetencia
	 * @param siglasTipoMovimento
	 * @return
	 * @throws IOException
	 * @throws ApplicationBusinessException
	 */
	@SuppressWarnings({ "rawtypes", "unchecked"})
	public String gerarCSVRelatorioAjusteEstoque(Date dtCompetencia,
			List<String> siglasTipoMovimento) throws IOException, ApplicationBusinessException {
		List<RelatorioAjusteEstoqueVO> listaDados = getSceMovimentoMaterialDAO().pesquisarDadosRelatorioAjusteEstoque(dtCompetencia, siglasTipoMovimento);
		File file = File.createTempFile(DominioNomeRelatorio.RELATORIO_AJUSTE_ESTOQUE.toString(), EXTENSAO);
		Writer out = new OutputStreamWriter(new FileOutputStream(file), ENCODE);
		Date dataGeracao = null;
		Date dataGeracaoAux = null;
		Double totalAjsts = 0.0;
		Double totalAjste = 0.0;
		NumberFormat df = DecimalFormat.getCurrencyInstance(new Locale("pt","BR"));
		for (RelatorioAjusteEstoqueVO item : listaDados) {
			dataGeracao = br.gov.mec.aghu.core.utils.DateUtil.truncaData(item.getDataGeracao());
			if(!dataGeracao.equals(dataGeracaoAux)){
				out.write("\n\nDT GERAÇÃO: " + br.gov.mec.aghu.core.utils.DateUtil.obterDataFormatada(dataGeracao , "dd/MM/yyyy") +"\n\n");
				out.write("GR" + SEPARADOR + "CÓDIGO" + SEPARADOR + "NOME MATERIAL" + SEPARADOR + "QTDE AJ" + SEPARADOR + 
						  "VALOR UNIT" + SEPARADOR + "VALOR AJST" + SEPARADOR + "TIPO" + SEPARADOR + "MOTIVO AJUSTE\n");
				dataGeracaoAux = dataGeracao;
			}
			out.write(item.getCodigoGrupoMaterial() + SEPARADOR + item.getCodigoMaterial() + SEPARADOR + item.getNomeMaterial() + SEPARADOR +
					item.getQuantidadeAjuste() + SEPARADOR + df.format(item.getValorUnitario()) + SEPARADOR + df.format(item.getValorAjuste()) + SEPARADOR +
					item.getSiglaTipoMovimento() + SEPARADOR + item.getDescricaoMotivoMovimento() +"\n");
			if(getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_SIGLA_TIPO_MOVIMENTO_AJUSTE_MENOS).getVlrTexto().equals(item.getSiglaTipoMovimento())){
				totalAjsts += item.getValorAjuste();
			}else if(getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_SIGLA_TIPO_MOVIMENTO_AJUSTE_MAIS).getVlrTexto().equals(item.getSiglaTipoMovimento())){
				totalAjste += item.getValorAjuste();
			}
		}
		
		out.write("\n\nTOTAL AJSTS: "+df.format(totalAjsts)+"   "+"TOTAL AJSTE: "+df.format(totalAjste));
		out.write("\n\nVALOR TOTAL POR MOTIVO DE AJUSTE DE ESTOQUE\n");
		
		out.write("MOTIVO" + SEPARADOR + "VALOR TOTAL\n");
		for(RelatorioAjusteEstoqueVO item : listaDados){
			out.write(item.getDescricaoMotivoMovimento()+ SEPARADOR + df.format(item.getValorAjuste())+"\n");  
		}
		
		out.flush();
		out.close();
		
		return file.getAbsolutePath();
	}
	
	/**
	 * Efetua o download do relatório gerado em CSV
	 * @param fileName
	 * @throws IOException
	 */
	public void efetuarDownloadCSVRelatorioAjusteEstoque(final String fileName, final Date dataCompetencia) throws IOException{		
		/*final FacesContext fc = FacesContext.getCurrentInstance();
		final String nomeDownload = "Rotina_Mensal_Materiais_AJ_CPE_" + new SimpleDateFormat("MM_yyyy").format(dataCompetencia) + EXTENSAO;
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
		}
		*/
	}
	
	/**
	 * Efetua o download do relatório gerado em CSV
	 * @param fileName
	 * @throws IOException
	 */
	public String nameHeaderEfetuarDownloadCSVRelatorioAjusteEstoque(final Date dataCompetencia) {		
		return "Rotina_Mensal_Materiais_AJ_CPE_" + new SimpleDateFormat("MM_yyyy").format(dataCompetencia)+".csv";
	}
	
}
