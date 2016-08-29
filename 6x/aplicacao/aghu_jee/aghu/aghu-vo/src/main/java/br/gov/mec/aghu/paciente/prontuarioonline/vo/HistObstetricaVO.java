package br.gov.mec.aghu.paciente.prontuarioonline.vo;

import java.util.Date;

import br.gov.mec.aghu.dominio.DominioOrigemAtendimento;
import br.gov.mec.aghu.model.McoGestacoes;
import br.gov.mec.aghu.core.commons.BaseBean;

public class HistObstetricaVO implements BaseBean{

	private static final long serialVersionUID = 3228271479512325263L;
	
	private Date dthrConsulta;
	private McoGestacoes mcoGestacoes;
	private Integer seqAtendimento;
	private DominioOrigemAtendimento origemAtendimento;
	
	private Integer gsoPacCodigo;
	private Short gsoSeqp;
	private Integer conNumero;
	
	private Boolean assinalaRadio = false;
	
	public HistObstetricaVO(Date dthrConsulta, McoGestacoes mcoGestacoes,
			Integer seqAtendimento, DominioOrigemAtendimento origemAtendimento,
			Integer gsoPacCodigo, Short gsoSeqp, Integer conNumero) {
		super();
		this.dthrConsulta = dthrConsulta;
		this.mcoGestacoes = mcoGestacoes;
		this.seqAtendimento = seqAtendimento;
		this.origemAtendimento = origemAtendimento;
		this.gsoPacCodigo = gsoPacCodigo;
		this.gsoSeqp = gsoSeqp;
		this.conNumero = conNumero;
	}

	public enum Fields {
		  GESTACAO("mcoGestacoes"),
		  DATA_CONSULTA("dthrConsulta"),
		  SEQ_ATENDIMENTO("seqAtendimento"),
		  ORIGEM_ATENDIMENTO("origemAtendimento"),

		  CODIGO_PACIENTE("gsoPacCodigo"), 
		  SEQUENCE("gsoSeqp"),          
		  NUMERO_CONSULTA("conNumero"),;
		  

			private String fields;

			private Fields(String fields) {
				this.fields = fields;
			}

			@Override
			public String toString() {
				return this.fields;
			}

		}

	public McoGestacoes getMcoGestacoes() {
		return mcoGestacoes;
	}

	public void setMcoGestacoes(McoGestacoes mcoGestacoes) {
		this.mcoGestacoes = mcoGestacoes;
	}

	public Date getDthrConsulta() {
		return dthrConsulta;
	}

	public void setDthrConsulta(Date dthrConsulta) {
		this.dthrConsulta = dthrConsulta;
	}


	public void setAssinalaRadio(Boolean assinalaRadio) {
		this.assinalaRadio = assinalaRadio;
	}

	public Boolean getAssinalaRadio() {
		return assinalaRadio;
	}

	public Integer getSeqAtendimento() {
		return seqAtendimento;
	}

	public void setSeqAtendimento(Integer seqAtendimento) {
		this.seqAtendimento = seqAtendimento;
	}

	public DominioOrigemAtendimento getOrigemAtendimento() {
		return origemAtendimento;
	}

	public void setOrigemAtendimento(DominioOrigemAtendimento origemAtendimento) {
		this.origemAtendimento = origemAtendimento;
	}

	public Integer getGsoPacCodigo() {
		return gsoPacCodigo;
	}

	public void setGsoPacCodigo(Integer gsoPacCodigo) {
		this.gsoPacCodigo = gsoPacCodigo;
	}

	public Short getGsoSeqp() {
		return gsoSeqp;
	}

	public void setGsoSeqp(Short gsoSeqp) {
		this.gsoSeqp = gsoSeqp;
	}

	public Integer getConNumero() {
		return conNumero;
	}

	public void setConNumero(Integer conNumero) {
		this.conNumero = conNumero;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((conNumero == null) ? 0 : conNumero.hashCode());
		result = prime * result
				+ ((gsoPacCodigo == null) ? 0 : gsoPacCodigo.hashCode());
		result = prime * result + ((gsoSeqp == null) ? 0 : gsoSeqp.hashCode());
		result = prime * result
				+ ((seqAtendimento == null) ? 0 : seqAtendimento.hashCode());
		return result;
	}

	@Override
	@SuppressWarnings("PMD.NPathComplexity")
	public boolean equals(Object obj) {
		if (this == obj){
			return true;
		}	
		if (obj == null){
			return false;
		}	
		if (!(obj instanceof HistObstetricaVO)){
			return false;
		}	
		HistObstetricaVO other = (HistObstetricaVO) obj;
		if (conNumero == null) {
			if (other.conNumero != null){
				return false;
			}	
		} else if (!conNumero.equals(other.conNumero)){
			return false;
		}	
		if (gsoPacCodigo == null) {
			if (other.gsoPacCodigo != null){
				return false;
			}	
		} else if (!gsoPacCodigo.equals(other.gsoPacCodigo)){
			return false;
		}	
		if (gsoSeqp == null) {
			if (other.gsoSeqp != null){
				return false;
			}	
		} else if (!gsoSeqp.equals(other.gsoSeqp)){
			return false;
		}	
		if (seqAtendimento == null) {
			if (other.seqAtendimento != null){
				return false;
			}	
		} else if (!seqAtendimento.equals(other.seqAtendimento)){
			return false;
		}	
		return true;
	}
}
