package br.gov.mec.aghu.exames.patologia.action;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.ApplicationBusinessExceptionCode;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.dominio.DominioNomeRelatorio;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.exames.business.IExamesFacade;
import br.gov.mec.aghu.exames.patologia.business.IExamesPatologiaFacade;
import br.gov.mec.aghu.model.AelCestoPatologia;

public class RelatorioMapaLaminaController extends ActionController {


	private static final long serialVersionUID = -8236839367411897064L;

	private static final String RELATORIO_MAPA_LAMINA_PDF = "relatorioMapaLaminaPdf";

	@EJB
	private IExamesFacade examesFacade;
	
	@EJB
	private IExamesPatologiaFacade examesPatologiaFacade;
	
	@Inject
	private RelatorioMapaLaminaPdfController relatorioMapaLaminaPdfController;
	
	private Date dtReferencia; 
	private AelCestoPatologia cesto;
	
	private Boolean gerouArquivo;
	private String fileName;

	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}
	
	public void iniciar(){
	 

		dtReferencia = new Date();
		cesto = null;
	
	}
	
	public String imprimirRelatorio(){
		return visualizarRelatorio();
	}
	
	public String visualizarRelatorio(){
		relatorioMapaLaminaPdfController.setDtReferencia(dtReferencia);
		return RELATORIO_MAPA_LAMINA_PDF;
	}
	
	public void limpar(){
		gerouArquivo = false;
		fileName = null;
		dtReferencia = new Date();
		cesto = null;
	}
		
	public void gerarCSV() {
		try{
			fileName = examesFacade.geraCSVRelatorioMapaLaminasVO(dtReferencia, cesto);
			gerouArquivo = true;
			
		} catch (IOException e) {
			apresentarExcecaoNegocio(new BaseException(ApplicationBusinessExceptionCode.OCORREU_O_SEGUINTE_ERRO, e, e.getLocalizedMessage()));
			
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		} 
	}
	
	public void dispararDownload(){
		if(fileName != null){
			try {
				super.download(fileName,DominioNomeRelatorio.AELR_MAPA_LAMINA.toString());
				gerouArquivo = false;
				fileName = null;
				
			} catch (IOException e) {
				apresentarExcecaoNegocio(new BaseException(ApplicationBusinessExceptionCode.OCORREU_O_SEGUINTE_ERRO, e, e.getLocalizedMessage()));
			}
		}
	}
	
	public List<AelCestoPatologia> pesquisarAelCestoPatologia(final String filtro){
		return this.returnSGWithCount(examesPatologiaFacade.pesquisarAelCestoPatologia((String) filtro, DominioSituacao.A),pesquisarAelCestoPatologiaCount(filtro)); 
	}

	public Long pesquisarAelCestoPatologiaCount(final String filtro){
		return examesPatologiaFacade.pesquisarAelCestoPatologiaCount((String) filtro, DominioSituacao.A); 
	}
	
	public Boolean getGerouArquivo() {
		return gerouArquivo;
	}

	public String getFileName() {
		return fileName;
	}

	public void setGerouArquivo(Boolean gerouArquivo) {
		this.gerouArquivo = gerouArquivo;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public Date getDtReferencia() {
		return dtReferencia;
	}

	public void setDtReferencia(Date dtReferencia) {
		this.dtReferencia = dtReferencia;
	}

	public AelCestoPatologia getCesto() {
		return cesto;
	}

	public void setCesto(AelCestoPatologia cesto) {
		this.cesto = cesto;
	}
}
