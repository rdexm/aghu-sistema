package br.gov.mec.aghu.bancosangue.vo;

import java.util.Date;

import br.gov.mec.aghu.dominio.DominioResponsavelColeta;
import br.gov.mec.aghu.dominio.DominioSituacaoColeta;

public class SitucaoSolicitacaoHemoterapicaVO {

	private DominioResponsavelColeta indResponsavelColeta;
	private DominioSituacaoColeta indSituacaoColeta;
	private Integer pacCodigo;
	private Date dtHrAmostra;
	
	public SitucaoSolicitacaoHemoterapicaVO() {
		super();
	}

	public SitucaoSolicitacaoHemoterapicaVO(DominioResponsavelColeta indResponsavelColeta, 
			DominioSituacaoColeta indSituacaoColeta,
			Integer pacCodigo, Date dtHrAmostra) {
		super();
		this.indResponsavelColeta = indResponsavelColeta;
		this.indSituacaoColeta = indSituacaoColeta;
		this.pacCodigo = pacCodigo;
		this.dtHrAmostra = dtHrAmostra;
	}

	public DominioResponsavelColeta getIndResponsavelColeta() {
		return indResponsavelColeta;
	}

	public void setIndResponsavelColeta(
			DominioResponsavelColeta indResponsavelColeta) {
		this.indResponsavelColeta = indResponsavelColeta;
	}

	public DominioSituacaoColeta getIndSituacaoColeta() {
		return indSituacaoColeta;
	}

	public void setIndSituacaoColeta(
			DominioSituacaoColeta indSituacaoColeta) {
		this.indSituacaoColeta = indSituacaoColeta;
	}

	public Integer getPacCodigo() {
		return pacCodigo;
	}

	public void setPacCodigo(Integer pacCodigo) {
		this.pacCodigo = pacCodigo;
	}

	public Date getDtHrAmostra() {
		return dtHrAmostra;
	}

	public void setDtHrAmostra(Date dtHrAmostra) {
		this.dtHrAmostra = dtHrAmostra;
	}
	
}
