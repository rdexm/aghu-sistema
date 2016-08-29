package br.gov.mec.aghu.faturamento.action;

import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.action.report.ActionReport;
import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.faturamento.business.IFaturamentoFacade;
import br.gov.mec.aghu.faturamento.vo.PendenciaEncerramentoVO;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.FatLogError;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.core.report.DocumentoJasper;

public class RelatorioLogInconsistenciaCargaController extends ActionReport {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1186662052475391712L;

	private static final Log LOG = LogFactory
			.getLog(RelatorioLogInconsistenciaCargaController.class);

	private Log getLog() {
		return LOG;
	}
	
	private static final String PAGINA_RELATORIO="relatorioLogInconsistenciaCargaPdf";
	
	private static final String VOLTAR_PARA="gerarArquivoAutorizacaoSms";

	@EJB
	private IFaturamentoFacade faturamentoFacade;

	@EJB
	private IParametroFacade parametroFacade;

	private Collection<PendenciaEncerramentoVO> collection;
	
	private FatLogError logError;

	@PostConstruct
	protected void init() {
		begin(conversation, true);

	}

	public Collection<PendenciaEncerramentoVO> getCollection() {
		return collection;
	}

	public void setCollection(Collection<PendenciaEncerramentoVO> collection) {
		this.collection = collection;
	}

	@Override
	public Collection<PendenciaEncerramentoVO> recuperarColecao() {
		collection = faturamentoFacade.obterDadosRelatorioLogInconsistenciaCarga(faturamentoFacade.obterUltimaDataCriacaoFatLogError());
		return collection;
	}

	@Override
	public String recuperarArquivoRelatorio() {
		return "br/gov/mec/aghu/faturamento/report/relatorioLogInconsistenciaCarga.jasper";
	}

	public String visualizarRelatorio() {
		return "relatorioPendenciaEncerramentoPdf";
	}

	public void directPrint() {
		try {
			recuperarColecao();
			if (collection == null || collection.isEmpty()) {
				this.apresentarMsgNegocio(Severity.INFO, "NENHUM_LOG");
				return;
			}
			DocumentoJasper documento = gerarDocumento();
			this.sistemaImpressao.imprimir(documento.getJasperPrint(), super.getEnderecoIPv4HostRemoto());
			recuperarColecao();
			apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_IMPRESSAO");
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		} catch (Exception e) {
			getLog().error("Exceção capturada: ", e);
			apresentarMsgNegocio(Severity.ERROR, "ERRO_GERAR_RELATORIO");
		}
	}
	
	
	public String visualizarRelatorioInconsistencia(){
		recuperarColecao();
		if (collection == null || collection.isEmpty()) {
			this.apresentarMsgNegocio(Severity.INFO, "NENHUM_LOG");
		} else {
			recuperarColecao();
			return PAGINA_RELATORIO;
		}
		return null;
	}


	public Map<String, Object> recuperarParametros() {
		Map<String, Object> params = new HashMap<String, Object>();
		Date dataAtual = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
		params.put("dataAtual", sdf.format(dataAtual));
		params.put("nomeRelatorio", "FATR_INT_LOG_AIH");
		params.put("nomeArquivo", logError != null ? logError.getNomeArquivoImp(): "");
		AghParametros parametroRazaoSocial = null;
		try {
			parametroRazaoSocial = parametroFacade
					.buscarAghParametro(AghuParametrosEnum.P_HOSPITAL_RAZAO_SOCIAL);
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
		if (parametroRazaoSocial != null) {
			params.put("nomeHospital", parametroRazaoSocial.getVlrTexto());
		}
		return params;
	}

	public String voltar() {
		return VOLTAR_PARA;
	}

	public FatLogError getLogError() {
		return logError;
	}

	public void setLogError(FatLogError logError) {
		this.logError = logError;
	}

}
