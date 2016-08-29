package br.gov.mec.aghu.procedimentoterapeutico.vo;

import java.util.List;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import br.gov.mec.aghu.core.commons.BaseBean;

public class AcomodacaoVO implements BaseBean {

	private static final long serialVersionUID = -3855553111743109511L;
	
	private Short loaSeq;
	private String descricaoLocal;
	private String corOcupacao;
	
	private List<HorarioAcomodacaoVO> listaHorariosReservados;
	private List<HorarioAcomodacaoVO> listaHorariosMarcados;
	private List<HorarioAcomodacaoVO> listaHorariosLivres;
	
	public Short getLoaSeq() {
		return loaSeq;
	}

	public void setLoaSeq(Short loaSeq) {
		this.loaSeq = loaSeq;
	}

	public String getDescricaoLocal() {
		return descricaoLocal;
	}

	public void setDescricaoLocal(String descricaoLocal) {
		this.descricaoLocal = descricaoLocal;
	}

	public String getCorOcupacao() {
		return corOcupacao;
	}

	public void setCorOcupacao(String corOcupacao) {
		this.corOcupacao = corOcupacao;
	}

	public List<HorarioAcomodacaoVO> getListaHorariosReservados() {
		return listaHorariosReservados;
	}

	public void setListaHorariosReservados(
			List<HorarioAcomodacaoVO> listaHorariosReservados) {
		this.listaHorariosReservados = listaHorariosReservados;
	}

	public List<HorarioAcomodacaoVO> getListaHorariosMarcados() {
		return listaHorariosMarcados;
	}

	public void setListaHorariosMarcados(
			List<HorarioAcomodacaoVO> listaHorariosMarcados) {
		this.listaHorariosMarcados = listaHorariosMarcados;
	}

	public List<HorarioAcomodacaoVO> getListaHorariosLivres() {
		return listaHorariosLivres;
	}

	public void setListaHorariosLivres(List<HorarioAcomodacaoVO> listaHorariosLivres) {
		this.listaHorariosLivres = listaHorariosLivres;
	}

	public enum Fields {

		LOA_SEQ("loaSeq"), 
		DESCRICAO_LOCAL("descricaoLocal");

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
        
        hashCodeBuilder.append(this.loaSeq);
        hashCodeBuilder.append(this.descricaoLocal);
        
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
        
        AcomodacaoVO other = (AcomodacaoVO) obj;
        
        EqualsBuilder umEqualsBuilder = new EqualsBuilder();
        umEqualsBuilder.append(this.loaSeq, other.loaSeq);
        umEqualsBuilder.append(this.descricaoLocal, other.descricaoLocal);
        
        return umEqualsBuilder.isEquals();
    }
}
