package br.gov.mec.aghu.prescricaomedica.action;


import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;

import net.sf.jasperreports.engine.JRException;

import org.primefaces.model.StreamedContent;

import br.gov.mec.aghu.action.report.ActionReport;
import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.impressao.SistemaImpressaoException;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.prescricaomedica.vo.ListaPacientePrescricaoVO;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;



public class RelatorioListaPacientesController extends ActionReport {

	private static final long serialVersionUID = -2435247148645058835L;
		
	@EJB
	private IParametroFacade parametroFacade;
		
	private String descricaoDocumento;
		
	private StreamedContent media;
	
	private List<ListaPacientePrescricaoVO> listaPacientes;
	
	private ListaPacientePrescricaoVO pacientes;
	
	
	@Override
	protected String recuperarArquivoRelatorio() {
		return "br/gov/mec/aghu/prescricaomedica/report/relatorioListaPacientes.jasper";
	}
	
	public void imprimir() throws UnknownHostException, JRException{
		if (!this.listaPacientes.isEmpty()) {
			try {
				directPrint();
				this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_IMPRESSAO_RELATORIO", "Lista de Pacientes");
			} catch (SistemaImpressaoException | ApplicationBusinessException e) {
				apresentarExcecaoNegocio(e);
			}
		} else {
			apresentarMsgNegocio(Severity.WARN, "NENHUM_REGISTRO_ENCONTRADO");
		}
	}

	@Override
	protected List<ListaPacientePrescricaoVO> recuperarColecao() throws ApplicationBusinessException {
		return listaPacientes;
	}
	
	
	@Override
	public Map<String, Object> recuperarParametros() {
		Map<String, Object> params = new HashMap<String, Object>();
		try {
			AghParametros aghParametros = this.parametroFacade.buscarAghParametro(AghuParametrosEnum.P_HOSPITAL_RAZAO_SOCIAL);
			params.put("razaoSocial", aghParametros.getVlrTexto());
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
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

	public List<ListaPacientePrescricaoVO> getListaPacientes() {
		return listaPacientes;
	}

	public void setListaPacientes(List<ListaPacientePrescricaoVO> listaPacientes) {
		this.listaPacientes = listaPacientes;
	}

	public ListaPacientePrescricaoVO getPacientes() {
		return pacientes;
	}

	public void setPacientes(ListaPacientePrescricaoVO pacientes) {
		this.pacientes = pacientes;
	}

		
}
