package br.gov.mec.aghu.internacao.vo;

import java.util.ArrayList;
import java.util.List;

import br.gov.mec.aghu.dominio.DominioSexo;
import br.gov.mec.aghu.dominio.DominioSexoDeterminante;
import br.gov.mec.aghu.model.AghClinicas;
import br.gov.mec.aghu.model.AinInternacao;
import br.gov.mec.aghu.core.commons.BaseBean;

public class QuartoDisponibilidadeVO implements BaseBean {
	
	private static final long serialVersionUID = 1688813552322533395L;

	private Short quarto;
	
	private DominioSexo sexoOcupacao;
	
	private DominioSexoDeterminante sexoDeterminante;
	
	private String ala;
	
	private AghClinicas clinica;
	
	private Integer totalInt;
	
	private String vagas;
	
	private Integer capacidade;
	
	private List<AinInternacao> internacoes = new ArrayList<AinInternacao>();
	
	private String solictLeito;
	
	private String descricao;

	public Short getQuarto() {
		return quarto;
	}

	public void setQuarto(Short quarto) {
		this.quarto = quarto;
	}

	public DominioSexo getSexoOcupacao() {
		return sexoOcupacao;
	}

	public void setSexoOcupacao(DominioSexo sexoOcupacao) {
		this.sexoOcupacao = sexoOcupacao;
	}

	public DominioSexoDeterminante getSexoDeterminante() {
		return sexoDeterminante;
	}

	public void setSexoDeterminante(DominioSexoDeterminante sexoDeterminante) {
		this.sexoDeterminante = sexoDeterminante;
	}

	public String getAla() {
		return ala;
	}

	public void setAla(String ala) {
		this.ala = ala;
	}

	public AghClinicas getClinica() {
		return clinica;
	}

	public void setClinica(AghClinicas clinica) {
		this.clinica = clinica;
	}

	public String getVagas() {
		return vagas;
	}

	public void setVagas(String vagas) {
		this.vagas = vagas;
	}

	public Integer getCapacidade() {
		return capacidade;
	}

	public void setCapacidade(Integer capacidade) {
		this.capacidade = capacidade;
	}

	public Integer getTotalInt() {
		return totalInt;
	}

	public void setTotalInt(Integer totalInt) {
		this.totalInt = totalInt;
	}

	public List<AinInternacao> getInternacoes() {
		return internacoes;
	}

	public void setInternacoes(List<AinInternacao> internacoes) {
		this.internacoes = internacoes;
	}

	public String getSolictLeito() {
		return solictLeito;
	}

	public void setSolictLeito(String solictLeito) {
		this.solictLeito = solictLeito;
	}
	
	public boolean isSexoOcupacaoMasculino() {
		return DominioSexo.M.equals(this.getSexoOcupacao());
	}

	public boolean isSexoOcupacaoFeminino() {
		return DominioSexo.F.equals(this.getSexoOcupacao());
	}
	
	public boolean isSexoDeterminanteMasculino() {
		return DominioSexoDeterminante.M.equals(this.getSexoDeterminante());		
	}

	public boolean isSexoDeterminanteFeminino() {
		return DominioSexoDeterminante.F.equals(this.getSexoDeterminante());		
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}
	
	
}
