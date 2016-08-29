package br.gov.mec.aghu.estoque.business;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.dominio.DominioFiltroEstocavelRelatorioMensalPosicaoFinalEstoque;
import br.gov.mec.aghu.dominio.DominioFiltroOrdemlRelatorioContagemEstoqueParaInventario;
import br.gov.mec.aghu.dominio.DominioNomeRelatorio;
import br.gov.mec.aghu.estoque.dao.SceEstoqueAlmoxarifadoDAO;
import br.gov.mec.aghu.estoque.vo.RelatorioContagemEstoqueParaInventarioVO;
import br.gov.mec.aghu.core.business.BaseBusiness;

@Stateless
public class GerarRelatorioContagemEstoqueInventarioON extends BaseBusiness {

	private static final Log LOG = LogFactory.getLog(GerarRelatorioContagemEstoqueInventarioON.class);

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}

	@Inject
	private SceEstoqueAlmoxarifadoDAO sceEstoqueAlmoxarifadoDAO;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -5462779059293587829L;
	private static final String SEPARADOR=";";
	private static final String ENCODE="ISO-8859-1";
	private static final String EXTENSAO=".csv";
	
	/**
	 * Obtém dados do Relatório de Contagem de Estoque para Inventário
	 * @param seqAlmoxarifado
	 * @param codigoGrupo
	 * @param estocavel
	 * @param ordem
	 * @return List<RelatorioContagemEstoqueParaInventarioVO>
	 */
	public List<RelatorioContagemEstoqueParaInventarioVO> pesquisarDadosRelatorioContagemEstoqueInventario(
			Short seqAlmoxarifado, Integer codigoGrupo, DominioFiltroEstocavelRelatorioMensalPosicaoFinalEstoque estocavel,
			DominioFiltroOrdemlRelatorioContagemEstoqueParaInventario ordem, boolean disponivelEstoque) {
		return getSceEstoqueAlmoxarifadoDAO().pesquisarDadosRelatorioContagemEstoqueInventario(
				seqAlmoxarifado, codigoGrupo, estocavel, ordem, disponivelEstoque);
	}
	
	private SceEstoqueAlmoxarifadoDAO getSceEstoqueAlmoxarifadoDAO() {
		return sceEstoqueAlmoxarifadoDAO;
	}

	/**
	 * Gera um arquivo CSV do Relatório de Contagem de Estoque para Inventário
	 * @param seqAlmoxarifado
	 * @param codigoGrupo
	 * @param estocavel
	 * @param ordem
	 * @return String Nome do Arquivo
	 * @throws IOException 
	 */
	public String geraCSVRelatorioContagemEstoqueInventario(
			Short seqAlmoxarifado, Integer codigoGrupo,
			DominioFiltroEstocavelRelatorioMensalPosicaoFinalEstoque estocavel,
			DominioFiltroOrdemlRelatorioContagemEstoqueParaInventario ordem, boolean disponivelEstoque, boolean mostraSaldo) throws IOException {
		List<RelatorioContagemEstoqueParaInventarioVO> listaDados = getSceEstoqueAlmoxarifadoDAO()
			.pesquisarDadosRelatorioContagemEstoqueInventario(seqAlmoxarifado, codigoGrupo, estocavel, ordem, disponivelEstoque);
		File file = File.createTempFile(DominioNomeRelatorio.RELATORIO_MENSAL_POSICAO_ESTOQUE.toString(), EXTENSAO);
		Writer out = new OutputStreamWriter(new FileOutputStream(file), ENCODE);
		out.write("GR" + SEPARADOR + "NOME MATERIAL" + SEPARADOR + "CODIGO" + SEPARADOR + "FORN." + SEPARADOR + 
				  "UN" + SEPARADOR + "ENDE" + SEPARADOR + "BLOQ" + SEPARADOR + "DISP" + SEPARADOR + 
				  "SALDO" + SEPARADOR + "CONT.1" + SEPARADOR + "CONT.2" + SEPARADOR + "CONT.3" + SEPARADOR + 
				  "OBSERVAÇÃO\n");
		for (RelatorioContagemEstoqueParaInventarioVO item : listaDados) {
			if(!mostraSaldo){
				item.setQuantidadeBloqueadaEstoqueAlmox(0);
				item.setQuantidadeDisponivelEstoqueAlmox(0);
			}
			
			out.write(item.getCodigoGrupoMaterial() + SEPARADOR + item.getNomeMaterial() + SEPARADOR + item.getCodigoMaterial() + SEPARADOR +
					item.getNumeroFornecedor() + SEPARADOR + item.getUnidadeMedidaCodigo() + SEPARADOR + item.getEnderecoEstoqueAlmox() + SEPARADOR +
					item.getQuantidadeBloqueadaEstoqueAlmox() + SEPARADOR + item.getQuantidadeDisponivelEstoqueAlmox() + SEPARADOR + item.getSaldo() + 
					SEPARADOR + SEPARADOR + SEPARADOR + SEPARADOR + "\n");					
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
	public void efetuarDownloadCSVRelatorioContagemEstoqueInventario(final String fileName) throws IOException{		
		/*final FacesContext fc = FacesContext.getCurrentInstance();
		final HttpServletResponse response = (HttpServletResponse) fc.getExternalContext().getResponse();
		
		response.setContentType("text/csv");
		response.setHeader("Content-Disposition","attachment;filename=" + fileName);
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
}