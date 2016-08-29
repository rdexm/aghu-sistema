package br.gov.mec.aghu.prescricaomedica.vo;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import br.gov.mec.aghu.core.commons.BaseBean;

/**
 * #990 VO de Composicoes - C4 - VO filho
 * 
 * @author paulo
 *
 */
public class MpmComposicaoPrescricaoNptVO implements BaseBean {

	/**
	 * 
	 */
	private static final long serialVersionUID = 652711289609470795L;
	private Integer pnpAtdSeq;
	private Integer pnpSeq;
	private Short seqp;
	private Short ticSeq;
	private String composicaoDescricao;
	private Short tvaSeq;
	private String unidade;
	private BigDecimal velocidadeAdministracao;
	private Byte qtdeHorasCorrer;
	private List<MpmItemPrescricaoNptVO> componentes = new ArrayList<MpmItemPrescricaoNptVO>();

	public enum Fields {
		PNP_ATD_SEQ("pnpAtdSeq"), PNP_SEQ("pnpSeq"), SEQP("seqp"), TIC_SEQ("ticSeq"), COMPOSICAO_DESCRICAO("composicaoDescricao"), TVA_SEQ("tvaSeq"), UNIDADE("unidade"), VELOCIDADE_ADMINISTRACAO("velocidadeAdministracao"), QTDE_HORAS_CORRER("qtdeHorasCorrer");

		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return this.fields;
		}
	}

	public Integer getPnpAtdSeq() {
		return pnpAtdSeq;
	}

	public void setPnpAtdSeq(Integer pnpAtdSeq) {
		this.pnpAtdSeq = pnpAtdSeq;
	}

	public Integer getPnpSeq() {
		return pnpSeq;
	}

	public void setPnpSeq(Integer pnpSeq) {
		this.pnpSeq = pnpSeq;
	}

	public Short getSeqp() {
		return seqp;
	}

	public void setSeqp(Short seqp) {
		this.seqp = seqp;
	}

	public Short getTicSeq() {
		return ticSeq;
	}

	public void setTicSeq(Short ticSeq) {
		this.ticSeq = ticSeq;
	}

	public String getComposicaoDescricao() {
		return composicaoDescricao;
	}

	public void setComposicaoDescricao(String composicaoDescricao) {
		this.composicaoDescricao = composicaoDescricao;
	}

	public Short getTvaSeq() {
		return tvaSeq;
	}

	public void setTvaSeq(Short tvaSeq) {
		this.tvaSeq = tvaSeq;
	}

	public String getUnidade() {
		return unidade;
	}

	public void setUnidade(String unidade) {
		this.unidade = unidade;
	}

	public BigDecimal getVelocidadeAdministracao() {
		return velocidadeAdministracao;
	}

	public void setVelocidadeAdministracao(BigDecimal velocidadeAdministracao) {
		this.velocidadeAdministracao = velocidadeAdministracao;
	}

	public Byte getQtdeHorasCorrer() {
		return qtdeHorasCorrer;
	}

	public void setQtdeHorasCorrer(Byte qtdeHorasCorrer) {
		this.qtdeHorasCorrer = qtdeHorasCorrer;
	}

	public List<MpmItemPrescricaoNptVO> getComponentes() {
		return componentes;
	}

	public void setComponentes(List<MpmItemPrescricaoNptVO> componentes) {
		this.componentes = componentes;
	}

	@Override
	public int hashCode() {
		HashCodeBuilder umHashCodeBuilder = new HashCodeBuilder();
		umHashCodeBuilder.append(this.getComponentes());
		umHashCodeBuilder.append(this.getComposicaoDescricao());
		umHashCodeBuilder.append(this.getPnpAtdSeq());
		umHashCodeBuilder.append(this.getPnpSeq());
		umHashCodeBuilder.append(this.getQtdeHorasCorrer());
		umHashCodeBuilder.append(this.getSeqp());
		umHashCodeBuilder.append(this.getTicSeq());
		umHashCodeBuilder.append(this.getTvaSeq());
		umHashCodeBuilder.append(this.getUnidade());
		umHashCodeBuilder.append(this.getVelocidadeAdministracao());
		return umHashCodeBuilder.toHashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof MpmComposicaoPrescricaoNptVO)) {
			return false;
		}
		MpmComposicaoPrescricaoNptVO other = (MpmComposicaoPrescricaoNptVO) obj;
		EqualsBuilder umEqualsBuilder = new EqualsBuilder();
		umEqualsBuilder.append(this.getComponentes(), other.getComponentes());
		umEqualsBuilder.append(this.getComposicaoDescricao(), other.getComposicaoDescricao());
		umEqualsBuilder.append(this.getPnpAtdSeq(), other.getPnpAtdSeq());
		umEqualsBuilder.append(this.getPnpSeq(), other.getPnpSeq());
		umEqualsBuilder.append(this.getQtdeHorasCorrer(), other.getQtdeHorasCorrer());
		umEqualsBuilder.append(this.getSeqp(), other.getSeqp());
		umEqualsBuilder.append(this.getTicSeq(), other.getTicSeq());
		umEqualsBuilder.append(this.getTvaSeq(), other.getTvaSeq());
		umEqualsBuilder.append(this.getUnidade(), other.getUnidade());
		umEqualsBuilder.append(this.getVelocidadeAdministracao(), other.getVelocidadeAdministracao());
		return umEqualsBuilder.isEquals();
	}
}