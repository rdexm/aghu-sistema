package br.gov.mec.aghu.faturamento.vo;

import java.io.Serializable;
import java.util.Date;

import br.gov.mec.aghu.dominio.DominioIndComparacao;
import br.gov.mec.aghu.dominio.DominioIndCompatExclus;

public class FatTabCompatibilidadeVO implements Serializable {

	private static final long serialVersionUID = 4739010917255234722L;

	private boolean wRetorno;
	private String wProcedimentoPrincipal;
	private String wRegistroPrincipal;
	private String wProcedimentoCompativel;
	private String wRegistroCompativel;
	private String wTpCompatibilidade;
	private Integer wQtPermitida;
	private String wDtCompetencia;

	private DominioIndCompatExclus indCompatExclus;
	private DominioIndComparacao indComparacao;

	private Long cpxSeq;
	private Date dataComp;

	private Boolean indAmbulatorio;
	private Boolean indInternacao;

	private String compats; // VARCHAR2(32700)
	private String compatsAux; // VARCHAR2(32700)

	public String getwProcedimentoPrincipal() {
		return wProcedimentoPrincipal;
	}

	public void setwProcedimentoPrincipal(String wProcedimentoPrincipal) {
		this.wProcedimentoPrincipal = wProcedimentoPrincipal;
	}

	public String getwRegistroPrincipal() {
		return wRegistroPrincipal;
	}

	public void setwRegistroPrincipal(String wRegistroPrincipal) {
		this.wRegistroPrincipal = wRegistroPrincipal;
	}

	public String getwProcedimentoCompativel() {
		return wProcedimentoCompativel;
	}

	public void setwProcedimentoCompativel(String wProcedimentoCompativel) {
		this.wProcedimentoCompativel = wProcedimentoCompativel;
	}

	public String getwRegistroCompativel() {
		return wRegistroCompativel;
	}

	public void setwRegistroCompativel(String wRegistroCompativel) {
		this.wRegistroCompativel = wRegistroCompativel;
	}

	public String getwTpCompatibilidade() {
		return wTpCompatibilidade;
	}

	public void setwTpCompatibilidade(String wTpCompatibilidade) {
		this.wTpCompatibilidade = wTpCompatibilidade;
	}

	public Integer getwQtPermitida() {
		return wQtPermitida;
	}

	public void setwQtPermitida(Integer wQtPermitida) {
		this.wQtPermitida = wQtPermitida;
	}

	public String getwDtCompetencia() {
		return wDtCompetencia;
	}

	public void setwDtCompetencia(String wDtCompetencia) {
		this.wDtCompetencia = wDtCompetencia;
	}

	public boolean iswRetorno() {
		return wRetorno;
	}

	public void setwRetorno(boolean wRetorno) {
		this.wRetorno = wRetorno;
	}

	public DominioIndCompatExclus getIndCompatExclus() {
		return indCompatExclus;
	}

	public void setIndCompatExclus(DominioIndCompatExclus indCompatExclus) {
		this.indCompatExclus = indCompatExclus;
	}

	public DominioIndComparacao getIndComparacao() {
		return indComparacao;
	}

	public void setIndComparacao(DominioIndComparacao indComparacao) {
		this.indComparacao = indComparacao;
	}

	public Long getCpxSeq() {
		return cpxSeq;
	}

	public void setCpxSeq(Long cpxSeq) {
		this.cpxSeq = cpxSeq;
	}

	public Date getDataComp() {
		return dataComp;
	}

	public void setDataComp(Date dataComp) {
		this.dataComp = dataComp;
	}

	public String getCompats() {
		return compats;
	}

	public void setCompats(String compats) {
		this.compats = compats;
	}

	public String getCompatsAux() {
		return compatsAux;
	}

	public void setCompatsAux(String compatsAux) {
		this.compatsAux = compatsAux;
	}

	public Boolean getIndAmbulatorio() {
		return indAmbulatorio;
	}

	public void setIndAmbulatorio(Boolean indAmbulatorio) {
		this.indAmbulatorio = indAmbulatorio;
	}

	public Boolean getIndInternacao() {
		return indInternacao;
	}

	public void setIndInternacao(Boolean indInternacao) {
		this.indInternacao = indInternacao;
	}

}
