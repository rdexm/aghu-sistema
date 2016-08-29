package br.gov.mec.aghu.model;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;


import org.hibernate.annotations.Immutable;
import org.hibernate.validator.constraints.Length;

import br.gov.mec.aghu.dominio.DominioIndFotoSensibilidade;
import br.gov.mec.aghu.dominio.DominioIndUnidTempoMdto;
import br.gov.mec.aghu.dominio.DominioQuimio;
import br.gov.mec.aghu.dominio.DominioSituacaoMedicamento;
import br.gov.mec.aghu.core.model.BaseJournal;

/**
 * Journal da AfaMedicamento
 * 
 * @author lcmoura
 * 
 */
@Entity
@SequenceGenerator(name = "AFA_MED_JN_SEQ", sequenceName = "AGH.AFA_MED_JN_SEQ", allocationSize = 1)
@Table(name = "AFA_MEDICAMENTOS_JN", schema = "AGH")
@Immutable
public class AfaMedicamentoJn extends BaseJournal {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5138134791264868001L;
	//private Integer seqJn;
	//private String nomeUsuario;
	//private Date dataAlteracao;
	//private DominioOperacoesJournal operacao;

	private Integer matCodigo;
	private String tprSigla;
	private Date criadoEm;
	private String descricao;
	private String descricaoEtiqueta;
	private DominioSituacaoMedicamento indSituacao;
	private Boolean indCalcDispensacaoFracionad;
	private Boolean indPadronizacao;
	private Boolean indPermiteDoseFracionada;
	private Boolean indSobraReaproveitavel;
	private Boolean indExigeObservacao;
	private Boolean indRevisaoCadastro;
	private BigDecimal concentracao;
	private Date hrioInicioAdmSugerido;
	private String observacao;
	private BigDecimal qtdeCaloriasGrama;
	private BigDecimal doseMaximaMgKg;
	private Short frequenciaUsual;
	private String indicacoes;
	private String contraIndicacoes;
	private String cuidadoConservacao;
	private String orientacoesAdministracao;
	private Boolean indDiluente;
	private Boolean indGeraDispensacao;
	private String linkParecerIndeferido;
	private String linkProtocoloUtilizacao;
	private String descricaoEtiquetaFrasco;
	private String descricaoEtiquetaSeringa;
	private Boolean indInteresseCcih;
	private Boolean indGeladeira;
	private Boolean indUnitariza;
	private DominioIndFotoSensibilidade indFotosensibilidade;
	private DominioIndUnidTempoMdto indUnidadeTempo;
	private Short tempoFotosensibilidade;
	private DominioQuimio tipoQuimio;

	private String siglaTipoUsoMdto;
	private Integer matricula;
	private Short vinCodigo;
	private Integer seqUnidadeMedidaMedica;
	private Short seqTipoFrequenciaAprazamento;

	public AfaMedicamentoJn() {

		super();
	}
	
	public void doSetPropriedades (AfaMedicamento entidade) {
		
		this.matCodigo = entidade.getMatCodigo();
		if (entidade.getTipoApresentacaoMedicamento() != null) {
			this.tprSigla = entidade.getTipoApresentacaoMedicamento()
					.getSigla();
		}
		this.criadoEm = this.getDataAlteracao();
		this.descricao = entidade.getDescricao();
		this.descricaoEtiqueta = entidade.getDescricaoEtiqueta();
		this.indSituacao = entidade.getIndSituacao();
		this.indCalcDispensacaoFracionad = entidade
				.getIndCalcDispensacaoFracionad();
		this.indPadronizacao = entidade.getIndPadronizacao();
		this.indPermiteDoseFracionada = entidade.getIndPermiteDoseFracionada();
		this.indSobraReaproveitavel = entidade.getIndSobraReaproveitavel();
		this.indExigeObservacao = entidade.getIndExigeObservacao();
		this.indRevisaoCadastro = entidade.getIndRevisaoCadastro();
		this.concentracao = entidade.getConcentracao();
		this.hrioInicioAdmSugerido = entidade.getHrioInicioAdmSugerido();
		this.observacao = entidade.getObservacao();
		this.qtdeCaloriasGrama = entidade.getQtdeCaloriasGrama();
		this.doseMaximaMgKg = entidade.getDoseMaximaMgKg();
		this.frequenciaUsual = entidade.getFrequenciaUsual();
		this.indicacoes = entidade.getIndicacoes();
		this.contraIndicacoes = entidade.getContraIndicacoes();
		this.cuidadoConservacao = entidade.getCuidadoConservacao();
		this.orientacoesAdministracao = entidade.getOrientacoesAdministracao();
		this.indDiluente = entidade.getIndDiluente();
		this.indGeraDispensacao = entidade.getIndGeraDispensacao();
		this.linkParecerIndeferido = entidade.getLinkParecerIndeferido();
		this.linkProtocoloUtilizacao = entidade.getLinkProtocoloUtilizacao();
		this.descricaoEtiquetaFrasco = entidade.getDescricaoEtiquetaFrasco();
		this.descricaoEtiquetaSeringa = entidade.getDescricaoEtiquetaSeringa();
		this.indInteresseCcih = entidade.getIndInteresseCcih();
		this.indGeladeira = entidade.getIndGeladeira();
		this.indFotosensibilidade = entidade.getIndFotosensibilidade();
		this.indUnidadeTempo = entidade.getIndUnidadeTempo();
		this.tempoFotosensibilidade = entidade.getTempoFotosensibilidade();
		this.tipoQuimio = entidade.getTipoQuimio();

		if (entidade.getAfaTipoUsoMdtos() != null) {
			this.siglaTipoUsoMdto = entidade.getAfaTipoUsoMdtos().getSigla();
		}
		if (entidade.getMpmUnidadeMedidaMedicas() != null) {
			this.seqUnidadeMedidaMedica = entidade.getMpmUnidadeMedidaMedicas()
					.getSeq();
		}
		if (entidade.getMpmTipoFreqAprazamentos() != null) {
			this.seqTipoFrequenciaAprazamento = entidade
					.getMpmTipoFreqAprazamentos().getSeq();
		}
	}
	
	@Id
	@Column(name = "SEQ_JN", unique = true, nullable = false)
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "AFA_MED_JN_SEQ")
	@Override
	public Integer getSeqJn() {
		return super.getSeqJn();
	}
	
	@Column(name = "MAT_CODIGO", unique = true, nullable = false, precision = 6, scale = 0)
	public Integer getMatCodigo() {
		return matCodigo;
	}

	@Column(name = "TUM_SIGLA", length = 2)
	public String getSiglaTipoUsoMdto() {
		return siglaTipoUsoMdto;
	}

	@Column(name = "SER_MATRICULA", precision = 7, scale = 0)
	public Integer getMatricula() {
		return matricula;
	}

	@Column(name = "SER_VIN_CODIGO", precision = 3, scale = 0)
	public Short getVinCodigo() {
		return vinCodigo;
	}

	@Column(name = "UMM_SEQ", precision = 5, scale = 0)
	public Integer getSeqUnidadeMedidaMedica() {
		return seqUnidadeMedidaMedica;
	}

	@Column(name = "TFQ_SEQ", precision = 4, scale = 0)
	public Short getSeqTipoFrequenciaAprazamento() {
		return seqTipoFrequenciaAprazamento;
	}

	@Column(name = "TPR_SIGLA", length = 3)
	@Length(max = 3)
	public String getTprSigla() {
		return tprSigla;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CRIADO_EM", length = 7)
	public Date getCriadoEm() {
		return criadoEm;
	}

	@Column(name = "DESCRICAO", length = 250)
	@Length(max = 250)
	public String getDescricao() {
		return descricao;
	}

	@Column(name = "DESCRICAO_ETIQUETA", length = 18)
	@Length(max = 18)
	public String getDescricaoEtiqueta() {
		return descricaoEtiqueta;
	}

	@Column(name = "IND_SITUACAO", nullable = false, length = 1)
	@Enumerated(EnumType.STRING)
	public DominioSituacaoMedicamento getIndSituacao() {
		return indSituacao;
	}

	@Column(name = "IND_CALC_DISPENSACAO_FRACIONAD", nullable = false, length = 1)
	@org.hibernate.annotations.Type(type = "br.gov.mec.aghu.core.persistence.type.BooleanUserType")
	public Boolean getIndCalcDispensacaoFracionad() {
		return indCalcDispensacaoFracionad;
	}

	@Column(name = "IND_PADRONIZACAO", nullable = false, length = 1)
	@org.hibernate.annotations.Type(type = "br.gov.mec.aghu.core.persistence.type.BooleanUserType")
	public Boolean getIndPadronizacao() {
		return indPadronizacao;
	}

	@Column(name = "IND_PERMITE_DOSE_FRACIONADA", nullable = false, length = 1)
	@org.hibernate.annotations.Type(type = "br.gov.mec.aghu.core.persistence.type.BooleanUserType")
	public Boolean getIndPermiteDoseFracionada() {
		return indPermiteDoseFracionada;
	}

	@Column(name = "IND_SOBRA_REAPROVEITAVEL", nullable = false, length = 1)
	@org.hibernate.annotations.Type(type = "br.gov.mec.aghu.core.persistence.type.BooleanUserType")
	public Boolean getIndSobraReaproveitavel() {
		return indSobraReaproveitavel;
	}

	@Column(name = "IND_EXIGE_OBSERVACAO", nullable = false, length = 1)
	@org.hibernate.annotations.Type(type = "br.gov.mec.aghu.core.persistence.type.BooleanUserType")
	public Boolean getIndExigeObservacao() {
		return indExigeObservacao;
	}

	@Column(name = "IND_REVISAO_CADASTRO", nullable = false, length = 1)
	@org.hibernate.annotations.Type(type = "br.gov.mec.aghu.core.persistence.type.BooleanUserType")
	public Boolean getIndRevisaoCadastro() {
		return indRevisaoCadastro;
	}

	@Column(name = "CONCENTRACAO", precision = 14, scale = 4)
	public BigDecimal getConcentracao() {
		return concentracao;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "HRIO_INICIO_ADM_SUGERIDO", length = 7)
	public Date getHrioInicioAdmSugerido() {
		return hrioInicioAdmSugerido;
	}

	@Column(name = "OBSERVACAO", length = 500)
	@Length(max = 500)
	public String getObservacao() {
		return observacao;
	}

	@Column(name = "QTDE_CALORIAS_GRAMA", precision = 6)
	public BigDecimal getQtdeCaloriasGrama() {
		return qtdeCaloriasGrama;
	}

	@Column(name = "DOSE_MAXIMA_MG_KG", precision = 6)
	public BigDecimal getDoseMaximaMgKg() {
		return doseMaximaMgKg;
	}

	@Column(name = "FREQUENCIA_USUAL", precision = 3, scale = 0)
	public Short getFrequenciaUsual() {
		return frequenciaUsual;
	}

	@Column(name = "INDICACOES", length = 2000)
	@Length(max = 2000)
	public String getIndicacoes() {
		return indicacoes;
	}

	@Column(name = "CONTRA_INDICACOES", length = 2000)
	@Length(max = 2000)
	public String getContraIndicacoes() {
		return contraIndicacoes;
	}

	@Column(name = "CUIDADO_CONSERVACAO", length = 2000)
	@Length(max = 2000)
	public String getCuidadoConservacao() {
		return cuidadoConservacao;
	}

	@Column(name = "ORIENTACOES_ADMINISTRACAO", length = 2000)
	@Length(max = 2000)
	public String getOrientacoesAdministracao() {
		return orientacoesAdministracao;
	}

	@Column(name = "IND_DILUENTE", length = 1)
	@org.hibernate.annotations.Type(type = "br.gov.mec.aghu.core.persistence.type.BooleanUserType")
	public Boolean getIndDiluente() {
		return indDiluente;
	}

	@Column(name = "IND_GERA_DISPENSACAO", length = 1)
	@org.hibernate.annotations.Type(type = "br.gov.mec.aghu.core.persistence.type.BooleanUserType")
	public Boolean getIndGeraDispensacao() {
		return indGeraDispensacao;
	}

	@Column(name = "LINK_PARECER_INDEFERIDO", length = 240)
	@Length(max = 240)
	public String getLinkParecerIndeferido() {
		return linkParecerIndeferido;
	}

	@Column(name = "LINK_PROTOCOLO_UTILIZACAO", length = 240)
	@Length(max = 240)
	public String getLinkProtocoloUtilizacao() {
		return linkProtocoloUtilizacao;
	}

	@Column(name = "DESCRICAO_ETIQUETA_FRASCO", length = 20)
	@Length(max = 20)
	public String getDescricaoEtiquetaFrasco() {
		return descricaoEtiquetaFrasco;
	}

	@Column(name = "DESCRICAO_ETIQUETA_SERINGA", length = 25)
	@Length(max = 25)
	public String getDescricaoEtiquetaSeringa() {
		return descricaoEtiquetaSeringa;
	}

	@Column(name = "IND_INTERESSE_CCIH", length = 1)
	@org.hibernate.annotations.Type(type = "br.gov.mec.aghu.core.persistence.type.BooleanUserType")
	public Boolean getIndInteresseCcih() {
		return indInteresseCcih;
	}

	@Column(name = "IND_GELADEIRA", length = 1)
	@org.hibernate.annotations.Type(type = "br.gov.mec.aghu.core.persistence.type.BooleanUserType")
	public Boolean getIndGeladeira() {
		return indGeladeira;
	}
	
	@Column(name = "IND_UNITARIZA", length = 1)
	@org.hibernate.annotations.Type(type = "br.gov.mec.aghu.core.persistence.type.BooleanUserType")
	public Boolean getIndUnitariza() {
		return indUnitariza;
	}

	@Column(name = "IND_FOTOSENSIBILIDADE", length = 1)
	@Enumerated(EnumType.STRING)
	public DominioIndFotoSensibilidade getIndFotosensibilidade() {
		return indFotosensibilidade;
	}

	@Column(name = "IND_UNIDADE_TEMPO", length = 1)
	@Enumerated(EnumType.STRING)
	public DominioIndUnidTempoMdto getIndUnidadeTempo() {
		return indUnidadeTempo;
	}

	@Column(name = "TEMPO_FOTOSENSIBILIDADE", precision = 3, scale = 0)
	public Short getTempoFotosensibilidade() {
		return tempoFotosensibilidade;
	}

	@Column(name = "TIPO_QUIMIO", length = 1)
	@Enumerated(EnumType.STRING)
	public DominioQuimio getTipoQuimio() {
		return tipoQuimio;
	}

	
	
	public enum Fields {

		SEQ_JN("seqJn"),
		MAT_CODIGO("matCodigo"),
		DATA_ALTERACAO("dataAlteracao");

		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return this.fields;
		}
	}


	public void setSiglaTipoUsoMdto(String siglaTipoUsoMdto) {
		this.siglaTipoUsoMdto = siglaTipoUsoMdto;
	}

	public void setMatricula(Integer matricula) {
		this.matricula = matricula;
	}

	public void setVinCodigo(Short vinCodigo) {
		this.vinCodigo = vinCodigo;
	}

	public void setSeqUnidadeMedidaMedica(Integer seqUnidadeMedidaMedica) {
		this.seqUnidadeMedidaMedica = seqUnidadeMedidaMedica;
	}

	public void setSeqTipoFrequenciaAprazamento(
			Short seqTipoFrequenciaAprazamento) {
		this.seqTipoFrequenciaAprazamento = seqTipoFrequenciaAprazamento;
	}

	public void setMatCodigo(Integer matCodigo) {
		this.matCodigo = matCodigo;
	}

	public void setTprSigla(String tprSigla) {
		this.tprSigla = tprSigla;
	}

	public void setCriadoEm(Date criadoEm) {
		this.criadoEm = criadoEm;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public void setDescricaoEtiqueta(String descricaoEtiqueta) {
		this.descricaoEtiqueta = descricaoEtiqueta;
	}

	public void setIndSituacao(DominioSituacaoMedicamento indSituacao) {
		this.indSituacao = indSituacao;
	}

	public void setIndCalcDispensacaoFracionad(
			Boolean indCalcDispensacaoFracionad) {
		this.indCalcDispensacaoFracionad = indCalcDispensacaoFracionad;
	}

	public void setIndPadronizacao(Boolean indPadronizacao) {
		this.indPadronizacao = indPadronizacao;
	}

	public void setIndPermiteDoseFracionada(Boolean indPermiteDoseFracionada) {
		this.indPermiteDoseFracionada = indPermiteDoseFracionada;
	}

	public void setIndSobraReaproveitavel(Boolean indSobraReaproveitavel) {
		this.indSobraReaproveitavel = indSobraReaproveitavel;
	}

	public void setIndExigeObservacao(Boolean indExigeObservacao) {
		this.indExigeObservacao = indExigeObservacao;
	}

	public void setIndRevisaoCadastro(Boolean indRevisaoCadastro) {
		this.indRevisaoCadastro = indRevisaoCadastro;
	}

	public void setConcentracao(BigDecimal concentracao) {
		this.concentracao = concentracao;
	}

	public void setHrioInicioAdmSugerido(Date hrioInicioAdmSugerido) {
		this.hrioInicioAdmSugerido = hrioInicioAdmSugerido;
	}

	public void setObservacao(String observacao) {
		this.observacao = observacao;
	}

	public void setQtdeCaloriasGrama(BigDecimal qtdeCaloriasGrama) {
		this.qtdeCaloriasGrama = qtdeCaloriasGrama;
	}

	public void setDoseMaximaMgKg(BigDecimal doseMaximaMgKg) {
		this.doseMaximaMgKg = doseMaximaMgKg;
	}

	public void setFrequenciaUsual(Short frequenciaUsual) {
		this.frequenciaUsual = frequenciaUsual;
	}

	public void setIndicacoes(String indicacoes) {
		this.indicacoes = indicacoes;
	}

	public void setContraIndicacoes(String contraIndicacoes) {
		this.contraIndicacoes = contraIndicacoes;
	}

	public void setCuidadoConservacao(String cuidadoConservacao) {
		this.cuidadoConservacao = cuidadoConservacao;
	}

	public void setOrientacoesAdministracao(String orientacoesAdministracao) {
		this.orientacoesAdministracao = orientacoesAdministracao;
	}

	public void setIndDiluente(Boolean indDiluente) {
		this.indDiluente = indDiluente;
	}

	public void setIndGeraDispensacao(Boolean indGeraDispensacao) {
		this.indGeraDispensacao = indGeraDispensacao;
	}

	public void setLinkParecerIndeferido(String linkParecerIndeferido) {
		this.linkParecerIndeferido = linkParecerIndeferido;
	}

	public void setLinkProtocoloUtilizacao(String linkProtocoloUtilizacao) {
		this.linkProtocoloUtilizacao = linkProtocoloUtilizacao;
	}

	public void setDescricaoEtiquetaFrasco(String descricaoEtiquetaFrasco) {
		this.descricaoEtiquetaFrasco = descricaoEtiquetaFrasco;
	}

	public void setDescricaoEtiquetaSeringa(String descricaoEtiquetaSeringa) {
		this.descricaoEtiquetaSeringa = descricaoEtiquetaSeringa;
	}

	public void setIndInteresseCcih(Boolean indInteresseCcih) {
		this.indInteresseCcih = indInteresseCcih;
	}

	public void setIndGeladeira(Boolean indGeladeira) {
		this.indGeladeira = indGeladeira;
	}

	public void setIndFotosensibilidade(
			DominioIndFotoSensibilidade indFotosensibilidade) {
		this.indFotosensibilidade = indFotosensibilidade;
	}

	public void setIndUnidadeTempo(DominioIndUnidTempoMdto indUnidadeTempo) {
		this.indUnidadeTempo = indUnidadeTempo;
	}

	public void setTempoFotosensibilidade(Short tempoFotosensibilidade) {
		this.tempoFotosensibilidade = tempoFotosensibilidade;
	}

	public void setTipoQuimio(DominioQuimio tipoQuimio) {
		this.tipoQuimio = tipoQuimio;
	}

	public void setIndUnitariza(Boolean indUnitariza) {
		this.indUnitariza = indUnitariza;
	}
}
