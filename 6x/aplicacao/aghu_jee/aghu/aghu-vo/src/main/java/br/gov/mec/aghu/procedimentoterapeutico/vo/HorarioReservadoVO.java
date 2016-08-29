package br.gov.mec.aghu.procedimentoterapeutico.vo;

import java.util.Date;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import br.gov.mec.aghu.dominio.DominioConformidadeHorarioSessao;
import br.gov.mec.aghu.protocolos.vo.CadIntervaloTempoVO;
import br.gov.mec.aghu.core.commons.BaseBean;

public class HorarioReservadoVO implements BaseBean {

	private static final long serialVersionUID = -6656970892219141474L;

	private Short seq;
	private Short agsSeq;
	private Short dia;
	private Date dataInicio;
	private Date dataFim;
	private Date tempo;
	private Short ciclo;
	private String consultasAmb;
	private Integer pacCodigo;
	private String protocolo;
	private String observacao;
	private boolean conforme;
	private DominioConformidadeHorarioSessao conformidade;
	private CadIntervaloTempoVO horarioPrescricao;
	
	public enum Fields {

		SEQ("seq"), 
		AGS_SEQ("agsSeq"),
		DIA("dia"),
		DATA_INICIO("dataInicio"),
		DATA_FIM("dataFim"),
		TEMPO("tempo"),
		CICLO("ciclo"),
		CONSULTAS_AMB("consultasAmb"),
		PAC_CODIGO("pacCodigo"),
		PROTOCOLO("protocolo"),
		OBSERVACAO("observacao"),
		CONFORME("conforme"),
		CONFORMIDADE("conformidade");

		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return fields;
		}
	}
	
	@Override
    public int hashCode() {

		HashCodeBuilder hashCodeBuilder = new HashCodeBuilder();
        
        hashCodeBuilder.append(this.seq);
        hashCodeBuilder.append(this.agsSeq);
        hashCodeBuilder.append(this.dia);
        hashCodeBuilder.append(this.dataInicio);
        hashCodeBuilder.append(this.dataFim);
        hashCodeBuilder.append(this.tempo);
        hashCodeBuilder.append(this.ciclo);
        hashCodeBuilder.append(this.consultasAmb);
        hashCodeBuilder.append(this.pacCodigo);
        hashCodeBuilder.append(this.protocolo);
        
        return hashCodeBuilder.toHashCode();
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

        HorarioReservadoVO other = (HorarioReservadoVO) obj;

        EqualsBuilder umEqualsBuilder = new EqualsBuilder();
        umEqualsBuilder.append(this.seq, other.seq);
        umEqualsBuilder.append(this.agsSeq, other.agsSeq);
        umEqualsBuilder.append(this.dia, other.dia);
        umEqualsBuilder.append(this.dataInicio, other.dataInicio);
        umEqualsBuilder.append(this.dataFim, other.dataFim);
        umEqualsBuilder.append(this.tempo, other.tempo);
        umEqualsBuilder.append(this.ciclo, other.ciclo);
        umEqualsBuilder.append(this.consultasAmb, other.consultasAmb);
        umEqualsBuilder.append(this.pacCodigo, other.pacCodigo);
        umEqualsBuilder.append(this.protocolo, other.protocolo);
        
        return umEqualsBuilder.isEquals();
    }

	public Short getSeq() {
		return seq;
	}

	public void setSeq(Short seq) {
		this.seq = seq;
	}

	public Short getAgsSeq() {
		return agsSeq;
	}

	public void setAgsSeq(Short agsSeq) {
		this.agsSeq = agsSeq;
	}

	public Short getDia() {
		return dia;
	}

	public void setDia(Short dia) {
		this.dia = dia;
	}

	public Date getDataInicio() {
		return dataInicio;
	}

	public void setDataInicio(Date dataInicio) {
		this.dataInicio = dataInicio;
	}

	public Date getDataFim() {
		return dataFim;
	}

	public void setDataFim(Date dataFim) {
		this.dataFim = dataFim;
	}

	public Date getTempo() {
		return tempo;
	}

	public void setTempo(Date tempo) {
		this.tempo = tempo;
	}

	public Short getCiclo() {
		return ciclo;
	}

	public void setCiclo(Short ciclo) {
		this.ciclo = ciclo;
	}

	public String getConsultasAmb() {
		return consultasAmb;
	}

	public void setConsultasAmb(String consultasAmb) {
		this.consultasAmb = consultasAmb;
	}

	public Integer getPacCodigo() {
		return pacCodigo;
	}

	public void setPacCodigo(Integer pacCodigo) {
		this.pacCodigo = pacCodigo;
	}

	public String getProtocolo() {
		return protocolo;
	}

	public void setProtocolo(String protocolo) {
		this.protocolo = protocolo;
	}

	public String getObservacao() {
		return observacao;
	}

	public void setObservacao(String observacao) {
		this.observacao = observacao;
	}

	public boolean isConforme() {
		return conforme;
	}

	public void setConforme(boolean conforme) {
		this.conforme = conforme;
	}

	public DominioConformidadeHorarioSessao getConformidade() {
		return conformidade;
	}

	public void setConformidade(DominioConformidadeHorarioSessao conformidade) {
		this.conformidade = conformidade;
	}

	public CadIntervaloTempoVO getHorarioPrescricao() {
		return horarioPrescricao;
	}

	public void setHorarioPrescricao(CadIntervaloTempoVO horarioPrescricao) {
		this.horarioPrescricao = horarioPrescricao;
	}

}
