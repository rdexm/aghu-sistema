package br.gov.mec.aghu.casca.vo;

import java.io.Serializable;
import java.util.Date;

import br.gov.mec.aghu.casca.business.CentralPendenciasInterface.TipoAcaoEnum;
import br.gov.mec.aghu.casca.business.CentralPendenciasInterface.TipoPendenciaEnum;

@SuppressWarnings("ucd")
public class PendenciaVO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1204157166706771705L;
	private final TipoPendenciaEnum tipo;
	private final String mensagem;	
	private TipoAcaoEnum tipoAcao;
	private String acao;
	private String nomeAcao;
	private String nomeMenu;
	private String url;
	private Object parametro;
	private Long seqCaixaPostal;
	private Date dataInicio;
	
	
	public PendenciaVO(TipoPendenciaEnum tipo, String url, String mensagem) {
		super();
		this.tipo = tipo;
		this.url = url;
		this.mensagem = mensagem;
	}

	public PendenciaVO(TipoAcaoEnum tipoAcao, TipoPendenciaEnum tipo, String url, String mensagem) {
		super();
		this.tipoAcao = tipoAcao;
		this.tipo = tipo;
		this.url = url;
		this.mensagem = mensagem;
	}
	
	
	public PendenciaVO(TipoAcaoEnum tipoAcao, TipoPendenciaEnum tipo, String url, String mensagem, String nomeMenu) {
		super();
		this.tipoAcao = tipoAcao;
		this.tipo = tipo;
		this.url = url;
		this.mensagem = mensagem;		
		this.nomeMenu = nomeMenu;
	}

	public PendenciaVO(TipoAcaoEnum tipoAcao, TipoPendenciaEnum tipo, String url, String mensagem, String nomeMenu, String nomeAcao) {
		super();
		this.tipoAcao = tipoAcao;
		this.tipo = tipo;
		this.url = url;
		this.mensagem = mensagem;		
		this.nomeMenu = nomeMenu;
		this.nomeAcao = nomeAcao;
	}

	public PendenciaVO(TipoAcaoEnum tipoAcao, TipoPendenciaEnum tipo,
			String mensagem, String url, String acao, String nomeMenu,
			String nomeAcao) {
		super();
		this.tipoAcao = tipoAcao;
		this.tipo = tipo;
		this.mensagem = mensagem;
		this.url = url;
		this.acao = acao;
		this.nomeMenu = nomeMenu;
		this.nomeAcao = nomeAcao;
	}

	public PendenciaVO(TipoAcaoEnum tipoAcao, TipoPendenciaEnum tipo, 
			String mensagem, String nomeMenu, String acao, 
			String url, String nomeAcao, Object parametro) {
		super();
		this.tipo = tipo;
		this.mensagem = mensagem;
		this.tipoAcao = tipoAcao;
		this.acao = acao;
		this.nomeAcao = nomeAcao;
		this.nomeMenu = nomeMenu;
		this.url = url;
		this.parametro = parametro;
	}

	public TipoPendenciaEnum getTipo() {

		return this.tipo;
	}

	public String getUrl() {

		return this.url;
	}

	public String getMensagem() {

		return this.mensagem;
	}
	
	public String getAcao() {
		return acao;
	}

	public void setAcao(String acao) {
		this.acao = acao;
	}

	public String getNomeAcao() {
		return nomeAcao;
	}

	public TipoAcaoEnum getTipoAcao() {
		return tipoAcao;
	}

	public Object getParametro() {
		return parametro;
	}

	@Override
	public int hashCode() {

		final int prime = 31;
		int result = 1;
		result = prime * result + ((this.mensagem == null)
				? 0
				: this.mensagem.hashCode());
		result = prime * result + ((this.tipo == null)
				? 0
				: this.tipo.hashCode());
		result = prime * result + ((this.url == null)
				? 0
				: this.url.hashCode());
		return result;
	}

	@Override
	public boolean equals(final Object obj) {

		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof PendenciaVO)) {
			return false;
		}
		PendenciaVO other = (PendenciaVO) obj;
		if (this.mensagem == null) {
			if (other.mensagem != null) {
				return false;
			}
		} else if (!this.mensagem.equals(other.mensagem)) {
			return false;
		}
		if (this.tipo != other.tipo) {
			return false;
		}
		if (this.url == null) {
			if (other.url != null) {
				return false;
			}
		} else if (!this.url.equals(other.url)) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {

		StringBuffer result = new StringBuffer(this.getClass().getSimpleName());
		result.append(": ").append(this.getTipo().name())
		.append(" - ").append(this.getUrl())
		.append(" - ").append(this.getMensagem());

		return result.toString();
	}

	public String getNomeMenu() {
		return nomeMenu;
	}

	public void setUrl(String url) {
		this.url = url;
	}


	public void setNomeMenu(String nomeMenu) {
		this.nomeMenu = nomeMenu;
	}

	public Date getDataInicio() {
		return dataInicio;
	}

	public void setDataInicio(Date dataInicio) {
		this.dataInicio = dataInicio;
	}

	public Long getSeqCaixaPostal() {
		return seqCaixaPostal;
	}

	public void setSeqCaixaPostal(Long seqCaixaPostal) {
		this.seqCaixaPostal = seqCaixaPostal;
	}
	
	
}
