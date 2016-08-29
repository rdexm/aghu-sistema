package br.gov.mec.aghu.model;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.Parameter;

import br.gov.mec.aghu.core.persistence.BaseEntityId;
import br.gov.mec.aghu.dominio.DominioAlinhamentoParametroCamposLaudo;
import br.gov.mec.aghu.dominio.DominioExibicaoParametroCamposLaudo;
import br.gov.mec.aghu.dominio.DominioObjetoVisual;
import br.gov.mec.aghu.dominio.DominioSimNao;

@Entity
@Table(name = "AEL_PARAMETRO_CAMPOS_LAUDO", schema = "AGH")
public class AelParametroCamposLaudo extends BaseEntityId<AelParametroCampoLaudoId> implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2189581639176301916L;
	private AelParametroCampoLaudoId id;
	private Boolean sumarioSemMascara;
	private Boolean italico;
	private Boolean negrito;
	private Boolean sublinhado;
	private String fonte;
	private DominioExibicaoParametroCamposLaudo exibicao;
	private Short tamanhoFonte;
	private Integer quantidadeCaracteres;
	private DominioAlinhamentoParametroCamposLaudo alinhamento;
	private Short posicaoColunaImpressao;
	private Short posicaoColunaTela;
	private Integer posicaoLinhaImpressao;
	private Integer posicaoLinhaTela;
	private String textoLivre;
	private Short quantidadeCasasDecimais;
	private DominioObjetoVisual objetoVisual;
	private Boolean riscado;
	private String cor;
	private Short script;
	private Short larguraObjetoVisual;
	private Short alturaObjetoVisual;
	private Boolean campoRecebimentoImagem;
	private AelCampoLaudo campoLaudo;
	private AelCampoLaudo campoLaudoRelacionado;
	private Set<AelResultadoExame> resultadosExames = new HashSet<AelResultadoExame>(
			0);
	private Set<AelCampoVinculado> campoVinculados;
	private AelVersaoLaudo aelVersaoLaudo;
	private Set<AelCampoCodifRelacionado> campoCodifRelacionado;
	private Set<AelCampoLaudoRelacionado> camposLaudoRelacionados;

	public AelParametroCamposLaudo() {
	}

	public AelParametroCamposLaudo(AelParametroCampoLaudoId id,
			Boolean sumarioSemMascara, Boolean italico, Boolean negrito,
			Boolean sublinhado, String fonte,
			DominioExibicaoParametroCamposLaudo exibicao, Short tamanhoFonte,
			Integer quantidadeCaracteres, Boolean riscado) {
		this.id = id;
		this.sumarioSemMascara = sumarioSemMascara;
		this.italico = italico;
		this.negrito = negrito;
		this.sublinhado = sublinhado;
		this.fonte = fonte;
		this.exibicao = exibicao;
		this.tamanhoFonte = tamanhoFonte;
		this.quantidadeCaracteres = quantidadeCaracteres;
		this.riscado = riscado;
	}

	@SuppressWarnings({"PMD.ExcessiveParameterList"})
	public AelParametroCamposLaudo(AelParametroCampoLaudoId id,
			Boolean sumarioSemMascara, Boolean italico, Boolean negrito,
			Boolean sublinhado, String fonte,
			DominioExibicaoParametroCamposLaudo exibicao, Short tamanhoFonte,
			Integer quantidadeCaracteres, DominioAlinhamentoParametroCamposLaudo alinhamento,
			Short posicaoColunaImpressao, Short posicaoColunaTela,
			Integer posicaoLinhaImpressao, Integer posicaoLinhaTela,
			String textoLivre, Short quantidadeCasasDecimais,
			DominioObjetoVisual objetoVisual, Boolean riscado, String cor,
			Short script, Short larguraObjetoVisual, Short alturaObjetoVisual,
			Boolean campoRecebimentoImagem,
			AelCampoLaudo campoLaudoRelacionado,
			Set<AelResultadoExame> resultadosExames) {
		this.id = id;
		this.sumarioSemMascara = sumarioSemMascara;
		this.italico = italico;
		this.negrito = negrito;
		this.sublinhado = sublinhado;
		this.fonte = fonte;
		this.exibicao = exibicao;
		this.tamanhoFonte = tamanhoFonte;
		this.quantidadeCaracteres = quantidadeCaracteres;
		this.alinhamento = alinhamento;
		this.posicaoColunaImpressao = posicaoColunaImpressao;
		this.posicaoColunaTela = posicaoColunaTela;
		this.posicaoLinhaImpressao = posicaoLinhaImpressao;
		this.posicaoLinhaTela = posicaoLinhaTela;
		this.textoLivre = textoLivre;
		this.quantidadeCasasDecimais = quantidadeCasasDecimais;
		this.objetoVisual = objetoVisual;
		this.riscado = riscado;
		this.cor = cor;
		this.script = script;
		this.larguraObjetoVisual = larguraObjetoVisual;
		this.alturaObjetoVisual = alturaObjetoVisual;
		this.campoLaudoRelacionado = campoLaudoRelacionado;
		this.campoRecebimentoImagem = campoRecebimentoImagem;
		this.resultadosExames = resultadosExames;
	}

	@EmbeddedId
	@AttributeOverrides({
			@AttributeOverride(name = "velEmaExaSigla", column = @Column(name = "VEL_EMA_EXA_SIGLA", nullable = false, length = 5)),
			@AttributeOverride(name = "velEmaManSeq", column = @Column(name = "VEL_EMA_MAN_SEQ", nullable = false, precision = 5, scale = 0)),
			@AttributeOverride(name = "velSeqp", column = @Column(name = "VEL_SEQP", nullable = false, precision = 5, scale = 0)),
			@AttributeOverride(name = "calSeq", column = @Column(name = "CAL_SEQ", nullable = false, precision = 7, scale = 0)),
			@AttributeOverride(name = "seqp", column = @Column(name = "SEQP", nullable = false, precision = 5, scale = 0)) })
	public AelParametroCampoLaudoId getId() {
		return this.id;
	}

	public void setId(AelParametroCampoLaudoId id) {
		this.id = id;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns({
			@JoinColumn(name = "VEL_EMA_EXA_SIGLA", referencedColumnName = "EMA_EXA_SIGLA", nullable = false, insertable = false, updatable = false),
			@JoinColumn(name = "VEL_EMA_MAN_SEQ", referencedColumnName = "EMA_MAN_SEQ", nullable = false, insertable = false, updatable = false),
			@JoinColumn(name = "VEL_SEQP", referencedColumnName = "SEQP", nullable = false, insertable = false, updatable = false) })
	public AelVersaoLaudo getAelVersaoLaudo() {
		return aelVersaoLaudo;
	}

	public void setAelVersaoLaudo(AelVersaoLaudo aelVersaoLaudo) {
		this.aelVersaoLaudo = aelVersaoLaudo;
	}

	@Column(name = "IND_SUMARIO_SEM_MASCARA", length = 1)
	@org.hibernate.annotations.Type(type = "br.gov.mec.aghu.core.persistence.type.BooleanUserType")
	public Boolean getSumarioSemMascara() {
		return this.sumarioSemMascara;
	}
	
	@Transient
	public DominioSimNao getSumarioSemMascaraDominio() {
		if(sumarioSemMascara != null) {
			return DominioSimNao.getInstance(this.sumarioSemMascara);
		}
		return null;
	}	

	public void setSumarioSemMascara(Boolean sumarioSemMascara) {
		this.sumarioSemMascara = sumarioSemMascara;
	}
	
	public void setSumarioSemMascaraDominio(DominioSimNao dominio) {
		this.sumarioSemMascara = dominio.isSim();
	}	

	@Column(name = "ITALICO", length = 1)
	@org.hibernate.annotations.Type(type = "br.gov.mec.aghu.core.persistence.type.BooleanUserType")
	public Boolean getItalico() {
		return this.italico;
	}

	public void setItalico(Boolean italico) {
		this.italico = italico;
	}

	@Column(name = "NEGRITO", length = 1)
	@org.hibernate.annotations.Type(type = "br.gov.mec.aghu.core.persistence.type.BooleanUserType")
	public Boolean getNegrito() {
		return this.negrito;
	}

	public void setNegrito(Boolean negrito) {
		this.negrito = negrito;
	}

	@Column(name = "SUBLINHADO", length = 1)
	@org.hibernate.annotations.Type(type = "br.gov.mec.aghu.core.persistence.type.BooleanUserType")
	public Boolean getSublinhado() {
		return this.sublinhado;
	}

	public void setSublinhado(Boolean sublinhado) {
		this.sublinhado = sublinhado;
	}

	@Column(name = "FONTE", nullable = false, length = 30)
	public String getFonte() {
		return this.fonte;
	}

	public void setFonte(String fonte) {
		this.fonte = fonte;
	}

	@Column(name = "IND_EXIBICAO", nullable = false, length = 1)
	@Enumerated(EnumType.STRING)
	public DominioExibicaoParametroCamposLaudo getExibicao() {
		return this.exibicao;
	}

	public void setExibicao(DominioExibicaoParametroCamposLaudo exibicao) {
		this.exibicao = exibicao;
	}

	@Column(name = "TAMANHO_FONTE", nullable = false, precision = 2, scale = 0)
	public Short getTamanhoFonte() {
		return this.tamanhoFonte;
	}

	public void setTamanhoFonte(Short tamanhoFonte) {
		this.tamanhoFonte = tamanhoFonte;
	}

	@Column(name = "QTDE_CARACTERES", nullable = false, precision = 5, scale = 0)
	public Integer getQuantidadeCaracteres() {
		return this.quantidadeCaracteres;
	}

	public void setQuantidadeCaracteres(Integer quantidadeCaracteres) {
		this.quantidadeCaracteres = quantidadeCaracteres;
	}

	@Column(name = "ALINHAMENTO", length = 1)
	@Enumerated(EnumType.STRING)
	public DominioAlinhamentoParametroCamposLaudo getAlinhamento() {
		return this.alinhamento;
	}

	public void setAlinhamento(DominioAlinhamentoParametroCamposLaudo alinhamento) {
		this.alinhamento = alinhamento;
	}

	@Column(name = "POSICAO_COLUNA_IMP", precision = 3, scale = 0)
	public Short getPosicaoColunaImpressao() {
		return this.posicaoColunaImpressao;
	}

	public void setPosicaoColunaImpressao(Short posicaoColunaImpressao) {
		this.posicaoColunaImpressao = posicaoColunaImpressao;
	}

	@Column(name = "POSICAO_COLUNA_TELA", precision = 3, scale = 0)
	public Short getPosicaoColunaTela() {
		return this.posicaoColunaTela;
	}

	public void setPosicaoColunaTela(Short posicaoColunaTela) {
		this.posicaoColunaTela = posicaoColunaTela;
	}

	@Column(name = "POSICAO_LINHA_IMP", precision = 5, scale = 0)
	public Integer getPosicaoLinhaImpressao() {
		return this.posicaoLinhaImpressao;
	}

	public void setPosicaoLinhaImpressao(Integer posicaoLinhaImpressao) {
		this.posicaoLinhaImpressao = posicaoLinhaImpressao;
	}

	@Column(name = "POSICAO_LINHA_TELA", precision = 5, scale = 0)
	public Integer getPosicaoLinhaTela() {
		return this.posicaoLinhaTela;
	}

	public void setPosicaoLinhaTela(Integer posicaoLinhaTela) {
		this.posicaoLinhaTela = posicaoLinhaTela;
	}

	@Column(name = "TEXTO_LIVRE", length = 4000)
	public String getTextoLivre() {
		return this.textoLivre;
	}

	public void setTextoLivre(String textoLivre) {
		this.textoLivre = textoLivre;
	}

	@Column(name = "QTDE_CASAS_DECIMAIS", precision = 2, scale = 0)
	public Short getQuantidadeCasasDecimais() {
		return this.quantidadeCasasDecimais;
	}

	public void setQuantidadeCasasDecimais(Short quantidadeCasasDecimais) {
		this.quantidadeCasasDecimais = quantidadeCasasDecimais;
	}
 
	@Column(name = "OBJETO_VISUAL", length = 1)
	@org.hibernate.annotations.Type(parameters = { @Parameter(name = "enumClassName", value = "br.gov.mec.aghu.dominio.DominioObjetoVisual") }, type = "br.gov.mec.aghu.model.jpa.ParametroLaudoUserType")
	public DominioObjetoVisual getObjetoVisual() {
		return this.objetoVisual;
	}

	public void setObjetoVisual(DominioObjetoVisual objetoVisual) {
		this.objetoVisual = objetoVisual;
	}

	@Column(name = "RISCADO", length = 1)
	@org.hibernate.annotations.Type(type = "br.gov.mec.aghu.core.persistence.type.BooleanUserType")
	public Boolean getRiscado() {
		return this.riscado;
	}

	public void setRiscado(Boolean riscado) {
		this.riscado = riscado;
	}

	@Column(name = "COR", length = 30)
	public String getCor() {
		return this.cor;
	}

	public void setCor(String cor) {
		this.cor = cor;
	}

	@Column(name = "SCRIPT", precision = 3, scale = 0)
	public Short getScript() {
		return this.script;
	}

	public void setScript(Short script) {
		this.script = script;
	}

	@Column(name = "LARGURA_OBJETO_VISUAL", precision = 3, scale = 0)
	public Short getLarguraObjetoVisual() {
		return this.larguraObjetoVisual;
	}

	public void setLarguraObjetoVisual(Short larguraObjetoVisual) {
		this.larguraObjetoVisual = larguraObjetoVisual;
	}

	@Column(name = "ALTURA_OBJETO_VISUAL", precision = 3, scale = 0)
	public Short getAlturaObjetoVisual() {
		return this.alturaObjetoVisual;
	}

	public void setAlturaObjetoVisual(Short alturaObjetoVisual) {
		this.alturaObjetoVisual = alturaObjetoVisual;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "CAL_SEQ", nullable = false, insertable = false, updatable = false)
	public AelCampoLaudo getCampoLaudo() {
		return campoLaudo;
	}

	public void setCampoLaudo(AelCampoLaudo campoLaudo) {
		this.campoLaudo = campoLaudo;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "CAL_SEQ_RELACIONADO")
	public AelCampoLaudo getCampoLaudoRelacionado() {
		return campoLaudoRelacionado;
	}

	public void setCampoLaudoRelacionado(AelCampoLaudo campoLaudoRelacionado) {
		this.campoLaudoRelacionado = campoLaudoRelacionado;
	}

	@Column(name = "IND_CAMPO_RECEB_IMAGEM", length = 1)
	@org.hibernate.annotations.Type(type = "br.gov.mec.aghu.core.persistence.type.BooleanUserType")
	public Boolean getCampoRecebimentoImagem() {
		return this.campoRecebimentoImagem;
	}

	public void setCampoRecebimentoImagem(Boolean campoRecebimentoImagem) {
		this.campoRecebimentoImagem = campoRecebimentoImagem;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "parametroCampoLaudo")
	public Set<AelResultadoExame> getResultadosExames() {
		return resultadosExames;
	}

	public void setResultadosExames(Set<AelResultadoExame> resultadosExames) {
		this.resultadosExames = resultadosExames;
	}
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "aelParametroCamposLaudoByAelCvcPclFk1")
	public Set<AelCampoVinculado> getCampoVinculados() {
		return campoVinculados;
	}
	
	public void setCampoVinculados(Set<AelCampoVinculado> campoVinculados) {
		this.campoVinculados = campoVinculados;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "aelParametroCamposLaudoByAelCcrPclFk1")
	public Set<AelCampoCodifRelacionado> getCampoCodifRelacionado() {
		return campoCodifRelacionado;
	}

	public void setCampoCodifRelacionado(
			Set<AelCampoCodifRelacionado> campoCodifRelacionado) {
		this.campoCodifRelacionado = campoCodifRelacionado;
	}
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "aelParametroCamposLaudoByAelClvPclFk1")
	public Set<AelCampoLaudoRelacionado> getCamposLaudoRelacionados() {
		return camposLaudoRelacionados;
	}

	public void setCamposLaudoRelacionados(Set<AelCampoLaudoRelacionado> camposLaudoRelacionados) {
		this.camposLaudoRelacionados = camposLaudoRelacionados;
	}

	@Transient
	public Integer posicaoFinalLinhaTela() {
		return (this.posicaoLinhaTela != null ? this.posicaoLinhaTela : 0)
				+ (this.alturaObjetoVisual != null ? this.alturaObjetoVisual : 0);
	}

	@PrePersist
	@PreUpdate
	@SuppressWarnings("unused")
	private void validar() {
		if (this.sumarioSemMascara == null) {
			this.sumarioSemMascara = true;
		}

		if (this.italico == null) {
			this.italico = false;
		}

		if (this.negrito == null) {
			this.negrito = false;
		}

		if (this.sublinhado == null) {
			this.sublinhado = false;
		}

		if (this.campoRecebimentoImagem == null) {
			this.campoRecebimentoImagem = false;
		}
	}
	
	
	

	public enum Fields {

		ID("id"), 
		VEL_EMA_EXA_SIGLA("id.velEmaExaSigla"), 
		VEL_EMA_MAN_SEQ("id.velEmaManSeq"), 
		VEL_SEQP("id.velSeqp"), 
		CAL_SEQ("id.calSeq"), 
		SEQP("id.seqp"), 
		SUMARIO_SEM_MASCARA("sumarioSemMascara"), 
		ITALICO("italico"), 
		NEGRITO("negrito"), 
		SUBLINHADO("sublinhado"), 
		FONTE("fonte"),
		EXIBICAO("exibicao"), 
		TAMANHO_FONTE("tamanhoFonte"), 
		QUANTIDADE_CARACTERES("quantidadeCaracteres"), 
		ALINHAMENTO("alinhamento"), 
		POSICAO_COLUNA_IMPRESSAO("posicaoColunaImpressao"), 
		POSICAO_COLUNA_TELA("posicaoColunaTela"), 
		POSICAO_LINHA_IMPRESSAO("posicaoLinhaImpressao"), 
		POSICAO_LINHA_TELA("posicaoLinhaTela"),
		TEXTO_LIVRE("textoLivre"),
		QUANTIDADE_CASAS_DECIMAIS("quantidadeCasasDecimais"), 
		OBJETO_VISUAL("objetoVisual"), 
		RISCADO("riscado"), 
		COR("cor"), 
		SCRIPT("script"),
		LARGURA_OBJETO_VISUAL("larguraObjetoVisual"), 
		ALTURA_OBJETO_VISUAL("alturaObjetoVisual"), 
		CAMPO_RECEBIMENTO_IMAGEM("campoRecebimentoImagem"),
		CAMPO_LAUDO("campoLaudo"), 
		CAMPO_VINCULADOS("campoVinculados"),
		CAMPO_RELACIONADO("camposLaudoRelacionados"),
		CAMPO_RELACIONADO_CODIFICADO("campoCodifRelacionado"),
		CAMPO_LAUDO_RELACIONADO("campoLaudoRelacionado"),
		RESULTADOS_EXAMES("resultadosExames"), 
		AEL_VERSAO_LAUDO("aelVersaoLaudo");
		

		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return this.fields;
		}

	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!super.equals(obj)) {
			return false;
		}
		if (!(obj instanceof AelParametroCamposLaudo)) {
			return false;
		}
		AelParametroCamposLaudo other = (AelParametroCamposLaudo) obj;
		if (id == null) {
			if (other.id != null) {
				return false;
			}
		} else if (!id.equals(other.id)) {
			return false;
		}
		return true;
	}
}
