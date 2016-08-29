package br.gov.mec.aghu.prescricaoenfermagem.action;

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
import br.gov.mec.aghu.business.exception.AghuCSVBaseException.AghuCSVBaseExceptionExceptionCode;
import br.gov.mec.aghu.business.prescricaoenfermagem.IPrescricaoEnfermagemFacade;
import br.gov.mec.aghu.dominio.DominioNomeRelatorio;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.EpeCuidados;
import br.gov.mec.aghu.core.dominio.DominioMimeType;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.core.report.DocumentoJasper;

public class RelatorioRotinaCuidadoController extends ActionReport {

	
	private static final long serialVersionUID = -8063424345080116858L;
	
	private static final String PAGE_PESQUISA_CUIDADOS = "prescricaoenfermagem-pesquisaCuidados";
   // private static final String PAGE_ROTINA_CUIDADOS_PDF = "prescricaoenfermagem-relatorioRotinaCuidadoPdf";
	
	@EJB
	private IPrescricaoEnfermagemFacade prescricaoEnfermagemFacade;
	
	@EJB
	private IParametroFacade parametroFacade;
	
	@Inject
	private SistemaImpressao sistemaImpressao;
	
	private EpeCuidados epeCuidados;
	
	private Short seq;
	private List<EpeCuidados> colecao;

	@PostConstruct
	protected void inicializar(){
	 this.begin(conversation);
	}	

    public void inicio() {
        colecao = prescricaoEnfermagemFacade.pesquisarEpeCuidadosPorCodigo(seq);
    }
	@Override
	public String recuperarArquivoRelatorio() {
		return "br/gov/mec/aghu/prescricaoenfermagem/report/relatorioRotinaCuidado.jasper";
	}
	
	/**
	 * Retorna os parametros utilizados no relatorio
	 */
	@Override
	public Map<String, Object> recuperarParametros() {	
		Map<String, Object> params = new HashMap<String, Object>();
		
		params.put("dataAtual", new Date());
		
		AghParametros parametroHospital = null;
		
		try {
			parametroHospital = this.parametroFacade.buscarAghParametro(AghuParametrosEnum.P_HOSPITAL_RAZAO_SOCIAL);
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}

		params.put("parametroHospital", parametroHospital.getVlrTexto());
		
		return params;
	}
	
	/**
	 * Recupera a coleção utilizada no relatório
	 */
	@Override
	public List<EpeCuidados> recuperarColecao() throws ApplicationBusinessException {

//		colecao = prescricaoEnfermagemFacade.pesquisarEpeCuidadosPorCodigo(seq);
		
		return colecao; 
	}
	
	public String voltar(){
		return PAGE_PESQUISA_CUIDADOS;
	}
	
	public void executarImpressaoEDownload() {
		String fileName = recuperarArquivoRelatorio();
		if (fileName != null) {

			try {
                DocumentoJasper documento = gerarDocumento();

                this.sistemaImpressao.imprimir(documento.getJasperPrint(),super.getEnderecoIPv4HostRemoto());

				this.download(
						documento.getPdfByteArray(false),
						DominioNomeRelatorio.REL_ROTINA_CUIDADOS.toString() , DominioMimeType.PDF.getContentType());

                this.apresentarMsgNegocio(Severity.INFO,"MENSAGEM_SUCESSO_IMPRESSAO");

			} catch (Exception e) {
				apresentarExcecaoNegocio(new ApplicationBusinessException(
						AghuCSVBaseExceptionExceptionCode.ERRO_GERACAO_ARQUIVO_PDF, e, e.getLocalizedMessage()));
			}
		}

    }


    public boolean isRelatorioPreenchido() {

        return colecao != null;
    }

    public void mostrarMensagem() {
        this.apresentarMsgNegocio(Severity.INFO,"MENSAGEM_SUCESSO_IMPRESSAO");
    }
	
	public List<EpeCuidados> getColecao() {
		return colecao;
	}

	public void setColecao(List<EpeCuidados> colecao) {
		this.colecao = colecao;
	}	

	public EpeCuidados getEpeCuidados() {
		return epeCuidados;
	}

	public Short getSeq() {
		return seq;
	}

	public void setEpeCuidados(EpeCuidados epeCuidados) {
		this.epeCuidados = epeCuidados;
	}

	public void setSeq(Short seq) {
		this.seq = seq;
	}

}
