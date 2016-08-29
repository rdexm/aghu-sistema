package br.gov.mec.aghu.model;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Version;


import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.core.persistence.BaseEntity;


/**
 * The persistent class for the fso_convenios_financeiros database table.
 * 
 */
@SuppressWarnings({"PMD.AghuUsoIndevidoDaEnumDominioSimNaoEntity"})
@Entity
@Table(name="FSO_CONVENIOS_FINANCEIROS", schema="AGH")
public class FsoConveniosFinanceiro implements BaseEntity {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1286665137464680508L;

	@Id
	@SequenceGenerator(name="FSO_CONVENIOS_FINANCEIROS_CODIGO_GENERATOR", sequenceName="AGH.FSO_CVF_SQ1", allocationSize = 1)
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="FSO_CONVENIOS_FINANCEIROS_CODIGO_GENERATOR")	
	private Integer codigo;

	@Column(name="DESCRICAO", length=60)
	private String descricao;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="FRF_CODIGO", nullable=false)
	private FsoFontesRecursoFinanc fsoFontesRecursoFinanc;

	@Column(name="IND_CONV_ESPECIAL")
	@Enumerated(EnumType.STRING)
	private DominioSimNao indConvEspecial;

	@Column(name="IND_DET_PI", length=2)
	private String indDetPi;

	@Column(name="IND_SITUACAO")
	@Enumerated(EnumType.STRING)
	private DominioSituacao indSituacao;

	@Column(name = "VERSION", length= 7)
	@Version
	private Integer version;

	//bi-directional many-to-one association to ScoConvItensContrato
	@OneToMany(mappedBy="fsoConveniosFinanceiro")
	private List<ScoConvItensContrato> scoConvItensContratos;

    public FsoConveniosFinanceiro() {
    }

	public Integer getCodigo() {
		return this.codigo;
	}

	public void setCodigo(Integer codigo) {
		this.codigo = codigo;
	}

	public String getDescricao() {
		return this.descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}


	public DominioSimNao getIndConvEspecial() {
		return this.indConvEspecial;
	}

	public void setIndConvEspecial(DominioSimNao indConvEspecial) {
		this.indConvEspecial = indConvEspecial;
	}

	public String getIndDetPi() {
		return this.indDetPi;
	}

	public void setIndDetPi(String indDetPi) {
		this.indDetPi = indDetPi;
	}

	public DominioSituacao getIndSituacao() {
		return this.indSituacao;
	}

	public void setIndSituacao(DominioSituacao indSituacao) {
		this.indSituacao = indSituacao;
	}

	public Integer getVersion() {
		return this.version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}

	public List<ScoConvItensContrato> getScoConvItensContratos() {
		return this.scoConvItensContratos;
	}

	public void setScoConvItensContratos(List<ScoConvItensContrato> scoConvItensContratos) {
		this.scoConvItensContratos = scoConvItensContratos;
	}

	public FsoFontesRecursoFinanc getFsoFontesRecursoFinanc() {
		return fsoFontesRecursoFinanc;
	}

	public void setFsoFontesRecursoFinanc(
			FsoFontesRecursoFinanc fsoFontesRecursoFinanc) {
		this.fsoFontesRecursoFinanc = fsoFontesRecursoFinanc;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((codigo == null) ? 0 : codigo.hashCode());
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
		FsoConveniosFinanceiro other = (FsoConveniosFinanceiro) obj;
		if (codigo == null) {
			if (other.codigo != null) {
				return false;
			}
		} else if (!codigo.equals(other.codigo)) {
			return false;
		}
		return true;
	}

	public enum Fields {
	    		
	    CODIGO("codigo"),
	    SITUACAO("indSituacao"),
	    DESC("descricao");
	    
	    private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return fields;
		}
	}
	
}