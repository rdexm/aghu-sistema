/**
 * 
 */
package br.gov.mec.aghu.prescricaomedica.vo;

import java.io.Serializable;
import java.util.Date;

import org.apache.commons.lang3.StringEscapeUtils;

import br.gov.mec.aghu.dominio.DominioIndPendenteItemPrescricao;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.model.MpmPrescricaoMdto;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.prescricaomedica.util.PrescricaoMedicaTypes;

/**
 * @author rcorvalao
 * 
 */
public class ItemPrescricaoMedicaVO implements Serializable {
	
	private static final long serialVersionUID = -1695374846842351294L;
	
	/**
	 * 
	 */
	private static final String ESCAPE_IGNORE = "&lt;br/&gt;";
	private static final String ESCAPE_IGNORE_CAPTALIZE = "&lt;BR/&gt;";
	private static final String QUEBRA_LINHA = "<br/>";
	
	private MpmPrescricaoMdto prescricaoMedicamento;
	
	private Integer atendimentoSeq;
	private Long itemSeq;
	
	private String descricao;
	private PrescricaoMedicaTypes tipo;
	
	private Boolean hierarquico;
	private Boolean emEdicao = Boolean.FALSE;
	private Boolean isNew = Boolean.FALSE;
	private Date dthrInicio;
	private Date dthrFim;
	private Date criadoEm;
	private Date alteradoEm;
	private DominioIndPendenteItemPrescricao situacao;
	private RapServidores servidorValidacao;
	private RapServidores servidorValidaMovimentacao;
	
	private String nomeMedicamento;
	private String dosagem;
	private String intervalo;
	private String via;
	private String dias;
	private Date dtFimTratamento;
	
	private Short seqp;
	
	/**
	 * Utilizado na tela de prescricao medica.
	 * Quando o usuario marcar o item para exclusao.
	 */
	private Boolean excluir = Boolean.FALSE;

	/**
	 * Utilizado para identificar se o item foi marcado na tela.
	 * Uso geral.
	 */
	private Boolean marcado;
	
	/**
	 * Indicar de forma diferenciada o item em uma lista.
	 */
	private Boolean mesclado = Boolean.FALSE;
	
	/**
	 * Atributos utilizados no formulÃ¡rio de antimicrobiÃ¡nos 
	 */
	private String agentesIsolados;
	private DominioSimNao autorizacao;
	private DominioSimNao alteracao;
	
	/**
	 * Atributo utilizado para sinalizar existência de medicamento represcrito.
	 */
	private Boolean represcrito;
	
	public ItemPrescricaoMedicaVO(){}
	
	public ItemPrescricaoMedicaVO(ItemPrescricaoMedicaVO item){
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
		this.setPrescricaoMedicamento(item.getPrescricaoMedicamento());
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

	public void setItemSeq(Long itemSeq) {
		this.itemSeq = itemSeq;
	}

	public Long getItemSeq() {
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

	public DominioIndPendenteItemPrescricao getSituacao() {
		return situacao;
	}

	public void setSituacao(DominioIndPendenteItemPrescricao situacao) {
		this.situacao = situacao;
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
		
		ItemPrescricaoMedicaVO other = (ItemPrescricaoMedicaVO) obj;
		
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

	/**
	 * @param prescricaoMedicamento the prescricaoMedicamento to set
	 */
	public void setPrescricaoMedicamento(MpmPrescricaoMdto prescricaoMedicamento) {
		this.prescricaoMedicamento = prescricaoMedicamento;
	}

	/**
	 * @return the prescricaoMedicamento
	 */
	public MpmPrescricaoMdto getPrescricaoMedicamento() {
		return prescricaoMedicamento;
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

	public Boolean getIsNew() {
		return isNew;
	}

	public void setIsNew(Boolean isNew) {
		this.isNew = isNew;
	}
	
	public String getDosagem() {
		return dosagem;
	}

	public void setDosagem(String dosagem) {
		this.dosagem = dosagem;
	}

	public String getIntervalo() {
		return intervalo;
	}

	public void setIntervalo(String intervalo) {
		this.intervalo = intervalo;
	}

	public String getVia() {
		return via;
	}

	public void setVia(String via) {
		this.via = via;
	}

	public String getDias() {
		return dias;
	}

	public void setDias(String dias) {
		this.dias = dias;
	}

	public Date getDtFimTratamento() {
		return dtFimTratamento;
	}

	public void setDtFimTratamento(Date dtFimTratamento) {
		this.dtFimTratamento = dtFimTratamento;
	}

	public String getAgentesIsolados() {
		return agentesIsolados;
	}

	public void setAgentesIsolados(String agentesIsolados) {
		this.agentesIsolados = agentesIsolados;
	}

	public DominioSimNao getAutorizacao() {
		if (autorizacao == null) {
			autorizacao = DominioSimNao.S;
		}
		return autorizacao;
	}

	public void setAutorizacao(DominioSimNao autorizacao) {
		this.autorizacao = autorizacao;
	}

	public DominioSimNao getAlteracao() {
		if (alteracao == null) {
			alteracao = DominioSimNao.N;
		}
		return alteracao;
	}

	public void setAlteracao(DominioSimNao alteracao) {
		this.alteracao = alteracao;
	}
	
	public String getAutorizacaoDs() {
		return getAutorizacao().getDescricao();
	}
	
	public String getAlteracaoDs() {
		return getAlteracao().getDescricao();
	}
	
	public Boolean getReprescrito() {
		return represcrito;
	}

	public void setReprescrito(Boolean represcrito) {
		this.represcrito = represcrito;
	}

	public Short getSeqp() {
		return seqp;
	}

	public void setSeqp(Short seqp) {
		this.seqp = seqp;
	}

	public String getNomeMedicamento() {
		return nomeMedicamento;
	}

	public void setNomeMedicamento(String nomeMedicamento) {
		this.nomeMedicamento = nomeMedicamento;
	}

	
}