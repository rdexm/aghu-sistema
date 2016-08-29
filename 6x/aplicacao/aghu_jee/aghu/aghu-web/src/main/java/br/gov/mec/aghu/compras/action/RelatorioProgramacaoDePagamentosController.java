package br.gov.mec.aghu.compras.action;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.action.report.ActionReport;
import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.compras.contaspagar.vo.TituloProgramadoVO;
import br.gov.mec.aghu.core.dominio.DominioMimeType;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.core.report.DocumentoJasper;
import br.gov.mec.aghu.core.utils.DateUtil;

/**
 * 
 * @author djalmars
 *
 */
public class RelatorioProgramacaoDePagamentosController extends ActionReport 
{
	private static final long serialVersionUID = -7898466511611799281L;

	private static final Log LOG = LogFactory.getLog(RelatorioProgramacaoDePagamentosController.class);

	private static final String NOME_PDF = "LISTA_PAGAMENTOS_PROGRAMADOS_";
	
	@EJB
	protected IParametroFacade parametroFacade;
	
	@PostConstruct
	public void inicio() {
		begin(conversation);
	}

	private List<TituloProgramadoVO> lista = new ArrayList<TituloProgramadoVO>();
	
	private File arquivoGerado;
	
	private Date dataPagamento;

	public void downloadArquivo(List<TituloProgramadoVO> tituloVOs, Date dataPagamento) {
		if (!tituloVOs.isEmpty()) {
			this.lista = tituloVOs;
			
		} else {
			this.lista = new ArrayList<TituloProgramadoVO>();
		}
		
		this.dataPagamento = dataPagamento;
		// Gera o PDF
		try {
			DocumentoJasper documento = gerarDocumento();

            String nomeArquivo = NOME_PDF + DateUtil.obterDataFormatada(this.dataPagamento, "dd_MM_yyyy");

			File file = this.createTempFile(nomeArquivo,".pdf");

            if (file.exists()) {
                file.delete();
            }
//            Recria o arquivo, quando este ja existia gerava problemas de permissão
            file = this.createTempFile(nomeArquivo,".pdf");
			final FileOutputStream out = new FileOutputStream(file);

			out.write(documento.getPdfByteArray(false));
			out.flush();
			out.close();

			arquivoGerado = file;

			if(arquivoGerado != null) {
				download(arquivoGerado, DominioMimeType.PDF.getContentType());
			}
			arquivoGerado = null;
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);

		} catch (Exception e) {
			LOG.error("Exceção capturada: ", e);
			apresentarMsgNegocio(Severity.ERROR, "ERRO_GERAR_RELATORIO");
		}
	}

	/**
	 * Método personalizado para poder gerar o nome do arquivo sem o número randômico.
	 * @param prefix
	 * @param suffix
	 * @return
	 */
	private File createTempFile(String prefix, String suffix) {
	     String tempDir = System.getProperty("java.io.tmpdir");
	     String fileName = prefix.concat(suffix);
	     return new File(tempDir, fileName);
	}

	@Override
	public Collection<TituloProgramadoVO> recuperarColecao() throws ApplicationBusinessException {
		return lista;
	}

	@Override
	public String recuperarArquivoRelatorio() {
		return "br/gov/mec/aghu/compras/compras/relatorioPagamentos.jasper";
	}
	
	@Override
	public Map<String, Object> recuperarParametros() {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("dataAtual", DateUtil.obterDataFormatada(new Date(), "dd/MM/yyyy HH:mm"));
		String hospital;
		try {
			hospital = this.parametroFacade.buscarAghParametro(AghuParametrosEnum.P_HOSPITAL_RAZAO_SOCIAL).getVlrTexto();
			params.put("hospitalLocal", hospital);
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
		params.put("tituloDataProgramacaoPagamento", DateUtil.obterDataFormatada(this.dataPagamento, "dd/MM/yyyy"));
		
		return params;
	}

	/*-*-*-* GETTERS e SETTERS *-*-*-*/
	public List<TituloProgramadoVO> getLista() {
		return lista;
	}
	
	public void setLista(List<TituloProgramadoVO> lista) {
		this.lista = lista;
	}
	
	public File getArquivoGerado() {
		return arquivoGerado;
	}

	public void setArquivoGerado(File arquivoGerado) {
		this.arquivoGerado = arquivoGerado;
	}

	public Date getDataPagamento() {
		return dataPagamento;
	}

	public void setDataPagamento(Date dataPagamento) {
		this.dataPagamento = dataPagamento;
	}
}
