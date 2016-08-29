package br.gov.mec.aghu.perinatologia.vo;



import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.core.commons.BaseBean;

public class AchadoVO implements BaseBean, Comparable<AchadoVO>{

	private static final long serialVersionUID = -2695221286649125934L;
	private Integer seq;
	private Integer seqRan;
	private String descricaoAcd;
	private String descricaoRan;
	private DominioSituacao situacao;
	private Boolean indExigeObs;
	private String mensagemAlerta;
	
	public Integer getSeq() {
		return seq;
	}
	public void setSeq(Integer seq) {
		this.seq = seq;
	}
	public String getDescricaoAcd() {
		return descricaoAcd;
	}
	public void setDescricaoAcd(String descricaoAcd) {
		this.descricaoAcd = descricaoAcd;
	}
	public String getDescricaoRan() {
		return descricaoRan;
	}
	public void setDescricaoRan(String descricaoRan) {
		this.descricaoRan = descricaoRan;
	}
	public DominioSituacao getSituacao() {
		return situacao;
	}
	public void setSituacao(DominioSituacao situacao) {
		this.situacao = situacao;
	}
	public Boolean getIndExigeObs() {
		return indExigeObs;
	}
	public void setIndExigeObs(Boolean indExigeObs) {
		this.indExigeObs = indExigeObs;
	}
	public String getMensagemAlerta() {
		return mensagemAlerta;
	}
	public void setMensagemAlerta(String mensagemAlerta) {
		this.mensagemAlerta = mensagemAlerta;
	}
	
	public String getIndExigeObsSimNao(){
		return DominioSimNao.getInstance(this.indExigeObs).getDescricao();
	}
	
	//TODO AJUSTAR
	public int compareTo(AchadoVO o) {
		return this.descricaoAcd.compareTo(o.descricaoAcd)
				+ this.descricaoRan.compareTo(o.descricaoRan);
	}
	public Integer getSeqRan() {
		return seqRan;
	}
	public void setSeqRan(Integer seqRan) {
		this.seqRan = seqRan;
	}

}
