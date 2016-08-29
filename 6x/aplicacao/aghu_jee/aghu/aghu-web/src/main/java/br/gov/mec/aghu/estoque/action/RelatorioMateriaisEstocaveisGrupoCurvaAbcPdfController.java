package br.gov.mec.aghu.estoque.action;

import java.io.File;
import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.action.report.ActionReport;
import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.dominio.DominioNomeRelatorio;
import br.gov.mec.aghu.estoque.business.IEstoqueFacade;
import br.gov.mec.aghu.estoque.vo.RelatorioMateriaisEstocaveisGrupoCurvaAbcVO;
import br.gov.mec.aghu.faturamento.action.RelatorioResumoAIHEmLotePdfController.RelatorioResumoAIHEmLotePdfExceptionCode;
import br.gov.mec.aghu.internacao.cadastrosbasicos.business.ICadastrosBasicosInternacaoFacade;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.report.DocumentoJasper;

public class RelatorioMateriaisEstocaveisGrupoCurvaAbcPdfController extends ActionReport {

	private static final Log LOG = LogFactory.getLog(RelatorioMateriaisEstocaveisGrupoCurvaAbcPdfController.class);

	private static final long serialVersionUID = -1430805840406742654L;
	
	private String fileName;
	
	@EJB
	private ICadastrosBasicosInternacaoFacade cadastrosBasicosInternacaoFacade;
	
	@EJB
	private IEstoqueFacade estoqueFacade;
	
	@EJB
	private IParametroFacade parametroFacade;

	private boolean considerarCompras = true;
	
	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}

	public String inicio()  {
		
			// Gera o PDF
			try {
				final AghParametros parametro =  this.parametroFacade.buscarAghParametro(AghuParametrosEnum.P_AGHU_COMPRAS_ATIVO);
				this.considerarCompras = BigDecimal.ONE.equals(parametro.getVlrNumerico());

				DocumentoJasper documento = gerarDocumento();

				final File file = File.createTempFile(DominioNomeRelatorio.SCER_MAT_EST_GRP_ABC.toString(),".pdf");
				final FileOutputStream out = new FileOutputStream(file);

				out.write(documento.getPdfByteArray(false));
				out.flush();
				out.close();

				fileName = file.getAbsolutePath();
				return "voltar";

			} catch (BaseException e) {
				apresentarExcecaoNegocio(e);
			} catch (Exception e) {
				LOG.error("Exceção capturada: ", e);
				apresentarExcecaoNegocio(new BaseException(
						RelatorioResumoAIHEmLotePdfExceptionCode.ERRO_AO_GERAR_PDF_RELATORIO_RESUMO_AIH_EM_LOTE));
			}

		return null;
	}

	@Override
	public String recuperarArquivoRelatorio() {
		if(this.considerarCompras){
			return "br/gov/mec/aghu/estoque/report/relatorioMateriaisEstocaveisGrupoCurvaAbc.jasper";
		}else{
			return "br/gov/mec/aghu/estoque/report/relatorioMateriaisEstocaveisGrupoSemComprasCurvaAbc.jasper";
		}
	}
	
	/**
	 * Recupera a coleção utilizada no relatório
	 */
	@Override
	public Collection<RelatorioMateriaisEstocaveisGrupoCurvaAbcVO> recuperarColecao() throws ApplicationBusinessException {
		return estoqueFacade.buscarRelatorioMaterialEstocaveisCurvaAbc(considerarCompras);
	}
	
	@Override
	public Map<String, Object> recuperarParametros() {
		Map<String, Object> params = new HashMap<String, Object>();
		String hospital = cadastrosBasicosInternacaoFacade
				.recuperarNomeInstituicaoHospitalarLocal();
		params.put("nomeInstituicao", hospital);
		params.put("nomeRelatorioRodape", "SCER_MAT_EST_GRP_ABC");
		params.put("nomeRelatorio",
				"Materiais Estocáveis por Grupo e Curva ABC");
		
		return params;
	}


	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

}
