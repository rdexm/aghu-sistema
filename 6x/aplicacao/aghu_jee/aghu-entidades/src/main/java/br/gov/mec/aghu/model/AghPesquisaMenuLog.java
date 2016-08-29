package br.gov.mec.aghu.model;

import java.util.Date;

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
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.Length;

import br.gov.mec.aghu.core.persistence.BaseEntitySeq;


@Entity
@SequenceGenerator(name="pesMenuSq1", sequenceName="AGH.pes_menu_sq1", allocationSize = 1)
@Table(name = "agh_pesquisa_menu_log", schema = "AGH")
public class AghPesquisaMenuLog extends BaseEntitySeq<Long> implements java.io.Serializable {

	private static final long serialVersionUID = 323218974773089147L;
	
	private Long seq;
	private Date criadoEm;
	private String menuPesquisado;
	private String url;
	private RapServidores servidor;

	public AghPesquisaMenuLog() {
	}

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "pesMenuSq1")
	@Column(name = "SEQ", nullable = false, precision = 14, scale = 0)
	public Long getSeq() {
		return this.seq;
	}

	public void setSeq(Long seq) {
		this.seq = seq;
	}
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CRIADO_EM", nullable = false, length = 7)
	@NotNull
	public Date getCriadoEm() {
		return this.criadoEm;
	}

	public void setCriadoEm(Date criadoEm) {
		this.criadoEm = criadoEm;
	}

	@Column(name = "MENU_PESQUISADO", nullable = true, length = 2000)
	@Length(max = 250)
	public String getMenuPesquisado() {
		return this.menuPesquisado;
	}

	public void setMenuPesquisado(String menuPesquisado) {
		this.menuPesquisado = menuPesquisado;
	}

	@Column(name = "URL", nullable = true, length = 2500)
	@Length(max = 2500)
	public String getUrl() {
		return this.url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns({
			@JoinColumn(name = "SER_MATRICULA", referencedColumnName = "MATRICULA"),
			@JoinColumn(name = "SER_VIN_CODIGO", referencedColumnName = "VIN_CODIGO") })
	public RapServidores getServidor() {
		return servidor;
	}

	public void setServidor(RapServidores servidor) {
		this.servidor = servidor;
	}

	public enum Fields {
		SEQ("seq"),
		CRIADO_EM("criadoEm"),
		MENU_PESQUISADO("menuPesquisado"),
		URL("url");

		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return fields;
		}
	}

}