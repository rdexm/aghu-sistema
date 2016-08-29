package br.gov.mec.aghu.model;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
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
import javax.validation.constraints.Max;
import javax.persistence.TemporalType;

import org.hibernate.annotations.Parameter;
import org.hibernate.validator.constraints.Length;

import br.gov.mec.aghu.core.persistence.BaseEntitySeq;
import br.gov.mec.aghu.dominio.DominioGrauParentesco;

@Entity
@SequenceGenerator(name="aelPeeSq2", sequenceName="AGH.AEL_PEE_SQ2", allocationSize = 1)
@Table(name = "AEL_PROTOCOLO_ENTREGA_EXAMES", schema = "AGH")
public class AelProtocoloEntregaExames extends BaseEntitySeq<Long> {

	private static final long serialVersionUID = -4579741769744553765L;
    private Long seq;
    private String nomeResponsavelRetirada;
    private DominioGrauParentesco grauParentesco;
    private Long cpf;
    private Short dddFoneResponsavel;
    private Long foneResponsavel;
    private Date criadoEm = new Date();
    private RapServidores servidor;
    private String observacao;
    private List<AelItemEntregaExames> itemEntregaExames = new LinkedList<AelItemEntregaExames>();


    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "aelPeeSq2")
    @Column(name = "SEQ", nullable = false, precision = 14, scale = 0)
	public Long getSeq() {
		return this.seq;
	}

	public void setSeq(Long seq) {
		this.seq = seq;
	}

    @Column(name = "NOME_RESPONSAVEL_RETIRADA", length = 60)
    @Length(max = 60)        
	public String getNomeResponsavelRetirada() {
		return nomeResponsavelRetirada;
	}

	public void setNomeResponsavelRetirada(String nomeResponsavelRetirada) {
		this.nomeResponsavelRetirada = nomeResponsavelRetirada;
	}

    @Column(name = "GRAU_PARENTESCO", nullable = false, precision = 1, scale = 0)
    @org.hibernate.annotations.Type(parameters = { @Parameter(name = "enumClassName", value = "br.gov.mec.aghu.dominio.DominioGrauParentesco") }, type = "br.gov.mec.aghu.core.model.jpa.DominioUserType")
    public DominioGrauParentesco getGrauParentesco() {
		return grauParentesco;
	}

	public void setGrauParentesco(DominioGrauParentesco grauParentesco) {
		this.grauParentesco = grauParentesco;
	}

    @Column(name = "CPF", precision = 11, scale = 0)
	public Long getCpf() {
		return cpf;
	}

	public void setCpf(Long cpf) {
		this.cpf = cpf;
	}

	@Column(name = "DDD_FONE_RESPONSAVEL", precision = 4, scale = 0)
	public Short getDddFoneResponsavel() {
		return dddFoneResponsavel;
	}

	public void setDddFoneResponsavel(Short dddFoneResponsavel) {
		this.dddFoneResponsavel = dddFoneResponsavel;
	}

    @Column(name = "FONE_RESPONSAVEL", precision = 10, scale = 0)
    @Max(value = 9999999999L, message = "Valor mÃ¡ximo permitido: 99 9999 9999")
	public Long getFoneResponsavel() {
		return foneResponsavel;
	}

	public void setFoneResponsavel(Long foneResponsavel) {
		this.foneResponsavel = foneResponsavel;
	}

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "CRIADO_EM")
	public Date getCriadoEm() {
		return criadoEm;
	}

	public void setCriadoEm(Date criadoEm) {
		this.criadoEm = criadoEm;
	}

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumns( {
            @JoinColumn(name = "SER_MATRICULA", referencedColumnName = "MATRICULA"),
            @JoinColumn(name = "SER_VIN_CODIGO", referencedColumnName = "VIN_CODIGO") })
	public RapServidores getServidor() {
		return servidor;
	}

	public void setServidor(RapServidores servidor) {
		this.servidor = servidor;
	}

    @Column(name = "OBSERVACAO", length = 2000)
    @Length(max = 2000)
	public String getObservacao() {
		return observacao;
	}

	public void setObservacao(String observacao) {
		this.observacao = observacao;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "protocoloEntregaExames")
	public List<AelItemEntregaExames> getItemEntregaExames() {
		return itemEntregaExames;
	}

	public void setItemEntregaExames(List<AelItemEntregaExames> itemEntregaExames) {
		this.itemEntregaExames = itemEntregaExames;
	}
	
    public enum Fields {

        SEQ("seq"),
        CRIADO_EM("criadoEm"),
        RETIRADO_POR("nomeResponsavelRetirada"),
        USUARIO_LOGADO("servidor"),
        ITEM_ENTREGA_EXAMES("itemEntregaExames");
        
        
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
		int result = super.hashCode();
		result = prime * result + ((seq == null) ? 0 : seq.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!super.equals(obj)) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		
		AelProtocoloEntregaExames other = (AelProtocoloEntregaExames) obj;
		
		if (seq == null) {
			if (other.seq != null) {
				return false;
			}
		} else {
			if (!seq.equals(other.seq)) {
				return false;
			}
		}
		return true;
	}

}
