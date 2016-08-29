package br.gov.mec.aghu.estoque.business;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.compras.business.IComprasFacade;
import br.gov.mec.aghu.dominio.DominioTipoQuantidadeEstoque;
import br.gov.mec.aghu.estoque.vo.RelatorioDiarioMateriaisComSaldoAteVinteDiasVO;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.utils.DateFormatUtil;

@Stateless
public class GerarRelatorioMateriaisComSaldoAteVinteDiasON extends BaseBusiness {

	private static final Log LOG = LogFactory.getLog(GerarRelatorioMateriaisComSaldoAteVinteDiasON.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@EJB
	private IComprasFacade comprasFacade;
	
	@EJB
	private IParametroFacade parametroFacade;

	private static final long serialVersionUID = -7218465228219939233L;
	private static final String SEPARADOR=";";
	private static final String ENCODE="ISO-8859-1";
	private static final String EXTENSAO=".csv";
	private static final String SIGLA = "MS";
	private static final String NOME_ARQUIVO ="Rotina_Diaria_Materiais";
	
	/**
	 * 
	 * @return
	 */
	protected IParametroFacade getParametroFacade() {
		return parametroFacade;
	}

	protected IComprasFacade getComprasFacade() {
		return comprasFacade;
	}
	
	/**
	 * 
	 * @param dados
	 * @return
	 * @throws IOException
	 */
	public String gerarCsvRelatorioMateriaisComSaldoAteVinteDias(List<RelatorioDiarioMateriaisComSaldoAteVinteDiasVO> dados) throws IOException {

		File file = File.createTempFile(nameHeaderEfetuarDownloadCSVRelatorioMateriaisComSaldoAteVinteDias(), EXTENSAO);
		Writer out = new OutputStreamWriter(new FileOutputStream(file), ENCODE);
		
		out.write("GR" + SEPARADOR
				+ "AL" + SEPARADOR
				+ "COD" + SEPARADOR
				+ "MATERIAL" + SEPARADOR
				+ "ABC" + SEPARADOR
				+ "DISP" + SEPARADOR
				+ "DUR" + SEPARADOR
				+ "SLD GER" + SEPARADOR
				+ "DUR" + SEPARADOR
				+ "ULT. ENT" + SEPARADOR
				+ "PPED" + SEPARADOR
				+ "SC" + SEPARADOR
				+ "DT SC" + SEPARADOR
				+ "QTD SOLIC" + SEPARADOR
				+ "LICIT" + SEPARADOR
				+ "DT LIC" + SEPARADOR
				+ "ITEM" + SEPARADOR
				+ "NRO AF" + SEPARADOR
				+ "SALDO" + SEPARADOR
				+ "ENTG" + SEPARADOR
				+ "ALT" + SEPARADOR
				+ "SIT" + SEPARADOR
				+ "FORNECEDOR" + SEPARADOR
				+ "\n");
		
		for(RelatorioDiarioMateriaisComSaldoAteVinteDiasVO obj : dados) {
			out.write(obj.getGmtCodigo() + SEPARADOR +
					  obj.getAlmSeq() + SEPARADOR +
					  obj.getCodigoMaterial() + SEPARADOR +
					  obj.getNomeMaterial() + SEPARADOR +
					  obj.getClassificacaoAbcString() + SEPARADOR +
					  obj.getQuantidadeDisponivel() + SEPARADOR +
					  obj.getDurQuantidadeDisponivel() + SEPARADOR +
					  obj.getQuantidadeEstoque() + SEPARADOR +
					  obj.getDurEstoque() + SEPARADOR +
					  obj.getUltimaDataGeracaoMovMaterialFormatada() + SEPARADOR +
					  obj.getQuantidadePontoPedido() + SEPARADOR +
					  obj.getNumeroSolicitacaoCompra() + SEPARADOR +
					  obj.getDataSolicitacaoFormatada() + SEPARADOR +
					  obj.getQuantidadeSolicitada() + SEPARADOR +
					  obj.getNumeroLicitacao() + SEPARADOR +
					  obj.getDataLicitacaoFormatada() + SEPARADOR +
					  obj.getNumeroItemLicitacao() + SEPARADOR +
					  obj.getConcatenaNumLicitacaoComNroComplemento() + SEPARADOR +
					  obj.getSaldo() + SEPARADOR +
					  obj.getDataPrevisaoEntregaFormatada() + SEPARADOR +
					  obj.getDtAlteracao() + SEPARADOR +
					  obj.getIndSituacaoString() + SEPARADOR +
					  obj.getRazaoSocial() + "\n");
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
	public String nameHeaderEfetuarDownloadCSVRelatorioMateriaisComSaldoAteVinteDias(){
		String dataFormatada = DateFormatUtil.formataDiaMesAnoParaNomeArquivo(Calendar.getInstance().getTime());
		return NOME_ARQUIVO + "_" + SIGLA + "_CPE_" + dataFormatada;
	}
	
	/**
	 * Pesquisa utilizada pela estoria 6634
	 * @param duracaoEstoque
	 * @return
	 * @throws ApplicationBusinessException
	 */
	public List<RelatorioDiarioMateriaisComSaldoAteVinteDiasVO> pesquisarMateriaisComSaldoAteVinteDias(Integer duracaoEstoque,Double percAjuste) throws ApplicationBusinessException {

		AghParametros paramDataCompetencia = getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_COMPETENCIA);
		AghParametros paramLimiteDiasDuracaoEstoque = getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_LIMITE_DIAS_DURACAO_ESTOQUE);	
		AghParametros paramClassificacaoComprasWeb = getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_GRUPO_CLASSIF_COMPRAS_WEB);
		
		List<RelatorioDiarioMateriaisComSaldoAteVinteDiasVO> result = new ArrayList<RelatorioDiarioMateriaisComSaldoAteVinteDiasVO>();
		
		if(duracaoEstoque == null || duracaoEstoque.intValue() == 0) {
			result.addAll(getComprasFacade().pesquisarDadosRelatorioMateriaisAteVinteDiasInicio(paramDataCompetencia.getVlrData(), paramLimiteDiasDuracaoEstoque.getVlrNumerico().intValue(), paramClassificacaoComprasWeb));					
		} else {
			result.addAll(getComprasFacade().pesquisarDadosRelatorioMateriaisAteVinteDiasInicio(paramDataCompetencia.getVlrData(), duracaoEstoque.intValue(), paramClassificacaoComprasWeb));
		}
		
		// TODO
		// Codigo abaixo comentado conforme solicitação da Estoria do usuário:
		// #17137 - ajustar relatório de materiais com saldo até 20 dias.				
		//result.addAll(getComprasFacade().pesquisarDadosRelatorioMateriaisAteVinteDiasPrimeiroUnion(paramDataCompetencia.getVlrData()));
		//result.addAll(getComprasFacade().pesquisarDadosRelatorioMateriaisAteVinteDiasSegundoUnion(paramDataCompetencia.getVlrData()));
		//result.addAll(getComprasFacade().pesquisarDadosRelatorioMateriaisAteVinteDiasTerceiroUnion(paramDataCompetencia.getVlrData()));

		Set<RelatorioDiarioMateriaisComSaldoAteVinteDiasVO> itensRelatorio = new TreeSet<RelatorioDiarioMateriaisComSaldoAteVinteDiasVO>(new OrdenadorUnionRelatorioDiarioMateriaisSaldoAteVinteDias());
		itensRelatorio.addAll(result);
		
		RelatorioDiarioMateriaisComSaldoAteVinteDiasVO anterior = new RelatorioDiarioMateriaisComSaldoAteVinteDiasVO();
		
		for (RelatorioDiarioMateriaisComSaldoAteVinteDiasVO vo : itensRelatorio) {
			
			vo.setMaterialRepetido(vo.equals(anterior));			
			vo.setQuantidadeDisponivel(calcularQuantidadeDisponivel(vo,percAjuste));
			vo.setDurQuantidadeDisponivel(calcularDurQtDisponivel(vo,percAjuste));
			vo.setQuantidadeEstoque(calcularQuantidadeEstoque(vo,percAjuste));
			vo.setDurEstoque(calcularDurEstoque(vo,percAjuste));
			
			Long quantidadeSolicitada = Long.valueOf(vo.getQuantidadePontoPedido() - vo.getQuantidadeDisponivel());
			if(quantidadeSolicitada != null && quantidadeSolicitada.intValue() < 0) {
				vo.setQuantidadeSolicitada(Long.valueOf(0));
			} else {
				vo.setQuantidadeSolicitada(quantidadeSolicitada);
			}
			//vo.setQuantidadeSolicitada(Long.valueOf(vo.getQuantidadePontoPedido() - vo.getQuantidadeDisponivel()));
			vo.setUltimaDataGeracaoMovMaterial(getComprasFacade().recuperarUltimaDataGeracaoMovMaterial(vo.getCodigoMaterial(), vo.getAlmSeq()));
			
			if (vo.getNumeroSolicitacaoCompra() != null) {
				RelatorioDiarioMateriaisComSaldoAteVinteDiasVO itemLicitacao = getComprasFacade().obterLicitacoesRelatorioMateriasAteVinteDias(vo.getNumeroSolicitacaoCompra());				
				if(itemLicitacao == null){
					itemLicitacao = getComprasFacade().pesquisarLicitacoesRelatorioMateriasAteVinteDiasUnionAll(vo.getNumeroSolicitacaoCompra());
				}
				if (itemLicitacao != null) {
					vo.setNumeroLicitacao(itemLicitacao.getNumeroLicitacao());
					vo.setDataLicitacao(itemLicitacao.getDataLicitacao());
					vo.setNumeroItemLicitacao(itemLicitacao.getNumeroItemLicitacao());
					vo.setNumeroItemAutorizacao(itemLicitacao.getNumeroItemAutorizacao());
					vo.setConcatenaNumLicitacaoComNroComplemento(itemLicitacao.getConcatenaNumLicitacaoComNroComplemento());
					vo.setDataPrevisaoEntrega(itemLicitacao.getDataPrevisaoEntrega());
					vo.setDtAlteracao(itemLicitacao.getDtAlteracao());
					vo.setIndSituacao(itemLicitacao.getIndSituacao());
					vo.setRazaoSocial(itemLicitacao.getRazaoSocial());
					vo.setQuantidadeRecebida(itemLicitacao.getQuantidadeRecebida());
					vo.setIafQuantidadeSolicitada(itemLicitacao.getIafQuantidadeSolicitada());
				}
			}	
			anterior = vo;
		}
		
		Set<RelatorioDiarioMateriaisComSaldoAteVinteDiasVO> resultOrdenado = new TreeSet<RelatorioDiarioMateriaisComSaldoAteVinteDiasVO>(new OrdenadorUnionRelatorioDiarioMateriaisSaldoAteVinteDias());
		resultOrdenado.addAll(itensRelatorio);
		
		return new ArrayList<RelatorioDiarioMateriaisComSaldoAteVinteDiasVO>(resultOrdenado);
	}
	
	/**
	 * Metodo que calcula a coluna QT_DISP da consulta C1 #ESTORIA 6634
	 * @param vo
	 * @param percAjuste 
	 * @return
	 */
	private Integer calcularQuantidadeDisponivel(RelatorioDiarioMateriaisComSaldoAteVinteDiasVO vo, Double percAjuste) {
		Integer qtdeEstoque = recuperarQuantidadeEstoque(vo.getCodigoMaterial(), DominioTipoQuantidadeEstoque.DISPONIVEL.toString(), vo.getAlmSeq()).intValue();
		Integer calculo = (int) Math.round(qtdeEstoque - (qtdeEstoque * percAjuste));
		return (calculo + Math.abs(calculo)) == 0 ? 0 : calculo;
		
	}
	
	/**
	 * Metodo que calcula a coluna dur_qt_disp da consulta C1 #ESTORIA 6634
	 * @param vo
	 * @return
	 */
	private Integer calcularDurQtDisponivel(RelatorioDiarioMateriaisComSaldoAteVinteDiasVO vo, Double percAjuste) {
		Integer calculo = (int) Math.round((vo.getEalQtdeDisponivel()	- (vo.getQuantidadePontoPedido()  * percAjuste))
				* vo.getTempoReposicao()
				/ vo.getQuantidadePontoPedido());		
		return (calculo + Math.abs(calculo)) == 0 ? 0 : calculo;
	}
	
	/**
	 * Metodo que calcula a coluna qt_estq da consulta C1 #ESTORIA 6634
	 * @param vo
	 * @return
	 */
	private Integer calcularQuantidadeEstoque(RelatorioDiarioMateriaisComSaldoAteVinteDiasVO vo, Double percAjuste) {
		Integer calculo = (int) (vo.getEgrQtde() - (vo.getQuantidadePontoPedido()  * percAjuste));
		return (calculo + Math.abs(calculo)) == 0 ? 0 : (calculo);			
	}
	
	/**
	 * Metodo que calcula a coluna dur_estq da consulta C1 #ESTORIA 6634
	 * @param vo
	 * @return
	 */
	private Integer calcularDurEstoque(RelatorioDiarioMateriaisComSaldoAteVinteDiasVO vo, Double percAjuste) {
		Integer calculo = (int) Math.round((vo.getEgrQtde() - (vo.getQuantidadePontoPedido()  * percAjuste))
				* vo.getTempoReposicao() / vo.getQuantidadePontoPedido());			
		return calculo + Math.abs(calculo) == 0 ? 0 : calculo;
	}
	
	/**
	 * ORADB SCOC_CALC_EST_FORN
	 * 
	 * @param codigoMaterial
	 * @param tipoEstoqueQuantidade
	 * @param almSeq
	 * 
	 * @return quantidade
	 */
	private Long recuperarQuantidadeEstoque(Integer codigoMaterial, String tipoEstoqueQuantidade, Short almSeq){
		Long resultado = 0l;
		if(DominioTipoQuantidadeEstoque.BLOQUEADA.toString().equals(tipoEstoqueQuantidade)) {
			resultado = getComprasFacade().obterQuantidadeBloqueada(codigoMaterial, almSeq);
		}
		if (DominioTipoQuantidadeEstoque.DISPONIVEL.toString().equals(tipoEstoqueQuantidade)) {
			resultado = getComprasFacade().obterQuantidadeDisponivel(codigoMaterial, almSeq);
		}
		if (DominioTipoQuantidadeEstoque.TOTAL.toString().equals(tipoEstoqueQuantidade)) {
			resultado = getComprasFacade().obterQuantidadeTotal(codigoMaterial, almSeq);
		}
		return resultado;
	}
	
	
	/**
	 * 
	 * @author aghu
	 *
	 */
	class OrdenadorUnionRelatorioDiarioMateriaisSaldoAteVinteDias implements Comparator<RelatorioDiarioMateriaisComSaldoAteVinteDiasVO> {
		@Override
		public int compare(RelatorioDiarioMateriaisComSaldoAteVinteDiasVO arg0,	RelatorioDiarioMateriaisComSaldoAteVinteDiasVO arg1) {
			if (arg0.getGmtCodigo().compareTo(arg1.getGmtCodigo())!=0){
				return arg0.getGmtCodigo().compareTo(arg1.getGmtCodigo()); 
			} else if (arg0.getDurQuantidadeDisponivel() != null && arg1.getDurQuantidadeDisponivel() != null 
						&& arg0.getDurQuantidadeDisponivel().compareTo(arg1.getDurQuantidadeDisponivel())!=0){
				return arg0.getDurQuantidadeDisponivel().compareTo(arg1.getDurQuantidadeDisponivel());
			} else if (arg0.getCodigoMaterial().compareTo(arg1.getCodigoMaterial()) != 0) {
				return arg0.getCodigoMaterial().compareTo(arg1.getCodigoMaterial());
			} else if (arg0.getNumeroSolicitacaoCompra() != null && arg1.getNumeroSolicitacaoCompra() != null) {
				return arg0.getNumeroSolicitacaoCompra().compareTo(arg1.getNumeroSolicitacaoCompra());
			} else {
				return 1;
			}
		}
	}


	public String efetuarDownloadCSVRelatorioMateriaisComSaldoAteVinteDias(
			String fileName) {

		File file = new File(fileName);
		FileInputStream fin = null;
		String retorno = null;
		try {
			// create FileInputStream object
			fin = new FileInputStream(file);

			byte fileContent[] = new byte[(int) file.length()];

			// Reads up to certain bytes of data from this input stream into an
			// array of bytes.
			fin.read(fileContent);
			// create string from byte array
			retorno = new String(fileContent);
		} catch (FileNotFoundException e) {
			LOG.error(e.getMessage(),e);
		} catch (IOException ioe) {
			LOG.error(ioe.getMessage(),ioe);
		} finally {
			// close the streams using close method
			try {
				if (fin != null) {
					fin.close();
				}
			} catch (IOException ioe) {
				LOG.error(ioe.getMessage(),ioe);
			}
		}

		return retorno;
	}

}
