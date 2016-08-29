package br.gov.mec.aghu.internacao.vo;

import java.util.Date;

import org.apache.commons.lang3.StringUtils;

import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.AghAla;
import br.gov.mec.aghu.model.AghClinicas;
import br.gov.mec.aghu.model.AghTiposUnidadeFuncional;
import br.gov.mec.aghu.core.commons.BaseBean;


public class AghUnidadesFuncionaisVO implements BaseBean {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4209019441855227823L;
	
	private Short seq;
	private String descricao;
	private String andar;
	private AghAla indAla;
	private Date hrioValidadePme;
	private Date hrioValidadePen;
	private String andarAlaDescricao;
	private DominioSituacao indSitUnidFunc;
	private DominioSimNao indPermPacienteExtra;
	private Date dthrConfCenso;
	private DominioSimNao indVerfEscalaProfInt;
	private DominioSimNao indBloqLtoIsolamento;
	private DominioSimNao indUnidEmergencia;
	private Short capacInternacao = 0;
	private AghClinicas clinicas;
	private AghTiposUnidadeFuncional tiposUnidadeFuncional;	
	private DominioSimNao indConsClin;
	private DominioSimNao indUnidCti;
	private DominioSimNao indUnidHospDia;
	private DominioSimNao indUnidInternacao;
	private Byte nroViasPme = 0;
	private Byte nroViasPen = 0;
	
	//GETTERs e SETTERs

	public Short getSeq() {
		return seq;
	}
	public void setSeq(Short seq) {
		this.seq = seq;
	}
	public String getDescricao() {
		return descricao;
	}
	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}
	public String getAndar() {
		return andar;
	}
	public void setAndar(String andar) {
		this.andar = andar;
	}
	public AghAla getIndAla() {
		return indAla;
	}
	public void setIndAla(AghAla a) {
		this.indAla = a;
	}
	public Date getHrioValidadePme() {
		return hrioValidadePme;
	}
	public void setHrioValidadePme(Date hrioValidadePme) {
		this.hrioValidadePme = hrioValidadePme;
	}
	
	public Date getHrioValidadePen() {
		return hrioValidadePen;
	}

	public void setHrioValidadePen(Date hrioValidadePen) {
		this.hrioValidadePen = hrioValidadePen;
	}
	
	public String getAndarAlaDescricao() {
		return andarAlaDescricao;
	}
	public void setAndarAlaDescricao(String andarAlaDescricao) {
		this.andarAlaDescricao = andarAlaDescricao;
	}
	public DominioSituacao getIndSitUnidFunc() {
		return indSitUnidFunc;
	}
	public void setIndSitUnidFunc(DominioSituacao indSitUnidFunc) {
		this.indSitUnidFunc = indSitUnidFunc;
	}
	public DominioSimNao getIndPermPacienteExtra() {
		return indPermPacienteExtra;
	}
	public void setIndPermPacienteExtra(DominioSimNao indPermPacienteExtra) {
		this.indPermPacienteExtra = indPermPacienteExtra;
	}
	public Date getDthrConfCenso() {
		return dthrConfCenso;
	}
	public void setDthrConfCenso(Date dthrConfCenso) {
		this.dthrConfCenso = dthrConfCenso;
	}
	public DominioSimNao getIndVerfEscalaProfInt() {
		return indVerfEscalaProfInt;
	}
	public void setIndVerfEscalaProfInt(DominioSimNao indVerfEscalaProfInt) {
		this.indVerfEscalaProfInt = indVerfEscalaProfInt;
	}
	public DominioSimNao getIndBloqLtoIsolamento() {
		return indBloqLtoIsolamento;
	}
	public void setIndBloqLtoIsolamento(DominioSimNao indBloqLtoIsolamento) {
		this.indBloqLtoIsolamento = indBloqLtoIsolamento;
	}
	public DominioSimNao getIndUnidEmergencia() {
		return indUnidEmergencia;
	}
	public void setIndUnidEmergencia(DominioSimNao indUnidEmergencia) {
		this.indUnidEmergencia = indUnidEmergencia;
	}
	public Short getCapacInternacao() {
		return capacInternacao;
	}
	public void setCapacInternacao(Short capacInternacao) {
		this.capacInternacao = capacInternacao;
	}
	public AghClinicas getClinicas() {
		return clinicas;
	}
	public void setClinicas(AghClinicas clinicas) {
		this.clinicas = clinicas;
	}
	public DominioSimNao getIndConsClin() {
		return indConsClin;
	}
	public void setIndConsClin(DominioSimNao indConsClin) {
		this.indConsClin = indConsClin;
	}
	public DominioSimNao getIndUnidCti() {
		return indUnidCti;
	}
	public void setIndUnidCti(DominioSimNao indUnidCti) {
		this.indUnidCti = indUnidCti;
	}
	public DominioSimNao getIndUnidHospDia() {
		return indUnidHospDia;
	}
	public void setIndUnidHospDia(DominioSimNao indUnidHospDia) {
		this.indUnidHospDia = indUnidHospDia;
	}
	public DominioSimNao getIndUnidInternacao() {
		return indUnidInternacao;
	}
	public void setIndUnidInternacao(DominioSimNao indUnidInternacao) {
		this.indUnidInternacao = indUnidInternacao;
	}
	public Byte getNroViasPme() {
		return nroViasPme;
	}
	public void setNroViasPme(Byte nroViasPme) {
		this.nroViasPme = nroViasPme;
	}
	public Byte getNroViasPen() {
		return nroViasPen;
	}
	public void setNroViasPen(Byte nroViasPen) {
		this.nroViasPen = nroViasPen;
	}
	public AghTiposUnidadeFuncional getTiposUnidadeFuncional() {
		return tiposUnidadeFuncional;
	}
	public void setTiposUnidadeFuncional(
			AghTiposUnidadeFuncional tiposUnidadeFuncional) {
		this.tiposUnidadeFuncional = tiposUnidadeFuncional;
	}
	
	public String getLPADAndarAlaDescricao() {
		return (this.getAndar() != null ? StringUtils.leftPad(this.getAndar()
				.toString(), 2, "0") : "")
				+ " "
				+ (this.getIndAla() != null ? this.getIndAla().toString() : "")
				+ " - "
				+ (this.getDescricao() != null ? this.getDescricao() : "");
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((seq == null) ? 0 : seq.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof AghUnidadesFuncionaisVO)) {
			return false;
		}
		AghUnidadesFuncionaisVO other = (AghUnidadesFuncionaisVO) obj;
		if (seq == null) {
			if (other.seq != null) {
				return false;
			}
		} else if (!seq.equals(other.seq)) {
			return false;
		}
		return true;
	}
	
}
