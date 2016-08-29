package br.gov.mec.aghu.ambulatorio.action;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.context.FacesContext;
import javax.inject.Inject;

import org.primefaces.model.StreamedContent;

import br.gov.mec.aghu.action.impressao.SistemaImpressao;
import br.gov.mec.aghu.action.report.ActionReport;
import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.ambulatorio.vo.RelatorioConsultoriaAmbulatorialVO;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.core.report.DocumentoJasper;

public class RelatorioConsultoriaAmbulatorialController extends ActionReport {

	private static final long serialVersionUID = -2435247148645058835L;
	
	private final String ESPACO_TRACO_ESPACO = " - ";

	@EJB
	private IParametroFacade parametroFacade;
	
	private List<RelatorioConsultoriaAmbulatorialVO> consultoriaAmbulatorial;
	
	private String descricaoDocumento;
	
	private StreamedContent media;
	
	@Inject
	private SistemaImpressao sistemaImpressao;
	
	private String enderecoHospital="";
	private String cepPadrao="";
	private String ufPadrao="";
	private String endCidade="";
	private String endFone=null;
	
	@PostConstruct
	protected void inicializar(){
		this.begin(conversation);
	}
	
	@Override
	public void directPrint() {
				
		try {			
			DocumentoJasper documento = gerarDocumento();
			this.sistemaImpressao.imprimir(documento.getJasperPrint(), super.getEnderecoIPv4HostRemoto());
			this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_IMPRESSAO_RELATORIO", descricaoDocumento);
		} catch (Exception e) {
			this.apresentarMsgNegocio(Severity.ERROR, "ERRO_GERAR_RELATORIO");
		}	
	}

	public Map<String, Object> recuperarParametros() {

		Map<String, Object> params = new HashMap<String, Object>();
		try {			
			AghParametros aghParametros = this.parametroFacade.buscarAghParametro(AghuParametrosEnum.P_HOSPITAL_END_COMPLETO_LINHA1);
			enderecoHospital = aghParametros.getVlrTexto();
			aghParametros = this.parametroFacade.buscarAghParametro(AghuParametrosEnum.P_CEP_PADRAO);
			cepPadrao = aghParametros.getVlrTexto();
			aghParametros = this.parametroFacade.buscarAghParametro(AghuParametrosEnum.P_UF_PADRAO);
			ufPadrao = aghParametros.getVlrTexto();
			aghParametros = this.parametroFacade.buscarAghParametro(AghuParametrosEnum.P_HOSPITAL_END_CIDADE);
			endCidade = aghParametros.getVlrTexto();
			aghParametros = this.parametroFacade.buscarAghParametro(AghuParametrosEnum.P_HOSPITAL_END_FONE);
			endFone = aghParametros.getVlrTexto();
			this.formataComplementoEndereco();
			params.put("enderecoHospital", enderecoHospital);
			params.put("caminhoLogo", FacesContext.getCurrentInstance().getExternalContext().getRealPath("/images/logoClinicas.jpg"));
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
		return params;
	}
	
	@Override
	protected List<RelatorioConsultoriaAmbulatorialVO> recuperarColecao() throws ApplicationBusinessException {
		return  getConsultoriaAmbulatorial();
	}

	@Override
	protected String recuperarArquivoRelatorio() {
		return "br/gov/mec/aghu/ambulatorio/report/relatorioSolicitacaoConsultoriaAmbulatorial.jasper";
	}	
	private void formataComplementoEndereco() {
		if(enderecoHospital!= null){
			if (endFone != null) {
				enderecoHospital =	enderecoHospital.concat(" Fone: ".concat(endFone).concat(ESPACO_TRACO_ESPACO));			
			}
			if (cepPadrao != null) {
				enderecoHospital = enderecoHospital.concat("CEP ").concat(cepPadrao).concat(ESPACO_TRACO_ESPACO);
			}
			if (endCidade != null) {
				enderecoHospital = enderecoHospital.concat(endCidade).concat(", ");
			}
			if (ufPadrao!= null) {
				enderecoHospital = enderecoHospital.concat(ufPadrao);
			}
		}
	}

	public List<RelatorioConsultoriaAmbulatorialVO> getConsultoriaAmbulatorial() {
		return consultoriaAmbulatorial;
	}

	public void setConsultoriaAmbulatorial(List<RelatorioConsultoriaAmbulatorialVO> consultoriaAmbulatorial) {
		this.consultoriaAmbulatorial = consultoriaAmbulatorial;
	}	

	public String getDescricaoDocumento() {
		return descricaoDocumento;
	}

	public void setDescricaoDocumento(String descricaoDocumento) {
		this.descricaoDocumento = descricaoDocumento;
	}

	public StreamedContent getMedia() {
		return media;
	}

	public void setMedia(StreamedContent media) {
		this.media = media;
	}
	
}
