package br.gov.mec.aghu.exames.vo;


public class PendenciaExecucaoVO {
	
	private Integer grupoSeq;
	private String grupo;
	private String dthrEvento;
	private Integer soeSeq;
	private Integer numUnico;
	private String material;
	private String exame;
	private Short ufeUnfSeq;
	private String ufeEmaExaSigla;
	private Integer ufeEmaManSeq;
	private Integer prontuario;
	
	public PendenciaExecucaoVO() {
		super();
	}
	
	public PendenciaExecucaoVO(Integer grupoSeq, String grupo, String dthrEvento,
			Integer soeSeq, Integer numUnico, String material, String exame,
			Short ufe_unf_seq, String ufe_ema_exa_sigla,
			Integer ufe_ema_man_seq, Integer prontuario) {
		super();
		this.grupoSeq = grupoSeq;
		this.grupo = grupo;
		this.dthrEvento = dthrEvento;
		this.soeSeq = soeSeq;
		this.numUnico = numUnico;
		this.material = material;
		this.exame = exame;
		this.ufeUnfSeq = ufe_unf_seq;
		this.ufeEmaExaSigla = ufe_ema_exa_sigla;
		this.ufeEmaManSeq = ufe_ema_man_seq;
		this.prontuario = prontuario;
	}

	public Integer getGrupoSeq() {
		return grupoSeq;
	}

	public void setGrupoSeq(Integer grupoSeq) {
		this.grupoSeq = grupoSeq;
	}

	public String getGrupo() {
		return grupo;
	}

	public void setGrupo(String grupo) {
		this.grupo = grupo;
	}

	public String getDthrEvento() {
		return dthrEvento;
	}

	public void setDthrEvento(String dthrEvento) {
		this.dthrEvento = dthrEvento;
	}

	public Integer getSoeSeq() {
		return soeSeq;
	}

	public void setSoeSeq(Integer soeSeq) {
		this.soeSeq = soeSeq;
	}

	public Integer getNumUnico() {
		return numUnico;
	}

	public void setNumUnico(Integer numUnico) {
		this.numUnico = numUnico;
	}

	public String getMaterial() {
		return material;
	}

	public void setMaterial(String material) {
		this.material = material;
	}

	public String getExame() {
		return exame;
	}

	public void setExame(String exame) {
		this.exame = exame;
	}

	public Integer getProntuario() {
		return prontuario;
	}

	public void setProntuario(Integer prontuario) {
		this.prontuario = prontuario;
	}

	public Short getUfeUnfSeq() {
		return ufeUnfSeq;
	}

	public void setUfeUnfSeq(Short ufeUnfSeq) {
		this.ufeUnfSeq = ufeUnfSeq;
	}

	public String getUfeEmaExaSigla() {
		return ufeEmaExaSigla;
	}

	public void setUfeEmaExaSigla(String ufeEmaExaSigla) {
		this.ufeEmaExaSigla = ufeEmaExaSigla;
	}

	public Integer getUfeEmaManSeq() {
		return ufeEmaManSeq;
	}

	public void setUfeEmaManSeq(Integer ufeEmaManSeq) {
		this.ufeEmaManSeq = ufeEmaManSeq;
	}

}
