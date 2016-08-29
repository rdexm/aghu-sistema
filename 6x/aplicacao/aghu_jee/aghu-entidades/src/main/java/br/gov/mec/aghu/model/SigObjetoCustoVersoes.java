package br.gov.mec.aghu.model;

import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.persistence.Version;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import br.gov.mec.aghu.dominio.DominioRepasse;
import br.gov.mec.aghu.dominio.DominioSituacaoVersoesCustos;
import br.gov.mec.aghu.dominio.DominioTipoObjetoCustoCcts;
import br.gov.mec.aghu.core.persistence.BaseEntitySeq;

@Entity
@SequenceGenerator(name = "sigOcvSq1", sequenceName = "SIG_OCV_SQ1", allocationSize = 1)
@Table(name = "SIG_OBJETO_CUSTO_VERSOES", schema = "AGH")
public class SigObjetoCustoVersoes extends BaseEntitySeq<Integer> implements Serializable {

	private static final long serialVersionUID = 6861878683792306737L;

	private Integer seq;
	private SigObjetoCustos sigObjetoCustos;
	private Integer nroVersao;
	private Date dataInicio;
	private Date dataFim;
	private Date criadoEm;
	private RapServidores rapServidores;
	private DominioSituacaoVersoesCustos indSituacao;
	private DominioRepasse indRepasse;
	private Integer version;

	private SigObjetoCustoCcts sigObjetoCustoCctsPrincipal;
	private Set<SigObjetoCustoCcts> listObjetoCustoCcts = new HashSet<SigObjetoCustoCcts>(0);
	private Set<SigObjetoCustoComposicoes> listObjetoCustoComposicoes = new HashSet<SigObjetoCustoComposicoes>(0);
	private Set<SigObjetoCustoPhis> listObjetoCustoPhis = new HashSet<SigObjetoCustoPhis>(0);
	private Set<SigObjetoCustoDirRateios> listObjetoCustoDirRateios = new HashSet<SigObjetoCustoDirRateios>(0);
	private Set<SigObjetoCustoClientes> listObjetoCustoClientes = new HashSet<SigObjetoCustoClientes>(0);
	private Set<SigCalculoObjetoCusto> listCalculoObjetoCusto = new HashSet<SigCalculoObjetoCusto>(0);
	private Set<SigDetalheProducao> listDetalheProducao = new HashSet<SigDetalheProducao>(0);

	public SigObjetoCustoVersoes(){
		
	}
	
	public SigObjetoCustoVersoes(Integer seq){
		this.seq = seq;
	}
	
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "sigOcvSq1")
	@Column(name = "SEQ", unique = true, nullable = false)
	public Integer getSeq() {
		return seq;
	}

	public void setSeq(Integer seq) {
		this.seq = seq;
	}

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "OBJ_SEQ", referencedColumnName = "SEQ")
	public SigObjetoCustos getSigObjetoCustos() {
		return sigObjetoCustos;
	}

	public void setSigObjetoCustos(SigObjetoCustos sigObjetoCustos) {
		this.sigObjetoCustos = sigObjetoCustos;
	}

	@Column(name = "NRO_VERSAO", nullable = false)
	public Integer getNroVersao() {
		return nroVersao;
	}

	public void setNroVersao(Integer nroVersao) {
		this.nroVersao = nroVersao;
	}

	@Temporal(TemporalType.DATE)
	@Column(name = "DATA_INICIO", nullable = true)
	public Date getDataInicio() {
		return dataInicio;
	}

	public void setDataInicio(Date dataInicio) {
		this.dataInicio = dataInicio;
	}

	@Temporal(TemporalType.DATE)
	@Column(name = "DATA_FIM", nullable = true)
	public Date getDataFim() {
		return dataFim;
	}

	public void setDataFim(Date dataFim) {
		this.dataFim = dataFim;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CRIADO_EM", nullable = false)
	public Date getCriadoEm() {
		return criadoEm;
	}

	public void setCriadoEm(Date criadoEm) {
		this.criadoEm = criadoEm;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns({ @JoinColumn(name = "SER_MATRICULA", referencedColumnName = "MATRICULA"),
			@JoinColumn(name = "SER_VIN_CODIGO", referencedColumnName = "VIN_CODIGO") })
	public RapServidores getRapServidores() {
		return rapServidores;
	}

	public void setRapServidores(RapServidores rapServidores) {
		this.rapServidores = rapServidores;
	}

	@Column(name = "IND_SITUACAO", nullable = false, length = 1)
	@Enumerated(EnumType.STRING)
	public DominioSituacaoVersoesCustos getIndSituacao() {
		return indSituacao;
	}

	public void setIndSituacao(DominioSituacaoVersoesCustos indSituacao) {
		this.indSituacao = indSituacao;
	}
	
	@Column(name = "IND_REPASSE", nullable = true, length = 1)
	@Enumerated(EnumType.STRING)
	public DominioRepasse getIndRepasse() {
		return indRepasse;
	}

	public void setIndRepasse(DominioRepasse indRepasse) {
		this.indRepasse = indRepasse;
	}

	@Column(name = "VERSION", nullable = false)
	@Version
	public Integer getVersion() {
		return version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "sigObjetoCustoVersoes")
	public Set<SigObjetoCustoCcts> getListObjetoCustoCcts() {
		return listObjetoCustoCcts;
	}

	public void setListObjetoCustoCcts(Set<SigObjetoCustoCcts> listObjetoCustoCcts) {
		this.listObjetoCustoCcts = listObjetoCustoCcts;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "sigObjetoCustoVersoes")
	public Set<SigObjetoCustoComposicoes> getListObjetoCustoComposicoes() {
		return listObjetoCustoComposicoes;
	}

	public void setListObjetoCustoComposicoes(Set<SigObjetoCustoComposicoes> listObjetoCustoComposicoes) {
		this.listObjetoCustoComposicoes = listObjetoCustoComposicoes;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "sigObjetoCustoVersoes")
	public Set<SigObjetoCustoPhis> getListObjetoCustoPhis() {
		return listObjetoCustoPhis;
	}

	public void setListObjetoCustoPhis(Set<SigObjetoCustoPhis> listObjetoCustoPhis) {
		this.listObjetoCustoPhis = listObjetoCustoPhis;
	}

	@Transient
	public SigObjetoCustoCcts getSigObjetoCustoCcts() {
		return this.getSigObjetoCustoCctsPrincipal();
	}

	@Transient
	public SigObjetoCustoCcts getSigObjetoCustoCctsPrincipal() {
		if (this.getListObjetoCustoCcts() != null && !this.getListObjetoCustoCcts().isEmpty()) {
			if (listObjetoCustoCcts.size() == 1) {
				this.setSigObjetoCustoCctsPrincipal(listObjetoCustoCcts.iterator().next());
			} else {
				for (SigObjetoCustoCcts objetoCustoCcts : listObjetoCustoCcts) {
					if (objetoCustoCcts.getIndTipo().equals(DominioTipoObjetoCustoCcts.P)) {
						this.setSigObjetoCustoCctsPrincipal(objetoCustoCcts);
					}
				}
			}
		}

		if (this.sigObjetoCustoCctsPrincipal == null) {
			FccCentroCustos fccCentroCustos = new FccCentroCustos();
			SigCentroProducao sigCentroProducao = new SigCentroProducao();
			SigObjetoCustoCcts sigObjetoCustoCcts = new SigObjetoCustoCcts();
			fccCentroCustos.setCentroProducao(sigCentroProducao);
			sigObjetoCustoCcts.setFccCentroCustos(fccCentroCustos);
			this.setSigObjetoCustoCctsPrincipal(sigObjetoCustoCcts);
		}

		return sigObjetoCustoCctsPrincipal;
	}

	public void setSigObjetoCustoCctsPrincipal(SigObjetoCustoCcts sigObjetoCustoCctsPrincipal) {
		this.sigObjetoCustoCctsPrincipal = sigObjetoCustoCctsPrincipal;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "sigObjetoCustoVersoes")
	public Set<SigCalculoObjetoCusto> getListCalculoObjetoCusto() {
		return listCalculoObjetoCusto;
	}

	public void setListCalculoObjetoCusto(Set<SigCalculoObjetoCusto> listCalculoObjetoCusto) {
		this.listCalculoObjetoCusto = listCalculoObjetoCusto;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "objetoCustoVersoes")
	public Set<SigObjetoCustoDirRateios> getListObjetoCustoDirRateios() {
		return listObjetoCustoDirRateios;
	}

	public void setListObjetoCustoDirRateios(Set<SigObjetoCustoDirRateios> listObjetoCustoDirRateios) {
		this.listObjetoCustoDirRateios = listObjetoCustoDirRateios;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "objetoCustoVersoes")
	public Set<SigObjetoCustoClientes> getListObjetoCustoClientes() {
		return listObjetoCustoClientes;
	}

	public void setListObjetoCustoClientes(Set<SigObjetoCustoClientes> listObjetoCustoClientes) {
		this.listObjetoCustoClientes = listObjetoCustoClientes;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "sigObjetoCustoVersoes")
	public Set<SigDetalheProducao> getListDetalheProducao() {
		return listDetalheProducao;
	}

	public void setListDetalheProducao(Set<SigDetalheProducao> listDetalheProducao) {
		this.listDetalheProducao = listDetalheProducao;
	}

	public enum Fields {

		SEQ("seq"),
		OBJETO_CUSTO("sigObjetoCustos"),
		OBJETO_CUSTO_SEQ("sigObjetoCustos.seq"),
		NRO_VERSAO("nroVersao"),
		DATA_INICIO("dataInicio"),
		DATA_FIM("dataFim"),
		CRIADO_EM("criadoEm"),
		SERVIDOR_RESPONSAVEL("rapServidores"),
		IND_SITUACAO("indSituacao"),
		IND_REPASSE("indRepasse"),
		OBJETO_CUSTO_CCTS("listObjetoCustoCcts"),
		COMPOSICOES("listObjetoCustoComposicoes"),
		PHI("listObjetoCustoPhis"),
		PHI_DETALHE_PRODUCAO("listObjetoCustoPhis.fatProcedHospInternos"),
		CALCULO_OBJETO_CUSTO("listCalculoObjetoCusto"),
		CLIENTES("listObjetoCustoClientes"),
		CLIENTES_CENTRO_CUSTO("listObjetoCustoClientes.centroCusto"),
		CLIENTES_DIRECIONADORES("listObjetoCustoClientes.direcionadores"),
		DIRECIONADOR_RATEIO("listObjetoCustoDirRateios"),
		DETALHE_PRODUCAO("listDetalheProducao");

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
	public boolean equals(Object other) {
		if (!(other instanceof SigObjetoCustoVersoes)) {
			return false;
		}
		SigObjetoCustoVersoes castOther = (SigObjetoCustoVersoes) other;
		return new EqualsBuilder().append(this.seq, castOther.getSeq()).isEquals();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(this.seq).toHashCode();
	}

}
