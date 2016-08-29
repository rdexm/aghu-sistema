package br.gov.mec.aghu.model.cups;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.persistence.Version;

import br.gov.mec.aghu.core.persistence.BaseEntityId;

@Entity
@Table(name = "IMP_COMPUTADOR_IMPRESSORA", schema = "AGH", uniqueConstraints = @UniqueConstraint(columnNames = {"ID_COMPUTADOR", "ID_CLASSE_IMPRESSAO" }))
@SequenceGenerator(name = "impComputadorImpressoraSq1", sequenceName = "AGH.IMP_COMPUTADOR_IMPRESSORA_SQ1", allocationSize = 1)
public class ImpComputadorImpressora extends BaseEntityId<Integer> implements java.io.Serializable {

	private static final long serialVersionUID = -393495482159557268L;
	
	private Integer id;
	private ImpComputador impComputador;
	private ImpClasseImpressao impClasseImpressao;
	private ImpImpressora impImpressora;
	private Integer version;

	public ImpComputadorImpressora() {
	}

	public ImpComputadorImpressora(Integer id, ImpComputador impComputador,
			ImpClasseImpressao impClasseImpressao, ImpImpressora impImpressora) {
		this.id = id;
		this.impComputador = impComputador;
		this.impClasseImpressao = impClasseImpressao;
		this.impImpressora = impImpressora;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "impComputadorImpressoraSq1")
	@Column(name = "ID", unique = true, nullable = false)
	public Integer getId() {
		return this.id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	@ManyToOne
	@JoinColumn(name = "ID_COMPUTADOR", nullable = false)
	public ImpComputador getImpComputador() {
		return this.impComputador;
	}

	public void setImpComputador(ImpComputador impComputador) {
		this.impComputador = impComputador;
	}

	@ManyToOne
	@JoinColumn(name = "ID_CLASSE_IMPRESSAO", nullable = false)
	public ImpClasseImpressao getImpClasseImpressao() {
		return this.impClasseImpressao;
	}

	public void setImpClasseImpressao(ImpClasseImpressao impClasseImpressao) {
		this.impClasseImpressao = impClasseImpressao;
	}

	@ManyToOne
	@JoinColumn(name = "ID_IMPRESSORA", nullable = false)
	public ImpImpressora getImpImpressora() {
		return this.impImpressora;
	}

	public void setImpImpressora(ImpImpressora impImpressora) {
		this.impImpressora = impImpressora;
	}

	public enum Fields {
		ID_COMPUTADOR("impComputador.id"), ID_CLASSE_IMPRESSORA("impClasseImpressao.id"), IMPRESSORA_FILA(
				"impImpressora.filaImpressora"),IMP_CLASSE_IMPRESSAO("impClasseImpressao"),
				IMP_COMPUTADOR("impComputador"), IMP_IMPRESSORA("impImpressora");
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