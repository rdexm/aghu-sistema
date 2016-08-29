package br.gov.mec.aghu.perinatologia.vo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import br.gov.mec.aghu.dominio.DominioCor;
import br.gov.mec.aghu.dominio.DominioDestinoRecemNascido;
import br.gov.mec.aghu.dominio.DominioGastrAspecto;
import br.gov.mec.aghu.dominio.DominioSexo;

public class RecemNascidoVO implements Serializable {

	private static final long serialVersionUID = 4058000195116564608L;
	
	
	// atributos para contrele persitencia
	private Integer gsoPacCodigoPK;
	private Short gsoSeqpPK;
	private Byte seqpPK;
	private Byte idFake;

	// 
	private Date dataHora;
	private String nome;
	private DominioSexo sexo;
	private DominioCor cor;
	private BigDecimal peso;
	private String strProntuario;
	private Integer prontuario;

	// dados recem nascido
	private Byte apagarUmMinuto;
	private Byte apagarCincoMinuto;
	private Byte apagarDezMinuto;
	private Byte qtdDiasRupturaBolsa;
	private Byte qtdHorasRupturaBolsa;
	private Byte qtdMinutosRupturaBolsa;
	private Boolean coletadoSangueCordao;

	private Boolean aspiracao;
	private Boolean aspiracaoTet;
	private Boolean indO2Inalatorio;
	private Boolean indVentilacaoPorMascaraBalao;
	private Boolean indVentilacaoPorMascaraBabyPuff;
	private Boolean indVentilacaoTetBalao;
	private Boolean indVentilacaoTetBabyPuff;
	private Boolean indCateterismoVenoso;
	private Boolean indMassCardiacaExt;
	private Boolean indCpap;
	private Boolean indUrinou;
	private Boolean indEvacuou;
	private Boolean indLavadoGastrico;
	private Boolean indAmamentado;
	private DominioDestinoRecemNascido destinoRecemnascido;
	private Short tempoClampeamentoCordao;
	private Short temperaturaSalaParto;
	private Boolean indContatoPele;

	private Short aspGastrVol;
	private DominioGastrAspecto aspGastrAspecto;
	private String observacao;

	// parametros
	private Integer pacCodigo;
	private Short seqp;
	private String nomePaciente;
	private Integer pacCodigoRecemNascido;
	
	//#41973 - RN02
	private List<Integer> seqMedicamentosExclusao;
	private List<Integer> seqMedicamentosInsercao;
	
	public Integer getGsoPacCodigoPK() {
		return gsoPacCodigoPK;
	}

	public void setGsoPacCodigoPK(Integer gsoPacCodigoPK) {
		this.gsoPacCodigoPK = gsoPacCodigoPK;
	}

	public Short getGsoSeqpPK() {
		return gsoSeqpPK;
	}

	public void setGsoSeqpPK(Short gsoSeqpPK) {
		this.gsoSeqpPK = gsoSeqpPK;
	}

	public Byte getSeqpPK() {
		return seqpPK;
	}

	public void setSeqpPK(Byte seqpPK) {
		this.seqpPK = seqpPK;
	}

	public Byte getIdFake() {
		return idFake;
	}

	public void setIdFake(Byte idFake) {
		this.idFake = idFake;
	}

	public Date getDataHora() {
		return dataHora;
	}

	public void setDataHora(Date dataHora) {
		this.dataHora = dataHora;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public DominioSexo getSexo() {
		return sexo;
	}

	public void setSexo(DominioSexo sexo) {
		this.sexo = sexo;
	}

	public DominioCor getCor() {
		return cor;
	}

	public void setCor(DominioCor cor) {
		this.cor = cor;
	}

	public BigDecimal getPeso() {
		return peso;
	}

	public void setPeso(BigDecimal peso) {
		this.peso = peso;
	}

	public String getStrProntuario() {
		return strProntuario;
	}

	public void setStrProntuario(String strProntuario) {
		this.strProntuario = strProntuario;
	}

	public Integer getProntuario() {
		return prontuario;
	}

	public void setProntuario(Integer prontuario) {
		this.prontuario = prontuario;
	}

	public Byte getApagarUmMinuto() {
		return apagarUmMinuto;
	}

	public void setApagarUmMinuto(Byte apagarUmMinuto) {
		this.apagarUmMinuto = apagarUmMinuto;
	}

	public Byte getApagarCincoMinuto() {
		return apagarCincoMinuto;
	}

	public void setApagarCincoMinuto(Byte apagarCincoMinuto) {
		this.apagarCincoMinuto = apagarCincoMinuto;
	}

	public Byte getApagarDezMinuto() {
		return apagarDezMinuto;
	}

	public void setApagarDezMinuto(Byte apagarDezMinuto) {
		this.apagarDezMinuto = apagarDezMinuto;
	}

	public Byte getQtdDiasRupturaBolsa() {
		return qtdDiasRupturaBolsa;
	}

	public void setQtdDiasRupturaBolsa(Byte qtdDiasRupturaBolsa) {
		this.qtdDiasRupturaBolsa = qtdDiasRupturaBolsa;
	}

	public Byte getQtdHorasRupturaBolsa() {
		return qtdHorasRupturaBolsa;
	}

	public void setQtdHorasRupturaBolsa(Byte qtdHorasRupturaBolsa) {
		this.qtdHorasRupturaBolsa = qtdHorasRupturaBolsa;
	}

	public Byte getQtdMinutosRupturaBolsa() {
		return qtdMinutosRupturaBolsa;
	}

	public void setQtdMinutosRupturaBolsa(Byte qtdMinutosRupturaBolsa) {
		this.qtdMinutosRupturaBolsa = qtdMinutosRupturaBolsa;
	}

	public Boolean getColetadoSangueCordao() {
		return coletadoSangueCordao;
	}

	public void setColetadoSangueCordao(Boolean coletadoSangueCordao) {
		this.coletadoSangueCordao = coletadoSangueCordao;
	}

	public Boolean getAspiracao() {
		return aspiracao;
	}

	public void setAspiracao(Boolean aspiracao) {
		this.aspiracao = aspiracao;
	}

	public Boolean getAspiracaoTet() {
		return aspiracaoTet;
	}

	public void setAspiracaoTet(Boolean aspiracaoTet) {
		this.aspiracaoTet = aspiracaoTet;
	}

	public Boolean getIndO2Inalatorio() {
		return indO2Inalatorio;
	}

	public void setIndO2Inalatorio(Boolean indO2Inalatorio) {
		this.indO2Inalatorio = indO2Inalatorio;
	}

	public Boolean getIndVentilacaoPorMascaraBalao() {
		return indVentilacaoPorMascaraBalao;
	}

	public void setIndVentilacaoPorMascaraBalao(
			Boolean indVentilacaoPorMascaraBalao) {
		this.indVentilacaoPorMascaraBalao = indVentilacaoPorMascaraBalao;
	}

	public Boolean getIndVentilacaoPorMascaraBabyPuff() {
		return indVentilacaoPorMascaraBabyPuff;
	}

	public void setIndVentilacaoPorMascaraBabyPuff(
			Boolean indVentilacaoPorMascaraBabyPuff) {
		this.indVentilacaoPorMascaraBabyPuff = indVentilacaoPorMascaraBabyPuff;
	}

	public Boolean getIndVentilacaoTetBalao() {
		return indVentilacaoTetBalao;
	}

	public void setIndVentilacaoTetBalao(Boolean indVentilacaoTetBalao) {
		this.indVentilacaoTetBalao = indVentilacaoTetBalao;
	}

	public Boolean getIndVentilacaoTetBabyPuff() {
		return indVentilacaoTetBabyPuff;
	}

	public void setIndVentilacaoTetBabyPuff(Boolean indVentilacaoTetBabyPuff) {
		this.indVentilacaoTetBabyPuff = indVentilacaoTetBabyPuff;
	}

	public Boolean getIndCateterismoVenoso() {
		return indCateterismoVenoso;
	}

	public void setIndCateterismoVenoso(Boolean indCateterismoVenoso) {
		this.indCateterismoVenoso = indCateterismoVenoso;
	}

	public Boolean getIndMassCardiacaExt() {
		return indMassCardiacaExt;
	}

	public void setIndMassCardiacaExt(Boolean indMassCardiacaExt) {
		this.indMassCardiacaExt = indMassCardiacaExt;
	}

	public Boolean getIndCpap() {
		return indCpap;
	}

	public void setIndCpap(Boolean indCpap) {
		this.indCpap = indCpap;
	}

	public Boolean getIndUrinou() {
		return indUrinou;
	}

	public void setIndUrinou(Boolean indUrinou) {
		this.indUrinou = indUrinou;
	}

	public Boolean getIndEvacuou() {
		return indEvacuou;
	}

	public void setIndEvacuou(Boolean indEvacuou) {
		this.indEvacuou = indEvacuou;
	}

	public Boolean getIndLavadoGastrico() {
		return indLavadoGastrico;
	}

	public void setIndLavadoGastrico(Boolean indLavadoGastrico) {
		this.indLavadoGastrico = indLavadoGastrico;
	}

	public Boolean getIndAmamentado() {
		return indAmamentado;
	}

	public void setIndAmamentado(Boolean indAmamentado) {
		this.indAmamentado = indAmamentado;
	}
	
	public DominioDestinoRecemNascido getDestinoRecemnascido() {
		return destinoRecemnascido;
	}

	public void setDestinoRecemnascido(DominioDestinoRecemNascido destinoRecemnascido) {
		this.destinoRecemnascido = destinoRecemnascido;
	}
	
	public Short getTempoClampeamentoCordao() {
		return tempoClampeamentoCordao;
	}

	public void setTempoClampeamentoCordao(Short tempoClampeamentoCordao) {
		this.tempoClampeamentoCordao = tempoClampeamentoCordao;
	}
	
	public Short getTemperaturaSalaParto() {
		return temperaturaSalaParto;
	}

	public void setTemperaturaSalaParto(Short temperaturaSalaParto) {
		this.temperaturaSalaParto = temperaturaSalaParto;
	}

	public Boolean getIndContatoPele() {
		return indContatoPele;
	}

	public void setIndContatoPele(Boolean indContatoPele) {
		this.indContatoPele = indContatoPele;
	}

	public Short getAspGastrVol() {
		return aspGastrVol;
	}

	public void setAspGastrVol(Short aspGastrVol) {
		this.aspGastrVol = aspGastrVol;
	}

	public DominioGastrAspecto getAspGastrAspecto() {
		return aspGastrAspecto;
	}

	public void setAspGastrAspecto(DominioGastrAspecto aspGastrAspecto) {
		this.aspGastrAspecto = aspGastrAspecto;
	}

	public String getObservacao() {
		return observacao;
	}

	public void setObservacao(String observacao) {
		this.observacao = observacao;
	}

	public Integer getPacCodigo() {
		return pacCodigo;
	}

	public void setPacCodigo(Integer pacCodigo) {
		this.pacCodigo = pacCodigo;
	}

	public Short getSeqp() {
		return seqp;
	}

	public void setSeqp(Short seqp) {
		this.seqp = seqp;
	}

	public String getNomePaciente() {
		return nomePaciente;
	}

	public void setNomePaciente(String nomePaciente) {
		this.nomePaciente = nomePaciente;
	}

	public Integer getPacCodigoRecemNascido() {
		return pacCodigoRecemNascido;
	}

	public void setPacCodigoRecemNascido(Integer pacCodigoRecemNascido) {
		this.pacCodigoRecemNascido = pacCodigoRecemNascido;
	}

	public List<Integer> getSeqMedicamentosExclusao() {
		if(seqMedicamentosExclusao == null) {
			seqMedicamentosExclusao = new ArrayList<Integer>(); 
		}
		return seqMedicamentosExclusao;
	}

	public void setSeqMedicamentosExclusao(List<Integer> seqMedicamentosExclusao) {
		this.seqMedicamentosExclusao = seqMedicamentosExclusao;
	}

	public List<Integer> getSeqMedicamentosInsercao() {
		if(seqMedicamentosInsercao == null) {
			seqMedicamentosInsercao = new ArrayList<Integer>(); 
		}
		return seqMedicamentosInsercao;
	}

	public void setSeqMedicamentosInsercao(List<Integer> seqMedicamentosInsercao) {
		this.seqMedicamentosInsercao = seqMedicamentosInsercao;
	}
}
