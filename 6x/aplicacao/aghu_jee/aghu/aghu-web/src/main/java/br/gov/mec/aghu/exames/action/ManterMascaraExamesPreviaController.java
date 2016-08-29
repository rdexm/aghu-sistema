package br.gov.mec.aghu.exames.action;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.primefaces.component.outputpanel.OutputPanel;

import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.exames.vo.DesenhoMascaraExameVO;



@SuppressWarnings("PMD.HierarquiaControllerIncorreta")
public class ManterMascaraExamesPreviaController extends ActionController{
	private String voltarPara;

	@PostConstruct
	protected void inicializar(){
	 this.begin(conversation);
	}

	private static final long serialVersionUID = -5371974036129937448L;
	private OutputPanel panelPrevia;
	private Integer alturaDiv;

	@Inject
	private CalculaValorMascaraExamesComponente calculaValorMascaraExamesComponente;
	private List<DesenhoMascaraExameVO> desenhosMascarasExamesVO;

	public Object calcularValor(String idCampo) {
		try {
			
				calculaValorMascaraExamesComponente.setDesenhosMascarasExamesVO(this.getDesenhosMascarasExamesVO());

			return calculaValorMascaraExamesComponente.calcularValor(idCampo);
		} catch (Exception e) {
			this.apresentarMsgNegocio(Severity.ERROR, "MSG_EXCESSAO_EXPRESSAO");
		}
	return null;
	}
	
	public String voltar(){
		return voltarPara;
	}
	
	public OutputPanel getPanelPrevia() {
		return panelPrevia;
	}

	public void setPanelPrevia(OutputPanel panelPrevia) {
		this.panelPrevia = panelPrevia;
	}

	public String getVoltarPara() {
		return voltarPara;
	}

	public void setVoltarPara(String voltarPara) {
		this.voltarPara = voltarPara;
	}	
	
	public void setAlturaDiv(Integer alturaDiv) {
		this.alturaDiv = alturaDiv;
	}
	
	public Integer getAlturaDiv(){
		return this.alturaDiv;
	}
	
	public List<DesenhoMascaraExameVO> getDesenhosMascarasExamesVO() {
		return desenhosMascarasExamesVO;
	}

	public void setDesenhosMascarasExamesVO(
			List<DesenhoMascaraExameVO> desenhosMascarasExamesVO) {
		this.desenhosMascarasExamesVO = desenhosMascarasExamesVO;
	}
}