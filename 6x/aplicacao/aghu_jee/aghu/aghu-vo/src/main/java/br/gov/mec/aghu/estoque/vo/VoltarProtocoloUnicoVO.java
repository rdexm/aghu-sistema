package br.gov.mec.aghu.estoque.vo;

import br.gov.mec.aghu.model.AelEquipamentos;




public class VoltarProtocoloUnicoVO {
	
	private String mensagemErro;
	private String mensagemAlerta;
	private String errMes;
	private boolean podeGravar = true;
	private String observacao;
	private String localizacao;
	private String localizacaoAlerta;
	private String situacaoExecutando;
	private String areaExecutora ;
	private String situacaoLiberado;
	private String situacaoCancelado;
	private String mocCancelaDept;
	private Integer marcaProcessada = 0;
	private Integer idComunicacaoNova;
	private String siglaExame;
	private Integer materialAnalise;
	private AelEquipamentos equipamento;
	private boolean primeiroResultado = true;
	private boolean primeiroResultadoOutros = true;
	private boolean primeiroResultadoAntibio = true;
	private Integer versaoAtiva;
	private Integer parametroCampoSeqp;
	private boolean exameSemAmostra;
	
	public String getMensagemErro() {
		return mensagemErro;
	}
	public void setMensagemErro(String mensagemErro) {
		this.mensagemErro = mensagemErro;
	}
	public String getMensagemAlerta() {
		return mensagemAlerta;
	}
	public void setMensagemAlerta(String mensagemAlerta) {
		this.mensagemAlerta = mensagemAlerta;
	}
	public String getErrMes() {
		return errMes;
	}
	public void setErrMes(String errMes) {
		this.errMes = errMes;
	}
	public boolean isPodeGravar() {
		return podeGravar;
	}
	public void setPodeGravar(boolean podeGravar) {
		this.podeGravar = podeGravar;
	}
	public String getObservacao() {
		return observacao;
	}
	public void setObservacao(String observacao) {
		this.observacao = observacao;
	}
	public String getLocalizacao() {
		return localizacao;
	}
	public void setLocalizacao(String localizacao) {
		this.localizacao = localizacao;
	}
	public String getLocalizacaoAlerta() {
		return localizacaoAlerta;
	}
	public void setLocalizacaoAlerta(String localizacaoAlerta) {
		this.localizacaoAlerta = localizacaoAlerta;
	}

	public String getAreaExecutora() {
		return areaExecutora;
	}
	public void setAreaExecutora(String areaExecutora) {
		this.areaExecutora = areaExecutora;
	}
	public String getSituacaoExecutando() {
		return situacaoExecutando;
	}
	public void setSituacaoExecutando(String situacaoExecutando) {
		this.situacaoExecutando = situacaoExecutando;
	}
	public String getSituacaoLiberado() {
		return situacaoLiberado;
	}
	public void setSituacaoLiberado(String situacaoLiberado) {
		this.situacaoLiberado = situacaoLiberado;
	}
	public String getSituacaoCancelado() {
		return situacaoCancelado;
	}
	public void setSituacaoCancelado(String situacaoCancelado) {
		this.situacaoCancelado = situacaoCancelado;
	}
	public String getMocCancelaDept() {
		return mocCancelaDept;
	}
	public void setMocCancelaDept(String mocCancelaDept) {
		this.mocCancelaDept = mocCancelaDept;
	}
	public Integer getMarcaProcessada() {
		return marcaProcessada;
	}
	public void setMarcaProcessada(Integer marcaProcessada) {
		this.marcaProcessada = marcaProcessada;
	}
	public Integer getIdComunicacaoNova() {
		return idComunicacaoNova;
	}
	public void setIdComunicacaoNova(Integer idComunicacaoNova) {
		this.idComunicacaoNova = idComunicacaoNova;
	}
	public String getSiglaExame() {
		return siglaExame;
	}
	public void setSiglaExame(String siglaExame) {
		this.siglaExame = siglaExame;
	}
	public Integer getMaterialAnalise() {
		return materialAnalise;
	}
	public void setMaterialAnalise(Integer materialAnalise) {
		this.materialAnalise = materialAnalise;
	}
	public AelEquipamentos getEquipamento() {
		return equipamento;
	}
	public void setEquipamento(AelEquipamentos equipamento) {
		this.equipamento = equipamento;
	}
	public boolean isPrimeiroResultado() {
		return primeiroResultado;
	}
	public void setPrimeiroResultado(boolean primeiroResultado) {
		this.primeiroResultado = primeiroResultado;
	}
	public boolean isPrimeiroResultadoOutros() {
		return primeiroResultadoOutros;
	}
	public void setPrimeiroResultadoOutros(boolean primeiroResultadoOutros) {
		this.primeiroResultadoOutros = primeiroResultadoOutros;
	}
	public boolean isPrimeiroResultadoAntibio() {
		return primeiroResultadoAntibio;
	}
	public void setPrimeiroResultadoAntibio(boolean primeiroResultadoAntibio) {
		this.primeiroResultadoAntibio = primeiroResultadoAntibio;
	}
	public Integer getVersaoAtiva() {
		return versaoAtiva;
	}
	public void setVersaoAtiva(Integer versaoAtiva) {
		this.versaoAtiva = versaoAtiva;
	}
	public Integer getParametroCampoSeqp() {
		return parametroCampoSeqp;
	}
	public void setParametroCampoSeqp(Integer parametroCampoSeqp) {
		this.parametroCampoSeqp = parametroCampoSeqp;
	}
	public boolean isExameSemAmostra() {
		return exameSemAmostra;
	}
	public void setExameSemAmostra(boolean exameSemAmostra) {
		this.exameSemAmostra = exameSemAmostra;
	}
	
	

}
