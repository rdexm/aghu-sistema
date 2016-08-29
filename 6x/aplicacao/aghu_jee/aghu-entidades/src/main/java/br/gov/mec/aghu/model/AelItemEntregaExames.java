package br.gov.mec.aghu.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import br.gov.mec.aghu.core.persistence.BaseEntitySeq;

@Entity
@SequenceGenerator(name="aelieesq", sequenceName="AGH.AEL_IEE_SQ", allocationSize = 1)
@Table(name = "AEL_ITEM_ENTREGA_EXAMES", schema = "AGH")
public class AelItemEntregaExames extends BaseEntitySeq<Long> {

	private static final long serialVersionUID = 3774766252143781945L;
	
    private Long seq;
    private AelItemSolicitacaoExames solicitacaoExames;
    private AelProtocoloEntregaExames protocoloEntregaExames;

	
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "aelieesq")
    @Column(name = "SEQ", nullable = false, precision = 14, scale = 0)
    public Long getSeq() {
            return seq;
    }

    public void setSeq(Long seq) {
            this.seq = seq;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumns({
                    @JoinColumn(name = "ISE_SOE_SEQ", referencedColumnName = "SOE_SEQ"),
                    @JoinColumn(name = "ISE_SEQP", referencedColumnName = "SEQP")})
	public AelItemSolicitacaoExames getSolicitacaoExames() {
		return solicitacaoExames;
	}

	public void setSolicitacaoExames(AelItemSolicitacaoExames solicitacaoExames) {
		this.solicitacaoExames = solicitacaoExames;
	}

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PEE_SEQ", nullable = false)
	public AelProtocoloEntregaExames getProtocoloEntregaExames() {
		return protocoloEntregaExames;
	}

	public void setProtocoloEntregaExames(
			AelProtocoloEntregaExames protocoloEntregaExames) {
		this.protocoloEntregaExames = protocoloEntregaExames;
	}
	
	public enum Fields {
	  SEQ("seq"),
	  PROTOCOLO("protocoloEntregaExames"),
	  ITEM_S0LICITACAO_EXAMES("solicitacaoExames");
		
		private String fields;

		private Fields(final String fields) {
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
		
		AelItemEntregaExames other = (AelItemEntregaExames) obj;
		
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
