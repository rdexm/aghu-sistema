package br.gov.mec.aghu.prescricaomedica.vo;

import java.io.Serializable;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;

import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioTipoPrescricaoReceituario;
import br.gov.mec.aghu.dominio.DominioTipoUsoReceituario;
import br.gov.mec.aghu.model.MamItemReceituario;

/**
 * 
 * @author andremachado
 * 
 */

public class ItemReceituarioVO implements Serializable, Cloneable, Comparable<ItemReceituarioVO> {
	/**
	 * 
	 */
	private static final long serialVersionUID = 224319410633172027L;
	// chave composta de MamItemReceituario
	private Long rctSeq;
	private Short seqp;

	private DominioTipoPrescricaoReceituario tipoPrescricao;
	private DominioTipoUsoReceituario indInterno;
	private Boolean indUsoContinuo;
	private String descricao;
	private String quantidade;
	private String formaUso;
	private Byte validadeMeses;
	private DominioSimNao indValidadeProlongada;
	private Byte nroGrupoImpressao;
	private Byte ordem;
	private DominioSituacao indSituacao;

	private MamItemReceituario ItemReceituario;
	private boolean edicao;
	private boolean persistido;
	private String descricaoEditada;


	public ItemReceituarioVO() {
	}

	public ItemReceituarioVO(MamItemReceituario item) {
		this.rctSeq = item.getId().getRctSeq();
		this.seqp = item.getId().getSeqp();
		this.tipoPrescricao = item.getTipoPrescricao();
		
		if(DominioSimNao.S.equals(item.getIndInterno())) {
			this.indInterno = DominioTipoUsoReceituario.S;
		} else {
			this.indInterno = DominioTipoUsoReceituario.N;
		}
		this.indUsoContinuo = false;
		this.descricao = item.getDescricao();
		this.quantidade = item.getQuantidade();
		this.formaUso = item.getFormaUso();
		this.validadeMeses = item.getValidadeMeses();
		this.indValidadeProlongada = item.getIndValidadeProlongada();
		this.nroGrupoImpressao = item.getNroGrupoImpressao();
		this.ordem = item.getOrdem();
		this.indSituacao = item.getIndSituacao();
		this.ItemReceituario = item;
	}

	public Long getRctSeq() {
		return rctSeq;
	}

	public void setRctSeq(Long rctSeq) {
		this.rctSeq = rctSeq;
	}

	public Short getSeqp() {
		return seqp;
	}

	public void setSeqp(Short seqp) {
		this.seqp = seqp;
	}

	public DominioTipoPrescricaoReceituario getTipoPrescricao() {
		return tipoPrescricao;
	}

	public void setTipoPrescricao(
			DominioTipoPrescricaoReceituario tipoPrescricao) {
		this.tipoPrescricao = tipoPrescricao;
	}

	public DominioTipoUsoReceituario getIndInterno() {
		return indInterno;
	}

	public void setIndInterno(DominioTipoUsoReceituario indInterno) {
		this.indInterno = indInterno;
	}

	public Boolean setIndUsoContinuo() {
		return indUsoContinuo;
	}
	
	public Boolean getIndUsoContinuo() {
		return indUsoContinuo;
	}

	public void setIndUsoContinuo(Boolean indUsoContinuo) {
		this.indUsoContinuo = indUsoContinuo;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public String getQuantidade() {
		return quantidade;
	}

	public void setQuantidade(String quantidade) {
		this.quantidade = quantidade;
	}

	public String getFormaUso() {
		return formaUso;
	}

	public void setFormaUso(String formaUso) {
		this.formaUso = formaUso;
	}

	public Byte getValidadeMeses() {
		return validadeMeses;
	}

	public void setValidadeMeses(Byte validadeMeses) {
		this.validadeMeses = validadeMeses;
	}

	public DominioSimNao getIndValidadeProlongada() {
		return indValidadeProlongada;
	}

	public void setIndValidadeProlongada(DominioSimNao indValidadeProlongada) {
		this.indValidadeProlongada = indValidadeProlongada;
	}

	public Byte getNroGrupoImpressao() {
		return nroGrupoImpressao;
	}

	public void setNroGrupoImpressao(Byte nroGrupoImpressao) {
		this.nroGrupoImpressao = nroGrupoImpressao;
	}

	public Byte getOrdem() {
		return ordem;
	}

	public void setOrdem(Byte ordem) {
		this.ordem = ordem;
	}

	public DominioSituacao getIndSituacao() {
		return indSituacao;
	}

	public void setIndSituacao(DominioSituacao indSituacao) {
		this.indSituacao = indSituacao;
	}

	public MamItemReceituario getItemReceituario() {
		return ItemReceituario;
	}

	public void setItemReceituario(MamItemReceituario itemReceituario) {
		ItemReceituario = itemReceituario;
	}

	public boolean isEdicao() {
		return edicao;
	}

	public void setEdicao(boolean edicao) {
		this.edicao = edicao;
	}

	public boolean isPersistido() {
		return persistido;
	}

	public void setPersistido(boolean persistido) {
		this.persistido = persistido;
	}

	/**
	 * RN03 - Montar resumo do item de receita
	 * @return descricaoEditada
	 */
	public String getDescricaoEditada() {

		this.descricaoEditada = this.descricao;
		
		if (StringUtils.isNotBlank(this.quantidade)) {
			this.descricaoEditada=this.descricaoEditada + " - " + this.quantidade;
		}
		
		if (StringUtils.isNotBlank(this.formaUso)) {
			this.descricaoEditada=this.descricaoEditada + " : " + this.formaUso ;
		}
		//Uso Interno 
		if (DominioTipoUsoReceituario.S.equals(this.indInterno)) {
			this.descricaoEditada=this.descricaoEditada + ". Uso Interno";
		} else if (DominioTipoUsoReceituario.N.equals(this.indInterno)) {
			this.descricaoEditada=this.descricaoEditada + ". Uso Externo";
		}
		//Contínuo
		if (this.getIndUsoContinuo()) {
			this.descricaoEditada=this.descricaoEditada + ", Contínuo";
		}
		//Validade
		if (this.validadeMeses != null) {
			this.descricaoEditada=this.descricaoEditada + ", Validade: " + this.validadeMeses + " meses";
		}
		
		return descricaoEditada;
	}

	public void setDescricaoEditada(String descricaoEditada) {
		this.descricaoEditada = descricaoEditada;
	}
		
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((rctSeq == null) ? 0 : rctSeq.hashCode());
		result = prime * result + ((seqp == null) ? 0 : seqp.hashCode());
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
		if (!(obj instanceof ItemReceituarioVO)) {
			return false;
		}
		ItemReceituarioVO other = (ItemReceituarioVO) obj;
		if (getDescricao() == null) {
			if (other.getDescricao() != null) {
				return false;
			}
		} else if (!getDescricao().equals(other.getDescricao())) {
			return false;
		}
		if (getRctSeq() == null) {
			if (other.getRctSeq() != null) {
				return false;
			}
		} else if (!getRctSeq().equals(other.getRctSeq())) {
			return false;
		}
		if (getSeqp() == null) {
			if (other.getSeqp() != null) {
				return false;
			}
		} else if (!getSeqp().equals(other.getSeqp())) {
			return false;
		}
		return true;
	}
	
	

	@Override
	public String toString() {
		return new ToStringBuilder(this).append("rctSeq", this.rctSeq)
				.append("seqp", this.seqp).toString();
	}

	@Override
	public Object clone() throws CloneNotSupportedException {
		return super.clone();
	}

	/**
	 * Compara este objeto com o objeto fornecido para ordenação.<br>
	 * A ordem natural implementada por este método é pelo atributo: Ordem
	 */
	@Override
	public int compareTo(ItemReceituarioVO other) {
		int result = 0 ;
		if (this.ordem != null && other.getOrdem() != null) {
		   result = this.ordem.compareTo(other.getOrdem());
		}  
		return result;
	}

}
