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
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.Length;

import br.gov.mec.aghu.core.persistence.BaseEntitySeq;
import br.gov.mec.aghu.dominio.DominioIndPendenteAmbulatorio;
import br.gov.mec.aghu.dominio.DominioSituacaoAnamnese;


@Entity
@SequenceGenerator(name="mpmAnaSeq", sequenceName="AGH.mpm_ana_sq1", allocationSize = 1)
@Table(name = "MPM_ANAMNESES", schema = "AGH")
public class MpmAnamneses extends BaseEntitySeq<Long> implements java.io.Serializable {

    /**
     * 
     */

    private static final long serialVersionUID = -3638660513556361007L;
    private Long seq;
    private Date dthrCriacao;
    private Date dthrAlteracao;
    private DominioIndPendenteAmbulatorio pendente;
    private MamTipoItemAnamneses tipoItemAnamneses;
    private String descricao;
    private RapServidores servidor;
    private AghAtendimentos atendimento;
    private DominioSituacaoAnamnese situacao;
    private Integer version;
    


    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "mpmAnaSeq")
    @Column(name = "SEQ", nullable = false, precision = 14, scale = 0)
	public Long getSeq() {
		return seq;
	}
    
	public void setSeq(Long seq) {
		this.seq = seq;
	}
	
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "DTHR_CRIACAO", nullable = false, length = 7)
    @NotNull
	public Date getDthrCriacao() {
		return dthrCriacao;
	}
    
	public void setDthrCriacao(Date dthrCriacao) {
		this.dthrCriacao = dthrCriacao;
	}
	
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "DTHR_ALTERACAO", length = 7)
	public Date getDthrAlteracao() {
		return dthrAlteracao;
	}
    
	public void setDthrAlteracao(Date dthrAlteracao) {
		this.dthrAlteracao = dthrAlteracao;
	}
	
    @Column(name = "IND_PENDENTE", nullable = false, length = 1)
    @NotNull
    @Enumerated(EnumType.STRING)
	public DominioIndPendenteAmbulatorio getPendente() {
		return pendente;
	}
	public void setPendente(DominioIndPendenteAmbulatorio pendente) {
		this.pendente = pendente;
	}
	
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "TIN_SEQ")
	public MamTipoItemAnamneses getTipoItemAnamneses() {
		return tipoItemAnamneses;
	}
	public void setTipoItemAnamneses(MamTipoItemAnamneses tipoItemAnamneses) {
		this.tipoItemAnamneses = tipoItemAnamneses;
	}
	
    @Column(name = "DESCRICAO", length = 12000)
    @Length(max = 12000)
	public String getDescricao() {
		return descricao;
	}
	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}
	
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumns({ 
    	@JoinColumn(name = "SER_MATRICULA", referencedColumnName = "MATRICULA", nullable = false), 
    	@JoinColumn(name = "SER_VIN_CODIGO", referencedColumnName = "VIN_CODIGO", nullable = false) })
    @NotNull
	public RapServidores getServidor() {
		return servidor;
	}
	public void setServidor(RapServidores servidor) {
		this.servidor = servidor;
	}
	
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ATD_SEQ")
	public AghAtendimentos getAtendimento() {
		return atendimento;
	}
	public void setAtendimento(AghAtendimentos atendimento) {
		this.atendimento = atendimento;
	}
	
    @Column(name = "IND_USO", nullable = false, length = 1)
    @NotNull
    @Enumerated(EnumType.STRING)
	public DominioSituacaoAnamnese getSituacao() {
		return situacao;
	}
	public void setSituacao(DominioSituacaoAnamnese situacao) {
		this.situacao = situacao;
	}
	
    @Version
    @Column(name = "VERSION", nullable = false)
	public Integer getVersion() {
		return version;
	}
	public void setVersion(Integer version) {
		this.version = version;
	}
	
    public enum Fields {
        IND_PENDENTE("pendente"), DTHR_CRIACAO("dthrCriacao"), PENDENTE("pendente"), SEQ("seq"), DTHR_ALTERACAO("dthrAlteracao"), 
        ATENDIMENTO("atendimento"), SERVIDOR("servidor");

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
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getSeq() == null) ? 0 : getSeq().hashCode());
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
        if (!(obj instanceof MpmAnamneses)) {
                return false;
        }
        MpmAnamneses other = (MpmAnamneses) obj;
        if (getSeq() == null) {
                if (other.getSeq() != null) {
                        return false;
                }
        } else if (!getSeq().equals(other.getSeq())) {
                return false;
        }
        return true;
	}
	
}