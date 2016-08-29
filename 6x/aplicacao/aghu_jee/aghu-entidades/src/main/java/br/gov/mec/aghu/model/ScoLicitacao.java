package br.gov.mec.aghu.model;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Set;

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
import javax.persistence.Transient;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.hibernate.annotations.Parameter;

import br.gov.mec.aghu.dominio.DominioCriterioJulgamentoOrcamento;
import br.gov.mec.aghu.dominio.DominioFrequenciaEntrega;
import br.gov.mec.aghu.dominio.DominioModalidadeEmpenho;
import br.gov.mec.aghu.dominio.DominioSituacaoLicitacao;
import br.gov.mec.aghu.dominio.DominioTipoLicitacao;
import br.gov.mec.aghu.dominio.DominioTipoPregao;
import br.gov.mec.aghu.core.persistence.BaseEntityNumero;
import br.gov.mec.aghu.core.utils.DateUtil;


@Entity
@Table(name = "SCO_LICITACOES", schema = "AGH")
@SequenceGenerator(name="scolicSq1", sequenceName="AGH.SCO_LIC_SQ1", allocationSize = 1)

public class ScoLicitacao extends BaseEntityNumero<Integer> implements Serializable , Cloneable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1396666139112497507L;
	private Integer numero;
	private String descricao;
	private Date dtDigitacao;
	private Date dtLimiteAceiteProposta;
	private Date dthrAberturaProposta;
	private Integer hrAberturaProposta;
	private Date dtPublicacao;
	private Date dtDivulgacaoPublicacao;
	private Boolean exclusao;
	private Date dtExclusao;
	private Integer artigoLicitacao;
	private String incisoArtigoLicitacao;
	private String obsLicitacao;
	private String obsQuadroPropostas;
	private String motivoExclusao;
	private Date dtEnvioParecTec;
	private Date dtEnvioComisLicit;
	private DominioSituacaoLicitacao situacao;
	private Integer numDocLicit;
	private Integer numEdital;
	private Integer anoComplemento;
	private Integer frequenciaEntrega;
	private DominioCriterioJulgamentoOrcamento critJulgOrcamento;
	private Date dtInicioFornecimento;
	private DominioModalidadeEmpenho modalidadeEmpenho;
	private Integer tempoPrevExecucao;
	private DominioTipoLicitacao tipo;
	private Integer tempoAtendimento;
	private DominioTipoPregao tipoPregao;
	private String incisoArtigoLicitacaoBkp;
	private ScoModalidadeLicitacao modalidadeLicitacao;
	private RapServidores servidorDigitado;
	private RapServidores servidorExcluido;
	private RapServidores servidorGestor;
	private List<ScoItemLicitacao> itensLicitacao;
	private Integer idEletronico;
	private Boolean indUrgente;
	private Boolean indEngenharia;
	private Date dtHrAberturaPropostaCompleta;
	private Integer varMaxima;
	private DominioFrequenciaEntrega tipoFreqEntrega;
	private String justificativa;
	private String periodoFornecimento;
	private Set<ScoPropostaFornecedor> scoPropostaFornecedores;
	
	private Date dtGeracaoArqRemessa;
	private String nomeArqRemessa;
	private Date dtLeituraArqRetorno;
	private String nomeArqRetorno;
	private String nomeArqCad;
	private String mlcCodigo;
	private Date dtLeituraArqCad;
	
	

	// construtores

	public ScoLicitacao() {
	}

	
	/*@Transient
	public List<ScoItemLicitacao> getItensLicitacaoList(){
		return new ArrayList<ScoItemLicitacao>(itensLicitacao);
	}*/
	
	// getters & setters
	@Id
	@Column(name = "NUMERO", length = 7, nullable = false)
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "scolicSq1")
	public Integer getNumero() {
		return this.numero;
	}

	public void setNumero(Integer numero) {
		this.numero = numero;
	}

	@Column(name = "DESCRICAO", length = 60, nullable = false)
	public String getDescricao() {
		return this.descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	@Column(name = "DT_DIGITACAO", nullable = false)
	@Temporal(TemporalType.TIMESTAMP)
	public Date getDtDigitacao() {
		return this.dtDigitacao;
	}

	public void setDtDigitacao(Date dtDigitacao) {
		this.dtDigitacao = dtDigitacao;
	}

	
	@Column(name = "DT_LIMITE_ACEITE_PROPOSTA", nullable = true)
	@Temporal(TemporalType.TIMESTAMP)
	public Date getDtLimiteAceiteProposta() {
		return this.dtLimiteAceiteProposta;
	}

	public void setDtLimiteAceiteProposta(Date dtLimiteAceiteProposta) {
		this.dtLimiteAceiteProposta = dtLimiteAceiteProposta;
	}


	
	@Column(name = "HR_ABERTURA_PROPOSTA", length = 4, nullable = true)
	public Integer getHrAberturaProposta() {
		return this.hrAberturaProposta;
	}

	public void setHrAberturaProposta(Integer hrAberturaProposta) {
		this.hrAberturaProposta = hrAberturaProposta;
	}

	@Column(name = "DT_ABERTURA_PROPOSTA", nullable = true)
	@Temporal(TemporalType.TIMESTAMP)
	public Date getDthrAberturaProposta() {
		return dthrAberturaProposta;
	}


	public void setDthrAberturaProposta(Date dthrAberturaProposta) {
		this.dthrAberturaProposta = dthrAberturaProposta;
	}


	@Column(name = "DT_PUBLICACAO", nullable = true)
	@Temporal(TemporalType.TIMESTAMP)
	public Date getDtPublicacao() {
		return this.dtPublicacao;
	}

	public void setDtPublicacao(Date dtPublicacao) {
		this.dtPublicacao = dtPublicacao;
	}

	@Column(name = "DT_DIVULGACAO_PUBLICACAO")
	@Temporal(TemporalType.TIMESTAMP)
	public Date getDtDivulgacaoPublicacao() {
		return this.dtDivulgacaoPublicacao;
	}

	public void setDtDivulgacaoPublicacao(Date dtDivulgacaoPublicacao) {
		this.dtDivulgacaoPublicacao = dtDivulgacaoPublicacao;
	}

	@Column(name = "IND_EXCLUSAO", length = 1, nullable = false)
	@org.hibernate.annotations.Type(type = "br.gov.mec.aghu.core.persistence.type.BooleanUserType")
	public Boolean getExclusao() {
		return exclusao;
	}

	public void setExclusao(Boolean exclusao) {
		this.exclusao = exclusao;
	}

	@Column(name = "DT_EXCLUSAO")
	@Temporal(TemporalType.TIMESTAMP)
	public Date getDtExclusao() {
		return this.dtExclusao;
	}

	public void setDtExclusao(Date dtExclusao) {
		this.dtExclusao = dtExclusao;
	}

	@Column(name = "ARTIGO_LICITACAO", length = 3)
	public Integer getArtigoLicitacao() {
		return this.artigoLicitacao;
	}

	public void setArtigoLicitacao(Integer artigoLicitacao) {
		this.artigoLicitacao = artigoLicitacao;
	}

	@Column(name = "INCISO_ARTIGO_LICITACAO", length = 2)
	public String getIncisoArtigoLicitacao() {
		return this.incisoArtigoLicitacao;
	}

	public void setIncisoArtigoLicitacao(String incisoArtigoLicitacao) {
		this.incisoArtigoLicitacao = incisoArtigoLicitacao;
	}

	@Column(name = "OBS_LICITACAO", length = 100)
	public String getObsLicitacao() {
		return this.obsLicitacao;
	}

	public void setObsLicitacao(String obsLicitacao) {
		this.obsLicitacao = obsLicitacao;
	}

	@Column(name = "OBS_QUADRO_PROPOSTAS", length = 100)
	public String getObsQuadroPropostas() {
		return this.obsQuadroPropostas;
	}

	public void setObsQuadroPropostas(String obsQuadroPropostas) {
		this.obsQuadroPropostas = obsQuadroPropostas;
	}

	@Column(name = "MOTIVO_EXCLUSAO", length = 60)
	public String getMotivoExclusao() {
		return this.motivoExclusao;
	}

	public void setMotivoExclusao(String motivoExclusao) {
		this.motivoExclusao = motivoExclusao;
	}

	@Column(name = "DT_ENVIO_PAREC_TEC")
	@Temporal(TemporalType.TIMESTAMP)
	public Date getDtEnvioParecTec() {
		return this.dtEnvioParecTec;
	}

	public void setDtEnvioParecTec(Date dtEnvioParecTec) {
		this.dtEnvioParecTec = dtEnvioParecTec;
	}

	@Column(name = "DT_ENVIO_COMIS_LICIT")
	@Temporal(TemporalType.TIMESTAMP)
	public Date getDtEnvioComisLicit() {
		return this.dtEnvioComisLicit;
	}

	public void setDtEnvioComisLicit(Date dtEnvioComisLicit) {
		this.dtEnvioComisLicit = dtEnvioComisLicit;
	}

	@Column(name = "IND_SITUACAO", length = 2, nullable = false)
	@Enumerated(EnumType.STRING)
	public DominioSituacaoLicitacao getSituacao() {
		return situacao;
	}

	public void setSituacao(DominioSituacaoLicitacao situacao) {
		this.situacao = situacao;
	}

	@Column(name = "NUM_DOC_LICIT", length = 7)
	public Integer getNumDocLicit() {
		return this.numDocLicit;
	}

	public void setNumDocLicit(Integer numDocLicit) {
		this.numDocLicit = numDocLicit;
	}

	@Column(name = "NUM_EDITAL", length = 7)
	public Integer getNumEdital() {
		return this.numEdital;
	}

	public void setNumEdital(Integer numEdital) {
		this.numEdital = numEdital;
	}

	@Column(name = "ANO_COMPLEMENTO", length = 4)
	public Integer getAnoComplemento() {
		return this.anoComplemento;
	}

	public void setAnoComplemento(Integer anoComplemento) {
		this.anoComplemento = anoComplemento;
	}

	@Column(name = "FREQUENCIA_ENTREGA", length = 3)
	public Integer getFrequenciaEntrega() {
		return this.frequenciaEntrega;
	}

	public void setFrequenciaEntrega(Integer frequenciaEntrega) {
		this.frequenciaEntrega = frequenciaEntrega;
	}

	@Column(name = "CRIT_JULG_ORCAMENTO", length = 1)
	@Enumerated(EnumType.STRING)
	public DominioCriterioJulgamentoOrcamento getCritJulgOrcamento() {
		return this.critJulgOrcamento;
	}

	public void setCritJulgOrcamento(DominioCriterioJulgamentoOrcamento critJulgOrcamento) {
		this.critJulgOrcamento = critJulgOrcamento;
	}

	@Column(name = "DT_INICIO_FORNECIMENTO")
	@Temporal(TemporalType.TIMESTAMP)
	public Date getDtInicioFornecimento() {
		return this.dtInicioFornecimento;
	}

	public void setDtInicioFornecimento(Date dtInicioFornecimento) {
		this.dtInicioFornecimento = dtInicioFornecimento;
	}

	@Column(name = "MODALIDADE_EMPENHO", length = 1)
	@org.hibernate.annotations.Type(parameters = { @Parameter(name = "enumClassName", value = "br.gov.mec.aghu.dominio.DominioModalidadeEmpenho") }, type = "br.gov.mec.aghu.core.model.jpa.DominioUserType")
	public DominioModalidadeEmpenho getModalidadeEmpenho() {
		return this.modalidadeEmpenho;
	}

	public void setModalidadeEmpenho(DominioModalidadeEmpenho modalidadeEmpenho) {
		this.modalidadeEmpenho = modalidadeEmpenho;
	}

	@Column(name = "TEMPO_PREV_EXECUCAO", length = 5)
	public Integer getTempoPrevExecucao() {
		return this.tempoPrevExecucao;
	}

	public void setTempoPrevExecucao(Integer tempoPrevExecucao) {
		this.tempoPrevExecucao = tempoPrevExecucao;
	}

	@Column(name = "TIPO", length = 2)
	@Enumerated(EnumType.STRING)
	public DominioTipoLicitacao getTipo() {
		return this.tipo;
	}

	public void setTipo(DominioTipoLicitacao tipo) {
		this.tipo = tipo;
	}

	@Column(name = "TEMPO_ATENDIMENTO", length = 5)
	public Integer getTempoAtendimento() {
		return this.tempoAtendimento;
	}

	public void setTempoAtendimento(Integer tempoAtendimento) {
		this.tempoAtendimento = tempoAtendimento;
	}

	@Column(name = "IND_TIPO_PREGAO", length = 1)
	@org.hibernate.annotations.Type( type = "br.gov.mec.aghu.core.model.jpa.DominioStringUserType" , parameters = @Parameter(name="enumClassName", value="br.gov.mec.aghu.dominio.DominioTipoPregao"))
	public DominioTipoPregao getTipoPregao() {
		return tipoPregao;
	}

	public void setTipoPregao(DominioTipoPregao tipoPregao) {
		this.tipoPregao = tipoPregao;
	}

	@Column(name = "INCISO_ARTIGO_LICITACAO_BKP", length = 2)
	public String getIncisoArtigoLicitacaoBkp() {
		return this.incisoArtigoLicitacaoBkp;
	}

	public void setIncisoArtigoLicitacaoBkp(String incisoArtigoLicitacaoBkp) {
		this.incisoArtigoLicitacaoBkp = incisoArtigoLicitacaoBkp;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "MLC_CODIGO", referencedColumnName = "CODIGO")
	public ScoModalidadeLicitacao getModalidadeLicitacao() {
		return modalidadeLicitacao;
	}

	public void setModalidadeLicitacao(ScoModalidadeLicitacao modalidadeLicitacao) {
		this.modalidadeLicitacao = modalidadeLicitacao;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns({
			@JoinColumn(name = "SER_MATRICULA_DIGITADA", referencedColumnName = "MATRICULA"),
			@JoinColumn(name = "SER_VIN_CODIGO_DIGITADA", referencedColumnName = "VIN_CODIGO") })
	public RapServidores getServidorDigitado() {
		return servidorDigitado;
	}

	public void setServidorDigitado(RapServidores servidorDigitado) {
		this.servidorDigitado = servidorDigitado;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns({
			@JoinColumn(name = "SER_MATRICULA_EXCLUIDA", referencedColumnName = "MATRICULA"),
			@JoinColumn(name = "SER_VIN_CODIGO_EXCLUIDA", referencedColumnName = "VIN_CODIGO") })
	public RapServidores getServidorExcluido() {
		return servidorExcluido;
	}

	public void setServidorExcluido(RapServidores servidorExcluido) {
		this.servidorExcluido = servidorExcluido;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns({
			@JoinColumn(name = "SER_MATRICULA_GESTOR", referencedColumnName = "MATRICULA"),
			@JoinColumn(name = "SER_VIN_CODIGO_GESTOR", referencedColumnName = "VIN_CODIGO") })
	public RapServidores getServidorGestor() {
		return servidorGestor;
	}

	public void setServidorGestor(RapServidores servidorGestor) {
		this.servidorGestor = servidorGestor;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "licitacao")
	public List<ScoItemLicitacao> getItensLicitacao() {
		return itensLicitacao;
	}

	public void setItensLicitacao(List<ScoItemLicitacao> itensLicitacao) {
		this.itensLicitacao = itensLicitacao;
	}

	@Column(name = "ID_ELETRONICO", length = 7)
	public Integer getIdEletronico() {
		return idEletronico;
	}


	public void setIdEletronico(Integer idEletronico) {
		this.idEletronico = idEletronico;
	}

	@Column(name = "IND_URGENTE")
	@org.hibernate.annotations.Type(type = "br.gov.mec.aghu.core.persistence.type.BooleanUserType")
	public Boolean getIndUrgente() {
		return indUrgente;
	}


	public void setIndUrgente(Boolean indUrgente) {
		this.indUrgente = indUrgente;
	}

	@Column(name = "IND_ENGENHARIA")
	@org.hibernate.annotations.Type(type = "br.gov.mec.aghu.core.persistence.type.BooleanUserType")
	public Boolean getIndEngenharia() {
		return indEngenharia;
	}


	public void setIndEngenharia(Boolean indEngenharia) {
		this.indEngenharia = indEngenharia;
	}


	@Column(name = "VARIACAO_MAXIMA")
	public Integer getVarMaxima() {
		return varMaxima;
	}


	public void setVarMaxima(Integer varMaxima) {
		this.varMaxima = varMaxima;
	}

	@Column(name = "IND_FREQ_ENTREGA", length = 1)
	@Enumerated(EnumType.ORDINAL)
	public DominioFrequenciaEntrega getTipoFreqEntrega() {
		return tipoFreqEntrega;
	}


	public void setTipoFreqEntrega(DominioFrequenciaEntrega tipoFreqEntrega) {
		this.tipoFreqEntrega = tipoFreqEntrega;
	}

	@Column(name = "JUSTIFICATIVA", length = 500)
	public String getJustificativa() {
		return justificativa;
	}


	public void setJustificativa(String justificativa) {
		this.justificativa = justificativa;
	}

	@Column(name = "PERIODO_FORNECIMENTO", length = 500)
	public String getPeriodoFornecimento() {
		return periodoFornecimento;
	}


	public void setPeriodoFornecimento(String periodoFornecimento) {
		this.periodoFornecimento = periodoFornecimento;
	}
	
	@Column(name = "MLC_CODIGO", insertable = false, updatable = false)
	public String getMlcCodigo() {
		return mlcCodigo;
	}

	public void setMlcCodigo(String mlcCodigo) {
		this.mlcCodigo = mlcCodigo;
	}
	//
	@Column(name = "DT_GERACAO_ARQ_REM")
	@Temporal(TemporalType.TIMESTAMP)
	public Date getDtGeracaoArqRemessa() {
		return dtGeracaoArqRemessa;
	}

	public void setDtGeracaoArqRemessa(Date dtGeracaoArqRemessa) {
		this.dtGeracaoArqRemessa = dtGeracaoArqRemessa;
	}

	@Column(name = "NOME_ARQ_REM", length = 40)
	public String getNomeArqRemessa() {
		return nomeArqRemessa;
	}


	public void setNomeArqRemessa(String nomeArqRemessa) {
		this.nomeArqRemessa = nomeArqRemessa;
	}
	
	@Column(name = "DT_LEITURA_ARQ_RET")
	@Temporal(TemporalType.TIMESTAMP)
	public Date getDtLeituraArqRetorno() {
		return dtLeituraArqRetorno;
	}


	public void setDtLeituraArqRetorno(Date dtLeituraArqRetorno) {
		this.dtLeituraArqRetorno = dtLeituraArqRetorno;
	}

	@Column(name = "NOME_ARQ_RET", length = 40)
	public String getNomeArqRetorno() {
		return nomeArqRetorno;
	}


	public void setNomeArqRetorno(String nomeArqRetorno) {
		this.nomeArqRetorno = nomeArqRetorno;
	}
	
	@Column(name = "NOME_ARQ_CAD", length = 40)
	public String getNomeArqCad() {
		return nomeArqCad;
	}

	public void setNomeArqCad(String nomeArqCad) {
		this.nomeArqCad = nomeArqCad;
	}
	
	//
	@Column(name = "DT_LEITURA_ARQ_CAD")
	@Temporal(TemporalType.TIMESTAMP)
	public Date getDtLeituraArqCad() {
		return dtLeituraArqCad;
	}

	public void setDtLeituraArqCad(Date dtLeituraArqCad) {
		this.dtLeituraArqCad = dtLeituraArqCad;
	}
	
	// outros
	@Override
	public String toString() {
		return new ToStringBuilder(this).append("numero", this.numero)
				.toString();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((numero == null) ? 0 : numero.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj){
			return true;
		}
		if (obj == null){
			return false;
		}
		if (getClass() != obj.getClass()){
			return false;
		}
		ScoLicitacao other = (ScoLicitacao) obj;
		if (numero == null) {
			if (other.numero != null){
				return false;
			}
		} else if (!numero.equals(other.numero)){
			return false;
		}
		return true;
	}
	
	@Override
	public Object clone() throws CloneNotSupportedException{
		
		ScoLicitacao licitacao = null;
		licitacao = (ScoLicitacao) super.clone();
		licitacao.modalidadeLicitacao = (ScoModalidadeLicitacao) this.modalidadeLicitacao.clone();		   
		return licitacao;
	}

	public enum Fields {
		NUMERO("numero"), 
		DESCRICAO("descricao"), 
		DT_DIGITACAO("dtDigitacao"), 
		DT_LIMITE_ACEITE_PROPOSTA("dtLimiteAceiteProposta"), 
		DT_ABERTURA_PROPOSTA("dthrAberturaProposta"), 
		HR_ABERTURA_PROPOSTA("hrAberturaProposta"), 
		DT_PUBLICACAO("dtPublicacao"), 
		DT_DIVULGACAO_PUBLICACAO("dtDivulgacaoPublicacao"), 
		IND_EXCLUSAO("exclusao"), 
		DT_EXCLUSAO("dtExclusao"), 
		ARTIGO_LICITACAO("artigoLicitacao"), 
		INCISO_ARTIGO_LICITACAO("incisoArtigoLicitacao"), 
		OBS_LICITACAO("obsLicitacao"), 
		OBS_QUADRO_PROPOSTAS("obsQuadroPropostas"), 
		MOTIVO_EXCLUSAO("motivoExclusao"), 
		DT_ENVIO_PAREC_TEC("dtEnvioParecTec"), 
		DT_ENVIO_COMIS_LICIT("dtEnvioComisLicit"), 
		IND_SITUACAO("situacao"), 
		NUM_DOC_LICIT("numDocLicit"), 
		NUM_EDITAL("numEdital"), 
		ANO_COMPLEMENTO("anoComplemento"), 
		FREQUENCIA_ENTREGA("frequenciaEntrega"), 
		CRIT_JULG_ORCAMENTO("critJulgOrcamento"), 
		DT_INICIO_FORNECIMENTO("dtInicioFornecimento"), 
		MODALIDADE_EMPENHO("modalidadeEmpenho"), 
		TEMPO_PREV_EXECUCAO("tempoPrevExecucao"), 
		TIPO("tipo"), 
		TEMPO_ATENDIMENTO("tempoAtendimento"), 
		IND_TIPO_PREGAO("tipoPregao"), 
		INCISO_ARTIGO_LICITACAO_BKP("incisoArtigoLicitacaoBkp"), 
		MODALIDADE_LICITACAO("modalidadeLicitacao"),
		MODALIDADE_LICITACAO_CODIGO("modalidadeLicitacao.codigo"),
		SERVIDOR_DIGITADO("servidorDigitado"), 
		MATRICULA_SERVIDOR_DIGITADO("servidorDigitado.id.matricula"),
		VINCULO_SERVIDOR_DIGITADO("servidorDigitado.id.vinCodigo"),
		SERVIDOR_EXCLUIDO("servidorExcluido"), 
		SERVIDOR_GESTOR("servidorGestor"),
		SERVIDOR_GESTOR_MATRICULA("servidorGestor.id.matricula"),
		ITENS_LICITACAO("itensLicitacao"),
		EXCLUSAO("exclusao"),
		ID_ELETRONICO("idEletronico"),
		IND_URGENTE("indUrgente"),
		MLC_CODIGO("modalidadeLicitacao.codigo"),
		SCO_PROPOSTA_FORNECEDOR("scoPropostaFornecedores"),
		MLCCODIGO("mlcCodigo"),
		DT_GERACAO_ARQ_REMESSA("dtGeracaoArqRemessa"),
		NOME_ARQ_REMESSA("nomeArqRemessa"),
		DT_LEITURA_ARQ_RETORNO("dtLeituraArqRetorno"),
		NOME_ARQ_RETORNO("nomeArqRetorno"),
		NOME_ARQ_CAD("nomeArqCad"),
		DT_LEITURA_ARQ_CAD("dtLeituraArqCad");

		private String field;

		private Fields(String field) {
			this.field = field;
		}
		
		@Override
		public String toString() {
			return this.field;
		}
	}

	@Transient
	public RapServidores getGestorExibicao() {
		if(this.servidorGestor == null) {
			if(this.getModalidadeLicitacao() != null && ("TP".equals(this.getModalidadeLicitacao().getCodigo()) || "CC".equals(this.getModalidadeLicitacao().getCodigo()))) {
				RapServidores serv = new RapServidores();
				serv.setId(new RapServidoresId(null, null));
				RapPessoasFisicas pf = new RapPessoasFisicas();
				pf.setNome("Comissão Licitações");
				serv.setPessoaFisica(pf);
				return serv;
			}
			else {
				RapServidores serv = new RapServidores();
				serv.setId(new RapServidoresId(null, null));
				RapPessoasFisicas pf = new RapPessoasFisicas();
				pf.setNome("Comprador Gerador");
				serv.setPessoaFisica(pf);
				return serv;
			}
		}
		else {
			return servidorGestor;
		}
	}
	
	@Transient
	public String getNumeroDescricao() {
		return this.getNumero() + " - " + this.getDescricao();
	}
	
	@Transient
	public Date getDtHrAberturaPropostaCompleta() {
		return dtHrAberturaPropostaCompleta;
	}
	
	public void setDtHrAberturaPropostaCompleta(Date dtHrAberturaProposta) {
		this.dtHrAberturaPropostaCompleta = dtHrAberturaProposta;
		//Seta valores dos campos 
		//seta a hora
		String valor = DateUtil.dataToString(dtHrAberturaProposta, "HH:mm");
		String[] arrayHora = valor.split(":");
		Integer horaInt = null;
		if (arrayHora.length == 2) {
			horaInt = Integer.valueOf(arrayHora[0])*100 + Integer.valueOf(arrayHora[1]); 
		}
		this.hrAberturaProposta = horaInt;
		//seta a data
		this.dthrAberturaProposta = dtHrAberturaProposta;
	}


	@OneToMany(fetch = FetchType.LAZY, mappedBy = "licitacao")
	public Set<ScoPropostaFornecedor> getScoPropostaFornecedores() {
		return scoPropostaFornecedores;
	}

	public void setScoPropostaFornecedores(Set<ScoPropostaFornecedor> scoPropostaFornecedores) {
		this.scoPropostaFornecedores = scoPropostaFornecedores;
	}


}
