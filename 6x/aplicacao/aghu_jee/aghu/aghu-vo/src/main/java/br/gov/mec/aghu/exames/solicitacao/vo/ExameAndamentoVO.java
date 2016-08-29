package br.gov.mec.aghu.exames.solicitacao.vo;

import java.util.Date;
import java.util.List;

import br.gov.mec.aghu.model.AelConfigExLaudoUnico;
import br.gov.mec.aghu.model.AelItemSolicitacaoExames;
import br.gov.mec.aghu.model.AelPatologista;

public class ExameAndamentoVO implements Comparable<ExameAndamentoVO> {

	private boolean selecionado;
	private String exame;
	private Long numeroExame;
	private Long lumSeq;
	private Long exameSeq;
	private String patologista;
	private Date dataRecebimento;
	private AelPatologista patologistaResponsavel;
	private AelConfigExLaudoUnico tipoExame;
	private List<AelItemSolicitacaoExames> itensSolicitacaoExame;
	private List<AelPatologista> aelPatologistas;

	public ExameAndamentoVO(AelConfigExLaudoUnico tipoExame, Long numeroExame, Long exameSeq, AelPatologista patologistaResponsavel,
 Date dataRecebimento) {
		this.tipoExame = tipoExame;
		this.numeroExame = numeroExame;
		this.exameSeq = exameSeq;
		this.patologistaResponsavel = patologistaResponsavel;
		this.dataRecebimento = dataRecebimento;
	}

	public ExameAndamentoVO() {

	}

	public Long getExameSeq() {
		return exameSeq;
	}

	public void setExameSeq(Long exameSeq) {
		this.exameSeq = exameSeq;
	}

	public void setSelecionado(boolean selecionado) {
		this.selecionado = selecionado;
	}

	public boolean isSelecionado() {
		return selecionado;
	}

	public String getExame() {
		return exame;
	}

	public void setExame(String exame) {
		this.exame = exame;
	}

	public Long getNumeroExame() {
		return numeroExame;
	}

	public void setNumeroExame(Long numeroExame) {
		this.numeroExame = numeroExame;
	}

	public String getPatologista() {
		return patologista;
	}

	public void setPatologista(String patologista) {
		this.patologista = patologista;
	}

	public Date getDataRecebimento() {
		return dataRecebimento;
	}

	public void setDataRecebimento(Date dataRecebimento) {
		this.dataRecebimento = dataRecebimento;
	}

	public List<AelItemSolicitacaoExames> getItensSolicitacaoExame() {
		return itensSolicitacaoExame;
	}

	public void setItensSolicitacaoExame(List<AelItemSolicitacaoExames> itensSolicitacaoExame) {
		this.itensSolicitacaoExame = itensSolicitacaoExame;
	}

	public AelPatologista getPatologistaResponsavel() {
		return patologistaResponsavel;
	}

	public void setPatologistaResponsavel(AelPatologista patologistaResponsavel) {
		this.patologistaResponsavel = patologistaResponsavel;
	}

	public AelConfigExLaudoUnico getTipoExame() {
		return tipoExame;
	}

	public void setTipoExame(AelConfigExLaudoUnico tipoExame) {
		this.tipoExame = tipoExame;
	}

	public void setAelPatologistas(List<AelPatologista> aelPatologistas) {
		this.aelPatologistas = aelPatologistas;
	}

	public List<AelPatologista> getAelPatologistas() {
		return aelPatologistas;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((numeroExame == null) ? 0 : numeroExame.hashCode());
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
		ExameAndamentoVO other = (ExameAndamentoVO) obj;
		if (numeroExame == null) {
			if (other.numeroExame != null) {
				return false;
			}
		} else {
			if (!numeroExame.equals(other.numeroExame)) {
				return false;
			}
		}
		return true;
	}

	@Override
	public int compareTo(ExameAndamentoVO o) {
		if (o == null) {
			return -1;
		}
		if (this.getNumeroExame() == null && o.getNumeroExame() == null) {
			return 0;
		}
		if (o.getNumeroExame() == null) {
			return -1;
		}
		if (this.getNumeroExame() == null) {
			return 1;
		}

		return this.getNumeroExame().intValue() - o.getNumeroExame().intValue();
	}

	public void setLumSeq(Long lumSeq) {
		this.lumSeq = lumSeq;
	}

	public Long getLumSeq() {
		return lumSeq;
	}
}