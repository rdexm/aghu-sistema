package br.gov.mec.aghu.estoque.vo;

import java.math.BigDecimal;
import java.util.Date;

import br.gov.mec.aghu.dominio.DominioClassifyXYZ;
import br.gov.mec.aghu.dominio.DominioIndFotoSensibilidade;
import br.gov.mec.aghu.dominio.DominioIndProducaoInterna;
import br.gov.mec.aghu.dominio.DominioIndTipoUso;
import br.gov.mec.aghu.dominio.DominioSazonalidade;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioTipoResiduo;
import br.gov.mec.aghu.model.AfaMedicamento;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.SceAlmoxarifado;
import br.gov.mec.aghu.model.ScoGrupoMaterial;
import br.gov.mec.aghu.model.ScoOrigemParecerTecnico;
import br.gov.mec.aghu.model.ScoSiasgMaterialMestre;
import br.gov.mec.aghu.model.ScoUnidadeMedida;

public class ScoMaterialVO {
	
	private Integer codigo;
	private ScoUnidadeMedida scoUnidadeMedida;
	private RapServidores servidor;
	private RapServidores servidorDesativado;
	private String descricao;
	private Date dtDigitacao;
	private DominioSituacao indSituacao;
	private DominioSimNao indEstocavel;
	private DominioSimNao indGenerico;
	private DominioSimNao indMenorPreco;
	private DominioIndProducaoInterna indProducaoInterna;
	private DominioSimNao indAtuQtdeDisponivel;
	private DominioClassifyXYZ classifXyz;
	private DominioSazonalidade sazonalidade;
	private String nome;
	private String observacao;
	private Date dtAlteracao;
	private Date dtDesativacao;
	private DominioSimNao indControleValidade;
	private DominioSimNao indFaturavel;
	private DominioSimNao indEnvioProdInterna;
	private Short almSeqLocalEstq;
	private RapServidores servidorAlteracao;
	private DominioSimNao indPadronizado;
	private DominioSimNao indCcih;
	private Boolean indControleLote;
	private DominioIndFotoSensibilidade indFotosensivel;
	private DominioIndTipoUso indTipoUso;
	private BigDecimal numero;
	private String indCadSapiens;
	private Date dtCadSapiens;
	private RapServidores servidorCadSapiens;
	private Short codRetencao;
	private String codTransacaoSapiens;
	private Integer codSiasg;
	private String codSitTribSapiens;
	private String ncmCodigo;
	private AfaMedicamento afaMedicamento;
	private ScoGrupoMaterial scoGrupoMaterial;
	private SceAlmoxarifado sceAlmoxarifado;
	private ScoOrigemParecerTecnico origemParecerTecnico;
	private Integer codCatmat;
	private Long codMatAntigo;
	private ScoSiasgMaterialMestre catMat;
	private Boolean indCorrosivo;
	private Boolean indInflamavel;
	private Boolean indRadioativo;
	private Boolean indReativo;
	private Boolean indToxico;
	private Boolean indUtilizaEspacoFisico;
	//#26669
	private DominioTipoResiduo indTipoResiduo;
	private Boolean indTermolabil;
	private Boolean indVinculado;
	private Boolean indSustentavel;
	private BigDecimal temperatura;
	private String legislacao;
	private DominioSimNao indConfaz;
	private DominioSimNao indCapCmed;
	//#34760
	private Integer serMatriculaJusProcRel;
	private String justificativaProcRel;
	private Date DataJusProcRel;
	private Short serVinCodigoJusProcRel;
	
	
	public Integer getSerMatriculaJusProcRel() {
		return serMatriculaJusProcRel;
	}
	public void setSerMatriculaJusProcRel(Integer serMatriculaJusProcRel) {
		this.serMatriculaJusProcRel = serMatriculaJusProcRel;
	}
	public String getJustificativaProcRel() {
		return justificativaProcRel;
	}
	public void setJustificativaProcRel(String justificativaProcRel) {
		this.justificativaProcRel = justificativaProcRel;
	}
	public Date getDataJusProcRel() {
		return DataJusProcRel;
	}
	public void setDataJusProcRel(Date dataJusProcRel) {
		DataJusProcRel = dataJusProcRel;
	}
	public Short getSerVinCodigoJusProcRel() {
		return serVinCodigoJusProcRel;
	}
	public void setSerVinCodigoJusProcRel(Short serVinCodigoJusProcRel) {
		this.serVinCodigoJusProcRel = serVinCodigoJusProcRel;
	}
	public Integer getCodigo() {
		return codigo;
	}
	public void setCodigo(Integer codigo) {
		this.codigo = codigo;
	}
	public ScoUnidadeMedida getScoUnidadeMedida() {
		return scoUnidadeMedida;
	}
	public void setScoUnidadeMedida(ScoUnidadeMedida scoUnidadeMedida) {
		this.scoUnidadeMedida = scoUnidadeMedida;
	}
	public RapServidores getServidor() {
		return servidor;
	}
	public void setServidor(RapServidores servidor) {
		this.servidor = servidor;
	}
	public RapServidores getServidorDesativado() {
		return servidorDesativado;
	}
	public void setServidorDesativado(RapServidores servidorDesativado) {
		this.servidorDesativado = servidorDesativado;
	}
	public String getDescricao() {
		return descricao;
	}
	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}
	public Date getDtDigitacao() {
		return dtDigitacao;
	}
	public void setDtDigitacao(Date dtDigitacao) {
		this.dtDigitacao = dtDigitacao;
	}
	public DominioSituacao getIndSituacao() {
		return indSituacao;
	}
	public void setIndSituacao(DominioSituacao indSituacao) {
		this.indSituacao = indSituacao;
	}
	public DominioSimNao getIndEstocavel() {
		return indEstocavel;
	}
	public void setIndEstocavel(DominioSimNao indEstocavel) {
		this.indEstocavel = indEstocavel;
	}
	public DominioSimNao getIndGenerico() {
		return indGenerico;
	}
	public void setIndGenerico(DominioSimNao indGenerico) {
		this.indGenerico = indGenerico;
	}
	public DominioSimNao getIndMenorPreco() {
		return indMenorPreco;
	}
	public void setIndMenorPreco(DominioSimNao indMenorPreco) {
		this.indMenorPreco = indMenorPreco;
	}
	public DominioIndProducaoInterna getIndProducaoInterna() {
		return indProducaoInterna;
	}
	public void setIndProducaoInterna(DominioIndProducaoInterna indProducaoInterna) {
		this.indProducaoInterna = indProducaoInterna;
	}
	public DominioSimNao getIndAtuQtdeDisponivel() {
		return indAtuQtdeDisponivel;
	}
	public void setIndAtuQtdeDisponivel(DominioSimNao indAtuQtdeDisponivel) {
		this.indAtuQtdeDisponivel = indAtuQtdeDisponivel;
	}
	public DominioClassifyXYZ getClassifXyz() {
		return classifXyz;
	}
	public void setClassifXyz(DominioClassifyXYZ classifXyz) {
		this.classifXyz = classifXyz;
	}
	public DominioSazonalidade getSazonalidade() {
		return sazonalidade;
	}
	public void setSazonalidade(DominioSazonalidade sazonalidade) {
		this.sazonalidade = sazonalidade;
	}
	public String getNome() {
		return nome;
	}
	public void setNome(String nome) {
		this.nome = nome;
	}
	public String getObservacao() {
		return observacao;
	}
	public void setObservacao(String observacao) {
		this.observacao = observacao;
	}
	public Date getDtAlteracao() {
		return dtAlteracao;
	}
	public void setDtAlteracao(Date dtAlteracao) {
		this.dtAlteracao = dtAlteracao;
	}
	public Date getDtDesativacao() {
		return dtDesativacao;
	}
	public void setDtDesativacao(Date dtDesativacao) {
		this.dtDesativacao = dtDesativacao;
	}
	public DominioSimNao getIndControleValidade() {
		return indControleValidade;
	}
	public void setIndControleValidade(DominioSimNao indControleValidade) {
		this.indControleValidade = indControleValidade;
	}
	public DominioSimNao getIndFaturavel() {
		return indFaturavel;
	}
	public void setIndFaturavel(DominioSimNao indFaturavel) {
		this.indFaturavel = indFaturavel;
	}
	public DominioSimNao getIndEnvioProdInterna() {
		return indEnvioProdInterna;
	}
	public void setIndEnvioProdInterna(DominioSimNao indEnvioProdInterna) {
		this.indEnvioProdInterna = indEnvioProdInterna;
	}
	public Short getAlmSeqLocalEstq() {
		return almSeqLocalEstq;
	}
	public void setAlmSeqLocalEstq(Short almSeqLocalEstq) {
		this.almSeqLocalEstq = almSeqLocalEstq;
	}
	public RapServidores getServidorAlteracao() {
		return servidorAlteracao;
	}
	public void setServidorAlteracao(RapServidores servidorAlteracao) {
		this.servidorAlteracao = servidorAlteracao;
	}
	public DominioSimNao getIndPadronizado() {
		return indPadronizado;
	}
	public void setIndPadronizado(DominioSimNao indPadronizado) {
		this.indPadronizado = indPadronizado;
	}
	public DominioSimNao getIndCcih() {
		return indCcih;
	}
	public void setIndCcih(DominioSimNao indCcih) {
		this.indCcih = indCcih;
	}
	public Boolean getIndControleLote() {
		return indControleLote;
	}
	public void setIndControleLote(Boolean indControleLote) {
		this.indControleLote = indControleLote;
	}
	public DominioIndFotoSensibilidade getIndFotosensivel() {
		return indFotosensivel;
	}
	public void setIndFotosensivel(DominioIndFotoSensibilidade indFotosensivel) {
		this.indFotosensivel = indFotosensivel;
	}
	public DominioIndTipoUso getIndTipoUso() {
		return indTipoUso;
	}
	public void setIndTipoUso(DominioIndTipoUso indTipoUso) {
		this.indTipoUso = indTipoUso;
	}
	public BigDecimal getNumero() {
		return numero;
	}
	public void setNumero(BigDecimal numero) {
		this.numero = numero;
	}
	public String getIndCadSapiens() {
		return indCadSapiens;
	}
	public void setIndCadSapiens(String indCadSapiens) {
		this.indCadSapiens = indCadSapiens;
	}
	public Date getDtCadSapiens() {
		return dtCadSapiens;
	}
	public void setDtCadSapiens(Date dtCadSapiens) {
		this.dtCadSapiens = dtCadSapiens;
	}
	public RapServidores getServidorCadSapiens() {
		return servidorCadSapiens;
	}
	public void setServidorCadSapiens(RapServidores servidorCadSapiens) {
		this.servidorCadSapiens = servidorCadSapiens;
	}
	public Short getCodRetencao() {
		return codRetencao;
	}
	public void setCodRetencao(Short codRetencao) {
		this.codRetencao = codRetencao;
	}
	public String getCodTransacaoSapiens() {
		return codTransacaoSapiens;
	}
	public void setCodTransacaoSapiens(String codTransacaoSapiens) {
		this.codTransacaoSapiens = codTransacaoSapiens;
	}
	public Integer getCodSiasg() {
		return codSiasg;
	}
	public void setCodSiasg(Integer codSiasg) {
		this.codSiasg = codSiasg;
	}
	public String getCodSitTribSapiens() {
		return codSitTribSapiens;
	}
	public void setCodSitTribSapiens(String codSitTribSapiens) {
		this.codSitTribSapiens = codSitTribSapiens;
	}
	public String getNcmCodigo() {
		return ncmCodigo;
	}
	public void setNcmCodigo(String ncmCodigo) {
		this.ncmCodigo = ncmCodigo;
	}
	public AfaMedicamento getAfaMedicamento() {
		return afaMedicamento;
	}
	public void setAfaMedicamento(AfaMedicamento afaMedicamento) {
		this.afaMedicamento = afaMedicamento;
	}
	public ScoGrupoMaterial getScoGrupoMaterial() {
		return scoGrupoMaterial;
	}
	public void setScoGrupoMaterial(ScoGrupoMaterial scoGrupoMaterial) {
		this.scoGrupoMaterial = scoGrupoMaterial;
	}
	public SceAlmoxarifado getSceAlmoxarifado() {
		return sceAlmoxarifado;
	}
	public void setSceAlmoxarifado(SceAlmoxarifado sceAlmoxarifado) {
		this.sceAlmoxarifado = sceAlmoxarifado;
	}
	public ScoOrigemParecerTecnico getOrigemParecerTecnico() {
		return origemParecerTecnico;
	}
	public void setOrigemParecerTecnico(ScoOrigemParecerTecnico origemParecerTecnico) {
		this.origemParecerTecnico = origemParecerTecnico;
	}
	public Integer getCodCatmat() {
		return codCatmat;
	}
	public void setCodCatmat(Integer codCatmat) {
		this.codCatmat = codCatmat;
	}
	public Long getCodMatAntigo() {
		return codMatAntigo;
	}
	public void setCodMatAntigo(Long codMatAntigo) {
		this.codMatAntigo = codMatAntigo;
	}
	public ScoSiasgMaterialMestre getCatMat() {
		return catMat;
	}
	public void setCatMat(ScoSiasgMaterialMestre catMat) {
		this.catMat = catMat;
	}
	public Boolean getIndCorrosivo() {
		return indCorrosivo;
	}
	public void setIndCorrosivo(Boolean indCorrosivo) {
		this.indCorrosivo = indCorrosivo;
	}
	public Boolean getIndInflamavel() {
		return indInflamavel;
	}
	public void setIndInflamavel(Boolean indInflamavel) {
		this.indInflamavel = indInflamavel;
	}
	public Boolean getIndRadioativo() {
		return indRadioativo;
	}
	public void setIndRadioativo(Boolean indRadioativo) {
		this.indRadioativo = indRadioativo;
	}
	public Boolean getIndReativo() {
		return indReativo;
	}
	public void setIndReativo(Boolean indReativo) {
		this.indReativo = indReativo;
	}
	public Boolean getIndToxico() {
		return indToxico;
	}
	public void setIndToxico(Boolean indToxico) {
		this.indToxico = indToxico;
	}
	public Boolean getIndUtilizaEspacoFisico() {
		return indUtilizaEspacoFisico;
	}
	public void setIndUtilizaEspacoFisico(Boolean indUtilizaEspacoFisico) {
		this.indUtilizaEspacoFisico = indUtilizaEspacoFisico;
	}
	
	public void setIndTipoResiduo(DominioTipoResiduo indTipoResiduo) {
		this.indTipoResiduo = indTipoResiduo;
	}
	public DominioTipoResiduo getIndTipoResiduo() {
		return indTipoResiduo;
	}
	public void setIndTermolabil(Boolean indTermolabil) {
		this.indTermolabil = indTermolabil;
		if(!this.getIndTermolabil().booleanValue()) {
			this.setTemperatura(null);
		}
	}
	public Boolean getIndTermolabil() {
		return indTermolabil;
	}
	public void setIndVinculado(Boolean indVinculado) {
		this.indVinculado = indVinculado;
	}
	public Boolean getIndVinculado() {
		return indVinculado;
	}
	public void setIndSustentavel(Boolean indSustentavel) {
		this.indSustentavel = indSustentavel;
	}
	public Boolean getIndSustentavel() {
		return indSustentavel;
	}
	public void setTemperatura(BigDecimal temperatura) {
		this.temperatura = temperatura;
	}
	public BigDecimal getTemperatura() {
		return temperatura;
	}
	public void setLegislacao(String legislacao) {
		this.legislacao = legislacao;
	}
	public String getLegislacao() {
		return legislacao;
	}
	public void setIndConfaz(DominioSimNao indConfaz) {
		this.indConfaz = indConfaz;
	}
	public DominioSimNao getIndConfaz() {
		return indConfaz;
	}
	public void setIndCapCmed(DominioSimNao indCapCmed) {
		this.indCapCmed = indCapCmed;
	}
	public DominioSimNao getIndCapCmed() {
		return indCapCmed;
	}
	
	
}
