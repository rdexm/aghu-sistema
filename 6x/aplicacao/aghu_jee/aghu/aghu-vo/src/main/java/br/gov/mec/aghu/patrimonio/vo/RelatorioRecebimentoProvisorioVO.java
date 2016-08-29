package br.gov.mec.aghu.patrimonio.vo;

import java.io.Serializable;
import java.util.Date;

import br.gov.mec.aghu.dominio.DominioStatusNotificacaoTecnica;

public class RelatorioRecebimentoProvisorioVO implements Serializable {
	
	private static final long serialVersionUID = -3841282108646753049L;
	
	private Integer nrpSeq;
	private Integer nroItem;
	private Integer eslSeq;
	private Integer irpQuantidade;
	private Integer pfrLctNumero;
	private Short nroComplemento;
	private Long pirpQuantidadeDisp;
	private String razaoSocial;
	private Long cgc;
	private Long cpf;
	private Long dfeNumero;
	private Integer matCodigo;
	private String matNome;
	private String nomeAreaTecAvaliacao;
	private Integer codCentroCusto;
	private String cctDescricao;
	private Date ptnData;
	private Integer ptnStatus;
	private Integer ptnSerMatricula;
	private String ptnDescricao;
	private String pesNome;
	private String tipoNotificacaoTecnica;
	private String nomeTecnicoFormatado;
	
	public enum Fields {
		NRP_SEQ("nrpSeq"),
		NRO_ITEM("nroItem"),
		ESL_SEQ("eslSeq"),
		IRP_QUANTIDADE("irpQuantidade"),
		PFR_LCT_NUMERO("pfrLctNumero"),
		NRO_COMPLEMENTO("nroComplemento"),
		PIRP_QUANTIDADE_DISP("pirpQuantidadeDisp"),
		RAZAO_SOCIAL("razaoSocial"),
		CGC("cgc"),
		CPF("cpf"),
		DFE_NUMERO("dfeNumero"),
		MAT_CODIGO("matCodigo"),
		MAT_NOME("matNome"),
		NOME_AREA_TEC_AVALIACAO("nomeAreaTecAvaliacao"),
		COD_CENTRO_CUSTO("codCentroCusto"),
		CCT_DESCRICAO("cctDescricao"),
		PTN_DATA("ptnData"),
		PTN_STATUS("ptnStatus"),
		PTN_SER_MATRICULA("ptnSerMatricula"),
		PTN_DESCRICAO("ptnDescricao"),
		PES_NOME("pesNome");

		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return this.fields;
		}
	}
	
	public Integer getNrpSeq() {
		return nrpSeq;
	}
	public void setNrpSeq(Integer nrpSeq) {
		this.nrpSeq = nrpSeq;
	}
	public Integer getNroItem() {
		return nroItem;
	}
	public void setNroItem(Integer nroItem) {
		this.nroItem = nroItem;
	}
	public Integer getEslSeq() {
		return eslSeq;
	}
	public void setEslSeq(Integer eslSeq) {
		this.eslSeq = eslSeq;
	}
	public Integer getIrpQuantidade() {
		return irpQuantidade;
	}
	public void setIrpQuantidade(Integer irpQuantidade) {
		this.irpQuantidade = irpQuantidade;
	}
	public Integer getPfrLctNumero() {
		return pfrLctNumero;
	}
	public void setPfrLctNumero(Integer pfrLctNumero) {
		this.pfrLctNumero = pfrLctNumero;
	}
	public Short getNroComplemento() {
		return nroComplemento;
	}
	public void setNroComplemento(Short nroComplemento) {
		this.nroComplemento = nroComplemento;
	}
	public Long getPirpQuantidadeDisp() {
		return pirpQuantidadeDisp;
	}
	public void setPirpQuantidadeDisp(Long pirpQuantidadeDisp) {
		this.pirpQuantidadeDisp = pirpQuantidadeDisp;
	}
	public String getRazaoSocial() {
		return razaoSocial;
	}
	public void setRazaoSocial(String razaoSocial) {
		this.razaoSocial = razaoSocial;
	}
	public Long getCgc() {
		return cgc;
	}
	public void setCgc(Long cgc) {
		this.cgc = cgc;
	}
	public Long getCpf() {
		return cpf;
	}
	public void setCpf(Long cpf) {
		this.cpf = cpf;
	}
	public Long getDfeNumero() {
		return dfeNumero;
	}
	public void setDfeNumero(Long dfeNumero) {
		this.dfeNumero = dfeNumero;
	}
	public Integer getMatCodigo() {
		return matCodigo;
	}
	public void setMatCodigo(Integer matCodigo) {
		this.matCodigo = matCodigo;
	}
	public String getMatNome() {
		return matNome;
	}
	public void setMatNome(String matNome) {
		this.matNome = matNome;
	}
	public String getNomeAreaTecAvaliacao() {
		return nomeAreaTecAvaliacao;
	}
	public void setNomeAreaTecAvaliacao(String nomeAreaTecAvaliacao) {
		this.nomeAreaTecAvaliacao = nomeAreaTecAvaliacao;
	}
	public Integer getCodCentroCusto() {
		return codCentroCusto;
	}
	public void setCodCentroCusto(Integer codCentroCusto) {
		this.codCentroCusto = codCentroCusto;
	}
	public String getCctDescricao() {
		return cctDescricao;
	}
	public void setCctDescricao(String cctDescricao) {
		this.cctDescricao = cctDescricao;
	}
	public Date getPtnData() {
		return ptnData;
	}
	public void setPtnData(Date ptnData) {
		this.ptnData = ptnData;
	}
	public Integer getPtnStatus() {
		return ptnStatus;
	}
	public void setPtnStatus(Integer ptnStatus) {
		this.ptnStatus = ptnStatus;
	}
	public Integer getPtnSerMatricula() {
		return ptnSerMatricula;
	}
	public void setPtnSerMatricula(Integer ptnSerMatricula) {
		this.ptnSerMatricula = ptnSerMatricula;
	}
	public String getPtnDescricao() {
		return ptnDescricao;
	}
	public void setPtnDescricao(String ptnDescricao) {
		this.ptnDescricao = ptnDescricao;
	}
	public String getPesNome() {
		return pesNome;
	}
	public void setPesNome(String pesNome) {
		this.pesNome = pesNome;
	}
	public String getTipoNotificacaoTecnica() {
		tipoNotificacaoTecnica = DominioStatusNotificacaoTecnica.obterDominioStatusNotificacaoTecnica(this.ptnStatus);
		return tipoNotificacaoTecnica;
	}
	public void setTipoNotificacaoTecnica(String tipoNotificacaoTecnica) {
		this.tipoNotificacaoTecnica = tipoNotificacaoTecnica;
	}
	public String getNomeTecnicoFormatado() {
		if (this.pesNome != null && this.pesNome.length() > 39) {
			nomeTecnicoFormatado = this.pesNome.substring(0, 40).concat("...");
		} else {
			nomeTecnicoFormatado = this.pesNome;
		}
		return nomeTecnicoFormatado;
	}
	public void setNomeTecnicoFormatado(String nomeTecnicoFormatado) {
		this.nomeTecnicoFormatado = nomeTecnicoFormatado;
	}
	
}
