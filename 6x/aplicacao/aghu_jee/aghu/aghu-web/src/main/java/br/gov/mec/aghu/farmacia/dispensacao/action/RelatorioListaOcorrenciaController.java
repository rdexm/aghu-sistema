package br.gov.mec.aghu.farmacia.dispensacao.action;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.transaction.SystemException;

import net.sf.jasperreports.engine.JRException;

import org.primefaces.model.StreamedContent;

import br.gov.mec.aghu.action.report.ActionReport;
import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.constante.ConstanteAghCaractUnidFuncionais;
import br.gov.mec.aghu.farmacia.dispensacao.business.IFarmaciaDispensacaoFacade;
import br.gov.mec.aghu.farmacia.vo.ListaOcorrenciaVO;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
//import br.gov.mec.aghu.constante.ConstanteAghCaractUnidFuncionais;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.report.DocumentoJasper;

import com.itextpdf.text.DocumentException;

public class RelatorioListaOcorrenciaController extends ActionReport {

	private StreamedContent media;

	public StreamedContent getMedia() {
		return media;
	}

	public void setMedia(StreamedContent media) {
		this.media = media;
	}	

	private static final long serialVersionUID = 1163478101333936914L;

	@EJB
	private IFarmaciaDispensacaoFacade farmaciaDispensacaoFacade;
	
	@EJB
	private IAghuFacade aghuFacade;
	
	@EJB
	private IParametroFacade parametroFacade;
	
	private String unidade;
	private String dtReferencia;
	private String ocorrencia;
	
	private String unidFarmacia;
	private Boolean unidPsiquiatrica;
	
	@PostConstruct
	public void init(){
		begin(conversation);
	}

	private List<ListaOcorrenciaVO> colecao = new ArrayList<ListaOcorrenciaVO>(0);	

	/**
	 * Apresenta PDF na tela do navegador.
	 */	
	public void print()throws JRException, IOException, DocumentException {
		try {			
			Short seqUnidadeFuncional = Short.valueOf(unidade); 
			unidPsiquiatrica = this.aghuFacade.verificarCaracteristicaUnidadeFuncional(seqUnidadeFuncional, ConstanteAghCaractUnidFuncionais.UNID_PSIQUIATRICA);
			
			colecao = this.farmaciaDispensacaoFacade.recuperarRelatorioListaOcorrencia(unidade,dtReferencia,ocorrencia,unidFarmacia,unidPsiquiatrica);
		}catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}	
	}

	@Override
	public Collection<ListaOcorrenciaVO> recuperarColecao() throws ApplicationBusinessException {	
		return colecao;
	}

	@Override
	public String recuperarArquivoRelatorio() {	
		if(!unidPsiquiatrica){
			return "br/gov/mec/aghu/farmacia/report/listaOcorrencia.jasper";
		}else{
			return "br/gov/mec/aghu/farmacia/report/listaOcorrenciaPacienteMedicamento.jasper";
		}
	}

	@Override
	public Map<String, Object> recuperarParametros() {

		Map<String, Object> params = new HashMap<String, Object>();		

		params.put("SUBREPORT_DIR","br/gov/mec/aghu/farmacia/report/");  

		try {			
			AghParametros parametroRazaoSocial = this.parametroFacade.buscarAghParametro(AghuParametrosEnum.P_HOSPITAL_RAZAO_SOCIAL);
			params.put("hospitalLocal", parametroRazaoSocial.getVlrTexto());
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
		
		if(unidFarmacia != null  && !unidFarmacia.isEmpty()){
			Short seqUnidadeFarmacia = Short.valueOf(unidFarmacia); 
			AghUnidadesFuncionais unidFarm = aghuFacade.obterUnidadeFuncional(seqUnidadeFarmacia);	
			if (unidFarm != null){
				String farmacia = "FARMÁCIA: " + unidFarm.getUnidadeDescricao();
				params.put("farmacia", farmacia);
			}
		}
						
		//params.put("unidPsiquiatrica", unidPsiquiatrica);

		return params;
	}
	
	public String voltar() {
		return "tratarOcorrenciasList";
	}

	/**
	 * Método invocado pelo a:mediaOutput para geração de PDF dentro de XHTML.
	 * @throws DocumentException 
	 */
	public StreamedContent getRenderPdf()throws IOException,
	ApplicationBusinessException, JRException, SystemException, DocumentException {
		DocumentoJasper documento = gerarDocumento();
		return this.criarStreamedContentPdf(documento.getPdfByteArray(false));
	}
	// Getters e Setters

	public String getUnidade() {
		return unidade;
	}

	public void setUnidade(String unidade) {
		this.unidade = unidade;
	}

	public String getDtReferencia() {
		return dtReferencia;
	}

	public void setDtReferencia(String dtReferencia) {
		this.dtReferencia = dtReferencia;
	}

	public String getOcorrencia() {
		return ocorrencia;
	}

	public void setOcorrencia(String ocorrencia) {
		this.ocorrencia = ocorrencia;
	}

	public List<ListaOcorrenciaVO> getColecao() {
		return colecao;
	}

	public void setColecao(List<ListaOcorrenciaVO> colecao) {
		this.colecao = colecao;
	}

	public String getUnidFarmacia() {
		return unidFarmacia;
	}

	public void setUnidFarmacia(String unidFarmacia) {
		this.unidFarmacia = unidFarmacia;
	}

	public Boolean getUnidPsiquiatrica() {
		return unidPsiquiatrica;
	}

	public void setUnidPsiquiatrica(Boolean unidPsiquiatrica) {
		this.unidPsiquiatrica = unidPsiquiatrica;
	}
}
