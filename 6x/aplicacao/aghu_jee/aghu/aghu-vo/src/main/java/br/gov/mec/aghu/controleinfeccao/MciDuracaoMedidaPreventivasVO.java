package br.gov.mec.aghu.controleinfeccao;

import java.util.Date;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.core.commons.BaseBean;


public class MciDuracaoMedidaPreventivasVO implements BaseBean {

	private static final long serialVersionUID = -2755258515971423321L;
	/*
	 * 
	 */
	private Short seq;
	private String descricao;
	private DominioSituacao situacao;
	private Date criadoAlterado;
	private Date criadoEm;
	
	
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

	public DominioSituacao getSituacao() {
		return situacao;
	}

	public void setSituacao(DominioSituacao situacao) {
		this.situacao = situacao;
	}

	public Date getCriadoAlterado() {
		return criadoAlterado;
	}

	public void setCriadoAlterado(Date criadoAlterado) {
		this.criadoAlterado = criadoAlterado;
	}

	public Date getCriadoEm() {
		return criadoEm;
	}

	public void setCriadoEm(Date criadoEm) {
		this.criadoEm = criadoEm;
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
		if (getClass() != obj.getClass()) {
			return false;
		}
		MciDuracaoMedidaPreventivasVO other = (MciDuracaoMedidaPreventivasVO) obj;
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
