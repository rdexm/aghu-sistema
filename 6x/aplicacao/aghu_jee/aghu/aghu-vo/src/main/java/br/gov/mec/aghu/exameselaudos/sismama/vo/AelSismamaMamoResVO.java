package br.gov.mec.aghu.exameselaudos.sismama.vo;

import java.io.Serializable;

import br.gov.mec.aghu.dominio.DominioCategoriaBiRadsMamografia;
import br.gov.mec.aghu.dominio.DominioComposicaoMama;
import br.gov.mec.aghu.dominio.DominioContornoNoduloMamografia;
import br.gov.mec.aghu.dominio.DominioDistribuicaoMicrocalcificacaoMamografia;
import br.gov.mec.aghu.dominio.DominioFormaMicrocalcificacaoMamografia;
import br.gov.mec.aghu.dominio.DominioLimiteNoduloMamografia;
import br.gov.mec.aghu.dominio.DominioLinfonodosAxilaresMamografia;
import br.gov.mec.aghu.dominio.DominioLocalizacaoMamografia;
import br.gov.mec.aghu.dominio.DominioPeleMamografia;
import br.gov.mec.aghu.dominio.DominioRecomendacaoMamografia;
import br.gov.mec.aghu.dominio.DominioTamanhoNoduloMamografia;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.VAelSerSismama;

public class AelSismamaMamoResVO implements Serializable {
	
	private static final long serialVersionUID = -8975741156222356599L;
	
	private boolean checked;
	private Short numeroFilmes;
	private String observacoes;
	private DominioComposicaoMama composicao;
	private DominioPeleMamografia pele;
	private DominioDistribuicaoMicrocalcificacaoMamografia distribuicao;
	private DominioLocalizacaoMamografia localizacao;
	private DominioFormaMicrocalcificacaoMamografia forma;
	private DominioContornoNoduloMamografia contorno;
	private DominioLimiteNoduloMamografia limite;
	private DominioLinfonodosAxilaresMamografia linfonodoAxilar;
	private DominioTamanhoNoduloMamografia tamanho;
	private DominioCategoriaBiRadsMamografia categoria;
	private DominioRecomendacaoMamografia recomendacao;
	
	private VAelSerSismama responsavel; 
	private RapServidores residente;

	public AelSismamaMamoResVO() {
	}

	public boolean isChecked() {
		return checked;
	}

	public void setChecked(boolean checked) {
		this.checked = checked;
	}

	public DominioComposicaoMama getComposicao() {
		return composicao;
	}

	public void setComposicao(DominioComposicaoMama composicao) {
		this.composicao = composicao;
	}

	public DominioPeleMamografia getPele() {
		return pele;
	}

	public void setPele(DominioPeleMamografia pele) {
		this.pele = pele;
	}

	public DominioDistribuicaoMicrocalcificacaoMamografia getDistribuicao() {
		return distribuicao;
	}

	public void setDistribuicao(
			DominioDistribuicaoMicrocalcificacaoMamografia distribuicao) {
		this.distribuicao = distribuicao;
	}

	public DominioLocalizacaoMamografia getLocalizacao() {
		return localizacao;
	}

	public void setLocalizacao(DominioLocalizacaoMamografia localizacao) {
		this.localizacao = localizacao;	
	}

	public DominioFormaMicrocalcificacaoMamografia getForma() {
		return forma;
	}

	public void setForma(DominioFormaMicrocalcificacaoMamografia forma) {
		this.forma = forma;
	}

	public DominioContornoNoduloMamografia getContorno() {
		return contorno;
	}

	public void setContorno(DominioContornoNoduloMamografia contorno) {
		this.contorno = contorno;
	}

	public DominioLimiteNoduloMamografia getLimite() {
		return limite;
	}

	public void setLimite(DominioLimiteNoduloMamografia limite) {
		this.limite = limite;
	}

	public DominioLinfonodosAxilaresMamografia getLinfonodoAxilar() {
		return linfonodoAxilar;
	}

	public void setLinfonodoAxilar(
			DominioLinfonodosAxilaresMamografia linfonodoAxilar) {
		this.linfonodoAxilar = linfonodoAxilar;
	}

	public DominioTamanhoNoduloMamografia getTamanho() {
		return tamanho;
	}

	public void setTamanho(DominioTamanhoNoduloMamografia tamanho) {
		this.tamanho = tamanho;
	}

	public DominioCategoriaBiRadsMamografia getCategoria() {
		return categoria;
	}

	public void setCategoria(DominioCategoriaBiRadsMamografia categoria) {
		this.categoria = categoria;
	}

	public DominioRecomendacaoMamografia getRecomendacao() {
		return recomendacao;
	}

	public void setRecomendacao(DominioRecomendacaoMamografia recomendacao) {
		this.recomendacao = recomendacao;
	}

	public Short getNumeroFilmes() {
		return numeroFilmes;
	}

	public void setNumeroFilmes(Short numeroFilmes) {
		this.numeroFilmes = numeroFilmes;
	}

	public String getObservacoes() {
		return observacoes;
	}

	public void setObservacoes(String observacoes) {
		this.observacoes = observacoes;
	}

	public VAelSerSismama getResponsavel() {
		return responsavel;
	}

	public void setResponsavel(VAelSerSismama responsavel) {
		this.responsavel = responsavel;
	}

	public RapServidores getResidente() {
		return residente;
	}

	public void setResidente(RapServidores residente) {
		this.residente = residente;
	}

	
}