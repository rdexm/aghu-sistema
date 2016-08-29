package br.gov.mec.aghu.compras.vo;

import java.util.List;




public class JulgamentoPropostasLicitacaoVO  implements Comparable<JulgamentoPropostasLicitacaoVO>{

	private String lctDescricao; //1
	private String lctNumero; //2
	private String lctData; //3
	private String lctHora; //4
	private String itlNumero; //5
	private String vil1Solicit; //6
	private String vil1BullFormula; //44
	private String vil1MaxCodigo; //7
	private String vil1SumQtdeAprovada; //8
	private String vil1MaxUndMedida; //9
	private String vil1FirstNome; //10
	private String vil1FirstDescrSolic; //11
	private String vil1FirstIndMenorPreco; //12
	private String vil1FirstDescr; //13
	private String ipfRazaoSocial; //14
	private String ipfCpMcmDesc; //15
	private String ipfIndNacional; //16
	private String ipfUmdCodigo; //17
	private String ipfCgcCpf; //18
	private String ipfFatorConversao; //19 
	private String ipfQuantidade; //20
	private String ipfQuantidadeXFatorConversao; //21
	private String ipfValorUnitario; //22
	private String ipfCpPrecoIpi; //23
	private String ipfCpNcDesc; //24
	private String ipfvalor; //25
	private String cdpNumero;
	private String cdpDescricao; //26
	private String cdpCfParcelas; //27
	private String cdpPercDesconto; //28
	private String cdpPercAcrescimo; //29
	private String cdpMoeda; //30
	private String cdpCpVlIpi; //31
	private String cdp1Numero;
	private String cdp1Descricao; //32
	private String cdp1CfParcelas; //33
	private String cdp1PercDesconto; //34
	private String cdp1PercAcrescimo; //35
	private String cdp1CpVlIpi; //36
	private List<JulgamentoPropostasLicitacaoRodapeVO> rodape;
	
	@Override
	public int compareTo(JulgamentoPropostasLicitacaoVO other) {
        int result = this.getLctNumero().compareTo(other.getLctNumero());
        if (result == 0) {
                if(this.getItlNumero() != null && other.getItlNumero() != null){
                	result = this.getItlNumero().compareTo(other.getItlNumero());
                	if(this.getIpfCgcCpf() != null && other.getIpfCgcCpf() != null){
                		return this.getIpfCgcCpf().compareTo(other.getIpfCgcCpf());
                	}
                         
                }                                
        }
        return result;
	}
	
	public String getLctDescricao() {
		return lctDescricao;
	}
	public void setLctDescricao(String lctDescricao) {
		this.lctDescricao = lctDescricao;
	}
	public String getLctNumero() {
		return lctNumero;
	}
	public void setLctNumero(String lctNumero) {
		this.lctNumero = lctNumero;
	}
	public String getLctData() {
		return lctData;
	}
	public void setLctData(String lctData) {
		this.lctData = lctData;
	}
	public String getLctHora() {
		return lctHora;
	}
	public void setLctHora(String lctHora) {
		this.lctHora = lctHora;
	}
	public String getItlNumero() {
		return itlNumero;
	}
	public void setItlNumero(String itlNumero) {
		this.itlNumero = itlNumero;
	}
	public String getVil1Solicit() {
		return vil1Solicit;
	}
	public void setVil1Solicit(String vil1Solicit) {
		this.vil1Solicit = vil1Solicit;
	}
	public String getVil1BullFormula() {
		return vil1BullFormula;
	}
	public void setVil1BullFormula(String vil1BullFormula) {
		this.vil1BullFormula = vil1BullFormula;
	}
	public String getVil1MaxCodigo() {
		return vil1MaxCodigo;
	}
	public void setVil1MaxCodigo(String vil1MaxCodigo) {
		this.vil1MaxCodigo = vil1MaxCodigo;
	}
	public String getVil1SumQtdeAprovada() {
		return vil1SumQtdeAprovada;
	}
	public void setVil1SumQtdeAprovada(String vil1SumQtdeAprovada) {
		this.vil1SumQtdeAprovada = vil1SumQtdeAprovada;
	}
	public String getVil1MaxUndMedida() {
		return vil1MaxUndMedida;
	}
	public void setVil1MaxUndMedida(String vil1MaxUndMedida) {
		this.vil1MaxUndMedida = vil1MaxUndMedida;
	}
	public String getVil1FirstNome() {
		return vil1FirstNome;
	}
	public void setVil1FirstNome(String vil1FirstNome) {
		this.vil1FirstNome = vil1FirstNome;
	}
	public String getVil1FirstDescrSolic() {
		return vil1FirstDescrSolic;
	}
	public void setVil1FirstDescrSolic(String vil1FirstDescrSolic) {
		this.vil1FirstDescrSolic = vil1FirstDescrSolic;
	}
	public String getVil1FirstIndMenorPreco() {
		return vil1FirstIndMenorPreco;
	}
	public void setVil1FirstIndMenorPreco(String vil1FirstIndMenorPreco) {
		this.vil1FirstIndMenorPreco = vil1FirstIndMenorPreco;
	}
	public String getVil1FirstDescr() {
		return vil1FirstDescr;
	}
	public void setVil1FirstDescr(String vil1FirstDescr) {
		this.vil1FirstDescr = vil1FirstDescr;
	}
	public String getIpfRazaoSocial() {
		return ipfRazaoSocial;
	}
	public void setIpfRazaoSocial(String ipfRazaoSocial) {
		this.ipfRazaoSocial = ipfRazaoSocial;
	}
	public String getIpfCpMcmDesc() {
		return ipfCpMcmDesc;
	}
	public void setIpfCpMcmDesc(String ipfCpMcmDesc) {
		this.ipfCpMcmDesc = ipfCpMcmDesc;
	}
	public String getIpfIndNacional() {
		return ipfIndNacional;
	}
	public void setIpfIndNacional(String ipfIndNacional) {
		this.ipfIndNacional = ipfIndNacional;
	}
	public String getIpfUmdCodigo() {
		return ipfUmdCodigo;
	}
	public void setIpfUmdCodigo(String ipfUmdCodigo) {
		this.ipfUmdCodigo = ipfUmdCodigo;
	}
	public String getIpfCgcCpf() {
		return ipfCgcCpf;
	}
	public void setIpfCgcCpf(String ipfCgcCpf) {
		this.ipfCgcCpf = ipfCgcCpf;
	}
	public String getIpfFatorConversao() {
		return ipfFatorConversao;
	}
	public void setIpfFatorConversao(String ipfFatorConversao) {
		this.ipfFatorConversao = ipfFatorConversao;
	}
	public String getIpfQuantidade() {
		return ipfQuantidade;
	}
	public void setIpfQuantidade(String ipfQuantidade) {
		this.ipfQuantidade = ipfQuantidade;
	}
	public String getIpfQuantidadeXFatorConversao() {
		return ipfQuantidadeXFatorConversao;
	}
	public void setIpfQuantidadeXFatorConversao(String ipfQuantidadeXFatorConversao) {
		this.ipfQuantidadeXFatorConversao = ipfQuantidadeXFatorConversao;
	}
	public String getIpfValorUnitario() {
		return ipfValorUnitario;
	}
	public void setIpfValorUnitario(String ipfValorUnitario) {
		this.ipfValorUnitario = ipfValorUnitario;
	}
	public String getIpfCpPrecoIpi() {
		return ipfCpPrecoIpi;
	}
	public void setIpfCpPrecoIpi(String ipfCpPrecoIpi) {
		this.ipfCpPrecoIpi = ipfCpPrecoIpi;
	}
	public String getIpfCpNcDesc() {
		return ipfCpNcDesc;
	}
	public void setIpfCpNcDesc(String ipfCpNcDesc) {
		this.ipfCpNcDesc = ipfCpNcDesc;
	}
	public String getIpfvalor() {
		return ipfvalor;
	}
	public void setIpfvalor(String ipfvalor) {
		this.ipfvalor = ipfvalor;
	}
	public String getCdpNumero() {
		return cdpNumero;
	}
	public void setCdpNumero(String cdpNumero) {
		this.cdpNumero = cdpNumero;
	}
	public String getCdpDescricao() {
		return cdpDescricao;
	}
	public void setCdpDescricao(String cdpDescricao) {
		this.cdpDescricao = cdpDescricao;
	}
	public String getCdpCfParcelas() {
		return cdpCfParcelas;
	}
	public void setCdpCfParcelas(String cdpCfParcelas) {
		this.cdpCfParcelas = cdpCfParcelas;
	}
	public String getCdpPercDesconto() {
		return cdpPercDesconto;
	}
	public void setCdpPercDesconto(String cdpPercDesconto) {
		this.cdpPercDesconto = cdpPercDesconto;
	}
	public String getCdpPercAcrescimo() {
		return cdpPercAcrescimo;
	}
	public void setCdpPercAcrescimo(String cdpPercAcrescimo) {
		this.cdpPercAcrescimo = cdpPercAcrescimo;
	}
	public String getCdpMoeda() {
		return cdpMoeda;
	}
	public void setCdpMoeda(String cdpMoeda) {
		this.cdpMoeda = cdpMoeda;
	}
	public String getCdpCpVlIpi() {
		return cdpCpVlIpi;
	}
	public void setCdpCpVlIpi(String cdpCpVlIpi) {
		this.cdpCpVlIpi = cdpCpVlIpi;
	}
	public String getCdp1Numero() {
		return cdp1Numero;
	}
	public void setCdp1Numero(String cdp1Numero) {
		this.cdp1Numero = cdp1Numero;
	}
	public String getCdp1Descricao() {
		return cdp1Descricao;
	}
	public void setCdp1Descricao(String cdp1Descricao) {
		this.cdp1Descricao = cdp1Descricao;
	}
	public String getCdp1CfParcelas() {
		return cdp1CfParcelas;
	}
	public void setCdp1CfParcelas(String cdp1CfParcelas) {
		this.cdp1CfParcelas = cdp1CfParcelas;
	}
	public String getCdp1PercDesconto() {
		return cdp1PercDesconto;
	}
	public void setCdp1PercDesconto(String cdp1PercDesconto) {
		this.cdp1PercDesconto = cdp1PercDesconto;
	}
	public String getCdp1PercAcrescimo() {
		return cdp1PercAcrescimo;
	}
	public void setCdp1PercAcrescimo(String cdp1PercAcrescimo) {
		this.cdp1PercAcrescimo = cdp1PercAcrescimo;
	}
	public String getCdp1CpVlIpi() {
		return cdp1CpVlIpi;
	}
	public void setCdp1CpVlIpi(String cdp1CpVlIpi) {
		this.cdp1CpVlIpi = cdp1CpVlIpi;
	}
	public List<JulgamentoPropostasLicitacaoRodapeVO> getRodape() {
		return rodape;
	}
	public void setRodape(List<JulgamentoPropostasLicitacaoRodapeVO> rodape) {
		this.rodape = rodape;
	}
}
