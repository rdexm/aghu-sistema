package br.gov.mec.aghu.emergencia.vo;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import br.gov.mec.aghu.dominio.DominioFormaOrelha;
import br.gov.mec.aghu.dominio.DominioFormacaoMamilo;
import br.gov.mec.aghu.dominio.DominioGlandulaMamaria;
import br.gov.mec.aghu.dominio.DominioPregasPlantares;
import br.gov.mec.aghu.dominio.DominioTexturaPele;
import br.gov.mec.aghu.registrocolaborador.vo.Servidor;
import br.gov.mec.aghu.core.commons.BaseBean;

public class CalculoCapurroVO implements BaseBean {
	
	private static final long serialVersionUID = -8687560206687110842L;
	
	private Integer pacCodigo;
	private Byte igSemanas;
	private Byte igDias;
	private DominioTexturaPele texturaPele;
	private DominioPregasPlantares pregasPlantares;
	private DominioFormacaoMamilo formacaoMamilo;
	private DominioFormaOrelha formaOrelha;
	private DominioGlandulaMamaria glandulaMamaria;
	private Integer serMatricula;
	private Short serVinCodigo;
	private Servidor elaborador;
	
	private boolean mesmoServidor;
	
	public Integer getPacCodigo() {
		return pacCodigo;
	}

	public void setPacCodigo(Integer pacCodigo) {
		this.pacCodigo = pacCodigo;
	}

	public Byte getIgSemanas() {
		return igSemanas;
	}

	public void setIgSemanas(Byte igSemanas) {
		this.igSemanas = igSemanas;
	}

	public Byte getIgDias() {
		return igDias;
	}

	public void setIgDias(Byte igDias) {
		this.igDias = igDias;
	}

	public DominioTexturaPele getTexturaPele() {
		return texturaPele;
	}

	public void setTexturaPele(DominioTexturaPele texturaPele) {
		this.texturaPele = texturaPele;
	}

	public DominioPregasPlantares getPregasPlantares() {
		return pregasPlantares;
	}

	public void setPregasPlantares(DominioPregasPlantares pregasPlantares) {
		this.pregasPlantares = pregasPlantares;
	}

	public DominioFormacaoMamilo getFormacaoMamilo() {
		return formacaoMamilo;
	}

	public void setFormacaoMamilo(DominioFormacaoMamilo formacaoMamilo) {
		this.formacaoMamilo = formacaoMamilo;
	}

	public DominioFormaOrelha getFormaOrelha() {
		return formaOrelha;
	}

	public void setFormaOrelha(DominioFormaOrelha formaOrelha) {
		this.formaOrelha = formaOrelha;
	}

	public DominioGlandulaMamaria getGlandulaMamaria() {
		return glandulaMamaria;
	}

	public void setGlandulaMamaria(DominioGlandulaMamaria glandulaMamaria) {
		this.glandulaMamaria = glandulaMamaria;
	}

	public Integer getSerMatricula() {
		return serMatricula;
	}

	public void setSerMatricula(Integer serMatricula) {
		this.serMatricula = serMatricula;
	}

	public Short getSerVinCodigo() {
		return serVinCodigo;
	}

	public void setSerVinCodigo(Short serVinCodigo) {
		this.serVinCodigo = serVinCodigo;
	}

	public Servidor getElaborador() {
		return elaborador;
	}

	public void setElaborador(Servidor elaborador) {
		this.elaborador = elaborador;
	}

	public boolean isMesmoServidor() {
		return mesmoServidor;
	}

	public void setMesmoServidor(boolean mesmoServidor) {
		this.mesmoServidor = mesmoServidor;
	}

	public enum Fields {
		PAC_CODIGO("pacCodigo"),
		IG_SEMANAS("igSemanas"),		
		IG_DIAS("igDias"),
		TEXTURA_PELE("texturaPele"),
		PREGAS_PLANTARES("pregasPlantares"),
		FORMACAO_MAMILO("formacaoMamilo"),
		FORMA_ORELHA("formaOrelha"),
		GLANDULA_MAMARIA("glandulaMamaria"),
		SER_MATRICULA("serMatricula"),
		SER_VIN_CODIGO("serVinCodigo");

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
        HashCodeBuilder umHashCodeBuilder = new HashCodeBuilder();
        umHashCodeBuilder.append(this.getPacCodigo());
        umHashCodeBuilder.append(this.getIgSemanas());
        umHashCodeBuilder.append(this.getIgDias());
        umHashCodeBuilder.append(this.getTexturaPele());
        umHashCodeBuilder.append(this.getPregasPlantares());
        umHashCodeBuilder.append(this.getFormacaoMamilo());
        umHashCodeBuilder.append(this.getFormaOrelha());
        umHashCodeBuilder.append(this.getGlandulaMamaria());
        umHashCodeBuilder.append(this.getSerMatricula());
        umHashCodeBuilder.append(this.getSerVinCodigo());
        umHashCodeBuilder.append(this.getElaborador());
        umHashCodeBuilder.append(this.isMesmoServidor());
        return umHashCodeBuilder.toHashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof CalculoCapurroVO)) {
            return false;
        }
        CalculoCapurroVO other = (CalculoCapurroVO) obj;
        EqualsBuilder umEqualsBuilder = new EqualsBuilder();
        umEqualsBuilder.append(this.getPacCodigo(), other.getPacCodigo());
        umEqualsBuilder.append(this.getIgSemanas(), other.getIgSemanas());
        umEqualsBuilder.append(this.getIgDias(), other.getIgDias());
        umEqualsBuilder.append(this.getTexturaPele(), other.getTexturaPele());
        umEqualsBuilder.append(this.getPregasPlantares(), other.getPregasPlantares());
        umEqualsBuilder.append(this.getFormacaoMamilo(), other.getFormacaoMamilo());
        umEqualsBuilder.append(this.getFormaOrelha(), other.getFormaOrelha());
        umEqualsBuilder.append(this.getGlandulaMamaria(), other.getGlandulaMamaria());
        umEqualsBuilder.append(this.getSerMatricula(), other.getSerMatricula());
        umEqualsBuilder.append(this.getSerVinCodigo(), other.getSerVinCodigo());
        umEqualsBuilder.append(this.getElaborador(), other.getElaborador());
        umEqualsBuilder.append(this.isMesmoServidor(), other.isMesmoServidor());
        return umEqualsBuilder.isEquals();
    }
}
