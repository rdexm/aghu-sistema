package br.gov.mec.aghu.paciente.prontuarioonline.vo;

import java.util.Date;

import br.gov.mec.aghu.dominio.DominioIndRespProc;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioSituacaoAgendas;
import br.gov.mec.aghu.dominio.DominioSituacaoCirurgia;
import br.gov.mec.aghu.dominio.DominioSituacaoCirurgiaEAgenda;
import br.gov.mec.aghu.core.commons.BaseBean;
import br.gov.mec.aghu.core.utils.StringUtil;

@SuppressWarnings("PMD.CyclomaticComplexity")
public class CirurgiasInternacaoPOLVO implements BaseBean {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 15443534L;
	/**
	 * 
	 */
	
	private Boolean habilitaBotaoMotivoCancel;
	private Boolean botaoDocAssinado;
	private Boolean assinalaRadio;
	private Integer seq;
	private Date data;
	private String descricao;
	private String especialidade;
	private String equipe;
	private DominioSituacaoCirurgia situacao;	
	private DominioSituacaoCirurgiaEAgenda situacaoCirurgiaEAgenda;
	private DominioSituacaoAgendas indSituacao;
	private Boolean temDescricao;
	private Boolean digitaNotaSala;
	private DominioIndRespProc indRespProc;
	private DominioSimNao indResponsavel;
	private String motivoCancelamento;
	private Integer eprPciSeq;
	private Short eprEspSeq;
	private String idRadio;
	private Integer atdSeq;
	private String pacOruAccNummer;
	
	
	
	public enum Fields {
	  SEQ("seq"), 
	  DATA("data"),
	  DESCRICAO("descricao"),
	  ESPECIALIDADE("especialidade"),	 
	  EQUIPE("equipe"),
	  SITUACAO("situacao"),
	  IND_SITUACAO("indSituacao"),
	  SITUACAO_CIRURGIA_AGENDA("situacaoCirurgiaEAgenda"),
	  TEM_DESCRICAO("temDescricao"),
	  DIGITA_NOTA_SALA("digitaNotaSala"),
	  IND_RESP_PROC("indRespProc"),
	  IND_RESPONSAVEL("indResponsavel"),
	  EPR_PCI_SEQ("eprPciSeq"),
	  EPR_ESP_SEQ("eprEspSeq"),	  
	  MOTIVO_CANCELAMENTO("motivoCancelamento"),
	  ATD_SEQ("atdSeq");

		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return this.fields;
		}

	}
			
	
//	public String getEspecialidadeEditadaTrunc(Long size) {
//		
//		if(size != null && especialidade != null && especialidade.length() > size) {
//			return especialidade.substring(0,size.intValue()-2) + "...";
//		} else {
//			return especialidade;
//		}
//	}
	
	public String getEspecialidadeEditadaTrunc(Long size) {
		return StringUtil.trunc(especialidade, true, size);
	}
	
	
	public String getEquipeEditadaTrunc(Long size) {
		return StringUtil.trunc(equipe, true, size);
	}
	
	
	public String getDescricaoEditadaTrunc(Long size) {
		return StringUtil.trunc(descricao, true, size);
	}


	public Integer getSeq() {
		return seq;
	}


	public void setSeq(Integer seq) {
		this.seq = seq;
	}	

	public Date getData() {
		return data;
	}


	public void setData(Date data) {
		this.data = data;
	}


	public String getEspecialidade() {
		return especialidade;
	}


	public void setEspecialidade(String especialidade) {
		this.especialidade = especialidade;
	}


	public String getEquipe() {
		return equipe;
	}


	public void setEquipe(String equipe) {
		this.equipe = equipe;
	}

	
	public String getDescricao() {
		return descricao;
	}


	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public Boolean getTemDescricao() {
		return temDescricao;
	}

	public void setTemDescricao(Boolean temDescricao) {
		this.temDescricao = temDescricao;
	}

	public Boolean getDigitaNotaSala() {
		return digitaNotaSala;
	}

	public void setDigitaNotaSala(Boolean digitaNotaSala) {
		this.digitaNotaSala = digitaNotaSala;
	}

	public DominioIndRespProc getIndRespProc() {
		return indRespProc;
	}

	public void setIndRespProc(DominioIndRespProc indRespProc) {
		this.indRespProc = indRespProc;
	}

	public DominioSimNao getIndResponsavel() {
		return indResponsavel;
	}

	public void setIndResponsavel(DominioSimNao indResponsavel) {
		this.indResponsavel = indResponsavel;
	}

	public Boolean getAssinalaRadio() {
		return assinalaRadio;
	}

	public void setAssinalaRadio(Boolean assinalaRadio) {
		this.assinalaRadio = assinalaRadio;
	}

	public String getMotivoCancelamento() {
		return motivoCancelamento;
	}

	public void setMotivoCancelamento(String motivoCancelamento) {
		this.motivoCancelamento = motivoCancelamento;
	}

	public Boolean getHabilitaBotaoMotivoCancel() {
		return habilitaBotaoMotivoCancel;
	}

	public void setHabilitaBotaoMotivoCancel(Boolean habilitaBotaoMotivoCancel) {
		this.habilitaBotaoMotivoCancel = habilitaBotaoMotivoCancel;
	}


	public Integer getEprPciSeq() {
		return eprPciSeq;
	}


	public void setEprPciSeq(Integer eprPciSeq) {
		this.eprPciSeq = eprPciSeq;
	}


	public Short getEprEspSeq() {
		return eprEspSeq;
	}


	public void setEprEspSeq(Short eprEspSeq) {
		this.eprEspSeq = eprEspSeq;
	}


	public String getIdRadio() {
		return idRadio;
	}


	public void setIdRadio(String idRadio) {
		this.idRadio = idRadio;
	}


	public DominioSituacaoCirurgia getSituacao() {
		return situacao;
	}


	public void setSituacao(DominioSituacaoCirurgia situacao) {
		this.situacao = situacao;
	}


	public DominioSituacaoCirurgiaEAgenda getSituacaoCirurgiaEAgenda() {
		return situacaoCirurgiaEAgenda;
	}


	public void setSituacaoCirurgiaEAgenda(
			DominioSituacaoCirurgiaEAgenda situacaoCirurgiaEAgenda) {
		this.situacaoCirurgiaEAgenda = situacaoCirurgiaEAgenda;
	}


	public DominioSituacaoAgendas getIndSituacao() {
		return indSituacao;
	}


	public void setIndSituacao(DominioSituacaoAgendas indSituacao) {
		this.indSituacao = indSituacao;
	}


	public Boolean getBotaoDocAssinado() {
		return botaoDocAssinado;
	}


	public void setBotaoDocAssinado(Boolean botaoDocAssinado) {
		this.botaoDocAssinado = botaoDocAssinado;
	}


	public Integer getAtdSeq() {
		return atdSeq;
	}


	public void setAtdSeq(Integer atdSeq) {
		this.atdSeq = atdSeq;
	}


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((assinalaRadio == null) ? 0 : assinalaRadio.hashCode());
		result = prime
				* result
				+ ((botaoDocAssinado == null) ? 0 : botaoDocAssinado.hashCode());
		result = prime * result + ((data == null) ? 0 : data.hashCode());
		result = prime * result
				+ ((descricao == null) ? 0 : descricao.hashCode());
		result = prime * result
				+ ((digitaNotaSala == null) ? 0 : digitaNotaSala.hashCode());
		result = prime * result
				+ ((eprEspSeq == null) ? 0 : eprEspSeq.hashCode());
		result = prime * result
				+ ((eprPciSeq == null) ? 0 : eprPciSeq.hashCode());
		result = prime * result + ((equipe == null) ? 0 : equipe.hashCode());
		result = prime * result
				+ ((especialidade == null) ? 0 : especialidade.hashCode());
		result = prime
				* result
				+ ((habilitaBotaoMotivoCancel == null) ? 0
						: habilitaBotaoMotivoCancel.hashCode());
		result = prime * result + ((idRadio == null) ? 0 : idRadio.hashCode());
		result = prime * result
				+ ((indRespProc == null) ? 0 : indRespProc.hashCode());
		result = prime * result
				+ ((indResponsavel == null) ? 0 : indResponsavel.hashCode());
		result = prime * result
				+ ((indSituacao == null) ? 0 : indSituacao.hashCode());
		result = prime
				* result
				+ ((motivoCancelamento == null) ? 0 : motivoCancelamento
						.hashCode());
		result = prime * result + ((seq == null) ? 0 : seq.hashCode());
		result = prime * result
				+ ((situacao == null) ? 0 : situacao.hashCode());
		result = prime
				* result
				+ ((situacaoCirurgiaEAgenda == null) ? 0
						: situacaoCirurgiaEAgenda.hashCode());
		result = prime * result
				+ ((temDescricao == null) ? 0 : temDescricao.hashCode());
		return result;
	}


	@Override
	@SuppressWarnings("PMD.CyclomaticComplexity")
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof CirurgiasInternacaoPOLVO)) {
			return false;
		}
		CirurgiasInternacaoPOLVO other = (CirurgiasInternacaoPOLVO) obj;
		if (assinalaRadio == null) {
			if (other.assinalaRadio != null) {
				return false;
			}
		} else if (!assinalaRadio.equals(other.assinalaRadio)) {
			return false;
		}
		if (botaoDocAssinado == null) {
			if (other.botaoDocAssinado != null) {
				return false;
			}
		} else if (!botaoDocAssinado.equals(other.botaoDocAssinado)) {
			return false;
		}
		if (data == null) {
			if (other.data != null) {
				return false;
			}
		} else if (!data.equals(other.data)) {
			return false;
		}
		if (descricao == null) {
			if (other.descricao != null) {
				return false;
			}
		} else if (!descricao.equals(other.descricao)) {
			return false;
		}
		if (digitaNotaSala == null) {
			if (other.digitaNotaSala != null) {
				return false;
			}
		} else if (!digitaNotaSala.equals(other.digitaNotaSala)) {
			return false;
		}
		if (eprEspSeq == null) {
			if (other.eprEspSeq != null) {
				return false;
			}
		} else if (!eprEspSeq.equals(other.eprEspSeq)) {
			return false;
		}
		if (eprPciSeq == null) {
			if (other.eprPciSeq != null) {
				return false;
			}
		} else if (!eprPciSeq.equals(other.eprPciSeq)) {
			return false;
		}
		if (equipe == null) {
			if (other.equipe != null) {
				return false;
			}
		} else if (!equipe.equals(other.equipe)) {
			return false;
		}
		if (especialidade == null) {
			if (other.especialidade != null) {
				return false;
			}
		} else if (!especialidade.equals(other.especialidade)) {
			return false;
		}
		if (habilitaBotaoMotivoCancel == null) {
			if (other.habilitaBotaoMotivoCancel != null) {
				return false;
			}
		} else if (!habilitaBotaoMotivoCancel
				.equals(other.habilitaBotaoMotivoCancel)) {
			return false;
		}
		if (idRadio == null) {
			if (other.idRadio != null) {
				return false;
			}
		} else if (!idRadio.equals(other.idRadio)) {
			return false;
		}
		if (indRespProc != other.indRespProc) {
			return false;
		}
		if (indResponsavel != other.indResponsavel) {
			return false;
		}
		if (indSituacao != other.indSituacao) {
			return false;
		}
		if (motivoCancelamento == null) {
			if (other.motivoCancelamento != null) {
				return false;
			}
		} else if (!motivoCancelamento.equals(other.motivoCancelamento)) {
			return false;
		}
		if (seq == null) {
			if (other.seq != null) {
				return false;
			}
		} else if (!seq.equals(other.seq)) {
			return false;
		}
		if (situacao != other.situacao) {
			return false;
		}
		if (situacaoCirurgiaEAgenda != other.situacaoCirurgiaEAgenda) {
			return false;
		}
		if (temDescricao == null) {
			if (other.temDescricao != null) {
				return false;
			}
		} else if (!temDescricao.equals(other.temDescricao)) {
			return false;
		}
		return true;
	}

	public void setPacOruAccNummer(String pacOruAccNummer) {
		this.pacOruAccNummer = pacOruAccNummer;
	}

	public String getPacOruAccNummer() {
		return pacOruAccNummer;
	}


	

		

}
	