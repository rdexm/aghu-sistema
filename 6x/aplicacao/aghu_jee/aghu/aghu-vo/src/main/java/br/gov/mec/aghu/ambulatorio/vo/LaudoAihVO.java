package br.gov.mec.aghu.ambulatorio.vo;

import java.io.Serializable;

/**
 * VO que representa os dados do laudo aih
 * 
 * @author luismoura
 * 
 */
public class LaudoAihVO implements Serializable {
	private static final long serialVersionUID = -8020648877848207282L;

	private Short seqEspecialidade;
	private Integer matricula;
	private Short vinCodigo;
	private Integer seqIPH;
	private Short seqPHO;
	private Integer seqCID;

	public Short getSeqEspecialidade() {
		return seqEspecialidade;
	}

	public void setSeqEspecialidade(Short seqEspecialidade) {
		this.seqEspecialidade = seqEspecialidade;
	}

	public Integer getMatricula() {
		return matricula;
	}

	public void setMatricula(Integer matricula) {
		this.matricula = matricula;
	}

	public Short getVinCodigo() {
		return vinCodigo;
	}

	public void setVinCodigo(Short vinCodigo) {
		this.vinCodigo = vinCodigo;
	}

	public Integer getSeqIPH() {
		return seqIPH;
	}

	public void setSeqIPH(Integer seqIPH) {
		this.seqIPH = seqIPH;
	}

	public Short getSeqPHO() {
		return seqPHO;
	}

	public void setSeqPHO(Short seqPHO) {
		this.seqPHO = seqPHO;
	}

	public Integer getSeqCID() {
		return seqCID;
	}

	public void setSeqCID(Integer seqCID) {
		this.seqCID = seqCID;
	}
}
