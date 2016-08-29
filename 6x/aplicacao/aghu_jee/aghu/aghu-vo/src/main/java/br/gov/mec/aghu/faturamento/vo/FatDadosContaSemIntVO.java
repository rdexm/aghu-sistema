package br.gov.mec.aghu.faturamento.vo;

import java.util.Date;

import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import br.gov.mec.aghu.dominio.DominioAtivoCancelado;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.AinTiposCaraterInternacao;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.core.commons.BaseBean;

public class FatDadosContaSemIntVO implements BaseBean {
	
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 2233225547924135859L;

	// FAT_CONTAS_INTERNACAO.CTH_SEQ
	private Integer cthSeq;
	
	// FAT_DADOS_CONTA_SEM_INT.SEQ
	private Integer seq;
	
	private String nomePaciente;

	private AinTiposCaraterInternacao tipoCaraterInternacao;
	
	private RapServidores servidor;
	
	private Date dataInicial;
	
	private Date dataFinal;
	
	private AghUnidadesFuncionais unf;
	
	private DominioAtivoCancelado indSituacao;
	
	// GETTERS AND SETTERS 

	public void setSeq(Integer seq) {
		this.seq = seq;
	}

	public Integer getSeq() {
		return seq;
	}
	
	public void setCthSeq(Integer cthSeq) {
		this.cthSeq = cthSeq;
	}

	public Integer getCthSeq() {
		return cthSeq;
	}

	public AinTiposCaraterInternacao getTipoCaraterInternacao() {
		return tipoCaraterInternacao;
	}

	public void setTipoCaraterInternacao(
			AinTiposCaraterInternacao tipoCaraterInternacao) {
		this.tipoCaraterInternacao = tipoCaraterInternacao;
	}

	public RapServidores getServidor() {
		return servidor;
	}

	public void setServidor(RapServidores servidor) {
		this.servidor = servidor;
	}

	@Temporal(TemporalType.TIMESTAMP)
	public Date getDataInicial() {
		return dataInicial;
	}

	public void setDataInicial(Date dataInicial) {
		this.dataInicial = dataInicial;
	}

	@Temporal(TemporalType.TIMESTAMP)
	public Date getDataFinal() {
		return dataFinal;
	}

	public void setDataFinal(Date dataFinal) {
		this.dataFinal = dataFinal;
	}

	public AghUnidadesFuncionais getUnf() {
		return unf;
	}

	public void setUnf(AghUnidadesFuncionais unf) {
		this.unf = unf;
	}

	public DominioAtivoCancelado getIndSituacao() {
		return indSituacao;
	}

	public void setIndSituacao(DominioAtivoCancelado indSituacao) {
		this.indSituacao = indSituacao;
	}
	
	public String getNomePaciente() {
		return nomePaciente;
	}

	public void setNomePaciente(String nomePaciente) {
		this.nomePaciente = nomePaciente;
	}
}
