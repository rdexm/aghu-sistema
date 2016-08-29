package br.gov.mec.aghu.exames.pesquisa.vo;

import java.io.Serializable;

import br.gov.mec.aghu.dominio.DominioOrigemAtendimento;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.AelGrupoExameUsual;
import br.gov.mec.aghu.model.AghEspecialidades;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;


public class PesquisaLoteExamesFiltroVO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7606582186275731772L;
	private Integer codigoLote;
	private AelGrupoExameUsual grupoExame;	
	private AghUnidadesFuncionais unidadeFuncional;
	private AghEspecialidades especialidade;
	private DominioSimNao indLoteDefault;
	private DominioSituacao indSituacao;
	private DominioOrigemAtendimento origemAtendimento;
	
	public Integer getCodigoLote() {
		return codigoLote;
	}
	public void setCodigoLote(Integer codigoLote) {
		this.codigoLote = codigoLote;
	}
	public AelGrupoExameUsual getGrupoExame() {
		return grupoExame;
	}
	public void setGrupoExame(AelGrupoExameUsual grupoExame) {
		this.grupoExame = grupoExame;
	}
	public AghUnidadesFuncionais getUnidadeFuncional() {
		return unidadeFuncional;
	}
	public void setUnidadeFuncional(AghUnidadesFuncionais unidadeFuncional) {
		this.unidadeFuncional = unidadeFuncional;
	}
	public AghEspecialidades getEspecialidade() {
		return especialidade;
	}
	public void setEspecialidade(AghEspecialidades especialidade) {
		this.especialidade = especialidade;
	}
	public DominioSimNao getIndLoteDefault() {
		return indLoteDefault;
	}
	public void setIndLoteDefault(DominioSimNao indLoteDefault) {
		this.indLoteDefault = indLoteDefault;
	}
	public DominioSituacao getIndSituacao() {
		return indSituacao;
	}
	public void setIndSituacao(DominioSituacao indSituacao) {
		this.indSituacao = indSituacao;
	}
	public DominioOrigemAtendimento getOrigemAtendimento() {
		return origemAtendimento;
	}
	public void setOrigemAtendimento(DominioOrigemAtendimento origemAtendimento) {
		this.origemAtendimento = origemAtendimento;
	}
	
	
}