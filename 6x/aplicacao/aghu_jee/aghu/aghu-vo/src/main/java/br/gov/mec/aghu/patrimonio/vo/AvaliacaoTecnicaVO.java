package br.gov.mec.aghu.patrimonio.vo;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import br.gov.mec.aghu.dominio.DominioAceiteTecnico;
import br.gov.mec.aghu.dominio.DominioSituacaoAceiteTecnico;
import br.gov.mec.aghu.model.FccCentroCustos;
import br.gov.mec.aghu.model.PtmBemPermanentes;
import br.gov.mec.aghu.model.PtmItemRecebProvisorios;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.ScoMarcaComercial;
import br.gov.mec.aghu.model.ScoMarcaModelo;
import br.gov.mec.aghu.core.utils.DateUtil;
/**
 * @author rafael.nascimento
 */
public class AvaliacaoTecnicaVO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7595081989690369935L;

	//Filtro
	private ScoMarcaComercial marcaComercial;
	private ScoMarcaModelo marcaModelo;
	private FccCentroCustos centroCusto;
	private RapServidores responsavelTecnico;
	private PtmBemPermanentes patrimonio;
	private PtmItemRecebProvisorios itemRecebimento;
	private DominioAceiteTecnico status;
	private DominioSituacaoAceiteTecnico situacao;
	private Date dtInicio;
	private Date dtFim;
	
	//Consulta Principal VO C11
	private Integer sceNrpSeq;//recebimento
	private Integer sceNrpItem;//itemRecebimento //TODO
	private Integer momCodAvaliacaoTec;//modelo
	private Integer mcmCodAvaliacaoTec;//marca
	private String justificativaAvaliacaoTec;
	private DominioAceiteTecnico indStatusAvaliacaoTec;
	private DominioSituacaoAceiteTecnico indSituacaoAvaliacaoTec;
	private Date dataCriacaoAvaliacaoTec;
	private Long numeroBem;//patrimonio
	private Integer codigoCentroCusto;
	private String descricaoCentroCusto;
	private Integer matricula;
	private Short vinCodigo;
	private String nomePessoaFisica;
	private Date dtInicioAvaliacaoTec;
	private Integer seqAvaliacaoTec; 
	private Integer avtSeq; 
	private String descricaoMarcaComercial;
	private String descricaoMarcaModelo;
	private List<DevolucaoBemPermanenteVO> numerosBens;
	private List<Long> numerosBensAvaliacaoTecnica;
	
	//Ordenação grid
	private String recebItemFormatado;
	private String patrimonioTruncado;
	
	
	public String obterRecebItemFormatado(){
		String descricao = "";
		descricao = getSceNrpSeq() + "/" + getSceNrpItem();
		return descricao;
	}
	
	//GETTERS AND SETTERS
	
	public ScoMarcaComercial getMarcaComercial() {
		return marcaComercial;
	}
	public ScoMarcaModelo getMarcaModelo() {
		return marcaModelo;
	}
	public FccCentroCustos getCentroCusto() {
		return centroCusto;
	}
	public RapServidores getResponsavelTecnico() {
		return responsavelTecnico;
	}
	public void setMarcaComercial(ScoMarcaComercial marcaComercial) {
		this.marcaComercial = marcaComercial;
	}
	public void setMarcaModelo(ScoMarcaModelo marcaModelo) {
		this.marcaModelo = marcaModelo;
	}
	public void setCentroCusto(FccCentroCustos centroCusto) {
		this.centroCusto = centroCusto;
	}
	public void setResponsavelTecnico(RapServidores responsavelTecnico) {
		this.responsavelTecnico = responsavelTecnico;
	}
	public PtmBemPermanentes getPatrimonio() {
		return patrimonio;
	}
	public void setPatrimonio(PtmBemPermanentes patrimonio) {
		this.patrimonio = patrimonio;
	}
	public DominioAceiteTecnico getStatus() {
		return status;
	}
	public void setStatus(DominioAceiteTecnico status) {
		this.status = status;
	}
	public DominioSituacaoAceiteTecnico getSituacao() {
		return situacao;
	}
	public void setSituacao(DominioSituacaoAceiteTecnico situacao) {
		this.situacao = situacao;
	}
	public Date getDtInicio() {
		return dtInicio;
	}
	public void setDtInicio(Date dtInicio) {
		this.dtInicio = dtInicio;
	}
	public Date getDtFim() {
		return dtFim;
	}
	public void setDtFim(Date dtFim) {
		this.dtFim = dtFim;
	}
	public PtmItemRecebProvisorios getItemRecebimento() {
		return itemRecebimento;
	}
	public void setItemRecebimento(PtmItemRecebProvisorios itemRecebimento) {
		this.itemRecebimento = itemRecebimento;
	}
	public Integer getSceNrpSeq() {
		return sceNrpSeq;
	}
	public void setSceNrpSeq(Integer sceNrpSeq) {
		this.sceNrpSeq = sceNrpSeq;
	}
	public Integer getSceNrpItem() {
		return sceNrpItem;
	}
	public void setSceNrpItem(Integer sceNrpItem) {
		this.sceNrpItem = sceNrpItem;
	}
	public Integer getMomCodAvaliacaoTec() {
		return momCodAvaliacaoTec;
	}
	public void setMomCodAvaliacaoTec(Integer momCodAvaliacaoTec) {
		this.momCodAvaliacaoTec = momCodAvaliacaoTec;
	}
	public Integer getMcmCodAvaliacaoTec() {
		return mcmCodAvaliacaoTec;
	}
	public void setMcmCodAvaliacaoTec(Integer mcmCodAvaliacaoTec) {
		this.mcmCodAvaliacaoTec = mcmCodAvaliacaoTec;
	}
	public String getJustificativaAvaliacaoTec() {
		return justificativaAvaliacaoTec;
	}
	public void setJustificativaAvaliacaoTec(String justificativaAvaliacaoTec) {
		this.justificativaAvaliacaoTec = justificativaAvaliacaoTec;
	}
	public DominioAceiteTecnico getIndStatusAvaliacaoTec() {
		return indStatusAvaliacaoTec;
	}
	public void setIndStatusAvaliacaoTec(DominioAceiteTecnico indStatusAvaliacaoTec) {
		this.indStatusAvaliacaoTec = indStatusAvaliacaoTec;
	}
	public DominioSituacaoAceiteTecnico getIndSituacaoAvaliacaoTec() {
		return indSituacaoAvaliacaoTec;
	}
	public void setIndSituacaoAvaliacaoTec(
			DominioSituacaoAceiteTecnico indSituacaoAvaliacaoTec) {
		this.indSituacaoAvaliacaoTec = indSituacaoAvaliacaoTec;
	}
	public Date getDataCriacaoAvaliacaoTec() {
		return dataCriacaoAvaliacaoTec;
	}
	public void setDataCriacaoAvaliacaoTec(Date dataCriacaoAvaliacaoTec) {
		this.dataCriacaoAvaliacaoTec = dataCriacaoAvaliacaoTec;
	}
	public String obterDataCriacaoAvaliacaoTecFormatada() {
		if (dataCriacaoAvaliacaoTec != null){
			return DateUtil.obterDataFormatada(dataCriacaoAvaliacaoTec, "dd/MM/yyyy HH:mm:ss");			
		}
		return StringUtils.EMPTY;
	}	
	public Long getNumeroBem() {
		return numeroBem;
	}
	public void setNumeroBem(Long numeroBem) {
		this.numeroBem = numeroBem;
	}
	public Integer getCodigoCentroCusto() {
		return codigoCentroCusto;
	}
	public void setCodigoCentroCusto(Integer codigoCentroCusto) {
		this.codigoCentroCusto = codigoCentroCusto;
	}
	public String getDescricaoCentroCusto() {
		return descricaoCentroCusto;
	}
	public void setDescricaoCentroCusto(String descricaoCentroCusto) {
		this.descricaoCentroCusto = descricaoCentroCusto;
	}
	public Integer getMatricula() {
		return matricula;
	}
	public void setMatricula(Integer matricula) {
		this.matricula = matricula;
	}
	public Short getVinCodigo() {
		return vinCodigo;
	}
	public void setVinCodigo(Short vinCodigo) {
		this.vinCodigo = vinCodigo;
	}
	public String getNomePessoaFisica() {
		return nomePessoaFisica;
	}
	public void setNomePessoaFisica(String nomePessoaFisica) {
		this.nomePessoaFisica = nomePessoaFisica;
	}
	public Date getDtInicioAvaliacaoTec() {
		return dtInicioAvaliacaoTec;
	}
	public void setDtInicioAvaliacaoTec(Date dtInicioAvaliacaoTec) {
		this.dtInicioAvaliacaoTec = dtInicioAvaliacaoTec;
	}
	public Integer getSeqAvaliacaoTec() {
		return seqAvaliacaoTec;
	}
	public void setSeqAvaliacaoTec(Integer seqAvaliacaoTec) {
		this.seqAvaliacaoTec = seqAvaliacaoTec;
	}
	public String getDescricaoMarcaComercial() {
		return descricaoMarcaComercial;
	}
	public void setDescricaoMarcaComercial(String descricaoMarcaComercial) {
		this.descricaoMarcaComercial = descricaoMarcaComercial;
	}
	public String getDescricaoMarcaModelo() {
		return descricaoMarcaModelo;
	}
	public void setDescricaoMarcaModelo(String descricaoMarcaModelo) {
		this.descricaoMarcaModelo = descricaoMarcaModelo;
	}
	public List<DevolucaoBemPermanenteVO> getNumerosBens() {
		return numerosBens;
	}
	public void setNumerosBens(List<DevolucaoBemPermanenteVO> numerosBens) {
		this.numerosBens = numerosBens;
	}

	//Consulta VO C14
	private Short vidaUtil;
	private Short tempoGarantia;
	private String descricaoMaterial;
	private Double valor;
	
	public Short getVidaUtil() {
		return vidaUtil;
	}
	public void setVidaUtil(Short vidaUtil) {
		this.vidaUtil = vidaUtil;
	}
	public Short getTempoGarantia() {
		return tempoGarantia;
	}
	public void setTempoGarantia(Short tempoGarantia) {
		this.tempoGarantia = tempoGarantia;
	}
	public String getDescricaoMaterial() {
		return descricaoMaterial;
	}
	public void setDescricaoMaterial(String descricaoMaterial) {
		this.descricaoMaterial = descricaoMaterial;
	}
	public Double getValor() {
		return valor;
	}
	public void setValor(Double valor) {
		this.valor = valor;
	}

	//Consulta VO C8
	private Integer irpNrpseq;   
	private Integer irpEslseq;     
	private Double irpValor;      
	private Integer irpnroitem;    
	private String matDescricao;      
	private Integer iafMcmCodigo;   
	private Integer iafMomSeqp;      
	private Integer iafMomMcmCodigo; 
	
	public Integer getIrpNrpseq() {
		return irpNrpseq;
	}
	public void setIrpNrpseq(Integer irpNrpseq) {
		this.irpNrpseq = irpNrpseq;
	}
	public Integer getIrpEslseq() {
		return irpEslseq;
	}
	public void setIrpEslseq(Integer irpEslseq) {
		this.irpEslseq = irpEslseq;
	}
	public Double getIrpValor() {
		return irpValor;
	}
	public void setIrpValor(Double irpValor) {
		this.irpValor = irpValor;
	}
	public Integer getIrpnroitem() {
		return irpnroitem;
	}
	public void setIrpnroitem(Integer irpnroitem) {
		this.irpnroitem = irpnroitem;
	}
	public String getMatDescricao() {
		return matDescricao;
	}
	public void setMatDescricao(String matDescricao) {
		this.matDescricao = matDescricao;
	}
	public Integer getIafMcmCodigo() {
		return iafMcmCodigo;
	}
	public void setIafMcmCodigo(Integer iafMcmCodigo) {
		this.iafMcmCodigo = iafMcmCodigo;
	}
	public Integer getIafMomSeqp() {
		return iafMomSeqp;
	}
	public void setIafMomSeqp(Integer iafMomSeqp) {
		this.iafMomSeqp = iafMomSeqp;
	}
	public Integer getIafMomMcmCodigo() {
		return iafMomMcmCodigo;
	}
	public void setIafMomMcmCodigo(Integer iafMomMcmCodigo) {
		this.iafMomMcmCodigo = iafMomMcmCodigo;
	}

	public enum Fields{
		SITUACAO("situacao"),
		STATUS("status"),
		DATA_INICIO("dtInicio"),
		DATA_FIM("dtFim"),
		MARCA_COMERCIAL("marcaComercial"),
		MARCA_MODELO("marcaModelo"),
		CENTRO_CUSTO("centroCusto"),
		RESPONSAVEL_TECNICO("responsavelTecnico"),
		PATRIMONIO("patrimonio"),
		ITEM_RECEBIMENTO("itemRecebimento"),
		SCE_NRP_SEQ("sceNrpSeq"),//RECEBIMENTO
		SCE_NRP_ITEM("sceNrpItem"),//ITEMRECEBIMENTO //TODO
		MOM_COD_AVALIACAO_TEC("momCodAvaliacaoTec"),//MODELO
		MCM_COD_AVALIACAO_TEC("mcmCodAvaliacaoTec"),//MARCA
		JUSTIFICATIVA_AVALIACAO_TEC("justificativaAvaliacaoTec"),
		IND_STATUS_AVALIACAO_TEC("indStatusAvaliacaoTec"),
		IND_SITUACAO_AVALIACAO_TEC("indSituacaoAvaliacaoTec"),
		DATA_CRIACAO_AVALIACAO_TEC("dataCriacaoAvaliacaoTec"),
		NUMERO_BEM("numeroBem"),//PATRIMONIO
		CODIGO_CENTRO_CUSTO("codigoCentroCusto"),
		DESCRICAO_CENTRO_CUSTO("descricaoCentroCusto"),
		MATRICULA_SERVIDOR("matricula"),
		VIN_CODIGO_SERVIDOR("vinCodigo"),
		NOME_PESSOA_FISICA("nomePessoaFisica"),
		DATA_INICIO_AVALIACAO_TEC("dtInicioAvaliacaoTec"),
		SEQ_AVALIACAO_TEC("seqAvaliacaoTec"),
		MARCA_COMERCIAL_DESCRICAO("descricaoMarcaComercial"), 
		MARCA_MODELO_DESCRICAO("descricaoMarcaModelo"),
		AVT_SEQ("avtSeq"),
		
		//Consulta VO C14
		VIDA_UTIL("vidaUtil"),
		TEMPO_GARANTIA("tempoGarantia"),
		DESCRICAO_MATERIAL("descricaoMaterial"),
		VALOR("valor"),
		
		//Consulta VO C8
		IRP_NRP_SEQ("irpNrpseq"),
	    IRP_ESL_SEQ("irpEslseq"),     
		IRP_VALOR("irpValor"),      
		IRP_NRO_ITEM("irpnroitem"),    
		MAT_DESCRICAO("matDescricao"),      
		IAF_MCM_CODIGO("iafMcmCodigo"),   
		IAF_MOM_SEQP("iafMomSeqp"),    
		IAF_MOM_MCM_CODIGO("iafMomMcmCodigo"),
		
		//Ordenação grid
		RECEB_ITEM_FORMATADO("recebItemFormatado"),
		PATRIMONIO_TRUNCADO("patrimonioTruncado")
		;
		
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
		umHashCodeBuilder.append(this.getSceNrpSeq());
		umHashCodeBuilder.append(this.getSceNrpItem());
		umHashCodeBuilder.append(this.getMomCodAvaliacaoTec());
		umHashCodeBuilder.append(this.getMcmCodAvaliacaoTec());
		umHashCodeBuilder.append(this.getJustificativaAvaliacaoTec());
		umHashCodeBuilder.append(this.getIndStatusAvaliacaoTec());
		umHashCodeBuilder.append(this.getIndSituacaoAvaliacaoTec());
		umHashCodeBuilder.append(this.getDataCriacaoAvaliacaoTec());
		umHashCodeBuilder.append(this.getNumeroBem());
		umHashCodeBuilder.append(this.getCodigoCentroCusto());
		umHashCodeBuilder.append(this.getDescricaoCentroCusto());
		umHashCodeBuilder.append(this.getMatricula());
		umHashCodeBuilder.append(this.getVinCodigo());
		umHashCodeBuilder.append(this.getNomePessoaFisica());
		umHashCodeBuilder.append(this.getDtInicioAvaliacaoTec());
		umHashCodeBuilder.append(this.getVidaUtil());
		umHashCodeBuilder.append(this.getTempoGarantia());
		umHashCodeBuilder.append(this.getDescricaoMaterial());
		umHashCodeBuilder.append(this.getValor());
		umHashCodeBuilder.append(this.getIrpNrpseq());
		umHashCodeBuilder.append(this.getIrpEslseq());
		umHashCodeBuilder.append(this.getIrpValor());
		umHashCodeBuilder.append(this.getIrpnroitem());
		umHashCodeBuilder.append(this.getMatDescricao());
		umHashCodeBuilder.append(this.getIafMcmCodigo());
		umHashCodeBuilder.append(this.getIafMomSeqp());
		umHashCodeBuilder.append(this.getIafMomMcmCodigo());
		umHashCodeBuilder.append(this.getSeqAvaliacaoTec());
		umHashCodeBuilder.append(this.getDescricaoMarcaComercial());
		umHashCodeBuilder.append(this.getDescricaoMarcaModelo());
		
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

		if (!(obj instanceof AvaliacaoTecnicaVO)) {
			return false;
		}

		AvaliacaoTecnicaVO other = (AvaliacaoTecnicaVO) obj;
		EqualsBuilder umEqualsBuilder = new EqualsBuilder();
		umEqualsBuilder.append(this.getSceNrpSeq(), other.getSceNrpSeq());
		umEqualsBuilder.append(this.getSceNrpItem(), other.getSceNrpItem());
		umEqualsBuilder.append(this.getMomCodAvaliacaoTec(), other.getMomCodAvaliacaoTec());
		umEqualsBuilder.append(this.getMcmCodAvaliacaoTec(), other.getMcmCodAvaliacaoTec());
		umEqualsBuilder.append(this.getJustificativaAvaliacaoTec(), other.getJustificativaAvaliacaoTec());
		umEqualsBuilder.append(this.getIndStatusAvaliacaoTec(), other.getIndStatusAvaliacaoTec());
		umEqualsBuilder.append(this.getIndSituacaoAvaliacaoTec(), other.getIndSituacaoAvaliacaoTec());
		umEqualsBuilder.append(this.getDataCriacaoAvaliacaoTec(), other.getDataCriacaoAvaliacaoTec());
		umEqualsBuilder.append(this.getNumeroBem(), other.getNumeroBem());
		umEqualsBuilder.append(this.getCodigoCentroCusto(), other.getCodigoCentroCusto());
		umEqualsBuilder.append(this.getDescricaoCentroCusto(), other.getDescricaoCentroCusto());
		umEqualsBuilder.append(this.getMatricula(), other.getMatricula());
		umEqualsBuilder.append(this.getVinCodigo(), other.getVinCodigo());
		umEqualsBuilder.append(this.getNomePessoaFisica(), other.getNomePessoaFisica());
		umEqualsBuilder.append(this.getDtInicioAvaliacaoTec(), other.getDtInicioAvaliacaoTec());
		umEqualsBuilder.append(this.getVidaUtil(), other.getVidaUtil());
		umEqualsBuilder.append(this.getTempoGarantia(), other.getTempoGarantia());
		umEqualsBuilder.append(this.getDescricaoMaterial(), other.getDescricaoMaterial());
		umEqualsBuilder.append(this.getValor(), other.getValor());
		umEqualsBuilder.append(this.getIrpNrpseq(), other.getIrpNrpseq());
		umEqualsBuilder.append(this.getIrpEslseq(), other.getIrpEslseq());
		umEqualsBuilder.append(this.getIrpValor(), other.getIrpValor());
		umEqualsBuilder.append(this.getIrpnroitem(), other.getIrpnroitem());
		umEqualsBuilder.append(this.getMatDescricao(), other.getMatDescricao());
		umEqualsBuilder.append(this.getIafMcmCodigo(), other.getIafMcmCodigo());
		umEqualsBuilder.append(this.getIafMomSeqp(), other.getIafMomSeqp());
		umEqualsBuilder.append(this.getIafMomMcmCodigo(), other.getIafMomMcmCodigo());
		umEqualsBuilder.append(this.getSeqAvaliacaoTec(), other.getSeqAvaliacaoTec());
		umEqualsBuilder.append(this.getDescricaoMarcaComercial(), other.getDescricaoMarcaComercial());
		umEqualsBuilder.append(this.getDescricaoMarcaModelo(), other.getDescricaoMarcaModelo());

		return umEqualsBuilder.isEquals();
	}

	public Integer getAvtSeq() {
		return avtSeq;
	}

	public void setAvtSeq(Integer avtSeq) {
		this.avtSeq = avtSeq;
	}

	public List<Long> getNumerosBensAvaliacaoTecnica() {
		return numerosBensAvaliacaoTecnica;
	}

	public void setNumerosBensAvaliacaoTecnica(
			List<Long> numerosBensAvaliacaoTecnica) {
		this.numerosBensAvaliacaoTecnica = numerosBensAvaliacaoTecnica;
	}

	public String getRecebItemFormatado() {
		return recebItemFormatado;
	}

	public void setRecebItemFormatado(String recebItemFormatado) {
		this.recebItemFormatado = recebItemFormatado;
	}

	public String getPatrimonioTruncado() {
		return patrimonioTruncado;
	}

	public void setPatrimonioTruncado(String patrimonioTruncado) {
		this.patrimonioTruncado = patrimonioTruncado;
	}
}