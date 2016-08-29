package br.gov.mec.aghu.faturamento.vo;

import java.io.Serializable;
import java.math.BigDecimal;

@SuppressWarnings("PMD.CyclomaticComplexity")
public class PreviaDiariaFaturamentoVO implements Serializable {

	// br.gov.mec.aghu.faturamento.vo.PreviaDiariaFaturamentoVO

	private static final long serialVersionUID = 5010745881138939049L;

	private Integer codCentroCusto;

	private String descCentroCusto;

	private Short codUnidade;

	private String descUnidade;

	private String generica;

	private String agenda;

	private Long ssm;

	private String descrSSM;

	private Integer phi;

	private String descrPhi;

	private Integer fcfSeq;

	private String financiamento;

	private Integer fccSeq;

	private String complexibilidade;

	private BigDecimal totalAnestesia;

	private BigDecimal totalServProf;
	
	private Integer iphSeq;
	
	private Short iphPhoSeq;


	private String cctCodigo;
	private String cctCodigoDesc;
	private Short grupo;
	private String grupoDesc;
	private Byte subGrupo;
	private String subGrupoDesc;
	private Byte fogCod;
	private String fogDesc;
	private Integer quantidade;
	private BigDecimal total;
	private Long quantidadeTeto;
	private BigDecimal valorTeto;
	private Long diferencaQuantidadeTeto;
	private BigDecimal diferencaValorTeto;
	
	
	public enum Fields {
		COD_CENTRO_CUSTO("codCentroCusto"), 
		DESC_CENTRO_CUSTO("descCentroCusto"), 
		COD_UNIDADE("codUnidade"), 	
		DESC_UNIDADE("descUnidade"), 
		GENERICA("generica"), 
		AGENDA("agenda"),
		SUB_GRUPO("subGrupo"), 
		SUB_GRUPO_DESC("subGrupoDesc"),
		GRUPO("grupo"),
		GRUPO_DESC("grupoDesc"),
		SSM("ssm"), 
		DESCR_SSM("descrSSM"),
		PHI("phi"),
		DESCR_PHI("descrPhi"),
		FCF_SEQ("fcfSeq"), 
		FINANCIAMENTO("financiamento"),
		FCC_SEQ("fccSeq"),
		COMPLEXIBILIDADE("complexibilidade"),
		TOTAL_ANESTESIA("totalAnestesia"), 
		TOTAL_SERV_PROF("totalServProf"),
		TOTAL("total"),
		QUANTIDADE("quantidade"),
		FOG_COD("fogCod"),
		FOG_DESC("fogDesc"),
		IPH_SEQ("iphSeq"),
		IPH_PHO_SEQ("iphPhoSeq");

		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return fields;
		}
	}

	public Integer getCodCentroCusto() {
		return codCentroCusto;
	}

	public void setCodCentroCusto(Integer codCentroCusto) {
		this.codCentroCusto = codCentroCusto;
	}

	public String getDescCentroCusto() {
		return descCentroCusto;
	}

	public void setDescCentroCusto(String descCentroCusto) {
		this.descCentroCusto = descCentroCusto;
	}

	public Short getCodUnidade() {
		return codUnidade;
	}

	public void setCodUnidade(Short codUnidade) {
		this.codUnidade = codUnidade;
	}

	public String getDescUnidade() {
		return descUnidade;
	}

	public void setDescUnidade(String descUnidade) {
		this.descUnidade = descUnidade;
	}

	public String getGenerica() {
		return generica;
	}

	public void setGenerica(String generica) {
		this.generica = generica;
	}

	public String getAgenda() {
		return agenda;
	}

	public void setAgenda(String agenda) {
		this.agenda = agenda;
	}

	public Byte getSubGrupo() {
		return subGrupo;
	}

	public void setSubGrupo(Byte subGrupo) {
		this.subGrupo = subGrupo;
	}

	public Short getGrupo() {
		return grupo;
	}

	public void setGrupo(Short grupo) {
		this.grupo = grupo;
	}

	public Long getSsm() {
		return ssm;
	}

	public void setSsm(Long ssm) {
		this.ssm = ssm;
	}

	public String getDescrSSM() {
		return descrSSM;
	}

	public void setDescrSSM(String descrSSM) {
		this.descrSSM = descrSSM;
	}

	public Integer getPhi() {
		return phi;
	}

	public void setPhi(Integer phi) {
		this.phi = phi;
	}

	public String getDescrPhi() {
		return descrPhi;
	}

	public void setDescrPhi(String descrPhi) {
		this.descrPhi = descrPhi;
	}

	public Integer getFcfSeq() {
		return fcfSeq;
	}

	public void setFcfSeq(Integer fcfSeq) {
		this.fcfSeq = fcfSeq;
	}

	public String getFinanciamento() {
		return financiamento;
	}

	public void setFinanciamento(String financiamento) {
		this.financiamento = financiamento;
	}

	public Integer getFccSeq() {
		return fccSeq;
	}

	public void setFccSeq(Integer fccSeq) {
		this.fccSeq = fccSeq;
	}

	public String getComplexibilidade() {
		return complexibilidade;
	}

	public void setComplexibilidade(String complexibilidade) {
		this.complexibilidade = complexibilidade;
	}

	public BigDecimal getTotalAnestesia() {
		return totalAnestesia;
	}

	public void setTotalAnestesia(BigDecimal totalAnestesia) {
		this.totalAnestesia = totalAnestesia;
	}

	public BigDecimal getTotalServProf() {
		return totalServProf;
	}

	public void setTotalServProf(BigDecimal totalServProf) {
		this.totalServProf = totalServProf;
	}

	public BigDecimal getTotal() {
		return total;
	}

	public void setTotal(BigDecimal total) {
		this.total = total;
	}

	public Integer getQuantidade() {
		return quantidade;
	}

	public void setQuantidade(Integer quantidade) {
		this.quantidade = quantidade;
	}

	public String getSubGrupoDesc() {
		return subGrupoDesc;
	}

	public void setSubGrupoDesc(String subGrupoDesc) {
		this.subGrupoDesc = subGrupoDesc;
	}

	@Override
	public String toString() {
		return "PreviaDiariaFaturamentoVO [codCentroCusto=" + codCentroCusto
				+ ", descCentroCusto=" + descCentroCusto + ", codUnidade="
				+ codUnidade + ", descUnidade=" + descUnidade + ", generica="
				+ generica + ", agenda=" + agenda + ", fogCod=" + fogCod
				+ ", subGrupo=" + subGrupo + ", grupo=" + grupo + ", ssm="
				+ ssm + ", descrSSM=" + descrSSM + ", phi=" + phi
				+ ", descrPhi=" + descrPhi + ", fcfSeq=" + fcfSeq
				+ ", financiamento=" + financiamento + ", fccSeq=" + fccSeq
				+ ", complexibilidade=" + complexibilidade + "]";
	}

	@Override
	@SuppressWarnings("PMD.NPathComplexity")
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((agenda == null) ? 0 : agenda.hashCode());
		result = prime * result
				+ ((codCentroCusto == null) ? 0 : codCentroCusto.hashCode());
		result = prime * result
				+ ((codUnidade == null) ? 0 : codUnidade.hashCode());
		result = prime
				* result
				+ ((complexibilidade == null) ? 0 : complexibilidade.hashCode());
		result = prime * result
				+ ((descCentroCusto == null) ? 0 : descCentroCusto.hashCode());
		result = prime * result
				+ ((descUnidade == null) ? 0 : descUnidade.hashCode());
		result = prime * result
				+ ((descrPhi == null) ? 0 : descrPhi.hashCode());
		result = prime * result
				+ ((descrSSM == null) ? 0 : descrSSM.hashCode());
		result = prime * result + ((fccSeq == null) ? 0 : fccSeq.hashCode());
		result = prime * result + ((fcfSeq == null) ? 0 : fcfSeq.hashCode());
		result = prime * result
				+ ((financiamento == null) ? 0 : financiamento.hashCode());
		result = prime * result
				+ ((fogCod == null) ? 0 : fogCod.hashCode());
		result = prime * result
				+ ((generica == null) ? 0 : generica.hashCode());
		result = prime * result + ((grupo == null) ? 0 : grupo.hashCode());
		result = prime * result + ((phi == null) ? 0 : phi.hashCode());
		result = prime * result + ((ssm == null) ? 0 : ssm.hashCode());
		result = prime * result
				+ ((subGrupo == null) ? 0 : subGrupo.hashCode());
		result = prime * result
				+ ((subGrupoDesc == null) ? 0 : subGrupoDesc.hashCode());
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
		PreviaDiariaFaturamentoVO other = (PreviaDiariaFaturamentoVO) obj;
		if (agenda == null) {
			if (other.agenda != null) {
				return false;
			}
		} else if (!agenda.equals(other.agenda)) {
			return false;
		}
		if (codCentroCusto == null) {
			if (other.codCentroCusto != null) {
				return false;
			}
		} else if (!codCentroCusto.equals(other.codCentroCusto)) {
			return false;
		}
		if (codUnidade == null) {
			if (other.codUnidade != null) {
				return false;
			}
		} else if (!codUnidade.equals(other.codUnidade)) {
			return false;
		}
		if (complexibilidade == null) {
			if (other.complexibilidade != null) {
				return false;
			}
		} else if (!complexibilidade.equals(other.complexibilidade)) {
			return false;
		}
		if (descCentroCusto == null) {
			if (other.descCentroCusto != null) {
				return false;
			}
		} else if (!descCentroCusto.equals(other.descCentroCusto)) {
			return false;
		}
		if (descUnidade == null) {
			if (other.descUnidade != null) {
				return false;
			}
		} else if (!descUnidade.equals(other.descUnidade)) {
			return false;
		}
		if (descrPhi == null) {
			if (other.descrPhi != null) {
				return false;
			}
		} else if (!descrPhi.equals(other.descrPhi)) {
			return false;
		}
		if (descrSSM == null) {
			if (other.descrSSM != null) {
				return false;
			}
		} else if (!descrSSM.equals(other.descrSSM)) {
			return false;
		}
		if (fccSeq == null) {
			if (other.fccSeq != null) {
				return false;
			}
		} else if (!fccSeq.equals(other.fccSeq)) {
			return false;
		}
		if (fcfSeq == null) {
			if (other.fcfSeq != null) {
				return false;
			}
		} else if (!fcfSeq.equals(other.fcfSeq)) {
			return false;
		}
		if (financiamento == null) {
			if (other.financiamento != null) {
				return false;
			}
		} else if (!financiamento.equals(other.financiamento)) {
			return false;
		}
		if (fogCod == null) {
			if (other.fogCod != null) {
				return false;
			}
		} else if (!fogCod.equals(other.fogCod)) {
			return false;
		}
		if (generica == null) {
			if (other.generica != null) {
				return false;
			}
		} else if (!generica.equals(other.generica)) {
			return false;
		}
		if (grupo == null) {
			if (other.grupo != null) {
				return false;
			}
		} else if (!grupo.equals(other.grupo)) {
			return false;
		}
		if (phi == null) {
			if (other.phi != null) {
				return false;
			}
		} else if (!phi.equals(other.phi)) {
			return false;
		}
		if (ssm == null) {
			if (other.ssm != null) {
				return false;
			}
		} else if (!ssm.equals(other.ssm)) {
			return false;
		}
		if (subGrupo == null) {
			if (other.subGrupo != null) {
				return false;
			}
		} else if (!subGrupo.equals(other.subGrupo)) {
			return false;
		}
		if (subGrupoDesc == null) {
			if (other.subGrupoDesc != null) {
				return false;
			}
		} else if (!subGrupoDesc.equals(other.subGrupoDesc)) {
			return false;
		}
		return true;
	}

	public String getGrupoDesc() {
		return grupoDesc;
	}

	public void setGrupoDesc(String grupoDesc) {
		this.grupoDesc = grupoDesc;
	}

	public Byte getFogCod() {
		return fogCod;
	}

	public void setFogCod(Byte fogCod) {
		this.fogCod = fogCod;
	}

	public String getFogDesc() {
		return fogDesc;
	}

	public void setFogDesc(String fogDesc) {
		this.fogDesc = fogDesc;
	}

	public String getCctCodigo() {
		return cctCodigo;
	}

	public void setCctCodigo(String cctCodigo) {
		this.cctCodigo = cctCodigo;
	}

	public String getCctCodigoDesc() {
		return cctCodigoDesc;
	}

	public void setCctCodigoDesc(String cctCodigoDesc) {
		this.cctCodigoDesc = cctCodigoDesc;
	}

	public Short getIphPhoSeq() {
		return iphPhoSeq;
	}

	public void setIphPhoSeq(Short iphPhoSeq) {
		this.iphPhoSeq = iphPhoSeq;
	}

	public Long getQuantidadeTeto() {
		return quantidadeTeto;
	}

	public void setQuantidadeTeto(Long quantidadeTeto) {
		this.quantidadeTeto = quantidadeTeto;
	}

	public BigDecimal getValorTeto() {
		return valorTeto;
	}

	public void setValorTeto(BigDecimal valorTeto) {
		this.valorTeto = valorTeto;
	}

	public Long getDiferencaQuantidadeTeto() {
		return diferencaQuantidadeTeto;
	}

	public void setDiferencaQuantidadeTeto(Long diferencaQuantidadeTeto) {
		this.diferencaQuantidadeTeto = diferencaQuantidadeTeto;
	}

	public BigDecimal getDiferencaValorTeto() {
		return diferencaValorTeto;
	}

	public void setDiferencaValorTeto(BigDecimal diferencaValorTeto) {
		this.diferencaValorTeto = diferencaValorTeto;
	}

	public Integer getIphSeq() {
		return iphSeq;
	}

	public void setIphSeq(Integer iphSeq) {
		this.iphSeq = iphSeq;
	}

}