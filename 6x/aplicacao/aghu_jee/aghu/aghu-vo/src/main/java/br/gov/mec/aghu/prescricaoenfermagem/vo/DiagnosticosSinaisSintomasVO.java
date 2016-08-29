package br.gov.mec.aghu.prescricaoenfermagem.vo;

import java.io.Serializable;

public class DiagnosticosSinaisSintomasVO implements Serializable {

	private static final long serialVersionUID = 5381658175246030382L;

	private String descricaoGrupo;
	private String descricaoSubgrupo;
	private String descricaoDiagnostico;
	private String descricaoEtiologia;
	private String indSituacao;
	private Short dgnSnbGnbSeq;
	private Short dgnSnbSequencia;
	private Short dgnSequencia;
	private Short freSeq;

	public String getDescricaoGrupo() {
		return descricaoGrupo;
	}

	public void setDescricaoGrupo(String descricaoGrupo) {
		this.descricaoGrupo = descricaoGrupo;
	}

	public String getDescricaoSubgrupo() {
		return descricaoSubgrupo;
	}

	public void setDescricaoSubgrupo(String descricaoSubgrupo) {
		this.descricaoSubgrupo = descricaoSubgrupo;
	}

	public String getDescricaoDiagnostico() {
		return descricaoDiagnostico;
	}

	public void setDescricaoDiagnostico(String descricaoDiagnostico) {
		this.descricaoDiagnostico = descricaoDiagnostico;
	}

	public String getDescricaoEtiologia() {
		return descricaoEtiologia;
	}

	public void setDescricaoEtiologia(String descricaoEtiologia) {
		this.descricaoEtiologia = descricaoEtiologia;
	}

	public String getIndSituacao() {
		return indSituacao;
	}

	public void setIndSituacao(String indSituacao) {
		this.indSituacao = indSituacao;
	}

	public Short getDgnSnbGnbSeq() {
		return dgnSnbGnbSeq;
	}

	public void setDgnSnbGnbSeq(Short dgnSnbGnbSeq) {
		this.dgnSnbGnbSeq = dgnSnbGnbSeq;
	}

	public Short getDgnSnbSequencia() {
		return dgnSnbSequencia;
	}

	public void setDgnSnbSequencia(Short dgnSnbSequencia) {
		this.dgnSnbSequencia = dgnSnbSequencia;
	}

	public Short getDgnSequencia() {
		return dgnSequencia;
	}

	public void setDgnSequencia(Short dgnSequencia) {
		this.dgnSequencia = dgnSequencia;
	}

	public Short getFreSeq() {
		return freSeq;
	}

	public void setFreSeq(Short freSeq) {
		this.freSeq = freSeq;
	}

}
