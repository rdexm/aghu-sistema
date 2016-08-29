package br.gov.mec.aghu.exames.pesquisa.vo;


public class FluxogramaLaborarorialDadosDataValorVO implements java.io.Serializable{

	private static final long serialVersionUID = -249666715837920877L;

	public FluxogramaLaborarorialDadosDataValorVO(String dataValor, boolean possuiNotaAdicional, Integer soeSeq, Short iseSeqp) {
		super();
		this.dataValor = dataValor;
		this.possuiNotaAdicional = possuiNotaAdicional;
		this.soeSeq = soeSeq;
		this.iseSeqp = iseSeqp;
	}
	
	private String dataValor;
	private boolean possuiNotaAdicional;
	private Integer soeSeq;
	private Short iseSeqp;

	public String getDataValor() {
		return dataValor;
	}
	public void setDataValor(String dataValor) {
		this.dataValor = dataValor;
	}
	public boolean isPossuiNotaAdicional() {
		return possuiNotaAdicional;
	}
	public void setPossuiNotaAdicional(boolean possuiNotaAdicional) {
		this.possuiNotaAdicional = possuiNotaAdicional;
	}
	public Integer getSoeSeq() {
		return soeSeq;
	}
	public void setSoeSeq(Integer soeSeq) {
		this.soeSeq = soeSeq;
	}
	public Short getIseSeqp() {
		return iseSeqp;
	}
	public void setIseSeqp(Short iseSeqp) {
		this.iseSeqp = iseSeqp;
	}
}