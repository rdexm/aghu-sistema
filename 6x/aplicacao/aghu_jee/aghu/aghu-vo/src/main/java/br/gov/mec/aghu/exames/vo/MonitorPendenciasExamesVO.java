package br.gov.mec.aghu.exames.vo;

import java.util.Date;

import br.gov.mec.aghu.dominio.DominoOrigemMapaAmostraItemExame;

public class MonitorPendenciasExamesVO implements Comparable<MonitorPendenciasExamesVO> {

	private Date dthrEvento;
	private Date dataHora;
	private Integer nroUnico;
	private Integer soeSeq;
	private Short seqp;
	private Short amoSeqp;
	private Integer prontuario; // Prontuário no formato numérico
	private String prontuarioFormatado; // Prontuário formatado com o delimitador /
	private String paciente;
	private Integer pacCodigo;
	private String local;
	private String exame;
	private String material;
	private String exameMaterial;
	private String tempo;
	private Short iseUfeUnfSeq;
	private Short amoUnfSeq;
	private Date dtNumeroUnico;
	private Boolean enviado;
	private DominoOrigemMapaAmostraItemExame origemMapa;
	private String ufeEmaExaSigla;
	private Integer ufeEmaManSeq;
	private String dataHoraFormatada;

	@Override
	public int compareTo(MonitorPendenciasExamesVO o) {
		
		
		int result = 0;
		
		if (this.getOrigemMapa() == null && o.getOrigemMapa() != null){
			result = -1;
	    }
	    else if (this.getOrigemMapa() != null && o.getOrigemMapa() == null){
	    	result = 1;
	    }
	    else if (this.getOrigemMapa() != null && o.getOrigemMapa() != null) {
	        result = o.getOrigemMapa().toString().compareToIgnoreCase(this.getOrigemMapa().toString());
	        if (result != 0){
	            return result;
	        }
	    }
		
		if(result == 0){
			if(this.dthrEvento!=null && o!=null && o.dthrEvento!=null){
				result = this.dthrEvento.compareTo(o.dthrEvento);
			}else{
				result = -1;
			}
		}

	    return  result;

	}
	
	/**
	 * Obtém a descrição do atributo enviado
	 * 
	 * @return
	 */
	public String obterEnviadoDescricao() {
		if (this.enviado != null) {
			return this.enviado ? "Sim" : "Não";
		}
		return null;
	}

	public Date getDthrEvento() {
		return dthrEvento;
	}

	public void setDthrEvento(Date dthrEvento) {
		this.dthrEvento = dthrEvento;
	}

	public Date getDataHora() {
		return dataHora;
	}

	public void setDataHora(Date dataHora) {
		this.dataHora = dataHora;
	}

	public Integer getNroUnico() {
		return nroUnico;
	}

	public void setNroUnico(Integer nroUnico) {
		this.nroUnico = nroUnico;
	}

	public Integer getSoeSeq() {
		return soeSeq;
	}

	public void setSoeSeq(Integer soeSeq) {
		this.soeSeq = soeSeq;
	}

	public Short getSeqp() {
		return seqp;
	}

	public void setSeqp(Short seqp) {
		this.seqp = seqp;
	}

	public Short getAmoSeqp() {
		return amoSeqp;
	}

	public void setAmoSeqp(Short amoSeqp) {
		this.amoSeqp = amoSeqp;
	}

	public Integer getProntuario() {
		return prontuario;
	}

	public void setProntuario(Integer prontuario) {
		this.prontuario = prontuario;
	}
	
	public String getProntuarioFormatado() {
		return prontuarioFormatado;
	}
	
	public void setProntuarioFormatado(String prontuarioFormatado) {
		this.prontuarioFormatado = prontuarioFormatado;
	}

	public String getPaciente() {
		return paciente;
	}

	public void setPaciente(String paciente) {
		this.paciente = paciente;
	}

	public Integer getPacCodigo() {
		return pacCodigo;
	}

	public void setPacCodigo(Integer pacCodigo) {
		this.pacCodigo = pacCodigo;
	}

	public String getLocal() {
		return local;
	}

	public void setLocal(String local) {
		this.local = local;
	}

	public String getExame() {
		return exame;
	}

	public void setExame(String exame) {
		this.exame = exame;
	}

	public String getMaterial() {
		return material;
	}

	public void setMaterial(String material) {
		this.material = material;
	}

	public String getExameMaterial() {
		return exameMaterial;
	}

	public void setExameMaterial(String exameMaterial) {
		this.exameMaterial = exameMaterial;
	}

	public String getTempo() {
		return tempo;
	}

	public void setTempo(String tempo) {
		this.tempo = tempo;
	}

	public Short getIseUfeUnfSeq() {
		return iseUfeUnfSeq;
	}

	public void setIseUfeUnfSeq(Short iseUfeUnfSeq) {
		this.iseUfeUnfSeq = iseUfeUnfSeq;
	}

	public Short getAmoUnfSeq() {
		return amoUnfSeq;
	}

	public void setAmoUnfSeq(Short amoUnfSeq) {
		this.amoUnfSeq = amoUnfSeq;
	}

	public Date getDtNumeroUnico() {
		return dtNumeroUnico;
	}

	public void setDtNumeroUnico(Date dtNumeroUnico) {
		this.dtNumeroUnico = dtNumeroUnico;
	}

	public Boolean getEnviado() {
		return enviado;
	}

	public void setEnviado(Boolean enviado) {
		this.enviado = enviado;
	}

	public DominoOrigemMapaAmostraItemExame getOrigemMapa() {
		return origemMapa;
	}

	public void setOrigemMapa(DominoOrigemMapaAmostraItemExame origemMapa) {
		this.origemMapa = origemMapa;
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

	public String getDataHoraFormatada() {
		return dataHoraFormatada;
	}

	public void setDataHoraFormatada(String dataHoraFormatada) {
		this.dataHoraFormatada = dataHoraFormatada;
	}

	
	
	

}
