/**
 *
 */
package br.gov.mec.aghu.compras.contaspagar.business;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.text.DecimalFormat;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.compras.contaspagar.vo.RelatorioDividaResumoCsvVO;
import br.gov.mec.aghu.compras.contaspagar.vo.RelatorioDividaResumoVO;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;

/**
 * @author joao.pan
 *
 */
@Stateless
public class FcpRelatorioDividaResumoON extends BaseBusiness {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 3755828207648534848L;

	private static final Log LOG = LogFactory.getLog(FcpRelatorioDividaResumoON.class);
	
	/** Prefixo do nome do arquivo csv */
	private static final String PREFIXO_ARQ_CSV = "rel_divida_resumo";
	private static final String SEPARADOR = ";";
	private static final String VAZIO = " ";
	private static final String QUEBRA_LINHA = "\n";
	private static final String ENCODE = "ISO-8859-1";
	private static final String EXTENSAO = ".csv";
	private static final String VALOR_FORMATO = "###,###,##0.00";

	@EJB
	FcpRelatorioDividaResumoRN fcpRelatorioDividaResumoRN;

	@Override
	protected Log getLogger() {
		return LOG;
	}	
	
	/**
	 * Método que monta o arquivo CSV de acordo com o layout especificado 
	 * @param relatorioDividaVencido colecao de titulos vencidos
	 * @param relatorioDividaAVencer colecao de titulos a vencer
	 * @return path
	 * @throws IOException
	 * @throws ApplicationBusinessException
	 */
	
	private String geraCSVRelatorioDividaResumo(RelatorioDividaResumoCsvVO relatorioDividaVencido, RelatorioDividaResumoCsvVO relatorioDividaAVencer) throws IOException, ApplicationBusinessException{		
		String nomeDiretorioTemp = System.getProperty ("java.io.tmpdir");
		File file = new File(nomeDiretorioTemp + File.separator + PREFIXO_ARQ_CSV + EXTENSAO);
		final Writer out = new OutputStreamWriter(new FileOutputStream(file), ENCODE);		
		int maxSize = relatorioDividaVencido.getTitulos().size() > relatorioDividaAVencer.getTitulos().size() ? relatorioDividaVencido.getTitulos().size() : relatorioDividaAVencer.getTitulos().size();
		boolean vencidoTotalizado = relatorioDividaVencido.getTitulos().size() == 0, aVencerTotalizado = relatorioDividaAVencer.getTitulos().size() == 0; 
		if (maxSize > 0) {
			//Cabeçalho
			setCabecalho(out);
			for(int i = 0; i <= (maxSize + 1); i++) {
				RelatorioDividaResumoVO tituloAVencer = setRelatorioDividaResumo(relatorioDividaAVencer, i);
				// A linha abaixo pode inserir data e valor, valor total dos titulos ou valor em branco
				setValorLinha(relatorioDividaVencido, out, vencidoTotalizado, i, false);
				out.write(SEPARADOR + VAZIO + SEPARADOR);
				// A linha abaixo pode inserir data e valor, valor total dos titulos ou valor em branco
				setValorLinha(relatorioDividaAVencer, out, aVencerTotalizado, i, true);
				if(!vencidoTotalizado && relatorioDividaVencido.getTitulos().size() == (i - 1)) {
					vencidoTotalizado = true;
				}
				if(!aVencerTotalizado && relatorioDividaAVencer.getTitulos().size() == (i - 1)) {
					aVencerTotalizado = true;
				}
				if (tituloAVencer == null) {
					out.write(QUEBRA_LINHA);
				}
			}
			out.write(QUEBRA_LINHA);
			out.write("Valor Total da Dívida" + SEPARADOR + (new DecimalFormat(VALOR_FORMATO).format(relatorioDividaVencido.getTotal() + relatorioDividaAVencer.getTotal())));
		}
		out.flush();
		out.close();
		return file.getAbsolutePath();
	}
	
	/**
	 * Método que insere uma linha no CSV de acordo com a necessidade. (valor e data, valor total ou espaço em branco)
	 * @param relatorio coleção de titulos
	 * @param out objeto OutputStreamWriter
	 * @param isTotalizado se já foi totalizado
	 * @param posicao posição da interação
	 * @param isAVencer se é do tipo a vencer
	 * @throws IOException
	 */
	private void setValorLinha(RelatorioDividaResumoCsvVO relatorio,	final Writer out, boolean isTotalizado, int posicao, boolean isAVencer) throws IOException {
		RelatorioDividaResumoVO titulo = setRelatorioDividaResumo(relatorio, posicao);		
			out.write(titulo != null ? titulo.getData() + SEPARADOR + new DecimalFormat(VALOR_FORMATO).format(titulo.getValor()) + 
					(isAVencer ? QUEBRA_LINHA : VAZIO) : (!isTotalizado && relatorio.getTitulos().size() == (posicao - 1) ? "Total" + SEPARADOR +  
							new DecimalFormat(VALOR_FORMATO).format(relatorio.getTotal()) : VAZIO + SEPARADOR + VAZIO));
	}
	
	/**
	 * Método que fornece um objeto do tipo RelatorioDividaResumoVO
	 * @param relatorio coleção de RelatorioDividaResumoVO
	 * @param posicao posição no vetor
	 * @return RelatorioDividaResumoVO
	 */
	private RelatorioDividaResumoVO setRelatorioDividaResumo(RelatorioDividaResumoCsvVO relatorio, int posicao) {
		RelatorioDividaResumoVO titulo = relatorio.getTitulos().size() > posicao ? relatorio.getTitulos().get(posicao) : null;
		return titulo;
	}
	
	/**
	 * Método que fornece o header do CSV
	 * @param out objeto OutputStreamWriter
	 * @throws IOException
	 */
	private void setCabecalho(final Writer out) throws IOException {
		out.write("Atrasados" + SEPARADOR + VAZIO + SEPARADOR + VAZIO + SEPARADOR + "A Vencer" + QUEBRA_LINHA 
				+ "Vencimento" + SEPARADOR + "Valor" + SEPARADOR + VAZIO + SEPARADOR + "Vencimento" + SEPARADOR + "Valor" + QUEBRA_LINHA);
	}	

	/**
	 * @return the fcpRelatorioDividaResumoRN
	 */
	public FcpRelatorioDividaResumoRN getFcpRelatorioDividaResumoRN() {
		return fcpRelatorioDividaResumoRN;
	}

	/**
	 * @param fcpRelatorioDividaResumoRN
	 *            the fcpRelatorioDividaResumoRN to set
	 */
	public void setFcpRelatorioDividaResumoRN(FcpRelatorioDividaResumoRN fcpRelatorioDividaResumoRN) {
		this.fcpRelatorioDividaResumoRN = fcpRelatorioDividaResumoRN;
	}

	/**
	 *  Método que retorna a coleção com os títulos atrasados
	 * @param dataFinal
	 * @return
	 * @throws BaseException 
	 */
	public List<RelatorioDividaResumoVO> pesquisarDividaResumoAtrasados(Date dataFinal) throws BaseException {
		return getFcpRelatorioDividaResumoRN().pesquisarGrupoAtrasado(dataFinal);
	}

	/**
	 *  Método que retorna a coleção com os títulos a vencer
	 * @param dataFinal
	 * @return
	 * @throws BaseException 
	 */
	public List<RelatorioDividaResumoVO> pesquisarDividaResumoAVencer(Date dataFinal) throws BaseException {
		return getFcpRelatorioDividaResumoRN().pesquisarGrupoAVencer(dataFinal);
	}

	/**
	 *  Método que gera o arquivo de relatório CSV
	 * @param dataFinal
	 * @return
	 * @throws IOException 
	 * @throws BaseException 
	 */
	public String gerarCSVDividaResumo(Date dataFinal) throws IOException, BaseException {		
		List<RelatorioDividaResumoVO> atrasados = pesquisarDividaResumoAtrasados(dataFinal);
		List<RelatorioDividaResumoVO> aVencer = pesquisarDividaResumoAVencer(dataFinal);		
		RelatorioDividaResumoCsvVO atrasadosCsv = new RelatorioDividaResumoCsvVO(atrasados, getTotalByVO(atrasados));
		RelatorioDividaResumoCsvVO aVencerCsv = new RelatorioDividaResumoCsvVO(aVencer, getTotalByVO(aVencer));		
		return geraCSVRelatorioDividaResumo(atrasadosCsv, aVencerCsv);

	}
	
	/**
	 * Método que retorna os valores para o relatório totalizado
	 * @param listaVO
	 * @return
	 */
	private Double getTotalByVO(List<RelatorioDividaResumoVO> listaVO) {		
		Double total = new Double(0);		
		for (RelatorioDividaResumoVO dividaVO : listaVO) {
				total+=dividaVO.getValor();
		}		
		return total;
	}
	
}
