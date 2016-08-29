package br.gov.mec.aghu.casca.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import br.gov.mec.aghu.core.persistence.BaseEntityId;

@Entity
@Table(name = "CSC_DASHBOARD", schema = "CASCA")
@SequenceGenerator(name="cscDshSeq", sequenceName="AGH.CASCA_DSH_SEQ", allocationSize = 1)
public class Dashboard extends BaseEntityId<Integer> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1801003472808726034L;
	
	private Integer id;
	private Usuario usuario;
	private Favorito favorito;
	private String modal;
	private Date dataCriacao;
	private Date dataAtualizacao;
	private Integer ordem;

	@Id
	@Column(name = "ID", nullable = false, precision = 8, scale = 0)
	@GeneratedValue(strategy=GenerationType.AUTO, generator="cscDshSeq")
	public Integer getId() {
		return id;
	}
	
	public void setId(Integer id) {
		this.id = id;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "ID_USUARIO", nullable = false, updatable = false)
	public Usuario getUsuario() {
		return this.usuario;
	}

	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "ID_FAVORITO")
	public Favorito getFavorito() {
		return favorito;
	}

	public void setFavorito(Favorito favorito) {
		this.favorito = favorito;
	}

	@Column(name = "MODAL")
	public String getModal() {
		return modal;
	}

	public void setModal(String modal) {
		this.modal = modal;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "DATA_CRIACAO", nullable = false)
	public Date getDataCriacao() {
		return this.dataCriacao;
	}

	public void setDataCriacao(Date dataCriacao) {
		this.dataCriacao = dataCriacao;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "DATA_ATUALIZACAO")
	public Date getDataAtualizacao() {
		return this.dataAtualizacao;
	}

	public void setDataAtualizacao(Date dataAtualizacao) {
		this.dataAtualizacao = dataAtualizacao;
	}

	@Column(name = "ORDEM", nullable = false, precision = 8, scale = 0)
	public Integer getOrdem() {
		return this.ordem;
	}

	public void setOrdem(Integer ordem) {
		this.ordem = ordem;
	}
	
	public enum Fields {
		ID("id"), USUARIO("usuario"), ORDEM("ordem"), FAVORITO("favorito"), MODAL("modal"), DATA_CRIACAO("dataCriacao");
		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return this.fields;
		}
	}
}
