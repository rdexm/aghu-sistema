package br.gov.mec.aghu.sicon.vo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.model.ScoAutorizacaoForn;
import br.gov.mec.aghu.model.ScoFaseSolicitacao;
import br.gov.mec.aghu.model.ScoItemAutorizacaoForn;

public class AutorizacaoFornecimentoVO implements Serializable{

	private static final long serialVersionUID = 6402629699255519990L;
	private Integer numeroAf;
	private Short nroComplemento;
	private String cgcCpf;
	private String razaoSocial;
	private BigDecimal valorProposta;
	private String modalidadeEmpenho;
	private String convenioFinanceiro;
	private Integer frequenciaEntrega;
	private ScoAutorizacaoForn af;
	private List<ItemAutorizacaoFornVO> itensAF;
	private DominioSimNao vincularAoContrato;
	private boolean jaVinculado;
	
	//AF Servicos - Suggestion AF sem contrato
	private Integer numeroInternoAf;
	private Short   complementoAf;
	private String  nomeServ;
	private Integer codigoServ;
	private Double  totalItem;
	

	public AutorizacaoFornecimentoVO(){
		
	}
	
	public AutorizacaoFornecimentoVO(Object[] obj){
		
		if (obj[0] != null) {
			numeroInternoAf = Integer.parseInt(obj[0].toString());
		}
		
		if (obj[1] != null) {
			numeroAf = Integer.parseInt(obj[1].toString());
		}
		
		if (obj[2] != null && StringUtils.isNotBlank(obj[2].toString())) {
			complementoAf = Short.parseShort(obj[2].toString());
		}
		
		if (obj[3] != null) {
			nomeServ = obj[3].toString();
		}
		
		if (obj[4] != null) {
			codigoServ = Integer.parseInt(obj[4].toString());
		}
		
		if (obj[5] != null) {
			totalItem = Double.parseDouble(obj[5].toString());
		}
	}
	
	public String getNumeroAfComComplemento(){
		return (this.getNumeroAf()+"/"+this.getComplementoAf());
	}
	
	public Integer getNumeroAf() {
		return numeroAf;
	}

	public void setNumeroAf(Integer numeroAf) {
		this.numeroAf = numeroAf;
	}

	public Short getNroComplemento() {
		return nroComplemento;
	}

	public void setNroComplemento(Short nroComplemento) {
		this.nroComplemento = nroComplemento;
	}

	public String getCgcCpf() {
		return cgcCpf;
	}

	public void setCgcCpf(String cgcCpf) {
		this.cgcCpf = cgcCpf;
	}

	public String getRazaoSocial() {
		return razaoSocial;
	}

	public void setRazaoSocial(String razaoSocial) {
		this.razaoSocial = razaoSocial;
	}

	public BigDecimal getValorProposta() {
		return valorProposta;
	}

	public void setValorProposta(BigDecimal valorProposta) {
		this.valorProposta = valorProposta;
	}

	public String getModalidadeEmpenho() {
		return modalidadeEmpenho;
	}

	public void setModalidadeEmpenho(String modalidadeEmpenho) {
		this.modalidadeEmpenho = modalidadeEmpenho;
	}

	public String getConvenioFinanceiro() {
		return convenioFinanceiro;
	}

	public void setConvenioFinanceiro(String convenioFinanceiro) {
		this.convenioFinanceiro = convenioFinanceiro;
	}

	public Integer getFrequenciaEntrega() {
		return frequenciaEntrega;
	}

	public void setFrequenciaEntrega(Integer frequenciaEntrega) {
		this.frequenciaEntrega = frequenciaEntrega;
	}
	
	public ScoAutorizacaoForn getAf() {
		return af;
	}

	public void setAf(ScoAutorizacaoForn af) {
		this.af = af;
	}

	public List<ItemAutorizacaoFornVO> getItensAF() {
		List<ItemAutorizacaoFornVO> res = new ArrayList<ItemAutorizacaoFornVO>();
		for(ScoItemAutorizacaoForn iprop : this.af.getItensAutorizacaoForn()){
			for(ScoFaseSolicitacao fas : iprop.getScoFaseSolicitacao()){
				ItemAutorizacaoFornVO vo = new ItemAutorizacaoFornVO();
				vo.setFreq(this.af.getPropostaFornecedor().getLicitacao().getFrequenciaEntrega());
				if(fas.getSolicitacaoDeCompra()!=null) {
					vo.setMaterial(fas.getSolicitacaoDeCompra().getMaterial());
				}
				if(fas.getSolicitacaoServico()!=null) {
					vo.setServico(fas.getSolicitacaoServico().getServico());
				}
				vo.setNumItem(iprop.getId().getNumero());
				vo.setQuant(iprop.getItemPropostaFornecedor().getQuantidade().intValue());
				vo.setUnidade(iprop.getItemPropostaFornecedor().getUnidadeMedida());
				vo.setValorUnit(iprop.getItemPropostaFornecedor().getValorUnitario());
				res.add(vo);
				}
		}
		this.itensAF =res;
		return this.itensAF;
	}

	public void setItensAF(List<ItemAutorizacaoFornVO> itensAF) {
		this.itensAF = itensAF;
	}	

	public DominioSimNao getVincularAoContrato() {
		return vincularAoContrato;
	}

	public void setVincularAoContrato(DominioSimNao vincularAoContrato) {
		this.vincularAoContrato = vincularAoContrato;
	}

	public boolean isJaVinculado() {
		return jaVinculado;
	}

	public void setJaVinculado(boolean jaVinculado) {
		this.jaVinculado = jaVinculado;
	}

	@Override
	public boolean equals(Object other) {
		if (!(other instanceof AutorizacaoFornecimentoVO)){
			return false;
		}
		AutorizacaoFornecimentoVO castOther = (AutorizacaoFornecimentoVO) other;
		return new EqualsBuilder().append(this.numeroAf, castOther.getNumeroAf())
				.isEquals();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(this.numeroAf).toHashCode();
	}

	public Integer getNumeroInternoAf() {
		return numeroInternoAf;
	}

	public void setNumeroInternoAf(Integer numeroInternoAf) {
		this.numeroInternoAf = numeroInternoAf;
	}

	public Short getComplementoAf() {
		return complementoAf;
	}

	public void setComplementoAf(Short complementoAf) {
		this.complementoAf = complementoAf;
	}

	public String getNomeServ() {
		return nomeServ;
	}

	public void setNomeServ(String nomeServ) {
		this.nomeServ = nomeServ;
	}

	public Integer getCodigoServ() {
		return codigoServ;
	}

	public void setCodigoServ(Integer codigoServ) {
		this.codigoServ = codigoServ;
	}

	public Double getTotalItem() {
		return totalItem;
	}

	public void setTotalItem(Double totalItem) {
		this.totalItem = totalItem;
	}
}