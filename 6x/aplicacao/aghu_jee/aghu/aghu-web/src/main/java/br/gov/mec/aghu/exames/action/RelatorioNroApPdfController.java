package br.gov.mec.aghu.exames.action;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import br.gov.mec.aghu.action.impressao.SistemaImpressao;
import br.gov.mec.aghu.action.report.ActionReport;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.core.report.DocumentoJasper;
import br.gov.mec.aghu.core.utils.StringUtil;
import br.gov.mec.aghu.impressao.SistemaImpressaoException;


public class RelatorioNroApPdfController extends ActionReport {

	private static final long serialVersionUID = 8516138849151435614L;

	@EJB
	private SistemaImpressao sistemaImpressao;

	private Integer nroAp;


	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}

	
	/**
	 * Impressão direta usando o CUPS.
	 */
	public void directPrint() throws SistemaImpressaoException, ApplicationBusinessException {

		try {
			DocumentoJasper documento = gerarDocumento();

			sistemaImpressao.imprimir(documento.getJasperPrint(), getEnderecoIPv4HostRemoto());

			apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_IMPRESSAO");
		} catch (SistemaImpressaoException e) {
			apresentarExcecaoNegocio(e);
			
		} catch (Exception e) {
			this.apresentarMsgNegocio(Severity.ERROR, "ERRO_GERAR_RELATORIO");
		}

	}

	@Override
	public String recuperarArquivoRelatorio()  {
		return "br/gov/mec/aghu/exames/report/relatorioNroAp.jasper";
	}

	@Override
	public Map<String, Object> recuperarParametros() {

		// Instancia mapa de parametros do relatorio
		Map<String, Object> params = new HashMap<String, Object>(); 
		params.put("nroAp", StringUtil.formataNumeroAp(nroAp));
		
		return params;
	}	
	
	@Override
	public Collection<String> recuperarColecao() throws ApplicationBusinessException {
		List<String> aa = new ArrayList<String>();
		aa.add("teste");
		return aa;
	}

	
	/**
	 * Método responsável por gerar a coleção de dados.
	 */
	public String print() {
		return "relatorio";
	}

	public Integer getNroAp() {
		return nroAp;
	}

	public void setNroAp(Integer nroAp) {
		this.nroAp = nroAp;
	}

}
