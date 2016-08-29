package br.gov.mec.aghu.compras.vo;

import java.math.BigDecimal;
import java.util.Date;

import br.gov.mec.aghu.dominio.DominioBoletimOcorrencias;
import br.gov.mec.aghu.dominio.DominioTipoFaseSolicitacao;
import br.gov.mec.aghu.core.utils.AghuNumberFormat;



public class EntregaPorItemVO {

	private Integer nrpSeq;
	private Boolean nrpIndEstorno;
	private Boolean nrpIndConfirmado;
	private Boolean situacaoDevolvido;
	private Long dfeNumero;
	private Date dtEntrada;
	private Integer irpQuantidadeVolumes;
	private Integer qtde;
	private Double peaValorEfetivado;
	private Integer fatorConversao;
	
	private DominioBoletimOcorrencias devolvido;
	private ParcelasAFVO parcelaAFVO;
	
	public Integer getNrpSeq() {
		return nrpSeq;
	}

	public void setNrpSeq(Integer nrpSeq) {
		this.nrpSeq = nrpSeq;
	}

	public Boolean getNrpIndEstorno() {
		return nrpIndEstorno;
	}

	public void setNrpIndEstorno(Boolean nrpIndEstorno) {
		this.nrpIndEstorno = nrpIndEstorno;
	}

	public Boolean getNrpIndConfirmado() {
		return nrpIndConfirmado;
	}

	public void setNrpIndConfirmado(Boolean nrpIndConfirmado) {
		this.nrpIndConfirmado = nrpIndConfirmado;
	}

	public Boolean getSituacaoDevolvido() {
		return situacaoDevolvido;
	}

	public void setSituacaoDevolvido(Boolean situacaoDevolvido) {
		this.situacaoDevolvido = situacaoDevolvido;
	}

	public Long getDfeNumero() {
		return dfeNumero;
	}

	public void setDfeNumero(Long dfeNumero) {
		this.dfeNumero = dfeNumero;
	}

	public Date getDtEntrada() {
		return dtEntrada;
	}

	public void setDtEntrada(Date dtEntrada) {
		this.dtEntrada = dtEntrada;
	}

	public Integer getIrpQuantidadeVolumes() {
		return irpQuantidadeVolumes;
	}

	public void setIrpQuantidadeVolumes(Integer irpQuantidadeVolumes) {
		this.irpQuantidadeVolumes = irpQuantidadeVolumes;
	}

	public Integer getQtde() {
		return qtde;
	}

	public void setQtde(Integer qtde) {
		this.qtde = qtde;
	}

	public Double getPeaValorEfetivado() {
		return peaValorEfetivado;
	}

	public void setPeaValorEfetivado(Double peaValorEfetivado) {
		this.peaValorEfetivado = peaValorEfetivado;
	}

	public Integer getFatorConversao() {
		return fatorConversao;
	}

	public void setFatorConversao(Integer fatorConversao) {
		this.fatorConversao = fatorConversao;
	}
	
	public String getIrpQuantidadeVolumesFormatado() {
		String valorFormatado = AghuNumberFormat.formatarValor(irpQuantidadeVolumes, "#,##0.00#");
		if(valorFormatado.length()>10){
			valorFormatado ="*********";
		}
		
		return valorFormatado;
	}
	
	public String getPeaValorEfetivadoFormatado() {
		String valorFormatado = AghuNumberFormat.formatarValor(peaValorEfetivado, "#,##0.00");
		if(valorFormatado.length()>10){
			valorFormatado ="*********";
		}
		
		return valorFormatado;
	}
	

	public enum Fields {
		NRP_SEQ("nrpSeq"), NRP_IND_ESTORNO("nrpIndEstorno"), NRP_IND_CONFIRMADO("nrpIndConfirmado"), 
		SITUACAO_DEVOLVIDO("situacaoDevolvido"), NUMERO_NF("numeroNF"), DATA_ENTRADA("dtEntrada"), 
		IRP_QUANTIDADE("irpQuantidadeVolumes"), QTDE("qtde"), PEA_VALOR_EFETIVADO("peaValorEfetivado"), 
		FATOR_CONVERSAO("fatorConversao"), DFE_NUMERO("dfeNumero");
		
		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return fields;
		}
	}

	public DominioBoletimOcorrencias getDevolvido() {
		return devolvido;
	}

	public void setDevolvido(DominioBoletimOcorrencias devolvido) {
		this.devolvido = devolvido;
	}
	
	public String getDevolvidoString() {
		if (this.getDevolvido() != null) {
			return this.getDevolvido().getDescricao();
		}
		
		return null;
	}

	public Double getVol() {
		if (DominioTipoFaseSolicitacao.C.equals(parcelaAFVO.getTipoParcela())) {
			BigDecimal qtde = new BigDecimal(irpQuantidadeVolumes);
			BigDecimal fator = new BigDecimal(parcelaAFVO.getIafFatorConversao());
			return qtde.divide(fator).doubleValue();
		}
		return null;
	}

	public ParcelasAFVO getParcelaAFVO() {
		return parcelaAFVO;
	}

	public void setParcelaAFVO(ParcelasAFVO parcelaAFVO) {
		this.parcelaAFVO = parcelaAFVO;
	}
}