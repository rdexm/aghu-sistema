package br.gov.mec.aghu.blococirurgico.vo;

import java.math.BigDecimal;

import org.apache.commons.lang3.builder.CompareToBuilder;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.FatItensProcedHospitalar;
import br.gov.mec.aghu.model.FatProcedHospInternos;
import br.gov.mec.aghu.model.ScoMaterial;

public class ItemProcedimentoHospitalarMaterialVO implements Comparable<ItemProcedimentoHospitalarMaterialVO> {

	private Short iphCompPho;
	private Integer iphCompSeq;
	private String iphCompDscr;
	private Long iphCompCod;
	private Short qtdMaxima;
	private Short maxQtdConta;
	private BigDecimal vlrServHospitalar;
	private BigDecimal vlrServProfissional;
	private BigDecimal vlrSadt;
	private BigDecimal vlrProcedimento;
	private BigDecimal vlrAnestesia;
	private Integer codigoMat;
	private String nomeMat;
	private Integer cmpPhiSeq;
	private Integer mioQtdSolic;
	private String indRequerido;
	private String indCompativel;
	private String indAutorizado;
	private String indConsumido;
	private String solcNovoMat;
	private Short ropSeq;
	private Integer iroSeq;
	private Integer mioSeq;
	private FatProcedHospInternos cmpPhi;
	private ScoMaterial material;
	private FatItensProcedHospitalar cmp;
	private DominioSituacao cmpPhiSituacao;
	private DominioSituacao matSituacao;
	private Integer codigoMarcasComerciais;
	private String descricaoMarcasComerciais;
	private String unidadeMaterial;
	private Double valorUnitario;

	public ItemProcedimentoHospitalarMaterialVO() {}
	
	public Short getIphCompPho() {
		return iphCompPho;
	}

	public void setIphCompPho(Short iphCompPho) {
		this.iphCompPho = iphCompPho;
	}

	public Integer getIphCompSeq() {
		return iphCompSeq;
	}

	public void setIphCompSeq(Integer iphCompSeq) {
		this.iphCompSeq = iphCompSeq;
	}

	public String getIphCompDscr() {
		return iphCompDscr;
	}

	public void setIphCompDscr(String iphCompDscr) {
		this.iphCompDscr = iphCompDscr;
	}

	public Long getIphCompCod() {
		return iphCompCod;
	}

	public void setIphCompCod(Long iphCompCod) {
		this.iphCompCod = iphCompCod;
	}

	public Short getQtdMaxima() {
		return qtdMaxima;
	}

	public void setQtdMaxima(Short qtdMaxima) {
		this.qtdMaxima = qtdMaxima;
	}

	public Short getMaxQtdConta() {
		return maxQtdConta;
	}

	public void setMaxQtdConta(Short maxQtdConta) {
		this.maxQtdConta = maxQtdConta;
	}

	public BigDecimal getVlrServHospitalar() {
		return vlrServHospitalar;
	}

	public void setVlrServHospitalar(BigDecimal vlrServHospitalar) {
		this.vlrServHospitalar = vlrServHospitalar;
	}

	public BigDecimal getVlrServProfissional() {
		return vlrServProfissional;
	}

	public void setVlrServProfissional(BigDecimal vlrServProfissional) {
		this.vlrServProfissional = vlrServProfissional;
	}

	public BigDecimal getVlrSadt() {
		return vlrSadt;
	}

	public void setVlrSadt(BigDecimal vlrSadt) {
		this.vlrSadt = vlrSadt;
	}

	public BigDecimal getVlrProcedimento() {
		return vlrProcedimento;
	}

	public void setVlrProcedimento(BigDecimal vlrProcedimento) {
		this.vlrProcedimento = vlrProcedimento;
	}

	public BigDecimal getVlrAnestesia() {
		return vlrAnestesia;
	}

	public void setVlrAnestesia(BigDecimal vlrAnestesia) {
		this.vlrAnestesia = vlrAnestesia;
	}

	public Integer getCodigoMat() {
		return codigoMat;
	}

	public void setCodigoMat(Integer codigoMat) {
		this.codigoMat = codigoMat;
	}

	public String getNomeMat() {
		return nomeMat;
	}

	public void setNomeMat(String nomeMat) {
		this.nomeMat = nomeMat;
	}

	public Integer getCmpPhiSeq() {
		return cmpPhiSeq;
	}

	public void setCmpPhiSeq(Integer cmpPhiSeq) {
		this.cmpPhiSeq = cmpPhiSeq;
	}

	public Integer getMioQtdSolic() {
		return mioQtdSolic;
	}

	public void setMioQtdSolic(Integer mioQtdSolic) {
		this.mioQtdSolic = mioQtdSolic;
	}
	
	public String getIndRequerido() {
		return indRequerido;
	}

	public void setIndRequerido(String indRequerido) {
		this.indRequerido = indRequerido;
	}

	public String getIndCompativel() {
		return indCompativel;
	}

	public void setIndCompativel(String indCompativel) {
		this.indCompativel = indCompativel;
	}

	public String getIndAutorizado() {
		return indAutorizado;
	}

	public void setIndAutorizado(String indAutorizado) {
		this.indAutorizado = indAutorizado;
	}

	public String getIndConsumido() {
		return indConsumido;
	}

	public void setIndConsumido(String indConsumido) {
		this.indConsumido = indConsumido;
	}

	public String getSolcNovoMat() {
		return solcNovoMat;
	}

	public void setSolcNovoMat(String solcNovoMat) {
		this.solcNovoMat = solcNovoMat;
	}

	public Short getRopSeq() {
		return ropSeq;
	}

	public void setRopSeq(Short ropSeq) {
		this.ropSeq = ropSeq;
	}

	public Integer getIroSeq() {
		return iroSeq;
	}

	public void setIroSeq(Integer iroSeq) {
		this.iroSeq = iroSeq;
	}
	
	public Integer getMioSeq() {
		return mioSeq;
	}

	public void setMioSeq(Integer mioSeq) {
		this.mioSeq = mioSeq;
	}

	public FatProcedHospInternos getCmpPhi() {
		return cmpPhi;
	}

	public void setCmpPhi(FatProcedHospInternos cmpPhi) {
		this.cmpPhi = cmpPhi;
	}

	public ScoMaterial getMaterial() {
		return material;
	}

	public void setMaterial(ScoMaterial material) {
		this.material = material;
	}

	public FatItensProcedHospitalar getCmp() {
		return cmp;
	}

	public void setCmp(FatItensProcedHospitalar cmp) {
		this.cmp = cmp;
	}

	public DominioSituacao getCmpPhiSituacao() {
		return cmpPhiSituacao;
	}

	public void setCmpPhiSituacao(DominioSituacao cmpPhiSituacao) {
		this.cmpPhiSituacao = cmpPhiSituacao;
	}

	public DominioSituacao getMatSituacao() {
		return matSituacao;
	}

	public void setMatSituacao(DominioSituacao matSituacao) {
		this.matSituacao = matSituacao;
	}

	public Integer getCodigoMarcasComerciais() {
		return codigoMarcasComerciais;
	}

	public void setCodigoMarcasComerciais(Integer codigoMarcasComerciais) {
		this.codigoMarcasComerciais = codigoMarcasComerciais;
	}

	public String getDescricaoMarcasComerciais() {
		return descricaoMarcasComerciais;
	}

	public void setDescricaoMarcasComerciais(String descricaoMarcasComerciais) {
		this.descricaoMarcasComerciais = descricaoMarcasComerciais;
	}

	public String getUnidadeMaterial() {
		return unidadeMaterial;
	}

	public void setUnidadeMaterial(String unidadeMaterial) {
		this.unidadeMaterial = unidadeMaterial;
	}

	public Double getValorUnitario() {
		return valorUnitario;
	}

	public void setValorUnitario(Double valorUnitario) {
		this.valorUnitario = valorUnitario;
	}


	public enum Fields {
		IPH_COMP_PHO("iphCompPho"),
		IPH_COMP_SEQ("iphCompSeq"),
		IPH_COMP_DSCR("iphCompDscr"),
		QTD_MAXIMA("qtdMaxima"),
		MAX_QTD_CONTA("maxQtdConta"),
		VLR_SERV_HOSPITALAR("vlrServHospitalar"),
		VLR_SERV_PROFISSIONAL("vlrServProfissional"),
		VLR_SADT("vlrSadt"),
		VLR_PROCEDIMENTO("vlrProcedimento"),
		VLR_ANESTESIA("vlrAnestesia"),
		CODIGO_MAT("codigoMat"),
		NOME_MAT("nomeMat"),
		CMP_PHI_SEQ("cmpPhiSeq"),
		MIO_QTD_SOLIC("mioQtdSolic"), CMP_PHI("cmpPhi"), MAT("material"), 
		IPH_COMP_COD("iphCompCod"), CMP("cmp"), MAT_SITUACAO("matSituacao"), CMP_PHI_SITUACAO("cmpPhiSituacao"),
		VALOR_UNITARIO("valorUnitario");

		private String fields;

		private Fields(final String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return this.fields;
		}
	}
	
	@Override
	public int compareTo(ItemProcedimentoHospitalarMaterialVO that) {
		if (that == null) {
			return 0;
		}
		if (this.equals(that)) {
			return 0;
		}
		final CompareToBuilder compareBuilder = new CompareToBuilder();
		compareBuilder.append(getIphCompCod(), that.getIphCompCod());
		compareBuilder.append(getCodigoMat(), that.getCodigoMat());
		return compareBuilder.toComparison();
	}

}
