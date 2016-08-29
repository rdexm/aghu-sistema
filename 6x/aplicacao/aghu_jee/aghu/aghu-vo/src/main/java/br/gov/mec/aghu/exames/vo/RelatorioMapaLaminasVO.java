package br.gov.mec.aghu.exames.vo;

import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.utils.StringUtil;

@SuppressWarnings("PMD.CyclomaticComplexity")
public class RelatorioMapaLaminasVO {

	private Long lumSeq;
	private Integer seqResidente;
	private String residente;
	private Date data;
	private String siglaCesto;
	private String descricaoCesto;
	private Long numeroAp;
	private String numeroCapsula;
	private String numeroFragmento;
	private String coloracao;
	private String descricao;
	private String numeroApFormatado;
	private String materiais;
	private Long totCapsulas;
	private String observacao;
	
	public RelatorioMapaLaminasVO() {}

	public String getCesto() {
		if(getSiglaCesto() != null && getDescricaoCesto() != null){
			return getSiglaCesto() + " - " + getDescricaoCesto();
		}
		
		return null;
	}


	public Long getNumeroAp() {
		return numeroAp;
	}

	public void setNumeroAp(Long numeroAp) {
		if(numeroAp != null){
			setNumeroApFormatado(StringUtil.formataNumeroAp(numeroAp));
		}
		
		this.numeroAp = numeroAp;
	}


	public String getNumeroCapsula() {
		return numeroCapsula;
	}
	
	public void setNumeroCapsula(String numeroCapsula) {
		if(numeroCapsula != null){
			setTotCapsulas(numeroCapsula);
		}
		
		this.numeroCapsula = numeroCapsula;
	}
	
	public Long getTotCapsulas() {
		return totCapsulas;
	}

	private final Pattern p = Pattern.compile("[0-9]+");
	public void setTotCapsulas(String capsulas) {
		if(CoreUtil.isNumeroLong(capsulas)){
			setTotCapsulas(Long.valueOf(capsulas));
		
			
		// Temporário por erro de base.
		} else {
			Matcher m = p.matcher(capsulas);
			StringBuffer valor = new StringBuffer();
			while (m.find()) {
				valor.append(m.group());
			}
			
			if(valor != null){
				setTotCapsulas(Long.valueOf(valor.toString()));
			}
		}
	}
	
	public void setTotCapsulas(Long totCapsulas) {
		this.totCapsulas = totCapsulas;
	}


	public enum Fields {
		SEQ_RESIDENTE("seqResidente"),
		RESIDENTE("residente"),
		DATA("data"),
		SIGLA_CESTO("siglaCesto"),
		DESCRICAO_CESTO("descricaoCesto"),
		NUMERO_AP("numeroAp"),
		NUMERO_CAPSULA("numeroCapsula"),
		NUMERO_FRAGMENTO("numeroFragmento"),
		COLORACAO("coloracao"),
		DESCRICAO("descricao"),
		MATERIAIS("materiais"),
		LUM_SEQ("lumSeq"),
		OBSERVACAO("observacao")
		;

		private String fields;

		private Fields(final String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return fields;
		}
	}


	public Long getLumSeq() {
		return lumSeq;
	}

	public void setLumSeq(Long lumSeq) {
		this.lumSeq = lumSeq;
	}

	public Integer getSeqResidente() {
		return seqResidente;
	}

	public void setSeqResidente(Integer seqResidente) {
		this.seqResidente = seqResidente;
	}

	public String getResidente() {
		if(residente != null && residente.length() > 15){
			return residente.substring(0,15) + "...";
		} else {
			return residente;
		}
	}

	public void setResidente(String residente) {
		this.residente = residente;
	}

	public Date getData() {
		return data;
	}

	public void setData(Date data) {
		this.data = data;
	}

	public String getSiglaCesto() {
		return siglaCesto;
	}

	public void setSiglaCesto(String siglaCesto) {
		this.siglaCesto = siglaCesto;
	}

	public String getDescricaoCesto() {
		return descricaoCesto;
	}

	public void setDescricaoCesto(String descricaoCesto) {
		this.descricaoCesto = descricaoCesto;
	}

	public String getNumeroFragmento() {
		return numeroFragmento;
	}

	public void setNumeroFragmento(String numeroFragmento) {
		this.numeroFragmento = numeroFragmento;
	}

	public String getColoracao() {
		return coloracao;
	}

	public void setColoracao(String coloracao) {
		this.coloracao = coloracao;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public String getNumeroApFormatado() {
		return numeroApFormatado;
	}

	public void setNumeroApFormatado(String numeroApFormatado) {
		this.numeroApFormatado = numeroApFormatado;
	}

	public String getMateriais() {
		return materiais;
	}

	public void setMateriais(String materiais) {
		this.materiais = materiais;
	}

	@Override
	@SuppressWarnings("PMD.NPathComplexity")
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((coloracao == null) ? 0 : coloracao.hashCode());
		result = prime * result + ((data == null) ? 0 : data.hashCode());
		result = prime * result
				+ ((descricao == null) ? 0 : descricao.hashCode());
		result = prime * result
				+ ((descricaoCesto == null) ? 0 : descricaoCesto.hashCode());
		result = prime * result + ((lumSeq == null) ? 0 : lumSeq.hashCode());
		result = prime * result
				+ ((materiais == null) ? 0 : materiais.hashCode());
		result = prime * result
				+ ((numeroAp == null) ? 0 : numeroAp.hashCode());
		result = prime
				* result
				+ ((numeroApFormatado == null) ? 0 : numeroApFormatado
						.hashCode());
		result = prime * result
				+ ((numeroCapsula == null) ? 0 : numeroCapsula.hashCode());
		result = prime * result
				+ ((numeroFragmento == null) ? 0 : numeroFragmento.hashCode());
		result = prime * result
				+ ((residente == null) ? 0 : residente.hashCode());
		result = prime * result
				+ ((seqResidente == null) ? 0 : seqResidente.hashCode());
		result = prime * result
				+ ((siglaCesto == null) ? 0 : siglaCesto.hashCode());
		result = prime * result
				+ ((totCapsulas == null) ? 0 : totCapsulas.hashCode());
		result = prime * result
		+ ((observacao == null) ? 0 : observacao.hashCode());
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
		if (!(obj instanceof RelatorioMapaLaminasVO)) {
			return false;
		}
		RelatorioMapaLaminasVO other = (RelatorioMapaLaminasVO) obj;
		if (coloracao == null) {
			if (other.coloracao != null) {
				return false;
			}
		} else if (!coloracao.equals(other.coloracao)) {
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

		if (observacao == null) {
			if (other.observacao != null) {
				return false;
			}
		} else if (!observacao.equals(other.observacao)) {
			return false;
		}
		if (descricaoCesto == null) {
			if (other.descricaoCesto != null) {
				return false;
			}
		} else if (!descricaoCesto.equals(other.descricaoCesto)) {
			return false;
		}
		if (lumSeq == null) {
			if (other.lumSeq != null) {
				return false;
			}
		} else if (!lumSeq.equals(other.lumSeq)) {
			return false;
		}
		if (materiais == null) {
			if (other.materiais != null) {
				return false;
			}
		} else if (!materiais.equals(other.materiais)) {
			return false;
		}
		if (numeroAp == null) {
			if (other.numeroAp != null) {
				return false;
			}
		} else if (!numeroAp.equals(other.numeroAp)) {
			return false;
		}
		if (numeroApFormatado == null) {
			if (other.numeroApFormatado != null) {
				return false;
			}
		} else if (!numeroApFormatado.equals(other.numeroApFormatado)) {
			return false;
		}
		if (numeroCapsula == null) {
			if (other.numeroCapsula != null) {
				return false;
			}
		} else if (!numeroCapsula.equals(other.numeroCapsula)) {
			return false;
		}
		if (numeroFragmento == null) {
			if (other.numeroFragmento != null) {
				return false;
			}
		} else if (!numeroFragmento.equals(other.numeroFragmento)) {
			return false;
		}
		if (residente == null) {
			if (other.residente != null) {
				return false;
			}
		} else if (!residente.equals(other.residente)) {
			return false;
		}
		if (seqResidente == null) {
			if (other.seqResidente != null) {
				return false;
			}
		} else if (!seqResidente.equals(other.seqResidente)) {
			return false;
		}
		if (siglaCesto == null) {
			if (other.siglaCesto != null) {
				return false;
			}
		} else if (!siglaCesto.equals(other.siglaCesto)) {
			return false;
		}
		if (totCapsulas == null) {
			if (other.totCapsulas != null) {
				return false;
			}
		} else if (!totCapsulas.equals(other.totCapsulas)) {
			return false;
		}
		return true;
	}

	public String getObservacao() {
		return observacao;
	}

	public void setObservacao(String observacao) {
		this.observacao = observacao;
	}
}