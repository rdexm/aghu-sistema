package br.gov.mec.aghu.paciente.prontuarioonline.vo;

import java.util.Date;

import br.gov.mec.aghu.core.commons.BaseBean;


public class ItemSolicitacaoExamePolVO implements BaseBean {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1875053305616821332L;
	private boolean itemSelecionadoLista;
	private Boolean temAnexo;
	private Boolean temImagem;
	private Integer soeSeq;
	private Short seqp;
	private Date dthrLiberada;
	private String exaDescricaoUsual;
	private String manDescricao;
	private String unfDescricao;
	private String situacaoCodigoDescricao;
	private Object resultado;
	private String imagem = "silk-attach";
	private Boolean notasAdicionais; 
	
	public boolean isItemSelecionadoLista() {
		return itemSelecionadoLista;
	}
	
	public void setItemSelecionadoLista(boolean itemSelecionadoLista) {
		this.itemSelecionadoLista = itemSelecionadoLista;
	}
	
	public Integer getSoeSeq() {
		return soeSeq;
	}
	public void setSoeSeq(Integer soeSeq) {
		this.soeSeq = soeSeq;
	}
	public Date getDthrLiberada() {
		return dthrLiberada;
	}
	public void setDthrLiberada(Date dthrLiberada) {
		this.dthrLiberada = dthrLiberada;
	}
	public String getExaDescricaoUsual() {
		return exaDescricaoUsual;
	}
	public void setExaDescricaoUsual(String exaDescricaoUsual) {
		this.exaDescricaoUsual = exaDescricaoUsual;
	}
	public String getManDescricao() {
		return manDescricao;
	}
	public void setManDescricao(String manDescricao) {
		this.manDescricao = manDescricao;
	}
	public String getUnfDescricao() {
		return unfDescricao;
	}
	public void setUnfDescricao(String unfDescricao) {
		this.unfDescricao = unfDescricao;
	}
	
	/**
	 * @param situacaoCodigoDescricao the situacaoCodigoDescricao to set
	 */
	public void setSituacaoCodigoDescricao(String situacaoCodigoDescricao) {
		this.situacaoCodigoDescricao = situacaoCodigoDescricao;
	}
	/**
	 * @return the situacaoCodigoDescricao
	 */
	public String getSituacaoCodigoDescricao() {
		return situacaoCodigoDescricao;
	}
	
	public void setResultado(Object resultado) {
		this.resultado = resultado;
	}
	
	public Object getResultado() {
		return resultado;
	}
	
	/**
	 * Se o exame tiver resultado no PACS (pac_oru_acc_number != null) 
	 *    então deve ser apresentado uma imagem (t:/ícones/imagemmedica.bmp).
	 * Porém se o exame tiver documento em anexo,
	 *    deve ser apresentado outro ícone (t:/ícones/attach.gif).
	 * Para descobrir se o exame tem documento deve ser executada a consulta:
	 * SELECT 1 FROM   ael_doc_resultado_exames WHERE  ise_seqp = c_doc_seq
	 * AND ise_soe_seq = c_doc_soe_seq AND ind_anulacao_doc = 'N'; 
	 * 
	 * @param imagem
	 */
	public void setImagem(String imagem) {
		this.imagem = imagem;
	}
	
	public String getImagem() {
		return imagem;
	}
	public void setSeqp(Short seqp) {
		this.seqp = seqp;
	}
	public Short getSeqp() {
		return seqp;
	}
	public Boolean getTemAnexo() {
		return temAnexo;
	}
	public void setTemAnexo(Boolean temAnexo) {
		this.temAnexo = temAnexo;
	}

	public Boolean getNotasAdicionais() {
		return notasAdicionais;
	}

	public void setNotasAdicionais(Boolean notasAdicionais) {
		this.notasAdicionais = notasAdicionais;
	}

	
	public Boolean getTemImagem() {
		return temImagem;
	}

	
	public void setTemImagem(Boolean temImagem) {
		this.temImagem = temImagem;
	}
}