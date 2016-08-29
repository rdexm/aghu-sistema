package br.gov.mec.aghu.compras.pac.vo;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

import br.gov.mec.aghu.core.commons.BaseBean;

public class ScoLicitacaoImpressaoVO implements BaseBean{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 4572637788689438764L;

	private String dataGeracao;
	private String codigoCliente;
	private String codigoIdentificador;
	private String ied;
	private String licitacao;
	private String processo;
	private String unidOrg;
	private String moeda;
	private String modlLicit;
	private String edital;
	private String tipo;
	private String dtPubl;
	private String dtIniEntgProp;
	private String dtFimEntgProp;
	private String dataAbertProp;
	private String dataIniPregao;
	private String prazoRecurso;
	private String idioma;
	private String formaPartic;
	private String descricao;
	
	private List<AutorizacaoLicitacaoVO> listaAutorizacoesLicitacao;
	private List<PropostaLicitacaoVO> listaPropostaLicitacao;
	private List<FasesLicitacaoVO> listaFasesLicitacao;
	private List<EmpresasHomologadasLoteLicitacaoVO> listaEmpresasHomologadasLoteLicitacao;
	private List<EstadoLoteLicitacaoVO> listaEstadoLoteLicitacao;
	private List<RegistroLancesLoteDisputaPregaoVO> listaRegistroLancesLoteDisputaPregao;
	private List<MensagensSalaDisputaVO> listaMensagensSalaDisputa;
	private List<RegistroInteressadosVO> listaRegistroInteressados;
	private List<ItemLicitacaoCabecalhoVO> listaItensLicitacaoCabecalho;
	
	
	public ScoLicitacaoImpressaoVO(){
		
	}
	
	public String getFaseAtualLicitacao(){
		for (FasesLicitacaoVO fase: listaFasesLicitacao){
			if(fase.getFimEstadoLicitacao() == null || fase.getFimEstadoLicitacao().isEmpty() || StringUtils.isBlank(fase.getFimEstadoLicitacao())){
				return fase.getEstadoLicitacao();
			}
		}
		return StringUtils.EMPTY;
	}
	
	public String getDataGeracao() {
		return dataGeracao;
	}

	public void setDataGeracao(String dataGeracao) {
		this.dataGeracao = dataGeracao;
	}

	public String getCodigoCliente() {
		return codigoCliente;
	}

	public void setCodigoCliente(String codigoCliente) {
		this.codigoCliente = codigoCliente;
	}

	public String getCodigoIdentificador() {
		return codigoIdentificador;
	}

	public void setCodigoIdentificador(String codigoIdentificador) {
		this.codigoIdentificador = codigoIdentificador;
	}

	public String getIed() {
		return ied;
	}

	public void setIed(String ied) {
		this.ied = ied;
	}

	public String getLicitacao() {
		return licitacao;
	}

	public void setLicitacao(String licitacao) {
		this.licitacao = licitacao;
	}

	public String getProcesso() {
		return processo;
	}

	public void setProcesso(String processo) {
		this.processo = processo;
	}

	public String getUnidOrg() {
		return unidOrg;
	}

	public void setUnidOrg(String unidOrg) {
		this.unidOrg = unidOrg;
	}

	public String getMoeda() {
		return moeda;
	}

	public void setMoeda(String moeda) {
		this.moeda = moeda;
	}

	public String getModlLicit() {
		return modlLicit;
	}

	public void setModlLicit(String modlLicit) {
		this.modlLicit = modlLicit;
	}

	public String getEdital() {
		return edital;
	}

	public void setEdital(String edital) {
		this.edital = edital;
	}

	public String getTipo() {
		return tipo;
	}

	public void setTipo(String tipo) {
		this.tipo = tipo;
	}

	public String getDtPubl() {
		return dtPubl;
	}

	public void setDtPubl(String dtPubl) {
		this.dtPubl = dtPubl;
	}

	public String getDtIniEntgProp() {
		return dtIniEntgProp;
	}

	public void setDtIniEntgProp(String dtIniEntgProp) {
		this.dtIniEntgProp = dtIniEntgProp;
	}

	public String getDtFimEntgProp() {
		return dtFimEntgProp;
	}

	public void setDtFimEntgProp(String dtFimEntgProp) {
		this.dtFimEntgProp = dtFimEntgProp;
	}

	public String getDataAbertProp() {
		return dataAbertProp;
	}

	public void setDataAbertProp(String dataAbertProp) {
		this.dataAbertProp = dataAbertProp;
	}

	public String getDataIniPregao() {
		return dataIniPregao;
	}

	public void setDataIniPregao(String dataIniPregao) {
		this.dataIniPregao = dataIniPregao;
	}

	public String getPrazoRecurso() {
		return prazoRecurso;
	}

	public void setPrazoRecurso(String prazoRecurso) {
		this.prazoRecurso = prazoRecurso;
	}

	public String getIdioma() {
		return idioma;
	}

	public void setIdioma(String idioma) {
		this.idioma = idioma;
	}

	public String getFormaPartic() {
		return formaPartic;
	}

	public void setFormaPartic(String formaPartic) {
		this.formaPartic = formaPartic;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public List<AutorizacaoLicitacaoVO> getListaAutorizacoesLicitacao() {
		return listaAutorizacoesLicitacao;
	}

	public void setListaAutorizacoesLicitacao(
			List<AutorizacaoLicitacaoVO> listaAutorizacoesLicitacao) {
		this.listaAutorizacoesLicitacao = listaAutorizacoesLicitacao;
	}

	public List<PropostaLicitacaoVO> getListaPropostaLicitacao() {
		return listaPropostaLicitacao;
	}

	public void setListaPropostaLicitacao(
			List<PropostaLicitacaoVO> listaPropostaLicitacao) {
		this.listaPropostaLicitacao = listaPropostaLicitacao;
	}

	public List<FasesLicitacaoVO> getListaFasesLicitacao() {
		return listaFasesLicitacao;
	}

	public void setListaFasesLicitacao(List<FasesLicitacaoVO> listaFasesLicitacao) {
		this.listaFasesLicitacao = listaFasesLicitacao;
	}

	public List<EmpresasHomologadasLoteLicitacaoVO> getListaEmpresasHomologadasLoteLicitacao() {
		return listaEmpresasHomologadasLoteLicitacao;
	}

	public void setListaEmpresasHomologadasLoteLicitacao(
			List<EmpresasHomologadasLoteLicitacaoVO> listaEmpresasHomologadasLoteLicitacao) {
		this.listaEmpresasHomologadasLoteLicitacao = listaEmpresasHomologadasLoteLicitacao;
	}

	public List<EstadoLoteLicitacaoVO> getListaEstadoLoteLicitacao() {
		return listaEstadoLoteLicitacao;
	}

	public void setListaEstadoLoteLicitacao(
			List<EstadoLoteLicitacaoVO> listaEstadoLoteLicitacao) {
		this.listaEstadoLoteLicitacao = listaEstadoLoteLicitacao;
	}

	public List<RegistroLancesLoteDisputaPregaoVO> getListaRegistroLancesLoteDisputaPregao() {
		return listaRegistroLancesLoteDisputaPregao;
	}

	public void setListaRegistroLancesLoteDisputaPregao(
			List<RegistroLancesLoteDisputaPregaoVO> listaRegistroLancesLoteDisputaPregao) {
		this.listaRegistroLancesLoteDisputaPregao = listaRegistroLancesLoteDisputaPregao;
	}

	public List<MensagensSalaDisputaVO> getListaMensagensSalaDisputa() {
		return listaMensagensSalaDisputa;
	}

	public void setListaMensagensSalaDisputa(
			List<MensagensSalaDisputaVO> listaMensagensSalaDisputa) {
		this.listaMensagensSalaDisputa = listaMensagensSalaDisputa;
	}

	public List<RegistroInteressadosVO> getListaRegistroInteressados() {
		return listaRegistroInteressados;
	}

	public void setListaRegistroInteressados(
			List<RegistroInteressadosVO> listaRegistroInteressados) {
		this.listaRegistroInteressados = listaRegistroInteressados;
	}

	public List<ItemLicitacaoCabecalhoVO> getListaItensLicitacaoCabecalho() {
		return listaItensLicitacaoCabecalho;
	}

	public void setListaItensLicitacaoCabecalho(
			List<ItemLicitacaoCabecalhoVO> listaItensLicitacaoCabecalho) {
		this.listaItensLicitacaoCabecalho = listaItensLicitacaoCabecalho;
	}
	
}
