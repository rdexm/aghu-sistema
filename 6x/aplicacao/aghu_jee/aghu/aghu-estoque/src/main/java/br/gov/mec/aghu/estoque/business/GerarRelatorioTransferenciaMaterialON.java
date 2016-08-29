package br.gov.mec.aghu.estoque.business;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.compras.business.IComprasFacade;
import br.gov.mec.aghu.dominio.DominioOrdenacaoRelatorioTransferenciaMaterial;
import br.gov.mec.aghu.estoque.dao.SceTransferenciaDAO;
import br.gov.mec.aghu.estoque.vo.RelatorioTransferenciaMaterialVO;
import br.gov.mec.aghu.model.SceTransferencia;
import br.gov.mec.aghu.model.VScoClasMaterial;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.utils.DateFormatUtil;

@Stateless
public class GerarRelatorioTransferenciaMaterialON extends BaseBusiness{

private static final Log LOG = LogFactory.getLog(GerarRelatorioTransferenciaMaterialON.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@Inject
private SceTransferenciaDAO sceTransferenciaDAO;

@EJB
private IComprasFacade comprasFacade;
	
	private static final long serialVersionUID = -5485124508497775337L;
	private static final String SEPARADOR=";";
	private static final String ENCODE="ISO-8859-1";
	private static final String EXTENSAO=".csv";
	private static final String NOME_ARQUIVO="Imprimir_Transferencia_de_Material_COD_";

	/**
	 * 
	 * @return
	 */
	protected SceTransferenciaDAO getSceTransferenciaDAO() {
		return sceTransferenciaDAO;
	}
	
	
	/**
	 * 
	 * @param numTransferenciaMaterial
	 * @param ordemImpressao
	 * @return
	 */
	public List<RelatorioTransferenciaMaterialVO> gerarDadosRelatorioTransferenciaMaterialItens(Integer numTransferenciaMaterial,DominioOrdenacaoRelatorioTransferenciaMaterial ordemImpressao) {		
		SceTransferencia transferencia =  getSceTransferenciaDAO().obterPorChavePrimaria(numTransferenciaMaterial);		
		DominioOrdenacaoRelatorioTransferenciaMaterial ordem = null;
		if(transferencia != null) {
			// RN 03
			if(transferencia.getDtEfetivacao() == null && ordemImpressao == null) {
				ordem = DominioOrdenacaoRelatorioTransferenciaMaterial.ENDERECO_ORIGEM;
			}
			// RN 07
			if(transferencia.getDtEfetivacao() != null && ordemImpressao == null) {
				ordem = DominioOrdenacaoRelatorioTransferenciaMaterial.DESCRICAO;
			}
		}
		// se foi informado a ordem
		if(ordemImpressao != null) {
			ordem = ordemImpressao;
		}		
		List<RelatorioTransferenciaMaterialVO> lista = getSceTransferenciaDAO().pesquisarDadosRelatorioTransferenciaMaterialItens(numTransferenciaMaterial, ordem);
		if(lista != null) {
			for(RelatorioTransferenciaMaterialVO vo : lista) {	
				if (vo.getCn5() != null) {
					VScoClasMaterial vScoClasMaterial = getComprasFacade().obterVScoClasMaterialPorNumero(vo.getCn5().getNumero());
					vo.setCn5Descricao(vScoClasMaterial.getId().getDescricao());
				}
				// RN 01
				if(!vo.getIndEfetivada()) {
					vo.setDtEfetivacaoStr(null);
				} else {
					Date data = new Date(vo.getDtEfetivacao().getTime());
					vo.setDtEfetivacaoStr(DateFormatUtil.fomataDiaMesAno(data));
				}
			}
		}
		return lista;
	}
	
	/**
	 * numTransferenciaMaterial
	 * @param fileName
	 * @param numTransferenciaMaterial
	 * @throws IOException
	 */
	public void efetuarDownloadCSVRelatorioTransferenciaMaterial(final String fileName, final Integer numTransferenciaMaterial) throws IOException{		
		
		/*final FacesContext fc = FacesContext.getCurrentInstance();
		final HttpServletResponse response = (HttpServletResponse) fc.getExternalContext().getResponse();
		final String nomeDownload = NOME_ARQUIVO + numTransferenciaMaterial + EXTENSAO;		
		
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
		fc.responseComplete();*/
	}
	
	/**
	 * Gera arquivo .csv de transferência de materiais
	 * @param numTransferenciaMaterial
	 * @param dados
	 * @param parametros
	 * @return
	 * @throws IOException
	 * @throws FileNotFoundException
	 */	
	public String gerarCsvRelatorioTransferenciaMaterialItens(Integer numTransferenciaMaterial, 
															  List<RelatorioTransferenciaMaterialVO> dados, 
															  Map<String, Object> parametros) throws IOException, FileNotFoundException{
				
		File file = File.createTempFile(NOME_ARQUIVO + numTransferenciaMaterial, EXTENSAO);
		Writer out = new OutputStreamWriter(new FileOutputStream(file), ENCODE);
		// formata campos data
		String dataGeradaStr = "";
		if(parametros.get("dataGerada") != null) {
			dataGeradaStr = parametros.get("dataGerada").toString();
		}
		String dataEfetivadaStr = "";
		if(parametros.get("dataEfetivada") != null) {
			dataEfetivadaStr = parametros.get("dataEfetivada").toString();
		}
		String dataEstornadaStr = "";
		if(parametros.get("dataEstornada") != null) {
			dataEstornadaStr = parametros.get("dataEstornada").toString();
		}
		
		out.write("SEQ" + SEPARADOR 
				+ "IND_TRANSF" + SEPARADOR
				+ "GERADA_EM" + SEPARADOR
				+ "EFETIVADA_EM" + SEPARADOR
				+ "ESTORNADA_EM" + SEPARADOR
				+ "ALMOX_ORIGEM" + SEPARADOR
				+ "DESC_ALMOX_ORIGEM" + SEPARADOR
				+ "ALMOX_DESTINO" + SEPARADOR
				+ "DESC_ALMOX_DESTINO" + SEPARADOR
				+ "CLASS_MAT" + SEPARADOR
				+ "DESC_CLASS_MAT\n");
		
		out.write(parametros.get("seq") + SEPARADOR +
				  parametros.get("indTransferenciaStr") + SEPARADOR +
				  dataGeradaStr + SEPARADOR +
				  dataEfetivadaStr + SEPARADOR +
				  dataEstornadaStr + SEPARADOR +
				  parametros.get("almSeq") + SEPARADOR +
				  parametros.get("almSeqDescricao") + SEPARADOR +
				  parametros.get("almSeqRecebe") + SEPARADOR +
				  parametros.get("almSeqRecebeDescricao") + SEPARADOR +
				  parametros.get("cnNumero") + SEPARADOR +
				  parametros.get("cnNumeroDescricao") + "\n");

		// itens
		out.write("MATERIAL" + SEPARADOR
				+ "UN_MEDIDA" + SEPARADOR
				+ "QTDE_ESTOQUE_MIN" + SEPARADOR
				+ "QTDE_ESTOQUE_DISP" + SEPARADOR
				+ "QTDE_DISP_DEST" + SEPARADOR
				+ "END_DEST" + SEPARADOR
				+ "TRANSF_DEST" + SEPARADOR
				+ "QTDE_DISP_ORIGEM" + SEPARADOR
				+ "END_ORIGEM\n");
		
		for(RelatorioTransferenciaMaterialVO obj : dados) {
			out.write(obj.getNome() + obj.getMatCodigo() + SEPARADOR +
					  obj.getUmdCodigo() + " " + obj.getUmdDescricao() + SEPARADOR + 
					  obj.getQtdeEstqMin() + SEPARADOR +
					  obj.getQtdeDisponivelOrigem() + SEPARADOR + 
					  obj.getQtdeDisponivelDestino() + SEPARADOR + 
					  obj.getEnderecoDestino() + SEPARADOR +
					  obj.getTransferenciaDestino() + SEPARADOR +
					  obj.getQtdeDisponivelOrigem() + SEPARADOR +
					  obj.getEnderecoOrigem() + "\n");	
		}
		out.flush();
		out.close();
		return file.getAbsolutePath();
	}
	
	
	/**
	 * numTransferenciaMaterial
	 * @param fileName
	 * @param numTransferenciaMaterial
	 * @throws IOException
	 */
	public String nameHeaderEfetuarDownloadCSVRelatorioTransferenciaMaterial(final Integer numTransferenciaMaterial) {		
		return NOME_ARQUIVO + numTransferenciaMaterial;		
	}
	
	protected IComprasFacade getComprasFacade() {
		return this.comprasFacade;
	}
	
}
