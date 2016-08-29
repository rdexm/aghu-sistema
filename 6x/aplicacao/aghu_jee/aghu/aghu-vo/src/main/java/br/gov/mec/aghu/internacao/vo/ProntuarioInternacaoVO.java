package br.gov.mec.aghu.internacao.vo;

import java.text.SimpleDateFormat;
import java.util.Date;

import br.gov.mec.aghu.model.AghAla;
import br.gov.mec.aghu.core.commons.BaseBean;

/**
 * VO que represanta uma entrada na view V_AIN_AVISO_SAMIS.
 * 
 * @author gmneto
 * 
 */
public class ProntuarioInternacaoVO implements
		Comparable<ProntuarioInternacaoVO>, BaseBean{

	private static final long serialVersionUID = 3583110056107574127L;

	private Integer idInternacao;

	private Integer seqAtendimentoUrgencia;

	private Integer codigoPaciente;

	private Integer prontuarioPaciente;

	private String dataHoraInternacao;

	private Short numeroQuarto;

	private String idLeito;

	private String nomePaciente;

	private String siglaEspecialidade;

	private String nomeEspecialidade;

	private Integer codigoClinicas;

	private String descricaoClinicas;

	private String nomeProfessor;

	private String andarAlaDescricao;

	private String descricaoCarater;

	private String origem;

	private String dataHoraAvisoSamis;

	private Boolean selecionado;

	public ProntuarioInternacaoVO() {

	}

	public ProntuarioInternacaoVO(Object[] paramsReceived) {
		Object[] params=(Object[])paramsReceived.clone();

		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");

		this.idInternacao = (Integer) params[0];
		this.seqAtendimentoUrgencia = (Integer) params[1];
		this.codigoPaciente = (Integer) params[2];
		this.prontuarioPaciente = (Integer) params[3];
		this.dataHoraInternacao = sdf.format((Date) params[4]);
		this.numeroQuarto = (Short) params[5];
		this.idLeito = (String) params[6];
		this.nomePaciente = (String) params[7];
		this.siglaEspecialidade = (String) params[8];
		this.nomeEspecialidade = (String) params[9];
		this.codigoClinicas = (Integer) params[10];
		this.descricaoClinicas = (String) params[11];
		this.nomeProfessor = (String) params[12];

		String andar = "";
		if (params[13] != null) {
			andar = ((String) params[13]).toString();
		}

		String ala = "";
		if (params[14] != null) {
			ala = ((AghAla) params[14]).toString();
		}
		String descricao = "";
		if (params[15] != null) {
			descricao = (String) params[15];
		}

		this.andarAlaDescricao = andar + " " + ala + " - " + descricao;

		this.descricaoCarater = (String) params[16];
		this.dataHoraAvisoSamis = sdf.format((Date) params[17]);
		this.origem = (String) params[18];
		this.selecionado = Boolean.FALSE;
	}

	public Integer getIdInternacao() {
		return idInternacao;
	}

	public void setIdInternacao(Integer idInternacao) {
		this.idInternacao = idInternacao;
	}

	public Integer getSeqAtendimentoUrgencia() {
		return seqAtendimentoUrgencia;
	}

	public void setSeqAtendimentoUrgencia(Integer seqAtendimentoUrgencia) {
		this.seqAtendimentoUrgencia = seqAtendimentoUrgencia;
	}

	public Integer getCodigoPaciente() {
		return codigoPaciente;
	}

	public void setCodigoPaciente(Integer codigoPaciente) {
		this.codigoPaciente = codigoPaciente;
	}

	public Integer getProntuarioPaciente() {
		return prontuarioPaciente;
	}

	public void setProntuarioPaciente(Integer prontuarioPaciente) {
		this.prontuarioPaciente = prontuarioPaciente;
	}

	public String getDataHoraInternacao() {
		return dataHoraInternacao;
	}

	public void setDataHoraInternacao(String dataHoraInternacao) {
		this.dataHoraInternacao = dataHoraInternacao;
	}

	public Short getNumeroQuarto() {
		return numeroQuarto;
	}

	public void setNumeroQuarto(Short numeroQuarto) {
		this.numeroQuarto = numeroQuarto;
	}

	public String getIdLeito() {
		return idLeito;
	}

	public void setIdLeito(String idLeito) {
		this.idLeito = idLeito;
	}

	public String getNomePaciente() {
		return nomePaciente;
	}

	public void setNomePaciente(String nomePaciente) {
		this.nomePaciente = nomePaciente;
	}

	public String getSiglaEspecialidade() {
		return siglaEspecialidade;
	}

	public void setSiglaEspecialidade(String siglaEspecialidade) {
		this.siglaEspecialidade = siglaEspecialidade;
	}

	public String getNomeEspecialidade() {
		return nomeEspecialidade;
	}

	public void setNomeEspecialidade(String nomeEspecialidade) {
		this.nomeEspecialidade = nomeEspecialidade;
	}

	public Integer getCodigoClinicas() {
		return codigoClinicas;
	}

	public void setCodigoClinicas(Integer codigoClinicas) {
		this.codigoClinicas = codigoClinicas;
	}

	public String getDescricaoClinicas() {
		return descricaoClinicas;
	}

	public void setDescricaoClinicas(String descricaoClinicas) {
		this.descricaoClinicas = descricaoClinicas;
	}

	public String getNomeProfessor() {
		return nomeProfessor;
	}

	public void setNomeProfessor(String nomeProfessor) {
		this.nomeProfessor = nomeProfessor;
	}

	public String getAndarAlaDescricao() {
		return andarAlaDescricao;
	}

	public void setAndarAlaDescricao(String andarAlaDescricao) {
		this.andarAlaDescricao = andarAlaDescricao;
	}

	public String getDescricaoCarater() {
		return descricaoCarater;
	}

	public void setDescricaoCarater(String descricaoCarater) {
		this.descricaoCarater = descricaoCarater;
	}

	public String getOrigem() {
		return origem;
	}

	public void setOrigem(String origem) {
		this.origem = origem;
	}

	public String getDataHoraAvisoSamis() {
		return dataHoraAvisoSamis;
	}

	public void setDataHoraAvisoSamis(String dataHoraAvisoSamis) {
		this.dataHoraAvisoSamis = dataHoraAvisoSamis;
	}

	@Override
	public int compareTo(ProntuarioInternacaoVO o) {
		return this.dataHoraAvisoSamis.compareTo(o.getDataHoraAvisoSamis());
	}

	public Boolean getSelecionado() {
		return selecionado;
	}

	public void setSelecionado(Boolean selecionado) {
		this.selecionado = selecionado;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((idInternacao == null) ? 0 : idInternacao.hashCode());
		result = prime * result + ((idLeito == null) ? 0 : idLeito.hashCode());
		result = prime
				* result
				+ ((seqAtendimentoUrgencia == null) ? 0
						: seqAtendimentoUrgencia.hashCode());
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
		if (!(obj instanceof ProntuarioInternacaoVO)) {
			return false;
		}
		ProntuarioInternacaoVO other = (ProntuarioInternacaoVO) obj;
		if (idInternacao == null) {
			if (other.idInternacao != null) {
				return false;
			}
		} else if (!idInternacao.equals(other.idInternacao)) {
			return false;
		}
		if (idLeito == null) {
			if (other.idLeito != null) {
				return false;
			}
		} else if (!idLeito.equals(other.idLeito)) {
			return false;
		}
		if (seqAtendimentoUrgencia == null) {
			if (other.seqAtendimentoUrgencia != null) {
				return false;
			}
		} else if (!seqAtendimentoUrgencia.equals(other.seqAtendimentoUrgencia)) {
			 return false;
		}
		return true;
	}
}
