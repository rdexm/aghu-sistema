package br.gov.mec.aghu.model;


import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.core.persistence.BaseEntityId;


/**
 * The persistent class for the sce_pacote_materiais database table.
 * 
 */
@Entity
@Table(name="SCE_PACOTE_MATERIAIS")
public class ScePacoteMateriais extends BaseEntityId<ScePacoteMateriaisId> implements Serializable {
		/**
	 * 
	 */
	private static final long serialVersionUID = -1574522368414216174L;
	
	private ScePacoteMateriaisId id = new ScePacoteMateriaisId();
	private String descricao;
	private DominioSituacao indSituacao;
	private Integer version = Integer.valueOf(0);
	private Set<SceItemPacoteMateriais> itens = new HashSet<SceItemPacoteMateriais>();
	private SceAlmoxarifado almoxarifado = new SceAlmoxarifado();
	private FccCentroCustos centroCustoProprietario;
	private FccCentroCustos centroCustoAplicacao;

    public ScePacoteMateriais() {
    }

	@EmbeddedId
	public ScePacoteMateriaisId getId() {
		return this.id;
	}

	public void setId(ScePacoteMateriaisId id) {
		this.id = id;
	}

	public String getDescricao() {
		return this.descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	@Column(name="IND_SITUACAO", nullable = false, length = 1)
	@Enumerated(EnumType.STRING)
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

	//bi-directional many-to-one association to SceItemPacoteMateriais
	@OneToMany(mappedBy="pacoteMaterial", cascade={CascadeType.PERSIST, CascadeType.MERGE})
	public Set<SceItemPacoteMateriais> getItens() {
		return this.itens;
	}

	public void setItens(Set<SceItemPacoteMateriais> itens) {
		this.itens = itens;
	}
	
	//bi-directional many-to-one association to SceAlmoxarifados
    @ManyToOne
	@JoinColumn(name="ALM_SEQ")
	public SceAlmoxarifado getAlmoxarifado() {
		return this.almoxarifado;
	}

	public void setAlmoxarifado(SceAlmoxarifado almoxarifado) {
		this.almoxarifado = almoxarifado;
	}


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
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
		ScePacoteMateriais other = (ScePacoteMateriais) obj;
		if (id == null) {
			if (other.id != null) {
				return false;
			}
		} else if (!id.equals(other.id)) {
			return false;
		}
		return true;
	}
	
	public enum Fields {
		ID("id"),
		CODIGO_CENTRO_CUSTO_PROPRIETARIO("id.codigoCentroCustoProprietario"),
		CODIGO_CENTRO_CUSTO_APLICACAO("id.codigoCentroCustoAplicacao"),
		NUMERO("id.numero"),
		DESCRICAO("descricao"),
		IND_SITUACAO("indSituacao"),
		ALMOXARIFADO("almoxarifado"),
		ALMOXARIFADO_CODIGO("almoxarifado.seq"),
		CENTRO_CUSTO_PROPRIETARIO("centroCustoProprietario"),
		CENTRO_CUSTO_APLICACAO("centroCustoAplicacao");

		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return this.fields;
		}
	}
	
	/*@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "CCT_CODIGO", nullable = false, insertable=false, updatable=false)
	public FccCentroCustos getFccCentroCustos() {
		return fccCentroCustos;
	}


	public void setFccCentroCustos(FccCentroCustos fccCentroCustos) {
		this.fccCentroCustos = fccCentroCustos;
	}*/
	
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="CCT_CODIGO_REFERE",nullable = false, insertable = false, updatable = false)
	public FccCentroCustos getCentroCustoProprietario() {
		return this.centroCustoProprietario;
	}

	public void setCentroCustoProprietario(FccCentroCustos centroCustoProprietario) {
		this.centroCustoProprietario = centroCustoProprietario;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="CCT_CODIGO",nullable = false, insertable = false, updatable = false)
	public FccCentroCustos getCentroCustoAplicacao() {
		return this.centroCustoAplicacao;
	}

	public void setCentroCustoAplicacao(FccCentroCustos centroCustoAplicacao) {
		this.centroCustoAplicacao = centroCustoAplicacao;
	}
	
	
}