package br.gov.mec.aghu.registrocolaborador.vo;

import java.io.Serializable;

import br.gov.mec.aghu.dominio.DominioCategoriaProfissionalEquipeCrg;
import br.gov.mec.aghu.dominio.DominioCategoriaProfissionalEquipePdt;
import br.gov.mec.aghu.dominio.DominioFuncaoProfissional;
import br.gov.mec.aghu.dominio.DominioTipoAtuacao;

/**
 * Classe VO que contém atributos relacionados a Profissional que realiza
 * Descrição Cirurgica/PDT.
 * 
 * @author dpacheco
 * 
 */
public class ProfDescricaoCirurgicaVO implements Serializable {
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -7104624726511968439L;
	
	
	private Short serVinCodigo;
	private Integer serMatricula;	
	private DominioFuncaoProfissional indFuncaoProf;
	private String sigla;	
	private String nroRegConselho;	
	private String nome;	
	private String conselhoSigla;	
	private Short unfSeq;
	private DominioTipoAtuacao tipoAtuacao;
	private DominioCategoriaProfissionalEquipeCrg categoria;
	private DominioCategoriaProfissionalEquipePdt categoriaPdt;
	
	public Short getSerVinCodigo() {
		return serVinCodigo;
	}

	public void setSerVinCodigo(Short serVinCodigo) {
		this.serVinCodigo = serVinCodigo;
	}

	public Integer getSerMatricula() {
		return serMatricula;
	}

	public void setSerMatricula(Integer serMatricula) {
		this.serMatricula = serMatricula;
	}

	public DominioFuncaoProfissional getIndFuncaoProf() {
		return indFuncaoProf;
	}

	public void setIndFuncaoProf(DominioFuncaoProfissional indFuncaoProf) {
		this.indFuncaoProf = indFuncaoProf;
	}

	public String getSigla() {
		return sigla;
	}

	public void setSigla(String sigla) {
		this.sigla = sigla;
	}

	public String getNroRegConselho() {
		return nroRegConselho;
	}

	public void setNroRegConselho(String nroRegConselho) {
		this.nroRegConselho = nroRegConselho;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getConselhoSigla() {
		return conselhoSigla;
	}

	public void setConselhoSigla(String conselhoSigla) {
		this.conselhoSigla = conselhoSigla;
	}

	public Short getUnfSeq() {
		return unfSeq;
	}

	public void setUnfSeq(Short unfSeq) {
		this.unfSeq = unfSeq;
	}
	
	public DominioCategoriaProfissionalEquipeCrg getCategoria() {
		return categoria;
	}

	public void setCategoria(DominioCategoriaProfissionalEquipeCrg categoria) {
		this.categoria = categoria;
	}

	public DominioTipoAtuacao getTipoAtuacao() {
		return tipoAtuacao;
	}

	public void setTipoAtuacao(DominioTipoAtuacao tipoAtuacao) {
		this.tipoAtuacao = tipoAtuacao;
	}
	
	public DominioCategoriaProfissionalEquipePdt getCategoriaPdt() {
		return categoriaPdt;
	}

	public void setCategoriaPdt(DominioCategoriaProfissionalEquipePdt categoriaPdt) {
		this.categoriaPdt = categoriaPdt;
	}

	public String getDescricaoSiglaNomeProfissional() {
		return sigla + " - " + nome;
	}


	public enum Fields {
		SER_VIN_CODIGO("serVinCodigo"),
		SER_MATRICULA("serMatricula"),	
		IND_FUNCAO_PROF("indFuncaoProf"),
		SIGLA("sigla"),	
		NRO_REG_CONSELHO("nroRegConselho"),	
		NOME("nome"),	
		CONSELHO_SIGLA("conselhoSigla"),	
		UNF_SEQ("unfSeq");

		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return fields;
		}
	}

}
