package br.gov.mec.aghu.ambulatorio.action;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import br.gov.mec.aghu.action.impressao.SistemaImpressao;
import br.gov.mec.aghu.action.report.ActionReport;
import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.ambulatorio.business.IAmbulatorioFacade;
import br.gov.mec.aghu.ambulatorio.vo.RelatorioGuiaAtendimentoUnimedVO;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.core.report.DocumentoJasper;
import br.gov.mec.aghu.core.utils.DateUtil;
import br.gov.mec.aghu.impressao.SistemaImpressaoException;
import br.gov.mec.aghu.model.AghParametros;

public class RelatorioGuiaAtendimentoUnimedController extends ActionReport {

	private static final long serialVersionUID = -2435247148645058835L;
	
	@Inject
	private SistemaImpressao sistemaImpressao;
	
	@EJB
	private IAmbulatorioFacade ambulatorioFacade;
	
	@EJB
	private IParametroFacade parametroFacade;
	
	private List<RelatorioGuiaAtendimentoUnimedVO> colecao;
	
	private Integer conNumero;
	
	
	@PostConstruct
	protected void inicializar(){
		this.begin(conversation);
	}

	public void directPrint() throws ApplicationBusinessException {
		try {
			DocumentoJasper documento = gerarDocumento();
			this.sistemaImpressao.imprimir(documento.getJasperPrint(), super.getEnderecoIPv4HostRemoto());
			apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_IMPRESSAO");

		} catch (SistemaImpressaoException e) {
			apresentarExcecaoNegocio(e);
		} catch (Exception e) {
			apresentarMsgNegocio(Severity.ERROR, "ERRO_GERAR_RELATORIO_COM_PARAMETRO", e.getMessage());
		}
	}

	@Override
	protected List<RelatorioGuiaAtendimentoUnimedVO> recuperarColecao() throws ApplicationBusinessException {
		this.colecao =  this.ambulatorioFacade.imprimirGuiaAtendimentoUnimed(conNumero);
		return colecao;
	}

	@Override
	protected String recuperarArquivoRelatorio() {
		return "br/gov/mec/aghu/ambulatorio/report/relatorioGuiaAtendimentoUnimed.jasper";
	}
	
	public Map<String, Object> recuperarParametros() {
		AghParametros logo = new AghParametros();
		Map<String, Object> params = new HashMap<String, Object>();
		Date dataAtual = new Date();

		try {
			logo = this.parametroFacade.obterAghParametro(AghuParametrosEnum.P_AGHU_LOGO_UNIMED);
		} catch (ApplicationBusinessException e1) {
			apresentarExcecaoNegocio(e1);
		}
		
		params.put("caminhoLogo", logo.getVlrTexto());
		params.put("dataHora", DateUtil.obterDataFormatada(dataAtual, "dd/MM/yyyy HH:mm"));
		params.put("dataAtual", DateUtil.obterDataFormatada(dataAtual, "dd/MM/yyyy"));
		params.put("SUBREPORT_DIR", "br/gov/mec/aghu/ambulatorio/report/");
			
		return params;
	}

	public Integer getConNumero() {
		return conNumero;
	}

	public void setConNumero(Integer conNumero) {
		this.conNumero = conNumero;
	}
}
