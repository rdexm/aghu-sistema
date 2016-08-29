package br.gov.mec.aghu.casca.model;



import java.util.Date;

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
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;


import org.hibernate.annotations.Immutable;
import org.hibernate.validator.constraints.Length;

import br.gov.mec.aghu.dominio.DominioTipoAcesso;
import br.gov.mec.aghu.core.persistence.BaseEntityId;

/**
 * 
 * @author aghu
 * 
 */
@Entity
@Immutable
@SequenceGenerator(name = "cscAcessoSeq", sequenceName = "casca.casca_acesso_sq1", allocationSize = 1)
@org.hibernate.annotations.AccessType("field")
@Table(name = "CSC_ACESSO", schema = "CASCA")
public class Acesso extends BaseEntityId<Integer> {

	private static final long serialVersionUID = 2457154613604440377L;

	public Acesso() {
	}

	public Acesso(Usuario usuario, String enderecoOrigem, Boolean autorizado,
			Date data, String observacao, DominioTipoAcesso tipoAcesso) {
		super();
		this.usuario = usuario;
		this.enderecoOrigem = enderecoOrigem;
		this.autorizado = autorizado;
		this.data = data;
		this.observacao = observacao;
		this.tipoAcesso = tipoAcesso;
	}

	@Id
	@Column(name = "ID", nullable = false, precision = 8, scale = 0)
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "cscAcessoSeq")
	private Integer id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "ID_USUARIO")
	private Usuario usuario;

	@Column(name = "ENDERECO_ORIGEM", nullable = false, length = 250, updatable = false)
	@Length(max = 250)
	private String enderecoOrigem;

	@Column(name = "AUTORIZADO", nullable = false, updatable = false)
	private Boolean autorizado;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "DATA")
	private Date data;

	@Column(name = "OBSERVACAO", nullable = false, length = 250, updatable = false)
	@Length(max = 250)
	private String observacao;
	
	@Column(name = "TIPO", nullable = false, updatable = false)
	@Enumerated(EnumType.STRING)
	private DominioTipoAcesso tipoAcesso;

	@Override
	public Integer getId() {
		return this.id;
	}

	public Usuario getUsuario() {
		return usuario;
	}

	public String getEnderecoOrigem() {
		return enderecoOrigem;
	}

	public Boolean getAutorizado() {
		return autorizado;
	}

	public Date getData() {
		return data;
	}

	public String getObservacao() {
		return observacao;
	}

	public enum Fields {
		USUARIO("usuario"), ENDERECO_ORIGEM("enderecoOrigem"), OBSERVACAO(
				"observacao"), AUTORIZADO("autorizado"), DATA("data"), TIPO_ACESSO("tipoAcesso");
		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return this.fields;
		}
	}

	public DominioTipoAcesso getTipoAcesso() {
		return tipoAcesso;
	}

	@Override
	public void setId(Integer id) {
		this.id = id;
		
	}

	

}