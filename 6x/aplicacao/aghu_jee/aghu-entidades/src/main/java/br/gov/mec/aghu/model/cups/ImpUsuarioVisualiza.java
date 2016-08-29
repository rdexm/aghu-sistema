package br.gov.mec.aghu.model.cups;

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
import javax.persistence.UniqueConstraint;
import javax.persistence.Version;

import br.gov.mec.aghu.casca.model.Usuario;
import br.gov.mec.aghu.core.persistence.BaseEntityId;

/**
 * ImpUsuarioVisualiza
 */
@Entity
@Table(name = "IMP_USUARIO_VISUALIZA", schema = "AGH", uniqueConstraints = @UniqueConstraint(columnNames = "ID_USUARIO"))
@SequenceGenerator(name = "impUsuVisSq1", sequenceName = "AGH.IMP_USU_VIS_SQ1", allocationSize = 1)
public class ImpUsuarioVisualiza extends BaseEntityId<Integer> implements java.io.Serializable {

	private static final long serialVersionUID = 1702936986603902549L;
	
	private Integer id;
	private Usuario usuario;
	private Date dataInclusao;
	private Integer version;

	public ImpUsuarioVisualiza() {
	}

	public ImpUsuarioVisualiza(Integer id, Usuario usuario, Date dataInclusao) {
		this.id = id;
		this.usuario = usuario;
		this.dataInclusao = dataInclusao;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "impUsuVisSq1")
	@Column(name = "ID", unique = true, nullable = false)
	public Integer getId() {
		return this.id;
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

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "DATA_INCLUSAO", nullable = false)
	public Date getDataInclusao() {
		return dataInclusao;
	}

	public void setDataInclusao(Date dataInclusao) {
		this.dataInclusao = dataInclusao;
	}

	public enum Fields {
		ID_USUARIO("usuario");
		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return this.fields;
		}
	}

	@Version
	@Column(name = "version", nullable = false)	
	public Integer getVersion() {
		return version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}
}