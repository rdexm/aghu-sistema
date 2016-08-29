package br.gov.mec.aghu.compras.pac.vo;

import java.util.Date;

import br.gov.mec.aghu.model.ScoLicitacao;
import br.gov.mec.aghu.core.commons.BaseBean;

public class GeracaoAfVO implements BaseBean {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 6967602809621547097L;
	private Integer seqLista;
	private Date dataCompetencia;
	private String umdCodigoEstoque;
	private Long quantidade;
	private Double valorUnitario;
	private String umdCodigo;
	private Integer fatorConversao;
	private Integer iafNumero;
	private Integer afnNumero;
	private ScoLicitacao licitacao;
	private Boolean geraAf;
	private Boolean mostrarAfGeradas;
	
	public Integer getSeqLista() {
		return seqLista;
	}
	public void setSeqLista(Integer seqLista) {
		this.seqLista = seqLista;
	}
	public Date getDataCompetencia() {
		return dataCompetencia;
	}
	public void setDataCompetencia(Date dataCompetencia) {
		this.dataCompetencia = dataCompetencia;
	}
	public String getUmdCodigoEstoque() {
		return umdCodigoEstoque;
	}
	public void setUmdCodigoEstoque(String umdCodigoEstoque) {
		this.umdCodigoEstoque = umdCodigoEstoque;
	}
	public Long getQuantidade() {
		return quantidade;
	}
	public void setQuantidade(Long quantidade) {
		this.quantidade = quantidade;
	}
	public Double getValorUnitario() {
		return valorUnitario;
	}
	public void setValorUnitario(Double valorUnitario) {
		this.valorUnitario = valorUnitario;
	}
	public String getUmdCodigo() {
		return umdCodigo;
	}
	public void setUmdCodigo(String umdCodigo) {
		this.umdCodigo = umdCodigo;
	}
	public Integer getFatorConversao() {
		return fatorConversao;
	}
	public void setFatorConversao(Integer fatorConversao) {
		this.fatorConversao = fatorConversao;
	}
	public Integer getAfnNumero() {
		return afnNumero;
	}
	public void setAfnNumero(Integer afnNumero) {
		this.afnNumero = afnNumero;
	}
	public ScoLicitacao getLicitacao() {
		return licitacao;
	}
	public void setLicitacao(ScoLicitacao licitacao) {
		this.licitacao = licitacao;
	}
	public Integer getIafNumero() {
		return iafNumero;
	}
	public void setIafNumero(Integer iafNumero) {
		this.iafNumero = iafNumero;
	}
	public Boolean getGeraAf() {
		return geraAf;
	}
	public void setGeraAf(Boolean geraAf) {
		this.geraAf = geraAf;
	}
	public Boolean getMostrarAfGeradas() {
		return mostrarAfGeradas;
	}
	public void setMostrarAfGeradas(Boolean mostrarAfGeradas) {
		this.mostrarAfGeradas = mostrarAfGeradas;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((afnNumero == null) ? 0 : afnNumero.hashCode());
		result = prime * result
				+ ((iafNumero == null) ? 0 : iafNumero.hashCode());
		result = prime * result
				+ ((seqLista == null) ? 0 : seqLista.hashCode());
		return result;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof GeracaoAfVO)) {
			return false;
		}
		GeracaoAfVO other = (GeracaoAfVO) obj;
		if (afnNumero == null) {
			if (other.afnNumero != null) {
				return false;
			}
		} else if (!afnNumero.equals(other.afnNumero)) {
			return false;
		}
		if (iafNumero == null) {
			if (other.iafNumero != null) {
				return false;
			}
		} else if (!iafNumero.equals(other.iafNumero)) {
			return false;
		}
		if (seqLista == null) {
			if (other.seqLista != null) {
				return false;
			}
		} else if (!seqLista.equals(other.seqLista)) {
			return false;
		}
		return true;
	}
}