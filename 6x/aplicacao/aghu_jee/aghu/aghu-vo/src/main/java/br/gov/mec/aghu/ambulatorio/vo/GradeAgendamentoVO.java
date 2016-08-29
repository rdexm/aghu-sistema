package br.gov.mec.aghu.ambulatorio.vo;

import java.util.Date;

import br.gov.mec.aghu.core.commons.BaseBean;

@SuppressWarnings("PMD.CyclomaticComplexity")
public class GradeAgendamentoVO implements BaseBean {

	/**
	 * 
	 */
	private static final long serialVersionUID = -758242166407462866L;

	private Integer seq;

	private Date criadoEm;

	private Date alteradoEm;

	private Date dtUltimaGeracao;

	private Boolean procedimento;

	private String nomeServidor;

	private String nomeServidorAlterado;

	private String sigla;

	private Byte sala;

	private String siglaEspecialidade;

	private String nomeEspecialidade;

	private String nomeEquipe;

	private String nomeServidorProfEspecialidade;

	private String descricaoPagador;

	public GradeAgendamentoVO() {
	}

	public GradeAgendamentoVO(Integer seq, Date criadoEm, Date alteradoEm, Date dtUltimaGeracao, Boolean procedimento,
			String nomeServidor, String nomeServidorAlterado, String sigla, Byte sala, String siglaEspecialidade,
			String nomeEspecialidade, String nomeEquipe, String nomeServidorProfEspecialidade, String descricaoPagador) {
		this.seq = seq;
		this.criadoEm = criadoEm;
		this.alteradoEm = alteradoEm;
		this.dtUltimaGeracao = dtUltimaGeracao;
		this.procedimento = procedimento;
		this.nomeServidor = nomeServidor;
		this.nomeServidorAlterado = nomeServidorAlterado;
		this.sigla = sigla;
		this.sala = sala;
		this.siglaEspecialidade = siglaEspecialidade;
		this.nomeEspecialidade = nomeEspecialidade;
		this.nomeEquipe = nomeEquipe;
		this.nomeServidorProfEspecialidade = nomeServidorProfEspecialidade;
		this.descricaoPagador = descricaoPagador;
	}

	public Integer getSeq() {
		return seq;
	}

	public void setSeq(Integer seq) {
		this.seq = seq;
	}

	public Date getCriadoEm() {
		return criadoEm;
	}

	public void setCriadoEm(Date criadoEm) {
		this.criadoEm = criadoEm;
	}

	public Date getAlteradoEm() {
		return alteradoEm;
	}

	public void setAlteradoEm(Date alteradoEm) {
		this.alteradoEm = alteradoEm;
	}

	public Date getDtUltimaGeracao() {
		return dtUltimaGeracao;
	}

	public void setDtUltimaGeracao(Date dtUltimaGeracao) {
		this.dtUltimaGeracao = dtUltimaGeracao;
	}

	public Boolean getProcedimento() {
		return procedimento;
	}

	public void setProcedimento(Boolean procedimento) {
		this.procedimento = procedimento;
	}

	public String getNomeServidor() {
		return nomeServidor;
	}

	public void setNomeServidor(String nomeServidor) {
		this.nomeServidor = nomeServidor;
	}

	public String getNomeServidorAlterado() {
		return nomeServidorAlterado;
	}

	public void setNomeServidorAlterado(String nomeServidorAlterado) {
		this.nomeServidorAlterado = nomeServidorAlterado;
	}

	public String getSigla() {
		return sigla;
	}

	public void setSigla(String sigla) {
		this.sigla = sigla;
	}

	public Byte getSala() {
		return sala;
	}

	public void setSala(Byte sala) {
		this.sala = sala;
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

	public String getNomeEquipe() {
		return nomeEquipe;
	}

	public void setNomeEquipe(String nomeEquipe) {
		this.nomeEquipe = nomeEquipe;
	}

	public String getNomeServidorProfEspecialidade() {
		return nomeServidorProfEspecialidade;
	}

	public void setNomeServidorProfEspecialidade(String nomeServidorProfEspecialidade) {
		this.nomeServidorProfEspecialidade = nomeServidorProfEspecialidade;
	}

	public String getDescricaoPagador() {
		return descricaoPagador;
	}

	public void setDescricaoPagador(String descricaoPagador) {
		this.descricaoPagador = descricaoPagador;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	@SuppressWarnings("PMD.NPathComplexity")
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((alteradoEm == null) ? 0 : alteradoEm.hashCode());
		result = prime * result + ((criadoEm == null) ? 0 : criadoEm.hashCode());
		result = prime * result + ((descricaoPagador == null) ? 0 : descricaoPagador.hashCode());
		result = prime * result + ((dtUltimaGeracao == null) ? 0 : dtUltimaGeracao.hashCode());
		result = prime * result + ((nomeEquipe == null) ? 0 : nomeEquipe.hashCode());
		result = prime * result + ((nomeEspecialidade == null) ? 0 : nomeEspecialidade.hashCode());
		result = prime * result + ((nomeServidor == null) ? 0 : nomeServidor.hashCode());
		result = prime * result + ((nomeServidorAlterado == null) ? 0 : nomeServidorAlterado.hashCode());
		result = prime * result + ((nomeServidorProfEspecialidade == null) ? 0 : nomeServidorProfEspecialidade.hashCode());
		result = prime * result + ((procedimento == null) ? 0 : procedimento.hashCode());
		result = prime * result + ((sala == null) ? 0 : sala.hashCode());
		result = prime * result + ((seq == null) ? 0 : seq.hashCode());
		result = prime * result + ((sigla == null) ? 0 : sigla.hashCode());
		result = prime * result + ((siglaEspecialidade == null) ? 0 : siglaEspecialidade.hashCode());
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	@SuppressWarnings("PMD.ExcessiveMethodLength")
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
		GradeAgendamentoVO other = (GradeAgendamentoVO) obj;
		if (alteradoEm == null) {
			if (other.alteradoEm != null){
				return false;
			}
		} else if (!alteradoEm.equals(other.alteradoEm)){
			return false;
		}
		if (criadoEm == null) {
			if (other.criadoEm != null){
				return false;
			}
		} else if (!criadoEm.equals(other.criadoEm)){
			return false;
		}
		if (descricaoPagador == null) {
			if (other.descricaoPagador != null){
				return false;
			}
		} else if (!descricaoPagador.equals(other.descricaoPagador)){
			return false;
		}
		if (dtUltimaGeracao == null) {
			if (other.dtUltimaGeracao != null){
				return false;
			}
		} else if (!dtUltimaGeracao.equals(other.dtUltimaGeracao)){
			return false;
		}
		if (nomeEquipe == null) {
			if (other.nomeEquipe != null){
				return false;
			}
		} else if (!nomeEquipe.equals(other.nomeEquipe)){
			return false;
		}
		if (nomeEspecialidade == null) {
			if (other.nomeEspecialidade != null){
				return false;
			}
		} else if (!nomeEspecialidade.equals(other.nomeEspecialidade)){
			return false;
		}
		if (nomeServidor == null) {
			if (other.nomeServidor != null){
				return false;
			}
		} else if (!nomeServidor.equals(other.nomeServidor)){
			return false;
		}
		if (nomeServidorAlterado == null) {
			if (other.nomeServidorAlterado != null){
				return false;
			}
		} else if (!nomeServidorAlterado.equals(other.nomeServidorAlterado)){
			return false;
		}
		if (nomeServidorProfEspecialidade == null) {
			if (other.nomeServidorProfEspecialidade != null){
				return false;
			}
		} else if (!nomeServidorProfEspecialidade.equals(other.nomeServidorProfEspecialidade)){
			return false;
		}
		if (procedimento == null) {
			if (other.procedimento != null){
				return false;
			}
		} else if (!procedimento.equals(other.procedimento)){
			return false;
		}
		if (sala == null) {
			if (other.sala != null){
				return false;
			}
		} else if (!sala.equals(other.sala)){
			return false;
		}
		if (seq == null) {
			if (other.seq != null){
				return false;
			}
		} else if (!seq.equals(other.seq)){
			return false;
		}
		if (sigla == null) {
			if (other.sigla != null){
				return false;
			}
		} else if (!sigla.equals(other.sigla)){
			return false;
		}
		if (siglaEspecialidade == null) {
			if (other.siglaEspecialidade != null){
				return false;
			}
		} else if (!siglaEspecialidade.equals(other.siglaEspecialidade)){
			return false;
		}
		return true;
	}
	
}
