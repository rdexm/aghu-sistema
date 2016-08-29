package br.gov.mec.aghu.paciente.vo;

import java.io.Serializable;
import java.util.Date;

import br.gov.mec.aghu.core.utils.DateUtil;
import br.gov.mec.aghu.dominio.DominioSituacaoMovimentoProntuario;
import br.gov.mec.aghu.dominio.DominioTipoEnvioProntuario;

public class AipMovimentacaoProntuariosVO implements Serializable {
	
	
	private static final long serialVersionUID = -3959312377376546613L;
	
	private Integer seq; 
	private Integer codigoMovimentacao;
	private Integer prontuario;
	private Integer pacCodigo;
	private String nomePaciente;
	private Date dataMovimentacao;
	private Date dataCadastroOrigemProntuario;
	private Date dataRetirada;
	private Date dataDevolucao;
	private String origemProntuario;
	private Short seqOrigemProntuario;
	private String localAtual;
	private String localPrimeiraMovimentacao;
	private String solicitanteDescricao;
	private Short codigoSolicitante;
	private String observacoes;
	private DominioSituacaoMovimentoProntuario situacao;
	private DominioTipoEnvioProntuario tipoEnvio;
	private Short volumes;
	private Integer servidorMatricula;
	private Short serVinCodigo;
	private Integer serMatriculaRetirado;
	private Short serVinCodigoRetirado;
	private Integer slpCodigo;
	
	private Boolean selecionado;

	
	public AipMovimentacaoProntuariosVO() {
		this.selecionado = false;
	}
	

	public Integer getCodigoMovimentacao() {
		return codigoMovimentacao;
	}


	public void setCodigoMovimentacao(Integer codigoMovimentacao) {
		this.codigoMovimentacao = codigoMovimentacao;
	}

	public Integer getProntuario() {
		return prontuario;
	}

	public void setProntuario(Integer prontuario) {
		this.prontuario = prontuario;
	}

	public Integer getPacCodigo() {
		return pacCodigo;
	}

	public void setPacCodigo(Integer pacCodigo) {
		this.pacCodigo = pacCodigo;
	}

	public String getNomePaciente() {
		return nomePaciente;
	}

	public void setNomePaciente(String nomePaciente) {
		this.nomePaciente = nomePaciente;
	}

	public Date getDataMovimentacao() {
		return dataMovimentacao;
	}

	public void setDataMovimentacao(Date dataMovimentacao) {
		this.dataMovimentacao = dataMovimentacao;
	}
	
	public String getDataMovimentacaoFormatada(){
		return DateUtil.obterDataFormatada(dataMovimentacao, "dd/MM/yyyy");
	}

	public String getOrigemProntuario() {
		return origemProntuario;
	}

	public void setOrigemProntuario(String origemProntuario) {
		this.origemProntuario = origemProntuario;
	}

	public String getLocalAtual() {
		return localAtual;
	}

	public void setLocalAtual(String localAtual) {
		this.localAtual = localAtual;
	}

	public String getLocalPrimeiraMovimentacao() {
		return localPrimeiraMovimentacao;
	}

	public void setLocalPrimeiraMovimentacao(String localPrimeiraMovimentacao) {
		this.localPrimeiraMovimentacao = localPrimeiraMovimentacao;
	}
	
	public Boolean getSelecionado() {
		return selecionado;
	}

	public void setSelecionado(Boolean selecionado) {
		this.selecionado = selecionado;
	}

	public Date getDataCadastroOrigemProntuario() {
		return dataCadastroOrigemProntuario;
	}

	public void setDataCadastroOrigemProntuario(Date dataCadastroOrigemProntuario) {
		this.dataCadastroOrigemProntuario = dataCadastroOrigemProntuario;
	}
	
	public Short getSeqOrigemProntuario() {
		return seqOrigemProntuario;
	}

	public void setSeqOrigemProntuario(Short seqOrigemProntuario) {
		this.seqOrigemProntuario = seqOrigemProntuario;
	}

	public DominioSituacaoMovimentoProntuario getSituacao() {
		return situacao;
	}

	public void setSituacao(DominioSituacaoMovimentoProntuario situacao) {
		this.situacao = situacao;
	}

	public String getSolicitanteDescricao() {
		return solicitanteDescricao;
	}

	public void setSolicitanteDescricao(String solicitanteDescricao) {
		this.solicitanteDescricao = solicitanteDescricao;
	}

	public Short getCodigoSolicitante() {
		return codigoSolicitante;
	}

	public void setCodigoSolicitante(Short codigoSolicitante) {
		this.codigoSolicitante = codigoSolicitante;
	}

	public DominioTipoEnvioProntuario getTipoEnvio() {
		return tipoEnvio;
	}

	public void setTipoEnvio(DominioTipoEnvioProntuario tipoEnvio) {
		this.tipoEnvio = tipoEnvio;
	}

	public Short getVolumes() {
		return volumes;
	}

	public void setVolumes(Short volumes) {
		this.volumes = volumes;
	}

	public Date getDataRetirada() {
		return dataRetirada;
	}

	public void setDataRetirada(Date dataRetirada) {
		this.dataRetirada = dataRetirada;
	}
	
	public Date getDataDevolucao() {
		return dataDevolucao;
	}

	public void setDataDevolucao(Date dataDevolucao) {
		this.dataDevolucao = dataDevolucao;
	}
	
	public String getObservacoes() {
		return observacoes;
	}

	public void setObservacoes(String observacoes) {
		this.observacoes = observacoes;
	}

	public Integer getServidorMatricula() {
		return servidorMatricula;
	}
	
	public void setServidorMatricula(Integer servidorMatricula) {
		this.servidorMatricula = servidorMatricula;
	}
	
	public Integer getSeq() {
		return seq;
	}

	public void setSeq(Integer seq) {
		this.seq = seq;
	}

	public Short getSerVinCodigo() {
		return serVinCodigo;
	}

	public void setSerVinCodigo(Short serVinCodigo) {
		this.serVinCodigo = serVinCodigo;
	}


	public Integer getSerMatriculaRetirado() {
		return serMatriculaRetirado;
	}


	public void setSerMatriculaRetirado(Integer serMatriculaRetirado) {
		this.serMatriculaRetirado = serMatriculaRetirado;
	}


	public Short getSerVinCodigoRetirado() {
		return serVinCodigoRetirado;
	}


	public void setSerVinCodigoRetirado(Short serVinCodigoRetirado) {
		this.serVinCodigoRetirado = serVinCodigoRetirado;
	}


	public Integer getSlpCodigo() {
		return slpCodigo;
	}


	public void setSlpCodigo(Integer slpCodigo) {
		this.slpCodigo = slpCodigo;
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
		if (this == obj){
			return true;
		}
		if (obj == null){
			return false;
		}
		if (getClass() != obj.getClass()){
			return false;
		}
		AipMovimentacaoProntuariosVO other = (AipMovimentacaoProntuariosVO) obj;
		if (seq == null) {
			if (other.seq != null){
				return false;
			}
		} else if (!seq.equals(other.seq)){
			return false;
		}
		return true;
	}
	
	
	
	
	
	
	
}
