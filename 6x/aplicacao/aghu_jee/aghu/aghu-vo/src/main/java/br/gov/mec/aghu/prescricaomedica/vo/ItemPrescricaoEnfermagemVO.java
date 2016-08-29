package br.gov.mec.aghu.prescricaomedica.vo;

import java.io.Serializable;
import java.util.Date;

import org.apache.commons.lang3.StringEscapeUtils;

import br.gov.mec.aghu.dominio.DominioIndPendentePrescricoesCuidados;
import br.gov.mec.aghu.model.EpePrescricaoEnfermagem;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.prescricaomedica.util.PrescricaoMedicaTypes;

/**
 * @author tfelini
 * 
 */
public class ItemPrescricaoEnfermagemVO implements Serializable {
	
	private static final long serialVersionUID = -1695374846842351294L;

	private static final String ESCAPE_IGNORE = "&lt;br/&gt;";
	private static final String ESCAPE_IGNORE_CAPTALIZE = "&lt;BR/&gt;";
	private static final String QUEBRA_LINHA = "<br/>";
	
	private EpePrescricaoEnfermagem prescricaoEnfermagem;
	
	private Integer atendimentoSeq;
	private Integer itemSeq;
	
	private String descricao;
	private PrescricaoMedicaTypes tipo;
	
	private Boolean hierarquico;
	private Boolean emEdicao = Boolean.FALSE;
	private Boolean isNew = Boolean.FALSE;
	private Date dthrInicio;
	private Date dthrFim;
	private Date criadoEm;
	private Date alteradoEm;
	private DominioIndPendentePrescricoesCuidados situacao;
	private RapServidores servidorValidacao;
	private RapServidores servidorValidaMovimentacao;
	
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
	 * Indicar de forma diferenciada o item em uma lista.
	 */
	private Boolean mesclado = Boolean.FALSE;
	
	public ItemPrescricaoEnfermagemVO(){}
	
	public ItemPrescricaoEnfermagemVO(ItemPrescricaoEnfermagemVO item){
		this.setAlteradoEm(item.getAlteradoEm());
		this.setAtendimentoSeq(item.getAtendimentoSeq());
		this.setCriadoEm(item.getCriadoEm());
		this.setDescricao(item.getDescricaoDB());
		this.setDthrFim(item.getDthrFim());
		this.setDthrInicio(item.getDthrInicio());
		this.setEmEdicao(item.getEmEdicao());
		this.setExcluir(item.getExcluir());
		this.setHierarquico(item.getHierarquico());
		this.setItemSeq(item.getItemSeq());
		this.setMarcado(item.getMarcado());
		this.setMesclado(item.getMesclado());
		this.setPrescricaoEnfermagem(item.getPrescricaoEnfermagem());
		this.setServidorValidacao(item.getServidorValidacao());
		this.setServidorValidaMovimentacao(item.getServidorValidaMovimentacao());
		this.setSituacao(item.getSituacao());
		this.setTipo(item.getTipo());
	}
	
	public void setAtendimentoSeq(Integer atendimentoSeq) {
		this.atendimentoSeq = atendimentoSeq;
	}

	public Integer getAtendimentoSeq() {
		return this.atendimentoSeq;
	}

	public void setItemSeq(Integer itemSeq) {
		this.itemSeq = itemSeq;
	}

	public Integer getItemSeq() {
		return this.itemSeq;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	/**
	 * Busca a descrição do item.
	 * Não foi modificado o metodo getDescricao() pois o mesmo já contém muitas referências 
	 * em xhtml.
	 * #979->902
	 * @return String.
	 */
	public String getDescricaoDB() {
		return this.descricao;
	}
	
	/**
	 * Busca a descrição formatada em html para apresentação em tela.
	 * @return
	 */	
	public String getDescricao() {
		String str = StringEscapeUtils.escapeHtml4(this.descricao);
		str = str.replace(ESCAPE_IGNORE, QUEBRA_LINHA);		
		str = str.replace(ESCAPE_IGNORE_CAPTALIZE, QUEBRA_LINHA);
		return str;
	}

	public void setTipo(PrescricaoMedicaTypes tipo) {
		this.tipo = tipo;
	}

	public PrescricaoMedicaTypes getTipo() {
		return this.tipo;
	}
	
	public Boolean getHierarquico() {
		return hierarquico;
	}

	public void setHierarquico(Boolean hierarquico) {
		this.hierarquico = hierarquico;
	}

	public Date getDthrInicio() {
		return dthrInicio;
	}

	public void setDthrInicio(Date dthrInicio) {
		this.dthrInicio = dthrInicio;
	}

	public Date getDthrFim() {
		return dthrFim;
	}

	public void setDthrFim(Date dthrFim) {
		this.dthrFim = dthrFim;
	}

	public Date getCriadoEm() {
		return criadoEm;
	}

	public void setCriadoEm(Date criadoEm) {
		this.criadoEm = criadoEm;
	}

	public Date getAlteradoEm() {
		return alteradoEm;
	}

	public void setAlteradoEm(Date alteradoEm) {
		this.alteradoEm = alteradoEm;
	}

	public DominioIndPendentePrescricoesCuidados getSituacao() {
		return situacao;
	}

	public void setSituacao(DominioIndPendentePrescricoesCuidados dominioIndPendentePrescricoesCuidados) {
		this.situacao = dominioIndPendentePrescricoesCuidados;
	}
	
	public RapServidores getServidorValidacao() {
		return servidorValidacao;
	}

	public void setServidorValidacao(RapServidores servidorValidacao) {
		this.servidorValidacao = servidorValidacao;
	}

	public RapServidores getServidorValidaMovimentacao() {
		return servidorValidaMovimentacao;
	}

	public void setServidorValidaMovimentacao(
			RapServidores servidorValidaMovimentacao) {
		this.servidorValidaMovimentacao = servidorValidaMovimentacao;
	}

	public boolean isValid() {
		return (this.atendimentoSeq != null && this.itemSeq != null && this.descricao != null && this.tipo != null);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		
		result = prime * result	+ (this.atendimentoSeq == null ? 0 : this.atendimentoSeq.hashCode());
		result = prime * result + (this.descricao == null ? 0 : this.descricao.hashCode());
		result = prime * result + (this.itemSeq == null ? 0 : this.itemSeq.hashCode());
		result = prime * result + (this.tipo == null ? 0 : this.tipo.hashCode());
		
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
		if (getClass() != obj.getClass()) {
			return false;
		}
		
		ItemPrescricaoEnfermagemVO other = (ItemPrescricaoEnfermagemVO) obj;
		
		return (this.getAtendimentoSeq() != null && this.getAtendimentoSeq().equals(other.getAtendimentoSeq()))
				&& (this.getItemSeq() != null && this.getItemSeq().equals(other.getItemSeq()))
				&& (this.getDescricao() != null && this.getDescricao().equals(other.getDescricao()))
				&& (this.getTipo() != null && this.getTipo().equals(other.getTipo()));
	}
	
	public void setExcluir(Boolean excluir) {
		this.excluir = excluir;
	}

	public Boolean getExcluir() {
		return excluir;
	}

	public void setMarcado(Boolean marcado) {
		this.marcado = marcado;
	}

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

	public Boolean getIsNew() {
		return isNew;
	}

	public void setIsNew(Boolean isNew) {
		this.isNew = isNew;
	}

	public void setPrescricaoEnfermagem(EpePrescricaoEnfermagem prescricaoEnfermagem) {
		this.prescricaoEnfermagem = prescricaoEnfermagem;
	}

	public EpePrescricaoEnfermagem getPrescricaoEnfermagem() {
		return prescricaoEnfermagem;
	}
	
}