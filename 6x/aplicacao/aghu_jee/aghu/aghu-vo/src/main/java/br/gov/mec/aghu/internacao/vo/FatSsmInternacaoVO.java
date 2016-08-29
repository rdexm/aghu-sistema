package br.gov.mec.aghu.internacao.vo;

import java.util.Date;

import br.gov.mec.aghu.dominio.DominioSexo;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.FatItensProcedHospitalarId;
import br.gov.mec.aghu.model.FatVlrItemProcedHospCompsId;

public class FatSsmInternacaoVO {

	private FatItensProcedHospitalarId id;
	
	private long codTabela;
	
	private String descricao;

	private Byte clcCodigo;
	
	private DominioSexo sexo;
	
	private Date dtFimCompetencia;

	private FatVlrItemProcedHospCompsId idFatVlrItemProcedHospComps;
	
	private DominioSituacao indSituacao;

	public FatItensProcedHospitalarId getId() {
		return id;
	}

	public void setId(FatItensProcedHospitalarId id) {
		this.id = id;
	}

	public long getCodTabela() {
		return codTabela;
	}

	public void setCodTabela(long codTabela) {
		this.codTabela = codTabela;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public Byte getClcCodigo() {
		return clcCodigo;
	}

	public void setClcCodigo(Byte clcCodigo) {
		this.clcCodigo = clcCodigo;
	}

	public DominioSexo getSexo() {
		return sexo;
	}

	public void setSexo(DominioSexo sexo) {
		this.sexo = sexo;
	}

	public Date getDtFimCompetencia() {
		return dtFimCompetencia;
	}

	public void setDtFimCompetencia(Date dtFimCompetencia) {
		this.dtFimCompetencia = dtFimCompetencia;
	}

	public FatVlrItemProcedHospCompsId getIdFatVlrItemProcedHospComps() {
		return idFatVlrItemProcedHospComps;
	}

	public void setIdFatVlrItemProcedHospComps(
			FatVlrItemProcedHospCompsId idFatVlrItemProcedHospComps) {
		this.idFatVlrItemProcedHospComps = idFatVlrItemProcedHospComps;
	}

	public DominioSituacao getIndSituacao() {
		return indSituacao;
	}

	public void setIndSituacao(DominioSituacao indSituacao) {
		this.indSituacao = indSituacao;
	}
}
