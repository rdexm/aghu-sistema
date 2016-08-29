package br.gov.mec.aghu.ambulatorio.action;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.faces.context.FacesContext;

import net.sf.jasperreports.engine.JRException;



import org.primefaces.model.StreamedContent;

import br.gov.mec.aghu.action.report.ActionReport;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.ambulatorio.vo.MamReceituarioCuidadoVO;
import br.gov.mec.aghu.impressao.SistemaImpressaoException;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.parametrosistema.business.IParametroSistemaFacade;
import br.gov.mec.aghu.parametrosistema.vo.AghParametroVO;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;



public class RelatorioReceitaCuidadosController extends ActionReport {

	private static final long serialVersionUID = -2435247148645058835L;
	
	private final String ESPACO_TRACO_ESPACO = " - ";

	@EJB
	private IParametroSistemaFacade parametroSistemaFacade;
	
	private MamReceituarioCuidadoVO receituarioCuidado;
	
	private String descricaoDocumento;
	
	private List<MamReceituarioCuidadoVO> colecao;
	
	private List<MamReceituarioCuidadoVO> rCuidado;
	
	private StreamedContent media;
	
	@Override
	protected String recuperarArquivoRelatorio() {
		return "br/gov/mec/aghu/ambulatorio/report/relatorioReceituarioCuidados.jasper";
	}

	public void imprimir() throws SistemaImpressaoException, ApplicationBusinessException, UnknownHostException, JRException {
				
		if (!this.rCuidado.isEmpty()) {
			directPrint();
			this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_IMPRESSAO_RELATORIO", descricaoDocumento);
		} else {
			apresentarMsgNegocio(Severity.WARN, "NENHUM_REGISTRO_ENCONTRADO");
		}
		
	}

	@Override
	protected List<MamReceituarioCuidadoVO> recuperarColecao() throws ApplicationBusinessException {
		
		colecao = new ArrayList<MamReceituarioCuidadoVO>();
				
		AghParametros parametro = new AghParametros();
		
		if(!this.rCuidado.isEmpty()){
			for(MamReceituarioCuidadoVO receituarioCuidado : rCuidado){
				parametro.setNome(AghuParametrosEnum.P_HOSPITAL_END_COMPLETO_LINHA1.toString());
				List<AghParametroVO> listaParametros = parametroSistemaFacade.pesquisaParametroList(0, 1, null, false, parametro);
				if (listaParametros != null && !listaParametros.isEmpty()) {
					receituarioCuidado.setEnderecoHospital(listaParametros.get(0).getVlrTexto());
				}
				
				parametro.setNome(AghuParametrosEnum.P_CEP_PADRAO.toString());
				listaParametros = parametroSistemaFacade.pesquisaParametroList(0, 1, null, false, parametro);
				if (listaParametros != null && !listaParametros.isEmpty()) {
					receituarioCuidado.setCepHospital(listaParametros.get(0).getVlrNumerico());
				}
				
				parametro.setNome(AghuParametrosEnum.P_HOSPITAL_END_CIDADE.toString());
				listaParametros = parametroSistemaFacade.pesquisaParametroList(0, 1, null, false, parametro);
				if (listaParametros != null && !listaParametros.isEmpty()) {
					receituarioCuidado.setCidadeHospital(listaParametros.get(0).getVlrTexto());
				}
				
				parametro.setNome(AghuParametrosEnum.P_UF_PADRAO.toString());
				listaParametros = parametroSistemaFacade.pesquisaParametroList(0, 1, null, false, parametro);
				if (listaParametros != null && !listaParametros.isEmpty()) {
					receituarioCuidado.setUfHospital(listaParametros.get(0).getVlrTexto());
				}
				
				parametro.setNome(AghuParametrosEnum.P_HOSPITAL_END_FONE.toString());
				listaParametros = parametroSistemaFacade.pesquisaParametroList(0, 1, null, false, parametro);
				if (listaParametros != null && !listaParametros.isEmpty()) {
					receituarioCuidado.setTelefoneHospital(listaParametros.get(0).getVlrTexto());
				}
				this.formatarCid(receituarioCuidado);
				this.formataComplementoEndereco(receituarioCuidado);
			}		
	
		colecao.addAll(rCuidado);
	}	
		return colecao;
}


	
	private void formatarCid(MamReceituarioCuidadoVO receituarioCuidado) {
		if (receituarioCuidado.getCodigoCid() != null && receituarioCuidado.getDescricaoCid() != null) {
			receituarioCuidado.setCidFormatado(receituarioCuidado.getCodigoCid() + ESPACO_TRACO_ESPACO + receituarioCuidado.getDescricaoCid());
		} else {
			receituarioCuidado.setCidFormatado("");
		}
	}
		
	private void formataComplementoEndereco(MamReceituarioCuidadoVO receituarioCuidado) {
		String complementoEndereco = "";
		if (receituarioCuidado.getTelefoneHospital() != null) {
			complementoEndereco = "Fone: ".concat(receituarioCuidado.getTelefoneHospital()).concat(ESPACO_TRACO_ESPACO);			
		}
		if (receituarioCuidado.getCepHospital() != null) {
			complementoEndereco = complementoEndereco.concat("CEP ").concat(receituarioCuidado.getCepHospital().toString()).concat(ESPACO_TRACO_ESPACO);
		}
		if (receituarioCuidado.getCidadeHospital() != null) {
			complementoEndereco = complementoEndereco.concat(receituarioCuidado.getCidadeHospital()).concat(", ");
		}
		if (receituarioCuidado.getUfHospital() != null) {
			complementoEndereco = complementoEndereco.concat(receituarioCuidado.getUfHospital());
		}
		
		receituarioCuidado.setComplementoEnderecoFormatado(complementoEndereco);
	}
	
	public Map<String, Object> recuperarParametros() {
		
		Map<String, Object> params = new HashMap<String, Object>();

		params.put("caminhoLogo", FacesContext.getCurrentInstance().getExternalContext().getRealPath("/images/logoClinicas.jpg"));
	
		return params;
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

	public MamReceituarioCuidadoVO getReceituarioCuidado() {
		return receituarioCuidado;
	}

	public void setReceituarioCuidado(MamReceituarioCuidadoVO receituarioCuidado) {
		this.receituarioCuidado = receituarioCuidado;
	}

	public List<MamReceituarioCuidadoVO> getColecao() {
		return colecao;
	}

	public void setColecao(List<MamReceituarioCuidadoVO> colecao) {
		this.colecao = colecao;
	}

	public List<MamReceituarioCuidadoVO> getrCuidado() {
		return rCuidado;
	}

	public void setrCuidado(List<MamReceituarioCuidadoVO> rCuidado) {
		this.rCuidado = rCuidado;
	}
	
	
		
}
