package br.gov.mec.aghu.exames.vo;

import java.io.Serializable;



public class RelatorioCaracteristicasResultadosPorExameVO  implements Serializable {
	
	private static final long serialVersionUID = 3288699632968893984L;
	private String siglaExame;
	private String descricaoUsualExame;
	private Integer emaManSeq;
	private Integer manSeq;
	private String manDescricao;
	private Integer egcGcaSeq;
	private Integer codigoFalante;
	private Integer gcaSeq;
	private String gcaDdescricao;
	private Integer ordemImpressao;
	private Integer cacSeq;
	private String cacDescricao;
	
	public RelatorioCaracteristicasResultadosPorExameVO() {
		super();
	}

	public String getSiglaExame() {
		return siglaExame;
	}

	public void setSiglaExame(String siglaExame) {
		this.siglaExame = siglaExame;
	}

	public String getDescricaoUsualExame() {
		return descricaoUsualExame;
	}

	public void setDescricaoUsualExame(String descricaoUsualExame) {
		this.descricaoUsualExame = descricaoUsualExame;
	}

	public Integer getEmaManSeq() {
		return emaManSeq;
	}

	public void setEmaManSeq(Integer emaManSeq) {
		this.emaManSeq = emaManSeq;
	}

	public Integer getManSeq() {
		return manSeq;
	}

	public void setManSeq(Integer manSeq) {
		this.manSeq = manSeq;
	}

	public String getManDescricao() {
		return manDescricao;
	}

	public void setManDescricao(String manDescricao) {
		this.manDescricao = manDescricao;
	}

	public Integer getEgcGcaSeq() {
		return egcGcaSeq;
	}

	public void setEgcGcaSeq(Integer egcGcaSeq) {
		this.egcGcaSeq = egcGcaSeq;
	}

	public Integer getCodigoFalante() {
		return codigoFalante;
	}

	public void setCodigoFalante(Integer codigoFalante) {
		this.codigoFalante = codigoFalante;
	}

	public Integer getGcaSeq() {
		return gcaSeq;
	}

	public void setGcaSeq(Integer gcaSeq) {
		this.gcaSeq = gcaSeq;
	}

	public String getGcaDdescricao() {
		return gcaDdescricao;
	}

	public void setGcaDdescricao(String gcaDdescricao) {
		this.gcaDdescricao = gcaDdescricao;
	}

	public Integer getOrdemImpressao() {
		return ordemImpressao;
	}

	public void setOrdemImpressao(Integer ordemImpressao) {
		this.ordemImpressao = ordemImpressao;
	}

	public Integer getCacSeq() {
		return cacSeq;
	}

	public void setCacSeq(Integer cacSeq) {
		this.cacSeq = cacSeq;
	}

	public String getCacDescricao() {
		return cacDescricao;
	}

	public void setCacDescricao(String cacDescricao) {
		this.cacDescricao = cacDescricao;
	}
	
	

}
