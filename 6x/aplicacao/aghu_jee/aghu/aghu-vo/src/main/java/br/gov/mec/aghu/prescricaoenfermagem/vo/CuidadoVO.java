package br.gov.mec.aghu.prescricaoenfermagem.vo;

import java.io.Serializable;
import java.util.List;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import br.gov.mec.aghu.model.EpeCuidados;
import br.gov.mec.aghu.model.EpePrescricaoEnfermagem;
import br.gov.mec.aghu.model.EpePrescricoesCuidados;
import br.gov.mec.aghu.model.MpmTipoFrequenciaAprazamento;
import br.gov.mec.aghu.prescricaoenfermagem.util.TipoPrescricaoCuidadoEnfermagem;
import br.gov.mec.aghu.core.commons.BaseBean;

/**
 * 
 * @author diego.pacheco
 *
 */

public class CuidadoVO  implements Serializable, Cloneable, BaseBean {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1530853259493389225L;
	
	/**
	 * 
	 */
	private static final String ESCAPE_IGNORE = "&lt;br/&gt;";
	private static final String ESCAPE_IGNORE_CAPTALIZE = "&lt;BR/&gt;";
	private static final String QUEBRA_LINHA = "<br/>";
	
	private Boolean emEdicao = Boolean.FALSE;
	
	private boolean imprimeAprazamento= true;
	
    private EpeCuidados cuidado;
    
    // Quando prescricaoCuidado.prescricaoEnfermagem é diferente de null, 
    // indica que cuidado já está prescrito
	private EpePrescricoesCuidados prescricaoCuidado;
	//Clone do original para fins de update.
	private EpePrescricoesCuidados prescricaoCuidadoClone;
	
	private EpePrescricaoEnfermagem prescricaoEnfermagem;
	
	// Utilizado no relatório da prescrição enfermagem
	private Integer numero;
	private String tipo;
	private String aprazamento;
	
	/**
	 * Utilizado na tela de prescricao medica.
	 * Quando o usuario marcar o item para exclusao.
	 */
	private Boolean excluir;

	/**
	 * Utilizado para identificar se o item foi marcado na tela.
	 * Uso geral.
	 */
	private Boolean marcado;
	
	/**
	 * Indica de forma diferenciada o item em uma lista.
	 */
	private Boolean mesclado = Boolean.FALSE;
	
	private TipoPrescricaoCuidadoEnfermagem tipoPrescricaoCuidado;
	
	private Boolean contemErro = Boolean.FALSE;
	
	private Boolean mergeRealizado = Boolean.FALSE;
	
	/**
	 *  Lista utilizada para seleção de valor no caso de manutenção de cuidado
	 */
	private List<MpmTipoFrequenciaAprazamento> listaTipoFrequenciaAprazamento;
	
	/**
	 * Etiologia do Cuidado
	 */
	private String descricaoEtiologia;
	
	private Short fdgDgnSnbGnbSeq;   
	private Short fdgDgnSnbSequencia;
	private Short fdgDgnSequencia;
	private Short fdgFreSeq;
	
	/**
	 * Descrição do cuidado formatada, seguindo o padrão:
	 * NOME DO CUIDADO - COMPLEMENTO, APRAZAMENTO;
	 */
	private String descricao;
	
	public CuidadoVO() { }
	
	public CuidadoVO(CuidadoVO cuidadoVO){
		this.setPrescricaoCuidado(cuidadoVO.getPrescricaoCuidado());
		this.setCuidado(cuidadoVO.getCuidado());
		this.setEmEdicao(cuidadoVO.getEmEdicao());
		this.setExcluir(cuidadoVO.getExcluir());
		this.setCuidado(cuidadoVO.getCuidado());
		this.setMarcado(cuidadoVO.getMarcado());
		this.setMesclado(cuidadoVO.getMesclado());
	}
	
	@Override
	public CuidadoVO clone() throws CloneNotSupportedException {
		return (CuidadoVO) super.clone();
	}

	/**
	 * Busca a descrição formatada em html para apresentação em tela.
	 * @return
	 */	
	public String getDescricaoPrescricaoCuidadoFormatada() {
		String str = StringEscapeUtils.escapeHtml4(this.getDescricao());
		str = str.replace(ESCAPE_IGNORE, QUEBRA_LINHA);		
		str = str.replace(ESCAPE_IGNORE_CAPTALIZE, QUEBRA_LINHA);
		return str;
	}
	
	public String getDescricaoPrescricaoCuidadoBundle(){
		String str = this.cuidado.getDescricao();
		str = str.replace(ESCAPE_IGNORE, QUEBRA_LINHA);		
		str = str.replace(ESCAPE_IGNORE_CAPTALIZE, QUEBRA_LINHA);
		return str;
	}
	
	public boolean isValid() {
		return (this.prescricaoCuidado != null && this.cuidado != null);
	}
	
	public void setExcluir(Boolean excluir) {
		this.excluir = excluir;
	}

	public Boolean getExcluir() {
		return excluir;
	}

	/**
	 * @param marcado the marcado to set
	 */
	public void setMarcado(Boolean marcado) {
		this.marcado = marcado;
	}

	/**
	 * @return the marcado
	 */
	public Boolean getMarcado() {
		return marcado;
	}

	public void setMesclado(Boolean mesclado) {
		this.mesclado = mesclado;
	}

	public Boolean getMesclado() {
		return mesclado;
	}

	public Boolean getEmEdicao() {
		return emEdicao;
	}

	public void setEmEdicao(Boolean emEdicao) {
		this.emEdicao = emEdicao;
	}

	public TipoPrescricaoCuidadoEnfermagem getTipoPrescricaoCuidado() {
		return tipoPrescricaoCuidado;
	}

	public void setTipoPrescricaoCuidado(
			TipoPrescricaoCuidadoEnfermagem tipoPrescricaoCuidado) {
		this.tipoPrescricaoCuidado = tipoPrescricaoCuidado;
	}

	public List<MpmTipoFrequenciaAprazamento> getListaTipoFrequenciaAprazamento() {
		return listaTipoFrequenciaAprazamento;
	}

	public void setListaTipoFrequenciaAprazamento(
			List<MpmTipoFrequenciaAprazamento> listaTipoFrequenciaAprazamento) {
		this.listaTipoFrequenciaAprazamento = listaTipoFrequenciaAprazamento;
	}

	public void setNumero(Integer numero) {
		this.numero = numero;
	}

	public Integer getNumero() {
		return numero;
	}

	public void setTipo(String tipo) {
		this.tipo = tipo;
	}

	public String getTipo() {
		return tipo;
	}

	public void setAprazamento(String aprazamento) {
		this.aprazamento = aprazamento;
	}

	public String getAprazamento() {
		return aprazamento;
	}

	public EpePrescricoesCuidados getPrescricaoCuidado() {
		return prescricaoCuidado;
	}

	public void setPrescricaoCuidado(EpePrescricoesCuidados prescricaoCuidado) {
		this.prescricaoCuidado = prescricaoCuidado;
	}

	public EpePrescricaoEnfermagem getPrescricaoEnfermagem() {
		return prescricaoEnfermagem;
	}

	public void setPrescricaoEnfermagem(EpePrescricaoEnfermagem prescricaoEnfermagem) {
		this.prescricaoEnfermagem = prescricaoEnfermagem;
	}

	public EpeCuidados getCuidado() {
		return cuidado;
	}

	public void setCuidado(EpeCuidados cuidado) {
		this.cuidado = cuidado;
	}

	public void setDescricaoEtiologia(String descricaoEtiologia) {
		this.descricaoEtiologia = descricaoEtiologia;
	}

	public String getDescricaoEtiologia() {
		return descricaoEtiologia;
	}

	public void setContemErro(Boolean contemErro) {
		this.contemErro = contemErro;
	}

	public Boolean getContemErro() {
		return contemErro;
	}

	public Short getFdgDgnSnbGnbSeq() {
		return fdgDgnSnbGnbSeq;
	}

	public void setFdgDgnSnbGnbSeq(Short fdgDgnSnbGnbSeq) {
		this.fdgDgnSnbGnbSeq = fdgDgnSnbGnbSeq;
	}

	public Short getFdgDgnSnbSequencia() {
		return fdgDgnSnbSequencia;
	}

	public void setFdgDgnSnbSequencia(Short fdgDgnSnbSequencia) {
		this.fdgDgnSnbSequencia = fdgDgnSnbSequencia;
	}

	public Short getFdgDgnSequencia() {
		return fdgDgnSequencia;
	}

	public void setFdgDgnSequencia(Short fdgDgnSequencia) {
		this.fdgDgnSequencia = fdgDgnSequencia;
	}

	public Short getFdgFreSeq() {
		return fdgFreSeq;
	}

	public void setFdgFreSeq(Short fdgFreSeq) {
		this.fdgFreSeq = fdgFreSeq;
	}

	public void setPrescricaoCuidadoClone(EpePrescricoesCuidados prescricaoCuidadoClone) {
		this.prescricaoCuidadoClone = prescricaoCuidadoClone;
	}

	public EpePrescricoesCuidados getPrescricaoCuidadoClone() {
		return prescricaoCuidadoClone;
	}

	public void setMergeRealizado(Boolean mergeRealizado) {
		this.mergeRealizado = mergeRealizado;
	}

	public Boolean getMergeRealizado() {
		return mergeRealizado;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public String getDescricao() {
		return descricao;
	}

	@Override
	public int hashCode() {
		HashCodeBuilder umHashCodeBuilder = new HashCodeBuilder();
		umHashCodeBuilder.append(this.getCuidado());
		umHashCodeBuilder.append(this.getAprazamento());
		umHashCodeBuilder.append(this.getContemErro());
		umHashCodeBuilder.append(this.getDescricao());
		umHashCodeBuilder.append(this.getDescricaoEtiologia());
		umHashCodeBuilder.append(this.getEmEdicao());
		umHashCodeBuilder.append(this.getFdgDgnSequencia());
		umHashCodeBuilder.append(this.getFdgDgnSnbGnbSeq());
		umHashCodeBuilder.append(this.getFdgDgnSnbSequencia());
		umHashCodeBuilder.append(this.getFdgFreSeq());
		umHashCodeBuilder.append(this.getListaTipoFrequenciaAprazamento());
		umHashCodeBuilder.append(this.getMergeRealizado());
		umHashCodeBuilder.append(this.getMesclado());
		umHashCodeBuilder.append(this.getPrescricaoCuidado());
		umHashCodeBuilder.append(this.getPrescricaoCuidadoClone());
		umHashCodeBuilder.append(this.getPrescricaoEnfermagem());
		umHashCodeBuilder.append(this.getTipoPrescricaoCuidado());
		umHashCodeBuilder.append(this.getListaTipoFrequenciaAprazamento());
		return umHashCodeBuilder.toHashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj){
			return true;}
		if (obj == null){
			return false;}
		if (getClass() != obj.getClass()){
			return false;}
		CuidadoVO other = (CuidadoVO) obj;
		EqualsBuilder umEqualsBuilder = new EqualsBuilder();
		umEqualsBuilder.append(this.getCuidado(), other.getCuidado());
		umEqualsBuilder.append(this.getAprazamento(), other.getAprazamento());
		umEqualsBuilder.append(this.getContemErro(), other.getContemErro());
		umEqualsBuilder.append(this.getDescricao(), other.getDescricao());
		umEqualsBuilder.append(this.getDescricaoEtiologia(), other.getDescricaoEtiologia());
		umEqualsBuilder.append(this.getEmEdicao(), other.getEmEdicao());
		umEqualsBuilder.append(this.getFdgDgnSequencia(), other.getFdgDgnSequencia());
		umEqualsBuilder.append(this.getFdgDgnSnbGnbSeq(), other.getFdgDgnSnbGnbSeq());
		umEqualsBuilder.append(this.getFdgDgnSnbSequencia(), other.getFdgDgnSnbSequencia());
		umEqualsBuilder.append(this.getFdgFreSeq(), other.getFdgFreSeq());
		umEqualsBuilder.append(this.getListaTipoFrequenciaAprazamento(), other.getListaTipoFrequenciaAprazamento());
		umEqualsBuilder.append(this.getMergeRealizado(), other.getMergeRealizado());
		umEqualsBuilder.append(this.getMesclado(), other.getMesclado());
		umEqualsBuilder.append(this.getPrescricaoCuidado(), other.getPrescricaoCuidado());
		umEqualsBuilder.append(this.getPrescricaoCuidadoClone(), other.getPrescricaoCuidadoClone());
		umEqualsBuilder.append(this.getPrescricaoEnfermagem(), other.getPrescricaoEnfermagem());
		umEqualsBuilder.append(this.getTipoPrescricaoCuidado(), other.getTipoPrescricaoCuidado());
		umEqualsBuilder.append(this.getListaTipoFrequenciaAprazamento(), other.getListaTipoFrequenciaAprazamento());
		return umEqualsBuilder.isEquals();
	}

	public boolean isImprimeAprazamento() {
		return imprimeAprazamento;
	}

	public void setImprimeAprazamento(boolean imprimeAprazamento) {
		this.imprimeAprazamento = imprimeAprazamento;
	}

}
