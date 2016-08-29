package br.gov.mec.aghu.model;

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
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.hibernate.validator.constraints.Length;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.core.persistence.BaseEntitySeq;

@Entity
@SequenceGenerator(name="mciFpdSq1", sequenceName="AGH.MCI_FPD_SQ1", allocationSize = 1)
@Table(name = "MCI_FATOR_PREDISPONENTES", schema = "AGH")
public class MciFatorPredisponentes extends BaseEntitySeq<Short> implements java.io.Serializable {

    private static final long serialVersionUID = 1025124054615711039L;
    
	private Short seq;
	private Integer version;
	private String descricao;
	private Byte grauRisco;
	private DominioSituacao indSituacao;
	private RapServidores servidor;
	private RapServidores servidorMovimentado;
	private Date criadoEm;
	private Date alteradoEm;
	private Double pesoInicial;
	private Double pesoFinal;
	private Boolean indIsolamento;
	private String procedureNotificacaoExames;
	private Short cgpSeq;
	private Boolean indNotificacaoSms;
	private Boolean indUsoMascara;
	private Boolean indUsoAvental;
	private Boolean indTecnicaAsseptica;

	public MciFatorPredisponentes() {
	}

	public MciFatorPredisponentes(Short seq, String descricao, Byte grauRisco,
			DominioSituacao indSituacao, RapServidores servidor,
			Date criadoEm, Boolean indIsolamento,
			Boolean indNotificacaoSms, Boolean indUsoMascara,
			Boolean indUsoAvental, Boolean indTecnicaAsseptica) {
		this.seq = seq;
		this.descricao = descricao;
		this.grauRisco = grauRisco;
		this.indSituacao = indSituacao;
		this.servidor = servidor;
		this.criadoEm = criadoEm;
		this.indIsolamento = indIsolamento;
		this.indNotificacaoSms = indNotificacaoSms;
		this.indUsoMascara = indUsoMascara;
		this.indUsoAvental = indUsoAvental;
		this.indTecnicaAsseptica = indTecnicaAsseptica;
	}

	public MciFatorPredisponentes(Short seq, String descricao, Byte grauRisco,
			DominioSituacao indSituacao, RapServidores servidor,
			Date criadoEm, Date alteradoEm, RapServidores servidorMovimentado, Double pesoInicial,
			Double pesoFinal, Boolean indIsolamento,
			String procedureNotificacaoExames, Short cgpSeq,
			Boolean indNotificacaoSms, Boolean indUsoMascara,
			Boolean indUsoAvental, Boolean indTecnicaAsseptica) {
		this.seq = seq;
		this.descricao = descricao;
		this.grauRisco = grauRisco;
		this.indSituacao = indSituacao;
		this.servidor = servidor;
		this.criadoEm = criadoEm;
		this.alteradoEm = alteradoEm;
		this.servidorMovimentado = servidorMovimentado;
		this.pesoInicial = pesoInicial;
		this.pesoFinal = pesoFinal;
		this.indIsolamento = indIsolamento;
		this.procedureNotificacaoExames = procedureNotificacaoExames;
		this.cgpSeq = cgpSeq;
		this.indNotificacaoSms = indNotificacaoSms;
		this.indUsoMascara = indUsoMascara;
		this.indUsoAvental = indUsoAvental;
		this.indTecnicaAsseptica = indTecnicaAsseptica;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "mciFpdSq1")
	@Column(name = "SEQ", nullable = false, precision = 3, scale = 0)
	public Short getSeq() {
		return this.seq;
	}

	public void setSeq(Short seq) {
		this.seq = seq;
	}

	@Column(name = "DESCRICAO", nullable = false, length = 60)
	@Length(max = 60)
	public String getDescricao() {
		return this.descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	@Column(name = "GRAU_RISCO", nullable = false, precision = 2, scale = 0)
	public Byte getGrauRisco() {
		return this.grauRisco;
	}

	public void setGrauRisco(Byte grauRisco) {
		this.grauRisco = grauRisco;
	}

	@Column(name = "IND_SITUACAO", nullable = false)
	@Enumerated(EnumType.STRING)
	public DominioSituacao getIndSituacao() {
		return this.indSituacao;
	}

	public void setIndSituacao(DominioSituacao indSituacao) {
		this.indSituacao = indSituacao;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns( {
			@JoinColumn(name = "SER_MATRICULA", referencedColumnName = "MATRICULA", nullable = false),
			@JoinColumn(name = "SER_VIN_CODIGO", referencedColumnName = "VIN_CODIGO", nullable = false) })
	public RapServidores getServidor() {
		return this.servidor;
	}
	
	public void setServidor(RapServidores servidor) {
		this.servidor = servidor;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CRIADO_EM", nullable = false, length = 7)
	public Date getCriadoEm() {
		return this.criadoEm;
	}

	public void setCriadoEm(Date criadoEm) {
		this.criadoEm = criadoEm;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "ALTERADO_EM", length = 7)
	public Date getAlteradoEm() {
		return this.alteradoEm;
	}

	public void setAlteradoEm(Date alteradoEm) {
		this.alteradoEm = alteradoEm;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns( {
			@JoinColumn(name = "SER_MATRICULA_MOVIMENTADO", referencedColumnName = "MATRICULA", nullable = false),
			@JoinColumn(name = "SER_VIN_CODIGO_MOVIMENTADO", referencedColumnName = "VIN_CODIGO", nullable = false) })
	public RapServidores getServidorMovimentado() {
		return this.servidorMovimentado;
	}
	
	public void setServidorMovimentado(RapServidores servidorMovimentado) {
		this.servidorMovimentado = servidorMovimentado;
	}

	@Column(name = "PESO_INICIAL", precision = 6, scale = 3)
	public Double getPesoInicial() {
		return this.pesoInicial;
	}

	public void setPesoInicial(Double pesoInicial) {
		this.pesoInicial = pesoInicial;
	}

	@Column(name = "PESO_FINAL", precision = 6, scale = 3)
	public Double getPesoFinal() {
		return this.pesoFinal;
	}

	public void setPesoFinal(Double pesoFinal) {
		this.pesoFinal = pesoFinal;
	}

	@Column(name = "IND_ISOLAMENTO")
	@org.hibernate.annotations.Type(type = "br.gov.mec.aghu.core.persistence.type.BooleanUserType")
	public Boolean getIndIsolamento() {
		return this.indIsolamento;
	}

	public void setIndIsolamento(Boolean indIsolamento) {
		this.indIsolamento = indIsolamento;
	}

	@Column(name = "PROCEDURE_NOTIFICACAO_EXAMES", length = 60)
	@Length(max = 60)
	public String getProcedureNotificacaoExames() {
		return this.procedureNotificacaoExames;
	}

	public void setProcedureNotificacaoExames(String procedureNotificacaoExames) {
		this.procedureNotificacaoExames = procedureNotificacaoExames;
	}

	@Column(name = "CGP_SEQ", precision = 3, scale = 0)
	public Short getCgpSeq() {
		return this.cgpSeq;
	}

	public void setCgpSeq(Short cgpSeq) {
		this.cgpSeq = cgpSeq;
	}

	@Column(name = "IND_NOTIFICACAO_SMS", nullable = false, length = 1)
	@org.hibernate.annotations.Type(type = "br.gov.mec.aghu.core.persistence.type.BooleanUserType")
	public Boolean getIndNotificacaoSms() {
		return this.indNotificacaoSms;
	}

	public void setIndNotificacaoSms(Boolean indNotificacaoSms) {
		this.indNotificacaoSms = indNotificacaoSms;
	}

	@Column(name = "IND_USO_MASCARA", nullable = false, length = 1)
	@org.hibernate.annotations.Type(type = "br.gov.mec.aghu.core.persistence.type.BooleanUserType")
	public Boolean getIndUsoMascara() {
		return this.indUsoMascara;
	}

	public void setIndUsoMascara(Boolean indUsoMascara) {
		this.indUsoMascara = indUsoMascara;
	}

	@Column(name = "IND_USO_AVENTAL", nullable = false, length = 1)
	@org.hibernate.annotations.Type(type = "br.gov.mec.aghu.core.persistence.type.BooleanUserType")
	public Boolean getIndUsoAvental() {
		return this.indUsoAvental;
	}

	public void setIndUsoAvental(Boolean indUsoAvental) {
		this.indUsoAvental = indUsoAvental;
	}

	@Column(name = "IND_TECNICA_ASSEPTICA", nullable = false, length = 1)
	@org.hibernate.annotations.Type(type = "br.gov.mec.aghu.core.persistence.type.BooleanUserType")
	public Boolean getIndTecnicaAsseptica() {
		return this.indTecnicaAsseptica;
	}

	public void setIndTecnicaAsseptica(Boolean indTecnicaAsseptica) {
		this.indTecnicaAsseptica = indTecnicaAsseptica;
	}
	
	@Version
	@Column(name = "VERSION", nullable = false)
	public Integer getVersion() {
		return this.version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}

	public enum Fields {
		SEQ("seq"), PESO_INICIAL("pesoInicial"), PESO_FINAL("pesoFinal"), DESCRICAO("descricao"), SITUACAO("indSituacao"), GRAU_RISCO("grauRisco"),
		SERVIDOR("servidor"), SERVIDOR_MOVIMENTADO("servidorMovimentado");

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
		umHashCodeBuilder.append(this.getDescricao());
		umHashCodeBuilder.append(this.getSeq());
		umHashCodeBuilder.append(this.getDescricao());
		umHashCodeBuilder.append(this.getGrauRisco());
		umHashCodeBuilder.append(this.getIndSituacao());
		umHashCodeBuilder.append(this.getServidor());
		umHashCodeBuilder.append(this.getServidorMovimentado());
		umHashCodeBuilder.append(this.getCriadoEm());
		umHashCodeBuilder.append(this.getAlteradoEm());
		umHashCodeBuilder.append(this.getPesoInicial());
		umHashCodeBuilder.append(this.getPesoFinal());
		umHashCodeBuilder.append(this.getIndIsolamento());
		umHashCodeBuilder.append(this.getProcedureNotificacaoExames());
		umHashCodeBuilder.append(this.getCgpSeq());
		umHashCodeBuilder.append(this.getIndNotificacaoSms());
		umHashCodeBuilder.append(this.getIndUsoMascara());
		umHashCodeBuilder.append(this.getIndUsoAvental());
		umHashCodeBuilder.append(this.getIndTecnicaAsseptica());
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
		if (!(obj instanceof MciFatorPredisponentes)) {
			return false;
		}
		MciFatorPredisponentes other = (MciFatorPredisponentes) obj;
		EqualsBuilder equalsBuilder = new EqualsBuilder();
		equalsBuilder.append(this.getDescricao(), other.getDescricao());
		equalsBuilder.append(this.getSeq(),other.getSeq());                
		equalsBuilder.append(this.getDescricao(),other.getDescricao());             
		equalsBuilder.append(this.getGrauRisco(),other.getGrauRisco());                 
		equalsBuilder.append(this.getIndSituacao(),other.getIndSituacao());               
		equalsBuilder.append(this.getServidor(),other.getServidor());                  
		equalsBuilder.append(this.getServidorMovimentado(),other.getServidorMovimentado());       
		equalsBuilder.append(this.getCriadoEm(),other.getCriadoEm());                  
		equalsBuilder.append(this.getAlteradoEm(),other.getAlteradoEm());                
		equalsBuilder.append(this.getPesoInicial(),other.getPesoInicial());               
		equalsBuilder.append(this.getPesoFinal(),other.getPesoFinal());                 
		equalsBuilder.append(this.getIndIsolamento(),other.getIndIsolamento());             
		equalsBuilder.append(this.getProcedureNotificacaoExames(),other.getProcedureNotificacaoExames());
		equalsBuilder.append(this.getCgpSeq(),other.getCgpSeq());                    
		equalsBuilder.append(this.getIndNotificacaoSms(),other.getIndNotificacaoSms());         
		equalsBuilder.append(this.getIndUsoMascara(),other.getIndUsoMascara());             
		equalsBuilder.append(this.getIndUsoAvental(),other.getIndUsoAvental());             
		equalsBuilder.append(this.getIndTecnicaAsseptica(),other.getIndTecnicaAsseptica());       
		return equalsBuilder.isEquals();
	}

}
