package br.gov.mec.aghu.casca.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;


import org.hibernate.annotations.Immutable;
import org.hibernate.validator.constraints.Length;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.core.model.BaseJournal;

@Entity
@Table(name = "CSC_PERFIL_JN", schema = "CASCA")
@SequenceGenerator(name = "cscPerfJnSeq", sequenceName = "casca.casca_perfil_jn_sq1", allocationSize = 1)
@Immutable
public class PerfilJn extends BaseJournal implements java.io.Serializable {
	private static final long serialVersionUID = -3831159300179061818L;

	private Integer id;
	private String nome;
	private String descricao;
	private String descricaoResumida;
	private DominioSituacao situacao;
	private boolean delegavel;

	@Id
	@Column(name = "SEQ_JN", unique = true, nullable = false)
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "cscPerfJnSeq")
	@Override
	public Integer getSeqJn() {
		return super.getSeqJn();
	}

	@Column(name = "ID", nullable = false, precision = 8, scale = 0)
	public Integer getId() {
		return this.id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	@Column(name = "NOME", nullable = false, length = 500)
	@Length(max = 500, message = "Nome do perfil pode conter no máximo 500 caracteres")
	public String getNome() {
		return this.nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	@Column(name = "DESCRICAO", nullable = false, length = 1000)
	@Length(min = 5, max = 1000, message = "Descrição do perfil deve ter no mínimo 5 e no máximo 1000 caracteres")
	public String getDescricao() {
		return this.descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	@Column(name = "DESCRICAO_RESUMIDA", length = 250)
	@Length(max = 250, message = "Descrição resumida do perfil pode conter no máximo 250 caracteres")
	public String getDescricaoResumida() {
		return this.descricaoResumida;
	}

	public void setDescricaoResumida(String descricaoResumida) {
		this.descricaoResumida = descricaoResumida;
	}

	@Column(name = "SITUACAO", nullable = false, length = 1)
	@Enumerated(EnumType.STRING)
	public DominioSituacao getSituacao() {
		return this.situacao;
	}

	public void setSituacao(DominioSituacao situacao) {
		this.situacao = situacao;
	}
	
	@Column(name = "DELEGAVEL", nullable = false)
	public boolean isDelegavel() {
		return delegavel;
	}

	public void setDelegavel(boolean delegavel) {
		this.delegavel = delegavel;
	}
	
	public enum Fields {
		SEQ_JN("seqJn"),
		ID("id"),
		NOME("nome"),
		NOME_USUARIO("nomeUsuario"),
		DATA_ALTERACAO("dataAlteracao"),
		OPERACAO("operacao"),
		DELEGAVEL("delegavel"),
		;
		
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
