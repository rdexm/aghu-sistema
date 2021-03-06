package br.gov.mec.aghu.model;

// Generated 11/06/2010 10:31:39 by Hibernate Tools 3.2.5.Beta



import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;


import org.hibernate.validator.constraints.Length;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioTipoMedidaPreventiva;
import br.gov.mec.aghu.core.persistence.BaseEntitySeq;

/**
 * MciPatologiaInfeccoes generated by hbm2java
 */
@Entity
@SequenceGenerator(name="mciPaiSq1", sequenceName="AGH.MCI_PAI_SQ1", allocationSize = 1)
@Table(name = "MCI_PATOLOGIA_INFECCOES", schema = "AGH")

public class MciPatologiaInfeccao extends BaseEntitySeq<Integer> implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3037908765496124183L;
	private Integer seq;
	private MciOrientacaoTratMaterial orientacaoTratMaterial;
	private MciTopografiaInfeccao topografiaInfeccao;
	private RapServidores servidor;
	private RapServidores movimentado;
	private MciDuracaoMedidaPreventiva duracaoMedidaPreventiva;
	private MciPlaca placa;
	private String descricao;
	private Date criadoEm;
	private Date alteradoEm;
	private Boolean notificaSsma;
	private Boolean usoQuartoPrivativo;
	private Boolean usoMascara;
	private Boolean usoAvental;
	private Boolean tecnicaAsseptica;
	private Boolean higienizacaoMaos;
	private Boolean usoMascaraN95;
	private Boolean usoOculos;
	private DominioSituacao situacao;
	private Boolean impNotificacao;
	private DominioTipoMedidaPreventiva tipoMedidaPreventiva;
	private String observacao;
	private Boolean permiteNotificacao;
	private String orientQuartoIndividual;
	private String observacao2;
	private String orientUtilizacaoSanitarios;
	private String profilaxiaContatos;
	private Integer version;
	private List<MciPalavraChavePatologia> palavrasChaves = new ArrayList<MciPalavraChavePatologia>();

	// TODO Implementar version quando usado POJO para persistencia.
	// Já existe o getter e setter comentado no final da classe.
	/* private Integer version; */

	public MciPatologiaInfeccao() {
	}

	public MciPatologiaInfeccao(Integer seq, RapServidores servidor,
			String descricao, Date criadoEm, Boolean notificaSsma,
			Boolean usoQuartoPrivativo, Boolean usoMascara, Boolean usoAvental,
			Boolean tecnicaAsseptica, DominioSituacao situacao,
			Boolean impNotificacao,
			DominioTipoMedidaPreventiva tipoMedidaPreventiva,
			Boolean permiteNotificacao) {
		this.seq = seq;
		this.servidor = servidor;
		this.descricao = descricao;
		this.criadoEm = criadoEm;
		this.notificaSsma = notificaSsma;
		this.usoQuartoPrivativo = usoQuartoPrivativo;
		this.usoMascara = usoMascara;
		this.usoAvental = usoAvental;
		this.tecnicaAsseptica = tecnicaAsseptica;
		this.situacao = situacao;
		this.impNotificacao = impNotificacao;
		this.tipoMedidaPreventiva = tipoMedidaPreventiva;
		this.permiteNotificacao = permiteNotificacao;
	}

	public MciPatologiaInfeccao(Integer seq,
			MciOrientacaoTratMaterial orientacaoTratMaterial,
			MciTopografiaInfeccao topografiaInfeccao, RapServidores servidor,
			MciDuracaoMedidaPreventiva duracaoMedidaPreventiva,
			MciPlaca placa, String descricao, Date criadoEm,
			Boolean notificaSsma, Boolean usoQuartoPrivativo,
			Boolean usoMascara, Boolean usoAvental, Boolean tecnicaAsseptica,
			DominioSituacao situacao, Boolean impNotificacao,
			DominioTipoMedidaPreventiva tipoMedidaPreventiva,
			String observacao, Boolean permiteNotificacao,
			String orientQuartoIndividual, String observacao2,
			String orientUtilizacaoSanitarios, String profilaxiaContatos) {
		this.seq = seq;
		this.orientacaoTratMaterial = orientacaoTratMaterial;
		this.topografiaInfeccao = topografiaInfeccao;
		this.servidor = servidor;
		this.duracaoMedidaPreventiva = duracaoMedidaPreventiva;
		this.placa = placa;
		this.descricao = descricao;
		this.criadoEm = criadoEm;
		this.notificaSsma = notificaSsma;
		this.usoQuartoPrivativo = usoQuartoPrivativo;
		this.usoMascara = usoMascara;
		this.usoAvental = usoAvental;
		this.tecnicaAsseptica = tecnicaAsseptica;
		this.situacao = situacao;
		this.impNotificacao = impNotificacao;
		this.tipoMedidaPreventiva = tipoMedidaPreventiva;
		this.observacao = observacao;
		this.permiteNotificacao = permiteNotificacao;
		this.orientQuartoIndividual = orientQuartoIndividual;
		this.observacao2 = observacao2;
		this.orientUtilizacaoSanitarios = orientUtilizacaoSanitarios;
		this.profilaxiaContatos = profilaxiaContatos;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "mciPaiSq1")
	@Column(name = "SEQ", nullable = false, precision = 5, scale = 0)
	public Integer getSeq() {
		return this.seq;
	}

	public void setSeq(Integer seq) {
		this.seq = seq;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "OTM_SEQ")
	public MciOrientacaoTratMaterial getOrientacaoTratMaterial() {
		return this.orientacaoTratMaterial;
	}

	public void setOrientacaoTratMaterial(
			MciOrientacaoTratMaterial orientacaoTratMaterial) {
		this.orientacaoTratMaterial = orientacaoTratMaterial;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "TOI_SEQ")
	public MciTopografiaInfeccao getTopografiaInfeccao() {
		return this.topografiaInfeccao;
	}

	public void setTopografiaInfeccao(MciTopografiaInfeccao topografiaInfeccao) {
		this.topografiaInfeccao = topografiaInfeccao;
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

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns( {
			@JoinColumn(name = "SER_MATRICULA_MOVIMENTADO", referencedColumnName = "MATRICULA"),
			@JoinColumn(name = "SER_VIN_CODIGO_MOVIMENTADO", referencedColumnName = "VIN_CODIGO") })
	public RapServidores getMovimentado() {
		return movimentado;
	}
	
	public void setMovimentado(RapServidores movimentado) {
		this.movimentado = movimentado;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "DMP_SEQ")
	public MciDuracaoMedidaPreventiva getDuracaoMedidaPreventiva() {
		return this.duracaoMedidaPreventiva;
	}

	public void setDuracaoMedidaPreventiva(
			MciDuracaoMedidaPreventiva duracaoMedidaPreventiva) {
		this.duracaoMedidaPreventiva = duracaoMedidaPreventiva;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "PLC_NUMERO")
	public MciPlaca getPlaca() {
		return this.placa;
	}

	public void setPlaca(MciPlaca placa) {
		this.placa = placa;
	}

	@Column(name = "DESCRICAO", nullable = false, length = 60)
	@Length(max = 60)
	public String getDescricao() {
		return this.descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
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
		return alteradoEm;
	}

	public void setAlteradoEm(Date alteradoEm) {
		this.alteradoEm = alteradoEm;
	}

	@Column(name = "IND_NOTIFICA_SSMA", nullable = false, length = 1)
	@org.hibernate.annotations.Type(type = "br.gov.mec.aghu.core.persistence.type.BooleanUserType")
	public Boolean getNotificaSsma() {
		return this.notificaSsma;
	}

	public void setNotificaSsma(Boolean notificaSsma) {
		this.notificaSsma = notificaSsma;
	}

	@Column(name = "IND_USO_QUARTO_PRIVATIVO", nullable = false, length = 1)
	@org.hibernate.annotations.Type(type = "br.gov.mec.aghu.core.persistence.type.BooleanUserType")
	public Boolean getUsoQuartoPrivativo() {
		return this.usoQuartoPrivativo;
	}

	public void setUsoQuartoPrivativo(Boolean usoQuartoPrivativo) {
		this.usoQuartoPrivativo = usoQuartoPrivativo;
	}

	@Column(name = "IND_USO_MASCARA", nullable = false, length = 1)
	@org.hibernate.annotations.Type(type = "br.gov.mec.aghu.core.persistence.type.BooleanUserType")
	public Boolean getUsoMascara() {
		return this.usoMascara;
	}

	public void setUsoMascara(Boolean usoMascara) {
		this.usoMascara = usoMascara;
	}

	@Column(name = "IND_USO_AVENTAL", nullable = false, length = 1)
	@org.hibernate.annotations.Type(type = "br.gov.mec.aghu.core.persistence.type.BooleanUserType")
	public Boolean getUsoAvental() {
		return this.usoAvental;
	}

	public void setUsoAvental(Boolean usoAvental) {
		this.usoAvental = usoAvental;
	}

	@Column(name = "IND_TECNICA_ASSEPTICA", nullable = false, length = 1)
	@org.hibernate.annotations.Type(type = "br.gov.mec.aghu.core.persistence.type.BooleanUserType")
	public Boolean getTecnicaAsseptica() {
		return this.tecnicaAsseptica;
	}

	public void setTecnicaAsseptica(Boolean tecnicaAsseptica) {
		this.tecnicaAsseptica = tecnicaAsseptica;
	}

	@Column(name = "IND_SITUACAO", nullable = false, length = 1)
	@Enumerated(EnumType.STRING)
	public DominioSituacao getSituacao() {
		return this.situacao;
	}

	public void setSituacao(DominioSituacao situacao) {
		this.situacao = situacao;
	}

	@Column(name = "IND_IMP_NOTIFICACAO", nullable = false, length = 1)
	@org.hibernate.annotations.Type(type = "br.gov.mec.aghu.core.persistence.type.BooleanUserType")
	public Boolean getImpNotificacao() {
		return this.impNotificacao;
	}

	public void setImpNotificacao(Boolean impNotificacao) {
		this.impNotificacao = impNotificacao;
	}

	@Column(name = "IND_TIPO_MEDIDA_PREVENTIVA", length = 1)
	@Enumerated(EnumType.STRING)
	public DominioTipoMedidaPreventiva getTipoMedidaPreventiva() {
		return this.tipoMedidaPreventiva;
	}

	public void setTipoMedidaPreventiva(
			DominioTipoMedidaPreventiva tipoMedidaPreventiva) {
		this.tipoMedidaPreventiva = tipoMedidaPreventiva;
	}

	@Column(name = "OBSERVACAO", length = 2000)
	@Length(max = 2000)
	public String getObservacao() {
		return this.observacao;
	}

	public void setObservacao(String observacao) {
		this.observacao = observacao;
	}

	@Column(name = "IND_PERMITE_NOTIFICACAO", length = 1)
	@org.hibernate.annotations.Type(type = "br.gov.mec.aghu.core.persistence.type.BooleanUserType")
	public Boolean getPermiteNotificacao() {
		return this.permiteNotificacao;
	}

	public void setPermiteNotificacao(Boolean permiteNotificacao) {
		this.permiteNotificacao = permiteNotificacao;
	}

	@Column(name = "ORIENT_QUARTO_INDIVIDUAL", length = 120)
	@Length(max = 120)
	public String getOrientQuartoIndividual() {
		return this.orientQuartoIndividual;
	}

	public void setOrientQuartoIndividual(String orientQuartoIndividual) {
		this.orientQuartoIndividual = orientQuartoIndividual;
	}

	@Column(name = "OBSERVACAO2", length = 2000)
	@Length(max = 2000)
	public String getObservacao2() {
		return this.observacao2;
	}

	public void setObservacao2(String observacao2) {
		this.observacao2 = observacao2;
	}

	@Column(name = "ORIENT_UTILIZACAO_SANITARIOS", length = 240)
	@Length(max = 240)
	public String getOrientUtilizacaoSanitarios() {
		return this.orientUtilizacaoSanitarios;
	}

	public void setOrientUtilizacaoSanitarios(String orientUtilizacaoSanitarios) {
		this.orientUtilizacaoSanitarios = orientUtilizacaoSanitarios;
	}

	@Column(name = "PROFILAXIA_CONTATOS", length = 500)
	@Length(max = 500)
	public String getProfilaxiaContatos() {
		return this.profilaxiaContatos;
	}

	public void setProfilaxiaContatos(String profilaxiaContatos) {
		this.profilaxiaContatos = profilaxiaContatos;
	}

	@Column(name = "IND_HIGIENIZACAO_MAOS", nullable = false, length = 1)
	@org.hibernate.annotations.Type(type = "br.gov.mec.aghu.core.persistence.type.BooleanUserType")
	public Boolean getHigienizacaoMaos() {
		return higienizacaoMaos;
	}

	public void setHigienizacaoMaos(Boolean higienizacaoMaos) {
		this.higienizacaoMaos = higienizacaoMaos;
	}

	@Column(name = "IND_USO_MASCARA_N95", nullable = false, length = 1)
	@org.hibernate.annotations.Type(type = "br.gov.mec.aghu.core.persistence.type.BooleanUserType")
	public Boolean getUsoMascaraN95() {
		return usoMascaraN95;
	}

	public void setUsoMascaraN95(Boolean usoMascaraN95) {
		this.usoMascaraN95 = usoMascaraN95;
	}
	
	@Column(name = "IND_USO_OCULOS", nullable = false, length = 1)
	@org.hibernate.annotations.Type(type = "br.gov.mec.aghu.core.persistence.type.BooleanUserType")
	public Boolean getUsoOculos() {
		return usoOculos;
	}

	public void setUsoOculos(Boolean usoOculos) {
		this.usoOculos = usoOculos;
	}
	
	@Version
	@Column(name = "VERSION", nullable = false, precision = 9, scale = 0)
	public Integer getVersion() {
		return this.version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "mciPatologiaInfeccao")
	public List<MciPalavraChavePatologia> getPalavrasChaves() {
		return palavrasChaves;
	}

	public void setPalavrasChaves(List<MciPalavraChavePatologia> palavrasChaves) {
		this.palavrasChaves = palavrasChaves;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((seq == null) ? 0 : seq.hashCode());
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
		if (getClass() != obj.getClass()) {
			return false;
		}
		MciPatologiaInfeccao other = (MciPatologiaInfeccao) obj;
		if (seq == null) {
			if (other.seq != null) {
				return false;
			}
		} else if (!seq.equals(other.seq)) {
			return false;
		}
		return true;
	}

	public enum Fields {
		
		SEQ("seq"),
		IND_QUARTO_USO_PRIVATIVO("usoQuartoPrivativo"),
		DMP_SEQ("duracaoMedidaPreventiva.seq"),
		CRIADO_EM("criadoEm"),
		DESCRICAO("descricao"),
		DURACAO_MEDIDA_PREVENTIVA("duracaoMedidaPreventiva"),
		IMP_NOTIFICACAO("impNotificacao"),
		NOTIFICA_SSMA("notificaSsma"),
		OBSERVACAO("observacao"),
		OBSERVACAO2("observacao2"),
		ORIENTACAO_TRAT_MATERIAL("orientacaoTratMaterial"),
		ORIENT_QUARTO_INDIVIDUAL("orientQuartoIndividual"),
		ORIENT_UTILIZACAO_SANITARIOS("orientUtilizacaoSanitarios"),
		PERMITE_NOTIFICACAO("permiteNotificacao"),
		PLACA("placa"),
		PROFILAXIACONTATO("profilaxiaContato"),
		SERVIDOR("servidor"),
		SITUACAO("situacao"),
		TECNICA_ASSEPTICA("tecnicaAsseptica"),
		TIPO_MEDIDA_PREVENTIVA("tipoMedidaPreventiva"),
		TOPOGRAFIA_INFECCAO("topografiaInfeccao"),
		TOPOGRAFIA_INFECCAO_SEQ("topografiaInfeccao.seq"),
		USO_AVENTAL("usoAvental"),
		USO_MASCARA("usoMascara"),
		HIGIENIZACAO_MAOS("higienizacaoMaos"),
		USO_MASCARA_N95("usoMascaraN95"),
		USO_OCULOS("usoOculos"),
		PALAVRAS_CHAVES("palavrasChaves")
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
