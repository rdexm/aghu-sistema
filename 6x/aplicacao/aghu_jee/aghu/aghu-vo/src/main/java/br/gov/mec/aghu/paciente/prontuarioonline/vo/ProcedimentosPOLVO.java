package br.gov.mec.aghu.paciente.prontuarioonline.vo;

import java.util.Date;

import br.gov.mec.aghu.dominio.DominioIndRespProc;
import br.gov.mec.aghu.dominio.DominioSituacaoCirurgia;
import br.gov.mec.aghu.core.persistence.BaseEntity;
import br.gov.mec.aghu.core.utils.StringUtil;

@SuppressWarnings("PMD.CyclomaticComplexity")
public class ProcedimentosPOLVO implements BaseEntity {
	
	private static final long serialVersionUID = -3166719409328915909L;
	
	private Integer pacCodigo;
	private Date data;
	private String descricao;
	private String tipo;
	private DominioSituacaoCirurgia situacao;		
	private Integer seq;// seq da Cirurgia
	private Integer eprPciSeq; // seq do Procedimento
	private String pacOruAccNummer;
	private Integer soeSeq;// seq da Solicitacao Exame
	private Short soeSeqP;// seqP da Solicitacao Exame
	private Integer agdSeq;// seq da Agenda
	
	private Boolean temDescricao;
	private Boolean digitaNotaSala;
	private DominioIndRespProc indRespProc;
	
	private String motivoCancelamento;
	
	private Boolean habilitaBotaoMotivoCancel;
	private Boolean assinalaRadio;
	
	private Integer id;
	private Integer atdSeq;
	
	
	public enum Fields {
	  PAC_CODIGO("pacCodigo"),
	  DATA("data"),
	  DESCRICAO("descricao"),
	  TIPO("tipo"),
	  SITUACAO("situacao"),
	  SEQ("seq"), 
	  EPR_PCI_SEQ("eprPciSeq"),
	  PAC_ORU_ACC_NUMMER("pacOruAccNummer"),
	  SOE_SEQ("soeSeq"),
	  SOE_SEQP("soeSeqP"),
	  AGD_SEQ("agdSeq"),

	  TEM_DESCRICAO("temDescricao"),
	  DIGITA_NOTA_SALA("digitaNotaSala"),
	  IND_RESP_PROC("indRespProc"),

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

	public void setIndRespProc(DominioIndRespProc indRespProc) {
		this.indRespProc = indRespProc;
	}


	public DominioIndRespProc getIndRespProc() {
		return indRespProc;
	}


	public DominioSituacaoCirurgia getSituacao() {
		return situacao;
	}

	public void setSituacao(DominioSituacaoCirurgia situacao) {
		this.situacao = situacao;
	}

	public Boolean getAssinalaRadio() {
		return assinalaRadio;
	}

	public void setAssinalaRadio(Boolean assinalaRadio) {
		this.assinalaRadio = assinalaRadio;
	}

	public void setPacCodigo(Integer pacCodigo) {
		this.pacCodigo = pacCodigo;
	}


	public Integer getPacCodigo() {
		return pacCodigo;
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

	public void setTipo(String tipo) {
		this.tipo = tipo;
	}


	public String getTipo() {
		return tipo;
	}


	public void setEprPciSeq(Integer eprPciSeq) {
		this.eprPciSeq = eprPciSeq;
	}


	public void setPacOruAccNummer(String pacOruAccNummer) {
		this.pacOruAccNummer = pacOruAccNummer;
	}


	public String getPacOruAccNummer() {
		return pacOruAccNummer;
	}


	public Integer getEprPciSeq() {
		return eprPciSeq;
	}

	public Integer getSoeSeq() {
		return soeSeq;
	}


	public void setSoeSeq(Integer soeSeq) {
		this.soeSeq = soeSeq;
	}


	public Short getSoeSeqP() {
		return soeSeqP;
	}


	public void setSoeSeqP(Short soeSeqP) {
		this.soeSeqP = soeSeqP;
	}


	public Integer getAgdSeq() {
		return agdSeq;
	}


	public void setAgdSeq(Integer agdSeq) {
		this.agdSeq = agdSeq;
	}


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((agdSeq == null) ? 0 : agdSeq.hashCode());
		result = prime * result
				+ ((assinalaRadio == null) ? 0 : assinalaRadio.hashCode());
		result = prime * result + ((data == null) ? 0 : data.hashCode());
		result = prime * result
				+ ((descricao == null) ? 0 : descricao.hashCode());
		result = prime * result
				+ ((digitaNotaSala == null) ? 0 : digitaNotaSala.hashCode());
		result = prime * result
				+ ((eprPciSeq == null) ? 0 : eprPciSeq.hashCode());
		result = prime
				* result
				+ ((habilitaBotaoMotivoCancel == null) ? 0
						: habilitaBotaoMotivoCancel.hashCode());
		result = prime * result
				+ ((indRespProc == null) ? 0 : indRespProc.hashCode());
		result = prime
				* result
				+ ((motivoCancelamento == null) ? 0 : motivoCancelamento
						.hashCode());
		result = prime * result
				+ ((pacCodigo == null) ? 0 : pacCodigo.hashCode());
		result = prime * result
				+ ((pacOruAccNummer == null) ? 0 : pacOruAccNummer.hashCode());
		result = prime * result + ((seq == null) ? 0 : seq.hashCode());
		result = prime * result
				+ ((situacao == null) ? 0 : situacao.hashCode());
		result = prime * result + ((soeSeq == null) ? 0 : soeSeq.hashCode());
		result = prime * result + ((soeSeqP == null) ? 0 : soeSeqP.hashCode());
		result = prime * result
				+ ((temDescricao == null) ? 0 : temDescricao.hashCode());
		result = prime * result + ((tipo == null) ? 0 : tipo.hashCode());
		return result;
	}


	@Override
	@SuppressWarnings("PMD.ExcessiveMethodLength")
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
		ProcedimentosPOLVO other = (ProcedimentosPOLVO) obj;
		if (agdSeq == null) {
			if (other.agdSeq != null) {
				return false;
			}
		} else if (!agdSeq.equals(other.agdSeq)) {
			return false;
		}
		if (assinalaRadio == null) {
			if (other.assinalaRadio != null) {
				return false;
			}
		} else if (!assinalaRadio.equals(other.assinalaRadio)) {
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
		if (eprPciSeq == null) {
			if (other.eprPciSeq != null) {
				return false;
			}
		} else if (!eprPciSeq.equals(other.eprPciSeq)) {
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
		if (indRespProc != other.indRespProc) {
			return false;
		}
		if (motivoCancelamento == null) {
			if (other.motivoCancelamento != null) {
				return false;
			}
		} else if (!motivoCancelamento.equals(other.motivoCancelamento)) {
			return false;
		}
		if (pacCodigo == null) {
			if (other.pacCodigo != null) {
				return false;
			}
		} else if (!pacCodigo.equals(other.pacCodigo)) {
			return false;
		}
		if (pacOruAccNummer == null) {
			if (other.pacOruAccNummer != null) {
				return false;
			}
		} else if (!pacOruAccNummer.equals(other.pacOruAccNummer)) {
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
		if (soeSeq == null) {
			if (other.soeSeq != null) {
				return false;
			}
		} else if (!soeSeq.equals(other.soeSeq)) {
			return false;
		}
		if (soeSeqP == null) {
			if (other.soeSeqP != null) {
				return false;
			}
		} else if (!soeSeqP.equals(other.soeSeqP)) {
			return false;
		}
		if (temDescricao == null) {
			if (other.temDescricao != null) {
				return false;
			}
		} else if (!temDescricao.equals(other.temDescricao)) {
			return false;
		}
		if (tipo == null) {
			if (other.tipo != null) {
				return false;
			}
		} else if (!tipo.equals(other.tipo)) {
			return false;
		}
		return true;
	}


	public Integer getId() {
		return id;
	}


	public void setId(Integer id) {
		this.id = id;
	}


	public Integer getAtdSeq() {
		return atdSeq;
	}


	public void setAtdSeq(Integer atdSeq) {
		this.atdSeq = atdSeq;
	}






}